package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集路径配置信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogCollectPathConfiguration {

    @ApiModelProperty(value = "文件类型日志采集路径 id")
    private Long pathId;

    @ApiModelProperty(value = "对应日志采集任务id")
    private Long logCollectTaskId;

    @ApiModelProperty(value = "待采集路径")
    private String path;

    @ApiModelProperty(value = "容器场景下，容器路径对应在宿主机路径")
    private String realPath;

    public Long getPathId() {
        return pathId;
    }

    public void setPathId(Long pathId) {
        this.pathId = pathId;
    }

    public Long getLogCollectTaskId() {
        return logCollectTaskId;
    }

    public void setLogCollectTaskId(Long logCollectTaskId) {
        this.logCollectTaskId = logCollectTaskId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }
}

