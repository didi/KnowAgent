package com.didichuxing.datachannel.agentmanager.rest;

import com.didichuxing.datachannel.agentmanager.persistence.ErrorLogsDAOFactory;
import com.didichuxing.datachannel.agentmanager.persistence.MetricsDAOFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@org.springframework.stereotype.Component
public class StorageFactoryBuilder {

    @Autowired
    @Qualifier(value = "MySQLMetricsDAOFactory")
    private MetricsDAOFactory mySQLMetricsDAOFactory;

    @Autowired
    @Qualifier(value = "MySQLErrorLogsDAOFactory")
    private ErrorLogsDAOFactory errorLogsDAOFactory;

    public MetricsDAOFactory buildMetricsDAOFactory() {
        return mySQLMetricsDAOFactory;
    }

    public ErrorLogsDAOFactory buildErrorLogsDAOFactory() {
        return errorLogsDAOFactory;
    }

}
