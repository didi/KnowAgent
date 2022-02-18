package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;
import com.didichuxing.datachannel.system.metrcis.bean.ProcMetrics;
import com.didichuxing.datachannel.system.metrcis.service.ProcessMetricsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.*;

/**
 * 获取进程级指标
 * 包括按需获取指标数据和一次性获取所有指标数据
 * @author Ronaldo
 * @Date 2021/11/3
 */
public class LinuxProcessMetricsServiceImpl implements ProcessMetricsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxProcessMetricsServiceImpl.class);

    /**
     * 当前agent进程id
     */
    private final Long PID;

    private LinuxCpuTime lastLinuxCpuTime;

    private LinuxNetFlow lastLinuxNetFlow;

    /**
     * agent宿主机cpu核（逻辑核）
     */
    private final int CPU_NUM;

    public LinuxProcessMetricsServiceImpl() {
        PID = initializePid();
        CPU_NUM = Runtime.getRuntime().availableProcessors();
        try {
            lastLinuxCpuTime = new LinuxCpuTime(getProcessPid(), getSystemCpuNumCores());// 记录上次的cpu耗时
        } catch (Exception e) {
            LOGGER.error("class=LinuxProcessMetricsServiceImpl||method=LinuxProcessMetricsServiceImpl()||msg=CpuTime init failed",
                    e);
        }
        try {
            lastLinuxNetFlow = new LinuxNetFlow(getProcessPid());// 记录上次的收发字节数
        } catch (Exception e) {
            LOGGER.error("class=LinuxProcessMetricsServiceImpl||method=LinuxProcessMetricsServiceImpl()||msg=NetFlow init failed",
                    e);
        }
    }

    @Override
    public Long getProcessStartupTime() {
        try {
            return ManagementFactory.getRuntimeMXBean().getStartTime();
        } catch (Exception ex) {
            LOGGER.error("class=LinuxProcessMetricsServiceImpl||method=getProcessStartupTime()||msg=failed to get process startup time", ex);
            return 0L;
        }
    }

    @Override
    public Long getProcUptime() {
        return null;
    }

    @Override
    public Long getProcessPid() {
        return PID;
    }

    @Override
    public Integer getSystemCpuNumCores() {
        return CPU_NUM;
    }

    @Override
    public PeriodStatistics getProcCpuUtil() {
        try {
            LinuxCpuTime curLinuxCpuTime = new LinuxCpuTime(getProcessPid(), getSystemCpuNumCores());
            float cpuUsage = curLinuxCpuTime.getUsage(lastLinuxCpuTime);
            lastLinuxCpuTime = curLinuxCpuTime;
            PeriodStatistics periodStatistics = new PeriodStatistics();
            periodStatistics.setLast(Float.valueOf(cpuUsage).doubleValue());

            //TODO：

            periodStatistics.setMin(Float.valueOf(cpuUsage).doubleValue());
            periodStatistics.setMax(Float.valueOf(cpuUsage).doubleValue());
            periodStatistics.setAvg(Float.valueOf(cpuUsage).doubleValue());
            periodStatistics.setStdDev(Float.valueOf(cpuUsage).doubleValue());
            periodStatistics.setQuantile55(Float.valueOf(cpuUsage).doubleValue());
            periodStatistics.setQuantile75(Float.valueOf(cpuUsage).doubleValue());
            periodStatistics.setQuantile95(Float.valueOf(cpuUsage).doubleValue());
            periodStatistics.setQuantile99(Float.valueOf(cpuUsage).doubleValue());
            return periodStatistics;
        } catch (Exception e) {
            LOGGER.error("class=LinuxProcessMetricsServiceImpl||method=getProcCpuUtil||msg=current process's cpu usage get failed",
                    e);
            return PeriodStatistics.defaultValue();
        }
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
        return getJvmProcHeapMemoryUsed() + getJvmProcNonHeapMemoryUsed();
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
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            return memoryMXBean.getHeapMemoryUsage().getUsed();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[当前进程堆内内存使用量]失败", ex);
            return 0L;
        }
    }

    @Override
    public Long getJvmProcNonHeapMemoryUsed() {
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            return memoryMXBean.getNonHeapMemoryUsage().getUsed();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[当前进程堆外内存使用量]失败", ex);
            return 0L;
        }
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
        long gcCounts = 0L;
        for (GarbageCollectorMXBean garbageCollector : ManagementFactory
                .getGarbageCollectorMXBeans()) {
            String name = garbageCollector.getName();
            if (StringUtils.isNotBlank(name) && name.contains("MarkSweep")) {
                gcCounts += garbageCollector.getCollectionCount();
            }
        }
        return gcCounts;
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
        List<String> lines = getOutputByCmd("ls /proc/%d/fd | wc -l", "jvm进程当前fd使用数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcOpenFdCount()||msg=data is null");
            return 0;
        }
    }

    @Override
    public List<Integer> getProcPortListen() {
        return null;
    }

    @Override
    public PeriodStatistics getProcNetworkReceiveBytesPs() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow(getProcessPid());
            double processReceiveBytesPs = curLinuxNetFlow.getProcessReceiveBytesPs(lastLinuxNetFlow);
            lastLinuxNetFlow = curLinuxNetFlow;
            PeriodStatistics periodStatistics = new PeriodStatistics();
            periodStatistics.setLast(processReceiveBytesPs);

            //TODO：

            periodStatistics.setMin(processReceiveBytesPs);
            periodStatistics.setMax(processReceiveBytesPs);
            periodStatistics.setAvg(processReceiveBytesPs);
            periodStatistics.setStdDev(processReceiveBytesPs);
            periodStatistics.setQuantile55(processReceiveBytesPs);
            periodStatistics.setQuantile75(processReceiveBytesPs);
            periodStatistics.setQuantile95(processReceiveBytesPs);
            periodStatistics.setQuantile99(processReceiveBytesPs);

            return periodStatistics;

        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=getProcNetworkReceiveBytesPs||msg=data is null",
                    e);
            return PeriodStatistics.defaultValue();
        }
    }

    @Override
    public PeriodStatistics getProcNetworkSendBytesPs() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow(getProcessPid());
            double processTransmitBytesPs = curLinuxNetFlow.getProcessTransmitBytesPs(lastLinuxNetFlow);
            lastLinuxNetFlow = curLinuxNetFlow;
            PeriodStatistics periodStatistics = new PeriodStatistics();
            periodStatistics.setLast(processTransmitBytesPs);

            //TODO：
            periodStatistics.setMin(processTransmitBytesPs);
            periodStatistics.setMax(processTransmitBytesPs);
            periodStatistics.setAvg(processTransmitBytesPs);
            periodStatistics.setStdDev(processTransmitBytesPs);
            periodStatistics.setQuantile55(processTransmitBytesPs);
            periodStatistics.setQuantile75(processTransmitBytesPs);
            periodStatistics.setQuantile95(processTransmitBytesPs);
            periodStatistics.setQuantile99(processTransmitBytesPs);

            return periodStatistics;

        } catch (Exception e) {
            LOGGER.error("class=LinuxProcessMetricsServiceImpl||method=getProcNetworkSendBytesPs||msg=data is null",
                    e);
            return PeriodStatistics.defaultValue();
        }
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

    /**
     * @return 返回当前 agent 进程 id
     */
    private long initializePid() {
        final String name = ManagementFactory.getRuntimeMXBean().getName();
        try {
            return Long.parseLong(name.split("@")[0]);
        } catch (final NumberFormatException e) {
            LOGGER.warn(String.format("failed parsing PID from [{}]", name), e);
            return -1;
        }
    }

    /**
     * linux 根据shell命令获取系统或者进程资源
     * @param procFDShell shell命令
     * @param resourceMessage    资源描述信息
     * @return
     */
    private List<String> getOutputByCmd(String procFDShell, String resourceMessage) {
        Process process = null;
        BufferedReader br = null;
        List<String> lines = new ArrayList<>();
        try {
            procFDShell = String.format(procFDShell, PID);
            String[] cmd = new String[] { "sh", "-c", procFDShell };
            process = Runtime.getRuntime().exec(cmd);
            int resultCode = process.waitFor();
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                lines.add(line.trim());
            }
            return lines;
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[{}]失败", resourceMessage, ex);
            return Collections.emptyList();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception ex) {
                LOGGER.error("获取系统资源项[{}]失败，原因为关闭执行获取{}的脚本进程对应输入流失败", resourceMessage, resourceMessage, ex);
            }
            try {
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception ex) {
                LOGGER.error("获取系统资源项[{}]失败，原因为关闭执行获取{}的脚本进程失败", resourceMessage, resourceMessage, ex);
            }
        }
    }

}
