package com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.agent;

import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.BaseMetricsQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.MultiMetricsQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "agent多条线指标请求参数", description = "")
public class AgentMultiMetricsQueryDTO extends MultiMetricsQueryDTO {

    @ApiModelProperty(value = "agent所在宿主机主机名")
    private String hostName;

}
