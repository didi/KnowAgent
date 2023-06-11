package com.didichuxing.datachannel.agentmanager.common.enumeration.metrics;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 指标类型枚举
 */
public enum MetricTypeEnum {

    AGENT("Agent",1, null, "Agent"),
    LOG_COLLECT_TASK("LogCollectTask", 2, null, "采集任务"),

    SYSTEM("System",3, AGENT, "系统级"),
    PROCESS("Process",4, AGENT,"进程级"),
    AGENT_BUSINESS("AgentBusiness",5, AGENT,"Agent业务级"),

    LOG_COLLECT_TASK_BUSINESS("LogCollectTaskBusiness",6, LOG_COLLECT_TASK,"采集任务业务级"),

    SYSTEM_BASIC("SystemBasic", 7, SYSTEM,"基础指标集"),
    SYSTEM_OS("SystemOS", 8, SYSTEM,"操作系统相关指标"),
    SYSTEM_PROCESS("SystemProcess", 9, SYSTEM,"进程相关指标"),
    SYSTEM_CPU("SystemCPU", 10, SYSTEM,"CPU相关指标"),
    SYSTEM_MEMORY("SystemMemory", 11, SYSTEM,"Memory相关指标"),
    SYSTEM_DISK("SystemDisk", 12, SYSTEM,"Disk相关指标"),
    SYSTEM_DISK_IO("SystemDiskIO", 13, SYSTEM,"DiskIO相关指标"),
    SYSTEM_FILE_HANDLES("SystemFileHandles", 14, SYSTEM,"文件句柄相关指标"),
    SYSTEM_NET_CARD("SystemNetCard", 15, SYSTEM,"网卡相关指标"),
    SYSTEM_NET_WORK("SystemNetWork", 16, SYSTEM,"网络相关指标"),

    PROCESS_BASIC("ProcessBasic", 17, PROCESS,"基础指标集"),
    PROCESS_CPU("ProcessCPU", 18, PROCESS,"CPU相关指标"),
    PROCESS_MEMORY("ProcessMemory", 19, PROCESS,"Memory相关指标"),
    PROCESS_DISK_IO("ProcessDiskIO", 20, PROCESS,"DiskIO相关指标"),
    PROCESS_GC("ProcessGC", 21, PROCESS,"GC相关指标"),
    PROCESS_THREAD("ProcessThread", 22, PROCESS,"Thread相关指标"),
    PROCESS_FD("ProcessFD", 23, PROCESS,"FD相关指标"),
    PROCESS_NET_WORK("ProcessNetWork", 24, PROCESS,"NetWork相关指标")

    ;

    /**
     * 指标类型名
     */
    private String type;

    /**
     * 指标类型代码
     */
    private Integer code;

    /**
     * 父指标类型（0：表示该指标类型为根节点，不存在父指标类型）
     */
    private MetricTypeEnum parentMetricType;

    /**
     * 指标类型描述信息
     */
    private String description;

    MetricTypeEnum(String type, Integer code, MetricTypeEnum parentMetricType, String description) {
        this.type = type;
        this.code = code;
        this.parentMetricType = parentMetricType;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public Integer getCode() {
        return code;
    }

    public MetricTypeEnum getParentMetricType() {
        return parentMetricType;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据指标类型代码返回对应指标类型
     * @param code 指标类型代码
     * @return 如指标类型代码在指标类型枚举集存在，返回指标类型代码对应指标类型枚举定义，如不存在，返回 null
     */
    public static MetricTypeEnum fromCode(Integer code) {
        for (MetricTypeEnum value : MetricTypeEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据给定父指标类型代码获取对应一级子指标（类型）集
     * @param parentMetricTypeCode 父指标类型代码
     * @return 返回根据给定父指标类型代码获取到的对应一级子指标（类型）集
     */
    public static List<MetricTypeEnum> fromParentMetricTypeCode(Integer parentMetricTypeCode) {
        List<MetricTypeEnum> subMetricTypeEnumList = new ArrayList<>();
        for (MetricTypeEnum value : MetricTypeEnum.values()) {
            if (null != value.parentMetricType && value.parentMetricType.code.equals(parentMetricTypeCode)) {
                subMetricTypeEnumList.add(value);
            }
        }
        return subMetricTypeEnumList;
    }

}
