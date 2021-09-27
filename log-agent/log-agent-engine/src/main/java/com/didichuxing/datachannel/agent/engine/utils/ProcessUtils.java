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

    private static final Logger       LOGGER         = LoggerFactory.getLogger(ProcessUtils.class);

    private static ProcessUtils       instance       = new ProcessUtils();

    private static IOSResourceService osResourceService;

    private long                      preFullGcCount = 0L;

    public static ProcessUtils getInstance() {
        return instance;
    }

    private ProcessUtils() {
        osResourceService = getOSResourceService();
    }

    public int getCpuNum() {
        return osResourceService.getCpuNum();
    }

    public String getPid() {
        return String.valueOf(osResourceService.getPid());
    }

    /**
     * @return 返回 周期内 full gc 次数
     */
    public long getFullGcCount() {
        long curFullGcCount = osResourceService.getFullGcCount();
        long result = curFullGcCount - preFullGcCount;
        preFullGcCount = curFullGcCount;
        return result;
    }

    /**
     * @return 返回当前 log-agent 进程对应 fd 使用量
     */
    public long getFdCount() {
        return osResourceService.getCurrentProcessFdUsed();
    }

    /**
     * 获取当前进程对应内存使用量 单位：byte
     * @return 返回获取到的当前进程对应内存使用量
     */
    public long getCurrentMemoryUsage() {
        return osResourceService.getCurrentProcessMemoryUsed();
    }

    public float getCurrentCpuUsage() {
        return osResourceService.getCurrentProcessCpuUsage();
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
