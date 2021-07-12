package com.didichuxing.datachannel.agentmanager.remote.operation.task.common;

import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskStateEnum;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/19
 */
public class ClusterTaskStatus {
    private Long taskId;

    private Long clusterId;

    private Boolean rollback;

    private AgentOperationTaskStateEnum status;

    private List<ClusterTaskSubStatus> subStatusList;

    public ClusterTaskStatus(Long taskId,
                             Long clusterId,
                             Boolean rollback,
                             AgentOperationTaskStateEnum status,
                             List<ClusterTaskSubStatus> subStatusList) {
        this.taskId = taskId;
        this.clusterId = clusterId;
        this.rollback = rollback;
        this.status = status;
        this.subStatusList = subStatusList;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Boolean getRollback() {
        return rollback;
    }

    public void setRollback(Boolean rollback) {
        this.rollback = rollback;
    }

    public AgentOperationTaskStateEnum getStatus() {
        return status;
    }

    public void setStatus(AgentOperationTaskStateEnum status) {
        this.status = status;
    }

    public List<ClusterTaskSubStatus> getSubStatusList() {
        return subStatusList;
    }

    public void setSubStatusList(List<ClusterTaskSubStatus> subStatusList) {
        this.subStatusList = subStatusList;
    }

    @Override
    public String toString() {
        return "ClusterTaskStatus{" +
                "taskId=" + taskId +
                ", clusterId=" + clusterId +
                ", rollback=" + rollback +
                ", status=" + status +
                ", subStatusList=" + subStatusList +
                '}';
    }
}