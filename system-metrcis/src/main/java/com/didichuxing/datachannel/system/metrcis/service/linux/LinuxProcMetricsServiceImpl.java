package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;
import com.didichuxing.datachannel.system.metrcis.bean.ProcMetrics;
import com.didichuxing.datachannel.system.metrcis.service.ProcMetricsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.*;

/**
 * 获取进程级指标
 * 包括按需获取指标数据和一次性获取所有指标数据
 * @author Ronaldo
 * @Date 2021/11/3
 */
public class LinuxProcMetricsServiceImpl implements ProcMetricsService {

    @Override
    public Long getProcStartupTime() {
        return null;
    }

    @Override
    public Long getProcUptime() {
        return null;
    }

    @Override
    public Long getProcPid() {
        return null;
    }

    @Override
    public Integer getSystemCpuNumCores() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getProcCpuUtil() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getProcCpuUtilTotalPercent() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getProcCpuSys() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getProcCpuUser() {
        return null;
    }

    @Override
    public PeriodStatistics<Long> getProcCpuSwitchesPS() {
        return null;
    }

    @Override
    public PeriodStatistics<Long> getProcCpuVoluntarySwitchesPS() {
        return null;
    }

    @Override
    public PeriodStatistics<Long> getProcCpuNonVoluntarySwitchesPS() {
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
    public PeriodStatistics<Double> getProcIOReadRate() {
        return null;
    }

    @Override
    public PeriodStatistics<Long> getProcIOReadBytesRate() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getProcIOWriteRate() {
        return null;
    }

    @Override
    public PeriodStatistics<Long> getProcIOWriteBytesRate() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getProcIOReadWriteRate() {
        return null;
    }

    @Override
    public PeriodStatistics<Double> getProcIOAwaitTimePercent() {
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
    public PeriodStatistics<Long> getProcNetworkReceiveBytesPs() {
        return null;
    }

    @Override
    public PeriodStatistics<Long> getProcNetworkSendBytesPs() {
        return null;
    }

    @Override
    public PeriodStatistics<Long> getProcNetworkConnRate() {
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
