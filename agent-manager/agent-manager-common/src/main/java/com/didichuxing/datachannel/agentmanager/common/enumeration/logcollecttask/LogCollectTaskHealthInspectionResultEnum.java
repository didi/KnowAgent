package com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务健康度巡检结果枚举
 */
public enum LogCollectTaskHealthInspectionResultEnum {

    LOG_PATH_IN_HOST_HEART_BEAT_NOT_EXISTS(1, "主机上日志采集路径不存在心跳", LogCollectTaskHealthLevelEnum.RED, false),
    LOG_PATH_NOT_EXISTS(2, "日志采集路径在主机上不存在",  LogCollectTaskHealthLevelEnum.RED, true),
    LOG_PATH_DISORDER(3, "主机上的日志存在乱序输出", LogCollectTaskHealthLevelEnum.RED, true),
    LOG_PATH_LOG_SLICE_ERROR_EXISTS(4, "存在日志切片错误", LogCollectTaskHealthLevelEnum.RED, true),
    LOG_PATH_LOG_SIZE_OVERRUN_TRUNCATE_EXISTS(5, "主机上存在日志大小超限截断", LogCollectTaskHealthLevelEnum.RED, true),
    LOG_PATH_LOG_DISCARD_EXISTS(6, "主机上存在日志丢弃", LogCollectTaskHealthLevelEnum.RED, true),
    LOG_PATH_CONCURRENT_COLLECT(7, "主机上存在日志被多个agent进程并发采集", LogCollectTaskHealthLevelEnum.RED, true),
    LOG_PATH_COLLECT_TIMEOUT(8, "日志采集任务执行超时 注：该 case 仅出现在时间范围采集场景", LogCollectTaskHealthLevelEnum.RED, false),
    LOG_PATH_COLLECT_DELAYED(9, "日志采集任务延时", LogCollectTaskHealthLevelEnum.RED, false),
    TOPIC_LIMIT_EXISTS(10, "日志采集任务对应下游topic被限流", LogCollectTaskHealthLevelEnum.YELLOW, true),
    HOST_CPU_LIMIT_EXISTS(11, "日志采集任务在对应主机端存在 CPU 阀值限流", LogCollectTaskHealthLevelEnum.YELLOW, true),
    HOST_BYTES_LIMIT_EXISTS(12, "日志采集任务在对应主机端存在出口流量阀值限流", LogCollectTaskHealthLevelEnum.YELLOW, true),
    LOG_SINK_TOPIC_ERROR_FREQUENTLY(13, "日志数据写入下游 topic 失败频繁", LogCollectTaskHealthLevelEnum.YELLOW, true),
    NOT_RELATE_ANY_HOST(14, "日志采集任务未关联主机", LogCollectTaskHealthLevelEnum.YELLOW, true),
    HEALTHY(0, "日志采集任务健康", LogCollectTaskHealthLevelEnum.GREEN, true);

    /**
     * 指标代码
     */
    private Integer code;
    /**
     * 指标描述
     */
    private String description;
    /**
     * 是否为最终诊断结果
     */
    private boolean finalDiagnosis;
    /**
     * 日志采集任务健康等级
     */
    private LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum;

    public Integer getCode() {
        return code;
    }
    public String getDescription() {
        return description;
    }
    public boolean isFinalDiagnosis() {
        return finalDiagnosis;
    }
    public LogCollectTaskHealthLevelEnum getLogCollectTaskHealthLevelEnum() {
        return logCollectTaskHealthLevelEnum;
    }

    LogCollectTaskHealthInspectionResultEnum(Integer code, String description, LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum, boolean finalDiagnosis) {
        this.code = code;
        this.description = description;
        this.logCollectTaskHealthLevelEnum = logCollectTaskHealthLevelEnum;
        this.finalDiagnosis = finalDiagnosis;
    }

    public static LogCollectTaskHealthInspectionResultEnum getByCode(Integer code) {
        for (LogCollectTaskHealthInspectionResultEnum item : values()) {
            if(item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

}
