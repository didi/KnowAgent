package com.didichuxing.datachannel.swan.agent.engine.metrics.source;

import java.util.Map;

import com.didichuxing.datachannel.metrics.MetricsBuilder;
import com.didichuxing.datachannel.metrics.MetricsRecordBuilder;
import com.didichuxing.datachannel.metrics.lib.MetricMutableGaugeLong;
import com.didichuxing.datachannel.metrics.lib.MetricMutablePeriodGaugeLong;
import com.didichuxing.datachannel.swan.agent.engine.AbstractTask;
import com.didichuxing.datachannel.swan.agent.engine.metrics.metric.SinkMetricsFields;
import com.didichuxing.datachannel.swan.agent.engine.metrics.metric.SourceMetricsFields;
import com.didichuxing.datachannel.swan.agent.engine.metrics.metric.TaskMetricsFields;
import com.didichuxing.datachannel.swan.agent.engine.metrics.stat.MetricMutableTimeStat;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;

// 需要和业务代码强耦合
public class TaskPatternStatistics extends AbstractStatistics {

    private static final ILog                     LOGGER = LogFactory.getLog("perfLogger");

    private AbstractTask                          abstractTask;

    /*
     * sourceCountPerPeriod 每周期读取总的条数 sourceBytePerPeriod 每周期读取总的流量 sourceTime 读取耗时统计
     */
    private volatile MetricMutablePeriodGaugeLong sourceCountPerPeriod;
    private volatile MetricMutablePeriodGaugeLong sourceBytePerPeriod;
    private volatile MetricMutableTimeStat        sourceTime;

    /*
     * filterCountPerPerod 每分钟被过滤的总的条数 filterTotalCount logAgent启动后被过滤的总的条数 filterTooLargeCount 每分钟超过一定大小后被截断的总的日志条数
     * filterTotalTooLargeCount logAgen启动后，超过一定大小后被截断的总的日志条数 filterTimeHistogram 每分钟读取日志的九分位耗时
     */
    private volatile MetricMutablePeriodGaugeLong filterRemainedPerPeriod;
    private volatile MetricMutablePeriodGaugeLong filterOutPerPeriod;
    private volatile MetricMutablePeriodGaugeLong filterTooLargeCountPerPeriod;
    private volatile MetricMutableGaugeLong       filterTotalTooLargeCount;
    private volatile MetricMutableTimeStat        filterTime;

    private volatile MetricMutablePeriodGaugeLong limitCountPerPeriod;

    /*
     * sinkCountPerPeriod 每个周期处理的总数 sinkBytePerPeriod 每个周期处理的总流量 sinkTime 发送耗时统计
     */
    private volatile MetricMutablePeriodGaugeLong sinkCountPerPeriod;
    private volatile MetricMutablePeriodGaugeLong sinkBytePerPeriod;
    private volatile MetricMutableTimeStat        sinkTime;

    /*
     * controlCountPerPeriod 每个周期处理的日志总条数 controlTime处理耗时统计
     */
    private volatile MetricMutableTimeStat        controlTime;

    /**
     * flushTime flush耗时， flushCountPerPeriod 每个周期flush次数 flushFailedCountPerPeriod 每个周期flush失败次数
     */
    private volatile MetricMutableTimeStat        flushTime;
    private volatile MetricMutablePeriodGaugeLong flushCountPerPeriod;
    private volatile MetricMutablePeriodGaugeLong flushFailedCountPerPeriod;

    public TaskPatternStatistics(String name, AbstractTask abstractTask) {
        super(name);

        this.abstractTask = abstractTask;

        // source
        this.sourceCountPerPeriod = metricsRegistry.newPeriodGauge(
            SourceMetricsFields.PREFIX_METRICS_ + "sourceCount", null, 0L);
        this.sourceBytePerPeriod = metricsRegistry.newPeriodGauge(
            SourceMetricsFields.PREFIX_METRICS_ + "sourceByte", null, 0L);
        this.sourceTime = new MetricMutableTimeStat(SourceMetricsFields.PREFIX_METRICS_
                                                    + "sourceTime", null);

        // filter
        filterRemainedPerPeriod = metricsRegistry.newPeriodGauge(SinkMetricsFields.PREFIX_METRICS_
                                                                 + "filterRemained", null, 0L);
        filterOutPerPeriod = metricsRegistry.newPeriodGauge(SinkMetricsFields.PREFIX_METRICS_
                                                            + "filterOut", null, 0L);
        filterTooLargeCountPerPeriod = metricsRegistry.newPeriodGauge(
            SinkMetricsFields.PREFIX_METRICS_ + "filterTooLargeCount", null, 0L);
        filterTotalTooLargeCount = metricsRegistry.newGauge(SinkMetricsFields.PREFIX_METRICS_
                                                            + "filterTotalTooLargeCount", null, 0L);
        filterTime = new MetricMutableTimeStat(SinkMetricsFields.PREFIX_METRICS_ + "filterTime",
            null);

        // limit
        limitCountPerPeriod = metricsRegistry.newPeriodGauge(TaskMetricsFields.PREFIX_METRICS_
                                                             + "limitTime", null, 0L);

        // sink
        sinkCountPerPeriod = metricsRegistry.newPeriodGauge(SinkMetricsFields.PREFIX_METRICS_
                                                            + "sinkCount", null, 0L);
        sinkBytePerPeriod = metricsRegistry.newPeriodGauge(SinkMetricsFields.PREFIX_METRICS_
                                                           + "sinkByte", null, 0L);
        sinkTime = new MetricMutableTimeStat(SinkMetricsFields.PREFIX_METRICS_ + "sinkTime", null);

        // control 整体耗时
        controlTime = new MetricMutableTimeStat(SinkMetricsFields.PREFIX_METRICS_ + "controlTime",
            null);

        flushTime = new MetricMutableTimeStat(SinkMetricsFields.PREFIX_METRICS_ + "flushTime", null);
        flushCountPerPeriod = metricsRegistry.newPeriodGauge(SinkMetricsFields.PREFIX_METRICS_
                                                             + "flushCount", null, 0L);
        flushFailedCountPerPeriod = metricsRegistry.newPeriodGauge(
            SinkMetricsFields.PREFIX_METRICS_ + "flushFailedCount", null, 0L);

    }

    @Override
    public void init() {
        super.init();
    }

    public void sourceOneRecord(long bytes, long cost) {
        sourceCountPerPeriod.incr();
        sourceBytePerPeriod.incr(bytes);
        sourceTime.add(cost);
    }

    public void tooLarge() {
        filterTooLargeCountPerPeriod.incr();
        filterTotalTooLargeCount.incr();
    }

    public void limitOneRecord(long num) {
        limitCountPerPeriod.incr(num);
    }

    public void sinkOneRecord(long bytes, long cost) {
        sinkCountPerPeriod.incr();
        sinkBytePerPeriod.incr(bytes);
        sinkTime.add(cost);
    }

    public void sinkMutilRecord(int num, long bytes, long cost) {
        sinkCountPerPeriod.incr(num);
        sinkBytePerPeriod.incr(bytes);
        sinkTime.add(cost, num);
    }

    public void filterOneRecord(long cost, boolean isFiltered) {
        if (isFiltered) {
            filterRemainedPerPeriod.incr();
        } else {
            filterOutPerPeriod.incr();
        }
        filterTime.add(cost);
    }

    public void controlOneRecord(long cost) {
        controlTime.add(cost);
    }

    public void flushOneRecord(long cost) {
        flushTime.add(cost);
        flushCountPerPeriod.incr();
    }

    public void flushFailedRecord(long cost) {
        flushTime.add(cost);
        flushFailedCountPerPeriod.incr();
    }

    @Override
    public void getMetrics(MetricsBuilder builder, boolean all) {
        Map<String, Object> m = abstractTask.metric();

        for (String key : m.keySet()) {
            if (m.get(key) != null) {
                metricsRegistry.tag(key, null, m.get(key).toString(), true);
            }
        }

        MetricsRecordBuilder metricsRecordBuilder = builder.addRecord(metricsRegistry.name());
        metricsRegistry.snapshot(metricsRecordBuilder, true);

        sourceTime.snapshot(metricsRecordBuilder, all);
        sinkTime.snapshot(metricsRecordBuilder, all);
        controlTime.snapshot(metricsRecordBuilder, all);
        flushTime.snapshot(metricsRecordBuilder, all);
    }
}
