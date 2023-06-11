package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.version;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentVersionDropdownVO {

    @ApiModelProperty(value = "Agent版本 id", notes="")
    private Long agentVersionId;

    @ApiModelProperty(value = "Agent版本号", notes="")
    private String agentVersion;

    public Long getAgentVersionId() {
        return agentVersionId;
    }

    public void setAgentVersionId(Long agentVersionId) {
        this.agentVersionId = agentVersionId;
    }

    public String getAgentVersion() {
        return agentVersion;
    }

    public void setAgentVersion(String agentVersion) {
        this.agentVersion = agentVersion;
    }
}
