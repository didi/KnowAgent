package com.didichuxing.datachannel.agent.common.configs.v2;

import com.didichuxing.datachannel.agent.common.api.ServiceSwitch;

/**
 * @description: 发送配置
 * @author: huangjw
 * @Date: 19/7/1 15:08
 */
public class ProducerConfig {

    private String nameServer;

    private String topic;

    private String properties;

    private int    switchConfig = ServiceSwitch.ON.getStatus();

    public boolean isValid() {
        if (nameServer == null || nameServer.length() == 0) {
            return false;
        }

        if (topic == null || topic.length() == 0) {
            return false;
        }

        if (switchConfig == ServiceSwitch.OFF.getStatus()) {
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

    public int getSwitchConfig() {
        return switchConfig;
    }

    public void setSwitchConfig(int switchConfig) {
        this.switchConfig = switchConfig;
    }

    @Override
    public String toString() {
        return "ProducerConfig{" + "nameServer='" + nameServer + '\'' + ", topic='" + topic + '\''
               + ", properties='" + properties + '\'' + ", switchConfig=" + switchConfig + '}';
    }
}
