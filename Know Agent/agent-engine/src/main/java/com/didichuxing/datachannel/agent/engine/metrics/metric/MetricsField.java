package com.didichuxing.datachannel.agent.engine.metrics.metric;

public enum MetricsField {

    // agent维度
    CPU_USAGE("cpuUsage", "cpu_usage", "cpuUsage"),

    HEARTBEAT_TIME("heartbeatTime", "heartbeat_time", "heartbeatTime"),

    HOST_IP("hostIp", "host_ip", "hostIp"),

    CPU_LIMIT("cpuLimit", "cpu_limit", "cpuLimit"),

    GC_COUNT("gcCount", "gc_count", "gcCount"),

    PATH_ID("pathId", "path_id", "pathId"),

    LOG_MODE_ID("logModeId", "log_mode_id", "logModeId"),

    HOSTNAME("hostname", "hostname", "hostname"),

    FD_COUNT("fdCount", "fd_count", "fdCount"),

    LIMIT_TPS("limitTps", "limit_tps", "limitTps"),

    MEMORY_USAGE("memoryUsage", "memory_usage", "memoryUsage"),

    START_TIME("startTime", "start_time", "startTime"),

    LOG_PATH_KEY("logPathKey", "log_path_key", "logPathKey"),

    MESSAGE_VERSION("messageVersion", "message_version", "messageVersion"),

    // 采集任务维度
    READ_TIME_MEAN("readTimeMean", "read_time_mean", "readTimeMean"),

    FILTER_REMAINED("filterRemained", "filter_remained", "filterRemained"),

    CHANNEL_CAPACITY("channelCapacity", "channel_capacity", "channelCapacity"),

    IS_FILE_EXIST("isFileExist", "is_file_exist", "isFileExist"),

    TYPE("type", "type", "type"),

    READ_COUNT("readCount", "read_count", "readCount"),

    SEND_TIME_MEAN("sendTimeMean", "send_time_mean", "sendTimeMean"),

    MASTER_FILE("masterFile", "master_file", "masterFile"),

    PATH("path", "path", "path"),

    SINK_NUM("sinkNum", "sink_num", "sinkNum"),

    FLUSH_TIME_MEAN("flushTimeMean", "flush_time_mean", "flushTimeMean"),

    LATEST_FILE("latestFile", "latest_file", "latestFile"),

    FILTER_TOO_LARGE_COUNT("filterTooLargeCount", "filter_too_large_count", "filterTooLargeCount"),

    CHANNEL_TYPE("channelType", "channel_type", "channelType"),

    LOG_MODEL_VERSION("logModelVersion", "log_model_version", "logModelVersion"),

    TOPIC("topic", "topic", "topic"),

    FLUSH_COUNT("flushCount", "flush_count", "flushCount"),

    FLUSH_TIME_MAX("flushTimeMax", "flush_time_max", "flushTimeMax"),

    FILTER_OUT("filterOut", "filter_out", "filterOut"),

    RELATED_FILES("relatedFiles", "related_files", "relatedFiles"),

    LOG_MODEL_HOST_NAME("logModelHostName", "log_model_host_name", "logModelHostName"),

    CLUSTER_ID("clusterId", "cluster_id", "clusterId"),

    LIMIT_RATE("limitRate", "limit_rate", "limitRate"),

    CONTROL_TIME_MEAN("controlTimeMean", "control_time_mean", "controlTimeMean"),

    LIMIT_TIME("limitTime", "limit_time", "limitTime"),

    FLUSH_TIME_MIN("flushTimeMin", "flush_time_min", "flushTimeMin"),

    READ_TIME_MIN("readTimeMin", "read_time_min", "readTimeMin"),

    SEND_TIME_MAX("sendTimeMax", "send_time_max", "sendTimeMax"),

    DYNAMIC_LIMITER("dynamicLimiter", "dynamic_limiter", "dynamicLimiter"),

    MAX_TIME_GAP("maxTimeGap", "max_time_gap", "maxTimeGap"),

    SEND_BYTE("sendByte", "send_byte", "sendByte"),

    SEND_TIME_MIN("sendTimeMin", "send_time_min", "sendTimeMin"),

    LOG_TIME_STR("logTimeStr", "log_time_str", "logTimeStr"),

    CONTROL_TIME_MAX("controlTimeMax", "control_time_max", "controlTimeMax"),

    SEND_COUNT("sendCount", "send_count", "sendCount"),

    SOURCE_TYPE("sourceType", "source_type", "sourceType"),

    LOG_TIME("logTime", "log_time", "logTime"),

    FLUSH_FAILED_COUNT("flushFailedCount", "flush_failed_count", "flushFailedCount"),

    CHANNEL_SIZE("channelSize", "channel_size", "channelSize"),

    FILTER_TOTAL_TOO_LARGE_COUNT("filterTotalTooLargeCount", "filter_total_too_large_count",
                                 "filterTotalTooLargeCount"),

    COLLECT_FILES("collectFiles", "collect_files", "collectFiles"),

    IS_FILE_ORDER("collectFiles.isFileOrder", "isFileOrder", "collectFiles.isFileOrder"),

    CONTROL_TIME_MIN("controlTimeMin", "control_time_min", "controlTimeMin"),

    READ_BYTE("readByte", "read_byte", "readByte"),

    READ_TIME_MAX("readTimeMax", "read_time_max", "readTimeMax"),

    VALID_TIME_CONFIG("collectFiles.validTimeConfig", "valid_time_config",
                      "collectFiles.validTimeConfig");

    /**
     * es存储字段，也是原生字段
     */
    private String esFieldName;

    /**
     * rds字段，用下划线方式命名
     */
    private String rdsFieldName;

    private String agentFieldName;

    public String getEsFieldName() {
        return esFieldName;
    }

    public String getRdsFieldName() {
        return rdsFieldName;
    }

    public String getAgentFieldName() {
        return agentFieldName;
    }

    MetricsField(String agentFieldName, String rdsFieldName, String esFieldName) {
        this.rdsFieldName = rdsFieldName;
        this.esFieldName = esFieldName;
        this.agentFieldName = agentFieldName;
    }

    public static MetricsField fromString(String name) {
        if (name == null) {
            return null;
        }
        MetricsField f;
        try {
            f = MetricsField.valueOf(name);
            return f;
        } catch (IllegalArgumentException e) {
            for (MetricsField value : MetricsField.values()) {
                if (value.esFieldName.equals(name) || value.rdsFieldName.equals(name)) {
                    return value;
                }
            }
        }
        return null;
    }

}
