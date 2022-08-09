package com.didichuxing.datachannel.agentmanager.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServicePaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServicePaginationRecordDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServicePO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServiceHostPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServiceProjectPO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.SourceEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.ModuleEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.OperationEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.service.ServiceTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.core.common.OperateRecordService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceLogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceProjectManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.ServiceMapper;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceHostManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import com.didichuxing.datachannel.agentmanager.thirdpart.service.extension.ServiceManageServiceExtension;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class ServiceManageServiceImpl implements ServiceManageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceManageServiceImpl.class);

    @Autowired
    private ServiceMapper serviceDAO;

    @Autowired
    private ServiceManageServiceExtension serviceManageServiceExtension;

    @Autowired
    private ServiceHostManageService serviceHostManageService;

    @Autowired
    private ServiceLogCollectTaskManageService serviceLogCollectTaskManageService;

    @Autowired
    private OperateRecordService operateRecordService;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private ServiceProjectManageService serviceProjectManageService;

    @Autowired
    private HostManageService hostManageService;

    @Override
    @Transactional
    public Long createService(ServiceDO service, String operator) {
        return handleCreateService(service, operator);
    }

    @Override
    public ServiceDO getServiceByServiceName(String serviceName) {
        ServicePO servicePO = serviceDAO.selectByServiceName(serviceName);
        if(null == servicePO) {
            return null;
        } else {
            return serviceManageServiceExtension.service2ServiceDO(servicePO);
        }
    }

    @Override
    @Transactional
    public void updateService(ServiceDO serviceDO, String operator) {
        handleUpdateService(serviceDO, operator);
    }

    @Override
    @Transactional
    public void deleteServices(List<Long> serviceIdList, boolean cascadeDeleteHostAndLogCollectTaskRelation, String operator) {
        for (Long serviceId : serviceIdList) {
            handleDeleteService(serviceId, cascadeDeleteHostAndLogCollectTaskRelation, operator);
        }
    }

    @Override
    public List<ServiceDO> list() {
        List<ServicePO> servicePOList = serviceDAO.list();
        if(CollectionUtils.isNotEmpty(servicePOList)) {
            return serviceManageServiceExtension.servicePOList2serviceDOList(servicePOList);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public ServiceDO getServiceById(Long id) {
        ServicePO servicePO = serviceDAO.selectByPrimaryKey(id);
        if(null == servicePO) {
            return null;
        } else {
            return serviceManageServiceExtension.service2ServiceDO(servicePO);
        }
    }

    @Override
    public List<ServiceDO> getServicesByHostId(Long hostId) {
        List<ServicePO> servicePOList = serviceDAO.selectByHostId(hostId);
        if(CollectionUtils.isEmpty(servicePOList)) {
            return new ArrayList<>();
        } else {
            return serviceManageServiceExtension.servicePOList2serviceDOList(servicePOList);
        }
    }

    @Override
    public List<ServiceDO> getServicesByProjectId(Long projectId) {
        List<ServicePO> servicePOList = serviceDAO.selectByProjectId(projectId);
        if(CollectionUtils.isEmpty(servicePOList)) {
            return new ArrayList<>();
        } else {
            return serviceManageServiceExtension.servicePOList2serviceDOList(servicePOList);
        }
    }

    @Override
    public List<ServiceDO> getServicesByLogCollectTaskId(Long logCollectTaskId) {
        List<ServicePO> servicePOList = serviceDAO.selectByLogCollectTaskId(logCollectTaskId);
        if(CollectionUtils.isEmpty(servicePOList)) {
            return new ArrayList<>();
        } else {
            return serviceManageServiceExtension.servicePOList2serviceDOList(servicePOList);
        }
    }

    @Override
    public List<ServicePaginationRecordDO> paginationQueryByConditon(ServicePaginationQueryConditionDO query) {
        String column = query.getSortColumn();
        if (column != null) {
            for (char c : column.toCharArray()) {
                if (!Character.isLetter(c) && c != '_') {
                    return Collections.emptyList();
                }
            }
        }
        return serviceDAO.paginationQueryByConditon(query);
    }

    @Override
    public Integer queryCountByCondition(ServicePaginationQueryConditionDO servicePaginationQueryConditionDO) {
        return serviceDAO.queryCountByCondition(servicePaginationQueryConditionDO);
    }

    @Override
    public Long countAll() {
        return serviceDAO.countAll();
    }

    /**
     * @param serviceName2ProjectServiceNameMap 服务节点名 ~ 服务节点所属项目节点名关联关系集
     * @param serviceDOListInLocal 系统全量服务节点对象集
     * @return 根据 serviceName2ProjectServiceNameMap & serviceDOListInLocal 构建 "服务节点 id ~ 服务节点所属项目节点 id " 关联关系集
     */
    private List<ServiceProjectPO> buildServiceProjectRelation(Map<String, String> serviceName2ProjectServiceNameMap, List<ServiceDO> serviceDOListInLocal) {
        List<ServiceProjectPO> serviceProjectPOList = new ArrayList<>();
        Map<String, ServiceDO> serviceName2ServiceDOMap = new HashMap<>();
        for (ServiceDO serviceDO : serviceDOListInLocal) {
            serviceName2ServiceDOMap.put(serviceDO.getServicename(), serviceDO);
        }
        for (Map.Entry<String, String> entry : serviceName2ProjectServiceNameMap.entrySet()) {
            String serviceName = entry.getKey();
            String projectServiceName = entry.getValue();
            ServiceDO serviceDO = serviceName2ServiceDOMap.get(serviceName);
            ServiceDO projectServiceDO = serviceName2ServiceDOMap.get(projectServiceName);
            if(null != projectServiceDO && null != serviceDO) {
                ServiceProjectPO serviceProjectPO = new ServiceProjectPO(serviceDO.getId(), projectServiceDO.getExtenalServiceId());
                serviceProjectPOList.add(serviceProjectPO);
            }
        }
        return serviceProjectPOList;
    }

    /**
     * 根据给定入参 serviceDOList 构建各服务节点 ~ 该服务节点名对应所属项目节点名关联关系
     * @param serviceDOList 服务节点集
     * @return 返回根据给定入参 serviceDOList 构建的各服务节点 ~ 该服务节点名对应所属项目节点名关联关系
     */
    private Map<String, String> buildServiceName2ProjectServiceNameRelation(List<ServiceDO> serviceDOList) {
        Map<String, String> serviceName2ProjectServiceNameMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(serviceDOList)) {
            Map<Long, ServiceDO> externalServiceId2ServiceDOMap = new HashMap<>();
            for (ServiceDO serviceDO : serviceDOList) {
                externalServiceId2ServiceDOMap.put(serviceDO.getExtenalServiceId(), serviceDO);
            }
            for (ServiceDO serviceDO : serviceDOList) {
                ServiceDO projectServiceDO = getProjectServiceDO(serviceDO, externalServiceId2ServiceDOMap);
                if(null != projectServiceDO) {
                    serviceName2ProjectServiceNameMap.put(serviceDO.getServicename(), projectServiceDO.getServicename());
                }
            }
            return serviceName2ProjectServiceNameMap;
        } else {
            return serviceName2ProjectServiceNameMap;
        }
    }

    /**
     * 获取给定服务对象对应所属项目节点
     * @param serviceDO 服务对象
     * @param externalServiceId2ServiceDOMap externalServiceId : serviceDO 关联关系
     * @return 获取给定服务对象对应所属项目节点 如给定服务节点不存在所属项目节点 return null
     */
    private ServiceDO getProjectServiceDO(ServiceDO serviceDO, Map<Long, ServiceDO> externalServiceId2ServiceDOMap) {
        if(null == externalServiceId2ServiceDOMap || null == serviceDO) {
            return null;
        } else {
            String cate = serviceDO.getCate();
            if(StringUtils.isNotBlank(cate)) {
                if(cate.equals(ServiceTypeEnum.项目.getDescription())) {
                    return serviceDO;
                } else if(ServiceTypeEnum.subOfProject(cate)) {//项目节点对应子节点
                    Long pid = serviceDO.getPid();
                    if(null != pid) {
                        ServiceDO parentServiceDO = externalServiceId2ServiceDOMap.get(pid);
                        return getProjectServiceDO(parentServiceDO, externalServiceId2ServiceDOMap);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }

            } else {
                return null;
            }
        }
    }

    /**
     * 删除服务
     * @param serviceId 待删除服务对象id
     * @param cascadeDeleteHostAndLogCollectTaskRelation 是否级联删除 Service & LogCollectTask 关联关系
     * @param operator 操作人
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleDeleteService(Long serviceId, boolean cascadeDeleteHostAndLogCollectTaskRelation, String operator) throws ServiceException {
        /*
         * 校验待删除service对象在系统是否存在
         */
        if(null == getServiceById(serviceId)) {
            throw new ServiceException(
                    "删除失败：待删除应用在系统中不存在",
                    ErrorCodeEnum.SERVICE_NOT_EXISTS.getCode()
            );
        }
        /*
         * 处理待删除 service & logcollecttask、service & host 关联关系
         * 如非级联删除，将校验待删除 service 是否存在关联 logcollecttask & host
         */
        if(cascadeDeleteHostAndLogCollectTaskRelation) {//级联删除
            /*
             * 删除服务-日志采集任务关联关系
             */
            serviceLogCollectTaskManageService.removeServiceLogCollectTaskByServiceId(serviceId);
            /*
             * 删除服务-主机关联关系
             */
            serviceHostManageService.deleteServiceHostByServiceId(serviceId);
        } else {//不级联删除，此时须校验
            List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageService.getLogCollectTaskListByServiceId(serviceId);
            if(CollectionUtils.isNotEmpty(logCollectTaskDOList)) {//待删除service存在关联logcollecttask
                throw new ServiceException(
                        String.format("删除失败：待删除应用存在%d个关联的采集任务", logCollectTaskDOList.size()),
                        ErrorCodeEnum.SERVICE_DELETE_FAILED_CAUSE_BY_RELA_LOGCOLLECTTASK_EXISTS.getCode()
                );
            }
            List<HostDO> hostDOList = hostManageService.getHostsByServiceId(serviceId);
            if(CollectionUtils.isNotEmpty(hostDOList)) {//待删除service存在关联 host
                throw new ServiceException(
                        String.format("删除失败：待删除应用存在%d个关联的主机", hostDOList.size()),
                        ErrorCodeEnum.SERVICE_DELETE_FAILED_CAUSE_BY_RELA_HOST_EXISTS.getCode()
                );
            }
        }
        /*
         * 删除服务对象
         */
        serviceDAO.deleteByPrimaryKey(serviceId);
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.SERVICE,
                OperationEnum.DELETE,
                serviceId,
                String.format("删除Service对象={id={%d}}", serviceId),
                operator
        );
    }

    /**
     * 修改服务，服务仅可修改 Service & Host 关联关系
     * @param serviceDO 待修改服务对象
     * @param operator 操作人
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleUpdateService(ServiceDO serviceDO, String operator) throws ServiceException {
        /*
         * 校验入参
         */
        CheckResult checkResult = serviceManageServiceExtension.checkUpdateParameterService(serviceDO);
        if(!checkResult.getCheckResult()) {
            throw new ServiceException(
                    checkResult.getMessage(),
                    checkResult.getCode()
            );
        }
        /*
         * 校验待更新服务对象在系统中是否存在
         */
        if(null == getServiceById(serviceDO.getId())) {
            throw new ServiceException(
                    String.format("待更新Service对象={id=%d}在系统中不存在", serviceDO.getId()),
                    ErrorCodeEnum.SERVICE_NOT_EXISTS.getCode()
            );
        }
        /*
         * 重构 servicePO & host 关联关系
         */
        serviceHostManageService.deleteServiceHostByServiceId(serviceDO.getId());
        List<Long> hostIdList = serviceDO.getHostIdList();
        if(CollectionUtils.isNotEmpty(hostIdList)) {
            saveServiceHostRelation(serviceDO.getId(), hostIdList);
        }
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.SERVICE,
                OperationEnum.EDIT,
                serviceDO.getId(),
                String.format("修改Service={%s}，修改成功的Service对象id={%d}", JSON.toJSONString(serviceDO), serviceDO.getId()),
                operator
        );
    }

    /**
     * 创建给定 serviceDO 对象流程
     * @param serviceDO 待创建 serviceDO 对象
     * @param operator 操作人
     * @return 返回持久化的 serviceDO 对象对应 id 值
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long handleCreateService(ServiceDO serviceDO, String operator) throws ServiceException {
        /*
         * 校验入参
         */
        CheckResult checkResult = serviceManageServiceExtension.checkCreateParameterService(serviceDO);
        if(!checkResult.getCheckResult()) {
            throw new ServiceException(
                    checkResult.getMessage(),
                    checkResult.getCode()
            );
        }
        /*
         * 校验待创建Service对象对应serviceName在系统中是否已存在
         */
        if(null != getServiceByServiceName(serviceDO.getServicename())) {
            throw new ServiceException(
                    String.format("待创建Service对象对应serviceName={%s}在系统中已存在", serviceDO.getServicename()),
                    ErrorCodeEnum.SERVICE_NAME_DUPLICATE.getCode()
            );
        }
        /*
         * 持久化 service
         */
        ServicePO servicePO = serviceManageServiceExtension.serviceDO2Service(serviceDO);
        servicePO.setOperator(CommonConstant.getOperator(operator));
        serviceDAO.insert(servicePO);
        Long serviceId = servicePO.getId();
        /*
         * 构建 service & host 关联关系
         */
        if(CollectionUtils.isNotEmpty(serviceDO.getHostIdList())) {
            saveServiceHostRelation(serviceId, serviceDO.getHostIdList());
        }
        /*
         * 构建 service & project 关联关系
         */
        if(CollectionUtils.isNotEmpty(serviceDO.getProjectIdList())) {
            saveServiceProjectRelation(serviceId, serviceDO.getProjectIdList());
        }
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.SERVICE,
                OperationEnum.ADD,
                serviceId,
                String.format("创建Service={%s}，创建成功的Service对象id={%d}", JSON.toJSONString(serviceDO), serviceId),
                operator
        );
        return serviceId;
    }

    /**
     * 根据给定 serviceId & projectIdList 持久化对应服务 & 项目关联关系
     * @param serviceId 服务id
     * @param projectIdList 项目id集
     */
    private void saveServiceProjectRelation(Long serviceId, List<Long> projectIdList) {
        List<ServiceProjectPO> serviceProjectPOList = new ArrayList<>(projectIdList.size());
        for (Long projectId : projectIdList) {
            serviceProjectPOList.add(new ServiceProjectPO(serviceId, projectId));
        }
        serviceProjectManageService.createServiceProjectList( serviceProjectPOList );
    }

    /**
     * 根据给定 serviceId & hostIdList 持久化对应服务 & 主机关联关系
     * @param serviceId 服务id
     * @param hostIdList 主机id集
     */
    private void saveServiceHostRelation(Long serviceId, List<Long> hostIdList) {
        List<ServiceHostPO> serviceHostPOList = new ArrayList<>(hostIdList.size());
        for (Long hostId : hostIdList) {
            serviceHostPOList.add(new ServiceHostPO(serviceId, hostId));
        }
        serviceHostManageService.createServiceHostList( serviceHostPOList );
    }

}
