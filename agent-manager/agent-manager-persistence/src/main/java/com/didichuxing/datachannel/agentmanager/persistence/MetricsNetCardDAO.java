package com.didichuxing.datachannel.agentmanager.persistence;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsNetCardPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsNetCardTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;

import java.util.List;
import java.util.Map;

public interface MetricsNetCardDAO {

    /**
     * 插入给定指标数据
     * @param record 待插入指标数据
     * @return 非 0 表示插入成功
     */
    int insertSelective(MetricsNetCardPO record);

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
     *  macAddress：mac地址
     *  startTime：心跳开始时间戳
     *  endTime：心跳结束时间戳
     */
    List<MetricPoint> getSingleChatNonStatisticByMacAddress(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  hostName：agent宿主机主机名
     *  macAddress：mac地址
     *  startTime：心跳开始时间戳
     *  endTime：心跳结束时间戳
     */
    List<MetricPoint> getSingleChatStatisticByMacAddress(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  hostName：agent宿主机主机名
     *  sortTime：排序时间戳（精度：分钟）
     *  topN：前n条记录
     *  sortType：排序方式 desc、asc
     */
    List<MetricsNetCardTopPO> getTopNMacAddress(Map<String, Object> params);

    /**
     * 删除给定心跳时间戳之前所有指标数据
     * @param heartBeatTime 心跳时间戳
     */
    void deleteByLtHeartbeatTime(Long heartBeatTime);

}
