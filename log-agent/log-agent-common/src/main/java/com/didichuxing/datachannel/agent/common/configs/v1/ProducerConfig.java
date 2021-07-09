package com.didichuxing.datachannel.agent.common.configs.v1;

public class ProducerConfig {

    private String nameServer;

    private String topic;

    private String properties;

    public boolean isValid() {
        if (nameServer == null || nameServer.trim().length() == 0) {
            return false;
        }

        if (topic == null || topic.trim().length() == 0) {
            return false;
        }

        return true;
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

    @Override
    public String toString() {
        return "ProducerConfig{" + "nameServer='" + nameServer + '\'' + ", topic='" + topic + '\''
               + ", properties='" + properties + '\'' + '}';
    }
}
