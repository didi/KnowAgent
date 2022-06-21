package com.didichuxing.datachannel.agentmanager.persistence;

import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.ErrorLogPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AgentErrorLogDAO {

    int insertSelective(ErrorLogPO record);

    int deleteBeforeTime(@Param("time") Long time);

    List<String> getErrorLogs(@Param("hostName") String hostName, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

}
