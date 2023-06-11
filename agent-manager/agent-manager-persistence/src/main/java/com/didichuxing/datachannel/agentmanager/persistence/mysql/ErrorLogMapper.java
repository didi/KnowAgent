package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.persistence.AgentErrorLogDAO;
import org.springframework.stereotype.Repository;

@Repository("MySQLAgentErrorLogDAO")
public interface ErrorLogMapper extends AgentErrorLogDAO {

}