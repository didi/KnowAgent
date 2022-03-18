package com.didichuxing.datachannel.agent.node.service.metrics.sink;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agent.engine.metrics.system.Metric;
import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsRecord;
import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsSink;
import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsTag;
import com.didichuxing.datachannel.agent.common.configs.v2.MetricConfig;
import com.didichuxing.datachannel.agent.engine.metrics.sink.AbstractMetricSink;

import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import org.apache.commons.configuration.SubsetConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaMetricSink extends AbstractMetricSink implements MetricsSink {

    private static final Logger LOGGER         = LoggerFactory.getLogger("perfLogger");
    private KafkaTopicSink      sink;

    private static final String LIMIT_TIME_TAG = "limitTime";

    @Override
    public void init(SubsetConfiguration conf) {
    }

    public KafkaMetricSink(MetricConfig metricConfig) {
        super(metricConfig);
        try {
            sink = new KafkaTopicSink(metricConfig);
        } catch (Exception e) {
            LogGather.recordErrorLog("KafkaMetricSink error", "KafkaTopicSink init error!", e);
        }
    }

    @Override
    public void stop() {
        sink.stop();
    }

    @Override
    public void flush() {
        // sink.flush();
    }

    @Override
    public void sendMetrics(String content) {
        sink.send(content);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(content);
        }
    }

    @Override
    public void onChange(MetricConfig newConfig) {
        try {
            if (!sink.onChange(newConfig)) {
                return;
            }
            KafkaTopicSink s = new KafkaTopicSink(newConfig);
            KafkaTopicSink oldSink = sink;

            this.sink = s;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LogGather.recordErrorLog("KafkaMetricSink error", "on change sleep error", e);
            }

            oldSink.stop();
        } catch (Exception e) {
            LogGather.recordErrorLog("KafkaMetricSink error", "on change error", e);
        }
    }

    @Override
    public void putMetrics(MetricsRecord record) {
        Map<String, Object> result = new HashMap<>();
        for (MetricsTag metricsTag : record.tags()) {
            result.put(metricsTag.getName(), metricsTag.getValue());
        }
        for (Metric metric : record.metrics()) {
            result.put(metric.name(), metric.value());
        }
        sendMetrics(JSON.toJSONString(result));
    }
}
