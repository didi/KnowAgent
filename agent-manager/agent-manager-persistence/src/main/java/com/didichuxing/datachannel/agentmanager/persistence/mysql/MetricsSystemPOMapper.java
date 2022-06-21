package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.persistence.MetricsSystemDAO;
import org.springframework.stereotype.Repository;

@Repository(value = "mySQLMetricsSystemDAO")
public interface MetricsSystemPOMapper extends MetricsSystemDAO {

}
