package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.LogCollectTaskHealthDetailPO;

public interface LogCollectTaskHealthDetailPOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LogCollectTaskHealthDetailPO record);

    int insertSelective(LogCollectTaskHealthDetailPO record);

    LogCollectTaskHealthDetailPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LogCollectTaskHealthDetailPO record);

    int updateByPrimaryKey(LogCollectTaskHealthDetailPO record);
}