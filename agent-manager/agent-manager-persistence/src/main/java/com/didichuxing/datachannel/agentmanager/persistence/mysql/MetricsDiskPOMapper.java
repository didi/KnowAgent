package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsDiskPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsDiskTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "metricsDiskDAO")
public interface MetricsDiskPOMapper {

    int insert(MetricsDiskPO record);

    int insertSelective(MetricsDiskPO record);

    MetricsDiskPO selectByPrimaryKey(Long id);

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
     *  path：磁盘挂载路径
     *  startTime：心跳开始时间戳
     *  endTime：心跳结束时间戳
     */
    List<MetricPoint> getSingleChatNonStatisticByPath(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  hostName：agent宿主机主机名
     *  path：磁盘挂载路径
     *  startTime：心跳开始时间戳
     *  endTime：心跳结束时间戳
     */
    List<MetricPoint> getSingleChatStatisticByPath(Map<String, Object> params);

    /**
     * @param params
     *  function：聚合函数名
     *  fieldName：字段名
     *  hostName：agent宿主机主机名
     *  sortTime：排序时间戳（精度：分钟）
     *  topN：前n条记录
     */
    List<MetricsDiskTopPO> getTopNDiskPath(Map<String, Object> params);

    List<MetricsDiskPO> selectAll();
}