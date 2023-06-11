package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.operationtask.AgentOperationSubTaskPO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "agentOperationSubTaskDAO")
public interface AgentOperationSubTaskMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AgentOperationSubTaskPO record);

    AgentOperationSubTaskPO selectByPrimaryKey(Long id);

    int updateByPrimaryKey(AgentOperationSubTaskPO record);

    List<AgentOperationSubTaskPO> getByAgentOperationTaskId(Long agentOperationTaskId);

    List<AgentOperationSubTaskPO> selectByAgentVersionId(Long agentVersionId);

    List<AgentOperationSubTaskPO> getByHostNameAndAgentStartupTime(Map<String, Object> params);

}
