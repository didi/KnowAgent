package com.didichuxing.datachannel.agentmanager.common.bean.dto.service;

import com.didichuxing.datachannel.agentmanager.common.bean.dto.PaginationRequestDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicePaginationRequestDTO extends PaginationRequestDTO {

    @ApiModelProperty(value = "服务名")
    private String servicename;

    @ApiModelProperty(value = "服务创建时间起始检索时间", notes="")
    private Long serviceCreateTimeStart;

    @ApiModelProperty(value = "服务创建时间结束检索时间", notes="")
    private Long serviceCreateTimeEnd;

    @ApiModelProperty(value = "排序字段 可选service_name relation_host_count create_time", notes="")
    private String sortColumn;

    @ApiModelProperty(value = "是否升序排序", notes="")
    private Boolean asc;

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public String getServicename() {
        return servicename;
    }

    public Long getServiceCreateTimeStart() {
        return serviceCreateTimeStart;
    }

    public Long getServiceCreateTimeEnd() {
        return serviceCreateTimeEnd;
    }

    public void setServiceCreateTimeStart(Long serviceCreateTimeStart) {
        this.serviceCreateTimeStart = serviceCreateTimeStart;
    }

    public void setServiceCreateTimeEnd(Long serviceCreateTimeEnd) {
        this.serviceCreateTimeEnd = serviceCreateTimeEnd;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public Boolean getAsc() {
        return asc;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }
}
