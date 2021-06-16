package com.didichuxing.datachannel.agentmanager.common.bean.po.agent;

import com.didichuxing.datachannel.agentmanager.common.bean.po.BasePO;
import lombok.Data;

import java.util.Date;

/**
 * @author huqidong
 * @date 2020-09-21
 * 采集端
 */
@Data
public class AgentPO extends BasePO {

    /**
     * 采集端唯一标识
     */
    private Long id;
    /**
     * 运行采集端进程的主机对应主机名
     */
    private String hostName;
    /**
     * 运行采集端进程的主机对应主机 ip
     */
    private String ip;
    /**
     * 采集端采集方式 0：采集宿主机日志 1：采集宿主机挂载的所有容器日志 2：采集宿主机日志 & 宿主机挂载的所有容器日志
     */
    private Integer collectType;
    /**
     * 采集端限流 cpu 阈值
     */
    private Integer cpuLimitThreshold;
    /**
     * 采集端限流流量阈值 单位：字节
     */
    private Long byteLimitThreshold;
    /**
     * agent版本号id
     */
    private Long agentVersionId;
    /**
     * Agent指标信息发往的topic名
     */
    private String metricsSendTopic;
    /**
     * Agent指标信息发往的接收端id
     */
    private Long metricsSendReceiverId;
    /**
     * Agent错误日志信息发往的topic名
     */
    private String errorLogsSendTopic;
    /**
     * Agent错误日志信息发往的接收端id
     */
    private Long errorLogsSendReceiverId;
    /**
     * 采集端高级配置项集，为json形式字符串
     */
    private String advancedConfigurationJsonString;
    /**
     * AgentPO 配置版本号
     */
    private Integer configurationVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getCollectType() {
        return collectType;
    }

    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
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

    public Integer getConfigurationVersion() {
        return configurationVersion;
    }

    public void setConfigurationVersion(Integer configurationVersion) {
        this.configurationVersion = configurationVersion;
    }

    public Long getAgentVersionId() {
        return agentVersionId;
    }

    public void setAgentVersionId(Long agentVersionId) {
        this.agentVersionId = agentVersionId;
    }
}