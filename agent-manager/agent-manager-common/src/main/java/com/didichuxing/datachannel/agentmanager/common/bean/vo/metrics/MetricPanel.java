package com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "指标面板")
public class MetricPanel {

    @ApiModelProperty(value = "指标面板名")
    private String panelName;

    @ApiModelProperty(value = "指标面板包括的指标集")
    private List<Metric> metricList;

    public MetricPanel(String panelName, List<Metric> metricList) {
        this.panelName = panelName;
        this.metricList = metricList;
    }

    public void setPanelName(String panelName) {
        this.panelName = panelName;
    }

    public void setMetricList(List<Metric> metricList) {
        this.metricList = metricList;
    }

    public String getPanelName() {
        return panelName;
    }

    public List<Metric> getMetricList() {
        return metricList;
    }

    public Metric buildMetric(String metricName, List<MetricPoint> metricPointList) {
        Metric metric = new Metric(metricName, metricPointList);
        this.metricList.add(metric);
        return metric;
    }

}
