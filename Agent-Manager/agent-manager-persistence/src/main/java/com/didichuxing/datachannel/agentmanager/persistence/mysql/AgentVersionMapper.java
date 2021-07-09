package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.version.AgentVersionPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "agentVersionDAO")
public interface AgentVersionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AgentVersionPO record);

    int insertSelective(AgentVersionPO record);

    AgentVersionPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AgentVersionPO record);

    int updateByPrimaryKey(AgentVersionPO record);

    AgentVersionPO selectByVersion(String version);

    List<AgentVersionPO> selectAll();

    Integer queryCountByConditon(AgentVersionPaginationQueryConditionDO agentVersionPaginationQueryConditionDO);

    List<AgentVersionPO> paginationQueryByConditon(AgentVersionPaginationQueryConditionDO agentVersionPaginationQueryConditionDO);

    AgentVersionPO selectByfileMd5(String fileMd5);

}
