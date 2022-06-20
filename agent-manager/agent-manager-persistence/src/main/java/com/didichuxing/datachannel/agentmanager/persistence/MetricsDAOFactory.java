package com.didichuxing.datachannel.agentmanager.persistence;

public interface MetricsDAOFactory {

    MetricsSystemDAO getMetricsSystemDAO();

    MetricsAgentDAO getMetricsAgentDAO();

    MetricsDiskDAO getMetricsDiskDAO();

    MetricsNetCardDAO getMetricsNetCardDAO();

    MetricsProcessDAO getMetricsProcessDAO();

    MetricsLogCollectTaskDAO getMetricsLogCollectTaskDAO();

    MetricsDiskIODAO getMetricsDiskIODAO();

}
