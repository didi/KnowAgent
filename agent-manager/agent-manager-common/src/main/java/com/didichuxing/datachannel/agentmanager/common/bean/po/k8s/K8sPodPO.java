package com.didichuxing.datachannel.agentmanager.common.bean.po.k8s;

import com.didichuxing.datachannel.agentmanager.common.bean.po.BasePO;

public class K8sPodPO extends BasePO {

    private Long id;
    private String uuid;
    private String name;
    private String namespace;
    private String podIp;
    private String serviceName;
    private String logMountPath;
    private String logHostPath;
    private String nodeName;
    private String nodeIp;
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
