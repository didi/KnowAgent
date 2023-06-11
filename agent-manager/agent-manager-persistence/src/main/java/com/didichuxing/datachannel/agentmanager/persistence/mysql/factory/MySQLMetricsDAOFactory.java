package com.didichuxing.datachannel.agentmanager.persistence.mysql.factory;

import com.didichuxing.datachannel.agentmanager.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@org.springframework.stereotype.Component("MySQLMetricsDAOFactory")
public class MySQLMetricsDAOFactory implements MetricsDAOFactory {

    @Autowired
    @Qualifier(value = "MySQLMetricsSystemDAO")
    private MetricsSystemDAO metricsSystemDAO;

    @Autowired
    @Qualifier(value = "MySQLMetricsAgentDAO")
    private MetricsAgentDAO metricsAgentDAO;

    @Autowired
    @Qualifier(value = "MySQLMetricsDiskDAO")
    private MetricsDiskDAO metricsDiskDAO;

    @Autowired
    @Qualifier(value = "MySQLMetricsNetCardDAO")
    private MetricsNetCardDAO metricsNetCardDAO;

    @Autowired
    @Qualifier(value = "MySQLMetricsProcessDAO")
    private MetricsProcessDAO metricsProcessDAO;

    @Autowired
    @Qualifier(value = "MySQLMetricsLogCollectTaskDAO")
    private MetricsLogCollectTaskDAO metricsLogCollectTaskDAO;

    @Autowired
    @Qualifier(value = "MySQLMetricsDiskIODAO")
    private MetricsDiskIODAO metricsDiskIODAO;

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
