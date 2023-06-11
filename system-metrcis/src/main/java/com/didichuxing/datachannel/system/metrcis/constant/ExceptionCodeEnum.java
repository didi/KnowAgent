package com.didichuxing.datachannel.system.metrcis.constant;

/**
 * 异常状态码枚举.
 * @author william.
 */
public enum ExceptionCodeEnum {

    SYSTEM_NOT_SUPPORT(21001, "系统不支持");

    private Integer    code;
    private String desc;

    ExceptionCodeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
