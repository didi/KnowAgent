package com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.logcollecttask;

import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.BaseMetricsQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "日志采集任务lable指标请求参数", description = "")
public class LogCollectTaskLableMetricsQueryDTO extends BaseMetricsQueryDTO {

    @ApiModelProperty(value = "日志采集任务id")
    private Long logCollectTaskId;

    @ApiModelProperty(value = "采集路径id")
    private Long pathId;

    @ApiModelProperty(value = "日志采集任务采集的日志所在主机的主机名")
    private String hostName;

}
