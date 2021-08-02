package com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "采集文件，从指标中获取", description = "")
public class CollectFileVO {

    @ApiModelProperty(value = "是否到文件尾")
    private Boolean fileEnd;

    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "最后修改时间")
    private Long lastModifyTime;

    @ApiModelProperty(value = "日志业务时间")
    private Long logTime;

    @ApiModelProperty(value = "采集进度百分比")
    private Integer rate;

    // TODO 该字段有错别字
    @ApiModelProperty(value = "时间格式是否有效")
    @JSONField(name = "vaildTimeConfig")
    private Boolean validTimeConfig;

    @ApiModelProperty(value = "文件顺序 1乱序")
    private Integer isFileOrder;

    public Boolean getFileEnd() {
        return fileEnd;
    }

    public void setFileEnd(Boolean fileEnd) {
        this.fileEnd = fileEnd;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getLogTime() {
        return logTime;
    }

    public void setLogTime(Long logTime) {
        this.logTime = logTime;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Boolean getValidTimeConfig() {
        return validTimeConfig;
    }

    public void setValidTimeConfig(Boolean validTimeConfig) {
        this.validTimeConfig = validTimeConfig;
    }

    public Integer getIsFileOrder() {
        return isFileOrder;
    }

    public void setIsFileOrder(Integer isFileOrder) {
        this.isFileOrder = isFileOrder;
    }
}
