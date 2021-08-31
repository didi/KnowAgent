package com.didichuxing.datachannel.agentmanager.common.bean.domain.k8s;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.BaseDO;

public class K8sPodDO extends BaseDO {

    /**
     * id
     */
    private Long id;
    /**
     * pod 在给定 k8s 集群唯一标示
     */
    private String uuid;
    /**
     * pod名称
     */
    private String name;
    /**
     * pod的namespace
     */
    private String namespace;
    /**
     * pod ip
     */
    private String podIp;
    /**
     * pod 所属服务名
     */
    private String serviceName;
    /**
     *  容器内路径
     */
    private String logMountPath;
    /*
     *  主机对应真实路径
     */
    private String logHostPath;
    /**
     * pod 对应宿主机名
     */
    private String nodeName;
    /**
     * pod 对应宿主机 ip
     */
    private String nodeIp;
    /**
     *  pod 包含容器名集
     */
    private String containerNames;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getPodIp() {
        return podIp;
    }

    public void setPodIp(String podIp) {
        this.podIp = podIp;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getLogMountPath() {
        return logMountPath;
    }

    public void setLogMountPath(String logMountPath) {
        this.logMountPath = logMountPath;
    }

    public String getLogHostPath() {
        return logHostPath;
    }

    public void setLogHostPath(String logHostPath) {
        this.logHostPath = logHostPath;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeIp() {
        return nodeIp;
    }

    public void setNodeIp(String nodeIp) {
        this.nodeIp = nodeIp;
    }

    public String getContainerNames() {
        return containerNames;
    }

    public void setContainerNames(String containerNames) {
        this.containerNames = containerNames;
    }

}
