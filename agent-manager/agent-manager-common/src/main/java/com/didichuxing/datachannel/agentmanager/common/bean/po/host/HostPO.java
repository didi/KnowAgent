package com.didichuxing.datachannel.agentmanager.common.bean.po.host;

import com.didichuxing.datachannel.agentmanager.common.bean.po.BasePO;
import lombok.Data;

import java.util.Date;

/**
 * @author huqidong
 * @date 2020-09-21
 * 主机信息
 */
@Data
public class HostPO extends BasePO {

    /**
     * 主机信息唯一标识
     */
    private Long id;
    /**
     * 主机名
     */
    private String hostName;
    /**
     * 主机 ip
     */
    private String ip;
    /**
     * 标识是否为容器节点
     * 0：否
     * 1：是
     */
    private Integer container;
    /**
     * 针对容器场景，表示容器对应宿主机名
     */
    private String parentHostName;
    /**
     * 主机所属机器单元
     */
    private String machineZone;
    /**
     * 主机所属部门
     */
    private String department;

    private Long externalId;
    /**
     * 扩展字段，json格式
     */
    private String extendField;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getParentHostName() {
        return parentHostName;
    }

    public void setParentHostName(String parentHostName) {
        this.parentHostName = parentHostName;
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

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public String getExtendField() {
        return extendField;
    }

    public void setExtendField(String extendField) {
        this.extendField = extendField;
    }

}