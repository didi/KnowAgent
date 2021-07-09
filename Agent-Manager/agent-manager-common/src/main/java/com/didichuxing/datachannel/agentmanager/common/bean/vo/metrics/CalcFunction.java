package com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics;

/**
 * normal 无函数
 * sum 求和函数
 * count 计数
 * avg 平均函数
 * max,min 最大、最小函数
 */
public enum CalcFunction {

    NORMAL("normal"),
    SUM("sum"),
    COUNT("count"),
    AVG("avg"),
    MAX("max"),
    MIN("min");

    private String value;

    public String getValue() {
        return value;
    }

    CalcFunction(String value) {
        this.value = value;
    }

    public static CalcFunction fromString(String name) {
        if (name == null) {
            return null;
        }
        CalcFunction f;
        try {
            f = CalcFunction.valueOf(name);
            return f;
        } catch (IllegalArgumentException e) {
            for (CalcFunction value : CalcFunction.values()) {
                if (value.value.equals(name)) {
                    return value;
                }
            }
        }
        return null;
    }
}
