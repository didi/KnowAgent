package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.constant.AgentHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.AggregationCalcFunctionEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricFieldEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain.context.AgentHealthCheckContext;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;

/**
 * 校验是否存在 agent 进程 fd 使用量指标异常
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 8, type = HealthCheckProcessorEnum.AGENT)
public class AgentFdUsageMetricExceptionExistsCheckProcessor extends BaseProcessor {

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
         * 校验是否存在 agent 进程 fd 使用量指标异常
         */
        boolean agentFdUsageMetricExceptionExists = checkAgentFdUsageMetricExceptionExists(
                context.getAgentDO().getHostName(),
                context.getMetricsManageService()
        );
        //  存在异常
        if (agentFdUsageMetricExceptionExists) {
            setAgentHealthCheckResult(
                    AgentHealthInspectionResultEnum.AGENT_FD_USAGE_METRIC_EXCEPTION,
                    context,
                    context.getAgentDO().getHostName()
            );
        }
    }

    /**
     * 校验是否存在 agent 进程 fd 使用量指标异常
     * @param hostName agent 宿主机名
     * @param metricsManageService MetricsManageService 对象
     * @return true：存在异常 false：不存在异常
     */
    private boolean checkAgentFdUsageMetricExceptionExists(String hostName, MetricsManageService metricsManageService) {
        /*
         * 获取近 10 mins agent 心跳对应 fd 使用量最大值，判断该值是否 > AgentHealthCheckConstant.AGENT_FD_USED_THRESHOLD
         */
        Long currentTime = System.currentTimeMillis();
        Object fdUsageObj = metricsManageService.getAggregationQueryPerHostNameFromMetricsProcess(
                hostName,
                currentTime - AgentHealthCheckConstant.AGENT_FD_USED_METRIC_CHECK_LASTEST_MS_THRESHOLD,
                currentTime,
                AggregationCalcFunctionEnum.MAX.getValue(),
                MetricFieldEnum.JVM_PROCESS_OPEN_FD_COUNT.getFieldName()
        );
        Integer fdUsage = 0;
        if(null != fdUsageObj) {
            fdUsage = Integer.valueOf(fdUsageObj.toString());
        }
        return fdUsage > AgentHealthCheckConstant.AGENT_FD_USED_THRESHOLD;
    }

}
