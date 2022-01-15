package com.didichuxing.datachannel.agentmanager.common.enumeration.metrics;

import org.apache.commons.lang3.StringUtils;

/**
 * 指标字段
 * @author william
 */
public enum MetricFieldEnum {

    /*********************************** 系统相关 ***********************************/

    /*
     * 基础信息
     */
    HOST_NAME("host_name", "hostName", "主机名", MetricTypeEnum.SYSTEM_BASIC.getCode(), AggregationCalcFunctionEnum.NONE,MetricValueTypeEnum.CURRENT),
    IPS("ips", "ips", "IP列表", MetricTypeEnum.SYSTEM_BASIC.getCode(), AggregationCalcFunctionEnum.NONE,MetricValueTypeEnum.CURRENT),
    SYSTEM_NTP_OFFSET("system_ntp_offset","systemNtpOffset","系统时间偏移量", MetricTypeEnum.SYSTEM_BASIC.getCode(), AggregationCalcFunctionEnum.MAX,MetricValueTypeEnum.CURRENT),
    SYSTEM_STARTUP_TIME("system_startup_time", "systemStartupTime", "系统启动时间", MetricTypeEnum.SYSTEM_BASIC.getCode(), AggregationCalcFunctionEnum.NONE,MetricValueTypeEnum.CURRENT),
    SYSTEM_UP_TIME("system_uptime", "systemUptime", "系统运行时间", MetricTypeEnum.SYSTEM_BASIC.getCode(), AggregationCalcFunctionEnum.NONE,MetricValueTypeEnum.CURRENT),

    /*
     * os 相关
     */
    SYSTEM_OS_TYPE("os_type", "osType", "系统操作系统类型", MetricTypeEnum.SYSTEM_OS.getCode(), AggregationCalcFunctionEnum.NONE,MetricValueTypeEnum.CURRENT),
    SYSTEM_OS_VERSION("os_version", "osVersion", "系统操作系统版本", MetricTypeEnum.SYSTEM_OS.getCode(), AggregationCalcFunctionEnum.NONE,MetricValueTypeEnum.CURRENT),
    SYSTEM_OS_KERNEL_VERSION("os_kernel_version", "osKernelVersion", "系统操作系统内核版本", MetricTypeEnum.SYSTEM_OS.getCode(), AggregationCalcFunctionEnum.NONE,MetricValueTypeEnum.CURRENT),

    /*
     * process 相关
     *
     * TODO：
     *
     */

    /*
     * cpu 相关
     */
    SYSTEM_CPU_CORES("cpu_cores","cpuCores","cpu核数", MetricTypeEnum.SYSTEM_CPU.getCode(), AggregationCalcFunctionEnum.NONE,MetricValueTypeEnum.CURRENT),
    SYSTEM_CPU_UTIL(
            "system_cpu_util",
            "systemCpuUtil",
            "系统总体CPU使用率(单位：%)，使用率采用全核方式计数，如系统使用一颗核，则返回100，如使用两颗核，则返回200",
            MetricTypeEnum.SYSTEM_CPU.getCode(),
            AggregationCalcFunctionEnum.MAX,
            MetricValueTypeEnum.CURRENT
    ),

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

    /**
     * 该指标计算时采用的聚合函数枚举
     */
    private AggregationCalcFunctionEnum aggregationCalcFunctionEnum;

    /**
     * 指标值类型枚举
     */
    private MetricValueTypeEnum metricValueTypeEnum;

    private MetricUnitEnum metricUnitEnum;

    MetricFieldEnum(String metricName, String fieldName, String description, Integer type, AggregationCalcFunctionEnum aggregationCalcFunctionEnum, MetricValueTypeEnum metricValueTypeEnum) {
        this.metricName = metricName;
        this.fieldName = fieldName;
        this.description = description;
        this.type = type;
        this.aggregationCalcFunctionEnum = aggregationCalcFunctionEnum;
        this.metricValueTypeEnum = metricValueTypeEnum;
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
