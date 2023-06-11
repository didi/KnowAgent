package com.didichuxing.datachannel.agentmanager.common.bean.domain.metrics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 以分钟为单位的指标点
 */
@Data
public class MinuteMetricPoint {

    @ApiModelProperty(value = "以分钟为单位的时间戳位点")
    private Long timeStampMinute;

}
