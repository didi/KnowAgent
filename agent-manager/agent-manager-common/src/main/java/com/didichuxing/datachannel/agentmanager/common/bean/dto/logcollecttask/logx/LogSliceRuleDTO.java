package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.logx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "日志切片规则", description = "")
public class LogSliceRuleDTO {

    @ApiModelProperty(value = "切片时间戳前缀字符串")
    private String sliceTimestampPrefixString;

    @ApiModelProperty(value = "切片时间戳前缀字符串左起第几个，index计数从1开始")
    private Integer sliceTimestampPrefixStringIndex;

    @ApiModelProperty(value = "切片 时间戳格式")
    private String sliceTimestampFormat;

    public void setSliceTimestampPrefixString(String sliceTimestampPrefixString) {
        this.sliceTimestampPrefixString = sliceTimestampPrefixString;
    }

    public void setSliceTimestampPrefixStringIndex(Integer sliceTimestampPrefixStringIndex) {
        this.sliceTimestampPrefixStringIndex = sliceTimestampPrefixStringIndex;
    }

    public void setSliceTimestampFormat(String sliceTimestampFormat) {
        this.sliceTimestampFormat = sliceTimestampFormat;
    }

    public String getSliceTimestampPrefixString() {
        return sliceTimestampPrefixString;
    }

    public Integer getSliceTimestampPrefixStringIndex() {
        return sliceTimestampPrefixStringIndex;
    }

    public String getSliceTimestampFormat() {
        return sliceTimestampFormat;
    }
}
