package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Component("mySQLMetricsDAOFactory")
public class MySQLMetricsDAOFactory implements MetricsDAOFactory {

    @Autowired
    private MetricsSystemPOMapper metricsSystemDAO;

    @Autowired
    private MetricsAgentPOMapper metricsAgentDAO;

    @Autowired
    private MetricsDiskPOMapper metricsDiskDAO;

    @Autowired
    private MetricsNetCardPOMapper metricsNetCardDAO;

    @Autowired
    private MetricsProcessPOMapper metricsProcessDAO;

    @Autowired
    private MetricsLogCollectTaskPOMapper metricsLogCollectTaskDAO;

    @Autowired
    private MetricsDiskIOPOMapper metricsDiskIODAO;

    @Override
    public MetricsSystemDAO getMetricsSystemDAO() {
        return metricsSystemDAO;
    }

    @Override
    public MetricsAgentDAO getMetricsAgentDAO() {
        return metricsAgentDAO;
    }

    @Override
    public MetricsDiskDAO getMetricsDiskDAO() {
        return metricsDiskDAO;
    }

    @Override
    public MetricsNetCardDAO getMetricsNetCardDAO() {
        return metricsNetCardDAO;
    }

    @Override
    public MetricsProcessDAO getMetricsProcessDAO() {
        return metricsProcessDAO;
    }

    @Override
    public MetricsLogCollectTaskDAO getMetricsLogCollectTaskDAO() {
        return metricsLogCollectTaskDAO;
    }

    @Override
    public MetricsDiskIODAO getMetricsDiskIODAO() {
        return metricsDiskIODAO;
    }

}
