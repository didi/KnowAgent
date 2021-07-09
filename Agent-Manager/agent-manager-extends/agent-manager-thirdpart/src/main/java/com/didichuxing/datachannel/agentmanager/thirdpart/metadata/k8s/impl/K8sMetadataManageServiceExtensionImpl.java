package com.didichuxing.datachannel.agentmanager.thirdpart.metadata.k8s.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.k8s.K8sPodDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetadataResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.host.HostTypeEnum;
import com.didichuxing.datachannel.agentmanager.thirdpart.metadata.MetadataManageServiceExtension;
import com.didichuxing.datachannel.agentmanager.thirdpart.metadata.k8s.domain.PodConfig;
import com.didichuxing.datachannel.agentmanager.thirdpart.metadata.k8s.domain.PodReference;
import com.didichuxing.datachannel.agentmanager.thirdpart.metadata.k8s.util.K8sUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class K8sMetadataManageServiceExtensionImpl implements MetadataManageServiceExtension {

    private static final Logger LOGGER = LoggerFactory.getLogger(K8sMetadataManageServiceExtensionImpl.class);

    @Value("${metadata.sync.request.k8s.service-key}")
    private String serviceKey;

    @Value("${metadata.sync.request.k8s.path-name}")
    private String pathName;

    /**
     * @param podConfigList
     * @return
     */
    private MetadataResult parse2MetadataResult(List<PodConfig> podConfigList) {
        MetadataResult result = new MetadataResult();
        List<K8sPodDO> podList = new ArrayList<>();
        for (PodConfig podConfig : podConfigList) {
            K8sPodDO k8sPodDO = this.convert(podConfig);
            if (k8sPodDO != null) {
                podList.add(this.convert(podConfig));
            }
        }
        List<HostDO> hostList = new ArrayList<>();
        List<HostDO> containerList = new ArrayList<>();
        List<ServiceDO> services = new ArrayList<>();
        Map<String, String> containerServiceMap = new HashMap<>();
        Map<String, String> containerHostMap = new HashMap<>();
        Map<String, String> containerUuidMap = new HashMap<>();
        for (PodConfig podConfig : podConfigList) {
            HostDO host = new HostDO();
            host.setIp(podConfig.getNodeIp());
            host.setHostName(podConfig.getNodeName());
            host.setContainer(HostTypeEnum.HOST.getCode());
            hostList.add(host);
            ServiceDO service = new ServiceDO();
            if (podConfig.getAnnotations() == null) {
                LOGGER.error("pod has no annotations, pod uid: {}", podConfig.getUuid());
                continue;
            }
            String servicename = podConfig.getAnnotations().get(serviceKey);
            if (servicename == null) {
                LOGGER.error("no annotations named {}, pod uid: {}, annotations: {}", serviceKey, podConfig.getUuid(), podConfig.getAnnotations());
                continue;
            }
            service.setServicename(servicename);
            services.add(service);
            List<String> containerNames = podConfig.getContainerNames();
            for (String containerName : containerNames) {
                containerServiceMap.put(containerName, servicename);
                containerHostMap.put(containerName, podConfig.getNodeName());
                containerUuidMap.put(containerName, podConfig.getUuid());

                HostDO container = new HostDO();
                container.setIp(podConfig.getPodIp());
                container.setHostName(containerName);
                container.setContainer(HostTypeEnum.CONTAINER.getCode());
                container.setParentHostName(podConfig.getNodeName());
                containerList.add(container);
            }
        }

        result.setK8sPodDOList(podList);
        result.setHostDOList(hostList);
        result.setContainerList(containerList);
        result.setK8sPodDOList(podList);
        result.setServiceDOListFromRemote(services);
        result.setContainerName2ServiceNameMap(containerServiceMap);
        result.setContainerName2HostNameMap(containerHostMap);
        result.setContainerName2PodUuidMap(containerUuidMap);
        return result;
    }

    private K8sPodDO convert(PodConfig config) {
        String uuid = config.getUuid();
        if (config.getReferenceMap().get(pathName) == null) {
            LOGGER.error("logpath is null, pod uid: {}", config.getUuid());
            return null;
        }
        if (config.getAnnotations().get(serviceKey) == null) {
            LOGGER.error("servicename is null, pod uid: {}", config.getUuid());
            return null;
        }
        K8sPodDO pod = new K8sPodDO();
        pod.setUuid(uuid);
        pod.setContainerNames(JSON.toJSONString(config.getContainerNames()));
        pod.setServiceName(config.getAnnotations().get(serviceKey));
        Map<String, PodReference> map = config.getReferenceMap();
        pod.setLogHostPath(map.get(pathName).getHostPath());
        pod.setLogMountPath(map.get(pathName).getMountPath());
        pod.setNodeIp(config.getNodeIp());
        pod.setPodIp(config.getPodIp());
        pod.setNodeName(config.getNodeName());
        return pod;
    }

    @Override
    public MetadataResult pullMetadataResultFromRemote() {
        List<PodConfig> podConfigList = K8sUtil.getAllPodConfig();
        if (CollectionUtils.isEmpty(podConfigList)) {
            return null;
        }
        return parse2MetadataResult(podConfigList);
    }

}
