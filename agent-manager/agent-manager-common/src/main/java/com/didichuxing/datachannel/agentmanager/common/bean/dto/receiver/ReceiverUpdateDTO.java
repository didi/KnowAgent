package com.didichuxing.datachannel.agentmanager.common.bean.dto.receiver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huqidong
 * @date 2020-09-21
 * kafka集群对象
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiverUpdateDTO {

    @ApiModelProperty(value = "接收端对象id 注：新增Receiver接口不可填，仅在更新Receiver接口必填")
    private Long id;

    @ApiModelProperty(value = "kafka集群名 必填")
    private String kafkaClusterName;

    @ApiModelProperty(value = "kafka集群broker配置 必填")
    private String kafkaClusterBrokerConfiguration;

    @ApiModelProperty(value = "kafka集群对应生产端初始化配置 必填")
    private String kafkaClusterProducerInitConfiguration;

    @ApiModelProperty(value = "agent errorlogs 流对应 topic 名")
    private String agentErrorLogsTopic;

    @ApiModelProperty(value = "agent metrics 流对应 topic 名")
    private String agentMetricsTopic;

    public String getAgentErrorLogsTopic() {
        return agentErrorLogsTopic;
    }

    public void setAgentErrorLogsTopic(String agentErrorLogsTopic) {
        this.agentErrorLogsTopic = agentErrorLogsTopic;
    }

    public String getAgentMetricsTopic() {
        return agentMetricsTopic;
    }

    public void setAgentMetricsTopic(String agentMetricsTopic) {
        this.agentMetricsTopic = agentMetricsTopic;
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

    public String getKafkaClusterName() {
        return kafkaClusterName;
    }

    public String getKafkaClusterBrokerConfiguration() {
        return kafkaClusterBrokerConfiguration;
    }

    public String getKafkaClusterProducerInitConfiguration() {
        return kafkaClusterProducerInitConfiguration;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
