package com.didichuxing.datachannel.agentmanager.persistence.elasticsearch.factory;

import com.didichuxing.datachannel.agentmanager.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@org.springframework.stereotype.Component("ElasticsearchMetricsDAOFactory")
public class ElasticsearchMetricsDAOFactory implements MetricsDAOFactory {

    @Autowired
    @Qualifier(value = "ElasticsearchMetricsSystemDAO")
    private MetricsSystemDAO metricsSystemDAO;

    @Autowired
    @Qualifier(value = "ElasticsearchMetricsAgentDAO")
    private MetricsAgentDAO metricsAgentDAO;

    @Autowired
    @Qualifier(value = "ElasticsearchMetricsDiskDAO")
    private MetricsDiskDAO metricsDiskDAO;

    @Autowired
    @Qualifier(value = "ElasticsearchMetricsNetCardDAO")
    private MetricsNetCardDAO metricsNetCardDAO;

    @Autowired
    @Qualifier(value = "ElasticsearchMetricsProcessDAO")
    private MetricsProcessDAO metricsProcessDAO;

    @Autowired
    @Qualifier(value = "ElasticsearchMetricsLogCollectTaskDAO")
    private MetricsLogCollectTaskDAO metricsLogCollectTaskDAO;

    @Autowired
    @Qualifier(value = "ElasticsearchMetricsDiskIODAO")
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
