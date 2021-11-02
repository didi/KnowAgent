package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.chain.ProcessorChain;
import com.didichuxing.datachannel.agentmanager.common.constant.AgentHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;

/**
 * @author Ronaldo
 * @Date 2021/11/1
 */
@HealthCheckProcessorAnnotation(seq = 5, type = HealthCheckProcessorEnum.AGENT)
public class AgentGcMetricExceptionExistsCheckProcessor implements Processor {
    @Override
    public void process(Context context, ProcessorChain chain) {
        AgentHealthCheckContext agentHealthCheckContext = (AgentHealthCheckContext) context;
        AgentDO agentDO = agentHealthCheckContext.getAgentDO();
        AgentMetricsManageService agentMetricsManageService = agentHealthCheckContext.getAgentMetricsManageService();

        /*
         * 校验是否存在 agent 进程 gc 指标异常
         */
        boolean agentGcMetricExceptionExists = checkAgentGcMetricExceptionExists(agentDO.getHostName(), agentMetricsManageService);
        // 不存在异常
        if (!agentGcMetricExceptionExists) {
            chain.process(context, chain);
            return;
        }
        // 存在异常
        AgentHealthLevelEnum agentHealthLevelEnum = AgentHealthInspectionResultEnum.AGENT_GC_METRIC_EXCEPTION.getAgentHealthLevel();
        String agentHealthDescription = String.format(
                "%s:AgentId={%d}, HostName={%s}",
                AgentHealthInspectionResultEnum.AGENT_GC_METRIC_EXCEPTION.getDescription(),
                agentDO.getId(),
                agentDO.getHostName()
        );
        agentHealthCheckContext.setAgentHealthLevelEnum(agentHealthLevelEnum);
        agentHealthCheckContext.setAgentHealthDescription(agentHealthDescription);

    }

    /**
     * 校验是否存在 agent 进程 gc 指标异常
     *
     * @param hostName agent 主机名
     * @return true：存在 agent 进程 gc 指标异常 false：不存在 agent 进程 gc 指标异常
     */
    private boolean checkAgentGcMetricExceptionExists(String hostName, AgentMetricsManageService agentMetricsManageService) {
        /*
         * 获取 agent 近一小时内 fullgc 次数是否 > 1，如是：表示存在 agent 进程 gc 指标异常 如不是：表示不存在 agent 进程 gc 指标异常
         *
         */
        Long startTime = System.currentTimeMillis() - AgentHealthCheckConstant.AGENT_GC_METRIC_CHECK_LASTEST_MS_THRESHOLD;
        Long endTime = System.currentTimeMillis();
        Long agentFullgcTimes = agentMetricsManageService.getAgentFullgcTimesByTimeFrame(
                startTime,
                endTime,
                hostName
        );
        return agentFullgcTimes > AgentHealthCheckConstant.AGENT_GC_TIMES_METRIC_CHECK_THRESHOLD;
    }
}
