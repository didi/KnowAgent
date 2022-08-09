package com.didichuxing.datachannel.agentmanager.common.bean.dto.host;

import com.didichuxing.datachannel.agentmanager.common.bean.dto.PaginationRequestDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HostPaginationRequestDTO extends PaginationRequestDTO {

    @ApiModelProperty(value = "主机名")
    private String hostName;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "Agent版本id")
    private List<Long> agentVersionIdList;

    @ApiModelProperty(value = "AgentPO 健康度 0:红 1：黄 2：绿色")
    private List<Integer> agentHealthLevelList;

    @ApiModelProperty(value = "主机类型 0：主机 1：容器")
    private List<Integer> containerList;

    @ApiModelProperty(value = "服务Id")
    private List<Long> serviceIdList;

    @ApiModelProperty(value = "所属机房集")
    private List<String> machineZoneList;

    @ApiModelProperty(value = "主机创建时间起始检索时间", notes="")
    private Long hostCreateTimeStart;

    @ApiModelProperty(value = "主机创建时间结束检索时间", notes="")
    private Long hostCreateTimeEnd;

    @ApiModelProperty(value = "排序依照的字段，可选host_name host_type agent_health_level host_machine_zone host_create_time", notes="")
    private String sortColumn;

    @ApiModelProperty(value = "是否升序", notes="")
    private Boolean asc;

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

    public List<Long> getAgentVersionIdList() {
        return agentVersionIdList;
    }

    public void setAgentVersionIdList(List<Long> agentVersionIdList) {
        this.agentVersionIdList = agentVersionIdList;
    }

    public List<Integer> getAgentHealthLevelList() {
        return agentHealthLevelList;
    }

    public void setAgentHealthLevelList(List<Integer> agentHealthLevelList) {
        this.agentHealthLevelList = agentHealthLevelList;
    }

    public List<String> getMachineZoneList() {
        return machineZoneList;
    }

    public void setMachineZoneList(List<String> machineZoneList) {
        this.machineZoneList = machineZoneList;
    }

    public List<Integer> getContainerList() {
        return containerList;
    }

    public void setContainerList(List<Integer> containerList) {
        this.containerList = containerList;
    }

    public List<Long> getServiceIdList() {
        return serviceIdList;
    }

    public void setServiceIdList(List<Long> serviceIdList) {
        this.serviceIdList = serviceIdList;
    }

    public Long getHostCreateTimeStart() {
        return hostCreateTimeStart;
    }

    public void setHostCreateTimeStart(Long hostCreateTimeStart) {
        this.hostCreateTimeStart = hostCreateTimeStart;
    }

    public Long getHostCreateTimeEnd() {
        return hostCreateTimeEnd;
    }

    public void setHostCreateTimeEnd(Long hostCreateTimeEnd) {
        this.hostCreateTimeEnd = hostCreateTimeEnd;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public Boolean getAsc() {
        return asc;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }
}
