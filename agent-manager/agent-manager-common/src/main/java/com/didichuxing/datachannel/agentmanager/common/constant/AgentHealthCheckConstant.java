package com.didichuxing.datachannel.agentmanager.common.constant;

public class AgentHealthCheckConstant {

    /**
     * 校验 hostName 写入下游 agent metrics topic 端失败频率指标是否存在异常过程中，获取最近 AGENT_METRICS_TOPIC_LIMIT_CHECK_LASTEST_MS_THRESHOLD ms 内指标
     */
    public static final Long AGENT_METRICS_TOPIC_LIMIT_CHECK_LASTEST_MS_THRESHOLD = 10 * 60 * 1000L;
    /**
     * 校验 hostName 写入下游 agent metrics topic 端失败频率指标是否存在异常过程中，写入失败次数阈值
     */
    public static final Integer AGENT_METRICS_TOPIC_LIMIT_CHECK_SEND_FAILED_TIMES_THRESHOLD = 0;
    /**
     * 校验 host 端是否存在采集端 cpu 阈值限流过程中，获取最近 HOST_CPU_LIMIT_CHECK_LASTEST_MS_THRESHOLD ms 内指标
     */
    public static final Long HOST_CPU_LIMIT_CHECK_LASTEST_MS_THRESHOLD = 10 * 60 * 1000L;
    /**
     * 校验 logcollecttask + logpath 在 host 端是否存在采集端 cpu 阈值限流过程中，限流总时长阈值
     */
    public static final Long HOST_CPU_LIMIT_MS_THRESHOLD = 5 * 1000L;
    /**
     * 校验 host 端是否存在采集端流量阈值限流过程中，获取最近 HOST_CPU_LIMIT_CHECK_LASTEST_MS_THRESHOLD ms 内指标
     */
    public static final Long HOST_BYTE_LIMIT_CHECK_LASTEST_MS_THRESHOLD = 10 * 60 * 1000L;
    /**
     * 校验 host 端是否存在采集端流量阈值限流过程中，限流总时长阈值
     */
    public static final Long HOST_BYTE_LIMIT_MS_THRESHOLD = 5 * 1000L;
    /**
     * 校验 logcollecttask + logpath 在 host 端是否存在多 agent 并发采集过程中，获取最近 CONCURRENT_COLLECT_CHECK_LASTEST_MS_THRESHOLD ms 内指标
     */
    public static final Long CONCURRENT_COLLECT_CHECK_LASTEST_MS_THRESHOLD = 10 * 60 * 1000L;
    /**
     * 校验 logcollecttask + logpath 在 host 端是否存在多 agent 并发采集过程中，心跳次数阈值
     */
    public static final Integer CONCURRENT_COLLECT_CHECK_HEARTBEAT_TIMES_THRESHOLD = 15;
    /**
     * 校验 logcollecttask + logpath 在 host 端是否存在心跳过程中，获取最近 ALIVE_CHECK_LASTEST_MS_THRESHOLD ms 内指标
     */
    public static final Long ALIVE_CHECK_LASTEST_MS_THRESHOLD = 5 * 60 * 1000L;
    /**
     * 校验 hostName 写入下游 agent errorlogs topic 端失败频率指标是否存在异常过程中，写入失败次数阈值
     */
    public static final Integer AGENT_ERROR_LOGS_TOPIC_LIMIT_CHECK_SEND_FAILED_TIMES_THRESHOLD = 0;
    /**
     * 校验 hostName 写入下游 agent errorlogs topic 端失败频率指标是否存在异常过程中，获取最近 AGENT_ERROR_LOGS_TOPIC_LIMIT_CHECK_LASTEST_MS_THRESHOLD ms 内指标
     */
    public static final long AGENT_ERROR_LOGS_TOPIC_LIMIT_CHECK_LASTEST_MS_THRESHOLD = 10 * 60 * 1000L;
    /**
     * 校验 agent fd 使用量阈值
     */
    public static final Integer AGENT_FD_USED_THRESHOLD = 2000;
    /**
     * 校验 agent 启动频繁阈值，表示agent两次启动时间间隔
     */
    public static final Long AGENT_STARTUP_FREQUENTLY_THRESHOLD = 30 * 24 * 3600 * 1000L;
    /**
     * 校验agent gc检查时间范围 距当前1h
     */
    public static final Long AGENT_GC_METRIC_CHECK_LASTEST_MS_THRESHOLD = 3600 * 1000L;
    /**
     * 校验agent gc检查次数阈值
     */
    public static final Integer AGENT_GC_TIMES_METRIC_CHECK_THRESHOLD = 2;
    /**
     * 校验 agent 宿主机系统时间阈值
     */
    public static final Long AGENT_SYSTEM_TIME_GAP_CHECK_THRESHOLD = 10 * 1000L;
    /**
     * 校验 agent error logs 发送失败检查时间范围 距当前 10 mins
     */
    public static final Long AGENT_ERROR_LOGS_SEND_FAILED_CHECK_LASTEST_MS_THRESHOLD = 10 * 60 * 1000L;
    /**
     * 校验agent cpu usage 检查时间范围 距当前 10 mins
     */
    public static final Long AGENT_CPU_USAGE_METRIC_CHECK_LASTEST_MS_THRESHOLD = 10 * 60 * 1000L;
    /**
     * 校验agent fd used 检查时间范围 距当前 10 mins
     */
    public static final Long AGENT_FD_USED_METRIC_CHECK_LASTEST_MS_THRESHOLD = 10 * 60 * 1000L;

}
