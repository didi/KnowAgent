package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "metricsLogCollectTaskDAO")
public interface MetricsLogCollectTaskPOMapper {

    int insert(MetricsLogCollectTaskPO record);

    int insertSelective(MetricsLogCollectTaskPO record);

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

}