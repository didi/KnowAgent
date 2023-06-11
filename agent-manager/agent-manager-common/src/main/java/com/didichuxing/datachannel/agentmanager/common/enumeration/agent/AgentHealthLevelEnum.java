package com.didichuxing.datachannel.agentmanager.common.enumeration.agent;

import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricFieldEnum;

public enum AgentHealthLevelEnum {

    RED(0,"不健康，且对业务有影响"),
    YELLOW(1,"不健康，对业务没影响"),
    GREEN(2,"健康");

    AgentHealthLevelEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    private Integer code;

    private String description;

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据指标代码返回对应指标枚举定义
     * @param metricCode 指标名
     * @return 如指标代码在指标枚举集存在，返回指标名对应指标枚举定义，如不存在，返回 null
     */
    public static AgentHealthLevelEnum fromMetricCode(Integer metricCode) {
        for (AgentHealthLevelEnum value : AgentHealthLevelEnum.values()) {
            if (value.code.equals(metricCode)) {
                return value;
            }
        }
        return null;
    }

}
