package com.didichuxing.datachannel.agentmanager.common.enumeration.metrics;

/**
 * 指标展示类型
 */
public enum MetricDisplayTypeEnum {

    LABLE(1,"lable类型指标"),
    MULTI_LINE_CHAT(2, "折线图-多根类型指标"),
    SINGLE_LINE_CHAT(3,"折线图-单根类型指标"),
    HISTOGRAM_CHAT(4, "柱状图")
    ;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    MetricDisplayTypeEnum(Integer code, String desc) {
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
