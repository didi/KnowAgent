package com.didichuxing.datachannel.agent.sink.kafkaSink;

import com.didichuxing.datachannel.agent.common.configs.v2.component.targetConfig.TargetConfig;
import com.didichuxing.datachannel.agent.common.constants.Tags;

/**
 * @description:
 * @author: huangjw
 * @Date: 19/7/1 16:17
 */
public class KafkaTargetConfig extends TargetConfig implements Cloneable {

    /**
     * 发送kafka最大消息量,默认4M,即超过4/3M就会做截断
     */
    private Long    maxContentSize          = 4 * 1024 * 1024L;

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
     * 发送的批次大小
     */
    private Integer sendBatchSize           = 50;

    /**
     * 发送的超时时间
     */
    private Long    sendBatchTimeThreshold  = 1000L;

    /**
     * key格式
     */
    private String  keyFormat               = "";
    /**
     * key开始标志
     */
    private String  keyStartFlag            = "";

    /**
     * 传输格式 0：List<MqLogEvent> 1: MqLogEvent 2: 原始类型（String
     */
    private Integer transFormate            = 0;

    /**
     * key开始位置索引
     */
    private int     keyStartFlagIndex       = 0;

    /**
     * 发送topic
     */
    private String  topic;

    /**
     * 固定partition key
     */
    private String  regularPartKey          = null;

    /**
     * 过滤字符串，以“，”分隔，可以有多个
     */
    private String  filterRule;

    /**
     * 过滤类型，0表示包含，1表示不包含，只有当filterRule字符不为空时才起作用
     */
    private Integer filterOprType           = 0;

    /**
     * 集群id
     */
    private Integer clusterId;

    /**
     * kafka集群地址
     */
    private String  bootstrap;

    /**
     * 发送配置
     */
    private String  properties;

    public KafkaTargetConfig() {
        super(Tags.TARGET_KAFKA);
    }

    @Override
    public KafkaTargetConfig clone() {
        KafkaTargetConfig config = null;
        try {
            config = (KafkaTargetConfig) super.clone();
        } catch (CloneNotSupportedException e) {

        }
        return config;
    }

    public Long getMaxContentSize() {
        return maxContentSize;
    }

    public void setMaxContentSize(Long maxContentSize) {
        this.maxContentSize = maxContentSize;
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

    public Long getFlushBatchTimeThreshold() {
        return flushBatchTimeThreshold;
    }

    public void setFlushBatchTimeThreshold(Long flushBatchTimeThreshold) {
        this.flushBatchTimeThreshold = flushBatchTimeThreshold;
    }

    public Integer getSendBatchSize() {
        return sendBatchSize;
    }

    public void setSendBatchSize(Integer sendBatchSize) {
        this.sendBatchSize = sendBatchSize;
    }

    public Long getSendBatchTimeThreshold() {
        return sendBatchTimeThreshold;
    }

    public void setSendBatchTimeThreshold(Long sendBatchTimeThreshold) {
        this.sendBatchTimeThreshold = sendBatchTimeThreshold;
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

    public String getRegularPartKey() {
        return regularPartKey;
    }

    public void setRegularPartKey(String regularPartKey) {
        this.regularPartKey = regularPartKey;
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

    public String getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(String bootstrap) {
        this.bootstrap = bootstrap;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    @Override
    public String toString() {
        return "KafkaTargetConfig{" + "maxContentSize=" + maxContentSize + ", isAsync=" + isAsync
               + ", flushBatchSize=" + flushBatchSize + ", flushBatchTimeThreshold="
               + flushBatchTimeThreshold + ", sendBatchSize=" + sendBatchSize
               + ", sendBatchTimeThreshold=" + sendBatchTimeThreshold + ", keyFormat='" + keyFormat
               + '\'' + ", keyStartFlag='" + keyStartFlag + '\'' + ", transFormate=" + transFormate
               + ", keyStartFlagIndex=" + keyStartFlagIndex + ", topic='" + topic + '\''
               + ", regularPartKey='" + regularPartKey + '\'' + ", filterRule='" + filterRule
               + '\'' + ", filterOprType=" + filterOprType + ", clusterId=" + clusterId
               + ", bootstrap='" + bootstrap + '\'' + ", properties='" + properties + '\'' + '}';
    }
}
