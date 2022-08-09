package com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "指标请求参数", description = "")
public class BaseMetricsQueryDTO {

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;

    @ApiModelProperty(value = "指标代码")
    private Integer metricCode;

}
