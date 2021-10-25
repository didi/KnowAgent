package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huqidong
 * @date 2020-09-21
 * 目录类型日志采集路径配置信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectoryLogCollectPathConfigDO extends LogCollectPathConfigDO {

    @ApiModelProperty(value = "采集文件筛选正则集 pipeline json 形式字符串，集合中每一项为一个过滤正则项< filterRegular , type >，filterRegular表示过滤正则内容，type表示黑/白名单类型0：白名单 1：黑名单 注：FilterRegular 须有序存储，过滤时按集合顺序进行过滤计算")
    private String collectFilesFilterRegularPipelineJsonString;

    @ApiModelProperty(value = "目录采集深度")
    private Integer directoryCollectDepth;

    public void setDirectoryCollectDepth(Integer directoryCollectDepth) {
        this.directoryCollectDepth = directoryCollectDepth;
    }

    public void setCollectFilesFilterRegularPipelineJsonString(String collectFilesFilterRegularPipelineJsonString) {
        this.collectFilesFilterRegularPipelineJsonString = collectFilesFilterRegularPipelineJsonString;
    }

    public String getCollectFilesFilterRegularPipelineJsonString() {
        return collectFilesFilterRegularPipelineJsonString;
    }

    public Integer getDirectoryCollectDepth() {
        return directoryCollectDepth;
    }
}
