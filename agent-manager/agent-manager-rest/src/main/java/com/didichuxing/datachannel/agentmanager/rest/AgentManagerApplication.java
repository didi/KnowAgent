package com.didichuxing.datachannel.agentmanager.rest;

import cn.hutool.core.lang.ClassScanner;
import com.didichuxing.datachannel.agentmanager.common.GlobalProperties;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.util.EnvUtil;
import com.didichuxing.datachannel.agentmanager.rest.swagger.SwaggerConfiguration;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.AgentMetricsDAO;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.MetricService;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.impl.AgentMetricsRDSImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * william.
 */
@EnableAsync
@EnableScheduling
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.didichuxing.datachannel.agentmanager"})
public class AgentManagerApplication {

    private static final Logger LOGGER           = LoggerFactory.getLogger(AgentManagerApplication.class);

    @Value("${agent.metrics.storage.type:#{null}}")
    public String type;

    public static final String PACKAGE_SCAN_BASE = "com.didichuxing.datachannel.agentmanager";

    /**
     * @param args
     */
    public static void main(String[] args) {
        /*
         * 加载日志采集任务 & agent 健康度检查处理器
         */
        try {
            loadHealthCheckProcessors();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(-1);
        }
        EnvUtil.setLoadActiveProfiles(args);
        SwaggerConfiguration.initEnv(args);
        ApplicationContext ctx = SpringApplication.run(AgentManagerApplication.class, args);
        EnvUtil.setLoadActiveProfiles(ctx.getEnvironment().getActiveProfiles());
        for (String profile : ctx.getEnvironment().getActiveProfiles()) {
            LOGGER.info("Spring Boot use profile: {}", profile);
        }
        LOGGER.info("agent-manager Application started");

        /**
         * TODO：定时任务 fix
         */
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        pool.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    MetricService metricService = ctx.getBean(MetricService.class);
                    metricService.resetMetricConsumers();
                } catch (Exception ex) {
                    LOGGER.error(String.format(" write metrics to db error, root cause is: %s", ex.getMessage()), ex);
                }
            }
        },0, 10, TimeUnit.MINUTES);

    }

    private static void loadHealthCheckProcessors() throws Exception {
        Set<Class<? extends  Object>> processorClazzSet = ClassScanner.scanPackageByAnnotation(PACKAGE_SCAN_BASE, HealthCheckProcessorAnnotation.class);
        for (Class clazz : processorClazzSet) {
            HealthCheckProcessorAnnotation healthCheckProcessorAnnotation = (HealthCheckProcessorAnnotation) clazz.getAnnotation(HealthCheckProcessorAnnotation.class);
            HealthCheckProcessorEnum healthCheckProcessorEnum = healthCheckProcessorAnnotation.type();
            if(null == healthCheckProcessorEnum) {
                throw new Exception(
                        String.format("%s 's HealthCheckProcessorAnnotation must set type", clazz.getName())
                );
            } else {
                if(healthCheckProcessorEnum.equals(HealthCheckProcessorEnum.LOGCOLLECTTASK)) {
                    GlobalProperties.LOG_COLLECT_TASK_HEALTH_CHECK_PROCESSOR_CLASS_LIST.add(clazz);
                } else {
                    GlobalProperties.AGENT_HEALTH_CHECK_PROCESSOR_CLASS_LIST.add(clazz);
                }
            }
        }
        /*
         * 校验配置processors是否存在非法情况，非法情况包括：
         * seq 为负数
         * seq 重复
         */
        Set<Integer> distinctSet = new HashSet<>();
        for(Class<Processor> processorClass : GlobalProperties.LOG_COLLECT_TASK_HEALTH_CHECK_PROCESSOR_CLASS_LIST) {
            boolean result = distinctSet.add(
                    ((HealthCheckProcessorAnnotation) processorClass.getAnnotation(HealthCheckProcessorAnnotation.class)).seq()
            );
            if(!result) {
                throw new Exception(
                        String.format("%s's HealthCheckProcessorAnnotation property seq cannot repeatable")
                );
            }
        }
        distinctSet.clear();
        for(Class<Processor> processorClass : GlobalProperties.AGENT_HEALTH_CHECK_PROCESSOR_CLASS_LIST) {
            boolean result = distinctSet.add(
                    ((HealthCheckProcessorAnnotation) processorClass.getAnnotation(HealthCheckProcessorAnnotation.class)).seq()
            );
            if(!result) {
                throw new Exception(
                        String.format("%s's HealthCheckProcessorAnnotation property seq cannot repeatable")
                );
            }
        }

        /*
         * 将 Processor 按 seq 顺序重排列
         */
        Comparator<Class<Processor>> comparator = new Comparator<Class<Processor>>() {
            @Override
            public int compare(Class<Processor> o1, Class<Processor> o2) {
                Integer seq1 = ((HealthCheckProcessorAnnotation) o1.getAnnotation(HealthCheckProcessorAnnotation.class)).seq();
                Integer seq2 = ((HealthCheckProcessorAnnotation) o2.getAnnotation(HealthCheckProcessorAnnotation.class)).seq();
                return seq1 - seq2;
            }
        };
        GlobalProperties.LOG_COLLECT_TASK_HEALTH_CHECK_PROCESSOR_CLASS_LIST.sort(comparator);
        GlobalProperties.AGENT_HEALTH_CHECK_PROCESSOR_CLASS_LIST.sort(comparator);

    }

    @Bean
    public AgentMetricsDAO getMetricReader() {
        if ("es".equals(type)) {
//            return new AgentMetricsElasticsearchDAOImpl();
            return null;
        } else {
            return new AgentMetricsRDSImpl();
        }
    }

}
