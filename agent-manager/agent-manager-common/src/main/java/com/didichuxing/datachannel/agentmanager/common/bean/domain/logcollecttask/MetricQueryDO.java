package com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask;

public class MetricQueryDO {
    private Long taskId;

    private Long logCollectPathId;

    private String hostName;

    private Long startTime;

    private Long endTime;

    private Boolean eachHost;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getLogCollectPathId() {
        return logCollectPathId;
    }

    public void setLogCollectPathId(Long logCollectPathId) {
        this.logCollectPathId = logCollectPathId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Boolean getEachHost() {
        return eachHost;
    }

    public void setEachHost(Boolean eachHost) {
        this.eachHost = eachHost;
    }
}
