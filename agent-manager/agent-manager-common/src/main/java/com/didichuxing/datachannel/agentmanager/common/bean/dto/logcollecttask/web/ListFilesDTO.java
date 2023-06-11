package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@ApiModel(value = "ListFilesDTO", description = "文件集查找请求 body")
public class ListFilesDTO {

    @ApiModelProperty(value = "主机名")
    private String hostName;

    @ApiModelProperty(value = "主文件路径")
    private String path;

    @ApiModelProperty(value = "文件名后缀匹配正则")
    private String suffixRegular;

}
