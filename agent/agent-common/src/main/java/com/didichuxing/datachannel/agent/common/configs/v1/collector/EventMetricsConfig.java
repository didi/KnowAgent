package com.didichuxing.datachannel.agent.common.configs.v1.collector;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: metrics配置
 * @author: huangjw
 * @Date: 18/6/20 12:54
 */
public class EventMetricsConfig implements Cloneable{
    /**
     * metrics冗余配置
     */
    Map<String, String> otherMetrics = new HashMap<>();

    public Map<String, String> getOtherMetrics() {
        return otherMetrics;
    }

    public void setOtherMetrics(Map<String, String> otherMetrics) {
        this.otherMetrics = otherMetrics;
    }

    @Override
    public EventMetricsConfig clone() {
        EventMetricsConfig eventMetricsConfig = null;
        try {
            eventMetricsConfig = (EventMetricsConfig) super.clone();
            Map<String, String> newOtherMetrics = new HashMap<>();
            eventMetricsConfig.setOtherMetrics(newOtherMetrics);
            if (this.otherMetrics != null) {
                for (Map.Entry<String, String> entry : this.otherMetrics.entrySet()){
                    newOtherMetrics.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (CloneNotSupportedException e) {

        }
        return eventMetricsConfig;
    }

    @Override public String toString() {
        return "EventMetricsConfig{" + "otherMetrics=" + otherMetrics + '}';
    }
}
