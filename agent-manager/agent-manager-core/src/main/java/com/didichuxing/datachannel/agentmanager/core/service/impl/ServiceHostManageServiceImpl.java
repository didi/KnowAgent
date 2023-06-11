package com.didichuxing.datachannel.agentmanager.core.service.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServiceHostPO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceHostManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.ServiceHostMapper;
import com.didichuxing.datachannel.agentmanager.thirdpart.service.extension.ServiceHostManageServiceExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class ServiceHostManageServiceImpl implements ServiceHostManageService {

    @Autowired
    private ServiceHostMapper serviceHostDAO;

    @Autowired
    private ServiceHostManageServiceExtension serviceHostManageServiceExtension;

    @Autowired
    private ServiceManageService serviceManageService;

    @Autowired
    private HostManageService hostManageService;

    @Override
    @Transactional
    public void createServiceHostList(List<ServiceHostPO> serviceHostPOList) {
        handleCreateServiceHostList(serviceHostPOList);
    }

    /**
     * 根据服务对象id删除其与主机的关联关系集
     *
     * @param serviceId 服务对象 id 值
     */
    @Override
    @Transactional
    public void deleteServiceHostByServiceId(Long serviceId) {
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

    @Override
    public Integer batchAdd(Long serviceId, List<Long> hostIds) {
        List<ServiceHostPO> serviceHostList = new ArrayList<>();
        if (serviceId == null || serviceId <= 0) {
            throw new ServiceException("service id非法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        for (Long hostId : hostIds) {
            if (hostId == null || hostId <= 0) {
                throw new ServiceException("host id非法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
            }
            ServiceHostPO serviceHostPO = new ServiceHostPO();
            serviceHostPO.setServiceId(serviceId);
            serviceHostPO.setHostId(hostId);
            serviceHostList.add(serviceHostPO);
        }
        return serviceHostDAO.batchInsert(serviceHostList);
    }

    @Override
    public List<Long> getRelatedHostIds(Long serviceId) {
        if (serviceId == null || serviceId <= 0) {
            throw new ServiceException("service id非法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        return serviceHostDAO.selectHostIdsByServiceId(serviceId);
    }

    /**
     * 全量替换service关联的host
     *
     * @param serviceId
     * @param hosts
     */
    @Override
    public void replaceHosts(Long serviceId, List<HostDO> hosts) {
        List<Long> hostIds = getRelatedHostIds(serviceId);
        for (Long hostId : hostIds) {
            hostManageService.deleteHost(hostId, true, true, null);
        }
        for (HostDO host : hosts) {
            hostManageService.createHost(host, null);
        }
    }

    /**
     * 根据给定主机对象id删除对应"服务-主机"关联关系
     *
     * @param hostId
     */
    private void handleDeleteByHostId(Long hostId) {
        serviceHostDAO.deleteByHostId(hostId);
    }

    /**
     * 保存给定服务 & 主机关联关系集
     *
     * @param serviceHostPOList 待保存服务 & 主机关联关系集
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleCreateServiceHostList(List<ServiceHostPO> serviceHostPOList) throws ServiceException {
        if (null == serviceHostPOList) {
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
