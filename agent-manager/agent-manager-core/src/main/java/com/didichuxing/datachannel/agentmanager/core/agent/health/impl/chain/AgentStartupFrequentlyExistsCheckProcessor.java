package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsProcessPO;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.constant.AgentHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain.context.AgentHealthCheckContext;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;

/**
 * agent 频繁启动检查处理器
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 9, type = HealthCheckProcessorEnum.AGENT)
public class AgentStartupFrequentlyExistsCheckProcessor extends BaseProcessor {

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
         * 校验是否存在 agent 非人工启动过频
         */
        boolean agentStartupFrequentlyExists = checkAgentStartupFrequentlyExists(
                context.getAgentDO().getHostName(),
                context.getAgentHealthDO(),
                context.getLastMetricsProcess(),
                context.getMetricsManageService()
        );
        if (agentStartupFrequentlyExists) {// 存在 agent 非人工启动过频
            setAgentHealthCheckResult(
                    AgentHealthInspectionResultEnum.AGENT_STARTUP_FREQUENTLY,
                    context,
                    context.getAgentDO().getHostName()
            );
        }
    }

    /**
     * 校验是否存在 agent 非人工启动过频
     * @param hostName      agent 宿主机名
     * @param agentHealthDO AgentHealthDO 对象
     * @param lastMetricsProcess MetricsProcessPO 对象
     * @param metricsManageService MetricsManageService 对象
     * @return true：存在启动过频 false：不存在启动过频
     */
    private boolean checkAgentStartupFrequentlyExists(String hostName, AgentHealthDO agentHealthDO, MetricsProcessPO lastMetricsProcess, MetricsManageService metricsManageService) {
        /*
         * 获取 agent 最近一次心跳对应 agent 启动时间，并对比该时间与系统记录的 agent 启动时间是否一致：
         *  一致：表示 agent 自系统记录的启动时间以来未有启动行为 do nothing.
         *  不一致：表示 agent 自系统记录的启动时间以来存在启动行为，将系统记录的 agent 启动时间记为上一次启动时间，将最近一次心跳对应 agent 启动时间记为系统记录的 agent 启动时间
         * 判断 "系统记录的 agent 启动时间" - "agent 上一次启动时间" < 对应系统设定阈值：
         *  是：进一步判断 "系统记录的 agent 启动时间" 是否由人工触发（人工触发判断条件为："系统记录的 agent 启动时间" 对应 agent 是否存在对应安装、升级类型 AgentOperationTask 执行）TODO：该步骤待实现
         *  否：不存在启动过频
         */
        Long lastestAgentStartupTime = lastMetricsProcess.getProcstartuptime();//agent心跳上报最近一次启动时间
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
