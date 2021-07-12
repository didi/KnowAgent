package com.didichuxing.datachannel.agentmanager.common.bean.domain.k8s;

public class K8sPodHostDO {
    private Long id;

    private Long k8sPodId;

    private Long hostId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getK8sPodId() {
        return k8sPodId;
    }

    public void setK8sPodId(Long k8sPodId) {
        this.k8sPodId = k8sPodId;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }
}