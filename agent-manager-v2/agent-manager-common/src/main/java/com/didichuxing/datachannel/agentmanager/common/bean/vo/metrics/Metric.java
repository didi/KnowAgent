package com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "指标")
public class Metric {

    @ApiModelProperty(value = "指标名")
    private String metricName;

    @ApiModelProperty(value = "指标对应的数据点集")
    private List<MetricPoint> metricPointList;

    public Metric() {
    }

    public Metric(String metricName, List<MetricPoint> metricPointList) {
        this.metricName = metricName;
        this.metricPointList = metricPointList;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public void setMetricPointList(List<MetricPoint> metricPointList) {
        this.metricPointList = metricPointList;
    }

    public String getMetricName() {
        return metricName;
    }

    public List<MetricPoint> getMetricPointList() {
        return metricPointList;
    }
}
