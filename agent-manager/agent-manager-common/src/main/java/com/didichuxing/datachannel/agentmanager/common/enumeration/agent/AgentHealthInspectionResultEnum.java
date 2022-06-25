package com.didichuxing.datachannel.agentmanager.common.enumeration.agent;

/**
 * @author huqidong
 * @date 2020-09-21agentMetricsReceiverConnectivities
 * 日志采集任务健康度巡检结果枚举
 */
public enum AgentHealthInspectionResultEnum {

    HOST_OF_AGENT_NOT_ALIVE(2, "Agent宿主机%s故障-无法连通", AgentHealthLevelEnum.RED),
    AGENT_METRICS_CONFIGURATION_NOT_EXISTS(3, "Agent%s没有配置指标流对应下游接收端信息", AgentHealthLevelEnum.RED),
    AGENT_METRICS_CONFIGURATION_ERROR(5, "Agent%s的指标流对应下游接收端生产者配置{producerConfiguration:%s, topic:%s}错误", AgentHealthLevelEnum.RED),
    AGENT_METRICS_RECEIVER_NOT_CONNECTED(6, "Agent%s的指标流到下游接收端%s的连通性存在异常", AgentHealthLevelEnum.RED),
    AGENT_ERRORLOGS_CONFIGURATION_NOT_EXISTS(7, "Agent%s没有配置错误日志流对应下游接收端信息", AgentHealthLevelEnum.RED),
    AGENT_ERRORLOGS_RECEIVER_NOT_CONNECTED(9, "Agent%s的错误日志流到下游接收端%s的连通性存在异常", AgentHealthLevelEnum.RED),
    AGENT_ERRORLOGS_CONFIGURATION_ERROR(10, "Agent%s的错误日志流对应下游接收端生产者配置{producerConfiguration:%s, topic:%s}错误", AgentHealthLevelEnum.RED),
    AGENT_PROCESS_BROKES_DOWN(11, "Agent%s进程故障", AgentHealthLevelEnum.RED),
    AGENT_ERROR_LOGS_EXISTS(14, "Agent%s存在错误日志输出。注意：错误日志属于Agent自身，非其他任何日志采集任务，不影响Agent上运行的任何日志采集任务对应数据完整性", AgentHealthLevelEnum.YELLOW),
    AGENT_STARTUP_FREQUENTLY(15, "Agent%s存在非人工启动过频情况", AgentHealthLevelEnum.YELLOW),
    AGENT_CPU_USAGE_METRIC_EXCEPTION(16, "Agent%s进程在近10分钟内存在CPU使用率超过Agent CPU使用率限流阈值%s的情况", AgentHealthLevelEnum.YELLOW),
    AGENT_GC_METRIC_EXCEPTION(17, "Agent%s进程在近1小时内存在频繁Full GC的情况", AgentHealthLevelEnum.YELLOW),
    AGENT_FD_USAGE_METRIC_EXCEPTION(18, "Agent%s进程在近10分钟内存在FD使用量过多的情况", AgentHealthLevelEnum.YELLOW),
    NOT_RELATE_ANY_LOGCOLLECTTASK(25, "Agent%s未关联任何日志采集任务", AgentHealthLevelEnum.YELLOW),
    HOST_SYSTEM_TIME_IN_VALID(26, "Agent宿主机%s的系统时间不准确", AgentHealthLevelEnum.RED),
    AGENT_ERROR_LOGS_SEND_FAILED_EXISTS_CAUSE_BY_AGENT_PROCESS_BREAK_DOWN(28, "Agent%s进程故障导致Agent的错误日志发送至下游接收端错误", AgentHealthLevelEnum.RED),
    HEALTHY(0, "Agent健康", AgentHealthLevelEnum.GREEN);

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
