package com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask;

import lombok.Data;

@Data
public class LogCollectTaskServicePO {
    private Long id;

    private Long logCollectorTaskId;

    private Long serviceId;

    public LogCollectTaskServicePO() {
    }

    public LogCollectTaskServicePO(Long logCollectorTaskId, Long serviceId) {
        this.logCollectorTaskId = logCollectorTaskId;
        this.serviceId = serviceId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLogCollectorTaskId(Long logCollectorTaskId) {
        this.logCollectorTaskId = logCollectorTaskId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public Long getId() {
        return id;
    }

    public Long getLogCollectorTaskId() {
        return logCollectorTaskId;
    }
}