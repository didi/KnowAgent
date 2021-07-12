package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "日志采集路径配置", description = "")
public class LogCollectPathCreateDTO {

    @ApiModelProperty(value = "待采集文件字符集")
    private String charset;

    @ApiModelProperty(value = "待采集路径")
    private String path;

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
