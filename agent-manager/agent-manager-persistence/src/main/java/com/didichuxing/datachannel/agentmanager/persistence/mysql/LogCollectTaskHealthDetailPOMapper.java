package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDetailDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskHealthDetailPO;
import org.springframework.stereotype.Repository;

@Repository(value = "logCollectTaskHealthDetailDAO")
public interface LogCollectTaskHealthDetailPOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LogCollectTaskHealthDetailPO record);

    int insertSelective(LogCollectTaskHealthDetailPO record);

    LogCollectTaskHealthDetailPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LogCollectTaskHealthDetailPO record);

    int updateByPrimaryKey(LogCollectTaskHealthDetailPO record);

    LogCollectTaskHealthDetailDO select(Long logCollectTaskId, Long pathId, String hostName);

}