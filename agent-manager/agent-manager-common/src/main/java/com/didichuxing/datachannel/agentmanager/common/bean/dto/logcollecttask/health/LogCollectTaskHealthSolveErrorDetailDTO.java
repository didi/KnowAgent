package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.health;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "LogCollectTaskHealthSolveErrorDetailDTO", description = "日志采集任务健康度修复请求参数")
public class LogCollectTaskHealthSolveErrorDetailDTO {

    @ApiModelProperty(value = "日志采集任务指标id")
    private Long logCollectTaskMetricId;

    @ApiModelProperty(value = "日志采集任务健康度巡检状态码")
    private Integer logCollectTaskHealthInspectionCode;

}
