package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.chain.ProcessorChain;
import com.didichuxing.datachannel.agentmanager.common.constant.AgentHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;

/**
 * @author Ronaldo
 * @Date 2021/11/1
 */
@HealthCheckProcessorAnnotation(seq = 8, type = HealthCheckProcessorEnum.AGENT)
public class AgentStartupFrequentlyExistsCheckProcessor implements Processor {
    @Override
    public void process(Context context, ProcessorChain chain) {
        AgentHealthCheckContext agentHealthCheckContext = (AgentHealthCheckContext) context;
        AgentDO agentDO = agentHealthCheckContext.getAgentDO();
        AgentHealthDO agentHealthDO = agentHealthCheckContext.getAgentHealthDO();
        AgentMetricsManageService agentMetricsManageService = agentHealthCheckContext.getAgentMetricsManageService();

        /*
         * 校验是否存在 agent 非人工启动过频
         */
        boolean agentStartupFrequentlyExists = checkAgentStartupFrequentlyExists(agentDO.getHostName(), agentHealthDO, agentMetricsManageService);
        // 不存在 agent 非人工启动过频
        if (!agentStartupFrequentlyExists) {
            chain.process(context, chain);
            return;
        }
        // 存在 agent 非人工启动过频
        AgentHealthLevelEnum agentHealthLevelEnum = AgentHealthInspectionResultEnum.AGENT_STARTUP_FREQUENTLY.getAgentHealthLevel();
        String agentHealthDescription = String.format(
                "%s:AgentId={%d}, HostName={%s}",
                AgentHealthInspectionResultEnum.AGENT_STARTUP_FREQUENTLY.getDescription(),
                agentDO.getId(),
                agentDO.getHostName()
        );
        agentHealthCheckContext.setAgentHealthLevelEnum(agentHealthLevelEnum);
        agentHealthCheckContext.setAgentHealthDescription(agentHealthDescription);
    }

    /**
     * 校验是否存在 agent 非人工启动过频
     *
     * @param hostName      agent 主机名
     * @param agentHealthDO AgentHealthDO 对象
     * @return true：存在启动过频 false：不存在启动过频
     */
    private boolean checkAgentStartupFrequentlyExists(String hostName, AgentHealthDO agentHealthDO, AgentMetricsManageService agentMetricsManageService) {
        /*
         * 获取 agent 最近一次心跳对应 agent 启动时间，并对比该时间与系统记录的 agent 启动时间是否一致：
         *  一致：表示 agent 自系统记录的启动时间以来未有启动行为 do nothing.
         *  不一致：表示 agent 自系统记录的启动时间以来存在启动行为，将系统记录的 agent 启动时间记为上一次启动时间，将最近一次心跳对应 agent 启动时间记为系统记录的 agent 启动时间
         * 判断 "系统记录的 agent 启动时间" - "agent 上一次启动时间" < 对应系统设定阈值：
         *  是：进一步判断 "系统记录的 agent 启动时间" 是否由人工触发（人工触发判断条件为："系统记录的 agent 启动时间" 对应 agent 是否存在对应安装、升级类型 AgentOperationTask 执行）TODO：该步骤待实现
         *  否：不存在启动过频
         */
        Long lastestAgentStartupTime = agentMetricsManageService.getLastestAgentStartupTime(hostName);//agent心跳上报最近一次启动时间
        Long agentStartupTime = agentHealthDO.getAgentStartupTime();//系统记录的agent最近一次启动时间
        if (null == agentStartupTime || agentStartupTime <= 0) {//agent初次启动上报
            agentHealthDO.setAgentStartupTime(lastestAgentStartupTime);
            return false;
        } else {//agent非初次启动上报
            if (lastestAgentStartupTime.equals(agentStartupTime)) {//表示 agent 自系统记录的启动时间以来未有启动行为
                //do nothing
            } else {//表示 agent 自系统记录的启动时间以来存在启动行为，将系统记录的 agent 启动时间记为上一次启动时间，将最近一次心跳对应 agent 启动时间记为系统记录的 agent 启动时间
                agentHealthDO.setAgentStartupTimeLastTime(agentStartupTime);
                agentHealthDO.setAgentStartupTime(lastestAgentStartupTime);
            }
            Long agentStartupTimeLastTime = agentHealthDO.getAgentStartupTimeLastTime();//agent上一次启动时间
            if (null == agentStartupTimeLastTime) {//表示agent处于初次启动
                return false;
            } else {//表示agent非初次启动
                if (agentHealthDO.getAgentStartupTime() - agentHealthDO.getAgentStartupTimeLastTime() > AgentHealthCheckConstant.AGENT_STARTUP_FREQUENTLY_THRESHOLD) {

                    //TODO：判断 "系统记录的 agent 启动时间" 是否由人工触发（人工触发判断条件为："系统记录的 agent 启动时间" 对应 agent 是否存在对应安装、升级类型 AgentOperationTask 执行）

                    return true;
                } else {
                    return false;
                }
            }
        }
    }
}
