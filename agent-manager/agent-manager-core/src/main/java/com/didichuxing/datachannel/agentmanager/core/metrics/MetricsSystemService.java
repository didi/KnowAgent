package com.didichuxing.datachannel.agentmanager.core.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.metrics.StatisticMetricPoint;

import java.util.List;

public interface MetricsSystemService {

    List<StatisticMetricPoint> getNumberTypeSingleLine(String hostName, String metricFieldName, Long startTime, Long endTime);

    Object getlast(String hostName, String metricFieldName, Long startTime, Long endTime);

}
