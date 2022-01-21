package com.didichuxing.datachannel.agentmanager.core.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.BusinessMetricsQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricNodeVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricTypeEnum;
import com.didichuxing.datachannel.agentmanager.core.ApplicationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Rollback
public class MetricsManageServiceManageTest extends ApplicationTests {

    @Autowired
    private MetricsManageService metricsManageService;

    @Test
    public void testGetMetricsTreeByMetricType() {
        MetricNodeVO metricNodeVO = metricsManageService.getMetricsTreeByMetricType(MetricTypeEnum.LOG_COLLECT_TASK.getCode());
        MetricNodeVO metricNodeVO1 = metricsManageService.getMetricsTreeByMetricType(MetricTypeEnum.AGENT.getCode());
        assert metricNodeVO1 != null && metricNodeVO != null;
    }

    @Test
    public void test() {
//        BusinessMetricsQueryDTO businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode();
//        MetricPanel metricPanel = metricsManageService.getMetric();
    }

}
