package com.didichuxing.datachannel.system.metrcis.service.macos;

import com.didichuxing.datachannel.system.metrcis.bean.*;
import com.didichuxing.datachannel.system.metrcis.service.SystemMetricsService;

import java.util.ArrayList;
import java.util.List;

public class MacOSSystemMetricsServiceImpl implements SystemMetricsService {

    @Override
    public String getOsType() {
        return null;
    }

    @Override
    public String getOsVersion() {
        return null;
    }

    @Override
    public String getOsKernelVersion() {
        return null;
    }

    @Override
    public String getHostName() {
        return "";
    }

    @Override
    public String getIps() {
        return null;
    }

    @Override
    public Long getSystemNtpOffset() {
        return 100L;
    }

    @Override
    public Long getSystemStartupTime() {
        return System.currentTimeMillis();
    }

    @Override
    public Long getSystemUptime() {
        return System.currentTimeMillis();
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
        return 4;
    }

    @Override
    public PeriodStatistics getSystemCpuUtil() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public PeriodStatistics getSystemCpuUtilTotalPercent() {
        return PeriodStatistics.defaultValue();
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
        return 100 * 1024 * 1024 * 1024L;
    }

    @Override
    public Long getSystemMemFree() {
        return 39 * 1024 * 1024 * 1024L;
    }

    @Override
    public Long getSystemMemUsed() {
        return 50 * 1024 * 1024 * 1024L;
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
        return 39d;
    }

    @Override
    public Double getSystemMemUsedPercent() {
        return 20d;
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
        return 4;
    }

    @Override
    public List<DiskInfo> getSystemDiskInfoList() {
        List<DiskInfo> diskInfoList = new ArrayList<>();
        DiskInfo diskInfo = new DiskInfo();
        diskInfo.setFsType("ext3");
        diskInfo.setPath("/");
        diskInfo.setBytesFree(999 * 1024 * 1024 * 1024L);
        diskInfoList.add(diskInfo);

        return diskInfoList;
    }

    @Override
    public List<DiskIOInfo> getSystemDiskIOInfoList() {
        List<DiskIOInfo> diskIOInfoList = new ArrayList<>();
        DiskIOInfo diskIOInfo = new DiskIOInfo();
        diskIOInfo.setDevice("vda");
        diskIOInfo.setiOUtil(PeriodStatistics.defaultValue());
        diskIOInfoList.add(diskIOInfo);

        return diskIOInfoList;
    }

    @Override
    public Integer getSystemFilesMax() {
        return 65535;
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
        return 10;
    }

    @Override
    public Double getSystemNetCardsBandWidth() {
        return null;
    }

    @Override
    public List<NetCardInfo> getSystemNetCardInfoList() {
        List<NetCardInfo> netCardInfoList = new ArrayList<>();

        NetCardInfo netCardInfo = new NetCardInfo();
        netCardInfo.setSystemNetCardsBandDevice("en0");
        netCardInfo.setSystemNetCardsBandMacAddress("88:66:5a:3d:4d:42");
        netCardInfo.setSystemNetCardsBandWidth(87 * 1024 * 1024L);
        netCardInfo.setSystemNetCardsSendBytesPs(PeriodStatistics.defaultValue());
        netCardInfoList.add(netCardInfo);

        return netCardInfoList;
    }

    @Override
    public PeriodStatistics getSystemNetworkReceiveBytesPs() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public PeriodStatistics getSystemNetworkSendBytesPs() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public PeriodStatistics getSystemNetworkSendAndReceiveBytesPs() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public PeriodStatistics getSystemNetWorkBandWidthUsedPercent() {
        return PeriodStatistics.defaultValue();
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

}
