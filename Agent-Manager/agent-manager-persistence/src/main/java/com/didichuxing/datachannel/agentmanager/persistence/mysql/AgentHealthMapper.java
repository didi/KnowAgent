package com.didichuxing.datachannel.agentmanager.persistence.mysql;


import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.health.AgentHealthPO;
import org.springframework.stereotype.Repository;

@Repository(value = "agentHealthDAO")
public interface AgentHealthMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AgentHealthPO record);

    int insertSelective(AgentHealthPO record);

    AgentHealthPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AgentHealthPO record);

    int updateByPrimaryKey(AgentHealthPO record);

    void deleteByAgentId(Long agentId);

    AgentHealthPO selectByAgentId(Long agentId);
}