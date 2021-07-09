package com.didichuxing.datachannel.agentmanager.remote.operation.task.common;

import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskSubStateEnum;

/**
 * @author zengqiao
 * @date 20/4/26
 */
public class ClusterTaskSubStatus {
    private Integer groupNum;

    private String hostname;

    private AgentOperationTaskSubStateEnum status;

    public Integer getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(Integer groupNum) {
        this.groupNum = groupNum;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public AgentOperationTaskSubStateEnum getStatus() {
        return status;
    }

    public void setStatus(AgentOperationTaskSubStateEnum status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ClusterTaskSubStatus{" +
                "groupNum=" + groupNum +
                ", hostname='" + hostname + '\'' +
                ", status=" + status +
                '}';
    }
}