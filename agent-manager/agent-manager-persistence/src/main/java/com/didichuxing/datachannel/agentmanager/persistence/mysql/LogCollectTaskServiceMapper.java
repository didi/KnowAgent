package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskServicePO;
import org.springframework.stereotype.Repository;

@Repository(value = "metricsSystemDAO")
public interface LogCollectTaskServiceMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LogCollectTaskServicePO record);

    int insertSelective(LogCollectTaskServicePO record);

    LogCollectTaskServicePO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LogCollectTaskServicePO record);

    int updateByPrimaryKey(LogCollectTaskServicePO record);

    int deleteByLogCollectTaskId(Long logCollectTaskId);

    int deleteByServiceId(Long serviceId);
}