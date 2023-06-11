package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "日志切片规则", description = "")
public class LogSliceRuleDTO {

    @ApiModelProperty(value = "日志内容切片类型 0：时间戳切片 1：正则匹配切片")
    private Integer sliceType;

    @ApiModelProperty(value = "切片正则")
    private String sliceRegular;

    @ApiModelProperty(value = "切片时间戳前缀字符串")
    private String sliceTimestampPrefixString;

    @ApiModelProperty(value = "切片时间戳前缀字符串左起第几个，index计数从1开始")
    private Integer sliceTimestampPrefixStringIndex;

    @ApiModelProperty(value = "切片 时间戳格式")
    private String sliceTimestampFormat;

    public void setSliceType(Integer sliceType) {
        this.sliceType = sliceType;
    }

    public void setSliceRegular(String sliceRegular) {
        this.sliceRegular = sliceRegular;
    }

    public void setSliceTimestampPrefixString(String sliceTimestampPrefixString) {
        this.sliceTimestampPrefixString = sliceTimestampPrefixString;
    }

    public void setSliceTimestampPrefixStringIndex(Integer sliceTimestampPrefixStringIndex) {
        this.sliceTimestampPrefixStringIndex = sliceTimestampPrefixStringIndex;
    }

    public void setSliceTimestampFormat(String sliceTimestampFormat) {
        this.sliceTimestampFormat = sliceTimestampFormat;
    }

    public Integer getSliceType() {
        return sliceType;
    }

    public String getSliceRegular() {
        return sliceRegular;
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
