package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsSystemPO;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.constant.AgentHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain.context.AgentHealthCheckContext;

/**
 * agent宿主机的系统时间是否准确
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 3, type = HealthCheckProcessorEnum.AGENT)
public class AgentHostSystemTimeCheckProcessor extends BaseProcessor {

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
        boolean valid = checkHostSystemTime(context.getLastMetricsSystem());
        if(!valid) {// 如不正确
            setAgentHealthCheckResult(
                    AgentHealthInspectionResultEnum.HOST_SYSTEM_TIME_IN_VALID,
                    context,
                    context.getAgentDO().getHostName()
            );
        }
    }

    /**
     * 校验 agent 系统时间是否正确
     * @param metricsSystemPO MetricsSystemPO 对象
     * @return true：正确 false：不正确
     */
    private boolean checkHostSystemTime(MetricsSystemPO metricsSystemPO) {
        Long systemNtpOffset = metricsSystemPO.getSystemntpoffset();
        if(Math.abs(systemNtpOffset) > AgentHealthCheckConstant.AGENT_SYSTEM_TIME_GAP_CHECK_THRESHOLD) {
            return false;
        }
        return true;
    }

}
