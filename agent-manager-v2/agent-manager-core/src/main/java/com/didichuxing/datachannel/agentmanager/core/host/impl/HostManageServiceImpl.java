package com.didichuxing.datachannel.agentmanager.core.host.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.common.ListCompareResult;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostAgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.host.HostAgentPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.host.HostPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServiceHostPO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentCollectTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.host.HostTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.ModuleEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.OperationEnum;
import com.didichuxing.datachannel.agentmanager.common.util.*;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.Comparator;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.common.OperateRecordService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceHostManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.HostMapper;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.collect.configuration.extension.AgentCollectConfigurationManageServiceExtension;
import com.didichuxing.datachannel.agentmanager.thirdpart.host.extension.HostManageServiceExtension;
import com.didichuxing.datachannel.agentmanager.remote.host.RemoteHostManageService;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author huqidong
 * @date 2020-09-21
 * 主机处理流程模板类
 */
@org.springframework.stereotype.Service
public class HostManageServiceImpl implements HostManageService {

    private static final ILog LOGGER = LogFactory.getLog(HostManageServiceImpl.class);

    @Autowired
    private HostMapper hostDAO;

    @Autowired
    private ServiceHostManageService serviceHostManageService;

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private RemoteHostManageService remoteHostService;

    @Autowired
    private AgentMetricsManageService agentMetricsManageService;

    @Autowired
    private ServiceManageService serviceManageService;

    @Autowired
    private HostManageServiceExtension hostManageServiceExtension;

    @Autowired
    private AgentCollectConfigurationManageServiceExtension agentCollectConfigurationManageServiceExtension;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private OperateRecordService operateRecordService;

    private HostDOComparator comparator = new HostDOComparator();

    private HostContainerComparator hostContainerComparator = new HostContainerComparator();

    private ServiceHostPOComparator serviceHostPOComparator = new ServiceHostPOComparator();

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
        if (host.getContainer() == 0 && host.getParentHostName() == null) {
            host.setParentHostName("");
        }
//        if(null != getHostByIp(host.getIp())) {
//            throw new ServiceException(
//                    String.format("待创建主机对应 ip={%s} 在系统中已存在", host.getIp()),
//                    ErrorCodeEnum.HOST_IP_DUPLICATE.getCode()
//            );
//        }
        /*
         * 持久化 host 对象
         */
        HostPO hostPO = hostManageServiceExtension.host2HostPO(host);
        hostPO.setOperator(CommonConstant.getOperator(operator));
        hostDAO.insert(hostPO);
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.HOST,
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
                    "入参hostId不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        /*
         * 检查待删除主机id对应主机对象在系统是否存在
         */
        HostDO hostDO = getHostById(hostId);
        if(null == hostDO) {
            throw new ServiceException(
                    String.format("删除Host对象{id=%d}失败，原因为：系统中不存在id为{%d}的主机对象", hostId, hostId),
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
                        String.format("待删除主机={id={%d}}上挂载{%d}个容器，请先删除这些容器", hostId, containerList.size()),
                        ErrorCodeEnum.RELATION_CONTAINER_EXISTS_WHEN_DELETE_HOST.getCode()
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
                        String.format("待删除主机={id=%d}上关联有Agent，请先删除关联的Agent={id=%d}", hostId, agentDO.getId()),
                        ErrorCodeEnum.RELATION_AGENT_EXISTS_WHEN_DELETE_HOST.getCode()
                );
            }
        }
        /*
         * 校验待删除主机上日志是否已采集完
         */
        if(!ignoreUncompleteCollect) {
            /*
             * 检查待删除 host 上待采集日志信息是否都已被采集完？如已采集完，可删 host，如未采集完，不可删 host
             */
            boolean completeCollect = agentMetricsManageService.completeCollect(hostDO);
            if(!completeCollect) {//未完成采集
                throw new ServiceException(
                        String.format("删除Host对象{id=%d}失败，原因为：该主机仍存在未被采集端采集完的日志", hostId),
                        ErrorCodeEnum.AGENT_COLLECT_NOT_COMPLETE.getCode()
                );
            }
        }
        /*
         * 删除主机 & 服务关联关系
         */
        serviceHostManageService.deleteByHostId(hostDO.getId());
        /*
         * 删除主机信息
         */
        hostDAO.deleteByPrimaryKey(hostDO.getId());
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.HOST,
                OperationEnum.DELETE,
                hostId,
                String.format("删除Host对象={id={%d}}", hostId),
                operator
        );
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
                ModuleEnum.HOST,
                OperationEnum.EDIT,
                targetHost.getId(),
                String.format("修改Host={%s}，修改成功的Host对象id={%d}", JSON.toJSONString(targetHost), targetHost.getId()),
                operator
        );
    }

    /****************************************** 外部接口方法 ******************************************/

    @Override
    public void pullHostListFromRemoteAndMergeInLocal() {
        long startTime = System.currentTimeMillis();//use to lo
        /*
         * 获取本地主机对象集
         */
        long getLocalListStartTime = System.currentTimeMillis();
        List<HostDO> localList = list();
        long getLocalListTime = System.currentTimeMillis() - getLocalListStartTime;//获取本地主机信息集耗时
        /*
         * 获取远程主机节点对象集
         */
        List<ServiceDO> serviceDOList = serviceManageService.list();
        List<HostDO> remoteList = new ArrayList<>();
        Map<String, HostDO> hostName2HostDOMap = new HashMap<>();//用于对同步主机集进行去重，原因为：存在一个主机挂在多个节点可能性
        List<Pair<Long, String>> serviceId2HostNamePairList = new ArrayList<>();//用于记录远程同步 serciceId & hostName 集
        for (ServiceDO serviceDO : serviceDOList) {
            if(null != serviceDO.getExtenalServiceId() && serviceDO.getExtenalServiceId() > 0) {
                /*
                 * 根据各servcieDO同步其关联的主机信息
                 */
                List<HostDO> hostDOList = remoteHostService.getHostsByServiceIdFromRemote(serviceDO.getExtenalServiceId());
                for (HostDO hostDO : hostDOList) {
                    String hostName = hostDO.getHostName();
                    HostDO host = hostName2HostDOMap.get(hostName);
                    if(null == host) {
                        remoteList.add(hostDO);
                        hostName2HostDOMap.put(hostName, hostDO);
                    } else {
                        if(
                                !host.getContainer().equals(hostDO.getContainer()) ||
                                        !host.getIp().equals(hostDO.getIp()) ||
                                        !host.getParentHostName().equals(hostDO.getParentHostName())
                        ) {
                            LOGGER.error(String.format("class=HostManageServiceImpl||method=pullHostListFromRemoteAndMergeInLocal||errMsg={%s}",
                                    String.format("远程同步的主机列表中存在两个相同hostName的主机对象，the 1st = {%s}, the 2nd = {%s}", JSON.toJSONString(host), JSON.toJSONString(hostDO))
                            ));
                        }
                    }
                    serviceId2HostNamePairList.add(new Pair<>(serviceDO.getId(), hostDO.getHostName()));
                }
            }
        }
        long getRemoteListTime = System.currentTimeMillis() - startTime;//获取远程主机信息集耗时
        /*
         * 与本地服务节点集进行对比，得到待新增、待删除、待更新服务节点列表
         */
        long compareStartTime = System.currentTimeMillis();
        ListCompareResult<HostDO> listCompareResult = ListCompareUtil.compare(localList, remoteList, comparator);//对比主机
        long compareTime = System.currentTimeMillis() - compareStartTime;
        /*
         * 针对上一步得到的待新增、待删除、待更新服务节点列表，进行新增、删除、更新操作
         */
        long persistStartTime = System.currentTimeMillis();
        int createSuccessCount = 0, removeScucessCount = 0, modifiedSuccessCount = 0;//创建成功数、删除成功数、更新成功数
        //处理待创建对象集
        List<HostDO> createList = listCompareResult.getCreateList();
        for (HostDO hostDO : createList) {
            //创建主机对象
            createHost(hostDO, null);
            createSuccessCount++;
        }
        //处理待修改对象集
        List<HostDO> modifyList = listCompareResult.getModifyList();
        for (HostDO hostDO : modifyList) {
            //更新主机信息
            updateHost(hostDO, null);
            modifiedSuccessCount++;
        }
        //处理待删除对象集
        List<HostDO> removeList = listCompareResult.getRemoveList();
        //由于待删除主机集可能存在主机 & 容器，须先删除容器再删除主机
        removeList.sort(hostContainerComparator);
        for (HostDO hostDO : removeList) {
            try {
                //删除主机对象
                deleteHost(hostDO.getId(), false, true, null);
                removeScucessCount++;
            } catch (ServiceException ex) {
                //此时，须判断是否因存在未被采集日志信息导致 host 对象删除失败，如是，则 warn log，否则，error log
                if (ErrorCodeEnum.AGENT_COLLECT_NOT_COMPLETE.getCode().equals(ex.getServiceExceptionCode())) {
                    LOGGER.warn(
                            String.format("class=HostManageServiceImpl||method=pullHostListFromRemoteAndMergeInLocal||warnMsg={%s}",
                                    String.format("由于存在未被采集完的日志信息，导致删除host={%s}对象失败，原因为：%s", JSON.toJSONString(hostDO), ex.getMessage()))
                    );
                } else {
                    LOGGER.error(
                            String.format("class=HostManageServiceImpl||method=pullHostListFromRemoteAndMergeInLocal||errMsg={%s}",
                                    String.format("删除Host={%s}对象失败，原因为：%s", JSON.toJSONString(hostDO), ex.getMessage()))
                    );
                }
            }
        }
        long persistTime = System.currentTimeMillis() - persistStartTime;

        long rebuildServcieHostRelationStartTime = System.currentTimeMillis();
        List<HostDO> localHostDOList = list();
        Map<String, Long> hostName2HostIdLocalMap = new HashMap<>();
        List<ServiceHostPO> serviceHostPOListRemote = new ArrayList<>();
        for (HostDO hostDO : localHostDOList) {
            hostName2HostIdLocalMap.put(hostDO.getHostName(), hostDO.getId());
        }
        for (Pair<Long, String> serviceId2HostNamePair : serviceId2HostNamePairList) {
            String hostName = serviceId2HostNamePair.getValue();
            Long hostId = hostName2HostIdLocalMap.get(hostName);
            Long serviceId = serviceId2HostNamePair.getKey();
            if(null == hostId) {
                LOGGER.error(
                        String.format("class=HostManageServiceImpl||method=pullHostListFromRemoteAndMergeInLocal||errMsg={%s}",
                                String.format("构建远程服务 & 主机关联关系失败，主机 hostName={%s} 在远程同步的主机集中不存在", hostName)
                        )
                );
            } else {
                serviceHostPOListRemote.add(new ServiceHostPO(serviceId, hostId));
            }
        }
        List<ServiceHostPO> serviceHostPOListLocal = serviceHostManageService.list();
        int serviceHostRelationCreateSuccessCount = 0, serviceHostRelationRemoveSuccessCount = 0;//服务~主机关联关系创建成功数、删除成功数
        ListCompareResult<ServiceHostPO> serviceHostPOListCompareResult = ListCompareUtil.compare(serviceHostPOListLocal, serviceHostPOListRemote, serviceHostPOComparator);//对比服务 & 主机关联关系
        //处理待创建对象集
        List<ServiceHostPO> createServiceHostPOList = serviceHostPOListCompareResult.getCreateList();
        serviceHostManageService.createServiceHostList(createServiceHostPOList);
        serviceHostRelationCreateSuccessCount = createServiceHostPOList.size();
        //处理待删除对象集
        List<ServiceHostPO> removeServiceHostPOList = serviceHostPOListCompareResult.getRemoveList();
        for (ServiceHostPO serviceHostPO : removeServiceHostPOList) {
            serviceHostManageService.deleteById(serviceHostPO.getId());
            serviceHostRelationRemoveSuccessCount++;
        }
        long rebuildServcieHostRelationTime = System.currentTimeMillis() - rebuildServcieHostRelationStartTime;

        /*
         * 记录日志
         */
        String logInfo = String.format(
                "class=HostManageServiceImpl||method=pullHostListFromRemoteAndMergeInLocal||remoteListSize={%d}||localListSize={%d}||" +
                        "total-cost-time={%d}||getRemoteList-cost-time={%d}||getLocalList-cost-time={%d}||compareRemoteListAndLocalList-cost-time={%d}||persistList-cost-time={%d}||rebuildServcieHostRelationTime-cost-time={%d}||" +
                        "计划扩容数={%d}||扩容成功数={%d}||扩容失败数={%d}||计划缩容数={%d}||缩容成功数={%d}||缩容失败数={%d}||计划更新数={%d}||更新成功数={%d}||更新失败数={%d}||新增服务-主机关联关系数={%d}||删除服务-主机关联关系数={%d}",
                remoteList.size(),
                localList.size(),
                System.currentTimeMillis() - startTime,
                getRemoteListTime,
                getLocalListTime,
                compareTime,
                persistTime,
                rebuildServcieHostRelationTime,
                listCompareResult.getCreateList().size(),
                createSuccessCount,
                (listCompareResult.getCreateList().size() - createSuccessCount),
                listCompareResult.getRemoveList().size(),
                removeScucessCount,
                (listCompareResult.getRemoveList().size() - removeScucessCount),
                listCompareResult.getModifyList().size(),
                modifiedSuccessCount,
                (listCompareResult.getModifyList().size() - modifiedSuccessCount),
                serviceHostRelationCreateSuccessCount,
                serviceHostRelationRemoveSuccessCount
        );
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(logInfo);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                    String.format(
                            "remoteList={%s}||localHostList={%s}",
                            JSON.toJSONString(remoteList),
                            JSON.toJSONString(localList)
                    )
            );
        }
    }

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
                if(agentCollectConfigurationManageServiceExtension.need2Deploy(logCollectTaskDO, hostDO)) {//根据日志采集任务设置的主机过滤规则进行对应主机过滤
                    relationHostDOList.add(hostDO);
                }
            }
        }
        return relationHostDOList;
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
    public HostDO getHostByIp(String ip) {
        HostPO hostPO = hostDAO.selectByIp(ip);
        if(null == hostPO) {
            return null;
        }
        HostDO host = hostManageServiceExtension.hostPO2HostDO(hostPO);
        return host;
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

    /**
     * HostDO 对象比较器类
     */
    class HostDOComparator implements Comparator<HostDO, String> {
        @Override
        public String getKey(HostDO host) {
            return host.getHostName();
        }
        @Override
        public boolean compare(HostDO t1, HostDO t2) {
            return t1.getContainer().equals(t2.getContainer()) &&
                    t1.getIp().equals(t2.getIp()) &&
                    t1.getParentHostName().equals(t2.getParentHostName());
        }
        @Override
        public HostDO getModified(HostDO source, HostDO target) {
            source.setContainer(target.getContainer());
            source.setParentHostName(target.getParentHostName());
            source.setIp(target.getIp());
            return source;
        }
    }

    class HostContainerComparator implements java.util.Comparator<HostDO> {
        @Override
        public int compare(HostDO o1, HostDO o2) {
            return o2.getContainer() - o1.getContainer();
        }
    }

    /**
     * ServiceHostPO 对象比较器类
     */
    class ServiceHostPOComparator implements Comparator<ServiceHostPO, String> {
        @Override
        public String getKey(ServiceHostPO serviceHostPO) {
            return serviceHostPO.getServiceId()+"_"+serviceHostPO.getHostId();
        }
        @Override
        public boolean compare(ServiceHostPO t1, ServiceHostPO t2) {
            return t1.getServiceId().equals(t2.getServiceId()) &&
                    t1.getHostId().equals(t2.getHostId());
        }
        @Override
        public ServiceHostPO getModified(ServiceHostPO source, ServiceHostPO target) {
            return source;
        }
    }

}
