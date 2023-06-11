package com.didichuxing.datachannel.agentmanager.persistence.elasticsearch;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsSystemPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.persistence.MetricsSystemDAO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "ElasticsearchMetricsSystemDAO")
public class ElasticsearchMetricsSystemDAO implements MetricsSystemDAO {
    @Override
    public int insertSelective(MetricsSystemPO record) {
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
    public MetricsSystemPO getLastRecord(Map<String, Object> params) {
        return null;
    }

    @Override
    public Double getSumMetricAllAgents(Map<String, Object> params) {
        return null;
    }
}
