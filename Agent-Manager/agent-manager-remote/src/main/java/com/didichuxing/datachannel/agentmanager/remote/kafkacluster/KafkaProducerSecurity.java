package com.didichuxing.datachannel.agentmanager.remote.kafkacluster;

/**
 * kafka 生产端安全认证结构体
 */
public class KafkaProducerSecurity {

    /**
     * kafka 集群 id，对应 kafka-manager 系统对应集群 id
     */
    private String clusterId;
    /**
     * 对应 kafka-manager 系统 app id
     */
    private String appId;
    /**
     * 对应 kafka-manager 系统 password
     */
    private String password;

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
