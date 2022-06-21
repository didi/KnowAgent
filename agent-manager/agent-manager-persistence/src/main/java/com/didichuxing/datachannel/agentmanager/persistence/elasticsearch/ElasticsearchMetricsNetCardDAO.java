package com.didichuxing.datachannel.agentmanager.persistence.elasticsearch;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsNetCardPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsNetCardTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.persistence.MetricsNetCardDAO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "ElasticsearchMetricsNetCardDAO")
public class ElasticsearchMetricsNetCardDAO implements MetricsNetCardDAO {
    @Override
    public int insertSelective(MetricsNetCardPO record) {
        return 0;
    }

    @Override
    public Object getLast(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricPoint> getSingleChatNonStatisticByMacAddress(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricPoint> getSingleChatStatisticByMacAddress(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<MetricsNetCardTopPO> getTopNMacAddress(Map<String, Object> params) {
        return null;
    }

    @Override
    public void deleteByLtHeartbeatTime(Long heartBeatTime) {

    }
}
