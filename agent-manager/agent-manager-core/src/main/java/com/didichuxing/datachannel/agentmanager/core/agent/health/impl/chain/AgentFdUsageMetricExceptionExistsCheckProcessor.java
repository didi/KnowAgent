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
 * 校验是否存在 agent 进程 fd 使用量指标异常
 * @author Ronaldo
 * @Date 2021/11/1
 */
@HealthCheckProcessorAnnotation(seq = 7, type = HealthCheckProcessorEnum.AGENT)
public class AgentFdUsageMetricExceptionExistsCheckProcessor implements Processor {
    @Override
    public void process(Context context, ProcessorChain chain) {
        AgentHealthCheckContext agentHealthCheckContext = (AgentHealthCheckContext) context;
        AgentDO agentDO = agentHealthCheckContext.getAgentDO();
        AgentMetricsManageService agentMetricsManageService = agentHealthCheckContext.getAgentMetricsManageService();

        /*
         * 校验是否存在 agent 进程 fd 使用量指标异常
         */
        boolean agentFdUsageMetricExceptionExists = checkAgentFdUsageMetricExceptionExists(agentDO.getHostName(), agentMetricsManageService);
        // 不存在异常
        if (!agentFdUsageMetricExceptionExists) {
            chain.process(context, chain);
            return;
        }
        // 存在异常
        AgentHealthLevelEnum agentHealthLevelEnum = AgentHealthInspectionResultEnum.AGENT_FD_USAGE_METRIC_EXCEPTION.getAgentHealthLevel();
        String agentHealthDescription = String.format(
                "%s:AgentId={%d}, HostName={%s}",
                AgentHealthInspectionResultEnum.AGENT_FD_USAGE_METRIC_EXCEPTION.getDescription(),
                agentDO.getId(),
                agentDO.getHostName()
        );
        agentHealthCheckContext.setAgentHealthLevelEnum(agentHealthLevelEnum);
        agentHealthCheckContext.setAgentHealthDescription(agentHealthDescription);
    }

    /**
     * 校验是否存在 agent 进程 fd 使用量指标异常
     *
     * @param hostName agent 主机名
     * @return true：存在异常 false：不存在异常
     */
    private boolean checkAgentFdUsageMetricExceptionExists(String hostName, AgentMetricsManageService agentMetricsManageService) {
        /*
         * 获取最近一次agent心跳中对应 fd 使用量值，判断该值是否 > AgentHealthCheckConstant.AGENT_FD_USED_THRESHOLD
         */
        Integer fdUsage = agentMetricsManageService.getLastestFdUsage(hostName);
        return fdUsage > AgentHealthCheckConstant.AGENT_FD_USED_THRESHOLD;
    }

}
