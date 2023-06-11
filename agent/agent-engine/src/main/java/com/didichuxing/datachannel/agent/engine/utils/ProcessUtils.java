package com.didichuxing.datachannel.agent.engine.utils;

import java.lang.management.ManagementFactory;
import java.util.Date;

import com.didichuxing.datachannel.agent.engine.utils.monitor.*;

import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by huangjw on 18/6/19.
 */
public class ProcessUtils {

    private static final Logger       LOGGER          = LoggerFactory.getLogger(ProcessUtils.class);

    private static ProcessUtils       instance        = new ProcessUtils();

    private static IOSResourceService osResourceService;

    private long                      preFullGcCount  = 0L;

    private long                      preYoungGcCount = 0L;
    private long                      preFullGcTime   = 0L;
    private long                      preYoungGcTime  = 0L;

    public static ProcessUtils getInstance() {
        return instance;
    }

    private ProcessUtils() {
        osResourceService = getOSResourceService();
        osResourceService.clearCache();
    }

    public int getCpuNum() {
        return osResourceService.getSystemCpuNumCores();
    }

    public String getPid() {
        return String.valueOf(osResourceService.getProcPid());
    }

    /*********************************** about agent process gc ***********************************/

    /**
     * @return 返回jvm周期内 young gc 次数
     */
    public long getYoungGcCount() {
        long curYoungGcCount = osResourceService.getJvmProcYoungGcCount();
        long result = curYoungGcCount - preYoungGcCount;
        preYoungGcCount = curYoungGcCount;
        return result;
    }

    /**
     * @return 返回 周期内 full gc 次数
     */
    public long getFullGcCount() {
        long curFullGcCount = osResourceService.getJvmProcFullGcCount();
        long result = curFullGcCount - preFullGcCount;
        preFullGcCount = curFullGcCount;
        return result;
    }

    /**
     * @return 返回jvm周期内 young gc 耗时
     */
    public long getYoungGcTime() {
        long curYoungGcTime = osResourceService.getJvmProcYoungGcTime();
        long result = curYoungGcTime - preYoungGcTime;
        preYoungGcTime = curYoungGcTime;
        return result;
    }

    /**
     * @return 返回 周期内 full gc 耗时
     */
    public long getFullGcTime() {
        long curFullGcTime = osResourceService.getJvmProcFullGcTime();
        long result = curFullGcTime - preFullGcTime;
        preFullGcTime = curFullGcTime;
        return result;
    }

    /*********************************** about fd ***********************************/
    /**
     * @return 返回当前 log-agent 进程对应 fd 使用量
     */
    public long getFdCount() {
        return osResourceService.getProcOpenFdCount();
    }

    /*********************************** about memory ***********************************/

    /**
     * 获取当前进程对应内存使用量 单位：byte
     * @return 返回获取到的当前进程对应内存使用量
     */
    public long getCurrentMemoryUsage() {
        return osResourceService.getProcMemUsed();
    }

    /**
     * @return 返回jvm进程而且堆内存使用量 单位：byte
     */
    public long getCurrentProcessHeapMemoryUsed() {
        return osResourceService.getJvmProcHeapMemoryUsed();
    }

    /**
     * @return 返回jvm进程当前堆外内存使用量 单位：byte
     */
    public long getCurrentProcessNonHeapMemoryUsed() {
        return osResourceService.getJvmProcNonHeapMemoryUsed();
    }

    /**
     * @return 返回jvm进程最大可用堆内存(对应 jvm Xmx) 单位：byte
     */
    public long getCurrentProcessMaxHeapSize() {
        return osResourceService.getJvmProcHeapSizeXmx();
    }

    /**
     * @return 返回jvm进程启动以来内存使用量峰值 单位：byte
     */
    public long getProcessMemoryUsedPeak() {
        return osResourceService.getJvmProcMemUsedPeak();
    }

    /*********************************** about thread ***********************************/
    /**
     * @return 返回jvm进程当前线程使用数
     */
    public int getCurrentProcessThreadNum() {
        return osResourceService.getJvmProcThreadNum();
    }

    /**
     * @return 返回jvm进程启动以来线程数峰值
     */
    public int getCurrentProcessThreadNumPeak() {
        return osResourceService.getJvmProcThreadNumPeak();
    }

    /*********************************** about io ***********************************/
    /**
     * @return 返回jvm进程当前磁盘 io 每秒读取字节数
     */
    public long getCurrentProcessDiskIOReadBytesPS() {
        return osResourceService.getProcIOReadBytesRate();
    }

    /**
     * @return 返回jvm进程当前磁盘 io 每秒写入字节数
     */
    public long getCurrentProcessDiskIOWriteBytesPS() {
        return osResourceService.getProcIOWriteBytesRate();
    }

    /**
     * @return 返回jvm进程磁盘 io 读写等待时间占总时间百分比 对应 iotop IO
     */
    public double getCurrentProcessDiskIOAwaitTimePercent() {
        return osResourceService.getProcIOAwaitTimePercent();
    }

    /*********************************** about network ***********************************/
    /**
     * @return 返回jvm进程当前网络每秒下行流量
     */
    public long getCurrentProcessNetworkReceiveBytesPS() {
        return osResourceService.getProcNetworkReceiveBytesPs();
    }

    /**
     * @return 返回系统网络当前每秒上行流量
     */
    public long getCurrentProcessNetworkSendBytesPS() {
        return osResourceService.getProcNetworkSendBytesPs();
    }

    /************************** about network tcp **************************/
    /**
     * @return 返回jvm进程当前tcp连接数
     */
    public int getCurrentProcessNetworkTcpConnectionNum() {
        return osResourceService.getProcNetworkTcpConnectionNum();
    }

    /**
     * @return 返回jvm进程当前处于 time wait 状态 tcp 连接数
     */
    public int getCurrentProcessNetworkTcpTimeWaitNum() {
        return osResourceService.getProcNetworkTcpTimeWaitNum();
    }

    /**
     * @return 返回jvm进程当前处于 close wait 状态 tcp 连接数
     */
    public int getCurrentProcessNetworkTcpCloseWaitNum() {
        return osResourceService.getProcNetworkTcpCloseWaitNum();
    }

    /*********************************** about cpu ***********************************/
    public float getCurrentCpuUsage() {
        return osResourceService.getProcCpuUtil();
    }

    public float getCurrentCpuUsageTotalPercent() {
        return osResourceService.getProcCpuUtilTotalPercent();
    }

    private IOSResourceService getOSResourceService() {
        //根据 os 类型进行对应实例化
        String osName = ManagementFactory.getOperatingSystemMXBean().getName().toLowerCase();
        if (osName.contains(OSTypeEnum.LINUX.getDesc())) {
            return new LinuxOSResourceService();
        } else if (osName.contains(OSTypeEnum.AIX.getDesc())) {
            return new AixOSResourceService();
        } else if (osName.contains(OSTypeEnum.WINDOWS.getDesc())) {
            return new WindowsOSResourceService();
        } else if (osName.contains(OSTypeEnum.MAC_OS.getDesc())) {
            return new MacOSResourceService();
        } else {
            throw new ServiceException(String.format(
                "class=ProcessUtils||method=getOSResourceService||msg=system={%s} not support",
                osName), ErrorCodeEnum.SYSTEM_NOT_SUPPORT.getCode());
        }
    }

    /**
     * 获取当前进程对应jdk版本
     * @return
     */
    public String getCurrentProcessJdkVersion() {
        String javaVersion = System.getProperty("java.version");
        return javaVersion;
    }

}
