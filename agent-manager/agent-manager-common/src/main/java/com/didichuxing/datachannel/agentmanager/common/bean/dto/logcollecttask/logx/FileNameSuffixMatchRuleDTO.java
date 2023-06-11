package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.logx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "文件名后缀匹配规则", description = "")
public class FileNameSuffixMatchRuleDTO {

    @ApiModelProperty(value = "文件名后缀匹配正则表达式")
    private String suffixMatchRegular;

    public String getSuffixMatchRegular() {
        return suffixMatchRegular;
    }

    public void setSuffixMatchRegular(String suffixMatchRegular) {
        this.suffixMatchRegular = suffixMatchRegular;
    }

}
