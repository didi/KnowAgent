package com.didichuxing.datachannel.agentmanager.common.enumeration;

public enum FileNameSuffixMatchTypeEnum {

    LENGTH(0, "长度"),
    REGULAR(1, "正则");

    FileNameSuffixMatchTypeEnum(Integer code, String desc) {
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
