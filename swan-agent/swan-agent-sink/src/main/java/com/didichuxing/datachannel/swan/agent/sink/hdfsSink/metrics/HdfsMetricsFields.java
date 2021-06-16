package com.didichuxing.datachannel.swan.agent.sink.hdfsSink.metrics;

import com.didichuxing.datachannel.swan.agent.engine.metrics.metric.SinkMetricsFields;

/**
 * @description: hdfs配置
 * @author: huangjw
 * @Date: 2019-07-18 17:29
 */
public interface HdfsMetricsFields extends SinkMetricsFields {

    String PREFIX_COUNT                 = PREFIX_ + "count";
    String PREFIX_SINK_NUM              = PREFIX_ + "sinkNum";
    String PREFIX_HDFS_PATH             = PREFIX_ + "rootPath";

    String FILTER_REMAINED              = PREFIX_METRICS_ + "filterRemained";
    String LOSS_COUNT                   = PREFIX_METRICS_ + "filterOut";
    String FILTER_TOO_LARGE_COUNT       = PREFIX_METRICS_ + "filterTooLargeCount";
    String FILTER_TOTAL_TOO_LARGE_COUNT = PREFIX_METRICS_ + "filterTotalTooLargeCount";

    String SINK_STAT_TIME               = PREFIX_METRICS_ + "sinkTime";
    String CONTROL_STAT_TIME            = PREFIX_METRICS_ + "controlTime";
}
