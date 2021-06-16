package com.didichuxing.datachannel.agentmanager.common.bean.dto.host;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huqidong
 * @date 2020-09-21
 * Host对象
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HostUpdateDTO {

    @ApiModelProperty(value = "Host对象id 注：新增主机接口不可填，仅在更新主机接口必填")
    private Long id;

    @ApiModelProperty(value = "主机所属机器单元 必填")
    private String machineZone;

    @ApiModelProperty(value = "主机所属部门 必填")
    private String department;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMachineZone() {
        return machineZone;
    }

    public void setMachineZone(String machineZone) {
        this.machineZone = machineZone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

}
