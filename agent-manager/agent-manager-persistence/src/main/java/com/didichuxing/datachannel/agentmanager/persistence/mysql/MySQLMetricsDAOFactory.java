package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@org.springframework.stereotype.Component("mySQLMetricsDAOFactory")
public class MySQLMetricsDAOFactory implements MetricsDAOFactory {

    @Autowired
    @Qualifier(value = "mySQLMetricsSystemDAO")
    private MetricsSystemPOMapper metricsSystemDAO;

    @Autowired
    @Qualifier(value = "mySQLMetricsAgentDAO")
    private MetricsAgentPOMapper metricsAgentDAO;

    @Autowired
    @Qualifier(value = "mySQLMetricsDiskDAO")
    private MetricsDiskPOMapper metricsDiskDAO;

    @Autowired
    @Qualifier(value = "mySQLMetricsNetCardDAO")
    private MetricsNetCardPOMapper metricsNetCardDAO;

    @Autowired
    @Qualifier(value = "mySQLMetricsProcessDAO")
    private MetricsProcessPOMapper metricsProcessDAO;

    @Autowired
    @Qualifier(value = "mySQLMetricsLogCollectTaskDAO")
    private MetricsLogCollectTaskPOMapper metricsLogCollectTaskDAO;

    @Autowired
    @Qualifier(value = "mySQLMetricsDiskIODAO")
    private MetricsDiskIOPOMapper metricsDiskIODAO;

    @Override
    public MetricsSystemDAO createMetricsSystemDAO() {
        return metricsSystemDAO;
    }

    @Override
    public MetricsAgentDAO createMetricsAgentDAO() {
        return metricsAgentDAO;
    }

    @Override
    public MetricsDiskDAO createMetricsDiskDAO() {
        return metricsDiskDAO;
    }

    @Override
    public MetricsNetCardDAO createMetricsNetCardDAO() {
        return metricsNetCardDAO;
    }

    @Override
    public MetricsProcessDAO createMetricsProcessDAO() {
        return metricsProcessDAO;
    }

    @Override
    public MetricsLogCollectTaskDAO createMetricsLogCollectTaskDAO() {
        return metricsLogCollectTaskDAO;
    }

    @Override
    public MetricsDiskIODAO createMetricsDiskIODAO() {
        return metricsDiskIODAO;
    }

}
