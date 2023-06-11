package com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "业务指标请求参数", description = "")
public class BusinessMetricsQueryDTO extends MultiMetricsQueryDTO {

    @ApiModelProperty(value = "日志采集任务id")
    private Long logCollectTaskId;

    @ApiModelProperty(value = "采集路径id")
    private Long pathId;

    @ApiModelProperty(value = "主机名")
    private String hostName;

}
