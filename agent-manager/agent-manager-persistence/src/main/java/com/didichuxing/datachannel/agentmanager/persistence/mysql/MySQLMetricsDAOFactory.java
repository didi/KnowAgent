package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@org.springframework.stereotype.Component("MySQLMetricsDAOFactory")
public class MySQLMetricsDAOFactory implements MetricsDAOFactory {

    @Autowired
    @Qualifier(value = "MySQLMetricsSystemDAO")
    private MetricsSystemPOMapper metricsSystemDAO;

    @Autowired
    @Qualifier(value = "MySQLMetricsAgentDAO")
    private MetricsAgentPOMapper metricsAgentDAO;

    @Autowired
    @Qualifier(value = "MySQLMetricsDiskDAO")
    private MetricsDiskPOMapper metricsDiskDAO;

    @Autowired
    @Qualifier(value = "MySQLMetricsNetCardDAO")
    private MetricsNetCardPOMapper metricsNetCardDAO;

    @Autowired
    @Qualifier(value = "MySQLMetricsProcessDAO")
    private MetricsProcessPOMapper metricsProcessDAO;

    @Autowired
    @Qualifier(value = "MySQLMetricsLogCollectTaskDAO")
    private MetricsLogCollectTaskPOMapper metricsLogCollectTaskDAO;

    @Autowired
    @Qualifier(value = "MySQLMetricsDiskIODAO")
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
