package com.didichuxing.datachannel.agentmanager.core.agent.health;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsAgentPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;

import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * Agent管理服务接口
 */
public interface AgentHealthManageService {

    /**
     * 根据给定Agent 对象 id 值创建初始AgentHealth对象
     * @param agentId Agent 对象 id 值
     * @param operator 操作人
     * @return 返回创建的Agent健康对象 id 值
     */
    Long createInitialAgentHealth(Long agentId, String operator);

    /**
     * 根据Agent对象id值删除对应AgentHealth对象
     * @param agentId Agent对象id值
     * @param operator 操作人
     */
    void deleteByAgentId(Long agentId, String operator);

    /**
     * 根据agentId获取对应AgentHealthDO对象
     * @param agentId Agent对象id值
     * @return 返回根据agentId获取到的对应AgentHealthDO对象
     */
    AgentHealthDO getByAgentId(Long agentId);

    /**
     * 更新给定AgentHealthDO对象
     * @param agentHealthDO 待更新AgentHealthDO对象
     * @param operator 操作人
     */
    void updateAgentHealth(AgentHealthDO agentHealthDO, String operator);

    /**
     * 检查给定 Agent 健康度，并将检查结果信息更新至表 Agent
     *
     * @param agentDO 待检查 agent 对象
     * @return 返回给定 agent 健康度检查结果，如给定Agent无须被诊断（如：处于 agent check 黑名单），返回 AgentHealthLevelEnum.GREEN
     */
    AgentHealthLevelEnum checkAgentHealth(AgentDO agentDO);

    List<MetricsAgentPO> getErrorDetails(String hostName);

    void solveErrorDetail(Long agentMetricId);

    /**
     *
     * @param heartbeatTime agent metrics 心跳时间
     * @param hostName agent 宿主机名
     * @return 返回给定心跳时间对应心跳周期内 agent 错误日志信息集
     */
    List<String> getErrorLogsInHeartbeatScope(String hostName, Long heartbeatTime);

}
