package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskHealthPO;
import org.springframework.stereotype.Repository;

@Repository(value = "logCollectTaskHealthDAO")
public interface LogCollectTaskHealthMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LogCollectTaskHealthPO record);

    LogCollectTaskHealthPO selectByPrimaryKey(Long id);

    int updateByPrimaryKey(LogCollectTaskHealthPO record);

    int deleteByLogCollectTaskId(Long logCollectTaskId);

    LogCollectTaskHealthPO selectByLogCollectTaskId(Long logCollectTaskId);

}