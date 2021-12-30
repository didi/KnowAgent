package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.MetricsAgentPO;

public interface MetricsAgentPOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MetricsAgentPO record);

    int insertSelective(MetricsAgentPO record);

    MetricsAgentPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MetricsAgentPO record);

    int updateByPrimaryKey(MetricsAgentPO record);
}