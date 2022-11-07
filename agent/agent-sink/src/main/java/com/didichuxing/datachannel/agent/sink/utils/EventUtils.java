package com.didichuxing.datachannel.agent.sink.utils;

import java.util.List;
import java.util.Map;

import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.api.StandardLogType;
import com.didichuxing.datachannel.agent.common.api.TopicPartitionKeyTypeEnum;
import org.apache.commons.lang.StringUtils;

import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaEvent;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaSink;
import com.didichuxing.datachannel.agent.sink.utils.serializer.EventListSerializer;
import com.didichuxing.datachannel.agent.sink.utils.serializer.LogEventSerializerObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: logEvent工具类
 * @author: huangjw
 * @Date: 18/6/22 19:20
 */
public class EventUtils {

    private static final Logger LOGGER             = LoggerFactory.getLogger(EventUtils.class
                                                       .getName());
    private static final String CUT_FLAG           = "...";

    private static final String START_ARRAY        = "[";
    private static final String ENG_ARRAY          = "]";
    private static final String QUAT               = "\"";
    private static final String COLON              = ":";
    private static final String COMMA              = ",";
    private static final byte[] END_OF_JSON_OBJECT = "\"}".getBytes();

    /**
     * 获取content的partition-key，
     *
     * @param mqEvent
     * @return
     */
    public static String getPartitionKey(KafkaSink mqSink, KafkaEvent mqEvent,
                                         int topicPartitionKeyType) {
        if (mqEvent == null) {
            return getPartitionKeyByType(topicPartitionKeyType);
        }
        if (mqSink == null || StringUtils.isBlank(mqSink.getKafkaTargetConfig().getKeyStartFlag())
            || StringUtils.isBlank(mqSink.getKafkaTargetConfig().getKeyFormat())) {
            if (mqSink != null
                && StringUtils.isNotBlank(mqSink.getKafkaTargetConfig().getRegularPartKey())) {
                if (mqSink.getKafkaTargetConfig().getRegularPartKey()
                    .equals(LogConfigConstants.HOSTNAME_FLAG)) {
                    // 以主机名作为key
                    return CommonUtils.getHOSTNAME();
                } else if (mqSink.getKafkaTargetConfig().getRegularPartKey()
                    .equals(LogConfigConstants.FILE_FLAG)) {
                    // 以文件key作为key
                    return mqEvent.getSourceItemKey();
                } else if (mqSink.getKafkaTargetConfig().getRegularPartKey()
                    .startsWith(LogConfigConstants.TIME_FLAG)) {
                    // time
                    return mqEvent.getSourceItemKey() + System.currentTimeMillis()
                           / mqSink.getKeyDelay();

                } else {
                    return getPartitionKeyByType(topicPartitionKeyType);
                }
            } else {
                // 判断是否需要开启时间轮转、（默认512ms换一个partition）
                return getPartitionKeyByType(topicPartitionKeyType);
            }
        } else {
            String keyStartFlag = mqSink.getKafkaTargetConfig().getKeyStartFlag();
            String keyFormat = mqSink.getKafkaTargetConfig().getKeyFormat();
            int keyStartFlagIndex = mqSink.getKafkaTargetConfig().getKeyStartFlagIndex();

            // 若keyFormat非空，且keyFormat为特殊标记，则以hostName为key
            if (StringUtils.isNotBlank(keyFormat)
                && keyFormat.equals(LogConfigConstants.HOSTNAME_FLAG)) {
                return CommonUtils.getHOSTNAME();
            }

            String line = mqEvent.getContent();
            if (StringUtils.isNotEmpty(keyStartFlag) && StringUtils.isNotEmpty(keyFormat)) {
                boolean isVaild = true;
                for (int i = 0; i < keyStartFlagIndex + 1; i++) {
                    int startSubIndex = line.indexOf(keyStartFlag);
                    if (startSubIndex >= 0) {
                        line = line.substring(startSubIndex + keyStartFlag.length());
                    } else {
                        // 此时说明line中不存在startFlag
                        isVaild = false;
                        break;
                    }
                }
                if (isVaild) {
                    if (line.length() < keyFormat.length()) {
                        LOGGER.warn("Content has no key, topic is "
                                    + mqSink.getKafkaTargetConfig().getTopic());
                        return getPartitionKeyByType(topicPartitionKeyType);
                    } else {
                        return line.substring(0, keyFormat.length());
                    }
                } else {
                    // 此时说明line中不存在startFlag
                    return getPartitionKeyByType(topicPartitionKeyType);
                }
            } else if (StringUtils.isEmpty(keyStartFlag) && keyStartFlagIndex == 0
                       && StringUtils.isNotBlank(keyFormat)) {
                // 兼容key的分隔符是空，但是范例有值的场景
                if (line.length() < keyFormat.length()) {
                    return getPartitionKeyByType(topicPartitionKeyType);
                } else {
                    return line.substring(0, keyFormat.length());
                }
            }

            return getPartitionKeyByType(topicPartitionKeyType);
        }
    }

    /**
     * 校验content是否过大
     *
     * @param kafkaSink
     * @param content
     * @return
     */
    public static boolean checkIsTooLarge(KafkaSink kafkaSink, String content) {
        if (content.length() * 3 > kafkaSink.getKafkaTargetConfig().getMaxContentSize()) {
            return true;
        } else {
            return false;
        }
    }

    public static String toListJson(KafkaSink kafkaSink, List<KafkaEvent> kafkaEvents) {
        StringBuilder sb = new StringBuilder(START_ARRAY);
        boolean first = true;
        for (KafkaEvent event : kafkaEvents) {
            String item = toObjectJson(kafkaSink, event);
            if (StringUtils.isNotBlank(item)) {
                if (first) {
                    sb.append(item);
                    first = false;
                } else {
                    sb.append(",").append(item);
                }
            }
        }
        sb.append(ENG_ARRAY);
        return sb.toString();
    }

    public static String toObjectJson(KafkaSink kafkaSink, KafkaEvent kafkaEvent) {
        if (kafkaSink != null && kafkaSink.getModelConfig() != null
            && kafkaSink.getModelConfig().getEventMetricsConfig() != null) {
            StringBuilder sb = new StringBuilder("{");
            String dockerName = kafkaSink.getModelConfig().getEventMetricsConfig().getDockerName();

            sb.append("\"hostName\"").append(":").append("\"")
                .append(dockerName == null ? CommonUtils.getHOSTNAME() : dockerName).append("\"")
                .append(",");
            sb.append("\"originalAppName\"").append(":").append("\"")
                .append(kafkaSink.getModelConfig().getEventMetricsConfig().getOriginalAppName())
                .append("\"").append(",");
            sb.append("\"logName\"").append(":").append("\"")
                .append(kafkaEvent.getSourceItemName()).append("\"").append(",");
            sb.append("\"queryFrom\"").append(":").append("\"")
                .append(kafkaSink.getModelConfig().getEventMetricsConfig().getQueryFrom())
                .append("\"").append(",");
            if (StringUtils.isNotBlank(kafkaSink.getModelConfig().getEventMetricsConfig()
                .getTransName())) {
                sb.append("\"appName\"").append(":").append("\"")
                    .append(kafkaSink.getModelConfig().getEventMetricsConfig().getTransName())
                    .append("\"").append(",");
            } else {
                sb.append("\"appName\"")
                    .append(":")
                    .append("\"")
                    .append(kafkaSink.getModelConfig().getEventMetricsConfig().getBelongToCluster())
                    .append("\"").append(",");
            }

            if (CommonUtils.getDidienvOdinSu() == null) {
                sb.append("\"DIDIENV_ODIN_SU\"").append(":").append("null").append(",");
            } else {
                sb.append("\"DIDIENV_ODIN_SU\"").append(":").append("\"")
                    .append(CommonUtils.getDidienvOdinSu()).append("\"").append(",");
            }

            if (kafkaEvent.getMsgUniqueKey() == null) {
                sb.append("\"uniqueKey\"").append(":").append("null").append(",");
            } else {
                sb.append("\"uniqueKey\"").append(":").append("\"")
                    .append(kafkaEvent.getMsgUniqueKey()).append("\"").append(",");
            }
            sb.append("\"parentPath\"").append(":").append("\"")
                .append(kafkaEvent.getSourceItemHeaderName()).append("\"").append(",");
            sb.append("\"odinLeaf\"").append(":").append("\"")
                .append(kafkaSink.getModelConfig().getEventMetricsConfig().getOdinLeaf())
                .append("\"").append(",");
            sb.append("\"fileKey\"").append(":").append("\"").append(kafkaEvent.getSourceItemKey())
                .append("\"").append(",");
            sb.append("\"timestamp\"").append(":").append("\"").append(kafkaEvent.getMsgTime())
                .append("\"").append(",");
            sb.append("\"logId\"").append(":")
                .append(kafkaSink.getModelConfig().getCommonConfig().getModelId()).append(",");
            sb.append("\"pathId\"").append(":").append(kafkaEvent.getSourceId()).append(",");
            sb.append("\"isService\"").append(":")
                .append(kafkaSink.getModelConfig().getEventMetricsConfig().getIsService())
                .append(",");
            sb.append("\"collectTime\"").append(":").append(kafkaEvent.getCollectTime())
                .append(",");
            sb.append("\"offset\"").append(":").append(kafkaEvent.getRate()).append(",");
            sb.append("\"preOffset\"").append(":").append(kafkaEvent.getPreRate()).append(",");
            sb.append("\"logTime\"").append(":").append(kafkaEvent.getMsgTime()).append(",");
            sb.append("\"content\"").append(":").append("\"").append(kafkaEvent.getContent())
                .append("\"");
            // sb.append("\"content\"").append(":").append("\"").append(StringEscapeUtils.escapeJava(kafkaEvent.getContent())).append("\"");

            if (kafkaSink.getModelConfig().getEventMetricsConfig().getOtherEvents() != null) {
                for (Map.Entry<String, String> entry : kafkaSink.getModelConfig()
                    .getEventMetricsConfig().getOtherEvents().entrySet()) {
                    sb.append(",").append(
                        "\"" + entry.getKey() + "\"" + ":" + "\"" + entry.getValue() + "\"");
                }
            }
            sb.append("}");
            return sb.toString();
        }
        return null;
    }

    /**
     * 构造list json
     *
     * @param kafkaEvents
     * @return
     */
    public static String toNewListJson(KafkaSink kafkaSink, List<KafkaEvent> kafkaEvents,
                                       Integer businessType) {
        EventListSerializer agentListSerializer = new EventListSerializer();
        for (KafkaEvent kafkaEvent : kafkaEvents) {
            if (kafkaEvent != null) {
                agentListSerializer.append(toSerializerObject(kafkaSink, kafkaEvent, businessType));
            }
        }
        return agentListSerializer.toListJsonString();
    }

    /**
     *
     * TODO：william 字段 抽出
     *
     * @param kafkaSink
     * @param kafkaEvent
     * @param businessType
     * @return
     */
    private static LogEventSerializerObject toSerializerObject(KafkaSink kafkaSink,
                                                               KafkaEvent kafkaEvent,
                                                               Integer businessType) {
        // 为保证数据的一致性，防止因为配置原因导致的问题，以下字段通过手动赋值
        if (kafkaSink != null && kafkaSink.getModelConfig() != null
            && kafkaSink.getModelConfig().getEventMetricsConfig() != null) {
            LogEventSerializerObject object = new LogEventSerializerObject();

            // public核心字段
            object.append("content", kafkaEvent.getContent());

            object.append("hostName", CommonUtils.getHOSTNAME());
            object.append("", "");
            object.append("uniqueKey", kafkaEvent.getMsgUniqueKey());
            object.append("originalAppName", kafkaSink.getModelConfig().getEventMetricsConfig()
                .getOriginalAppName());
            object.append("odinLeaf", kafkaSink.getModelConfig().getEventMetricsConfig()
                .getOdinLeaf());
            object.append("logTime", kafkaEvent.getMsgTime());
            object.append("logId", kafkaSink.getModelConfig().getCommonConfig().getModelId());

            if (businessType == null || businessType.equals(StandardLogType.Normal.getType())) {
                if (StringUtils.isNotBlank(kafkaSink.getModelConfig().getEventMetricsConfig()
                    .getTransName())) {
                    object.append("appName", kafkaSink.getModelConfig().getEventMetricsConfig()
                        .getTransName());
                } else {
                    object.append("appName", kafkaSink.getModelConfig().getEventMetricsConfig()
                        .getBelongToCluster());
                }
                object.append("queryFrom", kafkaSink.getModelConfig().getEventMetricsConfig()
                    .getQueryFrom());
                object.append("logName", kafkaEvent.getSourceItemName());
                object.append("isService", kafkaSink.getModelConfig().getEventMetricsConfig()
                    .getIsService());
                object.append("pathId", kafkaEvent.getSourceId());
                // 历史原因，timetamp使用logTime的string类型
                object.append("timestamp", kafkaEvent.getMsgTime() + "");
                object.append("collectTime", kafkaEvent.getCollectTime());
                object.append("fileKey", kafkaEvent.getSourceItemKey());
                object.append("parentPath", kafkaEvent.getSourceItemHeaderName());
                object.append("offset", kafkaEvent.getRate());
                object.append("preOffset", kafkaEvent.getPreRate());

                if (kafkaSink.getModelConfig().getEventMetricsConfig().getOtherEvents() != null) {
                    for (Map.Entry<String, String> entry : kafkaSink.getModelConfig()
                        .getEventMetricsConfig().getOtherEvents().entrySet()) {
                        object.append(entry.getKey(), entry.getValue());
                    }
                }
            }
            return object;
        }
        return null;
    }

    /**
     * 根据不同的partition key 类型获取对应的partition key
     * 判断是否需要开启时间轮转、（开启后默认512ms换一个partition）
     * @param topicPartitionKeyType
     * @return
     */
    private static String getPartitionKeyByType(int topicPartitionKeyType) {
        if (topicPartitionKeyType == TopicPartitionKeyTypeEnum.FIXED_TIME_PARTITION_KEY.getType()) {
            return CommonUtils.getHOSTNAME() + (System.currentTimeMillis() >> 9);
        } else {
            return String.valueOf(System.currentTimeMillis());
        }
    }

}
