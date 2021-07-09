package com.didichuxing.datachannel.agentmanager.common.enumeration;

/**
 * @author huqidong
 * @date 2020-09-21
 * 是或否枚举类
 */
public enum YesOrNoEnum {

    NO(0, "否"),
    YES(1, "是");

    private Integer code;
    private String description;

    YesOrNoEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static YesOrNoEnum valueOf(Integer code) {
        if(code.equals(YES.getCode())) {
            return YES;
        } else if(code.equals(NO.getCode())) {
            return NO;
        } else {
            return null;
        }
    }

}
