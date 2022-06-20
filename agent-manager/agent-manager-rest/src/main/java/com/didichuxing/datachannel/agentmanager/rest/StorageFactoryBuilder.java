package com.didichuxing.datachannel.agentmanager.rest;

import com.didichuxing.datachannel.agentmanager.persistence.MetricsDAOFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Component
public class StorageFactoryBuilder {

    @Autowired
    private MetricsDAOFactory metricsDAOFactory;

    public MetricsDAOFactory buildMetricsDAOFactory(String storageType) {
        if(StringUtils.isBlank(storageType) || "rds".equals(storageType)) {
            return metricsDAOFactory;
        }
        return null;
    }

}
