package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.metrics.DashBoardStatisticsDO;
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

    List<MetricPoint> selectFileDisorderPerMin(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("pathId") Long pathId);

    List<MetricPoint> selectAvgPerMin(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("pathId") Long pathId, @Param("column") String column);

    List<MetricPoint> selectSumPerMin(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("pathId") Long pathId, @Param("column") String column);

    List<MetricPoint> selectMinPerMin(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("pathId") Long pathId, @Param("column") String column);

    List<MetricPoint> selectDelayTimePerMin(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskId") Long taskId, @Param("hostName") String hostName, @Param("pathId") Long pathId);

    List<MetricPoint> selectByTask(@Param("taskId") Long logCollectTaskId, @Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("column") String column);

    List<MetricPoint> selectAggregationByTask(@Param("taskId") Long logCollectTaskId, @Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("column") String column, @Param("function") String function, @Param("step") Integer step);

    List<MetricPoint> selectAggregationByHostname(@Param("hostname") String hostname, @Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("column") String column, @Param("function") String function, @Param("step") Integer step);

    List<MetricPoint> selectByLogModel(@Param("taskId") Long logCollectTaskId, @Param("hostName") String hostName, @Param("pathId") Long pathId, @Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("column") String column);

    List<MetricPoint> selectAggregationByLogModel(@Param("taskId") Long logCollectTaskId, @Param("hostName") String hostName, @Param("pathId") Long pathId, @Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("column") String column, @Param("function") String function, @Param("step") Integer step);

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

    CollectTaskMetricPO selectLatest(@Param("taskId") Long taskId);

    List<CollectTaskMetricPO> selectLatestMetrics(@Param("time") Long time, @Param("step") Integer step);

    Double selectAggregationForAll(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("column") String column, @Param("function") String function);

    List<DashBoardStatisticsDO> groupByKeyAndMinute(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("key") String key, @Param("function") String function, @Param("metric") String metric);

    List<MetricPoint> selectAggregationByAgent(@Param("agentHostName") String agentHostName, @Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("column") String column, @Param("function") String function);

    List<MetricPoint> selectAggregationGroupByMinute(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("column") String column, @Param("function") String function);

}
