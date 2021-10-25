package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huqidong
 * @date 2020-09-21
 * Agent配置信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentConfigDO {

    @ApiModelProperty(value = "采集端id")
    private Long agentId;

    @ApiModelProperty(value = "运行采集端进程的主机对应主机名")
    private String hostName;

    @ApiModelProperty(value = "运行采集端进程的主机对应主机 ip")
    private String ip;

    @ApiModelProperty(value = "Agent 配置版本号")
    private Integer agentConfigurationVersion;

    @ApiModelProperty(value = "Agent 端高级配置项集，为json形式字符串")
    private String advancedConfigurationJsonString;

    @ApiModelProperty(value = "Agent指标信息发往的Receiver端生产者配置")
    private ReceiverConfigDO metricsReceiverConfigDO;

    @ApiModelProperty(value = "Agent错误日志信息发往的Receiver端生产者配置")
    private ReceiverConfigDO errorLogsReceiverConfigDO;

    @ApiModelProperty(value = "采集端限流 cpu 阈值")
    private Integer cpuLimitThreshold;

    @ApiModelProperty(value = "采集端限流流量阈值 单位：字节")
    private Long byteLimitThreshold;

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getAgentConfigurationVersion() {
        return agentConfigurationVersion;
    }

    public void setAgentConfigurationVersion(Integer agentConfigurationVersion) {
        this.agentConfigurationVersion = agentConfigurationVersion;
    }

    public String getAdvancedConfigurationJsonString() {
        return advancedConfigurationJsonString;
    }

    public void setAdvancedConfigurationJsonString(String advancedConfigurationJsonString) {
        this.advancedConfigurationJsonString = advancedConfigurationJsonString;
    }

    public ReceiverConfigDO getMetricsProducerConfiguration() {
        return metricsReceiverConfigDO;
    }

    public void setMetricsProducerConfiguration(ReceiverConfigDO metricsReceiverConfigDO) {
        this.metricsReceiverConfigDO = metricsReceiverConfigDO;
    }

    public ReceiverConfigDO getErrorLogsProducerConfiguration() {
        return errorLogsReceiverConfigDO;
    }

    public void setErrorLogsProducerConfiguration(ReceiverConfigDO errorLogsReceiverConfigDO) {
        this.errorLogsReceiverConfigDO = errorLogsReceiverConfigDO;
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
}
