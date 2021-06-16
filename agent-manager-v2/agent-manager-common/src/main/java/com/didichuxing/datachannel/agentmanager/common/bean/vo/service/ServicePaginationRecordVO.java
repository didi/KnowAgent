package com.didichuxing.datachannel.agentmanager.common.bean.vo.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicePaginationRecordVO {

    @ApiModelProperty(value = "服务对象id", notes="")
    private Long id;
    @ApiModelProperty(value = "服务名", notes="")
    private String serviceName;
    @ApiModelProperty(value = "服务关联的主机数", notes="")
    private Integer relationHostCount;
    @ApiModelProperty(value = "服务创建时间 格式：unix 13 位时间戳", notes="")
    private Long createTime;

    public void setId(Long id) {
        this.id = id;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setRelationHostCount(Integer relationHostCount) {
        this.relationHostCount = relationHostCount;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Integer getRelationHostCount() {
        return relationHostCount;
    }

    public Long getCreateTime() {
        return createTime;
    }

}
