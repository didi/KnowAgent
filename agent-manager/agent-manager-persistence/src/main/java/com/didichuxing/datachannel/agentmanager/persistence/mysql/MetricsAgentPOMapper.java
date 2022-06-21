package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.persistence.MetricsAgentDAO;
import org.springframework.stereotype.Repository;

@Repository(value = "mySQLMetricsAgentDAO")
public interface MetricsAgentPOMapper extends MetricsAgentDAO {

}
