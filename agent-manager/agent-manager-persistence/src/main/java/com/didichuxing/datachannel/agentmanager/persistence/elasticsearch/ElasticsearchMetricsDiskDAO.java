package com.didichuxing.datachannel.agentmanager.persistence.elasticsearch;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsDiskPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsDiskTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.persistence.MetricsDiskDAO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "ElasticsearchMetricsDiskDAO")
public class ElasticsearchMetricsDiskDAO implements MetricsDiskDAO {
    @Override
    public int insertSelective(MetricsDiskPO record) {
        return 0;
    }

    @Override
    public Object getLast(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricPoint> getSingleChatNonStatisticByPath(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricPoint> getSingleChatStatisticByPath(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricsDiskTopPO> getTopNDiskPath(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricsDiskPO> selectAll() {
        return null;
    }

    @Override
    public void deleteByLtHeartbeatTime(Long heartBeatTime) {

    }
}
