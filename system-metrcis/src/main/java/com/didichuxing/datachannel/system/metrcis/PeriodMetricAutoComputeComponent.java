package com.didichuxing.datachannel.system.metrcis;

import cn.hutool.core.lang.ClassScanner;
import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.system.metrcis.annotation.PeriodMethod;
import com.didichuxing.datachannel.system.metrcis.service.linux.LinuxMetricsService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;

/**
 * 周期性指标自动计算组件
 */
public class PeriodMetricAutoComputeComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodMetricAutoComputeComponent.class);

    /**
     * 用于定时计算的线程池对象
     */
    private ScheduledExecutorService autoComputeThreadPool = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder().setNameFormat("metricsAutoComputeThreadPool").build()
    );

    /**
     * key：待自动计算的类
     * value：待自动计算函数对象 * 对应函数计算周期
     */
    private Map<Class, Map<Method, Integer>> autoComputeMethodsMap = new HashMap<>();

    /**
     * key：外部计算组件实例id
     * value：待调度执行任务实例
     */
    private Map<String, List<Future<?>>> externalComputeInstanceId2FutureMap = new ConcurrentHashMap();

    public PeriodMetricAutoComputeComponent() {
        /*
         * 启动 system-metrics 系统内部定时指标计算任务
         */
        try {
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
            startInternalPeriodTasks();
        } catch (Exception ex) {
            LOGGER.error(
                    String.format(
                            "class=PeriodMetricAutoComputeComponent||method=PeriodMetricAutoComputeComponent()||errorMsg=load and start internal period tasks failed, root cause is: %s",
                            ex.getMessage()
                    ),
                    ex
            );
        }
    }

    private Future<?> submitPeriodMethodTask(Object instance, Method method, Integer periodMs) {
        Future<?> future = autoComputeThreadPool.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    method.invoke(instance);
                } catch (Exception ex) {
                    LOGGER.error(
                            String.format(
                                    "class=PeriodMetricAutoComputeComponent||method=submitPeriodMethodTask||errorMsg=invoke method=%s in class=%s error, root cause is: %s",
                                    method.getName(),
                                    instance.getClass().getName(),
                                    ex.getMessage()
                            ),
                            ex
                    );
                }
            }
        },0, periodMs, TimeUnit.MILLISECONDS);
        return future;
    }

    public boolean registerAutoComputeTask(String id, Object instance) {
        try {
            /*
             * 获取给定 instance 对应注解 PeriodMethod 函数集，并将这些函数集作为定时任务进行提交
             */
            Method[] methods = instance.getClass().getDeclaredMethods();
            List<Future<?>> futureList = new ArrayList<>();
            for (Method method : methods) {
                method.setAccessible(true);
                PeriodMethod periodMethod = method.getAnnotation(PeriodMethod.class);
                if(null != periodMethod) {
                    int periodMs = periodMethod.periodMs();
                    futureList.add(submitPeriodMethodTask(instance, method, periodMs));
                }
            }
            /*
             * 将 id 关联的任务对应 future 对象集映射至 externalComputeInstanceId2FutureMap
             */
            externalComputeInstanceId2FutureMap.put(id, futureList);
            return true;
        } catch (Exception ex) {
            LOGGER.error(
                    String.format(
                            "class=PeriodMetricAutoComputeComponent||method=registerAutoComputeTask()||errorMsg=register auto compute task failed, id: %s, instance: %s, root cause is: %s",
                            id,
                            JSON.toJSONString(instance),
                            ex.getMessage()
                    ),
                    ex
            );
            return false;
        }
    }

    public boolean unRegisterAutoComputeTask(String id, Object instance) {
        boolean unRegisterSuccessful = true;
        try {
            List<Future<?>> futureList = externalComputeInstanceId2FutureMap.get(id);
            if(CollectionUtils.isNotEmpty(futureList)) {
                for (Future<?> future : futureList) {
                    boolean cancelSuccessful = future.cancel(true);
                    if(!cancelSuccessful) {
                        LOGGER.error(
                                String.format(
                                        "class=PeriodMetricAutoComputeComponent||method=unRegisterAutoComputeTask()||errorMsg=unregister auto compute task failed, because stop task={id: %s, task's class: %s} failed",
                                        id,
                                        instance.getClass().getName()
                                )
                        );
                        unRegisterSuccessful = false;
                    }
                }
            } else {
                //对应组件 invoke 两 次，属 正 常 情 况
            }
        } catch (Exception ex) {
            LOGGER.error(
                    String.format(
                            "class=PeriodMetricAutoComputeComponent||method=unRegisterAutoComputeTask()||errorMsg=unregister auto compute task failed, id: %s, instance: %s, root cause is: %s",
                            id,
                            JSON.toJSONString(instance),
                            ex.getMessage()
                    ),
                    ex
            );
            unRegisterSuccessful = false;
        } finally {
            externalComputeInstanceId2FutureMap.remove(id);
        }
        return unRegisterSuccessful;
    }

    private void startInternalPeriodTasks() {
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
                submitPeriodMethodTask(instance, method, periodMs);
            }
        }
    }

}
