package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain.context.AgentHealthCheckContext;
import org.apache.commons.collections.CollectionUtils;

/**
 * 校验 agent 端是否未关联任何日志采集任务
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 10, type = HealthCheckProcessorEnum.AGENT)
public class AgentNotRelateAnyLogCollectTaskExceptionExistsCheckProcessor extends BaseProcessor {

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
         * 校验 agent 端是否未关联任何日志采集任务
         */
        boolean notRelateAnyLogCollectTask = CollectionUtils.isEmpty(
                context.getLogCollectTaskManageService().getLogCollectTaskListByAgentHostName(
                        context.getAgentDO().getHostName()
                )
        );
        if (notRelateAnyLogCollectTask) {
            setAgentHealthCheckResult(
                    AgentHealthInspectionResultEnum.NOT_RELATE_ANY_LOGCOLLECTTASK,
                    context,
                    context.getAgentDO().getHostName()
            );
        }
    }

}
