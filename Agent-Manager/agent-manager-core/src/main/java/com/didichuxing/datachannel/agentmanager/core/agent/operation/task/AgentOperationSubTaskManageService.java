package com.didichuxing.datachannel.agentmanager.core.agent.operation.task;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.operationtask.AgentOperationSubTaskDO;

import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * Agent操作子任务管理服务接口
 *
 * todo：操作记录
 *
 */
public interface AgentOperationSubTaskManageService {

    /**
     * 创建 AgentOperationSubTask 对象
     * @param agentOperationSubTaskDO  待创建 AgentOperationSubTask 对象
     * @param operator 操作人
     * @return 创建成功的 AgentOperationSubTask 对象 id
     */
    Long createAgentOperationSubTask(AgentOperationSubTaskDO agentOperationSubTaskDO, String operator);

    /**
     * 根据 agentOperationTaskId 获取其关联的 AgentOperationSubTaskDO 对象集
     * @param agentOperationTaskId AgentVersion 对象 id
     * @return 返回根据 agentOperationTaskId 获取到的其关联的 AgentOperationSubTaskDO 对象集
     */
    List<AgentOperationSubTaskDO> getByAgentOperationTaskId(Long agentOperationTaskId);

    /**
     * 更新给定 AgentOperationSubTaskDO 对象
     * @param agentOperationSubTaskDO 待更新 AgentOperationSubTaskDO 对象
     * @return 更新是否成功
     */
    void updateAgentOperationSubTask(AgentOperationSubTaskDO agentOperationSubTaskDO);

    /**
     * 校验系统中是否存在给定 agentVersionId 关联的未完成 AgentOperationSubTask 记录
     * @param agentVersionId AgentVersion 对象 id
     * @return true：存在 false：不存在
     */
    boolean unfinishedAgentOperationSubTaskExistsByAgentVersionId(Long agentVersionId);

    /**
     * 根据 hostName & agentStartupTime 查询表 tb_agent_operation_sub_task 存在给定 hostName 的 agent 操作子任务且 agentStartupTime 处于 agent 操作子任务开始时间 ~ 结束时间之内的 agent 操作子任务集
     * @param hostName 主机名
     * @param agentStartupTime agent 启动时间
     * @return 返回根据 hostName & agentStartupTime 查询到的表 tb_agent_operation_sub_task 存在给定 hostName 的 agent 操作子任务且 agentStartupTime 处于 agent 操作子任务开始时间 ~ 结束时间之内的 agent 操作子任务集
     */
    List<AgentOperationSubTaskDO> getByHostNameAndAgentStartupTime(String hostName, Long agentStartupTime);

}
