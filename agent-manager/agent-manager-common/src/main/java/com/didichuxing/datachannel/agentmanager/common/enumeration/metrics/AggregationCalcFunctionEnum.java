package com.didichuxing.datachannel.agentmanager.common.enumeration.metrics;

/**
 * normal 无函数
 * sum 求和函数
 * count 计数
 * avg 平均函数
 * max,min 最大、最小函数
 */
public enum AggregationCalcFunctionEnum {

    NONE("none"),
    SUM("sum"),
    COUNT("count"),
    AVG("avg"),
    MAX("max"),
    MIN("min");

    private String value;

    public String getValue() {
        return value;
    }

    AggregationCalcFunctionEnum(String value) {
        this.value = value;
    }

    public static AggregationCalcFunctionEnum fromString(String name) {
        if (name == null) {
            return null;
        }
        AggregationCalcFunctionEnum f;
        try {
            f = AggregationCalcFunctionEnum.valueOf(name);
            return f;
        } catch (IllegalArgumentException e) {
            for (AggregationCalcFunctionEnum value : AggregationCalcFunctionEnum.values()) {
                if (value.value.equals(name)) {
                    return value;
                }
            }
        }
        return null;
    }
}
