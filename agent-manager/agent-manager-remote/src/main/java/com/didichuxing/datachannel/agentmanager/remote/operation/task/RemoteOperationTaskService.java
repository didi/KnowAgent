package com.didichuxing.datachannel.agentmanager.remote.operation.task;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.*;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskActionEnum;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskStateEnum;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskSubStateEnum;

import java.util.Map;

/**
 * LogAgent操作任务接口
 * @author zengqiao
 * @date 2021/01/13
 */
public interface RemoteOperationTaskService {
    /**
     * 创建任务
     * @param agentOperationTaskCreation 创建任务参数
     * @return 任务ID
     */
    Result<Long> createTask(AgentOperationTaskCreation agentOperationTaskCreation);

    /**
     * 执行任务
     * @param taskId 任务ID
     * @param actionEnum 执行动作
     * @return true:触发成功, false:触发失败
     */
    Boolean actionTask(Long taskId, AgentOperationTaskActionEnum actionEnum);

    /**
     * 执行任务
     * @param taskId 任务ID
     * @param actionEnum 执行动作
     * @param hostname 具体主机
     * @return true:触发成功, false:触发失败
     */
    Boolean actionHostTask(Long taskId, AgentOperationTaskActionEnum actionEnum, String hostname);

    /**
     * 获取任务运行的状态[阻塞, 执行中, 完成等]
     * @param taskId 任务ID
     * @return 任务状态
     */
    Result<AgentOperationTaskStateEnum> getTaskExecuteState(Long taskId);

    /**
     * 获取任务结果
     * @param taskId 任务ID
     * @return 任务结果
     */
    Result<Map<String, AgentOperationTaskSubStateEnum>> getTaskResult(Long taskId);

    /**
     * 获取任务执行日志
     * @param taskId 任务ID
     * @param hostname 具体主机
     * @return 机器运行日志
     */
    Result<AgentOperationTaskLog> getTaskLog(Long taskId, String hostname);
}
