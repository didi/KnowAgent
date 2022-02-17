package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.bean.*;
import com.didichuxing.datachannel.system.metrcis.service.SystemMetricsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;


/**
 * 获取系统级指标
 * 包括按需获取指标数据和一次性获取所有指标数据
 * @author Ronaldo
 * @Date 2021/11/3
 */
public class LinuxSystemMetricsServiceImpl implements SystemMetricsService {

    @Override
    public String getHostName() {
        return null;
    }

    @Override
    public Long getSystemNtpOffset() {
        return null;
    }

    @Override
    public Integer getSystemProcCount() {
        return null;
    }

    @Override
    public Long getSystemStartupTime() {
        return null;
    }

    @Override
    public Long getSystemUptime() {
        return null;
    }

    @Override
    public Integer getProcessesBlocked() {
        return null;
    }

    @Override
    public Integer getProcessesSleeping() {
        return null;
    }

    @Override
    public Integer getProcessesZombies() {
        return null;
    }

    @Override
    public Integer getProcessesStopped() {
        return null;
    }

    @Override
    public Integer getProcessesRunning() {
        return null;
    }

    @Override
    public Integer getProcessesIdle() {
        return null;
    }

    @Override
    public Integer getProcessesWait() {
        return null;
    }

    @Override
    public Integer getProcessesDead() {
        return null;
    }

    @Override
    public Integer getProcessesPaging() {
        return null;
    }

    @Override
    public Integer getProcessesUnknown() {
        return null;
    }

    @Override
    public Integer getProcessesTotal() {
        return null;
    }

    @Override
    public Integer getProcessesTotalThreads() {
        return null;
    }

    @Override
    public Integer getSystemCpuCores() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuUtil() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuUtilTotalPercent() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuSystem() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuUser() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuIdle() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuSwitches() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuUsageIrq() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuUsageSoftIrq() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemLoad1() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemLoad5() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemLoad15() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuIOWait() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuGuest() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuSteal() {
        return null;
    }

    @Override
    public Long getSystemMemCommitLimit() {
        return null;
    }

    @Override
    public Long getSystemMemCommittedAs() {
        return null;
    }

    @Override
    public Long getSystemMemCommitted() {
        return null;
    }

    @Override
    public Long getSystemMemNonPaged() {
        return null;
    }

    @Override
    public Long getSystemMemPaged() {
        return null;
    }

    @Override
    public Long getSystemMemShared() {
        return null;
    }

    @Override
    public Long getSystemMemSlab() {
        return null;
    }

    @Override
    public Long getSystemMemTotal() {
        return null;
    }

    @Override
    public Long getSystemMemFree() {
        return null;
    }

    @Override
    public Long getSystemMemUsed() {
        return null;
    }

    @Override
    public Long getSystemMemBuffered() {
        return null;
    }

    @Override
    public Long getSystemMemCached() {
        return null;
    }

    @Override
    public Double getSystemMemFreePercent() {
        return null;
    }

    @Override
    public Double getSystemMemUsedPercent() {
        return null;
    }

    @Override
    public Long getSystemSwapCached() {
        return null;
    }

    @Override
    public Long getSystemSwapFree() {
        return null;
    }

    @Override
    public Double getSystemSwapFreePercent() {
        return null;
    }

    @Override
    public Long getSystemSwapTotal() {
        return null;
    }

    @Override
    public Long getSystemSwapUsed() {
        return null;
    }

    @Override
    public Double getSystemSwapUsedPercent() {
        return null;
    }

    @Override
    public Integer getSystemDisks() {
        return null;
    }

    @Override
    public List<DiskInfo> getSystemDiskInfoList() {
        return null;
    }

    @Override
    public List<DiskIOInfo> getSystemDiskIOInfoList() {
        return null;
    }

    @Override
    public Integer getSystemFilesMax() {
        return null;
    }

    @Override
    public Integer getSystemFilesAllocated() {
        return null;
    }

    @Override
    public Integer getSystemFilesLeft() {
        return null;
    }

    @Override
    public Double getSystemFilesUsedPercent() {
        return null;
    }

    @Override
    public Integer getSystemFilesUsed() {
        return null;
    }

    @Override
    public Integer getSystemFilesNotUsed() {
        return null;
    }

    @Override
    public Integer getSystemNetCards() {
        return null;
    }

    @Override
    public List<NetCardInfo> getSystemNetCardInfoList() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemNetworkReceiveBytesPs() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemNetworkSendBytesPs() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemNetworkSendAndReceiveBytesPs() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemNetWorkBandWidthUsedPercent() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpConnectionNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpListeningNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpEstablishedNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpSynSentNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpSynRecvNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpFinWait1Num() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpFinWait2Num() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpTimeWaitNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpClosedNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpCloseWaitNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpClosingNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpLastAckNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpNoneNum() {
        return null;
    }

    @Override
    public Long getSystemNetworkTcpActiveOpens() {
        return null;
    }

    @Override
    public Long getSystemNetworkTcpPassiveOpens() {
        return null;
    }

    @Override
    public Long getSystemNetworkTcpAttemptFails() {
        return null;
    }

    @Override
    public Long getSystemNetworkTcpEstabResets() {
        return null;
    }

    @Override
    public Long getSystemNetworkTcpRetransSegs() {
        return null;
    }

    @Override
    public Long getSystemNetworkTcpExtListenOverflows() {
        return null;
    }

    @Override
    public Long getSystemNetworkUdpInDatagrams() {
        return null;
    }

    @Override
    public Long getSystemNetworkUdpOutDatagrams() {
        return null;
    }

    @Override
    public Long getSystemNetworkUdpInErrors() {
        return null;
    }

    @Override
    public Long getSystemNetworkUdpNoPorts() {
        return null;
    }

    @Override
    public Long getSystemNetworkUdpSendBufferErrors() {
        return null;
    }

    @Override
    public SystemMetrics getSystemMetrics() {
        return null;
    }

    @Override
    public String getOSType() {
        return null;
    }

    @Override
    public String getOSVersion() {
        return null;
    }
}
