package com.didichuxing.datachannel.agentmanager.core.service;

import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServiceHostPO;

import java.util.List;

public interface ServiceHostManageService {

    /**
     * 保存给定服务 & 主机关联关系集
     * @param serviceHostPOList 待保存服务 & 主机关联关系集
     */
    void createServiceHostList(List<ServiceHostPO> serviceHostPOList);

    /**
     * 根据服务对象id删除其与主机的关联关系集
     */
    void deleteServiceHostByServiceId(Long id);

    /**
     * 根据 service 对象 serviceId 获取对应 service 对象关联的 host 对象数量
     * @param serviceId service 对象 id
     * @return 返回根据 service 对象 serviceId 获取到的对应 service 对象关联的 host 对象数量
     */
    Integer getRelationHostCountByServiceId(Long serviceId);

    /**
     * 根据给定主机对象id删除对应 "服务-主机" 关联关系
     * @param hostId 主机对象id
     */
    void deleteByHostId(Long hostId);

    /**
     * @return 返回系统全量服务 ~ 主机关联关系集
     */
    List<ServiceHostPO> list();

    /**
     * 根据给定id删除对应服务 ~ 主机关联关系
     * @param id 服务 ~ 主机关联关系对象id
     */
    void deleteById(Long id);

    /**
     * 批量增加某个service和host的关联
     *
     * @param serviceId
     * @param hostIds
     * @return
     */
    Integer batchAdd(Long serviceId, List<Long> hostIds);

}
