package com.didichuxing.datachannel.agentmanager.persistence.elasticsearch;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsDiskIOPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsDiskIOTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.persistence.MetricsDiskIODAO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "ElasticsearchMetricsDiskIODAO")
public class ElasticsearchMetricsDiskIODAO implements MetricsDiskIODAO {
    @Override
    public int insertSelective(MetricsDiskIOPO record) {
        return 0;
    }

    @Override
    public Object getLast(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricPoint> getSingleChatNonStatisticByDevice(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricPoint> getSingleChatStatisticByDevice(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricsDiskIOTopPO> getTopNDiskDevice(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricsDiskIOPO> selectAll() {
        return null;
    }

    @Override
    public void deleteByLtHeartbeatTime(Long heartBeatTime) {

    }
}
