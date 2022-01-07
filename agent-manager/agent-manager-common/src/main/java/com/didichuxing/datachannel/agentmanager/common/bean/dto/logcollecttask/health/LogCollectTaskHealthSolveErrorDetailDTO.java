package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.health;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LogCollectTaskHealthSolveErrorDetailDTO {

    @ApiModelProperty(value = "日志采集任务指标id")
    private Long logCollectTaskMetricId;

    @ApiModelProperty(value = "日志采集任务健康度巡检状态码")
    private Integer logCollectTaskHealthInspectionCode;

}
