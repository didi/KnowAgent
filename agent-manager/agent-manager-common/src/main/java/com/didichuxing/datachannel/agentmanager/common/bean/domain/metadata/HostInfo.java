package com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata;

import io.swagger.annotations.ApiModelProperty;

public class HostInfo {

    @ApiModelProperty(value = "主机名")
    private String hostName;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "主机类型 0：主机 1：容器")
    private Integer hostType;

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

    public Integer getHostType() {
        return hostType;
    }

    public void setHostType(Integer hostType) {
        this.hostType = hostType;
    }

}
