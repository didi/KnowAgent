package com.didichuxing.datachannel.agentmanager.persistence.elasticsearch;

import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.ErrorLogPO;
import com.didichuxing.datachannel.agentmanager.persistence.AgentErrorLogDAO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "ElasticsearchAgentErrorLogDAO")
public class ElasticsearchAgentErrorLogDAO implements AgentErrorLogDAO {
    @Override
    public int insertSelective(ErrorLogPO record) {
        return 0;
    }

    @Override
    public int deleteBeforeTime(Long time) {
        return 0;
    }

    @Override
    public List<String> getErrorLogs(String hostName, Long startTime, Long endTime) {
        return null;
    }
}
