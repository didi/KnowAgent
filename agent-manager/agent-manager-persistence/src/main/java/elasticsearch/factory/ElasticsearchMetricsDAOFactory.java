package elasticsearch.factory;

import com.didichuxing.datachannel.agentmanager.persistence.*;
import elasticsearch.*;

@org.springframework.stereotype.Component("ElasticsearchMetricsDAOFactory")
public class ElasticsearchMetricsDAOFactory implements MetricsDAOFactory {

    @Override
    public MetricsSystemDAO createMetricsSystemDAO() {
        return new ElasticsearchMetricsSystemDAO();
    }

    @Override
    public MetricsAgentDAO createMetricsAgentDAO() {
        return new ElasticsearchMetricsAgentDAO();
    }

    @Override
    public MetricsDiskDAO createMetricsDiskDAO() {
        return new ElasticsearchMetricsDiskDAO();
    }

    @Override
    public MetricsNetCardDAO createMetricsNetCardDAO() {
        return new ElasticsearchMetricsNetCardDAO();
    }

    @Override
    public MetricsProcessDAO createMetricsProcessDAO() {
        return new ElasticsearchMetricsProcessDAO();
    }

    @Override
    public MetricsLogCollectTaskDAO createMetricsLogCollectTaskDAO() {
        return new ElasticsearchMetricsLogCollectTaskDAO();
    }

    @Override
    public MetricsDiskIODAO createMetricsDiskIODAO() {
        return new ElasticsearchMetricsDiskIODAO();
    }

}
