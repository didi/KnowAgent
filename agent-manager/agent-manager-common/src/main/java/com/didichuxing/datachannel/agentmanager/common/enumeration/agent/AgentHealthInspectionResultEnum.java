package com.didichuxing.datachannel.agentmanager.common.enumeration.agent;

/**
 * @author huqidong
 * @date 2020-09-21agentMetricsReceiverConnectivities
 * 日志采集任务健康度巡检结果枚举
 */
public enum AgentHealthInspectionResultEnum {

    AGENT_HEART_BEAT_NOT_EXISTS(1, "agent 心跳不存在", AgentHealthLevelEnum.RED),
    HOST_OF_AGENT_NOT_ALIVE(2, "agent 宿主机不存活", AgentHealthLevelEnum.RED),
    AGENT_METRICS_CONFIGURATION_NOT_EXISTS(3, "agent 没有配置metrics流对应下游接收端信息", AgentHealthLevelEnum.RED),
    AGENT_METRICS_RECEIVER_NOT_CONNECTED(4, "agent的metrics流对应下游接收端连通性不正常", AgentHealthLevelEnum.RED),
    AGENT_ERRORLOGS_CONFIGURATION_NOT_EXISTS(5, "agent 没有配置errorlogs流对应下游接收端信息", AgentHealthLevelEnum.RED),
    AGENT_ERRORLOGS_RECEIVER_NOT_CONNECTED(6, "agent的errorlogs流对应下游接收端连通性不正常", AgentHealthLevelEnum.RED),
    AGENT_PROCESS_BROKES_DOWN(7, "agent 进程故障", AgentHealthLevelEnum.RED),
    AGENT_METRICS_KAFKA_CONFIG_ERROR(8, "agent metrics 流对下游 kafka 配置项错误",  AgentHealthLevelEnum.RED),
    AGENT_ERRORLOGS_KAFKA_CONFIG_ERROR(9, "agent errorlogs 流对下游 kafka 配置项错误",  AgentHealthLevelEnum.RED),
    AGENT_ERROR_LOGS_EXISTS(10, "agent 自身存在 errorlogs 输出 注：errorlogs 属 agent 自身，而非其他任何日志采集任务", AgentHealthLevelEnum.YELLOW),
    AGENT_STARTUP_FREQUENTLY(11, "agent 非人工启动过频", AgentHealthLevelEnum.YELLOW),
    AGENT_CPU_USAGE_METRIC_EXCEPTION(12, "agent 进程 cpu 使用率指标异常", AgentHealthLevelEnum.YELLOW),
    AGENT_GC_METRIC_EXCEPTION(13, "agent 进程 gc 指标异常", AgentHealthLevelEnum.YELLOW),
    AGENT_FD_USAGE_METRIC_EXCEPTION(14, "agent 进程 fd 使用量指标异常", AgentHealthLevelEnum.YELLOW),
    ERRORLOGS_TOPIC_LIMIT_EXISTS(15, "errorlogs 对应下游 topic 被限流", AgentHealthLevelEnum.YELLOW),
    METRICS_TOPIC_LIMIT_EXISTS(16, "metrics 对应下游 topic 被限流", AgentHealthLevelEnum.YELLOW),
    HOST_CPU_LIMIT_EXISTS(17, "日志采集任务在对应主机端存在 CPU 阀值限流", AgentHealthLevelEnum.YELLOW),
    HOST_BYTES_LIMIT_EXISTS(18, "日志采集任务在对应主机端存在出口流量阀值限流", AgentHealthLevelEnum.YELLOW),
    ERRORLOGS_SINK_TOPIC_ERROR_FREQUENTLY(19, "errorlogs 数据写入下游 topic 失败频繁", AgentHealthLevelEnum.YELLOW),
    METRICS_SINK_TOPIC_ERROR_FREQUENTLY(20, "metrics 数据写入下游 topic 失败频繁", AgentHealthLevelEnum.YELLOW),
    NOT_RELATE_ANY_LOGCOLLECTTASK(21, "agent 未关联任何日志采集任务", AgentHealthLevelEnum.YELLOW),
    HOST_SYSTEM_TIME_IN_VALID(22, "agent 主机系统时间不正确", AgentHealthLevelEnum.RED),
    AGENT_ERROR_LOGS_SEND_FAILED_EXISTS(23, "agent 错误日志发送下游接收端错误", AgentHealthLevelEnum.RED),
    HEALTHY(0, "agent 健康", AgentHealthLevelEnum.GREEN);

    private Integer code;
    private String description;
    private AgentHealthLevelEnum agentHealthLevel;


    public Integer getCode() {
        return code;
    }
    public String getDescription() {
        return description;
    }
    public AgentHealthLevelEnum getAgentHealthLevel() {
        return agentHealthLevel;
    }

    AgentHealthInspectionResultEnum(Integer code, String description, AgentHealthLevelEnum agentHealthLevel) {
        this.code = code;
        this.description = description;
        this.agentHealthLevel = agentHealthLevel;
    }

}
