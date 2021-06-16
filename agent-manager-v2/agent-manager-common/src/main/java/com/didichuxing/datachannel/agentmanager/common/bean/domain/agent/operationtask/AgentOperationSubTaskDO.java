package com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.operationtask;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.BaseDO;

import java.util.Date;

public class AgentOperationSubTaskDO extends BaseDO {

    private Long id;

    private Long agentOperationTaskId;

    private String hostName;

    private String ip;

    private Integer container;

    private Long sourceAgentVersionId;

    private Date taskStartTime;

    private Date taskEndTime;

    private Integer executeStatus;

    public Integer getExecuteStatus() {
        return executeStatus;
    }

    public void setExecuteStatus(Integer executeStatus) {
        this.executeStatus = executeStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgentOperationTaskId() {
        return agentOperationTaskId;
    }

    public void setAgentOperationTaskId(Long agentOperationTaskId) {
        this.agentOperationTaskId = agentOperationTaskId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getContainer() {
        return container;
    }

    public void setContainer(Integer container) {
        this.container = container;
    }

    public Long getSourceAgentVersionId() {
        return sourceAgentVersionId;
    }

    public void setSourceAgentVersionId(Long sourceAgentVersionId) {
        this.sourceAgentVersionId = sourceAgentVersionId;
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
