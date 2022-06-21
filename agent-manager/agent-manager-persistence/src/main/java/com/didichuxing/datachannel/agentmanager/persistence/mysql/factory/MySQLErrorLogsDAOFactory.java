package com.didichuxing.datachannel.agentmanager.persistence.mysql.factory;

import com.didichuxing.datachannel.agentmanager.persistence.*;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.ErrorLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@org.springframework.stereotype.Component("MySQLErrorLogsDAOFactory")
public class MySQLErrorLogsDAOFactory implements ErrorLogsDAOFactory {

    @Autowired
    @Qualifier(value = "MySQLAgentErrorLogDAO")
    private AgentErrorLogDAO agentErrorLogDAO;

    @Override
    public AgentErrorLogDAO createAgentErrorLogDAO() {
        return agentErrorLogDAO;
    }

}
