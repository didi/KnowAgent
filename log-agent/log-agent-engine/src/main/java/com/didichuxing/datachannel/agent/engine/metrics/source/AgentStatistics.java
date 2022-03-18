package com.didichuxing.datachannel.agent.engine.metrics.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsBuilder;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.bean.GlobalProperties;
import com.didichuxing.datachannel.agent.engine.limit.LimitService;
import com.didichuxing.datachannel.agent.engine.metrics.metric.*;
import com.didichuxing.datachannel.agent.engine.metrics.stat.MetricMutablePeriodGaugeLong;
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

    public AgentStatistics(String name, LimitService limiter, Long startTime,
                           Integer runningCollectTaskNum, Integer runningCollectPathNum) {
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

    public void sourceOneRecord(long bytes, long cost) {
        agentReadCountPerPeriod.incr();
        agentReadBytePerPeriod.incr(bytes);
    }

    public void sendErrorLogsRecord() {
        this.errorLogsCountPerPeriod.incr();
    }

}
