package com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskPaginationRecordDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.MetricQueryDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricAggregate;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricList;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanelGroup;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPointList;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务管理服务接口
 */
public interface LogCollectTaskManageService {

    /**
     * 创建一个日志采集任务信息
     * @param logCollectTask 待添加日志采集任务对象
     * @param operator 操作人
     * @return 创建成功的日志采集任务对象id
     */
    Long createLogCollectTask(LogCollectTaskDO logCollectTask, String operator);

    /**
     * 删除一个日志采集任务对象
     * @param id 待删除日志采集任务id
     * @param operator 操作人
     */
    void deleteLogCollectTask(Long id, String operator);

    /**
     * 更新一个日志采集任务对象
     * @param logCollectTask 待更新日志采集任务对象
     * @param operator 操作人
     */
    void updateLogCollectTask(LogCollectTaskDO logCollectTask, String operator);

    /**
     * 根据主机id获取需要运行在该主机上所有的日志采集任务对象集
     * @param hostId 主机id
     * @return 需要运行在该主机上所有的日志采集任务对象集
     */
    List<LogCollectTaskDO> getLogCollectTaskListByHostId(Long hostId);

    /**
     * 根据给定 id 获取对应 LogCollectTaskPO 对象
     * @param id 日志采集任务 id
     * @return 返回根据给定 id 获取对应 LogCollectTaskPO 对象
     */
    LogCollectTaskDO getById(Long id);

    /**
     * 启/停指定日志采集任务
     * @param logCollectTaskId 日志采集任务id
     * @param status 启/停状态
     * @param operator 操作人
     */
    void switchLogCollectTask(Long logCollectTaskId, Integer status, String operator);

    /**
     * 根据给定参数分页查询结果集
     * @param logCollectTaskPaginationQueryConditionDO 分页查询条件
     * @return 返回根据给定参数分页查询到的结果集
     */
    List<LogCollectTaskPaginationRecordDO> paginationQueryByConditon(LogCollectTaskPaginationQueryConditionDO logCollectTaskPaginationQueryConditionDO);

    /**
     * 根据给定参数查询满足条件的结果集总数量
     * @param logCollectTaskPaginationQueryConditionDO 查询条件
     * @return 返回根据给定参数查询到的满足条件的结果集总数量
     */
    Integer queryCountByCondition(LogCollectTaskPaginationQueryConditionDO logCollectTaskPaginationQueryConditionDO);

    /**
     * 根据日志采集任务 id 获取给定时间范围内对应日志采集任务运行时指标信息
     * @param logCollectTaskId 日志采集任务 id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回根据日志采集任务 id 获取到的给定时间范围内对应日志采集任务运行时指标信息
     */
    List<MetricPanelGroup> listLogCollectTaskMetrics(Long logCollectTaskId, Long startTime, Long endTime);

    /**
     * 日志采集任务对应某个待采集路径在具体某主机采集指标信息
     * @param logCollectTaskId 日志采集任务 id
     * @param logPathId 日志采集路径 id
     * @param hostName 主机名
     * @param startTime 指标信息查询开始时间
     * @param endTime 指标信息查询结束时间
     * @return 返回日志采集任务对应某个待采集路径在具体某主机采集指标信息
     */
    List<MetricPanelGroup> listLogCollectTaskMetricsPerHostAndPath(Long logCollectTaskId, Long logPathId, String hostName, Long startTime, Long endTime);

    List<MetricAggregate> getAliveHostCount(MetricQueryDO metricQueryDO);

    MetricList getCollectDelayMetric(MetricQueryDO metricQueryDO);

    MetricList getMinLogTime(MetricQueryDO metricQueryDO);

    MetricList getLimitTime(MetricQueryDO metricQueryDO);

    MetricList getAbnormalTruncation(MetricQueryDO metricQueryDO);

    MetricList getCollectPathExists(MetricQueryDO metricQueryDO);

    MetricList getIsFileOrder(MetricQueryDO metricQueryDO);

    MetricList getSliceError(MetricQueryDO metricQueryDO);

    MetricList getReadByte(MetricQueryDO metricQueryDO);

    MetricList getReadCount(MetricQueryDO metricQueryDO);

    MetricList getTotalReadTime(MetricQueryDO metricQueryDO);

    MetricList getReadTimeMean(MetricQueryDO metricQueryDO);

    MetricList getReadTimeMax(MetricQueryDO metricQueryDO);

    MetricList getSendBytes(MetricQueryDO metricQueryDO);

    MetricList getSendCount(MetricQueryDO metricQueryDO);

    MetricList getTotalSendTime(MetricQueryDO metricQueryDO);

    MetricList getFlushCount(MetricQueryDO metricQueryDO);

    MetricList getFlushTimeMax(MetricQueryDO metricQueryDO);

    MetricList getFlushTimeMean(MetricQueryDO metricQueryDO);

    MetricList getFlushFailedCount(MetricQueryDO metricQueryDO);

    MetricList getFilterCount(MetricQueryDO metricQueryDO);

    Long getCollectBytesToday();

    Long getCollectCountToday();

    Long getCurrentCollectBytes();

    Long getCurrentCollectCount();

    List<MetricPointList> getTop5HostCount(Long startTime, Long endTime);

    List<MetricPointList> getTop5AgentCount(Long startTime, Long endTime);

    /**
     * 检查给定日志采集任务健康度，具体包括如下操作：
     *  1.）检查给定日志采集任务健康度，并将检查结果信息存入对应 LogCollectTaskHealth 记录表
     *  2.）计算给定日志采集任务各采集路径的采集完整性时间，并将其存入对应 LogCollectTaskHealth 记录表
     *  3.）如给定日志采集任务对应采集类型为 "时间范围采集" 类型，则判断其是否已采集完毕，如已采集完毕，更新对应 LogCollectTask 记录 log_collect_task_finish_time 字段值为当前时间
     * @param logCollectTaskDO 待检查日志采集任务对象
     * @return 返回给定日志采集任务健康度检查结果 注：如待巡检日志采集任务处于暂停、或已完成、或处于健康度检查黑名单中，无需检查，return LogCollectTaskHealthLevelEnum.GREEN
     */
    LogCollectTaskHealthLevelEnum checkLogCollectTaskHealth(LogCollectTaskDO logCollectTaskDO);

    /**
     * 获取系统中所有需要被检查健康度的日志采集任务集
     * @return 返回系统中所有需要被检查健康度的日志采集任务集
     */
    List<LogCollectTaskDO> getAllLogCollectTask2HealthCheck();

    /**
     * 获取需要运行给定主机上所有的日志采集任务对象集
     * @param hostDO 主机对象
     * @return 需要运行在该主机上所有的日志采集任务对象集
     */
    List<LogCollectTaskDO> getLogCollectTaskListByHost(HostDO hostDO);

    /**
     * 获取serviceId对应Service对象关联的LogCollectTask对象集
     * @param serviceId Service对象id值
     * @return 返回获取serviceId对应Service对象关联的LogCollectTask对象集
     */
    List<LogCollectTaskDO> getLogCollectTaskListByServiceId(Long serviceId);

    /**
     * 根据 agentId 获取对应 agent 所有待采集的日志采集任务集
     * @param agentId agent 对象 id 值
     * @return 根据 agentId 获取到的对应 agent 所有待采集的日志采集任务集
     */
    List<LogCollectTaskDO> getLogCollectTaskListByAgentId(Long agentId);

    /**
     * 获取给定kafkaClusterId对应KafkaCluster对象关联的LogCollectTaskDO对象集
     * @param kafkaClusterId KafkaCluster对象id值
     * @return 返回给定kafkaClusterId对应KafkaCluster对象关联的LogCollectTaskDO对象集
     */
    List<LogCollectTaskDO> getLogCollectTaskListByKafkaClusterId(Long kafkaClusterId);

    /**
     * @return 返回系统日志采集任务总数
     */
    Long countAll();

    /**
     * @return 返回系统全量日志采集任务 id 集
     */
    List<Long> getAllIds();

    /**
     * 校验 logcollecttask 是否未关联主机
     * @param logCollectTaskId logcollecttask 对象 id
     * @return true：logcollecttask 未关联任何主机 false：logcollecttask 存在关联主机
     */
    boolean checkNotRelateAnyHost(Long logCollectTaskId);

    /**
     * @param logCollectTaskHealthLevelCode 日志采集任务健康度对应 code（对应枚举类 LogCollectTaskHealthLevelEnum）
     * @return 返回系统中给定健康度的日志采集任务对象集
     */
    List<LogCollectTaskDO> getByHealthLevel(Integer logCollectTaskHealthLevelCode);

}
