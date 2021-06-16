package com.didichuxing.datachannel.agentmanager.common.enumeration.agent;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务健康度巡检结果枚举
 */
public enum AgentHealthInspectionResultEnum {

    AGENT_HEART_BEAT_NOT_EXISTS(1, "agent 心跳不存在", AgentHealthLevelEnum.RED),
    AGENT_METRICS_KAFKA_CONFIG_ERROR(2, "agent metrics 流对下游 kafka 配置项错误",  AgentHealthLevelEnum.RED),
    AGENT_ERRORLOGS_KAFKA_CONFIG_ERROR(3, "agent errorlogs 流对下游 kafka 配置项错误",  AgentHealthLevelEnum.RED),
    AGENT_ERRORLOGS_EXISTS(4, "agent 自身是否存在 errorlogs 输出 注：errorlogs 属 agent 自身，而非其他任何日志采集任务", AgentHealthLevelEnum.RED),
    AGENT_STARTUP_FREQUENTLY(5, "agent 非人工启动过频", AgentHealthLevelEnum.YELLOW),
    AGENT_CPU_USAGE_METRIC_EXCEPTION(6, "agent 进程 cpu 使用率指标异常", AgentHealthLevelEnum.YELLOW),
    AGENT_GC_METRIC_EXCEPTION(7, "agent 进程 gc 指标异常", AgentHealthLevelEnum.YELLOW),
    AGENT_FD_USAGE_METRIC_EXCEPTION(8, "agent 进程 fd 使用量指标异常", AgentHealthLevelEnum.YELLOW),
    ERRORLOGS_TOPIC_LIMIT_EXISTS(9, "errorlogs 对应下游 topic 被限流", AgentHealthLevelEnum.YELLOW),
    METRICS_TOPIC_LIMIT_EXISTS(10, "metrics 对应下游 topic 被限流", AgentHealthLevelEnum.YELLOW),
    HOST_CPU_LIMIT_EXISTS(11, "日志采集任务在对应主机端存在 CPU 阀值限流", AgentHealthLevelEnum.YELLOW),
    HOST_BYTES_LIMIT_EXISTS(12, "日志采集任务在对应主机端存在出口流量阀值限流", AgentHealthLevelEnum.YELLOW),
    ERRORLOGS_SINK_TOPIC_ERROR_FREQUENTLY(13, "errorlogs 数据写入下游 topic 失败频繁", AgentHealthLevelEnum.YELLOW),
    METRICS_SINK_TOPIC_ERROR_FREQUENTLY(14, "metrics 数据写入下游 topic 失败频繁", AgentHealthLevelEnum.YELLOW),
    NOT_RELATE_ANY_LOGCOLLECTTASK(15, "agent 未关联任何日志采集任务", AgentHealthLevelEnum.YELLOW),
    HEALTHY(0, "日志采集任务健康", AgentHealthLevelEnum.GREEN);

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
