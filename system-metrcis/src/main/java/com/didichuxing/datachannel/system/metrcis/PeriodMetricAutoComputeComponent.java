package com.didichuxing.datachannel.system.metrcis;

import cn.hutool.core.lang.ClassScanner;
import com.didichuxing.datachannel.system.metrcis.annotation.PeriodMethod;
import com.didichuxing.datachannel.system.metrcis.service.linux.LinuxMetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 周期性指标自动计算组件
 */
public class PeriodMetricAutoComputeComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodMetricAutoComputeComponent.class);

    /**
     * 用于定时计算的线程池对象
     */
    private ScheduledExecutorService autoComputeThreadPool = Executors.newSingleThreadScheduledExecutor();

    /**
     * key：待自动计算的类
     * value：待自动计算函数对象 * 对应函数计算周期
     */
    private Map<Class, Map<Method, Integer>> autoComputeMethodsMap = new HashMap<>();

    public PeriodMetricAutoComputeComponent() {
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
    }

    public void start() {
        Map<Class, Object> metricsServiceClass2MetricsServiceInstanceMap = Metrics.getMetricsServiceFactory().getMetricsServiceMap();
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
