package com.didichuxing.datachannel.system.metrcis.constant;

/**
 * 操作系统类型
 * @author william.
 */
public enum OSTypeEnum {

    LINUX("linux", 0), MAC_OS("mac", 1), WINDOWS("windows", 2), AIX("aix", 3);

    private String desc;

    OSTypeEnum(String desc, Integer code) {
        this.desc = desc;
        this.code = code;
    }

    private Integer code;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
