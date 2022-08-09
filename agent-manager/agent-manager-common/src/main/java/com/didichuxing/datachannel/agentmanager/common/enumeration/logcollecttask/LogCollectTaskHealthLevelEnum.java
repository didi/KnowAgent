package com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask;

import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务健康度枚举
 */
public enum LogCollectTaskHealthLevelEnum {
    GREEN(0, "日志采集任务很健康，对业务没有任何影响，且运行该日志采集任务的Agent也健康"),
    YELLOW(1, "日志采集任务存在风险，该日志采集任务有对应错误日志输出"),
    RED(2, "日志采集任务不健康，对业务有影响，如：该日志采集任务需要做日志采集延迟监控但乱序输出，或该日志采集任务需要做采集延迟监控但延迟时间超过指定阈值，或该采集任务对应 kafka 集群信息不存在、待维护");

    private Integer code;
    private String description;

    LogCollectTaskHealthLevelEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static LogCollectTaskHealthLevelEnum fromMetricCode(Integer metricCode) {
        for (LogCollectTaskHealthLevelEnum value : LogCollectTaskHealthLevelEnum.values()) {
            if (value.code.equals(metricCode)) {
                return value;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }
    public String getDescription() {
        return description;
    }
}
