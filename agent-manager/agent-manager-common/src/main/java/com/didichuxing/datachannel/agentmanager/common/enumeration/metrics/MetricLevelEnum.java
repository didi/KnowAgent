package com.didichuxing.datachannel.agentmanager.common.enumeration.metrics;

/**
 * 指标级别枚举
 */
public enum MetricLevelEnum {

    LEVEL_ONE(
            1,
            "一级指标，黄金指标"
    ),
    LEVEL_TWO(
            2,
            "二级指标，表示用户较为关注的指标"
    ),
    LEVEL_THREE(
            3,
            "三级指标，普通指标"
    )

    ;

    private Integer code;
    private String description;

    MetricLevelEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
