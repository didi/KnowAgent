package com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务历史数据过滤类型枚举
 */
public enum LogCollectTaskOldDataFilterTypeEnum {
    NO(0, "不过滤"),
    FROM_CURRENT_TIME(1, "从当前时间开始采集"),
    CUSTOM_TIME(2, "从自定义时间开始采集");

    private int code;
    private String description;

    LogCollectTaskOldDataFilterTypeEnum(int code, String description) {
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
