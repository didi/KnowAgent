package com.didichuxing.datachannel.agentmanager.core.service.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServiceProjectPO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceProjectManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.ServiceProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceProjectManageServiceImpl implements ServiceProjectManageService {

    @Autowired
    private ServiceProjectMapper serviceProjectDAO;

    @Override
    @Transactional
    public void createServiceProjectList(List<ServiceProjectPO> serviceProjectPOList) {
        handleCreateServiceProjectList(serviceProjectPOList);
    }

    @Override
    public List<ServiceProjectPO> list() {
        return serviceProjectDAO.selectAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        serviceProjectDAO.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public void deleteByServiceId(Long serviceId) {
        serviceProjectDAO.deleteByServiceId(serviceId);
    }

    /**
     * 保存给定服务 & 项目关联关系集
     * @param serviceProjectPOList 待保存服务 & 项目关联关系集
     */
    private void handleCreateServiceProjectList(List<ServiceProjectPO> serviceProjectPOList) {
        if(null == serviceProjectPOList) {
            throw new ServiceException(
                    "入参serviceProjectPOList不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        for (ServiceProjectPO serviceProjectPO : serviceProjectPOList) {
            serviceProjectDAO.insert(serviceProjectPO);
        }
    }

}

