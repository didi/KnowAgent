package com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask.health;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class LogCollectTaskHealthErrorDetailPathHostsVO {

    @ApiModelProperty(value = "采集路径id")
    private Long pathId;

    @ApiModelProperty(value = "采集路径")
    private String path;

    @ApiModelProperty(value = "采集路径关联的主机名集")
    private List<String> hostNameList;

}
