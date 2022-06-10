package com.didichuxing.datachannel.agent.common.configs.v2.component;

import com.didichuxing.datachannel.agent.common.configs.v2.component.sourceConfig.SourceConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.targetConfig.TargetConfig;
import com.didichuxing.datachannel.agent.common.constants.ComponentType;

/**
 * @description: 单任务级别配置
 * @author: huangjw
 * @Date: 19/6/28 17:56
 */
public class ModelConfig extends ComponentConfig {

    /**
     * 版本号
     */
    private int                version         = 0;

    private EventMetricsConfig eventMetricsConfig;

    private CommonConfig       commonConfig;

    private ModelLimitConfig   modelLimitConfig;

    private SourceConfig       sourceConfig;

    private TargetConfig       targetConfig;

    private ChannelConfig      channelConfig;

    /**
     * 兼容弹性云场景,弹性云场景下为容器名，普通采集场景下可忽略
     */
    private String             hostname;

    /**
     * 采集类型，用于区分是正常的采集还是在宿主机内的采集
     */
    private int                collectType     = 0;

    /**
     * offset copy时使用，从该日志模型id复制出offset
     */
    private Long               sourceLogModeId = 0L;

    public ModelConfig(String tag) {
        super(ComponentType.TASK, tag);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public CommonConfig getCommonConfig() {
        return commonConfig;
    }

    public void setCommonConfig(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    public ModelLimitConfig getModelLimitConfig() {
        return modelLimitConfig;
    }

    public void setModelLimitConfig(ModelLimitConfig modelLimitConfig) {
        this.modelLimitConfig = modelLimitConfig;
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

    public ChannelConfig getChannelConfig() {
        return channelConfig;
    }

    public void setChannelConfig(ChannelConfig channelConfig) {
        this.channelConfig = channelConfig;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getModelConfigKey() {
        if (this.hostname == null || this.hostname.length() == 0) {
            return commonConfig.getModelId() + "";
        }
        return this.hostname + "_" + commonConfig.getModelId();
    }

    public int getCollectType() {
        return collectType;
    }

    public void setCollectType(int collectType) {
        this.collectType = collectType;
    }

    public Long getSourceLogModeId() {
        return sourceLogModeId;
    }

    public void setSourceLogModeId(Long sourceLogModeId) {
        this.sourceLogModeId = sourceLogModeId;
    }

    @Override
    public String toString() {
        return "ModelConfig{" + "version=" + version + ", eventMetricsConfig=" + eventMetricsConfig
               + ", commonConfig=" + commonConfig + ", modelLimitConfig=" + modelLimitConfig
               + ", sourceConfig=" + sourceConfig + ", targetConfig=" + targetConfig
               + ", channelConfig=" + channelConfig + ", hostname='" + hostname + '\''
               + ", collectType=" + collectType + ", sourceLogModeId=" + sourceLogModeId + '}';
    }
}
