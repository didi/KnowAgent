package com.didichuxing.datachannel.agentmanager.common.bean.po.service;

import lombok.Data;

@Data
public class ServiceHostPO {
    private Long id;

    private Long serviceId;

    private Long hostId;

    public ServiceHostPO() {
    }

    public ServiceHostPO(Long serviceId, Long hostId) {
        this.serviceId = serviceId;
        this.hostId = hostId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }
}