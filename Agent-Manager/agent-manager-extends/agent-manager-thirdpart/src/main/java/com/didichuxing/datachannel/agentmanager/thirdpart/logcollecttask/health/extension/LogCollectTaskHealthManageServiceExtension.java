package com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.health.extension;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskHealthPO;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;

public interface LogCollectTaskHealthManageServiceExtension {

    /**
     * 根据给定日志采集任务id构建该日志采集任务对应初始日志采集任务健康对象
     * @param logCollectTaskDO 日志采集任务对象
     * @param operator 操作人
     * @return 根据给定日志采集任务id构建的该日志采集任务对应初始日志采集任务健康对象
     */
    LogCollectTaskHealthPO buildInitialLogCollectorTaskHealthPO(LogCollectTaskDO logCollectTaskDO, String operator);

    /**
     * 将给定LogCollectTaskHealthPO对象转化为LogCollectTaskHealthDO对象
     * @param logCollectTaskHealthPO 待转化LogCollectTaskHealthPO对象
     * @return 返回将给定LogCollectTaskHealthPO对象转化为的LogCollectTaskHealthDO对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    LogCollectTaskHealthDO logCollectTaskHealthPO2LogCollectTaskHealthDO(LogCollectTaskHealthPO logCollectTaskHealthPO);

    /**
     * 将给定LogCollectTaskHealthDO对象转化为LogCollectTaskHealthPO对象
     * @param logCollectTaskHealthDO 待转化LogCollectTaskHealthDO对象
     * @return 返回将给定LogCollectTaskHealthDO对象转化为的LogCollectTaskHealthPO对象
     */
    LogCollectTaskHealthPO logCollectTaskHealthDO2LogCollectTaskHealthPO(LogCollectTaskHealthDO logCollectTaskHealthDO);

}
