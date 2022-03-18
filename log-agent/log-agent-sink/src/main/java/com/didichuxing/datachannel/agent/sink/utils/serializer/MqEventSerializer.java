package com.didichuxing.datachannel.agent.sink.utils.serializer;

import com.didichuxing.datachannel.agent.common.api.TransFormate;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.metrics.source.TaskPatternStatistics;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaEvent;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaSink;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaTargetConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public abstract class MqEventSerializer {
    private static final Logger LOG      = LoggerFactory.getLogger(MqEventSerializer.class);
    private static final String CUT_FLAG = "...";

    private final KafkaSink     mqSink;

    protected MqEventSerializer(KafkaSink mqSink) {
        this.mqSink = mqSink;
    }

    public abstract byte[] serializeArray(List<KafkaEvent> mqEvents);

    public abstract byte[] serializeSingle(KafkaEvent mqEvent);

    public byte[] serializeArray(KafkaEvent mqEvent) {
        return serializeArray(Collections.singletonList(mqEvent));
    }

    /**
     * 消息体较大时，获取有效的logEvent的解析后的结果
     */
    public byte[] getValidEvent(KafkaEvent mqEvent) {
        try {
            byte[] contentToSend = serializeArray(mqEvent);
            if (checkIsTooLarge(contentToSend)) {
                String eventContent = mqEvent.getContent();
                if (StringUtils.isNotBlank(eventContent)) {
                    // 有可能 contentToSend.length * 3 > commonConfig.getMaxContentSize(),但是content.length <
                    // commonConfig.getMaxContentSize() / 3
                    int threadHold = getTargetConfig().getMaxContentSize().intValue() / 3;
                    if (eventContent.length() > threadHold) {
                        eventContent = eventContent.substring(0, threadHold) + CUT_FLAG;
                    }
                } else {
                    eventContent = CUT_FLAG;
                }
                mqEvent.setContent(eventContent);
                contentToSend = serializeArray(mqEvent);
                LOG.warn("content is too large, so it be cutted.modelId is "
                         + getModelConfig().getCommonConfig().getModelId() + ", sourceItem is "
                         + mqEvent.getSourceItemHeaderName() + mqEvent.getSourceItemName()
                         + ", rate is " + mqEvent.getRate());
                this.getTaskPatternStatistics().tooLarge();
            }
            byte[] result = null;
            int transFormate = getTargetConfig().getTransFormate();
            if (transFormate == TransFormate.MQList.getStatus()) {
                // 按照list发送
                result = contentToSend;
            } else if (transFormate == TransFormate.MQItem.getStatus()) {
                // 单个发送
                result = this.serializeSingle(mqEvent);
            } else {
                // 纯内容发送
                result = mqEvent.getContent().getBytes(StandardCharsets.UTF_8);
            }
            return result;
        } catch (Exception e) {
            LogGather.recordErrorLog("LogEventUtils error", "getVaildEvent error. modelId is "
                                                            + getModelConfig().getCommonConfig()
                                                                .getModelId(), e);
        }
        return null;
    }

    /**
     * 校验content是否过大
     */
    public boolean checkIsTooLarge(byte[] content) {
        return content.length * 3 > getTargetConfig().getMaxContentSize();
    }

    protected ModelConfig getModelConfig() {
        return mqSink.getModelConfig();
    }

    protected KafkaTargetConfig getTargetConfig() {
        return (KafkaTargetConfig) getModelConfig().getTargetConfig();
    }

    protected TaskPatternStatistics getTaskPatternStatistics() {
        return mqSink.getTaskPatternStatistics();
    }
}
