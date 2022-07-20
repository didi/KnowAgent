package com.didichuxing.datachannel.agentmanager.core.host.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostAgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.host.HostAgentPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.host.HostPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServiceHostPO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.SourceEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentCollectTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.host.HostTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.ModuleEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.OperationEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.NetworkUtil;
import com.didichuxing.datachannel.agentmanager.core.agent.configuration.AgentCollectConfigManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.common.OperateRecordService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthDetailManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceHostManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.HostMapper;
import com.didichuxing.datachannel.agentmanager.thirdpart.host.extension.HostManageServiceExtension;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huqidong
 * @date 2020-09-21
 * 主机处理流程模板类
 */
@org.springframework.stereotype.Service
public class HostManageServiceImpl implements HostManageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostManageServiceImpl.class);

    @Autowired
    private HostMapper hostDAO;

    @Autowired
    private ServiceHostManageService serviceHostManageService;

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private ServiceManageService serviceManageService;

    @Autowired
    private HostManageServiceExtension hostManageServiceExtension;

    @Autowired
    private AgentCollectConfigManageService agentCollectConfigManageService;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private OperateRecordService operateRecordService;

    @Autowired
    private LogCollectTaskHealthDetailManageService logCollectTaskHealthDetailManageService;

    /**
     * 根据主机id获取对应主机对象
     * @param hostId 主机id
     * @return 根据主机id获取到的对应主机对象
     * @throws ServiceException 执行 "根据主机id获取对应主机对象" 过程中出现的异常
     */
    private HostDO getHostById(Long hostId) throws ServiceException {
        if (null == hostId) {
            throw new ServiceException(
                    "入参hostId不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        HostPO hostPO = hostDAO.selectByPrimaryKey(hostId);
        if(null == hostPO) {
            return null;
        } else {
            return hostManageServiceExtension.hostPO2HostDO(hostPO);
        }
    }

    /**
     * 创建一个主机信息，在主机添加操作成功后，自动创建主机关联的服务信息、主机 & 服务关联关系、采集任务 & 服务关联关系
     * @param host 主机对象
     * @param operator 操作人
     * @return 创建成功的主机对象id值
     */
    private Long handleCreateHost(HostDO host, String operator) throws ServiceException {
        /*
         * 校验待创建主机对象参数信息是否合法
         */
        CheckResult checkResult = hostManageServiceExtension.checkCreateParameterHost(host);
        if(!checkResult.getCheckResult()) {//主机对象信息不合法
            throw new ServiceException(
                    String.format("待创建Host对象参数检查非法，检查结果为：%s", checkResult.toString()),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        if(null != getHostByHostName(host.getHostName())) {
            throw new ServiceException(
                    String.format("待创建主机对应主机名={%s}在系统中已存在", host.getHostName()),
                    ErrorCodeEnum.HOST_NAME_DUPLICATE.getCode()
            );
        }
        if (!host.isContainer()) {//主机
            host.setParentHostName(StringUtils.EMPTY);
            /*
             * 主机ip不可与其他主机ip重复
             * TODO：暂去 ip 重复校验
             */
//            if(CollectionUtils.isNotEmpty(getHostByIp(host.getIp()))) {
//                throw new ServiceException(
//                        String.format("待创建主机对应 ip={%s} 在系统中已存在", host.getIp()),
//                        ErrorCodeEnum.HOST_IP_DUPLICATE.getCode()
//                );
//            }
        }

        /*
         * 持久化 host 对象
         */
        HostPO hostPO = hostManageServiceExtension.host2HostPO(host);
        hostPO.setOperator(CommonConstant.getOperator(operator));
        hostPO.setExternalId(SourceEnum.K8S.getCode());
        hostDAO.insert(hostPO);
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.AGENT,
                OperationEnum.ADD,
                hostPO.getId(),
                String.format("创建Host={%s}，创建成功的Host对象id={%d}", JSON.toJSONString(host), hostPO.getId()),
                operator
        );
        return hostPO.getId();
    }

    /**
     * 删除一个主机信息：删除主机 & 服务关联关系、主机信息
     * 注：该函数作为一个整体运行在一个事务中，不抛异常提交事务，抛异常回滚事务
     * @param hostId 待删除主机对象id值
     * @param ignoreUncompleteCollect 是否忽略待删除主机上是否存在未被采完日志或待删除主机存在未采集完日志的Agent，
     *                                如该参数设置为true，表示即使待删除主机上存在未被采完日志或待删除主机存在未采集完日志的Agent，也会卸载Agent & 删除该主机对象
     *                                如该参数设置为true，表示当待删除主机上存在未被采完日志或待删除主机存在未采集完日志的Agent，将终止删除操作，返回对应错误信息 & 错误码
     * @param cascadeDeleteAgentIfExists 待删除主机对象存在关联Agent对象时，是否级联删除关联Agent对象，true：删除 false：不删除（并抛出异常）
     * @param operator 操作人
     *
     * TODO：板块 2 function
     */
    private void handleDeleteHost(Long hostId, boolean ignoreUncompleteCollect, boolean cascadeDeleteAgentIfExists, String operator) {
        /*
         * 检查入参 hostId 是否为空
         */
        if(null == hostId) {
            throw new ServiceException(
                    "删除失败：待删除主机id不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        /*
         * 检查待删除主机id对应主机对象在系统是否存在
         */
        HostDO hostDO = getHostById(hostId);
        if(null == hostDO) {
            throw new ServiceException(
                    "删除失败：待删除主机在系统中不存在",
                    ErrorCodeEnum.HOST_NOT_EXISTS.getCode()
            );
        }
        /*
         * 校验主机类型为"主机"时，主机是否关联容器对象（集），如存在关联，不允许删除
         */
        if(hostDO.getContainer().equals(HostTypeEnum.HOST.getCode())) {//待删除主机为主机类型
            List<HostDO> containerList = getContainerListByParentHostName(hostDO.getHostName());
            if(CollectionUtils.isNotEmpty(containerList)) {
                throw new ServiceException(
                        String.format("删除失败：待删除主机上挂载有%d个容器，请先删除这些容器", containerList.size()),
                        ErrorCodeEnum.RELATION_CONTAINER_EXISTS_WHEN_DELETE_HOST.getCode()
                );
            }
        }
        /*
         * 校验待删除主机是否关联 service
         */
        List<ServiceDO> serviceDOList = serviceManageService.getServicesByHostId(hostDO.getId());
        if(CollectionUtils.isNotEmpty(serviceDOList)) {
            throw new ServiceException(
                    String.format(
                            "删除失败：待删除主机存在%d个关联的应用",
                            serviceDOList.size()
                    ),
                    ErrorCodeEnum.RELATION_SERVICES_EXISTS_WHEN_DELETE_HOST.getCode()
            );
        }
        /*
         * 校验待删除主机上日志是否已采集完
         */
        if(!ignoreUncompleteCollect) {
            /*
             * 检查待删除 host 上待采集日志信息是否都已被采集完？如已采集完，可删 host，如未采集完，不可删 host
             */
            boolean completeCollect = completeCollect(hostDO);
            if(!completeCollect) {//未完成采集
                throw new ServiceException(
                        "删除失败：待删除主机存在未被采集完的日志",
                        ErrorCodeEnum.AGENT_COLLECT_NOT_COMPLETE.getCode()
                );
            }
        }
        /*
         * 校验待删除 host 是否存在 agent？如存在 agent，根据入参是否级联删除关联agent对象，进行删 agent
         */
        AgentDO agentDO = agentManageService.getAgentByHostName(hostDO.getHostName());
        if(null != agentDO) {//待删除 host 存在 agentDO
            if(cascadeDeleteAgentIfExists) {
                //删除 agentDO
                if(ignoreUncompleteCollect) {//忽略未完成采集情况
                    agentManageService.deleteAgentByHostName(agentDO.getHostName(), false, true, operator);
                } else {//不忽略未完成采集情况
                    agentManageService.deleteAgentByHostName(agentDO.getHostName(), true, true, operator);
                }
            } else {
                throw new ServiceException(
                        "删除失败：待删除主机关联有 Agent，请先删除关联的 Agent",
                        ErrorCodeEnum.RELATION_AGENT_EXISTS_WHEN_DELETE_HOST.getCode()
                );
            }
        }
        /*
         * 删除 host 相关 tb_log_collect_task_health_detail 信息
         */
        logCollectTaskHealthDetailManageService.deleteByHostName(hostDO.getHostName());
        /*
         * 删除主机信息
         */
        hostDAO.deleteByPrimaryKey(hostDO.getId());
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.AGENT,
                OperationEnum.DELETE,
                hostId,
                String.format("删除Host对象={id={%d}}", hostId),
                operator
        );
    }

    private boolean completeCollect(HostDO hostDO) {
        //TODO：
        return true;
    }

    /**
     * @param serviceId 服务对象id
     * @param hostId 主机对象id
     * @return 根据给定服务对象id & 主机对象id 构建的 ServiceHostPO 对象
     */
    private ServiceHostPO buildServiceHostPO(Long serviceId, Long hostId) {
        ServiceHostPO serviceHostPO = new ServiceHostPO();
        serviceHostPO.setServiceId(serviceId);
        serviceHostPO.setHostId(hostId);
        return serviceHostPO;
    }

    /**
     * 更新一个主机信息，采用 removeLogCollectorTaskProcess -> createLogCollectorTaskProcess
     * 注：该函数作为一个整体运行在一个事务中，不抛异常提交事务，抛异常回滚事务
     * @param targetHost 待更新的主机对象 注：该主机对象需要具备更新后所有需要的属性值，而非仅具备需要更新的属性值
     * @param operator
     */
    private void handleUpdateHost(HostDO targetHost, String operator) throws ServiceException {
        /*
         * 校验主机对象参数信息是否合法
         */
        CheckResult checkResult = hostManageServiceExtension.checkModifyParameterHost(targetHost);
        if(!checkResult.getCheckResult()) {//待更新主机对象信息不合法
            throw new ServiceException(
                    String.format("待更新Host对象参数检查非法，检查结果为：%s", checkResult.toString()),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        /*
         * 校验待更新主机对象在系统中是否存在
         */
        HostDO sourceHost = getHostById(targetHost.getId());//本地主机对象
        if(null == sourceHost) {
            throw new ServiceException(
                    String.format("待更新Host对象{id=%d}在系统中不存在", targetHost.getId()),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        /*
         * 更新主机对象变更属性值
         */
        HostDO persistHost = hostManageServiceExtension.updateHost(sourceHost, targetHost);
        HostPO hostPO = hostManageServiceExtension.host2HostPO(persistHost);
        hostPO.setOperator(CommonConstant.getOperator(operator));
        hostDAO.updateByPrimaryKey(hostPO);
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.AGENT,
                OperationEnum.EDIT,
                targetHost.getId(),
                String.format("修改Host={%s}，修改成功的Host对象id={%d}", JSON.toJSONString(targetHost), targetHost.getId()),
                operator
        );
    }

    /****************************************** 外部接口方法 ******************************************/

    @Override
    public List<HostDO> getHostsByServiceId(Long serviceId) {
        List<HostPO> hostPOList = hostDAO.selectByServiceId(serviceId);
        if(CollectionUtils.isEmpty(hostPOList)) {
            return new ArrayList<>();
        } else {
            return hostManageServiceExtension.hostPOList2HostDOList(hostPOList);
        }
    }

    @Override
    public List<HostDO> getHostsByPodId(Long podId) {
        List<HostPO> hosts = hostDAO.selectByPodId(podId);
        if (hosts == null) {
            return Collections.emptyList();
        }
        return hostManageServiceExtension.hostPOList2HostDOList(hosts);
    }

    @Override
    public HostDO getById(Long id) {
        HostPO hostPO = hostDAO.selectByPrimaryKey(id);
        if(null == hostPO) {
            return null;
        } else {
            return hostManageServiceExtension.hostPO2HostDO(hostPO);
        }
    }

    @Override
    public List<HostAgentDO> paginationQueryByConditon(HostPaginationQueryConditionDO hostPaginationQueryConditionDO) {
        String column = hostPaginationQueryConditionDO.getSortColumn();
        if (column != null) {
            for (char c : column.toCharArray()) {
                if (!Character.isLetter(c) && c != '_') {
                    return Collections.emptyList();
                }
            }
        }
        List<HostAgentPO> hostAgentPOList = hostDAO.paginationQueryByConditon(hostPaginationQueryConditionDO);
        if(CollectionUtils.isEmpty(hostAgentPOList)) {
            return Collections.emptyList();
        }
        return hostManageServiceExtension.hostAgentPOList2HostAgentDOList(hostAgentPOList);
    }

    @Override
    public Integer queryCountByCondition(HostPaginationQueryConditionDO hostPaginationQueryConditionDO) {
        return hostDAO.queryCountByCondition(hostPaginationQueryConditionDO);
    }

    @Override
    public List<String> getAllMachineZones() {
        return hostDAO.selectAllMachineZones();
    }

    @Override
    public List<HostDO> getHostListByLogCollectTaskId(Long logCollectTaskId) {
        List<HostDO> relationHostDOList = new ArrayList<>();//日志采集任务关联的所有主机集
        LogCollectTaskDO logCollectTaskDO = logCollectTaskManageService.getById(logCollectTaskId);
        if(null == logCollectTaskDO) {
            return new ArrayList<>();
        }
        /*
         * 根据日志采集任务 id 获取该日志采集任务
         * TODO：如后续日志采集任务关联服务数较多，将此改为数据库直接查询对应结果
         */
        List<ServiceDO> serviceDOList = serviceManageService.getServicesByLogCollectTaskId(logCollectTaskId);
        for (ServiceDO serviceDO : serviceDOList) {
            List<HostDO> hostDOList = getHostsByServiceId(serviceDO.getId());
            for (HostDO hostDO : hostDOList) {
                //根据日志采集任务设置的主机过滤规则进行对应主机过滤
                if(agentCollectConfigManageService.need2Deploy(logCollectTaskDO, hostDO)) {
                    relationHostDOList.add(hostDO);
                }
            }
        }
        return relationHostDOList;
    }

    @Override
    public List<HostDO> getHostListContainsAgentByLogCollectTaskId(Long logCollectTaskId) {
        List<HostDO> hosts = getHostListByLogCollectTaskId(logCollectTaskId);
        return hosts.stream().filter(e -> agentManageService.getAgentByHostName(e.getHostName()) != null).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long createHost(HostDO host, String operator) {
        return this.handleCreateHost(host, operator);
    }

    @Override
    @Transactional
    public void deleteHost(Long hostId, boolean ignoreUncompleteCollect, boolean cascadeDeleteAgentIfExists, String operator) {
        this.handleDeleteHost(hostId, ignoreUncompleteCollect, cascadeDeleteAgentIfExists, operator);
    }

    @Override
    @Transactional
    public void updateHost(HostDO host, String operator) {
        this.handleUpdateHost(host, operator);
    }

    @Override
    public HostDO getHostByHostName(String hostName) {
        HostPO hostPO = hostDAO.selectByHostName(hostName);
        if(null == hostPO) {
            return null;
        }
        HostDO host = hostManageServiceExtension.hostPO2HostDO(hostPO);
        return host;
    }

    @Override
    public List<HostDO> getHostByIp(String ip) {
        List<HostPO> hostPOList = hostDAO.selectByIp(ip);
        if(CollectionUtils.isEmpty(hostPOList)) {
            return new ArrayList<>();
        }
        List<HostDO> hostDOList = hostManageServiceExtension.hostPOList2HostDOList(hostPOList);
        return hostDOList;
    }

    @Override
    public List<HostDO> getContainerListByParentHostName(String hostName) {
        List<HostPO> containerPOList = hostDAO.selectContainerListByParentHostName(hostName);
        if(CollectionUtils.isNotEmpty(containerPOList)) {
            return hostManageServiceExtension.hostPOList2HostDOList(containerPOList);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<HostDO> list() {
        List<HostPO> hostPOList = hostDAO.list();
        if (CollectionUtils.isNotEmpty(hostPOList)) {
            return hostManageServiceExtension.hostPOList2HostDOList(hostPOList);
        }
        return new ArrayList<>();
    }

    /**
     * 获取给定agent关联的主机列表
     * @param agentId agent 对象 id
     * @return 返回给定agent关联的主机列表
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    public List<HostDO> getRelationHostListByAgentId(Long agentId) throws ServiceException {
        AgentDO agentDO = agentManageService.getById(agentId);
        if(null == agentDO) {
            throw new ServiceException(
                    String.format("Agent={id=%d}在系统中不存在", agentId),
                    ErrorCodeEnum.AGENT_NOT_EXISTS.getCode()
            );
        }
        return getRelationHostListByAgent(agentDO);
    }

    public List<HostDO> getRelationHostListByAgent(AgentDO agentDO) {
        /*
         * 根据agent采集类型获取其关联的主机（集）
         */
        List<HostDO> relationHostList = new ArrayList<>();
        HostDO hostDO = getHostByHostName(agentDO.getHostName());
        if(null == hostDO) {
            throw new ServiceException(
                    String.format("Agent={id=%d}对应宿主机Host={hostName=%s}在系统在不存在", agentDO.getId(), agentDO.getHostName()),
                    ErrorCodeEnum.HOST_NOT_EXISTS.getCode()
            );
        }
        if(AgentCollectTypeEnum.COLLECT_HOST_ONLY.getCode().equals(agentDO.getCollectType())) {
            relationHostList.add(hostDO);
        } else if(AgentCollectTypeEnum.COLLECT_CONTAINERS_ONLY.getCode().equals(agentDO.getCollectType())) {
            List<HostDO> relationContainerList = getContainerListByParentHostName(hostDO.getHostName());
            if(CollectionUtils.isNotEmpty(relationContainerList)) {
                relationHostList.addAll(relationContainerList);
            }
        } else if(AgentCollectTypeEnum.COLLECT_HOST_AND_CONTAINERS.getCode().equals(agentDO.getCollectType())) {
            relationHostList.add(hostDO);
            List<HostDO> relationContainerList = getContainerListByParentHostName(hostDO.getHostName());
            if(CollectionUtils.isNotEmpty(relationContainerList)) {
                relationHostList.addAll(relationContainerList);
            }
        } else {
            throw new ServiceException(
                    String.format("Agent={id=%d}对应collectType值={%d}为系统未知采集类型", agentDO.getId(), agentDO.getCollectType()),
                    ErrorCodeEnum.UNKNOWN_COLLECT_TYPE.getCode()
            );
        }
        return relationHostList;
    }

    @Override
    public Long countAllHost() {
        return hostDAO.countByHostType(HostTypeEnum.HOST.getCode());
    }

    @Override
    public Long countAllContainer() {
        return hostDAO.countByHostType(HostTypeEnum.CONTAINER.getCode());
    }

    @Override
    public Long countAllFaultyHost() {
        Long faultyCount = 0l;
        List<HostDO> hostDOList = list();
        for(HostDO hostDO : hostDOList) {
            if(!NetworkUtil.ping(hostDO.getHostName())) {
                faultyCount++;
            }
        }
        return faultyCount;
    }

}
