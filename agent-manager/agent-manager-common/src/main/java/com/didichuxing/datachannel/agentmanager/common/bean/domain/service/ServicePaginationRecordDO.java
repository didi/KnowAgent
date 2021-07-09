package com.didichuxing.datachannel.agentmanager.common.bean.domain.service;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.BaseDO;
import lombok.Data;

@Data
public class ServicePaginationRecordDO extends BaseDO {

    /**
     * 服务信息唯一标识
     */
    private Long id;
    /**
     * 服务名
     */
    private String servicename;
    /**
     * 服务关联的主机数
     */
    private Integer relationHostCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public Integer getRelationHostCount() {
        return relationHostCount;
    }

    public void setRelationHostCount(Integer relationHostCount) {
        this.relationHostCount = relationHostCount;
    }
}
