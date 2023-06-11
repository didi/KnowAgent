package com.didichuxing.datachannel.agentmanager.core.service.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskServicePO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceLogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.LogCollectTaskServiceMapper;
import com.didichuxing.datachannel.agentmanager.thirdpart.service.extension.ServiceLogCollectTaskManageServiceExtension;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceLogCollectTaskManageServiceImpl implements ServiceLogCollectTaskManageService {

    @Autowired
    private LogCollectTaskServiceMapper logCollectTaskServiceDAO;

    @Autowired
    private ServiceLogCollectTaskManageServiceExtension serviceLogCollectTaskManageServiceExtension;

    @Override
    @Transactional
    public void createLogCollectTaskServiceList(List<LogCollectTaskServicePO> logCollectTaskServicePOList) {
        handleCreateLogCollectTaskServiceList(logCollectTaskServicePOList);
    }

    @Override
    @Transactional
    public void removeServiceLogCollectTaskByServiceId(Long serviceId) {
        handleRemoveServiceLogCollectTaskByServiceId(serviceId);
    }

    @Override
    @Transactional
    public void removeServiceLogCollectTaskByLogCollectTaskId(Long logCollectTaskId) {
        handleRemoveServiceLogCollectTaskByLogCollectTaskId(logCollectTaskId);
    }

    /**
     * 根据logCollectTask对象id删除该logCollectTask对象关联的所有LogCollectTaskService对象集
     * @param logCollectTaskId logCollectTask 对象 id
     */
    private void handleRemoveServiceLogCollectTaskByLogCollectTaskId(Long logCollectTaskId) {
        logCollectTaskServiceDAO.deleteByLogCollectTaskId(logCollectTaskId);
    }

    /**
     * 根据service对象id删除该service对象关联的所有LogCollectTaskService对象集
     * @param serviceId service 对象 id
     */
    private void handleRemoveServiceLogCollectTaskByServiceId(Long serviceId) {
        logCollectTaskServiceDAO.deleteByServiceId(serviceId);
    }

    /**
     * 保存给定服务 & 日志采集任务关联关系集
     * @param logCollectTaskServicePOList 待保存服务 & 日志采集任务关联关系集
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleCreateLogCollectTaskServiceList(List<LogCollectTaskServicePO> logCollectTaskServicePOList) throws ServiceException {
        if(CollectionUtils.isEmpty(logCollectTaskServicePOList)) {
            throw new ServiceException(
                    "入参logCollectTaskServicePOList为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        for (LogCollectTaskServicePO logCollectTaskServicePO : logCollectTaskServicePOList) {
            CheckResult checkResult = serviceLogCollectTaskManageServiceExtension.checkCreateParameterLogCollectTaskService( logCollectTaskServicePO );
            if (checkResult.getCheckResult()) {
                logCollectTaskServiceDAO.insert(logCollectTaskServicePO);
            } else {
                throw new ServiceException(
                        checkResult.getMessage(),
                        checkResult.getCode()
                );
            }
        }
    }

}
