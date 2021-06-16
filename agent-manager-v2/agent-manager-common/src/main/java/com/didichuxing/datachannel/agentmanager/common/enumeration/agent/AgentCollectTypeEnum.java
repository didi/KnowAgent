package com.didichuxing.datachannel.agentmanager.common.enumeration.agent;

/**
 * @author huqidong
 * @date 2020-09-21
 * Agent采集方式枚举类
 */
public enum AgentCollectTypeEnum {

    COLLECT_HOST_ONLY(0, "仅采集宿主机日志"),
    COLLECT_CONTAINERS_ONLY(1, "仅采集宿主机挂载的所有容器日志"),
    COLLECT_HOST_AND_CONTAINERS(2, "采集宿主机日志 & 宿主机挂载的所有容器日志");

    private Integer code;
    private String description;

    AgentCollectTypeEnum(Integer code, String description) {
        this.code = code;
        description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
