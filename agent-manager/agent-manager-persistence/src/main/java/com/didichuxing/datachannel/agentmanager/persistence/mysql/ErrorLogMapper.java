package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.ErrorLogPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ErrorLogPO record);

    int insertSelective(ErrorLogPO record);

    ErrorLogPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ErrorLogPO record);

    int updateByPrimaryKey(ErrorLogPO record);

    Long selectCount(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("hostName") String hostName);

    int deleteBeforeTime(@Param("time") Long time);

    List<String> getErrorLogs(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

}