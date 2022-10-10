package com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AdvancedConfigItem {

    @ApiModelProperty(value = "配置项名")
    private String name;

    @ApiModelProperty(value = "配置项描述信息")
    private String description;

    @ApiModelProperty(value = "配置项默认值")
    private String defaultValue;

    public AdvancedConfigItem(String name, String description, String defaultValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public AdvancedConfigItem() {
    }

}
