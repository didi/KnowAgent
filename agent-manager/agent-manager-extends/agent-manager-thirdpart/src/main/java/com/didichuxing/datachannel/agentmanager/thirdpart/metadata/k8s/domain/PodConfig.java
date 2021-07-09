package com.didichuxing.datachannel.agentmanager.thirdpart.metadata.k8s.domain;

import java.util.List;
import java.util.Map;

/**
 * k8s pod 配置
 */
public class PodConfig {

    private String uuid;

    private Map<String, String> annotations;

    private List<String> containerNames;

    private Map<String, PodReference> referenceMap;

    private String nodeName;

    private String namespace;

    private String nodeIp;

    private String podIp;

    public Map<String, String> getAnnotations() {
        return annotations;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setAnnotations(Map<String, String> annotations) {
        this.annotations = annotations;
    }

    public List<String> getContainerNames() {
        return containerNames;
    }

    public void setContainerNames(List<String> containerNames) {
        this.containerNames = containerNames;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Map<String, PodReference> getReferenceMap() {
        return referenceMap;
    }

    public void setReferenceMap(Map<String, PodReference> referenceMap) {
        this.referenceMap = referenceMap;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNodeIp() {
        return nodeIp;
    }

    public void setNodeIp(String nodeIp) {
        this.nodeIp = nodeIp;
    }

    public String getPodIp() {
        return podIp;
    }

    public void setPodIp(String podIp) {
        this.podIp = podIp;
    }
}