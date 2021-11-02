package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.chain.ProcessorChain;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;
import jdk.internal.agent.resources.agent;

/**
 * 校验 agent 是否存在错误日志输出
 * @author Ronaldo
 * @Date 2021/10/31
 */
@HealthCheckProcessorAnnotation(seq = 4, type = HealthCheckProcessorEnum.AGENT)
public class ErrorLogsExistsCheckProcessor implements Processor {
    @Override
    public void process(Context context, ProcessorChain chain) {
        AgentHealthCheckContext agentHealthCheckContext = (AgentHealthCheckContext) context;
        AgentDO agentDO = agentHealthCheckContext.getAgentDO();
        AgentHealthDO agentHealthDO = agentHealthCheckContext.getAgentHealthDO();
        AgentMetricsManageService agentMetricsManageService = agentHealthCheckContext.getAgentMetricsManageService();

        /*
         * 校验 agent 是否存在错误日志输出
         */
        Long agentHealthCheckTimeEnd = System.currentTimeMillis() - 1000; //Agent健康度检查流程获取agent心跳数据右边界时间，取当前时间前一秒
        boolean errorLogsExists = checkErrorLogsExists(agentDO.getHostName(), agentHealthDO, agentHealthCheckTimeEnd, agentMetricsManageService);
        // agent 不存在错误日志输出
        if (!errorLogsExists) {
            agentHealthDO.setLastestErrorLogsExistsCheckHealthyTime(agentHealthCheckTimeEnd);
            chain.process(context, chain);
            return;
        }
        // agent 存在错误日志输出
        AgentHealthLevelEnum agentHealthLevelEnum = AgentHealthInspectionResultEnum.AGENT_ERRORLOGS_EXISTS.getAgentHealthLevel();
        String agentHealthDescription = String.format(
                "%s:AgentId={%d}, HostName={%s}",
                AgentHealthInspectionResultEnum.AGENT_ERRORLOGS_EXISTS.getDescription(),
                agentDO.getId(),
                agentDO.getHostName()
        );
        agentHealthCheckContext.setAgentHealthLevelEnum(agentHealthLevelEnum);
        agentHealthCheckContext.setAgentHealthDescription(agentHealthDescription);
    }

    /**
     * 校验 agent 是否存在错误日志输出
     *
     * @param hostName                agent 主机名
     * @param agentHealthDO           AgentHealthDO对象
     * @param agentHealthCheckTimeEnd Agent健康度检查流程获取agent心跳数据右边界时间
     * @return true：存在错误日志输出 false：不存在错误日志输出
     */
    private boolean checkErrorLogsExists(String hostName, AgentHealthDO agentHealthDO, Long agentHealthCheckTimeEnd, AgentMetricsManageService agentMetricsManageService) {
        /*
         * 获取自上次"错误日志输出存在"健康点 ~ 当前时间，agent 是否存在错误日志输出
         */
        Long lastestCheckTime = agentHealthDO.getLastestErrorLogsExistsCheckHealthyTime();
        if (null == lastestCheckTime) {
            throw new ServiceException(
                    String.format("Agent={hostName=%s}对应lastestErrorLogsExistsCheckHealthyTime不存在", hostName),
                    ErrorCodeEnum.AGENT_HEALTH_ERROR_LOGS_EXISTS_CHECK_HEALTHY_TIME_NOT_EXISTS.getCode()
            );
        }
        Integer errorlogsCount = agentMetricsManageService.getErrorLogCount(
                lastestCheckTime,
                agentHealthCheckTimeEnd,
                hostName
        );
        return errorlogsCount > 0;
    }
}
