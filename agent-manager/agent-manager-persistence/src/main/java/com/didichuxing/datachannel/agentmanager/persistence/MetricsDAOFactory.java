package com.didichuxing.datachannel.agentmanager.persistence;

public interface MetricsDAOFactory {

    MetricsSystemDAO createMetricsSystemDAO();

    MetricsAgentDAO createMetricsAgentDAO();

    MetricsDiskDAO createMetricsDiskDAO();

    MetricsNetCardDAO createMetricsNetCardDAO();

    MetricsProcessDAO createMetricsProcessDAO();

    MetricsLogCollectTaskDAO createMetricsLogCollectTaskDAO();

    MetricsDiskIODAO createMetricsDiskIODAO();

}
