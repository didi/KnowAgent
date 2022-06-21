package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Component("mySQLErrorLogsDAOFactory")
public class MySQLErrorLogsDAOFactory implements ErrorLogsDAOFactory {

    @Autowired
    private ErrorLogMapper agentErrorLogDAO;

    @Override
    public AgentErrorLogDAO getAgentErrorLogDAO() {
        return agentErrorLogDAO;
    }

}
