package com.didichuxing.datachannel.agent.engine.metrics.source;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agent.common.metrics.MetricsBuilder;
import com.didichuxing.datachannel.agent.common.metrics.lib.MetricMutableGaugeLong;
import com.didichuxing.datachannel.agent.engine.AbstractTask;
import com.didichuxing.datachannel.agent.engine.bean.GlobalProperties;
import com.didichuxing.datachannel.agent.engine.metrics.metric.*;
import com.didichuxing.datachannel.agent.engine.metrics.stat.MetricMutablePeriodGaugeLong;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agentmanager.common.metrics.TaskMetrics;
import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
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
    //    private volatile MetricMutableTimeStat        sourceTime;

    /*
     * filterCountPerPerod 每分钟被过滤的总的条数 filterTotalCount logAgent启动后被过滤的总的条数 filterTooLargeCount 每分钟超过一定大小后被截断的总的日志条数
     * filterTotalTooLargeCount logAgen启动后，超过一定大小后被截断的总的日志条数 filterTimeHistogram 每分钟读取日志的九分位耗时
     */
    private volatile MetricMutablePeriodGaugeLong filterRemainedPerPeriod;
    private volatile MetricMutablePeriodGaugeLong filterOutPerPeriod;
    private volatile MetricMutablePeriodGaugeLong filterTooLargeCountPerPeriod;
    private volatile MetricMutableGaugeLong filterTotalTooLargeCount;
    //    private volatile MetricMutableTimeStat        filterTime;

    private volatile MetricMutablePeriodGaugeLong limitCountPerPeriod;

    /*
     * sinkCountPerPeriod 每个周期处理的总数 sinkBytePerPeriod 每个周期处理的总流量 sinkTime 发送耗时统计
     */
    private volatile MetricMutablePeriodGaugeLong sinkCountPerPeriod;
    private volatile MetricMutablePeriodGaugeLong sinkBytePerPeriod;
    //    private volatile MetricMutableTimeStat        sinkTime;

    /*
     * controlCountPerPeriod 每个周期处理的日志总条数 controlTime处理耗时统计
     */
    //    private volatile MetricMutableTimeStat        controlTime;

    /**
     * flushTime flush耗时， flushCountPerPeriod 每个周期flush次数 flushFailedCountPerPeriod 每个周期flush失败次数
     */
    //    private volatile MetricMutableTimeStat        flushTime;
    private volatile MetricMutablePeriodGaugeLong flushCountPerPeriod;
    private volatile MetricMutablePeriodGaugeLong flushFailedCountPerPeriod;

    public TaskPatternStatistics(String name, AbstractTask abstractTask) throws MetricsException {
        super(name);

        this.abstractTask = abstractTask;

        // source
        this.sourceCountPerPeriod = new MetricMutablePeriodGaugeLong();
        this.sourceBytePerPeriod = new MetricMutablePeriodGaugeLong();
        //        this.sourceTime = new MetricMutableTimeStat(SourceMetricsFields.PREFIX_METRICS_ + "sourceTime", null);

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
        //        sinkTime = new MetricMutableTimeStat(SinkMetricsFields.PREFIX_METRICS_ + "sinkTime", null);

        // control 整体耗时
        //        controlTime = new MetricMutableTimeStat(SinkMetricsFields.PREFIX_METRICS_ + "controlTime", null);

        //        flushTime = new MetricMutableTimeStat(SinkMetricsFields.PREFIX_METRICS_ + "flushTime", null);
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
        //        sourceTime.add(cost);
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
        //        sinkTime.add(cost);
    }

    public void sinkMutilRecord(int num, long bytes, long cost) {
        sinkCountPerPeriod.incr(num);
        sinkBytePerPeriod.incr(bytes);
        //        sinkTime.add(cost, num);
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
        //        controlTime.add(cost);
    }

    public void flushOneRecord(long cost) {
        //        flushTime.add(cost);
        flushCountPerPeriod.incr();
    }

    public void flushFailedRecord(long cost) {
        //        flushTime.add(cost);
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
