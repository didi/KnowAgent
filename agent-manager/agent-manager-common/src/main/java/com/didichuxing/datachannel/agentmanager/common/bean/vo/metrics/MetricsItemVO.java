package com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MetricsItemVO {

    @ApiModelProperty(value = "指标/指标类型代码")
    private Integer code;

    @ApiModelProperty(value = "指标/指标类型名")
    private String metricName;

    @ApiModelProperty(value = "指标描述 注意：仅在展示指标项（非指标类型）时存在值")
    private String metricDesc;

    @ApiModelProperty(value = "类型：0：指标类型 1：指标项")
    private Integer type;

}
