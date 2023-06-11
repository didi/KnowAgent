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
public class ServiceUpdateDTO {

    @ApiModelProperty(value = "服务对象id 注：新增Service接口不可填，仅在更新Service接口必填")
    private Long id;

    @ApiModelProperty(value = "服务关联的主机id集 必填")
    private List<Long> hostIdList;

    public void setHostIdList(List<Long> hostIdList) {
        this.hostIdList = hostIdList;
    }

    public List<Long> getHostIdList() {
        return hostIdList;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
