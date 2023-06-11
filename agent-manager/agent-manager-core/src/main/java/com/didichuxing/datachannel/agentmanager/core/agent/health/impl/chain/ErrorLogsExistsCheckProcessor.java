package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.AggregationCalcFunctionEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricFieldEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain.context.AgentHealthCheckContext;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;

/**
 * 校验 agent 是否存在错误日志输出
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 5, type = HealthCheckProcessorEnum.AGENT)
public class ErrorLogsExistsCheckProcessor extends BaseProcessor {

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
         * 校验 agent 是否存在错误日志输出
         */
        boolean errorLogsExists = checkErrorLogsExists(
                context.getAgentDO().getHostName(),
                context.getAgentHealthDO(),
                context.getAgentHealthCheckTimeEnd(),
                context.getMetricsManageService()
        );
        if (errorLogsExists) {// agent 存在错误日志输出
            setAgentHealthCheckResult(
                    AgentHealthInspectionResultEnum.AGENT_ERROR_LOGS_EXISTS,
                    context,
                    context.getAgentDO().getHostName()
            );
        }
    }

    /**
     * 校验 agent 是否存在错误日志输出
     * @param hostName agent 主机名
     * @param agentHealthDO AgentHealthDO对象
     * @param agentHealthCheckTimeEnd Agent健康度检查流程获取agent心跳数据右边界时间
     * @param metricsManageService MetricsManageService 对象
     * @return true：存在错误日志输出 false：不存在错误日志输出
     */
    private boolean checkErrorLogsExists(
            String hostName,
            AgentHealthDO agentHealthDO,
            Long agentHealthCheckTimeEnd,
            MetricsManageService metricsManageService
    ) {
        /*
         * 获取自上次"错误日志输出存在"健康点 ~ 最近一次 agent 业务指标心跳时间，agent 是否存在错误日志输出
         */
        Object errorLogsCountObj = metricsManageService.getAggregationQueryPerHostNameFromMetricsAgent(
                hostName,
                agentHealthDO.getLastestErrorLogsExistsCheckHealthyTime(),
                agentHealthCheckTimeEnd,
                AggregationCalcFunctionEnum.SUM.getValue(),
                MetricFieldEnum.AGENT_ERROR_LOGS_COUNT.getFieldName()
        );
        Long errorLogsCount = 0L;
        if(null != errorLogsCountObj) {
            errorLogsCount = Long.valueOf(errorLogsCountObj.toString());
        }
        return errorLogsCount > 0;
    }

}
