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
import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;
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
        agentBusinessMetrics.setLimittps(limiter.getAllQps());
        agentBusinessMetrics.setCpulimit(Float.valueOf(limiter.getConfig().getCpuThreshold())
            .doubleValue());
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
            PeriodStatistics systemNetCardsSendBytesPs = netCardInfo.getSystemNetCardsSendBytesPs();
            netCardMetrics.setSystemnetcardssendbytesps(systemNetCardsSendBytesPs.getLast());
            netCardMetrics.setSystemnetcardssendbytespsmin(systemNetCardsSendBytesPs.getMin());
            netCardMetrics.setSystemnetcardssendbytespsmax(systemNetCardsSendBytesPs.getMax());
            netCardMetrics.setSystemnetcardssendbytespsmean(systemNetCardsSendBytesPs.getAvg());
            netCardMetrics.setSystemnetcardssendbytespsstd(systemNetCardsSendBytesPs.getStdDev());
            netCardMetrics.setSystemnetcardssendbytesps55quantile(systemNetCardsSendBytesPs.getQuantile55());
            netCardMetrics.setSystemnetcardssendbytesps75quantile(systemNetCardsSendBytesPs.getQuantile75());
            netCardMetrics.setSystemnetcardssendbytesps95quantile(systemNetCardsSendBytesPs.getQuantile95());
            netCardMetrics.setSystemnetcardssendbytesps99quantile(systemNetCardsSendBytesPs.getQuantile99());
            netCardMetrics.setSystemnetcardsbandwidth(netCardInfo.getSystemNetCardsBandWidth());
            PeriodStatistics systemNetCardsReceiveBytesPs = netCardInfo.getSystemNetCardsReceiveBytesPs();
            netCardMetrics.setSystemnetcardsreceivebytesps(systemNetCardsReceiveBytesPs.getLast());
            netCardMetrics.setSystemnetcardsreceivebytespsmin(systemNetCardsReceiveBytesPs.getMin());
            netCardMetrics.setSystemnetcardsreceivebytespsmax(systemNetCardsReceiveBytesPs.getMax());
            netCardMetrics.setSystemnetcardsreceivebytespsmean(systemNetCardsReceiveBytesPs.getAvg());
            netCardMetrics.setSystemnetcardsreceivebytespsstd(systemNetCardsReceiveBytesPs.getStdDev());
            netCardMetrics.setSystemnetcardsreceivebytesps55quantile(systemNetCardsReceiveBytesPs.getQuantile55());
            netCardMetrics.setSystemnetcardsreceivebytesps75quantile(systemNetCardsReceiveBytesPs.getQuantile75());
            netCardMetrics.setSystemnetcardsreceivebytesps95quantile(systemNetCardsReceiveBytesPs.getQuantile95());
            netCardMetrics.setSystemnetcardsreceivebytesps99quantile(systemNetCardsReceiveBytesPs.getQuantile99());
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
            PeriodStatistics iOUtil = diskIOInfo.getiOUtil();
            diskIOMetrics.setSystemioutil(iOUtil.getLast());
            diskIOMetrics.setSystemioutilmin(iOUtil.getMin());
            diskIOMetrics.setSystemioutilmax(iOUtil.getMax());
            diskIOMetrics.setSystemioutilmean(iOUtil.getAvg());
            diskIOMetrics.setSystemioutilstd(iOUtil.getStdDev());
            diskIOMetrics.setSystemioutil55quantile(iOUtil.getQuantile55());
            diskIOMetrics.setSystemioutil75quantile(iOUtil.getQuantile75());
            diskIOMetrics.setSystemioutil95quantile(iOUtil.getQuantile95());
            diskIOMetrics.setSystemioutil99quantile(iOUtil.getQuantile99());
            PeriodStatistics iOAvgQuSz = diskIOInfo.getiOAvgQuSz();
            diskIOMetrics.setSystemioavgqusz(iOAvgQuSz.getLast());
            diskIOMetrics.setSystemioavgquszmin(iOAvgQuSz.getMin());
            diskIOMetrics.setSystemioavgquszmax(iOAvgQuSz.getMax());
            diskIOMetrics.setSystemioavgquszmean(iOAvgQuSz.getAvg());
            diskIOMetrics.setSystemioavgquszstd(iOAvgQuSz.getStdDev());
            diskIOMetrics.setSystemioavgqusz55quantile(iOAvgQuSz.getQuantile55());
            diskIOMetrics.setSystemioavgqusz75quantile(iOAvgQuSz.getQuantile75());
            diskIOMetrics.setSystemioavgqusz95quantile(iOAvgQuSz.getQuantile95());
            diskIOMetrics.setSystemioavgqusz99quantile(iOAvgQuSz.getQuantile99());
            PeriodStatistics iOAvgRqSz = diskIOInfo.getiOAvgRqSz();
            diskIOMetrics.setSystemioavgrqsz(iOAvgRqSz.getLast());
            diskIOMetrics.setSystemioavgrqszmin(iOAvgRqSz.getMin());
            diskIOMetrics.setSystemioavgrqszmax(iOAvgRqSz.getMax());
            diskIOMetrics.setSystemioavgrqszmean(iOAvgRqSz.getAvg());
            diskIOMetrics.setSystemioavgrqszstd(iOAvgRqSz.getStdDev());
            diskIOMetrics.setSystemioavgrqsz55quantile(iOAvgRqSz.getQuantile55());
            diskIOMetrics.setSystemioavgrqsz75quantile(iOAvgRqSz.getQuantile75());
            diskIOMetrics.setSystemioavgrqsz95quantile(iOAvgRqSz.getQuantile95());
            diskIOMetrics.setSystemioavgrqsz99quantile(iOAvgRqSz.getQuantile99());
            PeriodStatistics iOAwait = diskIOInfo.getiOAwait();
            diskIOMetrics.setSystemioawait(iOAwait.getLast());
            diskIOMetrics.setSystemioawaitmin(iOAwait.getMin());
            diskIOMetrics.setSystemioawaitmax(iOAwait.getMax());
            diskIOMetrics.setSystemioawaitmean(iOAwait.getAvg());
            diskIOMetrics.setSystemioawaitstd(iOAwait.getStdDev());
            diskIOMetrics.setSystemioawait55quantile(iOAwait.getQuantile55());
            diskIOMetrics.setSystemioawait75quantile(iOAwait.getQuantile75());
            diskIOMetrics.setSystemioawait95quantile(iOAwait.getQuantile95());
            diskIOMetrics.setSystemioawait99quantile(iOAwait.getQuantile99());
            PeriodStatistics iORAwait = diskIOInfo.getiORAwait();
            diskIOMetrics.setSystemiorawait(iORAwait.getLast());
            diskIOMetrics.setSystemiorawaitmin(iORAwait.getMin());
            diskIOMetrics.setSystemiorawaitmax(iORAwait.getMax());
            diskIOMetrics.setSystemiorawaitmean(iORAwait.getAvg());
            diskIOMetrics.setSystemiorawaitstd(iORAwait.getStdDev());
            diskIOMetrics.setSystemiorawait55quantile(iORAwait.getQuantile55());
            diskIOMetrics.setSystemiorawait75quantile(iORAwait.getQuantile75());
            diskIOMetrics.setSystemiorawait95quantile(iORAwait.getQuantile95());
            diskIOMetrics.setSystemiorawait99quantile(iORAwait.getQuantile99());
            PeriodStatistics iOReadRequest = diskIOInfo.getiOReadRequest();
            diskIOMetrics.setSystemioreadrequest(iOReadRequest.getLast());
            diskIOMetrics.setSystemioreadrequestmin(iOReadRequest.getMin());
            diskIOMetrics.setSystemioreadrequestmax(iOReadRequest.getMax());
            diskIOMetrics.setSystemioreadrequestmean(iOReadRequest.getAvg());
            diskIOMetrics.setSystemioreadrequeststd(iOReadRequest.getStdDev());
            diskIOMetrics.setSystemioreadrequest55quantile(iOReadRequest.getQuantile55());
            diskIOMetrics.setSystemioreadrequest75quantile(iOReadRequest.getQuantile75());
            diskIOMetrics.setSystemioreadrequest95quantile(iOReadRequest.getQuantile95());
            diskIOMetrics.setSystemioreadrequest99quantile(iOReadRequest.getQuantile99());
            PeriodStatistics iOReadBytes = diskIOInfo.getiOReadBytes();
            diskIOMetrics.setSystemioreadbytes(iOReadBytes.getLast());
            diskIOMetrics.setSystemioreadbytesmin(iOReadBytes.getMin());
            diskIOMetrics.setSystemioreadbytesmax(iOReadBytes.getMax());
            diskIOMetrics.setSystemioreadbytesmean(iOReadBytes.getAvg());
            diskIOMetrics.setSystemioreadbytesstd(iOReadBytes.getStdDev());
            diskIOMetrics.setSystemioreadbytes55quantile(iOReadBytes.getQuantile55());
            diskIOMetrics.setSystemioreadbytes75quantile(iOReadBytes.getQuantile75());
            diskIOMetrics.setSystemioreadbytes95quantile(iOReadBytes.getQuantile95());
            diskIOMetrics.setSystemioreadbytes99quantile(iOReadBytes.getQuantile99());
            PeriodStatistics iORRQMS = diskIOInfo.getiORRQMS();
            diskIOMetrics.setSystemiorrqms(iORRQMS.getLast());
            diskIOMetrics.setSystemiorrqmsmin(iORRQMS.getMin());
            diskIOMetrics.setSystemiorrqmsmax(iORRQMS.getMax());
            diskIOMetrics.setSystemiorrqmsmean(iORRQMS.getAvg());
            diskIOMetrics.setSystemiorrqmsstd(iORRQMS.getStdDev());
            diskIOMetrics.setSystemiorrqms55quantile(iORRQMS.getQuantile55());
            diskIOMetrics.setSystemiorrqms75quantile(iORRQMS.getQuantile75());
            diskIOMetrics.setSystemiorrqms95quantile(iORRQMS.getQuantile95());
            diskIOMetrics.setSystemiorrqms99quantile(iORRQMS.getQuantile99());
            PeriodStatistics iOSVCTM = diskIOInfo.getiOSVCTM();
            diskIOMetrics.setSystemiosvctm(iOSVCTM.getLast());
            diskIOMetrics.setSystemiosvctmmin(iOSVCTM.getMin());
            diskIOMetrics.setSystemiosvctmmax(iOSVCTM.getMax());
            diskIOMetrics.setSystemiosvctmmean(iOSVCTM.getAvg());
            diskIOMetrics.setSystemiosvctmstd(iOSVCTM.getStdDev());
            diskIOMetrics.setSystemiosvctm55quantile(iOSVCTM.getQuantile55());
            diskIOMetrics.setSystemiosvctm75quantile(iOSVCTM.getQuantile75());
            diskIOMetrics.setSystemiosvctm95quantile(iOSVCTM.getQuantile95());
            diskIOMetrics.setSystemiosvctm99quantile(iOSVCTM.getQuantile99());
            PeriodStatistics iOWAwait = diskIOInfo.getiOWAwait();
            diskIOMetrics.setSystemiowawait(iOWAwait.getLast());
            diskIOMetrics.setSystemiowawaitmin(iOWAwait.getMin());
            diskIOMetrics.setSystemiowawaitmax(iOWAwait.getMax());
            diskIOMetrics.setSystemiowawaitmean(iOWAwait.getAvg());
            diskIOMetrics.setSystemiowawaitstd(iOWAwait.getStdDev());
            diskIOMetrics.setSystemiowawait55quantile(iOWAwait.getQuantile55());
            diskIOMetrics.setSystemiowawait75quantile(iOWAwait.getQuantile75());
            diskIOMetrics.setSystemiowawait95quantile(iOWAwait.getQuantile95());
            diskIOMetrics.setSystemiowawait99quantile(iOWAwait.getQuantile99());
            PeriodStatistics iOWriteRequest = diskIOInfo.getiOWriteRequest();
            diskIOMetrics.setSystemiowriterequest(iOWriteRequest.getLast());
            diskIOMetrics.setSystemiowriterequestmin(iOWriteRequest.getMin());
            diskIOMetrics.setSystemiowriterequestmax(iOWriteRequest.getMax());
            diskIOMetrics.setSystemiowriterequestmean(iOWriteRequest.getAvg());
            diskIOMetrics.setSystemiowriterequeststd(iOWriteRequest.getStdDev());
            diskIOMetrics.setSystemiowriterequest55quantile(iOWriteRequest.getQuantile55());
            diskIOMetrics.setSystemiowriterequest75quantile(iOWriteRequest.getQuantile75());
            diskIOMetrics.setSystemiowriterequest95quantile(iOWriteRequest.getQuantile95());
            diskIOMetrics.setSystemiowriterequest99quantile(iOWriteRequest.getQuantile99());
            PeriodStatistics iOWriteBytes = diskIOInfo.getiOWriteBytes();
            diskIOMetrics.setSystemiowritebytes(iOWriteBytes.getLast());
            diskIOMetrics.setSystemiowritebytesmin(iOWriteBytes.getMin());
            diskIOMetrics.setSystemiowritebytesmax(iOWriteBytes.getMax());
            diskIOMetrics.setSystemiowritebytesmean(iOWriteBytes.getAvg());
            diskIOMetrics.setSystemiowritebytesstd(iOWriteBytes.getStdDev());
            diskIOMetrics.setSystemiowritebytes55quantile(iOWriteBytes.getQuantile55());
            diskIOMetrics.setSystemiowritebytes75quantile(iOWriteBytes.getQuantile75());
            diskIOMetrics.setSystemiowritebytes95quantile(iOWriteBytes.getQuantile95());
            diskIOMetrics.setSystemiowritebytes99quantile(iOWriteBytes.getQuantile99());
            PeriodStatistics iOReadWriteBytes = diskIOInfo.getiOReadWriteBytes();
            diskIOMetrics.setSystemioreadwritebytes(iOReadWriteBytes.getLast());
            diskIOMetrics.setSystemioreadwritebytesmin(iOReadWriteBytes.getMin());
            diskIOMetrics.setSystemioreadwritebytesmax(iOReadWriteBytes.getMax());
            diskIOMetrics.setSystemioreadwritebytesmean(iOReadWriteBytes.getAvg());
            diskIOMetrics.setSystemioreadwritebytesstd(iOReadWriteBytes.getStdDev());
            diskIOMetrics.setSystemioreadwritebytes55quantile(iOReadWriteBytes.getQuantile55());
            diskIOMetrics.setSystemioreadwritebytes75quantile(iOReadWriteBytes.getQuantile75());
            diskIOMetrics.setSystemioreadwritebytes95quantile(iOReadWriteBytes.getQuantile95());
            diskIOMetrics.setSystemioreadwritebytes99quantile(iOReadWriteBytes.getQuantile99());
            PeriodStatistics iOWRQMS = diskIOInfo.getiOWRQMS();
            diskIOMetrics.setSystemiowrqms(iOWRQMS.getLast());
            diskIOMetrics.setSystemiowrqmsmin(iOWRQMS.getMin());
            diskIOMetrics.setSystemiowrqmsmax(iOWRQMS.getMax());
            diskIOMetrics.setSystemiowrqmsmean(iOWRQMS.getAvg());
            diskIOMetrics.setSystemiowrqmsstd(iOWRQMS.getStdDev());
            diskIOMetrics.setSystemiowrqms55quantile(iOWRQMS.getQuantile55());
            diskIOMetrics.setSystemiowrqms75quantile(iOWRQMS.getQuantile75());
            diskIOMetrics.setSystemiowrqms95quantile(iOWRQMS.getQuantile95());
            diskIOMetrics.setSystemiowrqms99quantile(iOWRQMS.getQuantile99());
            PeriodStatistics readTime = diskIOInfo.getReadTime();
            diskIOMetrics.setSystemdiskreadtime(readTime.getLast());
            diskIOMetrics.setSystemdiskreadtimemin(readTime.getMin());
            diskIOMetrics.setSystemdiskreadtimemax(readTime.getMax());
            diskIOMetrics.setSystemdiskreadtimemean(readTime.getAvg());
            diskIOMetrics.setSystemdiskreadtimestd(readTime.getStdDev());
            diskIOMetrics.setSystemdiskreadtime55quantile(readTime.getQuantile55());
            diskIOMetrics.setSystemdiskreadtime75quantile(readTime.getQuantile75());
            diskIOMetrics.setSystemdiskreadtime95quantile(readTime.getQuantile95());
            diskIOMetrics.setSystemdiskreadtime99quantile(readTime.getQuantile99());
            PeriodStatistics readTimePercent = diskIOInfo.getReadTimePercent();
            diskIOMetrics.setSystemdiskreadtimepercent(readTimePercent.getLast());
            diskIOMetrics.setSystemdiskreadtimepercentmin(readTimePercent.getMin());
            diskIOMetrics.setSystemdiskreadtimepercentmax(readTimePercent.getMax());
            diskIOMetrics.setSystemdiskreadtimepercentmean(readTimePercent.getAvg());
            diskIOMetrics.setSystemdiskreadtimepercentstd(readTimePercent.getStdDev());
            diskIOMetrics.setSystemdiskreadtimepercent55quantile(readTimePercent.getQuantile55());
            diskIOMetrics.setSystemdiskreadtimepercent75quantile(readTimePercent.getQuantile75());
            diskIOMetrics.setSystemdiskreadtimepercent95quantile(readTimePercent.getQuantile95());
            diskIOMetrics.setSystemdiskreadtimepercent99quantile(readTimePercent.getQuantile99());
            PeriodStatistics writeTime = diskIOInfo.getWriteTime();
            diskIOMetrics.setSystemdiskwritetime(writeTime.getLast());
            diskIOMetrics.setSystemdiskwritetimemin(writeTime.getMin());
            diskIOMetrics.setSystemdiskwritetimemax(writeTime.getMax());
            diskIOMetrics.setSystemdiskwritetimemean(writeTime.getAvg());
            diskIOMetrics.setSystemdiskwritetimestd(writeTime.getStdDev());
            diskIOMetrics.setSystemdiskwritetime55quantile(writeTime.getQuantile55());
            diskIOMetrics.setSystemdiskwritetime75quantile(writeTime.getQuantile75());
            diskIOMetrics.setSystemdiskwritetime95quantile(writeTime.getQuantile95());
            diskIOMetrics.setSystemdiskwritetime99quantile(writeTime.getQuantile99());
            PeriodStatistics writeTimePercent = diskIOInfo.getWriteTimePercent();
            diskIOMetrics.setSystemdiskwritetimepercent(writeTimePercent.getLast());
            diskIOMetrics.setSystemdiskwritetimepercentmin(writeTimePercent.getMin());
            diskIOMetrics.setSystemdiskwritetimepercentmax(writeTimePercent.getMax());
            diskIOMetrics.setSystemdiskwritetimepercentmean(writeTimePercent.getAvg());
            diskIOMetrics.setSystemdiskwritetimepercentstd(writeTimePercent.getStdDev());
            diskIOMetrics.setSystemdiskwritetimepercent55quantile(writeTimePercent.getQuantile55());
            diskIOMetrics.setSystemdiskwritetimepercent75quantile(writeTimePercent.getQuantile75());
            diskIOMetrics.setSystemdiskwritetimepercent95quantile(writeTimePercent.getQuantile95());
            diskIOMetrics.setSystemdiskwritetimepercent99quantile(writeTimePercent.getQuantile99());
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
        processMetrics.setProcstartuptime(GlobalProperties.getProcessMetricsService()
            .getProcessStartupTime());
        processMetrics.setProcuptime(GlobalProperties.getProcessMetricsService().getProcUptime());
        processMetrics.setProcpid(GlobalProperties.getProcessMetricsService().getProcessPid());
        /* proccpuutil */
        PeriodStatistics procCpuUtil = GlobalProperties.getProcessMetricsService().getProcCpuUtil();
        processMetrics.setProccpuutil(procCpuUtil.getLast());
        processMetrics.setProccpuutilmin(procCpuUtil.getMin());
        processMetrics.setProccpuutilmax(procCpuUtil.getMax());
        processMetrics.setProccpuutilmean(procCpuUtil.getAvg());
        processMetrics.setProccpuutilstd(procCpuUtil.getStdDev());
        processMetrics.setProccpuutil55quantile(procCpuUtil.getQuantile55());
        processMetrics.setProccpuutil75quantile(procCpuUtil.getQuantile75());
        processMetrics.setProccpuutil95quantile(procCpuUtil.getQuantile95());
        processMetrics.setProccpuutil99quantile(procCpuUtil.getQuantile99());
        /* proccpuutiltotalpercent */
        PeriodStatistics procCpuUtilTotalPercent = GlobalProperties.getProcessMetricsService()
            .getProcCpuUtilTotalPercent();
        processMetrics.setProccpuutiltotalpercent(procCpuUtilTotalPercent.getLast());
        processMetrics.setProccpuutiltotalpercentmin(procCpuUtilTotalPercent.getMin());
        processMetrics.setProccpuutiltotalpercentmax(procCpuUtilTotalPercent.getMax());
        processMetrics.setProccpuutiltotalpercentmean(procCpuUtilTotalPercent.getAvg());
        processMetrics.setProccpuutiltotalpercentstd(procCpuUtilTotalPercent.getStdDev());
        processMetrics
            .setProccpuutiltotalpercent55quantile(procCpuUtilTotalPercent.getQuantile55());
        processMetrics
            .setProccpuutiltotalpercent75quantile(procCpuUtilTotalPercent.getQuantile75());
        processMetrics
            .setProccpuutiltotalpercent95quantile(procCpuUtilTotalPercent.getQuantile95());
        processMetrics
            .setProccpuutiltotalpercent99quantile(procCpuUtilTotalPercent.getQuantile99());
        /* proccpusys */
        PeriodStatistics procCpuSys = GlobalProperties.getProcessMetricsService().getProcCpuSys();
        processMetrics.setProccpusys(procCpuSys.getLast());
        processMetrics.setProccpusysmin(procCpuSys.getMin());
        processMetrics.setProccpusysmax(procCpuSys.getMax());
        processMetrics.setProccpusysmean(procCpuSys.getAvg());
        processMetrics.setProccpusysstd(procCpuSys.getStdDev());
        processMetrics.setProccpusys55quantile(procCpuSys.getQuantile55());
        processMetrics.setProccpusys75quantile(procCpuSys.getQuantile75());
        processMetrics.setProccpusys95quantile(procCpuSys.getQuantile95());
        processMetrics.setProccpusys99quantile(procCpuSys.getQuantile99());
        /* proccpuuser */
        PeriodStatistics procCpuUser = GlobalProperties.getProcessMetricsService().getProcCpuUser();
        processMetrics.setProccpuuser(procCpuUser.getLast());
        processMetrics.setProccpuusermin(procCpuUser.getMin());
        processMetrics.setProccpuusermax(procCpuUser.getMax());
        processMetrics.setProccpuusermean(procCpuUser.getAvg());
        processMetrics.setProccpuuserstd(procCpuUser.getStdDev());
        processMetrics.setProccpuuser55quantile(procCpuUser.getQuantile55());
        processMetrics.setProccpuuser75quantile(procCpuUser.getQuantile75());
        processMetrics.setProccpuuser95quantile(procCpuUser.getQuantile95());
        processMetrics.setProccpuuser99quantile(procCpuUser.getQuantile99());
        /* proccpuswitchesps */
        PeriodStatistics procCpuSwitchesPS = GlobalProperties.getProcessMetricsService()
            .getProcCpuSwitchesPS();
        processMetrics.setProccpuswitchesps(procCpuSwitchesPS.getLast());
        processMetrics.setProccpuswitchespsmin(procCpuSwitchesPS.getMin());
        processMetrics.setProccpuswitchespsmax(procCpuSwitchesPS.getMax());
        processMetrics.setProccpuswitchespsmean(procCpuSwitchesPS.getAvg());
        processMetrics.setProccpuswitchespsstd(procCpuSwitchesPS.getStdDev());
        processMetrics.setProccpuswitchesps55quantile(procCpuSwitchesPS.getQuantile55());
        processMetrics.setProccpuswitchesps75quantile(procCpuSwitchesPS.getQuantile75());
        processMetrics.setProccpuswitchesps95quantile(procCpuSwitchesPS.getQuantile95());
        processMetrics.setProccpuswitchesps99quantile(procCpuSwitchesPS.getQuantile99());
        /* proccpuvoluntaryswitchesps */
        PeriodStatistics procCpuVoluntarySwitchesPS = GlobalProperties.getProcessMetricsService()
            .getProcCpuVoluntarySwitchesPS();
        processMetrics.setProccpuvoluntaryswitchesps(procCpuVoluntarySwitchesPS.getLast());
        processMetrics.setProccpuvoluntaryswitchespsmin(procCpuVoluntarySwitchesPS.getMin());
        processMetrics.setProccpuvoluntaryswitchespsmax(procCpuVoluntarySwitchesPS.getMax());
        processMetrics.setProccpuvoluntaryswitchespsmean(procCpuVoluntarySwitchesPS.getAvg());
        processMetrics.setProccpuvoluntaryswitchespsstd(procCpuVoluntarySwitchesPS.getStdDev());
        processMetrics.setProccpuvoluntaryswitchesps55quantile(procCpuVoluntarySwitchesPS
            .getQuantile55());
        processMetrics.setProccpuvoluntaryswitchesps75quantile(procCpuVoluntarySwitchesPS
            .getQuantile75());
        processMetrics.setProccpuvoluntaryswitchesps95quantile(procCpuVoluntarySwitchesPS
            .getQuantile95());
        processMetrics.setProccpuvoluntaryswitchesps99quantile(procCpuVoluntarySwitchesPS
            .getQuantile99());
        /* proccpunonvoluntaryswitchesps */
        PeriodStatistics procCpuNonVoluntarySwitchesPS = GlobalProperties
            .getProcessMetricsService().getProcCpuNonVoluntarySwitchesPS();
        processMetrics.setProccpunonvoluntaryswitchesps(procCpuNonVoluntarySwitchesPS.getLast());
        processMetrics.setProccpunonvoluntaryswitchespsmin(procCpuNonVoluntarySwitchesPS.getMin());
        processMetrics.setProccpunonvoluntaryswitchespsmax(procCpuNonVoluntarySwitchesPS.getMax());
        processMetrics.setProccpunonvoluntaryswitchespsmean(procCpuNonVoluntarySwitchesPS.getAvg());
        processMetrics.setProccpunonvoluntaryswitchespsstd(procCpuNonVoluntarySwitchesPS
            .getStdDev());
        processMetrics.setProccpunonvoluntaryswitchesps55quantile(procCpuNonVoluntarySwitchesPS
            .getQuantile55());
        processMetrics.setProccpunonvoluntaryswitchesps75quantile(procCpuNonVoluntarySwitchesPS
            .getQuantile75());
        processMetrics.setProccpunonvoluntaryswitchesps95quantile(procCpuNonVoluntarySwitchesPS
            .getQuantile95());
        processMetrics.setProccpunonvoluntaryswitchesps99quantile(procCpuNonVoluntarySwitchesPS
            .getQuantile99());
        processMetrics.setProcmemused(GlobalProperties.getProcessMetricsService().getProcMemUsed());
        processMetrics.setProcmemutil(GlobalProperties.getProcessMetricsService().getProcMemUtil());
        processMetrics.setProcmemdata(GlobalProperties.getProcessMetricsService().getProcMemData());
        processMetrics.setProcmemdirty(GlobalProperties.getProcessMetricsService()
            .getProcMemDirty());
        processMetrics.setProcmemlib(GlobalProperties.getProcessMetricsService().getProcMemLib());
        processMetrics.setProcmemrss(GlobalProperties.getProcessMetricsService().getProcMemRss());
        processMetrics.setProcmemshared(GlobalProperties.getProcessMetricsService()
            .getProcMemShared());
        processMetrics.setProcmemswap(GlobalProperties.getProcessMetricsService().getProcMemSwap());
        processMetrics.setProcmemtext(GlobalProperties.getProcessMetricsService().getProcMemText());
        processMetrics.setProcmemvms(GlobalProperties.getProcessMetricsService().getProcMemVms());
        processMetrics.setJvmprocheapmemoryused(GlobalProperties.getProcessMetricsService()
            .getJvmProcHeapMemoryUsed());
        processMetrics.setJvmprocnonheapmemoryused(GlobalProperties.getProcessMetricsService()
            .getJvmProcNonHeapMemoryUsed());
        processMetrics.setJvmprocheapsizexmx(GlobalProperties.getProcessMetricsService()
            .getJvmProcHeapSizeXmx());
        processMetrics.setJvmprocmemusedpeak(GlobalProperties.getProcessMetricsService()
            .getJvmProcMemUsedPeak());
        processMetrics.setJvmprocheapmemusedpercent(GlobalProperties.getProcessMetricsService()
            .getJvmProcHeapMemUsedPercent());
        /* procioreadrate */
        PeriodStatistics procIOReadRate = GlobalProperties.getProcessMetricsService()
            .getProcIOReadRate();
        processMetrics.setProcioreadrate(procIOReadRate.getLast());
        processMetrics.setProcioreadratemin(procIOReadRate.getMin());
        processMetrics.setProcioreadratemax(procIOReadRate.getMax());
        processMetrics.setProcioreadratemean(procIOReadRate.getAvg());
        processMetrics.setProcioreadratestd(procIOReadRate.getStdDev());
        processMetrics.setProcioreadrate55quantile(procIOReadRate.getQuantile55());
        processMetrics.setProcioreadrate75quantile(procIOReadRate.getQuantile75());
        processMetrics.setProcioreadrate95quantile(procIOReadRate.getQuantile95());
        processMetrics.setProcioreadrate99quantile(procIOReadRate.getQuantile99());
        /* procioreadbytesrate */
        PeriodStatistics procIOReadBytesRate = GlobalProperties.getProcessMetricsService()
            .getProcIOReadBytesRate();
        processMetrics.setProcioreadbytesrate(procIOReadBytesRate.getLast());
        processMetrics.setProcioreadbytesratemin(procIOReadBytesRate.getMin());
        processMetrics.setProcioreadbytesratemax(procIOReadBytesRate.getMax());
        processMetrics.setProcioreadbytesratemean(procIOReadBytesRate.getAvg());
        processMetrics.setProcioreadbytesratestd(procIOReadBytesRate.getStdDev());
        processMetrics.setProcioreadbytesrate55quantile(procIOReadBytesRate.getQuantile55());
        processMetrics.setProcioreadbytesrate75quantile(procIOReadBytesRate.getQuantile75());
        processMetrics.setProcioreadbytesrate95quantile(procIOReadBytesRate.getQuantile95());
        processMetrics.setProcioreadbytesrate99quantile(procIOReadBytesRate.getQuantile99());
        /* prociowriterate */
        PeriodStatistics procIOWriteRate = GlobalProperties.getProcessMetricsService()
            .getProcIOWriteRate();
        processMetrics.setProciowriterate(procIOWriteRate.getLast());
        processMetrics.setProciowriteratemin(procIOWriteRate.getMin());
        processMetrics.setProciowriteratemax(procIOWriteRate.getMax());
        processMetrics.setProciowriteratemean(procIOWriteRate.getAvg());
        processMetrics.setProciowriteratestd(procIOWriteRate.getStdDev());
        processMetrics.setProciowriterate55quantile(procIOWriteRate.getQuantile55());
        processMetrics.setProciowriterate75quantile(procIOWriteRate.getQuantile75());
        processMetrics.setProciowriterate95quantile(procIOWriteRate.getQuantile95());
        processMetrics.setProciowriterate99quantile(procIOWriteRate.getQuantile99());
        /* prociowritebytesrate */
        PeriodStatistics procIOWriteBytesRate = GlobalProperties.getProcessMetricsService()
            .getProcIOWriteBytesRate();
        processMetrics.setProciowritebytesrate(procIOWriteBytesRate.getLast());
        processMetrics.setProciowritebytesratemin(procIOWriteBytesRate.getMin());
        processMetrics.setProciowritebytesratemax(procIOWriteBytesRate.getMax());
        processMetrics.setProciowritebytesratemean(procIOWriteBytesRate.getAvg());
        processMetrics.setProciowritebytesratestd(procIOWriteBytesRate.getStdDev());
        processMetrics.setProciowritebytesrate55quantile(procIOWriteBytesRate.getQuantile55());
        processMetrics.setProciowritebytesrate75quantile(procIOWriteBytesRate.getQuantile75());
        processMetrics.setProciowritebytesrate95quantile(procIOWriteBytesRate.getQuantile95());
        processMetrics.setProciowritebytesrate99quantile(procIOWriteBytesRate.getQuantile99());
        /* procioreadwriterate */
        PeriodStatistics procIOReadWriteRate = GlobalProperties.getProcessMetricsService()
            .getProcIOReadWriteRate();
        processMetrics.setProcioreadwriterate(procIOReadWriteRate.getLast());
        processMetrics.setProcioreadwriteratemin(procIOReadWriteRate.getMin());
        processMetrics.setProcioreadwriteratemax(procIOReadWriteRate.getMax());
        processMetrics.setProcioreadwriteratemean(procIOReadWriteRate.getAvg());
        processMetrics.setProcioreadwriteratestd(procIOReadWriteRate.getStdDev());
        processMetrics.setProcioreadwriterate55quantile(procIOReadWriteRate.getQuantile55());
        processMetrics.setProcioreadwriterate75quantile(procIOReadWriteRate.getQuantile75());
        processMetrics.setProcioreadwriterate95quantile(procIOReadWriteRate.getQuantile95());
        processMetrics.setProcioreadwriterate99quantile(procIOReadWriteRate.getQuantile99());
        /* procioreadwritebytesrate */
        PeriodStatistics procIOReadWriteBytesRate = GlobalProperties.getProcessMetricsService()
            .getProcIOReadWriteBytesRate();
        processMetrics.setProcioreadwritebytesrate(procIOReadWriteBytesRate.getLast());
        processMetrics.setProcioreadwritebytesratemin(procIOReadWriteBytesRate.getMin());
        processMetrics.setProcioreadwritebytesratemax(procIOReadWriteBytesRate.getMax());
        processMetrics.setProcioreadwritebytesratemean(procIOReadWriteBytesRate.getAvg());
        processMetrics.setProcioreadwritebytesratestd(procIOReadWriteBytesRate.getStdDev());
        processMetrics.setProcioreadwritebytesrate55quantile(procIOReadWriteBytesRate
            .getQuantile55());
        processMetrics.setProcioreadwritebytesrate75quantile(procIOReadWriteBytesRate
            .getQuantile75());
        processMetrics.setProcioreadwritebytesrate95quantile(procIOReadWriteBytesRate
            .getQuantile95());
        processMetrics.setProcioreadwritebytesrate99quantile(procIOReadWriteBytesRate
            .getQuantile99());
        /* procioawaittimepercent */
        PeriodStatistics procIOAwaitTimePercent = GlobalProperties.getProcessMetricsService()
            .getProcIOAwaitTimePercent();
        processMetrics.setProcioawaittimepercent(procIOAwaitTimePercent.getLast());
        processMetrics.setProcioawaittimepercentmin(procIOAwaitTimePercent.getMin());
        processMetrics.setProcioawaittimepercentmax(procIOAwaitTimePercent.getMax());
        processMetrics.setProcioawaittimepercentmean(procIOAwaitTimePercent.getAvg());
        processMetrics.setProcioawaittimepercentstd(procIOAwaitTimePercent.getStdDev());
        processMetrics.setProcioawaittimepercent55quantile(procIOAwaitTimePercent.getQuantile55());
        processMetrics.setProcioawaittimepercent75quantile(procIOAwaitTimePercent.getQuantile75());
        processMetrics.setProcioawaittimepercent95quantile(procIOAwaitTimePercent.getQuantile95());
        processMetrics.setProcioawaittimepercent99quantile(procIOAwaitTimePercent.getQuantile99());
        processMetrics.setJvmprocyounggccount(GlobalProperties.getProcessMetricsService()
            .getJvmProcYoungGcCount());
        processMetrics.setJvmprocfullgccount(GlobalProperties.getProcessMetricsService()
            .getJvmProcFullGcCount());
        processMetrics.setJvmprocyounggctime(GlobalProperties.getProcessMetricsService()
            .getJvmProcYoungGcTime());
        processMetrics.setJvmprocfullgctime(GlobalProperties.getProcessMetricsService()
            .getJvmProcFullGcTime());
        processMetrics.setJvmprocthreadnum(GlobalProperties.getProcessMetricsService()
            .getJvmProcThreadNum());
        processMetrics.setJvmprocthreadnumpeak(GlobalProperties.getProcessMetricsService()
            .getJvmProcThreadNumPeak());
        processMetrics.setProcopenfdcount(GlobalProperties.getProcessMetricsService()
            .getProcOpenFdCount());
        processMetrics.setProcportlisten(JSON.toJSONString(GlobalProperties
            .getProcessMetricsService().getProcPortListen()));
        /* procnetworkreceivebytesps */
        PeriodStatistics procNetworkReceiveBytesPs = GlobalProperties.getProcessMetricsService()
            .getProcNetworkReceiveBytesPs();
        processMetrics.setProcnetworkreceivebytesps(procNetworkReceiveBytesPs.getLast());
        processMetrics.setProcnetworkreceivebytespsmin(procNetworkReceiveBytesPs.getMin());
        processMetrics.setProcnetworkreceivebytespsmax(procNetworkReceiveBytesPs.getMax());
        processMetrics.setProcnetworkreceivebytespsmean(procNetworkReceiveBytesPs.getAvg());
        processMetrics.setProcnetworkreceivebytespsstd(procNetworkReceiveBytesPs.getStdDev());
        processMetrics.setProcnetworkreceivebytesps55quantile(procNetworkReceiveBytesPs
            .getQuantile55());
        processMetrics.setProcnetworkreceivebytesps75quantile(procNetworkReceiveBytesPs
            .getQuantile75());
        processMetrics.setProcnetworkreceivebytesps95quantile(procNetworkReceiveBytesPs
            .getQuantile95());
        processMetrics.setProcnetworkreceivebytesps99quantile(procNetworkReceiveBytesPs
            .getQuantile99());
        /* procnetworksendbytesps */
        PeriodStatistics procNetworkSendBytesPs = GlobalProperties.getProcessMetricsService()
            .getProcNetworkSendBytesPs();
        processMetrics.setProcnetworksendbytesps(procNetworkSendBytesPs.getLast());
        processMetrics.setProcnetworksendbytespsmin(procNetworkSendBytesPs.getMin());
        processMetrics.setProcnetworksendbytespsmax(procNetworkSendBytesPs.getMax());
        processMetrics.setProcnetworksendbytespsmean(procNetworkSendBytesPs.getAvg());
        processMetrics.setProcnetworksendbytespsstd(procNetworkSendBytesPs.getStdDev());
        processMetrics.setProcnetworksendbytesps55quantile(procNetworkSendBytesPs.getQuantile55());
        processMetrics.setProcnetworksendbytesps75quantile(procNetworkSendBytesPs.getQuantile75());
        processMetrics.setProcnetworksendbytesps95quantile(procNetworkSendBytesPs.getQuantile95());
        processMetrics.setProcnetworksendbytesps99quantile(procNetworkSendBytesPs.getQuantile99());
        /* procnetworkconnrate */
        PeriodStatistics procNetworkConnRate = GlobalProperties.getProcessMetricsService()
            .getProcNetworkConnRate();
        processMetrics.setProcnetworkconnrate(procNetworkConnRate.getLast());
        processMetrics.setProcnetworkconnratemin(procNetworkConnRate.getMin());
        processMetrics.setProcnetworkconnratemax(procNetworkConnRate.getMax());
        processMetrics.setProcnetworkconnratemean(procNetworkConnRate.getAvg());
        processMetrics.setProcnetworkconnratestd(procNetworkConnRate.getStdDev());
        processMetrics.setProcnetworkconnrate55quantile(procNetworkConnRate.getQuantile55());
        processMetrics.setProcnetworkconnrate75quantile(procNetworkConnRate.getQuantile75());
        processMetrics.setProcnetworkconnrate95quantile(procNetworkConnRate.getQuantile95());
        processMetrics.setProcnetworkconnrate99quantile(procNetworkConnRate.getQuantile99());
        processMetrics.setProcnetworktcpconnectionnum(GlobalProperties.getProcessMetricsService()
            .getProcNetworkTcpConnectionNum());
        processMetrics.setProcnetworktcplisteningnum(GlobalProperties.getProcessMetricsService()
            .getProcNetworkTcpListeningNum());
        processMetrics.setProcnetworktcptimewaitnum(GlobalProperties.getProcessMetricsService()
            .getProcNetworkTcpTimeWaitNum());
        processMetrics.setProcnetworktcpclosewaitnum(GlobalProperties.getProcessMetricsService()
            .getProcNetworkTcpCloseWaitNum());
        processMetrics.setProcnetworktcpestablishednum(GlobalProperties.getProcessMetricsService()
            .getProcNetworkTcpEstablishedNum());
        processMetrics.setProcnetworktcpsynsentnum(GlobalProperties.getProcessMetricsService()
            .getProcNetworkTcpSynSentNum());
        processMetrics.setProcnetworktcpsynrecvnum(GlobalProperties.getProcessMetricsService()
            .getProcNetworkTcpSynRecvNum());
        processMetrics.setProcnetworktcpfinwait1num(GlobalProperties.getProcessMetricsService()
            .getProcNetworkTcpFinWait1Num());
        processMetrics.setProcnetworktcpfinwait2num(GlobalProperties.getProcessMetricsService()
            .getProcNetworkTcpFinWait2Num());
        processMetrics.setProcnetworktcpclosednum(GlobalProperties.getProcessMetricsService()
            .getProcNetworkTcpClosedNum());
        processMetrics.setProcnetworktcpclosingnum(GlobalProperties.getProcessMetricsService()
            .getProcNetworkTcpClosingNum());
        processMetrics.setProcnetworktcplastacknum(GlobalProperties.getProcessMetricsService()
            .getProcNetworkTcpLastAckNum());
        processMetrics.setProcnetworktcpnonenum(GlobalProperties.getProcessMetricsService()
            .getProcNetworkTcpNoneNum());
        processMetrics.setHeartbeattime(heartbeatTime);
        processMetrics.setHeartbeattimeminute(heartbeatTimeMinute);
        processMetrics.setHeartbeattimehour(heartbeatTimeHour);
        processMetrics.setHeartbeatTimeDay(heartbeatTimeDay);
        processMetrics.setJvmProcS0C(GlobalProperties.getProcessMetricsService().getJvmProcS0C());
        processMetrics.setJvmProcS1C(GlobalProperties.getProcessMetricsService().getJvmProcS1C());
        processMetrics.setJvmProcS0U(GlobalProperties.getProcessMetricsService().getJvmProcS0U());
        processMetrics.setJvmProcS1U(GlobalProperties.getProcessMetricsService().getJvmProcS1U());
        processMetrics.setJvmProcEC(GlobalProperties.getProcessMetricsService().getJvmProcEC());
        processMetrics.setJvmProcEU(GlobalProperties.getProcessMetricsService().getJvmProcEU());
        processMetrics.setJvmProcOC(GlobalProperties.getProcessMetricsService().getJvmProcOC());
        processMetrics.setJvmProcOU(GlobalProperties.getProcessMetricsService().getJvmProcOU());
        processMetrics.setJvmProcMC(GlobalProperties.getProcessMetricsService().getJvmProcMC());
        processMetrics.setJvmProcMU(GlobalProperties.getProcessMetricsService().getJvmProcMU());
        processMetrics.setJvmProcCCSC(GlobalProperties.getProcessMetricsService().getJvmProcCCSC());
        processMetrics.setJvmProcCCSU(GlobalProperties.getProcessMetricsService().getJvmProcCCSU());
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
        systemMetrics.setOstype(GlobalProperties.getSystemMetricsService().getOsType());
        systemMetrics.setOsversion(GlobalProperties.getSystemMetricsService().getOsVersion());
        systemMetrics.setOskernelversion(GlobalProperties.getSystemMetricsService()
            .getOsKernelVersion());
        systemMetrics.setHostname(hostName);
        systemMetrics.setIps(GlobalProperties.getSystemMetricsService().getIps());
        systemMetrics.setSystemntpoffset(GlobalProperties.getSystemMetricsService()
            .getSystemNtpOffset());
        systemMetrics.setSystemstartuptime(GlobalProperties.getSystemMetricsService()
            .getSystemStartupTime());
        systemMetrics.setSystemuptime(GlobalProperties.getSystemMetricsService().getSystemUptime());
        systemMetrics.setProcessesblocked(GlobalProperties.getSystemMetricsService()
            .getProcessesBlocked());
        systemMetrics.setProcessessleeping(GlobalProperties.getSystemMetricsService()
            .getProcessesSleeping());
        systemMetrics.setProcesseszombies(GlobalProperties.getSystemMetricsService()
            .getProcessesZombies());
        systemMetrics.setProcessesstopped(GlobalProperties.getSystemMetricsService()
            .getProcessesStopped());
        systemMetrics.setProcessesrunning(GlobalProperties.getSystemMetricsService()
            .getProcessesRunning());
        systemMetrics.setProcessesidle(GlobalProperties.getSystemMetricsService()
            .getProcessesIdle());
        systemMetrics.setProcesseswait(GlobalProperties.getSystemMetricsService()
            .getProcessesWait());
        systemMetrics.setProcessesdead(GlobalProperties.getSystemMetricsService()
            .getProcessesDead());
        systemMetrics.setProcessespaging(GlobalProperties.getSystemMetricsService()
            .getProcessesPaging());
        systemMetrics.setProcessesunknown(GlobalProperties.getSystemMetricsService()
            .getProcessesUnknown());
        systemMetrics.setProcessestotal(GlobalProperties.getSystemMetricsService()
            .getProcessesTotal());
        systemMetrics.setProcessestotalthreads(GlobalProperties.getSystemMetricsService()
            .getProcessesTotalThreads());
        systemMetrics.setCpucores(GlobalProperties.getSystemMetricsService().getSystemCpuCores());
        /* Systemcpuutil */
        PeriodStatistics systemCpuUtil = GlobalProperties.getSystemMetricsService()
            .getSystemCpuUtil();
        systemMetrics.setSystemcpuutil(systemCpuUtil.getLast());
        systemMetrics.setSystemcpuutil55quantile(systemCpuUtil.getQuantile55());
        systemMetrics.setSystemcpuutil75quantile(systemCpuUtil.getQuantile75());
        systemMetrics.setSystemcpuutil95quantile(systemCpuUtil.getQuantile95());
        systemMetrics.setSystemcpuutilmax(systemCpuUtil.getMax());
        systemMetrics.setSystemcpuutil99quantile(systemCpuUtil.getQuantile99());
        systemMetrics.setSystemcpuutilmean(systemCpuUtil.getAvg());
        systemMetrics.setSystemcpuutilmin(systemCpuUtil.getMin());
        systemMetrics.setSystemcpuutilstd(systemCpuUtil.getStdDev());
        /* systemcpuutiltotalpercent */
        PeriodStatistics systemCpuUtilTotalPercent = GlobalProperties.getSystemMetricsService()
            .getSystemCpuUtilTotalPercent();
        systemMetrics.setSystemcpuutiltotalpercent(systemCpuUtilTotalPercent.getLast());
        systemMetrics.setSystemcpuutiltotalpercentmin(systemCpuUtilTotalPercent.getMin());
        systemMetrics.setSystemcpuutiltotalpercentmax(systemCpuUtilTotalPercent.getMax());
        systemMetrics.setSystemcpuutiltotalpercentmean(systemCpuUtilTotalPercent.getAvg());
        systemMetrics.setSystemcpuutiltotalpercentstd(systemCpuUtilTotalPercent.getStdDev());
        systemMetrics.setSystemcpuutiltotalpercent55quantile(systemCpuUtilTotalPercent
            .getQuantile55());
        systemMetrics.setSystemcpuutiltotalpercent75quantile(systemCpuUtilTotalPercent
            .getQuantile75());
        systemMetrics.setSystemcpuutiltotalpercent95quantile(systemCpuUtilTotalPercent
            .getQuantile95());
        systemMetrics.setSystemcpuutiltotalpercent99quantile(systemCpuUtilTotalPercent
            .getQuantile99());
        /* systemcpusystem */
        PeriodStatistics systemMetricsService = GlobalProperties.getSystemMetricsService()
            .getSystemCpuSystem();
        systemMetrics.setSystemcpusystem(systemMetricsService.getLast());
        systemMetrics.setSystemcpusystemmin(systemMetricsService.getMin());
        systemMetrics.setSystemcpusystemmax(systemMetricsService.getMax());
        systemMetrics.setSystemcpusystemmean(systemMetricsService.getAvg());
        systemMetrics.setSystemcpusystemstd(systemMetricsService.getStdDev());
        systemMetrics.setSystemcpusystem55quantile(systemMetricsService.getQuantile55());
        systemMetrics.setSystemcpusystem75quantile(systemMetricsService.getQuantile75());
        systemMetrics.setSystemcpusystem95quantile(systemMetricsService.getQuantile95());
        systemMetrics.setSystemcpusystem99quantile(systemMetricsService.getQuantile99());
        /* systemcpuuser */
        PeriodStatistics systemCpuUser = GlobalProperties.getSystemMetricsService()
            .getSystemCpuUser();
        systemMetrics.setSystemcpuuser(systemCpuUser.getLast());
        systemMetrics.setSystemcpuusermin(systemCpuUser.getMin());
        systemMetrics.setSystemcpuusermax(systemCpuUser.getMax());
        systemMetrics.setSystemcpuusermean(systemCpuUser.getAvg());
        systemMetrics.setSystemcpuuserstd(systemCpuUser.getStdDev());
        systemMetrics.setSystemcpuuser55quantile(systemCpuUser.getQuantile55());
        systemMetrics.setSystemcpuuser75quantile(systemCpuUser.getQuantile75());
        systemMetrics.setSystemcpuuser95quantile(systemCpuUser.getQuantile95());
        systemMetrics.setSystemcpuuser99quantile(systemCpuUser.getQuantile99());
        /* systemcpuidle */
        PeriodStatistics systemCpuIdle = GlobalProperties.getSystemMetricsService()
            .getSystemCpuIdle();
        systemMetrics.setSystemcpuidle(systemCpuIdle.getLast());
        systemMetrics.setSystemcpuidlemin(systemCpuIdle.getMin());
        systemMetrics.setSystemcpuidlemax(systemCpuIdle.getMax());
        systemMetrics.setSystemcpuidlemean(systemCpuIdle.getAvg());
        systemMetrics.setSystemcpuidlestd(systemCpuIdle.getStdDev());
        systemMetrics.setSystemcpuidle55quantile(systemCpuIdle.getQuantile55());
        systemMetrics.setSystemcpuidle75quantile(systemCpuIdle.getQuantile75());
        systemMetrics.setSystemcpuidle95quantile(systemCpuIdle.getQuantile95());
        systemMetrics.setSystemcpuidle99quantile(systemCpuIdle.getQuantile99());
        /* systemcpuswitches */
        PeriodStatistics systemCpuSwitches = GlobalProperties.getSystemMetricsService()
            .getSystemCpuSwitches();
        systemMetrics.setSystemcpuswitches(systemCpuSwitches.getLast());
        systemMetrics.setSystemcpuswitchesmin(systemCpuSwitches.getMin());
        systemMetrics.setSystemcpuswitchesmax(systemCpuSwitches.getMax());
        systemMetrics.setSystemcpuswitchesmean(systemCpuSwitches.getAvg());
        systemMetrics.setSystemcpuswitchesstd(systemCpuSwitches.getStdDev());
        systemMetrics.setSystemcpuswitches55quantile(systemCpuSwitches.getQuantile55());
        systemMetrics.setSystemcpuswitches75quantile(systemCpuSwitches.getQuantile75());
        systemMetrics.setSystemcpuswitches95quantile(systemCpuSwitches.getQuantile95());
        systemMetrics.setSystemcpuswitches99quantile(systemCpuSwitches.getQuantile99());
        /* systemcpuusageirq */
        PeriodStatistics systemCpuUsageIrq = GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageIrq();
        systemMetrics.setSystemcpuusageirq(systemCpuUsageIrq.getLast());
        systemMetrics.setSystemcpuusageirqmin(systemCpuUsageIrq.getMin());
        systemMetrics.setSystemcpuusageirqmax(systemCpuUsageIrq.getMax());
        systemMetrics.setSystemcpuusageirqmean(systemCpuUsageIrq.getAvg());
        systemMetrics.setSystemcpuusageirqstd(systemCpuUsageIrq.getStdDev());
        systemMetrics.setSystemcpuusageirq55quantile(systemCpuUsageIrq.getQuantile55());
        systemMetrics.setSystemcpuusageirq75quantile(systemCpuUsageIrq.getQuantile75());
        systemMetrics.setSystemcpuusageirq95quantile(systemCpuUsageIrq.getQuantile95());
        systemMetrics.setSystemcpuusageirq99quantile(systemCpuUsageIrq.getQuantile99());
        /* systemcpuusagesoftirq */
        PeriodStatistics systemCpuUsageSoftIrq = GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageSoftIrq();
        systemMetrics.setSystemcpuusagesoftirq(systemCpuUsageSoftIrq.getLast());
        systemMetrics.setSystemcpuusagesoftirqmin(systemCpuUsageSoftIrq.getMin());
        systemMetrics.setSystemcpuusagesoftirqmax(systemCpuUsageSoftIrq.getMax());
        systemMetrics.setSystemcpuusagesoftirqmean(systemCpuUsageSoftIrq.getAvg());
        systemMetrics.setSystemcpuusagesoftirqstd(systemCpuUsageSoftIrq.getStdDev());
        systemMetrics.setSystemcpuusagesoftirq55quantile(systemCpuUsageSoftIrq.getQuantile55());
        systemMetrics.setSystemcpuusagesoftirq75quantile(systemCpuUsageSoftIrq.getQuantile75());
        systemMetrics.setSystemcpuusagesoftirq95quantile(systemCpuUsageSoftIrq.getQuantile95());
        systemMetrics.setSystemcpuusagesoftirq99quantile(systemCpuUsageSoftIrq.getQuantile99());
        /* systemload1 */
        PeriodStatistics systemLoad1 = GlobalProperties.getSystemMetricsService().getSystemLoad1();
        systemMetrics.setSystemload1(systemLoad1.getLast());
        systemMetrics.setSystemload1min(systemLoad1.getMin());
        systemMetrics.setSystemload1max(systemLoad1.getMax());
        systemMetrics.setSystemload1mean(systemLoad1.getAvg());
        systemMetrics.setSystemload1std(systemLoad1.getStdDev());
        systemMetrics.setSystemload155quantile(systemLoad1.getQuantile55());
        systemMetrics.setSystemload175quantile(systemLoad1.getQuantile75());
        systemMetrics.setSystemload195quantile(systemLoad1.getQuantile95());
        systemMetrics.setSystemload199quantile(systemLoad1.getQuantile99());
        /* systemload5 */
        PeriodStatistics systemLoad5 = GlobalProperties.getSystemMetricsService().getSystemLoad5();
        systemMetrics.setSystemload5(systemLoad5.getLast());
        systemMetrics.setSystemload5min(systemLoad5.getMin());
        systemMetrics.setSystemload5max(systemLoad5.getMax());
        systemMetrics.setSystemload5mean(systemLoad5.getAvg());
        systemMetrics.setSystemload5std(systemLoad5.getStdDev());
        systemMetrics.setSystemload555quantile(systemLoad5.getQuantile55());
        systemMetrics.setSystemload575quantile(systemLoad5.getQuantile75());
        systemMetrics.setSystemload595quantile(systemLoad5.getQuantile95());
        systemMetrics.setSystemload599quantile(systemLoad5.getQuantile99());
        /* systemload15 */
        PeriodStatistics systemLoad15 = GlobalProperties.getSystemMetricsService()
            .getSystemLoad15();
        systemMetrics.setSystemload15(systemLoad15.getLast());
        systemMetrics.setSystemload15min(systemLoad15.getMin());
        systemMetrics.setSystemload15max(systemLoad15.getMax());
        systemMetrics.setSystemload15mean(systemLoad15.getAvg());
        systemMetrics.setSystemload15std(systemLoad15.getStdDev());
        systemMetrics.setSystemload1555quantile(systemLoad15.getQuantile55());
        systemMetrics.setSystemload1575quantile(systemLoad15.getQuantile75());
        systemMetrics.setSystemload1595quantile(systemLoad15.getQuantile95());
        systemMetrics.setSystemload1599quantile(systemLoad15.getQuantile99());
        /* systemcpuiowait */
        PeriodStatistics systemCpuIOWait = GlobalProperties.getSystemMetricsService()
            .getSystemCpuIOWait();
        systemMetrics.setSystemcpuiowait(systemCpuIOWait.getLast());
        systemMetrics.setSystemcpuiowaitmin(systemCpuIOWait.getMin());
        systemMetrics.setSystemcpuiowaitmax(systemCpuIOWait.getMax());
        systemMetrics.setSystemcpuiowaitmean(systemCpuIOWait.getAvg());
        systemMetrics.setSystemcpuiowaitstd(systemCpuIOWait.getStdDev());
        systemMetrics.setSystemcpuiowait55quantile(systemCpuIOWait.getQuantile55());
        systemMetrics.setSystemcpuiowait75quantile(systemCpuIOWait.getQuantile75());
        systemMetrics.setSystemcpuiowait95quantile(systemCpuIOWait.getQuantile95());
        systemMetrics.setSystemcpuiowait99quantile(systemCpuIOWait.getQuantile99());
        /* systemcpuguest */
        PeriodStatistics systemCpuGuest = GlobalProperties.getSystemMetricsService()
            .getSystemCpuGuest();
        systemMetrics.setSystemcpuguest(systemCpuGuest.getLast());
        systemMetrics.setSystemcpuguestmin(systemCpuGuest.getMin());
        systemMetrics.setSystemcpuguestmax(systemCpuGuest.getMax());
        systemMetrics.setSystemcpuguestmean(systemCpuGuest.getAvg());
        systemMetrics.setSystemcpugueststd(systemCpuGuest.getStdDev());
        systemMetrics.setSystemcpuguest55quantile(systemCpuGuest.getQuantile55());
        systemMetrics.setSystemcpuguest75quantile(systemCpuGuest.getQuantile75());
        systemMetrics.setSystemcpuguest95quantile(systemCpuGuest.getQuantile95());
        systemMetrics.setSystemcpuguest99quantile(systemCpuGuest.getQuantile99());
        /* systemcpusteal */
        PeriodStatistics systemCpuSteal = GlobalProperties.getSystemMetricsService()
            .getSystemCpuSteal();
        systemMetrics.setSystemcpusteal(systemCpuSteal.getLast());
        systemMetrics.setSystemcpustealmin(systemCpuSteal.getMin());
        systemMetrics.setSystemcpustealmax(systemCpuSteal.getMax());
        systemMetrics.setSystemcpustealmean(systemCpuSteal.getAvg());
        systemMetrics.setSystemcpustealstd(systemCpuSteal.getStdDev());
        systemMetrics.setSystemcpusteal55quantile(systemCpuSteal.getQuantile55());
        systemMetrics.setSystemcpusteal75quantile(systemCpuSteal.getQuantile75());
        systemMetrics.setSystemcpusteal95quantile(systemCpuSteal.getQuantile95());
        systemMetrics.setSystemcpusteal99quantile(systemCpuSteal.getQuantile99());
        systemMetrics.setSystemmemcommitlimit(GlobalProperties.getSystemMetricsService()
            .getSystemMemCommitLimit());
        systemMetrics.setSystemmemcommittedas(GlobalProperties.getSystemMetricsService()
            .getSystemMemCommittedAs());
        systemMetrics.setSystemmemcommitted(GlobalProperties.getSystemMetricsService()
            .getSystemMemCommitted());
        systemMetrics.setSystemmemnonpaged(GlobalProperties.getSystemMetricsService()
            .getSystemMemNonPaged());
        systemMetrics.setSystemmempaged(GlobalProperties.getSystemMetricsService()
            .getSystemMemPaged());
        systemMetrics.setSystemmemshared(GlobalProperties.getSystemMetricsService()
            .getSystemMemShared());
        systemMetrics.setSystemmemslab(GlobalProperties.getSystemMetricsService()
            .getSystemMemSlab());
        systemMetrics.setSystemmemtotal(GlobalProperties.getSystemMetricsService()
            .getSystemMemTotal());
        systemMetrics.setSystemmemfree(GlobalProperties.getSystemMetricsService()
            .getSystemMemFree());
        systemMetrics.setSystemmemused(GlobalProperties.getSystemMetricsService()
            .getSystemMemUsed());
        systemMetrics.setSystemmembuffered(GlobalProperties.getSystemMetricsService()
            .getSystemMemBuffered());
        systemMetrics.setSystemmemcached(GlobalProperties.getSystemMetricsService()
            .getSystemMemCached());
        systemMetrics.setSystemmemfreepercent(GlobalProperties.getSystemMetricsService()
            .getSystemMemFreePercent());
        systemMetrics.setSystemmemusedpercent(GlobalProperties.getSystemMetricsService()
            .getSystemMemUsedPercent());
        systemMetrics.setSystemswapcached(GlobalProperties.getSystemMetricsService()
            .getSystemSwapCached());
        systemMetrics.setSystemswapfree(GlobalProperties.getSystemMetricsService()
            .getSystemSwapFree());
        systemMetrics.setSystemswapfreepercent(GlobalProperties.getSystemMetricsService()
            .getSystemSwapFreePercent());
        systemMetrics.setSystemswaptotal(GlobalProperties.getSystemMetricsService()
            .getSystemSwapTotal());
        systemMetrics.setSystemswapused(GlobalProperties.getSystemMetricsService()
            .getSystemSwapUsed());
        systemMetrics.setSystemswapusedpercent(GlobalProperties.getSystemMetricsService()
            .getSystemSwapUsedPercent());
        systemMetrics.setSystemdisks(GlobalProperties.getSystemMetricsService().getSystemDisks());
        systemMetrics.setSystemfilesmax(GlobalProperties.getSystemMetricsService()
            .getSystemFilesMax());
        systemMetrics.setSystemfilesallocated(GlobalProperties.getSystemMetricsService()
            .getSystemFilesAllocated());
        systemMetrics.setSystemfilesleft(GlobalProperties.getSystemMetricsService()
            .getSystemFilesLeft());
        systemMetrics.setSystemfilesusedpercent(GlobalProperties.getSystemMetricsService()
            .getSystemFilesUsedPercent());
        systemMetrics.setSystemfilesused(GlobalProperties.getSystemMetricsService()
            .getSystemFilesUsed());
        systemMetrics.setSystemfilesnotused(GlobalProperties.getSystemMetricsService()
            .getSystemFilesNotUsed());
        systemMetrics.setSystemnetcards(GlobalProperties.getSystemMetricsService()
            .getSystemNetCards());
        /* systemnetworkreceivebytesps */
        PeriodStatistics systemNetworkReceiveBytesPs = GlobalProperties.getSystemMetricsService()
            .getSystemNetworkReceiveBytesPs();
        systemMetrics.setSystemnetworkreceivebytesps(systemNetworkReceiveBytesPs.getLast());
        systemMetrics.setSystemnetworkreceivebytespsmin(systemNetworkReceiveBytesPs.getMin());
        systemMetrics.setSystemnetworkreceivebytespsmax(systemNetworkReceiveBytesPs.getMax());
        systemMetrics.setSystemnetworkreceivebytespsmean(systemNetworkReceiveBytesPs.getAvg());
        systemMetrics.setSystemnetworkreceivebytespsstd(systemNetworkReceiveBytesPs.getStdDev());
        systemMetrics.setSystemnetworkreceivebytesps55quantile(systemNetworkReceiveBytesPs
            .getQuantile55());
        systemMetrics.setSystemnetworkreceivebytesps75quantile(systemNetworkReceiveBytesPs
            .getQuantile75());
        systemMetrics.setSystemnetworkreceivebytesps95quantile(systemNetworkReceiveBytesPs
            .getQuantile95());
        systemMetrics.setSystemnetworkreceivebytesps99quantile(systemNetworkReceiveBytesPs
            .getQuantile99());
        /* systemnetworksendbytesps */
        PeriodStatistics systemNetworkSendBytesPs = GlobalProperties.getSystemMetricsService()
            .getSystemNetworkSendBytesPs();
        systemMetrics.setSystemnetworksendbytesps(systemNetworkSendBytesPs.getLast());
        systemMetrics.setSystemnetworksendbytespsmin(systemNetworkSendBytesPs.getMin());
        systemMetrics.setSystemnetworksendbytespsmax(systemNetworkSendBytesPs.getMax());
        systemMetrics.setSystemnetworksendbytespsmean(systemNetworkSendBytesPs.getAvg());
        systemMetrics.setSystemnetworksendbytespsstd(systemNetworkSendBytesPs.getStdDev());
        systemMetrics.setSystemnetworksendbytesps55quantile(systemNetworkSendBytesPs
            .getQuantile55());
        systemMetrics.setSystemnetworksendbytesps75quantile(systemNetworkSendBytesPs
            .getQuantile75());
        systemMetrics.setSystemnetworksendbytesps95quantile(systemNetworkSendBytesPs
            .getQuantile95());
        systemMetrics.setSystemnetworksendbytesps99quantile(systemNetworkSendBytesPs
            .getQuantile99());
        systemMetrics.setSystemnetworktcpconnectionnum(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpConnectionNum());
        systemMetrics.setSystemnetworktcplisteningnum(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpListeningNum());
        systemMetrics.setSystemnetworktcpestablishednum(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpEstablishedNum());
        systemMetrics.setSystemnetworktcpsynsentnum(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpSynSentNum());
        systemMetrics.setSystemnetworktcpsynrecvnum(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpSynRecvNum());
        systemMetrics.setSystemnetworktcpfinwait1num(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpFinWait1Num());
        systemMetrics.setSystemnetworktcpfinwait2num(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpFinWait2Num());
        systemMetrics.setSystemnetworktcptimewaitnum(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpTimeWaitNum());
        systemMetrics.setSystemnetworktcpclosednum(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpClosedNum());
        systemMetrics.setSystemnetworktcpclosewaitnum(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpCloseWaitNum());
        systemMetrics.setSystemnetworktcpclosingnum(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpClosingNum());
        systemMetrics.setSystemnetworktcplastacknum(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpLastAckNum());
        systemMetrics.setSystemnetworktcpnonenum(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpNoneNum());
        systemMetrics.setSystemnetworktcpactiveopens(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpActiveOpens());
        systemMetrics.setSystemnetworktcppassiveopens(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpPassiveOpens());
        systemMetrics.setSystemnetworktcpattemptfails(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpAttemptFails());
        systemMetrics.setSystemnetworktcpestabresets(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpEstabResets());
        systemMetrics.setSystemnetworktcpretranssegs(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkTcpRetransSegs());
        systemMetrics.setSystemnetworktcpextlistenoverflows(GlobalProperties
            .getSystemMetricsService().getSystemNetworkTcpExtListenOverflows());
        systemMetrics.setSystemnetworkudpindatagrams(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkUdpInDatagrams());
        systemMetrics.setSystemnetworkudpoutdatagrams(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkUdpOutDatagrams());
        systemMetrics.setSystemnetworkudpinerrors(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkUdpInErrors());
        systemMetrics.setSystemnetworkudpnoports(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkUdpNoPorts());
        systemMetrics.setSystemnetworkudpsendbuffererrors(GlobalProperties
            .getSystemMetricsService().getSystemNetworkUdpSendBufferErrors());
        systemMetrics.setHeartbeattime(heartbeatTime);
        systemMetrics.setHeartbeattimeminute(heartbeatTimeMinute);
        systemMetrics.setHeartbeattimehour(heartbeatTimeHour);
        systemMetrics.setHeartbeatTimeDay(heartbeatTimeDay);
        systemMetrics.setSystemNetCardsBandWidth(GlobalProperties.getSystemMetricsService()
            .getSystemNetCardsBandWidth());
        /* systemNetworkSendAndReceiveBytesPs */
        PeriodStatistics systemNetworkSendAndReceiveBytesPs = GlobalProperties
            .getSystemMetricsService().getSystemNetworkSendAndReceiveBytesPs();
        systemMetrics.setSystemNetworkSendAndReceiveBytesPs(systemNetworkSendAndReceiveBytesPs
            .getLast());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPsMin(systemNetworkSendAndReceiveBytesPs
            .getMin());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPsMax(systemNetworkSendAndReceiveBytesPs
            .getMax());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPsMean(systemNetworkSendAndReceiveBytesPs
            .getAvg());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPsStd(systemNetworkSendAndReceiveBytesPs
            .getStdDev());
        systemMetrics
            .setSystemNetworkSendAndReceiveBytesPs55Quantile(systemNetworkSendAndReceiveBytesPs
                .getQuantile55());
        systemMetrics
            .setSystemNetworkSendAndReceiveBytesPs75Quantile(systemNetworkSendAndReceiveBytesPs
                .getQuantile75());
        systemMetrics
            .setSystemNetworkSendAndReceiveBytesPs95Quantile(systemNetworkSendAndReceiveBytesPs
                .getQuantile95());
        systemMetrics
            .setSystemNetworkSendAndReceiveBytesPs99Quantile(systemNetworkSendAndReceiveBytesPs
                .getQuantile99());
        /* systemNetWorkBandWidthUsedPercent */
        PeriodStatistics systemNetWorkBandWidthUsedPercent = GlobalProperties
            .getSystemMetricsService().getSystemNetWorkBandWidthUsedPercent();
        systemMetrics.setSystemNetWorkBandWidthUsedPercent(systemNetWorkBandWidthUsedPercent
            .getLast());
        systemMetrics.setSystemNetWorkBandWidthUsedPercentMin(systemNetWorkBandWidthUsedPercent
            .getMin());
        systemMetrics.setSystemNetWorkBandWidthUsedPercentMax(systemNetWorkBandWidthUsedPercent
            .getMax());
        systemMetrics.setSystemNetWorkBandWidthUsedPercentMean(systemNetWorkBandWidthUsedPercent
            .getAvg());
        systemMetrics.setSystemNetWorkBandWidthUsedPercentStd(systemNetWorkBandWidthUsedPercent
            .getStdDev());
        systemMetrics
            .setSystemNetWorkBandWidthUsedPercent55Quantile(systemNetWorkBandWidthUsedPercent
                .getQuantile55());
        systemMetrics
            .setSystemNetWorkBandWidthUsedPercent75Quantile(systemNetWorkBandWidthUsedPercent
                .getQuantile75());
        systemMetrics
            .setSystemNetWorkBandWidthUsedPercent95Quantile(systemNetWorkBandWidthUsedPercent
                .getQuantile95());
        systemMetrics
            .setSystemNetWorkBandWidthUsedPercent99Quantile(systemNetWorkBandWidthUsedPercent
                .getQuantile99());
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
