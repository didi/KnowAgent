package com.didichuxing.datachannel.agent.engine.metrics.sink;

import org.apache.commons.configuration.SubsetConfiguration;

import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsSink;
import com.didichuxing.datachannel.agent.common.configs.v2.MetricConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinbinbin
 * @version $Id: AbstractMetricSink.java, v 0.1 2017年05月16日 20:19 jinbinbin Exp $
 */
public abstract class AbstractMetricSink implements MetricsSink {

    private static final Logger LOGGER = LoggerFactory
                                           .getLogger(AbstractMetricSink.class.getName());
    protected MetricConfig      metricConfig;

    public AbstractMetricSink(MetricConfig metricConfig) {
        this.metricConfig = metricConfig;
    }

    @Override
    public void init(SubsetConfiguration conf) {
    }

    @Override
    public void flush() {
    }

    public abstract void sendMetrics(String content);

    public abstract void stop();

    public abstract void onChange(MetricConfig metricConfig);
}
