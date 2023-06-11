package com.didichuxing.datachannel.agentmanager.persistence.elasticsearch;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsProcessPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.persistence.MetricsProcessDAO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "ElasticsearchMetricsProcessDAO")
public class ElasticsearchMetricsProcessDAO implements MetricsProcessDAO {
    @Override
    public int insertSelective(MetricsProcessPO record) {
        return 0;
    }

    @Override
    public Object getLast(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricPoint> getSingleChatNonStatistic(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricPoint> getSingleChatStatistic(Map<String, Object> params) {
        return null;
    }

    @Override
    public Double getSumMetricAllAgents(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricsLogCollectTaskTopPO> getTopNByMetricPerHostName(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricPoint> getSingleChatStatisticByHostName(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricPoint> getSingleChatNonStatisticByHostName(Map<String, Object> params) {
        return null;
    }

    @Override
    public void deleteByLtHeartbeatTime(Long heartBeatTime) {

    }

    @Override
    public MetricsProcessPO getLastRecord(Map<String, Object> params) {
        return null;
    }

    @Override
    public Object getAggregationQueryPerHostNameFromMetricsProcess(Map<String, Object> params) {
        return null;
    }
}
