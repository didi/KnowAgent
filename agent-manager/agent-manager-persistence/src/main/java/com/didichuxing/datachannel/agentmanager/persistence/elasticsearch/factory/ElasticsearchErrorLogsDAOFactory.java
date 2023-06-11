package com.didichuxing.datachannel.agentmanager.persistence.elasticsearch.factory;

import com.didichuxing.datachannel.agentmanager.persistence.AgentErrorLogDAO;
import com.didichuxing.datachannel.agentmanager.persistence.ErrorLogsDAOFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@org.springframework.stereotype.Component("ElasticsearchErrorLogsDAOFactory")
public class ElasticsearchErrorLogsDAOFactory implements ErrorLogsDAOFactory {

    @Autowired
    @Qualifier(value = "ElasticsearchAgentErrorLogDAO")
    private AgentErrorLogDAO agentErrorLogDAO;

    @Override
    public AgentErrorLogDAO createAgentErrorLogDAO() {
        return agentErrorLogDAO;
    }

}
