package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsDiskPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsDiskTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.persistence.MetricsDiskDAO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "MySQLMetricsDiskDAO")
public interface MetricsDiskPOMapper extends MetricsDiskDAO {

}