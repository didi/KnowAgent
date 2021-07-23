package com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics;

import java.util.List;

public class MetricPointList {
    private List<MetricPoint> metricPointList;
    private String name;

    public List<MetricPoint> getMetricPointList() {
        return metricPointList;
    }

    public void setMetricPointList(List<MetricPoint> metricPointList) {
        this.metricPointList = metricPointList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
