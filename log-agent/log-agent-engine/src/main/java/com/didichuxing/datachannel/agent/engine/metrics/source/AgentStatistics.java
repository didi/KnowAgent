package com.didichuxing.datachannel.agent.engine.metrics.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import com.alibaba.fastjson.JSON;

import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsBuilder;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.bean.GlobalProperties;
import com.didichuxing.datachannel.agent.engine.limit.LimitService;
import com.didichuxing.datachannel.agent.engine.metrics.metric.*;
import com.didichuxing.datachannel.agent.engine.metrics.stat.MetricMutablePeriodGaugeLong;
import com.didichuxing.datachannel.agent.engine.service.TaskRunningPool;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;

import com.didichuxing.datachannel.agentmanager.common.metrics.*;
import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.system.metrcis.bean.DiskIOInfo;
import com.didichuxing.datachannel.system.metrcis.bean.DiskInfo;
import com.didichuxing.datachannel.system.metrcis.bean.NetCardInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentStatistics extends AbstractStatistics {

    private static final Logger                   LOGGER = LoggerFactory
                                                             .getLogger(AgentStatistics.class);

    /**
     * agent 限流服务
     */
    private LimitService                          limiter;

    /**
     * agent 启动时间
     */
    private Long                                  startTime;

    /**
     * 初始化字段 系统启动时间
     */
    private Long                                  systemStartupTime;
    /**
     * 初始化字段 系统 cpu 核数
     */
    private Integer                               cpuCores;
    /**
     * 初始化字段 进程启动时间
     */
    private Long                                  processStartupTime;
    /**
     * 初始化字段 进程 id
     */
    private Long                                  processPid;
    /**
     * 初始化字段 agent 版本号
     */
    private String                                agentVersion;
    /**
     * 初始化字段 运行状态日志采集任务数
     */
    private volatile Integer                      runningCollectTaskNum;
    /**
     * 初始化字段 运行状态日志采集路径数
     */
    private volatile Integer                      runningCollectPathNum;

    /**
     * 初始化字段 日志采集任务数
     */
    private volatile Integer                      collectTaskNum;

    /**
     * 初始化字段 日志采集路径数
     */
    private volatile Integer                      collectPathNum;

    /**
     * agent 两次指标数据发送周期内发送日志条数
     */
    private volatile MetricMutablePeriodGaugeLong agentSendCountPerPeriod;

    /**
     * agent 两次指标数据发送周期内发送日志量
     */
    private volatile MetricMutablePeriodGaugeLong agentSendBytePerPeriod;

    /**
     * agent 两次指标数据发送周期内采集日志条数
     */
    private volatile MetricMutablePeriodGaugeLong agentReadCountPerPeriod;

    /**
     * agent 两次指标数据发送周期内采集日志量
     */
    private volatile MetricMutablePeriodGaugeLong agentReadBytePerPeriod;

    /**
     * agent 两次指标数据发送周期内错误日志发送量
     */
    private volatile MetricMutablePeriodGaugeLong errorLogsCountPerPeriod;

    /**
     * agent 两次指标数据发送周期内错误日志发送失败量
     */
    private volatile MetricMutablePeriodGaugeLong errorLogsSendFailedCount;

    public AgentStatistics(String name, LimitService limiter, Long startTime,
                           Integer runningCollectTaskNum, Integer runningCollectPathNum,
                           Integer collectTaskNum, Integer collectPathNum) {
        super(name);
        this.limiter = limiter;
        this.startTime = startTime;
        this.agentSendCountPerPeriod = new MetricMutablePeriodGaugeLong();
        this.agentSendBytePerPeriod = new MetricMutablePeriodGaugeLong();
        this.runningCollectTaskNum = runningCollectTaskNum;
        this.runningCollectPathNum = runningCollectPathNum;
        this.agentReadBytePerPeriod = new MetricMutablePeriodGaugeLong();
        this.agentReadCountPerPeriod = new MetricMutablePeriodGaugeLong();
        this.errorLogsCountPerPeriod = new MetricMutablePeriodGaugeLong();
        this.errorLogsSendFailedCount = new MetricMutablePeriodGaugeLong();
        this.collectTaskNum = collectTaskNum;
        this.collectPathNum = collectPathNum;
    }

    @Override
    public void init() {
        /*
         * 初始化启动后值不变指标集
         */
        this.systemStartupTime = GlobalProperties.getSystemMetricsService().getSystemStartupTime();
        this.cpuCores = GlobalProperties.getSystemMetricsService().getSystemCpuCores();
        this.processStartupTime = GlobalProperties.getProcessMetricsService()
            .getProcessStartupTime();
        this.processPid = GlobalProperties.getProcessMetricsService().getProcessPid();
        this.agentVersion = getAgentVersion();
        super.init();
    }

    @Override
    public void getMetrics(MetricsBuilder builder, boolean all) {
        /*
         * 构建agent相关指标
         */
        AgentMetrics agentMetrics = buildAgentMetrics();
        /*
         * 填充初始化时缓存的指标
         */
        setInitMetrics(agentMetrics);
        /*
         * 将agent相关指标存入待发送缓存队列
         */
        metricsRegistry.tag(MetricsFieldConstant.AGENT_METRICS, null,
            JSON.toJSONString(agentMetrics), true);
        super.getMetrics(builder, all);
    }

    /**
     * @param hostName 主机名
     * @param heartbeatTime 当前时间戳（精度：毫秒）
     * @param heartbeatTimeMinute 当前时间戳（精度：分钟）
     * @param heartbeatTimeHour 当前时间戳（精度：小时）
     * @param heartbeatTimeDay 当前时间戳（精度：日）
     * @return 返回构建完成的AgentBusinessMetrics对象
     */
    private AgentBusinessMetrics buildAgentBusinessMetrics(String hostName, Long heartbeatTime,
                                                           Long heartbeatTimeMinute,
                                                           Long heartbeatTimeHour,
                                                           Long heartbeatTimeDay) {
        AgentBusinessMetrics agentBusinessMetrics = new AgentBusinessMetrics();
        agentBusinessMetrics.setHostname(hostName);
        agentBusinessMetrics.setWritecount(agentSendCountPerPeriod.snapshot());
        agentBusinessMetrics.setWritebytes(agentSendBytePerPeriod.snapshot());
        agentBusinessMetrics.setRunningcollecttasknum(runningCollectTaskNum);
        agentBusinessMetrics.setRunningcollectpathnum(runningCollectPathNum);
        agentBusinessMetrics.setReadbytes(agentReadBytePerPeriod.snapshot());
        agentBusinessMetrics.setReadcount(agentReadCountPerPeriod.snapshot());
        agentBusinessMetrics.setHeartbeattime(heartbeatTime);
        agentBusinessMetrics.setHeartbeattimeminute(heartbeatTimeMinute);
        agentBusinessMetrics.setHeartbeattimehour(heartbeatTimeHour);
        agentBusinessMetrics.setHeartbeatTimeDay(heartbeatTimeDay);
        agentBusinessMetrics.setErrorlogscount(errorLogsCountPerPeriod.snapshot());
        agentBusinessMetrics.setErrorlogssendfailedcount(errorLogsSendFailedCount.snapshot());
        agentBusinessMetrics.setNormalcollectthreadnummax(getNormalCollectThreadNumMax());
        agentBusinessMetrics.setNormalcollectthreadnumsize(getNormalCollectThreadNumSize());
        agentBusinessMetrics.setNormalcollectthreadqueuemax(getNormalCollectThreadQueueMax());
        agentBusinessMetrics.setNormalcollectthreadqueuesize(getNormalCollectThreadQueueSize());
        agentBusinessMetrics.setTemporarycollectthreadnummax(getTemporaryCollectThreadNumMax());
        agentBusinessMetrics.setTemporarycollectthreadnumsize(getTemporaryCollectThreadNumSize());
        agentBusinessMetrics.setTemporarycollectthreadqueuemax(getTemporaryCollectThreadQueueMax());
        agentBusinessMetrics
            .setTemporarycollectthreadqueuesize(getTemporaryCollectThreadQueueSize());
        agentBusinessMetrics.setCollecttasknum(collectTaskNum);
        agentBusinessMetrics.setPausecollecttasknum(getPauseCollectTaskNum());
        agentBusinessMetrics.setCollectpathnum(collectPathNum);
        agentBusinessMetrics.setPausecollectpathnum(getPauseCollectPathNum());

        return agentBusinessMetrics;
    }

    private Integer getPauseCollectPathNum() {
        return this.collectPathNum - this.runningCollectPathNum;
    }

    private Integer getPauseCollectTaskNum() {
        return this.collectTaskNum - this.runningCollectTaskNum;
    }

    private Integer getTemporaryCollectThreadQueueSize() {
        ExecutorService executorService = TaskRunningPool.getTempExecutorService();
        return ((ThreadPoolExecutor) executorService).getQueue().size();
    }

    private Integer getTemporaryCollectThreadQueueMax() {
        ExecutorService executorService = TaskRunningPool.getTempExecutorService();
        BlockingQueue<Runnable> queue = ((ThreadPoolExecutor) executorService).getQueue();
        return queue.size() + queue.remainingCapacity();
    }

    private Integer getTemporaryCollectThreadNumSize() {
        ExecutorService executorService = TaskRunningPool.getTempExecutorService();
        return ((ThreadPoolExecutor) executorService).getActiveCount();
    }

    private Integer getTemporaryCollectThreadNumMax() {
        ExecutorService executorService = TaskRunningPool.getTempExecutorService();
        return ((ThreadPoolExecutor) executorService).getMaximumPoolSize();
    }

    private Integer getNormalCollectThreadQueueSize() {
        ExecutorService executorService = TaskRunningPool.getExecutorService();
        return ((ThreadPoolExecutor) executorService).getQueue().size();
    }

    private Integer getNormalCollectThreadQueueMax() {
        ExecutorService executorService = TaskRunningPool.getExecutorService();
        BlockingQueue<Runnable> queue = ((ThreadPoolExecutor) executorService).getQueue();
        return queue.size() + queue.remainingCapacity();
    }

    private Integer getNormalCollectThreadNumSize() {
        ExecutorService executorService = TaskRunningPool.getExecutorService();
        return ((ThreadPoolExecutor) executorService).getActiveCount();
    }

    private Integer getNormalCollectThreadNumMax() {
        ExecutorService executorService = TaskRunningPool.getExecutorService();
        return ((ThreadPoolExecutor) executorService).getMaximumPoolSize();
    }

    /**
     * @param hostName 主机名
     * @param heartbeatTime 当前时间戳（精度：毫秒）
     * @param heartbeatTimeMinute 当前时间戳（精度：分钟）
     * @param heartbeatTimeHour 当前时间戳（精度：小时）
     * @param heartbeatTimeDay 当前时间戳（精度：日）
     * @return 返回构建完成的NetCardMetrics对象集
     */
    private List<NetCardMetrics> buildNetCardMetricsList(String hostName, Long heartbeatTime, Long heartbeatTimeMinute, Long heartbeatTimeHour, Long heartbeatTimeDay) {
        List<NetCardInfo> netCardInfoList = GlobalProperties.getSystemMetricsService().getSystemNetCardInfoList();
        List<NetCardMetrics> netCardMetricsList = new ArrayList<>(netCardInfoList.size());
        for (NetCardInfo netCardInfo : netCardInfoList) {
            NetCardMetrics netCardMetrics = new NetCardMetrics();
            netCardMetrics.setHostname(hostName);
            netCardMetrics.setSystemnetcardsbandmacaddress(netCardInfo.getSystemNetCardsBandMacAddress());
            netCardMetrics.setSystemnetcardsbanddevice(netCardInfo.getSystemNetCardsBandDevice());
            netCardMetrics.setHeartbeattime(heartbeatTime);
            netCardMetrics.setHeartbeattimeminute(heartbeatTimeMinute);
            netCardMetrics.setHeartbeattimehour(heartbeatTimeHour);
            netCardMetrics.setHeartbeatTimeDay(heartbeatTimeDay);
            netCardMetrics.setSystemnetcardssendbytesps(netCardInfo.getSystemNetCardsSendBytesPs().getLast());
            netCardMetrics.setSystemnetcardssendbytespsmin(netCardInfo.getSystemNetCardsSendBytesPs().getMin());
            netCardMetrics.setSystemnetcardssendbytespsmax(netCardInfo.getSystemNetCardsSendBytesPs().getMax());
            netCardMetrics.setSystemnetcardssendbytespsmean(netCardInfo.getSystemNetCardsSendBytesPs().getAvg());
            netCardMetrics.setSystemnetcardssendbytespsstd(netCardInfo.getSystemNetCardsSendBytesPs().getStdDev());
            netCardMetrics.setSystemnetcardssendbytesps55quantile(netCardInfo.getSystemNetCardsSendBytesPs().getQuantile55());
            netCardMetrics.setSystemnetcardssendbytesps75quantile(netCardInfo.getSystemNetCardsSendBytesPs().getQuantile75());
            netCardMetrics.setSystemnetcardssendbytesps95quantile(netCardInfo.getSystemNetCardsSendBytesPs().getQuantile95());
            netCardMetrics.setSystemnetcardssendbytesps99quantile(netCardInfo.getSystemNetCardsSendBytesPs().getQuantile99());
            netCardMetrics.setSystemnetcardsbandwidth(netCardInfo.getSystemNetCardsBandWidth());
            netCardMetrics.setSystemnetcardsreceivebytesps(netCardInfo.getSystemNetCardsReceiveBytesPs().getLast());
            netCardMetrics.setSystemnetcardsreceivebytespsmin(netCardInfo.getSystemNetCardsReceiveBytesPs().getMin());
            netCardMetrics.setSystemnetcardsreceivebytespsmax(netCardInfo.getSystemNetCardsReceiveBytesPs().getMax());
            netCardMetrics.setSystemnetcardsreceivebytespsmean(netCardInfo.getSystemNetCardsReceiveBytesPs().getAvg());
            netCardMetrics.setSystemnetcardsreceivebytespsstd(netCardInfo.getSystemNetCardsReceiveBytesPs().getStdDev());
            netCardMetrics.setSystemnetcardsreceivebytesps55quantile(netCardInfo.getSystemNetCardsReceiveBytesPs().getQuantile55());
            netCardMetrics.setSystemnetcardsreceivebytesps75quantile(netCardInfo.getSystemNetCardsReceiveBytesPs().getQuantile75());
            netCardMetrics.setSystemnetcardsreceivebytesps95quantile(netCardInfo.getSystemNetCardsReceiveBytesPs().getQuantile95());
            netCardMetrics.setSystemnetcardsreceivebytesps99quantile(netCardInfo.getSystemNetCardsReceiveBytesPs().getQuantile99());
            netCardMetricsList.add(netCardMetrics);
        }
        return netCardMetricsList;
    }

    /**
     * @param hostName 主机名
     * @param heartbeatTime 当前时间戳（精度：毫秒）
     * @param heartbeatTimeMinute 当前时间戳（精度：分钟）
     * @param heartbeatTimeHour 当前时间戳（精度：小时）
     * @param heartbeatTimeDay 当前时间戳（精度：日）
     * @return 返回构建完成的SystemMetrics对象
     */
    private List<DiskIOMetrics> buildDiskIOMetricsList(String hostName, Long heartbeatTime, Long heartbeatTimeMinute, Long heartbeatTimeHour, Long heartbeatTimeDay) {
        List<DiskIOInfo> diskIOInfoList = GlobalProperties.getSystemMetricsService().getSystemDiskIOInfoList();
        List<DiskIOMetrics> diskIOMetricsList = new ArrayList<>(diskIOInfoList.size());
        for (DiskIOInfo diskIOInfo : diskIOInfoList) {
            DiskIOMetrics diskIOMetrics = new DiskIOMetrics();
            diskIOMetrics.setHostname(hostName);
            diskIOMetrics.setSystemdiskdevice(diskIOInfo.getDevice());
            diskIOMetrics.setHeartbeattime(heartbeatTime);
            diskIOMetrics.setHeartbeattimeminute(heartbeatTimeMinute);
            diskIOMetrics.setHeartbeattimehour(heartbeatTimeHour);
            diskIOMetrics.setHeartbeatTimeDay(heartbeatTimeDay);
            diskIOMetrics.setSystemioutil(diskIOInfo.getiOUtil().getLast());
            diskIOMetrics.setSystemioutilmin(diskIOInfo.getiOUtil().getMin());
            diskIOMetrics.setSystemioutilmax(diskIOInfo.getiOUtil().getMax());
            diskIOMetrics.setSystemioutilmean(diskIOInfo.getiOUtil().getAvg());
            diskIOMetrics.setSystemioutilstd(diskIOInfo.getiOUtil().getStdDev());
            diskIOMetrics.setSystemioutil55quantile(diskIOInfo.getiOUtil().getQuantile55());
            diskIOMetrics.setSystemioutil75quantile(diskIOInfo.getiOUtil().getQuantile75());
            diskIOMetrics.setSystemioutil95quantile(diskIOInfo.getiOUtil().getQuantile95());
            diskIOMetrics.setSystemioutil99quantile(diskIOInfo.getiOUtil().getQuantile99());
            diskIOMetrics.setSystemioavgqusz(diskIOInfo.getiOAvgQuSz().getLast());
            diskIOMetrics.setSystemioavgquszmin(diskIOInfo.getiOAvgQuSz().getMin());
            diskIOMetrics.setSystemioavgquszmax(diskIOInfo.getiOAvgQuSz().getMax());
            diskIOMetrics.setSystemioavgquszmean(diskIOInfo.getiOAvgQuSz().getAvg());
            diskIOMetrics.setSystemioavgquszstd(diskIOInfo.getiOAvgQuSz().getStdDev());
            diskIOMetrics.setSystemioavgqusz55quantile(diskIOInfo.getiOAvgQuSz().getQuantile55());
            diskIOMetrics.setSystemioavgqusz75quantile(diskIOInfo.getiOAvgQuSz().getQuantile75());
            diskIOMetrics.setSystemioavgqusz95quantile(diskIOInfo.getiOAvgQuSz().getQuantile95());
            diskIOMetrics.setSystemioavgqusz99quantile(diskIOInfo.getiOAvgQuSz().getQuantile99());
            diskIOMetrics.setSystemioavgrqsz(diskIOInfo.getiOAvgRqSz().getLast());
            diskIOMetrics.setSystemioavgrqszmin(diskIOInfo.getiOAvgRqSz().getMin());
            diskIOMetrics.setSystemioavgrqszmax(diskIOInfo.getiOAvgRqSz().getMax());
            diskIOMetrics.setSystemioavgrqszmean(diskIOInfo.getiOAvgRqSz().getAvg());
            diskIOMetrics.setSystemioavgrqszstd(diskIOInfo.getiOAvgRqSz().getStdDev());
            diskIOMetrics.setSystemioavgrqsz55quantile(diskIOInfo.getiOAvgRqSz().getQuantile55());
            diskIOMetrics.setSystemioavgrqsz75quantile(diskIOInfo.getiOAvgRqSz().getQuantile75());
            diskIOMetrics.setSystemioavgrqsz95quantile(diskIOInfo.getiOAvgRqSz().getQuantile95());
            diskIOMetrics.setSystemioavgrqsz99quantile(diskIOInfo.getiOAvgRqSz().getQuantile99());
            diskIOMetrics.setSystemioawait(diskIOInfo.getiOAwait().getLast());
            diskIOMetrics.setSystemioawaitmin(diskIOInfo.getiOAwait().getMin());
            diskIOMetrics.setSystemioawaitmax(diskIOInfo.getiOAwait().getMax());
            diskIOMetrics.setSystemioawaitmean(diskIOInfo.getiOAwait().getAvg());
            diskIOMetrics.setSystemioawaitstd(diskIOInfo.getiOAwait().getStdDev());
            diskIOMetrics.setSystemioawait55quantile(diskIOInfo.getiOAwait().getQuantile55());
            diskIOMetrics.setSystemioawait75quantile(diskIOInfo.getiOAwait().getQuantile75());
            diskIOMetrics.setSystemioawait95quantile(diskIOInfo.getiOAwait().getQuantile95());
            diskIOMetrics.setSystemioawait99quantile(diskIOInfo.getiOAwait().getQuantile99());
            diskIOMetrics.setSystemiorawait(diskIOInfo.getiORAwait().getLast());
            diskIOMetrics.setSystemiorawaitmin(diskIOInfo.getiORAwait().getMin());
            diskIOMetrics.setSystemiorawaitmax(diskIOInfo.getiORAwait().getMax());
            diskIOMetrics.setSystemiorawaitmean(diskIOInfo.getiORAwait().getAvg());
            diskIOMetrics.setSystemiorawaitstd(diskIOInfo.getiORAwait().getStdDev());
            diskIOMetrics.setSystemiorawait55quantile(diskIOInfo.getiORAwait().getQuantile55());
            diskIOMetrics.setSystemiorawait75quantile(diskIOInfo.getiORAwait().getQuantile75());
            diskIOMetrics.setSystemiorawait95quantile(diskIOInfo.getiORAwait().getQuantile95());
            diskIOMetrics.setSystemiorawait99quantile(diskIOInfo.getiORAwait().getQuantile99());
            diskIOMetrics.setSystemioreadrequest(diskIOInfo.getiOReadRequest().getLast());
            diskIOMetrics.setSystemioreadrequestmin(diskIOInfo.getiOReadRequest().getMin());
            diskIOMetrics.setSystemioreadrequestmax(diskIOInfo.getiOReadRequest().getMax());
            diskIOMetrics.setSystemioreadrequestmean(diskIOInfo.getiOReadRequest().getAvg());
            diskIOMetrics.setSystemioreadrequeststd(diskIOInfo.getiOReadRequest().getStdDev());
            diskIOMetrics.setSystemioreadrequest55quantile(diskIOInfo.getiOReadRequest().getQuantile55());
            diskIOMetrics.setSystemioreadrequest75quantile(diskIOInfo.getiOReadRequest().getQuantile75());
            diskIOMetrics.setSystemioreadrequest95quantile(diskIOInfo.getiOReadRequest().getQuantile95());
            diskIOMetrics.setSystemioreadrequest99quantile(diskIOInfo.getiOReadRequest().getQuantile99());
            diskIOMetrics.setSystemioreadbytes(diskIOInfo.getiOReadBytes().getLast());
            diskIOMetrics.setSystemioreadbytesmin(diskIOInfo.getiOReadBytes().getMin());
            diskIOMetrics.setSystemioreadbytesmax(diskIOInfo.getiOReadBytes().getMax());
            diskIOMetrics.setSystemioreadbytesmean(diskIOInfo.getiOReadBytes().getAvg());
            diskIOMetrics.setSystemioreadbytesstd(diskIOInfo.getiOReadBytes().getStdDev());
            diskIOMetrics.setSystemioreadbytes55quantile(diskIOInfo.getiOReadBytes().getQuantile55());
            diskIOMetrics.setSystemioreadbytes75quantile(diskIOInfo.getiOReadBytes().getQuantile75());
            diskIOMetrics.setSystemioreadbytes95quantile(diskIOInfo.getiOReadBytes().getQuantile95());
            diskIOMetrics.setSystemioreadbytes99quantile(diskIOInfo.getiOReadBytes().getQuantile99());
            diskIOMetrics.setSystemiorrqms(diskIOInfo.getiORRQMS().getLast());
            diskIOMetrics.setSystemiorrqmsmin(diskIOInfo.getiORRQMS().getMin());
            diskIOMetrics.setSystemiorrqmsmax(diskIOInfo.getiORRQMS().getMax());
            diskIOMetrics.setSystemiorrqmsmean(diskIOInfo.getiORRQMS().getAvg());
            diskIOMetrics.setSystemiorrqmsstd(diskIOInfo.getiORRQMS().getStdDev());
            diskIOMetrics.setSystemiorrqms55quantile(diskIOInfo.getiORRQMS().getQuantile55());
            diskIOMetrics.setSystemiorrqms75quantile(diskIOInfo.getiORRQMS().getQuantile75());
            diskIOMetrics.setSystemiorrqms95quantile(diskIOInfo.getiORRQMS().getQuantile95());
            diskIOMetrics.setSystemiorrqms99quantile(diskIOInfo.getiORRQMS().getQuantile99());
            diskIOMetrics.setSystemiosvctm(diskIOInfo.getiOSVCTM().getLast());
            diskIOMetrics.setSystemiosvctmmin(diskIOInfo.getiOSVCTM().getMin());
            diskIOMetrics.setSystemiosvctmmax(diskIOInfo.getiOSVCTM().getMax());
            diskIOMetrics.setSystemiosvctmmean(diskIOInfo.getiOSVCTM().getAvg());
            diskIOMetrics.setSystemiosvctmstd(diskIOInfo.getiOSVCTM().getStdDev());
            diskIOMetrics.setSystemiosvctm55quantile(diskIOInfo.getiOSVCTM().getQuantile55());
            diskIOMetrics.setSystemiosvctm75quantile(diskIOInfo.getiOSVCTM().getQuantile75());
            diskIOMetrics.setSystemiosvctm95quantile(diskIOInfo.getiOSVCTM().getQuantile95());
            diskIOMetrics.setSystemiosvctm99quantile(diskIOInfo.getiOSVCTM().getQuantile99());
            diskIOMetrics.setSystemiowawait(diskIOInfo.getiOWAwait().getLast());
            diskIOMetrics.setSystemiowawaitmin(diskIOInfo.getiOWAwait().getMin());
            diskIOMetrics.setSystemiowawaitmax(diskIOInfo.getiOWAwait().getMax());
            diskIOMetrics.setSystemiowawaitmean(diskIOInfo.getiOWAwait().getAvg());
            diskIOMetrics.setSystemiowawaitstd(diskIOInfo.getiOWAwait().getStdDev());
            diskIOMetrics.setSystemiowawait55quantile(diskIOInfo.getiOWAwait().getQuantile55());
            diskIOMetrics.setSystemiowawait75quantile(diskIOInfo.getiOWAwait().getQuantile75());
            diskIOMetrics.setSystemiowawait95quantile(diskIOInfo.getiOWAwait().getQuantile95());
            diskIOMetrics.setSystemiowawait99quantile(diskIOInfo.getiOWAwait().getQuantile99());
            diskIOMetrics.setSystemiowriterequest(diskIOInfo.getiOWriteRequest().getLast());
            diskIOMetrics.setSystemiowriterequestmin(diskIOInfo.getiOWriteRequest().getMin());
            diskIOMetrics.setSystemiowriterequestmax(diskIOInfo.getiOWriteRequest().getMax());
            diskIOMetrics.setSystemiowriterequestmean(diskIOInfo.getiOWriteRequest().getAvg());
            diskIOMetrics.setSystemiowriterequeststd(diskIOInfo.getiOWriteRequest().getStdDev());
            diskIOMetrics.setSystemiowriterequest55quantile(diskIOInfo.getiOWriteRequest().getQuantile55());
            diskIOMetrics.setSystemiowriterequest75quantile(diskIOInfo.getiOWriteRequest().getQuantile75());
            diskIOMetrics.setSystemiowriterequest95quantile(diskIOInfo.getiOWriteRequest().getQuantile95());
            diskIOMetrics.setSystemiowriterequest99quantile(diskIOInfo.getiOWriteRequest().getQuantile99());
            diskIOMetrics.setSystemiowritebytes(diskIOInfo.getiOWriteBytes().getLast());
            diskIOMetrics.setSystemiowritebytesmin(diskIOInfo.getiOWriteBytes().getMin());
            diskIOMetrics.setSystemiowritebytesmax(diskIOInfo.getiOWriteBytes().getMax());
            diskIOMetrics.setSystemiowritebytesmean(diskIOInfo.getiOWriteBytes().getAvg());
            diskIOMetrics.setSystemiowritebytesstd(diskIOInfo.getiOWriteBytes().getStdDev());
            diskIOMetrics.setSystemiowritebytes55quantile(diskIOInfo.getiOWriteBytes().getQuantile55());
            diskIOMetrics.setSystemiowritebytes75quantile(diskIOInfo.getiOWriteBytes().getQuantile75());
            diskIOMetrics.setSystemiowritebytes95quantile(diskIOInfo.getiOWriteBytes().getQuantile95());
            diskIOMetrics.setSystemiowritebytes99quantile(diskIOInfo.getiOWriteBytes().getQuantile99());
            diskIOMetrics.setSystemioreadwritebytes(diskIOInfo.getiOReadWriteBytes().getLast());
            diskIOMetrics.setSystemioreadwritebytesmin(diskIOInfo.getiOReadWriteBytes().getMin());
            diskIOMetrics.setSystemioreadwritebytesmax(diskIOInfo.getiOReadWriteBytes().getMax());
            diskIOMetrics.setSystemioreadwritebytesmean(diskIOInfo.getiOReadWriteBytes().getAvg());
            diskIOMetrics.setSystemioreadwritebytesstd(diskIOInfo.getiOReadWriteBytes().getStdDev());
            diskIOMetrics.setSystemioreadwritebytes55quantile(diskIOInfo.getiOReadWriteBytes().getQuantile55());
            diskIOMetrics.setSystemioreadwritebytes75quantile(diskIOInfo.getiOReadWriteBytes().getQuantile75());
            diskIOMetrics.setSystemioreadwritebytes95quantile(diskIOInfo.getiOReadWriteBytes().getQuantile95());
            diskIOMetrics.setSystemioreadwritebytes99quantile(diskIOInfo.getiOReadWriteBytes().getQuantile99());
            diskIOMetrics.setSystemiowrqms(diskIOInfo.getiOWRQMS().getLast());
            diskIOMetrics.setSystemiowrqmsmin(diskIOInfo.getiOWRQMS().getMin());
            diskIOMetrics.setSystemiowrqmsmax(diskIOInfo.getiOWRQMS().getMax());
            diskIOMetrics.setSystemiowrqmsmean(diskIOInfo.getiOWRQMS().getAvg());
            diskIOMetrics.setSystemiowrqmsstd(diskIOInfo.getiOWRQMS().getStdDev());
            diskIOMetrics.setSystemiowrqms55quantile(diskIOInfo.getiOWRQMS().getQuantile55());
            diskIOMetrics.setSystemiowrqms75quantile(diskIOInfo.getiOWRQMS().getQuantile75());
            diskIOMetrics.setSystemiowrqms95quantile(diskIOInfo.getiOWRQMS().getQuantile95());
            diskIOMetrics.setSystemiowrqms99quantile(diskIOInfo.getiOWRQMS().getQuantile99());
            diskIOMetrics.setSystemdiskreadtime(diskIOInfo.getReadTime().getLast());
            diskIOMetrics.setSystemdiskreadtimemin(diskIOInfo.getReadTime().getMin());
            diskIOMetrics.setSystemdiskreadtimemax(diskIOInfo.getReadTime().getMax());
            diskIOMetrics.setSystemdiskreadtimemean(diskIOInfo.getReadTime().getAvg());
            diskIOMetrics.setSystemdiskreadtimestd(diskIOInfo.getReadTime().getStdDev());
            diskIOMetrics.setSystemdiskreadtime55quantile(diskIOInfo.getReadTime().getQuantile55());
            diskIOMetrics.setSystemdiskreadtime75quantile(diskIOInfo.getReadTime().getQuantile75());
            diskIOMetrics.setSystemdiskreadtime95quantile(diskIOInfo.getReadTime().getQuantile95());
            diskIOMetrics.setSystemdiskreadtime99quantile(diskIOInfo.getReadTime().getQuantile99());
            diskIOMetrics.setSystemdiskreadtimepercent(diskIOInfo.getReadTimePercent().getLast());
            diskIOMetrics.setSystemdiskreadtimepercentmin(diskIOInfo.getReadTimePercent().getMin());
            diskIOMetrics.setSystemdiskreadtimepercentmax(diskIOInfo.getReadTimePercent().getMax());
            diskIOMetrics.setSystemdiskreadtimepercentmean(diskIOInfo.getReadTimePercent().getAvg());
            diskIOMetrics.setSystemdiskreadtimepercentstd(diskIOInfo.getReadTimePercent().getStdDev());
            diskIOMetrics.setSystemdiskreadtimepercent55quantile(diskIOInfo.getReadTimePercent().getQuantile55());
            diskIOMetrics.setSystemdiskreadtimepercent75quantile(diskIOInfo.getReadTimePercent().getQuantile75());
            diskIOMetrics.setSystemdiskreadtimepercent95quantile(diskIOInfo.getReadTimePercent().getQuantile95());
            diskIOMetrics.setSystemdiskreadtimepercent99quantile(diskIOInfo.getReadTimePercent().getQuantile99());
            diskIOMetrics.setSystemdiskwritetime(diskIOInfo.getWriteTime().getLast());
            diskIOMetrics.setSystemdiskwritetimemin(diskIOInfo.getWriteTime().getMin());
            diskIOMetrics.setSystemdiskwritetimemax(diskIOInfo.getWriteTime().getMax());
            diskIOMetrics.setSystemdiskwritetimemean(diskIOInfo.getWriteTime().getAvg());
            diskIOMetrics.setSystemdiskwritetimestd(diskIOInfo.getWriteTime().getStdDev());
            diskIOMetrics.setSystemdiskwritetime55quantile(diskIOInfo.getWriteTime().getQuantile55());
            diskIOMetrics.setSystemdiskwritetime75quantile(diskIOInfo.getWriteTime().getQuantile75());
            diskIOMetrics.setSystemdiskwritetime95quantile(diskIOInfo.getWriteTime().getQuantile95());
            diskIOMetrics.setSystemdiskwritetime99quantile(diskIOInfo.getWriteTime().getQuantile99());
            diskIOMetrics.setSystemdiskwritetimepercent(diskIOInfo.getWriteTimePercent().getLast());
            diskIOMetrics.setSystemdiskwritetimepercentmin(diskIOInfo.getWriteTimePercent().getMin());
            diskIOMetrics.setSystemdiskwritetimepercentmax(diskIOInfo.getWriteTimePercent().getMax());
            diskIOMetrics.setSystemdiskwritetimepercentmean(diskIOInfo.getWriteTimePercent().getAvg());
            diskIOMetrics.setSystemdiskwritetimepercentstd(diskIOInfo.getWriteTimePercent().getStdDev());
            diskIOMetrics.setSystemdiskwritetimepercent55quantile(diskIOInfo.getWriteTimePercent().getQuantile55());
            diskIOMetrics.setSystemdiskwritetimepercent75quantile(diskIOInfo.getWriteTimePercent().getQuantile75());
            diskIOMetrics.setSystemdiskwritetimepercent95quantile(diskIOInfo.getWriteTimePercent().getQuantile95());
            diskIOMetrics.setSystemdiskwritetimepercent99quantile(diskIOInfo.getWriteTimePercent().getQuantile99());
            diskIOMetricsList.add(diskIOMetrics);
        }
        return diskIOMetricsList;
    }

    /**
     * @param hostName 主机名
     * @param heartbeatTime 当前时间戳（精度：毫秒）
     * @param heartbeatTimeMinute 当前时间戳（精度：分钟）
     * @param heartbeatTimeHour 当前时间戳（精度：小时）
     * @param heartbeatTimeDay 当前时间戳（精度：日）
     * @return 返回构建完成的ProcessMetrics对象
     */
    private ProcessMetrics buildProcessMetrics(String hostName, Long heartbeatTime,
                                               Long heartbeatTimeMinute, Long heartbeatTimeHour,
                                               Long heartbeatTimeDay) {
        ProcessMetrics processMetrics = new ProcessMetrics();
        processMetrics.setHostname(hostName);
        processMetrics.setProccpuutil(GlobalProperties.getProcessMetricsService().getProcCpuUtil()
            .getLast());
        processMetrics.setProccpuutilmin(GlobalProperties.getProcessMetricsService()
            .getProcCpuUtil().getMin());
        processMetrics.setProccpuutilmax(GlobalProperties.getProcessMetricsService()
            .getProcCpuUtil().getMax());
        processMetrics.setProccpuutilmean(GlobalProperties.getProcessMetricsService()
            .getProcCpuUtil().getAvg());
        processMetrics.setProccpuutilstd(GlobalProperties.getProcessMetricsService()
            .getProcCpuUtil().getStdDev());
        processMetrics.setProccpuutil55quantile(GlobalProperties.getProcessMetricsService()
            .getProcCpuUtil().getQuantile55());
        processMetrics.setProccpuutil75quantile(GlobalProperties.getProcessMetricsService()
            .getProcCpuUtil().getQuantile75());
        processMetrics.setProccpuutil95quantile(GlobalProperties.getProcessMetricsService()
            .getProcCpuUtil().getQuantile95());
        processMetrics.setProccpuutil99quantile(GlobalProperties.getProcessMetricsService()
            .getProcCpuUtil().getQuantile99());
        processMetrics.setProcmemused(GlobalProperties.getProcessMetricsService().getProcMemUsed());
        processMetrics.setProcnetworksendbytesps(GlobalProperties.getProcessMetricsService()
            .getProcNetworkSendBytesPs().getLast().longValue());
        processMetrics.setProcnetworksendbytespsmin(GlobalProperties.getProcessMetricsService()
            .getProcNetworkSendBytesPs().getMin().longValue());
        processMetrics.setProcnetworksendbytespsmax(GlobalProperties.getProcessMetricsService()
            .getProcNetworkSendBytesPs().getMax().longValue());
        processMetrics.setProcnetworksendbytespsmean(GlobalProperties.getProcessMetricsService()
            .getProcNetworkSendBytesPs().getAvg().longValue());
        processMetrics.setProcnetworksendbytespsstd(GlobalProperties.getProcessMetricsService()
            .getProcNetworkSendBytesPs().getStdDev().longValue());
        processMetrics.setProcnetworksendbytesps55quantile(GlobalProperties
            .getProcessMetricsService().getProcNetworkSendBytesPs().getQuantile55().longValue());
        processMetrics.setProcnetworksendbytesps75quantile(GlobalProperties
            .getProcessMetricsService().getProcNetworkSendBytesPs().getQuantile75().longValue());
        processMetrics.setProcnetworksendbytesps95quantile(GlobalProperties
            .getProcessMetricsService().getProcNetworkSendBytesPs().getQuantile95().longValue());
        processMetrics.setProcnetworksendbytesps99quantile(GlobalProperties
            .getProcessMetricsService().getProcNetworkSendBytesPs().getQuantile99().longValue());
        processMetrics.setProcnetworkreceivebytesps(GlobalProperties.getProcessMetricsService()
            .getProcNetworkReceiveBytesPs().getLast().longValue());
        processMetrics.setProcnetworkreceivebytespsmin(GlobalProperties.getProcessMetricsService()
            .getProcNetworkReceiveBytesPs().getMin().longValue());
        processMetrics.setProcnetworkreceivebytespsmax(GlobalProperties.getProcessMetricsService()
            .getProcNetworkReceiveBytesPs().getMax().longValue());
        processMetrics.setProcnetworkreceivebytespsmean(GlobalProperties.getProcessMetricsService()
            .getProcNetworkReceiveBytesPs().getAvg().longValue());
        processMetrics.setProcnetworkreceivebytespsstd(GlobalProperties.getProcessMetricsService()
            .getProcNetworkReceiveBytesPs().getStdDev().longValue());
        processMetrics.setProcnetworkreceivebytesps55quantile(GlobalProperties
            .getProcessMetricsService().getProcNetworkReceiveBytesPs().getQuantile55().longValue());
        processMetrics.setProcnetworkreceivebytesps75quantile(GlobalProperties
            .getProcessMetricsService().getProcNetworkReceiveBytesPs().getQuantile75().longValue());
        processMetrics.setProcnetworkreceivebytesps95quantile(GlobalProperties
            .getProcessMetricsService().getProcNetworkReceiveBytesPs().getQuantile95().longValue());
        processMetrics.setProcnetworkreceivebytesps99quantile(GlobalProperties
            .getProcessMetricsService().getProcNetworkReceiveBytesPs().getQuantile99().longValue());
        processMetrics.setJvmprocfullgccount(GlobalProperties.getProcessMetricsService()
            .getJvmProcFullGcCount());
        processMetrics.setProcopenfdcount(GlobalProperties.getProcessMetricsService()
            .getProcOpenFdCount());
        processMetrics.setJvmProcS0C(GlobalProperties.getProcessMetricsService().getJvmProcS0C());
        processMetrics.setHeartbeattime(heartbeatTime);
        processMetrics.setHeartbeattimeminute(heartbeatTimeMinute);
        processMetrics.setHeartbeattimehour(heartbeatTimeHour);
        processMetrics.setHeartbeatTimeDay(heartbeatTimeDay);
        return processMetrics;
    }

    /**
     * @param hostName 主机名
     * @param heartbeatTime 当前时间戳（精度：毫秒）
     * @param heartbeatTimeMinute 当前时间戳（精度：分钟）
     * @param heartbeatTimeHour 当前时间戳（精度：小时）
     * @param heartbeatTimeDay 当前时间戳（精度：日）
     * @return 返回构建完成的SystemMetrics对象
     */
    private SystemMetrics buildSystemMetrics(String hostName, Long heartbeatTime,
                                             Long heartbeatTimeMinute, Long heartbeatTimeHour,
                                             Long heartbeatTimeDay) {
        SystemMetrics systemMetrics = new SystemMetrics();
        systemMetrics.setHostname(hostName);
        systemMetrics.setSystemntpoffset(GlobalProperties.getSystemMetricsService()
            .getSystemNtpOffset());
        systemMetrics.setSystemcpuutil(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUtil().getLast());
        systemMetrics.setSystemcpuutil55quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUtil().getQuantile55());
        systemMetrics.setSystemcpuutil75quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUtil().getQuantile75());
        systemMetrics.setSystemcpuutil95quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUtil().getQuantile95());
        systemMetrics.setSystemcpuutilmax(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUtil().getMax());
        systemMetrics.setSystemcpuutil99quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUtil().getQuantile99());
        systemMetrics.setSystemcpuutilmean(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUtil().getAvg());
        systemMetrics.setSystemcpuutilmin(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUtil().getMin());
        systemMetrics.setSystemcpuutilstd(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUtil().getStdDev());
        systemMetrics.setSystemmemfree(GlobalProperties.getSystemMetricsService()
            .getSystemMemFree());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPs(GlobalProperties
            .getSystemMetricsService().getSystemNetworkSendAndReceiveBytesPs().getLast());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPsMin(GlobalProperties
            .getSystemMetricsService().getSystemNetworkSendAndReceiveBytesPs().getMin());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPsMax(GlobalProperties
            .getSystemMetricsService().getSystemNetworkSendAndReceiveBytesPs().getMax());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPsMean(GlobalProperties
            .getSystemMetricsService().getSystemNetworkSendAndReceiveBytesPs().getAvg());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPsStd(GlobalProperties
            .getSystemMetricsService().getSystemNetworkSendAndReceiveBytesPs().getStdDev());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPs55Quantile(GlobalProperties
            .getSystemMetricsService().getSystemNetworkSendAndReceiveBytesPs().getQuantile55());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPs75Quantile(GlobalProperties
            .getSystemMetricsService().getSystemNetworkSendAndReceiveBytesPs().getQuantile75());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPs95Quantile(GlobalProperties
            .getSystemMetricsService().getSystemNetworkSendAndReceiveBytesPs().getQuantile95());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPs99Quantile(GlobalProperties
            .getSystemMetricsService().getSystemNetworkSendAndReceiveBytesPs().getQuantile99());
        systemMetrics.setSystemNetWorkBandWidthUsedPercent(GlobalProperties
            .getSystemMetricsService().getSystemNetWorkBandWidthUsedPercent().getLast());
        systemMetrics.setSystemNetWorkBandWidthUsedPercentMin(GlobalProperties
            .getSystemMetricsService().getSystemNetWorkBandWidthUsedPercent().getMin());
        systemMetrics.setSystemNetWorkBandWidthUsedPercentMax(GlobalProperties
            .getSystemMetricsService().getSystemNetWorkBandWidthUsedPercent().getMax());
        systemMetrics.setSystemNetWorkBandWidthUsedPercentMean(GlobalProperties
            .getSystemMetricsService().getSystemNetWorkBandWidthUsedPercent().getAvg());
        systemMetrics.setSystemNetWorkBandWidthUsedPercentStd(GlobalProperties
            .getSystemMetricsService().getSystemNetWorkBandWidthUsedPercent().getStdDev());
        systemMetrics.setSystemNetWorkBandWidthUsedPercent55Quantile(GlobalProperties
            .getSystemMetricsService().getSystemNetWorkBandWidthUsedPercent().getQuantile55());
        systemMetrics.setSystemNetWorkBandWidthUsedPercent75Quantile(GlobalProperties
            .getSystemMetricsService().getSystemNetWorkBandWidthUsedPercent().getQuantile75());
        systemMetrics.setSystemNetWorkBandWidthUsedPercent95Quantile(GlobalProperties
            .getSystemMetricsService().getSystemNetWorkBandWidthUsedPercent().getQuantile95());
        systemMetrics.setSystemNetWorkBandWidthUsedPercent99Quantile(GlobalProperties
            .getSystemMetricsService().getSystemNetWorkBandWidthUsedPercent().getQuantile99());
        systemMetrics.setHeartbeattime(heartbeatTime);
        systemMetrics.setHeartbeattimeminute(heartbeatTimeMinute);
        systemMetrics.setHeartbeattimehour(heartbeatTimeHour);
        systemMetrics.setHeartbeatTimeDay(heartbeatTimeDay);
        return systemMetrics;
    }

    /**
     * @return 返回构建完成的AgentMetrics对象
     */
    private AgentMetrics buildAgentMetrics() {
        String hostName = CommonUtils.getHOSTNAME();//GlobalProperties.getSystemMetricsService().getHostName();
        Date current = new Date();
        Long heartbeatTime = current.getTime();
        Long heartbeatTimeMinute = DateUtils.getMinuteUnitTimeStamp(current);
        Long heartbeatTimeHour = DateUtils.getHourUnitTimeStamp(current);
        Long heartbeatTimeDay = DateUtils.getDayUnitTimeStamp(current);
        SystemMetrics systemMetrics = buildSystemMetrics(hostName, heartbeatTime,
            heartbeatTimeMinute, heartbeatTimeHour, heartbeatTimeDay);
        ProcessMetrics processMetrics = buildProcessMetrics(hostName, heartbeatTime,
            heartbeatTimeMinute, heartbeatTimeHour, heartbeatTimeDay);
        List<DiskMetrics> diskMetricsList = buildDiskMetricsList(hostName, heartbeatTime,
            heartbeatTimeMinute, heartbeatTimeHour, heartbeatTimeDay);
        List<DiskIOMetrics> diskIOMetricsList = buildDiskIOMetricsList(hostName, heartbeatTime,
            heartbeatTimeMinute, heartbeatTimeHour, heartbeatTimeDay);
        List<NetCardMetrics> netCardMetricsList = buildNetCardMetricsList(hostName, heartbeatTime,
            heartbeatTimeMinute, heartbeatTimeHour, heartbeatTimeDay);
        AgentBusinessMetrics agentBusinessMetrics = buildAgentBusinessMetrics(hostName,
            heartbeatTime, heartbeatTimeMinute, heartbeatTimeHour, heartbeatTimeDay);
        AgentMetrics agentMetrics = new AgentMetrics(systemMetrics, processMetrics,
            diskIOMetricsList, netCardMetricsList, agentBusinessMetrics, diskMetricsList);
        return agentMetrics;
    }

    /**
     * @param hostName 主机名
     * @param heartbeatTime 当前时间戳（精度：毫秒）
     * @param heartbeatTimeMinute 当前时间戳（精度：分钟）
     * @param heartbeatTimeHour 当前时间戳（精度：小时）
     * @param heartbeatTimeDay 当前时间戳（精度：日）
     * @return 返回构建完成的DiskMetrics对象集
     */
    private List<DiskMetrics> buildDiskMetricsList(String hostName, Long heartbeatTime, Long heartbeatTimeMinute, Long heartbeatTimeHour, Long heartbeatTimeDay) {
        List<DiskInfo> diskInfoList = GlobalProperties.getSystemMetricsService().getSystemDiskInfoList();
        List<DiskMetrics> diskMetricsList = new ArrayList<>(diskInfoList.size());
        for (DiskInfo diskInfo : diskInfoList) {
            DiskMetrics diskMetrics = new DiskMetrics();
            diskMetrics.setHostname(hostName);
            diskMetrics.setSystemdiskpath(diskInfo.getPath());
            diskMetrics.setSystemdiskfstype(diskInfo.getFsType());
            diskMetrics.setHeartbeattime(heartbeatTime);
            diskMetrics.setHeartbeattimeminute(heartbeatTimeMinute);
            diskMetrics.setHeartbeattimehour(heartbeatTimeHour);
            diskMetrics.setHeartbeatTimeDay(heartbeatTimeDay);
            diskMetrics.setSystemdiskbytesfree(diskInfo.getBytesFree());
            diskMetrics.setSystemdiskbytestotal(diskInfo.getBytesTotal());
            diskMetrics.setSystemdiskbytesused(diskInfo.getBytesUsed());
            diskMetrics.setSystemdiskusedpercent(diskInfo.getBytesUsedPercent());
            diskMetrics.setSystemdiskinodestotal(diskInfo.getInodesTotal());
            diskMetrics.setSystemdiskinodesfree(diskInfo.getInodesFree());
            diskMetrics.setSystemdiskinodesused(diskInfo.getInodesUsed());
            diskMetrics.setSystemdiskinodesusedpercent(diskInfo.getInodesUsedPercent());
            diskMetricsList.add(diskMetrics);
        }
        return diskMetricsList;
    }

    /**
     * 填充初始化时缓存的指标
     * @param agentMetrics 待填充 agent 相关指标对象
     */
    private void setInitMetrics(AgentMetrics agentMetrics) {
        agentMetrics.getSystemMetrics().setSystemstartuptime(systemStartupTime);
        agentMetrics.getSystemMetrics().setCpucores(cpuCores);
        agentMetrics.getProcessMetrics().setProcstartuptime(processStartupTime);
        agentMetrics.getProcessMetrics().setProcpid(processPid);
        agentMetrics.getAgentBusinessMetrics().setAgentversion(agentVersion);
    }

    /**
     * @return 返回根据agent配置文件获取到的agent对应版本号
     */
    private String getAgentVersion() {
        Map<String, String> settings = null;
        try {
            settings = CommonUtils.readSettings();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        if (settings == null) {
            LOGGER.error("setting is null");
            LogGather.recordErrorLog("AgentStatistics error", "get local settings error.");
            throw new NullPointerException();
        }
        String agentVersion = settings.get(LogConfigConstants.AGENT_VERSION);
        return agentVersion;
    }

    public void sinkOneRecord(long bytes, long cost) {
        agentSendCountPerPeriod.incr();
        agentSendBytePerPeriod.incr(bytes);
    }

    public void sinkMutilRecord(int num, long bytes, long cost) {
        agentSendCountPerPeriod.incr(num);
        agentSendBytePerPeriod.incr(bytes);
    }

    public void setRunningCollectTaskNum(Integer runningCollectTaskNum) {
        this.runningCollectTaskNum = runningCollectTaskNum;
    }

    public void setCollectTaskNum(Integer collectTaskNum) {
        this.collectTaskNum = collectTaskNum;
    }

    public void setCollectPathNum(Integer collectPathNum) {
        this.collectPathNum = collectPathNum;
    }

    public void setRunningCollectPathNum(Integer runningCollectPathNum) {
        this.runningCollectPathNum = runningCollectPathNum;
    }

    public void sourceOneRecord(long bytes, long cost) {
        agentReadCountPerPeriod.incr();
        agentReadBytePerPeriod.incr(bytes);
    }

    public void sendErrorLogsRecord() {
        this.errorLogsCountPerPeriod.incr();
    }

    public void sendErrorLogsFailedRecord() {
        this.errorLogsSendFailedCount.incr();
    }

}
