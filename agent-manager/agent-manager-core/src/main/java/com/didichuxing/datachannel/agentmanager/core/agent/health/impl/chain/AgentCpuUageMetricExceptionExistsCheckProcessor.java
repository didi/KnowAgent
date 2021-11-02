package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.chain.ProcessorChain;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;

/**
 * 校验是否存在 agent 进程 cpu 使用率指标异常
 * @author Ronaldo
 * @Date 2021/11/1
 */
@HealthCheckProcessorAnnotation(seq = 6, type = HealthCheckProcessorEnum.AGENT)
public class AgentCpuUageMetricExceptionExistsCheckProcessor implements Processor {
    @Override
    public void process(Context context, ProcessorChain chain) {
        AgentHealthCheckContext agentHealthCheckContext = (AgentHealthCheckContext) context;
        AgentDO agentDO = agentHealthCheckContext.getAgentDO();
        AgentMetricsManageService agentMetricsManageService = agentHealthCheckContext.getAgentMetricsManageService();

        /*
         * 校验是否存在 agent 进程 cpu 使用率指标异常
         */
        boolean agentCpuUageMetricExceptionExists = checkAgentCpuUageMetricExceptionExists(agentDO.getHostName(), agentDO.getCpuLimitThreshold(), agentMetricsManageService);
        // 不存在异常
        if (!agentCpuUageMetricExceptionExists) {
            chain.process(context, chain);
            return;
        }
        // 存在异常
        AgentHealthLevelEnum agentHealthLevelEnum = AgentHealthInspectionResultEnum.AGENT_CPU_USAGE_METRIC_EXCEPTION.getAgentHealthLevel();
        String agentHealthDescription = String.format(
                "%s:AgentId={%d}, HostName={%s}",
                AgentHealthInspectionResultEnum.AGENT_CPU_USAGE_METRIC_EXCEPTION.getDescription(),
                agentDO.getId(),
                agentDO.getHostName()
        );
        agentHealthCheckContext.setAgentHealthLevelEnum(agentHealthLevelEnum);
        agentHealthCheckContext.setAgentHealthDescription(agentHealthDescription);
    }

    /**
     * 校验是否存在 agent 进程 cpu 使用率指标异常
     *
     * @param hostName          agent 主机名
     * @param cpuLimitThreshold agent cpu 限流阈值
     * @return true：存在异常 false：不存在异常
     */
    private boolean checkAgentCpuUageMetricExceptionExists(String hostName, Integer cpuLimitThreshold, AgentMetricsManageService agentMetricsManageService) {
        /*
         * 获取 agent 最近一次心跳对应 cpu_usage（ps：表示一个心跳周期内，cpu平均 使用率）
         */
        Integer cpuUsage = agentMetricsManageService.getLastestCpuUsage(hostName);
        /*
         * cpu_usage 是否超限
         */
        return cpuUsage > cpuLimitThreshold;
    }
}
