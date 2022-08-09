package com.didichuxing.datachannel.agentmanager.core.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsDiskIOPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsDiskPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsNetCardPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsSystemPO;
import com.didichuxing.datachannel.agentmanager.core.ApplicationTests;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Rollback
public class MetricsInsertTest extends ApplicationTests {

    @Autowired
    private MetricsDiskIOPOMapper metricsDiskIODAO;

    @Autowired
    private MetricsAgentPOMapper metricsAgentDAO;

    @Autowired
    private MetricsProcessPOMapper metricsProcessDAO;

    @Autowired
    private MetricsSystemPOMapper metricsSystemDAO;

    @Autowired
    private MetricsDiskPOMapper metricsDiskDAO;

    @Autowired
    private MetricsNetCardPOMapper metricsNetCardDAO;

    @Autowired
    private MetricsLogCollectTaskPOMapper metricsLogCollectTaskDAO;

    @Test
    public void testInsertMetricsDiskIOPO() {
        MetricsDiskIOPO metricsDiskIOPO = new MetricsDiskIOPO();
        metricsDiskIOPO.setHostname("test_01");
        metricsDiskIOPO.setSystemdiskdevice("qwdqwd");
        metricsDiskIODAO.insertSelective(metricsDiskIOPO);
    }

    @Test
    public void testInsertMetricsDiskPO() {
        MetricsDiskPO metricsDiskPO = new MetricsDiskPO();
        metricsDiskPO.setHostname("test_01");
        metricsDiskPO.setSystemdiskpath("qwdqwd");
        metricsDiskDAO.insertSelective(metricsDiskPO);
    }

    @Test
    public void testInsertMetricsNetCardPO() {
        MetricsNetCardPO metricsNetCardPO = new MetricsNetCardPO();
        metricsNetCardPO.setHostname("test_01");
        metricsNetCardPO.setSystemnetcardsbandmacaddress("qwdqwd");
        metricsNetCardDAO.insertSelective(metricsNetCardPO);
    }

    @Test
    public void testInsertMetricsSystemPO() {
        MetricsSystemPO metricsSystemPO = new MetricsSystemPO();
        metricsSystemPO.setSystemnetworksendbytesps99quantile(1.0d);
        metricsSystemDAO.insertSelective(metricsSystemPO);
    }

}
