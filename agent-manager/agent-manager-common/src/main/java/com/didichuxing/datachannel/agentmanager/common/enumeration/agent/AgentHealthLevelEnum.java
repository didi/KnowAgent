package com.didichuxing.datachannel.agentmanager.common.enumeration.agent;

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

}
