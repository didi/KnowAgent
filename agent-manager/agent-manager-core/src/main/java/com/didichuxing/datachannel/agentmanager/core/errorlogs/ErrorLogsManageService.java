package com.didichuxing.datachannel.agentmanager.core.errorlogs;

import java.util.List;

public interface ErrorLogsManageService {

    /**
     * 添加错误日志记录集
     * @param errorLogRecord 待添加错误日志记录对象
     */
    void insertErrorLogs(String errorLogRecord);

    /**
     * 消费 & 写入错误日志
     */
    void consumeAndWriteErrorLogs();

    /**
     * 获取给定时间范围内给定agent错误日志集
     * @param hostName agent 宿主机名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回获取到的给定时间范围内给定agent错误日志集
     */
    List<String> getErrorLogs(String hostName, Long startTime, Long endTime);

    void clearExpireErrorLogs(Integer metricsExpireDays);
}
