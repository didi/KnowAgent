package com.didichuxing.datachannel.agentmanager.rest;

import com.didichuxing.datachannel.agentmanager.persistence.ErrorLogsDAOFactory;
import com.didichuxing.datachannel.agentmanager.persistence.MetricsDAOFactory;
import org.apache.commons.lang3.StringUtils;
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

    public MetricsDAOFactory buildMetricsDAOFactory(String storageType) {
        if(StringUtils.isBlank(storageType) || "rds".equals(storageType)) {
            return mySQLMetricsDAOFactory;
        }
        return null;
    }

    public ErrorLogsDAOFactory buildErrorLogsDAOFactory(String storageType) {
        if(StringUtils.isBlank(storageType) || "rds".equals(storageType)) {
            return errorLogsDAOFactory;
        }
        return null;
    }
}
