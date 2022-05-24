package com.didichuxing.datachannel.agentmanager.common.enumeration;

public enum OperatorEnum {

    GT(">", 0),
    GE(">=", 1),
    LT("<", 2),
    LE("<=", 3),
    EQ("=", 4)

    ;

    OperatorEnum(String operator, Integer operatorType) {
        this.operator = operator;
        this.operatorType = operatorType;
    }

    public String getOperator() {
        return operator;
    }

    public Integer getOperatorType() {
        return operatorType;
    }

    private String operator;
    private Integer operatorType;

}
