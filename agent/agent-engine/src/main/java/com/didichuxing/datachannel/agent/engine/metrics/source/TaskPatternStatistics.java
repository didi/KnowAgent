package com.didichuxing.datachannel.agent.engine.metrics.source;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsBuilder;
import com.didichuxing.datachannel.agent.engine.metrics.system.lib.MetricMutableGaugeLong;
import com.didichuxing.datachannel.agent.engine.AbstractTask;
import com.didichuxing.datachannel.agent.engine.metrics.metric.*;
import com.didichuxing.datachannel.agent.engine.metrics.stat.MetricMutablePeriodGaugeLong;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agentmanager.common.metrics.TaskMetrics;
import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;
import com.didichuxing.datachannel.system.metrcis.exception.MetricsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

// 需要和业务代码强耦合
public class TaskPatternStatistics extends AbstractStatistics {

    private static final Logger                   LOGGER = LoggerFactory.getLogger("perfLogger");

    /**
     * 任务统计指标关联日志采集任务对象
     */
    private AbstractTask                          abstractTask;

    /*
     * sourceCountPerPeriod 每周期读取总的条数 sourceBytePerPeriod 每周期读取总的流量 sourceTime 读取耗时统计
     */
    private volatile MetricMutablePeriodGaugeLong sourceCountPerPeriod;
    private volatile MetricMutablePeriodGaugeLong sourceBytePerPeriod;
    private volatile PeriodStatistics             sourceTime;

    /*
     * filterCountPerPerod 每分钟被过滤的总的条数 filterTotalCount logAgent启动后被过滤的总的条数 filterTooLargeCount 每分钟超过一定大小后被截断的总的日志条数
     * filterTotalTooLargeCount logAgen启动后，超过一定大小后被截断的总的日志条数 filterTimeHistogram 每分钟读取日志的九分位耗时
     */
    private volatile MetricMutablePeriodGaugeLong filterRemainedPerPeriod;
    private volatile MetricMutablePeriodGaugeLong filterOutPerPeriod;
    private volatile MetricMutablePeriodGaugeLong filterTooLargeCountPerPeriod;
    private volatile MetricMutableGaugeLong       filterTotalTooLargeCount;
    //    private volatile MetricMutableTimeStat        filterTime;

    private volatile MetricMutablePeriodGaugeLong limitCountPerPeriod;

    /*
     * sinkCountPerPeriod 每个周期处理的总数 sinkBytePerPeriod 每个周期处理的总流量 sinkTime 发送耗时统计
     */
    private volatile MetricMutablePeriodGaugeLong sinkCountPerPeriod;
    private volatile MetricMutablePeriodGaugeLong sinkBytePerPeriod;
    private volatile PeriodStatistics             sinkTime;

    /*
     * controlCountPerPeriod 每个周期处理的日志总条数 controlTime处理耗时统计
     */
    private volatile PeriodStatistics             controlTime;

    /**
     * flushTime flush耗时， flushCountPerPeriod 每个周期flush次数 flushFailedCountPerPeriod 每个周期flush失败次数
     */
    private volatile PeriodStatistics             flushTime;
    private volatile MetricMutablePeriodGaugeLong flushCountPerPeriod;
    private volatile MetricMutablePeriodGaugeLong flushFailedCountPerPeriod;

    public TaskPatternStatistics(String name, AbstractTask abstractTask) throws MetricsException {
        super(name);

        this.abstractTask = abstractTask;

        // source
        this.sourceCountPerPeriod = new MetricMutablePeriodGaugeLong();
        this.sourceBytePerPeriod = new MetricMutablePeriodGaugeLong();
        this.sourceTime = new PeriodStatistics();

        // filter
        filterRemainedPerPeriod = new MetricMutablePeriodGaugeLong();
        filterOutPerPeriod = new MetricMutablePeriodGaugeLong();
        filterTooLargeCountPerPeriod = new MetricMutablePeriodGaugeLong();
        filterTotalTooLargeCount = new MetricMutableGaugeLong();
        //        filterTime = new MetricMutableTimeStat(SinkMetricsFields.PREFIX_METRICS_ + "filterTime", null);

        // limit
        limitCountPerPeriod = new MetricMutablePeriodGaugeLong();

        // sink
        sinkCountPerPeriod = new MetricMutablePeriodGaugeLong();
        sinkBytePerPeriod = new MetricMutablePeriodGaugeLong();
        sinkTime = new PeriodStatistics();

        // control 整体耗时
        controlTime = new PeriodStatistics();

        flushTime = new PeriodStatistics();
        flushCountPerPeriod = new MetricMutablePeriodGaugeLong();
        flushFailedCountPerPeriod = new MetricMutablePeriodGaugeLong();

    }

    @Override
    public void init() {
        super.init();
    }

    public void sourceOneRecord(long bytes, long cost) {
        sourceCountPerPeriod.incr();
        sourceBytePerPeriod.incr(bytes);
        sourceTime.add(Double.valueOf(cost));
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
        sinkTime.add(Double.valueOf(cost));
    }

    public void sinkMutilRecord(int num, long bytes, long cost) {
        sinkCountPerPeriod.incr(num);
        sinkBytePerPeriod.incr(bytes);
        sinkTime.add(Double.valueOf(cost));
    }

    public void filterOneRecord(long cost, boolean isFiltered) {
        if (isFiltered) {
            filterRemainedPerPeriod.incr();
        } else {
            filterOutPerPeriod.incr();
        }
        //        filterTime.add(cost);
    }

    public void controlOneRecord(long cost) {
        controlTime.add(Double.valueOf(cost));
    }

    public void flushOneRecord(long cost) {
        flushTime.add(Double.valueOf(cost));
        flushCountPerPeriod.incr();
    }

    public void flushFailedRecord(long cost) {
        flushTime.add(Double.valueOf(cost));
        flushFailedCountPerPeriod.incr();
    }

    @Override
    public void getMetrics(MetricsBuilder builder, boolean all) {
        /*
         * 构建 log collect task 相关指标
         */
        TaskMetrics taskMetrics = buildAgentMetrics();
        /*
         * 填充初始化时缓存的指标
         */
        setInitMetrics(taskMetrics);

        /*
         * 将 log collect task 相关指标存入待发送缓存队列
         */
        metricsRegistry.tag(MetricsFieldConstant.TASK_METRICS, null,
            JSON.toJSONString(taskMetrics), true);
        super.getMetrics(builder, all);
    }

    private void setInitMetrics(TaskMetrics taskMetrics) {

    }

    private TaskMetrics buildAgentMetrics() {

        TaskMetrics taskMetrics = new TaskMetrics();

        abstractTask.setMetrics(taskMetrics);

        taskMetrics.setReadbytes(sourceBytePerPeriod.snapshot());
        taskMetrics.setReadcount(sourceCountPerPeriod.snapshot());
        taskMetrics.setFiltereventsnum(filterOutPerPeriod.snapshot());
        taskMetrics.setToolargetruncatenum(filterTooLargeCountPerPeriod.snapshot());
        taskMetrics.setToolargetruncatenumtotal(filterTotalTooLargeCount.snapshot());
        taskMetrics.setLimittime(limitCountPerPeriod.snapshot());
        taskMetrics.setSendbytes(sinkBytePerPeriod.snapshot());
        taskMetrics.setSendcount(sinkCountPerPeriod.snapshot());
        taskMetrics.setFlushtimes(flushCountPerPeriod.snapshot());
        taskMetrics.setFlushfailedtimes(flushFailedCountPerPeriod.snapshot());

        PeriodStatistics sourceTimePeriodStatistics = sourceTime.snapshot();
        taskMetrics.setReadtimeperevent(sourceTimePeriodStatistics.getLast());
        taskMetrics.setReadtimepereventmin(sourceTimePeriodStatistics.getMin());
        taskMetrics.setReadtimepereventmax(sourceTimePeriodStatistics.getMax());
        taskMetrics.setReadtimepereventmean(sourceTimePeriodStatistics.getAvg());
        taskMetrics.setReadtimepereventstd(sourceTimePeriodStatistics.getStdDev());
        taskMetrics.setReadtimeperevent55quantile(sourceTimePeriodStatistics.getQuantile55());
        taskMetrics.setReadtimeperevent75quantile(sourceTimePeriodStatistics.getQuantile75());
        taskMetrics.setReadtimeperevent95quantile(sourceTimePeriodStatistics.getQuantile95());
        taskMetrics.setReadtimeperevent99quantile(sourceTimePeriodStatistics.getQuantile99());

        PeriodStatistics sinkTimePeriodStatistics = sinkTime.snapshot();
        taskMetrics.setSendtime(sinkTimePeriodStatistics.getLast());
        taskMetrics.setSendtimemin(sinkTimePeriodStatistics.getMin());
        taskMetrics.setSendtimemax(sinkTimePeriodStatistics.getMax());
        taskMetrics.setSendtimemean(sinkTimePeriodStatistics.getAvg());
        taskMetrics.setSendtimestd(sinkTimePeriodStatistics.getStdDev());
        taskMetrics.setSendtime55quantile(sinkTimePeriodStatistics.getQuantile55());
        taskMetrics.setSendtime75quantile(sinkTimePeriodStatistics.getQuantile75());
        taskMetrics.setSendtime95quantile(sinkTimePeriodStatistics.getQuantile95());
        taskMetrics.setSendtime99quantile(sinkTimePeriodStatistics.getQuantile99());

        PeriodStatistics flushTimePeriodStatistics = flushTime.snapshot();
        taskMetrics.setFlushtime(flushTimePeriodStatistics.getLast());
        taskMetrics.setFlushtimemin(flushTimePeriodStatistics.getMin());
        taskMetrics.setFlushtimemax(flushTimePeriodStatistics.getMax());
        taskMetrics.setFlushtimemean(flushTimePeriodStatistics.getAvg());
        taskMetrics.setFlushtimestd(flushTimePeriodStatistics.getStdDev());
        taskMetrics.setFlushtime55quantile(flushTimePeriodStatistics.getQuantile55());
        taskMetrics.setFlushtime75quantile(flushTimePeriodStatistics.getQuantile75());
        taskMetrics.setFlushtime95quantile(flushTimePeriodStatistics.getQuantile95());
        taskMetrics.setFlushtime99quantile(flushTimePeriodStatistics.getQuantile99());

        PeriodStatistics controlTimePeriodStatistics = controlTime.snapshot();
        taskMetrics.setProcesstimeperevent(controlTimePeriodStatistics.getLast());
        taskMetrics.setProcesstimepereventmin(controlTimePeriodStatistics.getMin());
        taskMetrics.setProcesstimepereventmax(controlTimePeriodStatistics.getMax());
        taskMetrics.setProcesstimepereventmean(controlTimePeriodStatistics.getAvg());
        taskMetrics.setProcesstimepereventstd(controlTimePeriodStatistics.getStdDev());
        taskMetrics.setProcesstimeperevent55quantile(controlTimePeriodStatistics.getQuantile55());
        taskMetrics.setProcesstimeperevent75quantile(controlTimePeriodStatistics.getQuantile75());
        taskMetrics.setProcesstimeperevent95quantile(controlTimePeriodStatistics.getQuantile95());
        taskMetrics.setProcesstimeperevent99quantile(controlTimePeriodStatistics.getQuantile99());

        //TODO：

        Date current = new Date();
        Long heartbeatTime = current.getTime();
        Long heartbeatTimeMinute = DateUtils.getMinuteUnitTimeStamp(current);
        Long heartbeatTimeHour = DateUtils.getHourUnitTimeStamp(current);
        Long heartbeatTimeDay = DateUtils.getDayUnitTimeStamp(current);

        taskMetrics.setAgenthostname(CommonUtils.getHOSTNAME());//(GlobalProperties.getSystemMetricsService().getHostName());
        taskMetrics.setAgenthostip(CommonUtils.getHOSTIP());
        taskMetrics.setHeartbeattime(heartbeatTime);
        taskMetrics.setHeartbeattimeminute(heartbeatTimeMinute);
        taskMetrics.setHeartbeattimehour(heartbeatTimeHour);
        taskMetrics.setHeartbeatTimeDay(heartbeatTimeDay);

        return taskMetrics;
    }

}
