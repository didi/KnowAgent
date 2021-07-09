package com.didichuxing.datachannel.agentmanager.remote.operation.task.common;

import java.util.List;

public class AgentOperationTaskCreation {
    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务类型 0：安装 1：卸载 2：升级
     */
    private Integer taskType;

    /**
     * agent安装包名称
     */
    private String agentPackageName;

    /**
     * agent安装包md5
     */
    private String agentPackageMd5;

    /**
     * agent安装包下载地址
     */
    private String agentPackageDownloadUrl;

    /**
     * 操作的机器
     */
    private List<String> hostList;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getAgentPackageName() {
        return agentPackageName;
    }

    public void setAgentPackageName(String agentPackageName) {
        this.agentPackageName = agentPackageName;
    }

    public String getAgentPackageMd5() {
        return agentPackageMd5;
    }

    public void setAgentPackageMd5(String agentPackageMd5) {
        this.agentPackageMd5 = agentPackageMd5;
    }

    public String getAgentPackageDownloadUrl() {
        return agentPackageDownloadUrl;
    }

    public void setAgentPackageDownloadUrl(String agentPackageDownloadUrl) {
        this.agentPackageDownloadUrl = agentPackageDownloadUrl;
    }

    public List<String> getHostList() {
        return hostList;
    }

    public void setHostList(List<String> hostList) {
        this.hostList = hostList;
    }

    @Override
    public String toString() {
        return "AgentOperationTaskCreation{" +
                "taskName='" + taskName + '\'' +
                ", taskType=" + taskType +
                ", agentPackageName='" + agentPackageName + '\'' +
                ", agentPackageMd5='" + agentPackageMd5 + '\'' +
                ", agentPackageDownloadUrl='" + agentPackageDownloadUrl + '\'' +
                ", hostList=" + hostList +
                '}';
    }
}
