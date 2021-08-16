package com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "元数据同步结果", description = "")
public class MetadataSyncResult {

    @ApiModelProperty(value = "各服务对应元数据同步结果集")
    private List<MetadataSyncResultPerService> metadataSyncResultPerServiceList;

    public List<MetadataSyncResultPerService> getMetadataSyncResultPerServiceList() {
        return metadataSyncResultPerServiceList;
    }

    public void setMetadataSyncResultPerServiceList(List<MetadataSyncResultPerService> metadataSyncResultPerServiceList) {
        this.metadataSyncResultPerServiceList = metadataSyncResultPerServiceList;
    }

}

class MetadataSyncResultPerService {

    @ApiModelProperty(value = "服务名")
    private String serviceName;

    @ApiModelProperty(value = "关联主机数")
    private Integer relateHostNum;

    @ApiModelProperty(value = "同步状态 0：失败 1：成功")
    private Integer syncSuccess;

    @ApiModelProperty(value = "ip重复主机信息集")
    private List<HostInfo> duplicateIpHostList;

    @ApiModelProperty(value = "主机名重复主机信息集")
    private List<HostInfo> duplicateHostNameHostList;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Integer getRelateHostNum() {
        return relateHostNum;
    }

    public void setRelateHostNum(Integer relateHostNum) {
        this.relateHostNum = relateHostNum;
    }

    public Integer getSyncSuccess() {
        return syncSuccess;
    }

    public void setSyncSuccess(Integer syncSuccess) {
        this.syncSuccess = syncSuccess;
    }

    public List<HostInfo> getDuplicateIpHostList() {
        return duplicateIpHostList;
    }

    public void setDuplicateIpHostList(List<HostInfo> duplicateIpHostList) {
        this.duplicateIpHostList = duplicateIpHostList;
    }

    public List<HostInfo> getDuplicateHostNameHostList() {
        return duplicateHostNameHostList;
    }

    public void setDuplicateHostNameHostList(List<HostInfo> duplicateHostNameHostList) {
        this.duplicateHostNameHostList = duplicateHostNameHostList;
    }

}

class HostInfo {

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