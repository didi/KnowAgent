package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web;

import com.didichuxing.datachannel.agentmanager.common.bean.dto.PaginationRequestDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LogCollectTaskPaginationRequestDTO extends PaginationRequestDTO {

    @ApiModelProperty(value = "日志采集任务名")
    private String logCollectTaskName;

    @ApiModelProperty(value = "采集任务类型 0：常规流式采集 1：按指定时间范围采集")
    private List<Integer> logCollectTaskTypeList;

    @ApiModelProperty(value = "日志采集任务健康度 ", notes="")
    private List<Integer> logCollectTaskHealthLevelList;

    @ApiModelProperty(value = "日志采集任务id", notes="")
    private Long logCollectTaskId;

    @ApiModelProperty(value = "服务id")
    private List<Long> serviceIdList;

    @ApiModelProperty(value = "日志采集任务创建时间起始检索时间", notes="")
    private Long locCollectTaskCreateTimeStart;

    @ApiModelProperty(value = "日志采集任务创建时间结束检索时间", notes="")
    private Long locCollectTaskCreateTimeEnd;

    @ApiModelProperty(value = "排序依照的字段，可选log_collect_task_finish_time create_time", notes="")
    private String sortColumn;

    @ApiModelProperty(value = "是否升序", notes="")
    private Boolean asc;

    public String getLogCollectTaskName() {
        return logCollectTaskName;
    }

    public void setLogCollectTaskName(String logCollectTaskName) {
        this.logCollectTaskName = logCollectTaskName;
    }

    public List<Integer> getLogCollectTaskTypeList() {
        return logCollectTaskTypeList;
    }

    public void setLogCollectTaskTypeList(List<Integer> logCollectTaskTypeList) {
        this.logCollectTaskTypeList = logCollectTaskTypeList;
    }

    public List<Integer> getLogCollectTaskHealthLevelList() {
        return logCollectTaskHealthLevelList;
    }

    public void setLogCollectTaskHealthLevelList(List<Integer> logCollectTaskHealthLevelList) {
        this.logCollectTaskHealthLevelList = logCollectTaskHealthLevelList;
    }

    public Long getLogCollectTaskId() {
        return logCollectTaskId;
    }

    public void setLogCollectTaskId(Long logCollectTaskId) {
        this.logCollectTaskId = logCollectTaskId;
    }

    public List<Long> getServiceIdList() {
        return serviceIdList;
    }

    public void setServiceIdList(List<Long> serviceIdList) {
        this.serviceIdList = serviceIdList;
    }

    public Long getLocCollectTaskCreateTimeStart() {
        return locCollectTaskCreateTimeStart;
    }

    public void setLocCollectTaskCreateTimeStart(Long locCollectTaskCreateTimeStart) {
        this.locCollectTaskCreateTimeStart = locCollectTaskCreateTimeStart;
    }

    public Long getLocCollectTaskCreateTimeEnd() {
        return locCollectTaskCreateTimeEnd;
    }

    public void setLocCollectTaskCreateTimeEnd(Long locCollectTaskCreateTimeEnd) {
        this.locCollectTaskCreateTimeEnd = locCollectTaskCreateTimeEnd;
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
