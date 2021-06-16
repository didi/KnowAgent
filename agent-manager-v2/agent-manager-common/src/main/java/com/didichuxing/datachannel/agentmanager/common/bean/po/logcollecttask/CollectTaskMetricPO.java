package com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask;

import com.didichuxing.datachannel.agentmanager.common.bean.po.BasePO;

import java.util.Date;

public class CollectTaskMetricPO extends BasePO {
    private Long id;

    private Integer readTimeMean;

    private Integer filterRemained;

    private String channelCapacity;

    private Boolean isFileExist;

    private Long pathId;

    private String type;

    private Integer readCount;

    private Integer sendTimeMean;

    private String masterFile;

    private String path;

    private String hostname;

    private Long heartbeatTime;

    private String hostIp;

    private Integer sinkNum;

    private Integer flushTimeMean;

    private String latestFile;

    private Integer filterTooLargeCount;

    private String channelType;

    private Integer logModelVersion;

    private String topic;

    private Integer flushCount;

    private Integer flushTimeMax;

    private Integer filterOut;

    private Integer relatedFiles;

    private String logModelHostName;

    private Long clusterId;

    private Integer limitRate;

    private Integer controlTimeMean;

    private Integer limitTime;

    private Long logModeId;

    private Integer flushTimeMin;

    private Integer readTimeMin;

    private Integer sendTimeMax;

    private Integer dynamicLimiter;

    private String logPathKey;

    private Integer maxTimeGap;

    private Integer sendByte;

    private Integer sendTimeMin;

    private String logTimeStr;

    private Integer controlTimeMax;

    private Integer sendCount;

    private String sourceType;

    private Date logTime;

    private Integer flushFailedCount;

    private Integer channelSize;

    private Integer filterTotalTooLargeCount;

    private String collectFiles;

    private Integer controlTimeMin;

    private Integer readByte;

    private Integer readTimeMax;

    private Boolean validTimeConfig;

    private Boolean isFileDisorder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getReadTimeMean() {
        return readTimeMean;
    }

    public void setReadTimeMean(Integer readTimeMean) {
        this.readTimeMean = readTimeMean;
    }

    public Integer getFilterRemained() {
        return filterRemained;
    }

    public void setFilterRemained(Integer filterRemained) {
        this.filterRemained = filterRemained;
    }

    public String getChannelCapacity() {
        return channelCapacity;
    }

    public void setChannelCapacity(String channelCapacity) {
        this.channelCapacity = channelCapacity;
    }

    public Boolean getIsFileExist() {
        return isFileExist;
    }

    public void setIsFileExist(Boolean isFileExist) {
        this.isFileExist = isFileExist;
    }

    public Long getPathId() {
        return pathId;
    }

    public void setPathId(Long pathId) {
        this.pathId = pathId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public Integer getSendTimeMean() {
        return sendTimeMean;
    }

    public void setSendTimeMean(Integer sendTimeMean) {
        this.sendTimeMean = sendTimeMean;
    }

    public String getMasterFile() {
        return masterFile;
    }

    public void setMasterFile(String masterFile) {
        this.masterFile = masterFile;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
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

    public Integer getSinkNum() {
        return sinkNum;
    }

    public void setSinkNum(Integer sinkNum) {
        this.sinkNum = sinkNum;
    }

    public Integer getFlushTimeMean() {
        return flushTimeMean;
    }

    public void setFlushTimeMean(Integer flushTimeMean) {
        this.flushTimeMean = flushTimeMean;
    }

    public String getLatestFile() {
        return latestFile;
    }

    public void setLatestFile(String latestFile) {
        this.latestFile = latestFile;
    }

    public Integer getFilterTooLargeCount() {
        return filterTooLargeCount;
    }

    public void setFilterTooLargeCount(Integer filterTooLargeCount) {
        this.filterTooLargeCount = filterTooLargeCount;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public Integer getLogModelVersion() {
        return logModelVersion;
    }

    public void setLogModelVersion(Integer logModelVersion) {
        this.logModelVersion = logModelVersion;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getFlushCount() {
        return flushCount;
    }

    public void setFlushCount(Integer flushCount) {
        this.flushCount = flushCount;
    }

    public Integer getFlushTimeMax() {
        return flushTimeMax;
    }

    public void setFlushTimeMax(Integer flushTimeMax) {
        this.flushTimeMax = flushTimeMax;
    }

    public Integer getFilterOut() {
        return filterOut;
    }

    public void setFilterOut(Integer filterOut) {
        this.filterOut = filterOut;
    }

    public Integer getRelatedFiles() {
        return relatedFiles;
    }

    public void setRelatedFiles(Integer relatedFiles) {
        this.relatedFiles = relatedFiles;
    }

    public String getLogModelHostName() {
        return logModelHostName;
    }

    public void setLogModelHostName(String logModelHostName) {
        this.logModelHostName = logModelHostName;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getLimitRate() {
        return limitRate;
    }

    public void setLimitRate(Integer limitRate) {
        this.limitRate = limitRate;
    }

    public Integer getControlTimeMean() {
        return controlTimeMean;
    }

    public void setControlTimeMean(Integer controlTimeMean) {
        this.controlTimeMean = controlTimeMean;
    }

    public Integer getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(Integer limitTime) {
        this.limitTime = limitTime;
    }

    public Long getLogModeId() {
        return logModeId;
    }

    public void setLogModeId(Long logModeId) {
        this.logModeId = logModeId;
    }

    public Integer getFlushTimeMin() {
        return flushTimeMin;
    }

    public void setFlushTimeMin(Integer flushTimeMin) {
        this.flushTimeMin = flushTimeMin;
    }

    public Integer getReadTimeMin() {
        return readTimeMin;
    }

    public void setReadTimeMin(Integer readTimeMin) {
        this.readTimeMin = readTimeMin;
    }

    public Integer getSendTimeMax() {
        return sendTimeMax;
    }

    public void setSendTimeMax(Integer sendTimeMax) {
        this.sendTimeMax = sendTimeMax;
    }

    public Integer getDynamicLimiter() {
        return dynamicLimiter;
    }

    public void setDynamicLimiter(Integer dynamicLimiter) {
        this.dynamicLimiter = dynamicLimiter;
    }

    public String getLogPathKey() {
        return logPathKey;
    }

    public void setLogPathKey(String logPathKey) {
        this.logPathKey = logPathKey;
    }

    public Integer getMaxTimeGap() {
        return maxTimeGap;
    }

    public void setMaxTimeGap(Integer maxTimeGap) {
        this.maxTimeGap = maxTimeGap;
    }

    public Integer getSendByte() {
        return sendByte;
    }

    public void setSendByte(Integer sendByte) {
        this.sendByte = sendByte;
    }

    public Integer getSendTimeMin() {
        return sendTimeMin;
    }

    public void setSendTimeMin(Integer sendTimeMin) {
        this.sendTimeMin = sendTimeMin;
    }

    public String getLogTimeStr() {
        return logTimeStr;
    }

    public void setLogTimeStr(String logTimeStr) {
        this.logTimeStr = logTimeStr;
    }

    public Integer getControlTimeMax() {
        return controlTimeMax;
    }

    public void setControlTimeMax(Integer controlTimeMax) {
        this.controlTimeMax = controlTimeMax;
    }

    public Integer getSendCount() {
        return sendCount;
    }

    public void setSendCount(Integer sendCount) {
        this.sendCount = sendCount;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    public Integer getFlushFailedCount() {
        return flushFailedCount;
    }

    public void setFlushFailedCount(Integer flushFailedCount) {
        this.flushFailedCount = flushFailedCount;
    }

    public Integer getChannelSize() {
        return channelSize;
    }

    public void setChannelSize(Integer channelSize) {
        this.channelSize = channelSize;
    }

    public Integer getFilterTotalTooLargeCount() {
        return filterTotalTooLargeCount;
    }

    public void setFilterTotalTooLargeCount(Integer filterTotalTooLargeCount) {
        this.filterTotalTooLargeCount = filterTotalTooLargeCount;
    }

    public String getCollectFiles() {
        return collectFiles;
    }

    public void setCollectFiles(String collectFiles) {
        this.collectFiles = collectFiles;
    }

    public Integer getControlTimeMin() {
        return controlTimeMin;
    }

    public void setControlTimeMin(Integer controlTimeMin) {
        this.controlTimeMin = controlTimeMin;
    }

    public Integer getReadByte() {
        return readByte;
    }

    public void setReadByte(Integer readByte) {
        this.readByte = readByte;
    }

    public Integer getReadTimeMax() {
        return readTimeMax;
    }

    public void setReadTimeMax(Integer readTimeMax) {
        this.readTimeMax = readTimeMax;
    }

    public Boolean getValidTimeConfig() {
        return validTimeConfig;
    }

    public void setValidTimeConfig(Boolean validTimeConfig) {
        this.validTimeConfig = validTimeConfig;
    }

    public Boolean getIsFileDisorder() {
        return isFileDisorder;
    }

    public void setIsFileDisorder(Boolean fileDisorder) {
        isFileDisorder = fileDisorder;
    }
}