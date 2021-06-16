package com.didichuxing.datachannel.swan.agent.sink.hdfsSink;

import com.didichuxing.datachannel.swan.agent.engine.bean.Event;

/**
 * @description: hdfs event
 * @author: huangjw
 * @Date: 2019-07-10 21:47
 */
public class HdfsEvent extends Event {

    /**
     * 传输内容的时间， string形式
     */
    private String  timestamp;

    /**
     * 传输内容的时间， 时间戳形式
     * 若不解析时间戳时，为日志文件的最新修改时间
     */
    private Long    logTime;

    /**
     * source key
     */
    private String  sourceKey;

    /**
     * 传输进度
     */
    private Long    rateOfProgress;

    /**
     * 是否需要发送
     */
    private boolean needToSend = true;

    /**
     * 配置id
     */
    private Long    modelId;

    /**
     * 源头名
     * 对应logEvent的 fileName
     */
    private String  sourceName;

    /**
     * 源头名 别名
     * 对应logEvent的 masterFileName
     */
    private String  anotherSourceName;

    /**
     * 源头id
     */
    private Long    sourceId;

    /**
     * source获取该数据的时间
     */
    private Long    sourceTime;

    /**
     * 消息唯一key
     */
    private String  msgUniqueKey;

    /**
     * 专用于日志场景下，透传
     */
    private Long    rateOfSource;

    public HdfsEvent() {
        super("", null);
    }

    public HdfsEvent(String content, byte[] bytes, String timestamp, Long logTime,
                     String sourceKey, Long rateOfProgress, Long modelId, boolean needToSend,
                     String sourceName, String anotherSourceName, Long sourceId, Long sourceTime,
                     String msgUniqueKey, Long rateOfSource) {
        super(content, bytes);
        this.timestamp = timestamp;
        this.logTime = logTime;
        this.sourceKey = sourceKey;
        this.rateOfProgress = rateOfProgress;
        this.needToSend = needToSend;
        this.modelId = modelId;
        this.sourceName = sourceName;
        this.anotherSourceName = anotherSourceName;

        this.sourceId = sourceId;
        this.sourceTime = sourceTime;
        this.msgUniqueKey = msgUniqueKey;
        this.rateOfSource = rateOfSource;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Long getLogTime() {
        return logTime;
    }

    public void setLogTime(Long logTime) {
        this.logTime = logTime;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public Long getRateOfProgress() {
        return rateOfProgress;
    }

    public void setRateOfProgress(Long rateOfProgress) {
        this.rateOfProgress = rateOfProgress;
    }

    public boolean isNeedToSend() {
        return needToSend;
    }

    public void setNeedToSend(boolean needToSend) {
        this.needToSend = needToSend;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getAnotherSourceName() {
        return anotherSourceName;
    }

    public void setAnotherSourceName(String anotherSourceName) {
        this.anotherSourceName = anotherSourceName;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getSourceTime() {
        return sourceTime;
    }

    public void setSourceTime(Long sourceTime) {
        this.sourceTime = sourceTime;
    }

    public String getMsgUniqueKey() {
        return msgUniqueKey;
    }

    public void setMsgUniqueKey(String msgUniqueKey) {
        this.msgUniqueKey = msgUniqueKey;
    }

    public Long getRateOfSource() {
        return rateOfSource;
    }

    public void setRateOfSource(Long rateOfSource) {
        this.rateOfSource = rateOfSource;
    }

    @Override
    public String toString() {
        return "HdfsEvent{" + "timestamp='" + timestamp + '\'' + ", logTime=" + logTime
               + ", sourceKey='" + sourceKey + '\'' + ", rateOfProgress=" + rateOfProgress
               + ", needToSend=" + needToSend + ", modelId=" + modelId + ", sourceName='"
               + sourceName + '\'' + ", anotherSourceName='" + anotherSourceName + '\'' + '}';
    }
}
