package com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务类型枚举
 */
public enum LogCollectTaskTypeEnum {
    NORMAL_COLLECT(0, "常规流式采集"),
    TIME_SCOPE_COLLECT(1, "按指定时间范围采集");

    private Integer code;
    private String description;

    LogCollectTaskTypeEnum(Integer code, String description) {
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
