package com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.logcollecttask;

import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.BaseMetricsQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "日志采集任务单条线指标请求参数", description = "")
public class LogCollectTaskSingleLineMetricsQueryDTO extends BaseMetricsQueryDTO {

    @ApiModelProperty(value = "日志采集任务id")
    private Long logCollectTaskId;

}
