package com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask.health;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LogCollectTaskHealthErrorDetailVO {

    @ApiModelProperty(value = "主机名")
    private String hostName;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "日志采集任务id")
    private Long logCollectTaskId;

    @ApiModelProperty(value = "采集路径id")
    private Long pathId;

    @ApiModelProperty(value = "采集路径")
    private String path;

    @ApiModelProperty(value = "日志时间")
    private String logTime;

    @ApiModelProperty(value = "心跳时间")
    private String heartbeatTime;

    @ApiModelProperty(value = "采集的文件（集）信息")
    private String collectFiles;

    @ApiModelProperty(value = "日志采集任务指标id")
    private Long logCollectTaskMetricId;

}
