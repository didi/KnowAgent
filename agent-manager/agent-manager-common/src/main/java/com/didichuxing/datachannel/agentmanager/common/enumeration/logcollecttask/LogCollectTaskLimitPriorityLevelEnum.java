package com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务限流等级枚举
 */
public enum LogCollectTaskLimitPriorityLevelEnum {
    HIGH(0, "高：最高等级保障"),
    MIDDLE(1, "中：正常等级保障"),
    LOW(2, "低：最低等级保障");

    private Integer code;
    private String description;

    LogCollectTaskLimitPriorityLevelEnum(Integer code, String description) {
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
