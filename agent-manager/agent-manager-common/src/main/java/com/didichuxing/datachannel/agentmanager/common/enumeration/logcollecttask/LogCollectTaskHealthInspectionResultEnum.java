package com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务健康度巡检结果枚举
 */
public enum LogCollectTaskHealthInspectionResultEnum {

    LOG_PATH_NOT_EXISTS(2, "日志采集路径%s在主机%s上不存在",  LogCollectTaskHealthLevelEnum.RED, true),
    LOG_PATH_DISORDER(3, "主机%s上日志采集路径%s对应的日志文件中存在日志乱序输出", LogCollectTaskHealthLevelEnum.RED, true),
    LOG_PATH_LOG_SLICE_ERROR_EXISTS(4, "主机%s上日志采集路径%s对应的日志文件中存在日志切片错误", LogCollectTaskHealthLevelEnum.RED, true),
    LOG_PATH_LOG_SIZE_OVERRUN_TRUNCATE_EXISTS(5, "主机%s上日志采集路径%s对应的日志文件中存在日志大小超限截断", LogCollectTaskHealthLevelEnum.RED, true),
    LOG_PATH_CONCURRENT_COLLECT(7, "主机%s上日志采集路径%s对应的日志文件存在被多个agent进程并发采集", LogCollectTaskHealthLevelEnum.RED, true),
    LOG_PATH_COLLECT_DELAYED_CAUSE_BY_DATA_SEND_FAILED(10, "主机%s上日志采集路径%s对应的日志文件存在因下游接收端写入失败导致的采集延时", LogCollectTaskHealthLevelEnum.RED, false),
    LOG_PATH_COLLECT_DELAYED_CAUSE_BY_HOST_CPU_USAGE_LIMIT_EXISTS_CAUSE_BY_AGENT_FULL_GC_OVER_FREQUENCY(11, "主机%s上日志采集路径%s对应的日志文件存在因agent触发cpu阈值限流导致的采集延时，限流原因为：agent full gc 过频", LogCollectTaskHealthLevelEnum.RED, false),
    LOG_PATH_COLLECT_DELAYED_CAUSE_BY_HOST_CPU_USAGE_LIMIT_EXISTS(12, "主机%s上日志采集路径%s对应的日志文件存在因agent触发cpu阈值限流导致的日志采集任务延时", LogCollectTaskHealthLevelEnum.RED, false),
    LOG_PATH_COLLECT_DELAYED_CAUSE_BY_SINK_SLOW(13, "主机%s上日志采集路径%s对应的日志文件存在因agent sink端发送速率瓶颈导致的日志采集任务延时", LogCollectTaskHealthLevelEnum.RED, false),
    LOG_PATH_COLLECT_DELAYED_CAUSE_BY_SOURCE_SLOW(14, "主机%s上日志采集路径%s对应的日志文件存在因agent source端采集速率瓶颈导致的日志采集任务延时", LogCollectTaskHealthLevelEnum.RED, false),
    LOG_PATH_COLLECT_DELAYED_CAUSE_BY_BUSINESS_DATA_WRITE_SLOW(25, "主机%s上日志采集路径%s对应的日志文件存在因业务数据写入慢导致的日志采集任务延时（如：业务数据写入最后时间为16：08：00，当前时间为17：00：00），注意：此时所有业务数据已被全部采集，建议适当调大该日志采集任务对应延迟判断间隔", LogCollectTaskHealthLevelEnum.RED, false),
    TOPIC_LIMIT_EXISTS(15, "日志采集任务%s对应下游topic %s被限流", LogCollectTaskHealthLevelEnum.YELLOW, true),
    HOST_CPU_USAGE_LIMIT_EXISTS(17, "主机%s上日志采集路径%s对应的日志文件采集存在agent cpu阀值限流", LogCollectTaskHealthLevelEnum.YELLOW, true),
    HOST_CPU_USAGE_LIMIT_EXISTS_CAUSE_BY_AGENT_FULL_GC_OVER_FREQUENCY(18, "主机%s上日志采集路径%s对应的日志文件存在因agent full gc过频导致cpu使用率突破限流阈值引发的agent cpu阀值限流", LogCollectTaskHealthLevelEnum.YELLOW, true),
    NOT_RELATE_ANY_HOST(20, "日志采集任务%s未关联主机", LogCollectTaskHealthLevelEnum.YELLOW, true),
    DATA_SEND_FAILED_EXISTS(21, "主机%s上日志采集路径%s对应的日志文件采集存在数据写入下游接收端失败", LogCollectTaskHealthLevelEnum.YELLOW, true),
    HOST_UNABLE_CONNECT(22, "待采集主机%s故障-无法连通", LogCollectTaskHealthLevelEnum.RED, true),
    HOST_NOT_BIND_AGENT(23, "待采集主机%s未绑定任何Agent进行采集", LogCollectTaskHealthLevelEnum.RED, true),
    AGENT_BREAKDOWN(24, "待采集主机%s绑定的Agent[Agent主机名：%s]存在故障", LogCollectTaskHealthLevelEnum.RED, true),
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
