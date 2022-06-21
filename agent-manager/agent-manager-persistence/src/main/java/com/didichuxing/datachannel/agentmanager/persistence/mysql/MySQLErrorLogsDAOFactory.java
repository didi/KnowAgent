package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@org.springframework.stereotype.Component("MySQLErrorLogsDAOFactory")
public class MySQLErrorLogsDAOFactory implements ErrorLogsDAOFactory {

    @Autowired
    @Qualifier(value = "MySQLAgentErrorLogDAO")
    private ErrorLogMapper agentErrorLogDAO;

    @Override
    public AgentErrorLogDAO createAgentErrorLogDAO() {
        return agentErrorLogDAO;
    }

}
