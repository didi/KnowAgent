package com.didichuxing.datachannel.agentmanager.core.service.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServiceHostPO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceHostManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.ServiceHostMapper;
import com.didichuxing.datachannel.agentmanager.thirdpart.service.extension.ServiceHostManageServiceExtension;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceHostManageServiceImpl implements ServiceHostManageService {

    @Autowired
    private ServiceHostMapper serviceHostDAO;

    @Autowired
    private ServiceHostManageServiceExtension serviceHostManageServiceExtension;

    @Override
    @Transactional
    public void createServiceHostList(List<ServiceHostPO> serviceHostPOList) {
        handleCreateServiceHostList(serviceHostPOList);
    }

    @Override
    @Transactional
    public void deleteServiceHostByServiceId(Long id) {
        deleteRemoveServiceHostByServiceId(id);
    }

    /**
     * 根据服务对象id删除其与主机的关联关系集
     * @param serviceId 服务对象 id 值
     */
    private void deleteRemoveServiceHostByServiceId(Long serviceId) {
        serviceHostDAO.deleteByServiceId(serviceId);
    }

    @Override
    public Integer getRelationHostCountByServiceId(Long serviceId) {
        return serviceHostDAO.selectRelationHostCountByServiceId(serviceId);
    }

    @Override
    @Transactional
    public void deleteByHostId(Long hostId) {
        handleDeleteByHostId(hostId);
    }

    @Override
    public List<ServiceHostPO> list() {
        return serviceHostDAO.selectAll();
    }

    @Override
    public void deleteById(Long id) {
        serviceHostDAO.deleteByPrimaryKey(id);
    }

    /**
     * 根据给定主机对象id删除对应"服务-主机"关联关系
     * @param hostId
     */
    private void handleDeleteByHostId(Long hostId) {
        serviceHostDAO.deleteByHostId(hostId);
    }

    /**
     * 保存给定服务 & 主机关联关系集
     * @param serviceHostPOList 待保存服务 & 主机关联关系集
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleCreateServiceHostList(List<ServiceHostPO> serviceHostPOList) throws ServiceException {
        if(null == serviceHostPOList) {
            throw new ServiceException(
                    "入参serviceHostPOList不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        for (ServiceHostPO serviceHostPO : serviceHostPOList) {
            CheckResult checkResult = serviceHostManageServiceExtension.checkCreateParameterServiceHost(serviceHostPO);
            if (checkResult.getCheckResult()) {
                serviceHostDAO.insert(serviceHostPO);
            } else {
                throw new ServiceException(
                        checkResult.getMessage(),
                        checkResult.getCode()
                );
            }
        }
    }

}
