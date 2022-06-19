package com.didichuxing.datachannel.agent.common.configs.v1.collector;

/**
 * @description: 采集目的地配置
 * @author: huangjw
 * @Date: 18/6/20 12:05
 */
public class TargetConfig implements Cloneable {

    /**
     * 发送集群id
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

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(String bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public TargetConfig clone() {
        TargetConfig targetConfig = null;
        try {
            targetConfig = (TargetConfig) super.clone();
        } catch (CloneNotSupportedException e) {

        }
        return targetConfig;
    }

    @Override
    public String toString() {
        return "TargetConfig{" + "clusterId=" + clusterId + ", properties='" + properties + '\''
               + ", bootstrap='" + bootstrap + '\'' + '}';
    }
}
