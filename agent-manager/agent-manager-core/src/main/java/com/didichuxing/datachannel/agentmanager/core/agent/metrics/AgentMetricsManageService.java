package com.didichuxing.datachannel.agentmanager.core.agent.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;

import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * AgentPO 指标管理服务接口
 */
public interface AgentMetricsManageService {

    /**
     * 校验给定主机上的待采集日志是否已被采集完成
     * @param hostDO 待校验主机对象
     * @return true：已完成采集 false：未完成采集
     */
    boolean completeCollect(HostDO hostDO);

    /**
     * 获取 logCollectTaskId+fileLogCollectPathId+hostName 在给定时间范围内流量限流总时长，sql 形式 如下：
     * SELECT SUM(byte_limit_dturation)
     *  FROM tb_agent_metrics
     *  WHERE logCollectTaskId = #{logCollectTaskId} AND
     *        fileLogCollectPathId = #{fileLogCollectPathId} AND
     *        hostName = #{hostName} AND
     *        heartbeatTime BETWEEN startTime AND endTime
     * @param startTime 开始时间时间戳
     * @param endTime 结束时间时间戳
     * @param logCollectTaskId 日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param logModelHostName 主机名
     * @return 返回logCollectTaskId+fileLogCollectPathId+hostName 在给定时间范围内流量限流总时长
     */
    Long getHostByteLimitDurationByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName);

    /**
     * 获取 logCollectTaskId+fileLogCollectPathId+hostName 在给定时间范围内心跳次数，sql 形式 如下：
     * SELECT COUNT(*)
     *  FROM tb_agent_metrics
     *  WHERE logModelId = #{logCollectTaskId} AND
     *      filePathId = #{fileLogCollectPathId} AND
     *      logModelHostName = #{logCollectTaskHostName} AND
     *      heartbeatTime > startTime AND heartbeatTime <= endTime
     * @param startTime 开始时间时间戳
     * @param endTime 结束时间时间戳
     * @param logCollectTaskId 日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param logCollectTaskHostName 日志采集任务须运行主机名
     * @return 返回logCollectTaskId+fileLogCollectPathId+hostName 在给定时间范围内流量限流总时长
     */
    Long getHeartbeatTimesByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName);

    /**
     * 获取 logCollectTaskId+fileLogCollectPathId+hostName 最近采集时间，sql 形式 如下：
     * SELECT logTime
     *  FROM tb_agent_metrics
     *  WHERE logCollectTaskId = #{logCollectTaskId} AND
     *      fileLogCollectPathId = #{fileLogCollectPathId} AND
     *      hostName = #{hostName} AND
     *      heartbeatTime > startTime AND heartbeatTime <= endTime
     *  ORDER BY logTime DESC
     *  LIMIT 1
     * @param logCollectTaskId 日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param hostName 主机名
     * @return 返回 logCollectTaskId+fileLogCollectPathId+hostName 最近采集时间
     */
    Long getLastestCollectTime(Long logCollectTaskId, Long fileLogCollectPathId, String hostName);

    /**
     * 获取 logCollectTaskId+fileLogCollectPathId+logCollectTaskhostName 在给定时间范围内存在的待采集文件不存在次数，sql 形式 如下：
     * SELECT COUNT(isFileExist)
     *  FROM tb_agent_metrics
     *  WHERE logCollectTaskId = #{logCollectTaskId} AND
     *        fileLogCollectPathId = #{fileLogCollectPathId} AND
     *        logCollectTaskhostName = #{logCollectTaskhostName} AND
     *        heartbeatTime > startTime AND heartbeatTime <= endTime
     * @param logCollectTaskHealthLastestCheckTime 心跳开始时间
     * @param logCollectTaskHealthCheckTimeEnd 心跳结束时间
     * @param logCollectTaskId 日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param logCollectTaskhostName 主机名
     * @return 返回 logCollectTaskId+fileLogCollectPathId+logCollectTaskhostName 在给定时间范围内存在的待采集文件不存在次数
     */
    Integer getFilePathNotExistsCountByTimeFrame(Long logCollectTaskHealthLastestCheckTime, Long logCollectTaskHealthCheckTimeEnd, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskhostName);

    /**
     * 获取 logCollectTaskId+fileLogCollectPathId+hostName 在给定时间范围内存在待采集文件乱序心跳数量，sql 形式 如下：
     * SELECT sum(isFileDisorder)
     *  FROM tb_agent_metrics
     *  WHERE logCollectTaskId = #{logCollectTaskId} AND
     *        fileLogCollectPathId = #{fileLogCollectPathId} AND
     *        hostName = #{hostName} AND
     *        heartbeatTime > startTime AND heartbeatTime <= endTime
     * @param logCollectTaskHealthLastestCheckTime 心跳开始时间
     * @param logCollectTaskHealthCheckTimeEnd 心跳结束时间
     * @param logCollectTaskId 日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param hostName 主机名
     * @return 返回 logCollectTaskId+fileLogCollectPathId+hostName 在给定时间范围内存在待采集文件乱序心跳数量
     */
    Integer getFileDisorderCountByTimeFrame(Long logCollectTaskHealthLastestCheckTime, Long logCollectTaskHealthCheckTimeEnd, Long logCollectTaskId, Long fileLogCollectPathId, String hostName);

    /**
     * 获取 logCollectTaskId+fileLogCollectPathId+hostName 在给定时间范围内存在的日志切片错误数量，sql 形式 如下：
     * SELECT COUNT(*)
     *  FROM tb_agent_metrics
     *  WHERE logCollectTaskId = #{logCollectTaskId} AND
     *        fileLogCollectPathId = #{fileLogCollectPathId} AND
     *        hostName = #{hostName} AND
     *        heartbeatTime > startTime AND heartbeatTime <= endTime AND
     *        isVaildTimeConfig = false
     * @param logCollectTaskHealthLastestCheckTime 心跳开始时间
     * @param logCollectTaskHealthCheckTimeEnd 心跳结束时间
     * @param logCollectTaskId 日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param hostName 主机名
     * @return 返回 logCollectTaskId+fileLogCollectPathId+hostName 在给定时间范围内存在的日志切片错误数量
     */
    Integer getSliceErrorCount(Long logCollectTaskHealthLastestCheckTime, Long logCollectTaskHealthCheckTimeEnd, Long logCollectTaskId, Long fileLogCollectPathId, String hostName);

    /**
     * 获取 logCollectTaskId+fileLogCollectPathId+logCollectTaskHostName 在给定时间范围内存在的日志被异常截断数量，sql 形式 如下：
     * SELECT SUM(filterTooLargeCount)
     *  FROM tb_agent_metrics
     *  WHERE logCollectTaskId = #{logCollectTaskId} AND
     *        fileLogCollectPathId = #{fileLogCollectPathId} AND
     *        logCollectTaskHostName = #{logCollectTaskHostName} AND
     *        heartbeatTime > startTime AND heartbeatTime <= endTime
     * @param logCollectTaskHealthLastestCheckTime 心跳开始时间
     * @param logCollectTaskHealthCheckTimeEnd 心跳结束时间
     * @param logCollectTaskId 日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param logCollectTaskHostName 主机名
     * @return 返回 logCollectTaskId+fileLogCollectPathId+logCollectTaskHostName 在给定时间范围内存在的日志被异常截断数量
     */
    Integer getAbnormalTruncationCountByTimeFrame(Long logCollectTaskHealthLastestCheckTime, Long logCollectTaskHealthCheckTimeEnd, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName);

    /**
     * 校验给定容器上给定路径的日志是否已采完
     * @param containerHostName 容器名
     * @param parentHostName 容器宿主机名
     * @param logCollectTaskId 日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @return true：容器已采集完 false：容器未采集完
     * @throws ServiceException 执行该函数过程中出现的异常
     * 注：容器之所以须指定对应宿主机名，原因为：容器名不会变，但宿主机可能会变（ps：容器漂移场景）
     */
    boolean containerCompleteCollect(String containerHostName, String parentHostName, Long logCollectTaskId, Long fileLogCollectPathId);

    /**
     * 校验给定主机上给定路径的日志是否已采完
     * @param hostName 主机名
     * @param logCollectTaskId 日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @return true：容器已采集完 false：容器未采集完
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    boolean hostCompleteCollect(String hostName, Long logCollectTaskId, Long fileLogCollectPathId);

    /**
     * 获取 agent 在给定时间范围内 cpu 限流总时长，sql 形式 如下：
     * SELECT SUM(cpu_limit_dturation)
     *  FROM tb_agent_metrics
     *  WHERE hostName = #{hostName} AND
     *        heartbeatTime > startTime AND heartbeatTime <= endTime
     * @param startTime 开始时间时间戳
     * @param endTime 结束时间时间戳
     * @param hostName 主机名
     * @return 返回 hostName 在给定时间范围内 cpu 限流总时长
     */
    Long getHostCpuLimiDturationByTimeFrame(Long startTime, Long endTime, String hostName);

    /**
     * 获取 agent 在给定时间范围内流量限流总时长，sql 形式 如下：
     * SELECT SUM(byte_limit_dturation)
     *  FROM tb_agent_metrics
     *  WHERE hostName = #{hostName} AND
     *        heartbeatTime > startTime AND heartbeatTime <= endTime
     * @param startTime 开始时间时间戳
     * @param endTime 结束时间时间戳
     * @param hostName 主机名
     * @return 返回 hostName 在给定时间范围内流量限流总时长
     */
    Long getHostByteLimitDurationByTimeFrame(Long startTime, Long endTime, String hostName);

    /**
     * 获取 agent 最近一次心跳上报对应 fd 使用量，sql 形式 如下：
     * SELECT fdUsage
     *  FROM tb_agent_metrics
     *  WHERE hostName = #{hostName} AND
     *      ORDER BY heartbeatTime DESC
     *      LIMIT 1
     * @param hostName agent 主机名
     * @return 返回 agent 最近一次心跳上报对应 fd 使用量
     */
    Integer getLastestFdUsage(String hostName);

    /**
     * 获取 agent 在给定时间范围内存在的错误日志输出数量，sql 形式 如下：
     * SELECT COUNT(*)
     *  FROM tb_agent_errlogs
     *  WHERE hostName = #{hostName} AND
     *        heartbeatTime > startTime AND heartbeatTime <= endTime
     * @param lastestCheckTimeStart 心跳开始时间
     * @param agentHealthCheckTimeEnd 心跳结束时间
     * @param hostName agent 主机名
     * @return 返回 hostName 在给定时间范围内存在的错误日志输出数量
     */
    Integer getErrorLogCount(Long lastestCheckTimeStart, Long agentHealthCheckTimeEnd, String hostName);

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
    Long getHeartbeatTimesByTimeFrame(Long startTime, Long endTime, String hostName);

    /**
     * 获取 agent 最近一次心跳上报对应 cpu 使用量，sql 形式 如下：
     * SELECT cpuUsage
     *  FROM tb_agent_metrics
     *  WHERE hostName = #{hostName}
     *      ORDER BY heartbeatTime DESC
     *      LIMIT 1
     * @param hostName agent 主机名
     * @return 返回 agent 最近一次心跳上报对应 fd 使用量
     */
    Integer getLastestCpuUsage(String hostName);

    /**
     * 获取 agent 最近一次启动时间，sql 形式 如下：
     * SELECT MAX(agent_startup_time)
     *  FROM tb_agent_metrics
     *  WHERE hostName = #{hostName}
     * @param hostName agent 主机名
     * @return 返回 agent 最近一次启动时间
     */
    Long getLastestAgentStartupTime(String hostName);

    /**
     * 获取 agent 在给定时间范围内 full gc 次数，sql 形式 如下：
     * SELECT COUNT(gc_times)
     *  FROM tb_agent_metrics
     *  WHERE hostName = #{hostName} AND
     *      heartbeatTime > startTime AND heartbeatTime <= endTime
     * @param startTime 开始时间时间戳
     * @param endTime 结束时间时间戳
     * @param hostName agent 主机名
     * @return 返回 agent 在给定时间范围内 full gc 次数
     */
    Long getAgentFullgcTimesByTimeFrame(Long startTime, Long endTime, String hostName);

    /**
     * 获取agent cpu 使用率/分钟指标信息
     * @param hostName agent所在主机名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回获取到的agent cpu 使用率/分钟指标信息
     */
    List<MetricPoint> getAgentCpuUsagePerMinMetric(String hostName, Long startTime, Long endTime);

    /**
     * 获取agent 内存使用/分钟指标信息
     * @param hostName agent所在主机名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回获取到的agent 内存使用/分钟指标信息
     */
    List<MetricPoint> getAgentMemoryUsagePerMinMetric(String hostName, Long startTime, Long endTime);

    /**
     * 获取agent fullgc次数/分钟指标信息
     * @param hostName agent所在主机名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回获取到的agent fullgc次数/分钟指标信息
     */
    List<MetricPoint> getAgentFullGcTimesPerMinMetric(String hostName, Long startTime, Long endTime);

    /**
     * 获取agent出口流量/分钟指标信息
     * @param hostName agent所在主机名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回获取到的agent出口流量/分钟指标信息
     */
    List<MetricPoint> getAgentOutputBytesPerMinMetric(String hostName, Long startTime, Long endTime);

    /**
     * 获取agent出口条数/分钟指标信息
     * @param hostName agent所在主机名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回获取到的agent出口条数/分钟指标信息
     */
    List<MetricPoint> getAgentOutputLogsCountPerMinMetric(String hostName, Long startTime, Long endTime);

    /**
     * 获取agent fd 使用量/分钟指标信息
     * @param hostName agent所在主机名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回获取到的agent fd 使用量/分钟指标信息
     */
    List<MetricPoint> getAgentFdUsagePerMinMetric(String hostName, Long startTime, Long endTime);

    /**
     * 获取Agent是否存在启动/分钟指标信息
     * @param hostName agent所在主机名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回获取到的Agent是否存在启动/分钟指标信息
     */
    List<MetricPoint> getAgentStartupExistsPerMinMetric(String hostName, Long startTime, Long endTime);

    /**
     * 获取日志采集任务对应日志采集流量 bytes/分钟信息
     * @param logCollectTaskId 日志采集任务id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回获取到的日志采集任务对应日志采集流量 bytes/分钟信息
     */
    List<MetricPoint> getLogCollectTaskLogsBytesPerMinMetric(Long logCollectTaskId, Long startTime, Long endTime);

    /**
     * 获取日志采集任务对应日志采集条数/分钟信息
     * @param logCollectTaskId 日志采集任务id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回获取到的日志采集任务对应日志采集流量条数/分钟信息
     */
    List<MetricPoint> getLogCollectTaskLogsCountPerMinMetric(Long logCollectTaskId, Long startTime, Long endTime);

    /**
     * 查询给定采集路径在给定主机是否存在待采集路径无法找到对应待采集文件/分钟
     * @param logCollectTaskId 日志采集任务id
     * @param fileLogCollectPathId 日志采集路径id
     * @param hostName 主机名
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 返回查询到的给定采集路径在给定主机是否存在待采集路径无法找到对应待采集文件/分钟
     */
    List<MetricPoint> getFileLogPathExistsPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime);

    /**
     * 查询给定采集路径在给定主机是否存在乱序/分钟
     * @param logCollectTaskId 日志采集任务id
     * @param fileLogCollectPathId 日志采集路径id
     * @param hostName 主机名
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 返回查询到的给定采集路径在给定主机是否存在乱序/分钟
     */
    List<MetricPoint> getFileLogPathDisorderPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime);

    /**
     * 查询给定采集路径在给定主机是否存日志切片错误/分钟
     * @param logCollectTaskId 日志采集任务id
     * @param fileLogCollectPathId 日志采集路径id
     * @param hostName 主机名
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 返回查询到的给定采集路径在给定主机是否存日志切片错误/分钟
     */
    List<MetricPoint> getFileLogPathLogSliceErrorPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime);

    /**
     * 查询给定采集路径在给定主机是否存日志异常截断/分钟
     * @param logCollectTaskId 日志采集任务id
     * @param fileLogCollectPathId 日志采集路径id
     * @param hostName 主机名
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 返回查询到的给定采集路径在给定主机是否存日志异常截断/分钟
     */
    List<MetricPoint> getFileLogPathAbnormalTruncationPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime);

    /**
     * 查询给定采集路径在给定主机日志过滤条数/分钟
     * @param logCollectTaskId 日志采集任务id
     * @param fileLogCollectPathId 日志采集路径id
     * @param hostName 主机名
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 返回查询到的给定采集路径在给定主机日志过滤条数/分钟
     */
    List<MetricPoint> getFilterOutPerLogPathPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime);

    /**
     * 获取给定日志采集任务 & 采集路径 & 主机在给定时间段内每分钟最小采集时间
     * @param logCollectTaskId 日志采集任务id
     * @param fileLogCollectPathId 日志采集路径id
     * @param hostName 主机名
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 返回获取到的给定日志采集任务 & 采集路径 & 主机在给定时间段内每分钟最小采集时间
     */
    List<MetricPoint> getMinCurrentCollectTimePerLogPathPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime);

    List<MetricPoint> getLimitTimePerLogPathPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime);

}
