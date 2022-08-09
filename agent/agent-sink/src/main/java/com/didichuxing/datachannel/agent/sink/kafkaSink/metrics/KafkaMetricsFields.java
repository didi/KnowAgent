package com.didichuxing.datachannel.agent.sink.kafkaSink.metrics;

import com.didichuxing.datachannel.agent.engine.metrics.metric.SinkMetricsFields;

/**
 * @description: kafka metrics
 * @author: huangjw
 * @Date: 2019-07-18 16:04
 */
public interface KafkaMetricsFields extends SinkMetricsFields {

    String PREFIX_COUNT                 = PREFIX_ + "count";
    String PREFIX_SINK_NUM              = PREFIX_ + "sinkNum";
    String PREFIX_TARGET_ID             = PREFIX_ + "clusterId";
    String PREFIX_TARGET_TOPIC          = PREFIX_ + "topic";

    String FILTER_REMAINED              = PREFIX_METRICS_ + "filterRemained";
    String LOSS_COUNT                   = PREFIX_METRICS_ + "filterOut";
    String FILTER_TOO_LARGE_COUNT       = PREFIX_METRICS_ + "filterTooLargeCount";
    String FILTER_TOTAL_TOO_LARGE_COUNT = PREFIX_METRICS_ + "filterTotalTooLargeCount";

    String SINK_STAT_TIME               = PREFIX_METRICS_ + "sinkTime";
    String CONTROL_STAT_TIME            = PREFIX_METRICS_ + "controlTime";
}
