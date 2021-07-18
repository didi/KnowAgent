package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "请求metric的参数", description = "")
public class MetricQueryDTO {

    @ApiModelProperty(value = "采集任务id")
    private Long taskId;

    @ApiModelProperty(value = "采集路径id")
    private Long logCollectPathId;

    @ApiModelProperty(value = "采集主机名")
    private String hostName;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;

    @ApiModelProperty(value = "是否查看各主机")
    private Boolean eachHost;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getLogCollectPathId() {
        return logCollectPathId;
    }

    public void setLogCollectPathId(Long logCollectPathId) {
        this.logCollectPathId = logCollectPathId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Boolean getEachHost() {
        return eachHost;
    }

    public void setEachHost(Boolean eachHost) {
        this.eachHost = eachHost;
    }
}
