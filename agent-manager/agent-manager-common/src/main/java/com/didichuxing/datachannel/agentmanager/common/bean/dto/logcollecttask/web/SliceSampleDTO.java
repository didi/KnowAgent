package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SliceSampleDTO", description = "日志切片样本 body")
public class SliceSampleDTO {

    @ApiModelProperty(value = "待日志切片内容")
    private String content;

    @ApiModelProperty(value = "切片 时间戳串开始位置索引")
    private Integer sliceDateTimeStringStartIndex;

    @ApiModelProperty(value = "切片 时间戳串结束位置索引")
    private Integer sliceDateTimeStringEndIndex;

}
