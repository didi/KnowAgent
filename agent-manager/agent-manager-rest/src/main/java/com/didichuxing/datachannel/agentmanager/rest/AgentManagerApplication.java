package com.didichuxing.datachannel.agentmanager.rest;

import cn.hutool.core.lang.ClassScanner;
import com.didichuxing.datachannel.agentmanager.common.GlobalProperties;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.dashboard.DashBoardDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.dashboard.MaintenanceDashBoardVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.dashboard.OperatingDashBoardVO;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.util.EnvUtil;
import com.didichuxing.datachannel.agentmanager.core.agent.health.impl.AgentHealthManageServiceImpl;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.impl.AgentManageServiceImpl;
import com.didichuxing.datachannel.agentmanager.core.dashboard.impl.DashboardManageServiceImpl;
import com.didichuxing.datachannel.agentmanager.core.errorlogs.impl.ErrorLogsManageServiceImpl;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.LogCollectTaskHealthManageServiceImpl;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.impl.LogCollectTaskManageServiceImpl;
import com.didichuxing.datachannel.agentmanager.core.metrics.impl.MetricsManageServiceImpl;
import com.didichuxing.datachannel.agentmanager.persistence.*;
import com.didichuxing.datachannel.agentmanager.rest.swagger.SwaggerConfiguration;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.CollectionUtils;
import java.util.*;
import java.util.concurrent.*;

/**
 * william.
 */
@EnableAsync
@EnableScheduling
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.didichuxing.datachannel.agentmanager", "com.didiglobal.logi"})
public class AgentManagerApplication {

    private static final Logger LOGGER           = LoggerFactory.getLogger(AgentManagerApplication.class);

    public static final String PACKAGE_SCAN_BASE = "com.didichuxing.datachannel.agentmanager";

    @Autowired
    private StorageFactoryBuilder storageFactoryBuilder;

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

    @Bean("metricsSystemDAO")
    public MetricsSystemDAO getMetricsSystemDAO() {
        return storageFactoryBuilder.buildMetricsDAOFactory().createMetricsSystemDAO();
    }

    @Bean("metricsAgentDAO")
    public MetricsAgentDAO getMetricsAgentDAO() {
        return storageFactoryBuilder.buildMetricsDAOFactory().createMetricsAgentDAO();
    }

    @Bean("metricsNetCardDAO")
    public MetricsNetCardDAO getMetricsNetCardDAO() {
        return storageFactoryBuilder.buildMetricsDAOFactory().createMetricsNetCardDAO();
    }

    @Bean("metricsProcessDAO")
    public MetricsProcessDAO getMetricsProcessDAO() {
        return storageFactoryBuilder.buildMetricsDAOFactory().createMetricsProcessDAO();
    }

    @Bean("metricsLogCollectTaskDAO")
    public MetricsLogCollectTaskDAO getMetricsLogCollectTaskDAO() {
        return storageFactoryBuilder.buildMetricsDAOFactory().createMetricsLogCollectTaskDAO();
    }

    @Bean("metricsDiskIODAO")
    public MetricsDiskIODAO getMetricsDiskIODAO() {
        return storageFactoryBuilder.buildMetricsDAOFactory().createMetricsDiskIODAO();
    }

    @Bean("metricsDiskDAO")
    public MetricsDiskDAO getMetricsDiskDAO() {
        return storageFactoryBuilder.buildMetricsDAOFactory().createMetricsDiskDAO();
    }

    @Bean("agentErrorLogDAO")
    public AgentErrorLogDAO getAgentErrorLogDAO() {
        return storageFactoryBuilder.buildErrorLogsDAOFactory().createAgentErrorLogDAO();
    }

}
