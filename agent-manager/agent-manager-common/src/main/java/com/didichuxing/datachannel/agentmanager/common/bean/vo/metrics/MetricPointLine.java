package com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "单根指标线")
@Data
public class MetricPointLine {

    @ApiModelProperty(value = "指标点集")
    private List<MetricPoint> metricPointList;

    @ApiModelProperty(value = "名称")
    private String name;

}
