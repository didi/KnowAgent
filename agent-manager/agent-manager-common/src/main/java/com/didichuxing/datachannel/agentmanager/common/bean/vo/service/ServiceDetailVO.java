package com.didichuxing.datachannel.agentmanager.common.bean.vo.service;

import com.didichuxing.datachannel.agentmanager.common.bean.vo.host.HostVO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceDetailVO {

    @ApiModelProperty(value = "服务对象id", notes="")
    private Long id;
    @ApiModelProperty(value = "服务名", notes="")
    private String serviceName;
    @ApiModelProperty(value = "服务关联的主机对象集", notes="")
    private List<HostVO> hostList;
    @ApiModelProperty(value = "服务新增时间", notes="")
    private Long createTime;

    public void setId(Long id) {
        this.id = id;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setHostList(List<HostVO> hostList) {
        this.hostList = hostList;
    }

    public Long getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public List<HostVO> getHostList() {
        return hostList;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getCreateTime() {
        return createTime;
    }
}
