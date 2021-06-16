package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.AgentPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "agentDAO")
public interface AgentMapper {
    int deleteByPrimaryKey(Long id);

    //已测
    int deleteByHostName(String hostName);

    //已测
    int insert(AgentPO record);

    int insertSelective(AgentPO record);

    AgentPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AgentPO record);

    int updateByPrimaryKey(AgentPO record);

    AgentPO selectByHostName(String hostName);

    List<String> listVersions();

    List<AgentPO> selectByAgentVersionId(Long agentVersionId);

    List<AgentPO> getAll();

    List<AgentPO> selectByKafkaClusterId(Long kafkaClusterId);

}
