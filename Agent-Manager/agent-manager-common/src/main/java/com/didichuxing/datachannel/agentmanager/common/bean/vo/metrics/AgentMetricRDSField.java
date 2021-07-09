package com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics;

public enum AgentMetricRDSField {
    // agent维度
    CPU_USAGE("cpu_usage"),

    HEARTBEAT_TIME("heartbeat_time"),

    HOST_IP("host_ip"),

    CPU_LIMIT("cpu_limit"),

    GC_COUNT("gc_count"),

    PATH_ID("path_id"),

    LOG_MODE_ID("log_mode_id"),

    HOSTNAME("hostname"),

    FD_COUNT("fd_count"),

    LIMIT_TPS("limit_tps"),

    MEMORY_USAGE("memory_usage"),

    START_TIME("start_time"),

    LOG_PATH_KEY("log_path_key"),

    MESSAGE_VERSION("message_version"),

    // 采集任务维
    READ_TIME_MEAN("read_time_mean"),

    FILTER_REMAINED("filter_remained"),

    CHANNEL_CAPACITY("channel_capacity"),

    IS_FILE_EXIST("is_file_exist"),

    TYPE("type"),

    READ_COUNT("read_count"),

    SEND_TIME_MEAN("send_time_mean"),

    MASTER_FILE("master_file"),

    PATH("path"),

    SINK_NUM("sink_num"),

    FLUSH_TIME_MEAN("flush_time_mean"),

    LATEST_FILE("latest_file"),

    FILTER_TOO_LARGE_COUNT("filter_too_large_count"),

    CHANNEL_TYPE("channel_type"),

    LOG_MODEL_VERSION("log_model_version"),

    TOPIC("topic"),

    FLUSH_COUNT("flush_count"),

    FLUSH_TIME_MAX("flush_time_max"),

    FILTER_OUT("filter_out"),

    RELATED_FILES("related_files"),

    LOG_MODEL_HOST_NAME("log_model_host_name"),

    CLUSTER_ID("cluster_id"),

    LIMIT_RATE("limit_rate"),

    CONTROL_TIME_MEAN("control_time_mean"),

    LIMIT_TIME("limit_time"),

    FLUSH_TIME_MIN("flush_time_min"),

    READ_TIME_MIN("read_time_min"),

    SEND_TIME_MAX("send_time_max"),

    DYNAMIC_LIMITER("dynamic_limiter"),

    MAX_TIME_GAP("max_time_gap"),

    SEND_BYTE("send_byte"),

    SEND_TIME_MIN("send_time_min"),

    LOG_TIME_STR("log_time_str"),

    CONTROL_TIME_MAX("control_time_max"),

    SEND_COUNT("send_count"),

    SOURCE_TYPE("source_type"),

    LOG_TIME("log_time"),

    FLUSH_FAILED_COUNT("flush_failed_count"),

    CHANNEL_SIZE("channel_size"),

    FILTER_TOTAL_TOO_LARGE_COUNT("filter_total_too_large_count"),

    COLLECT_FILES("collect_files"),

    IS_FILE_ORDER("isFileOrder"),

    CONTROL_TIME_MIN("control_time_min"),

    READ_BYTE("read_byte"),

    READ_TIME_MAX("read_time_max"),

    VALID_TIME_CONFIG("valid_time_config");

    private String value;

    public String getValue() {
        return value;
    }

    AgentMetricRDSField(String value) {
        this.value = value;
    }

    public static AgentMetricRDSField fromString(String name) {
        if (name == null) {
            return null;
        }
        AgentMetricRDSField f;
        try {
            f = AgentMetricRDSField.valueOf(name);
            return f;
        } catch (IllegalArgumentException e) {
            for (AgentMetricRDSField value : AgentMetricRDSField.values()) {
                if (value.value.equals(name)) {
                    return value;
                }
            }
        }
        return null;
    }
}
