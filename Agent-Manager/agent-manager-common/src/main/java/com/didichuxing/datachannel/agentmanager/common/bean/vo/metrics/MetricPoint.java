package com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "指标点")
public class MetricPoint {

    @ApiModelProperty(value = "Agent指标的某个数据点对应时间戳")
    private Long timestamp;

    @ApiModelProperty(value = "Agent指标的某个数据点对应值")
    private Object value;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
