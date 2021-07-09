package com.didichuxing.datachannel.agentmanager.common.bean.vo.host;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HostVO {

    @ApiModelProperty(value = "主机id")
    private Long id;

    @ApiModelProperty(value = "主机名")
    private String hostName;

    @ApiModelProperty(value = "主机ip")
    private String ip;

    @ApiModelProperty(value = "主机类型 0：主机 1：容器")
    private Integer container;

    @ApiModelProperty(value = "主机类型为容器时，表示容器对应宿主机名")
    private String parentHostName;

    @ApiModelProperty(value = "主机所属机器单元")
    private String machineZone;

    @ApiModelProperty(value = "主机所属部门")
    private String department;

    public void setId(Long id) {
        this.id = id;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setContainer(Integer container) {
        this.container = container;
    }

    public void setParentHostName(String parentHostName) {
        this.parentHostName = parentHostName;
    }

    public void setMachineZone(String machineZone) {
        this.machineZone = machineZone;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Long getId() {
        return id;
    }

    public String getHostName() {
        return hostName;
    }

    public String getIp() {
        return ip;
    }

    public Integer getContainer() {
        return container;
    }

    public String getParentHostName() {
        return parentHostName;
    }

    public String getMachineZone() {
        return machineZone;
    }

    public String getDepartment() {
        return department;
    }
}
