package com.didichuxing.datachannel.agentmanager.common.metrics;

import java.util.List;

/**
 * Agent 相关指标，含：
 *  系统级指标
 *      disk/io 级指标
 *      net card 级指标
 *  进程级指标
 *  agent 业务级指标
 */
public class AgentMetrics {

    /**
     * 系统级指标
     */
    private SystemMetrics systemMetrics;

    /**
     * 进程级指标
     */
    private ProcessMetrics       processMetrics;

    /**
     * disk/io 相关指标
     */
    private List<DiskIOMetrics>  diskIOMetricsList;

    /**
     * disk 相关指标
     */
    private List<DiskMetrics>    diskMetricsList;

    /**
     * net card 相关指标
     */
    private List<NetCardMetrics> netCardMetricsList;

    /**
     * agent 业务级指标
     */
    private AgentBusinessMetrics agentBusinessMetrics;

    public AgentMetrics(SystemMetrics systemMetrics, ProcessMetrics processMetrics,
                        List<DiskIOMetrics> diskIOMetricsList,
                        List<NetCardMetrics> netCardMetricsList,
                        AgentBusinessMetrics agentBusinessMetrics, List<DiskMetrics> diskMetricsList) {
        this.systemMetrics = systemMetrics;
        this.processMetrics = processMetrics;
        this.diskIOMetricsList = diskIOMetricsList;
        this.netCardMetricsList = netCardMetricsList;
        this.agentBusinessMetrics = agentBusinessMetrics;
        this.diskMetricsList = diskMetricsList;
    }

    public AgentMetrics() {

    }

    public List<DiskMetrics> getDiskMetricsList() {
        return diskMetricsList;
    }

    public SystemMetrics getSystemMetrics() {
        return systemMetrics;
    }

    public ProcessMetrics getProcessMetrics() {
        return processMetrics;
    }

    public List<DiskIOMetrics> getDiskIOMetricsList() {
        return diskIOMetricsList;
    }

    public List<NetCardMetrics> getNetCardMetricsList() {
        return netCardMetricsList;
    }

    public AgentBusinessMetrics getAgentBusinessMetrics() {
        return agentBusinessMetrics;
    }

    public void setSystemMetrics(SystemMetrics systemMetrics) {
        this.systemMetrics = systemMetrics;
    }

    public void setProcessMetrics(ProcessMetrics processMetrics) {
        this.processMetrics = processMetrics;
    }

    public void setDiskIOMetricsList(List<DiskIOMetrics> diskIOMetricsList) {
        this.diskIOMetricsList = diskIOMetricsList;
    }

    public void setDiskMetricsList(List<DiskMetrics> diskMetricsList) {
        this.diskMetricsList = diskMetricsList;
    }

    public void setNetCardMetricsList(List<NetCardMetrics> netCardMetricsList) {
        this.netCardMetricsList = netCardMetricsList;
    }

    public void setAgentBusinessMetrics(AgentBusinessMetrics agentBusinessMetrics) {
        this.agentBusinessMetrics = agentBusinessMetrics;
    }
}
