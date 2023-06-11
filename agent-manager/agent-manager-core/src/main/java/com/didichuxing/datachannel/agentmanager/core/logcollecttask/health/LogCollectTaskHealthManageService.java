package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;

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

    /**
     * 检查给定日志采集任务健康度，具体包括如下操作：
     *  1.）检查给定日志采集任务健康度，并将检查结果信息存入对应 LogCollectTaskHealth 记录表
     *  2.）计算给定日志采集任务各采集路径的采集完整性时间，并将其存入对应 LogCollectTaskHealth 记录表
     *  3.）如给定日志采集任务对应采集类型为 "时间范围采集" 类型，则判断其是否已采集完毕，如已采集完毕，更新对应 LogCollectTask 记录 log_collect_task_finish_time 字段值为当前时间
     * @param logCollectTaskDO 待检查日志采集任务对象
     * @return 返回给定日志采集任务健康度检查结果 注：如待巡检日志采集任务处于暂停、或已完成、或处于健康度检查黑名单中，无需检查，return LogCollectTaskHealthLevelEnum.GREEN
     */
    LogCollectTaskHealthLevelEnum checkLogCollectTaskHealth(LogCollectTaskDO logCollectTaskDO);

}
