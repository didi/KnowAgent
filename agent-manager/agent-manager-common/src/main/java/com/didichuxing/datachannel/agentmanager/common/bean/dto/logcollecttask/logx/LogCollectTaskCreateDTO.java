package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.logx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "LogCollectTaskCreateDTO", description = "待添加日志采集任务")
public class LogCollectTaskCreateDTO {

    @ApiModelProperty(value = "日志采集任务名")
    private String logCollectTaskName;

    @ApiModelProperty(value = "夜莺侧服务 id 值")
    private Long serviceId;

    @ApiModelProperty(value = "采集任务采集的日志需要发往的topic名")
    private String sendTopic;

    @ApiModelProperty(value = "采集任务采集的日志需要发往的对应Kafka集群信息id")
    private Long kafkaClusterId;

    @ApiModelProperty(value = "文件类型采集路径集")
    private List<FileLogCollectPathCreateDTO> fileLogCollectPathList;

//    @ApiModelProperty(value = "目录类型采集路径集")
//    private List<DirectoryLogCollectPathCreateDTO> directoryLogCollectPathList;

    public String getLogCollectTaskName() {
        return logCollectTaskName;
    }

    public void setLogCollectTaskName(String logCollectTaskName) {
        this.logCollectTaskName = logCollectTaskName;
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

    public List<FileLogCollectPathCreateDTO> getFileLogCollectPathList() {
        return fileLogCollectPathList;
    }

    public void setFileLogCollectPathList(List<FileLogCollectPathCreateDTO> fileLogCollectPathList) {
        this.fileLogCollectPathList = fileLogCollectPathList;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

//    public List<DirectoryLogCollectPathCreateDTO> getDirectoryLogCollectPathList() {
//        return directoryLogCollectPathList;
//    }
//
//    public void setDirectoryLogCollectPathList(List<DirectoryLogCollectPathCreateDTO> directoryLogCollectPathList) {
//        this.directoryLogCollectPathList = directoryLogCollectPathList;
//    }
}
