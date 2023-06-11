package com.didichuxing.datachannel.agent.common.configs.v1;

import com.alibaba.fastjson.annotation.JSONField;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.configs.v1.collector.CollectorConfig;
import com.didichuxing.datachannel.agent.common.configs.v1.limit.LimitConfig;

import java.util.*;

public class LogConfig {

    private Integer               version = 0;

    private String                ip;

    private String                hostName;

    private Integer               status  = LogConfigConstants.AGENT_STATUS_RUNNING;

    private ProducerConfig        errLogConfig;

    private ProducerConfig        metricConfig;

    private LimitConfig           limitConfig;

    private OffsetConfig          offsetConfig;

    private List<CollectorConfig> collectorConfigList;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public LimitConfig getLimitConfig() {
        return limitConfig;
    }

    public void setLimitConfig(LimitConfig limitConfig) {
        this.limitConfig = limitConfig;
    }

    public ProducerConfig getMetricConfig() {
        return metricConfig;
    }

    public void setMetricConfig(ProducerConfig metricConfig) {
        this.metricConfig = metricConfig;
    }

    public List<CollectorConfig> getCollectorConfigList() {
        return collectorConfigList;
    }

    public void setCollectorConfigList(List<CollectorConfig> collectorConfigList) {
        this.collectorConfigList = collectorConfigList;
    }

    public Map<String, CollectorConfig> getMap() {
        return null;
    }

    public OffsetConfig getOffsetConfig() {
        return offsetConfig;
    }

    @JSONField(serialize = false)
    public Map<Integer, CollectorConfig> getCollectorConfigMap() {
        Map<Integer, CollectorConfig> ret = new HashMap<>();
        for (CollectorConfig collectorConfig : collectorConfigList) {
            ret.put(collectorConfig.getCommonConfig().getLogModelId(), collectorConfig);
        }

        return ret;
    }

    public void setOffsetConfig(OffsetConfig offsetConfig) {
        this.offsetConfig = offsetConfig;
    }

    public ProducerConfig getErrLogConfig() {
        return errLogConfig;
    }

    public void setErrLogConfig(ProducerConfig errLogConfig) {
        this.errLogConfig = errLogConfig;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LogConfig{" + "version=" + version + ", ip='" + ip + '\'' + ", hostName='"
               + hostName + '\'' + ", status=" + status + ", errLogConfig=" + errLogConfig
               + ", metricConfig=" + metricConfig + ", limitConfig=" + limitConfig
               + ", offsetConfig=" + offsetConfig + ", collectorConfigList=" + collectorConfigList
               + '}';
    }
}
