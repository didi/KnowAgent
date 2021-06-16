package com.didichuxing.datachannel.agentmanager.common.constant;

public class LogCollectTaskConstant {

    /**
     * LogCollectTask配置版本初始值
     */
    public static final Integer LOG_COLLECT_TASK_CONFIGURATION_VERSION_INIT = 0;

    public static final String LOG_COLLECT_TASK_METRIC_NAME_LOGS_BYTES_PER_MIN = "日志采集任务对应日志采集流量 bytes/分钟";
    public static final String LOG_COLLECT_TASK_METRIC_PANEL_NAME_LOGS_BYTES_PER_MIN = "日志采集任务对应日志采集流量 bytes/分钟";
    public static final String LOG_COLLECT_TASK_METRIC_PANEL_NAME_FILELOGPATH_EXISTS_PER_MIN = "日志采集路径在各主机上是否存在";
    public static final String LOG_COLLECT_TASK_METRIC_PANEL_NAME_FILELOGPATH_DISORDER_PER_MIN = "日志采集路径在各主机上是否存在乱序";
    public static final String LOG_COLLECT_TASK_METRIC_PANEL_NAME_FILELOGPATH_LOG_SLICE_ERROR_PER_MIN = "日志采集路径在各主机上是否存在日志切片错误";
    public static final String LOG_COLLECT_TASK_METRIC_PANEL_GROUP_NAME_LOGCOLLECTTASK_LEVEL = "LogCollectTask级相关指标";

    public static final String LOG_COLLECT_TASK_METRIC_PANEL_GROUP_NAME_FILELOGPATH_LEVEL = "FileLogPath级相关指标";

    public static final String LOG_COLLECT_TASK_METRIC_PANEL_NAME_LOGS_COUNT_PER_MIN = "日志采集任务对应日志采集条数/分钟";
    public static final String LOG_COLLECT_TASK_METRIC_NAME_LOGS_COUNT_PER_MIN = "日志采集任务对应日志采集条数/分钟";

    public static final String LOG_PATH_METRIC_PANEL_GROUP_NAME_RUNTIME = "LogCollectTask 各 logpath 运行时相关指标";

    public static final String LOG_COLLECT_TASK_METRIC_PANEL_NAME_CURRENT_COLLECT_TIME_PER_LOG_PATH_PER_MIN = "各logPath对应日志采集时间/分钟";

    public static final String LOG_COLLECT_TASK_METRIC_PANEL_NAME_CURRENT_COLLECT_TIME_PER_LOG_PATH_PER_HOST_PER_MIN = "各logPath对应在各host上对应当前日志采集时间/分钟";

    public static final String LOG_COLLECT_TASK_METRIC_PANEL_NAME_FILELOGPATH_ABNORMAL_TRUNCATION_PER_MIN = "日志采集路径在各主机上是否存在异常截断";

    public static final String LOG_COLLECT_TASK_METRIC_PANEL_NAME_FILELOGPATH_CONCURRENT_COLLECT_PER_MIN = "日志采集路径在各主机上是否存在并发采集";

    public static final String LOG_COLLECT_TASK_METRIC_PANEL_NAME_FILELOGPATH_FILTER_OUT_PER_MIN = "日志采集路径过滤条数";

    public static final String LOG_COLLECT_TASK_METRIC_PANEL_NAME_FILELOGPATH_MIN_COLLECT_BUSINESS_TIME_PER_MIN = "日志采集路径采集最小时间";

}
