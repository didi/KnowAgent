package com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "采集任务指标", description = "")
public class CollectTaskMetricVO {
    private Long id;

    @ApiModelProperty(value = "平均读取时间ns")
    private Integer readTimeMean;

    @ApiModelProperty(value = "日志过滤剩余数")
    private Integer filterRemained;

    @ApiModelProperty(value = "channel容量")
    private String channelCapacity;

    @ApiModelProperty(value = "文件是否存在")
    private Boolean isFileExist;

    @ApiModelProperty(value = "采集路径id")
    private Long pathId;

    @ApiModelProperty(value = "消息发送类型，例如kafka")
    private String type;

    @ApiModelProperty(value = "读取数")
    private Integer readCount;

    @ApiModelProperty(value = "平均发送时间ns")
    private Integer sendTimeMean;

    @ApiModelProperty(value = "主文件名")
    private String masterFile;

    @ApiModelProperty(value = "文件路径")
    private String path;

    @ApiModelProperty(value = "agent宿主机名")
    private String hostname;

    @ApiModelProperty(value = "心跳时间")
    private Long heartbeatTime;

    @ApiModelProperty(value = "宿主机ip")
    private String hostIp;

    @ApiModelProperty(value = "清洗数量")
    private Integer sinkNum;

    @ApiModelProperty(value = "平均flush时间ns")
    private Integer flushTimeMean;

    @ApiModelProperty(value = "最晚采集日志文件名")
    private String latestFile;

    @ApiModelProperty(value = "日志过大截断数")
    private Integer filterTooLargeCount;

    @ApiModelProperty(value = "channel类型")
    @JSONField(name = "channel.type")
    private String channelType;

    @ApiModelProperty(value = "log model版本")
    private Integer logModelVersion;

    @ApiModelProperty(value = "topic名称")
    private String topic;

    @ApiModelProperty(value = "flush次数")
    private Integer flushCount;

    @ApiModelProperty(value = "最大flush时间ns")
    private Integer flushTimeMax;

    @ApiModelProperty(value = "过滤条数")
    private Integer filterOut;

    @ApiModelProperty(value = "关联文件数")
    private Integer relatedFiles;

    @ApiModelProperty(value = "log model所在的主机名（采集容器则为容器名，采集主机则为主机名）")
    private String logModelHostName;

    @ApiModelProperty(value = "cluster id")
    private Long clusterId;

    @ApiModelProperty(value = "cpu限流时长")
    private Integer limitRate;

    @ApiModelProperty(value = "平均control时间")
    private Long controlTimeMean;

    @ApiModelProperty(value = "限流时长")
    private Integer limitTime;

    @ApiModelProperty(value = "log model id（采集任务id）")
    private Long logModeId;

    @ApiModelProperty(value = "平均flush时间")
    private Integer flushTimeMin;

    @ApiModelProperty(value = "最小读取时间")
    private Integer readTimeMin;

    @ApiModelProperty(value = "最大发送时间")
    private Integer sendTimeMax;

    @ApiModelProperty(value = "")
    private Integer dynamicLimiter;

    @ApiModelProperty(value = "采集路径唯一key")
    private String logPathKey;

    @ApiModelProperty(value = "日志最大时间间隔")
    private Integer maxTimeGap;

    @ApiModelProperty(value = "发送byte数")
    private Integer sendByte;

    @ApiModelProperty(value = "最小发送时间")
    private Integer sendTimeMin;

    @ApiModelProperty(value = "日志业务时间（格式化）")
    private String logTimeStr;

    @ApiModelProperty(value = "最大control时间")
    private Long controlTimeMax;

    @ApiModelProperty(value = "发送日志数量")
    private Integer sendCount;

    @ApiModelProperty(value = "消息源类型")
    @JSONField(name = "source.type")
    private String sourceType;

    @ApiModelProperty(value = "日志业务时间戳")
    private Long logTime;

    @ApiModelProperty(value = "flush失败次数")
    private Integer flushFailedCount;

    @ApiModelProperty(value = "channel大小")
    private Integer channelSize;

    @ApiModelProperty(value = "超长截断总数")
    private Integer filterTotalTooLargeCount;

    @ApiModelProperty(value = "正在采集的文件列表")
    private List<CollectFileVO> collectFiles;

    @ApiModelProperty(value = "最小control时间")
    private Long controlTimeMin;

    @ApiModelProperty(value = "读取byte数")
    private Integer readByte;

    @ApiModelProperty(value = "最大读取时间")
    private Integer readTimeMax;

    @ApiModelProperty(value = "*无效字段* 时间格式是否合法")
    private Boolean validTimeConfig;

    @ApiModelProperty(value = "任务状态 0停止 1运行中 2完成")
    private Integer taskStatus;

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

    public Boolean getFileExist() {
        return isFileExist;
    }

    public void setFileExist(Boolean fileExist) {
        isFileExist = fileExist;
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

    public Long getControlTimeMean() {
        return controlTimeMean;
    }

    public void setControlTimeMean(Long controlTimeMean) {
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

    public Long getControlTimeMax() {
        return controlTimeMax;
    }

    public void setControlTimeMax(Long controlTimeMax) {
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

    public Long getLogTime() {
        return logTime;
    }

    public void setLogTime(Long logTime) {
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

    public List<CollectFileVO> getCollectFiles() {
        return collectFiles;
    }

    public void setCollectFiles(List<CollectFileVO> collectFiles) {
        this.collectFiles = collectFiles;
    }

    public Long getControlTimeMin() {
        return controlTimeMin;
    }

    public void setControlTimeMin(Long controlTimeMin) {
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

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }
}
