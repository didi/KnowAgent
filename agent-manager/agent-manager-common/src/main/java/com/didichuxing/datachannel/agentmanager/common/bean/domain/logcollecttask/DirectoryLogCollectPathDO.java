package com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask;

import lombok.Data;

/**
 * @author huqidong
 * @date 2020-09-21
 * 目录类型日志采集路径
 */
@Data
public class DirectoryLogCollectPathDO extends LogCollectPathDO {

    /**
     * 采集文件筛选正则集 pipeline json 形式字符串，集合中每一项为一个过滤正则项< filterRegular , type >，filterRegular表示过滤正则内容，type表示黑/白名单类型0：白名单 1：黑名单
     * 注：FilterRegular 须有序存储，过滤时按集合顺序进行过滤计算
     */
    private String collectFilesFilterRegularPipelineJsonString;
    /**
     * 目录采集深度
     */
    private Integer directoryCollectDepth;

    public String getCollectFilesFilterRegularPipelineJsonString() {
        return collectFilesFilterRegularPipelineJsonString;
    }

    public void setCollectFilesFilterRegularPipelineJsonString(String collectFilesFilterRegularPipelineJsonString) {
        this.collectFilesFilterRegularPipelineJsonString = collectFilesFilterRegularPipelineJsonString == null ? null : collectFilesFilterRegularPipelineJsonString.trim();
    }

    public Integer getDirectoryCollectDepth() {
        return directoryCollectDepth;
    }

    public void setDirectoryCollectDepth(Integer directoryCollectDepth) {
        this.directoryCollectDepth = directoryCollectDepth;
    }

}