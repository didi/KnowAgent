package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.bean.DiskInfo;
import com.didichuxing.datachannel.system.metrcis.bean.NetCardInfo;
import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;
import com.didichuxing.datachannel.system.metrcis.bean.SystemMetrics;
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
    public long getSystemNtpOffset() {
        return 0;
    }

    @Override
    public int getSystemProcCount() {
        return 0;
    }

    @Override
    public long getSystemStartupTime() {
        return 0;
    }

    @Override
    public long getSystemUptime() {
        return 0;
    }

    @Override
    public int getProcessesBlocked() {
        return 0;
    }

    @Override
    public int getProcessesSleeping() {
        return 0;
    }

    @Override
    public int getProcessesZombies() {
        return 0;
    }

    @Override
    public int getProcessesStopped() {
        return 0;
    }

    @Override
    public int getProcessesRunning() {
        return 0;
    }

    @Override
    public int getProcessesIdle() {
        return 0;
    }

    @Override
    public int getProcessesWait() {
        return 0;
    }

    @Override
    public int getProcessesDead() {
        return 0;
    }

    @Override
    public int getProcessesPaging() {
        return 0;
    }

    @Override
    public int getProcessesUnknown() {
        return 0;
    }

    @Override
    public int getProcessesTotal() {
        return 0;
    }

    @Override
    public int getProcessesTotalThreads() {
        return 0;
    }

    @Override
    public int getSystemCpuCores() {
        return 0;
    }

    @Override
    public PeriodStatistics<Double> getSystemCpuUtil() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getSystemCpuUtilTotalPercent() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getSystemCpuSystem() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getSystemCpuUser() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getSystemCpuIdle() {
        return null;
    }

    @Override
    public PeriodStatistics<Long> getSystemCpuSwitches() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getSystemCpuUsageIrq() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getSystemCpuUsageSoftIrq() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getSystemLoad1() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getSystemLoad5() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getSystemLoad15() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getSystemCpuIOWait() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getSystemCpuGuest() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getSystemCpuSteal() {
        return null;
    }

    @Override
    public long getSystemMemCommitLimit() {
        return 0;
    }

    @Override
    public long getSystemMemCommittedAs() {
        return 0;
    }

    @Override
    public long getSystemMemCommitted() {
        return 0;
    }

    @Override
    public long getSystemMemNonPaged() {
        return 0;
    }

    @Override
    public long getSystemMemPaged() {
        return 0;
    }

    @Override
    public long getSystemMemShared() {
        return 0;
    }

    @Override
    public long getSystemMemSlab() {
        return 0;
    }

    @Override
    public long getSystemMemTotal() {
        return 0;
    }

    @Override
    public long getSystemMemFree() {
        return 0;
    }

    @Override
    public long getSystemMemUsed() {
        return 0;
    }

    @Override
    public long getSystemMemBuffered() {
        return 0;
    }

    @Override
    public long getSystemMemCached() {
        return 0;
    }

    @Override
    public double getSystemMemFreePercent() {
        return 0;
    }

    @Override
    public double getSystemMemUsedPercent() {
        return 0;
    }

    @Override
    public long getSystemSwapCached() {
        return 0;
    }

    @Override
    public long getSystemSwapFree() {
        return 0;
    }

    @Override
    public double getSystemSwapFreePercent() {
        return 0;
    }

    @Override
    public long getSystemSwapTotal() {
        return 0;
    }

    @Override
    public long getSystemSwapUsed() {
        return 0;
    }

    @Override
    public double getSystemSwapUsedPercent() {
        return 0;
    }

    @Override
    public int getSystemDisks() {
        return 0;
    }

    @Override
    public List<DiskInfo> getSystemDiskInfoList() {
        return null;
    }

    @Override
    public int getSystemFilesMax() {
        return 0;
    }

    @Override
    public int getSystemFilesAllocated() {
        return 0;
    }

    @Override
    public int getSystemFilesLeft() {
        return 0;
    }

    @Override
    public double getSystemFilesUsedPercent() {
        return 0;
    }

    @Override
    public int getSystemFilesUsed() {
        return 0;
    }

    @Override
    public int getSystemFilesNotUsed() {
        return 0;
    }

    @Override
    public int getSystemNetCards() {
        return 0;
    }

    @Override
    public List<NetCardInfo> getSystemNetCardInfoList() {
        return null;
    }

    @Override
    public PeriodStatistics<Long> getSystemNetworkReceiveBytesPs() {
        return null;
    }

    @Override
    public PeriodStatistics<Long> getSystemNetworkSendBytesPs() {
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
}
