package com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "返回给logx的日志采集任务指标信息接口", description = "")
public class LogCollectTaskMetrics2LogxVO {

//    @ApiModelProperty(value = "logx任务对应日志采集任务数据流写入接收端topic名")
//    private String topic;
//
//    @ApiModelProperty(value = "logx任务对应日志采集任务在给定时间范围内出现的最大采集延时时长 单位：ms")
//    private Long maxDelayMs;
//
//    @ApiModelProperty(value = "logx任务对应日志采集任务当前流量 单位：byte/1min")
//    private Long currentBytesPerMinute;
//
//    @ApiModelProperty(value = "logx任务对应日志采集任务当前采集条数 单位：条/1min")
//    private Long logEventCountPerMinute;
//
//    @ApiModelProperty(value = "logx任务对应日志采集任务健康度 0：绿色 1：黄色 2：红色")
//    private Integer logCollectTaskHealthLevel;
//
//    @ApiModelProperty(value = "logx任务对应日志采集任务健康诊断信息")
//    private String logCollectTaskDiagnosticInformation;
//
//    @ApiModelProperty(value = "logx任务对应日志采集任务在给定时间范围内出现在Agent上的异常信息集")
//    private List<String> errorLogs;
//
//    @ApiModelProperty(value = "topic对应完整性时间 单位：ms")
//    private Long completion;//TODO：是否须拆为两个接口 根据topic获取该topic对应完整性时间
//
//    public String getTopic() {
//        return topic;
//    }
//
//    public void setTopic(String topic) {
//        this.topic = topic;
//    }
//
//    public Long getMaxDelayMs() {
//        return maxDelayMs;
//    }
//
//    public void setMaxDelayMs(Long maxDelayMs) {
//        this.maxDelayMs = maxDelayMs;
//    }
//
//    public Long getCurrentBytesPerMinute() {
//        return currentBytesPerMinute;
//    }
//
//    public void setCurrentBytesPerMinute(Long currentBytesPerMinute) {
//        this.currentBytesPerMinute = currentBytesPerMinute;
//    }
//
//    public Long getLogEventCountPerMinute() {
//        return logEventCountPerMinute;
//    }
//
//    public void setLogEventCountPerMinute(Long logEventCountPerMinute) {
//        this.logEventCountPerMinute = logEventCountPerMinute;
//    }
//
//    public Integer getLogCollectTaskHealthLevel() {
//        return logCollectTaskHealthLevel;
//    }
//
//    public void setLogCollectTaskHealthLevel(Integer logCollectTaskHealthLevel) {
//        this.logCollectTaskHealthLevel = logCollectTaskHealthLevel;
//    }
//
//    public String getLogCollectTaskDiagnosticInformation() {
//        return logCollectTaskDiagnosticInformation;
//    }
//
//    public void setLogCollectTaskDiagnosticInformation(String logCollectTaskDiagnosticInformation) {
//        this.logCollectTaskDiagnosticInformation = logCollectTaskDiagnosticInformation;
//    }
//
//    public List<String> getErrorLogs() {
//        return errorLogs;
//    }
//
//    public void setErrorLogs(List<String> errorLogs) {
//        this.errorLogs = errorLogs;
//    }
//
//    public Long getCompletion() {
//        return completion;
//    }
//
//    public void setCompletion(Long completion) {
//        this.completion = completion;
//    }

    @ApiModelProperty(value = "日志采集任务延时时长，如日志采集任务含多个采集路径，取延时最大的采集路径对应延时时长 单位：ms")
    private Long collectDalayMs;

    public Long getCollectDalayMs() {
        return collectDalayMs;
    }

    public void setCollectDalayMs(Long collectDalayMs) {
        this.collectDalayMs = collectDalayMs;
    }

}
