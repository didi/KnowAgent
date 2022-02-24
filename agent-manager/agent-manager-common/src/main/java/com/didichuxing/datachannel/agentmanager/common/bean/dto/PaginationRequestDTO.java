package com.didichuxing.datachannel.agentmanager.common.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

/**
 * 分页请求基类DTO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaginationRequestDTO {

    @ApiModelProperty(value = "每页行数")
    private Integer pageSize;

    @ApiModelProperty(value = "当前第几页")
    private Integer pageNo;

    @ApiModelProperty(value = "检索关键字")
    private String queryTerm;

    public String getQueryTerm() {
        return queryTerm;
    }

    public void setQueryTerm(String queryTerm) {
        this.queryTerm = queryTerm;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public Integer getLimitFrom() {
        if(1 == pageNo) {
            return 0;
        } else {//pageNo > 1
            return (pageNo - 1) * pageSize;
        }
    }

    public Integer getLimitSize() {
        return pageSize;
    }

}
