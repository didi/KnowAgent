package com.didichuxing.datachannel.agentmanager.common.bean.dto.agent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * Agent对象
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentDTO {

    @ApiModelProperty(value = "Agent对象id 注：注册Agent接口不可填，仅在更新Agent接口必填")
    private Long id;

    @ApiModelProperty(value = "Agent宿主机名 注：注册Agent接口必填，更新Agent接口不可填")
    private String hostName;

    @ApiModelProperty(value = "Agent宿主机ip 注：注册Agent接口必填，更新Agent接口不可填")
    private String ip;

    @ApiModelProperty(value = "Agent采集方式 0：采集宿主机日志 1：采集宿主机挂载的所有容器日志 2：采集宿主机日志 & 宿主机挂载的所有容器日志 注：注册Agent接口必填，更新Agent接口不可填")
    private Integer collectType;

    @ApiModelProperty(value = "Agent版本号 注：创建Agent接口必填，更新Agent接口不可填")
    private String version;

    @ApiModelProperty(value = "AgentPO cpu 限流阈值 单位：核")
    private Integer cpuLimitThreshold;

    @ApiModelProperty(value = "AgentPO 流量限流阈值 单位：字节")
    private Long byteLimitThreshold;

    @ApiModelProperty(value = "Agent指标信息发往的topic名")
    private String metricsSendTopic;

    @ApiModelProperty(value = "Agent指标信息发往的接收端id")
    private Long metricsSendReceiverId;

    @ApiModelProperty(value = "Agent错误日志信息发往的topic名")
    private String errorLogsSendTopic;

    @ApiModelProperty(value = "Agent错误日志信息发往的接收端id")
    private Long errorLogsSendReceiverId;

    @ApiModelProperty(value = "Agent高级配置项集，为json形式字符串")
    private String advancedConfigurationJsonString;

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

    public String getAdvancedConfigurationJsonString() {
        return advancedConfigurationJsonString;
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
}
