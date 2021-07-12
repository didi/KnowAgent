package com.didichuxing.datachannel.agentmanager.common.bean.po.k8s;

import lombok.Data;

@Data
public class K8sPodContainerPO {

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

    public K8sPodContainerPO(Long k8sPodId, Long hostId) {
        this.k8sPodId = k8sPodId;
        this.hostId = hostId;
    }

    public K8sPodContainerPO() {
    }
}
