package com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics;

import java.util.ArrayList;
import java.util.List;

/**
 * 指标看板
 */
public class MetricsDashBoard {

    private List<MetricPanelGroup> metricPanelGroupList = new ArrayList<>();

    public MetricPanelGroup buildMetricPanelGroup(String metricPanelGroupName) {
        List<MetricPanel> metricPanelList = new ArrayList<>();
        MetricPanelGroup metricPanelGroup = new MetricPanelGroup(metricPanelGroupName, metricPanelList);
        this.metricPanelGroupList.add(metricPanelGroup);
        return metricPanelGroup;
    }

    public List<MetricPanelGroup> getMetricPanelGroupList() {
        return metricPanelGroupList;
    }

}
