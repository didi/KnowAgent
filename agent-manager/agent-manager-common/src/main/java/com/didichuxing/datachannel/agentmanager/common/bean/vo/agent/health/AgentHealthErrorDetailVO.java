package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.health;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AgentHealthErrorDetailVO {

    @ApiModelProperty(value = "主机名")
    private String hostName;

    @ApiModelProperty(value = "错误日志发送条数")
    private Long errorLogsCount;

    @ApiModelProperty(value = "错误日志信息")
    private List<String> errorLogs;

    @ApiModelProperty(value = "心跳时间")
    private String heartbeatTime;

    @ApiModelProperty(value = "agent 业务指标 id")
    private Long agentMetricId;

}
