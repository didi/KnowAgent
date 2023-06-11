package com.didichuxing.datachannel.agentmanager.common.bean.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaginationResult<T> {

    @ApiModelProperty(value = "分页列表结果集", notes="")
    private List<T> resultSet;
    @ApiModelProperty(value = "满足条件总记录数", notes="")
    private Integer total;
    @ApiModelProperty(value = "当前第几页", notes="")
    private Integer pageNo;
    @ApiModelProperty(value = "每页记录行数", notes="")
    private Integer pageSize;

    public PaginationResult() {

    }

    public PaginationResult(List resultSet, Integer total, Integer pageNo, Integer pageSize) {
        this.resultSet = resultSet;
        this.total = total;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public List getResultSet() {
        return resultSet;
    }

    public void setResultSet(List resultSet) {
        this.resultSet = resultSet;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
