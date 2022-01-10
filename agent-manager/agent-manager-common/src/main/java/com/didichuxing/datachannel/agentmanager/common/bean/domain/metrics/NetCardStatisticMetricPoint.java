package com.didichuxing.datachannel.agentmanager.common.bean.domain.metrics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 网卡统计类指标
 */
@Data
public class NetCardStatisticMetricPoint extends StatisticMetricPoint {

    @ApiModelProperty(value = "网卡mac地址")
    private String macAddress;

    @ApiModelProperty(value = "网卡设备名")
    private String device;

}
