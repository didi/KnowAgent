package com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "指标面板")
@Data
public class MetricPanel {

    @ApiModelProperty(value = "指标名")
    private String name;

    @ApiModelProperty(value = "指标类型 1：lable 2：多根折线图 3：单根折线图")
    private Integer type;

    @ApiModelProperty(value = "基础单位")
    private Integer baseUnit;

    @ApiModelProperty(value = "展示单位")
    private Integer displayUnit;

    @ApiModelProperty(value = "lable类型指标值")
    private Object lableValue;

    @ApiModelProperty(value = "单根折线图类型指标值")
    private MetricPointLine singleLineChatValue;

    @ApiModelProperty(value = "多根折线图类型指标值")
    private List<MetricPointLine> multiLineChatValue;

    @ApiModelProperty(value = "柱状图类型指标值")
    private List<Pair<Object, Object>> histogramChatValue;

}
