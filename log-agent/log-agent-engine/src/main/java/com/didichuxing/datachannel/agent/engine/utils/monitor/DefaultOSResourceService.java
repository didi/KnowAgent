package com.didichuxing.datachannel.agent.engine.utils.monitor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.*;
import java.lang.reflect.Method;

/**
 * 默认系统资源服务
 */
public class DefaultOSResourceService implements IOSResourceService {

    private static final Logger         LOGGER   = LoggerFactory.getLogger(DefaultOSResourceService.class);

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
    /**
     * 用于获取agent宿主机最大fd数量方法，供反射调用
     */
    private final Method                getMaxFileDescriptorCountField;
    /**
     * 用于获取agent宿主机已使用fd数量方法，供反射调用
     */
    private final Method                getOpenFileDescriptorCountField;
    private final Method                getProcessCpuLoad;

    public DefaultOSResourceService() {
        PID = initializePid();
        CPU_NUM = Runtime.getRuntime().availableProcessors();
        getMaxFileDescriptorCountField = getUnixMethod("getMaxFileDescriptorCount");
        getOpenFileDescriptorCountField = getUnixMethod("getOpenFileDescriptorCount");
        getProcessCpuLoad = getMethod("getProcessCpuLoad");
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
     * Returns a given method of the UnixOperatingSystemMXBean,
     * or null if the method is not found or unavailable.
     */
    private Method getUnixMethod(String methodName) {
        try {
            return Class.forName("com.sun.management.UnixOperatingSystemMXBean").getMethod(methodName);
        } catch (Exception t) {
            // not available
            return null;
        }
    }

    /**
     * Returns a given method of the OperatingSystemMXBean,
     * or null if the method is not found or unavailable.
     */
    private Method getMethod(String methodName) {
        try {
            return Class.forName("com.sun.management.OperatingSystemMXBean").getMethod(methodName);
        } catch (Exception t) {
            // not available
            return null;
        }
    }

    /**
     * invoke the method given use OperatingSystemMXBean
     */
    private <T> T invoke(Method method, OperatingSystemMXBean osMxBean) {
        if (method != null) {
            try {
                T t = (T) method.invoke(osMxBean);
                return t;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public long getPid() {
        return PID;
    }

    @Override
    public float getCurrentProcessCpuUsage() {
        float currentCpuUsageTotalPercent = getCurrentProcessCpuUsageTotalPercent();
        float currentCpuUsage = currentCpuUsageTotalPercent * CPU_NUM;
        return currentCpuUsage;
    }

    @Override
    public float getCurrentProcessCpuUsageTotalPercent() {
        Float currentCpuUsageTotalPercent = invoke(getProcessCpuLoad, osMxBean);
        return null != currentCpuUsageTotalPercent ? currentCpuUsageTotalPercent.floatValue() * 100 : 0;
    }

    @Override
    public float getCurrentSystemCpuUsage() {
        return 0;
    }

    @Override
    public float getCurrentSystemCpuUsageTotalPercent() {
        return 0;
    }

    @Override
    public float getCurrentSystemCpuLoad() {
        return 0;
    }

    @Override
    public int getCpuNum() {
        return CPU_NUM;
    }

    @Override
    public long getCurrentProcessMemoryUsed() {
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
    public long getCurrentProcessHeapMemoryUsed() {
        return 0;
    }

    @Override
    public long getCurrentProcessNonHeapMemoryUsed() {
        return 0;
    }

    @Override
    public long getCurrentProcessMaxHeapSize() {
        return 0;
    }

    @Override
    public long getCurrentSystemMemoryFree() {
        return 0;
    }

    @Override
    public long getSystemMemoryTotal() {
        return 0;
    }

    @Override
    public long getSystemMemoryUsed() {
        return 0;
    }

    @Override
    public long getSystemMemorySwapSize() {
        return 0;
    }

    @Override
    public long getSystemMemorySwapUsed() {
        return 0;
    }

    @Override
    public long getProcessMemoryUsedPeak() {
        return 0;
    }

    @Override
    public long getSystemDiskTotal() {
        return 0;
    }

    @Override
    public long getSystemDiskUsed() {
        return 0;
    }

    @Override
    public long getSystemDiskFree() {
        return 0;
    }

    @Override
    public long getSystemDiskFreeMin() {
        return 0;
    }

    @Override
    public int getSystemDiskNum() {
        return 0;
    }

    @Override
    public long getYoungGcCount() {
        return 0;
    }

    @Override
    public long getFullGcCount() {
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
    public long getYoungGcTime() {
        return 0;
    }

    @Override
    public long getFullGcTime() {
        return 0;
    }

    @Override
    public int getCurrentProcessFdUsed() {
        if (osMxBean.getName().toLowerCase().contains("aix")) {
            Process process = null;
            BufferedReader br = null;
            String procFDShell = "svmon -P $pid | wc -l";
            try {
                procFDShell = procFDShell.replaceAll("\\$pid", PID + "");
                String[] cmd = new String[] { "/bin/sh", "-c", procFDShell };
                process = Runtime.getRuntime().exec(cmd);
                int resultCode = process.waitFor();
                br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = null;
                while ((line = br.readLine()) != null) {
                    int fdThreshold = Integer.parseInt(line.trim());
                    return fdThreshold;
                }
            } catch (Exception ex) {
                LOGGER.error("获取系统资源项[文件句柄数使用率]失败", ex);
                return 0;
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (Exception ex) {
                    LOGGER.error("获取系统资源项[文件句柄数使用率]失败，原因为关闭执行获取文件句柄数的脚本进程对应输入流失败", ex);
                }
                try {
                    if (process != null) {
                        process.destroy();
                    }
                } catch (Exception ex) {
                    LOGGER.error("获取系统资源项[文件句柄数使用率]失败，原因为关闭执行获取文件句柄数的脚本进程失败", ex);
                }
            }
            LOGGER.error("获取系统资源项[文件句柄数使用率]失败");
            return 0;
        } else {
            //linux 获取 fd 使用量
            Process process = null;
            BufferedReader br = null;
            String procFDShell = "ls /proc/%d/fd | wc -l";
            try {
                procFDShell = String.format(procFDShell, PID);
                String[] cmd = new String[] { "sh", "-c", procFDShell };
                process = Runtime.getRuntime().exec(cmd);
                int resultCode = process.waitFor();
                br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = null;
                while ((line = br.readLine()) != null) {
                    int fdNum = Integer.parseInt(line.trim());
                    return fdNum;
                }
            } catch (Exception ex) {
                LOGGER.error("获取系统资源项[文件句柄数使用率]失败", ex);
                return 0;
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (Exception ex) {
                    LOGGER.error("获取系统资源项[文件句柄数使用率]失败，原因为关闭执行获取文件句柄数的脚本进程对应输入流失败", ex);
                }
                try {
                    if (process != null) {
                        process.destroy();
                    }
                } catch (Exception ex) {
                    LOGGER.error("获取系统资源项[文件句柄数使用率]失败，原因为关闭执行获取文件句柄数的脚本进程失败", ex);
                }
            }
            LOGGER.error("获取系统资源项[文件句柄数使用率]失败");
            return 0;
        }
    }

    @Override
    public int getSystemMaxFdSize() {
        return 0;
    }

    @Override
    public int getCurrentSystemFdUsed() {
        return 0;
    }

    @Override
    public int getCurrentProcessThreadNum() {
        return 0;
    }

    @Override
    public int getCurrentProcessThreadNumPeak() {
        return 0;
    }

    @Override
    public float getCurrentSystemDiskIOUsagePercent() {
        return 0;
    }

    @Override
    public float getCurrentProcessDiskIOAwaitTimePercent() {
        return 0;
    }

    @Override
    public int getCurrentSystemIOPS() {
        return 0;
    }

    @Override
    public long getCurrentSystemDiskIOReadBytesPS() {
        return 0;
    }

    @Override
    public long getCurrentSystemDiskIOWriteBytesPS() {
        return 0;
    }

    @Override
    public long getCurrentProcessDiskIOReadBytesPS() {
        return 0;
    }

    @Override
    public long getCurrentProcessDiskIOWriteBytesPS() {
        return 0;
    }

    @Override
    public long getCurrentSystemDiskIOResponseTimeAvg() {
        return 0;
    }

    @Override
    public long getCurrentSystemDiskIOProcessTimeAvg() {
        return 0;
    }

    @Override
    public long getCurrentSystemNetworkReceiveBytesPS() {
        return 0;
    }

    @Override
    public long getCurrentSystemNetworkSendBytesPS() {
        return 0;
    }

    @Override
    public long getCurrentProcessNetworkReceiveBytesPS() {
        return 0;
    }

    @Override
    public long getCurrentProcessNetworkSendBytesPS() {
        return 0;
    }

    @Override
    public int getCurrentSystemNetworkTcpConnectionNum() {
        return 0;
    }

    @Override
    public int getCurrentProcessNetworkTcpConnectionNum() {
        return 0;
    }

    @Override
    public int getCurrentSystemNetworkTcpTimeWaitNum() {
        return 0;
    }

    @Override
    public int getCurrentProcessNetworkTcpTimeWaitNum() {
        return 0;
    }

    @Override
    public int getCurrentSystemNetworkTcpCloseWaitNum() {
        return 0;
    }

    @Override
    public int getCurrentProcessNetworkTcpCloseWaitNum() {
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
}
