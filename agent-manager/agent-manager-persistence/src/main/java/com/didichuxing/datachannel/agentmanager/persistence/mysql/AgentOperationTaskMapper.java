package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.operationtask.AgentOperationTaskPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.operationtask.AgentOperationTaskPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "agentOperationTaskDAO")
public interface AgentOperationTaskMapper {

    int deleteByPrimaryKey(Long id);

    int insert(AgentOperationTaskPO record);

    int insertSelective(AgentOperationTaskPO record);

    AgentOperationTaskPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AgentOperationTaskPO record);

    int updateByPrimaryKey(AgentOperationTaskPO record);

    List<AgentOperationTaskPO> paginationQueryByConditon(AgentOperationTaskPaginationQueryConditionDO agentOperationTaskPaginationQueryConditionDO);

    Integer queryCountByConditon(AgentOperationTaskPaginationQueryConditionDO agentOperationTaskPaginationQueryConditionDO);

    List<AgentOperationTaskPO> selectByAgentVersionId(Long agentVersionId);

    void updateTaskNameByPrimaryKey(AgentOperationTaskPO agentOperationTaskPO);

    List<AgentOperationTaskPO> selectByTaskStatus(Integer status);

}
