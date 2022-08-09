//package com.didichuxing.datachannel.agentmanager.core.metrics;
//
//import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.BusinessMetricsQueryDTO;
//import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.*;
//import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricNodeVO;
//import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;
//import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricFieldEnum;
//import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricTypeEnum;
//import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
//import com.didichuxing.datachannel.agentmanager.core.ApplicationTests;
//import com.didichuxing.datachannel.agentmanager.persistence.mysql.*;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.UUID;
//
//@Transactional
//@Rollback
//public class MetricsManageServiceManageTest extends ApplicationTests {
//
//    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    private static String HOST_NAME = "host01";
//    private static String DISK_PATH_1 = "/data01";
//    private static String DISK_PATH_2 = "/data02";
//    private static String DISK_DEVICE_1 = "disk_device_01";
//    private static String DISK_DEVICE_2 = "disk_device_02";
//    private static String DISK_FS_Type_1 = "ext3";
//    private static String DISK_FS_Type_2 = "ext3";
//    private static String LOG_COLLECT_TASK_PATH_1 = "/logs/info.log";
//    private static String LOG_COLLECT_TASK_PATH_2 = "/logs/error.log";
//    private static Long LOG_COLLECT_TASK_PATH_ID_1 = 1L;
//    private static Long LOG_COLLECT_TASK_PATH_ID_2 = 2L;
//    private static String LOG_COLLECT_TASK_HOST_NAME_1 = "host02";
//    private static String LOG_COLLECT_TASK_HOST_NAME_2 = "host03";
//    private static Long LOG_COLLECT_TASK_ID = 1L;
//    private static Long START_TIME;
//    private static Long HEART_BEAT_TIME_1;
//    private static Long HEART_BEAT_TIME_2;
//    private static String NET_CARD_DEVICE_1 = "net_card_device_01";
//    private static String NET_CARD_DEVICE_2 = "net_card_device_02";
//    private static String NET_CARD_MAC_ADDRESS_1 = UUID.randomUUID().toString();
//    private static String NET_CARD_MAC_ADDRESS_2 = UUID.randomUUID().toString();
//
//    static {
//        try {
//            HEART_BEAT_TIME_1 = df.parse("2022-01-21 18:25:39").getTime();
//            HEART_BEAT_TIME_2 = df.parse("2022-01-21 18:26:23").getTime();
//            START_TIME = df.parse("2022-01-21 18:00:00").getTime();
//        } catch (ParseException e) {
//            //TODO：
//        }
//    }
//
//    @Autowired
//    private MetricsManageService metricsManageService;
//
//    @Autowired
//    private MetricsDiskPOMapper metricsDiskDAO;
//
//    @Autowired
//    private MetricsNetCardPOMapper metricsNetCardDAO;
//
//    @Autowired
//    private MetricsSystemPOMapper metricsSystemDAO;
//
//    @Autowired
//    private MetricsProcessPOMapper metricsProcessDAO;
//
//    @Autowired
//    private MetricsAgentPOMapper metricsAgentDAO;
//
//    @Autowired
//    private MetricsLogCollectTaskPOMapper metricsLogCollectTaskDAO;
//
//    /********************************* metric type *********************************/
//
//    @Test
//    public void testGetMetricsTreeByMetricType() {
//        MetricNodeVO metricNodeLogCollectTask = metricsManageService.getMetricsTreeByMetricType(MetricTypeEnum.LOG_COLLECT_TASK.getCode());
//        MetricNodeVO metricNodeAgent = metricsManageService.getMetricsTreeByMetricType(MetricTypeEnum.AGENT.getCode());
//        assert metricNodeAgent != null && metricNodeLogCollectTask != null;
//    }
//
//    /********************************* disk io *********************************/
//
//    @Test
//    public void testMetricQuerySystemDiskIOUtil() {
//        /*
//         * 1.）插入指标数据
//         */
//        iniDiskIOtData();
//
//        /*
//         * 2.）指标查询存在
//         */
//        // disk/io 多条线指标查询 - 初始
//        BusinessMetricsQueryDTO businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.SYSTEM_DISK_IO_IO_UTIL.getCode());
//        businessMetricsQueryDTO.setHostName(HOST_NAME);
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        MetricPanel metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getMultiLineChatValue().size();
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().equals(23.5);
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getStd().equals(2.2);
//
//        // disk/io 多条线指标查询 - 大截面其他维度排序
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.SYSTEM_DISK_IO_IO_UTIL.getCode());
//        businessMetricsQueryDTO.setHostName(HOST_NAME);
//        businessMetricsQueryDTO.setTopN(0);
//        businessMetricsQueryDTO.setSortTime(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(4);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getMultiLineChatValue().size();
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().equals(17.7);
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getStd().equals(9.2);
//    }
//
//    @Test
//    public void testMetricQuerySystemDiskBytesFree() {
//
//        /*
//         * 1.）插入指标数据
//         */
//        iniDiskIOtData();
//
//        /*
//         * 2.）指标查询存在
//         */
//        // disk/io 多条线指标查询 - 初始
//        BusinessMetricsQueryDTO businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.SYSTEM_DISK_BYTES_FREE.getCode());
//        businessMetricsQueryDTO.setHostName(HOST_NAME);
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        MetricPanel metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getMultiLineChatValue().size();
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().longValue() == 1990 * 1024 * 1024 * 1024L;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getPath().equals(DISK_PATH_2);
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getDevice().equals(DISK_DEVICE_2);
//
////        // disk/io 多条线指标查询 - 大截面其他维度排序
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.SYSTEM_DISK_BYTES_FREE.getCode());
//        businessMetricsQueryDTO.setHostName(HOST_NAME);
//        businessMetricsQueryDTO.setTopN(0);
//        businessMetricsQueryDTO.setSortTime(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(0);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getMultiLineChatValue().size();
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().longValue() == 199 * 1024 * 1024 * 1024L;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getPath().equals(DISK_PATH_1);
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getDevice().equals(DISK_DEVICE_1);
//
//    }
//
//    /********************************* net card *********************************/
//
//    @Test
//    public void testMetricQuerySystemNetCardSendBytesPS() {
//
//        iniNetCardData();
//
//        // net card 多条线指标查询 - 初始
//        BusinessMetricsQueryDTO businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.SYSTEM_NET_CARD_SEND_BYTES_PS.getCode());
//        businessMetricsQueryDTO.setHostName(HOST_NAME);
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        MetricPanel metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getMultiLineChatValue().size();
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().longValue() == 27*1024*1024L;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getMacAddress().equals(NET_CARD_MAC_ADDRESS_2);
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getDevice().equals(NET_CARD_DEVICE_2);
//
//        // net card 多条线指标查询 - 大截面其他维度排序
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.SYSTEM_NET_CARD_SEND_BYTES_PS.getCode());
//        businessMetricsQueryDTO.setHostName(HOST_NAME);
//        businessMetricsQueryDTO.setTopN(0);
//        businessMetricsQueryDTO.setSortTime(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(4);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getMultiLineChatValue().size();
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().longValue() == 5*1024*1024L;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getMacAddress().equals(NET_CARD_MAC_ADDRESS_1);
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getDevice().equals(NET_CARD_DEVICE_1);
//
//    }
//
//    @Test
//    public void testMetricQuerySystemNetCardsBandWidth() {
//
//        iniNetCardData();
//
//        // net card 多条线指标查询 - 初始
//        BusinessMetricsQueryDTO businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.SYSTEM_NET_CARD_BAND_WIDTH.getCode());
//        businessMetricsQueryDTO.setHostName(HOST_NAME);
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        MetricPanel metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getMultiLineChatValue().size();
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().longValue() == 900 * 1024 * 1024L;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getMacAddress().equals(NET_CARD_MAC_ADDRESS_2);
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getDevice().equals(NET_CARD_DEVICE_2);
//
//        // net card 多条线指标查询 - 大截面其他维度排序
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.SYSTEM_NET_CARD_BAND_WIDTH.getCode());
//        businessMetricsQueryDTO.setHostName(HOST_NAME);
//        businessMetricsQueryDTO.setTopN(0);
//        businessMetricsQueryDTO.setSortTime(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(0);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getMultiLineChatValue().size();
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().longValue() == 500 * 1024 * 1024L;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getMacAddress().equals(NET_CARD_MAC_ADDRESS_1);
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getDevice().equals(NET_CARD_DEVICE_1);
//
//    }
//
//    /********************************* system *********************************/
//
//    @Test
//    public void testMetricQuerySystemStartupTime() throws Exception {
//
//        initSystemMetricsData();
//
//        // system lable 指标查询 - 初始
//        BusinessMetricsQueryDTO businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.SYSTEM_STARTUP_TIME.getCode());
//        businessMetricsQueryDTO.setHostName(HOST_NAME);
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        MetricPanel metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert null!= metricPanel.getLableValue();
//        assert (long) metricPanel.getLableValue() == df.parse("2022-01-19 18:26:23").getTime();
//
//    }
//
//    @Test
//    public void testMetricQuerySystemCpuUtil() throws Exception {
//
//        initSystemMetricsData();
//
//        // system 单条线指标查询 - 初始
//        BusinessMetricsQueryDTO businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.SYSTEM_CPU_UTIL.getCode());
//        businessMetricsQueryDTO.setHostName(HOST_NAME);
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        MetricPanel metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getSingleLineChatValue().size();
//        assert metricPanel.getSingleLineChatValue().get(0).getLast().doubleValue() == 30d;
//        assert metricPanel.getSingleLineChatValue().get(0).getNinetyFiveQuantile().doubleValue() == 45d;
//        assert metricPanel.getSingleLineChatValue().get(1).getStd().doubleValue() == 9.4;
//
//    }
//
//    @Test
//    public void testMetricQuerySystemMemFree() throws Exception {
//
//        initSystemMetricsData();
//
//        // system 单条线指标查询 - 初始
//        BusinessMetricsQueryDTO businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.SYSTEM_MEMORY_FREE.getCode());
//        businessMetricsQueryDTO.setHostName(HOST_NAME);
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        MetricPanel metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getSingleLineChatValue().size();
//        assert metricPanel.getSingleLineChatValue().get(0).getLast().longValue() == 55 * 1024 * 1024 * 1024L;
//        assert metricPanel.getSingleLineChatValue().get(1).getLast().longValue() == 35 * 1024 * 1024 * 1024L;
//
//    }
//
//    /********************************* process *********************************/
//
//    @Test
//    public void testMetricQueryProcStartupTime() throws Exception {
//
//        initProcessMetricsData();
//
//        // system lable 指标查询 - 初始
//        BusinessMetricsQueryDTO businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.PROCESS_START_UP_TIME.getCode());
//        businessMetricsQueryDTO.setHostName(HOST_NAME);
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        MetricPanel metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert null!= metricPanel.getLableValue();
//        assert (long) metricPanel.getLableValue() == df.parse("2022-01-19 18:26:23").getTime();
//
//    }
//
//    @Test
//    public void testMetricQueryProcCpuUtil() throws Exception {
//
//        initProcessMetricsData();
//
//        // system 单条线指标查询 - 初始
//        BusinessMetricsQueryDTO businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.PROCESS_CPU_UTIL.getCode());
//        businessMetricsQueryDTO.setHostName(HOST_NAME);
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        MetricPanel metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getSingleLineChatValue().size();
//        assert metricPanel.getSingleLineChatValue().get(0).getLast().doubleValue() == 30d;
//        assert metricPanel.getSingleLineChatValue().get(0).getNinetyFiveQuantile().doubleValue() == 45d;
//        assert metricPanel.getSingleLineChatValue().get(1).getStd().doubleValue() == 9.4;
//
//    }
//
//    @Test
//    public void testMetricQueryProcMemUsed() throws Exception {
//
//        initProcessMetricsData();
//
//        // system 单条线指标查询 - 初始
//        BusinessMetricsQueryDTO businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.PROCESS_MEMORY_USED.getCode());
//        businessMetricsQueryDTO.setHostName(HOST_NAME);
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        MetricPanel metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getSingleLineChatValue().size();
//        assert metricPanel.getSingleLineChatValue().get(0).getLast().longValue() == 55 * 1024 * 1024 * 1024L;
//        assert metricPanel.getSingleLineChatValue().get(1).getLast().longValue() == 35 * 1024 * 1024 * 1024L;
//
//    }
//
//    /********************************* agent *********************************/
//
//    @Test
//    public void testMetricQueryAgentVersion() {
//
//        initAgentMetricsData();
//
//        // system lable 指标查询 - 初始
//        BusinessMetricsQueryDTO businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.AGENT_VERSION.getCode());
//        businessMetricsQueryDTO.setHostName(HOST_NAME);
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        MetricPanel metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert null!= metricPanel.getLableValue();
//        assert metricPanel.getLableValue().toString().equals("0.0.2");
//
//    }
//
//    @Test
//    public void testMetricQueryAgentWriteCount() {
//
//        initAgentMetricsData();
//
//        BusinessMetricsQueryDTO businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.AGENT_WRITE_COUNT.getCode());
//        businessMetricsQueryDTO.setHostName(HOST_NAME);
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        MetricPanel metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getSingleLineChatValue().size();
//        assert metricPanel.getSingleLineChatValue().get(0).getLast().longValue() == 39 * 1024 * 1024L;
//        assert metricPanel.getSingleLineChatValue().get(1).getLast().longValue() == 14 * 1024 * 1024L;
//
//    }
//
//    /********************************* log collect task *********************************/
//
//    @Test
//    public void testMetricQueryBusinessTimestamp() throws Exception {
//
//        initLogCollectTaskMetricsData();
//
//        //only logCollectTaskId
//        BusinessMetricsQueryDTO businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_BUSINESS_TIMESTAMP.getCode());
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        MetricPanel metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert null != metricPanel.getLableValue();
//        assert (long) metricPanel.getLableValue() == df.parse("2022-01-17 23:39:23").getTime();
//
//        //only logCollectTaskId & pathId
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_BUSINESS_TIMESTAMP.getCode());
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        businessMetricsQueryDTO.setPathId(LOG_COLLECT_TASK_PATH_ID_1);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert null != metricPanel.getLableValue();
//        assert (long) metricPanel.getLableValue() == df.parse("2022-01-17 14:59:23").getTime();
//
//        //only logCollectTaskId & host name
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_BUSINESS_TIMESTAMP.getCode());
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        businessMetricsQueryDTO.setHostName(LOG_COLLECT_TASK_HOST_NAME_2);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert null != metricPanel.getLableValue();
//        assert (long) metricPanel.getLableValue() == df.parse("2022-01-17 23:39:23").getTime();
//
//        //logCollectTaskId & pathId * host name
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_BUSINESS_TIMESTAMP.getCode());
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        businessMetricsQueryDTO.setPathId(LOG_COLLECT_TASK_PATH_ID_1);
//        businessMetricsQueryDTO.setHostName(LOG_COLLECT_TASK_HOST_NAME_2);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert null != metricPanel.getLableValue();
//        assert (long) metricPanel.getLableValue() == df.parse("2022-01-17 14:59:23").getTime();
//
//    }
//
//    @Test
//    public void testMetricQueryReadTimePerEvent() throws Exception {
//
//        initLogCollectTaskMetricsData();
//
//        //only logCollectTaskId
//        BusinessMetricsQueryDTO businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_READ_TIME_PER_EVENT.getCode());
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        MetricPanel metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getMultiLineChatValue().size();
//        assert metricPanel.getMultiLineChatValue().get(0).size() == 2 && metricPanel.getMultiLineChatValue().get(1).size() == 2;
//        assert metricPanel.getMultiLineChatValue().get(0).get(0).getLast().longValue() == 12l;
//        assert metricPanel.getMultiLineChatValue().get(1).get(1).getLast().longValue() == 3l;
//        assert metricPanel.getMultiLineChatValue().get(1).get(1).getStd().doubleValue() == 9.5;
//
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_READ_TIME_PER_EVENT.getCode());
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        businessMetricsQueryDTO.setTopN(0);
//        businessMetricsQueryDTO.setSortTime(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        businessMetricsQueryDTO.setSortMetricType(3);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getMultiLineChatValue().size();
//        assert metricPanel.getMultiLineChatValue().get(0).size() == 2 && metricPanel.getMultiLineChatValue().get(1).size() == 2;
//        assert metricPanel.getMultiLineChatValue().get(0).get(0).getLast().longValue() == 99l;
//        assert metricPanel.getMultiLineChatValue().get(1).get(1).getLast().longValue() == 5l;
//        assert metricPanel.getMultiLineChatValue().get(1).get(1).getStd().doubleValue() == 1.5;
//
//        //only logCollectTaskId & pathId
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_READ_TIME_PER_EVENT.getCode());
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        businessMetricsQueryDTO.setPathId(LOG_COLLECT_TASK_PATH_ID_2);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getMultiLineChatValue().size();
//        assert metricPanel.getMultiLineChatValue().get(0).size() == 2 && metricPanel.getMultiLineChatValue().get(1).size() == 2;
//        assert metricPanel.getMultiLineChatValue().get(0).get(0).getLast().longValue() == 12l;
//        assert metricPanel.getMultiLineChatValue().get(1).get(1).getLast().longValue() == 3l;
//        assert metricPanel.getMultiLineChatValue().get(1).get(1).getStd().doubleValue() == 9.5;
//
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_READ_TIME_PER_EVENT.getCode());
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        businessMetricsQueryDTO.setPathId(LOG_COLLECT_TASK_PATH_ID_2);
//        businessMetricsQueryDTO.setTopN(0);
//        businessMetricsQueryDTO.setSortTime(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        businessMetricsQueryDTO.setSortMetricType(5);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getMultiLineChatValue().size();
//        assert metricPanel.getMultiLineChatValue().get(0).size() == 2 && metricPanel.getMultiLineChatValue().get(1).size() == 2;
//        assert metricPanel.getMultiLineChatValue().get(0).get(0).getLast().longValue() == 12l;
//        assert metricPanel.getMultiLineChatValue().get(1).get(1).getLast().longValue() == 3l;
//        assert metricPanel.getMultiLineChatValue().get(1).get(1).getStd().doubleValue() == 9.5;
//
//        //only logCollectTaskId & host name
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_READ_TIME_PER_EVENT.getCode());
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        businessMetricsQueryDTO.setHostName(LOG_COLLECT_TASK_HOST_NAME_2);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 1 == metricPanel.getMultiLineChatValue().size();
//        assert 2 == metricPanel.getMultiLineChatValue().get(0).size();
//        assert metricPanel.getMultiLineChatValue().get(0).get(0).getLast().longValue() == 99l;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().longValue() == 3l;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getStd().doubleValue() == 9.5;
//
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_READ_TIME_PER_EVENT.getCode());
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        businessMetricsQueryDTO.setHostName(LOG_COLLECT_TASK_HOST_NAME_2);
//        businessMetricsQueryDTO.setTopN(0);
//        businessMetricsQueryDTO.setSortTime(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        businessMetricsQueryDTO.setSortMetricType(5);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 1 == metricPanel.getMultiLineChatValue().size();
//        assert 2 == metricPanel.getMultiLineChatValue().get(0).size();
//        assert metricPanel.getMultiLineChatValue().get(0).get(0).getLast().longValue() == 99l;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().longValue() == 3l;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getStd().doubleValue() == 9.5;
//
//        //logCollectTaskId & pathId & host name
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_READ_TIME_PER_EVENT.getCode());
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        businessMetricsQueryDTO.setHostName(LOG_COLLECT_TASK_HOST_NAME_2);
//        businessMetricsQueryDTO.setPathId(LOG_COLLECT_TASK_PATH_ID_2);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 1 == metricPanel.getMultiLineChatValue().size();
//        assert 2 == metricPanel.getMultiLineChatValue().get(0).size();
//        assert metricPanel.getMultiLineChatValue().get(0).get(0).getLast().longValue() == 1l;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().longValue() == 3l;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getStd().doubleValue() == 9.5;
//
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_READ_TIME_PER_EVENT.getCode());
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        businessMetricsQueryDTO.setHostName(LOG_COLLECT_TASK_HOST_NAME_2);
//        businessMetricsQueryDTO.setPathId(LOG_COLLECT_TASK_PATH_ID_2);
//        businessMetricsQueryDTO.setTopN(0);
//        businessMetricsQueryDTO.setSortTime(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        businessMetricsQueryDTO.setSortMetricType(5);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 1 == metricPanel.getMultiLineChatValue().size();
//        assert 2 == metricPanel.getMultiLineChatValue().get(0).size();
//        assert metricPanel.getMultiLineChatValue().get(0).get(0).getLast().longValue() == 1l;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().longValue() == 3l;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getStd().doubleValue() == 9.5;
//
//    }
//
//    @Test
//    public void testMetricQuerySendBytes() throws Exception {
//
//        initLogCollectTaskMetricsData();
//
//        //only logCollectTaskId
//        BusinessMetricsQueryDTO businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getCode());
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        MetricPanel metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getMultiLineChatValue().size();
//        assert metricPanel.getMultiLineChatValue().get(0).size() == 2 && metricPanel.getMultiLineChatValue().get(1).size() == 2;
//        assert metricPanel.getMultiLineChatValue().get(0).get(0).getLast().longValue() == 58 * 1024 * 1024L + 55 * 1024 * 1024L;
//        assert metricPanel.getMultiLineChatValue().get(1).get(1).getLast().longValue() == 94 * 1024 * 1024L + 17 * 1024 * 1024L;
//
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getCode());
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        businessMetricsQueryDTO.setTopN(0);
//        businessMetricsQueryDTO.setSortTime(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        businessMetricsQueryDTO.setSortMetricType(0);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getMultiLineChatValue().size();
//        assert metricPanel.getMultiLineChatValue().get(0).size() == 2 && metricPanel.getMultiLineChatValue().get(1).size() == 2;
//        assert metricPanel.getMultiLineChatValue().get(1).get(0).getLast().longValue() == 58 * 1024 * 1024L + 55 * 1024 * 1024L;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().longValue() == 94 * 1024 * 1024L + 17 * 1024 * 1024L;
//
//        //only logCollectTaskId & pathId
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getCode());
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        businessMetricsQueryDTO.setPathId(LOG_COLLECT_TASK_PATH_ID_2);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getMultiLineChatValue().size();
//        assert metricPanel.getMultiLineChatValue().get(0).size() == 2 && metricPanel.getMultiLineChatValue().get(1).size() == 2;
//        assert metricPanel.getMultiLineChatValue().get(1).get(0).getLast().longValue() == 99 * 1024 * 1024L;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().longValue() == 259 * 1024 * 1024L;
//
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getCode());
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        businessMetricsQueryDTO.setPathId(LOG_COLLECT_TASK_PATH_ID_2);
//        businessMetricsQueryDTO.setTopN(0);
//        businessMetricsQueryDTO.setSortTime(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        businessMetricsQueryDTO.setSortMetricType(0);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 2 == metricPanel.getMultiLineChatValue().size();
//        assert metricPanel.getMultiLineChatValue().get(0).size() == 2 && metricPanel.getMultiLineChatValue().get(1).size() == 2;
//        assert metricPanel.getMultiLineChatValue().get(1).get(0).getLast().longValue() == 60817408l;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().longValue() == 98566144L;
//
//        //only logCollectTaskId & host name
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getCode());
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        businessMetricsQueryDTO.setHostName(LOG_COLLECT_TASK_HOST_NAME_2);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 1 == metricPanel.getMultiLineChatValue().size();
//        assert 2 == metricPanel.getMultiLineChatValue().get(0).size();
//        assert metricPanel.getMultiLineChatValue().get(0).get(0).getLast().longValue() == 99 * 1024 * 1024L + 99 * 1024 * 1024L;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().longValue() == 94 * 1024 * 1024L + 17 * 1024 * 1024L;
//
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getCode());
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        businessMetricsQueryDTO.setHostName(LOG_COLLECT_TASK_HOST_NAME_2);
//        businessMetricsQueryDTO.setTopN(0);
//        businessMetricsQueryDTO.setSortTime(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        businessMetricsQueryDTO.setSortMetricType(0);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 1 == metricPanel.getMultiLineChatValue().size();
//        assert 2 == metricPanel.getMultiLineChatValue().get(0).size();
//        assert metricPanel.getMultiLineChatValue().get(0).get(0).getLast().longValue() == 99 * 1024 * 1024L + 99 * 1024 * 1024L;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().longValue() == 94 * 1024 * 1024L + 17 * 1024 * 1024L;
//
//        //logCollectTaskId & pathId & host name
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getCode());
//        businessMetricsQueryDTO.setTopN(null);
//        businessMetricsQueryDTO.setSortTime(null);
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        businessMetricsQueryDTO.setHostName(LOG_COLLECT_TASK_HOST_NAME_2);
//        businessMetricsQueryDTO.setPathId(LOG_COLLECT_TASK_PATH_ID_2);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 1 == metricPanel.getMultiLineChatValue().size();
//        assert 2 == metricPanel.getMultiLineChatValue().get(0).size();
//        assert metricPanel.getMultiLineChatValue().get(0).get(0).getLast().longValue() == 99 * 1024 * 1024l;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().longValue() == 94 * 1024 * 1024l;
//
//        businessMetricsQueryDTO = new BusinessMetricsQueryDTO();
//        businessMetricsQueryDTO.setMetricCode(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getCode());
//        businessMetricsQueryDTO.setStartTime(START_TIME);
//        businessMetricsQueryDTO.setEndTime(HEART_BEAT_TIME_2);
//        businessMetricsQueryDTO.setSortMetricType(null);
//        businessMetricsQueryDTO.setLogCollectTaskId(LOG_COLLECT_TASK_ID);
//        businessMetricsQueryDTO.setHostName(LOG_COLLECT_TASK_HOST_NAME_2);
//        businessMetricsQueryDTO.setPathId(LOG_COLLECT_TASK_PATH_ID_2);
//        businessMetricsQueryDTO.setTopN(0);
//        businessMetricsQueryDTO.setSortTime(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        businessMetricsQueryDTO.setSortMetricType(0);
//        metricPanel = metricsManageService.getMetric(businessMetricsQueryDTO);
//        assert null != metricPanel;
//        assert 1 == metricPanel.getMultiLineChatValue().size();
//        assert 2 == metricPanel.getMultiLineChatValue().get(0).size();
//        assert metricPanel.getMultiLineChatValue().get(0).get(0).getLast().longValue() == 99 * 1024 * 1024l;
//        assert metricPanel.getMultiLineChatValue().get(0).get(1).getLast().longValue() == 94 * 1024 * 1024l;
//
//    }
//
//    @Test
//    public void initAllTestData() throws Exception {
//
//        initLogCollectTaskMetricsData();
//        initAgentMetricsData();
//        initProcessMetricsData();
//        initSystemMetricsData();
//        iniNetCardData();
//        iniDiskIOtData();
//
//    }
//
//    /********************************* init data test *********************************/
//
//    /**
//     * 初始化logCollectTask相关metrics数据
//     */
//    private void initLogCollectTaskMetricsData() throws Exception {
//
//        MetricsLogCollectTaskPO path1host1Point1 = new MetricsLogCollectTaskPO();
//        path1host1Point1.setHeartbeattime(HEART_BEAT_TIME_1);
//        path1host1Point1.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        path1host1Point1.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_1));
//        path1host1Point1.setPath(LOG_COLLECT_TASK_PATH_1);
//        path1host1Point1.setPathid(LOG_COLLECT_TASK_PATH_ID_1);
//        path1host1Point1.setCollecttaskhostname(LOG_COLLECT_TASK_HOST_NAME_1);
//        path1host1Point1.setCollecttaskid(LOG_COLLECT_TASK_ID);
//        //lable
//        path1host1Point1.setBusinesstimestamp(df.parse("2022-01-14 18:26:23").getTime());
//        // single line statics
//        path1host1Point1.setReadtimeperevent(5d);
//        path1host1Point1.setReadtimepereventmin(2d);
//        path1host1Point1.setReadtimepereventmax(15d);
//        path1host1Point1.setReadtimepereventmean(9d);
//        path1host1Point1.setReadtimepereventstd(1.7);
//        path1host1Point1.setReadtimeperevent55quantile(21d);
//        path1host1Point1.setReadtimeperevent75quantile(29d);
//        path1host1Point1.setReadtimeperevent95quantile(45d);
//        path1host1Point1.setReadtimeperevent99quantile(15d);
//        // single line non statics
//        path1host1Point1.setSendbytes(55 * 1024 * 1024L);
//        metricsLogCollectTaskDAO.insert(path1host1Point1);
//
//        MetricsLogCollectTaskPO path1host2Point1 = new MetricsLogCollectTaskPO();
//        path1host2Point1.setHeartbeattime(HEART_BEAT_TIME_1);
//        path1host2Point1.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        path1host2Point1.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_1));
//        path1host2Point1.setPath(LOG_COLLECT_TASK_PATH_1);
//        path1host2Point1.setPathid(LOG_COLLECT_TASK_PATH_ID_1);
//        path1host2Point1.setCollecttaskhostname(LOG_COLLECT_TASK_HOST_NAME_2);
//        path1host2Point1.setCollecttaskid(LOG_COLLECT_TASK_ID);
//        //lable
//        path1host2Point1.setBusinesstimestamp(df.parse("2022-01-14 18:29:23").getTime());
//        // single line statics
//        path1host2Point1.setReadtimeperevent(99d);
//        path1host2Point1.setReadtimepereventmin(45d);
//        path1host2Point1.setReadtimepereventmax(9999d);
//        path1host2Point1.setReadtimepereventmean(953d);
//        path1host2Point1.setReadtimepereventstd(5.7);
//        path1host2Point1.setReadtimeperevent55quantile(221d);
//        path1host2Point1.setReadtimeperevent75quantile(329d);
//        path1host2Point1.setReadtimeperevent95quantile(415d);
//        path1host2Point1.setReadtimeperevent99quantile(125d);
//        // single line non statics
//        path1host2Point1.setSendbytes(99 * 1024 * 1024L);
//        metricsLogCollectTaskDAO.insert(path1host2Point1);
//
//        MetricsLogCollectTaskPO path2host1Point1 = new MetricsLogCollectTaskPO();
//        path2host1Point1.setHeartbeattime(HEART_BEAT_TIME_1);
//        path2host1Point1.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        path2host1Point1.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_1));
//        path2host1Point1.setPath(LOG_COLLECT_TASK_PATH_2);
//        path2host1Point1.setPathid(LOG_COLLECT_TASK_PATH_ID_2);
//        path2host1Point1.setCollecttaskhostname(LOG_COLLECT_TASK_HOST_NAME_1);
//        path2host1Point1.setCollecttaskid(LOG_COLLECT_TASK_ID);
//        //lable
//        path2host1Point1.setBusinesstimestamp(df.parse("2022-01-14 17:59:23").getTime());
//        // single line statics
//        path2host1Point1.setReadtimeperevent(12d);
//        path2host1Point1.setReadtimepereventmin(4d);
//        path2host1Point1.setReadtimepereventmax(99d);
//        path2host1Point1.setReadtimepereventmean(15d);
//        path2host1Point1.setReadtimepereventstd(4.7);
//        path2host1Point1.setReadtimeperevent55quantile(21d);
//        path2host1Point1.setReadtimeperevent75quantile(29d);
//        path2host1Point1.setReadtimeperevent95quantile(15d);
//        path2host1Point1.setReadtimeperevent99quantile(15d);
//        // single line non statics
//        path2host1Point1.setSendbytes(58 * 1024 * 1024L);
//        metricsLogCollectTaskDAO.insert(path2host1Point1);
//
//        MetricsLogCollectTaskPO path2host2Point1 = new MetricsLogCollectTaskPO();
//        path2host2Point1.setHeartbeattime(HEART_BEAT_TIME_1);
//        path2host2Point1.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        path2host2Point1.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_1));
//        path2host2Point1.setPath(LOG_COLLECT_TASK_PATH_2);
//        path2host2Point1.setPathid(LOG_COLLECT_TASK_PATH_ID_2);
//        path2host2Point1.setCollecttaskhostname(LOG_COLLECT_TASK_HOST_NAME_2);
//        path2host2Point1.setCollecttaskid(LOG_COLLECT_TASK_ID);
//        //lable
//        path2host2Point1.setBusinesstimestamp(df.parse("2022-01-15 23:59:23").getTime());
//        // single line statics
//        path2host2Point1.setReadtimeperevent(1d);
//        path2host2Point1.setReadtimepereventmin(1d);
//        path2host2Point1.setReadtimepereventmax(12d);
//        path2host2Point1.setReadtimepereventmean(4d);
//        path2host2Point1.setReadtimepereventstd(0.4);
//        path2host2Point1.setReadtimeperevent55quantile(5d);
//        path2host2Point1.setReadtimeperevent75quantile(4d);
//        path2host2Point1.setReadtimeperevent95quantile(2d);
//        path2host2Point1.setReadtimeperevent99quantile(1d);
//        // single line non statics
//        path2host2Point1.setSendbytes(99 * 1024 * 1024L);
//        metricsLogCollectTaskDAO.insert(path2host2Point1);
//
//        MetricsLogCollectTaskPO path1host1Point2 = new MetricsLogCollectTaskPO();
//        path1host1Point2.setHeartbeattime(HEART_BEAT_TIME_2);
//        path1host1Point2.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_2));
//        path1host1Point2.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_2));
//        path1host1Point2.setPath(LOG_COLLECT_TASK_PATH_1);
//        path1host1Point2.setPathid(LOG_COLLECT_TASK_PATH_ID_1);
//        path1host1Point2.setCollecttaskhostname(LOG_COLLECT_TASK_HOST_NAME_1);
//        path1host1Point2.setCollecttaskid(LOG_COLLECT_TASK_ID);
//        //lable
//        path1host1Point2.setBusinesstimestamp(df.parse("2022-01-17 14:59:23").getTime());
//        // single line statics
//        path1host1Point2.setReadtimeperevent(3d);
//        path1host1Point2.setReadtimepereventmin(2d);
//        path1host1Point2.setReadtimepereventmax(99d);
//        path1host1Point2.setReadtimepereventmean(17d);
//        path1host1Point2.setReadtimepereventstd(1.4);
//        path1host1Point2.setReadtimeperevent55quantile(25d);
//        path1host1Point2.setReadtimeperevent75quantile(34d);
//        path1host1Point2.setReadtimeperevent95quantile(41d);
//        path1host1Point2.setReadtimeperevent99quantile(78d);
//        // single line non statics
//        path1host1Point2.setSendbytes(57 * 1024 * 1024L);
//        metricsLogCollectTaskDAO.insert(path1host1Point2);
//
//        MetricsLogCollectTaskPO path1host2Point2 = new MetricsLogCollectTaskPO();
//        path1host2Point2.setHeartbeattime(HEART_BEAT_TIME_2);
//        path1host2Point2.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_2));
//        path1host2Point2.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_2));
//        path1host2Point2.setPath(LOG_COLLECT_TASK_PATH_1);
//        path1host2Point2.setPathid(LOG_COLLECT_TASK_PATH_ID_1);
//        path1host2Point2.setCollecttaskhostname(LOG_COLLECT_TASK_HOST_NAME_2);
//        path1host2Point2.setCollecttaskid(LOG_COLLECT_TASK_ID);
//        //lable
//        path1host2Point2.setBusinesstimestamp(df.parse("2022-01-17 14:59:23").getTime());
//        // single line statics
//        path1host2Point2.setReadtimeperevent(1d);
//        path1host2Point2.setReadtimepereventmin(1d);
//        path1host2Point2.setReadtimepereventmax(39d);
//        path1host2Point2.setReadtimepereventmean(27d);
//        path1host2Point2.setReadtimepereventstd(1.9);
//        path1host2Point2.setReadtimeperevent55quantile(15d);
//        path1host2Point2.setReadtimeperevent75quantile(23d);
//        path1host2Point2.setReadtimeperevent95quantile(32d);
//        path1host2Point2.setReadtimeperevent99quantile(37d);
//        // single line non statics
//        path1host2Point2.setSendbytes(17 * 1024 * 1024L);
//        metricsLogCollectTaskDAO.insert(path1host2Point2);
//
//        MetricsLogCollectTaskPO path2host1Point2 = new MetricsLogCollectTaskPO();
//        path2host1Point2.setHeartbeattime(HEART_BEAT_TIME_2);
//        path2host1Point2.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_2));
//        path2host1Point2.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_2));
//        path2host1Point2.setPath(LOG_COLLECT_TASK_PATH_2);
//        path2host1Point2.setPathid(LOG_COLLECT_TASK_PATH_ID_2);
//        path2host1Point2.setCollecttaskhostname(LOG_COLLECT_TASK_HOST_NAME_1);
//        path2host1Point2.setCollecttaskid(LOG_COLLECT_TASK_ID);
//        //lable
//        path2host1Point2.setBusinesstimestamp(df.parse("2022-01-17 15:39:23").getTime());
//        // single line statics
//        path2host1Point2.setReadtimeperevent(5d);
//        path2host1Point2.setReadtimepereventmin(3d);
//        path2host1Point2.setReadtimepereventmax(599d);
//        path2host1Point2.setReadtimepereventmean(323d);
//        path2host1Point2.setReadtimepereventstd(1.5);
//        path2host1Point2.setReadtimeperevent55quantile(235d);
//        path2host1Point2.setReadtimeperevent75quantile(312d);
//        path2host1Point2.setReadtimeperevent95quantile(292d);
//        path2host1Point2.setReadtimeperevent99quantile(397d);
//        // single line non statics
//        path2host1Point2.setSendbytes(259 * 1024 * 1024L);
//        metricsLogCollectTaskDAO.insert(path2host1Point2);
//
//        MetricsLogCollectTaskPO path2host2Point2 = new MetricsLogCollectTaskPO();
//        path2host2Point2.setHeartbeattime(HEART_BEAT_TIME_2);
//        path2host2Point2.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_2));
//        path2host2Point2.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_2));
//        path2host2Point2.setPath(LOG_COLLECT_TASK_PATH_2);
//        path2host2Point2.setPathid(LOG_COLLECT_TASK_PATH_ID_2);
//        path2host2Point2.setCollecttaskhostname(LOG_COLLECT_TASK_HOST_NAME_2);
//        path2host2Point2.setCollecttaskid(LOG_COLLECT_TASK_ID);
//        //lable
//        path2host2Point2.setBusinesstimestamp(df.parse("2022-01-17 23:39:23").getTime());
//        // single line statics
//        path2host2Point2.setReadtimeperevent(3d);
//        path2host2Point2.setReadtimepereventmin(2d);
//        path2host2Point2.setReadtimepereventmax(99d);
//        path2host2Point2.setReadtimepereventmean(29d);
//        path2host2Point2.setReadtimepereventstd(9.5);
//        path2host2Point2.setReadtimeperevent55quantile(35d);
//        path2host2Point2.setReadtimeperevent75quantile(31d);
//        path2host2Point2.setReadtimeperevent95quantile(22d);
//        path2host2Point2.setReadtimeperevent99quantile(37d);
//        // single line non statics
//        path2host2Point2.setSendbytes(94 * 1024 * 1024L);
//        metricsLogCollectTaskDAO.insert(path2host2Point2);
//
//    }
//
//    /**
//     * 初始化agent相关metrics数据
//     */
//    private void initAgentMetricsData() {
//        MetricsAgentPO point1 = new MetricsAgentPO();
//        point1.setHostname(HOST_NAME);
//        point1.setHeartbeattime(HEART_BEAT_TIME_1);
//        point1.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        point1.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_1));
//        //lable
//        point1.setAgentversion("0.0.1");
//        // single line non statics
//        point1.setWritecount(39 * 1024 * 1024L);
//        metricsAgentDAO.insert(point1);
//
//        MetricsAgentPO point2 = new MetricsAgentPO();
//        point2.setHostname(HOST_NAME);
//        point2.setHeartbeattime(HEART_BEAT_TIME_2);
//        point2.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_2));
//        point2.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_2));
//        //lable
//        point2.setAgentversion("0.0.2");
//        // single line non statics
//        point2.setWritecount(14 * 1024 * 1024L);
//        metricsAgentDAO.insert(point2);
//
//    }
//
//    /**
//     * 初始化process相关metrics数据
//     */
//    private void initProcessMetricsData() throws Exception {
//
//        MetricsProcessPO point1 = new MetricsProcessPO();
//        point1.setHostname(HOST_NAME);
//        point1.setHeartbeattime(HEART_BEAT_TIME_1);
//        point1.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        point1.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_1));
//        //lable
//        point1.setProcstartuptime(df.parse("2022-01-14 18:26:23").getTime());
//        // single line statics
//        point1.setProccpuutil(30d);
//        point1.setProccpuutilmin(1.5);
//        point1.setProccpuutilmax(78d);
//        point1.setProccpuutilmean(39d);
//        point1.setProccpuutilstd(1.7);
//        point1.setProccpuutil55quantile(21d);
//        point1.setProccpuutil75quantile(29d);
//        point1.setProccpuutil95quantile(45d);
//        point1.setProccpuutil99quantile(15d);
//        // single line non statics
//        point1.setProcmemused(55 * 1024 * 1024 * 1024L);
//        metricsProcessDAO.insert(point1);
//
//        MetricsProcessPO point2 = new MetricsProcessPO();
//        point2.setHostname(HOST_NAME);
//        point2.setHeartbeattime(HEART_BEAT_TIME_2);
//        point2.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_2));
//        point2.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_2));
//        //lable
//        point2.setProcstartuptime(df.parse("2022-01-19 18:26:23").getTime());
//        // single line statics
//        point2.setProccpuutil(10d);
//        point2.setProccpuutilmin(9.5);
//        point2.setProccpuutilmax(58d);
//        point2.setProccpuutilmean(29d);
//        point2.setProccpuutilstd(9.4);
//        point2.setProccpuutil55quantile(31d);
//        point2.setProccpuutil75quantile(49d);
//        point2.setProccpuutil95quantile(15d);
//        point2.setProccpuutil99quantile(29d);
//        // single line non statics
//        point2.setProcmemused(35 * 1024 * 1024 * 1024L);
//        metricsProcessDAO.insert(point2);
//
//    }
//
//    /**
//     * 初始化system相关metrics数据
//     */
//    private void initSystemMetricsData() throws Exception {
//
//        MetricsSystemPO point1 = new MetricsSystemPO();
//        point1.setHostname(HOST_NAME);
//        point1.setHeartbeattime(HEART_BEAT_TIME_1);
//        point1.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        point1.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_1));
//        //lable
//        point1.setSystemstartuptime(df.parse("2022-01-14 18:26:23").getTime());
//        // single line statics
//        point1.setSystemcpuutil(30d);
//        point1.setSystemcpuutilmin(1.5);
//        point1.setSystemcpuutilmax(78d);
//        point1.setSystemcpuutilmean(39d);
//        point1.setSystemcpuutilstd(1.7);
//        point1.setSystemcpuutil55quantile(21d);
//        point1.setSystemcpuutil75quantile(29d);
//        point1.setSystemcpuutil95quantile(45d);
//        point1.setSystemcpuutil99quantile(15d);
//        // single line non statics
//        point1.setSystemmemfree(55 * 1024 * 1024 * 1024L);
//        metricsSystemDAO.insert(point1);
//
//        MetricsSystemPO point2 = new MetricsSystemPO();
//        point2.setHostname(HOST_NAME);
//        point2.setHeartbeattime(HEART_BEAT_TIME_2);
//        point2.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_2));
//        point2.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_2));
//        //lable
//        point2.setSystemstartuptime(df.parse("2022-01-19 18:26:23").getTime());
//        // single line statics
//        point2.setSystemcpuutil(10d);
//        point2.setSystemcpuutilmin(9.5);
//        point2.setSystemcpuutilmax(58d);
//        point2.setSystemcpuutilmean(29d);
//        point2.setSystemcpuutilstd(9.4);
//        point2.setSystemcpuutil55quantile(31d);
//        point2.setSystemcpuutil75quantile(49d);
//        point2.setSystemcpuutil95quantile(15d);
//        point2.setSystemcpuutil99quantile(29d);
//        // single line non statics
//        point2.setSystemmemfree(35 * 1024 * 1024 * 1024L);
//        metricsSystemDAO.insert(point2);
//
//    }
//
//    /**
//     * 初始化net card相关数据
//     */
//    private void iniNetCardData() {
//        MetricsNetCardPO netCard1point1 = new MetricsNetCardPO();
//        netCard1point1.setHostname(HOST_NAME);
//        netCard1point1.setHeartbeattime(HEART_BEAT_TIME_1);
//        netCard1point1.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        netCard1point1.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_1));
//        netCard1point1.setSystemnetcardsbanddevice(NET_CARD_DEVICE_1);
//        netCard1point1.setSystemnetcardsbandmacaddress(NET_CARD_MAC_ADDRESS_1);
//        netCard1point1.setSystemnetcardssendbytesps(20*1024*1024L);
//        netCard1point1.setSystemnetcardssendbytespsmin(4*1024*1024L);
//        netCard1point1.setSystemnetcardssendbytespsmax(99*1024*1024L);
//        netCard1point1.setSystemnetcardssendbytespsmean(39*1024*1024d);
//        netCard1point1.setSystemnetcardssendbytespsstd(3.9);
//        netCard1point1.setSystemnetcardssendbytesps55quantile(58*1024*1024L);
//        netCard1point1.setSystemnetcardssendbytesps75quantile(29*1024*1024L);
//        netCard1point1.setSystemnetcardssendbytesps95quantile(14*1024*1024L);
//        netCard1point1.setSystemnetcardssendbytesps99quantile(9*1024*1024L);
//        netCard1point1.setSystemnetcardsbandwidth(9000 * 1024 * 1024L); // non statics
//        metricsNetCardDAO.insertSelective(netCard1point1);
//
//        MetricsNetCardPO netCard2point1 = new MetricsNetCardPO();
//        netCard2point1.setHostname(HOST_NAME);
//        netCard2point1.setHeartbeattime(HEART_BEAT_TIME_1);
//        netCard2point1.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        netCard2point1.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_1));
//        netCard2point1.setSystemnetcardsbanddevice(NET_CARD_DEVICE_2);
//        netCard2point1.setSystemnetcardsbandmacaddress(NET_CARD_MAC_ADDRESS_2);
//        netCard2point1.setSystemnetcardssendbytesps(17*1024*1024L);
//        netCard2point1.setSystemnetcardssendbytespsmin(8*1024*1024L);
//        netCard2point1.setSystemnetcardssendbytespsmax(99*1024*1024L);
//        netCard2point1.setSystemnetcardssendbytespsmean(26*1024*1024d);
//        netCard2point1.setSystemnetcardssendbytespsstd(1.8);
//        netCard2point1.setSystemnetcardssendbytesps55quantile(49*1024*1024L);
//        netCard2point1.setSystemnetcardssendbytesps75quantile(39*1024*1024L);
//        netCard2point1.setSystemnetcardssendbytesps95quantile(19*1024*1024L);
//        netCard2point1.setSystemnetcardssendbytesps99quantile(29*1024*1024L);
//        netCard2point1.setSystemnetcardsbandwidth(100 * 1024 * 1024L); // non statics
//        metricsNetCardDAO.insertSelective(netCard2point1);
//
//        MetricsNetCardPO netCard1point2 = new MetricsNetCardPO();
//        netCard1point2.setHostname(HOST_NAME);
//        netCard1point2.setHeartbeattime(HEART_BEAT_TIME_2);
//        netCard1point2.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_2));
//        netCard1point2.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_2));
//        netCard1point2.setSystemnetcardsbanddevice(NET_CARD_DEVICE_1);
//        netCard1point2.setSystemnetcardsbandmacaddress(NET_CARD_MAC_ADDRESS_1);
//        netCard1point2.setSystemnetcardssendbytesps(5*1024*1024L);
//        netCard1point2.setSystemnetcardssendbytespsmin(3*1024*1024L);
//        netCard1point2.setSystemnetcardssendbytespsmax(89*1024*1024L);
//        netCard1point2.setSystemnetcardssendbytespsmean(15*1024*1024d);
//        netCard1point2.setSystemnetcardssendbytespsstd(9.8);
//        netCard1point2.setSystemnetcardssendbytesps55quantile(59*1024*1024L);
//        netCard1point2.setSystemnetcardssendbytesps75quantile(29*1024*1024L);
//        netCard1point2.setSystemnetcardssendbytesps95quantile(9*1024*1024L);
//        netCard1point2.setSystemnetcardssendbytesps99quantile(4*1024*1024L);
//        netCard1point2.setSystemnetcardsbandwidth(500 * 1024 * 1024L); // non statics
//        metricsNetCardDAO.insertSelective(netCard1point2);
//
//        MetricsNetCardPO netCard2point2 = new MetricsNetCardPO();
//        netCard2point2.setHostname(HOST_NAME);
//        netCard2point2.setHeartbeattime(HEART_BEAT_TIME_2);
//        netCard2point2.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_2));
//        netCard2point2.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_2));
//        netCard2point2.setSystemnetcardsbanddevice(NET_CARD_DEVICE_2);
//        netCard2point2.setSystemnetcardsbandmacaddress(NET_CARD_MAC_ADDRESS_2);
//        netCard2point2.setSystemnetcardssendbytesps(27*1024*1024L);
//        netCard2point2.setSystemnetcardssendbytespsmin(1*1024*1024L);
//        netCard2point2.setSystemnetcardssendbytespsmax(59*1024*1024L);
//        netCard2point2.setSystemnetcardssendbytespsmean(5*1024*1024d);
//        netCard2point2.setSystemnetcardssendbytespsstd(5.8);
//        netCard2point2.setSystemnetcardssendbytesps55quantile(29*1024*1024L);
//        netCard2point2.setSystemnetcardssendbytesps75quantile(17*1024*1024L);
//        netCard2point2.setSystemnetcardssendbytesps95quantile(13*1024*1024L);
//        netCard2point2.setSystemnetcardssendbytesps99quantile(1*1024*1024L);
//        netCard2point2.setSystemnetcardsbandwidth(900 * 1024 * 1024L); // non statics
//        metricsNetCardDAO.insertSelective(netCard2point2);
//    }
//
//    /**
//     * 初始化disk/io相关数据
//     */
//    private void iniDiskIOtData() {
//        MetricsDiskPO disk1point1 = new MetricsDiskPO();
//        disk1point1.setHostname(HOST_NAME);
//        disk1point1.setSystemdiskpath(DISK_PATH_1);
//        disk1point1.setSystemdiskfstype(DISK_FS_Type_1);
//
//        disk1point1.setSystemdiskbytesfree(1090 * 1024 * 1024 * 1024L); // non statics
//
//        disk1point1.setHeartbeattime(HEART_BEAT_TIME_1);
//        disk1point1.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        disk1point1.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_1));
//        metricsDiskDAO.insertSelective(disk1point1);
//
//        MetricsDiskPO disk2point1 = new MetricsDiskPO();
//        disk2point1.setHostname(HOST_NAME);
//        disk2point1.setSystemdiskpath(DISK_PATH_2);
//        disk2point1.setSystemdiskfstype(DISK_FS_Type_2);
//
//        disk2point1.setSystemdiskbytesfree(400 * 1024 * 1024 * 1024L); // non statics
//
//        disk2point1.setHeartbeattime(HEART_BEAT_TIME_1);
//        disk2point1.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_1));
//        disk2point1.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_1));
//        metricsDiskDAO.insertSelective(disk2point1);
//
//        MetricsDiskPO disk1point2 = new MetricsDiskPO();
//        disk1point2.setHostname(HOST_NAME);
//        disk1point2.setSystemdiskpath(DISK_PATH_1);
//        disk1point2.setSystemdiskfstype(DISK_FS_Type_1);
//
//        disk1point2.setSystemdiskbytesfree(199 * 1024 * 1024 * 1024L); // non statics
//
//        disk1point2.setHeartbeattime(HEART_BEAT_TIME_2);
//        disk1point2.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_2));
//        disk1point2.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_2));
//        metricsDiskDAO.insertSelective(disk1point2);
//
//        MetricsDiskPO disk2point2 = new MetricsDiskPO();
//        disk2point2.setHostname(HOST_NAME);
//        disk2point2.setSystemdiskpath(DISK_PATH_2);
//        disk2point2.setSystemdiskfstype(DISK_FS_Type_2);
//
//        disk2point2.setSystemdiskbytesfree(1990 * 1024 * 1024 * 1024L); // non statics
//
//        disk2point2.setHeartbeattime(HEART_BEAT_TIME_2);
//        disk2point2.setHeartbeattimeminute(DateUtils.getMinuteUnitTimeStamp(HEART_BEAT_TIME_2));
//        disk2point2.setHeartbeattimehour(DateUtils.getHourUnitTimeStamp(HEART_BEAT_TIME_2));
//        metricsDiskDAO.insertSelective(disk2point2);
//    }
//
//}
