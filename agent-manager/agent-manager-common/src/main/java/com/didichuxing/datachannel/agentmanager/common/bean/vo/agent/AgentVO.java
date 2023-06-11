package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentVO {

    @ApiModelProperty(value = "AgentPO id")
    private Long id;

    @ApiModelProperty(value = "Agent宿主机名")
    private String hostName;

    @ApiModelProperty(value = "Agent宿主机ip")
    private String ip;

    @ApiModelProperty(value = "Agent采集方式 0：采集宿主机日志 1：采集宿主机挂载的所有容器日志 2：采集宿主机日志 & 宿主机挂载的所有容器日志")
    private Integer collectType;

    @ApiModelProperty(value = "Agent限流cpu阈值 单位：核")
    private Integer cpuLimitThreshold;

    @ApiModelProperty(value = "Agent限流流量阈值 单位：字节")
    private Long byteLimitThreshold;

    @ApiModelProperty(value = "Agent版本号")
    private String version;

    @ApiModelProperty(value = "Agent指标信息发往的topic名")
    private String metricsSendTopic;

    @ApiModelProperty(value = "Agent指标信息发往的接收端id")
    private Long metricsSendReceiverId;

    @ApiModelProperty(value = "Agent错误日志信息发往的topic名")
    private String errorLogsSendTopic;

    @ApiModelProperty(value = "Agent错误日志信息发往的接收端id")
    private Long errorLogsSendReceiverId;

    @ApiModelProperty(value = "采集端高级配置项集，为json形式字符串")
    private String advancedConfigurationJsonString;

    @ApiModelProperty(value = "Agent健康度 0：红 1：黄 2：绿")
    private Integer healthLevel;

    @ApiModelProperty(value = "agent健康描述信息")
    private String agentHealthDescription;

    @ApiModelProperty(value = "Agent指标信息发送端属性")
    private String metricsProducerConfiguration;

    @ApiModelProperty(value = "Agent错误日志信息发送端属性")
    private String errorLogsProducerConfiguration;

    public String getMetricsProducerConfiguration() {
        return metricsProducerConfiguration;
    }

    public void setMetricsProducerConfiguration(String metricsProducerConfiguration) {
        this.metricsProducerConfiguration = metricsProducerConfiguration;
    }

    public String getAgentHealthDescription() {
        return agentHealthDescription;
    }

    public void setAgentHealthDescription(String agentHealthDescription) {
        this.agentHealthDescription = agentHealthDescription;
    }

    public String getErrorLogsProducerConfiguration() {
        return errorLogsProducerConfiguration;
    }

    public void setErrorLogsProducerConfiguration(String errorLogsProducerConfiguration) {
        this.errorLogsProducerConfiguration = errorLogsProducerConfiguration;
    }

    public void setHealthLevel(Integer healthLevel) {
        this.healthLevel = healthLevel;
    }

    public Integer getHealthLevel() {
        return healthLevel;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }

    public void setCpuLimitThreshold(Integer cpuLimitThreshold) {
        this.cpuLimitThreshold = cpuLimitThreshold;
    }

    public void setByteLimitThreshold(Long byteLimitThreshold) {
        this.byteLimitThreshold = byteLimitThreshold;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setMetricsSendTopic(String metricsSendTopic) {
        this.metricsSendTopic = metricsSendTopic;
    }

    public void setMetricsSendReceiverId(Long metricsSendReceiverId) {
        this.metricsSendReceiverId = metricsSendReceiverId;
    }

    public void setErrorLogsSendTopic(String errorLogsSendTopic) {
        this.errorLogsSendTopic = errorLogsSendTopic;
    }

    public void setErrorLogsSendReceiverId(Long errorLogsSendReceiverId) {
        this.errorLogsSendReceiverId = errorLogsSendReceiverId;
    }

    public void setAdvancedConfigurationJsonString(String advancedConfigurationJsonString) {
        this.advancedConfigurationJsonString = advancedConfigurationJsonString;
    }

    public Long getId() {
        return id;
    }

    public String getHostName() {
        return hostName;
    }

    public String getIp() {
        return ip;
    }

    public Integer getCollectType() {
        return collectType;
    }

    public Integer getCpuLimitThreshold() {
        return cpuLimitThreshold;
    }

    public Long getByteLimitThreshold() {
        return byteLimitThreshold;
    }

    public String getVersion() {
        return version;
    }

    public String getMetricsSendTopic() {
        return metricsSendTopic;
    }

    public Long getMetricsSendReceiverId() {
        return metricsSendReceiverId;
    }

    public String getErrorLogsSendTopic() {
        return errorLogsSendTopic;
    }

    public Long getErrorLogsSendReceiverId() {
        return errorLogsSendReceiverId;
    }

    public String getAdvancedConfigurationJsonString() {
        return advancedConfigurationJsonString;
    }
}
