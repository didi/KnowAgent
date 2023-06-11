package com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask;

import com.didichuxing.datachannel.agentmanager.common.bean.vo.receiver.ReceiverVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.service.ServiceVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.host.HostFilterRuleVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "LogCollectTaskVO", description = "一个日志采集任务")
public class LogCollectTaskVO {

    @ApiModelProperty(value = "日志采集任务id 添加时不填，更新时必填")
    private Long id;

    @ApiModelProperty(value = "日志采集任务名")
    private String logCollectTaskName;

    @ApiModelProperty(value = "日志采集任务备注")
    private String logCollectTaskRemark;

    @ApiModelProperty(value = "采集服务集")
    private List<ServiceVO> services;

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

    @ApiModelProperty(value = "日志采集任务状态 0：暂停 1：运行 2：已完成（状态2仅针对 \"按指定时间范围采集\" 类型）")
    private Integer logCollectTaskStatus;

    @ApiModelProperty(value = "采集任务采集的日志需要发往的topic名")
    private String sendTopic;

    @ApiModelProperty(value = "采集任务采集的日志需要发往的接收端信息")
    private ReceiverVO receiver;

    @ApiModelProperty(value = "采集任务高级配置项集，为json形式字符串")
    private String advancedConfigurationJsonString;

    @ApiModelProperty(value = "主机过滤规则")
    private HostFilterRuleVO hostFilterRuleVO;

    @ApiModelProperty(value = "目录类型采集路径集")
    private List<DirectoryLogCollectPathVO> directoryLogCollectPathList;

    @ApiModelProperty(value = "文件类型采集路径集")
    private List<FileLogCollectPathVO> fileLogCollectPathList;

    @ApiModelProperty(value = "日志采集任务执行超时时间，注意：该字段仅在日志采集任务类型为类型\"按指定时间范围采集\"时才存在值")
    private Long logCollectTaskExecuteTimeoutMs;

    @ApiModelProperty(value = "日志内容过滤规则")
    private LogContentFilterRuleVO logContentFilterRuleVO;

    @ApiModelProperty(value = "日志采集任务执行完成时间 注：仅日志采集任务为时间范围采集类型时")
    private Long logCollectTaskFinishTime;

    @ApiModelProperty(value = "日志采集任务健康度", notes="")
    private Integer logCollectTaskHealthLevel;

    @ApiModelProperty(value = "日志采集任务健康描述信息", notes="")
    private String logCollectTaskHealthDescription;

    @ApiModelProperty(value = "日志采集任务创建人", notes="")
    private String logCollectTaskCreator;

    @ApiModelProperty(value = "kafka生产端属性", notes="")
    private String kafkaProducerConfiguration;

    @ApiModelProperty(value = "日志切片规则", notes="")
    private LogSliceRuleVO logContentSliceRule;

    @ApiModelProperty(value = "待采集文件后缀匹配规则信息", notes="")
    private FileNameSuffixMatchRuleVO fileNameSuffixMatchRule;

    @ApiModelProperty(value = "采集延迟监控阈值", notes="")
    private Long collectDelayThresholdMs;

    @ApiModelProperty(value = "日志采集任务关联 agent 数", notes="")
    private Integer relateAgentNum;

    public Integer getLogCollectTaskHealthLevel() {
        return logCollectTaskHealthLevel;
    }

    public void setLogCollectTaskHealthLevel(Integer logCollectTaskHealthLevel) {
        this.logCollectTaskHealthLevel = logCollectTaskHealthLevel;
    }

    public String getLogCollectTaskCreator() {
        return logCollectTaskCreator;
    }

    public void setLogCollectTaskCreator(String logCollectTaskCreator) {
        this.logCollectTaskCreator = logCollectTaskCreator;
    }

    public Integer getRelateAgentNum() {
        return relateAgentNum;
    }

    public void setRelateAgentNum(Integer relateAgentNum) {
        this.relateAgentNum = relateAgentNum;
    }

    public Long getLogCollectTaskExecuteTimeoutMs() {
        return logCollectTaskExecuteTimeoutMs;
    }

    public void setLogCollectTaskExecuteTimeoutMs(Long logCollectTaskExecuteTimeoutMs) {
        this.logCollectTaskExecuteTimeoutMs = logCollectTaskExecuteTimeoutMs;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLogCollectTaskName(String logCollectTaskName) {
        this.logCollectTaskName = logCollectTaskName;
    }

    public void setLogCollectTaskRemark(String logCollectTaskRemark) {
        this.logCollectTaskRemark = logCollectTaskRemark;
    }

    public void setServices(List<ServiceVO> services) {
        this.services = services;
    }

    public void setLogCollectTaskType(Integer logCollectTaskType) {
        this.logCollectTaskType = logCollectTaskType;
    }

    public void setOldDataFilterType(Integer oldDataFilterType) {
        this.oldDataFilterType = oldDataFilterType;
    }

    public void setCollectStartBusinessTime(Long collectStartBusinessTime) {
        this.collectStartBusinessTime = collectStartBusinessTime;
    }

    public void setCollectEndBusinessTime(Long collectEndBusinessTime) {
        this.collectEndBusinessTime = collectEndBusinessTime;
    }

    public void setLimitPriority(Integer limitPriority) {
        this.limitPriority = limitPriority;
    }

    public void setSendTopic(String sendTopic) {
        this.sendTopic = sendTopic;
    }

    public void setReceiver(ReceiverVO receiver) {
        this.receiver = receiver;
    }

    public void setAdvancedConfigurationJsonString(String advancedConfigurationJsonString) {
        this.advancedConfigurationJsonString = advancedConfigurationJsonString;
    }

    public void setLogContentFilterRuleVO(LogContentFilterRuleVO logContentFilterRuleVO) {
        this.logContentFilterRuleVO = logContentFilterRuleVO;
    }

    public void setHostFilterRuleVO(HostFilterRuleVO hostFilterRuleVO) {
        this.hostFilterRuleVO = hostFilterRuleVO;
    }

    public void setDirectoryLogCollectPathList(List<DirectoryLogCollectPathVO> directoryLogCollectPathList) {
        this.directoryLogCollectPathList = directoryLogCollectPathList;
    }

    public void setFileLogCollectPathList(List<FileLogCollectPathVO> fileLogCollectPathList) {
        this.fileLogCollectPathList = fileLogCollectPathList;
    }

    public Long getId() {
        return id;
    }

    public String getLogCollectTaskName() {
        return logCollectTaskName;
    }

    public String getLogCollectTaskRemark() {
        return logCollectTaskRemark;
    }

    public List<ServiceVO> getServices() {
        return services;
    }

    public Integer getLogCollectTaskType() {
        return logCollectTaskType;
    }

    public Integer getOldDataFilterType() {
        return oldDataFilterType;
    }

    public Long getCollectStartBusinessTime() {
        return collectStartBusinessTime;
    }

    public Long getCollectEndBusinessTime() {
        return collectEndBusinessTime;
    }

    public Integer getLimitPriority() {
        return limitPriority;
    }

    public String getSendTopic() {
        return sendTopic;
    }

    public ReceiverVO getReceiver() {
        return receiver;
    }

    public String getAdvancedConfigurationJsonString() {
        return advancedConfigurationJsonString;
    }

    public LogContentFilterRuleVO getLogContentFilterRuleVO() {
        return logContentFilterRuleVO;
    }

    public HostFilterRuleVO getHostFilterRuleVO() {
        return hostFilterRuleVO;
    }

    public List<DirectoryLogCollectPathVO> getDirectoryLogCollectPathList() {
        return directoryLogCollectPathList;
    }

    public List<FileLogCollectPathVO> getFileLogCollectPathList() {
        return fileLogCollectPathList;
    }

    public Integer getLogCollectTaskStatus() {
        return logCollectTaskStatus;
    }

    public void setLogCollectTaskStatus(Integer logCollectTaskStatus) {
        this.logCollectTaskStatus = logCollectTaskStatus;
    }

    public Long getLogCollectTaskFinishTime() {
        return logCollectTaskFinishTime;
    }

    public void setLogCollectTaskFinishTime(Long logCollectTaskFinishTime) {
        this.logCollectTaskFinishTime = logCollectTaskFinishTime;
    }

    public String getKafkaProducerConfiguration() {
        return kafkaProducerConfiguration;
    }

    public void setKafkaProducerConfiguration(String kafkaProducerConfiguration) {
        this.kafkaProducerConfiguration = kafkaProducerConfiguration;
    }

    public LogSliceRuleVO getLogContentSliceRule() {
        return logContentSliceRule;
    }

    public void setLogContentSliceRule(LogSliceRuleVO logContentSliceRule) {
        this.logContentSliceRule = logContentSliceRule;
    }

    public FileNameSuffixMatchRuleVO getFileNameSuffixMatchRule() {
        return fileNameSuffixMatchRule;
    }

    public void setFileNameSuffixMatchRule(FileNameSuffixMatchRuleVO fileNameSuffixMatchRule) {
        this.fileNameSuffixMatchRule = fileNameSuffixMatchRule;
    }

    public Long getCollectDelayThresholdMs() {
        return collectDelayThresholdMs;
    }

    public void setCollectDelayThresholdMs(Long collectDelayThresholdMs) {
        this.collectDelayThresholdMs = collectDelayThresholdMs;
    }

    public String getLogCollectTaskHealthDescription() {
        return logCollectTaskHealthDescription;
    }

    public void setLogCollectTaskHealthDescription(String logCollectTaskHealthDescription) {
        this.logCollectTaskHealthDescription = logCollectTaskHealthDescription;
    }
}
