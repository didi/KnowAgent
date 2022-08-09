package com.didichuxing.datachannel.agent.node.am.v2;

import com.didichuxing.datachannel.agent.common.api.FileReadType;
import com.didichuxing.datachannel.agent.common.api.TransFormate;

import java.util.HashMap;
import java.util.Map;

public class LogCollectTaskAdvancedConfiguration {

    /**
     * channel 最大内存
     */
    private Long channelMaxBytes = 10 * 1024 * 1024L;
    /**
     * channel 最大缓存数量
     */
    private Integer channelMaxNum = 1000;
    /**
     * 编码类型
     */
    private String  encodeType = "UTF-8";
    /**
     * 附属指标集
     */
    private Map<String, String> otherMetrics = new HashMap<>();
    /**
     * 附属事件集
     */
    private Map<String, String> otherEvents = new HashMap<>();
    /**
     * 连续100行无法解析到日志，则采集结束
     */
    private Integer       maxErrorLineNum    = 100;
    /**
     * 文件最晚修改时间
     */
    private Long          maxModifyTime      = 7 * 24 * 60 * 60 * 1000L;
    /**
     * 最大的同时采集的线程数
     */
    private Integer       maxThreadNum       = 10;
    /**
     * 上线两条日志时间戳相差orderTimeMaxGap，即认为是乱序的日志,乱序阈值
     */
    private Long          orderTimeMaxGap    = 10 * 60 * 1000L;
    /**
     * 文件读取类型，默认多行聚合（java栈）
     */
    private Integer       readFileType = FileReadType.MultiRow.getType();
    /**
     * 日志读取超时时间，单位为ms。即读到文件末尾，等待timeout秒再读一次
     */
    private Long          readTimeOut        = 3000L;
    /**
     * 发送类型，默认异步发送
     */
    private Boolean isAsync                 = true;
    /**
     * 执行flush的批次大小
     */
    private Integer flushBatchSize          = 10;
    /**
     * 执行flush的超时时间
     */
    private Long    flushBatchTimeThreshold = 30000L;
    /**
     * key格式
     */
    private String  keyFormat               = "";
    /**
     * key开始标志
     */
    private String  keyStartFlag            = "";
    /**
     * key开始位置索引
     */
    private int     keyStartFlagIndex       = 0;
    /**
     * 发送kafka最大消息量,默认4M,即超过4/3M就会做截断
     */
    private Long    maxContentSize          = 4 * 1024 * 1024L;
    /**
     * 固定partition key
     */
    private String  regularPartKey          = null;
    /**
     * 发送的批次大小
     */
    private Integer sendBatchSize           = 50;
    /**
     * 发送的超时时间
     */
    private Long    sendBatchTimeThreshold  = 1000L;
    /**
     * 传输格式 0：List<MqLogEvent> 1: MqLogEvent 2: 原始类型（String
     */
    private Integer transFormate            = TransFormate.MQList.getStatus();
    /**
     * sink数量
     */
    private int sinkNum = 1;

    /**
     * 日志模型对应其实限流阈值 单位：byte
     */
    private long startThrehold = 200000;
    /**
     * 日志模型对应最小限流阈值
     */
    private long minThreshold  = 100000;

    public int getSinkNum() {
        return sinkNum;
    }

    public void setSinkNum(int sinkNum) {
        this.sinkNum = sinkNum;
    }

    public Integer getTransFormate() {
        return transFormate;
    }

    public void setTransFormate(Integer transFormate) {
        this.transFormate = transFormate;
    }

    public Long getSendBatchTimeThreshold() {
        return sendBatchTimeThreshold;
    }

    public void setSendBatchTimeThreshold(Long sendBatchTimeThreshold) {
        this.sendBatchTimeThreshold = sendBatchTimeThreshold;
    }

    public Integer getSendBatchSize() {
        return sendBatchSize;
    }

    public void setSendBatchSize(Integer sendBatchSize) {
        this.sendBatchSize = sendBatchSize;
    }

    public String getRegularPartKey() {
        return regularPartKey;
    }

    public long getStartThrehold() {
        return startThrehold;
    }

    public void setStartThrehold(long startThrehold) {
        this.startThrehold = startThrehold;
    }

    public long getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(long minThreshold) {
        this.minThreshold = minThreshold;
    }

    public void setRegularPartKey(String regularPartKey) {
        this.regularPartKey = regularPartKey;
    }

    public Long getMaxContentSize() {
        return maxContentSize;
    }

    public void setMaxContentSize(Long maxContentSize) {
        this.maxContentSize = maxContentSize;
    }

    public int getKeyStartFlagIndex() {
        return keyStartFlagIndex;
    }

    public void setKeyStartFlagIndex(int keyStartFlagIndex) {
        this.keyStartFlagIndex = keyStartFlagIndex;
    }

    public String getKeyStartFlag() {
        return keyStartFlag;
    }

    public void setKeyStartFlag(String keyStartFlag) {
        this.keyStartFlag = keyStartFlag;
    }

    public String getKeyFormat() {
        return keyFormat;
    }

    public void setKeyFormat(String keyFormat) {
        this.keyFormat = keyFormat;
    }

    public Long getFlushBatchTimeThreshold() {
        return flushBatchTimeThreshold;
    }

    public void setFlushBatchTimeThreshold(Long flushBatchTimeThreshold) {
        this.flushBatchTimeThreshold = flushBatchTimeThreshold;
    }

    public Integer getFlushBatchSize() {
        return flushBatchSize;
    }

    public void setFlushBatchSize(Integer flushBatchSize) {
        this.flushBatchSize = flushBatchSize;
    }

    public Boolean getAsync() {
        return isAsync;
    }

    public void setAsync(Boolean async) {
        isAsync = async;
    }

    public Long getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(Long readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public Integer getReadFileType() {
        return readFileType;
    }

    public void setReadFileType(Integer readFileType) {
        this.readFileType = readFileType;
    }

    public Long getOrderTimeMaxGap() {
        return orderTimeMaxGap;
    }

    public void setOrderTimeMaxGap(Long orderTimeMaxGap) {
        this.orderTimeMaxGap = orderTimeMaxGap;
    }

    public Integer getMaxThreadNum() {
        return maxThreadNum;
    }

    public void setMaxThreadNum(Integer maxThreadNum) {
        this.maxThreadNum = maxThreadNum;
    }

    public Long getMaxModifyTime() {
        return maxModifyTime;
    }

    public void setMaxModifyTime(Long maxModifyTime) {
        this.maxModifyTime = maxModifyTime;
    }

    public Integer getMaxErrorLineNum() {
        return maxErrorLineNum;
    }

    public void setMaxErrorLineNum(Integer maxErrorLineNum) {
        this.maxErrorLineNum = maxErrorLineNum;
    }

    public Integer getChannelMaxNum() {
        return channelMaxNum;
    }

    public void setChannelMaxNum(Integer channelMaxNum) {
        this.channelMaxNum = channelMaxNum;
    }

    public Long getChannelMaxBytes() {
        return channelMaxBytes;
    }

    public void setChannelMaxBytes(Long channelMaxBytes) {
        this.channelMaxBytes = channelMaxBytes;
    }

    public String getEncodeType() {
        return encodeType;
    }

    public void setEncodeType(String encodeType) {
        this.encodeType = encodeType;
    }

    public Map<String, String> getOtherMetrics() {
        return otherMetrics;
    }

    public void setOtherMetrics(Map<String, String> otherMetrics) {
        this.otherMetrics = otherMetrics;
    }

    public Map<String, String> getOtherEvents() {
        return otherEvents;
    }

    public void setOtherEvents(Map<String, String> otherEvents) {
        this.otherEvents = otherEvents;
    }

}
