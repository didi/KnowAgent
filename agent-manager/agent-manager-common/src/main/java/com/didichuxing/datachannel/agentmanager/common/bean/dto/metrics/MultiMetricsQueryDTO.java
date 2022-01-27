package com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "多条线指标请求参数", description = "")
public class MultiMetricsQueryDTO extends BaseMetricsQueryDTO {

    @ApiModelProperty(value = "排序时间点 精度：分 默认值：endTime")
    private Long sortTime;

    @ApiModelProperty(
            value = "指标排序类型：" +
                    "0：最近一次采样值，" +
                    "1：采样周期内最小值，" +
                    "2：采样周期内最大值，" +
                    "3：采样周期内均值，" +
                    "4：采样周期内样本值标准差，" +
                    "5：采样周期内55分位数值，" +
                    "6：采样周期内75分位数值，" +
                    "7：采样周期内95分位数值，" +
                    "8：采样周期内99分位数值。" +
                    "注意：对于不存在统计值的指标，请求时的指标排序类型sortMetricType，取值0（最近一次采样值）默认值：0"
    )
    private Integer sortMetricType;

    @ApiModelProperty(value = "按给定指标类型排序后取的top数，注意：0：表示获取全部，非0表示：获取给定topN 默认值：5")
    private Integer topN;

}
