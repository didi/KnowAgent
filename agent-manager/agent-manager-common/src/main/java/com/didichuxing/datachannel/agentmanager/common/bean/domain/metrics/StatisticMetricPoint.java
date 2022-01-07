package com.didichuxing.datachannel.agentmanager.common.bean.domain.metrics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 带统计值的数值类型指标点
 */
@Data
public class StatisticMetricPoint extends MinuteMetricPoint {

    @ApiModelProperty(value = "当前采样周期最后一次采样值，注意：如指标仅不存在统计值，仅存在当前值，取last值")
    private Number last;

    @ApiModelProperty(value = "当前采样周期最小值")
    private Number min;

    @ApiModelProperty(value = "当前采样周期最大值")
    private Number max;

    @ApiModelProperty(value = "当前采样周期均值")
    private Number mean;

    @ApiModelProperty(value = "当前采样周期标准差")
    private Number std;

    @ApiModelProperty(value = "当前采样周期55分位数")
    private Number fiftyFiveQuantile;

    @ApiModelProperty(value = "当前采样周期75分位数")
    private Number seventyFiveQuantile;

    @ApiModelProperty(value = "当前采样周期95分位数")
    private Number ninetyFiveQuantile;

    @ApiModelProperty(value = "当前采样周期99分位数")
    private Number ninetyNineQuantile;

}
