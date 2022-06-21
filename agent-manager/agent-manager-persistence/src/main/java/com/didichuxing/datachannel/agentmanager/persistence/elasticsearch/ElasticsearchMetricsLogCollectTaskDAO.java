package com.didichuxing.datachannel.agentmanager.persistence.elasticsearch;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskIdTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsServiceNamesTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.persistence.MetricsLogCollectTaskDAO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "ElasticsearchMetricsLogCollectTaskDAO")
public class ElasticsearchMetricsLogCollectTaskDAO implements MetricsLogCollectTaskDAO {
    @Override
    public int insertSelective(MetricsLogCollectTaskPO record) {
        return 0;
    }

    @Override
    public MetricsLogCollectTaskPO selectByPrimaryKey(Long id) {
        return null;
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
    public List<MetricsLogCollectTaskTopPO> getTopNByHostName(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricsLogCollectTaskPO> getErrorMetrics(Map params) {
        return null;
    }

    @Override
    public List<MetricsLogCollectTaskIdTopPO> getTopNByMetricPerLogCollectTaskId(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricPoint> getSingleChatStatisticByLogCollectTaskId(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricPoint> getSingleChatNonStatisticByLogCollectTaskId(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricsServiceNamesTopPO> getTopNByMetricPerServiceNames(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricPoint> getSingleChatNonStatisticByServiceNames(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricPoint> getSingleChatStatisticByServiceNames(Map<String, Object> params) {
        return null;
    }

    @Override
    public void deleteByLtHeartbeatTime(Long heartBeatTime) {

    }

    @Override
    public Object aggregationQueryPerLogCollectTskAndPathAndHostNameFromMetricsLogCollectTask(Map<String, Object> params) {
        return null;
    }

    @Override
    public MetricsLogCollectTaskPO getLastRecord(Map<String, Object> params) {
        return null;
    }

    @Override
    public MetricsLogCollectTaskPO getLatestMetrics(Map<String, Object> params) {
        return null;
    }

    @Override
    public Object getAggregationQueryPerLogCollectTskAndPathAndHostNameWithConditionFromMetricsLogCollectTask(Map<String, Object> params) {
        return null;
    }
}
