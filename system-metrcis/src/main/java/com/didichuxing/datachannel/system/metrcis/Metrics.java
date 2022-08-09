package com.didichuxing.datachannel.system.metrcis;

import com.didichuxing.datachannel.system.metrcis.constant.ExceptionCodeEnum;
import com.didichuxing.datachannel.system.metrcis.constant.OSTypeEnum;
import com.didichuxing.datachannel.system.metrcis.exception.MetricsException;
import com.didichuxing.datachannel.system.metrcis.factory.MetricsServiceFactory;
import com.didichuxing.datachannel.system.metrcis.factory.linux.LinuxMetricsServiceFactory;
import com.didichuxing.datachannel.system.metrcis.factory.linux.mac.MacOSMetricsServiceFactory;

import java.lang.management.ManagementFactory;

/**
 * 入口类
 */
public class Metrics {

    /**
     * 周期性指标自动计算组件对象
     */
    private static PeriodMetricAutoComputeComponent periodMetricAutoComputeComponent = new PeriodMetricAutoComputeComponent();

    public static MetricsServiceFactory getMetricsServiceFactory() {
        //根据 os 类型进行对应实例化
        String osName = ManagementFactory.getOperatingSystemMXBean().getName().toLowerCase();
        if (osName.contains(OSTypeEnum.LINUX.getDesc())) {
            return LinuxMetricsServiceFactory.getInstance();
        } else if (osName.contains(OSTypeEnum.AIX.getDesc())) {
            throw new MetricsException(String.format(
                    "class=Metrics||method=getMetricsServiceFactory||errMsg=os={%s} not support",
                    osName), ExceptionCodeEnum.SYSTEM_NOT_SUPPORT.getCode());
        } else if (osName.contains(OSTypeEnum.WINDOWS.getDesc())) {
            throw new MetricsException(String.format(
                    "class=Metrics||method=getMetricsServiceFactory||errMsg=os={%s} not support",
                    osName), ExceptionCodeEnum.SYSTEM_NOT_SUPPORT.getCode());
        } else if (osName.contains(OSTypeEnum.MAC_OS.getDesc())) {
            return new MacOSMetricsServiceFactory();
        } else {
            throw new MetricsException(String.format(
                    "class=Metrics||method=getMetricsServiceFactory||errMsg=os={%s} not support",
                    osName), ExceptionCodeEnum.SYSTEM_NOT_SUPPORT.getCode());
        }
    }

    public static PeriodMetricAutoComputeComponent getPeriodMetricAutoComputeComponent() {
        return periodMetricAutoComputeComponent;
    }

}
