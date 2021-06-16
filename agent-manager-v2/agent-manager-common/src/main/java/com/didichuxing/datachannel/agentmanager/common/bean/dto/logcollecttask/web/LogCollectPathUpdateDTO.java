package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "日志采集路径配置", description = "")
public class LogCollectPathUpdateDTO {

    @ApiModelProperty(value = "待采集文件字符集")
    private String charset;

    @ApiModelProperty(value = "待采集路径")
    private String path;

    @ApiModelProperty(value = "待采集文件 offset 有效期 单位：ms 注：待采集文件自最后一次写入时间 ~ 当前时间间隔 > fdOffset时，采集端将删除其维护的该文件对应 offset 信息，如此时，该文件仍存在于待采集目录下，将被重新采集")
    private Long fdOffsetExpirationTimeMs;

    @ApiModelProperty(value = "单个日志切片最大大小 单位：字节 注：单个日志切片大小超过该值后，采集端将以该值进行截断采集")
    private Long maxBytesPerLogEvent;

    @ApiModelProperty(value = "日志切片规则")
    private LogSliceRuleDTO logSliceRuleDTO;

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFdOffsetExpirationTimeMs(Long fdOffsetExpirationTimeMs) {
        this.fdOffsetExpirationTimeMs = fdOffsetExpirationTimeMs;
    }

    public void setMaxBytesPerLogEvent(Long maxBytesPerLogEvent) {
        this.maxBytesPerLogEvent = maxBytesPerLogEvent;
    }

    public void setLogSliceRuleDTO(LogSliceRuleDTO logSliceRuleDTO) {
        this.logSliceRuleDTO = logSliceRuleDTO;
    }

    public String getCharset() {
        return charset;
    }

    public String getPath() {
        return path;
    }

    public Long getFdOffsetExpirationTimeMs() {
        return fdOffsetExpirationTimeMs;
    }

    public Long getMaxBytesPerLogEvent() {
        return maxBytesPerLogEvent;
    }

    public LogSliceRuleDTO getLogSliceRuleDTO() {
        return logSliceRuleDTO;
    }

}
