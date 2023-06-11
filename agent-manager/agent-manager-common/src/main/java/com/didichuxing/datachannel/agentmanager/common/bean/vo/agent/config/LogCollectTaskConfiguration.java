package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务配置信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogCollectTaskConfiguration {

    @ApiModelProperty(value = "日志采集任务id")
    private Long logCollectTaskId;

    @ApiModelProperty(value = "日志采集任务类型 0：常规流式采集 1：按指定时间范围采集")
    private Integer logCollectTaskType;

    @ApiModelProperty(value = "历史数据过滤类型 0：不过滤 1：从当前时间开始采集 2：从自定义时间开始采集，自定义时间取collectStartBusinessTime属性值")
    private Integer oldDataFilterType;

    @ApiModelProperty(value = "日志采集任务对应采集开始业务时间 注：针对 logCollectTaskType = 1 情况，该值必填；logCollectTaskType = 0 情况，该值不填")
    private Long collectStartTimeBusiness;

    @ApiModelProperty(value = "日志采集任务对应采集结束业务时间 注：针对 logCollectTaskType = 1 情况，该值必填；logCollectTaskType = 0 情况，该值不填")
    private Long collectEndTimeBusiness;

    @ApiModelProperty(value = "日志采集任务限流保障优先级 0：高 1：中 2：低")
    private Integer limitPriority;

    @ApiModelProperty(value = "日志采集任务状态 0：暂停 1：运行")
    private Integer logCollectTaskStatus;

    @ApiModelProperty(value = "日志采集任务配置版本号")
    private Integer configurationVersion;

    @ApiModelProperty(value = "日志采集任务高级配置项集，为json形式字符串")
    private String advancedConfigurationJsonString;

    @ApiModelProperty(value = "日志采集任务采集的日志需要发往的Receiver端生产者配置")
    private ReceiverConfigDO logReceiverConfigDO;

    @ApiModelProperty(value = "日志采集任务关联的文件类型采集路径集")
    private List<FileLogCollectPathConfigDO> fileLogCollectPathList;

    @ApiModelProperty(value = "日志内容过滤规则信息（存储 BaseLogContentFilterRuleLogic 某具体实现类的 json 化形式）")
    private String logContentFilterRuleLogicJsonString;

    @ApiModelProperty(value = "日志内容切片规则信息（存储 BaseLogContentSliceRuleLogic 某具体实现类的 json 化形式）")
    private String logContentSliceRuleLogicJsonString;

    @ApiModelProperty(value = "日志内容切片规则信息（存储 BaseLogContentSliceRuleLogic 某具体实现类的 json 化形式）")
    private String serviceNames;

    @ApiModelProperty(value = "待采集文件后缀匹配规则信息（存储 BaseCollectFileSuffixMatchRuleLogic 某具体实现类的 json 化形式）")
    private String fileNameSuffixMatchRuleLogicJsonString;

    public String getFileNameSuffixMatchRuleLogicJsonString() {
        return fileNameSuffixMatchRuleLogicJsonString;
    }

    public void setFileNameSuffixMatchRuleLogicJsonString(String fileNameSuffixMatchRuleLogicJsonString) {
        this.fileNameSuffixMatchRuleLogicJsonString = fileNameSuffixMatchRuleLogicJsonString;
    }

    public String getLogContentFilterRuleLogicJsonString() {
        return logContentFilterRuleLogicJsonString;
    }

    public void setLogContentFilterRuleLogicJsonString(String logContentFilterRuleLogicJsonString) {
        this.logContentFilterRuleLogicJsonString = logContentFilterRuleLogicJsonString;
    }

    public Long getLogCollectTaskId() {
        return logCollectTaskId;
    }

    public void setLogCollectTaskId(Long logCollectTaskId) {
        this.logCollectTaskId = logCollectTaskId;
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

    public Long getCollectStartTimeBusiness() {
        return collectStartTimeBusiness;
    }

    public void setCollectStartTimeBusiness(Long collectStartTimeBusiness) {
        this.collectStartTimeBusiness = collectStartTimeBusiness;
    }

    public Long getCollectEndTimeBusiness() {
        return collectEndTimeBusiness;
    }

    public void setCollectEndTimeBusiness(Long collectEndTimeBusiness) {
        this.collectEndTimeBusiness = collectEndTimeBusiness;
    }

    public Integer getLimitPriority() {
        return limitPriority;
    }

    public void setLimitPriority(Integer limitPriority) {
        this.limitPriority = limitPriority;
    }

    public Integer getLogCollectTaskStatus() {
        return logCollectTaskStatus;
    }

    public void setLogCollectTaskStatus(Integer logCollectTaskStatus) {
        this.logCollectTaskStatus = logCollectTaskStatus;
    }

    public Integer getConfigurationVersion() {
        return configurationVersion;
    }

    public void setConfigurationVersion(Integer configurationVersion) {
        this.configurationVersion = configurationVersion;
    }

    public String getAdvancedConfigurationJsonString() {
        return advancedConfigurationJsonString;
    }

    public void setAdvancedConfigurationJsonString(String advancedConfigurationJsonString) {
        this.advancedConfigurationJsonString = advancedConfigurationJsonString;
    }

    public ReceiverConfigDO getLogProducerConfiguration() {
        return logReceiverConfigDO;
    }

    public void setLogProducerConfiguration(ReceiverConfigDO logReceiverConfigDO) {
        this.logReceiverConfigDO = logReceiverConfigDO;
    }

    public List<FileLogCollectPathConfigDO> getFileLogCollectPathList() {
        return fileLogCollectPathList;
    }

    public void setFileLogCollectPathList(List<FileLogCollectPathConfigDO> fileLogCollectPathList) {
        this.fileLogCollectPathList = fileLogCollectPathList;
    }

    public String getLogContentSliceRuleLogicJsonString() {
        return logContentSliceRuleLogicJsonString;
    }

    public void setLogContentSliceRuleLogicJsonString(String logContentSliceRuleLogicJsonString) {
        this.logContentSliceRuleLogicJsonString = logContentSliceRuleLogicJsonString;
    }

    public String getServiceNames() {
        return serviceNames;
    }

    public void setServiceNames(String serviceNames) {
        this.serviceNames = serviceNames;
    }
}
