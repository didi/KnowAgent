package com.didichuxing.datachannel.agentmanager.common.bean.vo.receiver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiverVO {

    @ApiModelProperty(value = "接收端对象id")
    private Long id;

    @ApiModelProperty(value = "kafka集群名")
    private String kafkaClusterName;

    @ApiModelProperty(value = "kafka集群broker配置")
    private String kafkaClusterBrokerConfiguration;

    @ApiModelProperty(value = "kafka集群对应生产端初始化配置")
    private String kafkaClusterProducerInitConfiguration;

    @ApiModelProperty(value = "接收端创建时间", notes="")
    private Long createTime;

    public void setId(Long id) {
        this.id = id;
    }

    public void setKafkaClusterName(String kafkaClusterName) {
        this.kafkaClusterName = kafkaClusterName;
    }

    public void setKafkaClusterBrokerConfiguration(String kafkaClusterBrokerConfiguration) {
        this.kafkaClusterBrokerConfiguration = kafkaClusterBrokerConfiguration;
    }

    public void setKafkaClusterProducerInitConfiguration(String kafkaClusterProducerInitConfiguration) {
        this.kafkaClusterProducerInitConfiguration = kafkaClusterProducerInitConfiguration;
    }

    public Long getId() {
        return id;
    }

    public String getKafkaClusterName() {
        return kafkaClusterName;
    }

    public String getKafkaClusterBrokerConfiguration() {
        return kafkaClusterBrokerConfiguration;
    }

    public String getKafkaClusterProducerInitConfiguration() {
        return kafkaClusterProducerInitConfiguration;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getCreateTime() {
        return createTime;
    }
}
