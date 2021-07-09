package com.didichuxing.datachannel.agentmanager.common.bean.po.service;

import lombok.Data;

@Data
public class ServiceProjectPO {
    private Long id;

    private Long serviceId;

    private Long projectId;

    public ServiceProjectPO() {
    }

    public ServiceProjectPO(Long serviceId, Long projectId) {
        this.serviceId = serviceId;
        this.projectId = projectId;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}