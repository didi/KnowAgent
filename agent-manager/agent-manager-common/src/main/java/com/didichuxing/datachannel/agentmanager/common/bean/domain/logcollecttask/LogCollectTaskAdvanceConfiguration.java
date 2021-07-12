package com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask;

/**
 * 日志采集任务高级配置项
 */
public class LogCollectTaskAdvanceConfiguration {

    /**
     * 主机过滤规则信息（存储 BaseHostFilterRuleLogic 某具体实现类的 json 化形式）
     */
    private String hostFilterRuleLogicJsonString;
    /*
     * 历史数据过滤 0：不过滤 1：从当前时间开始采集 2：从自定义时间开始采集，自定义时间取collectStartBusinessTime属性值
     */
    private Integer oldDataFilterType;
    /**
     * 日志内容过滤规则信息（存储 BaseLogContentFilterRuleLogic 某具体实现类的 json 化形式）
     */
    private String logContentFilterRuleLogicJsonString;
    /**
     * 待采集文件字符集
     */
    private String charset;
    /**
     * 待采集文件 offset 有效期 单位：ms
     * 注：待采集文件自最后一次写入时间 ~ 当前时间间隔 > fdOffset时，采集端将删除其维护的该文件对应 offset 信息，如此时，
     * 该文件仍存在于待采集目录下，将被重新采集
     */
    private Long fdOffsetExpirationTimeMs;
    /**
     * 单个日志切片最大大小 单位：字节
     * 注：单个日志切片大小超过该值后，采集端将以该值进行截断采集
     */
    private Long maxBytesPerLogEvent;

    public String getHostFilterRuleLogicJsonString() {
        return hostFilterRuleLogicJsonString;
    }

    public void setHostFilterRuleLogicJsonString(String hostFilterRuleLogicJsonString) {
        this.hostFilterRuleLogicJsonString = hostFilterRuleLogicJsonString;
    }

    public Integer getOldDataFilterType() {
        return oldDataFilterType;
    }

    public void setOldDataFilterType(Integer oldDataFilterType) {
        this.oldDataFilterType = oldDataFilterType;
    }

    public String getLogContentFilterRuleLogicJsonString() {
        return logContentFilterRuleLogicJsonString;
    }

    public void setLogContentFilterRuleLogicJsonString(String logContentFilterRuleLogicJsonString) {
        this.logContentFilterRuleLogicJsonString = logContentFilterRuleLogicJsonString;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Long getFdOffsetExpirationTimeMs() {
        return fdOffsetExpirationTimeMs;
    }

    public void setFdOffsetExpirationTimeMs(Long fdOffsetExpirationTimeMs) {
        this.fdOffsetExpirationTimeMs = fdOffsetExpirationTimeMs;
    }

    public Long getMaxBytesPerLogEvent() {
        return maxBytesPerLogEvent;
    }

    public void setMaxBytesPerLogEvent(Long maxBytesPerLogEvent) {
        this.maxBytesPerLogEvent = maxBytesPerLogEvent;
    }
}

