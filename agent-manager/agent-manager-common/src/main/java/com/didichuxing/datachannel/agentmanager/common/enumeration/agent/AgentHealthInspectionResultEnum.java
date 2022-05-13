package com.didichuxing.datachannel.agentmanager.common.enumeration.agent;

/**
 * @author huqidong
 * @date 2020-09-21agentMetricsReceiverConnectivities
 * 日志采集任务健康度巡检结果枚举
 */
public enum AgentHealthInspectionResultEnum {

    AGENT_HEART_BEAT_NOT_EXISTS(1, "agent 心跳不存在", AgentHealthLevelEnum.RED),
    HOST_OF_AGENT_NOT_ALIVE(2, "Agent宿主机故障-无法连通", AgentHealthLevelEnum.RED),
    AGENT_METRICS_CONFIGURATION_NOT_EXISTS(3, "Agent没有配置Metrics流对应下游接收端信息", AgentHealthLevelEnum.RED),
    AGENT_METRICS_CONFIGURATION_ERROR_OR_RECEIVER_NOT_CONNECTED(4, "Agent的Metrics流对应下游接收端生产者配置错误或到下游接收端连通性存在异常", AgentHealthLevelEnum.RED),
    AGENT_METRICS_CONFIGURATION_ERROR(5, "Agent的Metrics流对应下游接收端生产者配置错误", AgentHealthLevelEnum.RED),
    AGENT_METRICS_RECEIVER_NOT_CONNECTED(6, "Agent的Metrics流到下游接收端连通性存在异常", AgentHealthLevelEnum.RED),
    AGENT_ERRORLOGS_CONFIGURATION_NOT_EXISTS(7, "Agent没有配置ErrorLogs流对应下游接收端信息", AgentHealthLevelEnum.RED),
    AGENT_ERRORLOGS_CONFIGURATION_ERROR_OR_RECEIVER_NOT_CONNECTED(8, "Agent的ErrorLogs流对应下游接收端生产者配置错误或到下游接收端连通性存在异常", AgentHealthLevelEnum.RED),
    AGENT_ERRORLOGS_RECEIVER_NOT_CONNECTED(9, "Agent的ErrorLogs流到下游接收端连通性存在异常", AgentHealthLevelEnum.RED),
    AGENT_ERRORLOGS_CONFIGURATION_ERROR(10, "Agent的ErrorLogs流对应下游接收端生产者配置错误", AgentHealthLevelEnum.RED),
    AGENT_PROCESS_BROKES_DOWN(11, "Agent进程故障", AgentHealthLevelEnum.RED),
    AGENT_METRICS_KAFKA_CONFIG_ERROR(12, "agent metrics 流对下游 kafka 配置项错误",  AgentHealthLevelEnum.RED),
    AGENT_ERRORLOGS_KAFKA_CONFIG_ERROR(13, "agent errorlogs 流对下游 kafka 配置项错误",  AgentHealthLevelEnum.RED),
    AGENT_ERROR_LOGS_EXISTS(14, "agent 自身存在 errorlogs 输出 注：errorlogs 属 agent 自身，而非其他任何日志采集任务", AgentHealthLevelEnum.YELLOW),
    AGENT_STARTUP_FREQUENTLY(15, "agent 非人工启动过频", AgentHealthLevelEnum.YELLOW),
    AGENT_CPU_USAGE_METRIC_EXCEPTION(16, "agent 进程 cpu 使用率指标异常", AgentHealthLevelEnum.YELLOW),
    AGENT_GC_METRIC_EXCEPTION(17, "agent 进程 gc 指标异常", AgentHealthLevelEnum.YELLOW),
    AGENT_FD_USAGE_METRIC_EXCEPTION(18, "agent 进程 fd 使用量指标异常", AgentHealthLevelEnum.YELLOW),
    ERRORLOGS_TOPIC_LIMIT_EXISTS(19, "errorlogs 对应下游 topic 被限流", AgentHealthLevelEnum.YELLOW),
    METRICS_TOPIC_LIMIT_EXISTS(20, "metrics 对应下游 topic 被限流", AgentHealthLevelEnum.YELLOW),
    HOST_CPU_LIMIT_EXISTS(21, "日志采集任务在对应主机端存在 CPU 阀值限流", AgentHealthLevelEnum.YELLOW),
    HOST_BYTES_LIMIT_EXISTS(22, "日志采集任务在对应主机端存在出口流量阀值限流", AgentHealthLevelEnum.YELLOW),
    ERRORLOGS_SINK_TOPIC_ERROR_FREQUENTLY(23, "errorlogs 数据写入下游 topic 失败频繁", AgentHealthLevelEnum.YELLOW),
    METRICS_SINK_TOPIC_ERROR_FREQUENTLY(24, "metrics 数据写入下游 topic 失败频繁", AgentHealthLevelEnum.YELLOW),
    NOT_RELATE_ANY_LOGCOLLECTTASK(25, "agent 未关联任何日志采集任务", AgentHealthLevelEnum.YELLOW),
    HOST_SYSTEM_TIME_IN_VALID(26, "agent 主机系统时间不正确", AgentHealthLevelEnum.RED),
    AGENT_ERROR_LOGS_SEND_FAILED_EXISTS(27, "agent 错误日志发送下游接收端错误", AgentHealthLevelEnum.RED),
    AGENT_ERROR_LOGS_SEND_FAILED_EXISTS_CAUSE_BY_AGENT_PROCESS_BREAK_DOWN(28, "Agent进程故障导致Agent错误日志发送下游接收端错误", AgentHealthLevelEnum.RED),
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
