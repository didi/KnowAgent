package com.didichuxing.datachannel.agentmanager.thirdpart.metadata.k8s.domain;

public class PodReference {

    private String name;

    private String hostPath;

    private String mountPath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostPath() {
        return hostPath;
    }

    public void setHostPath(String hostPath) {
        this.hostPath = hostPath;
    }

    public String getMountPath() {
        return mountPath;
    }

    public void setMountPath(String mountPath) {
        this.mountPath = mountPath;
    }
}
