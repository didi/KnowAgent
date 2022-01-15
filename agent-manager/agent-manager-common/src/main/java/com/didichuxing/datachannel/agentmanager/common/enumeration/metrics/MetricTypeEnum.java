package com.didichuxing.datachannel.agentmanager.common.enumeration.metrics;

/**
 * 指标类型枚举
 */
public enum MetricTypeEnum {

    AGENT("Agent",1, 0, "Agent"),
    LOG_COLLECT_TASK("LogCollectTask", 2, 0, "采集任务"),

    SYSTEM("System",3, 1, "系统级"),
    PROCESS("Process",4, 1,"进程级"),
    AGENT_BUSINESS("AgentBusiness",5, 1,"Agent业务级"),

    LOG_COLLECT_TASK_BUSINESS("LogCollectTaskBusiness",6, 2,"采集任务业务级"),

    SYSTEM_BASIC("SystemBasic", 7, 3,"基础指标集"),
    SYSTEM_OS("SystemOS", 8, 3,"操作系统相关指标"),
    SYSTEM_PROCESS("SystemProcess", 9, 3,"进程相关指标"),
    SYSTEM_CPU("SystemCPU", 10, 3,"CPU相关指标"),
    SYSTEM_MEMORY("SystemMemory", 11, 3,"Memory相关指标"),
    SYSTEM_DISK("SystemDisk", 12, 3,"Disk相关指标"),
    SYSTEM_DISK_IO("SystemDiskIO", 13, 3,"DiskIO相关指标"),
    SYSTEM_FILE_HANDLES("SystemFileHandles", 14, 3,"文件句柄相关指标"),
    SYSTEM_NET_CARD("SystemNetCard", 15, 3,"网卡相关指标"),
    SYSTEM_NET_WORK("SystemNetWork", 16, 3,"网络相关指标"),

    PROCESS_BASIC("ProcessBasic", 17, 4,"基础指标集"),
    PROCESS_CPU("ProcessCPU", 18, 4,"CPU相关指标"),
    PROCESS_MEMORY("ProcessMemory", 19, 4,"Memory相关指标"),
    PROCESS_DISK_IO("ProcessDiskIO", 20, 4,"DiskIO相关指标"),
    PROCESS_GC("ProcessGC", 21, 4,"GC相关指标"),
    PROCESS_THREAD("ProcessThread", 22, 4,"Thread相关指标"),
    PROCESS_FD("ProcessFD", 23, 4,"FD相关指标"),
    PROCESS_NET_WORK("ProcessNetWork", 24, 4,"NetWork相关指标")

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
     * 父指标类型代码（0：表示该指标类型为根节点，不存在父指标类型）
     */
    private Integer parentMetricTypeCode;

    /**
     * 指标类型描述信息
     */
    private String description;

    MetricTypeEnum(String type, Integer code, Integer parentMetricTypeCode, String description) {
        this.type = type;
        this.code = code;
        this.parentMetricTypeCode = parentMetricTypeCode;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public Integer getCode() {
        return code;
    }

    public Integer getParentMetricTypeCode() {
        return parentMetricTypeCode;
    }

    public String getDescription() {
        return description;
    }
}
