package com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.metrics;

public class DashBoardStatisticsDO {

    /**
     * 指标值
     */
    private Object metricValue;

    /**
     * key
     */
    private Object key;

    /**
     * 心跳时间
     */
    private Long heartbeatTime;

    public Object getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(Object metricValue) {
        this.metricValue = metricValue;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Long getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(Long heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

}
