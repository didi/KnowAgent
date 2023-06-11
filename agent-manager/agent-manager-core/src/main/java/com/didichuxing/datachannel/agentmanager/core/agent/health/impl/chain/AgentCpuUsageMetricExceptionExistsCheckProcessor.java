package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.constant.AgentHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.AggregationCalcFunctionEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain.context.AgentHealthCheckContext;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;

/**
 * 校验是否存在 agent 进程 cpu 使用率指标异常
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 7, type = HealthCheckProcessorEnum.AGENT)
public class AgentCpuUsageMetricExceptionExistsCheckProcessor extends BaseProcessor {

    @Override
    protected void process(AgentHealthCheckContext context) {
        /*
         * 校验 agent 是否为红 黄
         */
        if(
                context.getAgentHealthLevelEnum().equals(AgentHealthLevelEnum.RED) ||
                        context.getAgentHealthLevelEnum().equals(AgentHealthLevelEnum.YELLOW)
        ) {
            return;
        }
        /*
         * 校验是否存在 agent 进程 cpu 使用率指标异常
         */
        boolean agentCpuUsageMetricExceptionExists = checkAgentCpuUageMetricExceptionExists(
                context.getAgentDO().getHostName(),
                context.getAgentDO().getCpuLimitThreshold(),
                context.getMetricsManageService()
        );
        // 存在异常
        if (agentCpuUsageMetricExceptionExists) {
            setAgentHealthCheckResult(
                    AgentHealthInspectionResultEnum.AGENT_CPU_USAGE_METRIC_EXCEPTION,
                    context,
                    context.getAgentDO().getHostName(),
                    String.format("%d%", context.getAgentDO().getCpuLimitThreshold())
            );
        }
    }

    /**
     * 校验是否存在 agent 进程 cpu 使用率指标异常
     * @param hostName          agent 宿主机名
     * @param cpuLimitThreshold agent cpu 限流阈值
     * @param metricsManageService MetricsManageService 对象
     * @return true：存在异常 false：不存在异常
     */
    private boolean checkAgentCpuUageMetricExceptionExists(
            String hostName,
            Integer cpuLimitThreshold,
            MetricsManageService metricsManageService
    ) {
        /*
         * 获取 agent 最近10分钟，各心跳对应 cpu_usage 均值的最大值是否超 agent cpu 限流阈值
         */
        Long currentTime = System.currentTimeMillis();
        Object cpuUsageMaxObj = metricsManageService.getAggregationQueryPerHostNameFromMetricsProcess(
                hostName,
                currentTime - AgentHealthCheckConstant.AGENT_CPU_USAGE_METRIC_CHECK_LASTEST_MS_THRESHOLD,
                currentTime,
                AggregationCalcFunctionEnum.MAX.getValue(),
                "procCpuUtilMean"
        );
        Double cpuUsageMax = 0d;
        if(null != cpuUsageMaxObj) {
            cpuUsageMax = Double.valueOf(cpuUsageMaxObj.toString());
        }
        return cpuUsageMax > cpuLimitThreshold;
    }

}
