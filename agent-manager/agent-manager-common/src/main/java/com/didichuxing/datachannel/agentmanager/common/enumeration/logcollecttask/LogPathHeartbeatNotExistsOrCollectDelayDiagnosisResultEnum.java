package com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务出现某待采集文件路径心跳不存在或采集延时诊断结果枚举
 */
public enum LogPathHeartbeatNotExistsOrCollectDelayDiagnosisResultEnum {

    HOST_NOT_COLLECTED_BY_ANY_AGENT(1, "待采集主机未被任何agent所采集"),
    HOST_NOT_ALIVE(2, "待采集主机已宕机"),
    AGENT_PARENT_HOST_NOT_ALIVE(3, "agent进程对应宿主机宕机"),
    AGENT_NOT_ALIVE(4, "agent进程宕机"),
    KAFKA_CONFIG_ERROR(5, "下游 kafka 配置项错误"),
    LOG_SINK_TOPIC_ERROR(6, "下游 kafka 写入失败"),
    TOPIC_LIMIT_EXISTS(7, "日志采集任务对应下游topic被限流"),
    HOST_CPU_LIMIT_EXISTS(8, "日志采集任务在对应主机端存在 CPU 阀值限流"),
    HOST_BYTES_LIMIT_EXISTS(9, "日志采集任务在对应主机端存在出口流量阀值限流"),
    AGENT_PERFORMANCE_LIMITED(10,"达到 agent 进程采集性能极限 注：该情况展示出现性能问题 agent 进程全景指标视图");

    private Integer code;
    private String description;

    public Integer getCode() {
        return code;
    }
    public String getDescription() {
        return description;
    }

    LogPathHeartbeatNotExistsOrCollectDelayDiagnosisResultEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
