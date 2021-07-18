package com.didichuxing.datachannel.agent.engine.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.*;
import java.util.Date;

import com.didichuxing.datachannel.agent.engine.utils.monitor.DefaultOSResourceService;
import com.didichuxing.datachannel.agent.engine.utils.monitor.IOSResourceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by huangjw on 18/6/19.
 */
public class SystemUtils {

    private static final Logger          LOGGER            = LoggerFactory
                                                               .getLogger(SystemUtils.class);

    private static IOSResourceService    osResourceService = new DefaultOSResourceService();

    private final static String          startTime;
    private static long                  preFullGcCount    = 0L;

    /**
     * 用于获取 OS 信息 bean
     */
    private static OperatingSystemMXBean osMxBean          = ManagementFactory
                                                               .getOperatingSystemMXBean();

    static {
        startTime = new Date().toString();
    }

    public static int getCpuNum() {
        return osResourceService.getCpuNum();
    }

    public static String getPid() {
        return String.valueOf(osResourceService.getPid());
    }

    public static String getStartTime() {
        return startTime;
    }

    /**
     * @return 返回 周期内 full gc 次数
     */
    public static long getFullGcCount() {
        long curFullGcCount = osResourceService.getFullGcCount();
        long result = curFullGcCount - preFullGcCount;
        preFullGcCount = curFullGcCount;
        return result;
    }

    /**
     * @return 返回当前 log-agent 进程对应 fd 使用量
     */
    public static long getFdCount() {
        return osResourceService.getPid();
    }

    /**
     * 获取当前进程对应内存使用量 单位：byte
     * @return 返回获取到的当前进程对应内存使用量
     */
    public static long getCurrentMemoryUsage() {
        return osResourceService.getCurrentProcessMemoryUsed();
    }

}
