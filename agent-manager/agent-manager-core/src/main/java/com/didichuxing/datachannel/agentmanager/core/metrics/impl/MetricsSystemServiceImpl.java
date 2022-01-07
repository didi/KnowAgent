package com.didichuxing.datachannel.agentmanager.core.metrics.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.metrics.StatisticMetricPoint;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsSystemService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.MetricsSystemPOMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class MetricsSystemServiceImpl implements MetricsSystemService {

    @Autowired
    private MetricsSystemPOMapper metricsSystemDAO;

    @Override
    public List<StatisticMetricPoint> getNumberTypeSingleLine(String hostName, String metricFieldName, Long startTime, Long endTime) {
        return null;
    }

    @Override
    public Object getlast(String hostName, String metricFieldName, Long startTime, Long endTime) {
        return null;
    }

}
