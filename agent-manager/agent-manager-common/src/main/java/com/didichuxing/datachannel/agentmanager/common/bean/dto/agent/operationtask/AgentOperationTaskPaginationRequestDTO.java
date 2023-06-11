package com.didichuxing.datachannel.agentmanager.common.bean.dto.agent.operationtask;

import com.didichuxing.datachannel.agentmanager.common.bean.dto.PaginationRequestDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentOperationTaskPaginationRequestDTO extends PaginationRequestDTO {

    @ApiModelProperty(value = "任务 id", notes="")
    private Long agentOperationTaskId;

    @ApiModelProperty(value = "任务名", notes="")
    private String agentOperationTaskName;

    @ApiModelProperty(value = "任务类型 0:安装 1：卸载 2：升级")
    private List<Integer> agentOperationTaskTypeList;

    @ApiModelProperty(value = "任务状态 0：完成 1：执行中", notes="")
    private List<Integer> agentOperationTaskStatusList;

    @ApiModelProperty(value = "任务创建时间开始检索时间", notes="")
    private Long agentOperationTaskStartTimeStart;

    @ApiModelProperty(value = "任务创建时间结束检索时间", notes="")
    private Long agentOperationTaskStartTimeEnd;

    @ApiModelProperty(value = "排序依照的字段，可选task_type hosts_number task_status task_start_time task_end_time operator", notes="")
    private String sortColumn;

    @ApiModelProperty(value = "是否升序", notes="")
    private Boolean asc;

    public Long getAgentOperationTaskId() {
        return agentOperationTaskId;
    }

    public void setAgentOperationTaskId(Long agentOperationTaskId) {
        this.agentOperationTaskId = agentOperationTaskId;
    }

    public String getAgentOperationTaskName() {
        return agentOperationTaskName;
    }

    public void setAgentOperationTaskName(String agentOperationTaskName) {
        this.agentOperationTaskName = agentOperationTaskName;
    }

    public List<Integer> getAgentOperationTaskTypeList() {
        return agentOperationTaskTypeList;
    }

    public void setAgentOperationTaskTypeList(List<Integer> agentOperationTaskTypeList) {
        this.agentOperationTaskTypeList = agentOperationTaskTypeList;
    }

    public List<Integer> getAgentOperationTaskStatusList() {
        return agentOperationTaskStatusList;
    }

    public void setAgentOperationTaskStatusList(List<Integer> agentOperationTaskStatusList) {
        this.agentOperationTaskStatusList = agentOperationTaskStatusList;
    }

    public Long getAgentOperationTaskStartTimeStart() {
        return agentOperationTaskStartTimeStart;
    }

    public void setAgentOperationTaskStartTimeStart(Long agentOperationTaskStartTimeStart) {
        this.agentOperationTaskStartTimeStart = agentOperationTaskStartTimeStart;
    }

    public Long getAgentOperationTaskStartTimeEnd() {
        return agentOperationTaskStartTimeEnd;
    }

    public void setAgentOperationTaskStartTimeEnd(Long agentOperationTaskStartTimeEnd) {
        this.agentOperationTaskStartTimeEnd = agentOperationTaskStartTimeEnd;
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
