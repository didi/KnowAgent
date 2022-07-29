package com.didichuxing.datachannel.agentmanager.common.enumeration.host;

/**
 * @author huqidong
 * @date 2020-09-21
 * 主机类型枚举类
 */
public enum HostTypeEnum {

    HOST(0, "物理机"),
    CONTAINER(1, "容器");

    private Integer code;
    private String description;

    HostTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
