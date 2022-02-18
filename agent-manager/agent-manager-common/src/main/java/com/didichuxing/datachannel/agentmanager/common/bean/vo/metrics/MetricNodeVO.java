package com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MetricNodeVO {

    @ApiModelProperty(value = "指标/指标类型代码")
    private Integer code;

    @ApiModelProperty(value = "指标/指标类型名")
    private String metricName;

    @ApiModelProperty(value = "指标（类型）描述")
    private String metricDesc;

    @ApiModelProperty(value = "子节点集")
    private List<MetricNodeVO> children;

    @ApiModelProperty(value = "是否默认选中")
    private Boolean checked;

}
