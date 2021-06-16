package com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "指标面板组")
public class MetricPanelGroup {

    @ApiModelProperty(value = "指标面板组名")
    private String metricPanelGroupName;

    @ApiModelProperty(value = "指标面板组包括的指标面板集")
    private List<MetricPanel> metricPanelList;

    public MetricPanelGroup(String metricPanelGroupName, List<MetricPanel> metricPanelList) {
        this.metricPanelGroupName = metricPanelGroupName;
        this.metricPanelList = metricPanelList;
    }

    public void setMetricPanelGroupName(String metricPanelGroupName) {
        this.metricPanelGroupName = metricPanelGroupName;
    }

    public void setMetricPanelList(List<MetricPanel> metricPanelList) {
        this.metricPanelList = metricPanelList;
    }

    public String getMetricPanelGroupName() {
        return metricPanelGroupName;
    }

    public List<MetricPanel> getMetricPanelList() {
        return metricPanelList;
    }

    public MetricPanel buildMetricPanel(String panelName) {
        List<Metric> metricList = new ArrayList<>();
        MetricPanel metricPanel = new MetricPanel(panelName, metricList);
        this.metricPanelList.add(metricPanel);
        return metricPanel;
    }

}
