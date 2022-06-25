package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.constant.AgentHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.AggregationCalcFunctionEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricFieldEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain.context.AgentHealthCheckContext;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;

/**
 * agent gc 指标检查处理器
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 6, type = HealthCheckProcessorEnum.AGENT)
public class AgentGcMetricExceptionExistsCheckProcessor extends BaseProcessor {

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
         * 校验是否存在 agent 进程 gc 指标异常
         */
        boolean agentGcMetricExceptionExists = checkAgentGcMetricExceptionExists(
                context.getAgentDO().getHostName(),
                context.getMetricsManageService()
        );
        // 存在异常
        if (agentGcMetricExceptionExists) {
            setAgentHealthCheckResult(
                    AgentHealthInspectionResultEnum.AGENT_GC_METRIC_EXCEPTION,
                    context,
                    context.getAgentDO().getHostName()
            );
        }
    }

    /**
     * 校验是否存在 agent 进程 gc 指标异常
     * @param hostName agent 宿主机名
     * @param metricsManageService MetricsManageService 对象
     * @return true：存在 agent 进程 gc 指标异常 false：不存在 agent 进程 gc 指标异常
     */
    public static boolean checkAgentGcMetricExceptionExists(
            String hostName,
            MetricsManageService metricsManageService
    ) {
        /*
         * 获取 agent 近一小时内 fullgc 次数是否 > 2，如是：表示存在 agent 进程 gc 指标异常 如不是：表示不存在 agent 进程 gc 指标异常
         *
         */
        Long startTime = System.currentTimeMillis() - AgentHealthCheckConstant.AGENT_GC_METRIC_CHECK_LASTEST_MS_THRESHOLD;
        Long endTime = System.currentTimeMillis();
        Object agentFullGcTimesObj = metricsManageService.getAggregationQueryPerHostNameFromMetricsProcess(
                hostName,
                startTime,
                endTime,
                AggregationCalcFunctionEnum.SUM.getValue(),
                MetricFieldEnum.JVM_PROCESS_FULL_GC_COUNT.getFieldName()
        );
        Long agentFullGcTimes = 0L;
        if(null != agentFullGcTimesObj) {
            agentFullGcTimes = Long.valueOf(agentFullGcTimesObj.toString());
        }
        return agentFullGcTimes > AgentHealthCheckConstant.AGENT_GC_TIMES_METRIC_CHECK_THRESHOLD;
    }

}
