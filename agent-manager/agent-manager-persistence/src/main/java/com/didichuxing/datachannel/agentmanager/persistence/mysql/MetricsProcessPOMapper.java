package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsProcessPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.persistence.MetricsProcessDAO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "MySQLMetricsProcessDAO")
public interface MetricsProcessPOMapper extends MetricsProcessDAO {

}
