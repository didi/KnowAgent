package com.didichuxing.datachannel.agentmanager.common.constant;

public class AgentConstant {

    /**
     * Agent配置版本初始值
     */
    public static final Integer AGENT_CONFIGURATION_VERSION_INIT = 0;

    /**
     * Agent采集完成判断阈值：5 mins
     */
    public static final Integer AGENT_COLLECT_COMPLETE_TIME_THRESHOLD = 5 * 60 * 1000;

    /**
     * Agent指标面板组名 - Agent运行时相关指标
     */
    public static final String AGENT_METRIC_PANEL_GROUP_NAME_RUNTIME = "Agent运行时相关指标";
    /**
     * Agent指标名 - Agent Cpu 使用率/分钟
     */
    public static final String AGENT_METRIC_NAME_CPU_USAGE_PER_MIN = "Agent Cpu 使用率/分钟";
    /**
     * Agent指标面板名 - Agent Cpu 使用率/分钟
     */
    public static final String AGENT_METRIC_PANEL_NAME_CPU_USAGE_PER_MIN = "Agent Cpu 使用率/分钟";
    /**
     * Agent指标面板名 - Agent 内存使用/分钟
     */
    public static final String AGENT_METRIC_PANEL_NAME_MEMORY_USAGE_PER_MIN = "Agent 内存使用量 MB/分钟";
    /**
     * Agent指标名 - Agent fullgc 次数/分钟
     */
    public static final String AGENT_METRIC_NAME_FULL_GC_TIMES_PER_MIN = "Agent fullgc 次数/分钟";
    /**
     * Agent指标面板名 - Agent fullgc 次数/分钟
     */
    public static final String AGENT_METRIC_PANEL_NAME_FULL_GC_TIMES_PER_MIN = "Agent fullgc 次数/分钟";
    /**
     * Agent指标面板名 - Agent入口采集流量bytes/分钟
     */
    public static final String AGENT_METRIC_PANEL_NAME_INPUT_BYTES_PER_MIN = "Agent数据流入口采集流量 MB/分钟";
    /**
     * Agent指标名 - Agent入口采集流量bytes/分钟
     */
    public static final String AGENT_METRIC_NAME_INPUT_BYTES_PER_MIN = "Agent数据流入口采集流量 MB/分钟";
    /**
     * Agent指标名 - Agent出口发送流量bytes/分钟
     */
    public static final String AGENT_METRIC_NAME_OUTPUT_BYTES_PER_MIN = "Agent数据流出口发送流量 MB/分钟";
    /**
     * Agent指标面板名 - Agent出口发送流量bytes/分钟
     */
    public static final String AGENT_METRIC_PANEL_NAME_OUTPUT_BYTES_PER_MIN = "Agent数据流出口发送流量 MB/分钟";
    /**
     * Agent指标面板名 - Agent入口采集条数/分钟
     */
    public static final String AGENT_METRIC_PANEL_NAME_INPUT_LOGS_COUNT_PER_MIN = "Agent数据流入口采集条数/分钟";
    /**
     * Agent指标名 - Agent入口采集条数/分钟
     */
    public static final String AGENT_METRIC_NAME_INPUT_LOGS_COUNT_PER_MIN = "Agent数据流入口采集条数/分钟";
    /**
     * Agent指标面板名 - Agent出口发送条数/分钟
     */
    public static final String AGENT_METRIC_PANEL_NAME_OUTPUT_LOGS_COUNT_PER_MIN = "Agent数据流出口发送条数/分钟";
    /**
     * Agent指标名 - Agent出口发送条数/分钟
     */
    public static final String AGENT_METRIC_NAME_OUTPUT_LOGS_COUNT_PER_MIN = "Agent数据流出口发送条数/分钟";
    /**
     * Agent指标面板名 - Agent fd使用量/分钟
     */
    public static final String AGENT_METRIC_PANEL_NAME_FD_USAGE_PER_MIN = "Agent fd使用量/分钟";
    /**
     * Agent指标名 - Agent fd使用量/分钟
     */
    public static final String AGENT_METRIC_NAME_FD_USAGE_PER_MIN = "Agent fd使用量/分钟";
    /**
     * Agent指标面板名 - Agent errorlogs 发送条数/分钟
     */
    public static final String AGENT_METRIC_PANEL_NAME_ERRORLOGS_SEND_PER_MIN = "Agent errorlogs 流发送条数/分钟";
    /**
     * Agent指标名 - Agent errorlogs 发送条数/分钟
     */
    public static final String AGENT_METRIC_NAME_ERRORLOGS_SEND_PER_MIN = "Agent errorlogs 流发送条数/分钟";
    /**
     * Agent指标名 - Agent metrics 流写入下游topic失败次数/分钟
     */
    public static final String AGENT_METRIC_NAME_METRICS_SEND_FAIL_TIMES_PER_MIN = "Agent metrics 流写入下游topic失败次数/分钟";
    /**
     * Agent指标面板名 - Agent metrics 流写入下游topic失败次数/分钟
     */
    public static final String AGENT_METRIC_PANEL_NAME_METRICS_SEND_FAIL_TIMES_PER_MIN = "Agent metrics 流写入下游topic失败次数/分钟";
    /**
     * Agent指标面板名 - Agent errorlogs 流写入下游topic失败次数/分钟
     */
    public static final String AGENT_METRIC_PANEL_NAME_ERROR_LOGS_SEND_FAIL_TIMES_PER_MIN = "Agent errorlogs 流写入下游topic失败次数/分钟";
    /**
     * Agent指标名 - Agent errorlogs 流写入下游topic失败次数/分钟
     */
    public static final String AGENT_METRIC_NAME_ERROR_LOGS_SEND_FAIL_TIMES_PER_MIN = "Agent errorlogs 流写入下游topic失败次数/分钟";
    /**
     * Agent指标面板名 - Agent是否存在启动/分钟
     */
    public static final String AGENT_METRIC_PANEL_NAME_STARTUP_EXISTS_COUNT_PER_MIN = "Agent是否存在启动/分钟";
    /**
     * Agent指标名 - Agent是否存在启动/分钟
     */
    public static final String AGENT_METRIC_NAME_STARTUP_EXISTS_PER_MIN = "Agent是否存在启动/分钟";
    /**
     * Agent指标面板组名 - Agent数据流相关指标
     */
    public static final String AGENT_METRIC_PANEL_GROUP_NAME_DATA_STREAM = "Agent数据流相关指标";
    /**
     * Agent指标面板组名 - Agent errorlogs 流相关指标
     */
    public static final String AGENT_METRIC_PANEL_GROUP_NAME_ERROR_LOGS_STREAM = "Agent errorlogs 流相关指标";
    /**
     * Agent指标面板组名 - Agent metrics 流相关指标
     */
    public static final String AGENT_METRIC_PANEL_GROUP_NAME_METRICS_STREAM = "Agent metrics 流相关指标";
    /**
     * Agent指标面板组名 - Agent各日志采集任务对应数据流入口采集流量bytes/分钟
     */
    public static final String AGENT_METRIC_PANEL_NAME_INPUT_BYTES_PER_LOG_COLLECT_TASK_PER_MIN = "Agent各日志采集任务对应数据流入口采集流量 MB/分钟";
    /**
     * Agent指标面板组名 - Agent各日志采集任务对应数据流入口采集条数/分钟
     */
    public static final String AGENT_METRIC_PANEL_NAME_INPUT_LOGS_COUNT_PER_LOG_COLLECT_TASK_PER_MIN = "Agent各日志采集任务对应数据流入口采集条数/分钟";
    /**
     * Agent指标面板组名 - Agent各日志采集任务对应数据流出口采集流量bytes/分钟
     */
    public static final String AGENT_METRIC_PANEL_NAME_OUTPUT_BYTES_PER_LOG_COLLECT_TASK_PER_MIN = "Agent各日志采集任务对应数据流出口采集流量 MB/分钟";
    /**
     * Agent指标面板组名 - Agent各日志采集任务对应数据流出口采集条数/分钟
     */
    public static final String AGENT_METRIC_PANEL_NAME_OUTPUT_LOGS_COUNT_PER_LOG_COLLECT_TASK_PER_MIN = "Agent各日志采集任务对应数据流出口采集条数/分钟";
    /**
     * Agent指标面板组名 - Agent source 端性能相关指标
     */
    public static final String AGENT_METRIC_PANEL_GROUP_NAME_SOURCE_PERFORMANCE = "Agent source 端性能相关指标";

    public static final String AGENT_ACTIVE_COLLECTS = "开启状态采集任务数";
    public static final String AGENT_INACTIVE_COLLECTS = "未开启状态采集任务数";
    public static final String AGENT_ACTIVE_PATHS = "开启状态采集路径数";
    public static final String AGENT_INACTIVE_PATHS = "未开启状态采集路径数";

}
