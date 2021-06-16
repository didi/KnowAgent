package com.didichuxing.datachannel.agentmanager.common.bean.po.agent;

import com.didichuxing.datachannel.agentmanager.common.bean.po.BasePO;

import java.util.Date;

public class AgentMetricPO extends BasePO {
    private Long id;

    private Double cpuUsage;

    private Long heartbeatTime;

    private String hostIp;

    private Double cpuLimit;

    private Integer gcCount;

    private Integer pathId;

    private Integer logModeId;

    private String hostname;

    private Integer fdCount;

    private Integer limitTps;

    private Date startTime;

    private Integer logPathKey;

    private String messageVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Long getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(Long heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public Double getCpuLimit() {
        return cpuLimit;
    }

    public void setCpuLimit(Double cpuLimit) {
        this.cpuLimit = cpuLimit;
    }

    public Integer getGcCount() {
        return gcCount;
    }

    public void setGcCount(Integer gcCount) {
        this.gcCount = gcCount;
    }

    public Integer getPathId() {
        return pathId;
    }

    public void setPathId(Integer pathId) {
        this.pathId = pathId;
    }

    public Integer getLogModeId() {
        return logModeId;
    }

    public void setLogModeId(Integer logModeId) {
        this.logModeId = logModeId;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getFdCount() {
        return fdCount;
    }

    public void setFdCount(Integer fdCount) {
        this.fdCount = fdCount;
    }

    public Integer getLimitTps() {
        return limitTps;
    }

    public void setLimitTps(Integer limitTps) {
        this.limitTps = limitTps;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getLogPathKey() {
        return logPathKey;
    }

    public void setLogPathKey(Integer logPathKey) {
        this.logPathKey = logPathKey;
    }

    public String getMessageVersion() {
        return messageVersion;
    }

    public void setMessageVersion(String messageVersion) {
        this.messageVersion = messageVersion;
    }
}