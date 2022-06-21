package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.persistence.MetricsAgentDAO;
import org.springframework.stereotype.Repository;

@Repository(value = "MySQLMetricsAgentDAO")
public interface MetricsAgentPOMapper extends MetricsAgentDAO {

}
