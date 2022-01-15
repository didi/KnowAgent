package com.didichuxing.datachannel.agentmanager.common.enumeration.metrics;

public enum AgentMetricField {
    // agent维度
    CPU_USAGE("cpu_usage", "cpuUsage"),

    HEARTBEAT_TIME("heartbeat_time", "heartbeatTime"),

    HOST_IP("host_ip", "hostIp"),

    CPU_LIMIT("cpu_limit", "cpuLimit"),

    GC_COUNT("gc_count", "gcCount"),

    PATH_ID("path_id", "pathId"),

    LOG_MODE_ID("log_mode_id", "logModeId"),

    HOSTNAME("hostname", "hostname"),

    FD_COUNT("fd_count", "fdCount"),

    LIMIT_TPS("limit_tps", "limitTps"),

    MEMORY_USAGE("memory_usage", "memoryUsage"),

    START_TIME("start_time", "startTime"),

    LOG_PATH_KEY("log_path_key", "logPathKey"),

    MESSAGE_VERSION("message_version", "messageVersion"),

    // 采集任务维度
    READ_TIME_MEAN("read_time_mean", "readTimeMean"),

    FILTER_REMAINED("filter_remained", "filterRemained"),

    CHANNEL_CAPACITY("channel_capacity", "channelCapacity"),

    IS_FILE_EXIST("is_file_exist", "isFileExist"),

    TYPE("type", "type"),

    READ_COUNT("read_count", "readCount"),

    SEND_TIME_MEAN("send_time_mean", "sendTimeMean"),

    MASTER_FILE("master_file", "masterFile"),

    PATH("path", "path"),

    SINK_NUM("sink_num", "sinkNum"),

    FLUSH_TIME_MEAN("flush_time_mean", "flushTimeMean"),

    LATEST_FILE("latest_file", "latestFile"),

    FILTER_TOO_LARGE_COUNT("filter_too_large_count", "filterTooLargeCount"),

    CHANNEL_TYPE("channel_type", "channelType"),

    LOG_MODEL_VERSION("log_model_version", "logModelVersion"),

    TOPIC("topic", "topic"),

    FLUSH_COUNT("flush_count", "flushCount"),

    FLUSH_TIME_MAX("flush_time_max", "flushTimeMax"),

    FILTER_OUT("filter_out", "filterOut"),

    RELATED_FILES("related_files", "relatedFiles"),

    LOG_MODEL_HOST_NAME("log_model_host_name", "logModelHostName"),

    CLUSTER_ID("cluster_id", "clusterId"),

    LIMIT_RATE("limit_rate", "limitRate"),

    CONTROL_TIME_MEAN("control_time_mean", "controlTimeMean"),

    LIMIT_TIME("limit_time", "limitTime"),

    FLUSH_TIME_MIN("flush_time_min", "flushTimeMin"),

    READ_TIME_MIN("read_time_min", "readTimeMin"),

    SEND_TIME_MAX("send_time_max", "sendTimeMax"),

    DYNAMIC_LIMITER("dynamic_limiter", "dynamicLimiter"),

    MAX_TIME_GAP("max_time_gap", "maxTimeGap"),

    SEND_BYTE("send_byte", "sendByte"),

    SEND_TIME_MIN("send_time_min", "sendTimeMin"),

    LOG_TIME_STR("log_time_str", "logTimeStr"),

    CONTROL_TIME_MAX("control_time_max", "controlTimeMax"),

    SEND_COUNT("send_count", "sendCount"),

    SOURCE_TYPE("source_type", "sourceType"),

    LOG_TIME("log_time", "logTime"),

    FLUSH_FAILED_COUNT("flush_failed_count", "flushFailedCount"),

    CHANNEL_SIZE("channel_size", "channelSize"),

    FILTER_TOTAL_TOO_LARGE_COUNT("filter_total_too_large_count", "filterTotalTooLargeCount"),

    COLLECT_FILES("collect_files", "collectFiles"),

    IS_FILE_ORDER("isFileOrder", "collectFiles.isFileOrder"),

    CONTROL_TIME_MIN("control_time_min", "controlTimeMin"),

    READ_BYTE("read_byte", "readByte"),

    READ_TIME_MAX("read_time_max", "readTimeMax"),

    VALID_TIME_CONFIG("valid_time_config", "collectFiles.validTimeConfig");

    /**
     * es存储字段，也是原生字段
     */
    private String esValue;

    /**
     * rds字段，用下划线方式命名
     */
    private String rdsValue;

    public String getEsValue() {
        return esValue;
    }

    public String getRdsValue() {
        return rdsValue;
    }

    AgentMetricField(String rdsValue, String esValue) {
        this.rdsValue = rdsValue;
        this.esValue = esValue;
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
                if (value.esValue.equals(name) || value.rdsValue.equals(name)) {
                    return value;
                }
            }
        }
        return null;
    }
}
