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