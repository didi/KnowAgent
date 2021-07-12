package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.version;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentVersionPaginationRecordVO {

    @ApiModelProperty(value = "Agent版本 id", notes="")
    private Long agentVersionId;

    @ApiModelProperty(value = "Agent版本号", notes="")
    private String agentVersion;

    @ApiModelProperty(value = "Agent版本包名", notes="")
    private String agentPackageName;

    @ApiModelProperty(value = "Agent版本描述", notes="")
    private String agentVersionDescription;

    @ApiModelProperty(value = "Agent版本创建时间", notes="")
    private Long createTime;

    @ApiModelProperty(value = "文件MD5值")
    private String fileMd5;

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

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

    public String getAgentPackageName() {
        return agentPackageName;
    }

    public void setAgentPackageName(String agentPackageName) {
        this.agentPackageName = agentPackageName;
    }

    public String getAgentVersionDescription() {
        return agentVersionDescription;
    }

    public void setAgentVersionDescription(String agentVersionDescription) {
        this.agentVersionDescription = agentVersionDescription;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

}
