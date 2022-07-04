package com.didichuxing.datachannel.agentmanager.common.enumeration.metrics;

public enum MetricUnitEnum {

    NONE(0, "无单位"),
    BYTE(1,"字节"),
    M_BYTE(2,"兆字节"),
    TIMESTAMP_MILLISECOND(3,"时间戳-毫秒"),
    TIMESTAMP_SECOND(4,"时间戳-秒"),
    PERCENT(5,"百分比"),
    DATE_TIME(6,"日期/时间"),
    TIMESTAMP_NANOSECOND(7,"时间戳-纳秒"),
    G_BYTE(8," G 字节"),
    ;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    MetricUnitEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 指标展示类型代码
     */
    private Integer code;

    /**
     * 指标展示类型描述
     */
    private String desc;

}
