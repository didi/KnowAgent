package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * @author huqidong
 * @date 2020-09-21
 * Agent采集配置信息：包括"Agent自身配置信息" & "Agent待采集的日志采集任务配置信息"
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentCollectConfigDO {

    @ApiModelProperty(value = "agent自身配置信息")
    private AgentConfigDO agentConfigDO;

    @ApiModelProperty(value = "Agent需要采集的主机名 & 需要运行在该主机上的日志采集任务集")
    private Map<HostInfoDO, List<LogCollectTaskConfiguration>> hostName2LogCollectTaskConfigurationMap;

    public AgentConfigDO getAgentConfiguration() {
        return agentConfigDO;
    }

    public Map<HostInfoDO, List<LogCollectTaskConfiguration>> getHostName2LogCollectTaskConfigurationMap() {
        return hostName2LogCollectTaskConfigurationMap;
    }

    public void setAgentConfiguration(AgentConfigDO agentConfigDO) {
        this.agentConfigDO = agentConfigDO;
    }

    public void setHostName2LogCollectTaskConfigurationMap(Map<HostInfoDO, List<LogCollectTaskConfiguration>> hostName2LogCollectTaskConfigurationMap) {
        this.hostName2LogCollectTaskConfigurationMap = hostName2LogCollectTaskConfigurationMap;
    }
}
