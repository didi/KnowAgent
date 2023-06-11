package com.didichuxing.datachannel.agentmanager.common.bean.po.receiver;

import com.didichuxing.datachannel.agentmanager.common.bean.po.BasePO;
import lombok.Data;

import java.util.Date;

/**
 * @author huqidong
 * @date 2020-09-21
 * Kafka集群信息
 */
@Data
public class KafkaClusterPO extends BasePO {

    /**
     * Kafka集群信息唯一标识
     */
    private Long id;
    /**
     * kafka 集群名
     */
    private String kafkaClusterName;
    /**
     * kafka 集群 broker 配置
     */
    private String kafkaClusterBrokerConfiguration;
    /**
     * kafka 集群对应生产端初始化配置
     */
    private String kafkaClusterProducerInitConfiguration;
    /**
     * kafka集群id
     */
    private Long kafkaClusterId;

    /**
     * agent errorlogs 流对应 topic 名
     */
    private String agentErrorLogsTopic;

    /**
     * agent metrics 流对应 topic 名
     */
    private String agentMetricsTopic;

    /**
     * 接收端类型
     */
    private Integer receiverType;

    public Integer getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(Integer receiverType) {
        this.receiverType = receiverType;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKafkaClusterName() {
        return kafkaClusterName;
    }

    public void setKafkaClusterName(String kafkaClusterName) {
        this.kafkaClusterName = kafkaClusterName;
    }

    public String getKafkaClusterBrokerConfiguration() {
        return kafkaClusterBrokerConfiguration;
    }

    public void setKafkaClusterBrokerConfiguration(String kafkaClusterBrokerConfiguration) {
        this.kafkaClusterBrokerConfiguration = kafkaClusterBrokerConfiguration;
    }

    public String getKafkaClusterProducerInitConfiguration() {
        return kafkaClusterProducerInitConfiguration;
    }

    public void setKafkaClusterProducerInitConfiguration(String kafkaClusterProducerInitConfiguration) {
        this.kafkaClusterProducerInitConfiguration = kafkaClusterProducerInitConfiguration;
    }

    public Long getKafkaClusterId() {
        return kafkaClusterId;
    }

    public void setKafkaClusterId(Long kafkaClusterId) {
        this.kafkaClusterId = kafkaClusterId;
    }

}