package com.didichuxing.datachannel.swan.agent.sink.utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.didichuxing.datachannel.swan.agent.common.api.*;
import com.didichuxing.datachannel.swan.agent.sink.hdfsSink.HdfsEvent;
import com.didichuxing.datachannel.swan.agent.sink.hdfsSink.HdfsSink;
import org.apache.commons.lang.StringUtils;

import com.didichuxing.datachannel.swan.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.swan.agent.sink.kafkaSink.KafkaEvent;
import com.didichuxing.datachannel.swan.agent.sink.kafkaSink.KafkaSink;
import com.didichuxing.datachannel.swan.agent.sink.utils.serializer.EventItemSerializer;
import com.didichuxing.datachannel.swan.agent.sink.utils.serializer.EventListSerializer;
import com.didichuxing.datachannel.swan.agent.sink.utils.serializer.LogEventSerializerObject;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import com.didichuxing.tunnel.util.log.LogGather;

/**
 * @description: logEvent工具类
 * @author: huangjw
 * @Date: 18/6/22 19:20
 */
public class EventUtils {

    private static final ILog   LOGGER             = LogFactory.getLog(EventUtils.class.getName());

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
     * @param kafkaEvent
     * @return
     */
    public static String getPartitionKey(KafkaSink kafkaSink, KafkaEvent kafkaEvent) {
        if (kafkaEvent == null) {
            return String.valueOf(System.currentTimeMillis());
        }
        if (kafkaSink == null
            || StringUtils.isBlank(kafkaSink.getKafkaTargetConfig().getKeyStartFlag())
            || StringUtils.isBlank(kafkaSink.getKafkaTargetConfig().getKeyFormat())) {
            if (kafkaSink != null
                && StringUtils.isNotBlank(kafkaSink.getKafkaTargetConfig().getRegularPartKey())) {
                if (kafkaSink.getKafkaTargetConfig().getRegularPartKey()
                    .equals(LogConfigConstants.HOSTNAME_FLAG)) {
                    // 以主机名作为key
                    return CommonUtils.getHOSTNAME();
                } else if (kafkaSink.getKafkaTargetConfig().getRegularPartKey()
                    .equals(LogConfigConstants.FILE_FLAG)) {
                    // 以文件key作为key
                    return kafkaEvent.getSourceItemKey();
                } else if (kafkaSink.getKafkaTargetConfig().getRegularPartKey()
                    .startsWith(LogConfigConstants.TIME_FLAG)) {
                    // time
                    return kafkaEvent.getSourceItemKey() + System.currentTimeMillis()
                           / kafkaSink.getKeyDelay();

                } else {
                    return String.valueOf(System.currentTimeMillis());
                }
            } else {
                return String.valueOf(System.currentTimeMillis());
            }
        } else {
            String keyStartFlag = kafkaSink.getKafkaTargetConfig().getKeyStartFlag();
            String keyFormat = kafkaSink.getKafkaTargetConfig().getKeyFormat();
            int keyStartFlagIndex = kafkaSink.getKafkaTargetConfig().getKeyStartFlagIndex();

            // 若keyFormat非空，且keyFormat为特殊标记，则以hostName为key
            if (StringUtils.isNotBlank(keyFormat)
                && keyFormat.equals(LogConfigConstants.HOSTNAME_FLAG)) {
                return CommonUtils.getHOSTNAME();
            }

            String line = kafkaEvent.getContent();
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
                                    + kafkaSink.getKafkaTargetConfig().getTopic());
                        return String.valueOf(System.currentTimeMillis());
                    } else {
                        return line.substring(0, keyFormat.length());
                    }
                } else {
                    // 此时说明line中不存在startFlag
                    return String.valueOf(System.currentTimeMillis());
                }
            } else if (StringUtils.isEmpty(keyStartFlag) && keyStartFlagIndex == 0
                       && StringUtils.isNotBlank(keyFormat)) {
                // 兼容key的分隔符是空，但是范例有值的场景
                if (line.length() < keyFormat.length()) {
                    return String.valueOf(System.currentTimeMillis());
                } else {
                    return line.substring(0, keyFormat.length());
                }
            }

            return String.valueOf(System.currentTimeMillis());
        }
    }

    /**
     * 消息体较大时，获取有效的logEvent的解析后的结果
     *
     * @param kafkaSink
     * @param kafkaEvent
     * @return
     */
    public static String getVaildEvent(KafkaSink kafkaSink, KafkaEvent kafkaEvent, Integer businessType) {
        try {
            String contentToSend = toNewItemJson(kafkaSink, kafkaEvent, businessType);
            if (checkIsTooLarge(kafkaSink, contentToSend)) {
                String eventContent = kafkaEvent.getContent();
                if (StringUtils.isNotBlank(eventContent)) {
                    // 有可能 contentToSend.length * 3 > commonConfig.getMaxContentSize(),但是content.length <
                    // commonConfig.getMaxContentSize() / 3
                    int threadHold = kafkaSink.getKafkaTargetConfig().getMaxContentSize().intValue() / 3;
                    if (eventContent.length() > threadHold) {
                        eventContent = eventContent.substring(0, threadHold) + CUT_FLAG;
                    }
                } else {
                    eventContent = CUT_FLAG;
                }
                kafkaEvent.setContent(eventContent);
                contentToSend = toNewItemJson(kafkaSink, kafkaEvent, businessType);
                LOGGER.warn("content is too large, so it be cutted.modelId is "
                            + kafkaSink.getModelConfig().getCommonConfig().getModelId() + ", sourceItem is "
                            + kafkaEvent.getSourceItemHeaderName() + kafkaEvent.getSourceItemName() + ", rate is "
                            + kafkaEvent.getRate());
                kafkaSink.getTaskPatternStatistics().tooLarge();
            }
            String result = null;
            int transFormate = kafkaSink.getKafkaTargetConfig().getTransFormate();
            if (transFormate == TransFormate.MQList.getStatus()) {
                // 按照list发送
                List<KafkaEvent> list = new ArrayList<>();
                list.add(kafkaEvent);
                result = toNewListJson(kafkaSink, list, businessType);
            } else if (transFormate == TransFormate.MQItem.getStatus()) {
                // 单个发送
                result = contentToSend;
            } else {
                // 纯内容发送
                result = kafkaEvent.getContent();
            }
            return result;
        } catch (Exception e) {
            LogGather.recordErrorLog("LogEventUtils error",
                                     "getVaildEvent error. modelId is "
                                                            + kafkaSink.getModelConfig().getCommonConfig().getModelId(),
                                     e);
        }
        return null;
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

    public static void buildHdfsContent(ByteArrayOutputStream byteStream, HdfsSink hdfsSink,
                                        HdfsEvent hdfsEvent) throws Exception {
        if (hdfsSink.getHdfsTargetConfig().getTransFormate() == HdfsTransFormat.HDFS_EVENT
            .getStatus()) {
            StringBuilder sb = new StringBuilder("");
            sb.append("{");
            sb.append("\"hostName\":").append("\"" + CommonUtils.getHOSTNAME() + "\"").append(",");
            sb.append("\"logId\":").append(hdfsEvent.getModelId()).append(",");
            sb.append("\"pathId\":").append(hdfsEvent.getSourceId()).append(",");
            sb.append("\"service\":")
                .append(
                    "\"" + hdfsSink.getModelConfig().getEventMetricsConfig().getOriginalAppName()
                            + "\"").append(",");
            sb.append("\"leaf\":")
                .append(
                    "\"" + hdfsSink.getModelConfig().getEventMetricsConfig().getOdinLeaf() + "\"")
                .append(",");
            sb.append("\"offset\":").append(hdfsEvent.getRateOfSource()).append(",");
            sb.append("\"collectTime\":").append(hdfsEvent.getSourceTime()).append(",");
            sb.append("\"logTime\":").append(hdfsEvent.getLogTime()).append(",");
            sb.append("\"uniqueKey\":").append("\"" + hdfsEvent.getMsgUniqueKey() + "\"")
                .append(",");
            sb.append("\"content\":").append("\"");

            byteStream.write(sb.toString().getBytes());
            byteStream.write(hdfsEvent.getBytes());
            byteStream.write(END_OF_JSON_OBJECT);
        } else {
            byteStream.write(hdfsEvent.getBytes());
        }

    }

    /**
     * 单个logEvent转换成string
     *
     * @param kafkaSink kafkaSink
     * @param kafkaEvent kafkaEvent
     * @return json of logEvent
     */
    public static String toNewItemJson(KafkaSink kafkaSink, KafkaEvent kafkaEvent,
                                       Integer businessType) {
        EventItemSerializer ser = new EventItemSerializer();
        return ser.toItemJsonString(toSerializerObject(kafkaSink, kafkaEvent, businessType));
    }

    private static void appendItem(StringBuilder sb, String key, String value) {
        if (value != null) {
            sb.append(QUAT).append(key).append(QUAT).append(COLON).append(QUAT).append(value)
                .append(QUAT).append(COMMA);
        }
    }

    private static void appendItem(StringBuilder sb, String key, Integer value) {
        if (value != null) {
            sb.append(QUAT).append(key).append(QUAT).append(COLON).append(value).append(COMMA);
        }
    }

    private static void appendItem(StringBuilder sb, String key, Long value) {
        if (value != null) {
            sb.append(QUAT).append(key).append(QUAT).append(COLON).append(value).append(COMMA);
        }
    }

    private static void appendItemEnd(StringBuilder sb, String key, Long value) {
        if (value != null) {
            sb.append(QUAT).append(key).append(QUAT).append(COLON).append(value);
        }
    }

}
