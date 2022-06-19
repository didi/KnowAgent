package com.didichuxing.datachannel.agent.common.configs.v1.collector;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 基本配置
 * @author: huangjw
 * @Date: 18/6/20 12:04
 */
public class CommonConfig implements Cloneable {

    /** 日志模型ID */
    private Integer             logModelId;

    /** 日志模型名称 */
    private String              logModelName;

    /**
     * 日志采集类型,1、周期性采集 2、定时采集
     */
    private Integer             collectType             = 1;
    /**
     * 编码类型
     */
    private String              encodeType              = "UTF-8";

    /** 日志内容中时间的格式 */
    private String              timeFormat              = "yyyy-MM-dd HH:mm:ss";

    /** 日志内容开始标示 */
    private String              timeStartFlag           = "";

    /** 第几个开始标示 */
    private int                 timeStartFlagIndex      = 0;
    /**
     * 日志采集开始时刻
     */
    private Date                startTime;
    /**
     * 日志采集停止时刻
     */
    private Date                endTime;

    /**
     * 日志模型版本号
     */
    private Integer             version;

    /**
     * 发送kafka最大消息量,默认4M,即超过4/3M就会做截断
     */
    private Long                maxContentSize          = 4 * 1024 * 1024L;

    /**
     * 上线两条日志时间戳相差orderTimeMaxGap，即认为是乱序的日志,乱序阈值
     */
    private Long                orderTimeMaxGap         = 10 * 60 * 1000L;

    /**
     * mqEvent新增内容
     */
    private Map<String, Object> eventAddition           = null;

    /**
     * 是否顺序采集.只能保证2个文件之间的顺序，若出现延迟，则无法保证
     */
    private Boolean             sequentialCollect       = false;

    /**
     * 优先级
     */
    private Integer             priority                = 0;

    /**
     * 开始采集位置
     */
    private Integer             collcetLocation         = 0;

    /**
     * 过滤字符串，以“，”分隔，可以有多个
     */
    private String              filterRule;

    /**
     * 过滤类型，0表示包含，1表示不包含，只有当filterRule字符不为空时才起作用
     */
    private Integer             filterOprType;

    /**
     * 是否是顺序文件
     */
    private Integer             isOrderFile             = 0;

    /*
     * 是否停止
     */
    private boolean             isStop                  = false;

    /**
     * 有效的最新文件，最新的前2个文件，一直处于等待状态，不close
     */
    private Integer             vaildLatestFiles        = 2;

    /**
     * 校验是否可关闭的时间间隔
     */
    private Long                waitCheckTime           = 5 * 60 * 1000L;

    /**
     * 最大的同时采集的线程数
     */
    private Integer             maxThreadNum            = 10;

    /**
     * 发送类型，默认异步发送
     */
    private Boolean             isAsync                 = true;

    /**
     * 执行flush的批次大小
     */
    private Integer             flushBatchSize          = 10;

    /**
     * 执行flush的超时时间
     */
    private Long                flushBatchTimeThreshold = 30000L;

    /**
     * 发送的批次大小
     */
    private Integer             sendBatchSize;

    /**
     * 发送的超时时间
     */
    private Long                sendBatchTimeThreshold  = 1000L;

    /**
     * key格式
     */
    private String              keyFormat               = "";
    /**
     * key开始标志
     */
    private String              keyStartFlag            = "";

    /**
     * 传输格式 0：List<MqLogEvent> 1: MqLogEvent 2: 原始类型（String
     */
    private Integer             transFormate            = 0;

    /**
     * key开始位置索引
     */
    private int                 keyStartFlagIndex       = 0;

    /**
     * 发送topic
     */
    private String              topic;

    /**
     * 连续100行无法解析到日志，则采集结束
     */
    private Integer             maxErrorLineNum         = 100;

    /**
     * 是否是有效的时间戳配置
     */
    private Boolean             isVaildTimeConfig       = true;

    /**
     * 文件读取类型，默认多行聚合（java栈）
     */
    private Integer             readFileType            = 0;

    /**
     * 文件最晚修改时间
     */
    private Long                maxModifyTime           = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 固定partition key
     */
    private String              regularPartKey          = null;

    /**
     * 日志读取超时时间，单位为ms。即读到文件末尾，等待timeout秒再读一次
     */
    private Long                readTimeOut             = 3000L;

    public Integer getLogModelId() {
        return logModelId;
    }

    public void setLogModelId(Integer logModelId) {
        this.logModelId = logModelId;
    }

    public String getLogModelName() {
        return logModelName;
    }

    public void setLogModelName(String logModelName) {
        this.logModelName = logModelName;
    }

    public Integer getCollectType() {
        return collectType;
    }

    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }

    public String getEncodeType() {
        return encodeType;
    }

    public void setEncodeType(String encodeType) {
        this.encodeType = encodeType;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getTimeStartFlag() {
        return timeStartFlag;
    }

    public void setTimeStartFlag(String timeStartFlag) {
        this.timeStartFlag = timeStartFlag;
    }

    public int getTimeStartFlagIndex() {
        return timeStartFlagIndex;
    }

    public void setTimeStartFlagIndex(int timeStartFlagIndex) {
        this.timeStartFlagIndex = timeStartFlagIndex;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getMaxContentSize() {
        return maxContentSize;
    }

    public void setMaxContentSize(Long maxContentSize) {
        this.maxContentSize = maxContentSize;
    }

    public Long getOrderTimeMaxGap() {
        return orderTimeMaxGap;
    }

    public void setOrderTimeMaxGap(Long orderTimeMaxGap) {
        this.orderTimeMaxGap = orderTimeMaxGap;
    }

    public Map<String, Object> getEventAddition() {
        return eventAddition;
    }

    public void setEventAddition(Map<String, Object> eventAddition) {
        this.eventAddition = eventAddition;
    }

    public Boolean getSequentialCollect() {
        return sequentialCollect;
    }

    public void setSequentialCollect(Boolean sequentialCollect) {
        this.sequentialCollect = sequentialCollect;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getCollcetLocation() {
        return collcetLocation;
    }

    public void setCollcetLocation(Integer collcetLocation) {
        this.collcetLocation = collcetLocation;
    }

    public String getFilterRule() {
        return filterRule;
    }

    public void setFilterRule(String filterRule) {
        this.filterRule = filterRule;
    }

    public Integer getFilterOprType() {
        return filterOprType;
    }

    public void setFilterOprType(Integer filterOprType) {
        this.filterOprType = filterOprType;
    }

    public Integer getIsOrderFile() {
        return isOrderFile;
    }

    public void setIsOrderFile(Integer isOrderFile) {
        this.isOrderFile = isOrderFile;
    }

    public void setStop(boolean isStop) {
        this.isStop = isStop;
    }

    public boolean isStop() {
        return isStop;
    }

    public Integer getVaildLatestFiles() {
        return vaildLatestFiles;
    }

    public void setVaildLatestFiles(Integer vaildLatestFiles) {
        this.vaildLatestFiles = vaildLatestFiles;
    }

    public Long getWaitCheckTime() {
        return waitCheckTime;
    }

    public void setWaitCheckTime(Long waitCheckTime) {
        this.waitCheckTime = waitCheckTime;
    }

    public Integer getMaxThreadNum() {
        return maxThreadNum;
    }

    public void setMaxThreadNum(Integer maxThreadNum) {
        this.maxThreadNum = maxThreadNum;
    }

    public Boolean getAsync() {
        return isAsync;
    }

    public void setAsync(Boolean async) {
        isAsync = async;
    }

    public Integer getFlushBatchSize() {
        return flushBatchSize;
    }

    public void setFlushBatchSize(Integer flushBatchSize) {
        this.flushBatchSize = flushBatchSize;
    }

    public Integer getSendBatchSize() {
        return sendBatchSize;
    }

    public void setSendBatchSize(Integer sendBatchSize) {
        this.sendBatchSize = sendBatchSize;
    }

    public String getKeyFormat() {
        return keyFormat;
    }

    public void setKeyFormat(String keyFormat) {
        this.keyFormat = keyFormat;
    }

    public String getKeyStartFlag() {
        return keyStartFlag;
    }

    public void setKeyStartFlag(String keyStartFlag) {
        this.keyStartFlag = keyStartFlag;
    }

    public Integer getTransFormate() {
        return transFormate;
    }

    public void setTransFormate(Integer transFormate) {
        this.transFormate = transFormate;
    }

    public int getKeyStartFlagIndex() {
        return keyStartFlagIndex;
    }

    public void setKeyStartFlagIndex(int keyStartFlagIndex) {
        this.keyStartFlagIndex = keyStartFlagIndex;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getMaxErrorLineNum() {
        return maxErrorLineNum;
    }

    public void setMaxErrorLineNum(Integer maxErrorLineNum) {
        this.maxErrorLineNum = maxErrorLineNum;
    }

    public Boolean getVaildTimeConfig() {
        return isVaildTimeConfig;
    }

    public void setVaildTimeConfig(Boolean vaildTimeConfig) {
        isVaildTimeConfig = vaildTimeConfig;
    }

    public Integer getReadFileType() {
        return readFileType;
    }

    public void setReadFileType(Integer readFileType) {
        this.readFileType = readFileType;
    }

    public Long getMaxModifyTime() {
        return maxModifyTime;
    }

    public void setMaxModifyTime(Long maxModifyTime) {
        this.maxModifyTime = maxModifyTime;
    }

    public String getRegularPartKey() {
        return regularPartKey;
    }

    public void setRegularPartKey(String regularPartKey) {
        this.regularPartKey = regularPartKey;
    }

    public Long getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(Long readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    @Override
    public CommonConfig clone() {
        CommonConfig commonConfig = null;
        try {
            commonConfig = (CommonConfig) super.clone();
            Map<String, Object> newEventAddition = new HashMap<>();
            commonConfig.setEventAddition(newEventAddition);
            if (this.eventAddition != null) {
                for (Map.Entry<String, Object> entry : this.eventAddition.entrySet()) {
                    newEventAddition.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (CloneNotSupportedException e) {

        }
        return commonConfig;
    }

    public Long getFlushBatchTimeThreshold() {
        return flushBatchTimeThreshold;
    }

    public void setFlushBatchTimeThreshold(Long flushBatchTimeThreshold) {
        this.flushBatchTimeThreshold = flushBatchTimeThreshold;
    }

    public Long getSendBatchTimeThreshold() {
        return sendBatchTimeThreshold;
    }

    public void setSendBatchTimeThreshold(Long sendBatchTimeThreshold) {
        this.sendBatchTimeThreshold = sendBatchTimeThreshold;
    }

    @Override
    public String toString() {
        return "CommonConfig{" + "logModelId=" + logModelId + ", logModelName='" + logModelName
               + '\'' + ", collectType=" + collectType + ", encodeType='" + encodeType + '\''
               + ", timeFormat='" + timeFormat + '\'' + ", timeStartFlag='" + timeStartFlag + '\''
               + ", timeStartFlagIndex=" + timeStartFlagIndex + ", startTime=" + startTime
               + ", endTime=" + endTime + ", version=" + version + ", maxContentSize="
               + maxContentSize + ", orderTimeMaxGap=" + orderTimeMaxGap + ", eventAddition="
               + eventAddition + ", sequentialCollect=" + sequentialCollect + ", priority="
               + priority + ", collcetLocation=" + collcetLocation + ", filterRule='" + filterRule
               + '\'' + ", filterOprType=" + filterOprType + ", isOrderFile=" + isOrderFile
               + ", isStop=" + isStop + ", vaildLatestFiles=" + vaildLatestFiles
               + ", waitCheckTime=" + waitCheckTime + ", maxThreadNum=" + maxThreadNum
               + ", isAsync=" + isAsync + ", flushBatchSize=" + flushBatchSize
               + ", flushBatchTimeThreshold=" + flushBatchTimeThreshold + ", sendBatchSize="
               + sendBatchSize + ", sendBatchTimeThreshold=" + sendBatchTimeThreshold
               + ", keyFormat='" + keyFormat + '\'' + ", keyStartFlag='" + keyStartFlag + '\''
               + ", transFormate=" + transFormate + ", keyStartFlagIndex=" + keyStartFlagIndex
               + ", topic='" + topic + '\'' + ", maxErrorLineNum=" + maxErrorLineNum
               + ", isVaildTimeConfig=" + isVaildTimeConfig + ", readFileType=" + readFileType
               + ", maxModifyTime=" + maxModifyTime + ", regularPartKey='" + regularPartKey + '\''
               + ", readTimeOut=" + readTimeOut + '}';
    }
}
