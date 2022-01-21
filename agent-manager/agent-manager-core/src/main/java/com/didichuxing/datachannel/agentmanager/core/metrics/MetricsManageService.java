package com.didichuxing.datachannel.agentmanager.core.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.BusinessMetricsQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricNodeVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;

public interface MetricsManageService {

    /**
     * 根据给定指标类型获取该指标类型下的指标集（以树形式组织）
     * @param metricTypeCode 指标类型 对应MetricTypeEnum.code
     * @return 返回根据给定指标类型获取该指标类型下的指标集（以树形式组织）
     */
    MetricNodeVO getMetricsTreeByMetricType(Integer metricTypeCode);

    /**
     * 根据给定指标查询条件获取对应的指标数据信息
     * @param metricQueryDTO 指标查询条件
     * @return 返回根据给定指标查询条件获取对应的指标数据信息
     */
    MetricPanel getMetric(BusinessMetricsQueryDTO metricQueryDTO);

}
