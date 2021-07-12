package com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics;

public enum AgentMetricField {
    // agent维度
    CPU_USAGE("cpuUsage"),

    HEARTBEAT_TIME("heartbeatTime"),

    HOST_IP("hostIp"),

    CPU_LIMIT("cpuLimit"),

    GC_COUNT("gcCount"),

    PATH_ID("pathId"),

    LOG_MODE_ID("logModeId"),

    HOSTNAME("hostname"),

    FD_COUNT("fdCount"),

    LIMIT_TPS("limitTps"),

    MEMORY_USAGE("memoryUsage"),

    START_TIME("startTime"),

    LOG_PATH_KEY("logPathKey"),

    MESSAGE_VERSION("messageVersion"),

    // 采集任务维度
    READ_TIME_MEAN("readTimeMean"),

    FILTER_REMAINED("filterRemained"),

    CHANNEL_CAPACITY("channelCapacity"),

    IS_FILE_EXIST("isFileExist"),

    TYPE("type"),

    READ_COUNT("readCount"),

    SEND_TIME_MEAN("sendTimeMean"),

    MASTER_FILE("masterFile"),

    PATH("path"),

    SINK_NUM("sinkNum"),

    FLUSH_TIME_MEAN("flushTimeMean"),

    LATEST_FILE("latestFile"),

    FILTER_TOO_LARGE_COUNT("filterTooLargeCount"),

    CHANNEL_TYPE("channelType"),

    LOG_MODEL_VERSION("logModelVersion"),

    TOPIC("topic"),

    FLUSH_COUNT("flushCount"),

    FLUSH_TIME_MAX("flushTimeMax"),

    FILTER_OUT("filterOut"),

    RELATED_FILES("relatedFiles"),

    LOG_MODEL_HOST_NAME("logModelHostName"),

    CLUSTER_ID("clusterId"),

    LIMIT_RATE("limitRate"),

    CONTROL_TIME_MEAN("controlTimeMean"),

    LIMIT_TIME("limitTime"),

    FLUSH_TIME_MIN("flushTimeMin"),

    READ_TIME_MIN("readTimeMin"),

    SEND_TIME_MAX("sendTimeMax"),

    DYNAMIC_LIMITER("dynamicLimiter"),

    MAX_TIME_GAP("maxTimeGap"),

    SEND_BYTE("sendByte"),

    SEND_TIME_MIN("sendTimeMin"),

    LOG_TIME_STR("logTimeStr"),

    CONTROL_TIME_MAX("controlTimeMax"),

    SEND_COUNT("sendCount"),

    SOURCE_TYPE("sourceType"),

    LOG_TIME("logTime"),

    FLUSH_FAILED_COUNT("flushFailedCount"),

    CHANNEL_SIZE("channelSize"),

    FILTER_TOTAL_TOO_LARGE_COUNT("filterTotalTooLargeCount"),

    COLLECT_FILES("collectFiles"),

    IS_FILE_ORDER("collectFiles.isFileOrder"),

    CONTROL_TIME_MIN("controlTimeMin"),

    READ_BYTE("readByte"),

    READ_TIME_MAX("readTimeMax"),

    VALID_TIME_CONFIG("collectFiles.validTimeConfig");

    private String value;

    public String getValue() {
        return value;
    }

    AgentMetricField(String value) {
        this.value = value;
    }

    public static AgentMetricField fromString(String name) {
        if (name == null) {
            return null;
        }
        AgentMetricField f;
        try {
            f = AgentMetricField.valueOf(name);
            return f;
        } catch (IllegalArgumentException e) {
            for (AgentMetricField value : AgentMetricField.values()) {
                if (value.value.equals(name)) {
                    return value;
                }
            }
        }
        return null;
    }
}
