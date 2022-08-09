package com.didichuxing.datachannel.agentmanager.core.metrics.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.MetricsLogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.BusinessMetricsQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.*;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricNodeVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPointLine;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.*;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.metrics.*;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.FileLogCollectPathManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;
import com.didichuxing.datachannel.agentmanager.persistence.*;
import com.didichuxing.datachannel.agentmanager.thirdpart.kafkacluster.extension.KafkaClusterManageServiceExtension;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

@org.springframework.stereotype.Service
public class MetricsManageServiceImpl implements MetricsManageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsManageServiceImpl.class);

    @Autowired
    @Qualifier(value = "metricsSystemDAO")
    private MetricsSystemDAO metricsSystemDAO;

    @Autowired
    @Qualifier(value = "metricsAgentDAO")
    private MetricsAgentDAO metricsAgentDAO;

    @Autowired
    @Qualifier(value = "metricsDiskDAO")
    private MetricsDiskDAO metricsDiskDAO;

    @Autowired
    @Qualifier(value = "metricsNetCardDAO")
    private MetricsNetCardDAO metricsNetCardDAO;

    @Autowired
    @Qualifier(value = "metricsProcessDAO")
    private MetricsProcessDAO metricsProcessDAO;

    @Autowired
    @Qualifier(value = "metricsLogCollectTaskDAO")
    private MetricsLogCollectTaskDAO metricsLogCollectTaskDAO;

    @Autowired
    @Qualifier(value = "metricsDiskIODAO")
    private MetricsDiskIODAO metricsDiskIODAO;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private FileLogCollectPathManageService fileLogCollectPathManageService;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private KafkaClusterManageService kafkaClusterManageService;

    @Autowired
    private KafkaClusterManageServiceExtension kafkaClusterManageServiceExtension;

    /**
     * top n 默认值
     */
    private static Integer TOP_N_DEFAULT_VALUE = 5;

    /**
     * 指标排序类型 默认值
     */
    private static Integer SORT_METRIC_TYPE_DEFAULT_VALUE = 0;

    /* metrics write 相关 */
    private volatile boolean metricsWriteStopTrigger = false;
    private volatile boolean metricsWriteStopped = true;
    private static final Long RECEIVER_CLOSE_TIME_OUT_MS = 1 * 60 * 1000l;
    private ReceiverDO lastAgentMetricsReceiver = null;
    private static final ExecutorService metricsWriteThreadPool = Executors.newSingleThreadExecutor();

    @Override
    @Transactional
    public void insertMetrics(String metricsRecord) {
        handleInsertMetrics(metricsRecord);
    }

    @Override
    public void consumeAndWriteMetrics() {
        /*
         * 1.）获取 agent metrics 对应接收端信息、topic
         */
        ReceiverDO agentMetricsReceiver = kafkaClusterManageService.getAgentMetricsTopicExistsReceiver();
        /*
         * 2.）校验较上一次获取是否相同，如不同，则立即进行对应变更处理
         */
        if(metricsReceiverChanged(lastAgentMetricsReceiver, agentMetricsReceiver)) {
            LOGGER.info(
                    String.format("Metrics receiver changed, before is %s, after is %s", JSON.toJSONString(lastAgentMetricsReceiver), JSON.toJSONString(agentMetricsReceiver))
            );
            restartWriteMetrics(agentMetricsReceiver);
            lastAgentMetricsReceiver = agentMetricsReceiver;
        }
    }

    private boolean metricsReceiverChanged(ReceiverDO lastAgentMetricsReceiver, ReceiverDO agentMetricsReceiver) {
        if(null == lastAgentMetricsReceiver && null == agentMetricsReceiver) {
            return false;
        }
        if(null == agentMetricsReceiver) {
            return false;
        }
        if(null == lastAgentMetricsReceiver && null != agentMetricsReceiver) {
            return true;
        }
        if(
                !lastAgentMetricsReceiver.getAgentMetricsTopic().equals(agentMetricsReceiver.getAgentMetricsTopic()) ||
                        !lastAgentMetricsReceiver.getKafkaClusterBrokerConfiguration().equals(agentMetricsReceiver.getKafkaClusterBrokerConfiguration())
        ) {
            return true;
        }
        return false;
    }

    public void writeMetrics(String agentMetricsTopic, String kafkaClusterBrokerConfiguration) {
        try {
            LOGGER.info("Thread: {}, cluster: {}, topic: {}", Thread.currentThread().getName(), kafkaClusterBrokerConfiguration, agentMetricsTopic);
            Properties properties = kafkaClusterManageServiceExtension.getKafkaConsumerProperties(kafkaClusterBrokerConfiguration);
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
            consumer.subscribe(Arrays.asList(agentMetricsTopic));
            while (true) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
                    for (ConsumerRecord<String, String> record : records) {
                        insertMetrics(record.value());
                    }
                    if (metricsWriteStopTrigger) {
                        consumer.close();
                        break;
                    }
                } catch (Throwable ex) {
                    LOGGER.error(
                            String.format("writeMetrics error: %s", ex.getMessage()),
                            ex
                    );
                    consumer.close();
                    break;
                }
            }
        } catch (Throwable ex) {
            LOGGER.error(
                    String.format("writeMetrics error: %s", ex.getMessage()),
                    ex
            );
        } finally {
            metricsWriteStopped = true;
        }
    }

    private void restartWriteMetrics(ReceiverDO agentMetricsReceiver) {
        LOGGER.info(
                String.format("restartWriteMetrics: Is going to stop receiver %s", JSON.toJSONString(lastAgentMetricsReceiver))
        );
        /*
         * stop
         */
        metricsWriteStopTrigger = true;
        Long currentTime = System.currentTimeMillis();
        while (
                !metricsWriteStopped &&
                        (System.currentTimeMillis() - currentTime) <= RECEIVER_CLOSE_TIME_OUT_MS
        ) {
            try {
                // 等待现有的kafka consumer线程全部关闭
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                LOGGER.error("thread interrupted", e);
            }
        }
        LOGGER.info(
                String.format("restartWriteErrorLogs: Stop receiver %s successful", JSON.toJSONString(lastAgentMetricsReceiver))
        );
        LOGGER.info(
                String.format("restartWriteErrorLogs: Is going to start receiver %s", JSON.toJSONString(agentMetricsReceiver))
        );
        /*
         * start
         */
        metricsWriteStopped = false;
        metricsWriteStopTrigger = false;
        metricsWriteThreadPool.execute(() -> writeMetrics(agentMetricsReceiver.getAgentMetricsTopic(), agentMetricsReceiver.getKafkaClusterBrokerConfiguration()));
        LOGGER.info(
                String.format("restartWriteErrorLogs: Start receiver %s successful", JSON.toJSONString(agentMetricsReceiver))
        );
    }

    private void handleInsertMetrics(String metricsRecord) {
            try {
                if(StringUtils.isNotBlank(metricsRecord)) {
                    JSONObject object = JSON.parseObject(metricsRecord);
                    Object taskMetricsObj = object.get("taskMetrics");
                    Object agentMetricsObj = object.get("agentMetrics");
                    if(taskMetricsObj != null) {
                        String taskMetricsStr = taskMetricsObj.toString();
                        TaskMetrics taskMetrics = JSON.parseObject(taskMetricsStr, TaskMetrics.class);
                        MetricsLogCollectTaskPO logCollectTaskPO = ConvertUtil.obj2Obj(taskMetrics, MetricsLogCollectTaskPO.class);
                        metricsLogCollectTaskDAO.insertSelective(logCollectTaskPO);
                    } else if(agentMetricsObj != null) {
                        String agentMetricsStr = agentMetricsObj.toString();
                        AgentMetrics agentMetrics = JSON.parseObject(agentMetricsStr, AgentMetrics.class);
                        AgentBusinessMetrics agentBusinessMetrics = agentMetrics.getAgentBusinessMetrics();
                        SystemMetrics systemMetrics = agentMetrics.getSystemMetrics();
                        ProcessMetrics processMetrics = agentMetrics.getProcessMetrics();
                        List<DiskIOMetrics> diskIOMetricsList = agentMetrics.getDiskIOMetricsList();
                        List<DiskMetrics> diskMetricsList = agentMetrics.getDiskMetricsList();
                        List<NetCardMetrics> netCardMetrics = agentMetrics.getNetCardMetricsList();
                        MetricsAgentPO metricsAgentPO = ConvertUtil.obj2Obj(agentBusinessMetrics, MetricsAgentPO.class);
                        MetricsSystemPO metricsSystemPO = ConvertUtil.obj2Obj(systemMetrics, MetricsSystemPO.class);
                        MetricsProcessPO metricsProcessPO = ConvertUtil.obj2Obj(processMetrics, MetricsProcessPO.class);
                        List<MetricsDiskIOPO> metricsDiskIOPOS = ConvertUtil.list2List(diskIOMetricsList, MetricsDiskIOPO.class);
                        List<MetricsDiskPO> metricsDiskPOList = ConvertUtil.list2List(diskMetricsList, MetricsDiskPO.class);
                        List<MetricsNetCardPO> metricsNetCardPOList = ConvertUtil.list2List(netCardMetrics, MetricsNetCardPO.class);
                        for (MetricsDiskIOPO metricsDiskIOPO : metricsDiskIOPOS) {
                            metricsDiskIODAO.insertSelective(metricsDiskIOPO);
                        }
                        for (MetricsDiskPO metricsDiskPO : metricsDiskPOList) {
                            metricsDiskDAO.insertSelective(metricsDiskPO);
                        }
                        for (MetricsNetCardPO metricsNetCardPO : metricsNetCardPOList) {
                            metricsNetCardDAO.insertSelective(metricsNetCardPO);
                        }
                        metricsAgentDAO.insertSelective(metricsAgentPO);
                        metricsSystemDAO.insertSelective(metricsSystemPO);
                        metricsProcessDAO.insertSelective(metricsProcessPO);
                    }
                }
            } catch (Exception ex) {
                LOGGER.error(
                        String.format(
                                "handleInsertMetrics {%s} error, root cause is: %s",
                                metricsRecord,
                                ex.getMessage()
                        ),
                        ex
                );
            }
    }

    @Override
    public MetricNodeVO getMetricsTreeByMetricType(Integer metricTypeCode) {
        MetricTypeEnum rootMetricTypeEnum = MetricTypeEnum.fromCode(metricTypeCode);
        if(null == rootMetricTypeEnum) {
            throw new ServiceException(
                    String.format("class=MetricsServiceImpl||method=getMetricsTreeByMetricType||msg={%s}", ErrorCodeEnum.METRICS_TYPE_NOT_EXISTS.getMessage()),
                    ErrorCodeEnum.METRICS_TYPE_NOT_EXISTS.getCode()
            );
        }
        return handleGetMetricsTreeByMetricType(rootMetricTypeEnum);
    }

    @Override
    public MetricPanel getMetric(BusinessMetricsQueryDTO metricQueryDTO) {
        Integer metricCode = metricQueryDTO.getMetricCode();
        MetricFieldEnum metricFieldEnum = MetricFieldEnum.fromMetricCode(metricCode);
        if(null == metricFieldEnum) {
            throw new ServiceException(
                    String.format("class=MetricsServiceImpl||method=getMetric||msg={%s}", ErrorCodeEnum.METRIC_NOT_EXISTS.getMessage()),
                    ErrorCodeEnum.METRIC_NOT_EXISTS.getCode()
            );
        }
        if(isLogCollectTaskMetric(metricFieldEnum)) {//日志采集任务相关指标
            MetricPanel metricPanel = getLogCollectTaskMetric(metricQueryDTO, metricFieldEnum);
            if(panelIsNull(metricPanel)) {//当前时间未获取到，end 最晚时间获取
                return reGetLogCollectTaskMetric(metricQueryDTO, metricFieldEnum, metricPanel);
            } else {
                return metricPanel;
            }
        } else {//agent相关指标
            MetricPanel metricPanel = getAgentMetric(metricQueryDTO, metricFieldEnum);
            if(panelIsNull(metricPanel)) {//当前时间未获取到，end 最晚时间获取
                return reGetAgentMetric(metricQueryDTO, metricFieldEnum, metricPanel);
            } else {
                return metricPanel;
            }
        }
    }

    private MetricPanel reGetAgentMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum, MetricPanel metricPanel) {
        Long endTime = null;
        Long sortTime = null;
        if(isSystemMetric(metricFieldEnum)) {
            MetricsSystemPO metricsSystemPO = getLastSystemMetric(metricQueryDTO.getHostName());
            if(null != metricsSystemPO) {
                endTime = metricsSystemPO.getHeartbeattime();
                sortTime = metricsSystemPO.getHeartbeattimeminute();
            }
        } else if(isProcessMetric(metricFieldEnum)) {
            MetricsProcessPO metricsProcessPO = getLastProcessMetric(metricQueryDTO.getHostName());
            if(null != metricsProcessPO) {
                endTime = metricsProcessPO.getHeartbeattime();
                sortTime = metricsProcessPO.getHeartbeattimeminute();
            }
        } else if(isAgentBusinessMetric(metricFieldEnum)) {
            MetricsAgentPO metricsAgentPO = getLastAgentMetric(metricQueryDTO.getHostName());
            if(null != metricsAgentPO) {
                endTime = metricsAgentPO.getHeartbeattime();
                sortTime = metricsAgentPO.getHeartbeattimeminute();
            }
        } else {
            return metricPanel;
        }
        //TODO：须进一步添加 disk/io disk net card 校验 else if(isAgentBusinessMetric(metricFieldEnum)) {
        if(null != sortTime && null != endTime) {
            metricQueryDTO.setEndTime(endTime);
            metricQueryDTO.setSortTime(sortTime);
            return getAgentMetric(metricQueryDTO, metricFieldEnum);
        } else {
            return metricPanel;
        }
    }

    private MetricPanel reGetLogCollectTaskMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum, MetricPanel metricPanel) {
        MetricsLogCollectTaskPO metricsLogCollectTaskPO = getLastLogCollectTaskMetric(metricQueryDTO.getLogCollectTaskId(), metricQueryDTO.getPathId(), metricQueryDTO.getHostName());
        if(null != metricsLogCollectTaskPO) {
            metricQueryDTO.setEndTime(metricsLogCollectTaskPO.getHeartbeattime());
            metricQueryDTO.setSortTime(metricsLogCollectTaskPO.getHeartbeattimeminute());
            return getLogCollectTaskMetric(metricQueryDTO, metricFieldEnum);
        } else {
            return metricPanel;
        }
    }

    private boolean panelIsNull(MetricPanel metricPanel) {
        return null == metricPanel.getLableValue() &&
                CollectionUtils.isEmpty(metricPanel.getMultiLineChatValue()) &&
                (
                        null == metricPanel.getSingleLineChatValue() ||
                                (null != metricPanel.getSingleLineChatValue() && CollectionUtils.isEmpty(metricPanel.getSingleLineChatValue().getMetricPointList()))
                );
    }

    @Override
    public List<MetricsLogCollectTaskPO> getErrorMetrics(Long logCollectTaskId, Long pathId, String hostName, String errorFieldName, Long startHeartbeatTime, Long endHeartbeatTime) {
        Map<String, Object> params = new HashMap<>();
        params.put("logCollectTaskId", logCollectTaskId);
        params.put("pathId", pathId);
        params.put("hostName", hostName);
        params.put("errorFieldName", errorFieldName);
        params.put("startHeartbeatTime", startHeartbeatTime);
        params.put("endHeartbeatTime", endHeartbeatTime);
        return metricsLogCollectTaskDAO.getErrorMetrics(params);
    }

    @Override
    public MetricsLogCollectTaskPO getMetricLogCollectTask(Long logCollectTaskMetricId) {
        return metricsLogCollectTaskDAO.selectByPrimaryKey(logCollectTaskMetricId);
    }

    @Override
    public Long getSumMetricAllAgents(MetricFieldEnum metricFieldEnum, Long startTime, Long endTime, String fieldName) {
        Map<String, Object> params = new HashMap<>();
        params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
        if(StringUtils.isNotBlank(fieldName)) {
            params.put("fieldName", fieldName);
        } else {
            params.put("fieldName", metricFieldEnum.getFieldName());
        }
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        Double value = 0d;
        if(isProcessMetric(metricFieldEnum)) {
            value = metricsProcessDAO.getSumMetricAllAgents(params);
        } else if(isAgentBusinessMetric(metricFieldEnum)) {
            value = metricsAgentDAO.getSumMetricAllAgents(params);
        } else if(isSystemMetric(metricFieldEnum)) {
            value = metricsSystemDAO.getSumMetricAllAgents(params);
        } else {
            //TODO：
            throw new RuntimeException();
        }
        if(null == value) {
            return 0L;
        } else {
            return Double.valueOf(Math.ceil(value)).longValue();
        }
    }

    @Override
    public List<Pair<Object, Object>> getTopNByMetric(MetricFieldEnum metricFieldEnum, Long startTime, Long endTime, String sortTimeField, boolean logCollectTaskByServiceNames) {
        if(isSystemMetric(metricFieldEnum)) {
            return handleGetTopNBySystemMetric(metricFieldEnum, startTime, endTime, sortTimeField);
        } else if(isProcessMetric(metricFieldEnum)) {
            return handleGetTopNByProcessMetric(metricFieldEnum, startTime, endTime, sortTimeField);
        } else if(isAgentBusinessMetric(metricFieldEnum)) {
            return handleGetTopNByAgentBusinessMetric(metricFieldEnum, startTime, endTime, sortTimeField);
        } else if(isLogCollectTaskMetric(metricFieldEnum)) {
            if(logCollectTaskByServiceNames) {
                return handleGetTopNByLogCollectTaskMetricPerServiceNames(metricFieldEnum, startTime, endTime, sortTimeField);
            } else {
                return handleGetTopNByLogCollectTaskMetricPerLogCollectTaskId(metricFieldEnum, startTime, endTime, sortTimeField);
            }
        } else {
            //TODO：
            throw new RuntimeException();
        }
    }

    @Override
    @Transactional
    public void clearExpireMetrics(Integer metricsExpireDays) {
        Long heartBeatTime = DateUtils.getBeforeDays(new Date(), metricsExpireDays).getTime();
        metricsLogCollectTaskDAO.deleteByLtHeartbeatTime(heartBeatTime);
        metricsAgentDAO.deleteByLtHeartbeatTime(heartBeatTime);
        metricsProcessDAO.deleteByLtHeartbeatTime(heartBeatTime);
        metricsSystemDAO.deleteByLtHeartbeatTime(heartBeatTime);
        metricsNetCardDAO.deleteByLtHeartbeatTime(heartBeatTime);
        metricsDiskDAO.deleteByLtHeartbeatTime(heartBeatTime);
        metricsDiskIODAO.deleteByLtHeartbeatTime(heartBeatTime);
    }

    @Override
    public Object getAggregationQueryPerLogCollectTskAndPathAndHostNameFromMetricsLogCollectTask(
            Long logCollectTaskId,
            Long fileLogCollectPathId,
            String logCollectTaskHostName,
            Long heartbeatTimeStart,
            Long heartbeatTimeEnd,
            String aggregationFunction,
            String aggregationField
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("logCollectTaskId", logCollectTaskId);
        params.put("pathId", fileLogCollectPathId);
        params.put("hostName", logCollectTaskHostName);
        params.put("startTime", heartbeatTimeStart);
        params.put("endTime", heartbeatTimeEnd);
        params.put("function", aggregationFunction);
        params.put("fieldName", aggregationField);
        return metricsLogCollectTaskDAO.aggregationQueryPerLogCollectTskAndPathAndHostNameFromMetricsLogCollectTask(params);
    }

    @Override
    public MetricsLogCollectTaskPO getLastLogCollectTaskMetric(Long logCollectTaskId, Long pathId, String hostName) {
        Map<String, Object> params = new HashMap<>();
        if(null != logCollectTaskId && logCollectTaskId > 0l) {
            params.put("logCollectTaskId", logCollectTaskId);
        }
        if(null != pathId && pathId > 0l) {
            params.put("pathId", pathId);
        }
        if(StringUtils.isNotBlank(hostName)) {
            params.put("hostName", hostName);
        }
        return metricsLogCollectTaskDAO.getLastRecord(params);
    }

    @Override
    public Object getAggregationQueryPerHostNameFromMetricsAgent(
            String hostName,
            Long heartbeatTimeStart,
            Long heartbeatTimeEnd,
            String aggregationFunction,
            String aggregationField
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("hostName", hostName);
        params.put("startTime", heartbeatTimeStart);
        params.put("endTime", heartbeatTimeEnd);
        params.put("function", aggregationFunction);
        params.put("fieldName", aggregationField);
        return metricsAgentDAO.getAggregationQueryPerHostNameFromMetricsAgent(params);
    }

    @Override
    public MetricsAgentPO getLastAgentMetric(String hostName) {
        Map<String, Object> params = new HashMap<>();
        params.put("hostName", hostName);
        return metricsAgentDAO.getLastRecord(params);
    }

    @Override
    public MetricsSystemPO getLastSystemMetric(String hostName) {
        Map<String, Object> params = new HashMap<>();
        params.put("hostName", hostName);
        return metricsSystemDAO.getLastRecord(params);
    }

    @Override
    public MetricsProcessPO getLastProcessMetric(String hostName) {
        Map<String, Object> params = new HashMap<>();
        params.put("hostName", hostName);
        return metricsProcessDAO.getLastRecord(params);
    }

    @Override
    public Object getAggregationQueryPerHostNameFromMetricsProcess(String hostName, Long heartbeatTimeStart, Long heartbeatTimeEnd, String aggregationFunction, String aggregationField) {
        Map<String, Object> params = new HashMap<>();
        params.put("hostName", hostName);
        params.put("startTime", heartbeatTimeStart);
        params.put("endTime", heartbeatTimeEnd);
        params.put("function", aggregationFunction);
        params.put("fieldName", aggregationField);
        return metricsProcessDAO.getAggregationQueryPerHostNameFromMetricsProcess(params);
    }

    @Override
    public List<MetricsLogCollectTaskDO> getLastCollectTaskMetricsByAgentHostName(String hostName) {
        List<LogCollectTaskDO> taskList = logCollectTaskManageService.getLogCollectTaskListByAgentHostName(hostName);
        List<HostDO> hostDOList = hostManageService.getRelationHostListByAgent(agentManageService.getAgentByHostName(hostName));
        List<MetricsLogCollectTaskDO> list = new ArrayList<>();
        for (LogCollectTaskDO logCollectTaskDO : taskList) {
            for (FileLogCollectPathDO fileLogCollectPathDO : logCollectTaskDO.getFileLogCollectPathList()) {
                for (HostDO hostDO : hostDOList) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("logCollectTaskId", logCollectTaskDO.getId());
                    params.put("logCollectPathId", fileLogCollectPathDO.getId());
                    params.put("hostName", hostDO.getHostName());
                    MetricsLogCollectTaskPO metricsLogCollectTaskPO = metricsLogCollectTaskDAO.getLatestMetrics(params);
                    if(null != metricsLogCollectTaskPO) {
                        MetricsLogCollectTaskDO metricsLogCollectTaskDO = ConvertUtil.obj2Obj(metricsLogCollectTaskPO, MetricsLogCollectTaskDO.class);
                        metricsLogCollectTaskDO.setTaskStatus(logCollectTaskDO.getLogCollectTaskStatus());
                        list.add(metricsLogCollectTaskDO);
                    }
                }
            }
        }
        return list;
    }

    @Override
    public List<MetricsAgentPO> getErrorMetrics(String hostName, Long startHeartbeatTime, Long endHeartbeatTime) {
        Map<String, Object> params = new HashMap<>();
        params.put("hostName", hostName);
        params.put("startHeartbeatTime", startHeartbeatTime);
        params.put("endHeartbeatTime", endHeartbeatTime);
        return metricsAgentDAO.getErrorMetrics(params);
    }

    @Override
    public MetricsAgentPO getMetricAgent(Long agentMetricId) {
        return metricsAgentDAO.selectByPrimaryKey(agentMetricId);
    }

    @Override
    public Object getAggregationQueryPerLogCollectTskAndPathAndHostNameWithConditionFromMetricsLogCollectTask(
            Long logCollectTaskId,
            Long fileLogCollectPathId,
            String logCollectTaskHostName,
            Long heartbeatTimeStart,
            Long heartbeatTimeEnd,
            String conditionFieldName,
            Integer operatorType,
            Object conditionFieldValue,
            String aggregationFunction,
            String aggregationField
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("logCollectTaskId", logCollectTaskId);
        params.put("pathId", fileLogCollectPathId);
        params.put("hostName", logCollectTaskHostName);
        params.put("startTime", heartbeatTimeStart);
        params.put("endTime", heartbeatTimeEnd);
        params.put("conditionFieldName", conditionFieldName);
        params.put("operatorType", operatorType);
        params.put("conditionFieldValue", conditionFieldValue);
        params.put("function", aggregationFunction);
        params.put("fieldName", aggregationField);
        return metricsLogCollectTaskDAO.getAggregationQueryPerLogCollectTskAndPathAndHostNameWithConditionFromMetricsLogCollectTask(params);
    }

    private List<Pair<Object, Object>> handleGetTopNByLogCollectTaskMetricPerServiceNames(MetricFieldEnum metricFieldEnum, Long startTime, Long endTime, String sortTimeField) {
        /*
         * 1.）获取 top n logcollecttask
         */
        Map<String, Object> params = new HashMap<>();
        params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
        Integer sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
        params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
        params.put("sortTime", getSortTime(sortTimeField, endTime));
        params.put("topN", TOP_N_DEFAULT_VALUE);
        params.put("sortType", SortTypeEnum.DESC.getType());
        params.put("sortTimeField", sortTimeField);
        List<MetricsServiceNamesTopPO> metricsServiceNamesTopPOList = metricsLogCollectTaskDAO.getTopNByMetricPerServiceNames(params);
        List<Pair<Object, Object>> result = new ArrayList<>();
        for (MetricsServiceNamesTopPO metricsServiceNamesTopPO : metricsServiceNamesTopPOList) {
            result.add(new Pair<>(metricsServiceNamesTopPO.getServiceNames(), metricsServiceNamesTopPO.getAggVal()));
        }
        return result;
    }

    private List<Pair<Object, Object>> handleGetTopNByLogCollectTaskMetricPerLogCollectTaskId(MetricFieldEnum metricFieldEnum, Long startTime, Long endTime, String sortTimeField) {
        /*
         * 1.）获取 top n logcollecttask
         */
        Map<String, Object> params = new HashMap<>();
        params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
        Integer sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
        params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
        params.put("sortTime", getSortTime(sortTimeField, endTime));
        params.put("topN", TOP_N_DEFAULT_VALUE);
        params.put("sortType", SortTypeEnum.DESC.getType());
        params.put("sortTimeField", sortTimeField);
        List<MetricsLogCollectTaskIdTopPO> metricsLogCollectTaskIdTopPOList = metricsLogCollectTaskDAO.getTopNByMetricPerLogCollectTaskId(params);
        List<Pair<Object, Object>> result = new ArrayList<>();
        for (MetricsLogCollectTaskIdTopPO metricsLogCollectTaskIdTopPO : metricsLogCollectTaskIdTopPOList) {
            LogCollectTaskDO logCollectTaskDO = logCollectTaskManageService.getById(metricsLogCollectTaskIdTopPO.getLogCollectTaskId());
            if(null != logCollectTaskDO) {
                result.add(new Pair<>(logCollectTaskDO, metricsLogCollectTaskIdTopPO.getAggVal()));
            }
        }
        return result;
    }

    private List<Pair<Object, Object>> handleGetTopNByAgentBusinessMetric(MetricFieldEnum metricFieldEnum, Long startTime, Long endTime, String sortTimeField) {
        /*
         * 1.）获取 top n host name
         */
        Map<String, Object> params = new HashMap<>();
        params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
        Integer sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
        params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
        params.put("sortTime", getSortTime(sortTimeField, endTime));
        params.put("topN", TOP_N_DEFAULT_VALUE);
        params.put("sortType", SortTypeEnum.DESC.getType());
        params.put("sortTimeField", sortTimeField);
        List<MetricsLogCollectTaskTopPO> metricsLogCollectTaskTopPOList = metricsAgentDAO.getTopNByMetricPerHostName(params);
        List<Pair<Object, Object>> result = new ArrayList<>();
        for (MetricsLogCollectTaskTopPO metricsLogCollectTaskTopPO : metricsLogCollectTaskTopPOList) {
            result.add(new Pair<>(metricsLogCollectTaskTopPO.getHostName(), metricsLogCollectTaskTopPO.getAggVal()));
        }
        return result;
    }

    private List<Pair<Object, Object>> handleGetTopNByProcessMetric(MetricFieldEnum metricFieldEnum, Long startTime, Long endTime, String sortTimeField) {
        /*
         * 1.）获取 top n host name
         */
        Map<String, Object> params = new HashMap<>();
        params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
        Integer sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
        params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
        params.put("sortTime", getSortTime(sortTimeField, endTime));
        params.put("topN", TOP_N_DEFAULT_VALUE);
        params.put("sortType", SortTypeEnum.DESC.getType());
        params.put("sortTimeField", sortTimeField);
        List<MetricsLogCollectTaskTopPO> metricsLogCollectTaskTopPOList = metricsProcessDAO.getTopNByMetricPerHostName(params);
        List<Pair<Object, Object>> result = new ArrayList<>();
        for (MetricsLogCollectTaskTopPO metricsLogCollectTaskTopPO : metricsLogCollectTaskTopPOList) {
            result.add(new Pair<>(metricsLogCollectTaskTopPO.getHostName(), metricsLogCollectTaskTopPO.getAggVal()));
        }
        return result;
    }

    private List<Pair<Object, Object>> handleGetTopNBySystemMetric(MetricFieldEnum metricFieldEnum, Long startTime, Long endTime, String sortTimeField) {
        /*
         * 1.）获取 top n host name
         */
        Map<String, Object> params = new HashMap<>();
        params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
        Integer sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
        params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
        params.put("sortTime", getSortTime(sortTimeField, endTime));
        params.put("topN", TOP_N_DEFAULT_VALUE);
        params.put("sortType", SortTypeEnum.DESC.getType());
        params.put("sortTimeField", sortTimeField);
        List<MetricsLogCollectTaskTopPO> metricsLogCollectTaskTopPOList = metricsSystemDAO.getTopNByMetricPerHostName(params);
        List<Pair<Object, Object>> result = new ArrayList<>();
        for (MetricsLogCollectTaskTopPO metricsLogCollectTaskTopPO : metricsLogCollectTaskTopPOList) {
            result.add(new Pair<>(metricsLogCollectTaskTopPO.getHostName(), metricsLogCollectTaskTopPO.getAggVal()));
        }
        return result;
    }

    private Long getSortTime(String sortTimeField, Long time) {
        if(SortTimeFieldEnum.HEARTBEAT_TIME_DAY.getFieldName().equals(sortTimeField)) {
            return DateUtils.getDayUnitTimeStamp(time);
        } else if(SortTimeFieldEnum.HEARTBEAT_TIME_HOUR.getFieldName().equals(sortTimeField)) {
            return DateUtils.getHourUnitTimeStamp(time);
        } else if(SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName().equals(sortTimeField)) {
            return DateUtils.getMinuteUnitTimeStamp(time);
        } else {
            //TODO：
            throw new RuntimeException();
        }
    }

    /**
     * @param metricFieldEnum 指标字段定义枚举对象
     * @return 校验给定指标字段定义枚举对象是否为日志采集任务相关指标 true：是 false：否
     */
    private boolean isLogCollectTaskMetric(MetricFieldEnum metricFieldEnum) {
        return metricFieldEnum.getMetricType().equals(MetricTypeEnum.LOG_COLLECT_TASK_BUSINESS);
    }

    /**
     * 根据给定指标查询条件获取agent相关指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取agent相关指标数据
     */
    private MetricPanel getAgentMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 根据系统级、进程级、业务级，进行分别处理
         */
        if(isSystemMetric(metricFieldEnum)) {
            return getAgentSystemMetric(metricQueryDTO, metricFieldEnum);
        } else if(isProcessMetric(metricFieldEnum)) {
            return getAgentProcessMetric(metricQueryDTO, metricFieldEnum);
        } else if(isAgentBusinessMetric(metricFieldEnum)) {
            return getAgentBusinessMetric(metricQueryDTO, metricFieldEnum);
        } else {
            throw new ServiceException(
                    String.format("class=MetricsServiceImpl||method=getAgentMetric||msg={%s, metricCode=%d非法，必须为系统级、进程级、业务级指标}", ErrorCodeEnum.METRICS_QUERY_ERROR.getMessage(), metricFieldEnum.getCode()),
                    ErrorCodeEnum.METRICS_QUERY_ERROR.getCode()
            );
        }
    }

    /**
     * 根据给定指标查询条件获取agent业务级相关指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取agent业务级相关指标数据
     */
    private MetricPanel getAgentBusinessMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 如下几种情况分别处理：
         *  1.lable
         *  2.多根折线图：not support
         *  3.单根折线图：
         *      1.）附带统计值指标
         *      2.）不附带统计值指标
         */
        if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.LABLE)) {
            Map<String, Object> params = new HashMap<>();
            params.put("fieldName", metricFieldEnum.getFieldName());
            params.put("hostName", metricQueryDTO.getHostName());
            params.put("startTime", metricQueryDTO.getStartTime());
            params.put("endTime", metricQueryDTO.getEndTime());
            Object value = metricsAgentDAO.getLast(params);
            return getMetricsPanel(metricFieldEnum, value, null, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            //TODO：不支持多条线指标
            throw new RuntimeException();
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.SINGLE_LINE_CHAT)) {
            if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                Map<String, Object> params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", metricQueryDTO.getHostName());
                params.put("startTime", metricQueryDTO.getStartTime());
                params.put("endTime", metricQueryDTO.getEndTime());
                List<MetricPoint> result = metricsAgentDAO.getSingleChatStatistic(params);
                MetricPointLine metricPointLine = new MetricPointLine();
                metricPointLine.setMetricPointList(result);
                metricPointLine.setName(metricQueryDTO.getHostName());
                return getMetricsPanel(metricFieldEnum, null, null, metricPointLine);
            } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                Map<String, Object> params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", metricQueryDTO.getHostName());
                params.put("startTime", metricQueryDTO.getStartTime());
                params.put("endTime", metricQueryDTO.getEndTime());
                List<MetricPoint> result = metricsAgentDAO.getSingleChatNonStatistic(params);
                MetricPointLine metricPointLine = new MetricPointLine();
                metricPointLine.setMetricPointList(result);
                metricPointLine.setName(metricQueryDTO.getHostName());
                return getMetricsPanel(metricFieldEnum, null, null, metricPointLine);
            } else {
                //TODO：throw exception 未知MetricValueTypeEnum类型
                throw new RuntimeException();
            }
        } else {
            //TODO：throw exception 未知MetricDisplayTypeEnum类型
            throw new RuntimeException();
        }
    }

    /**
     * 根据给定指标查询条件获取agent进程级相关指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取agent进程级相关指标数据
     */
    private MetricPanel getAgentProcessMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 如下几种情况分别处理：
         *  1.lable
         *  2.多根折线图：not support
         *  3.单根折线图：
         *      1.）附带统计值指标
         *      2.）不附带统计值指标
         */
        if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.LABLE)) {
            Map<String, Object> params = new HashMap<>();
            params.put("fieldName", metricFieldEnum.getFieldName());
            params.put("hostName", metricQueryDTO.getHostName());
            params.put("startTime", metricQueryDTO.getStartTime());
            params.put("endTime", metricQueryDTO.getEndTime());
            Object value = metricsProcessDAO.getLast(params);
            return getMetricsPanel(metricFieldEnum, value, null, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            //TODO：不支持多条线指标
            throw new RuntimeException();
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.SINGLE_LINE_CHAT)) {
            if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                Map<String, Object> params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", metricQueryDTO.getHostName());
                params.put("startTime", metricQueryDTO.getStartTime());
                params.put("endTime", metricQueryDTO.getEndTime());
                List<MetricPoint> result = metricsProcessDAO.getSingleChatStatistic(params);
                MetricPointLine metricPointLine = new MetricPointLine();
                metricPointLine.setMetricPointList(result);
                metricPointLine.setName(metricQueryDTO.getHostName());
                return getMetricsPanel(metricFieldEnum, null, null, metricPointLine);
            } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                Map<String, Object> params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", metricQueryDTO.getHostName());
                params.put("startTime", metricQueryDTO.getStartTime());
                params.put("endTime", metricQueryDTO.getEndTime());
                List<MetricPoint> result = metricsProcessDAO.getSingleChatNonStatistic(params);
                MetricPointLine metricPointLine = new MetricPointLine();
                metricPointLine.setMetricPointList(result);
                metricPointLine.setName(metricQueryDTO.getHostName());
                return getMetricsPanel(metricFieldEnum, null, null, metricPointLine);
            } else {
                //TODO：throw exception 未知MetricValueTypeEnum类型
                throw new RuntimeException();
            }
        } else {
            //TODO：throw exception 未知MetricDisplayTypeEnum类型
            throw new RuntimeException();
        }
    }

    /**
     * 根据给定指标数据（集）、指标定义对象获取MetricPanel对象
     * @param metricFieldEnum 待查询指标枚举定义
     * @param lableValue lable值
     * @param multiLineChatValue 多条线指标值
     * @param singleLineChatValue 单条线指标值
     * @return 返回根据给定指标查询条件、数据访问层对象获取MetricPanel对象
     */
    private MetricPanel getMetricsPanel(
            MetricFieldEnum metricFieldEnum,
            Object lableValue,
            List<MetricPointLine> multiLineChatValue,
            MetricPointLine singleLineChatValue
            ) {
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(metricFieldEnum.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(metricFieldEnum.getDisplayUnit().getCode());
        metricPanel.setName(metricFieldEnum.getMetricName());
        metricPanel.setType(metricFieldEnum.getMetricDisplayType().getCode());
        MetricDisplayTypeEnum metricDisplayTypeEnum = metricFieldEnum.getMetricDisplayType();
        if(metricDisplayTypeEnum.equals(MetricDisplayTypeEnum.LABLE)) {
            metricPanel.setLableValue(lableValue);
        } else if(metricDisplayTypeEnum.equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            metricPanel.setMultiLineChatValue(multiLineChatValue);
        } else {// single line
            metricPanel.setSingleLineChatValue(singleLineChatValue);
        }
        return metricPanel;
    }

    /**
     * 根据给定指标查询条件获取agent系统级相关指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取agent系统级相关指标数据
     */
    private MetricPanel getAgentSystemMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 根据普通系统级、disk/io相关、net card相关，进行分别处理
         */
        if(metricFieldEnum.getMetricType().equals(MetricTypeEnum.SYSTEM_DISK)) {//disk相关
            return getAgentSystemDiskMetric(metricQueryDTO, metricFieldEnum);
        } else if(metricFieldEnum.getMetricType().equals(MetricTypeEnum.SYSTEM_DISK_IO)) {//disk/io相关
            return getAgentSystemDiskIOMetric(metricQueryDTO, metricFieldEnum);
        } else if(metricFieldEnum.getMetricType().equals(MetricTypeEnum.SYSTEM_NET_CARD)) {//net card相关
            return getAgentSystemNetCardMetric(metricQueryDTO, metricFieldEnum);
        } else if(metricFieldEnum.getMetricType().getParentMetricType().equals(MetricTypeEnum.SYSTEM)) {//普通系统级
            return getAgentSystemNormalMetric(metricQueryDTO, metricFieldEnum);
        } else {
            throw new ServiceException(
                    String.format("class=MetricsServiceImpl||method=getAgentSystemMetric||msg={%s, metricCode=%d非法，必须为系统disk/io级、系统net card级、普通系统指标}", ErrorCodeEnum.METRICS_QUERY_ERROR.getMessage(), metricFieldEnum.getCode()),
                    ErrorCodeEnum.METRICS_QUERY_ERROR.getCode()
            );
        }
    }

    /**
     * 根据给定指标查询条件获取agent系统级disk/io相关指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取agent系统级disk/io相关指标数据
     */
    private MetricPanel getAgentSystemDiskIOMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 如下几种情况分别处理：
         *  1.lable
         *  2.多根折线图：
         *      1.）附带统计值指标
         *      2.）不附带统计值指标
         *  3.单根折线图：not support
         *
         */
        if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.LABLE)) {
            //TODO：不支持lable指标
            throw new RuntimeException();
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            /*
             * 1.）获取 top n device
             */
            Map<String, Object> params = new HashMap<>();
            params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
            Integer sortMetricType;
            if(null == metricQueryDTO.getSortMetricType()) {
                sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
            } else {
                sortMetricType = metricQueryDTO.getSortMetricType();
            }
            params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
            params.put("hostName", metricQueryDTO.getHostName());
            if(null == metricQueryDTO.getSortTime() || metricQueryDTO.getSortTime().equals(0L)) {//排序时间点未设置值，将采用时间范围最后时间
                params.put("sortTime", DateUtils.getMinuteUnitTimeStamp(metricQueryDTO.getEndTime()));
            } else {
                params.put("sortTime", metricQueryDTO.getSortTime());
            }
            if(null == metricQueryDTO.getTopN()) {
                params.put("topN", TOP_N_DEFAULT_VALUE);
            } else {
                params.put("topN", metricQueryDTO.getTopN());
            }
            params.put("sortType", metricFieldEnum.getSortTypeEnum().getType());
            List<MetricsDiskIOTopPO> metricsDiskIOTopPOList = metricsDiskIODAO.getTopNDiskDevice(params);
            /*
             * 2.）根据 top n disk，挨个获取单条线
             */
            List<MetricPointLine> multiLineChatValue = new ArrayList<>();
            for (MetricsDiskIOTopPO metricsDiskIOTopPO : metricsDiskIOTopPOList) {
                String device = metricsDiskIOTopPO.getDevice();
                if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                    params = new HashMap<>();
                    params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                    params.put("fieldName", metricFieldEnum.getFieldName());
                    params.put("hostName", metricQueryDTO.getHostName());
                    params.put("device", device);
                    params.put("startTime", metricQueryDTO.getStartTime());
                    params.put("endTime", metricQueryDTO.getEndTime());
                    List<MetricPoint> result = metricsDiskIODAO.getSingleChatStatisticByDevice(params);
                    MetricPointLine metricPointLine = new MetricPointLine();
                    metricPointLine.setMetricPointList(result);
                    metricPointLine.setName(device);
                    multiLineChatValue.add(metricPointLine);
                } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                    params = new HashMap<>();
                    params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                    params.put("fieldName", metricFieldEnum.getFieldName());
                    params.put("hostName", metricQueryDTO.getHostName());
                    params.put("device", device);
                    params.put("startTime", metricQueryDTO.getStartTime());
                    params.put("endTime", metricQueryDTO.getEndTime());
                    List<MetricPoint> result = metricsDiskIODAO.getSingleChatNonStatisticByDevice(params);
                    MetricPointLine metricPointLine = new MetricPointLine();
                    metricPointLine.setMetricPointList(result);
                    metricPointLine.setName(device);
                    multiLineChatValue.add(metricPointLine);
                } else {
                    //TODO：throw exception 未知MetricValueTypeEnum类型
                }
            }
            return getMetricsPanel(metricFieldEnum, null, multiLineChatValue, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.SINGLE_LINE_CHAT)) {
            //TODO：不支持单条线指标
            throw new RuntimeException();
        } else {
            //TODO：throw exception 未知MetricDisplayTypeEnum类型
            throw new RuntimeException();
        }
    }

    /**
     * 根据给定指标查询条件获取agent系统级普通指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取agent系统级普通指标数据
     */
    private MetricPanel getAgentSystemNormalMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 如下几种情况分别处理：
         *  1.lable
         *  2.多根折线图：not support
         *  3.单根折线图：
         *      1.）附带统计值指标
         *      2.）不附带统计值指标
         */
        if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.LABLE)) {
            Map<String, Object> params = new HashMap<>();
            params.put("fieldName", metricFieldEnum.getFieldName());
            params.put("hostName", metricQueryDTO.getHostName());
            params.put("startTime", metricQueryDTO.getStartTime());
            params.put("endTime", metricQueryDTO.getEndTime());
            Object value = metricsSystemDAO.getLast(params);
            return getMetricsPanel(metricFieldEnum, value, null, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            //TODO：不支持多条线指标
            throw new RuntimeException();
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.SINGLE_LINE_CHAT)) {
            if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                Map<String, Object> params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", metricQueryDTO.getHostName());
                params.put("startTime", metricQueryDTO.getStartTime());
                params.put("endTime", metricQueryDTO.getEndTime());
                List<MetricPoint> result = metricsSystemDAO.getSingleChatStatistic(params);
                MetricPointLine metricPointLine = new MetricPointLine();
                metricPointLine.setMetricPointList(result);
                metricPointLine.setName(metricQueryDTO.getHostName());
                return getMetricsPanel(metricFieldEnum, null, null, metricPointLine);
            } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                Map<String, Object> params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", metricQueryDTO.getHostName());
                params.put("startTime", metricQueryDTO.getStartTime());
                params.put("endTime", metricQueryDTO.getEndTime());
                List<MetricPoint> result = metricsSystemDAO.getSingleChatNonStatistic(params);
                MetricPointLine metricPointLine = new MetricPointLine();
                metricPointLine.setMetricPointList(result);
                metricPointLine.setName(metricQueryDTO.getHostName());
                return getMetricsPanel(metricFieldEnum, null, null, metricPointLine);
            } else {
                //TODO：throw exception 未知MetricValueTypeEnum类型
                throw new RuntimeException();
            }
        } else {
            //TODO：throw exception 未知MetricDisplayTypeEnum类型
            throw new RuntimeException();
        }
    }

    /**
     * 根据给定指标查询条件获取agent系统级网卡相关指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取agent系统级网卡相关指标数据
     */
    private MetricPanel getAgentSystemNetCardMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 如下几种情况分别处理：
         *  1.lable
         *  2.多根折线图：
         *      1.）附带统计值指标
         *      2.）不附带统计值指标
         *  3.单根折线图：not support
         *
         */
        if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.LABLE)) {
            //TODO：不支持lable指标
            throw new RuntimeException();
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            /*
             * 1.）获取 top n net card
             */
            Map<String, Object> params = new HashMap<>();
            params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
            Integer sortMetricType;
            if(null == metricQueryDTO.getSortMetricType()) {
                sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
            } else {
                sortMetricType = metricQueryDTO.getSortMetricType();
            }
            params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
            params.put("hostName", metricQueryDTO.getHostName());
            if(null == metricQueryDTO.getSortTime() || metricQueryDTO.getSortTime().equals(0L)) {//排序时间点未设置值，将采用时间范围最后时间
                params.put("sortTime", DateUtils.getMinuteUnitTimeStamp(metricQueryDTO.getEndTime()));
            } else {
                params.put("sortTime", metricQueryDTO.getSortTime());
            }
            if(null == metricQueryDTO.getTopN()) {
                params.put("topN", TOP_N_DEFAULT_VALUE);
            } else {
                params.put("topN", metricQueryDTO.getTopN());
            }
            params.put("sortType", metricFieldEnum.getSortTypeEnum().getType());
            List<MetricsNetCardTopPO> MetricsNetCardTopPOList = metricsNetCardDAO.getTopNMacAddress(params);
            /*
             * 2.）根据 top n net card，挨个获取单条线
             */
            List<MetricPointLine> multiLineChatValue = new ArrayList<>();
            for (MetricsNetCardTopPO metricsNetCardTopPO : MetricsNetCardTopPOList) {
                String macAddress = metricsNetCardTopPO.getMacAddress();
                if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                    params = new HashMap<>();
                    params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                    params.put("fieldName", metricFieldEnum.getFieldName());
                    params.put("hostName", metricQueryDTO.getHostName());
                    params.put("macAddress", macAddress);
                    params.put("startTime", metricQueryDTO.getStartTime());
                    params.put("endTime", metricQueryDTO.getEndTime());
                    List<MetricPoint> result = metricsNetCardDAO.getSingleChatStatisticByMacAddress(params);
                    MetricPointLine metricPointLine = new MetricPointLine();
                    metricPointLine.setMetricPointList(result);
                    metricPointLine.setName(macAddress);
                    multiLineChatValue.add(metricPointLine);
                } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                    params = new HashMap<>();
                    params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                    params.put("fieldName", metricFieldEnum.getFieldName());
                    params.put("hostName", metricQueryDTO.getHostName());
                    params.put("macAddress", macAddress);
                    params.put("startTime", metricQueryDTO.getStartTime());
                    params.put("endTime", metricQueryDTO.getEndTime());
                    List<MetricPoint> result = metricsNetCardDAO.getSingleChatNonStatisticByMacAddress(params);
                    MetricPointLine metricPointLine = new MetricPointLine();
                    metricPointLine.setMetricPointList(result);
                    metricPointLine.setName(macAddress);
                    multiLineChatValue.add(metricPointLine);
                } else {
                    //TODO：throw exception 未知MetricValueTypeEnum类型
                }
            }
            return getMetricsPanel(metricFieldEnum, null, multiLineChatValue, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.SINGLE_LINE_CHAT)) {
            //TODO：不支持单条线指标
            throw new RuntimeException();
        } else {
            //TODO：throw exception 未知MetricDisplayTypeEnum类型
            throw new RuntimeException();
        }
    }

    /**
     * 根据给定指标查询条件获取agent系统级disk相关指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取agent系统级disk相关指标数据
     */
    private MetricPanel getAgentSystemDiskMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 如下几种情况分别处理：
         *  1.lable
         *  2.多根折线图：
         *      1.）附带统计值指标
         *      2.）不附带统计值指标
         *  3.单根折线图：not support
         *
         */
        if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.LABLE)) {
            //TODO：不支持lable指标
            throw new RuntimeException();
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            /*
             * 1.）获取 top n disk
             */
            Map<String, Object> params = new HashMap<>();
            params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
            Integer sortMetricType;
            if(null == metricQueryDTO.getSortMetricType()) {
                sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
            } else {
                sortMetricType = metricQueryDTO.getSortMetricType();
            }
            params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
            params.put("hostName", metricQueryDTO.getHostName());
            if(null == metricQueryDTO.getSortTime() || metricQueryDTO.getSortTime().equals(0L)) {//排序时间点未设置值，将采用时间范围最后时间
                params.put("sortTime", DateUtils.getMinuteUnitTimeStamp(metricQueryDTO.getEndTime()));
            } else {
                params.put("sortTime", metricQueryDTO.getSortTime());
            }
            if(null == metricQueryDTO.getTopN()) {
                params.put("topN", TOP_N_DEFAULT_VALUE);
            } else {
                params.put("topN", metricQueryDTO.getTopN());
            }
            params.put("sortType", metricFieldEnum.getSortTypeEnum().getType());
            List<MetricsDiskTopPO> metricsDiskTopPOList = metricsDiskDAO.getTopNDiskPath(params);
            /*
             * 2.）根据 top n disk，挨个获取单条线
             */
            List<MetricPointLine> multiLineChatValue = new ArrayList<>();
            for (MetricsDiskTopPO metricsDiskTopPO : metricsDiskTopPOList) {
                String path = metricsDiskTopPO.getPath();
                if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                    params = new HashMap<>();
                    params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                    params.put("fieldName", metricFieldEnum.getFieldName());
                    params.put("hostName", metricQueryDTO.getHostName());
                    params.put("path", path);
                    params.put("startTime", metricQueryDTO.getStartTime());
                    params.put("endTime", metricQueryDTO.getEndTime());
                    List<MetricPoint> result = metricsDiskDAO.getSingleChatStatisticByPath(params);
                    MetricPointLine metricPointLine = new MetricPointLine();
                    metricPointLine.setMetricPointList(result);
                    metricPointLine.setName(path);
                    multiLineChatValue.add(metricPointLine);
                } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                    params = new HashMap<>();
                    params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                    params.put("fieldName", metricFieldEnum.getFieldName());
                    params.put("hostName", metricQueryDTO.getHostName());
                    params.put("path", path);
                    params.put("startTime", metricQueryDTO.getStartTime());
                    params.put("endTime", metricQueryDTO.getEndTime());
                    List<MetricPoint> result = metricsDiskDAO.getSingleChatNonStatisticByPath(params);
                    MetricPointLine metricPointLine = new MetricPointLine();
                    metricPointLine.setMetricPointList(result);
                    metricPointLine.setName(path);
                    multiLineChatValue.add(metricPointLine);
                } else {
                    //TODO：throw exception 未知MetricValueTypeEnum类型
                }
            }
            return getMetricsPanel(metricFieldEnum, null, multiLineChatValue, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.SINGLE_LINE_CHAT)) {
            //TODO：不支持单条线指标
            throw new RuntimeException();
        } else {
            //TODO：throw exception 未知MetricDisplayTypeEnum类型
            throw new RuntimeException();
        }
    }

    /**
     * 根据给定原始字段名与字段排序类型获取排序字段名
      * @param fieldName 原始字段名
     * @param sortMetricType 字段排序类型
     * @return 返回根据给定原始字段名与字段排序类型获取排序字段名
     */
    private String getSortFieldName(String fieldName, Integer sortMetricType) {
        if(sortMetricType == 0) {
          return fieldName;
        } else if(sortMetricType == 1) {
            return fieldName+"Min";
        } else if(sortMetricType == 2) {
            return fieldName+"Max";
        } else if(sortMetricType == 3) {
            return fieldName+"Mean";
        } else if(sortMetricType == 4) {
            return fieldName+"Std";
        } else if(sortMetricType == 5) {
            return fieldName+"55Quantile";
        } else if(sortMetricType == 6) {
            return fieldName+"75Quantile";
        } else if(sortMetricType == 7) {
            return fieldName+"95Quantile";
        } else if(sortMetricType == 8) {
            return fieldName+"99Quantile";
        } else {
            //TODO：throw exception 未知sortMetricType类型
            throw new RuntimeException();
        }
    }

    /**
     * 校验给定指标定义是否为agent业务级指标
     * @param metricFieldEnum 指标定义枚举对象
     * @return 校验给定指标定义是否为agent业务级指标 true：是 false：否
     */
    private boolean isAgentBusinessMetric(MetricFieldEnum metricFieldEnum) {
        MetricTypeEnum metricTypeEnum = metricFieldEnum.getMetricType();
        return metricTypeEnum.equals(MetricTypeEnum.AGENT_BUSINESS);
    }

    /**
     * 校验给定指标定义是否为进程级指标
     * @param metricFieldEnum 指标定义枚举对象
     * @return 校验给定指标定义是否为进程级指标 true：是 false：否
     */
    private boolean isProcessMetric(MetricFieldEnum metricFieldEnum) {
        MetricTypeEnum metricTypeEnum = metricFieldEnum.getMetricType().getParentMetricType();
        return metricTypeEnum.equals(MetricTypeEnum.PROCESS);
    }

    /**
     * 校验给定指标定义是否为系统级指标
     * @param metricFieldEnum 指标定义枚举对象
     * @return 校验给定指标定义是否为系统级指标 true：是 false：否
     */
    private boolean isSystemMetric(MetricFieldEnum metricFieldEnum) {
        MetricTypeEnum metricTypeEnum = metricFieldEnum.getMetricType().getParentMetricType();
        return metricTypeEnum.equals(MetricTypeEnum.SYSTEM);
    }

    /**
     * 根据给定指标查询条件获取日志采集任务相关指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取日志采集任务相关指标数据
     */
    private MetricPanel getLogCollectTaskMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 如下几种情况分别处理：
         *  1.lable
         *  2.多根折线图：
         *      1.）附带统计值指标
         *      2.）不附带统计值指标
         *  3.单根折线图：
         *      1.）附带统计值指标
         *      2.）不附带统计值指标
         *
         */
        if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.LABLE)) {
            Map<String, Object> params = new HashMap<>();
            params.put("fieldName", metricFieldEnum.getFieldName());
            params.put("hostName", metricQueryDTO.getHostName());
            params.put("logCollectTaskId", metricQueryDTO.getLogCollectTaskId());
            params.put("pathId", metricQueryDTO.getPathId());
            params.put("startTime", metricQueryDTO.getStartTime());
            params.put("endTime", metricQueryDTO.getEndTime());
            Object value = metricsLogCollectTaskDAO.getLast(params);
            return getMetricsPanel(metricFieldEnum, value, null, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            /*
             * 1.）获取 top n host
             */
            Map<String, Object> params = new HashMap<>();
            params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
            Integer sortMetricType;
            if(null == metricQueryDTO.getSortMetricType()) {
                sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
            } else {
                sortMetricType = metricQueryDTO.getSortMetricType();
            }
            params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
            params.put("logCollectTaskId", metricQueryDTO.getLogCollectTaskId());
            params.put("pathId", metricQueryDTO.getPathId());
            params.put("hostName", metricQueryDTO.getHostName());
            if(null == metricQueryDTO.getSortTime() || metricQueryDTO.getSortTime().equals(0L)) {//排序时间点未设置值，将采用时间范围最后时间
                params.put("sortTime", DateUtils.getMinuteUnitTimeStamp(metricQueryDTO.getEndTime()));
            } else {
                params.put("sortTime", metricQueryDTO.getSortTime());
            }
            if(null == metricQueryDTO.getTopN()) {
                params.put("topN", TOP_N_DEFAULT_VALUE);
            } else {
                params.put("topN", metricQueryDTO.getTopN());
            }
            params.put("sortType", metricFieldEnum.getSortTypeEnum().getType());
            List<MetricsLogCollectTaskTopPO> metricsLogCollectTaskTopPOList = metricsLogCollectTaskDAO.getTopNByHostName(params);
            /*
             * 2.）根据 top n host name，挨个获取单条线
             */
            List<MetricPointLine> multiLineChatValue = new ArrayList<>();
            for (MetricsLogCollectTaskTopPO metricsLogCollectTaskTopPO : metricsLogCollectTaskTopPOList) {
                String hostName = metricsLogCollectTaskTopPO.getHostName();
                if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                    params = new HashMap<>();
                    params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                    params.put("fieldName", metricFieldEnum.getFieldName());
                    params.put("logCollectTaskId", metricQueryDTO.getLogCollectTaskId());
                    params.put("pathId", metricQueryDTO.getPathId());
                    params.put("hostName", hostName);
                    params.put("startTime", metricQueryDTO.getStartTime());
                    params.put("endTime", metricQueryDTO.getEndTime());
                    List<MetricPoint> result = metricsLogCollectTaskDAO.getSingleChatStatistic(params);
                    MetricPointLine metricPointLine = new MetricPointLine();
                    metricPointLine.setMetricPointList(result);
                    metricPointLine.setName(hostName);
                    multiLineChatValue.add(metricPointLine);
                } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                    params = new HashMap<>();
                    params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                    params.put("fieldName", metricFieldEnum.getFieldName());
                    params.put("logCollectTaskId", metricQueryDTO.getLogCollectTaskId());
                    params.put("pathId", metricQueryDTO.getPathId());
                    params.put("hostName", hostName);
                    params.put("startTime", metricQueryDTO.getStartTime());
                    params.put("endTime", metricQueryDTO.getEndTime());
                    List<MetricPoint> result = metricsLogCollectTaskDAO.getSingleChatNonStatistic(params);
                    MetricPointLine metricPointLine = new MetricPointLine();
                    metricPointLine.setMetricPointList(result);
                    metricPointLine.setName(hostName);
                    multiLineChatValue.add(metricPointLine);
                } else {
                    //TODO：throw exception 未知MetricValueTypeEnum类型
                }
            }
            return getMetricsPanel(metricFieldEnum, null, multiLineChatValue, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.SINGLE_LINE_CHAT)) {
            if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                Map<String, Object> params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("logCollectTaskId", metricQueryDTO.getLogCollectTaskId());
                params.put("pathId", metricQueryDTO.getPathId());
                params.put("hostName", metricQueryDTO.getHostName());
                params.put("startTime", metricQueryDTO.getStartTime());
                params.put("endTime", metricQueryDTO.getEndTime());
                List<MetricPoint> result = metricsLogCollectTaskDAO.getSingleChatStatistic(params);
                MetricPointLine metricPointLine = new MetricPointLine();
                metricPointLine.setMetricPointList(result);
                String metricPointLineName = String.format(
                        "%s:%s:%s",
                        logCollectTaskManageService.getById(metricQueryDTO.getLogCollectTaskId()).getLogCollectTaskName(),
                        fileLogCollectPathManageService.getById(metricQueryDTO.getPathId()).getPath(),
                        metricQueryDTO.getHostName()
                );
                metricPointLine.setName(metricPointLineName);
                return getMetricsPanel(metricFieldEnum, null, null, metricPointLine);
            } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                Map<String, Object> params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("logCollectTaskId", metricQueryDTO.getLogCollectTaskId());
                params.put("pathId", metricQueryDTO.getPathId());
                params.put("hostName", metricQueryDTO.getHostName());
                params.put("startTime", metricQueryDTO.getStartTime());
                params.put("endTime", metricQueryDTO.getEndTime());
                List<MetricPoint> result = metricsLogCollectTaskDAO.getSingleChatNonStatistic(params);
                MetricPointLine metricPointLine = new MetricPointLine();
                metricPointLine.setMetricPointList(result);
                String metricPointLineName = String.format(
                        "%s:%s:%s",
                        logCollectTaskManageService.getById(metricQueryDTO.getLogCollectTaskId()).getLogCollectTaskName(),
                        fileLogCollectPathManageService.getById(metricQueryDTO.getPathId()).getPath(),
                        metricQueryDTO.getHostName()
                );
                metricPointLine.setName(metricPointLineName);
                return getMetricsPanel(metricFieldEnum, null, null, metricPointLine);
            } else {
                //TODO：throw exception 未知MetricValueTypeEnum类型
                throw new RuntimeException();
            }
        } else {
            //TODO：throw exception 未知MetricDisplayTypeEnum类型
            throw new RuntimeException();
        }
    }

    /**
     * 根据给定指标类型构建对应指标树
     * @param metricTypeEnum 指标类型
     * @return 返回根据给定指标类型构建对应指标树
     */
    private MetricNodeVO handleGetMetricsTreeByMetricType(MetricTypeEnum metricTypeEnum) {
        MetricNodeVO metricNodeVO = buildFromMetricTypeEnum(metricTypeEnum);
        LinkedList<Pair<MetricTypeEnum, MetricNodeVO>> queue = new LinkedList<>();
        queue.addLast(new Pair<>(metricTypeEnum, metricNodeVO));
        while(!queue.isEmpty()) {
            Pair<MetricTypeEnum, MetricNodeVO> firstNode = queue.removeFirst();
            List<MetricTypeEnum> subNodes = MetricTypeEnum.fromParentMetricTypeCode(firstNode.getKey().getCode());
            if(CollectionUtils.isNotEmpty(subNodes)) {
                List<MetricNodeVO> children = new ArrayList<>();
                for (MetricTypeEnum item : subNodes) {
                    MetricNodeVO child = buildFromMetricTypeEnum(item);
                    queue.add(new Pair<>(item, child));
                    children.add(child);
                }
                firstNode.getValue().setChildren(children);
            } else {
                List<MetricFieldEnum> metricFieldEnumList = MetricFieldEnum.fromMetricTypeEnum(firstNode.getKey());
                if(CollectionUtils.isNotEmpty(metricFieldEnumList)) {
                    List<MetricNodeVO> children = new ArrayList<>();
                    for (MetricFieldEnum metricFieldEnum : metricFieldEnumList) {
                        MetricNodeVO metric = buildFromMetricFieldEnum(metricFieldEnum);
                        children.add(metric);
                    }
                    firstNode.getValue().setChildren(children);
                }
            }
        }
        return metricNodeVO;
    }

    /**
     * @param metricFieldEnum 指标字段定义
     * @return 将给定指标字段定义转化为MetricNodeVO对象
     */
    private MetricNodeVO buildFromMetricFieldEnum(MetricFieldEnum metricFieldEnum) {
        MetricNodeVO metricNodeVO = new MetricNodeVO();
        metricNodeVO.setMetricName(metricFieldEnum.getMetricName());
        metricNodeVO.setMetricDesc(metricFieldEnum.getDescription());
        metricNodeVO.setCode(metricFieldEnum.getCode());
        metricNodeVO.setMetricLevel(metricFieldEnum.getMetricLevelEnum().getCode());
        metricNodeVO.setIsLeafNode(true);
        return metricNodeVO;
    }

    /**
     * @param metricTypeEnum 指标类型定义
     * @return 将给定指标类型定义转化为MetricNodeVO对象
     */
    private MetricNodeVO buildFromMetricTypeEnum(MetricTypeEnum metricTypeEnum) {
        MetricNodeVO metricNodeVO = new MetricNodeVO();
        metricNodeVO.setMetricName(metricTypeEnum.getType());
        metricNodeVO.setMetricDesc(metricTypeEnum.getDescription());
        metricNodeVO.setCode(metricTypeEnum.getCode());
        metricNodeVO.setIsLeafNode(false);
        return metricNodeVO;
    }

}
