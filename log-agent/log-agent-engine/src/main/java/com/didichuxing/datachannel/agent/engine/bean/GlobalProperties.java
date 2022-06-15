package com.didichuxing.datachannel.agent.engine.bean;

import com.didichuxing.datachannel.agent.engine.metrics.source.AgentStatistics;
import com.didichuxing.datachannel.system.metrcis.Metrics;
import com.didichuxing.datachannel.system.metrcis.exception.MetricsException;
import com.didichuxing.datachannel.system.metrcis.service.ProcessMetricsService;
import com.didichuxing.datachannel.system.metrcis.service.SystemMetricsService;

public class GlobalProperties {

    private static volatile AgentStatistics       agentStatistics;

    /**
     * 系统相关指标服务
     */
    private static volatile SystemMetricsService  systemMetricsService;

    /**
     * 进程相关指标服务
     */
    private static volatile ProcessMetricsService processMetricsService;

    static {
        try {
            systemMetricsService = Metrics.getMetricsServiceFactory().createSystemMetrics();
            processMetricsService = Metrics.getMetricsServiceFactory().createProcessMetrics();
        } catch (MetricsException e) {
            //TODO：
        }
    }

    public static void setAgentStatistics(AgentStatistics agentStatistics) {
        GlobalProperties.agentStatistics = agentStatistics;
    }

    public static AgentStatistics getAgentStatistics() {
        return agentStatistics;
    }

    public static SystemMetricsService getSystemMetricsService() {
        return systemMetricsService;
    }

    public static void setSystemMetricsService(SystemMetricsService systemMetricsService) {
        GlobalProperties.systemMetricsService = systemMetricsService;
    }

    public static ProcessMetricsService getProcessMetricsService() {
        return processMetricsService;
    }

    public static void setProcessMetricsService(ProcessMetricsService processMetricsService) {
        GlobalProperties.processMetricsService = processMetricsService;
    }
}
