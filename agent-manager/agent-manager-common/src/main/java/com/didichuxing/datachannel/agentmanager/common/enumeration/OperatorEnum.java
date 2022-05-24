package com.didichuxing.datachannel.agentmanager.common.enumeration;

public enum OperatorEnum {

    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),
    EQ("=")

    ;


    OperatorEnum(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    private String operator;

}
