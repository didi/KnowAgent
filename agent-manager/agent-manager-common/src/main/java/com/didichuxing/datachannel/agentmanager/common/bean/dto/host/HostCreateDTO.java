package com.didichuxing.datachannel.agentmanager.common.bean.dto.host;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huqidong
 * @date 2020-09-21
 * Host对象
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HostCreateDTO {

    @ApiModelProperty(value = "主机名 注：新增主机接口必填，更新主机接口不可填")
    private String hostName;

    @ApiModelProperty(value = "主机ip 必填")
    private String ip;

    @ApiModelProperty(value = "主机类型 0：主机 1：容器 必填")
    private Integer container;

    @ApiModelProperty(value = "容器场景，表示容器对应宿主机名 当主机类型为容器时，必填")
    private String parentHostName;

    @ApiModelProperty(value = "主机所属机器单元 必填")
    private String machineZone;

    @ApiModelProperty(value = "主机所属部门 必填")
    private String department;

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
