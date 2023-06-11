package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.operationtask;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentOperationTaskPaginationRecordVO {

    @ApiModelProperty(value = "任务 id", notes="")
    private Long agentOperationTaskId;

    @ApiModelProperty(value = "任务名", notes="")
    private String agentOperationTaskName;

    @ApiModelProperty(value = "任务类型 0:安装 1：卸载 2：升级")
    private Integer agentOperationTaskType;

    @ApiModelProperty(value = "任务涉及到的主机数量")
    private Integer relationHostCount;

    @ApiModelProperty(value = "任务状态 0：完成 1：执行中", notes="")
    private Integer agentOperationTaskStatus;

    @ApiModelProperty(value = "任务开始时间", notes="")
    private Long agentOperationTaskStartTime;

    @ApiModelProperty(value = "任务结束时间", notes="")
    private Long agentOperationTaskEndTime;

    @ApiModelProperty(value = "创建人", notes="")
    private String operator;

    @ApiModelProperty(value = "执行概览：成功数", notes="")
    private Integer successful;

    @ApiModelProperty(value = "执行概览：失败数", notes="")
    private Integer failed;

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

    public Integer getAgentOperationTaskType() {
        return agentOperationTaskType;
    }

    public void setAgentOperationTaskType(Integer agentOperationTaskType) {
        this.agentOperationTaskType = agentOperationTaskType;
    }

    public Integer getRelationHostCount() {
        return relationHostCount;
    }

    public void setRelationHostCount(Integer relationHostCount) {
        this.relationHostCount = relationHostCount;
    }

    public Integer getAgentOperationTaskStatus() {
        return agentOperationTaskStatus;
    }

    public void setAgentOperationTaskStatus(Integer agentOperationTaskStatus) {
        this.agentOperationTaskStatus = agentOperationTaskStatus;
    }

    public Long getAgentOperationTaskStartTime() {
        return agentOperationTaskStartTime;
    }

    public void setAgentOperationTaskStartTime(Long agentOperationTaskStartTime) {
        this.agentOperationTaskStartTime = agentOperationTaskStartTime;
    }

    public Long getAgentOperationTaskEndTime() {
        return agentOperationTaskEndTime;
    }

    public void setAgentOperationTaskEndTime(Long agentOperationTaskEndTime) {
        this.agentOperationTaskEndTime = agentOperationTaskEndTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getSuccessful() {
        return successful;
    }

    public void setSuccessful(Integer successful) {
        this.successful = successful;
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }
}
