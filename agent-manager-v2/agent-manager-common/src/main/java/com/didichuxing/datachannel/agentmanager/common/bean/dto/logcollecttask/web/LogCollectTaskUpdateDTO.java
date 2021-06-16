package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web;

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

    @ApiModelProperty(value = "日志采集任务备注")
    private String logCollectTaskRemark;

    @ApiModelProperty(value = "采集服务集")
    private List<Long> serviceIdList;

    @ApiModelProperty(value = "采集任务类型 0：常规流式采集 1：按指定时间范围采集")
    private Integer logCollectTaskType;

    @ApiModelProperty(value = "历史数据过滤 0：不过滤 1：从当前时间开始采集 2：从自定义时间开始采集，自定义时间取collectStartBusinessTime属性值")
    private Integer oldDataFilterType;

    @ApiModelProperty(value = "日志采集任务对应采集开始业务时间 注：针对 logCollectTaskType = 1 情况，该值必填；logCollectTaskType = 0 & oldDataFilterTyp = 2 时，该值必填")
    private Long collectStartBusinessTime;

    @ApiModelProperty(value = "日志采集任务对应采集结束业务时间 注：针对 logCollectTaskType = 1 情况，该值必填；logCollectTaskType = 0 情况，该值不填")
    private Long collectEndBusinessTime;

    @ApiModelProperty(value = "采集任务限流保障优先级 0：高 1：中 2：低")
    private Integer limitPriority;

    @ApiModelProperty(value = "采集任务采集的日志需要发往的topic名")
    private String sendTopic;

    @ApiModelProperty(value = "采集任务采集的日志需要发往的对应Kafka集群信息id")
    private Long kafkaClusterId;

    @ApiModelProperty(value = "采集任务高级配置项集，为json形式字符串")
    private String advancedConfigurationJsonString;

    @ApiModelProperty(value = "主机过滤规则")
    private HostFilterRuleDTO hostFilterRuleDTO;

    @ApiModelProperty(value = "目录类型采集路径集")
    private List<DirectoryLogCollectPathUpdateDTO> directoryLogCollectPathList;

    @ApiModelProperty(value = "文件类型采集路径集")
    private List<FileLogCollectPathUpdateDTO> fileLogCollectPathList;

    @ApiModelProperty(value = "日志采集任务执行超时时间，注意：该字段仅在日志采集任务类型为类型\"按指定时间范围采集\"时才存在值")
    private Long logCollectTaskExecuteTimeoutMs;

    @ApiModelProperty(value = "日志内容过滤规则")
    private LogContentFilterRuleDTO logContentFilterLogicDTO;

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

    public String getLogCollectTaskRemark() {
        return logCollectTaskRemark;
    }

    public void setLogCollectTaskRemark(String logCollectTaskRemark) {
        this.logCollectTaskRemark = logCollectTaskRemark;
    }

    public List<Long> getServiceIdList() {
        return serviceIdList;
    }

    public void setServiceIdList(List<Long> serviceIdList) {
        this.serviceIdList = serviceIdList;
    }

    public Integer getLogCollectTaskType() {
        return logCollectTaskType;
    }

    public void setLogCollectTaskType(Integer logCollectTaskType) {
        this.logCollectTaskType = logCollectTaskType;
    }

    public Integer getOldDataFilterType() {
        return oldDataFilterType;
    }

    public void setOldDataFilterType(Integer oldDataFilterType) {
        this.oldDataFilterType = oldDataFilterType;
    }

    public Long getCollectStartBusinessTime() {
        return collectStartBusinessTime;
    }

    public void setCollectStartBusinessTime(Long collectStartBusinessTime) {
        this.collectStartBusinessTime = collectStartBusinessTime;
    }

    public Long getCollectEndBusinessTime() {
        return collectEndBusinessTime;
    }

    public void setCollectEndBusinessTime(Long collectEndBusinessTime) {
        this.collectEndBusinessTime = collectEndBusinessTime;
    }

    public Integer getLimitPriority() {
        return limitPriority;
    }

    public void setLimitPriority(Integer limitPriority) {
        this.limitPriority = limitPriority;
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

    public String getAdvancedConfigurationJsonString() {
        return advancedConfigurationJsonString;
    }

    public void setAdvancedConfigurationJsonString(String advancedConfigurationJsonString) {
        this.advancedConfigurationJsonString = advancedConfigurationJsonString;
    }

    public HostFilterRuleDTO getHostFilterRuleDTO() {
        return hostFilterRuleDTO;
    }

    public void setHostFilterRuleDTO(HostFilterRuleDTO hostFilterRuleDTO) {
        this.hostFilterRuleDTO = hostFilterRuleDTO;
    }

    public List<DirectoryLogCollectPathUpdateDTO> getDirectoryLogCollectPathList() {
        return directoryLogCollectPathList;
    }

    public void setDirectoryLogCollectPathList(List<DirectoryLogCollectPathUpdateDTO> directoryLogCollectPathList) {
        this.directoryLogCollectPathList = directoryLogCollectPathList;
    }

    public List<FileLogCollectPathUpdateDTO> getFileLogCollectPathList() {
        return fileLogCollectPathList;
    }

    public void setFileLogCollectPathList(List<FileLogCollectPathUpdateDTO> fileLogCollectPathList) {
        this.fileLogCollectPathList = fileLogCollectPathList;
    }

    public Long getLogCollectTaskExecuteTimeoutMs() {
        return logCollectTaskExecuteTimeoutMs;
    }

    public void setLogCollectTaskExecuteTimeoutMs(Long logCollectTaskExecuteTimeoutMs) {
        this.logCollectTaskExecuteTimeoutMs = logCollectTaskExecuteTimeoutMs;
    }

    public LogContentFilterRuleDTO getLogContentFilterLogicDTO() {
        return logContentFilterLogicDTO;
    }

    public void setLogContentFilterLogicDTO(LogContentFilterRuleDTO logContentFilterLogicDTO) {
        this.logContentFilterLogicDTO = logContentFilterLogicDTO;
    }
}
