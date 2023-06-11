package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SliceDTO", description = "日志切片请求 body")
public class SliceDTO {

    @ApiModelProperty(value = "待日志切片内容")
    private String content;

    @ApiModelProperty(value = "切片时间戳前缀字符串")
    private String sliceTimestampPrefixString;

    @ApiModelProperty(value = "切片时间戳前缀字符串左起第几个，index计数从1开始")
    private Integer sliceTimestampPrefixStringIndex;

    @ApiModelProperty(value = "切片 时间戳格式")
    private String sliceTimestampFormat;

}
