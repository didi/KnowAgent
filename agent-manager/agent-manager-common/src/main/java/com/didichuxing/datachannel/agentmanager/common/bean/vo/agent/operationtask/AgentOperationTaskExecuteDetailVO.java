package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.operationtask;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentOperationTaskExecuteDetailVO {

    @ApiModelProperty(value = "主机名")
    private String hostName;

    @ApiModelProperty(value = "主机ip")
    private String ip;

    @ApiModelProperty(value = "主机类型 0：主机 1：容器")
    private Integer container;

    @ApiModelProperty(value = "执行状态 0：待执行 1：执行中 2：执行成功 3：执行失败")
    private Integer executeStatus;

    @ApiModelProperty(value = "源Agent版本")
    private String sourceAgentVersion;

    @ApiModelProperty(value = "任务执行开始时间")
    private Long executeStartTime;

    @ApiModelProperty(value = "任务执行完成时间")
    private Long executeEndTime;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getContainer() {
        return container;
    }

    public void setContainer(Integer container) {
        this.container = container;
    }

    public Integer getExecuteStatus() {
        return executeStatus;
    }

    public void setExecuteStatus(Integer executeStatus) {
        this.executeStatus = executeStatus;
    }

    public String getSourceAgentVersion() {
        return sourceAgentVersion;
    }

    public void setSourceAgentVersion(String sourceAgentVersion) {
        this.sourceAgentVersion = sourceAgentVersion;
    }

    public Long getExecuteStartTime() {
        return executeStartTime;
    }

    public void setExecuteStartTime(Long executeStartTime) {
        this.executeStartTime = executeStartTime;
    }

    public Long getExecuteEndTime() {
        return executeEndTime;
    }

    public void setExecuteEndTime(Long executeEndTime) {
        this.executeEndTime = executeEndTime;
    }
}
