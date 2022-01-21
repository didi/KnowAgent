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
     *  sortTime：排序时间戳（精度：分钟）
     *  topN：前n条记录
     */
    List<MetricsLogCollectTaskTopPO> getTopNByHostName(Map<String, Object> params);

}