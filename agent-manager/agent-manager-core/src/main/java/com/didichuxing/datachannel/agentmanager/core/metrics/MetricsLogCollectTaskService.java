package com.didichuxing.datachannel.agentmanager.core.metrics;

public interface MetricsLogCollectTaskService {

    /**
     * 获取最近一次日志采集业务时间
     * @param logCollectTaskId 采集任务id
     * @param pathId 采集路径id
     * @param collectTaskHostName 运行采集任务主机名
     */
    Long getLastCollectBusinessTime(Long logCollectTaskId, Long pathId, String collectTaskHostName);



}
