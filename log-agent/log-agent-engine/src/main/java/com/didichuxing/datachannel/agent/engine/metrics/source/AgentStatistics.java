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
        processMetrics.setProcstartuptime(GlobalProperties.getProcessMetricsService()
            .getProcessStartupTime());
        processMetrics.setProcuptime(GlobalProperties.getProcessMetricsService().getProcUptime());
        processMetrics.setProcpid(GlobalProperties.getProcessMetricsService().getProcessPid());
        /* proccpuutil */
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
        /* proccpuutiltotalpercent */
        processMetrics.setProccpuutiltotalpercent(GlobalProperties.getProcessMetricsService()
            .getProcCpuUtilTotalPercent().getLast());
        processMetrics.setProccpuutiltotalpercentmin(GlobalProperties.getProcessMetricsService()
            .getProcCpuUtilTotalPercent().getMin());
        processMetrics.setProccpuutiltotalpercentmax(GlobalProperties.getProcessMetricsService()
            .getProcCpuUtilTotalPercent().getMax());
        processMetrics.setProccpuutiltotalpercentmean(GlobalProperties.getProcessMetricsService()
            .getProcCpuUtilTotalPercent().getAvg());
        processMetrics.setProccpuutiltotalpercentstd(GlobalProperties.getProcessMetricsService()
            .getProcCpuUtilTotalPercent().getStdDev());
        processMetrics.setProccpuutiltotalpercent55quantile(GlobalProperties
            .getProcessMetricsService().getProcCpuUtilTotalPercent().getQuantile55());
        processMetrics.setProccpuutiltotalpercent75quantile(GlobalProperties
            .getProcessMetricsService().getProcCpuUtilTotalPercent().getQuantile75());
        processMetrics.setProccpuutiltotalpercent95quantile(GlobalProperties
            .getProcessMetricsService().getProcCpuUtilTotalPercent().getQuantile95());
        processMetrics.setProccpuutiltotalpercent99quantile(GlobalProperties
            .getProcessMetricsService().getProcCpuUtilTotalPercent().getQuantile99());
        /* proccpusys */
        processMetrics.setProccpusys(GlobalProperties.getProcessMetricsService().getProcCpuSys()
            .getLast());
        processMetrics.setProccpusysmin(GlobalProperties.getProcessMetricsService().getProcCpuSys()
            .getMin());
        processMetrics.setProccpusysmax(GlobalProperties.getProcessMetricsService().getProcCpuSys()
            .getMax());
        processMetrics.setProccpusysmean(GlobalProperties.getProcessMetricsService()
            .getProcCpuSys().getAvg());
        processMetrics.setProccpusysstd(GlobalProperties.getProcessMetricsService().getProcCpuSys()
            .getStdDev());
        processMetrics.setProccpusys55quantile(GlobalProperties.getProcessMetricsService()
            .getProcCpuSys().getQuantile55());
        processMetrics.setProccpusys75quantile(GlobalProperties.getProcessMetricsService()
            .getProcCpuSys().getQuantile75());
        processMetrics.setProccpusys95quantile(GlobalProperties.getProcessMetricsService()
            .getProcCpuSys().getQuantile95());
        processMetrics.setProccpusys99quantile(GlobalProperties.getProcessMetricsService()
            .getProcCpuSys().getQuantile99());
        /* proccpuuser */
        processMetrics.setProccpuuser(GlobalProperties.getProcessMetricsService().getProcCpuUser()
            .getLast());
        processMetrics.setProccpuusermin(GlobalProperties.getProcessMetricsService()
            .getProcCpuUser().getMin());
        processMetrics.setProccpuusermax(GlobalProperties.getProcessMetricsService()
            .getProcCpuUser().getMax());
        processMetrics.setProccpuusermean(GlobalProperties.getProcessMetricsService()
            .getProcCpuUser().getAvg());
        processMetrics.setProccpuuserstd(GlobalProperties.getProcessMetricsService()
            .getProcCpuUser().getStdDev());
        processMetrics.setProccpuuser55quantile(GlobalProperties.getProcessMetricsService()
            .getProcCpuUser().getQuantile55());
        processMetrics.setProccpuuser75quantile(GlobalProperties.getProcessMetricsService()
            .getProcCpuUser().getQuantile75());
        processMetrics.setProccpuuser95quantile(GlobalProperties.getProcessMetricsService()
            .getProcCpuUser().getQuantile95());
        processMetrics.setProccpuuser99quantile(GlobalProperties.getProcessMetricsService()
            .getProcCpuUser().getQuantile99());
        /* proccpuswitchesps */
        processMetrics.setProccpuswitchesps(GlobalProperties.getProcessMetricsService()
            .getProcCpuSwitchesPS().getLast());
        processMetrics.setProccpuswitchespsmin(GlobalProperties.getProcessMetricsService()
            .getProcCpuSwitchesPS().getMin());
        processMetrics.setProccpuswitchespsmax(GlobalProperties.getProcessMetricsService()
            .getProcCpuSwitchesPS().getMax());
        processMetrics.setProccpuswitchespsmean(GlobalProperties.getProcessMetricsService()
            .getProcCpuSwitchesPS().getAvg());
        processMetrics.setProccpuswitchespsstd(GlobalProperties.getProcessMetricsService()
            .getProcCpuSwitchesPS().getStdDev());
        processMetrics.setProccpuswitchesps55quantile(GlobalProperties.getProcessMetricsService()
            .getProcCpuSwitchesPS().getQuantile55());
        processMetrics.setProccpuswitchesps75quantile(GlobalProperties.getProcessMetricsService()
            .getProcCpuSwitchesPS().getQuantile75());
        processMetrics.setProccpuswitchesps95quantile(GlobalProperties.getProcessMetricsService()
            .getProcCpuSwitchesPS().getQuantile95());
        processMetrics.setProccpuswitchesps99quantile(GlobalProperties.getProcessMetricsService()
            .getProcCpuSwitchesPS().getQuantile99());
        /* proccpuvoluntaryswitchesps */
        processMetrics.setProccpuvoluntaryswitchesps(GlobalProperties.getProcessMetricsService()
            .getProcCpuVoluntarySwitchesPS().getLast());
        processMetrics.setProccpuvoluntaryswitchespsmin(GlobalProperties.getProcessMetricsService()
            .getProcCpuVoluntarySwitchesPS().getMin());
        processMetrics.setProccpuvoluntaryswitchespsmax(GlobalProperties.getProcessMetricsService()
            .getProcCpuVoluntarySwitchesPS().getMax());
        processMetrics.setProccpuvoluntaryswitchespsmean(GlobalProperties
            .getProcessMetricsService().getProcCpuVoluntarySwitchesPS().getAvg());
        processMetrics.setProccpuvoluntaryswitchespsstd(GlobalProperties.getProcessMetricsService()
            .getProcCpuVoluntarySwitchesPS().getStdDev());
        processMetrics.setProccpuvoluntaryswitchesps55quantile(GlobalProperties
            .getProcessMetricsService().getProcCpuVoluntarySwitchesPS().getQuantile55());
        processMetrics.setProccpuvoluntaryswitchesps75quantile(GlobalProperties
            .getProcessMetricsService().getProcCpuVoluntarySwitchesPS().getQuantile75());
        processMetrics.setProccpuvoluntaryswitchesps95quantile(GlobalProperties
            .getProcessMetricsService().getProcCpuVoluntarySwitchesPS().getQuantile95());
        processMetrics.setProccpuvoluntaryswitchesps99quantile(GlobalProperties
            .getProcessMetricsService().getProcCpuVoluntarySwitchesPS().getQuantile99());
        /* proccpunonvoluntaryswitchesps */
        processMetrics.setProccpunonvoluntaryswitchesps(GlobalProperties.getProcessMetricsService()
            .getProcCpuNonVoluntarySwitchesPS().getLast());
        processMetrics.setProccpunonvoluntaryswitchespsmin(GlobalProperties
            .getProcessMetricsService().getProcCpuNonVoluntarySwitchesPS().getMin());
        processMetrics.setProccpunonvoluntaryswitchespsmax(GlobalProperties
            .getProcessMetricsService().getProcCpuNonVoluntarySwitchesPS().getMax());
        processMetrics.setProccpunonvoluntaryswitchespsmean(GlobalProperties
            .getProcessMetricsService().getProcCpuNonVoluntarySwitchesPS().getAvg());
        processMetrics.setProccpunonvoluntaryswitchespsstd(GlobalProperties
            .getProcessMetricsService().getProcCpuNonVoluntarySwitchesPS().getStdDev());
        processMetrics.setProccpunonvoluntaryswitchesps55quantile(GlobalProperties
            .getProcessMetricsService().getProcCpuNonVoluntarySwitchesPS().getQuantile55());
        processMetrics.setProccpunonvoluntaryswitchesps75quantile(GlobalProperties
            .getProcessMetricsService().getProcCpuNonVoluntarySwitchesPS().getQuantile75());
        processMetrics.setProccpunonvoluntaryswitchesps95quantile(GlobalProperties
            .getProcessMetricsService().getProcCpuNonVoluntarySwitchesPS().getQuantile95());
        processMetrics.setProccpunonvoluntaryswitchesps99quantile(GlobalProperties
            .getProcessMetricsService().getProcCpuNonVoluntarySwitchesPS().getQuantile99());
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
        processMetrics.setProcioreadrate(GlobalProperties.getProcessMetricsService()
            .getProcIOReadRate().getLast());
        processMetrics.setProcioreadratemin(GlobalProperties.getProcessMetricsService()
            .getProcIOReadRate().getMin());
        processMetrics.setProcioreadratemax(GlobalProperties.getProcessMetricsService()
            .getProcIOReadRate().getMax());
        processMetrics.setProcioreadratemean(GlobalProperties.getProcessMetricsService()
            .getProcIOReadRate().getAvg());
        processMetrics.setProcioreadratestd(GlobalProperties.getProcessMetricsService()
            .getProcIOReadRate().getStdDev());
        processMetrics.setProcioreadrate55quantile(GlobalProperties.getProcessMetricsService()
            .getProcIOReadRate().getQuantile55());
        processMetrics.setProcioreadrate75quantile(GlobalProperties.getProcessMetricsService()
            .getProcIOReadRate().getQuantile75());
        processMetrics.setProcioreadrate95quantile(GlobalProperties.getProcessMetricsService()
            .getProcIOReadRate().getQuantile95());
        processMetrics.setProcioreadrate99quantile(GlobalProperties.getProcessMetricsService()
            .getProcIOReadRate().getQuantile99());
        /* procioreadbytesrate */
        processMetrics.setProcioreadbytesrate(GlobalProperties.getProcessMetricsService()
            .getProcIOReadBytesRate().getLast());
        processMetrics.setProcioreadbytesratemin(GlobalProperties.getProcessMetricsService()
            .getProcIOReadBytesRate().getMin());
        processMetrics.setProcioreadbytesratemax(GlobalProperties.getProcessMetricsService()
            .getProcIOReadBytesRate().getMax());
        processMetrics.setProcioreadbytesratemean(GlobalProperties.getProcessMetricsService()
            .getProcIOReadBytesRate().getAvg());
        processMetrics.setProcioreadbytesratestd(GlobalProperties.getProcessMetricsService()
            .getProcIOReadBytesRate().getStdDev());
        processMetrics.setProcioreadbytesrate55quantile(GlobalProperties.getProcessMetricsService()
            .getProcIOReadBytesRate().getQuantile55());
        processMetrics.setProcioreadbytesrate75quantile(GlobalProperties.getProcessMetricsService()
            .getProcIOReadBytesRate().getQuantile75());
        processMetrics.setProcioreadbytesrate95quantile(GlobalProperties.getProcessMetricsService()
            .getProcIOReadBytesRate().getQuantile95());
        processMetrics.setProcioreadbytesrate99quantile(GlobalProperties.getProcessMetricsService()
            .getProcIOReadBytesRate().getQuantile99());
        /* prociowriterate */
        processMetrics.setProciowriterate(GlobalProperties.getProcessMetricsService()
            .getProcIOWriteRate().getLast());
        processMetrics.setProciowriteratemin(GlobalProperties.getProcessMetricsService()
            .getProcIOWriteRate().getMin());
        processMetrics.setProciowriteratemax(GlobalProperties.getProcessMetricsService()
            .getProcIOWriteRate().getMax());
        processMetrics.setProciowriteratemean(GlobalProperties.getProcessMetricsService()
            .getProcIOWriteRate().getAvg());
        processMetrics.setProciowriteratestd(GlobalProperties.getProcessMetricsService()
            .getProcIOWriteRate().getStdDev());
        processMetrics.setProciowriterate55quantile(GlobalProperties.getProcessMetricsService()
            .getProcIOWriteRate().getQuantile55());
        processMetrics.setProciowriterate75quantile(GlobalProperties.getProcessMetricsService()
            .getProcIOWriteRate().getQuantile75());
        processMetrics.setProciowriterate95quantile(GlobalProperties.getProcessMetricsService()
            .getProcIOWriteRate().getQuantile95());
        processMetrics.setProciowriterate99quantile(GlobalProperties.getProcessMetricsService()
            .getProcIOWriteRate().getQuantile99());
        /* prociowritebytesrate */
        processMetrics.setProciowritebytesrate(GlobalProperties.getProcessMetricsService()
            .getProcIOWriteBytesRate().getLast());
        processMetrics.setProciowritebytesratemin(GlobalProperties.getProcessMetricsService()
            .getProcIOWriteBytesRate().getMin());
        processMetrics.setProciowritebytesratemax(GlobalProperties.getProcessMetricsService()
            .getProcIOWriteBytesRate().getMax());
        processMetrics.setProciowritebytesratemean(GlobalProperties.getProcessMetricsService()
            .getProcIOWriteBytesRate().getAvg());
        processMetrics.setProciowritebytesratestd(GlobalProperties.getProcessMetricsService()
            .getProcIOWriteBytesRate().getStdDev());
        processMetrics.setProciowritebytesrate55quantile(GlobalProperties
            .getProcessMetricsService().getProcIOWriteBytesRate().getQuantile55());
        processMetrics.setProciowritebytesrate75quantile(GlobalProperties
            .getProcessMetricsService().getProcIOWriteBytesRate().getQuantile75());
        processMetrics.setProciowritebytesrate95quantile(GlobalProperties
            .getProcessMetricsService().getProcIOWriteBytesRate().getQuantile95());
        processMetrics.setProciowritebytesrate99quantile(GlobalProperties
            .getProcessMetricsService().getProcIOWriteBytesRate().getQuantile99());
        /* procioreadwriterate */
        processMetrics.setProcioreadwriterate(GlobalProperties.getProcessMetricsService()
            .getProcIOReadWriteRate().getLast());
        processMetrics.setProcioreadwriteratemin(GlobalProperties.getProcessMetricsService()
            .getProcIOReadWriteRate().getMin());
        processMetrics.setProcioreadwriteratemax(GlobalProperties.getProcessMetricsService()
            .getProcIOReadWriteRate().getMax());
        processMetrics.setProcioreadwriteratemean(GlobalProperties.getProcessMetricsService()
            .getProcIOReadWriteRate().getAvg());
        processMetrics.setProcioreadwriteratestd(GlobalProperties.getProcessMetricsService()
            .getProcIOReadWriteRate().getStdDev());
        processMetrics.setProcioreadwriterate55quantile(GlobalProperties.getProcessMetricsService()
            .getProcIOReadWriteRate().getQuantile55());
        processMetrics.setProcioreadwriterate75quantile(GlobalProperties.getProcessMetricsService()
            .getProcIOReadWriteRate().getQuantile75());
        processMetrics.setProcioreadwriterate95quantile(GlobalProperties.getProcessMetricsService()
            .getProcIOReadWriteRate().getQuantile95());
        processMetrics.setProcioreadwriterate99quantile(GlobalProperties.getProcessMetricsService()
            .getProcIOReadWriteRate().getQuantile99());
        /* procioreadwritebytesrate */
        processMetrics.setProcioreadwritebytesrate(GlobalProperties.getProcessMetricsService()
            .getProcIOReadWriteBytesRate().getLast());
        processMetrics.setProcioreadwritebytesratemin(GlobalProperties.getProcessMetricsService()
            .getProcIOReadWriteBytesRate().getMin());
        processMetrics.setProcioreadwritebytesratemax(GlobalProperties.getProcessMetricsService()
            .getProcIOReadWriteBytesRate().getMax());
        processMetrics.setProcioreadwritebytesratemean(GlobalProperties.getProcessMetricsService()
            .getProcIOReadWriteBytesRate().getAvg());
        processMetrics.setProcioreadwritebytesratestd(GlobalProperties.getProcessMetricsService()
            .getProcIOReadWriteBytesRate().getStdDev());
        processMetrics.setProcioreadwritebytesrate55quantile(GlobalProperties
            .getProcessMetricsService().getProcIOReadWriteBytesRate().getQuantile55());
        processMetrics.setProcioreadwritebytesrate75quantile(GlobalProperties
            .getProcessMetricsService().getProcIOReadWriteBytesRate().getQuantile75());
        processMetrics.setProcioreadwritebytesrate95quantile(GlobalProperties
            .getProcessMetricsService().getProcIOReadWriteBytesRate().getQuantile95());
        processMetrics.setProcioreadwritebytesrate99quantile(GlobalProperties
            .getProcessMetricsService().getProcIOReadWriteBytesRate().getQuantile99());
        /* procioawaittimepercent */
        processMetrics.setProcioawaittimepercent(GlobalProperties.getProcessMetricsService()
            .getProcIOAwaitTimePercent().getLast());
        processMetrics.setProcioawaittimepercentmin(GlobalProperties.getProcessMetricsService()
            .getProcIOAwaitTimePercent().getMin());
        processMetrics.setProcioawaittimepercentmax(GlobalProperties.getProcessMetricsService()
            .getProcIOAwaitTimePercent().getMax());
        processMetrics.setProcioawaittimepercentmean(GlobalProperties.getProcessMetricsService()
            .getProcIOAwaitTimePercent().getAvg());
        processMetrics.setProcioawaittimepercentstd(GlobalProperties.getProcessMetricsService()
            .getProcIOAwaitTimePercent().getStdDev());
        processMetrics.setProcioawaittimepercent55quantile(GlobalProperties
            .getProcessMetricsService().getProcIOAwaitTimePercent().getQuantile55());
        processMetrics.setProcioawaittimepercent75quantile(GlobalProperties
            .getProcessMetricsService().getProcIOAwaitTimePercent().getQuantile75());
        processMetrics.setProcioawaittimepercent95quantile(GlobalProperties
            .getProcessMetricsService().getProcIOAwaitTimePercent().getQuantile95());
        processMetrics.setProcioawaittimepercent99quantile(GlobalProperties
            .getProcessMetricsService().getProcIOAwaitTimePercent().getQuantile99());
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
        processMetrics.setProcnetworkreceivebytesps(GlobalProperties.getProcessMetricsService()
            .getProcNetworkReceiveBytesPs().getLast());
        processMetrics.setProcnetworkreceivebytespsmin(GlobalProperties.getProcessMetricsService()
            .getProcNetworkReceiveBytesPs().getMin());
        processMetrics.setProcnetworkreceivebytespsmax(GlobalProperties.getProcessMetricsService()
            .getProcNetworkReceiveBytesPs().getMax());
        processMetrics.setProcnetworkreceivebytespsmean(GlobalProperties.getProcessMetricsService()
            .getProcNetworkReceiveBytesPs().getAvg());
        processMetrics.setProcnetworkreceivebytespsstd(GlobalProperties.getProcessMetricsService()
            .getProcNetworkReceiveBytesPs().getStdDev());
        processMetrics.setProcnetworkreceivebytesps55quantile(GlobalProperties
            .getProcessMetricsService().getProcNetworkReceiveBytesPs().getQuantile55());
        processMetrics.setProcnetworkreceivebytesps75quantile(GlobalProperties
            .getProcessMetricsService().getProcNetworkReceiveBytesPs().getQuantile75());
        processMetrics.setProcnetworkreceivebytesps95quantile(GlobalProperties
            .getProcessMetricsService().getProcNetworkReceiveBytesPs().getQuantile95());
        processMetrics.setProcnetworkreceivebytesps99quantile(GlobalProperties
            .getProcessMetricsService().getProcNetworkReceiveBytesPs().getQuantile99());
        /* procnetworksendbytesps */
        processMetrics.setProcnetworksendbytesps(GlobalProperties.getProcessMetricsService()
            .getProcNetworkSendBytesPs().getLast());
        processMetrics.setProcnetworksendbytespsmin(GlobalProperties.getProcessMetricsService()
            .getProcNetworkSendBytesPs().getMin());
        processMetrics.setProcnetworksendbytespsmax(GlobalProperties.getProcessMetricsService()
            .getProcNetworkSendBytesPs().getMax());
        processMetrics.setProcnetworksendbytespsmean(GlobalProperties.getProcessMetricsService()
            .getProcNetworkSendBytesPs().getAvg());
        processMetrics.setProcnetworksendbytespsstd(GlobalProperties.getProcessMetricsService()
            .getProcNetworkSendBytesPs().getStdDev());
        processMetrics.setProcnetworksendbytesps55quantile(GlobalProperties
            .getProcessMetricsService().getProcNetworkSendBytesPs().getQuantile55());
        processMetrics.setProcnetworksendbytesps75quantile(GlobalProperties
            .getProcessMetricsService().getProcNetworkSendBytesPs().getQuantile75());
        processMetrics.setProcnetworksendbytesps95quantile(GlobalProperties
            .getProcessMetricsService().getProcNetworkSendBytesPs().getQuantile95());
        processMetrics.setProcnetworksendbytesps99quantile(GlobalProperties
            .getProcessMetricsService().getProcNetworkSendBytesPs().getQuantile99());
        /* procnetworkconnrate */
        processMetrics.setProcnetworkconnrate(GlobalProperties.getProcessMetricsService()
            .getProcNetworkConnRate().getLast());
        processMetrics.setProcnetworkconnratemin(GlobalProperties.getProcessMetricsService()
            .getProcNetworkConnRate().getMin());
        processMetrics.setProcnetworkconnratemax(GlobalProperties.getProcessMetricsService()
            .getProcNetworkConnRate().getMax());
        processMetrics.setProcnetworkconnratemean(GlobalProperties.getProcessMetricsService()
            .getProcNetworkConnRate().getAvg());
        processMetrics.setProcnetworkconnratestd(GlobalProperties.getProcessMetricsService()
            .getProcNetworkConnRate().getStdDev());
        processMetrics.setProcnetworkconnrate55quantile(GlobalProperties.getProcessMetricsService()
            .getProcNetworkConnRate().getQuantile55());
        processMetrics.setProcnetworkconnrate75quantile(GlobalProperties.getProcessMetricsService()
            .getProcNetworkConnRate().getQuantile75());
        processMetrics.setProcnetworkconnrate95quantile(GlobalProperties.getProcessMetricsService()
            .getProcNetworkConnRate().getQuantile95());
        processMetrics.setProcnetworkconnrate99quantile(GlobalProperties.getProcessMetricsService()
            .getProcNetworkConnRate().getQuantile99());
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
        /* systemcpuutiltotalpercent */
        systemMetrics.setSystemcpuutiltotalpercent(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUtilTotalPercent().getLast());
        systemMetrics.setSystemcpuutiltotalpercentmin(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUtilTotalPercent().getMin());
        systemMetrics.setSystemcpuutiltotalpercentmax(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUtilTotalPercent().getMax());
        systemMetrics.setSystemcpuutiltotalpercentmean(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUtilTotalPercent().getAvg());
        systemMetrics.setSystemcpuutiltotalpercentstd(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUtilTotalPercent().getStdDev());
        systemMetrics.setSystemcpuutiltotalpercent55quantile(GlobalProperties
            .getSystemMetricsService().getSystemCpuUtilTotalPercent().getQuantile55());
        systemMetrics.setSystemcpuutiltotalpercent75quantile(GlobalProperties
            .getSystemMetricsService().getSystemCpuUtilTotalPercent().getQuantile75());
        systemMetrics.setSystemcpuutiltotalpercent95quantile(GlobalProperties
            .getSystemMetricsService().getSystemCpuUtilTotalPercent().getQuantile95());
        systemMetrics.setSystemcpuutiltotalpercent99quantile(GlobalProperties
            .getSystemMetricsService().getSystemCpuUtilTotalPercent().getQuantile99());
        /* systemcpusystem */
        systemMetrics.setSystemcpusystem(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSystem().getLast());
        systemMetrics.setSystemcpusystemmin(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSystem().getMin());
        systemMetrics.setSystemcpusystemmax(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSystem().getMax());
        systemMetrics.setSystemcpusystemmean(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSystem().getAvg());
        systemMetrics.setSystemcpusystemstd(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSystem().getStdDev());
        systemMetrics.setSystemcpusystem55quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSystem().getQuantile55());
        systemMetrics.setSystemcpusystem75quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSystem().getQuantile75());
        systemMetrics.setSystemcpusystem95quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSystem().getQuantile95());
        systemMetrics.setSystemcpusystem99quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSystem().getQuantile99());
        /* systemcpuuser */
        systemMetrics.setSystemcpuuser(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUser().getLast());
        systemMetrics.setSystemcpuusermin(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUser().getMin());
        systemMetrics.setSystemcpuusermax(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUser().getMax());
        systemMetrics.setSystemcpuusermean(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUser().getAvg());
        systemMetrics.setSystemcpuuserstd(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUser().getStdDev());
        systemMetrics.setSystemcpuuser55quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUser().getQuantile55());
        systemMetrics.setSystemcpuuser75quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUser().getQuantile75());
        systemMetrics.setSystemcpuuser95quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUser().getQuantile95());
        systemMetrics.setSystemcpuuser99quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUser().getQuantile99());
        /* systemcpuidle */
        systemMetrics.setSystemcpuidle(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIdle().getLast());
        systemMetrics.setSystemcpuidlemin(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIdle().getMin());
        systemMetrics.setSystemcpuidlemax(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIdle().getMax());
        systemMetrics.setSystemcpuidlemean(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIdle().getAvg());
        systemMetrics.setSystemcpuidlestd(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIdle().getStdDev());
        systemMetrics.setSystemcpuidle55quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIdle().getQuantile55());
        systemMetrics.setSystemcpuidle75quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIdle().getQuantile75());
        systemMetrics.setSystemcpuidle95quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIdle().getQuantile95());
        systemMetrics.setSystemcpuidle99quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIdle().getQuantile99());
        /* systemcpuswitches */
        systemMetrics.setSystemcpuswitches(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSwitches().getLast());
        systemMetrics.setSystemcpuswitchesmin(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSwitches().getMin());
        systemMetrics.setSystemcpuswitchesmax(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSwitches().getMax());
        systemMetrics.setSystemcpuswitchesmean(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSwitches().getAvg());
        systemMetrics.setSystemcpuswitchesstd(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSwitches().getStdDev());
        systemMetrics.setSystemcpuswitches55quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSwitches().getQuantile55());
        systemMetrics.setSystemcpuswitches75quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSwitches().getQuantile75());
        systemMetrics.setSystemcpuswitches95quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSwitches().getQuantile95());
        systemMetrics.setSystemcpuswitches99quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSwitches().getQuantile99());
        /* systemcpuusageirq */
        systemMetrics.setSystemcpuusageirq(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageIrq().getLast());
        systemMetrics.setSystemcpuusageirqmin(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageIrq().getMin());
        systemMetrics.setSystemcpuusageirqmax(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageIrq().getMax());
        systemMetrics.setSystemcpuusageirqmean(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageIrq().getAvg());
        systemMetrics.setSystemcpuusageirqstd(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageIrq().getStdDev());
        systemMetrics.setSystemcpuusageirq55quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageIrq().getQuantile55());
        systemMetrics.setSystemcpuusageirq75quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageIrq().getQuantile75());
        systemMetrics.setSystemcpuusageirq95quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageIrq().getQuantile95());
        systemMetrics.setSystemcpuusageirq99quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageIrq().getQuantile99());
        /* systemcpuusagesoftirq */
        systemMetrics.setSystemcpuusagesoftirq(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageSoftIrq().getLast());
        systemMetrics.setSystemcpuusagesoftirqmin(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageSoftIrq().getMin());
        systemMetrics.setSystemcpuusagesoftirqmax(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageSoftIrq().getMax());
        systemMetrics.setSystemcpuusagesoftirqmean(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageSoftIrq().getAvg());
        systemMetrics.setSystemcpuusagesoftirqstd(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageSoftIrq().getStdDev());
        systemMetrics.setSystemcpuusagesoftirq55quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageSoftIrq().getQuantile55());
        systemMetrics.setSystemcpuusagesoftirq75quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageSoftIrq().getQuantile75());
        systemMetrics.setSystemcpuusagesoftirq95quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageSoftIrq().getQuantile95());
        systemMetrics.setSystemcpuusagesoftirq99quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuUsageSoftIrq().getQuantile99());
        /* systemload1 */
        systemMetrics.setSystemload1(GlobalProperties.getSystemMetricsService().getSystemLoad1()
            .getLast());
        systemMetrics.setSystemload1min(GlobalProperties.getSystemMetricsService().getSystemLoad1()
            .getMin());
        systemMetrics.setSystemload1max(GlobalProperties.getSystemMetricsService().getSystemLoad1()
            .getMax());
        systemMetrics.setSystemload1mean(GlobalProperties.getSystemMetricsService()
            .getSystemLoad1().getAvg());
        systemMetrics.setSystemload1std(GlobalProperties.getSystemMetricsService().getSystemLoad1()
            .getStdDev());
        systemMetrics.setSystemload155quantile(GlobalProperties.getSystemMetricsService()
            .getSystemLoad1().getQuantile55());
        systemMetrics.setSystemload175quantile(GlobalProperties.getSystemMetricsService()
            .getSystemLoad1().getQuantile75());
        systemMetrics.setSystemload195quantile(GlobalProperties.getSystemMetricsService()
            .getSystemLoad1().getQuantile95());
        systemMetrics.setSystemload199quantile(GlobalProperties.getSystemMetricsService()
            .getSystemLoad1().getQuantile99());
        /* systemload5 */
        systemMetrics.setSystemload5(GlobalProperties.getSystemMetricsService().getSystemLoad5()
            .getLast());
        systemMetrics.setSystemload5min(GlobalProperties.getSystemMetricsService().getSystemLoad5()
            .getMin());
        systemMetrics.setSystemload5max(GlobalProperties.getSystemMetricsService().getSystemLoad5()
            .getMax());
        systemMetrics.setSystemload5mean(GlobalProperties.getSystemMetricsService()
            .getSystemLoad5().getAvg());
        systemMetrics.setSystemload5std(GlobalProperties.getSystemMetricsService().getSystemLoad5()
            .getStdDev());
        systemMetrics.setSystemload555quantile(GlobalProperties.getSystemMetricsService()
            .getSystemLoad5().getQuantile55());
        systemMetrics.setSystemload575quantile(GlobalProperties.getSystemMetricsService()
            .getSystemLoad5().getQuantile75());
        systemMetrics.setSystemload595quantile(GlobalProperties.getSystemMetricsService()
            .getSystemLoad5().getQuantile95());
        systemMetrics.setSystemload599quantile(GlobalProperties.getSystemMetricsService()
            .getSystemLoad5().getQuantile99());
        /* systemload15 */
        systemMetrics.setSystemload15(GlobalProperties.getSystemMetricsService().getSystemLoad15()
            .getLast());
        systemMetrics.setSystemload15min(GlobalProperties.getSystemMetricsService()
            .getSystemLoad15().getMin());
        systemMetrics.setSystemload15max(GlobalProperties.getSystemMetricsService()
            .getSystemLoad15().getMax());
        systemMetrics.setSystemload15mean(GlobalProperties.getSystemMetricsService()
            .getSystemLoad15().getAvg());
        systemMetrics.setSystemload15std(GlobalProperties.getSystemMetricsService()
            .getSystemLoad15().getStdDev());
        systemMetrics.setSystemload1555quantile(GlobalProperties.getSystemMetricsService()
            .getSystemLoad15().getQuantile55());
        systemMetrics.setSystemload1575quantile(GlobalProperties.getSystemMetricsService()
            .getSystemLoad15().getQuantile75());
        systemMetrics.setSystemload1595quantile(GlobalProperties.getSystemMetricsService()
            .getSystemLoad15().getQuantile95());
        systemMetrics.setSystemload1599quantile(GlobalProperties.getSystemMetricsService()
            .getSystemLoad15().getQuantile99());
        /* systemcpuiowait */
        systemMetrics.setSystemcpuiowait(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIOWait().getLast());
        systemMetrics.setSystemcpuiowaitmin(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIOWait().getMin());
        systemMetrics.setSystemcpuiowaitmax(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIOWait().getMax());
        systemMetrics.setSystemcpuiowaitmean(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIOWait().getAvg());
        systemMetrics.setSystemcpuiowaitstd(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIOWait().getStdDev());
        systemMetrics.setSystemcpuiowait55quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIOWait().getQuantile55());
        systemMetrics.setSystemcpuiowait75quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIOWait().getQuantile75());
        systemMetrics.setSystemcpuiowait95quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIOWait().getQuantile95());
        systemMetrics.setSystemcpuiowait99quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuIOWait().getQuantile99());
        /* systemcpuguest */
        systemMetrics.setSystemcpuguest(GlobalProperties.getSystemMetricsService()
            .getSystemCpuGuest().getLast());
        systemMetrics.setSystemcpuguestmin(GlobalProperties.getSystemMetricsService()
            .getSystemCpuGuest().getMin());
        systemMetrics.setSystemcpuguestmax(GlobalProperties.getSystemMetricsService()
            .getSystemCpuGuest().getMax());
        systemMetrics.setSystemcpuguestmean(GlobalProperties.getSystemMetricsService()
            .getSystemCpuGuest().getAvg());
        systemMetrics.setSystemcpugueststd(GlobalProperties.getSystemMetricsService()
            .getSystemCpuGuest().getStdDev());
        systemMetrics.setSystemcpuguest55quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuGuest().getQuantile55());
        systemMetrics.setSystemcpuguest75quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuGuest().getQuantile75());
        systemMetrics.setSystemcpuguest95quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuGuest().getQuantile95());
        systemMetrics.setSystemcpuguest99quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuGuest().getQuantile99());
        /* systemcpusteal */
        systemMetrics.setSystemcpusteal(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSteal().getLast());
        systemMetrics.setSystemcpustealmin(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSteal().getMin());
        systemMetrics.setSystemcpustealmax(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSteal().getMax());
        systemMetrics.setSystemcpustealmean(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSteal().getAvg());
        systemMetrics.setSystemcpustealstd(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSteal().getStdDev());
        systemMetrics.setSystemcpusteal55quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSteal().getQuantile55());
        systemMetrics.setSystemcpusteal75quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSteal().getQuantile75());
        systemMetrics.setSystemcpusteal95quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSteal().getQuantile95());
        systemMetrics.setSystemcpusteal99quantile(GlobalProperties.getSystemMetricsService()
            .getSystemCpuSteal().getQuantile99());
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
        systemMetrics.setSystemnetworkreceivebytesps(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkReceiveBytesPs().getLast());
        systemMetrics.setSystemnetworkreceivebytespsmin(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkReceiveBytesPs().getMin());
        systemMetrics.setSystemnetworkreceivebytespsmax(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkReceiveBytesPs().getMax());
        systemMetrics.setSystemnetworkreceivebytespsmean(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkReceiveBytesPs().getAvg());
        systemMetrics.setSystemnetworkreceivebytespsstd(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkReceiveBytesPs().getStdDev());
        systemMetrics.setSystemnetworkreceivebytesps55quantile(GlobalProperties
            .getSystemMetricsService().getSystemNetworkReceiveBytesPs().getQuantile55());
        systemMetrics.setSystemnetworkreceivebytesps75quantile(GlobalProperties
            .getSystemMetricsService().getSystemNetworkReceiveBytesPs().getQuantile75());
        systemMetrics.setSystemnetworkreceivebytesps95quantile(GlobalProperties
            .getSystemMetricsService().getSystemNetworkReceiveBytesPs().getQuantile95());
        systemMetrics.setSystemnetworkreceivebytesps99quantile(GlobalProperties
            .getSystemMetricsService().getSystemNetworkReceiveBytesPs().getQuantile99());
        /* systemnetworksendbytesps */
        systemMetrics.setSystemnetworksendbytesps(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkSendBytesPs().getLast());
        systemMetrics.setSystemnetworksendbytespsmin(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkSendBytesPs().getMin());
        systemMetrics.setSystemnetworksendbytespsmax(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkSendBytesPs().getMax());
        systemMetrics.setSystemnetworksendbytespsmean(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkSendBytesPs().getAvg());
        systemMetrics.setSystemnetworksendbytespsstd(GlobalProperties.getSystemMetricsService()
            .getSystemNetworkSendBytesPs().getStdDev());
        systemMetrics.setSystemnetworksendbytesps55quantile(GlobalProperties
            .getSystemMetricsService().getSystemNetworkSendBytesPs().getQuantile55());
        systemMetrics.setSystemnetworksendbytesps75quantile(GlobalProperties
            .getSystemMetricsService().getSystemNetworkSendBytesPs().getQuantile75());
        systemMetrics.setSystemnetworksendbytesps95quantile(GlobalProperties
            .getSystemMetricsService().getSystemNetworkSendBytesPs().getQuantile95());
        systemMetrics.setSystemnetworksendbytesps99quantile(GlobalProperties
            .getSystemMetricsService().getSystemNetworkSendBytesPs().getQuantile99());
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
        /* systemNetWorkBandWidthUsedPercent */
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
