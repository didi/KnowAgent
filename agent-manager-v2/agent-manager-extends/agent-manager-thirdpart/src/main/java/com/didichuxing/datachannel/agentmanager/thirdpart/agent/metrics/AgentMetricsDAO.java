package com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;

import java.util.List;

public interface AgentMetricsDAO {

    /**
     * 获取容器在给定时间范围内给定容器在给定宿主机上关于给定日志采集路径对应 metric "sendcount = 0" 的心跳记录集数量
     * select count(*) from tb_agent_metrics where
     * logModelHostName = ${containerHostName} and
     * agentHostName = ${parentHostName} and
     * logModelId = ${logCollectTaskId} and
     * logPathId = {fileLogCollectPathId} and
     * heartbeatTime >= ${heartbeatStartTime} and
     * heartbeatTime <= ${heartbeatEndTime} and
     * sendCount == 0
     *
     * @param containerHostName    容器名
     * @param parentHostName       容器对应宿主机名
     * @param logCollectTaskId     日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param heartbeatStartTime   agent 开始心跳时间
     * @param heartbeatEndTime     agent 心跳结束时间
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    Long getContainerSendCountEqualsZeroRecordSize(String containerHostName, String parentHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException;

    /**
     * 获取容器在给定时间范围内给定容器在给定宿主机上关于给定日志采集路径对应 metric "sendcount > 0" 的心跳记录集数量
     * select count(*) from tb_agent_metrics where
     * logModelHostName = ${containerHostName} and
     * agentHostName = ${parentHostName} and
     * logModelId = ${logCollectTaskId} and
     * logPathId = {fileLogCollectPathId} and
     * heartbeatTime >= ${heartbeatStartTime} and
     * heartbeatTime <= ${heartbeatEndTime} and
     * sendCount > 0
     *
     * @param containerHostName    容器名
     * @param parentHostName       容器对应宿主机名
     * @param logCollectTaskId     日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param heartbeatStartTime   agent 开始心跳时间
     * @param heartbeatEndTime     agent 心跳结束时间
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    Long getContainerSendCountGtZeroRecordSize(String containerHostName, String parentHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException;

    /**
     * 获取主机在给定时间范围内关于给定日志采集路径对应 metric "sendcount = 0" 的心跳记录集数量
     * select count(*) from tb_agent_metrics where
     * logModelHostName = ${hostName} and
     * logModelId = ${logCollectTaskId} and
     * logPathId = {fileLogCollectPathId} and
     * heartbeatTime >= ${heartbeatStartTime} and
     * heartbeatTime <= ${heartbeatEndTime} and
     * sendCount == 0
     *
     * @param logModelHostName     主机名
     * @param logCollectTaskId     日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param heartbeatStartTime   agent 开始心跳时间
     * @param heartbeatEndTime     agent 心跳结束时间
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    Long getHostSendCountEqualsZeroRecordSize(String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException;

    /**
     * 获取主机在给定时间范围内关于给定日志采集路径对应 metric "sendcount > 0" 的心跳记录集数量
     * select count(*) from tb_agent_metrics where
     * logModelHostName = ${hostName} and
     * logModelId = ${logCollectTaskId} and
     * logPathId = {fileLogCollectPathId} and
     * heartbeatTime >= ${heartbeatStartTime} and
     * heartbeatTime <= ${heartbeatEndTime} and
     * sendCount > 0
     *
     * @param logModelHostName     主机名
     * @param logCollectTaskId     日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param heartbeatStartTime   agent 开始心跳时间
     * @param heartbeatEndTime     agent 心跳结束时间
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    Long getHostSendCountGtZeroRecordSize(String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException;

    /**
     * 获取给定日志采集任务在指定主机名对应主机上在给定agent心跳时间范围内心跳次数
     * SELECT COUNT(*)
     * FROM tb_agent_metrics
     * WHERE logModelId = ${logCollectTaskId} AND
     * filePathId = ${fileLogCollectPathId} AND
     * logModelHostName = ${logCollectTaskHostName}
     * heartbeatTime > ${startTime} AND
     * heartbeatTime <= ${endTime}
     *
     * @param startTime              agent 心跳时间开始区间
     * @param endTime                agent 心跳时间结束区间
     * @param logCollectTaskId       日志采集任务 id
     * @param fileLogCollectPathId   日志采集 path id
     * @param logCollectTaskHostName 日志采集任务运行主机名
     * @return 返回获取到的给定日志采集任务在指定主机名对应主机上在给定agent心跳时间范围内心跳次数
     */
    Long getHeartbeatTimesByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName);

    /**
     * 获取 agent 在给定时间范围内心跳次数，sql 形式 如下：
     * SELECT COUNT(*)
     *  FROM tb_agent_metrics
     *  WHERE hostName = #{hostName} AND
     *      heartbeatTime > startTime AND heartbeatTime <= endTime
     * @param startTime 开始时间时间戳
     * @param endTime 结束时间时间戳
     * @param hostName agent 主机名
     * @return 返回 agent 在给定时间范围内流量限流总时长
     */
    Long getHeartBeatTimes(Long startTime, Long endTime, String hostName);

    /**
     * 获取 logCollectTaskId+fileLogCollectPathId+logCollectTaskhostName 在给定时间范围内存在的待采集文件不存在次数，sql 形式 如下：
     * SELECT COUNT(*)
     * FROM tb_agent_metrics
     * WHERE logCollectTaskId = #{logCollectTaskId} AND
     * fileLogCollectPathId = #{fileLogCollectPathId} AND
     * logCollectTaskhostName = #{logCollectTaskhostName} AND
     * heartbeatTime > startTime AND heartbeatTime <= endTime AND
     * isFileExist = true
     *
     * @param startTime              心跳开始时间
     * @param endTime                心跳结束时间
     * @param logCollectTaskId       日志采集任务 id
     * @param fileLogCollectPathId   日志采集路径 id
     * @param logCollectTaskHostName 主机名
     * @return 返回 logCollectTaskId+fileLogCollectPathId+logCollectTaskhostName 在给定时间范围内存在的待采集文件不存在次数
     */
    Integer getFilePathNotExistsCountByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName);

    /**
     * 获取 logCollectTaskId+fileLogCollectPathId+logCollectTaskHostName 在给定时间范围内存在的日志被异常截断数量，sql 形式 如下：
     * SELECT SUM(filterTooLargeCount)
     * FROM tb_agent_metrics
     * WHERE logCollectTaskId = #{logCollectTaskId} AND
     * fileLogCollectPathId = #{fileLogCollectPathId} AND
     * logCollectTaskHostName = #{logCollectTaskHostName} AND
     * heartbeatTime > startTime AND heartbeatTime <= endTime
     *
     * @param startTime              心跳开始时间
     * @param endTime                心跳结束时间
     * @param logCollectTaskId       日志采集任务 id
     * @param fileLogCollectPathId   日志采集路径 id
     * @param logCollectTaskHostName 主机名
     * @return 返回 logCollectTaskId+fileLogCollectPathId+logCollectTaskHostName 在给定时间范围内存在的日志被异常截断数量
     */
    Integer getAbnormalTruncationCountByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName);

    /**
     * 获取 logCollectTaskId+fileLogCollectPathId+hostName 在给定时间范围内存在待采集文件乱序心跳数量，sql 形式 如下：
     * SELECT COUNT(*)
     * FROM tb_agent_metrics
     * WHERE logCollectTaskId = #{logCollectTaskId} AND
     * fileLogCollectPathId = #{fileLogCollectPathId} AND
     * hostName = #{hostName} AND
     * isFileDisorder = true AND
     * heartbeatTime > startTime AND heartbeatTime <= endTime
     *
     * @param startTime            心跳开始时间
     * @param endTime              心跳结束时间
     * @param logCollectTaskId     日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param logModelHostName     主机名
     * @return 返回 logCollectTaskId+fileLogCollectPathId+hostName 在给定时间范围内存在待采集文件乱序心跳数量
     */
    Integer getFileDisorderCount(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName);

    /**
     * 获取 logCollectTaskId+fileLogCollectPathId+hostName 在给定时间范围内存在的日志切片错误数量，sql 形式 如下：
     * SELECT COUNT(*)
     * FROM tb_agent_metrics
     * WHERE logCollectTaskId = #{logCollectTaskId} AND
     * fileLogCollectPathId = #{fileLogCollectPathId} AND
     * hostName = #{hostName} AND
     * heartbeatTime > startTime AND heartbeatTime <= endTime AND
     * isVaildTimeConfig = false
     *
     * @param startTime            心跳开始时间
     * @param endTime              心跳结束时间
     * @param logCollectTaskId     日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param logModelHostName     主机名
     * @return 返回 logCollectTaskId+fileLogCollectPathId+hostName 在给定时间范围内存在的日志切片错误数量
     */
    Integer getSliceErrorCount(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName);

    /**
     * 获取最后一次采集的时间
     *
     * @param logCollectTaskId
     * @param fileLogCollectPathId
     * @param logModelHostName
     * @return
     */
    Long getLatestCollectTime(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName);

    Long getLatestStartupTime(String hostName);

    /**
     * @param startTime
     * @param endTime
     * @param hostName
     * @return
     */
    Long getHostCpuLimitDuration(Long startTime, Long endTime, String hostName);

    /**
     * @param startTime
     * @param endTime
     * @param hostName
     * @return
     */
    Long getHostByteLimitDuration(Long startTime, Long endTime, String hostName);

    Long getHostByteLimitDuration(Long startTime, Long endTime, String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId);

    Integer getErrorLogCount(Long startTime, Long endTime, String hostName);

    Integer getLatestFdUsage(String hostName);

    Double getLatestCpuUsage(String hostName);

    Long getLatestMemoryUsage(String hostName);

    Long getGCCount(Long startTime, Long endTime, String hostName);

    List<MetricPoint> getAgentCpuUsagePerMin(Long startTime, Long endTime, String hostName);

    List<MetricPoint> getAgentMemoryUsagePerMin(Long startTime, Long endTime, String hostName);

    List<MetricPoint> getAgentGCTimesPerMin(Long startTime, Long endTime, String hostName);

    List<MetricPoint> getAgentOutputBytesPerMin(Long startTime, Long endTime, String hostName);

    List<MetricPoint> getAgentOutputLogsPerMin(Long startTime, Long endTime, String hostName);

    List<MetricPoint> getAgentFdUsagePerMin(Long startTime, Long endTime, String hostName);

    List<MetricPoint> getAgentStartupExistsPerMin(Long startTime, Long endTime, String hostName);

    List<MetricPoint> getLogCollectTaskBytesPerMin(Long taskId, Long startTime, Long endTime);

    List<MetricPoint> getLogCollectTaskLogCountPerMin(Long taskId, Long startTime, Long endTime);

    List<MetricPoint> getFileLogPathNotExistsPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime);

    List<MetricPoint> getFileLogPathDisorderPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime);

    List<MetricPoint> getFilterOutPerLogPathPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime);

    List<MetricPoint> getMinCurrentCollectTimePerLogPathPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime);

    List<MetricPoint> getFileLogPathLogSliceErrorPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime);

    List<MetricPoint> getFileLogPathAbnormalTruncationPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime);

}
