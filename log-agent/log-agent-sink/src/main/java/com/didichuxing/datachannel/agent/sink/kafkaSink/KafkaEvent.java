package com.didichuxing.datachannel.agent.sink.kafkaSink;

import com.didichuxing.datachannel.agent.engine.bean.Event;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-11 10:43
 */
public class KafkaEvent extends Event {

    /**
     * 消息生成时间
     */

    private String  timestamp;

    /**
     * 消息收集时间
     */
    private Long    collectTime;

    /**
     * 消息业务时间
     */
    private Long    msgTime;

    /**
     * 消息进度
     */
    private Long    rate;

    /**
     * 消息唯一key
     */
    private String  msgUniqueKey;

    /**
     * 前一条消息的进度
     */
    private Long    preRate;

    /**
     * 模型Id
     */
    private Long    modelId;

    /**
     * 源头单例key
     */
    private String  sourceItemKey;

    /**
     * 源头key
     */
    private String  sourceKey;

    /**
     * 源头单例的header name
     */
    private String  sourceItemHeaderName;

    /**
     * 源头单例name
     */
    private String  sourceItemName;

    /**
     * 源头id
     */
    private Long    sourceId;

    /**
     * 是否需要发送
     */
    private boolean needToSend = true;

    /**
     * bytes 大小
     */
    private int     length     = 0;

    public KafkaEvent() {
        super("", null);
    }

    public KafkaEvent(String content, int length, String timestamp, Long collectTime, Long msgTime,
                      Long rate, String msgUniqueKey, Long preRate, Long modelId, Long sourceId,
                      String sourceItemKey, String sourceKey, String sourceItemHeaderName,
                      String sourceItemName, boolean needToSend) {
        super(content, null);
        this.timestamp = timestamp;
        this.collectTime = collectTime;
        this.msgTime = msgTime;
        this.rate = rate;
        this.msgUniqueKey = msgUniqueKey;
        this.preRate = preRate;
        this.modelId = modelId;
        this.sourceItemKey = sourceItemKey;
        this.sourceKey = sourceKey;
        this.sourceItemName = sourceItemName;
        this.needToSend = needToSend;
        this.sourceId = sourceId;
        this.sourceItemHeaderName = sourceItemHeaderName;
        this.length = length;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Long collectTime) {
        this.collectTime = collectTime;
    }

    public Long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(Long msgTime) {
        this.msgTime = msgTime;
    }

    public Long getRate() {
        return rate;
    }

    public void setRate(Long rate) {
        this.rate = rate;
    }

    public String getMsgUniqueKey() {
        return msgUniqueKey;
    }

    public void setMsgUniqueKey(String msgUniqueKey) {
        this.msgUniqueKey = msgUniqueKey;
    }

    public Long getPreRate() {
        return preRate;
    }

    public void setPreRate(Long preRate) {
        this.preRate = preRate;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getSourceItemKey() {
        return sourceItemKey;
    }

    public void setSourceItemKey(String sourceItemKey) {
        this.sourceItemKey = sourceItemKey;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public String getSourceItemName() {
        return sourceItemName;
    }

    public void setSourceItemName(String sourceItemName) {
        this.sourceItemName = sourceItemName;
    }

    public boolean isNeedToSend() {
        return needToSend;
    }

    public void setNeedToSend(boolean needToSend) {
        this.needToSend = needToSend;
    }

    public String getSourceItemHeaderName() {
        return sourceItemHeaderName;
    }

    public void setSourceItemHeaderName(String sourceItemHeaderName) {
        this.sourceItemHeaderName = sourceItemHeaderName;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    @Override
    public int length() {
        return this.length;
    }

    @Override
    public String toString() {
        return "KafkaEvent{" + "timestamp='" + timestamp + '\'' + ", collectTime=" + collectTime
               + ", msgTime=" + msgTime + ", rate=" + rate + ", msgUniqueKey='" + msgUniqueKey
               + '\'' + ", preRate=" + preRate + ", modelId=" + modelId + ", sourceItemKey='"
               + sourceItemKey + '\'' + ", sourceKey='" + sourceKey + '\''
               + ", sourceItemHeaderName='" + sourceItemHeaderName + '\'' + ", sourceItemName='"
               + sourceItemName + '\'' + ", sourceId=" + sourceId + ", needToSend=" + needToSend
               + ", length=" + length + '}';
    }
}
