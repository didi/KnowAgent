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

    @ApiModelProperty(value = "指标描述 注意：仅在展示指标项（非指标类型）时存在值")
    private String metricDesc;

    @ApiModelProperty(value = "子节点集")
    private List<MetricNodeVO> children;

}
