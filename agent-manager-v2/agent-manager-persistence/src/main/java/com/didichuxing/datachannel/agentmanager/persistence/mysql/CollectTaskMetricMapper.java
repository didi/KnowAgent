package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.CollectTaskMetricPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectTaskMetricMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CollectTaskMetricPO record);

    int insertSelective(CollectTaskMetricPO record);

    CollectTaskMetricPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CollectTaskMetricPO record);

    int updateByPrimaryKey(CollectTaskMetricPO record);

    List<CollectTaskMetricPO> selectSome(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("pathId") Long pathId);

    List<MetricPoint> selectSumByHostnamePerMin(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("hostName") String hostName, @Param("column") String column);

    List<MetricPoint> selectSumByTaskIdPerMin(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("column") String column);

    List<MetricPoint> selectSingleByTaskIdPerMin(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("column") String column);

    List<MetricPoint> selectSinglePerMin(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("pathId") Long pathId, @Param("column") String column);

    List<MetricPoint> selectMinPerMin(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("pathId") Long pathId, @Param("column") String column);

    int deleteBeforeTime(@Param("time") Long time);

    Long selectContainerCountEqualsZero(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("containerName") String containerName, @Param("column") String column);

    Long selectContainerCountGtZero(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("containerName") String containerName, @Param("column") String column);

    Long selectSingleCountEqualsZero(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("pathId") Long pathId, @Param("column") String column);

    Long selectSingleCountGtZero(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("pathId") Long pathId, @Param("column") String column);

    Long selectSingleCountLtZero(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("pathId") Long pathId, @Param("column") String column);

    Long selectSingleHeartbeatCount(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("pathId") Long pathId);

    Long selectHeartbeatCountByHostname(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("hostName") String hostName);

    Long selectHeartbeatCount(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("pathId") Long pathId);

    Long selectSingleCountWithTerm(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("pathId") Long pathId, @Param("column") String column, @Param("value") Object value);

    Long selectSingleSum(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("pathId") Long pathId, @Param("column") String column);

    Long selectSumByHostname(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("hostName") String hostName, @Param("column") String column);

    Object selectSingleMax(@Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("pathId") Long pathId, @Param("column") String column);

    Object selectMaxByHostname(@Param("hostName") String hostName, @Param("column") String column);

}