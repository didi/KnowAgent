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
    private List<MetricPoint> metricPonitList;

    public Metric() {
    }

    public Metric(String metricName, List<MetricPoint> metricPonitList) {
        this.metricName = metricName;
        this.metricPonitList = metricPonitList;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public void setMetricPonitList(List<MetricPoint> metricPonitList) {
        this.metricPonitList = metricPonitList;
    }

    public String getMetricName() {
        return metricName;
    }

    public List<MetricPoint> getMetricPonitList() {
        return metricPonitList;
    }
}
