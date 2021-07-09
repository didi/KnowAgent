package com.didichuxing.datachannel.agentmanager.common.enumeration.host;

/**
 * @author huqidong
 * @date 2020-09-21
 * 主机过滤规则逻辑枚举
 */
public enum HostFilterRuleLogicEnum {

    ExpressionHostFilterRuleLogic(0, "表达式类型的主机过滤规则逻辑");

    private int code;
    private String description;

    HostFilterRuleLogicEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }
    public String getDescription() {
        return description;
    }

}
