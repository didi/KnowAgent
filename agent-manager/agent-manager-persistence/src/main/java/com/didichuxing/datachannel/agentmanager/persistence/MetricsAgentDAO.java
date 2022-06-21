package com.didichuxing.datachannel.agentmanager.persistence;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsAgentPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;

import java.util.List;
import java.util.Map;

public interface MetricsAgentDAO {

    /**
     * 插入给定指标数据
     * @param record 待插入指标数据
     * @return 非 0 表示插入成功
     */
    int insertSelective(MetricsAgentPO record);

    /**
     * 根据 id 获取对应指标记录
     * @param id 指标 id
     * @return 返回根据 id 获取到的对应指标记录
     */
    MetricsAgentPO selectByPrimaryKey(Long id);

    /**
     * @param params
     *  fieldName：字段名
     *  hostName：agent宿主机主机名
     *  startTime：心跳开始时间戳
     *  endTime：心跳结束时间戳
     */
    Object getLast(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  hostName：agent宿主机主机名
     *  startTime：心跳开始时间戳
     *  endTime：心跳结束时间戳
     */
    List<MetricPoint> getSingleChatNonStatistic(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  hostName：agent宿主机主机名
     *  startTime：心跳开始时间戳
     *  endTime：心跳结束时间戳
     */
    List<MetricPoint> getSingleChatStatistic(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  startTime：心跳开始时间戳
     *  endTime：心跳结束时间戳
     */
    Double getSumMetricAllAgents(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  sortTime：排序时间戳（精度：分钟）
     *  topN：前n条记录
     *  sortType：排序方式 desc、asc
     *  sortTimeField：sortTimeField：排序字段名
     */
    List<MetricsLogCollectTaskTopPO> getTopNByMetricPerHostName(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  hostName：主机名
     *  startTime：心跳开始时间戳
     *  endTime：心跳结束时间戳
     */
    List<MetricPoint> getSingleChatStatisticByHostName(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  hostName：主机名
     *  startTime：心跳开始时间戳
     *  endTime：心跳结束时间戳
     */
    List<MetricPoint> getSingleChatNonStatisticByHostName(Map<String, Object> params);

    /**
     * 删除给定心跳时间戳之前所有指标数据
     * @param heartBeatTime 心跳时间戳
     */
    void deleteByLtHeartbeatTime(Long heartBeatTime);

    /**
     * @param params
     *  hostName：日志采集任务运行主机名
     *  startTime：心跳开始时间（>）
     *  endTime：心跳结束时间(<=)
     *  function：聚合函数名
     *  fieldName：聚合字段名
     */
    Object getAggregationQueryPerHostNameFromMetricsAgent(Map<String, Object> params);

    /**
     * @param params
     *  hostName：主机名
     * @return 返回最后一个 agent 指标数据
     */
    MetricsAgentPO getLastRecord(Map<String, Object> params);

    /**
     * @param params 含：
     *  hostName：主机名
     *  startHeartbeatTime：开始时间戳（不含）
     *  endHeartbeatTime：结束时间戳（含）
     * @return 返回根据给定参数获取到的心跳信息集
     */
    List<MetricsAgentPO> getErrorMetrics(Map<String, Object> params);

}
