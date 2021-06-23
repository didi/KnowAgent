package com.didichuxing.datachannel.agentmanager.common.bean.dto.agent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huqidong
 * @date 2020-09-21
 * Agent对象
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentUpdateDTO {

    @ApiModelProperty(value = "Agent对象id 注：注册Agent接口不可填，仅在更新Agent接口必填")
    private Long id;

    @ApiModelProperty(value = "AgentPO cpu 限流阈值 单位：核")
    private Integer cpuLimitThreshold;

    @ApiModelProperty(value = "AgentPO 流量限流阈值 单位：字节")
    private Long byteLimitThreshold;

    @ApiModelProperty(value = "Agent指标信息发往的topic名")
    private String metricsSendTopic;

    @ApiModelProperty(value = "Agent指标信息发往的接收端id")
    private Long metricsSendReceiverId;

    @ApiModelProperty(value = "Agent指标信息发送端属性")
    private String metricsProducerConfiguration;

    @ApiModelProperty(value = "Agent错误日志信息发往的topic名")
    private String errorLogsSendTopic;

    @ApiModelProperty(value = "Agent错误日志信息发往的接收端id")
    private Long errorLogsSendReceiverId;

    @ApiModelProperty(value = "Agent错误日志信息发送端属性")
    private String errorLogsProducerConfiguration;

    @ApiModelProperty(value = "Agent高级配置项集，为json形式字符串")
    private String advancedConfigurationJsonString;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCpuLimitThreshold() {
        return cpuLimitThreshold;
    }

    public void setCpuLimitThreshold(Integer cpuLimitThreshold) {
        this.cpuLimitThreshold = cpuLimitThreshold;
    }

    public Long getByteLimitThreshold() {
        return byteLimitThreshold;
    }

    public void setByteLimitThreshold(Long byteLimitThreshold) {
        this.byteLimitThreshold = byteLimitThreshold;
    }

    public String getMetricsSendTopic() {
        return metricsSendTopic;
    }

    public void setMetricsSendTopic(String metricsSendTopic) {
        this.metricsSendTopic = metricsSendTopic;
    }

    public Long getMetricsSendReceiverId() {
        return metricsSendReceiverId;
    }

    public void setMetricsSendReceiverId(Long metricsSendReceiverId) {
        this.metricsSendReceiverId = metricsSendReceiverId;
    }

    public String getMetricsProducerConfiguration() {
        return metricsProducerConfiguration;
    }

    public void setMetricsProducerConfiguration(String metricProducerConfiguration) {
        this.metricsProducerConfiguration = metricProducerConfiguration;
    }

    public String getErrorLogsSendTopic() {
        return errorLogsSendTopic;
    }

    public void setErrorLogsSendTopic(String errorLogsSendTopic) {
        this.errorLogsSendTopic = errorLogsSendTopic;
    }

    public Long getErrorLogsSendReceiverId() {
        return errorLogsSendReceiverId;
    }

    public void setErrorLogsSendReceiverId(Long errorLogsSendReceiverId) {
        this.errorLogsSendReceiverId = errorLogsSendReceiverId;
    }

    public String getAdvancedConfigurationJsonString() {
        return advancedConfigurationJsonString;
    }

    public void setAdvancedConfigurationJsonString(String advancedConfigurationJsonString) {
        this.advancedConfigurationJsonString = advancedConfigurationJsonString;
    }

    public String getErrorLogsProducerConfiguration() {
        return errorLogsProducerConfiguration;
    }

    public void setErrorLogsProducerConfiguration(String errorLogsProducerConfiguration) {
        this.errorLogsProducerConfiguration = errorLogsProducerConfiguration;
    }
}
