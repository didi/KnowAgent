package com.didichuxing.datachannel.agent.common.configs.v2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;

/**
 * @description: agent级别配置
 * @author: huangjw
 * @Date: 19/7/1 14:36
 */
public class AgentConfig {

    /**
     * 主机名
     */
    private String            hostname;

    /**
     * 启停状态
     */
    private int               status  = LogConfigConstants.AGENT_STATUS_RUNNING;

    /**
     * 版本号
     */
    private int               version = 0;

    /**
     * 系统统计量启停状态
     */
    private int               systemStatisticsStatus;

    private OffsetConfig      offsetConfig;
    private MetricConfig      metricConfig;
    private ErrorLogConfig    errorLogConfig;
    private LimitConfig       limitConfig;
    private List<ModelConfig> modelConfigs;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public OffsetConfig getOffsetConfig() {
        return offsetConfig;
    }

    public void setOffsetConfig(OffsetConfig offsetConfig) {
        this.offsetConfig = offsetConfig;
    }

    public MetricConfig getMetricConfig() {
        return metricConfig;
    }

    public void setMetricConfig(MetricConfig metricConfig) {
        this.metricConfig = metricConfig;
    }

    public ErrorLogConfig getErrorLogConfig() {
        return errorLogConfig;
    }

    public void setErrorLogConfig(ErrorLogConfig errorLogConfig) {
        this.errorLogConfig = errorLogConfig;
    }

    public LimitConfig getLimitConfig() {
        return limitConfig;
    }

    public void setLimitConfig(LimitConfig limitConfig) {
        this.limitConfig = limitConfig;
    }

    public List<ModelConfig> getModelConfigs() {
        return modelConfigs;
    }

    public void setModelConfigs(List<ModelConfig> modelConfigs) {
        this.modelConfigs = modelConfigs;
    }

    public int getSystemStatisticsStatus() {
        return systemStatisticsStatus;
    }

    public void setSystemStatisticsStatus(int systemStatisticsStatus) {
        this.systemStatisticsStatus = systemStatisticsStatus;
    }

    @JSONField(serialize = false)
    public Map<String, ModelConfig> getModelConfigMap() {
        Map<String, ModelConfig> ret = new HashMap<>();
        for (ModelConfig config : modelConfigs) {
            ret.put(config.getModelConfigKey(), config);
        }

        return ret;
    }

    @Override
    public String toString() {
        return "AgentConfig{" + "hostname='" + hostname + '\'' + ", status=" + status
               + ", version=" + version + ", offsetConfig=" + offsetConfig + ", metricConfig="
               + metricConfig + ", errorLogConfig=" + errorLogConfig + ", limitConfig="
               + limitConfig + ", modelConfigs=" + modelConfigs + '}';
    }
}
