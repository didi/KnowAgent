package com.didichuxing.datachannel.agent.sink.utils.serializer;

import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaEvent;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaSink;
import com.didichuxing.datachannel.agent.sink.utils.serializer.MqEventSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class JsonMqEventSerializer extends MqEventSerializer {
    private static final Logger LOGGER       = LoggerFactory.getLogger(JsonMqEventSerializer.class);
    private final ObjectMapper  objectMapper = new ObjectMapper();

    public JsonMqEventSerializer(KafkaSink mqSink) {
        super(mqSink);
    }

    @Override
    public byte[] serializeArray(List<KafkaEvent> mqEvents) {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (KafkaEvent mqEvent : mqEvents) {
            if (mqEvent != null) {
                JsonNode node = createSingleNode(mqEvent);
                if (node != null) {
                    arrayNode.add(node);
                }
            }
        }
        try {
            return objectMapper.writeValueAsBytes(arrayNode);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to serialize events to json", e);
            return new byte[0];
        }
    }

    @Override
    public byte[] serializeSingle(KafkaEvent mqEvent) {
        JsonNode jsonNode = createSingleNode(mqEvent);
        try {
            return objectMapper.writeValueAsBytes(jsonNode);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to serialize events to json", e);
            return new byte[0];
        }
    }

    private JsonNode createSingleNode(KafkaEvent mqEvent) {
        JsonNodeFactory nodeFactory = objectMapper.getNodeFactory();
        ObjectNode objectNode = objectMapper.createObjectNode();
        // 为保证数据的一致性，防止因为配置原因导致的问题，以下字段通过手动赋值
        if (getModelConfig() != null && getModelConfig().getEventMetricsConfig() != null) {

            // public核心字段
            objectNode.set("content", nodeFactory.textNode(mqEvent.getContent()));
            objectNode.set("hostName", nodeFactory.textNode(CommonUtils.getHOSTNAME()));
            objectNode.set("uniqueKey", nodeFactory.textNode(mqEvent.getMsgUniqueKey()));
            objectNode
                .set("originalAppName", nodeFactory.textNode(getModelConfig()
                    .getEventMetricsConfig().getOriginalAppName()));
            objectNode.set("odinLeaf",
                nodeFactory.textNode(getModelConfig().getEventMetricsConfig().getOdinLeaf()));
            objectNode.set("logTime", nodeFactory.numberNode(mqEvent.getMsgTime()));
            objectNode.set("logId",
                nodeFactory.numberNode(getModelConfig().getCommonConfig().getModelId()));

            if (StringUtils.isNotBlank(getModelConfig().getEventMetricsConfig().getTransName())) {
                objectNode.set("appName",
                    nodeFactory.textNode(getModelConfig().getEventMetricsConfig().getTransName()));
            } else {
                objectNode.set("appName", nodeFactory.textNode(getModelConfig()
                    .getEventMetricsConfig().getBelongToCluster()));
            }
            objectNode.set("queryFrom",
                nodeFactory.textNode(getModelConfig().getEventMetricsConfig().getQueryFrom()));
            objectNode.set("logName", nodeFactory.textNode(mqEvent.getSourceItemName()));
            objectNode.set("isService",
                nodeFactory.numberNode(getModelConfig().getEventMetricsConfig().getIsService()));
            objectNode.set("pathId", nodeFactory.numberNode(mqEvent.getSourceId()));
            // 历史原因，timetamp使用logTime的string类型
            objectNode.set("timestamp", nodeFactory.textNode(String.valueOf(mqEvent.getMsgTime())));
            objectNode.set("collectTime", nodeFactory.numberNode(mqEvent.getCollectTime()));
            objectNode.set("fileKey", nodeFactory.textNode(mqEvent.getSourceItemKey()));
            objectNode.set("parentPath", nodeFactory.textNode(mqEvent.getSourceItemHeaderName()));
            objectNode.set("offset", nodeFactory.numberNode(mqEvent.getRate()));
            objectNode.set("preOffset", nodeFactory.numberNode(mqEvent.getPreRate()));

            if (getModelConfig().getEventMetricsConfig().getOtherEvents() != null) {
                for (Map.Entry<String, String> entry : getModelConfig().getEventMetricsConfig()
                    .getOtherEvents().entrySet()) {
                    objectNode.set(entry.getKey(), nodeFactory.textNode(entry.getValue()));
                }
            }
            return objectNode;
        }
        return null;
    }
}
