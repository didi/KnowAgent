package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.MetricsLogCollectTaskPO;

public interface MetricsLogCollectTaskPOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MetricsLogCollectTaskPO record);

    int insertSelective(MetricsLogCollectTaskPO record);

    MetricsLogCollectTaskPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MetricsLogCollectTaskPO record);

    int updateByPrimaryKey(MetricsLogCollectTaskPO record);
}