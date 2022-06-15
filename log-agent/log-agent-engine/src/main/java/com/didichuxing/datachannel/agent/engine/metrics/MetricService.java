package com.didichuxing.datachannel.agent.engine.metrics;

import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsSink;
import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsSource;
import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsSystem;
import com.didichuxing.datachannel.agent.engine.metrics.system.lib.DefaultMetricsSystem;
import com.didichuxing.datachannel.agent.common.configs.v2.MetricConfig;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.engine.metrics.sink.AbstractMetricSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricService {

    private static final Logger                LOGGER       = LoggerFactory
                                                                .getLogger(MetricService.class
                                                                    .getName());

    public static final String                 NAME         = "Agent";

    private static final String                AGENT_METRIC = "agent-metrics";

    private volatile static MetricsSystem      metricsSystem;

    private volatile static AbstractMetricSink metricSink;

    private static boolean                     isInit       = false;

    /* 分配metrics cpu */
    public static synchronized MetricsSystem getMetricsSystem() {
        if (null == metricsSystem) {
            LOGGER.info("create metrics cpu");
            metricsSystem = DefaultMetricsSystem.initialize(NAME);
        }
        return metricsSystem;
    }

    public static void init(MetricConfig config, AbstractMetricSink abstractMetricSink)
                                                                                       throws Exception {
        if (config.isValid()) {
            LOGGER.info("begin to init metric reporter");
            /*
             * 设置 metrics 流对应 topic
             */
            config.setTopic(CommonUtils.selectTopic(config.getTopic()));
            /*
             * 设置 metrics 流对应 kafka producer
             */
            metricSink = abstractMetricSink;
            /*
             * 将封装 kafka producer 注册进 metrics system
             */
            registerSink(AGENT_METRIC, null, metricSink);
            LOGGER.info("init metric reporter success");
            isInit = true;
        }
    }

    public static void stop() throws Exception {
        LOGGER.info("stop metric reporter");
        metricSink.stop();
        unRegisterSrc(AGENT_METRIC);
        isInit = false;
        LOGGER.info("stop metric reporter success");
    }

    public static void onChange(MetricConfig config) throws Exception {
        LOGGER.info("begin to change metrics config. config is " + config);
        if (isInit) {
            config.setTopic(CommonUtils.selectTopic(config.getTopic()));
            metricSink.onChange(config);
        } else {
            init(config, metricSink);
        }
        LOGGER.info("success to change metrics config");
    }

    /* 注册source */
    public static <T extends MetricsSource> T registerSrc(String name, String desc, T metricSource) {
        return getMetricsSystem().register(name, desc, metricSource);
    }

    /**
     * 注销
     *
     * @param name
     */
    public static void unRegisterSrc(String name) {
        getMetricsSystem().unRegister(name);
    }

    public static <T extends MetricsSink> T registerSink(String name, String desc, T metricSink) {
        return getMetricsSystem().register(name, desc, metricSink);
    }

}
