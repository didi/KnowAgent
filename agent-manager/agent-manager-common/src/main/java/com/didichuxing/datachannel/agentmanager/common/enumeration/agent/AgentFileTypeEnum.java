package com.didichuxing.datachannel.agentmanager.common.enumeration.agent;

/**
 * @author huqidong
 * @date 2020-09-21
 * 文件类型枚举类
 */
public enum AgentFileTypeEnum {

    AGENT_INSTALL_FILE(0, "agent安装文件"),
    AGENT_CONFIGURATION_FILE(1, "agent配置文件");

    private Integer code;
    private String description;

    AgentFileTypeEnum(Integer code, String description) {
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
