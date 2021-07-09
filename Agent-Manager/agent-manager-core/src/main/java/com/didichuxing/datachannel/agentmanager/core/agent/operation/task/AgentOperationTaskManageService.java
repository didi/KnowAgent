package com.didichuxing.datachannel.agentmanager.core.agent.operation.task;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.operationtask.AgentOperationTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.operationtask.AgentOperationTaskPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskSubStateEnum;

import java.util.List;
import java.util.Map;

/**
 * @author huqidong
 * @date 2020-09-21
 * Agent操作任务管理服务接口
 *
 * todo：操作记录
 *
 */
public interface AgentOperationTaskManageService {

    /**
     * 创建一个 AgentOperationTaskPO 对象
     * @param agentOperationTask  待创建 AgentOperationTaskPO 对象
     * @param operator 操作人
     * @return 创建成功的agentOperationTask对象id
     *
     * TODO：容器安装 提交任务时须区分主机类型，对于 容器 类型 安装，调用不通接口
     *
     */
    Long createAgentOperationTask(AgentOperationTaskDO agentOperationTask, String operator);

    /**
     * 根据给定Agent操作任务id获取对应Agent操作任务对象
     * @param taskId Agent操作任务id
     * @return 返回给定Agent操作任务id对应Agent操作任务对象
     */
    AgentOperationTaskDO getById(Long taskId);

    /**
     * 获取给定Agent操作任务id对应Agent操作任务在给定主机名对应主机上日志信息
     * @param taskId Agent操作任务id
     * @param hostname 主机名
     * @return 返回给定Agent操作任务id对应Agent操作任务在给定主机名对应主机上日志信息
     */
    String getTaskLog(Long taskId, String hostname);

    /**
     * 根据给定查询条件进行对应分页查询
     * @param agentOperationTaskPaginationQueryConditionDO 封装分页查询条件对象
     * @return 返回根据给定查询条件进行对应分页查询结果集
     */
    List<AgentOperationTaskDO> paginationQueryByConditon(AgentOperationTaskPaginationQueryConditionDO agentOperationTaskPaginationQueryConditionDO);

    /**
     * 根据查询条件查询满足条件结果集数量
     * @param agentOperationTaskPaginationQueryConditionDO 封装查询条件对象
     * @return 返回根据查询到的条件查询满足条件结果集数量
     */
    Integer queryCountByCondition(AgentOperationTaskPaginationQueryConditionDO agentOperationTaskPaginationQueryConditionDO);

    /**
     * 根据给定外部任务id获取任务执行结果
     * @param taskId 外部任务ID
     * @return 任务结果
     */
    Map<String, AgentOperationTaskSubStateEnum> getTaskResultByExternalTaskId(Long taskId);

    /**
     * 更新系统所有 agent 操作任务状态
     * TODO：批量、升级、卸载对应 insert、updateAgentHealth 批量
     */
    void updateAgentOperationTasks();

    /**
     * 校验系统中是否存在给定 agentVersionId 关联的未完成 AgentOperationTask 记录
     * @param agentVersionId AgentVersion 对象 id
     * @return true：存在 false：不存在
     */
    boolean unfinishedAgentOperationTaskExistsByAgentVersionId(Long agentVersionId);

    /**
     * 根据给定agent对象构造一个用于卸载该agent的AgentOperationTask对象
     * @param agentDO 待卸载 agent 对象
     * @return 返回根据给定agent对象构造的一个用于卸载该agent的AgentOperationTask对象
     */
    AgentOperationTaskDO agent2AgentOperationTaskUnInstall(AgentDO agentDO);

}
