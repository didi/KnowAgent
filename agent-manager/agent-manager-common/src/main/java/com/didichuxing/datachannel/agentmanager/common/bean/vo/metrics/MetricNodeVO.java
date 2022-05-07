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

    @ApiModelProperty(value = "指标级别 1：一级指标，黄金指标 2：二级指标，表示用户较为关注的指标 3：三级指标，普通指标")
    private Integer metricLevel;

    @ApiModelProperty(value = "是否叶节点")
    private Boolean isLeafNode;

}
