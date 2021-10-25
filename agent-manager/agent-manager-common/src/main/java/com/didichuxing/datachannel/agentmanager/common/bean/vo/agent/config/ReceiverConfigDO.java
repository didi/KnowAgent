package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

/**
 * Kafka生产者相关配置项
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiverConfigDO {

    @ApiModelProperty(value = "接收端对象id")
    private Long receiverId;

    @ApiModelProperty(value = "kafka集群nameServer配置")
    private String nameServer;

    @ApiModelProperty(value = "topic名")
    private String topic;

    @ApiModelProperty(value = "kafka集群对应生产端初始化配置")
    private String properties;

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getNameServer() {
        return nameServer;
    }

    public void setNameServer(String nameServer) {
        this.nameServer = nameServer;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

}
