package com.didichuxing.datachannel.system.metrcis.service.macos;

import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;
import com.didichuxing.datachannel.system.metrcis.bean.ProcMetrics;
import com.didichuxing.datachannel.system.metrcis.service.ProcessMetricsService;

import java.util.List;
import java.util.Random;

public class MacOSProcessMetricsServiceImpl implements ProcessMetricsService {
    @Override
    public Long getProcessStartupTime() {
        return System.currentTimeMillis();
    }

    @Override
    public Long getProcUptime() {
        return System.currentTimeMillis();
    }

    @Override
    public Long getProcessPid() {
        return new Random().nextLong();
    }

    @Override
    public PeriodStatistics getProcCpuUtil() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public Double getCurrentProcCpuUtil() {
        return 20d;
    }

    @Override
    public PeriodStatistics getProcCpuUtilTotalPercent() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public PeriodStatistics getProcCpuSys() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public PeriodStatistics getProcCpuUser() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public PeriodStatistics getProcCpuSwitchesPS() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public PeriodStatistics getProcCpuVoluntarySwitchesPS() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public PeriodStatistics getProcCpuNonVoluntarySwitchesPS() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public Long getProcMemUsed() {
        return 1 * 1024 * 1024 * 1024L;
    }

    @Override
    public Double getProcMemUtil() {
        return 10d;
    }

    @Override
    public Long getProcMemData() {
        return null;
    }

    @Override
    public Long getProcMemDirty() {
        return null;
    }

    @Override
    public Long getProcMemLib() {
        return null;
    }

    @Override
    public Long getProcMemRss() {
        return null;
    }

    @Override
    public Long getProcMemShared() {
        return null;
    }

    @Override
    public Long getProcMemSwap() {
        return null;
    }

    @Override
    public Long getProcMemText() {
        return null;
    }

    @Override
    public Long getProcMemVms() {
        return null;
    }

    @Override
    public Long getJvmProcHeapMemoryUsed() {
        return 500 * 1024 * 1024L;
    }

    @Override
    public Long getJvmProcNonHeapMemoryUsed() {
        return 500 * 1024 * 1024L;
    }

    @Override
    public Long getJvmProcHeapSizeXmx() {
        return 2 * 1024 * 1024 * 1024L;
    }

    @Override
    public Long getJvmProcMemUsedPeak() {
        return 1 * 1024 * 1024 * 1024L;
    }

    @Override
    public Double getJvmProcHeapMemUsedPercent() {
        return null;
    }

    @Override
    public PeriodStatistics getProcIOReadRate() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public PeriodStatistics getProcIOReadBytesRate() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public PeriodStatistics getProcIOWriteRate() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public PeriodStatistics getProcIOWriteBytesRate() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public PeriodStatistics getProcIOReadWriteRate() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public PeriodStatistics getProcIOReadWriteBytesRate() {
        return null;
    }

    @Override
    public PeriodStatistics getProcIOAwaitTimePercent() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public Long getJvmProcYoungGcCount() {
        return 10L;
    }

    @Override
    public Long getJvmProcFullGcCount() {
        return 5L;
    }

    @Override
    public Long getJvmProcYoungGcTime() {
        return 10000L;
    }

    @Override
    public Long getJvmProcFullGcTime() {
        return 100000L;
    }

    @Override
    public Double getJvmProcS0C() {
        return null;
    }

    @Override
    public Double getJvmProcS1C() {
        return null;
    }

    @Override
    public Double getJvmProcS0U() {
        return null;
    }

    @Override
    public Double getJvmProcS1U() {
        return null;
    }

    @Override
    public Double getJvmProcEC() {
        return null;
    }

    @Override
    public Double getJvmProcEU() {
        return null;
    }

    @Override
    public Double getJvmProcOC() {
        return null;
    }

    @Override
    public Double getJvmProcOU() {
        return null;
    }

    @Override
    public Double getJvmProcMC() {
        return null;
    }

    @Override
    public Double getJvmProcMU() {
        return null;
    }

    @Override
    public Double getJvmProcCCSC() {
        return null;
    }

    @Override
    public Double getJvmProcCCSU() {
        return null;
    }

    @Override
    public Integer getJvmProcThreadNum() {
        return 5;
    }

    @Override
    public Integer getJvmProcThreadNumPeak() {
        return 100;
    }

    @Override
    public Integer getProcOpenFdCount() {
        return 999;
    }

    @Override
    public List<Integer> getProcPortListen() {
        return null;
    }

    @Override
    public PeriodStatistics getProcNetworkReceiveBytesPs() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public PeriodStatistics getProcNetworkSendBytesPs() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public PeriodStatistics getProcNetworkConnRate() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public Integer getProcNetworkTcpConnectionNum() {
        return null;
    }

    @Override
    public Integer getProcNetworkTcpListeningNum() {
        return null;
    }

    @Override
    public Integer getProcNetworkTcpEstablishedNum() {
        return null;
    }

    @Override
    public Integer getProcNetworkTcpSynSentNum() {
        return null;
    }

    @Override
    public Integer getProcNetworkTcpSynRecvNum() {
        return null;
    }

    @Override
    public Integer getProcNetworkTcpFinWait1Num() {
        return null;
    }

    @Override
    public Integer getProcNetworkTcpFinWait2Num() {
        return null;
    }

    @Override
    public Integer getProcNetworkTcpTimeWaitNum() {
        return null;
    }

    @Override
    public Integer getProcNetworkTcpClosedNum() {
        return null;
    }

    @Override
    public Integer getProcNetworkTcpCloseWaitNum() {
        return null;
    }

    @Override
    public Integer getProcNetworkTcpClosingNum() {
        return null;
    }

    @Override
    public Integer getProcNetworkTcpLastAckNum() {
        return null;
    }

    @Override
    public Integer getProcNetworkTcpNoneNum() {
        return null;
    }

}
