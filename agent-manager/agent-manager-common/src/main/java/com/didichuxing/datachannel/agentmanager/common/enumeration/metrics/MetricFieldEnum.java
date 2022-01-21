package com.didichuxing.datachannel.agentmanager.common.enumeration.metrics;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 指标字段
 * @author william
 */
public enum MetricFieldEnum {

    /*********************************** 系统相关 ***********************************/

    /*
     * 基础信息
     */
//    HOST_NAME("host_name", "hostName", "主机名", MetricTypeEnum.SYSTEM_BASIC, AggregationCalcFunctionEnum.NONE,MetricValueTypeEnum.CURRENT),
//    IPS("ips", "ips", "IP列表", MetricTypeEnum.SYSTEM_BASIC, AggregationCalcFunctionEnum.NONE,MetricValueTypeEnum.CURRENT),
//    SYSTEM_NTP_OFFSET("system_ntp_offset","systemNtpOffset","系统时间偏移量", MetricTypeEnum.SYSTEM_BASIC, AggregationCalcFunctionEnum.MAX,MetricValueTypeEnum.CURRENT),
//    SYSTEM_STARTUP_TIME("system_startup_time", "systemStartupTime", "系统启动时间", MetricTypeEnum.SYSTEM_BASIC, AggregationCalcFunctionEnum.NONE,MetricValueTypeEnum.CURRENT),
//    SYSTEM_UP_TIME("system_uptime", "systemUptime", "系统运行时间", MetricTypeEnum.SYSTEM_BASIC, AggregationCalcFunctionEnum.NONE,MetricValueTypeEnum.CURRENT),

    /*
     * os 相关
     */
//    SYSTEM_OS_TYPE("os_type", "osType", "系统操作系统类型", MetricTypeEnum.SYSTEM_OS, AggregationCalcFunctionEnum.NONE,MetricValueTypeEnum.CURRENT),
//    SYSTEM_OS_VERSION("os_version", "osVersion", "系统操作系统版本", MetricTypeEnum.SYSTEM_OS, AggregationCalcFunctionEnum.NONE,MetricValueTypeEnum.CURRENT),
//    SYSTEM_OS_KERNEL_VERSION("os_kernel_version", "osKernelVersion", "系统操作系统内核版本", MetricTypeEnum.SYSTEM_OS, AggregationCalcFunctionEnum.NONE,MetricValueTypeEnum.CURRENT),

    /*
     * process 相关
     *
     * TODO：
     *
     */

    /*
     * cpu 相关
     */
//    SYSTEM_CPU_CORES("cpu_cores","cpuCores","cpu核数", MetricTypeEnum.SYSTEM_CPU, AggregationCalcFunctionEnum.NONE,MetricValueTypeEnum.CURRENT),
//    SYSTEM_CPU_UTIL(
//            "system_cpu_util",
//            "systemCpuUtil",
//            "系统总体CPU使用率(单位：%)，使用率采用全核方式计数，如系统使用一颗核，则返回100，如使用两颗核，则返回200",
//            MetricTypeEnum.SYSTEM_CPU,
//            AggregationCalcFunctionEnum.MAX,
//            MetricValueTypeEnum.CURRENT
//    ),

    /*
     * disk io 相关
     */
    SYSTEM_DISK_IO_IO_UTIL(
            1,
            "io_util",
            "systemIOUtil",
            "各磁盘I/O请求的时间百分比",
            MetricTypeEnum.SYSTEM_DISK_IO,
            AggregationCalcFunctionEnum.MAX,
            MetricValueTypeEnum.STATISTICS,
            MetricUnitEnum.PERCENT,
            MetricUnitEnum.PERCENT,
            MetricDisplayTypeEnum.MULTI_LINE_CHAT,
            false
            ),

    SYSTEM_NET_CARD_SEND_BYTES_PS(
            2,
            "send_bytes/s",
            "systemNetCardsSendBytesPs",
            "各网卡每秒上行流量（单位：byte）",
            MetricTypeEnum.SYSTEM_NET_CARD,
            AggregationCalcFunctionEnum.MAX,
            MetricValueTypeEnum.STATISTICS,
            MetricUnitEnum.BYTE,
            MetricUnitEnum.M_BYTE,
            MetricDisplayTypeEnum.MULTI_LINE_CHAT,
            false
    ),

    /*********************************** 当前进程相关 ***********************************/

    /*
     * 基础信息
     */
    PROCESS_START_UP_TIME(
            3,
            "process_startup_time",
            "procStartupTime",
            "当前进程启动时间",
            MetricTypeEnum.PROCESS_BASIC,
            AggregationCalcFunctionEnum.MAX,
            MetricValueTypeEnum.CURRENT,
            MetricUnitEnum.TIMESTAMP_MILLISECOND,
            MetricUnitEnum.DATE_TIME,
            MetricDisplayTypeEnum.LABLE,
            false
    ),

    /*
     * cpu 相关
     */
    PROCESS_CPU_UTIL(
            4,
            "process_cpu_util",
            "procCpuUtil",
            "当前进程cpu使用率(单位：%) 使用率采用全核方式计数，如进程使用一颗核，则返回100，如进程使用两颗核，则返回200",
            MetricTypeEnum.PROCESS_CPU,
            AggregationCalcFunctionEnum.MAX,
            MetricValueTypeEnum.STATISTICS,
            MetricUnitEnum.PERCENT,
            MetricUnitEnum.PERCENT,
            MetricDisplayTypeEnum.SINGLE_LINE_CHAT,
            true
    ),

    /*********************************** agent 相关 ***********************************/

    /*********************************** 采集任务相关 ***********************************/


    LOG_COLLECT_TASK_BUSINESS_TIMESTAMP(
            5,
            "business_time",
            "businessTimestamp",
            "采集业务时间",
            MetricTypeEnum.LOG_COLLECT_TASK_BUSINESS,
            AggregationCalcFunctionEnum.MIN,
            MetricValueTypeEnum.CURRENT,
            MetricUnitEnum.TIMESTAMP_MILLISECOND,
            MetricUnitEnum.DATE_TIME,
            MetricDisplayTypeEnum.LABLE,
            true
    ),

    LOG_COLLECT_TASK_SEND_BYTES(
            6,
     "send_bytes",
     "sendBytes",
     "日志发送字节数",
            MetricTypeEnum.LOG_COLLECT_TASK_BUSINESS,
            AggregationCalcFunctionEnum.SUM,
            MetricValueTypeEnum.CURRENT,
            MetricUnitEnum.BYTE,
            MetricUnitEnum.M_BYTE,
            MetricDisplayTypeEnum.MULTI_LINE_CHAT,
            true
    )

    ;

    /**
     * 指标代码
     */
    private Integer code;

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
    private MetricTypeEnum metricType;

    /**
     * 该指标计算时采用的聚合函数枚举
     */
    private AggregationCalcFunctionEnum aggregationCalcFunction;

    /**
     * 指标值类型枚举
     */
    private MetricValueTypeEnum metricValueType;

    /**
     * 基础单位
     */
    private MetricUnitEnum baseUnit;

    /**
     * 显示单位
     */
    private MetricUnitEnum displayUnit;

    /**
     * 指标展示类型
     */
    private MetricDisplayTypeEnum metricDisplayType;

    /**
     * 是否默认选中
     */
    private boolean checked;

    MetricFieldEnum(
            Integer code,
            String metricName,
            String fieldName,
            String description,
            MetricTypeEnum metricType,
            AggregationCalcFunctionEnum aggregationCalcFunction,
            MetricValueTypeEnum metricValueType,
            MetricUnitEnum baseUnit,
            MetricUnitEnum displayUnit,
            MetricDisplayTypeEnum metricDisplayType,
            boolean checked
            ) {
        this.code = code;
        this.metricName = metricName;
        this.fieldName = fieldName;
        this.description = description;
        this.metricType = metricType;
        this.aggregationCalcFunction = aggregationCalcFunction;
        this.metricValueType = metricValueType;
        this.baseUnit = baseUnit;
        this.displayUnit = displayUnit;
        this.metricDisplayType = metricDisplayType;
        this.checked = checked;
    }

    public String getMetricName() {
        return metricName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDescription() {
        return description;
    }

    public MetricTypeEnum getMetricType() {
        return metricType;
    }

    public AggregationCalcFunctionEnum getAggregationCalcFunction() {
        return aggregationCalcFunction;
    }

    public MetricValueTypeEnum getMetricValueType() {
        return metricValueType;
    }

    public MetricUnitEnum getBaseUnit() {
        return baseUnit;
    }

    public MetricUnitEnum getDisplayUnit() {
        return displayUnit;
    }

    public MetricDisplayTypeEnum getMetricDisplayType() {
        return metricDisplayType;
    }

    public Integer getCode() {
        return code;
    }

    public boolean isChecked() {
        return checked;
    }

    /**
     * 根据指标代码返回对应指标枚举定义
     * @param metricCode 指标名
     * @return 如指标代码在指标枚举集存在，返回指标名对应指标枚举定义，如不存在，返回 null
     */
    public static MetricFieldEnum fromMetricCode(Integer metricCode) {
        for (MetricFieldEnum value : MetricFieldEnum.values()) {
            if (value.code.equals(metricCode)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据给定指标类型获取属该指标类型的指标集
     * @param metricTypeEnum 指标类型
     * @return 返回根据给定指标类型获取属该指标类型的指标集
     */
    public static List<MetricFieldEnum> fromMetricTypeEnum(MetricTypeEnum metricTypeEnum) {
        List<MetricFieldEnum> result = new ArrayList<>();
        for (MetricFieldEnum value : MetricFieldEnum.values()) {
            if (value.metricType.equals(metricTypeEnum)) {
                result.add(value);
            }
        }
        return result;
    }

}
