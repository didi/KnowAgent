package com.didichuxing.datachannel.agent.common.configs.v1.collector;

import com.alibaba.fastjson.annotation.JSONField;
import com.didichuxing.datachannel.agent.common.beans.LogPath;
import com.didichuxing.datachannel.agent.common.configs.v1.limit.LimitNodeConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 采集配置类
 * @author: huangjw
 * @Date: 18/6/20 13:11
 */
public class CollectorConfig {

    private Integer            version;

    /**
     * 基本配置
     */
    private CommonConfig       commonConfig;

    /**
     * 路径类型
     */
    private MatchConfig        matchConfig;

    /**
     * 业务相关配置
     */
    private BusinessConfig     businessConfig;

    /**
     * 限流配置
     */
    private LimitNodeConfig    limitNodeConfig;

    /**
     * 采集源头配置，一般为路径
     */
    private SourceConfig       sourceConfig;

    /**
     * 采集目的地配置，一般为topic信息
     */
    private TargetConfig       targetConfig;

    /**
     * metrics配置类
     */
    private EventMetricsConfig eventMetricsConfig;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public CommonConfig getCommonConfig() {
        return commonConfig;
    }

    public void setCommonConfig(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    public SourceConfig getSourceConfig() {
        return sourceConfig;
    }

    public void setSourceConfig(SourceConfig sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    public TargetConfig getTargetConfig() {
        return targetConfig;
    }

    public void setTargetConfig(TargetConfig targetConfig) {
        this.targetConfig = targetConfig;
    }

    public EventMetricsConfig getEventMetricsConfig() {
        return eventMetricsConfig;
    }

    public void setEventMetricsConfig(EventMetricsConfig eventMetricsConfig) {
        this.eventMetricsConfig = eventMetricsConfig;
    }

    public BusinessConfig getBusinessConfig() {
        return businessConfig;
    }

    public void setBusinessConfig(BusinessConfig businessConfig) {
        this.businessConfig = businessConfig;
    }

    public LimitNodeConfig getLimitNodeConfig() {
        return limitNodeConfig;
    }

    public MatchConfig getMatchConfig() {
        return matchConfig;
    }

    public void setMatchConfig(MatchConfig matchConfig) {
        this.matchConfig = matchConfig;
    }

    @JSONField(serialize=false)
    public Map<Long, LogPath> getLogPathMap() {
        Map<Long, LogPath> logPathMap = new HashMap<>();
        for (LogPath logPath : sourceConfig.logPaths) {
            logPathMap.put(logPath.getPathId(), logPath);
        }
        return logPathMap;
    }

    public void setLimitNodeConfig(LimitNodeConfig limitNodeConfig) {
        this.limitNodeConfig = limitNodeConfig;
    }

    @Override
    public String toString() {
        return "CollectorConfig{" + "version=" + version + ", commonConfig=" + commonConfig
               + ", matchConfig=" + matchConfig + ", businessConfig=" + businessConfig
               + ", limitNodeConfig=" + limitNodeConfig + ", sourceConfig=" + sourceConfig
               + ", targetConfig=" + targetConfig + ", eventMetricsConfig=" + eventMetricsConfig
               + '}';
    }
}
