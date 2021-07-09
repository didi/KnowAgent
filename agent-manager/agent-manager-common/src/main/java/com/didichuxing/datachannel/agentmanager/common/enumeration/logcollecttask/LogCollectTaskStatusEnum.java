package com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务状态枚举
 */
public enum LogCollectTaskStatusEnum {
    STOP(0, "停止"),
    RUNNING(1, "运行中"),
    FINISH(2, "完成");

    private Integer code;
    private String description;

    LogCollectTaskStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }
    public String getDescription() {
        return description;
    }

    public static boolean invalidStatus(Integer status) {
        if(!status.equals(STOP.getCode()) && !status.equals(RUNNING.getCode())) {
            return true;
        }
        return false;
    }

}
