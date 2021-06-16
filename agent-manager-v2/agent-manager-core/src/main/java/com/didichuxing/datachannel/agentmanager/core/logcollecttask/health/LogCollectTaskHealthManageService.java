package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDO;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务健康管理服务接口
 */
public interface LogCollectTaskHealthManageService {

    /**
     * 根据给定日志采集任务id创建初始日志采集任务健康对象
     * @param logCollectTaskId 日志采集任务id
     * @param operator 操作人
     * @return 返回根据给定日志采集任务id创建的初始日志采集任务健康对象id
     */
    Long createInitialLogCollectorTaskHealth(Long logCollectTaskId, String operator);

    /**
     * 根据给定日志采集任务id删除对应日志采集任务健康对象
     * @param logCollectTaskId 日志采集任务id
     * @param operator 操作人
     * @return 删除操作结果
     */
    void deleteByLogCollectTaskId(Long logCollectTaskId, String operator);

    /**
     * 根据日志采集任务 id 查询该日志采集任务对应日志采集任务健康对象
     * @param logCollectTaskId 日志采集任务 id
     * @return 返回根据日志采集任务 id 查询到的该日志采集任务对应日志采集任务健康对象
     */
    LogCollectTaskHealthDO getByLogCollectTaskId(Long logCollectTaskId);

    /**
     * 更新LogCollectTaskHealthDO
     * @param logCollectTaskHealthDO 待更新LogCollectTaskHealthDO对象
     * @param operator 操作人
     */
    void updateLogCollectorTaskHealth(LogCollectTaskHealthDO logCollectTaskHealthDO, String operator);

}
