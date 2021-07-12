package com.didichuxing.datachannel.agentmanager.core.service;

import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServiceProjectPO;

import java.util.List;

public interface ServiceProjectManageService {

    /**
     * 保存给定服务 & 项目关联关系集
     * @param serviceProjectPOList 待保存服务 & 项目关联关系集
     */
    void createServiceProjectList(List<ServiceProjectPO> serviceProjectPOList);

    /**
     * @return 返回系统全量 "服务 ~ 项目" 关联关系集
     */
    List<ServiceProjectPO> list();

    /**
     * @param id 待删除 ServiceProjectPO 对象 id
     */
    void deleteById(Long id);

    /**
     * 根据serviceId删除所有关联该服务 服务 ~ 项目 关联关系集
     * @param serviceId 服务对象 id
     */
    void deleteByServiceId(Long serviceId);

}
