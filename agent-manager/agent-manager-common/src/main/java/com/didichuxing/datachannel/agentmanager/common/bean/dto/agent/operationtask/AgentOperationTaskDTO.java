package com.didichuxing.datachannel.agentmanager.common.bean.dto.agent.operationtask;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentOperationTaskDTO {

    @ApiModelProperty(value = "任务类型 0：安装 1：卸载 2：升级 必填", notes="")
    private Integer taskType;

    @ApiModelProperty(value = "待安装Agent的主机id集 注：Agent安装case必填", notes="")
    private List<Long> hostIds;

    @ApiModelProperty(value = "待安装/升级的Agent对应的Agent版本对象id 注：Agent安装/升级case必填", notes="")
    private Long agentVersionId;

    @ApiModelProperty(value = "待升级/卸载的Agent对象id集 注：Agent卸载/升级case必填", notes="")
    private List<Long> agentIds;

    @ApiModelProperty(value = "卸载Agent时是否需要检查Agent待采集日志是否全部采集完，如设置为检查，当卸载Agent时，Agent仍未采集完待采集日志，将会导致Agent卸载失败 0：不检查 1：检查 注：Agent卸载case必填", notes="")
    private Integer checkAgentCompleteCollect;

    @ApiModelProperty(value = "操作任务名", notes="")
    private String taskName;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public List<Long> getHostIds() {
        return hostIds;
    }

    public void setHostIds(List<Long> hostIds) {
        this.hostIds = hostIds;
    }

    public Long getAgentVersionId() {
        return agentVersionId;
    }

    public void setAgentVersionId(Long agentVersionId) {
        this.agentVersionId = agentVersionId;
    }

    public List<Long> getAgentIds() {
        return agentIds;
    }

    public void setAgentIds(List<Long> agentIds) {
        this.agentIds = agentIds;
    }

    public Integer getCheckAgentCompleteCollect() {
        return checkAgentCompleteCollect;
    }

    public void setCheckAgentCompleteCollect(Integer checkAgentCompleteCollect) {
        this.checkAgentCompleteCollect = checkAgentCompleteCollect;
    }

}
