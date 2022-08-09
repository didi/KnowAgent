package com.didichuxing.datachannel.agentmanager.persistence;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskIdTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsServiceNamesTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;

import java.util.List;
import java.util.Map;

public interface MetricsLogCollectTaskDAO {

    /**
     * 插入给定指标数据
     * @param record 待插入指标数据
     * @return 非 0 表示插入成功
     */
    int insertSelective(MetricsLogCollectTaskPO record);

    /**
     * 根据 id 获取对应指标记录
     * @param id 指标 id
     * @return 返回根据 id 获取到的对应指标记录
     */
    MetricsLogCollectTaskPO selectByPrimaryKey(Long id);

    /**
     * @param params
     *  fieldName：字段名
     *  logCollectTaskId：日志采集任务id
     *  pathId：日志采集路径id（optional）
     *  hostName：日志采集任务对应主机名（optional）
     *  startTime：心跳开始时间戳
     *  endTime：心跳结束时间戳
     */
    Object getLast(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  logCollectTaskId：日志采集任务id
     *  pathId：日志采集路径id（optional）
     *  hostName：日志采集任务对应主机名（optional）
     *  startTime：心跳开始时间戳
     *  endTime：心跳结束时间戳
     */
    List<MetricPoint> getSingleChatNonStatistic(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  logCollectTaskId：日志采集任务id
     *  pathId：日志采集路径id（optional）
     *  hostName：日志采集任务对应主机名（optional）
     *  startTime：心跳开始时间戳
     *  endTime：心跳结束时间戳
     */
    List<MetricPoint> getSingleChatStatistic(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  logCollectTaskId：日志采集任务id
     *  pathId：日志采集路径id（optional）
     *  hostName：日志采集任务对应主机名（optional）
     *  sortTime：排序时间戳（精度：分钟）
     *  topN：前n条记录
     *  sortType：排序方式 desc、asc
     */
    List<MetricsLogCollectTaskTopPO> getTopNByHostName(Map<String, Object> params);

    /**
     * @param params 含：
     *  logCollectTaskId：日志采集任务id
     *  pathId：日志采集路径id
     *  hostName：主机名
     *  errorFieldName：待查错误对应字段名
     *  startHeartbeatTime：开始时间戳（不含）
     *  endHeartbeatTime：结束时间戳（含）
     * @return 返回根据给定参数获取到的心跳信息集
     */
    List<MetricsLogCollectTaskPO> getErrorMetrics(Map params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  sortTime：排序时间戳（精度：分钟）
     *  topN：前n条记录
     *  sortType：排序方式 desc、asc
     *  sortTimeField：sortTimeField：排序字段名
     */
    List<MetricsLogCollectTaskIdTopPO> getTopNByMetricPerLogCollectTaskId(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  logCollectTaskId：日志采集任务 id
     *  startTime：心跳开始时间戳
     *  endTime：心跳结束时间戳
     */
    List<MetricPoint> getSingleChatStatisticByLogCollectTaskId(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  logCollectTaskId：日志采集任务 id
     *  startTime：心跳开始时间戳
     *  endTime：心跳结束时间戳
     */
    List<MetricPoint> getSingleChatNonStatisticByLogCollectTaskId(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  sortTime：排序时间戳（精度：分钟）
     *  topN：前n条记录
     *  sortType：排序方式 desc、asc
     *  sortTimeField：sortTimeField：排序字段名
     */
    List<MetricsServiceNamesTopPO> getTopNByMetricPerServiceNames(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  serviceNames：服务名集
     *  startTime：心跳开始时间戳
     *  endTime：心跳结束时间戳
     */
    List<MetricPoint> getSingleChatNonStatisticByServiceNames(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  serviceNames：服务名集
     *  startTime：心跳开始时间戳
     *  endTime：心跳结束时间戳
     */
    List<MetricPoint> getSingleChatStatisticByServiceNames(Map<String, Object> params);

    /**
     * 删除给定心跳时间戳之前所有指标数据
     * @param heartBeatTime 心跳时间戳
     */
    void deleteByLtHeartbeatTime(Long heartBeatTime);

    /**
     * @param params
     *  logCollectTaskId：日志采集任务 id
     *  pathId：日志采集路径 id
     *  hostName：日志采集任务运行主机名
     *  startTime：心跳开始时间（>）
     *  endTime：心跳结束时间(<=)
     *  function：聚合函数名
     *  fieldName：聚合字段名
     */
    Object aggregationQueryPerLogCollectTskAndPathAndHostNameFromMetricsLogCollectTask(Map<String, Object> params);

    /**
     * @param params
     *  logCollectTaskId：日志采集任务 id
     *  pathId：日志采集路径 id
     *  hostName：日志采集任务运行主机名
     * @return 返回最后一个日志采集任务指标数据
     */
    MetricsLogCollectTaskPO getLastRecord(Map<String, Object> params);

    /**
     * 根据给定日志采集任务id、日志采集路径id、日志采集任务对应主机名，获取对应日志采集任务的最近一次指标集
     * @param params
     *  logCollectTaskId：日志采集任务 id
     *  logCollectPathId：日志采集路径 id
     *  hostName：日志采集任务对应主机名
     * @return 返回根据给定日志采集任务id、日志采集路径id、日志采集任务对应主机名，获取到的对应日志采集任务的最近一次指标集
     */
    MetricsLogCollectTaskPO getLatestMetrics(Map<String, Object> params);

    /**
     * @param params
     *  logCollectTaskId：日志采集任务 id
     *  pathId：日志采集路径 id
     *  hostName：日志采集任务运行主机名
     *  startTime：心跳开始时间（>）
     *  endTime：心跳结束时间(<=)
     *  conditionFieldName：where 条件字段名
     *  operatorType：where 条件操作符类型
     *  conditionFieldValue：where 条件字段值
     *  function：聚合函数名
     *  fieldName：聚合字段名
     */
    Object getAggregationQueryPerLogCollectTskAndPathAndHostNameWithConditionFromMetricsLogCollectTask(Map<String, Object> params);

}
