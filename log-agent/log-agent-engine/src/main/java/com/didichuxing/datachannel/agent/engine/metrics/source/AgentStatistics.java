package com.didichuxing.datachannel.agent.engine.metrics.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import com.didichuxing.datachannel.agent.common.metrics.MetricsBuilder;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.limit.LimitService;
import com.didichuxing.datachannel.agent.engine.metrics.metric.*;
import com.didichuxing.datachannel.agent.engine.metrics.stat.MetricMutablePeriodGaugeLong;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;

import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.system.metrcis.Metrics;
import com.didichuxing.datachannel.system.metrcis.bean.DiskIOInfo;
import com.didichuxing.datachannel.system.metrcis.bean.DiskInfo;
import com.didichuxing.datachannel.system.metrcis.bean.NetCardInfo;
import com.didichuxing.datachannel.system.metrcis.exception.MetricsException;
import com.didichuxing.datachannel.system.metrcis.service.ProcessMetricsService;
import com.didichuxing.datachannel.system.metrcis.service.SystemMetricsService;
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
     * 系统相关指标服务
     */
    private SystemMetricsService                  systemMetricsService;

    /**
     * 进程相关指标服务
     */
    private ProcessMetricsService                 processMetricsService;

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
     * agent 两次指标数据发送周期内发送日志条数
     */
    private volatile MetricMutablePeriodGaugeLong agentSendCountPerPeriod;

    /**
     * agent 两次指标数据发送周期内发送日志量
     */
    private volatile MetricMutablePeriodGaugeLong agentSendBytePerPeriod;

    public AgentStatistics(String name, LimitService limiter, Long startTime,
                           Integer runningCollectTaskNum, Integer runningCollectPathNum)
                                                                                        throws MetricsException {
        super(name);
        this.limiter = limiter;
        this.startTime = startTime;
        this.systemMetricsService = Metrics.getMetricsServiceFactory().createSystemMetrics();
        this.processMetricsService = Metrics.getMetricsServiceFactory().createProcessMetrics();
        this.agentSendCountPerPeriod = new MetricMutablePeriodGaugeLong();
        this.agentSendBytePerPeriod = new MetricMutablePeriodGaugeLong();
        this.runningCollectTaskNum = runningCollectTaskNum;
        this.runningCollectPathNum = runningCollectPathNum;
    }

    @Override
    public void init() {
        /*
         * 初始化启动后值不变指标集
         */
        this.systemStartupTime = systemMetricsService.getSystemStartupTime();
        this.cpuCores = systemMetricsService.getSystemCpuCores();
        this.processStartupTime = processMetricsService.getProcessStartupTime();
        this.processPid = processMetricsService.getProcessPid();
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
        agentBusinessMetrics.setHeartbeattime(heartbeatTime);
        agentBusinessMetrics.setHeartbeattimeminute(heartbeatTimeMinute);
        agentBusinessMetrics.setHeartbeattimehour(heartbeatTimeHour);
        agentBusinessMetrics.setHeartbeatTimeDay(heartbeatTimeDay);
        return agentBusinessMetrics;
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
        List<NetCardInfo> netCardInfoList = systemMetricsService.getSystemNetCardInfoList();
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
            netCardMetrics.setSystemnetcardssendbytesps(netCardInfo.getSystemNetCardsSendBytesPs().getLast().longValue());
            netCardMetrics.setSystemnetcardssendbytespsmin(netCardInfo.getSystemNetCardsSendBytesPs().getMin().longValue());
            netCardMetrics.setSystemnetcardssendbytespsmax(netCardInfo.getSystemNetCardsSendBytesPs().getMax().longValue());
            netCardMetrics.setSystemnetcardssendbytespsmean(netCardInfo.getSystemNetCardsSendBytesPs().getAvg());
            netCardMetrics.setSystemnetcardssendbytespsstd(netCardInfo.getSystemNetCardsSendBytesPs().getStdDev());
            netCardMetrics.setSystemnetcardssendbytesps55quantile(netCardInfo.getSystemNetCardsSendBytesPs().getQuantile55().longValue());
            netCardMetrics.setSystemnetcardssendbytesps75quantile(netCardInfo.getSystemNetCardsSendBytesPs().getQuantile75().longValue());
            netCardMetrics.setSystemnetcardssendbytesps95quantile(netCardInfo.getSystemNetCardsSendBytesPs().getQuantile95().longValue());
            netCardMetrics.setSystemnetcardssendbytesps99quantile(netCardInfo.getSystemNetCardsSendBytesPs().getQuantile99().longValue());
            netCardMetrics.setSystemnetcardsbandwidth(netCardInfo.getSystemNetCardsBandWidth());
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
        List<DiskIOInfo> diskIOInfoList = systemMetricsService.getSystemDiskIOInfoList();
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
        processMetrics.setProccpuutil(processMetricsService.getProcCpuUtil().getLast());
        processMetrics.setProccpuutilmin(processMetricsService.getProcCpuUtil().getMin());
        processMetrics.setProccpuutilmax(processMetricsService.getProcCpuUtil().getMax());
        processMetrics.setProccpuutilmean(processMetricsService.getProcCpuUtil().getAvg());
        processMetrics.setProccpuutilstd(processMetricsService.getProcCpuUtil().getStdDev());
        processMetrics.setProccpuutil55quantile(processMetricsService.getProcCpuUtil()
            .getQuantile55());
        processMetrics.setProccpuutil75quantile(processMetricsService.getProcCpuUtil()
            .getQuantile75());
        processMetrics.setProccpuutil95quantile(processMetricsService.getProcCpuUtil()
            .getQuantile95());
        processMetrics.setProccpuutil99quantile(processMetricsService.getProcCpuUtil()
            .getQuantile99());
        processMetrics.setProcmemused(processMetricsService.getProcMemUsed());
        processMetrics.setProcnetworksendbytesps(processMetricsService.getProcNetworkSendBytesPs()
            .getLast().longValue());
        processMetrics.setProcnetworksendbytespsmin(processMetricsService
            .getProcNetworkSendBytesPs().getMin().longValue());
        processMetrics.setProcnetworksendbytespsmax(processMetricsService
            .getProcNetworkSendBytesPs().getMax().longValue());
        processMetrics.setProcnetworksendbytespsmean(processMetricsService
            .getProcNetworkSendBytesPs().getAvg().longValue());
        processMetrics.setProcnetworksendbytespsstd(processMetricsService
            .getProcNetworkSendBytesPs().getStdDev().longValue());
        processMetrics.setProcnetworksendbytesps55quantile(processMetricsService
            .getProcNetworkSendBytesPs().getQuantile55().longValue());
        processMetrics.setProcnetworksendbytesps75quantile(processMetricsService
            .getProcNetworkSendBytesPs().getQuantile75().longValue());
        processMetrics.setProcnetworksendbytesps95quantile(processMetricsService
            .getProcNetworkSendBytesPs().getQuantile95().longValue());
        processMetrics.setProcnetworksendbytesps99quantile(processMetricsService
            .getProcNetworkSendBytesPs().getQuantile99().longValue());
        processMetrics.setProcnetworkreceivebytesps(processMetricsService
            .getProcNetworkReceiveBytesPs().getLast().longValue());
        processMetrics.setProcnetworkreceivebytespsmin(processMetricsService
            .getProcNetworkReceiveBytesPs().getMin().longValue());
        processMetrics.setProcnetworkreceivebytespsmax(processMetricsService
            .getProcNetworkReceiveBytesPs().getMax().longValue());
        processMetrics.setProcnetworkreceivebytespsmean(processMetricsService
            .getProcNetworkReceiveBytesPs().getAvg().longValue());
        processMetrics.setProcnetworkreceivebytespsstd(processMetricsService
            .getProcNetworkReceiveBytesPs().getStdDev().longValue());
        processMetrics.setProcnetworkreceivebytesps55quantile(processMetricsService
            .getProcNetworkReceiveBytesPs().getQuantile55().longValue());
        processMetrics.setProcnetworkreceivebytesps75quantile(processMetricsService
            .getProcNetworkReceiveBytesPs().getQuantile75().longValue());
        processMetrics.setProcnetworkreceivebytesps95quantile(processMetricsService
            .getProcNetworkReceiveBytesPs().getQuantile95().longValue());
        processMetrics.setProcnetworkreceivebytesps99quantile(processMetricsService
            .getProcNetworkReceiveBytesPs().getQuantile99().longValue());
        processMetrics.setJvmprocfullgccount(processMetricsService.getJvmProcFullGcCount());
        processMetrics.setProcopenfdcount(processMetricsService.getProcOpenFdCount());
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
        systemMetrics.setSystemntpoffset(systemMetricsService.getSystemNtpOffset());
        systemMetrics.setSystemcpuutil(systemMetricsService.getSystemCpuUtil().getLast());
        systemMetrics.setSystemcpuutil55quantile(systemMetricsService.getSystemCpuUtil()
            .getQuantile55());
        systemMetrics.setSystemcpuutil75quantile(systemMetricsService.getSystemCpuUtil()
            .getQuantile75());
        systemMetrics.setSystemcpuutil95quantile(systemMetricsService.getSystemCpuUtil()
            .getQuantile95());
        systemMetrics.setSystemcpuutilmax(systemMetricsService.getSystemCpuUtil().getMax());
        systemMetrics.setSystemcpuutil99quantile(systemMetricsService.getSystemCpuUtil()
            .getQuantile99());
        systemMetrics.setSystemcpuutilmean(systemMetricsService.getSystemCpuUtil().getAvg());
        systemMetrics.setSystemcpuutilmin(systemMetricsService.getSystemCpuUtil().getMin());
        systemMetrics.setSystemcpuutilstd(systemMetricsService.getSystemCpuUtil().getStdDev());
        systemMetrics.setSystemmemfree(systemMetricsService.getSystemMemFree());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPs(systemMetricsService
            .getSystemNetworkSendAndReceiveBytesPs().getLast());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPsMin(systemMetricsService
            .getSystemNetworkSendAndReceiveBytesPs().getMin());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPsMax(systemMetricsService
            .getSystemNetworkSendAndReceiveBytesPs().getMax());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPsMean(systemMetricsService
            .getSystemNetworkSendAndReceiveBytesPs().getAvg());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPsStd(systemMetricsService
            .getSystemNetworkSendAndReceiveBytesPs().getStdDev());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPs55Quantile(systemMetricsService
            .getSystemNetworkSendAndReceiveBytesPs().getQuantile55());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPs75Quantile(systemMetricsService
            .getSystemNetworkSendAndReceiveBytesPs().getQuantile75());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPs95Quantile(systemMetricsService
            .getSystemNetworkSendAndReceiveBytesPs().getQuantile95());
        systemMetrics.setSystemNetworkSendAndReceiveBytesPs99Quantile(systemMetricsService
            .getSystemNetworkSendAndReceiveBytesPs().getQuantile99());
        systemMetrics.setSystemNetWorkBandWidthUsedPercent(systemMetricsService
            .getSystemNetWorkBandWidthUsedPercent().getLast());
        systemMetrics.setSystemNetWorkBandWidthUsedPercentMin(systemMetricsService
            .getSystemNetWorkBandWidthUsedPercent().getMin());
        systemMetrics.setSystemNetWorkBandWidthUsedPercentMax(systemMetricsService
            .getSystemNetWorkBandWidthUsedPercent().getMax());
        systemMetrics.setSystemNetWorkBandWidthUsedPercentMean(systemMetricsService
            .getSystemNetWorkBandWidthUsedPercent().getAvg());
        systemMetrics.setSystemNetWorkBandWidthUsedPercentStd(systemMetricsService
            .getSystemNetWorkBandWidthUsedPercent().getStdDev());
        systemMetrics.setSystemNetWorkBandWidthUsedPercent55Quantile(systemMetricsService
            .getSystemNetWorkBandWidthUsedPercent().getQuantile55());
        systemMetrics.setSystemNetWorkBandWidthUsedPercent75Quantile(systemMetricsService
            .getSystemNetWorkBandWidthUsedPercent().getQuantile75());
        systemMetrics.setSystemNetWorkBandWidthUsedPercent95Quantile(systemMetricsService
            .getSystemNetWorkBandWidthUsedPercent().getQuantile95());
        systemMetrics.setSystemNetWorkBandWidthUsedPercent99Quantile(systemMetricsService
            .getSystemNetWorkBandWidthUsedPercent().getQuantile99());
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
        String hostName = systemMetricsService.getHostName();
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
        List<DiskInfo> diskInfoList = systemMetricsService.getSystemDiskInfoList();
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

    public void setRunningCollectPathNum(Integer runningCollectPathNum) {
        this.runningCollectPathNum = runningCollectPathNum;
    }

}
