package com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "AdvancedConfigTips", description = "高级配置提示信息")
public class AdvancedConfigTips {

    @ApiModelProperty(value = "概述信息")
    private String summary;

    @ApiModelProperty(value = "各高级配置项信息")
    private List<AdvancedConfigItem> advancedConfigItemList;

}
