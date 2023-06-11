package com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.metrics.StatisticMetricPoint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "指标点")
@Data
public class MetricPoint extends StatisticMetricPoint {

    @ApiModelProperty(value = "网卡mac地址")
    private String macAddress;

    @ApiModelProperty(value = "网卡/磁盘设备名")
    private String device;

    @ApiModelProperty(value = "磁盘挂载/日志采集路径")
    private String path;

    @ApiModelProperty(value = "文件系统类型")
    private String fsType;

    @ApiModelProperty(value = "主机名")
    private String hostName;

    @ApiModelProperty(value = "日志采集任务 id")
    private Long logCollectTaskId;

    @ApiModelProperty(value = "服务名集")
    private String serviceNames;

}
