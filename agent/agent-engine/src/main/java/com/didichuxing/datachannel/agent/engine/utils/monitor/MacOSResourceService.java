package com.didichuxing.datachannel.agent.engine.utils.monitor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 默认系统资源服务
 */
public class MacOSResourceService implements IOSResourceService {

    private static final Logger         LOGGER   = LoggerFactory
                                                     .getLogger(MacOSResourceService.class);

    /**
     * 当前agent进程id
     */
    private final long                  PID;
    /**
     * agent宿主机cpu核（逻辑核）
     */
    private final int                   CPU_NUM;
    /**
     * 用于获取操作系统相关属性bean
     */
    private final OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();

    public MacOSResourceService() {
        PID = initializePid();
        CPU_NUM = Runtime.getRuntime().availableProcessors();
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

    @Override
    public void clearCache() {

    }

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
    public double getSystemCpuUtil() {
        return 0;
    }

    @Override
    public double getSystemCpuUtilTotalPercent() {
        return 0;
    }

    @Override
    public long getSystemCpuSwitches() {
        return 0;
    }

    @Override
    public double getSystemCpuGuest() {
        return 0;
    }

    @Override
    public double getSystemCpuIdle() {
        return 0;
    }

    @Override
    public double getSystemCpuIOWait() {
        return 0;
    }

    @Override
    public int getSystemCpuNumCores() {
        return CPU_NUM;
    }

    @Override
    public double getSystemCpuSteal() {
        return 0;
    }

    @Override
    public double getSystemCpuSystem() {
        return 0;
    }

    @Override
    public double getSystemCpuUser() {
        return 0;
    }

    @Override
    public Map<String, Long> getSystemDiskBytesFree() {
        return null;
    }

    @Override
    public Map<String, Double> getSystemDiskUsedPercent() {
        return null;
    }

    @Override
    public Map<String, Long> getSystemDiskReadTime() {
        return null;
    }

    @Override
    public double getSystemDiskReadTimePercent() {
        return 0;
    }

    @Override
    public Map<String, Long> getSystemDiskBytesTotal() {
        return null;
    }

    @Override
    public Map<String, Long> getSystemDiskBytesUsed() {
        return null;
    }

    @Override
    public Map<String, Long> getSystemDiskWriteTime() {
        return null;
    }

    @Override
    public double getSystemDiskWriteTimePercent() {
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
    public int getSystemFilesMax() {
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
    public Map<String, Long> getSystemDiskInodesFree() {
        return null;
    }

    @Override
    public Map<String, Double> getSystemDiskInodesUsedPercent() {
        return null;
    }

    @Override
    public Map<String, Long> getSystemDiskInodesTotal() {
        return null;
    }

    @Override
    public Map<String, Long> getSystemDiskInodesUsed() {
        return null;
    }

    @Override
    public Map<String, Long> getSystemIOAvgQuSz() {
        return null;
    }

    @Override
    public Map<String, Long> getSystemIOAvgRqSz() {
        return null;
    }

    @Override
    public Map<String, Long> getSystemIOAwait() {
        return null;
    }

    @Override
    public Map<String, Long> getSystemIORAwait() {
        return null;
    }

    @Override
    public Map<String, Long> getSystemIOReadRequest() {
        return null;
    }

    @Override
    public Map<String, Long> getSystemIOReadBytes() {
        return null;
    }

    @Override
    public Map<String, Long> getSystemIORRQMS() {
        return null;
    }

    @Override
    public Map<String, Long> getSystemIOSVCTM() {
        return null;
    }

    @Override
    public Map<String, Double> getSystemIOUtil() {
        return null;
    }

    @Override
    public Map<String, Long> getSystemIOWAwait() {
        return null;
    }

    @Override
    public Map<String, Long> getSystemIOWriteRequest() {
        return null;
    }

    @Override
    public Map<String, Long> getSystemIOWriteBytes() {
        return null;
    }

    @Override
    public Map<String, Long> getSystemIOWRQMS() {
        return null;
    }

    @Override
    public double getSystemLoad1() {
        return 0;
    }

    @Override
    public double getSystemLoad5() {
        return 0;
    }

    @Override
    public double getSystemLoad15() {
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
    public long getSystemMemCommitLimit() {
        return 0;
    }

    @Override
    public long getSystemMemCommitted() {
        return 0;
    }

    @Override
    public long getSystemMemCommittedAs() {
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
    public double getSystemMemFreePercent() {
        return 0;
    }

    @Override
    public double getSystemMemUsedPercent() {
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
    public long getSystemNetworkReceiveBytesPs() {
        return 0;
    }

    @Override
    public long getSystemNetworkSendBytesPs() {
        return 0;
    }

    @Override
    public long getProcNetworkReceiveBytesPs() {
        return 0;
    }

    @Override
    public long getProcNetworkSendBytesPs() {
        return 0;
    }

    @Override
    public int getSystemNetworkTcpConnectionNum() {
        return 0;
    }

    @Override
    public int getProcNetworkTcpConnectionNum() {
        return 0;
    }

    @Override
    public int getSystemNetworkTcpTimeWaitNum() {
        return 0;
    }

    @Override
    public int getProcNetworkTcpTimeWaitNum() {
        return 0;
    }

    @Override
    public int getSystemNetworkTcpCloseWaitNum() {
        return 0;
    }

    @Override
    public int getProcNetworkTcpCloseWaitNum() {
        return 0;
    }

    @Override
    public long getSystemNetworkTcpActiveOpens() {
        return 0;
    }

    @Override
    public long getSystemNetworkTcpPassiveOpens() {
        return 0;
    }

    @Override
    public long getSystemNetworkTcpAttemptFails() {
        return 0;
    }

    @Override
    public long getSystemNetworkTcpEstabResets() {
        return 0;
    }

    @Override
    public long getSystemNetworkTcpRetransSegs() {
        return 0;
    }

    @Override
    public long getSystemNetworkTcpExtListenOverflows() {
        return 0;
    }

    @Override
    public long getSystemNetworkUdpInDatagrams() {
        return 0;
    }

    @Override
    public long getSystemNetworkUdpOutDatagrams() {
        return 0;
    }

    @Override
    public long getSystemNetworkUdpInErrors() {
        return 0;
    }

    @Override
    public long getSystemNetworkUdpNoPorts() {
        return 0;
    }

    @Override
    public long getSystemNetworkUdpSendBufferErrors() {
        return 0;
    }

    @Override
    public long getProcStartupTime() {
        return 0;
    }

    @Override
    public long getProcUptime() {
        return 0;
    }

    @Override
    public long getProcPid() {
        return PID;
    }

    @Override
    public double getProcCpuSys() {
        return 0;
    }

    @Override
    public long getProcCpuSwitchesPS() {
        return 0;
    }

    @Override
    public long getProcCpuVoluntarySwitchesPS() {
        return 0;
    }

    @Override
    public long getProcCpuNonVoluntarySwitchesPS() {
        return 0;
    }

    @Override
    public float getProcCpuUtil() {
        Process process = null;
        BufferedReader br = null;
        String procCpuShell = String.format("top -pid %d", getProcPid());
        try {
            procCpuShell = String.format(procCpuShell, PID);
            String[] cmd = new String[] { "sh", "-c", procCpuShell };
            process = Runtime.getRuntime().exec(cmd);
            //            int resultCode = process.waitFor();
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int lineNum = 1;
            String line = null;
            while ((line = br.readLine()) != null) {
                if (13 == lineNum) {
                    String[] kpiArray = line.trim().split(" ");
                    if (null != kpiArray && kpiArray.length == 97) {
                        String cpuUsageStr = kpiArray[6];
                        try {
                            float cpuUsage = Float.valueOf(cpuUsageStr);
                            return cpuUsage * getSystemCpuNumCores();
                        } catch (Exception ex) {
                            LOGGER.error("获取系统资源项[cpu使用率]失败", ex);
                            return 0;
                        }
                    } else {
                        LOGGER.error("获取系统资源项[cpu使用率]失败");
                        return 0;
                    }
                }
                lineNum++;
            }
            return 0;
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[cpu使用率]失败", ex);
            return 0;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception ex) {
                LOGGER.error("获取系统资源项[cpu使用率]失败，原因为关闭执行获取文件句柄数的脚本进程对应输入流失败", ex);
            }
            try {
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception ex) {
                LOGGER.error("获取系统资源项[cpu使用率]失败，原因为关闭执行获取文件句柄数的脚本进程失败", ex);
            }
        }
    }

    @Override
    public float getProcCpuUtilTotalPercent() {
        return 0;
    }

    @Override
    public double getProcCpuUser() {
        return 0;
    }

    @Override
    public double getProcIOReadRate() {
        return 0;
    }

    @Override
    public long getProcIOReadBytesRate() {
        return 0;
    }

    @Override
    public double getProcIOWriteRate() {
        return 0;
    }

    @Override
    public long getProcIOWriteBytesRate() {
        return 0;
    }

    @Override
    public double getProcIOAwaitTimePercent() {
        return 0;
    }

    @Override
    public long getProcMemData() {
        return 0;
    }

    @Override
    public long getProcMemDirty() {
        return 0;
    }

    @Override
    public long getProcMemLib() {
        return 0;
    }

    @Override
    public long getProcMemRss() {
        return 0;
    }

    @Override
    public long getProcMemShared() {
        return 0;
    }

    @Override
    public long getProcMemSwap() {
        return 0;
    }

    @Override
    public long getProcMemText() {
        return 0;
    }

    @Override
    public long getProcMemUsed() {
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
            MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
            return heapMemoryUsage.getUsed() + nonHeapMemoryUsage.getUsed();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[当前进程内存使用量]失败", ex);
            return 0;
        }
    }

    @Override
    public double getProcMemUtil() {
        return 0;
    }

    @Override
    public long getProcMemVms() {
        return 0;
    }

    @Override
    public long getJvmProcHeapMemoryUsed() {
        return 0;
    }

    @Override
    public long getJvmProcNonHeapMemoryUsed() {
        return 0;
    }

    @Override
    public long getJvmProcHeapSizeXmx() {
        return 0;
    }

    @Override
    public long getJvmProcMemUsedPeak() {
        return 0;
    }

    @Override
    public long getJvmProcYoungGcCount() {
        return 0;
    }

    @Override
    public long getJvmProcFullGcCount() {
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
    public long getJvmProcYoungGcTime() {
        return 0;
    }

    @Override
    public long getJvmProcFullGcTime() {
        return 0;
    }

    @Override
    public int getJvmProcThreadNum() {
        return 0;
    }

    @Override
    public int getJvmProcThreadNumPeak() {
        return 0;
    }

    @Override
    public int getJvmProcNormalSourceThreadPoolMaxThreadNum() {
        return 0;
    }

    @Override
    public int getJvmProcNormalSourceThreadPoolThreadNum() {
        return 0;
    }

    @Override
    public int getJvmProcNormalThreadPoolMaxQueueSize() {
        return 0;
    }

    @Override
    public int getJvmProcNormalThreadPoolQueueSize() {
        return 0;
    }

    @Override
    public int getJvmProcTempSourceThreadPoolMaxThreadNum() {
        return 0;
    }

    @Override
    public int getJvmProcTempSourceThreadPoolThreadNum() {
        return 0;
    }

    @Override
    public int getJvmProcTempThreadPoolMaxQueueSize() {
        return 0;
    }

    @Override
    public int getJvmProcTempThreadPoolQueueSize() {
        return 0;
    }

    @Override
    public int getProcOpenFdCount() {
        Process process = null;
        BufferedReader br = null;
        String procCpuShell = String.format(
            "lsof -n|awk '{print $2}'|sort|uniq -c |sort -nr|grep %d", getProcPid());
        try {
            procCpuShell = String.format(procCpuShell, PID);
            String[] cmd = new String[] { "sh", "-c", procCpuShell };
            process = Runtime.getRuntime().exec(cmd);
            //            int resultCode = process.waitFor();
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] kpiArray = line.trim().split(" ");
                if (null != kpiArray && kpiArray.length == 2) {
                    String fdUsedStr = kpiArray[0];
                    try {
                        Integer fdUsed = Integer.valueOf(fdUsedStr);
                        return fdUsed;
                    } catch (Exception ex) {
                        LOGGER.error("获取系统资源项[fd使用量]失败", ex);
                        return 0;
                    }
                }
            }
            return 0;
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[cpu使用率]失败", ex);
            return 0;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception ex) {
                LOGGER.error("获取系统资源项[cpu使用率]失败，原因为关闭执行获取文件句柄数的脚本进程对应输入流失败", ex);
            }
            try {
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception ex) {
                LOGGER.error("获取系统资源项[cpu使用率]失败，原因为关闭执行获取文件句柄数的脚本进程失败", ex);
            }
        }
    }

    @Override
    public List<Integer> getProcPortListen() {
        return null;
    }
}
