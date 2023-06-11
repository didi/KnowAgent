package com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.manage.extension;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask.LogRecordVO;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;

import java.util.List;

public interface LogCollectTaskManageServiceExtension {

    /**
     * 将给定logCollectTask对象转化为待持久化LogCollectTaskPO对象
     * @param logCollectTask 待转化 logCollectTask 对象
     * @return logCollectTask转化后得到的LogCollectTaskPO对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    LogCollectTaskPO logCollectTask2LogCollectTaskPO(LogCollectTaskDO logCollectTask) throws ServiceException;

    /**
     * 检测待创建日志采集任务对象 logCollectTask 是否合法
     * @param logCollectTask 待检测日志采集任务对象
     * @return true：合法 false：不合法
     */
    CheckResult checkCreateParameterLogCollectTask(LogCollectTaskDO logCollectTask);

    /**
     * 将给定logCollectTaskPO对象转化为待持久化LogCollectTask对象
     * @param logCollectTaskPO 待转化 logCollectTaskPO 对象
     * @return logCollectTaskPO转化后得到的LogCollectTask对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    LogCollectTaskDO logCollectTaskPO2LogCollectTaskDO(LogCollectTaskPO logCollectTaskPO) throws ServiceException;

    /**
     * 检测待更新日志采集任务对象 logCollectTask 是否合法
     * @param logCollectTaskDO 待检测日志采集任务对象
     * @return true：合法 false：不合法
     *
     * TODO：CheckResult 构建
     *
     */
    CheckResult checkUpdateParameterLogCollectTask(LogCollectTaskDO logCollectTaskDO);

    /**
     * 根据给定源LogCollectTaskDO对象 & 目标LogCollectTaskDO对象，进行对比，得到待更新LogCollectTaskDO对象
     * @param logCollectTaskDOSource 源LogCollectTaskDO对象
     * @param logCollectTaskDO 目标LogCollectTaskDO对象
     * @return 返回待更新LogCollectTaskDO对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    LogCollectTaskDO updateLogCollectTask(LogCollectTaskDO logCollectTaskDOSource, LogCollectTaskDO logCollectTaskDO) throws ServiceException;

    /**
     * 将给定LogCollectTaskPO对象集转化为LogCollectTaskDO对象集
     * @param logCollectTaskPOList LogCollectTaskPO对象集
     * @return 返回将给定LogCollectTaskPO对象集转化为的LogCollectTaskDO对象集
     */
    List<LogCollectTaskDO> logCollectTaskPOList2LogCollectTaskDOList(List<LogCollectTaskPO> logCollectTaskPOList);

    List<LogRecordVO> slice(String content, String sliceTimestampFormat, String sliceTimestampPrefixString, Integer sliceTimestampPrefixStringIndex);

}
