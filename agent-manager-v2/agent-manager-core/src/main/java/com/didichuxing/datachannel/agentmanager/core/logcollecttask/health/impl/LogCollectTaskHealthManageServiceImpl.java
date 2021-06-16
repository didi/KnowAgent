package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskHealthPO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.LogCollectTaskHealthMapper;
import com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.health.extension.LogCollectTaskHealthManageServiceExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务健康管理服务实现类
 */
@org.springframework.stereotype.Service
public class LogCollectTaskHealthManageServiceImpl implements LogCollectTaskHealthManageService {

    @Autowired
    private LogCollectTaskHealthMapper logCollectTaskHealthDAO;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private LogCollectTaskHealthManageServiceExtension logCollectTaskHealthManageServiceExtension;

    @Override
    @Transactional
    public Long createInitialLogCollectorTaskHealth(Long logCollectTaskId, String operator) {
        return this.handleCreateInitialLogCollectorTaskHealth(logCollectTaskId, operator);
    }

    @Override
    @Transactional
    public void deleteByLogCollectTaskId(Long logCollectTaskId, String operator) {
        this.handleDeleteByLogCollectTaskId(logCollectTaskId, operator);
    }

    @Override
    public LogCollectTaskHealthDO getByLogCollectTaskId(Long logCollectTaskId) {
        LogCollectTaskHealthPO logCollectTaskHealthPO = logCollectTaskHealthDAO.selectByLogCollectTaskId(logCollectTaskId);
        if (null == logCollectTaskHealthPO) {
            return null;
        } else {
            return logCollectTaskHealthManageServiceExtension.logCollectTaskHealthPO2LogCollectTaskHealthDO(logCollectTaskHealthPO);
        }
    }

    @Override
    @Transactional
    public void updateLogCollectorTaskHealth(LogCollectTaskHealthDO logCollectTaskHealthDO, String operator) {
        this.handleUpdateLogCollectorTaskHealth(logCollectTaskHealthDO, operator);
    }

    /**
     * 更新LogCollectTaskHealthDO
     * @param logCollectTaskHealthDO 待更新LogCollectTaskHealthDO对象
     * @param operator 操作人
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleUpdateLogCollectorTaskHealth(LogCollectTaskHealthDO logCollectTaskHealthDO, String operator) throws ServiceException {
        if(null == logCollectTaskHealthDAO.selectByPrimaryKey(logCollectTaskHealthDO.getId())) {
            throw new ServiceException(
                    String.format("LogCollectTaskHealth={id=%d}在系统中不存在", logCollectTaskHealthDO.getId()),
                    ErrorCodeEnum.LOGCOLLECTTASK_HEALTH_NOT_EXISTS.getCode()
            );
        }
        LogCollectTaskHealthPO logCollectorTaskHealthPO = logCollectTaskHealthManageServiceExtension.logCollectTaskHealthDO2LogCollectTaskHealthPO(logCollectTaskHealthDO);
        logCollectorTaskHealthPO.setOperator(CommonConstant.getOperator(operator));
        logCollectTaskHealthDAO.updateByPrimaryKey(logCollectorTaskHealthPO);
    }

    /**
     * 根据给定日志采集任务id创建初始日志采集任务健康对象流程
     * @param logCollectTaskId 日志采集任务id
     * @param operator 操作人
     * @return 返回根据给定日志采集任务id创建的初始日志采集任务健康对象id
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long handleCreateInitialLogCollectorTaskHealth(Long logCollectTaskId, String operator) throws ServiceException {
        LogCollectTaskDO logCollectTaskDO = logCollectTaskManageService.getById(logCollectTaskId);
        if(null == logCollectTaskDO) {
            throw new ServiceException(
                    String.format("系统中不存在id={%d}的LogCollectTask对象", logCollectTaskId),
                    ErrorCodeEnum.LOGCOLLECTTASK_NOT_EXISTS.getCode()
            );
        }
        LogCollectTaskHealthPO logCollectorTaskHealthPO = logCollectTaskHealthManageServiceExtension.buildInitialLogCollectorTaskHealthPO(logCollectTaskDO, operator);
        logCollectTaskHealthDAO.insert(logCollectorTaskHealthPO);
        return logCollectorTaskHealthPO.getId();
    }

    /**
     * 根据给定日志采集任务 id 删除对应日志采集任务关联的日志采集任务健康对象流程
     * @param logCollectTaskId 日志采集任务 id
     * @param operator 操作人
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleDeleteByLogCollectTaskId(Long logCollectTaskId, String operator) throws ServiceException {
        LogCollectTaskHealthPO logCollectorTaskHealthPO = logCollectTaskHealthDAO.selectByLogCollectTaskId(logCollectTaskId);
        if(null == logCollectorTaskHealthPO) {
            throw new ServiceException(
                    String.format("根据给定日志采集任务id={%d}删除对应日志采集任务健康对象失败，原因为：系统中不存在logCollectTaskId为{%d}的LogCollectorTaskHealthPO对象", logCollectTaskId, logCollectTaskId),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        logCollectTaskHealthDAO.deleteByLogCollectTaskId(logCollectTaskId);
    }

}
