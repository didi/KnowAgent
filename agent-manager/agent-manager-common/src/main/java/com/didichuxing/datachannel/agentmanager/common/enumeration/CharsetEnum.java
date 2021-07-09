package com.didichuxing.datachannel.agentmanager.common.enumeration;

public enum CharsetEnum {

    UTF8(0, "utf-8");

    CharsetEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;

    private String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
