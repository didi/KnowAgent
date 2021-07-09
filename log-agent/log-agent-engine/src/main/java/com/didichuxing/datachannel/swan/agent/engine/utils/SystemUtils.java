package com.didichuxing.datachannel.swan.agent.engine.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.*;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by huangjw on 18/6/19.
 */
public class SystemUtils {

    private final static int             cpuNum;
    private final static String          pid;
    private final static String          startTime;
    private static long                  preGcCount = 0L;

    private static final Logger          LOGGER     = LoggerFactory.getLogger(SystemUtils.class);
    /**
     * 用于获取 OS 信息 bean
     */
    private static OperatingSystemMXBean osMxBean   = ManagementFactory.getOperatingSystemMXBean();

    static {
        cpuNum = Runtime.getRuntime().availableProcessors();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        pid = runtimeMXBean.getName().split("@")[0].trim();

        startTime = new Date().toString();
    }

    public static int getCpuNum() {
        return cpuNum;
    }

    public static String getPid() {
        return pid;
    }

    public static String getStartTime() {
        return startTime;
    }

    /**
     * 周期内gc次数
     *
     * @return
     */
    public static long getGcCount() {
        long curGcCount = getCurGcCount();
        long result = curGcCount - preGcCount;
        preGcCount = curGcCount;
        return result;
    }

    public static long getCurGcCount() {
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

    /**
     * @return 返回当前 log-agent 进程对应 fd 使用量
     */
    public static long getFdCount() {
        return getCurentFdUsage(Long.valueOf(pid));
    }

    /**
     * @return 返回当前采集端进程使用的文件句柄数，如获取出现异常 return 0
     */
    private static long getCurentFdUsage(long pid) {
        if (osMxBean.getName().toLowerCase().contains("aix")) {
            Process process = null;
            BufferedReader br = null;
            String procFDShell = "svmon -P $pid | wc -l";
            try {
                procFDShell = procFDShell.replaceAll("\\$pid", pid + "");
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
                procFDShell = String.format(procFDShell, pid);
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

    /**
     * 获取当前进程对应内存使用量 单位：byte
     * @return 返回获取到的当前进程对应内存使用量
     */
    public static long getCurrentMemoryUsage() {
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

}
