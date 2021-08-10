package com.didichuxing.datachannel.agentmanager.common.bean.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class DashboardRequestDTO {

    @ApiModelProperty(value = "指标开始时间戳")
    private Long startTime;

    @ApiModelProperty(value = "指标结束时间戳")
    private Long endTime;

    @ApiModelProperty(value = "需要查询的dashboard指标code集")
    private List<Integer> dashboardMetricsCodes;

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public List<Integer> getDashboardMetricsCodes() {
        return dashboardMetricsCodes;
    }

    public void setDashboardMetricsCodes(List<Integer> dashboardMetricsCodes) {
        this.dashboardMetricsCodes = dashboardMetricsCodes;
    }

}
