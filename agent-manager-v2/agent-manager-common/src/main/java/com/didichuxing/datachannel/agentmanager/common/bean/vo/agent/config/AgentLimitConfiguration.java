package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

/**
 * Agent限流相关配置项
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentLimitConfiguration {

    @ApiModelProperty(value = "采集端限流 cpu 阈值")
    private Double cpuLimitThreshold;

    @ApiModelProperty(value = "采集端限流流量阈值 单位：字节")
    private Long byteLimitThreshold;

}
