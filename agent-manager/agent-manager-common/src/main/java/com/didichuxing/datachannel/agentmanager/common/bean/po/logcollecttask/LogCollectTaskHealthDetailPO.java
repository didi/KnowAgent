package com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask;

public class LogCollectTaskHealthDetailPO {
    private Long id;

    private Long logCollectTaskId;

    private Long pathId;

    private String hostName;

    private Long collectDqualityTime;

    private Long tooLargeTruncateCheckHealthyHeartbeatTime;

    private Long filePathExistsCheckHealthyHeartbeatTime;

    private Long fileDisorderCheckHealthyHeartbeatTime;

    private Long logSliceCheckHealthyHeartbeatTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLogCollectTaskId() {
        return logCollectTaskId;
    }

    public void setLogCollectTaskId(Long logCollectTaskId) {
        this.logCollectTaskId = logCollectTaskId;
    }

    public Long getPathId() {
        return pathId;
    }

    public void setPathId(Long pathId) {
        this.pathId = pathId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Long getCollectDqualityTime() {
        return collectDqualityTime;
    }

    public void setCollectDqualityTime(Long collectDqualityTime) {
        this.collectDqualityTime = collectDqualityTime;
    }

    public Long getTooLargeTruncateCheckHealthyHeartbeatTime() {
        return tooLargeTruncateCheckHealthyHeartbeatTime;
    }

    public void setTooLargeTruncateCheckHealthyHeartbeatTime(Long tooLargeTruncateCheckHealthyHeartbeatTime) {
        this.tooLargeTruncateCheckHealthyHeartbeatTime = tooLargeTruncateCheckHealthyHeartbeatTime;
    }

    public Long getFilePathExistsCheckHealthyHeartbeatTime() {
        return filePathExistsCheckHealthyHeartbeatTime;
    }

    public void setFilePathExistsCheckHealthyHeartbeatTime(Long filePathExistsCheckHealthyHeartbeatTime) {
        this.filePathExistsCheckHealthyHeartbeatTime = filePathExistsCheckHealthyHeartbeatTime;
    }

    public Long getFileDisorderCheckHealthyHeartbeatTime() {
        return fileDisorderCheckHealthyHeartbeatTime;
    }

    public void setFileDisorderCheckHealthyHeartbeatTime(Long fileDisorderCheckHealthyHeartbeatTime) {
        this.fileDisorderCheckHealthyHeartbeatTime = fileDisorderCheckHealthyHeartbeatTime;
    }

    public Long getLogSliceCheckHealthyHeartbeatTime() {
        return logSliceCheckHealthyHeartbeatTime;
    }

    public void setLogSliceCheckHealthyHeartbeatTime(Long logSliceCheckHealthyHeartbeatTime) {
        this.logSliceCheckHealthyHeartbeatTime = logSliceCheckHealthyHeartbeatTime;
    }
}