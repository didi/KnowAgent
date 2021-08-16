package com.didichuxing.datachannel.agentmanager.core.agent.metrics.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.TypeUtils;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.metrics.DashBoardStatisticsDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.AgentMetricQueryDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.MetricQueryDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.AgentMetricPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.CollectTaskMetricPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.AgentMetricField;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.CalcFunction;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPointList;
import com.didichuxing.datachannel.agentmanager.common.constant.AgentConstant;
import com.didichuxing.datachannel.agentmanager.common.constant.MetricConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.host.HostTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.AgentMetricsDAO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class AgentMetricsManageServiceImpl implements AgentMetricsManageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentMetricsManageServiceImpl.class);

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private AgentMetricsDAO agentMetricsDAO;

    private DashBoardStatisticsDOHeartbeatTimeComparator dashBoardStatisticsDOHeartbeatTimeComparator = new DashBoardStatisticsDOHeartbeatTimeComparator();

    private DashBoardStatisticsDOValueComparator dashBoardStatisticsDOValueComparator = new DashBoardStatisticsDOValueComparator();

    @Override
    public boolean completeCollect(HostDO hostDO) {
        /*
         *
         * 校验hostDO对应主机类型：
         *  主机：
         *      1.）获取该主机对应所有日志采集任务列表
         *      2.）针对日志采集任务列表中各日志采集任务，获取其待采集文件路径集，针对日志采集任务id+日志采集路径id+hostName，判断是是否已采集完
         *      3.）获取该主机关联的容器列表：
         *          针对各容器走如下 "容器" 判断逻辑
         *  容器：
         *      1.）获取该容器对应所有日志采集任务列表
         *      2.）针对日志采集任务列表中各日志采集任务，获取其待采集文件路径集，针对日志采集任务id+日志采集路径id+容器宿主机hostName+容器名，判断是是否已采集完
         *
         */
        if (HostTypeEnum.HOST.getCode().equals(hostDO.getContainer())) {//主机类型
            /*
             * 检查主机对应日志采集任务集是否已采集完
             */
            List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageService.getLogCollectTaskListByHostId(hostDO.getId());//主机关联的日志采集任务集
            for (LogCollectTaskDO logCollectTaskDO : logCollectTaskDOList) {
                List<FileLogCollectPathDO> logCollectPathDOList = logCollectTaskDO.getFileLogCollectPathList();
                for (FileLogCollectPathDO fileLogCollectPathDO : logCollectPathDOList) {
                    boolean hostCompleteCollect = hostCompleteCollect(hostDO.getHostName(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId());
                    if (!hostCompleteCollect) {//未完成 采集
                        return false;
                    }
                }
            }
            /*
             * 检查主机上运行的各容器对应日志采集任务集是否已采集完
             */
            List<HostDO> containerList = hostManageService.getContainerListByParentHostName(hostDO.getHostName());
            if (CollectionUtils.isEmpty(containerList)) {//主机未运行任何容器
                return true;
            }
            for (HostDO container : containerList) {
                List<LogCollectTaskDO> logCollectTaskDOListRelationContainer = logCollectTaskManageService.getLogCollectTaskListByHostId(hostDO.getId());
                for (LogCollectTaskDO logCollectTaskDO : logCollectTaskDOListRelationContainer) {
                    List<FileLogCollectPathDO> logCollectPathDOList = logCollectTaskDO.getFileLogCollectPathList();
                    for (FileLogCollectPathDO fileLogCollectPathDO : logCollectPathDOList) {
                        boolean containerCompleteCollect = containerCompleteCollect(container.getHostName(), hostDO.getHostName(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId());
                        if (!containerCompleteCollect) {//未完成 采集
                            return false;
                        }
                    }
                }
            }
            return true;
        } else if (HostTypeEnum.CONTAINER.getCode().equals(hostDO.getContainer())) {//容器类型
            List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageService.getLogCollectTaskListByHostId(hostDO.getId());//主机关联的日志采集任务集
            String parentHostName = hostDO.getParentHostName();//容器宿主机名
            for (LogCollectTaskDO logCollectTaskDO : logCollectTaskDOList) {
                List<FileLogCollectPathDO> logCollectPathDOList = logCollectTaskDO.getFileLogCollectPathList();
                for (FileLogCollectPathDO fileLogCollectPathDO : logCollectPathDOList) {
                    boolean containerCompleteCollect = containerCompleteCollect(hostDO.getHostName(), parentHostName, logCollectTaskDO.getId(), fileLogCollectPathDO.getId());
                    if (!containerCompleteCollect) {//未完成 采集
                        return false;
                    }
                }
            }
            return true;
        } else {
            throw new ServiceException(
                    String.format("获取主机={%s}关联的日志采集任务集失败，原因为：主机类型={%d}为系统未知主机类型", JSON.toJSONString(hostDO), hostDO.getContainer()),
                    ErrorCodeEnum.UNKNOWN_HOST_TYPE.getCode()
            );
        }
    }

    @Override
    public Long getHostByteLimitDurationByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
        return agentMetricsDAO.getHostByteLimitDuration(startTime, endTime, logModelHostName, logCollectTaskId, fileLogCollectPathId);
    }

    @Override
    public Long getHeartbeatTimesByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        return agentMetricsDAO.getHeartbeatTimesByTimeFrame(startTime, endTime, logCollectTaskId, fileLogCollectPathId, logCollectTaskHostName);
    }

    @Override
    public Long getLastestCollectTime(Long logCollectTaskId, Long fileLogCollectPathId, String hostName) {
        return agentMetricsDAO.getLatestCollectTime(logCollectTaskId, fileLogCollectPathId, hostName);
    }

    @Override
    public Integer getFilePathNotExistsCountByTimeFrame(Long logCollectTaskHealthLastestCheckTime, Long logCollectTaskHealthCheckTimeEnd, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskhostName) {
        return agentMetricsDAO.getFilePathNotExistsCountByTimeFrame(logCollectTaskHealthLastestCheckTime, logCollectTaskHealthCheckTimeEnd, logCollectTaskId, fileLogCollectPathId, logCollectTaskhostName);
    }

    @Override
    public Integer getFileDisorderCountByTimeFrame(Long logCollectTaskHealthLastestCheckTime, Long logCollectTaskHealthCheckTimeEnd, Long logCollectTaskId, Long fileLogCollectPathId, String hostName) {
        return agentMetricsDAO.getFileDisorderCount(logCollectTaskHealthLastestCheckTime, logCollectTaskHealthCheckTimeEnd, logCollectTaskId, fileLogCollectPathId, hostName);
    }

    @Override
    public Integer getSliceErrorCount(Long logCollectTaskHealthLastestCheckTime, Long logCollectTaskHealthCheckTimeEnd, Long logCollectTaskId, Long fileLogCollectPathId, String hostName) {
        return agentMetricsDAO.getSliceErrorCount(logCollectTaskHealthLastestCheckTime, logCollectTaskHealthCheckTimeEnd, logCollectTaskId, fileLogCollectPathId, hostName);
    }

    @Override
    public Integer getAbnormalTruncationCountByTimeFrame(Long logCollectTaskHealthLastestCheckTime, Long logCollectTaskHealthCheckTimeEnd, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        return agentMetricsDAO.getAbnormalTruncationCountByTimeFrame(logCollectTaskHealthLastestCheckTime, logCollectTaskHealthCheckTimeEnd, logCollectTaskId, fileLogCollectPathId, logCollectTaskHostName);
    }

    public boolean containerCompleteCollect(String containerHostName, String parentHostName, Long logCollectTaskId, Long fileLogCollectPathId) {
        /*
         * 获取近距当前时间近5mins关于 "parentHostName+containerHostName+logCollectTaskId+fileLogCollectPathId"
         * 对应 metric sendCount > 0 的列表为空 & 对应 metric sendCount = 0 列表不为空（ps：采集量为空且心跳正常情况下才代表采集完成）
         */
        Long endTime = System.currentTimeMillis();
        Long startTime = endTime - AgentConstant.AGENT_COLLECT_COMPLETE_TIME_THRESHOLD;
        Long sendCountEqualsZeroSize = agentMetricsDAO.getContainerSendCountEqualsZeroRecordSize(containerHostName, parentHostName, logCollectTaskId, fileLogCollectPathId, startTime, endTime);
        Long sendCountGtZeroSize = agentMetricsDAO.getContainerSendCountGtZeroRecordSize(containerHostName, parentHostName, logCollectTaskId, fileLogCollectPathId, startTime, endTime);
        boolean containerCompleteCollect = sendCountEqualsZeroSize > 0 && sendCountGtZeroSize == 0;
        return containerCompleteCollect;
    }

    public boolean hostCompleteCollect(String hostName, Long logCollectTaskId, Long fileLogCollectPathId) {
        /*
         * 获取近距当前时间近5mins关于 "hostName+logCollectTaskId+fileLogCollectPathId"
         * 对应 metric sendCount > 0 的列表为空 & 对应 metric sendCount = 0 列表不为空（ps：采集量为空且心跳正常情况下才代表采集完成）
         */
        Long endTime = System.currentTimeMillis();
        Long startTime = endTime - AgentConstant.AGENT_COLLECT_COMPLETE_TIME_THRESHOLD;
        Long sendCountEqualsZeroSize = agentMetricsDAO.getHostSendCountEqualsZeroRecordSize(hostName, logCollectTaskId, fileLogCollectPathId, startTime, endTime);
        Long sendCountGtZeroSize = agentMetricsDAO.getHostSendCountGtZeroRecordSize(hostName, logCollectTaskId, fileLogCollectPathId, startTime, endTime);
        boolean containerCompleteCollect = sendCountEqualsZeroSize > 0 && sendCountGtZeroSize == 0;
        return containerCompleteCollect;
    }

    @Override
    public Long getHostCpuLimiDturationByTimeFrame(Long startTime, Long endTime, String hostName) {
        return agentMetricsDAO.getHostCpuLimitDuration(startTime, endTime, hostName);
    }

    @Override
    public Long getHostByteLimitDurationByTimeFrame(Long startTime, Long endTime, String hostName) {
        Long value = agentMetricsDAO.getHostByteLimitDuration(startTime, endTime, hostName);
        return value == null ? 0L : value;
    }

    @Override
    public Integer getLastestFdUsage(String hostName) {
        return agentMetricsDAO.selectLatestByHostname(hostName).getFdCount();
    }

    @Override
    public Integer getErrorLogCount(Long lastestCheckTimeStart, Long agentHealthCheckTimeEnd, String hostName) {
        // TODO: 2021-06-28 优化错误日志的管理
//        return agentMetricsDAO.getErrorLogCount(lastestCheckTimeStart, agentHealthCheckTimeEnd, hostName);
        return 0;
    }

    @Override
    public Long getHeartbeatTimesByTimeFrame(Long startTime, Long endTime, String hostName) {
        return agentMetricsDAO.getHeartBeatTimes(startTime, endTime, hostName);
    }

    @Override
    public Integer getLastestCpuUsage(String hostName) {
        return agentMetricsDAO.selectLatestByHostname(hostName).getCpuUsage().intValue();
    }

    public Long getLatestMemoryUsage(String hostName) {
        return agentMetricsDAO.selectLatestByHostname(hostName).getMemoryUsage();
    }

    @Override
    public Long getLastestAgentStartupTime(String hostName) {
        return agentMetricsDAO.getLatestStartupTime(hostName);
    }

    @Override
    public Long getAgentFullgcTimesByTimeFrame(Long startTime, Long endTime, String hostName) {
        return agentMetricsDAO.getGCCount(startTime, endTime, hostName);
    }

    @Override
    public List<MetricPoint> getAgentCpuUsagePerMinMetric(String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getAgentCpuUsagePerMin(startTime, endTime, hostName);
    }

    @Override
    public List<MetricPoint> getAgentMemoryUsagePerMinMetric(String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getAgentMemoryUsagePerMin(startTime, endTime, hostName);
    }

    @Override
    public List<MetricPoint> getAgentFullGcTimesPerMinMetric(String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getAgentGCTimesPerMin(startTime, endTime, hostName);
    }

    @Override
    public List<MetricPoint> getAgentOutputBytesPerMinMetric(String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getAgentOutputBytesPerMin(startTime, endTime, hostName);
    }

    @Override
    public List<MetricPoint> getAgentOutputLogsCountPerMinMetric(String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getAgentOutputLogsPerMin(startTime, endTime, hostName);
    }

    @Override
    public List<MetricPoint> getAgentFdUsagePerMinMetric(String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getAgentFdUsagePerMin(startTime, endTime, hostName);
    }

    @Override
    public List<MetricPoint> getAgentStartupExistsPerMinMetric(String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getAgentStartupExistsPerMin(startTime, endTime, hostName);
    }

    @Override
    public List<MetricPoint> getLogCollectTaskLogsBytesPerMinMetric(Long logCollectTaskId, Long startTime, Long endTime) {
        return agentMetricsDAO.getLogCollectTaskBytesPerMin(logCollectTaskId, startTime, endTime);
    }

    @Override
    public List<MetricPoint> getLogCollectTaskLogsCountPerMinMetric(Long logCollectTaskId, Long startTime, Long endTime) {
        return agentMetricsDAO.getLogCollectTaskLogCountPerMin(logCollectTaskId, startTime, endTime);
    }

    @Override
    public List<MetricPoint> getFileLogPathExistsPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getFileLogPathNotExistsPerMin(logCollectTaskId, fileLogCollectPathId, hostName, startTime, endTime);
    }

    @Override
    public List<MetricPoint> getFileLogPathDisorderPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getFileLogPathDisorderPerMin(logCollectTaskId, fileLogCollectPathId, hostName, startTime, endTime);
    }

    @Override
    public List<MetricPoint> getFileLogPathLogSliceErrorPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getFileLogPathLogSliceErrorPerMin(logCollectTaskId, fileLogCollectPathId, hostName, startTime, endTime);
    }

    @Override
    public List<MetricPoint> getFileLogPathAbnormalTruncationPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getFileLogPathAbnormalTruncationPerMin(logCollectTaskId, fileLogCollectPathId, hostName, startTime, endTime);
    }

    @Override
    public List<MetricPoint> getFilterOutPerLogPathPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getFilterOutPerLogPathPerMin(logCollectTaskId, fileLogCollectPathId, hostName, startTime, endTime);
    }

    @Override
    public List<MetricPoint> getMinCurrentCollectTimePerLogPathPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getMinCurrentCollectTimePerLogPathPerMin(logCollectTaskId, fileLogCollectPathId, hostName, startTime, endTime);
    }

    @Override
    public List<MetricPoint> getLimitTimePerLogPathPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getLimitTimePerLogPathPerMin(logCollectTaskId, fileLogCollectPathId, hostName, startTime, endTime);
    }

    @Override
    public List<MetricPoint> getAgentErrorLogCountPerMin(AgentMetricQueryDO agentMetricQueryDO) {
        return agentMetricsDAO.getAgentErrorLogCountPerMin(agentMetricQueryDO.getHostname(), agentMetricQueryDO.getStartTime(), agentMetricQueryDO.getEndTime());
    }

    @Override
    public List<MetricPoint> queryByTask(Long logCollectTaskId, Long startTime, Long endTime, String column) {
        AgentMetricField field = AgentMetricField.fromString(column);
        if (field == null) {
            throw new ServiceException("字段不合法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        return agentMetricsDAO.queryByTask(logCollectTaskId, startTime, endTime, field);
    }

    @Override
    public List<MetricPoint> queryAggregationByTask(Long logCollectTaskId, Long startTime, Long endTime, String column, String method, int step) {
        AgentMetricField field = AgentMetricField.fromString(column);
        CalcFunction function = CalcFunction.fromString(method);
        if (field == null) {
            throw new ServiceException("字段不合法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        if (function == null) {
            throw new ServiceException("聚合方法名不合法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        if (function == CalcFunction.NORMAL) {
            return agentMetricsDAO.queryByTask(logCollectTaskId, startTime, endTime, field);
        }
        return agentMetricsDAO.queryAggregationByTask(logCollectTaskId, trimTimestamp(startTime), endTime, field, function, step);
    }

    @Override
    public List<MetricPoint> queryAggregationByHost(String hostname, Long startTime, Long endTime, String column, String method, int step) {
        AgentMetricField field = AgentMetricField.fromString(column);
        CalcFunction function = CalcFunction.fromString(method);
        if (field == null) {
            throw new ServiceException("字段不合法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        if (function == null) {
            throw new ServiceException("聚合方法名不合法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        if (function == CalcFunction.NORMAL) {
            throw new ServiceException("根据主机查询时需要提供聚合方法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        return agentMetricsDAO.queryAggregationByHostname(hostname, trimTimestamp(startTime), endTime, field, function, step);
    }

    @Override
    public List<MetricPoint> queryByLogModel(MetricQueryDO metricQueryDO, String column) {
        AgentMetricField field = AgentMetricField.fromString(column);
        if (field == null) {
            throw new ServiceException("字段不合法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        List<MetricPoint> graph = agentMetricsDAO.queryByLogModel(metricQueryDO.getTaskId(), metricQueryDO.getLogCollectPathId(), metricQueryDO.getHostName(), metricQueryDO.getStartTime(), metricQueryDO.getEndTime(), field);
        for (MetricPoint metricPoint : graph) {
            Object value = metricPoint.getValue();
            if (value.getClass() == Boolean.class) {
                metricPoint.setValue((Boolean) value ? 1 : 0);
            }
        }
        return graph;
    }

    @Override
    public List<MetricPoint> queryAggregationByLogModel(MetricQueryDO metricQueryDO, String column, String method, int step) {
        AgentMetricField field = AgentMetricField.fromString(column);
        CalcFunction function = CalcFunction.fromString(method);
        if (field == null) {
            throw new ServiceException("字段不合法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        if (function == null) {
            throw new ServiceException("聚合方法名不合法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        List<MetricPoint> graph;
        if (function == CalcFunction.NORMAL) {
            graph = agentMetricsDAO.queryByLogModel(metricQueryDO.getTaskId(), metricQueryDO.getLogCollectPathId(), metricQueryDO.getHostName(), metricQueryDO.getStartTime(), metricQueryDO.getEndTime(), field);
        } else {
            graph = agentMetricsDAO.queryAggregationByLogModel(metricQueryDO.getTaskId(), metricQueryDO.getLogCollectPathId(), metricQueryDO.getHostName(), trimTimestamp(metricQueryDO.getStartTime()), metricQueryDO.getEndTime(), field, function, step);
        }
        for (MetricPoint metricPoint : graph) {
            Object value = metricPoint.getValue();
            if (value.getClass() == Boolean.class) {
                metricPoint.setValue((Boolean) value ? 1 : 0);
            }
        }
        return graph;
    }

    @Override
    public List<MetricPoint> queryCollectDelay(MetricQueryDO metricQueryDO) {
        return agentMetricsDAO.getCollectDelayPerMin(metricQueryDO.getTaskId(), metricQueryDO.getLogCollectPathId(), metricQueryDO.getHostName(), metricQueryDO.getStartTime(), metricQueryDO.getEndTime());
    }

    @Override
    public List<MetricPoint> queryAgent(AgentMetricQueryDO agentMetricQueryDO, String column) {
        AgentMetricField field = AgentMetricField.fromString(column);
        if (field == null) {
            throw new ServiceException("字段不合法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        return agentMetricsDAO.queryAgent(agentMetricQueryDO.getHostname(), agentMetricQueryDO.getStartTime(), agentMetricQueryDO.getEndTime(), field);
    }

    @Override
    public List<MetricPoint> queryAgentAggregation(AgentMetricQueryDO agentMetricQueryDO, String column, String method, int step) {
        AgentMetricField field = AgentMetricField.fromString(column);
        CalcFunction function = CalcFunction.fromString(method);
        if (field == null) {
            throw new ServiceException("字段不合法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        if (function == null) {
            throw new ServiceException("聚合方法名不合法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        if (function == CalcFunction.NORMAL) {
            return agentMetricsDAO.queryAgent(agentMetricQueryDO.getHostname(), agentMetricQueryDO.getStartTime(), agentMetricQueryDO.getEndTime(), field);
        }
        return agentMetricsDAO.queryAgentAggregation(agentMetricQueryDO.getHostname(), trimTimestamp(agentMetricQueryDO.getStartTime()), agentMetricQueryDO.getEndTime(), field, function, step);
    }

    @Override
    public CollectTaskMetricPO getLatestMetric(Long taskId) {
        return agentMetricsDAO.selectLatestMetric(taskId);
    }

    @Override
    public List<CollectTaskMetricPO> queryLatest(Long time) {
        return agentMetricsDAO.queryLatestMetrics(time, MetricConstant.HEARTBEAT_PERIOD);
    }

    @Override
    public List<AgentMetricPO> queryAgentLatest(Long time) {
        return agentMetricsDAO.queryLatestAgentMetrics(time, MetricConstant.HEARTBEAT_PERIOD);
    }

    @Override
    public Double queryAggregationForAll(Long startTime, Long endTime, AgentMetricField column, CalcFunction function) {
        return agentMetricsDAO.queryAggregationForAll(trimTimestamp(startTime), endTime, column, function);
    }

    @Override
    public List<MetricPointList> getLogCollectTaskListCollectBytesLastest1MinTop5(Long startTime, Long endTime) {
        List<CollectTaskMetricPO> metrics = queryLatest(endTime);
        Map<Long, Integer> idValueMap = new HashMap<>();
        for (CollectTaskMetricPO metric : metrics) {
            idValueMap.merge(metric.getLogModeId(), metric.getReadByte(), (a, b) -> a + b);
        }
        int limit = 5 < idValueMap.size() ? 5 : idValueMap.size();
        List<Map.Entry<Long, Integer>> entries = new ArrayList<>(idValueMap.entrySet());
        List<Long> topTaskIds = entries.stream().sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).limit(limit).map(Map.Entry::getKey).collect(Collectors.toList());
        List<MetricPointList> result = new ArrayList<>();
        for (Long taskId : topTaskIds) {
            List<MetricPoint> metricPoint = agentMetricsDAO.queryAggregationByTask(taskId, startTime, endTime, AgentMetricField.READ_BYTE, CalcFunction.SUM, MetricConstant.HEARTBEAT_PERIOD);
            LogCollectTaskDO logCollectTaskDO = logCollectTaskManageService.getById(taskId);
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(metricPoint);
            if (logCollectTaskDO != null) {
                metricPointList.setName(logCollectTaskDO.getLogCollectTaskName());
            } else {
                metricPointList.setName(StringUtils.EMPTY);
                LOGGER.warn("class=AgentMetricsManageServiceImpl||method=getLogCollectTaskListCollectBytesLastest1MinTop5||msg={}",
                        String.format("系统中不存在id={%d}的LogCollectTask，将其指标Name设置为空串\"\"", taskId));
            }
            result.add(metricPointList);
        }
        return result;
    }

    @Override
    public List<MetricPointList> getLogCollectTaskListCollectCountLastest1MinTop5(Long startTime, Long endTime) {
        List<CollectTaskMetricPO> metrics = queryLatest(endTime);
        Map<Long, Integer> idValueMap = new HashMap<>();
        for (CollectTaskMetricPO metric : metrics) {
            idValueMap.merge(metric.getLogModeId(), metric.getReadCount(), (a, b) -> a + b);
        }
        int limit = 5 < idValueMap.size() ? 5 : idValueMap.size();
        List<Map.Entry<Long, Integer>> entries = new ArrayList<>(idValueMap.entrySet());
        List<Long> topTaskIds = entries.stream().sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).limit(limit).map(Map.Entry::getKey).collect(Collectors.toList());
        List<MetricPointList> result = new ArrayList<>();
        for (Long taskId : topTaskIds) {
            List<MetricPoint> metricPoint = agentMetricsDAO.queryAggregationByTask(taskId, startTime, endTime, AgentMetricField.READ_COUNT, CalcFunction.SUM, MetricConstant.HEARTBEAT_PERIOD);
            LogCollectTaskDO logCollectTaskDO = logCollectTaskManageService.getById(taskId);
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(metricPoint);
            if (logCollectTaskDO != null) {
                metricPointList.setName(logCollectTaskDO.getLogCollectTaskName());
            } else {
                metricPointList.setName(StringUtils.EMPTY);
                LOGGER.warn("class=AgentMetricsManageServiceImpl||method=getLogCollectTaskListCollectBytesLastest1MinTop5||msg={}",
                        String.format("系统中不存在id={%d}的LogCollectTask，将其指标Name设置为空串\"\"", taskId));
            }
            result.add(metricPointList);
        }
        return result;
    }

    @Override
    public List<MetricPointList> getAgentListCollectBytesLastest1MinTop5(Long startTime, Long endTime) {
        List<CollectTaskMetricPO> metrics = queryLatest(endTime);
        Map<String, Integer> idValueMap = new HashMap<>();
        for (CollectTaskMetricPO metric : metrics) {
            idValueMap.merge(metric.getHostname(), metric.getReadByte(), (a, b) -> a + b);
        }
        int limit = 5 < idValueMap.size() ? 5 : idValueMap.size();
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(idValueMap.entrySet());
        List<String> hostnames = entries.stream().sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).limit(limit).map(Map.Entry::getKey).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        List<MetricPointList> result = new ArrayList<>();
        for (String hostname : hostnames) {
            List<MetricPoint> metricPoint = agentMetricsDAO.queryAggregationByLogModel(null, null, hostname, startTime, endTime, AgentMetricField.READ_BYTE, CalcFunction.SUM, MetricConstant.HEARTBEAT_PERIOD);
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(metricPoint);
            metricPointList.setName(hostname);
            result.add(metricPointList);
        }
        return result;
    }

    @Override
    public List<MetricPointList> getAgentListCollectCountLastest1MinTop5(Long startTime, Long endTime) {
        List<CollectTaskMetricPO> metrics = queryLatest(endTime);
        Map<String, Integer> idValueMap = new HashMap<>();
        for (CollectTaskMetricPO metric : metrics) {
            idValueMap.merge(metric.getHostname(), metric.getReadCount(), (a, b) -> a + b);
        }
        int limit = 5 < idValueMap.size() ? 5 : idValueMap.size();
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(idValueMap.entrySet());
        List<String> hostnames = entries.stream().sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).limit(limit).map(Map.Entry::getKey).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        List<MetricPointList> result = new ArrayList<>();
        for (String hostname : hostnames) {
            List<MetricPoint> metricPoint = agentMetricsDAO.queryAggregationByLogModel(null, null, hostname, startTime, endTime, AgentMetricField.READ_COUNT, CalcFunction.SUM, MetricConstant.HEARTBEAT_PERIOD);
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(metricPoint);
            metricPointList.setName(hostname);
            result.add(metricPointList);
        }
        return result;
    }

    @Override
    public List<MetricPointList> getAgentListCpuUsageLastest1MinTop5(Long startTime, Long endTime) {
        List<AgentMetricPO> metrics = queryAgentLatest(endTime);
        int limit = 5 < metrics.size() ? 5 : metrics.size();
        List<String> hostnames = metrics.stream().sorted((o1, o2) -> o2.getCpuUsage().compareTo(o1.getCpuUsage())).limit(limit).map(AgentMetricPO::getHostname).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        List<MetricPointList> result = new ArrayList<>();
        for (String hostname : hostnames) {
            List<MetricPoint> metricPoint = agentMetricsDAO.queryAgentAggregation(hostname, startTime, endTime, AgentMetricField.CPU_USAGE, CalcFunction.MAX, MetricConstant.QUERY_INTERVAL);
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(metricPoint);
            metricPointList.setName(hostname);
            result.add(metricPointList);
        }
        return result;
    }

    @Override
    public List<MetricPointList> getAgentListFdUsedLastest1MinTop5(Long startTime, Long endTime) {
        List<AgentMetricPO> metrics = queryAgentLatest(endTime);
        int limit = 5 < metrics.size() ? 5 : metrics.size();
        List<String> hostnames = metrics.stream().sorted((o1, o2) -> o2.getFdCount().compareTo(o1.getFdCount())).limit(limit).map(AgentMetricPO::getHostname).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        List<MetricPointList> result = new ArrayList<>();
        for (String hostname : hostnames) {
            List<MetricPoint> metricPoint = agentMetricsDAO.queryAgentAggregation(hostname, startTime, endTime, AgentMetricField.FD_COUNT, CalcFunction.MAX, MetricConstant.QUERY_INTERVAL);
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(metricPoint);
            metricPointList.setName(hostname);
            result.add(metricPointList);
        }
        return result;
    }

    @Override
    public List<MetricPointList> getAgentListMemoryUsedLastest1MinTop5(Long startTime, Long endTime) {
        List<AgentMetricPO> metrics = queryAgentLatest(endTime);
        int limit = 5 < metrics.size() ? 5 : metrics.size();
        List<String> hostnames = metrics.stream().sorted((o1, o2) -> o2.getMemoryUsage().compareTo(o1.getMemoryUsage())).limit(limit).map(AgentMetricPO::getHostname).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        List<MetricPointList> result = new ArrayList<>();
        for (String hostname : hostnames) {
            List<MetricPoint> metricPoint = agentMetricsDAO.queryAgentAggregation(hostname, startTime, endTime, AgentMetricField.MEMORY_USAGE, CalcFunction.MAX, MetricConstant.QUERY_INTERVAL);
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(metricPoint);
            metricPointList.setName(hostname);
            result.add(metricPointList);
        }
        return result;
    }

    @Override
    public List<MetricPointList> getAgentListFullGcCountLastest1MinTop5(Long startTime, Long endTime) {
        List<AgentMetricPO> metrics = queryAgentLatest(endTime);
        int limit = 5 < metrics.size() ? 5 : metrics.size();
        List<String> hostnames = metrics.stream().sorted((o1, o2) -> o2.getGcCount().compareTo(o1.getGcCount())).limit(limit).map(AgentMetricPO::getHostname).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        List<MetricPointList> result = new ArrayList<>();
        for (String hostname : hostnames) {
            List<MetricPoint> metricPoint = agentMetricsDAO.queryAgentAggregation(hostname, startTime, endTime, AgentMetricField.GC_COUNT, CalcFunction.MAX, MetricConstant.QUERY_INTERVAL);
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(metricPoint);
            metricPointList.setName(hostname);
            result.add(metricPointList);
        }
        return result;
    }

    @Override
    public List<MetricPoint> queryAggregationByAgent(String agentHostName, Long startTime, Long endTime, AgentMetricField column, CalcFunction function) {
        return agentMetricsDAO.queryAggregationByAgent(agentHostName, startTime, endTime, column, function);
    }

    @Override
    public List<MetricPoint> queryAggregationGroupByHearttimeMinute(Long startTime, Long endTime, AgentMetricField column, CalcFunction function) {
        return agentMetricsDAO.queryAggregationGroupByMinute(startTime, endTime, column, function);
    }

    /**
     * 根据给定DashBoardStatisticsDO对象集，获取各指标按 heartbeat time 倒序排序，根据指标值获取其最大 topN
     *
     * @param dashBoardStatisticsDOList DashBoardStatisticsDO 对象集
     * @param topN                      top 数
     * @return 返回根据给定DashBoardStatisticsDO对象集，获取到的各指标按 heartbeat time 倒序排序，根据指标值获取其最大 topN
     */
    private List<DashBoardStatisticsDO> getMetricPointListLastestTop5(List<DashBoardStatisticsDO> dashBoardStatisticsDOList, int topN) {
        Map<Object, List<DashBoardStatisticsDO>> id2DashboardStatisticsDOMap = new HashMap<>();
        for (DashBoardStatisticsDO dashBoardStatisticsDO : dashBoardStatisticsDOList) {
            List<DashBoardStatisticsDO> list = id2DashboardStatisticsDOMap.get(dashBoardStatisticsDO.getKey());
            if (null == list) {
                list = new ArrayList<>();
                list.add(dashBoardStatisticsDO);
                id2DashboardStatisticsDOMap.put(dashBoardStatisticsDO.getKey(), list);
            } else {
                list.add(dashBoardStatisticsDO);
            }
        }
        List<DashBoardStatisticsDO> dashBoardStatisticsDOLastest1MinList = new ArrayList<>(id2DashboardStatisticsDOMap.size());
        for (Map.Entry<Object, List<DashBoardStatisticsDO>> entry : id2DashboardStatisticsDOMap.entrySet()) {
            List<DashBoardStatisticsDO> list = entry.getValue();
            if (CollectionUtils.isNotEmpty(list)) {
                list.sort(dashBoardStatisticsDOHeartbeatTimeComparator);
                dashBoardStatisticsDOLastest1MinList.add(list.get(0));
            }
        }
        dashBoardStatisticsDOLastest1MinList.sort(dashBoardStatisticsDOValueComparator);
        List<DashBoardStatisticsDO> sendBytesTop5List = new ArrayList<>(topN);
        for (int i = 0, size = dashBoardStatisticsDOLastest1MinList.size() > topN ? topN : dashBoardStatisticsDOLastest1MinList.size(); i < size; i++) {
            DashBoardStatisticsDO dashBoardStatisticsDO = dashBoardStatisticsDOLastest1MinList.get(i);
            sendBytesTop5List.add(dashBoardStatisticsDO);
        }
        return sendBytesTop5List;
    }

    /**
     * 将时间修整为整分钟，解决聚合查询时开头和结尾可能出现的缺数据情况
     *
     * @param timestamp
     * @return
     */
    private static long trimTimestamp(long timestamp) {
        return timestamp / 60000 * 60000;
    }

    class DashBoardStatisticsDOHeartbeatTimeComparator implements Comparator<DashBoardStatisticsDO> {
        @Override
        public int compare(DashBoardStatisticsDO o1, DashBoardStatisticsDO o2) {
            return o1.getHeartbeatTime().compareTo(o2.getHeartbeatTime());
        }
    }

    class DashBoardStatisticsDOValueComparator implements Comparator<DashBoardStatisticsDO> {
        @Override
        public int compare(DashBoardStatisticsDO o1, DashBoardStatisticsDO o2) {
            if (o1.getMetricValue() instanceof Number && o2.getMetricValue() instanceof Number) {
                Number n1 = ((Number) o1.getMetricValue());
                Number n2 = ((Number) o2.getMetricValue());
                if (n1 instanceof Long && n2 instanceof Long) {
                    return ((Long) n1).compareTo((Long) n2);
                }
                if (n1 instanceof Integer && n2 instanceof Integer) {
                    return ((Integer) n1).compareTo((Integer) n2);
                }
                if (n1 instanceof Float && n2 instanceof Float) {
                    return ((Float) n1).compareTo((Float) n2);
                }
                if (n1 instanceof Double && n2 instanceof Double) {
                    return ((Double) n1).compareTo((Double) n2);
                }
                if (n1 instanceof BigDecimal && n2 instanceof BigDecimal) {
                    return ((BigDecimal) n1).compareTo((BigDecimal) n2);
                }
                throw new ServiceException(
                        String.format(
                                "class=DashBoardStatisticsDOValueComparator||method=compare||msg={%s}",
                                String.format("给定DashBoardStatisticsDO对象={%s}对应metricValue属性值类型={%s}系统不支持", JSON.toJSONString(o1), o1.getMetricValue().getClass().getName())
                        ),
                        ErrorCodeEnum.UNSUPPORTED_CLASS_CAST_EXCEPTION.getCode()
                );
            } else {
                throw new ServiceException(
                        String.format(
                                "class=DashBoardStatisticsDOValueComparator||method=compare||msg={%s}",
                                String.format("metric value不为数字, o1 class=%s, o2 class=%s", o1.getMetricValue().getClass().getCanonicalName(), o2.getMetricValue().getClass().getCanonicalName())
                        ),
                        ErrorCodeEnum.UNSUPPORTED_CLASS_CAST_EXCEPTION.getCode()
                );
            }
        }
    }

}
