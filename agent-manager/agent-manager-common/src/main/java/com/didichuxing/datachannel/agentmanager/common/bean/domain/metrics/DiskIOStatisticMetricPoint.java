package com.didichuxing.datachannel.agentmanager.common.bean.domain.metrics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 磁盘io统计类指标
 */
@Data
public class DiskIOStatisticMetricPoint extends StatisticMetricPoint {

    @ApiModelProperty(value = "磁盘挂载路径")
    private String path;

    @ApiModelProperty(value = "磁盘设备名")
    private String device;

    @ApiModelProperty(value = "文件系统类型")
    private String fsType;

}
