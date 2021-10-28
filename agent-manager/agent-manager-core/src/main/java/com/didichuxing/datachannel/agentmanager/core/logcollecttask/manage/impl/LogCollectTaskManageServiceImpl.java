package com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.didichuxing.datachannel.agentmanager.common.GlobalProperties;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.common.ListCompareResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.k8s.K8sPodDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.DirectoryLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskPaginationRecordDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.MetricQueryDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskServicePO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.AgentMetricField;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.CalcFunction;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricAggregate;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricList;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanelGroup;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPointList;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricsDashBoard;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.chain.ProcessorChain;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.constant.LogCollectTaskConstant;
import com.didichuxing.datachannel.agentmanager.common.constant.LogCollectTaskHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.constant.MetricConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.host.HostTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskStatusEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.ModuleEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.OperationEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.Comparator;
import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.agentmanager.common.util.ListCompareUtil;
import com.didichuxing.datachannel.agentmanager.common.util.MetricUtils;
import com.didichuxing.datachannel.agentmanager.core.agent.configuration.AgentCollectConfigManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.common.OperateRecordService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.k8s.K8sPodManageService;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.DirectoryLogCollectPathManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.FileLogCollectPathManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceLogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.LogCollectTaskMapper;
import com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.manage.extension.LogCollectTaskManageServiceExtension;
import com.didichuxing.datachannel.agentmanager.thirdpart.metadata.k8s.util.K8sUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务管理服务实现类
 */
@Service
public class LogCollectTaskManageServiceImpl implements LogCollectTaskManageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCollectTaskManageServiceImpl.class);
    private static final int HEARTBEAT_PERIOD = 30;

    @Autowired
    private LogCollectTaskMapper logCollectorTaskDAO;

    @Autowired
    private LogCollectTaskManageServiceExtension logCollectTaskManageServiceExtension;

    @Autowired
    private LogCollectTaskHealthManageService logCollectTaskHealthManageService;

    @Autowired
    private DirectoryLogCollectPathManageService directoryLogCollectPathManageService;

    @Autowired
    private FileLogCollectPathManageService fileLogCollectPathManageService;

    @Autowired
    private ServiceManageService serviceManageService;

    @Autowired
    private ServiceLogCollectTaskManageService serviceLogCollectTaskManageService;

    @Autowired
    private KafkaClusterManageService kafkaClusterManageService;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private OperateRecordService operateRecordService;

    @Autowired
    private AgentCollectConfigManageService agentCollectConfigManageService;

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private AgentMetricsManageService agentMetricsManageService;

    @Autowired
    private K8sPodManageService k8sPodManageService;

    @Override
    @Transactional
    public Long createLogCollectTask(LogCollectTaskDO logCollectTask, String operator) {
        return this.handleCreateLogCollectorTask(logCollectTask, operator);
    }

    @Override
    @Transactional
    public void switchLogCollectTask(Long logCollectTaskId, Integer status, String operator) {
        this.handleSwitchLogCollectTask(logCollectTaskId, status, operator);
    }

    @Override
    public List<LogCollectTaskPaginationRecordDO> paginationQueryByConditon(LogCollectTaskPaginationQueryConditionDO logCollectTaskPaginationQueryConditionDO) {
        String column = logCollectTaskPaginationQueryConditionDO.getSortColumn();
        if (column != null) {
            for (char c : column.toCharArray()) {
                if (!Character.isLetter(c) && c != '_') {
                    return Collections.emptyList();
                }
            }
        }
        List<LogCollectTaskPaginationRecordDO> logCollectTaskPaginationRecordDOList = logCollectorTaskDAO.paginationQueryByConditon(logCollectTaskPaginationQueryConditionDO);
        if (CollectionUtils.isEmpty(logCollectTaskPaginationRecordDOList)) {
            return new ArrayList<>();
        } else {
            for (LogCollectTaskPaginationRecordDO logCollectTaskPaginationRecordDO : logCollectTaskPaginationRecordDOList) {
                logCollectTaskPaginationRecordDO.setRelationServiceList(serviceManageService.getServicesByLogCollectTaskId(logCollectTaskPaginationRecordDO.getLogCollectTaskId()));
                logCollectTaskPaginationRecordDO.setRelationReceiverDO(kafkaClusterManageService.getById(logCollectTaskPaginationRecordDO.getKafkaClusterId()));
            }
        }
        return logCollectTaskPaginationRecordDOList;
    }

    @Override
    public Integer queryCountByCondition(LogCollectTaskPaginationQueryConditionDO logCollectTaskPaginationQueryConditionDO) {
        return logCollectorTaskDAO.queryCountByCondition(logCollectTaskPaginationQueryConditionDO);
    }

    @Override
    public List<MetricPanelGroup> listLogCollectTaskMetrics(Long logCollectTaskId, Long startTime, Long endTime) {
        return handlerListLogCollectTaskMetrics(logCollectTaskId, startTime, endTime).getMetricPanelGroupList();
    }

    @Override
    public List<MetricPanelGroup> listLogCollectTaskMetricsPerHostAndPath(Long logCollectTaskId, Long logPathId, String hostName, Long startTime, Long endTime) {
        return handleListLogCollectTaskMetricsPerHostAndPath(logCollectTaskId, logPathId, hostName, startTime, endTime);
    }

    @Override
    public List<MetricAggregate> getAliveHostCount(MetricQueryDO metricQueryDO) {
        List<HostDO> hostDOList = hostManageService.getHostListByLogCollectTaskId(metricQueryDO.getTaskId());
        MetricAggregate resultExist = new MetricAggregate();
        MetricAggregate resultNotExist = new MetricAggregate();
        resultExist.setName(LogCollectTaskConstant.HEARTBEAT_EXIST);
        resultNotExist.setName(LogCollectTaskConstant.HEARTBEAT_NOT_EXIST);
        int exist = 0;
        int notExist = 0;
        Long endTime = System.currentTimeMillis();
        for (HostDO hostDO : hostDOList) {
            if (checkAliveByHeartbeat(metricQueryDO.getTaskId(), metricQueryDO.getLogCollectPathId(), hostDO.getHostName(), endTime)) {
                exist++;
            } else {
                notExist++;
            }
        }

        resultExist.setValue(exist);
        resultNotExist.setValue(notExist);
        return Arrays.asList(resultExist, resultNotExist);
    }

    @Override
    public MetricList getCollectDelayMetric(MetricQueryDO metricQueryDO) {
        MetricList metricList = new MetricList();
        List<MetricPointList> total = new ArrayList<>();
        if (metricQueryDO.getEachHost()) {
            List<HostDO> hostDOList = hostManageService.getHostListByLogCollectTaskId(metricQueryDO.getTaskId());
            for (HostDO hostDO : hostDOList) {
                metricQueryDO.setHostName(hostDO.getHostName());
                MetricPointList metricPointList = new MetricPointList();
                List<MetricPoint> list = agentMetricsManageService.queryCollectDelay(metricQueryDO);
                metricPointList.setMetricPointList(list);
                metricPointList.setName(hostDO.getHostName());
                total.add(metricPointList);
            }
        } else {
            MetricPointList metricPointList = new MetricPointList();
            List<MetricPoint> list = agentMetricsManageService.queryCollectDelay(metricQueryDO);
            metricPointList.setName(metricQueryDO.getHostName());
            metricPointList.setMetricPointList(list);
            total.add(metricPointList);
        }
        metricList.setMetricList(total);
        return metricList;
    }

    @Override
    public MetricList getMinLogTime(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.LOG_TIME.name(), CalcFunction.MIN.name(), MetricConstant.QUERY_INTERVAL);
    }

    @Override
    public MetricList getLimitTime(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.LIMIT_TIME.name(), CalcFunction.SUM.name(), MetricConstant.HEARTBEAT_PERIOD);
    }

    @Override
    public MetricList getAbnormalTruncation(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.FILTER_TOO_LARGE_COUNT.name(), CalcFunction.SUM.name(), MetricConstant.HEARTBEAT_PERIOD);
    }

    @Override
    public MetricList getCollectPathExists(MetricQueryDO metricQueryDO) {
        MetricList metricList = new MetricList();
        List<MetricPointList> total = new ArrayList<>();
        if (metricQueryDO.getEachHost()) {
            List<HostDO> hostDOList = hostManageService.getHostListByLogCollectTaskId(metricQueryDO.getTaskId());
            for (HostDO hostDO : hostDOList) {
                metricQueryDO.setHostName(hostDO.getHostName());
                MetricPointList metricPointList = new MetricPointList();
                List<MetricPoint> list = agentMetricsManageService.queryByLogModel(metricQueryDO, AgentMetricField.IS_FILE_EXIST.name());
                metricPointList.setMetricPointList(list);
                metricPointList.setName(hostDO.getHostName());
                total.add(metricPointList);
            }
        } else {
            MetricPointList metricPointList = new MetricPointList();
            List<MetricPoint> list = agentMetricsManageService.queryByLogModel(metricQueryDO, AgentMetricField.IS_FILE_EXIST.name());
            metricPointList.setName(metricQueryDO.getHostName());
            metricPointList.setMetricPointList(list);
            total.add(metricPointList);
        }
        metricList.setMetricList(total);
        return metricList;
    }

    @Override
    public MetricList getIsFileOrder(MetricQueryDO metricQueryDO) {
        MetricList metricList = new MetricList();
        List<MetricPointList> total = new ArrayList<>();
        if (metricQueryDO.getEachHost()) {
            List<HostDO> hostDOList = hostManageService.getHostListByLogCollectTaskId(metricQueryDO.getTaskId());
            for (HostDO hostDO : hostDOList) {
                metricQueryDO.setHostName(hostDO.getHostName());
                MetricPointList metricPointList = new MetricPointList();
                List<MetricPoint> list = agentMetricsManageService.getFileLogPathDisorderPerMinMetric(metricQueryDO.getTaskId(), metricQueryDO.getLogCollectPathId(), metricQueryDO.getHostName(), metricQueryDO.getStartTime(), metricQueryDO.getEndTime());
                metricPointList.setMetricPointList(list);
                metricPointList.setName(hostDO.getHostName());
                total.add(metricPointList);
            }
        } else {
            MetricPointList metricPointList = new MetricPointList();
            List<MetricPoint> list = agentMetricsManageService.getFileLogPathDisorderPerMinMetric(metricQueryDO.getTaskId(), metricQueryDO.getLogCollectPathId(), metricQueryDO.getHostName(), metricQueryDO.getStartTime(), metricQueryDO.getEndTime());
            metricPointList.setName(metricQueryDO.getHostName());
            metricPointList.setMetricPointList(list);
            total.add(metricPointList);
        }
        metricList.setMetricList(total);
        return metricList;
    }

    @Override
    public MetricList getSliceError(MetricQueryDO metricQueryDO) {
        MetricList metricList = new MetricList();
        List<MetricPointList> total = new ArrayList<>();
        if (metricQueryDO.getEachHost()) {
            List<HostDO> hostDOList = hostManageService.getHostListByLogCollectTaskId(metricQueryDO.getTaskId());
            for (HostDO hostDO : hostDOList) {
                metricQueryDO.setHostName(hostDO.getHostName());
                MetricPointList metricPointList = new MetricPointList();
                List<MetricPoint> list = agentMetricsManageService.getFileLogPathLogSliceErrorPerMinMetric(metricQueryDO.getTaskId(), metricQueryDO.getLogCollectPathId(), metricQueryDO.getHostName(), metricQueryDO.getStartTime(), metricQueryDO.getEndTime());
                metricPointList.setMetricPointList(list);
                metricPointList.setName(hostDO.getHostName());
                total.add(metricPointList);
            }
        } else {
            MetricPointList metricPointList = new MetricPointList();
            List<MetricPoint> list = agentMetricsManageService.getFileLogPathLogSliceErrorPerMinMetric(metricQueryDO.getTaskId(), metricQueryDO.getLogCollectPathId(), metricQueryDO.getHostName(), metricQueryDO.getStartTime(), metricQueryDO.getEndTime());
            metricPointList.setName(metricQueryDO.getHostName());
            metricPointList.setMetricPointList(list);
            total.add(metricPointList);
        }
        metricList.setMetricList(total);
        return metricList;
    }

    @Override
    public MetricList getReadByte(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.READ_BYTE.name(), CalcFunction.SUM.name(), MetricConstant.HEARTBEAT_PERIOD);
    }

    @Override
    public MetricList getReadCount(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.READ_COUNT.name(), CalcFunction.SUM.name(), MetricConstant.HEARTBEAT_PERIOD);
    }

    @Override
    public MetricList getTotalReadTime(MetricQueryDO metricQueryDO) {
        MetricList metricList = new MetricList();
        List<MetricPointList> total = new ArrayList<>();
        if (metricQueryDO.getEachHost()) {
            List<HostDO> hostDOList = hostManageService.getHostListByLogCollectTaskId(metricQueryDO.getTaskId());
            for (HostDO hostDO : hostDOList) {
                metricQueryDO.setHostName(hostDO.getHostName());
                MetricPointList metricPointList = new MetricPointList();
                List<MetricPoint> list = agentMetricsManageService.queryAggregationByLogModel(metricQueryDO, AgentMetricField.READ_TIME_MEAN.name(), CalcFunction.AVG.name(), MetricConstant.QUERY_INTERVAL);
                metricPointList.setMetricPointList(list);
                metricPointList.setName(hostDO.getHostName());
                total.add(metricPointList);
            }
        } else {
            MetricPointList metricPointList = new MetricPointList();
            List<MetricPoint> list = agentMetricsManageService.queryAggregationByLogModel(metricQueryDO, AgentMetricField.READ_TIME_MEAN.name(), CalcFunction.AVG.name(), MetricConstant.QUERY_INTERVAL);
            metricPointList.setName(metricQueryDO.getHostName());
            metricPointList.setMetricPointList(list);
            total.add(metricPointList);
        }
        metricList.setMetricList(total);
        return metricList;
    }

    @Override
    public MetricList getReadTimeMean(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.READ_TIME_MEAN.name(), CalcFunction.MAX.name(), MetricConstant.QUERY_INTERVAL);
    }

    @Override
    public MetricList getReadTimeMax(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.READ_TIME_MAX.name(), CalcFunction.MAX.name(), MetricConstant.QUERY_INTERVAL);
    }

    @Override
    public MetricList getReadTimeMin(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.READ_TIME_MIN.name(), CalcFunction.MIN.name(), MetricConstant.QUERY_INTERVAL);
    }

    @Override
    public MetricList getSendBytes(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.SEND_BYTE.name(), CalcFunction.SUM.name(), MetricConstant.HEARTBEAT_PERIOD);
    }

    @Override
    public MetricList getSendCount(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.SEND_COUNT.name(), CalcFunction.SUM.name(), MetricConstant.HEARTBEAT_PERIOD);
    }

    @Override
    public MetricList getTotalSendTime(MetricQueryDO metricQueryDO) {
        MetricList metricList = new MetricList();
        List<MetricPointList> total = new ArrayList<>();
        if (metricQueryDO.getEachHost()) {
            List<HostDO> hostDOList = hostManageService.getHostListByLogCollectTaskId(metricQueryDO.getTaskId());
            for (HostDO hostDO : hostDOList) {
                metricQueryDO.setHostName(hostDO.getHostName());
                MetricPointList metricPointList = new MetricPointList();
                List<MetricPoint> list = agentMetricsManageService.queryAggregationByLogModel(metricQueryDO, AgentMetricField.SEND_TIME_MEAN.name(), CalcFunction.AVG.name(), MetricConstant.QUERY_INTERVAL);
                metricPointList.setMetricPointList(list);
                metricPointList.setName(hostDO.getHostName());
                total.add(metricPointList);
            }
        } else {
            MetricPointList metricPointList = new MetricPointList();
            List<MetricPoint> list = agentMetricsManageService.queryAggregationByLogModel(metricQueryDO, AgentMetricField.SEND_TIME_MEAN.name(), CalcFunction.AVG.name(), MetricConstant.QUERY_INTERVAL);
            metricPointList.setMetricPointList(list);
            metricPointList.setName(metricQueryDO.getHostName());
            total.add(metricPointList);
        }
        metricList.setMetricList(total);
        return metricList;
    }

    @Override
    public MetricList getSendTimeMax(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.SEND_TIME_MAX.name(), CalcFunction.MAX.name(), MetricConstant.HEARTBEAT_PERIOD);
    }

    @Override
    public MetricList getSendTimeMean(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.SEND_TIME_MEAN.name(), CalcFunction.MAX.name(), MetricConstant.HEARTBEAT_PERIOD);
    }

    @Override
    public MetricList getSendTimeMin(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.SEND_TIME_MIN.name(), CalcFunction.MIN.name(), MetricConstant.HEARTBEAT_PERIOD);
    }

    @Override
    public MetricList getFlushCount(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.FLUSH_COUNT.name(), CalcFunction.SUM.name(), MetricConstant.HEARTBEAT_PERIOD);
    }

    @Override
    public MetricList getFlushTimeMax(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.FLUSH_TIME_MAX.name(), CalcFunction.MAX.name(), MetricConstant.QUERY_INTERVAL);
    }

    @Override
    public MetricList getFlushTimeMean(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.FLUSH_TIME_MEAN.name(), CalcFunction.MAX.name(), MetricConstant.QUERY_INTERVAL);
    }

    @Override
    public MetricList getFlushTimeMin(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.FLUSH_TIME_MIN.name(), CalcFunction.MIN.name(), MetricConstant.QUERY_INTERVAL);
    }

    @Override
    public MetricList getFlushFailedCount(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.FLUSH_FAILED_COUNT.name(), CalcFunction.SUM.name(), MetricConstant.HEARTBEAT_PERIOD);
    }

    @Override
    public MetricList getFilterCount(MetricQueryDO metricQueryDO) {
        return handleAggregationQuery(metricQueryDO, AgentMetricField.FILTER_OUT.name(), CalcFunction.SUM.name(), MetricConstant.HEARTBEAT_PERIOD);
    }

    @Override
    public Long getCollectBytesToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Long startTime = calendar.getTimeInMillis();
        Long endTime = System.currentTimeMillis();
        Double value = agentMetricsManageService.queryAggregationForAll(startTime, endTime, AgentMetricField.SEND_BYTE, CalcFunction.SUM);
        return null == value ? 0L : value.longValue();
    }

    @Override
    public Long getCollectCountToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Long startTime = calendar.getTimeInMillis();
        Long endTime = System.currentTimeMillis();
        Double value = agentMetricsManageService.queryAggregationForAll(startTime, endTime, AgentMetricField.SEND_COUNT, CalcFunction.SUM);
        return null == value ? 0L : value.longValue();
    }

    @Override
    public Long getCurrentCollectBytes() {
        Long endTime = System.currentTimeMillis();
        Long startTime = endTime - HEARTBEAT_PERIOD * 1000;
        Double value = agentMetricsManageService.queryAggregationForAll(startTime, endTime, AgentMetricField.SEND_BYTE, CalcFunction.SUM);
        value = null == value ? 0 : value;
        return (long) Math.ceil(value / HEARTBEAT_PERIOD);
    }

    class MetricPointComparator implements java.util.Comparator<MetricPoint> {
        @Override
        public int compare(MetricPoint o1, MetricPoint o2) {
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        }
    }

    @Override
    public Long getCurrentCollectCount() {
        Long endTime = System.currentTimeMillis();
        Long startTime = endTime - HEARTBEAT_PERIOD * 1000;
        Double value = agentMetricsManageService.queryAggregationForAll(startTime, endTime, AgentMetricField.SEND_COUNT, CalcFunction.SUM);
        value = null == value ? 0 : value;
        return (long) Math.ceil(value / HEARTBEAT_PERIOD);
    }

    @Override
    public List<MetricPointList> getTop5HostCount(Long startTime, Long endTime) {
        List<LogCollectTaskPO> taskList = logCollectorTaskDAO.queryAll();
        int limit = Math.min(taskList.size(), 5);
        List<MetricPointList> metricPointLists = new ArrayList<>();
        List<LogCollectTaskPO> sortedList = taskList.stream().sorted((i1, i2) -> {
            int size1 = hostManageService.getHostListByLogCollectTaskId(i1.getId()).size();
            int size2 = hostManageService.getHostListByLogCollectTaskId(i2.getId()).size();
            return size2 - size1;
        }).limit(limit).collect(Collectors.toList());

        for (LogCollectTaskPO logCollectTaskPO : sortedList) {
            List<MetricPoint> graph = new ArrayList<>();
            MetricUtils.buildEmptyMetric(graph, startTime, endTime, MetricConstant.QUERY_INTERVAL, hostManageService.getHostListByLogCollectTaskId(logCollectTaskPO.getId()).size());
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(graph);
            metricPointList.setName(logCollectTaskPO.getLogCollectTaskName());
            metricPointLists.add(metricPointList);
        }
        return metricPointLists;
    }

    @Override
    public List<MetricPointList> getTop5AgentCount(Long startTime, Long endTime) {
        List<LogCollectTaskPO> taskList = logCollectorTaskDAO.queryAll();
        int limit = Math.min(taskList.size(), 5);
        List<MetricPointList> metricPointLists = new ArrayList<>();
        List<LogCollectTaskPO> sortedList = taskList.stream().sorted((i1, i2) -> {
            int size1 = hostManageService.getHostListContainsAgentByLogCollectTaskId(i1.getId()).size();
            int size2 = hostManageService.getHostListContainsAgentByLogCollectTaskId(i2.getId()).size();
            return size2 - size1;
        }).limit(limit).collect(Collectors.toList());

        for (LogCollectTaskPO logCollectTaskPO : sortedList) {
            List<MetricPoint> graph = new ArrayList<>();
            MetricUtils.buildEmptyMetric(graph, startTime, endTime, MetricConstant.QUERY_INTERVAL, hostManageService.getHostListContainsAgentByLogCollectTaskId(logCollectTaskPO.getId()).size());
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(graph);
            metricPointList.setName(logCollectTaskPO.getLogCollectTaskName());
            metricPointLists.add(metricPointList);
        }
        return metricPointLists;
    }

    /**
     * 日志采集任务对应某个待采集路径在具体某主机采集指标信息
     *
     * @param logCollectTaskId 日志采集任务 id
     * @param logPathId        日志采集路径 id
     * @param hostName         主机名
     * @param startTime        指标信息查询开始时间
     * @param endTime          指标信息查询结束时间
     * @return 返回日志采集任务对应某个待采集路径在具体某主机采集指标信息
     */
    private List<MetricPanelGroup> handleListLogCollectTaskMetricsPerHostAndPath(Long logCollectTaskId, Long logPathId, String hostName, Long startTime, Long endTime) {

        MetricsDashBoard metricsDashBoard = new MetricsDashBoard();

        /*
         * 构建指标面板组
         */
        MetricPanelGroup metricPanelGroup = metricsDashBoard.buildMetricPanelGroup(LogCollectTaskConstant.LOG_COLLECT_TASK_METRIC_PANEL_GROUP_NAME_LOGCOLLECTTASK_LEVEL);

        /*
         * 构建 日志采集任务+采集日志路径+主机 对应 "日志读取字节数/分钟" 指标
         * 日志读取意义为：从给定 fd 读取一个 log event（含：时间戳 解析）
         */

        /*
         * 构建 日志采集任务+采集日志路径+主机 对应 "日志读取条数/分钟" 指标
         * 日志读取意义为：从给定 fd 读取一个 log event（含：时间戳 解析）
         */

        /*
         * 构建 日志采集任务+采集日志路径+主机 对应 "日志读取耗时/分钟" 指标
         * 日志读取意义为：从给定 fd 读取一个 log event（含：时间戳 解析）
         */

        /*
         * 构建 日志采集任务+采集日志路径+主机 对应 "日志处理耗时/分钟" 指标
         * 日志处理意义为：日志被读取 ~ 日志被 kafka sink 取出 & 将 log event 转化为 kafka event & 发送（ps：存入 sink 缓冲区 一定条件下 触发 kafka producer 发送） & 提交 对应 offset
         */

        /*
         * 构建 日志采集任务+采集日志路径+主机 对应 "日志发送字节数/分钟" 指标
         * 日志发送意义为：kafka event 集 被 kafka producer 实际 发送 至 kafka broker 并得到 对应 response
         */

        /*
         * 构建 日志采集任务+采集日志路径+主机 对应 "日志发送条数/分钟" 指标
         * 日志发送意义为：kafka event 集 被 kafka producer 实际 发送 至 kafka broker 并得到 对应 response
         */

        /*
         * 构建 日志采集任务+采集日志路径+主机 对应 "日志发送耗时/分钟" 指标
         * 日志发送意义为：kafka event 集 被 kafka producer 实际 发送 至 kafka broker 并得到 对应 response
         */

        /*
         * 构建 日志采集任务+采集日志路径+主机 对应 "日志 flush 次数/分钟" 指标
         * 日志flush意义为：kafka producer 从各 sink 端获取所有待发送 kafka event 发送至 kafka broker 端并 get 对应 response
         */

        /*
         * 构建 日志采集任务+采集日志路径+主机 对应 "日志 flush 耗时/分钟" 指标
         * 日志flush意义为：kafka producer 从各 sink 端获取所有待发送 kafka event 发送至 kafka broker 端并 get 对应 response
         */

        /*
         * 构建 日志采集任务+采集日志路径+主机 对应 "日志 flush 失败次数/分钟" 指标
         * 日志flush意义为：kafka producer 从各 sink 端获取所有待发送 kafka event 发送至 kafka broker 端并 get 对应 response
         */

        /*
         * 构建"日志采集任务+采集日志路径+主机 对应 "channel 当前 log event 数" 指标（ps：channel size）
         */

        return null;

    }

    /**
     * 根据日志采集任务 id 获取给定时间范围内对应日志采集任务运行时指标信息
     *
     * @param logCollectTaskId 日志采集任务 id
     * @param startTime        开始时间
     * @param endTime          结束时间
     * @return 返回根据日志采集任务 id 获取到的给定时间范围内对应日志采集任务运行时指标信息
     */
    private MetricsDashBoard handlerListLogCollectTaskMetrics(Long logCollectTaskId, Long startTime, Long endTime) {
        LogCollectTaskDO logCollectTaskDO = getById(logCollectTaskId);
        List<HostDO> hostDOList = hostManageService.getHostListByLogCollectTaskId(logCollectTaskId);
        if (null == logCollectTaskDO) {
            throw new ServiceException(
                    String.format("LogCollectTask={id=%d}在系统中不存在", logCollectTaskId),
                    ErrorCodeEnum.LOGCOLLECTTASK_NOT_EXISTS.getCode()
            );
        }
        MetricsDashBoard metricsDashBoard = new MetricsDashBoard();
        /********************************************* LogCollectTask 级相关指标 *********************************************/
        /*
         * 构建日志采集任务级指标面板组
         */
        MetricPanelGroup logCollectTaskLevelMetricPanelGroup = metricsDashBoard.buildMetricPanelGroup(LogCollectTaskConstant.LOG_COLLECT_TASK_METRIC_PANEL_GROUP_NAME_LOGCOLLECTTASK_LEVEL);
        /*
         * 构建"日志采集任务对应日志采集流量 bytes/分钟"指标
         */
        MetricPanel logCollectTaskLogsBytesPerMinMetricPanel = logCollectTaskLevelMetricPanelGroup.buildMetricPanel(LogCollectTaskConstant.LOG_COLLECT_TASK_METRIC_PANEL_NAME_LOGS_BYTES_PER_MIN);
        List<MetricPoint> logCollectTaskLogsBytesPerMinMetricPointList = agentMetricsManageService.getLogCollectTaskLogsBytesPerMinMetric(logCollectTaskId, startTime, endTime);
        logCollectTaskLogsBytesPerMinMetricPanel.buildMetric(LogCollectTaskConstant.LOG_COLLECT_TASK_METRIC_NAME_LOGS_BYTES_PER_MIN, logCollectTaskLogsBytesPerMinMetricPointList);
        /*
         * 构建"日志采集任务对应日志采集条数/分钟"指标
         */
        MetricPanel logCollectTaskLogsCountPerMinMetricPanel = logCollectTaskLevelMetricPanelGroup.buildMetricPanel(LogCollectTaskConstant.LOG_COLLECT_TASK_METRIC_PANEL_NAME_LOGS_COUNT_PER_MIN);
        List<MetricPoint> logCollectTaskLogsCountPerMinMetricPointList = agentMetricsManageService.getLogCollectTaskLogsCountPerMinMetric(logCollectTaskId, startTime, endTime);
        logCollectTaskLogsCountPerMinMetricPanel.buildMetric(LogCollectTaskConstant.LOG_COLLECT_TASK_METRIC_NAME_LOGS_COUNT_PER_MIN, logCollectTaskLogsCountPerMinMetricPointList);
        /********************************************* fileLogPath 级相关指标 *********************************************/
        List<FileLogCollectPathDO> fileLogCollectPathDOList = logCollectTaskDO.getFileLogCollectPathList();//日志采集任务关联的所有待采集路径集
        for (FileLogCollectPathDO fileLogCollectPathDO : fileLogCollectPathDOList) {
            //构建日志采集任务级指标面板组
            MetricPanelGroup fileLogPathLevelMetricPanelGroup = metricsDashBoard.buildMetricPanelGroup(fileLogCollectPathDO.getPath());
            /*
             * 构建"fileLogPath在各host是否存在"指标
             */
            MetricPanel fileLogPathExistsPerMinMetricPanel = fileLogPathLevelMetricPanelGroup.buildMetricPanel(LogCollectTaskConstant.LOG_COLLECT_TASK_METRIC_PANEL_NAME_FILELOGPATH_EXISTS_PER_MIN);
            for (HostDO hostDO : hostDOList) {
                //获取该路径在给定范围时间在各主机是否存在
                List<MetricPoint> fileLogPathExistsPerMinMetricPointList = agentMetricsManageService.getFileLogPathExistsPerMinMetric(logCollectTaskId, fileLogCollectPathDO.getId(), hostDO.getHostName(), startTime, endTime);
                fileLogPathExistsPerMinMetricPanel.buildMetric(hostDO.getHostName(), fileLogPathExistsPerMinMetricPointList);
            }
            /*
             * 构建"fileLogPath在各host是否存在乱序"指标
             */
            MetricPanel fileLogPathDisorderPerMinMetricPanel = fileLogPathLevelMetricPanelGroup.buildMetricPanel(LogCollectTaskConstant.LOG_COLLECT_TASK_METRIC_PANEL_NAME_FILELOGPATH_DISORDER_PER_MIN);
            for (HostDO hostDO : hostDOList) {
                //获取该路径在给定范围时间在各主机是否存在待采集路径乱序输出
                List<MetricPoint> fileLogPathDisorderPerMinMetricPointList = agentMetricsManageService.getFileLogPathDisorderPerMinMetric(logCollectTaskId, fileLogCollectPathDO.getId(), hostDO.getHostName(), startTime, endTime);
                fileLogPathDisorderPerMinMetricPanel.buildMetric(hostDO.getHostName(), fileLogPathDisorderPerMinMetricPointList);
            }
            /*
             * 构建"fileLogPath在各host是否存在日志切片错误"指标
             */
            MetricPanel fileLogPathLogSliceErrorPerMinMetricPanel = fileLogPathLevelMetricPanelGroup.buildMetricPanel(LogCollectTaskConstant.LOG_COLLECT_TASK_METRIC_PANEL_NAME_FILELOGPATH_LOG_SLICE_ERROR_PER_MIN);
            for (HostDO hostDO : hostDOList) {
                //获取该路径在给定范围时间在各主机是否存在日志切片错误
                List<MetricPoint> fileLogPathLogSliceErrorPerMinMetricPointList = agentMetricsManageService.getFileLogPathLogSliceErrorPerMinMetric(logCollectTaskId, fileLogCollectPathDO.getId(), hostDO.getHostName(), startTime, endTime);
                fileLogPathLogSliceErrorPerMinMetricPanel.buildMetric(hostDO.getHostName(), fileLogPathLogSliceErrorPerMinMetricPointList);
            }
            /*
             * 构建"fileLogPath在各host是否存在日志异常截断"指标
             */
            MetricPanel fileLogPathLogAbnormalTruncationPerMinMetricPanel = fileLogPathLevelMetricPanelGroup.buildMetricPanel(LogCollectTaskConstant.LOG_COLLECT_TASK_METRIC_PANEL_NAME_FILELOGPATH_ABNORMAL_TRUNCATION_PER_MIN);
            for (HostDO hostDO : hostDOList) {
                //获取该路径在给定范围时间在各主机是否存在日志异常截断
                List<MetricPoint> fileLogPathAbnormalTruncationPerMinMetricPointList = agentMetricsManageService.getFileLogPathAbnormalTruncationPerMinMetric(logCollectTaskId, fileLogCollectPathDO.getId(), hostDO.getHostName(), startTime, endTime);
                fileLogPathLogAbnormalTruncationPerMinMetricPanel.buildMetric(hostDO.getHostName(), fileLogPathAbnormalTruncationPerMinMetricPointList);
            }
            /*
             * 构建"fileLogPath对应数据过滤条数/分钟"指标
             */
            MetricPanel fileLogPathFilterOutPerMinMetricPanel = fileLogPathLevelMetricPanelGroup.buildMetricPanel(LogCollectTaskConstant.LOG_COLLECT_TASK_METRIC_PANEL_NAME_FILELOGPATH_FILTER_OUT_PER_MIN);
            for (HostDO hostDO : hostDOList) {
                //获取该路径在给定范围时间过滤条数
                List<MetricPoint> fileLogPathFilterOutPerMinMetricPointList = agentMetricsManageService.getFilterOutPerLogPathPerMinMetric(logCollectTaskId, fileLogCollectPathDO.getId(), hostDO.getHostName(), startTime, endTime);
                fileLogPathFilterOutPerMinMetricPanel.buildMetric(hostDO.getHostName(), fileLogPathFilterOutPerMinMetricPointList);
            }
            /*
             * 构建"fileLogPath最小采集业务时间 top5 host "指标
             */
            MetricPanel fileLogPathMinCollectBusinessTimePerMinMetricPanel = fileLogPathLevelMetricPanelGroup.buildMetricPanel(LogCollectTaskConstant.LOG_COLLECT_TASK_METRIC_PANEL_NAME_FILELOGPATH_MIN_COLLECT_BUSINESS_TIME_PER_MIN);
            for (HostDO hostDO : hostDOList) {
                //获取该路径在给定范围时间在各主机对应最小采集业务时间
                List<MetricPoint> fileLogPathMinCollectBusinessTimePerMinMetricPointList = agentMetricsManageService.getMinCurrentCollectTimePerLogPathPerMinMetric(logCollectTaskId, fileLogCollectPathDO.getId(), hostDO.getHostName(), startTime, endTime);
                fileLogPathMinCollectBusinessTimePerMinMetricPanel.buildMetric(hostDO.getHostName(), fileLogPathMinCollectBusinessTimePerMinMetricPointList);
            }

            /*
             * 构建"fileLogPath对应数据限流时长/分钟"指标
             */
            MetricPanel fileLogPathLimitTimePerMinMetricPanel = fileLogPathLevelMetricPanelGroup.buildMetricPanel(LogCollectTaskConstant.LOG_COLLECT_TASK_METRIC_PANEL_NAME_FILELOGPATH_LIMIT_TIME_PER_MIN);
            for (HostDO hostDO : hostDOList) {
                List<MetricPoint> fileLogPathLimitTimePerMinMetricPointList = agentMetricsManageService.getLimitTimePerLogPathPerMinMetric(logCollectTaskId, fileLogCollectPathDO.getId(), hostDO.getHostName(), startTime, endTime);
                fileLogPathLimitTimePerMinMetricPanel.buildMetric(hostDO.getHostName(), fileLogPathLimitTimePerMinMetricPointList);
            }
        }
        return metricsDashBoard;
    }

    /**
     * 启/停日志采集任务
     *
     * @param logCollectTaskId 日志采集任务id
     * @param operator         操作人
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleSwitchLogCollectTask(Long logCollectTaskId, Integer status, String operator) throws ServiceException {
        /*
         * 校验 status 是否合法
         */
        if (null == status || (LogCollectTaskStatusEnum.invalidStatus(status))) {
            throw new ServiceException(
                    String.format("给定日志采集任务启|停状态={%d}非法，合法取值范围为[0,1]", status),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        /*
         * 校验待启/停日志采集任务在系统中是否存在
         */
        LogCollectTaskPO logCollectTaskPO = logCollectorTaskDAO.selectByPrimaryKey(logCollectTaskId);
        if (null == logCollectTaskPO) {
            throw new ServiceException(
                    String.format("待启/停LogCollectTask={id=%d}在系统中不存在", logCollectTaskId),
                    ErrorCodeEnum.LOGCOLLECTTASK_NOT_EXISTS.getCode()
            );
        }
        /*
         * 更新日志采集任务启/停状态
         */
        logCollectTaskPO.setLogCollectTaskStatus(status);
        logCollectTaskPO.setOperator(CommonConstant.getOperator(operator));
        logCollectorTaskDAO.updateByPrimaryKey(logCollectTaskPO);
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.LOG_COLLECT_TASK,
                OperationEnum.EDIT,
                logCollectTaskId,
                String.format("修改LogCollectTask={id={%d}}对应状态logCollectTaskStatus={%d}", logCollectTaskId, status),
                operator
        );
    }

    /**
     * 创建一个日志采集任务信息，在日志采集任务添加操作成功后，自动构建日志采集任务 & 服务关联关系
     * 注：该函数作为一个整体运行在一个事务中，不抛异常提交事务，抛异常回滚事务
     *
     * @param logCollectTaskDO 日志采集任务对象
     * @param operator         操作人
     * @return 创建成功的日志采集任务对象id值
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long handleCreateLogCollectorTask(LogCollectTaskDO logCollectTaskDO, String operator) throws ServiceException {
        /*
         * 校验日志采集任务对象参数信息是否合法
         */
        CheckResult checkResult = logCollectTaskManageServiceExtension.checkCreateParameterLogCollectTask(logCollectTaskDO);
        if (!checkResult.getCheckResult()) {//日志采集任务对象信息不合法
            throw new ServiceException(
                    checkResult.getMessage(),
                    checkResult.getCode()
            );
        }
        /*
         * 持久化给定logCollectTask对象，及其关联的LogCollectPath对象集，并获取持久化的日志采集任务对象 id
         */
        Long savedLogCollectTaskId = saveLogCollectTask(logCollectTaskDO, operator);
        /*
         * 初始化 & 持久化日志采集任务关联的日志采集任务健康度信息
         */
        logCollectTaskHealthManageService.createInitialLogCollectorTaskHealth(savedLogCollectTaskId, operator);
        /*
         * 持久化待创建日志采集任务对象 & 服务关联关系
         */
        saveServiceLogCollectTaskRelation(logCollectTaskDO.getServiceIdList(), savedLogCollectTaskId);
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.LOG_COLLECT_TASK,
                OperationEnum.ADD,
                savedLogCollectTaskId,
                String.format("创建LogCollectTask={%s}，创建成功的LogCollectTask对象id={%d}", JSON.toJSONString(logCollectTaskDO), savedLogCollectTaskId),
                operator
        );
        return savedLogCollectTaskId;
    }

    /**
     * 根据给定 serviceIdList & logCollectTaskId 持久化对应服务 & 日志采集任务关联关系
     *
     * @param serviceIdList    服务对象id集
     * @param logCollectTaskId 日志采集任务对象id
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void saveServiceLogCollectTaskRelation(List<Long> serviceIdList, Long logCollectTaskId) throws ServiceException {
        List<LogCollectTaskServicePO> logCollectTaskServicePOList = new ArrayList<>(serviceIdList.size());
        for (Long serviceId : serviceIdList) {
            logCollectTaskServicePOList.add(new LogCollectTaskServicePO(logCollectTaskId, serviceId));
        }
        serviceLogCollectTaskManageService.createLogCollectTaskServiceList(logCollectTaskServicePOList);
    }

    /**
     * 持久化给定日志采集任务 & 关联 LogCollectPath对象集 & LogCollectTaskHealthPO 对象对象并返回已持久化的日志采集任务对象 id 值
     *
     * @param logCollectTaskDO 日志采集任务对象
     * @return 持久化的日志采集任务对象 id 值
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long saveLogCollectTask(LogCollectTaskDO logCollectTaskDO, String operator) throws ServiceException {
        /*
         * 持久化日志采集任务对象 LogCollectTaskPO
         */
        LogCollectTaskPO logCollectTaskPO = logCollectTaskManageServiceExtension.logCollectTask2LogCollectTaskPO(logCollectTaskDO);
        logCollectTaskPO.setOperator(CommonConstant.getOperator(operator));
        logCollectTaskPO.setConfigurationVersion(LogCollectTaskConstant.LOG_COLLECT_TASK_CONFIGURATION_VERSION_INIT);
        logCollectTaskPO.setLogCollectTaskStatus(LogCollectTaskStatusEnum.RUNNING.getCode());
        logCollectorTaskDAO.insert(logCollectTaskPO);
        Long logCollectTaskId = logCollectTaskPO.getId();
        /*
         * 持久化日志采集任务关联的日志采集路径对象集
         */
        List<DirectoryLogCollectPathDO> directoryLogCollectPathList = logCollectTaskDO.getDirectoryLogCollectPathList();
        List<FileLogCollectPathDO> fileLogCollectPathList = logCollectTaskDO.getFileLogCollectPathList();
        if (CollectionUtils.isEmpty(directoryLogCollectPathList) && CollectionUtils.isEmpty(fileLogCollectPathList)) {
            throw new ServiceException(
                    String.format(
                            "class=LogCollectTaskManageServiceImpl||method=saveLogCollectTask||msg={%s}",
                            String.format("LogCollectTask对象={%s}关联的日志采集路径不可为空", JSON.toJSONString(logCollectTaskDO))
                    ),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        if (CollectionUtils.isNotEmpty(directoryLogCollectPathList)) {
            for (DirectoryLogCollectPathDO directoryLogCollectPath : directoryLogCollectPathList) {
                //持久化目录类型日志采集路径对象 DirectoryLogCollectPathPO
                directoryLogCollectPath.setLogCollectTaskId(logCollectTaskId);
                directoryLogCollectPathManageService.createDirectoryLogCollectPath(directoryLogCollectPath, operator);
            }
        }
        if (CollectionUtils.isNotEmpty(fileLogCollectPathList)) {
            for (FileLogCollectPathDO fileLogCollectPath : fileLogCollectPathList) {
                //持久化文件类型日志采集路径对象 FileLogCollectPathPO
                fileLogCollectPath.setLogCollectTaskId(logCollectTaskId);
                fileLogCollectPathManageService.createFileLogCollectPath(fileLogCollectPath, operator);
            }
        }
        return logCollectTaskId;
    }

    @Override
    @Transactional
    public void deleteLogCollectTask(Long id, String operator) {
        this.handleDeleteLogCollectTask(id, operator);
    }

    /**
     * 删除给定id对应日志采集任务对象
     *
     * @param logCollectTaskId 待删除日志采集任务对象 logCollectTaskId 值
     * @param operator         操作人
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleDeleteLogCollectTask(Long logCollectTaskId, String operator) throws ServiceException {
        /*
         * 检查入参 logCollectTaskId 是否为空
         */
        if (null == logCollectTaskId) {
            throw new ServiceException(
                    "入参logCollectTaskId不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        /*
         * 检查待删除日志采集任务 logCollectTaskId 对应日志采集任务对象在系统是否存在
         */
        if (null == logCollectorTaskDAO.selectByPrimaryKey(logCollectTaskId)) {
            throw new ServiceException(
                    String.format("删除logCollectTask对象{logCollectTaskId=%d}失败，原因为：系统中不存在id={%d}的logCollectTask对象", logCollectTaskId, logCollectTaskId),
                    ErrorCodeEnum.LOGCOLLECTTASK_NOT_EXISTS.getCode()
            );
        }
        /*
         * 删除日志采集任务 & 服务关联关系
         */
        serviceLogCollectTaskManageService.removeServiceLogCollectTaskByLogCollectTaskId(logCollectTaskId);
        /*
         * 删除日志采集任务关联的日志采集任务健康信息
         */
        logCollectTaskHealthManageService.deleteByLogCollectTaskId(logCollectTaskId, operator);
        /*
         * 删除日志采集任务关联的日志采集路径对象集
         */
        directoryLogCollectPathManageService.deleteByLogCollectTaskId(logCollectTaskId);
        fileLogCollectPathManageService.deleteByLogCollectTaskId(logCollectTaskId);
        /*
         * 删除日志采集任务信息
         */
        logCollectorTaskDAO.deleteByPrimaryKey(logCollectTaskId);
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.LOG_COLLECT_TASK,
                OperationEnum.DELETE,
                logCollectTaskId,
                String.format("删除LogCollectTask对象={id={%d}}", logCollectTaskId),
                operator
        );
    }

    @Override
    @Transactional
    public void updateLogCollectTask(LogCollectTaskDO logCollectTask, String operator) {
        this.handleUpdateLogCollectTask(logCollectTask, operator);
    }

    /**
     * 更新一个日志采集任务信息：删除更新前的日志采集任务 & 服务关联关系 -> 构建更新后的日志采集任务 & 已存在服务关联关系 -> 更新"除日志采集任务 & 已存在服务关联关系外的全量日志采集任务元信息"
     * 注：该函数作为一个整体运行在一个事务中，不抛异常提交事务，抛异常回滚事务
     *
     * @param logCollectTaskDO 待更新日志采集任务对象
     * @param operator         操作人
     */
    private void handleUpdateLogCollectTask(LogCollectTaskDO logCollectTaskDO, String operator) throws ServiceException {
        /*
         * 校验日志采集任务对象参数信息是否合法
         */
        CheckResult checkResult = logCollectTaskManageServiceExtension.checkUpdateParameterLogCollectTask(logCollectTaskDO);
        if (!checkResult.getCheckResult()) {//日志采集任务对象信息不合法
            throw new ServiceException(
                    checkResult.getMessage(),
                    checkResult.getCode()
            );
        }
        /*
         * 校验待更新日志采集任务在系统中是否存在
         */
        LogCollectTaskDO logCollectTaskDOSource = getById(logCollectTaskDO.getId());
        if (null == logCollectTaskDOSource) {
            throw new ServiceException(
                    String.format("待更新LogCollectTask={id=%d}在系统中不存在", logCollectTaskDO.getId()),
                    ErrorCodeEnum.LOGCOLLECTTASK_NOT_EXISTS.getCode()
            );
        }
        /*
         * 更新日志采集任务
         */
        LogCollectTaskDO logCollectTaskDO2Save = logCollectTaskManageServiceExtension.updateLogCollectTask(logCollectTaskDOSource, logCollectTaskDO);
        LogCollectTaskPO logCollectTaskPO = logCollectTaskManageServiceExtension.logCollectTask2LogCollectTaskPO(logCollectTaskDO2Save);
        logCollectTaskPO.setOperator(CommonConstant.getOperator(operator));
        logCollectorTaskDAO.updateByPrimaryKey(logCollectTaskPO);
        /*
         * 更新日志采集任务关联的日志采集路径集相关信息
         */
        ListCompareResult<DirectoryLogCollectPathDO> directoryLogCollectPathDOListCompareResult = ListCompareUtil.compare(logCollectTaskDOSource.getDirectoryLogCollectPathList(), logCollectTaskDO.getDirectoryLogCollectPathList(), new Comparator<DirectoryLogCollectPathDO, String>() {
            @Override
            public String getKey(DirectoryLogCollectPathDO directoryLogCollectPathDO) {
                return directoryLogCollectPathDO.getPath();
            }

            @Override
            public boolean compare(DirectoryLogCollectPathDO t1, DirectoryLogCollectPathDO t2) {
                return t1.getCollectFilesFilterRegularPipelineJsonString().equals(t2.getCollectFilesFilterRegularPipelineJsonString()) &&
                        t1.getDirectoryCollectDepth().equals(t2.getDirectoryCollectDepth()) &&
                        t1.getPath().equals(t2.getPath());
            }

            @Override
            public DirectoryLogCollectPathDO getModified(DirectoryLogCollectPathDO source, DirectoryLogCollectPathDO target) {
                if (!source.getPath().equals(target.getPath())) {
                    source.setPath(target.getPath());
                }
                if (!source.getDirectoryCollectDepth().equals(target.getDirectoryCollectDepth())) {
                    source.setDirectoryCollectDepth(target.getDirectoryCollectDepth());
                }
                if (!source.getCollectFilesFilterRegularPipelineJsonString().equals(target.getCollectFilesFilterRegularPipelineJsonString())) {
                    source.setCollectFilesFilterRegularPipelineJsonString(target.getCollectFilesFilterRegularPipelineJsonString());
                }
                return source;
            }
        });
        for (DirectoryLogCollectPathDO directoryLogCollectPathDO : directoryLogCollectPathDOListCompareResult.getCreateList()) {
            directoryLogCollectPathDO.setLogCollectTaskId(logCollectTaskDO.getId());
            directoryLogCollectPathManageService.createDirectoryLogCollectPath(directoryLogCollectPathDO, operator);
        }
        for (DirectoryLogCollectPathDO directoryLogCollectPathDO : directoryLogCollectPathDOListCompareResult.getRemoveList()) {
            directoryLogCollectPathManageService.deleteDirectoryLogCollectPath(directoryLogCollectPathDO.getId(), operator);
        }
        for (DirectoryLogCollectPathDO directoryLogCollectPathDO : directoryLogCollectPathDOListCompareResult.getModifyList()) {
            directoryLogCollectPathManageService.updateDirectoryLogCollectPath(directoryLogCollectPathDO, operator);
        }
        ListCompareResult<FileLogCollectPathDO> fileLogCollectPathDOListCompareResult = ListCompareUtil.compare(logCollectTaskDOSource.getFileLogCollectPathList(), logCollectTaskDO.getFileLogCollectPathList(), new Comparator<FileLogCollectPathDO, String>() {
            @Override
            public String getKey(FileLogCollectPathDO fileLogCollectPathDO) {
                return fileLogCollectPathDO.getPath();
            }

            @Override
            public boolean compare(FileLogCollectPathDO t1, FileLogCollectPathDO t2) {
                return t1.getPath().equals(t2.getPath());
            }

            @Override
            public FileLogCollectPathDO getModified(FileLogCollectPathDO source, FileLogCollectPathDO target) {
                if (!source.getPath().equals(target.getPath())) {
                    source.setPath(target.getPath());
                }
                return source;
            }
        });

        LogCollectTaskHealthDO logCollectTaskHealthDO = logCollectTaskHealthManageService.getByLogCollectTaskId(logCollectTaskDO.getId());//待更新LogCollectTask对应LogCollectTaskHealth对象
        //存储各健康值对应时间点
        Map<Long, Long> fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap = JSON.parseObject(logCollectTaskHealthDO.getLastestAbnormalTruncationCheckHealthyTimePerLogFilePath(), Map.class);
        Map<Long, Long> fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap = JSON.parseObject(logCollectTaskHealthDO.getLastestLogSliceCheckHealthyTimePerLogFilePath(), Map.class);
        Map<Long, Long> fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap = JSON.parseObject(logCollectTaskHealthDO.getLastestFileDisorderCheckHealthyTimePerLogFilePath(), Map.class);
        Map<Long, Long> fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap = JSON.parseObject(logCollectTaskHealthDO.getLastestFilePathExistsCheckHealthyTimePerLogFilePath(), Map.class);
        Long fileLogPathCreateTime = System.currentTimeMillis();
        for (FileLogCollectPathDO fileLogCollectPathDO : fileLogCollectPathDOListCompareResult.getCreateList()) {
            fileLogCollectPathDO.setLogCollectTaskId(logCollectTaskDO.getId());
            Long fileLogCollectPathId = fileLogCollectPathManageService.createFileLogCollectPath(fileLogCollectPathDO, operator);
            /*
             * 增加FileLogPath需要更新对应LogCollectTaskHealth.lastestDataDiscardCheckHealthyTimePerLogFilePath、lastestAbnormalTruncationCheckHealthyTimePerLogFilePath、lastestErrorLogsExistsCheckHealthyTimePerLogFilePath、
             * lastestFileDisorderCheckHealthyTimePerLogFilePath、lastestFilePathExistsCheckHealthyTimePerLogFilePath 对应新增 filelogpath 对应 key - value
             */
            fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap.put(fileLogCollectPathId, fileLogPathCreateTime);
            fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap.put(fileLogCollectPathId, fileLogPathCreateTime);
            fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap.put(fileLogCollectPathId, fileLogPathCreateTime);
            fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap.put(fileLogCollectPathId, fileLogPathCreateTime);
        }
        for (FileLogCollectPathDO fileLogCollectPathDO : fileLogCollectPathDOListCompareResult.getRemoveList()) {
            fileLogCollectPathManageService.deleteFileLogCollectPath(fileLogCollectPathDO.getId(), operator);
            /*
             * 增加FileLogPath需要更新对应LogCollectTaskHealth.lastestDataDiscardCheckHealthyTimePerLogFilePath、lastestAbnormalTruncationCheckHealthyTimePerLogFilePath、lastestErrorLogsExistsCheckHealthyTimePerLogFilePath、
             * lastestFileDisorderCheckHealthyTimePerLogFilePath、lastestFilePathExistsCheckHealthyTimePerLogFilePath 对应删除 filelogpath 对应 key - value
             */
            fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap.remove(fileLogCollectPathDO.getId());
            fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap.remove(fileLogCollectPathDO.getId());
            fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap.remove(fileLogCollectPathDO.getId());
            fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap.remove(fileLogCollectPathDO.getId());
        }
        for (FileLogCollectPathDO fileLogCollectPathDO : fileLogCollectPathDOListCompareResult.getModifyList()) {
            fileLogCollectPathManageService.updateFileLogCollectPath(fileLogCollectPathDO, operator);
        }
        /*
         * 更新 LogCollectTaskHealth 对象
         */
        logCollectTaskHealthDO.setLastestAbnormalTruncationCheckHealthyTimePerLogFilePath(JSON.toJSONString(fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap));
        logCollectTaskHealthDO.setLastestLogSliceCheckHealthyTimePerLogFilePath(JSON.toJSONString(fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap));
        logCollectTaskHealthDO.setLastestFileDisorderCheckHealthyTimePerLogFilePath(JSON.toJSONString(fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap));
        logCollectTaskHealthDO.setLastestFilePathExistsCheckHealthyTimePerLogFilePath(JSON.toJSONString(fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap));
        logCollectTaskHealthManageService.updateLogCollectorTaskHealth(logCollectTaskHealthDO, CommonConstant.getOperator(null));
        /*
         * 更新日志采集任务对象 & 服务关联关系
         */
        serviceLogCollectTaskManageService.removeServiceLogCollectTaskByLogCollectTaskId(logCollectTaskDO.getId());
        saveServiceLogCollectTaskRelation(logCollectTaskDO.getServiceIdList(), logCollectTaskDO.getId());
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.LOG_COLLECT_TASK,
                OperationEnum.EDIT,
                logCollectTaskDO.getId(),
                String.format("修改LogCollectTask={%s}，修改成功的LogCollectTask对象id={%d}", JSON.toJSONString(logCollectTaskDO), logCollectTaskDO.getId()),
                operator
        );
    }

    @Override
    public List<LogCollectTaskDO> getLogCollectTaskListByHostId(Long hostId) {
        HostDO hostDO = hostManageService.getById(hostId);
        if (null == hostDO) {
            throw new ServiceException(
                    String.format("Host={id=%d}在系统中不存在", hostId),
                    ErrorCodeEnum.HOST_NOT_EXISTS.getCode()
            );
        }
        return getLogCollectTaskListByHost(hostDO);
    }

    @Override
    public LogCollectTaskDO getById(Long id) {
        /*
         * 加载LogCollectTaskDO
         */
        LogCollectTaskPO logCollectTaskPO = this.logCollectorTaskDAO.selectByPrimaryKey(id);
        if (null == logCollectTaskPO) {
            return null;
        }
        LogCollectTaskDO logCollectTaskDO = logCollectTaskManageServiceExtension.logCollectTaskPO2LogCollectTaskDO(logCollectTaskPO);
        /*
         * 加载LogCollectTaskDO.serviceIdList
         */
        List<ServiceDO> serviceDOList = serviceManageService.getServicesByLogCollectTaskId(id);
        List<Long> serviceIdList = new ArrayList<>(serviceDOList.size());
        for (ServiceDO serviceDO : serviceDOList) {
            serviceIdList.add(serviceDO.getId());
        }
        logCollectTaskDO.setServiceIdList(serviceIdList);
        /*
         * 加载LogCollectTaskDO.directoryLogCollectPathList
         */
        logCollectTaskDO.setDirectoryLogCollectPathList(directoryLogCollectPathManageService.getAllDirectoryLogCollectPathByLogCollectTaskId(id));
        /*
         * 加载LogCollectTaskDO.fileLogCollectPathList
         */
        logCollectTaskDO.setFileLogCollectPathList(fileLogCollectPathManageService.getAllFileLogCollectPathByLogCollectTaskId(id));
        return logCollectTaskDO;
    }

    @Override
    public Integer getRelatedAgentCount(Long id) {
        if (id <= 0) {
            throw new ServiceException("task id非法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        Set<Long> agentIds = new HashSet<>();
        List<HostDO> hosts = hostManageService.getHostListByLogCollectTaskId(id);
        Set<String> hostnames = new HashSet<>();
        for (HostDO host : hosts) {
            if (HostTypeEnum.CONTAINER.getCode().equals(host.getContainer())) {
                hostnames.add(host.getParentHostName());
            } else {
                hostnames.add(host.getHostName());
            }
        }
        for (String hostname : hostnames) {
            AgentDO agentDO = agentManageService.getAgentByHostName(hostname);
            if (agentDO != null) {
                agentIds.add(agentDO.getId());
            }
        }
        return agentIds.size();
    }

    @Override
    public LogCollectTaskHealthLevelEnum checkLogCollectTaskHealth(LogCollectTaskDO logCollectTaskDO) {
        /*
         * 校验日志采集任务是否须被诊断
         */
        CheckResult checkResult = logCollectTaskNeedCheck(logCollectTaskDO);
        /*
         * 诊断对应日志采集任务
         */
        if (checkResult.getCheckResult()) {//须诊断对应日志采集任务
            if (logCollectTaskDO.getLogCollectTaskType().equals(LogCollectTaskTypeEnum.NORMAL_COLLECT.getCode())) {
                return handleCheckNormalLogCollectTaskHealth(logCollectTaskDO, true);
            } else if (logCollectTaskDO.getLogCollectTaskType().equals(LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode()) && !logCollectTaskDO.getLogCollectTaskStatus().equals(LogCollectTaskStatusEnum.FINISH.getCode())) {
                return handleCheckTimeScopeLogCollectTaskHealth(logCollectTaskDO, true);
            } else {
                throw new ServiceException(
                        String.format("LogCollectTask={id=%d}未知采集类型={%d}", logCollectTaskDO.getId(), logCollectTaskDO.getLogCollectTaskType()),
                        ErrorCodeEnum.UNKNOWN_COLLECT_TYPE.getCode()
                );
            }
        } else {//该日志采集任务无须被诊断
            return LogCollectTaskHealthLevelEnum.GREEN;
        }
    }

    /**
     * 诊断给定时间范围采集类型日志采集任务
     *
     * @param logCollectTaskDO  待诊断日志采集任务对象
     * @param checkCollectDelay 是否校验日志采集任务采集延时 true：校验 false：不校验
     * @return 待诊断日志采集任务对象对应诊断结果
     */
    private LogCollectTaskHealthLevelEnum handleCheckTimeScopeLogCollectTaskHealth(LogCollectTaskDO logCollectTaskDO, boolean checkCollectDelay) {
        /*
         * 获取待检测日志采集任务对应健康记录
         */
        LogCollectTaskHealthDO logCollectTaskHealthDO = logCollectTaskHealthManageService.getByLogCollectTaskId(logCollectTaskDO.getId());
        if (null == logCollectTaskHealthDO) {
            throw new ServiceException(String.format("LogCollectTaskHealth={logCollectTaskId=%d}在系统中不存在", logCollectTaskDO.getId()), ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode());
        }
        /*
         * 获取日式采集任务须部署的主机列表
         */
        List<HostDO> hostDOList = hostManageService.getHostListByLogCollectTaskId(logCollectTaskDO.getId());//部署日志采集任务的主机对象集
        /*
         * 获取日志采集任务须采集的文件路径集
         */
        List<FileLogCollectPathDO> fileLogCollectPathDOList = logCollectTaskDO.getFileLogCollectPathList();//日志采集任务对应各采集路径
        /*
         * 进入巡检流程
         */

        LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum = LogCollectTaskHealthLevelEnum.GREEN;//日志采集任务健康度检查结果
        String logCollectTaskHealthDescription = "";//日志采集任务健康检查描述
        Long minCurrentCollectTime = Long.MAX_VALUE;//记录logCollectTaskDO对应各LogCollectPath最小采集时间，记为采集完整性时间
        Integer scopeCollectCompleteLogPathCount = 0;//记录时间范围采集类型日志采集任务已采集完logCollectPath数 如 scopeCollectCompleteLogPathCount == fileLogCollectPathDOList.size() 表示 logCollectTaskDO 已完成，记录对应任务执行结束时间，并修改对应状态为 "已完成"
        Map<Long, Long> logFilePathId2CollctCompleteTimeMap = new HashMap<>();//存储各fileLogPath对应采集完整性时间 map，key:logPathId value:collctCompleteTime
        Long logCollectTaskHealthCheckTimeEnd = DateUtils.getBeforeSeconds(new Date(), 1).getTime();//日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一秒
        //存储各健康值对应时间点
        Map<Long, Long> fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap = JSON.parseObject(logCollectTaskHealthDO.getLastestAbnormalTruncationCheckHealthyTimePerLogFilePath(), new TypeReference<Map<Long, Long>>() {
        });
        Map<Long, Long> fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap = JSON.parseObject(logCollectTaskHealthDO.getLastestLogSliceCheckHealthyTimePerLogFilePath(), new TypeReference<Map<Long, Long>>() {
        });
        Map<Long, Long> fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap = JSON.parseObject(logCollectTaskHealthDO.getLastestFileDisorderCheckHealthyTimePerLogFilePath(), new TypeReference<Map<Long, Long>>() {
        });
        Map<Long, Long> fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap = JSON.parseObject(logCollectTaskHealthDO.getLastestFilePathExistsCheckHealthyTimePerLogFilePath(), new TypeReference<Map<Long, Long>>() {
        });
        if (CollectionUtils.isNotEmpty(fileLogCollectPathDOList)) {
            for (FileLogCollectPathDO fileLogCollectPathDO : fileLogCollectPathDOList) {
                minCurrentCollectTime = Long.MAX_VALUE;
                Integer abnormalTruncationCheckHealthyCount = 0;
                Integer fileDisorderCheckHealthyCount = 0;
                Integer filePathExistsCheckHealthyCount = 0;
                Integer logSliceCheckHealthyCount = 0;
                for (HostDO hostDO : hostDOList) {

                    /*
                     * 计算logpath对应完整性时间
                     */
                    minCurrentCollectTime = Math.min(minCurrentCollectTime, getCurrentCompleteTime(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName()));

                    /************************ mark red cases ************************/

                    /*
                     * 校验日志采集任务健康度
                     */
                    if (null != logCollectTaskHealthLevelEnum && (logCollectTaskHealthLevelEnum.getCode().equals(LogCollectTaskHealthLevelEnum.RED.getCode()))) {
                        //表示健康度已被检测为 red，此时，无须再进行校验
                        continue;
                    }

                    /*
                     * 校验在距当前时间的心跳存活判定周期内，logCollectTaskId+fileLogCollectPathId+hostName是否存在心跳
                     */
                    boolean alive = checkAliveByHeartbeat(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd);
                    if (!alive) {//不存活
                        logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_IN_HOST_HEART_BEAT_NOT_EXISTS.getLogCollectTaskHealthLevelEnum();
                        logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_IN_HOST_HEART_BEAT_NOT_EXISTS.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                    }
                    /*
                     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在
                     */
                    boolean filePathExists = checkFilePathExists(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd, fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap);
                    if (!filePathExists) {//不存在
                        logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_NOT_EXISTS.getLogCollectTaskHealthLevelEnum();
                        logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_NOT_EXISTS.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                    } else {
                        filePathExistsCheckHealthyCount++;
                    }
                    /*
                     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在乱序
                     */
                    boolean fileDisorder = checkFileDisorder(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd, fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap);
                    if (fileDisorder) {//存在 乱序
                        logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_DISORDER.getLogCollectTaskHealthLevelEnum();
                        logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_DISORDER.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                    } else {
                        fileDisorderCheckHealthyCount++;
                    }
                    /*
                     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在日志切片配置错误
                     */
                    boolean errorLogsExists = checkLogSliceErrorExists(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd, fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap);
                    if (errorLogsExists) {//存在 日志切片配置错误
                        logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SLICE_ERROR_EXISTS.getLogCollectTaskHealthLevelEnum();
                        logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SLICE_ERROR_EXISTS.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                    } else {
                        logSliceCheckHealthyCount++;
                    }
                    /*
                     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在日志被异常截断
                     */
                    boolean abnormalTruncationExists = checkAbnormalTruncationExists(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd, fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap);
                    if (abnormalTruncationExists) {//存在 异常截断
                        logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SIZE_OVERRUN_TRUNCATE_EXISTS.getLogCollectTaskHealthLevelEnum();
                        logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SIZE_OVERRUN_TRUNCATE_EXISTS.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                    } else {
                        abnormalTruncationCheckHealthyCount++;
                    }
                    /*
                     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在多 agent 并发采集
                     */
                    boolean concurrentCollectExists = checkConcurrentCollectExists(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd);
                    if (concurrentCollectExists) {
                        logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_CONCURRENT_COLLECT.getLogCollectTaskHealthLevelEnum();
                        logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_CONCURRENT_COLLECT.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                    }
                    /*
                     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在采集延迟
                     */
                    if (checkCollectDelay && null != logCollectTaskDO.getCollectDelayThresholdMs() && logCollectTaskDO.getCollectDelayThresholdMs() > 0) {//该文件型日志采集路径须做采集延迟监控
                        boolean collectDelay = checkCollectDelay(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskDO.getCollectDelayThresholdMs());
                        if (collectDelay) {//存在 采集延迟
                            logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_COLLECT_DELAYED.getLogCollectTaskHealthLevelEnum();
                            logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_COLLECT_DELAYED.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                        }
                    }
                    /*
                     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在执行超时情况 & 其是否已执行完
                     */
                    if (logCollectTaskDO.getLogCollectTaskType().equals(LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode())) {
                        if (!logCollectTaskDO.getLogCollectTaskStatus().equals(LogCollectTaskStatusEnum.FINISH.getCode())) {//处于非 "已完成状态" & 时间范围采集类型的日志采集任务须检查其运行时长是否超时
                            boolean executeTimeout = checkTimeScopeLogCollectTaskExecuteTimeout(logCollectTaskDO, fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskDO.getLogCollectTaskExecuteTimeoutMs());
                            if (executeTimeout) {//存在 执行超时
                                logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_COLLECT_TIMEOUT.getLogCollectTaskHealthLevelEnum();
                                logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_COLLECT_TIMEOUT.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                            }
                            //时间范围采集类型日志采集任务是否已执行完，如已执行完，scopeCollectCompleteLogPathCount++
                            boolean timeScopeCollectComplete = checkTimeScopeCollectComplete(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO);
                            if (timeScopeCollectComplete) {
                                scopeCollectCompleteLogPathCount++;
                            }
                        }
                    }

                    /************************ mark yellow cases ************************/

                    /*
                     * 校验日志采集任务健康度
                     */
                    if (null != logCollectTaskHealthLevelEnum && (logCollectTaskHealthLevelEnum.getCode().equals(LogCollectTaskHealthLevelEnum.RED.getCode()) || logCollectTaskHealthLevelEnum.getCode().equals(LogCollectTaskHealthLevelEnum.YELLOW.getCode()))) {
                        //表示健康度已被检测为 red or yellow，此时，无须再进行校验
                        continue;
                    }

                    /*
                     * 校验 logcollecttask + logpath 在 host 端是否存在采集端出口流量阈值限流
                     */
                    boolean byteLimitOnHostExists = checkByteLimitOnHostExists(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                    if (byteLimitOnHostExists) {//存在采集端出口流量阈值限流
                        logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.HOST_BYTES_LIMIT_EXISTS.getLogCollectTaskHealthLevelEnum();
                        logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.HOST_BYTES_LIMIT_EXISTS.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                    }
                    /*
                     * 校验 logcollecttask 是否未关联主机
                     */
                    boolean notRelateAnyHost = checkNotRelateAnyHost(logCollectTaskDO.getId());
                    if (notRelateAnyHost) {//logcollecttask 未关联主机
                        logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.NOT_RELATE_ANY_HOST.getLogCollectTaskHealthLevelEnum();
                        logCollectTaskHealthDescription = String.format(
                                "%s:LogCollectTaskId={%d}",
                                LogCollectTaskHealthInspectionResultEnum.NOT_RELATE_ANY_HOST.getDescription(),
                                logCollectTaskDO.getId()
                        );
                    }
                }
                /*
                 * 设置各 filePathId 对应采集完整性时间
                 */
                logFilePathId2CollctCompleteTimeMap.put(fileLogCollectPathDO.getId(), minCurrentCollectTime);
                /*
                 * 设置各filePathId对应各指标健康时时间，以便下次进行巡检
                 */
                if (abnormalTruncationCheckHealthyCount.equals(hostDOList.size())) {
                    fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap.put(fileLogCollectPathDO.getId(), logCollectTaskHealthCheckTimeEnd);
                }
                if (logSliceCheckHealthyCount.equals(hostDOList.size())) {
                    fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap.put(fileLogCollectPathDO.getId(), logCollectTaskHealthCheckTimeEnd);
                }
                if (fileDisorderCheckHealthyCount.equals(hostDOList.size())) {
                    fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap.put(fileLogCollectPathDO.getId(), logCollectTaskHealthCheckTimeEnd);
                }
                if (filePathExistsCheckHealthyCount.equals(hostDOList.size())) {
                    fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap.put(fileLogCollectPathDO.getId(), logCollectTaskHealthCheckTimeEnd);
                }
            }
        }

        if (null != logCollectTaskHealthLevelEnum && (logCollectTaskHealthLevelEnum.getCode().equals(LogCollectTaskHealthLevelEnum.RED.getCode()) || logCollectTaskHealthLevelEnum.getCode().equals(LogCollectTaskHealthLevelEnum.YELLOW.getCode()))) {
            //表示健康度已被检测为 red or yellow，此时，无须再进行校验
            //do nothing
        } else {
            /*
             * 校验 logcollecttask 对应下游 topic 是否被限流
             */
            boolean topicLimitExists = checkTopicLimitExists(logCollectTaskDO.getKafkaClusterId(), logCollectTaskDO.getSendTopic());
            if (topicLimitExists) {//存在下游 topic 端被限流
                logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.TOPIC_LIMIT_EXISTS.getLogCollectTaskHealthLevelEnum();
                logCollectTaskHealthDescription = String.format(
                        "%s:LogCollectTaskId={%d}, kafkaClusterId={%d}, sendTopic={%s}",
                        LogCollectTaskHealthInspectionResultEnum.TOPIC_LIMIT_EXISTS.getDescription(),
                        logCollectTaskDO.getId(),
                        logCollectTaskDO.getKafkaClusterId(),
                        logCollectTaskDO.getSendTopic()
                );
            }
        }

        /*
         * 校验 "时间范围日志采集任务" 是否已执行完成，scopeCollectCompleteLogPathCount 是否 等于 fileLogCollectPathDOList.size()，如是，表示 logCollectTaskDO 已执行完成，记录对应任务执行结束时间，并修改对应状态为 "已完成"
         */
        if (scopeCollectCompleteLogPathCount.equals(fileLogCollectPathDOList.size())) {
            LogCollectTaskPO logCollectTaskPO = logCollectorTaskDAO.selectByPrimaryKey(logCollectTaskDO.getId());
            logCollectTaskPO.setLogCollectTaskFinishTime(new Date());
            logCollectTaskPO.setLogCollectTaskStatus(LogCollectTaskStatusEnum.FINISH.getCode());
            logCollectTaskPO.setOperator(CommonConstant.getOperator(null));
            logCollectorTaskDAO.updateByPrimaryKey(logCollectTaskPO);
        }

        /*
         * 持久化 logCollectTaskHealth 信息
         */
        logCollectTaskHealthDO.setLogCollectTaskHealthLevel(logCollectTaskHealthLevelEnum.getCode());
        logCollectTaskHealthDO.setLogCollectTaskHealthDescription(logCollectTaskHealthDescription);
        logCollectTaskHealthDO.setLastestCollectDqualityTimePerLogFilePathJsonString(JSON.toJSONString(logFilePathId2CollctCompleteTimeMap));
        logCollectTaskHealthDO.setLastestAbnormalTruncationCheckHealthyTimePerLogFilePath(JSON.toJSONString(fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap));
        logCollectTaskHealthDO.setLastestLogSliceCheckHealthyTimePerLogFilePath(JSON.toJSONString(fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap));
        logCollectTaskHealthDO.setLastestFileDisorderCheckHealthyTimePerLogFilePath(JSON.toJSONString(fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap));
        logCollectTaskHealthDO.setLastestFilePathExistsCheckHealthyTimePerLogFilePath(JSON.toJSONString(fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap));
        logCollectTaskHealthManageService.updateLogCollectorTaskHealth(logCollectTaskHealthDO, CommonConstant.getOperator(null));
        return logCollectTaskHealthLevelEnum;
    }

    @Override
    public List<LogCollectTaskDO> getAllLogCollectTask2HealthCheck() {
        List<LogCollectTaskPO> logCollectTaskPOList = logCollectorTaskDAO.getByStatus(LogCollectTaskStatusEnum.RUNNING.getCode());
        List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageServiceExtension.logCollectTaskPOList2LogCollectTaskDOList(logCollectTaskPOList);
        for (LogCollectTaskDO logCollectTaskDO : logCollectTaskDOList) {
            logCollectTaskDO.setDirectoryLogCollectPathList(directoryLogCollectPathManageService.getAllDirectoryLogCollectPathByLogCollectTaskId(logCollectTaskDO.getId()));
            logCollectTaskDO.setFileLogCollectPathList(fileLogCollectPathManageService.getAllFileLogCollectPathByLogCollectTaskId(logCollectTaskDO.getId()));
        }
        return logCollectTaskDOList;
    }

    @Override
    public List<LogCollectTaskDO> getLogCollectTaskListByHost(HostDO hostDO) {
        List<LogCollectTaskPO> logCollectTaskPOList = logCollectorTaskDAO.getLogCollectTaskListByHostId(hostDO.getId());
        if (CollectionUtils.isEmpty(logCollectTaskPOList)) {
            return new ArrayList<>();
        }
        String logMountPath = "";//k8s 容器内路径
        String logHostPath = "";//k8s 主机路径
        if (hostDO.getContainer().equals(HostTypeEnum.CONTAINER.getCode())) {
            K8sPodDO k8sPodDO = k8sPodManageService.getByContainerId(hostDO.getId());
            logMountPath = k8sPodDO.getLogMountPath();
            logHostPath = k8sPodDO.getLogHostPath();
        }
        List<LogCollectTaskDO> logCollectTaskList = new ArrayList<>(logCollectTaskPOList.size());
        for (LogCollectTaskPO logCollectTaskPO : logCollectTaskPOList) {
            LogCollectTaskDO logCollectTaskDO = logCollectTaskManageServiceExtension.logCollectTaskPO2LogCollectTaskDO(logCollectTaskPO);
            if (agentCollectConfigManageService.need2Deploy(logCollectTaskDO, hostDO)) {
                //根据日志采集任务id获取其关联的日志采集任务路径对象集
                List<FileLogCollectPathDO> fileLogCollectPathDOList = fileLogCollectPathManageService.getAllFileLogCollectPathByLogCollectTaskId(logCollectTaskDO.getId());
                /*
                 * 对于容器日志，进行日志路径转化 映射
                 */
                if (hostDO.getContainer().equals(HostTypeEnum.CONTAINER.getCode())) {
                    for (FileLogCollectPathDO fileLogCollectPathDO : fileLogCollectPathDOList) {
                        String path = fileLogCollectPathDO.getPath();
                        String realPath = K8sUtil.getRealPath(logMountPath, logHostPath, path);
                        fileLogCollectPathDO.setRealPath(realPath);
                    }
                }
                logCollectTaskDO.setFileLogCollectPathList(fileLogCollectPathDOList);
                logCollectTaskList.add(logCollectTaskDO);
            }
        }
        return logCollectTaskList;
    }

    @Override
    public List<LogCollectTaskDO> getLogCollectTaskListByServiceId(Long serviceId) {
        List<LogCollectTaskPO> logCollectTaskPOList = logCollectorTaskDAO.getLogCollectTaskListByServiceId(serviceId);
        return logCollectTaskPOList2LogCollectTaskDOListAndLoadRelationLogCollectPath(logCollectTaskPOList);
    }

    /**
     * @return 获取日志采集任务健康度检查处理器链
     */
    private ProcessorChain getLogCollectTaskHealthCheckProcessorChain() {
        ProcessorChain processorChain = new ProcessorChain();
        for(Class<Processor> clazz : GlobalProperties.LOG_COLLECT_TASK_HEALTH_CHECK_PROCESSOR_CLASS_LIST) {
            try {
                processorChain.addProcessor(clazz.newInstance());
            } catch (Exception ex) {
                throw new ServiceException(
                        String.format("%s invoke newInstance() failed, cause by: %s", clazz.getName(), ex.getMessage()),
                        ex,
                        ErrorCodeEnum.REFLECTION_NEW_INSTANCE_EXCEPTION.getCode()
                );
            }
        }
        return processorChain;
    }

    /**
     * 诊断给定流式日志采集任务
     *
     * @param logCollectTaskDO  待诊断日志采集任务对象
     * @param checkCollectDelay 是否校验日志采集任务采集延时 true：校验 false：不校验
     * @return 待诊断日志采集任务对象对应诊断结果
     */
    private LogCollectTaskHealthLevelEnum handleCheckNormalLogCollectTaskHealth(LogCollectTaskDO logCollectTaskDO, boolean checkCollectDelay) throws ServiceException {
        /*
         * 获取待检测日志采集任务对应健康记录
         */
        LogCollectTaskHealthDO logCollectTaskHealthDO = logCollectTaskHealthManageService.getByLogCollectTaskId(logCollectTaskDO.getId());
        if (null == logCollectTaskHealthDO) {
            throw new ServiceException(String.format("LogCollectTaskHealth={logCollectTaskId=%d}在系统中不存在", logCollectTaskDO.getId()), ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode());
        }
        /*
         * 获取日式采集任务须部署的主机列表
         */
        List<HostDO> hostDOList = hostManageService.getHostListByLogCollectTaskId(logCollectTaskDO.getId());//部署日志采集任务的主机对象集
        /*
         * 获取日志采集任务须采集的文件路径集
         */
        List<FileLogCollectPathDO> fileLogCollectPathDOList = logCollectTaskDO.getFileLogCollectPathList();//日志采集任务对应各采集路径
        /*
         * 进入巡检流程
         */
        LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum = LogCollectTaskHealthLevelEnum.GREEN;//日志采集任务健康度检查结果
        String logCollectTaskHealthDescription = "";//日志采集任务健康检查描述
        Long minCurrentCollectTime = Long.MAX_VALUE;//记录logCollectTaskDO对应各LogCollectPath最小采集时间，记为采集完整性时间
        Map<Long, Long> logFilePathId2CollctCompleteTimeMap = new HashMap<>(); //存储各fileLogPath对应采集完整性时间 map，key:logPathId value:collctCompleteTime
        Long logCollectTaskHealthCheckTimeEnd = System.currentTimeMillis() - 1000; //日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一秒
        //存储各健康值对应时间点
        Map<Long, Long> fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap = JSON.parseObject(logCollectTaskHealthDO.getLastestAbnormalTruncationCheckHealthyTimePerLogFilePath(), new TypeReference<Map<Long, Long>>() {
        });
        Map<Long, Long> fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap = JSON.parseObject(logCollectTaskHealthDO.getLastestLogSliceCheckHealthyTimePerLogFilePath(), new TypeReference<Map<Long, Long>>() {
        });
        Map<Long, Long> fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap = JSON.parseObject(logCollectTaskHealthDO.getLastestFileDisorderCheckHealthyTimePerLogFilePath(), new TypeReference<Map<Long, Long>>() {
        });
        Map<Long, Long> fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap = JSON.parseObject(logCollectTaskHealthDO.getLastestFilePathExistsCheckHealthyTimePerLogFilePath(), new TypeReference<Map<Long, Long>>() {
        });
        if (CollectionUtils.isNotEmpty(fileLogCollectPathDOList)) {
            for (FileLogCollectPathDO fileLogCollectPathDO : fileLogCollectPathDOList) {
                minCurrentCollectTime = Long.MAX_VALUE;
                Integer abnormalTruncationCheckHealthyCount = 0;
                Integer fileDisorderCheckHealthyCount = 0;
                Integer filePathExistsCheckHealthyCount = 0;
                Integer logSliceCheckHealthyCount = 0;
                for (HostDO hostDO : hostDOList) {

                    //TODO：@ 徐光
                    ProcessorChain processorChain = getLogCollectTaskHealthCheckProcessorChain();
                    /*
                     * 计算logpath对应完整性时间
                     */
                    minCurrentCollectTime = Math.min(minCurrentCollectTime, getCurrentCompleteTime(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName()));

                    /************************ mark red cases ************************/

                    /*
                     * 校验日志采集任务健康度
                     */
                    if (null != logCollectTaskHealthLevelEnum && (logCollectTaskHealthLevelEnum.getCode().equals(LogCollectTaskHealthLevelEnum.RED.getCode()))) {
                        //表示健康度已被检测为 red，此时，无须再进行校验
                        continue;
                    }

                    /*
                     * 校验在距当前时间的心跳存活判定周期内，logCollectTaskId+fileLogCollectPathId+hostName是否存在心跳
                     */
                    boolean alive = checkAliveByHeartbeat(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd);
                    if (!alive) {//不存活
                        logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_IN_HOST_HEART_BEAT_NOT_EXISTS.getLogCollectTaskHealthLevelEnum();
                        logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_IN_HOST_HEART_BEAT_NOT_EXISTS.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                    }
                    /*
                     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在
                     */
                    boolean filePathExists = checkFilePathExists(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd, fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap);
                    if (!filePathExists) {//不存在
                        logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_NOT_EXISTS.getLogCollectTaskHealthLevelEnum();
                        logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_NOT_EXISTS.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                    } else {
                        filePathExistsCheckHealthyCount++;
                    }
                    /*
                     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在乱序
                     */
                    boolean fileDisorder = checkFileDisorder(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd, fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap);
                    if (fileDisorder) {//存在 乱序
                        logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_DISORDER.getLogCollectTaskHealthLevelEnum();
                        logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_DISORDER.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                    } else {
                        fileDisorderCheckHealthyCount++;
                    }
                    /*
                     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在日志切片配置错误
                     */
                    boolean errorLogsExists = checkLogSliceErrorExists(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd, fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap);
                    if (errorLogsExists) {//存在 日志切片配置错误
                        logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SLICE_ERROR_EXISTS.getLogCollectTaskHealthLevelEnum();
                        logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SLICE_ERROR_EXISTS.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                    } else {
                        logSliceCheckHealthyCount++;
                    }
                    /*
                     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在日志被异常截断
                     */
                    boolean abnormalTruncationExists = checkAbnormalTruncationExists(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd, fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap);
                    if (abnormalTruncationExists) {//存在 异常截断
                        logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SIZE_OVERRUN_TRUNCATE_EXISTS.getLogCollectTaskHealthLevelEnum();
                        logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SIZE_OVERRUN_TRUNCATE_EXISTS.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                    } else {
                        abnormalTruncationCheckHealthyCount++;
                    }
                    /*
                     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在多 agent 并发采集
                     */
                    boolean concurrentCollectExists = checkConcurrentCollectExists(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd);
                    if (concurrentCollectExists) {
                        logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_CONCURRENT_COLLECT.getLogCollectTaskHealthLevelEnum();
                        logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_CONCURRENT_COLLECT.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                    }
                    /*
                     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在采集延迟
                     */
                    if (checkCollectDelay && null != logCollectTaskDO.getCollectDelayThresholdMs() && logCollectTaskDO.getCollectDelayThresholdMs() > 0) {//该文件型日志采集路径须做采集延迟监控
                        boolean collectDelay = checkCollectDelay(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskDO.getCollectDelayThresholdMs());
                        if (collectDelay) {//存在 采集延迟
                            logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_COLLECT_DELAYED.getLogCollectTaskHealthLevelEnum();
                            logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_COLLECT_DELAYED.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                        }
                    }

                    /************************ mark yellow cases ************************/

                    /*
                     * 校验日志采集任务健康度
                     */
                    if (null != logCollectTaskHealthLevelEnum && (logCollectTaskHealthLevelEnum.getCode().equals(LogCollectTaskHealthLevelEnum.RED.getCode()) || logCollectTaskHealthLevelEnum.getCode().equals(LogCollectTaskHealthLevelEnum.YELLOW.getCode()))) {
                        //表示健康度已被检测为 red or yellow，此时，无须再进行校验
                        continue;
                    }

                    /*
                     * 校验 logcollecttask + logpath 在 host 端是否存在采集端出口限流
                     */
                    boolean byteLimitOnHostExists = checkByteLimitOnHostExists(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                    if (byteLimitOnHostExists) {//存在采集端出口流量阈值限流
                        logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.HOST_BYTES_LIMIT_EXISTS.getLogCollectTaskHealthLevelEnum();
                        logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.HOST_BYTES_LIMIT_EXISTS.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                    }
                }
                /*
                 * 设置各 filePathId 对应采集完整性时间
                 */
                logFilePathId2CollctCompleteTimeMap.put(fileLogCollectPathDO.getId(), minCurrentCollectTime);
                /*
                 * 设置各filePathId对应各指标健康时时间，以便下次进行巡检
                 */
                if (abnormalTruncationCheckHealthyCount.equals(hostDOList.size())) {
                    fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap.put(fileLogCollectPathDO.getId(), logCollectTaskHealthCheckTimeEnd);
                }
                if (logSliceCheckHealthyCount.equals(hostDOList.size())) {
                    fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap.put(fileLogCollectPathDO.getId(), logCollectTaskHealthCheckTimeEnd);
                }
                if (fileDisorderCheckHealthyCount.equals(hostDOList.size())) {
                    fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap.put(fileLogCollectPathDO.getId(), logCollectTaskHealthCheckTimeEnd);
                }
                if (filePathExistsCheckHealthyCount.equals(hostDOList.size())) {
                    fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap.put(fileLogCollectPathDO.getId(), logCollectTaskHealthCheckTimeEnd);
                }
            }
        }

        if (null != logCollectTaskHealthLevelEnum && (logCollectTaskHealthLevelEnum.getCode().equals(LogCollectTaskHealthLevelEnum.RED.getCode()) || logCollectTaskHealthLevelEnum.getCode().equals(LogCollectTaskHealthLevelEnum.YELLOW.getCode()))) {
            //表示健康度已被检测为 red or yellow，此时，无须再进行校验
            //do nothing
        } else {
            /*
             * 校验 logcollecttask 对应下游 topic 是否被限流
             */
            boolean topicLimitExists = checkTopicLimitExists(logCollectTaskDO.getKafkaClusterId(), logCollectTaskDO.getSendTopic());
            if (topicLimitExists) {//存在下游 topic 端被限流
                logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.TOPIC_LIMIT_EXISTS.getLogCollectTaskHealthLevelEnum();
                logCollectTaskHealthDescription = String.format(
                        "%s:LogCollectTaskId={%d}, kafkaClusterId={%d}, sendTopic={%s}",
                        LogCollectTaskHealthInspectionResultEnum.TOPIC_LIMIT_EXISTS.getDescription(),
                        logCollectTaskDO.getId(),
                        logCollectTaskDO.getKafkaClusterId(),
                        logCollectTaskDO.getSendTopic()
                );
            }
            /*
             * 校验 logcollecttask 是否未关联主机
             */
            boolean notRelateAnyHost = checkNotRelateAnyHost(logCollectTaskDO.getId());
            if (notRelateAnyHost) {//logcollecttask 未关联主机
                logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.NOT_RELATE_ANY_HOST.getLogCollectTaskHealthLevelEnum();
                logCollectTaskHealthDescription = String.format(
                        "%s:LogCollectTaskId={%d}",
                        LogCollectTaskHealthInspectionResultEnum.NOT_RELATE_ANY_HOST.getDescription(),
                        logCollectTaskDO.getId()
                );
            }
        }

        LOGGER.info(logCollectTaskHealthDescription);

        /*
         * 持久化 logCollectTaskHealth 信息
         */
        logCollectTaskHealthDO.setLogCollectTaskHealthLevel(logCollectTaskHealthLevelEnum.getCode());
        logCollectTaskHealthDO.setLogCollectTaskHealthDescription(logCollectTaskHealthDescription);
        logCollectTaskHealthDO.setLastestCollectDqualityTimePerLogFilePathJsonString(JSON.toJSONString(logFilePathId2CollctCompleteTimeMap));
        logCollectTaskHealthDO.setLastestAbnormalTruncationCheckHealthyTimePerLogFilePath(JSON.toJSONString(fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap));
        logCollectTaskHealthDO.setLastestLogSliceCheckHealthyTimePerLogFilePath(JSON.toJSONString(fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap));
        logCollectTaskHealthDO.setLastestFileDisorderCheckHealthyTimePerLogFilePath(JSON.toJSONString(fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap));
        logCollectTaskHealthDO.setLastestFilePathExistsCheckHealthyTimePerLogFilePath(JSON.toJSONString(fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap));
        logCollectTaskHealthManageService.updateLogCollectorTaskHealth(logCollectTaskHealthDO, CommonConstant.getOperator(null));
        return logCollectTaskHealthLevelEnum;

    }

    /**
     * 校验 时间范围采集类型日志采集任务是否已执行完
     *
     * @param logCollectTaskId     日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param hostDO               主机对象
     * @return 返回时间范围采集类型日志采集任务是否已执行完 true：已采集完 false：未采集完
     */
    private boolean checkTimeScopeCollectComplete(Long logCollectTaskId, Long fileLogCollectPathId, HostDO hostDO) {
        /*
         * 校验 logCollectTaskId + fileLogCollectPathId + hostName 近5分钟是否心跳正常 & 采集数量为 0
         */
        if (hostDO.getContainer().equals(HostTypeEnum.HOST.getCode())) {
            return agentMetricsManageService.hostCompleteCollect(hostDO.getHostName(), logCollectTaskId, fileLogCollectPathId);
        } else if (hostDO.getContainer().equals(HostTypeEnum.CONTAINER.getCode())) {
            return agentMetricsManageService.containerCompleteCollect(hostDO.getHostName(), hostDO.getParentHostName(), logCollectTaskId, fileLogCollectPathId);
        } else {
            throw new ServiceException(
                    String.format("Host={id=%d}未知主机类型={%d}", hostDO.getId(), hostDO.getContainer()),
                    ErrorCodeEnum.UNKNOWN_HOST_TYPE.getCode()
            );
        }
    }

    /**
     * 校验 logcollecttask 对应下游 topic 是否被限流
     *
     * @param kafkaClusterId 日志采集任务对应下游 kafkaCLuster id
     * @param sendTopic      日志采集任务对应下游 topic
     * @return true：限流 false：非限流
     */
    private boolean checkTopicLimitExists(Long kafkaClusterId, String sendTopic) {
        return kafkaClusterManageService.checkTopicLimitExists(kafkaClusterId, sendTopic);
    }

    public boolean checkNotRelateAnyHost(Long logCollectTaskId) {
        List<HostDO> hostDOList = hostManageService.getHostListByLogCollectTaskId(logCollectTaskId);
        return !CollectionUtils.isNotEmpty(hostDOList);
    }

    @Override
    public List<LogCollectTaskDO> getByHealthLevel(Integer logCollectTaskHealthLevelCode) {
        List<LogCollectTaskPO> logCollectTaskPOList = logCollectorTaskDAO.getLogCollectTaskListByHealthLevel(logCollectTaskHealthLevelCode);
        return logCollectTaskManageServiceExtension.logCollectTaskPOList2LogCollectTaskDOList(logCollectTaskPOList);
    }

    /**
     * 校验 logcollecttask + logpath 在 host 端是否存在采集端出口流量阈值限流
     *
     * @param logCollectTaskId     日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param hostName             主机名
     * @return
     */
    private boolean checkByteLimitOnHostExists(Long logCollectTaskId, Long fileLogCollectPathId, String hostName) {
        /*
         * 获取近 LogCollectTaskHealthCheckConstant.HOST_BYTE_LIMIT_CHECK_LASTEST_MS_THRESHOLD 时间范围内 logCollectTaskId+fileLogCollectPathId+hostName 指标集中，
         * 总限流时间是否超过阈值 LogCollectTaskHealthCheckConstant.HOST_BYTE_LIMIT_MS_THRESHOLD
         */
        Long startTime = System.currentTimeMillis() - LogCollectTaskHealthCheckConstant.HOST_BYTE_LIMIT_CHECK_LASTEST_MS_THRESHOLD;
        Long endTime = System.currentTimeMillis();
        Long hostCpuLimiDturationMs = agentMetricsManageService.getHostByteLimitDurationByTimeFrame(
                startTime,
                endTime,
                logCollectTaskId,
                fileLogCollectPathId,
                hostName
        );//主机cpu限流时长 单位：ms
        return hostCpuLimiDturationMs > LogCollectTaskHealthCheckConstant.HOST_BYTE_LIMIT_MS_THRESHOLD;
    }

    /**
     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在执行超时情况
     *
     * @param logCollectTaskDO               日志采集任务对象
     * @param fileLogCollectPathId           日志采集路径 id
     * @param hostName                       主机名
     * @param logCollectTaskExecuteTimeoutMs 超时阈值 单位：ms
     * @return 返回 logCollectTaskId+fileLogCollectPathId在host上是否存在执行超时情况 true：存在 执行超时 false：不存在 执行超时
     */
    private boolean checkTimeScopeLogCollectTaskExecuteTimeout(LogCollectTaskDO logCollectTaskDO, Long fileLogCollectPathId, String hostName, Long logCollectTaskExecuteTimeoutMs) {
        /*
         * 根据 logCollectTaskId 获取对应日志采集任务对象，并获取其开始执行时间 executeStartTime
         */
        Long executeStartTime = logCollectTaskDO.getCreateTime().getTime();
        /*
         * 获取logCollectTaskId+fileLogCollectPathId在host上对应当前采集时间 currentCollectTime
         */
        Long currentCollectTime = getCurrentCompleteTime(logCollectTaskDO.getId(), fileLogCollectPathId, hostName);
        /*
         * currentCollectTime - executeStartTime > logCollectTaskExecuteTimeoutMs ? true : false
         */
        return currentCollectTime - executeStartTime > logCollectTaskExecuteTimeoutMs;
    }

    /**
     * 校验 logCollectTaskId+fileLogCollectPathId 在host上是否存在采集延迟
     *
     * @param logCollectTaskId        日志采集任务 id
     * @param fileLogCollectPathId    日志采集路径 id
     * @param hostName                主机名
     * @param collectDelayThresholdMs 采集延时阈值
     * @return 返回 logCollectTaskId+fileLogCollectPathId 在host上是否存在采集延迟 true：存在 采集延时 false：不存在 采集延时
     */
    private boolean checkCollectDelay(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long collectDelayThresholdMs) {
        /*
         * 获取logCollectTaskId+fileLogCollectPathId在host上对应当前采集时间 currentCollectTime
         */
        Long currentCollectTime = getCurrentCompleteTime(logCollectTaskId, fileLogCollectPathId, hostName);
        /*
         * System.currentTimeMillis() - currentCollectTime > collectDelayThresholdMs ? true : false
         */
        return System.currentTimeMillis() - currentCollectTime > collectDelayThresholdMs;
    }

    /**
     * 校验 logCollectTaskId+fileLogCollectPathId 在host上是否存在多 agent 并发采集
     *
     * @param logCollectTaskId                 日志采集任务 id
     * @param fileLogCollectPathId             日志采集路径 id
     * @param hostName                         主机名
     * @param logCollectTaskHealthCheckTimeEnd 日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一毫秒
     * @return 返回 logCollectTaskId+fileLogCollectPathId 在host上是否存在多 agent 并发采集 true：存在 并发采集 false：不存在 并发采集
     */
    private boolean checkConcurrentCollectExists(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long logCollectTaskHealthCheckTimeEnd) {
        /*
         * 获取近 LogCollectTaskHealthCheckConstant.HOST_CPU_LIMIT_CHECK_LASTEST_MS_THRESHOLD 时间范围内 logCollectTaskId + fileLogCollectPathId + hostName 心跳数，
         * 心跳数量 > LogCollectTaskHealthCheckConstant.HOST_CPU_LIMIT_CHECK_LASTEST_MS_THRESHOLD，表示 logCollectTaskId+fileLogCollectPathId 在 host 上是否存在多 agent 并发采集
         */
        Long startTime = logCollectTaskHealthCheckTimeEnd - LogCollectTaskHealthCheckConstant.CONCURRENT_COLLECT_CHECK_LASTEST_MS_THRESHOLD;
        Long heartbeatTimes = agentMetricsManageService.getHeartbeatTimesByTimeFrame(
                startTime,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                hostName
        );
        return heartbeatTimes > LogCollectTaskHealthCheckConstant.CONCURRENT_COLLECT_CHECK_HEARTBEAT_TIMES_THRESHOLD;
    }

    /**
     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在日志被异常截断
     *
     * @param logCollectTaskId                                                  日志采集任务 id
     * @param fileLogCollectPathId                                              日志采集路径 id
     * @param hostName                                                          主机名
     * @param logCollectTaskHealthCheckTimeEnd                                  日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一毫秒
     * @param fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap filePathId : LastestAbnormalTruncationCheckHealthyTime
     * @return 返回logCollectTaskId+fileLogCollectPathId在host上是否存在日志被异常截断 true：存在 异常截断 false：不存在 异常截断
     */
    private boolean checkAbnormalTruncationExists(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long logCollectTaskHealthCheckTimeEnd, Map<Long, Long> fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap) {
        /*
         * 获取自上次"异常截断"健康点 ~ 当前时间，logCollectTaskId+fileLogCollectPathId在host上是否存在异常截断
         */
        Long lastestCheckTime = fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap.get(fileLogCollectPathId);
        if (null == lastestCheckTime) {
            throw new ServiceException(
                    String.format("FileLogCollectPath={id=%d}对应AbnormalTruncationExistsCheckHealthyTime不存在", fileLogCollectPathId),
                    ErrorCodeEnum.LOGCOLLECTTASK_HEALTH_ABNORMAL_TRUNCATION_EXISTS_CHECK_HEALTHY_TIME_NOT_EXISTS.getCode()
            );
        }
        Integer abnormalTruncationCount = agentMetricsManageService.getAbnormalTruncationCountByTimeFrame(
                lastestCheckTime,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                hostName
        );
        return abnormalTruncationCount > 0;
    }

    /**
     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在日志切片错误
     *
     * @param logCollectTaskId                                        日志采集任务 id
     * @param fileLogCollectPathId                                    日志采集路径 id
     * @param hostName                                                主机名
     * @param logCollectTaskHealthCheckTimeEnd                        日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一毫秒
     * @param fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap filePathId : lastestLogSliceCheckHealthyTimeMap
     * @return 返回logCollectTaskId+fileLogCollectPathId在host上是否存在日志切片错误
     */
    private boolean checkLogSliceErrorExists(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long logCollectTaskHealthCheckTimeEnd, Map<Long, Long> fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap) {
        /*
         * 获取自上次"错误日志输出存在"健康点 ~ 当前时间，logCollectTaskId+fileLogCollectPathId在host上是否存在日志切片错误
         */
        Long lastestCheckTime = fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap.get(fileLogCollectPathId);
        if (null == lastestCheckTime) {
            throw new ServiceException(
                    String.format("FileLogCollectPath={id=%d}对应LogSliceCheckHealthyTime不存在", fileLogCollectPathId),
                    ErrorCodeEnum.LOGCOLLECTTASK_HEALTH_LOG_SLICE_CHECK_HEALTHY_TIME_NOT_EXISTS.getCode()
            );
        }
        Integer logSliceErrorCount = agentMetricsManageService.getSliceErrorCount(
                lastestCheckTime,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                hostName
        );
        return logSliceErrorCount > 0;
    }

    /**
     * 校验 logCollectTaskId+fileLogCollectPathId 在 host 上是否存在乱序
     *
     * @param logCollectTaskId                                            日志采集任务 id
     * @param fileLogCollectPathId                                        日志采集路径 id
     * @param hostName                                                    主机名
     * @param logCollectTaskHealthCheckTimeEnd                            日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一毫秒
     * @param fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap filePathId : LastestFileDisorderCheckHealthyTime
     * @return logCollectTaskId+fileLogCollectPathId 在 host 上是否存在乱序 true：存在 乱序 false：不存在 乱序
     */
    private boolean checkFileDisorder(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long logCollectTaskHealthCheckTimeEnd, Map<Long, Long> fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap) {
        /*
         * 获取自上次"文件乱序"健康点 ~ 当前时间，logCollectTaskId+fileLogCollectPathId在host上是否存在日志乱序
         */
        Long lastestCheckTime = fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap.get(fileLogCollectPathId);
        if (null == lastestCheckTime) {
            throw new ServiceException(
                    String.format("FileLogCollectPath={id=%d}对应FileDisorderCheckHealthyTime不存在", fileLogCollectPathId),
                    ErrorCodeEnum.LOGCOLLECTTASK_HEALTH_FILE_DISORDER_CHECK_HEALTHY_TIME_NOT_EXISTS.getCode()
            );
        }
        Integer fileDisorderCount = agentMetricsManageService.getFileDisorderCountByTimeFrame(
                lastestCheckTime,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                hostName
        );
        return fileDisorderCount > 0;
    }

    /**
     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在
     *
     * @param logCollectTaskId                                              日志采集任务 id
     * @param fileLogCollectPathId                                          日志采集路径 id
     * @param hostName                                                      主机名
     * @param logCollectTaskHealthCheckTimeEnd                              日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一秒
     * @param fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap filePathId : LastestFilePathExistsCheckHealthyTime
     * @return 返回logCollectTaskId+fileLogCollectPathId在host上是否存在 true：存在 false：不存在
     */
    private boolean checkFilePathExists(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long logCollectTaskHealthCheckTimeEnd, Map<Long, Long> fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap) {
        /*
         * 获取自上次"日志采集路径存在"健康点 ~ 当前时间，logCollectTaskId+fileLogCollectPathId在host上是否存在对应待采集日志文件
         */
        Long lastestCheckTime = fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap.get(fileLogCollectPathId);
        if (null == lastestCheckTime) {
            throw new ServiceException(
                    String.format("FileLogCollectPath={id=%d}对应FilePathExistsCheckHealthyTime不存在", fileLogCollectPathId),
                    ErrorCodeEnum.LOGCOLLECTTASK_HEALTH_FILE_PATH_EXISTS_CHECK_HEALTHY_TIME_NOT_EXISTS.getCode()
            );
        }
        Integer filePathNotExistsCount = agentMetricsManageService.getFilePathNotExistsCountByTimeFrame(
                lastestCheckTime,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                hostName
        );
        return filePathNotExistsCount == 0;
    }

    /**
     * 校验在距当前时间的心跳存活判定周期内，logCollectTaskId+fileLogCollectPathId+hostName是否存在心跳
     *
     * @param logCollectTaskId                 日志采集任务 id
     * @param fileLogCollectPathId             日志采集路径 id
     * @param logCollectTaskHostName           日志采集任务对应主机名
     * @param logCollectTaskHealthCheckTimeEnd 日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一秒
     * @return 距当前时间的心跳存活判定周期内，logCollectTaskId+fileLogCollectPathId+hostName是否存在心跳 true：存在 心跳 false：不存在心跳
     */
    private boolean checkAliveByHeartbeat(Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName, Long logCollectTaskHealthCheckTimeEnd) {
        /*
         * 获取近 LogCollectTaskHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD 时间范围内 logCollectTaskId + fileLogCollectPathId + hostName 心跳数，
         * 心跳数量 == 0，表示 logCollectTaskId+fileLogCollectPathId 在 host 上不存在心跳
         */
        Long heartbeatTimes = agentMetricsManageService.getHeartbeatTimesByTimeFrame(
                logCollectTaskHealthCheckTimeEnd - LogCollectTaskHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                logCollectTaskHostName
        );
        return !heartbeatTimes.equals(0L);
    }

    /**
     * 获取logCollectTaskId+fileLogCollectPathId+hostName当前采集时间
     *
     * @param logCollectTaskId     日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param hostName             主机名
     * @return 返回logCollectTaskId+fileLogCollectPathId+hostName当前采集时间
     */
    private Long getCurrentCompleteTime(Long logCollectTaskId, Long fileLogCollectPathId, String hostName) {
        return agentMetricsManageService.getLastestCollectTime(logCollectTaskId, fileLogCollectPathId, hostName);
    }

    /**
     * 校验给定日志采集任务是否需要进行诊断
     *
     * @param logCollectTaskDO 待校验日志采集任务对象
     * @return 返回给定日志采集任务是否需要进行诊断 true：需要 false：不需要
     */
    private CheckResult logCollectTaskNeedCheck(LogCollectTaskDO logCollectTaskDO) throws ServiceException {
        //TODO：后续添加日志采集任务黑名单功能后，须添加黑名单过滤规则
        Integer logCollectTaskType = logCollectTaskDO.getLogCollectTaskType();
        if (LogCollectTaskTypeEnum.NORMAL_COLLECT.getCode().equals(logCollectTaskType)) {//流采
            if (logCollectTaskDO.getLogCollectTaskStatus().equals(LogCollectTaskStatusEnum.STOP.getCode())) {//待校验日志采集任务处于"停止"状态
                return new CheckResult(false);
            }
        } else if (LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode().equals(logCollectTaskType)) {//时间范围采集
            if (logCollectTaskDO.getLogCollectTaskStatus().equals(LogCollectTaskStatusEnum.FINISH.getCode())) {//待校验日志采集任务处于"已完成"状态
                return new CheckResult(false);
            }
        } else {
            throw new ServiceException(
                    String.format("待校验日志采集任务对象={%s}的logCollectTaskType属性值={%d}不合法，合法值范围见枚举类LogCollectTaskTypeEnum定义", JSON.toJSONString(logCollectTaskDO), logCollectTaskType),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        return new CheckResult(true);
    }

    @Override
    public List<LogCollectTaskDO> getLogCollectTaskListByAgentId(Long agentId) {
        List<HostDO> collectHostDOList = hostManageService.getRelationHostListByAgentId(agentId);//agent 待采集 host 集
        List<LogCollectTaskDO> result = new ArrayList<>();
        /*
         * 获取各host对应日志采集任务
         */
        for (HostDO hostDO : collectHostDOList) {
            List<LogCollectTaskDO> LogCollectTaskDOList = getLogCollectTaskListByHost(hostDO);
            if (CollectionUtils.isNotEmpty(LogCollectTaskDOList)) {
                result.addAll(LogCollectTaskDOList);
            }
        }
        return result;
    }

    @Override
    public List<LogCollectTaskDO> getLogCollectTaskListByAgentHostName(String agentHostName) {
        /*
         * 根据 hostName 获取其对应 agent
         */
        AgentDO agentDO = agentManageService.getAgentByHostName(agentHostName);
        if (null == agentDO) {
            return new ArrayList<>();
        }
        /*
         * 获取 agent 关联的日志采集任务集
         */
        List<LogCollectTaskDO> logCollectTaskDOList = getLogCollectTaskListByAgentId(agentDO.getId());
        return logCollectTaskDOList;
    }

    @Override
    public List<LogCollectTaskDO> getLogCollectTaskListByKafkaClusterId(Long kafkaClusterId) {
        List<LogCollectTaskPO> logCollectTaskPOList = logCollectorTaskDAO.getLogCollectTaskListByKafkaClusterId(kafkaClusterId);
        return logCollectTaskPOList2LogCollectTaskDOListAndLoadRelationLogCollectPath(logCollectTaskPOList);
    }

    @Override
    public Long countAll() {
        return logCollectorTaskDAO.countAll();
    }

    @Override
    public List<Long> getAllIds() {
        return logCollectorTaskDAO.getAllIds();
    }

    /**
     * 将给定LogCollectTaskPO对象集转化为LogCollectTaskDO对象集，并在转化过程中加载各LogCollectTaskDO对象所关联的LogCollectPath对象集
     * 注：加载将会导致两次db查询
     *
     * @param logCollectTaskPOList 待转化LogCollectTaskPO对象集
     * @return 返回将给定LogCollectTaskPO对象集转化为的LogCollectTaskDO对象集，并在转化过程中加载各LogCollectTaskDO对象所关联的LogCollectPath对象集
     */
    private List<LogCollectTaskDO> logCollectTaskPOList2LogCollectTaskDOListAndLoadRelationLogCollectPath(List<LogCollectTaskPO> logCollectTaskPOList) {
        if (CollectionUtils.isEmpty(logCollectTaskPOList)) {
            return new ArrayList<>();
        }
        List<LogCollectTaskDO> logCollectTaskList = new ArrayList<>(logCollectTaskPOList.size());
        for (LogCollectTaskPO logCollectTaskPO : logCollectTaskPOList) {
            LogCollectTaskDO logCollectTaskDO = logCollectTaskManageServiceExtension.logCollectTaskPO2LogCollectTaskDO(logCollectTaskPO);
            //根据日志采集任务id获取其关联的日志采集任务路径对象集
            logCollectTaskDO.setDirectoryLogCollectPathList(directoryLogCollectPathManageService.getAllDirectoryLogCollectPathByLogCollectTaskId(logCollectTaskDO.getId()));
            logCollectTaskDO.setFileLogCollectPathList(fileLogCollectPathManageService.getAllFileLogCollectPathByLogCollectTaskId(logCollectTaskDO.getId()));
            logCollectTaskList.add(logCollectTaskDO);
        }
        return logCollectTaskList;
    }

    private MetricList handleAggregationQuery(MetricQueryDO metricQueryDO, String column, String function, int queryInterval) {
        MetricList metricList = new MetricList();
        List<MetricPointList> total = new ArrayList<>();
        if (metricQueryDO.getEachHost()) {
            List<HostDO> hostDOList = hostManageService.getHostListByLogCollectTaskId(metricQueryDO.getTaskId());
            for (HostDO hostDO : hostDOList) {
                metricQueryDO.setHostName(hostDO.getHostName());
                MetricPointList metricPointList = new MetricPointList();
                List<MetricPoint> list = agentMetricsManageService.queryAggregationByLogModel(metricQueryDO, column, function, queryInterval);
                metricPointList.setMetricPointList(list);
                metricPointList.setName(hostDO.getHostName());
                total.add(metricPointList);
            }
        } else {
            MetricPointList metricPointList = new MetricPointList();
            List<MetricPoint> list = agentMetricsManageService.queryAggregationByLogModel(metricQueryDO, column, function, queryInterval);
            metricPointList.setMetricPointList(list);
            metricPointList.setName(metricQueryDO.getHostName());
            total.add(metricPointList);
        }
        metricList.setMetricList(total);
        return metricList;
    }

}
