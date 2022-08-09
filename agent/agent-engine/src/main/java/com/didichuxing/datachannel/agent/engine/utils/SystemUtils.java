package com.didichuxing.datachannel.agent.engine.utils;

import com.didichuxing.datachannel.agent.engine.utils.monitor.*;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;

/**
 * @author Ronaldo
 */
public class SystemUtils {
    private static final Logger       LOGGER   = LoggerFactory.getLogger(SystemUtils.class);

    private static SystemUtils        instance = new SystemUtils();

    private static IOSResourceService osResourceService;

    public static SystemUtils getInstance() {
        return instance;
    }

    private SystemUtils() {
        osResourceService = getOSResourceService();
        osResourceService.clearCache();
    }

    /**
     * @return 返回系统启动时间
     */
    public long getSystemStartupTime() {
        return osResourceService.getSystemStartupTime();
    }

    /*********************************** about cpu ***********************************/
    /**
     * @return 返回系统当前cpu总体使用率
     * 注：使用率采用全核方式计数，如系统使用一颗核，则返回100，如使用两颗核，则返回200
     */
    public double getCurrentSystemCpuUsage() {
        return osResourceService.getSystemCpuUtil();
    }

    /**
     * @return 返回系统当前cpu使用率
     * 注意：使用率为总使用比率，如agent进程宿主机使用一颗核，系统共10核，则返回0.1 = 10%
     */
    public double getCurrentSystemCpuUsageTotalPercent() {
        return osResourceService.getSystemCpuUtilTotalPercent();
    }

    /*********************************** about memory ***********************************/
    /**
     * @return 返回系统当前可用内存 单位：byte
     */
    public long getCurrentSystemMemoryFree() {
        return osResourceService.getSystemMemFree();
    }

    /**
     * @return 返回系统内存总大小 单位：byte
     */
    public long getSystemMemoryTotal() {
        return osResourceService.getSystemMemTotal();
    }

    /**
     * @return 返回系统已使用内存大小 单位：byte
     */
    public long getSystemMemoryUsed() {
        return osResourceService.getSystemMemUsed();
    }

    /**
     * @return 返回系统swap内存总大小 单位：byte
     */
    public long getSystemMemorySwapSize() {
        return osResourceService.getSystemSwapTotal();
    }

    /**
     * @return 返回系统可用swap内存 单位：byte
     */
    public long getSystemMemorySwapFree() {
        return osResourceService.getSystemSwapFree();
    }

    /**
     * @return 返回系统swap内存使用量 单位：byte
     */
    public long getSystemMemorySwapUsed() {
        return osResourceService.getSystemSwapUsed();
    }

    /**
     * @return 返回系统启动以来 Tcp 主动连接次数
     */
    public long getSystemNetworkTcpActiveOpens() {
        return osResourceService.getSystemNetworkTcpActiveOpens();
    }

    /**
     * @return 返回系统启动以来 Tcp 被动连接次数
     */
    public long getSystemNetworkTcpPassiveOpens() {
        return osResourceService.getSystemNetworkTcpPassiveOpens();
    }

    /**
     * @return 返回系统启动以来 Tcp 连接失败次数
     */
    public long getSystemNetworkTcpAttemptFails() {
        return osResourceService.getSystemNetworkTcpAttemptFails();
    }

    /**
     * @return 返回系统启动以来 Tcp 连接异常断开次数
     */
    public long getSystemNetworkTcpEstabResets() {
        return osResourceService.getSystemNetworkTcpEstabResets();
    }

    /**
     * @return 返回系统启动以来 Tcp 重传的报文段总个数
     */
    public long getSystemNetworkTcpRetransSegs() {
        return osResourceService.getSystemNetworkTcpRetransSegs();
    }

    /**
     * @return 返回系统启动以来 Tcp 监听队列溢出次数
     */
    public long getSystemNetworkTcpExtListenOverflows() {
        return osResourceService.getSystemNetworkTcpExtListenOverflows();
    }

    /************************** about network udp **************************/

    /**
     * @return 返回系统启动以来 UDP 入包量
     */
    public long getSystemNetworkUdpInDatagrams() {
        return osResourceService.getSystemNetworkUdpInDatagrams();
    }

    /**
     * @return 返回系统启动以来 UDP 出包量
     */
    public long getSystemNetworkUdpOutDatagrams() {
        return osResourceService.getSystemNetworkUdpOutDatagrams();
    }

    /**
     * @return 返回系统启动以来 UDP 入包错误数
     */
    public long getSystemNetworkUdpInErrors() {
        return osResourceService.getSystemNetworkUdpInErrors();
    }

    /**
     * @return 返回系统启动以来 UDP 端口不可达个数
     */
    public long getSystemNetworkUdpNoPorts() {
        return osResourceService.getSystemNetworkUdpNoPorts();
    }

    /**
     * @return 返回系统启动以来 UDP 发送缓冲区满次数
     */
    public long getSystemNetworkUdpSendBufferErrors() {
        return osResourceService.getSystemNetworkUdpSendBufferErrors();
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
}
