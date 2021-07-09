package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.logx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "LogCollectTaskUpdateDTO", description = "待修改日志采集任务")
public class LogCollectTaskUpdateDTO {

    @ApiModelProperty(value = "日志采集任务id 添加时不填，更新时必填")
    private Long id;

    @ApiModelProperty(value = "日志采集任务名")
    private String logCollectTaskName;

    @ApiModelProperty(value = "夜莺侧服务 id 值")
    private Long serviceId;

    @ApiModelProperty(value = "采集任务采集的日志需要发往的topic名")
    private String sendTopic;

    @ApiModelProperty(value = "采集任务采集的日志需要发往的对应Kafka集群信息id")
    private Long kafkaClusterId;

    @ApiModelProperty(value = "文件类型采集路径集")
    private List<FileLogCollectPathUpdateDTO> fileLogCollectPathList;

//    @ApiModelProperty(value = "目录类型采集路径集")
//    private List<DirectoryLogCollectPathCreateDTO> directoryLogCollectPathList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogCollectTaskName() {
        return logCollectTaskName;
    }

    public void setLogCollectTaskName(String logCollectTaskName) {
        this.logCollectTaskName = logCollectTaskName;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getSendTopic() {
        return sendTopic;
    }

    public void setSendTopic(String sendTopic) {
        this.sendTopic = sendTopic;
    }

    public Long getKafkaClusterId() {
        return kafkaClusterId;
    }

    public void setKafkaClusterId(Long kafkaClusterId) {
        this.kafkaClusterId = kafkaClusterId;
    }

    public List<FileLogCollectPathUpdateDTO> getFileLogCollectPathList() {
        return fileLogCollectPathList;
    }

    public void setFileLogCollectPathList(List<FileLogCollectPathUpdateDTO> fileLogCollectPathList) {
        this.fileLogCollectPathList = fileLogCollectPathList;
    }

//    public List<DirectoryLogCollectPathCreateDTO> getDirectoryLogCollectPathList() {
//        return directoryLogCollectPathList;
//    }
//
//    public void setDirectoryLogCollectPathList(List<DirectoryLogCollectPathCreateDTO> directoryLogCollectPathList) {
//        this.directoryLogCollectPathList = directoryLogCollectPathList;
//    }

}
