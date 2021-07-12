package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huqidong
 * @date 2020-09-21
 * 接收端配置信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiverConfiguration {

    @ApiModelProperty(value = "接收端id")
    private Long id;

    @ApiModelProperty(value = "kafka 集群名")
    private String kafkaClusterName;

    @ApiModelProperty(value = "kafka 集群 broker 配置")
    private String kafkaClusterBrokerConfiguration;

    @ApiModelProperty(value = "kafka 集群对应生产端初始化配置")
    private String kafkaClusterProducerInitConfiguration;

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

}
