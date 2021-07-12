package com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "文件名后缀匹配规则", description = "")
public class FileNameSuffixMatchRuleVO {

    @ApiModelProperty(value = "文件名后缀匹配类型 0：长度 1：正则")
    private Integer suffixMatchType;

    @ApiModelProperty(value = "文件名后缀匹配类型 0：长度 1：正则")
    private String suffixSeparationCharacter;

    @ApiModelProperty(value = "文件名后缀长度 suffixMatchType为0时必填")
    private Integer suffixLength;

    @ApiModelProperty(value = "文件名后缀长度 suffixMatchType为1时必填")
    private String suffixMatchRegular;

    public void setSuffixMatchType(Integer suffixMatchType) {
        this.suffixMatchType = suffixMatchType;
    }

    public void setSuffixSeparationCharacter(String suffixSeparationCharacter) {
        this.suffixSeparationCharacter = suffixSeparationCharacter;
    }

    public void setSuffixLength(Integer suffixLength) {
        this.suffixLength = suffixLength;
    }

    public void setSuffixMatchRegular(String suffixMatchRegular) {
        this.suffixMatchRegular = suffixMatchRegular;
    }

    public Integer getSuffixMatchType() {
        return suffixMatchType;
    }

    public String getSuffixSeparationCharacter() {
        return suffixSeparationCharacter;
    }

    public Integer getSuffixLength() {
        return suffixLength;
    }

    public String getSuffixMatchRegular() {
        return suffixMatchRegular;
    }
}
