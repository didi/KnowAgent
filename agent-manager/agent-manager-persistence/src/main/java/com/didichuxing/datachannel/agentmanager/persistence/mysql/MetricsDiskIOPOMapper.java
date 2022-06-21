package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsDiskIOPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsDiskIOTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.persistence.MetricsDiskIODAO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "MySQLMetricsDiskIODAO")
public interface MetricsDiskIOPOMapper extends MetricsDiskIODAO {

}
