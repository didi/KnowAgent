package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.constant.AgentHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.AggregationCalcFunctionEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricFieldEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain.context.AgentHealthCheckContext;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;

/**
 * agent的errorlogs流对应的下游接收端是否存在写入失败情况
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 4, type = HealthCheckProcessorEnum.AGENT)
public class ErrorLogsSendFailedExistsCheckProcessor extends BaseProcessor {

    @Override
    protected void process(AgentHealthCheckContext context) {
        /*
         * 校验 agent 是否为红 黄
         */
        if(
                context.getAgentHealthLevelEnum().equals(LogCollectTaskHealthLevelEnum.RED) ||
                        context.getAgentHealthLevelEnum().equals(LogCollectTaskHealthLevelEnum.YELLOW)
        ) {
            return;
        }
        /*
         * 校验在距当前时间的心跳存活判定周期内，agent 是否存在心跳
         */
        boolean errorLogsSendExceptionExists = checkErrorLogsSendExceptionExists(
                context.getMetricsManageService(),
                context.getAgentDO().getHostName()
        );
        if(errorLogsSendExceptionExists) {//  存在
            setAgentHealthCheckResult(AgentHealthInspectionResultEnum.AGENT_ERROR_LOGS_SEND_FAILED_EXISTS, context);
        }
    }

    /**
     * 检查 agent error logs 发送对应下游接收端是否存在错误
     * @param metricsManageService MetricsManageService 对象
     * @param hostName 主机名
     * @return true：存在 false：不存在
     */
    private boolean checkErrorLogsSendExceptionExists(MetricsManageService metricsManageService, String hostName) {
        Long currentTime = System.currentTimeMillis();
        Object errorLogsSendFailedCountObj = metricsManageService.getAggregationQueryPerHostNameFromMetricsAgent(
                hostName,
                currentTime - AgentHealthCheckConstant.AGENT_ERROR_LOGS_SEND_FAILED_CHECK_LASTEST_MS_THRESHOLD,
                currentTime,
                AggregationCalcFunctionEnum.SUM.getValue(),
                MetricFieldEnum.AGENT_ERROR_LOGS_SEND_FAILED_COUNT.getFieldName()
        );
        Long errorLogsSendFailedCount = 0L;
        if(null != errorLogsSendFailedCountObj) {
            errorLogsSendFailedCount = Long.valueOf(errorLogsSendFailedCountObj.toString());
        }
        return errorLogsSendFailedCount != 0;
    }

}
