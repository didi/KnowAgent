package com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.k8s.K8sPodDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;

import java.util.List;
import java.util.Map;

/**
 * 元数据结果集
 */
public class MetadataResult {
    /**
     * k8s pod 集
     */
    private List<K8sPodDO> k8sPodDOList;
    /**
     * pod 对应宿主机集
     */
    private List<HostDO> hostDOList;
    /**
     * pod 对应容器集
     */
    private List<HostDO> containerList;
    /**
     * pod 对应服务集
     */
    private List<ServiceDO> serviceDOListFromRemote;
    /**
     * 服务名 - 容器名关联关系
     */
    private Map<String, String> containerName2ServiceNameMap;
    /**
     * 容器名 - 主机名关联关系
     */
    private Map<String, String> containerName2HostNameMap;
    /**
     * 容器名 - pod uuid 关联关系
     */
    private Map<String, String> containerName2PodUuidMap;

    public List<K8sPodDO> getK8sPodDOList() {
        return k8sPodDOList;
    }

    public void setK8sPodDOList(List<K8sPodDO> k8sPodDOList) {
        this.k8sPodDOList = k8sPodDOList;
    }

    public List<HostDO> getHostDOList() {
        return hostDOList;
    }

    public void setHostDOList(List<HostDO> hostDOList) {
        this.hostDOList = hostDOList;
    }

    public List<HostDO> getContainerList() {
        return containerList;
    }

    public void setContainerList(List<HostDO> containerList) {
        this.containerList = containerList;
    }

    public List<ServiceDO> getServiceDOListFromRemote() {
        return serviceDOListFromRemote;
    }

    public void setServiceDOListFromRemote(List<ServiceDO> serviceDOListFromRemote) {
        this.serviceDOListFromRemote = serviceDOListFromRemote;
    }

    public Map<String, String> getContainerName2ServiceNameMap() {
        return containerName2ServiceNameMap;
    }

    public void setContainerName2ServiceNameMap(Map<String, String> containerName2ServiceNameMap) {
        this.containerName2ServiceNameMap = containerName2ServiceNameMap;
    }

    public Map<String, String> getContainerName2HostNameMap() {
        return containerName2HostNameMap;
    }

    public void setContainerName2HostNameMap(Map<String, String> containerName2HostNameMap) {
        this.containerName2HostNameMap = containerName2HostNameMap;
    }

    public Map<String, String> getContainerName2PodUuidMap() {
        return containerName2PodUuidMap;
    }

    public void setContainerName2PodUuidMap(Map<String, String> containerName2PodUuidMap) {
        this.containerName2PodUuidMap = containerName2PodUuidMap;
    }
}
