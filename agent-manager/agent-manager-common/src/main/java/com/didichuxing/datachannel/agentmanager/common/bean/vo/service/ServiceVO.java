package com.didichuxing.datachannel.agentmanager.common.bean.vo.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceVO {

    @ApiModelProperty(value = "服务对象id", notes="")
    private Long id;
    @ApiModelProperty(value = "服务名", notes="")
    private String servicename;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public String getServicename() {
        return servicename;
    }
}
