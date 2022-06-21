package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsDiskTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsNetCardPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsNetCardTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.persistence.MetricsNetCardDAO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "MySQLMetricsNetCardDAO")
public interface MetricsNetCardPOMapper extends MetricsNetCardDAO {

}