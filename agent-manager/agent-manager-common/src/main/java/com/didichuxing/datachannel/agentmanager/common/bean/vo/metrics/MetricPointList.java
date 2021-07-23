package com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics;

import java.util.List;

public class MetricPointList {
    private List<MetricPoint> metricPointList;
    private String hostName;

    public List<MetricPoint> getMetricPointList() {
        return metricPointList;
    }

    public void setMetricPointList(List<MetricPoint> metricPointList) {
        this.metricPointList = metricPointList;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
