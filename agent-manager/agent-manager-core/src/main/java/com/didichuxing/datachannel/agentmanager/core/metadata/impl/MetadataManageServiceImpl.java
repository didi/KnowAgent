package com.didichuxing.datachannel.agentmanager.core.metadata.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.k8s.K8sPodDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.HostInfo;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetadataSyncResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetadataSyncResultPerService;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.k8s.K8sPodHostPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServiceHostPO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.SourceEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.k8s.K8sPodContainerManageService;
import com.didichuxing.datachannel.agentmanager.core.k8s.K8sPodManageService;
import com.didichuxing.datachannel.agentmanager.core.metadata.MetadataManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceHostManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import com.didichuxing.datachannel.agentmanager.thirdpart.metadata.MetadataManageServiceExtension;
import com.didichuxing.datachannel.agentmanager.thirdpart.metadata.k8s.domain.PodConfig;
import com.didichuxing.datachannel.agentmanager.thirdpart.metadata.k8s.domain.PodReference;
import com.didichuxing.datachannel.agentmanager.thirdpart.metadata.k8s.util.K8sUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@org.springframework.stereotype.Service
public class MetadataManageServiceImpl implements MetadataManageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataManageServiceImpl.class);

    @Value("${metadata.sync.request.k8s.service-key}")
    private String serviceKey;

    @Value("${metadata.sync.request.k8s.path-name}")
    private String logPath;

    @Autowired
    private MetadataManageServiceExtension metadataManageServiceExtension;

    @Autowired
    private ServiceManageService serviceManageService;

    @Autowired
    private K8sPodManageService k8sPodManageService;//TODO：

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private ServiceHostManageService serviceHostManageService;

    @Autowired
    private K8sPodContainerManageService k8sPodContainerManageService;//TODO：

/*
    private HostDOComparator hostDOComparator = new HostDOComparator();

    private ServiceDOComparator serviceDOComparator = new ServiceDOComparator();

    private K8sPodDOComparator k8sPodDOComparator = new K8sPodDOComparator();

    private ServiceHostPOComparator serviceHostPOComparator = new ServiceHostPOComparator();

    private K8sPodHostPOComparator k8sPodContainerPOComparator = new K8sPodHostPOComparator();

    private HostContainerComparator hostContainerComparator = new HostContainerComparator();
*/

    @Override
    @Transactional
    public MetadataSyncResult sync() {
        List<PodConfig> podConfigs = K8sUtil.filterWithServices(K8sUtil.getAllPodConfig());
        List<HostDO> localHosts = hostManageService.list();
        MetadataSyncResult metadataSyncResult = new MetadataSyncResult();
        List<MetadataSyncResultPerService> resultList = new ArrayList<>();
        Map<String, List<PodConfig>> configServiceMap = new HashMap<>();

        for (PodConfig podConfig : podConfigs) {
            String serviceName = podConfig.getAnnotations().get(serviceKey);
            if (StringUtils.isBlank(serviceName)) {
                LOGGER.warn("pod has no service, uid: {}", podConfig.getUuid());
                continue;
            }
            configServiceMap.putIfAbsent(serviceName, new ArrayList<>());
            configServiceMap.get(serviceName).add(podConfig);
        }
        int relatedHosts = 0;
        for (Map.Entry<String, List<PodConfig>> entry : configServiceMap.entrySet()) {
            MetadataSyncResultPerService metadataSyncResultPerService = new MetadataSyncResultPerService();
            metadataSyncResultPerService.setSyncSuccess(1);
            metadataSyncResultPerService.setNameDuplicate(0);
            String serviceName = entry.getKey();
            List<PodConfig> configList = entry.getValue();
            metadataSyncResultPerService.setServiceName(serviceName);
            ServiceDO oldService = serviceManageService.getServiceByServiceName(serviceName);
            Long serviceId;
            if (oldService != null && oldService.getExtenalServiceId() == SourceEnum.MANUAL.getCode()) {
                metadataSyncResultPerService.setNameDuplicate(1);
                metadataSyncResultPerService.setSyncSuccess(0);
                resultList.add(metadataSyncResultPerService);
                continue;
            } else if (oldService == null) {
                ServiceDO serviceDO = new ServiceDO();
                serviceDO.setServicename(serviceName);
                serviceDO.setExtenalServiceId(SourceEnum.K8S.getCode());
                serviceId = serviceManageService.createService(serviceDO, null);
            } else {
                serviceId = oldService.getId();
            }
            List<HostInfo> duplicateHostnames = new ArrayList<>();
            List<HostInfo> duplicateIps = new ArrayList<>();
            for (PodConfig podConfig : configList) {
                String hostName = podConfig.getNodeName();
                String hostIp = podConfig.getNodeIp();
                List<String> containerNames = podConfig.getContainerNames();
                for (HostDO localHost : localHosts) {
                    if (localHost.getExternalId() != SourceEnum.MANUAL.getCode()) {
                        continue;
                    }
                    if (localHost.getContainer() == 0) {
                        if (localHost.getHostName().equals(hostName)) {
                            HostInfo hostInfo = new HostInfo();
                            hostInfo.setHostName(hostName);
                            hostInfo.setIp(hostIp);
                            hostInfo.setHostType(0);
                            duplicateHostnames.add(hostInfo);
                        }
                        if (localHost.getIp().equals(hostIp)) {
                            HostInfo hostInfo = new HostInfo();
                            hostInfo.setHostName(hostName);
                            hostInfo.setIp(hostIp);
                            hostInfo.setHostType(0);
                            duplicateIps.add(hostInfo);
                        }
                    } else if (localHost.getContainer() == 1) {
                        for (String containerName : containerNames) {
                            if (localHost.getHostName().equals(containerName) && localHost.getExternalId() == SourceEnum.MANUAL.getCode()) {
                                HostInfo hostInfo = new HostInfo();
                                hostInfo.setHostName(containerName);
                                hostInfo.setIp(podConfig.getPodIp());
                                hostInfo.setHostType(1);
                                duplicateHostnames.add(hostInfo);
                            }
                        }
                    } else {
                        // 通常不可能出现
                        throw new ServiceException(String.format("本地主机类型异常，主机id: %d", localHost.getId()), ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode());
                    }
                }
                relatedHosts += podConfig.getContainerNames().size();
            }
            metadataSyncResultPerService.setRelateHostNum(relatedHosts);
            if (CollectionUtils.isEmpty(duplicateIps) && CollectionUtils.isEmpty(duplicateHostnames)) {
                // serviceHostManageService.deleteServiceHostByServiceId(serviceId);
                // 清除所有旧数据和关联关系
                for (PodConfig podConfig : configList) {
                    K8sPodDO k8sPodDO = k8sPodManageService.getByNameAndSpace(podConfig.getNamespace(), podConfig.getPodName());
                    if (k8sPodDO != null) {
                        Long podId = k8sPodDO.getId();
                        k8sPodManageService.deleteK8sPod(podId, null);
                        k8sPodContainerManageService.deleteByPodId(podId);
                    }
                }
                for (HostDO hostDO : localHosts) {
                    if (hostDO.getExternalId() == SourceEnum.K8S.getCode()) {
                        try {
                            hostManageService.deleteHost(hostDO.getId(), false, true, null);
                        } catch (ServiceException e) {
                        }
                    }
                }
                for (PodConfig podConfig : configList) {
                    K8sPodDO k8sPodDO = buildK8sPod(podConfig, serviceName);
                    Long podId = k8sPodManageService.createK8sPod(k8sPodDO, null);
                    handleCreateK8sMeta(podConfig, serviceId, podId);
                }
                metadataSyncResultPerService.setDuplicateHostNameHostList(Collections.emptyList());
                metadataSyncResultPerService.setDuplicateIpHostList(Collections.emptyList());
            } else {
                metadataSyncResultPerService.setSyncSuccess(0);
                metadataSyncResultPerService.setDuplicateHostNameHostList(duplicateHostnames);
                metadataSyncResultPerService.setDuplicateIpHostList(duplicateIps);
            }
            resultList.add(metadataSyncResultPerService);
        }
        metadataSyncResult.setMetadataSyncResultPerServiceList(resultList);
        return metadataSyncResult;
    }

    /**
     * 当前的逻辑为全量删除然后新增，可以优化同步逻辑
     *
     * @param podConfig
     * @param serviceId
     * @param podId
     */
    private void handleCreateK8sMeta(PodConfig podConfig, Long serviceId, Long podId) {
        List<ServiceHostPO> serviceHostList = new ArrayList<>();
        String hostname = podConfig.getNodeName();
        HostDO hostDO = new HostDO();
        hostDO.setHostName(hostname);
        hostDO.setIp(podConfig.getNodeIp());
        hostDO.setParentHostName(hostname);
        hostDO.setContainer(0);
        hostDO.setExternalId(SourceEnum.K8S.getCode());
        try {
            hostManageService.createHost(hostDO, null);
        } catch (ServiceException e) {
        }
        List<String> containerNames = podConfig.getContainerNames();
        List<K8sPodHostPO> k8sPodHostList = new ArrayList<>();
        for (String containerName : containerNames) {
            HostDO container = new HostDO();
            container.setHostName(containerName);
            container.setIp(podConfig.getPodIp());
            container.setParentHostName(hostname);
            container.setContainer(1);
            container.setExternalId(SourceEnum.K8S.getCode());
            Long id1;
            try {
                id1 = hostManageService.createHost(container, null);
            } catch (ServiceException e) {
                continue;
            }
            ServiceHostPO serviceHostPO1 = new ServiceHostPO();
            serviceHostPO1.setServiceId(serviceId);
            serviceHostPO1.setHostId(id1);
            serviceHostList.add(serviceHostPO1);
            K8sPodHostPO k8sPodHostPO = new K8sPodHostPO();
            k8sPodHostPO.setHostId(id1);
            k8sPodHostPO.setK8sPodId(podId);
            k8sPodHostList.add(k8sPodHostPO);
        }
        serviceHostManageService.createServiceHostList(serviceHostList);
        k8sPodContainerManageService.createK8sPodContainerList(k8sPodHostList);
    }

    private K8sPodDO buildK8sPod(PodConfig podConfig, String serviceName) {
        K8sPodDO k8sPodDO = new K8sPodDO();
        k8sPodDO.setUuid(podConfig.getUuid());
        k8sPodDO.setName(podConfig.getPodName());
        k8sPodDO.setNamespace(podConfig.getNamespace());
        k8sPodDO.setPodIp(podConfig.getPodIp());
        k8sPodDO.setServiceName(serviceName);
        Map<String, PodReference> reference = podConfig.getReferenceMap();
        k8sPodDO.setLogMountPath(reference.get(logPath).getMountPath());
        k8sPodDO.setLogHostPath(reference.get(logPath).getHostPath());
        k8sPodDO.setNodeName(podConfig.getNodeName());
        k8sPodDO.setNodeIp(podConfig.getNodeIp());
        k8sPodDO.setContainerNames(JSON.toJSONString(podConfig.getContainerNames()));
        return k8sPodDO;
    }

    /**
     * 处理 listCompareResult 结果集，对 K8sPodHostPO 对象进行对应增、删、修改
     *
     * @param listCompareResult 远程 & 本地 K8sPodHostPO 对象对比结果集
     */
/*
    private void handleK8sPodHostPOListCompareResult(ListCompareResult<K8sPodHostPO> listCompareResult) {
        //处理待删除对象集
        List<K8sPodHostPO> removeList = listCompareResult.getRemoveList();
        for (K8sPodHostPO k8sPodContainerPO : removeList) {
            k8sPodContainerManageService.deleteById(k8sPodContainerPO.getId());
        }
        //处理待创建对象集
        List<K8sPodHostPO> createList = listCompareResult.getCreateList();
        k8sPodContainerManageService.createK8sPodContainerList(createList);
    }
*/

    /**
     * @param listCompareResult
     */
/*    private void handleServiceHostPOListCompareResult(ListCompareResult<ServiceHostPO> listCompareResult) {
        //处理待删除对象集
        List<ServiceHostPO> removeServiceHostPOList = listCompareResult.getRemoveList();
        for (ServiceHostPO serviceHostPO : removeServiceHostPOList) {
            serviceHostManageService.deleteById(serviceHostPO.getId());
        }
        //处理待创建对象集
        List<ServiceHostPO> createServiceHostPOList = listCompareResult.getCreateList();
        serviceHostManageService.createServiceHostList(createServiceHostPOList);
    }*/

    /**
     * 处理 listCompareResult 结果集，对 HostDO 对象进行对应增、删、修改
     *
     * @param listCompareResult 远程 & 本地 ServiceDO 对象对比结果集
     */
/*    private void handleHostDOListCompareResult(ListCompareResult<HostDO> listCompareResult) {
        //处理待删除对象集
        List<HostDO> removeList = listCompareResult.getRemoveList();
        //由于待删除主机集可能存在主机 & 容器，须先删除容器再删除主机
        removeList.sort(hostContainerComparator);
        for (HostDO hostDO : removeList) {
            try {
                //删除主机对象
                hostManageService.deleteHost(hostDO.getId(), false, true, null);
            } catch (ServiceException ex) {
                //此时，须判断是否因存在未被采集日志信息导致 host 对象删除失败，如是，则 warn log，否则，error log
                if (ErrorCodeEnum.AGENT_COLLECT_NOT_COMPLETE.getCode().equals(ex.getServiceExceptionCode())) {
                    LOGGER.warn(
                            String.format("class=MetadataManageServiceImpl||method=handleHostDOListCompareResult||msg={%s}",
                                    String.format("由于存在未被采集完的日志信息，导致删除host={%s}对象失败，原因为：%s", JSON.toJSONString(hostDO), ex.getMessage()))
                    );
                } else {
                    LOGGER.error(
                            String.format("class=MetadataManageServiceImpl||method=handleHostDOListCompareResult||msg={%s}",
                                    String.format("删除Host={%s}对象失败，原因为：%s", JSON.toJSONString(hostDO), ex.getMessage()))
                    );
                }
            }
        }
        //处理待修改对象集
        List<HostDO> modifyList = listCompareResult.getModifyList();
        for (HostDO hostDO : modifyList) {
            //更新主机信息
            hostManageService.updateHost(hostDO, null);
        }
        //处理待创建对象集
        List<HostDO> createList = listCompareResult.getCreateList();
        for (HostDO hostDO : createList) {
            //创建主机对象
            hostManageService.createHost(hostDO, null);
        }
    }*/

    /**
     * 处理 listCompareResult 结果集，对 ServiceDO 对象进行对应增、删、修改
     *
     * @param listCompareResult 远程 & 本地 ServiceDO 对象对比结果集
     */
/*    private void handleServiceDOListCompareResult(ListCompareResult<ServiceDO> listCompareResult) {
        //处理待删除对象集
        List<ServiceDO> removeList = listCompareResult.getRemoveList();
        for (ServiceDO serviceDO : removeList) {
            serviceManageService.deleteService(serviceDO.getId(), true, null);
        }
        //处理待修改对象集
        List<ServiceDO> modifyList = listCompareResult.getModifyList();
        for (ServiceDO serviceDO : modifyList) {
            serviceManageService.updateService(serviceDO, null);
        }
        //处理待创建对象集
        List<ServiceDO> createList = listCompareResult.getCreateList();
        for (ServiceDO serviceDO : createList) {
            serviceManageService.createService(serviceDO, null);
        }
    }*/

    /**
     * 处理 listCompareResult 结果集，对 K8sPodDO 对象进行对应增、删、修改
     *
     * @param listCompareResult
     */
/*    private void handleK8sPodDOListCompareResult(ListCompareResult<K8sPodDO> listCompareResult) {
        //处理待删除对象集
        List<K8sPodDO> removeList = listCompareResult.getRemoveList();
        for (K8sPodDO k8sPodDO : removeList) {
            k8sPodManageService.deleteK8sPod(k8sPodDO.getId(), null);
        }
        //处理待修改对象集
        List<K8sPodDO> modifyList = listCompareResult.getModifyList();
        for (K8sPodDO k8sPodDO : modifyList) {
            k8sPodManageService.updateK8sPod(k8sPodDO, null);
        }
        //处理待创建对象集
        List<K8sPodDO> createList = listCompareResult.getCreateList();
        for (K8sPodDO k8sPodDO : createList) {
            k8sPodManageService.createK8sPod(k8sPodDO, null);
        }
    }*/

    /**
     * @param containerName2PodUuidMap 全量容器-pod关联关系 key：containerName value：pod uuid map
     * @param k8sPodUuid2K8sPodDOMap   全量pod uuid-pod对象关联关系 key：pod uuid value：K8sPodDO 对象
     * @param hostName2HostDOMap       全量服务名-服务对象关联关系 key：serviceName value：ServiceDO 对象
     * @return 返回
     * 根据给定 containerName2PodUuidMap k8sPodUuid2K8sPodDOMap hostName2HostDOMap 构建podId & HostId关联关系集
     */
    /*private List<K8sPodHostPO> parse2K8sPodHostPOList(Map<String, String> containerName2PodUuidMap,
                                                      Map<String, K8sPodDO> k8sPodUuid2K8sPodDOMap,
                                                      Map<String, HostDO> hostName2HostDOMap) {
        List<K8sPodHostPO> k8sPodContainerPOList = new ArrayList<>(containerName2PodUuidMap.size());
        for (Map.Entry<String, String> entry : containerName2PodUuidMap.entrySet()) {
            String containerName = entry.getKey();
            String podUuid = entry.getValue();
            K8sPodDO k8sPodDO = k8sPodUuid2K8sPodDOMap.get(podUuid);
            HostDO hostDO = hostName2HostDOMap.get(containerName);
            if (null == k8sPodDO) {
                throw new ServiceException(
                        String.format("k8s集群同步的元数据={%s}中不存在uuid={%s}的k8sPod", JSON.toJSONString(k8sPodUuid2K8sPodDOMap), podUuid),
                        ErrorCodeEnum.K8S_META_DATA_SYNC_ERROR.getCode()
                );
            }
            if (null == hostDO) {
                throw new ServiceException(
                        String.format("k8s集群同步的元数据={%s}中不存在hostName={%s}的container", JSON.toJSONString(hostName2HostDOMap), containerName),
                        ErrorCodeEnum.K8S_META_DATA_SYNC_ERROR.getCode()
                );
            }
            K8sPodHostPO k8sPodContainerPO = new K8sPodHostPO(k8sPodDO.getId(), hostDO.getId());
            k8sPodContainerPOList.add(k8sPodContainerPO);
        }
        return k8sPodContainerPOList;
    }*/

    /**
     * @param containerName2ServiceNameMap 全量容器-服务关联关系 key：containerName value：serviceName map
     * @param serviceName2ServiceDOMap     全量服务名-服务对象关联关系 key：serviceName value：ServiceDO 对象
     * @param hostName2HostDOMap           全量服务名-服务对象关联关系 key：serviceName value：ServiceDO 对象
     * @return 返回根据给定 containerName2ServiceNameMap serviceName2ServiceDOMap hostName2HostDOMap 构建serviceId & HostId关联关系集
     */
    /*private List<ServiceHostPO> parse2ServiceHostPOList(Map<String, String> containerName2ServiceNameMap,
                                                        Map<String, ServiceDO> serviceName2ServiceDOMap,
                                                        Map<String, HostDO> hostName2HostDOMap) {
        List<ServiceHostPO> serviceHostPOList = new ArrayList<>(containerName2ServiceNameMap.size());
        for (Map.Entry<String, String> entry : containerName2ServiceNameMap.entrySet()) {
            String containerName = entry.getKey();
            String serviceName = entry.getValue();
            ServiceDO serviceDO = serviceName2ServiceDOMap.get(serviceName);
            HostDO hostDO = hostName2HostDOMap.get(containerName);
            if (null == serviceDO) {
                throw new ServiceException(
                        String.format("k8s集群同步的元数据={%s}中不存在serviceName={%s}的service", JSON.toJSONString(serviceName2ServiceDOMap), serviceName),
                        ErrorCodeEnum.K8S_META_DATA_SYNC_ERROR.getCode()
                );
            }
            if (null == hostDO) {
                throw new ServiceException(
                        String.format("k8s集群同步的元数据={%s}中不存在hostName={%s}的container", JSON.toJSONString(hostName2HostDOMap), containerName),
                        ErrorCodeEnum.K8S_META_DATA_SYNC_ERROR.getCode()
                );
            }
            ServiceHostPO serviceHostPO = new ServiceHostPO(serviceDO.getId(), hostDO.getId());
            serviceHostPOList.add(serviceHostPO);
        }
        return serviceHostPOList;
    }*/

    /**
     * @param k8sPodDOList K8sPodDO对象集
     * @return 返回 key：uuid value：k8sPodDO 对象    map
     */
    /*private Map<String, K8sPodDO> parse2K8sPodMap(List<K8sPodDO> k8sPodDOList) {
        Map<String, K8sPodDO> k8sPodUuid2K8sPodDOMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(k8sPodDOList)) {
            for (K8sPodDO k8sPodDO : k8sPodDOList) {
                k8sPodUuid2K8sPodDOMap.put(k8sPodDO.getUuid(), k8sPodDO);
            }
        }
        return k8sPodUuid2K8sPodDOMap;
    }*/

/*    *//**
     * HostDO 对象比较器类
     *//*
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

    *//**
     * ServiceDO 对象比较器类
     *//*
    private class ServiceDOComparator implements Comparator<ServiceDO, String> {
        @Override
        public String getKey(ServiceDO serviceDO) {
            return serviceDO.getServicename();
        }

        @Override
        public boolean compare(ServiceDO t1, ServiceDO t2) {
            return t1.getServicename().equals(t2.getServicename());
        }

        @Override
        public ServiceDO getModified(ServiceDO source, ServiceDO target) {
            source.setServicename(target.getServicename());
            return source;
        }
    }*/

/*    *//**
     * K8sPodDO 对象比较器类
     *//*
    class K8sPodDOComparator implements Comparator<K8sPodDO, String> {
        @Override
        public String getKey(K8sPodDO k8sPodDO) {
            return k8sPodDO.getUuid();
        }

        @Override
        public boolean compare(K8sPodDO t1, K8sPodDO t2) {

            return t1.getContainerNames().equals(t2.getContainerNames()) &&
                    t1.getLogHostPath().equals(t2.getLogHostPath()) &&
                    t1.getLogMountPath().equals(t2.getLogMountPath()) &&
                    t1.getNodeIp().equals(t2.getNodeIp()) &&
                    t1.getNodeName().equals(t2.getNodeName()) &&
                    t1.getPodIp().equals(t2.getPodIp()) &&
                    t1.getServiceName().equals(t2.getServiceName());
        }

        @Override
        public K8sPodDO getModified(K8sPodDO source, K8sPodDO target) {
            source.setContainerNames(target.getContainerNames());
            source.setLogHostPath(target.getLogHostPath());
            source.setLogMountPath(target.getLogMountPath());
            source.setNodeIp(target.getNodeIp());
            source.setNodeName(target.getNodeName());
            source.setPodIp(target.getPodIp());
            source.setServiceName(target.getServiceName());
            return source;
        }
    }

    *//**
     * ServiceHostPO 对象比较器类
     *//*
    class ServiceHostPOComparator implements Comparator<ServiceHostPO, String> {
        @Override
        public String getKey(ServiceHostPO serviceHostPO) {
            return serviceHostPO.getServiceId() + "_" + serviceHostPO.getHostId();
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

    *//**
     * K8sPodHostPO 对象比较器
     *//*
    class K8sPodHostPOComparator implements Comparator<K8sPodHostPO, String> {
        @Override
        public String getKey(K8sPodHostPO k8sPodContainerPO) {
            return k8sPodContainerPO.getK8sPodId() + "_" + k8sPodContainerPO.getHostId();
        }

        @Override
        public boolean compare(K8sPodHostPO t1, K8sPodHostPO t2) {
            return t1.getK8sPodId().equals(t2.getK8sPodId()) &&
                    t1.getHostId().equals(t2.getHostId());
        }

        @Override
        public K8sPodHostPO getModified(K8sPodHostPO source, K8sPodHostPO target) {
            return source;
        }
    }

    *//**
     * 主机容器比较器 容器前 主机后
     *//*
    class HostContainerComparator implements java.util.Comparator<HostDO> {
        @Override
        public int compare(HostDO o1, HostDO o2) {
            return o2.getContainer() - o1.getContainer();
        }
    }*/

}

