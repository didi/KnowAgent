package com.didichuxing.datachannel.agentmanager.common.bean.dto.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * 服务对象
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceCreateDTO {

    @ApiModelProperty(value = "服务名 注：新增Service接口必填，更新Service接口不可填")
    private String servicename;

    @ApiModelProperty(value = "服务关联的主机id集 必填")
    private List<Long> hostIdList;

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public void setHostIdList(List<Long> hostIdList) {
        this.hostIdList = hostIdList;
    }

    public String getServicename() {
        return servicename;
    }

    public List<Long> getHostIdList() {
        return hostIdList;
    }

}
