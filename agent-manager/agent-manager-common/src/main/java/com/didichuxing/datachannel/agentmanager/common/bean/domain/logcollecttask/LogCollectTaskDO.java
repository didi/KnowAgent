package com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.BaseDO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务
 */
@Data
public class LogCollectTaskDO extends BaseDO {

    /**
     * 日志采集任务唯一标识
     */
    private Long id;
    /**
     * 采集任务名
     */
    private String logCollectTaskName;
    /**
     * 采集任务备注
     */
    private String logCollectTaskRemark;
    /**
     * 采集任务需要部署到的服务对象id集
     */
    private List<Long> serviceIdList;
    /**
     * 采集任务类型 0：常规流式采集 1：按指定时间范围采集
     */
    private Integer logCollectTaskType;
    /*
     * 历史数据过滤 0：不过滤 1：从当前时间开始采集 2：从自定义时间开始采集，自定义时间取collectStartBusinessTime属性值
     */
    private Integer oldDataFilterType;
    /**
     * 采集任务对应采集开始业务时间
     * 注：针对 logCollectTaskType = 1 情况，该值必填；logCollectTaskType = 0 情况，该值不填
     */
    private Long collectStartTimeBusiness;
    /**
     * 采集任务对应采集结束业务时间
     * 注：针对 logCollectTaskType = 1 情况，该值必填；logCollectTaskType = 0 情况，该值不填
     */
    private Long collectEndTimeBusiness;
    /**
     * 采集任务限流保障优先级 0：高 1：中 2：低
     */
    private Integer limitPriority;
    /**
     * 日志采集任务状态 0：暂停 1：运行 2：已完成（状态2仅针对 "按指定时间范围采集" 类型）
     */
    private Integer logCollectTaskStatus;
    /**
     * 采集任务采集的日志需要发往的topic名
     */
    private String sendTopic;
    /**
     * 对应Kafka集群信息id
     */
    private Long kafkaClusterId;
    /**
     * 采集任务高级配置项集，为json形式字符串
     */
    private String advancedConfigurationJsonString;
    /**
     * 主机过滤规则信息（存储 BaseHostFilterRuleLogic 某具体实现类的 json 化形式）
     */
    private String hostFilterRuleLogicJsonString;
    /**
     * 目录类型采集路径集
     */
    private List<DirectoryLogCollectPathDO> directoryLogCollectPathList;
    /**
     * 文件类型采集路径集
     */
    private List<FileLogCollectPathDO> fileLogCollectPathList;
    /**
     * 日志采集任务配置版本号
     */
    private Integer configurationVersion;
    /**
     * 日志采集任务执行超时时间，注意：该字段仅在日志采集任务类型为类型"按指定时间范围采集"时才存在值
     */
    private Long logCollectTaskExecuteTimeoutMs;
    /**
     * 日志内容过滤规则信息（存储 BaseLogContentFilterRuleLogic 某具体实现类的 json 化形式）
     */
    private String logContentFilterRuleLogicJsonString;
    /**
     * 日志采集任务执行完成时间
     * 注：仅日志采集任务为时间范围采集类型时
     */
    private Date logCollectTaskFinishTime;
    /**
     * kafka 生产端属性，如该字段存在值，则用该字段值替换 kafkaClusterId 对应 kafka cluster 对应全局 kafkaProducerConfiguration 字段值
     */
    private String kafkaProducerConfiguration;
    /**
     * 日志内容切片规则信息（存储 BaseLogContentSliceRuleLogic 某具体实现类的 json 化形式）
     */
    private String logContentSliceRuleLogicJsonString;
    /**
     * 待采集文件后缀匹配规则信息（存储 BaseCollectFileSuffixMatchRuleLogic 某具体实现类的 json 化形式）
     */
    private String fileNameSuffixMatchRuleLogicJsonString;
    /**
     * 该路径的日志对应采集延迟监控阈值 单位：ms，该阈值表示：该采集路径对应到所有待采集主机上正在采集的业务时间最小值 ~ 当前时间间隔
     */
    private Long collectDelayThresholdMs;

    private Integer relateAgentNum;

    public Date getLogCollectTaskFinishTime() {
        return logCollectTaskFinishTime;
    }

    public void setLogCollectTaskFinishTime(Date logCollectTaskFinishTime) {
        this.logCollectTaskFinishTime = logCollectTaskFinishTime;
    }

    public String getLogContentFilterRuleLogicJsonString() {
        return logContentFilterRuleLogicJsonString;
    }

    public void setLogContentFilterRuleLogicJsonString(String logContentFilterRuleLogicJsonString) {
        this.logContentFilterRuleLogicJsonString = logContentFilterRuleLogicJsonString;
    }

    public Long getLogCollectTaskExecuteTimeoutMs() {
        return logCollectTaskExecuteTimeoutMs;
    }

    public void setLogCollectTaskExecuteTimeoutMs(Long logCollectTaskExecuteTimeoutMs) {
        this.logCollectTaskExecuteTimeoutMs = logCollectTaskExecuteTimeoutMs;
    }

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

    public String getHostFilterRuleLogicJsonString() {
        return hostFilterRuleLogicJsonString;
    }

    public void setHostFilterRuleLogicJsonString(String hostFilterRuleLogicJsonString) {
        this.hostFilterRuleLogicJsonString = hostFilterRuleLogicJsonString;
    }

    public List<DirectoryLogCollectPathDO> getDirectoryLogCollectPathList() {
        return directoryLogCollectPathList;
    }

    public void setDirectoryLogCollectPathList(List<DirectoryLogCollectPathDO> directoryLogCollectPathList) {
        this.directoryLogCollectPathList = directoryLogCollectPathList;
    }

    public List<FileLogCollectPathDO> getFileLogCollectPathList() {
        return fileLogCollectPathList;
    }

    public void setFileLogCollectPathList(List<FileLogCollectPathDO> fileLogCollectPathList) {
        this.fileLogCollectPathList = fileLogCollectPathList;
    }

    public Integer getConfigurationVersion() {
        return configurationVersion;
    }

    public void setConfigurationVersion(Integer configurationVersion) {
        this.configurationVersion = configurationVersion;
    }

    public String getKafkaProducerConfiguration() {
        return kafkaProducerConfiguration;
    }

    public void setKafkaProducerConfiguration(String kafkaProducerConfiguration) {
        this.kafkaProducerConfiguration = kafkaProducerConfiguration;
    }

    public String getLogContentSliceRuleLogicJsonString() {
        return logContentSliceRuleLogicJsonString;
    }

    public void setLogContentSliceRuleLogicJsonString(String logContentSliceRuleLogicJsonString) {
        this.logContentSliceRuleLogicJsonString = logContentSliceRuleLogicJsonString;
    }

    public String getFileNameSuffixMatchRuleLogicJsonString() {
        return fileNameSuffixMatchRuleLogicJsonString;
    }

    public void setFileNameSuffixMatchRuleLogicJsonString(String fileNameSuffixMatchRuleLogicJsonString) {
        this.fileNameSuffixMatchRuleLogicJsonString = fileNameSuffixMatchRuleLogicJsonString;
    }

    public Long getCollectDelayThresholdMs() {
        return collectDelayThresholdMs;
    }

    public void setCollectDelayThresholdMs(Long collectDelayThresholdMs) {
        this.collectDelayThresholdMs = collectDelayThresholdMs;
    }

    public Integer getRelateAgentNum() {
        return relateAgentNum;
    }

    public void setRelateAgentNum(Integer relateAgentNum) {
        this.relateAgentNum = relateAgentNum;
    }
}
