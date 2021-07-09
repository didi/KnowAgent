package com.didichuxing.datachannel.agentmanager.core.service;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskServicePO;

import java.util.List;

public interface ServiceLogCollectTaskManageService {

    /**
     * 保存给定服务 & 日志采集任务关联关系集
     * @param logCollectTaskServicePOList 待保存服务 & 日志采集任务关联关系集
     */
    void createLogCollectTaskServiceList(List<LogCollectTaskServicePO> logCollectTaskServicePOList);

    /**
     * 根据service对象id删除该service对象关联的所有LogCollectTaskService对象集
     * @param serviceId service 对象 id
     */
    void removeServiceLogCollectTaskByServiceId(Long serviceId);

    /**
     * 根据logCollectTask对象id删除该logCollectTask对象关联的所有LogCollectTaskService对象集
     * @param logCollectTaskId logCollectTask 对象 id
     */
    void removeServiceLogCollectTaskByLogCollectTaskId(Long logCollectTaskId);

}
