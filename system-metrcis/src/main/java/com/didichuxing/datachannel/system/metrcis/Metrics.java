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
import com.didichuxing.datachannel.system.metrcis.service.linux.LinuxNetCardMetricsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 入口类
 */
public class Metrics {

    private static final Logger LOGGER = LoggerFactory.getLogger(Metrics.class);

    public static MetricsServiceFactory getMetricsServiceFactory() throws MetricsException {
        //根据 os 类型进行对应实例化
        String osName = ManagementFactory.getOperatingSystemMXBean().getName().toLowerCase();
        if (osName.contains(OSTypeEnum.LINUX.getDesc())) {
            //初始化定时计算线程池 & 需要定时计算函数对象
            initLinuxAutoComputeComponent();
            return new LinuxMetricsServiceFactory();
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

    private static Map<Class, Map<Method, Integer>> autoComputeMethodsMap = new HashMap<>();

    private static ScheduledExecutorService autoComputeThreadPool;

    private static void initLinuxAutoComputeComponent() {
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
        autoComputeThreadPool = Executors.newScheduledThreadPool(clazzSet.size());
        submitPeriodMethodTask();
    }

    private static void submitPeriodMethodTask() {
        for (Map.Entry<Class, Map<Method, Integer>> entry : autoComputeMethodsMap.entrySet()) {
            Class clazz = entry.getKey();
            Map<Method, Integer> method2PeriodMsMap = entry.getValue();
            for (Map.Entry<Method, Integer> method2PeriodMsEntry : method2PeriodMsMap.entrySet()) {
                Method method = method2PeriodMsEntry.getKey();
                Integer periodMs = method2PeriodMsEntry.getValue();
                autoComputeThreadPool.scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            method.invoke(clazz.newInstance());
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
