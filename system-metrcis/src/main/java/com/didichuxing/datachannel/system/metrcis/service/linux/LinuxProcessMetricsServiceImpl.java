package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;
import com.didichuxing.datachannel.system.metrcis.bean.ProcMetrics;
import com.didichuxing.datachannel.system.metrcis.service.ProcessMetricsService;

import java.util.*;

/**
 * 获取进程级指标
 * 包括按需获取指标数据和一次性获取所有指标数据
 * @author Ronaldo
 * @Date 2021/11/3
 */
public class LinuxProcessMetricsServiceImpl implements ProcessMetricsService {

    @Override
    public Long getProcessStartupTime() {
        return null;
    }

    @Override
    public Long getProcUptime() {
        return null;
    }

    @Override
    public Long getProcessPid() {
        return null;
    }

    @Override
    public Integer getSystemCpuNumCores() {
        return null;
    }

    @Override
    public PeriodStatistics getProcCpuUtil() {
        return null;
    }

    @Override
    public PeriodStatistics getProcCpuUtilTotalPercent() {
        return null;
    }

    @Override
    public PeriodStatistics getProcCpuSys() {
        return null;
    }

    @Override
    public PeriodStatistics getProcCpuUser() {
        return null;
    }

    @Override
    public PeriodStatistics getProcCpuSwitchesPS() {
        return null;
    }

    @Override
    public PeriodStatistics getProcCpuVoluntarySwitchesPS() {
        return null;
    }

    @Override
    public PeriodStatistics getProcCpuNonVoluntarySwitchesPS() {
        return null;
    }

    @Override
    public Long getProcMemUsed() {
        return null;
    }

    @Override
    public Double getProcMemUtil() {
        return null;
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
        return null;
    }

    @Override
    public Long getJvmProcNonHeapMemoryUsed() {
        return null;
    }

    @Override
    public Long getJvmProcHeapSizeXmx() {
        return null;
    }

    @Override
    public Long getJvmProcMemUsedPeak() {
        return null;
    }

    @Override
    public PeriodStatistics getProcIOReadRate() {
        return null;
    }

    @Override
    public PeriodStatistics getProcIOReadBytesRate() {
        return null;
    }

    @Override
    public PeriodStatistics getProcIOWriteRate() {
        return null;
    }

    @Override
    public PeriodStatistics getProcIOWriteBytesRate() {
        return null;
    }

    @Override
    public PeriodStatistics getProcIOReadWriteRate() {
        return null;
    }

    @Override
    public PeriodStatistics getProcIOAwaitTimePercent() {
        return null;
    }

    @Override
    public Long getJvmProcYoungGcCount() {
        return null;
    }

    @Override
    public Long getJvmProcFullGcCount() {
        return null;
    }

    @Override
    public Long getJvmProcYoungGcTime() {
        return null;
    }

    @Override
    public Long getJvmProcFullGcTime() {
        return null;
    }

    @Override
    public Integer getJvmProcThreadNum() {
        return null;
    }

    @Override
    public Integer getJvmProcThreadNumPeak() {
        return null;
    }

    @Override
    public Integer getProcOpenFdCount() {
        return null;
    }

    @Override
    public List<Integer> getProcPortListen() {
        return null;
    }

    @Override
    public PeriodStatistics getProcNetworkReceiveBytesPs() {
        return null;
    }

    @Override
    public PeriodStatistics getProcNetworkSendBytesPs() {
        return null;
    }

    @Override
    public PeriodStatistics getProcNetworkConnRate() {
        return null;
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

    @Override
    public ProcMetrics getProcMetrics() {
        return null;
    }
}
