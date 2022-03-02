package com.didichuxing.datachannel.system.metrcis;

import cn.hutool.core.lang.ClassScanner;
import com.didichuxing.datachannel.system.metrcis.annotation.PeriodMethod;
import com.didichuxing.datachannel.system.metrcis.constant.ExceptionCodeEnum;
import com.didichuxing.datachannel.system.metrcis.constant.OSTypeEnum;
import com.didichuxing.datachannel.system.metrcis.exception.MetricsException;
import com.didichuxing.datachannel.system.metrcis.factory.MetricsServiceFactory;
import com.didichuxing.datachannel.system.metrcis.factory.linux.LinuxMetricsServiceFactory;
import com.didichuxing.datachannel.system.metrcis.factory.linux.mac.MacOSMetricsServiceFactory;
import com.didichuxing.datachannel.system.metrcis.service.linux.LinuxMetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 入口类
 */
public class Metrics {

    private static final Logger LOGGER = LoggerFactory.getLogger(Metrics.class);

    public static MetricsServiceFactory getMetricsServiceFactory() {
        //根据 os 类型进行对应实例化
        String osName = ManagementFactory.getOperatingSystemMXBean().getName().toLowerCase();
        if (osName.contains(OSTypeEnum.LINUX.getDesc())) {
            //初始化定时计算线程池 & 需要定时计算函数对象
            initLinuxAutoComputeComponent(LinuxMetricsServiceFactory.getInstance());
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
            LinuxMetricsServiceFactory linuxMetricsServiceFactory = LinuxMetricsServiceFactory.getInstance();
            initLinuxAutoComputeComponent(linuxMetricsServiceFactory);
            return linuxMetricsServiceFactory;
        } else {
            throw new MetricsException(String.format(
                    "class=Metrics||method=getMetricsServiceFactory||errMsg=os={%s} not support",
                    osName), ExceptionCodeEnum.SYSTEM_NOT_SUPPORT.getCode());
        }
    }

    private static ScheduledExecutorService autoComputeThreadPool;

    private static void initLinuxAutoComputeComponent(LinuxMetricsServiceFactory linuxMetricsServiceFactory) {
        Map<Class, Map<Method, Integer>> autoComputeMethodsMap = new HashMap<>();
        String basePackage = "com.didichuxing.datachannel.system.metrcis.service.linux";
        Set<Class<? extends  Object>> clazzSet = ClassScanner.scanPackageBySuper(basePackage, LinuxMetricsService.class);
        for (Class clazz : clazzSet) {
            if(null == autoComputeMethodsMap.get(clazz)) {
                autoComputeMethodsMap.put(clazz, new HashMap<>());
            }
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                method.setAccessible(true);
                PeriodMethod periodMethod = method.getAnnotation(PeriodMethod.class);
                if(null != periodMethod) {
                    int periodMs = periodMethod.periodMs();
                    autoComputeMethodsMap.get(clazz).put(method, periodMs);
                }
            }
        }
        autoComputeThreadPool = Executors.newSingleThreadScheduledExecutor();
        submitLinuxPeriodMethodTask(linuxMetricsServiceFactory, autoComputeMethodsMap);
    }

    private static void submitLinuxPeriodMethodTask(LinuxMetricsServiceFactory linuxMetricsServiceFactory, Map<Class, Map<Method, Integer>> autoComputeMethodsMap) {
        Map<Class, Object> metricsServiceClass2MetricsServiceInstanceMap = linuxMetricsServiceFactory.getMetricsServiceMap();
        for (Map.Entry<Class, Map<Method, Integer>> entry : autoComputeMethodsMap.entrySet()) {
            Class clazz = entry.getKey();
            Object instance = metricsServiceClass2MetricsServiceInstanceMap.get(clazz);
            if(null == instance) {
                LOGGER.error(
                        String.format("%s类型的对象在LinuxMetricsServiceFactory.metricsServiceClass2MetricsServiceInstanceMap中未注册", clazz.getName())
                );
                continue;
            }
            Map<Method, Integer> method2PeriodMsMap = entry.getValue();
            for (Map.Entry<Method, Integer> method2PeriodMsEntry : method2PeriodMsMap.entrySet()) {
                Method method = method2PeriodMsEntry.getKey();
                Integer periodMs = method2PeriodMsEntry.getValue();
                autoComputeThreadPool.scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            method.invoke(instance);
                        } catch (Exception ex) {
                            LOGGER.error(
                                    String.format(
                                            "class=Metrics||method=submitPeriodMethodTask||errorMsg=invoke method=%s in class=%s error, root cause is: %s",
                                            method.getName(),
                                            clazz.getName(),
                                            ex.getMessage()
                                    ),
                                    ex
                            );
                        }
                    }
                },0, periodMs, TimeUnit.MILLISECONDS);
            }
        }
    }

}
