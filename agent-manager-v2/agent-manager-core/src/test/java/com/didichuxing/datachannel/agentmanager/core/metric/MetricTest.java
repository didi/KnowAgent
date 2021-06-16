package com.didichuxing.datachannel.agentmanager.core.metric;

import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanelGroup;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.core.ApplicationTests;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.AgentMetricsDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class MetricTest extends ApplicationTests {

    @Qualifier("agentMetricsMysqlDAOImpl")
    @Autowired
    private AgentMetricsDAO dao;

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    //    private static String hostName = "cs-ecmc-k8s01-01.py";
    private static String hostName = "10.190.32.213";

    private static long endTime = System.currentTimeMillis();
    private static long startTime = endTime - 10 * 60 * 1000;

    @Test
    public void test() {
        Long size = dao.getHeartBeatTimes(startTime, endTime, hostName);
        System.out.println(size);
    }

    @Test
    public void filePathNotExistTest() {
        Integer count = dao.getFilePathNotExistsCountByTimeFrame(startTime, endTime, 1056L, 1146L, hostName);
        System.out.println(count);
    }

    @Test
    public void sliceErrorTest() {
        Integer count = dao.getSliceErrorCount(startTime, endTime, 1057L, 1147L, hostName);
        System.out.println(count);
    }

    @Test
    public void latestCollectTimeTest() {
        Long time = dao.getLatestCollectTime(1057L, 1147L, hostName);
        System.out.println(time);
    }

    @Test
    public void abnormalTruncationCountTest() {
        Integer count = dao.getAbnormalTruncationCountByTimeFrame(startTime, endTime, 1057L, 1147L, hostName);
        System.out.println(count);
    }

    @Test
    public void latestStartTimeTest() {
        Long time = dao.getLatestStartupTime(hostName);
        System.out.println(time);
    }

    @Test
    public void errorLogCountTest() {
        Integer count = dao.getErrorLogCount(startTime, endTime, hostName);
        System.out.println(count);
    }

    @Test
    public void gcCountTest() {
        Long count = dao.getGCCount(startTime, endTime, hostName);
        System.out.println(count);
    }

    @Test
    public void cpuUsageTest() {
        Double usage = dao.getLatestCpuUsage(hostName);
        System.out.println(usage);
    }

    @Test
    public void fdUsageTest() {
        Integer usage = dao.getLatestFdUsage(hostName);
        System.out.println(usage);
    }

    @Test
    public void disorderCountTest() {
        Integer count = dao.getLatestFdUsage("10.190.24.99");
        System.out.println(count);
    }

    @Test
    public void agentFdUsagePerMinTest() {
        List<MetricPoint> points = dao.getAgentFdUsagePerMin(System.currentTimeMillis() - 600 * 1000, System.currentTimeMillis(), "cs-ecmc-k8s01-01.py");
        for (MetricPoint point : points) {
            System.out.println(point.getTimestamp());
            System.out.println(point.getValue());
        }
    }

    @Test
    public void collectSendBytePerMinTest() {
        List<MetricPoint> points = dao.getAgentOutputBytesPerMin(System.currentTimeMillis() - 600 * 1000, System.currentTimeMillis(), "cs-ecmc-k8s01-01.py");
        for (MetricPoint point : points) {
            System.out.println(point.getTimestamp());
            System.out.println(point.getValue());
        }
    }

    @Test
    public void collectCountPerMinTest() {
        List<MetricPoint> points = dao.getLogCollectTaskLogCountPerMin(1070L, System.currentTimeMillis() - 600 * 1000, System.currentTimeMillis());
        for (MetricPoint point : points) {
            System.out.println(point.getTimestamp());
            System.out.println(point.getValue());
        }
    }

    @Test
    public void collectTaskBytesPerMinTest() {
        List<MetricPoint> points = dao.getLogCollectTaskBytesPerMin(1070L, System.currentTimeMillis() - 600 * 1000, System.currentTimeMillis());
        for (MetricPoint point : points) {
            System.out.println(point.getTimestamp());
            System.out.println(point.getValue());
        }
    }

    @Test
    public void listAgentMetricsTest() {
        List<MetricPanelGroup> list = agentManageService.listAgentMetrics(393L, System.currentTimeMillis() - 600 * 1000, System.currentTimeMillis());
        for (MetricPanelGroup metricPanelGroup : list) {
            List<MetricPanel> panelList = metricPanelGroup.getMetricPanelList();
            System.out.println(panelList);
        }
    }

    @Test
    public void listCollectTaskMetricsTest() {
        List<MetricPanelGroup> list = logCollectTaskManageService.listLogCollectTaskMetrics(1070L, System.currentTimeMillis() - 600 * 1000, System.currentTimeMillis());
        for (MetricPanelGroup metricPanelGroup : list) {
            List<MetricPanel> panelList = metricPanelGroup.getMetricPanelList();
            System.out.println(panelList);
        }
    }

}
