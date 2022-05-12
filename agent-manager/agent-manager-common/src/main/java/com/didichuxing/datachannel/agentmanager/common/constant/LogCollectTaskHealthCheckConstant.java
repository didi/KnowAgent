package com.didichuxing.datachannel.agentmanager.common.constant;

public class LogCollectTaskHealthCheckConstant {

    /**
     * 校验 logcollecttask + logpath + hostName 写入下游 topic 端失败频率指标是否存在异常过程中，获取最近 AGENT_METRICS_TOPIC_LIMIT_CHECK_LASTEST_MS_THRESHOLD ms 内指标
     */
    public static final Long TOPIC_LIMIT_CHECK_LASTEST_MS_THRESHOLD = 10 * 60 * 1000L;

    /**
     * 校验 hostName 写入下游 topic 端失败频率指标是否存在异常过程中，写入失败次数阈值
     */
    public static final Integer TOPIC_LIMIT_CHECK_SEND_FAILED_TIMES_THRESHOLD = 0;

    /**
     * 校验 logcollecttask + logpath 在 host 端是否存在采集端 cpu 阈值限流过程中，获取最近 HOST_CPU_LIMIT_CHECK_LASTEST_MS_THRESHOLD ms 内指标
     */
    public static final Long HOST_CPU_LIMIT_CHECK_LASTEST_MS_THRESHOLD = 10 * 60 * 1000L;

    /**
     * 校验 logcollecttask + logpath 在 host 端是否存在采集端 cpu 阈值限流过程中，限流总时长阈值
     */
    public static final Long HOST_CPU_LIMIT_MS_THRESHOLD = 5 * 1000L;

    /**
     * 校验 logcollecttask + logpath 在 host 端是否存在采集端流量阈值限流过程中，获取最近 HOST_CPU_LIMIT_CHECK_LASTEST_MS_THRESHOLD ms 内指标
     */
    public static final Long HOST_BYTE_LIMIT_CHECK_LASTEST_MS_THRESHOLD = 10 * 60 * 1000L;

    /**
     * 校验 logcollecttask + logpath 在 host 端是否存在下游接收端写入失败，获取最近 DATA_SEND_FAILED_EXISTS_CHECK_LASTEST_MS_THRESHOLD ms 内指标
     */
    public static final Long DATA_SEND_FAILED_EXISTS_CHECK_LASTEST_MS_THRESHOLD = 10 * 60 * 1000L;

    /**
     * 校验 logcollecttask + logpath 在 host 端是否存在采集端流量阈值限流过程中，限流总时长阈值
     */
    public static final Long HOST_BYTE_LIMIT_MS_THRESHOLD = 5 * 1000L;
    /**
     * 校验 logcollecttask + logpath 在 host 端是否存在多 agent 并发采集过程中，获取最近 CONCURRENT_COLLECT_CHECK_LASTEST_MS_THRESHOLD ms 内指标
     */
    public static final Long CONCURRENT_COLLECT_CHECK_LASTEST_MS_THRESHOLD = 10 * 60 * 1000L;
    /**
     * 校验 logcollecttask + logpath 在 host 端是否存在多 agent 并发采集过程中，心跳次数阈值
     * agent通常为60s一次
     */
    public static final Integer CONCURRENT_COLLECT_CHECK_HEARTBEAT_TIMES_THRESHOLD = 15;
    /**
     * 校验 logcollecttask + logpath 在 host 端是否存在心跳过程中，获取最近 ALIVE_CHECK_LASTEST_MS_THRESHOLD ms 内指标
     */
    public static final Long ALIVE_CHECK_LASTEST_MS_THRESHOLD = 5 * 60 * 1000L;

    /**
     * 校验 logcollecttask + logpath 在 host 端是否存在采集端流量阈值限流过程中，获取最近 CHANNEL_USAGE_CHECK_LASTEST_MS_THRESHOLD ms 内指标
     */
    public static final Long CHANNEL_USAGE_CHECK_LASTEST_MS_THRESHOLD = 10 * 60 * 1000L;

}
