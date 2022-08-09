package com.didichuxing.datachannel.agentmanager.common.bean.po.host;

import com.didichuxing.datachannel.agentmanager.common.bean.po.BasePO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author huqidong
 * @date 2020-09-21
 * 主机信息
 */
@Data
public class HostAgentPO extends BasePO {

    /**
     * 主机信息唯一标识
     */
    private Long hostId;
    /**
     * 主机名
     */
    private String hostName;
    /**
     * 主机 ip
     */
    private String hostIp;
    /**
     * 主机类型
     * 0：主机
     * 1：容器
     */
    private Integer hostType;
    /**
     * agent健康等级
     */
    private Integer agentHealthLevel;
    /**
     * 主机所属机器单元
     */
    private String hostMachineZone;
    /**
     * 主机创建时间
     */
    private Date hostCreateTime;
    /**
     * agent 版本 id
     */
    private Long agentVersionId;
    /**
     * Agent对象id
     */
    private Long agentId;
    /**
     * 宿主机 名
     */
    private String parentHostName;
    /**
     * agent健康描述信息
     */
    private String agentHealthDescription;
    /**
     * agent健康度巡检结果类型
     */
    private Integer agentHealthInspectionResultType;

    public Integer getAgentHealthInspectionResultType() {
        return agentHealthInspectionResultType;
    }

    public void setAgentHealthInspectionResultType(Integer agentHealthInspectionResultType) {
        this.agentHealthInspectionResultType = agentHealthInspectionResultType;
    }

    public Long getAgentId() {
        return agentId;
    }

    public String getParentHostName() {
        return parentHostName;
    }

    public void setParentHostName(String parentHostName) {
        this.parentHostName = parentHostName;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Long getHostId() {
        return hostId;
    }

    public Long getAgentVersionId() {
        return agentVersionId;
    }

    public void setAgentVersionId(Long agentVersionId) {
        this.agentVersionId = agentVersionId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public Integer getHostType() {
        return hostType;
    }

    public void setHostType(Integer hostType) {
        this.hostType = hostType;
    }

    public String getAgentHealthDescription() {
        return agentHealthDescription;
    }

    public void setAgentHealthDescription(String agentHealthDescription) {
        this.agentHealthDescription = agentHealthDescription;
    }

    public Integer getAgentHealthLevel() {
        return agentHealthLevel;
    }

    public void setAgentHealthLevel(Integer agentHealthLevel) {
        this.agentHealthLevel = agentHealthLevel;
    }

    public String getHostMachineZone() {
        return hostMachineZone;
    }

    public void setHostMachineZone(String hostMachineZone) {
        this.hostMachineZone = hostMachineZone;
    }

    public Date getHostCreateTime() {
        return hostCreateTime;
    }

    public void setHostCreateTime(Date hostCreateTime) {
        this.hostCreateTime = hostCreateTime;
    }
}