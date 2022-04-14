package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.constant.AgentHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.AggregationCalcFunctionEnum;
import com.didichuxing.datachannel.agentmanager.common.util.NetworkUtil;
import com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain.context.AgentHealthCheckContext;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;
import org.apache.commons.lang3.StringUtils;

/**
 * Agent心跳检查
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 2, type = HealthCheckProcessorEnum.AGENT)
public class AgentHeartBeatCheckProcessor extends BaseProcessor {

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
         * 校验在距当前时间的心跳存活判定周期内，agent 是否存在心跳
         */
        boolean alive = checkAliveByHeartbeat(
                context.getAgentDO().getHostName(),
                context.getMetricsManageService()
        );
        if(!alive) {// 如不存活
            /*
             * agent宿主机是否存活
             */
            boolean hostConnect = NetworkUtil.ping(context.getAgentDO().getHostName());
            if(!hostConnect) {
                setAgentHealthCheckResult(AgentHealthInspectionResultEnum.HOST_OF_AGENT_NOT_ALIVE, context);
            } else {
                /*
                 * agent是否已配置指标流的接收端
                 */
                if(checkAgentMetricsReceiverConfigured(context.getAgentDO())) {
                    /*
                     * agent的指标流下游接收端连通性是否正常
                     */
                    boolean agentMetricsReceiverConfigValid = checkAgentMetricsReceiverConfigValid(context.getAgentDO());
                    if(agentMetricsReceiverConfigValid) {
                        setAgentHealthCheckResult(AgentHealthInspectionResultEnum.AGENT_PROCESS_BROKES_DOWN, context);
                    } else {
                        setAgentHealthCheckResult(AgentHealthInspectionResultEnum.AGENT_METRICS_RECEIVER_NOT_CONNECTED, context);
                    }
                } else {
                    setAgentHealthCheckResult(AgentHealthInspectionResultEnum.AGENT_METRICS_CONFIGURATION_NOT_EXISTS, context);
                }
            }
        }
    }

    private boolean checkAgentMetricsReceiverConfigValid(AgentDO agentDO) {

        //TODO：

        return true;

    }

    private boolean checkAgentMetricsReceiverConfigured(AgentDO agentDO) {
        if(
                null != agentDO.getMetricsSendReceiverId() &&
                        0l != agentDO.getMetricsSendReceiverId() &&
                        StringUtils.isNotBlank(agentDO.getMetricsSendTopic()) &&
                        StringUtils.isNotBlank(agentDO.getMetricsProducerConfiguration())
        ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 校验在距当前时间的心跳存活判定周期内，agent 是否存或
     * @param hostName agent 主机名
     * @param metricsManageService MetricsManageService 对象
     * @return true：存活 false：不存活
     */
    private boolean checkAliveByHeartbeat(String hostName, MetricsManageService metricsManageService) {
        /*
         * 获取近 AgentHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD 时间范围内 agent 心跳数，
         * 心跳数量 == 0，表示 agent 不存在心跳
         */
        Long currentTime = System.currentTimeMillis();
        Object heartbeatTimesObj = metricsManageService.getAggregationQueryPerHostNameFromMetricsAgent(
                hostName,
                currentTime - AgentHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD,
                currentTime,
                AggregationCalcFunctionEnum.COUNT.getValue(),
                "*"
        );
        Long heartbeatTimes = 0L;
        if(null != heartbeatTimesObj) {
            heartbeatTimes = Long.valueOf(heartbeatTimesObj.toString());
        }
        return heartbeatTimes != 0L;
    }

}
