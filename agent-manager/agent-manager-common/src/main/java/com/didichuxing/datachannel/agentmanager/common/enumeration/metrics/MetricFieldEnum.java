package com.didichuxing.datachannel.agentmanager.common.enumeration.metrics;

import org.apache.commons.lang3.StringUtils;

/**
 * 指标字段
 * @author william
 */
public enum MetricFieldEnum {

    /*********************************** 系统相关 ***********************************/

//    SYSTEM_NTP_OFFSET("systemNtpOffset", "systemNtpOffset")
    ;

    /*********************************** 当前进程相关 ***********************************/

    /*********************************** agent 相关 ***********************************/

    /*********************************** 采集任务相关 ***********************************/

    public String getMetricName() {
        return metricName;
    }

    public String getFieldName() {
        return fieldName;
    }

    /**
     * 指标名
     */
    private String metricName;

    /**
     * 存储系统字段名
     */
    private String fieldName;

    /**
     * 指标描述
     */
    private String description;

    /**
     * 指标类型（对应MetricTypeEnum.code）
     */
    private Integer type;

    MetricFieldEnum(String metricName, String fieldName, String description, Integer type) {
        this.metricName = metricName;
        this.fieldName = fieldName;
        this.description = description;
        this.type = type;
    }

    /**
     * 根据指标名返回对应指标枚举定义
     * @param metricName 指标名
     * @return 如指标名在指标枚举集存在，返回指标名对应指标枚举定义，如不存在，返回 null
     */
    public static MetricFieldEnum fromMetricName(String metricName) {
        if (StringUtils.isBlank(metricName)) {
            return null;
        }
        for (MetricFieldEnum value : MetricFieldEnum.values()) {
            if (value.metricName.equals(metricName)) {
                return value;
            }
        }
        return null;
    }

}
