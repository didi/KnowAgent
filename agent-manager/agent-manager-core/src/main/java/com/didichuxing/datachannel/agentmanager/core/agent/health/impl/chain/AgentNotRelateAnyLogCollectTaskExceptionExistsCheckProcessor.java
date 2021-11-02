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
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import org.apache.commons.collections.CollectionUtils;

/**
 * 校验 agent 端是否未关联任何日志采集任务
 * @author Ronaldo
 * @Date 2021/11/1
 */
@HealthCheckProcessorAnnotation(seq = 9, type = HealthCheckProcessorEnum.AGENT)
public class AgentNotRelateAnyLogCollectTaskExceptionExistsCheckProcessor implements Processor {
    @Override
    public void process(Context context, ProcessorChain chain) {
        AgentHealthCheckContext agentHealthCheckContext = (AgentHealthCheckContext) context;
        AgentDO agentDO = agentHealthCheckContext.getAgentDO();
        AgentHealthDO agentHealthDO = agentHealthCheckContext.getAgentHealthDO();
        LogCollectTaskManageService logCollectTaskManageService = agentHealthCheckContext.getLogCollectTaskManageService();

        /*
         * 校验 agent 端是否未关联任何日志采集任务
         */
        boolean notRelateAnyLogCollectTask = CollectionUtils.isEmpty(logCollectTaskManageService.getLogCollectTaskListByAgentHostName(agentDO.getHostName()));
        // 不存在异常
        if (!notRelateAnyLogCollectTask) {
            chain.process(context, chain);
            return;
        }
        // 存在异常
        AgentHealthLevelEnum agentHealthLevelEnum = AgentHealthInspectionResultEnum.NOT_RELATE_ANY_LOGCOLLECTTASK.getAgentHealthLevel();
        String agentHealthDescription = String.format(
                "%s:AgentId={%d}, HostName={%s}",
                AgentHealthInspectionResultEnum.NOT_RELATE_ANY_LOGCOLLECTTASK.getDescription(),
                agentDO.getId(),
                agentDO.getHostName()
        );
        agentHealthCheckContext.setAgentHealthLevelEnum(agentHealthLevelEnum);
        agentHealthCheckContext.setAgentHealthDescription(agentHealthDescription);
    }
}
