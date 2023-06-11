package com.didichuxing.datachannel.agent.common.configs.v2.component;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 发送下游配置event配置、metric上报配置
 * @author: huangjw
 * @Date: 19/7/1 14:41
 */
public class EventMetricsConfig implements Cloneable {

    /**
     * 所属应用
     */
    private String      belongToCluster = "default";

    /**
     * 对应主机的部门信息
     */
    private String      queryFrom       = "didi";

    /**
     * 传输标签
     */
    private String      transName;

    /**
     * 叶子节点
     */
    private String      odinLeaf;

    /**
     * 原始的appName
     */
    private String      originalAppName;

    /**
     * 是否是服务化,0表示非Service,1表示是Service,
     */
    private Integer     isService       = 0;

    /**
     * 地区信息
     */
    private String      location        = "cn";

    /**
     * 容器名
     */
    private String      dockerName;

    /**
     * metrics冗余配置
     */
    Map<String, String> otherMetrics    = new HashMap<>();

    /**
     * events 冗余配置
     */
    Map<String, String> otherEvents     = new HashMap<>();

    public String getBelongToCluster() {
        return belongToCluster;
    }

    public void setBelongToCluster(String belongToCluster) {
        this.belongToCluster = belongToCluster;
    }

    public String getQueryFrom() {
        return queryFrom;
    }

    public void setQueryFrom(String queryFrom) {
        this.queryFrom = queryFrom;
    }

    public String getTransName() {
        return transName;
    }

    public void setTransName(String transName) {
        this.transName = transName;
    }

    public String getOdinLeaf() {
        return odinLeaf;
    }

    public void setOdinLeaf(String odinLeaf) {
        this.odinLeaf = odinLeaf;
    }

    public String getOriginalAppName() {
        return originalAppName;
    }

    public void setOriginalAppName(String originalAppName) {
        this.originalAppName = originalAppName;
    }

    public Integer getIsService() {
        return isService;
    }

    public void setIsService(Integer isService) {
        this.isService = isService;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Map<String, String> getOtherMetrics() {
        return otherMetrics;
    }

    public void setOtherMetrics(Map<String, String> otherMetrics) {
        this.otherMetrics = otherMetrics;
    }

    public Map<String, String> getOtherEvents() {
        return otherEvents;
    }

    public void setOtherEvents(Map<String, String> otherEvents) {
        this.otherEvents = otherEvents;
    }

    @Override
    public EventMetricsConfig clone() {
        EventMetricsConfig eventMetricsConfig = null;
        try {
            eventMetricsConfig = (EventMetricsConfig) super.clone();
            Map<String, String> newOtherMetrics = new HashMap<>();
            eventMetricsConfig.setOtherMetrics(newOtherMetrics);
            if (this.otherMetrics != null) {
                for (Map.Entry<String, String> entry : this.otherMetrics.entrySet()) {
                    newOtherMetrics.put(entry.getKey(), entry.getValue());
                }
            }

            Map<String, String> newOtherEvents = new HashMap<>();
            eventMetricsConfig.setOtherEvents(newOtherEvents);
            if (this.otherEvents != null) {
                for (Map.Entry<String, String> entry : this.otherEvents.entrySet()) {
                    newOtherEvents.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (CloneNotSupportedException e) {

        }
        return eventMetricsConfig;
    }

    public String getDockerName() {
        return dockerName;
    }

    public void setDockerName(String dockerName) {
        this.dockerName = dockerName;
    }

    @Override
    public String toString() {
        return "EventMetricsConfig{" + "belongToCluster='" + belongToCluster + '\'' + ", queryFrom='" + queryFrom + '\''
               + ", transName='" + transName + '\'' + ", odinLeaf='" + odinLeaf + '\'' + ", originalAppName='"
               + originalAppName + '\'' + ", isService=" + isService + ", location='" + location + '\''
               + ", dockerName='" + dockerName + '\'' + ", otherMetrics=" + otherMetrics + ", otherEvents="
               + otherEvents + '}';
    }
}
