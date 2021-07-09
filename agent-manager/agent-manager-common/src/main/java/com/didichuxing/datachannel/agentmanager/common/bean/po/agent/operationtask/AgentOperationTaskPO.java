package com.didichuxing.datachannel.agentmanager.common.bean.po.agent.operationtask;

import com.didichuxing.datachannel.agentmanager.common.bean.po.BasePO;
import lombok.Data;

import java.util.Date;

/**
 * @author huqidong
 * @date 2020-09-21
 * 采集端操作任务
 */
@Data
public class AgentOperationTaskPO extends BasePO {

        private Long id;

        private String taskName;

        private Integer taskStatus;

        private Integer taskType;

        private Integer hostsNumber;

        private Long sourceAgentVersionId;

        private Long targetAgentVersionId;

        private Long externalAgentTaskId;

        private Date taskStartTime;

        private Date taskEndTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getHostsNumber() {
        return hostsNumber;
    }

    public void setHostsNumber(Integer hostsNumber) {
        this.hostsNumber = hostsNumber;
    }

    public Long getSourceAgentVersionId() {
        return sourceAgentVersionId;
    }

    public void setSourceAgentVersionId(Long sourceAgentVersionId) {
        this.sourceAgentVersionId = sourceAgentVersionId;
    }

    public Long getTargetAgentVersionId() {
        return targetAgentVersionId;
    }

    public void setTargetAgentVersionId(Long targetAgentVersionId) {
        this.targetAgentVersionId = targetAgentVersionId;
    }

    public Long getExternalAgentTaskId() {
        return externalAgentTaskId;
    }

    public void setExternalAgentTaskId(Long externalAgentTaskId) {
        this.externalAgentTaskId = externalAgentTaskId;
    }

    public Date getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(Date taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public Date getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(Date taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

}
