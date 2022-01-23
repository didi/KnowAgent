package com.didichuxing.datachannel.agentmanager.core.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.BusinessMetricsQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsDiskPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricNodeVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricFieldEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.agentmanager.core.ApplicationTests;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.MetricsDiskPOMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Transactional
@Rollback
public class MetricsManageServiceManageTest extends ApplicationTests {

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static String HOST_NAME = "host01";
    private static String DISK_PATH_1 = "/data01";
    private static String DISK_PATH_2 = "/data02";
    private static String DISK_DEVICE_1 = "disk_device_01";
    private static String DISK_DEVICE_2 = "disk_device_02";
    private static String DISK_FS_Type_1 = "ext3";
    private static String DISK_FS_Type_2 = "ext3";
    private static Long START_TIME;
    private static Long END_TIME;
    private static Long HEART_BEAT_TIME_1;
    private static Long HEART_BEAT_TIME_2;

    static {
        try {
            HEART_BEAT_TIME_1 = df.parse("2022-01-21 18:25:39").getTime();
            HEART_BEAT_TIME_2 = df.parse("2022-01-21 18:26:23").getTime();
            START_TIME = df.parse("2022-01-21 18:00:00").getTime();
            END_TIME = df.parse("2022-01-21 23:59:59").getTime();
        } catch (ParseException e) {
            //TODO：
        }
    }



    @Autowired
    private MetricsManageService metricsManageService;

    @Autowired
    private MetricsDiskPOMapper metricsDiskDAO;

    @Test
    public void testGetMetricsTreeByMetricType() {
        MetricNodeVO metricNodeLogCollectTask = metricsManageService.getMetricsTreeByMetricType(MetricTypeEnum.LOG_COLLECT_TASK.getCode());
        MetricNodeVO metricNodeAgent = metricsManageService.getMetricsTreeByMetricType(MetricTypeEnum.AGENT.getCode());
        assert metricNodeAgent != null && metricNodeLogCollectTask != null;
    }

    @Test
    public void testMetricQuerySystemDiskIOUtil() {

        /*
         * 1.）插入指标数据
         */
        MetricsDiskPO disk1point1 = new MetricsDiskPO();
        disk1point1.setHostname(HOST_NAME);
        disk1point1.setSystemdiskpath(DISK_PATH_1);
        disk1point1.setSystemdiskdevice(DISK_DEVICE_1);
        disk1point1.setSystemdiskfstype(DISK_FS_Type_1);
        disk1point1.setSystemioutil(25.5);
        disk1point1.setHeartbeattime(HEART_BEAT_TIME_1);
        disk1point1.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
        disk1point1.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_1));
        metricsDiskDAO.insertSelective(disk1point1);

        MetricsDiskPO disk2point1 = new MetricsDiskPO();
        disk2point1.setHostname(HOST_NAME);
        disk2point1.setSystemdiskpath(DISK_PATH_2);
        disk2point1.setSystemdiskdevice(DISK_DEVICE_2);
        disk2point1.setSystemdiskfstype(DISK_FS_Type_2);
        disk2point1.setSystemioutil(99.5);
        disk2point1.setHeartbeattime(HEART_BEAT_TIME_1);
        disk2point1.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
        disk2point1.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_1));
        metricsDiskDAO.insertSelective(disk2point1);

        MetricsDiskPO disk1point2 = new MetricsDiskPO();
        disk1point2.setHostname(HOST_NAME);
        disk1point2.setSystemdiskpath(DISK_PATH_1);
        disk1point2.setSystemdiskdevice(DISK_DEVICE_1);
        disk1point2.setSystemdiskfstype(DISK_FS_Type_1);
        disk1point2.setSystemioutil(53.5);
        disk1point2.setHeartbeattime(HEART_BEAT_TIME_2);
        disk1point2.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_2));
        disk1point2.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_2));
        metricsDiskDAO.insertSelective(disk1point2);

        MetricsDiskPO disk2point2 = new MetricsDiskPO();
        disk2point2.setHostname(HOST_NAME);
        disk2point2.setSystemdiskpath(DISK_PATH_2);
        disk2point2.setSystemdiskdevice(DISK_DEVICE_2);
        disk2point2.setSystemdiskfstype(DISK_FS_Type_2);
        disk2point2.setSystemioutil(37.7);
        disk2point2.setHeartbeattime(HEART_BEAT_TIME_2);
        disk2point2.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_2));
        disk2point2.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_2));
        metricsDiskDAO.insertSelective(disk2point2);

        assert 4 == metricsDiskDAO.selectAll().size();

        /*
         * 2.）指标查询存在
         */
        BusinessMetricsQueryDTO businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.SYSTEM_DISK_IO_IO_UTIL.getCode());
        businessMetricsQueryDTO.setHostName(HOST_NAME);
        businessMetricsQueryDTO.setSortMetricType(0);
        businessMetricsQueryDTO.setTopN(null);
        businessMetricsQueryDTO.setSortTime(null);
        businessMetricsQueryDTO.setStartTime(START_TIME);
        businessMetricsQueryDTO.setEndTime(END_TIME);
        MetricPanel metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);

        assert null != metricPanel;

    }

}
