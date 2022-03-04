package com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask;

import com.didichuxing.datachannel.agentmanager.common.bean.vo.receiver.ReceiverVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.service.ServiceVO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LogCollectTaskPaginationRecordVO {

    @ApiModelProperty(value = "日志采集任务id", notes="")
    private Long logCollectTaskId;

    @ApiModelProperty(value = "日志采集任务名")
    private String logCollectTaskName;

    @ApiModelProperty(value = "采集任务类型 0：常规流式采集 1：按指定时间范围采集")
    private Integer logCollectTaskType;

    @ApiModelProperty(value = "采集服务集")
    private List<ServiceVO> serviceList;

    @ApiModelProperty(value = "接收端集群", notes="")
    private ReceiverVO receiverVO;

    @ApiModelProperty(value = "接收端topic", notes="")
    private String receiverTopic;

    @ApiModelProperty(value = "日志采集任务健康度 ", notes="")
    private Integer logCollectTaskHealthLevel;

    @ApiModelProperty(value = "日志采集任务健康描述信息", notes="")
    private String logCollectTaskHealthDescription;

    @ApiModelProperty(value = "日志采集任务巡检结果类型", notes="")
    private Integer logCollectTaskHealthInspectionResultType;

    @ApiModelProperty(value = "日志采集任务创建时间 格式：unix 13 位时间戳", notes="")
    private Long logCollectTaskCreateTime;

    @ApiModelProperty(value = "日志采集任务结束时间 格式：unix 13 位时间戳 注：仅针对logCollectTaskType为1的日志采集任务", notes="")
    private Long logCollectTaskFinishTime;

    @ApiModelProperty(value = "日志采集任务状态 0：暂停 1：运行 2：已完成（状态2仅针对 \"按指定时间范围采集\" 类型）", notes="")
    private Integer logCollectTaskStatus;

    public Integer getLogCollectTaskStatus() {
        return logCollectTaskStatus;
    }

    public void setLogCollectTaskStatus(Integer logCollectTaskStatus) {
        this.logCollectTaskStatus = logCollectTaskStatus;
    }

    public void setLogCollectTaskId(Long logCollectTaskId) {
        this.logCollectTaskId = logCollectTaskId;
    }

    public void setLogCollectTaskName(String logCollectTaskName) {
        this.logCollectTaskName = logCollectTaskName;
    }

    public void setLogCollectTaskType(Integer logCollectTaskType) {
        this.logCollectTaskType = logCollectTaskType;
    }

    public void setServiceList(List<ServiceVO> serviceList) {
        this.serviceList = serviceList;
    }

    public void setReceiverVO(ReceiverVO receiverVO) {
        this.receiverVO = receiverVO;
    }

    public void setReceiverTopic(String receiverTopic) {
        this.receiverTopic = receiverTopic;
    }

    public void setLogCollectTaskHealthLevel(Integer logCollectTaskHealthLevel) {
        this.logCollectTaskHealthLevel = logCollectTaskHealthLevel;
    }

    public void setLogCollectTaskCreateTime(Long logCollectTaskCreateTime) {
        this.logCollectTaskCreateTime = logCollectTaskCreateTime;
    }

    public void setLogCollectTaskFinishTime(Long logCollectTaskFinishTime) {
        this.logCollectTaskFinishTime = logCollectTaskFinishTime;
    }

    public Long getLogCollectTaskId() {
        return logCollectTaskId;
    }

    public String getLogCollectTaskHealthDescription() {
        return logCollectTaskHealthDescription;
    }

    public void setLogCollectTaskHealthDescription(String logCollectTaskHealthDescription) {
        this.logCollectTaskHealthDescription = logCollectTaskHealthDescription;
    }

    public Integer getLogCollectTaskHealthInspectionResultType() {
        return logCollectTaskHealthInspectionResultType;
    }

    public void setLogCollectTaskHealthInspectionResultType(Integer logCollectTaskHealthInspectionResultType) {
        this.logCollectTaskHealthInspectionResultType = logCollectTaskHealthInspectionResultType;
    }

    public String getLogCollectTaskName() {
        return logCollectTaskName;
    }

    public Integer getLogCollectTaskType() {
        return logCollectTaskType;
    }

    public List<ServiceVO> getServiceList() {
        return serviceList;
    }

    public ReceiverVO getReceiverVO() {
        return receiverVO;
    }

    public String getReceiverTopic() {
        return receiverTopic;
    }

    public Integer getLogCollectTaskHealthLevel() {
        return logCollectTaskHealthLevel;
    }

    public Long getLogCollectTaskCreateTime() {
        return logCollectTaskCreateTime;
    }

    public Long getLogCollectTaskFinishTime() {
        return logCollectTaskFinishTime;
    }
}
