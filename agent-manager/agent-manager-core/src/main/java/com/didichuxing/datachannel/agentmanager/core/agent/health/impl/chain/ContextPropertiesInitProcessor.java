package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsAgentPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsProcessPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsSystemPO;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain.context.AgentHealthCheckContext;

/**
 * context 属性初始化处理器
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 1, type = HealthCheckProcessorEnum.AGENT)
public class ContextPropertiesInitProcessor extends BaseProcessor {

    @Override
    protected void process(AgentHealthCheckContext context) {

        /*
         * set lastAgentMetric
         */
        MetricsAgentPO metricsAgentPO = context.getMetricsManageService().getLastAgentMetric(context.getAgentDO().getHostName());
        if(null != metricsAgentPO) {
            context.setLastMetricsAgent(metricsAgentPO);
        }

        /*
         * set lastSystemMetric
         */
        MetricsSystemPO metricsSystemPO = context.getMetricsManageService().getLastSystemMetric(context.getAgentDO().getHostName());
        if(null != metricsSystemPO) {
            context.setLastMetricsSystem(metricsSystemPO);
        }

        /*
         * set lastProcessMetric
         */
        MetricsProcessPO metricsProcessPO = context.getMetricsManageService().getLastProcessMetric(context.getAgentDO().getHostName());
        if(null != metricsProcessPO) {
            context.setLastMetricsProcess(metricsProcessPO);
        }

        /*
         * set logCollectTaskHealthCheckTimeEnd
         */
        if(null != metricsAgentPO) {
            context.setAgentHealthCheckTimeEnd(metricsAgentPO.getHeartbeattime());
        }

    }

}
