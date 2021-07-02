package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.AgentMetricPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentMetricMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AgentMetricPO record);

    int insertSelective(AgentMetricPO record);

    AgentMetricPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AgentMetricPO record);

    int updateByPrimaryKey(AgentMetricPO record);

    List<MetricPoint> selectSinglePerMin(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("hostName") String hostName, @Param("column") String column);

    AgentMetricPO selectLatestByHostname(@Param("hostname") String hostName);

    Long selectSum(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("hostName") String hostName, @Param("column") String column);

    List<MetricPoint> selectSumPerMin(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("hostName") String hostName, @Param("column") String column);

    int deleteBeforeTime(@Param("time") Long time);

    Long selectMaxByHostname(@Param("hostName") String hostName, @Param("column") String column);

    Long selectHeartbeatCount(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("hostName") String hostName);
}