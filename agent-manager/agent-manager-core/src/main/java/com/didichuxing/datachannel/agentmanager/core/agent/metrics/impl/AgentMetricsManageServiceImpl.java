package com.didichuxing.datachannel.agentmanager.core.agent.metrics.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.metrics.DashBoardStatisticsDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.AgentMetricQueryDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.MetricQueryDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.CollectTaskMetricPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.AgentMetricField;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.CalcFunction;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPointList;
import com.didichuxing.datachannel.agentmanager.common.constant.AgentConstant;
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

import java.util.*;

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
        return agentMetricsDAO.getLatestFdUsage(hostName);
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
        return agentMetricsDAO.getLatestCpuUsage(hostName).intValue();
    }

    public Long getLatestMemoryUsage(String hostName) {
        return agentMetricsDAO.getLatestMemoryUsage(hostName);
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
    public List<MetricPoint> queryAggregationByTask(Long logCollectTaskId, Long startTime, Long endTime, String column, String method) {
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
        return agentMetricsDAO.queryAggregationByTask(logCollectTaskId, startTime, endTime, field, function);
    }

    @Override
    public List<MetricPoint> queryByLogModel(MetricQueryDO metricQueryDO, String column) {
        AgentMetricField field = AgentMetricField.fromString(column);
        if (field == null) {
            throw new ServiceException("字段不合法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        return agentMetricsDAO.queryByLogModel(metricQueryDO.getTaskId(), metricQueryDO.getLogCollectPathId(), metricQueryDO.getHostName(), metricQueryDO.getStartTime(), metricQueryDO.getEndTime(), field);
    }

    @Override
    public List<MetricPoint> queryAggregationByLogModel(MetricQueryDO metricQueryDO, String column, String method) {
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
            graph = agentMetricsDAO.queryAggregationByLogModel(metricQueryDO.getTaskId(), metricQueryDO.getLogCollectPathId(), metricQueryDO.getHostName(), metricQueryDO.getStartTime(), metricQueryDO.getEndTime(), field, function);
        }
        for (MetricPoint metricPoint : graph) {
            Object value = metricPoint.getValue();
            if (value.getClass() == Boolean.class) {
                metricPoint.setValue((Boolean)value ? 1 : 0);
            } else if (value.getClass() == boolean.class) {
                metricPoint.setValue((boolean)value ? 1 : 0);
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
    public List<MetricPoint> queryAgentAggregation(AgentMetricQueryDO agentMetricQueryDO, String column, String method) {
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
        return agentMetricsDAO.queryAgentAggregation(agentMetricQueryDO.getHostname(), agentMetricQueryDO.getStartTime(), agentMetricQueryDO.getEndTime(), field, function);
    }

    @Override
    public CollectTaskMetricPO getLatestMetric(Long taskId) {
        return agentMetricsDAO.selectLatestMetric(taskId);
    }

    @Override
    public Double queryAggregationForAll(Long startTime, Long endTime, AgentMetricField column, CalcFunction function) {
        return agentMetricsDAO.queryAggregationForAll(startTime, endTime, column, function);
    }

    @Override
    public List<MetricPointList> getLogCollectTaskListCollectBytesLastest1MinTop5(Long startTime, Long endTime) {
        List<DashBoardStatisticsDO> dashBoardStatisticsDOList = agentMetricsDAO.groupByKeyAndMinuteLogCollectTaskMetric(startTime, endTime, AgentMetricField.LOG_MODE_ID.getRdsValue(), CalcFunction.SUM.getValue(), AgentMetricField.SEND_BYTE.getRdsValue());
        List<DashBoardStatisticsDO> sendBytesTopNList = getMetricPointListLastestTop5(dashBoardStatisticsDOList, 5);
        List<MetricPointList> result = new ArrayList<>(sendBytesTopNList.size());
        for (DashBoardStatisticsDO dashBoardStatisticsDO : sendBytesTopNList) {
            Object key = dashBoardStatisticsDO.getKey();
            Long logCollectTaskId = (Long) key;
            List<MetricPoint> metricPoint = agentMetricsDAO.queryAggregationByTask(logCollectTaskId, startTime, endTime, AgentMetricField.SEND_BYTE, CalcFunction.SUM);
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(metricPoint);
            LogCollectTaskDO logCollectTaskDO = logCollectTaskManageService.getById(logCollectTaskId);
            if(null != logCollectTaskDO) {
                metricPointList.setName(logCollectTaskDO.getLogCollectTaskName());
            } else {
                metricPointList.setName(StringUtils.EMPTY);
                LOGGER.warn(
                        "class=AgentMetricsManageServiceImpl||method=getLogCollectTaskListCollectBytesLastest1MinTop5||msg={}",
                            String.format("系统中不存在id={%d}的LogCollectTask，将其指标Name设置为空串\"\"", dashBoardStatisticsDO.getKey())
                        );
            }
            result.add(metricPointList);
        }
        return result;
    }

    /**
     * 根据给定DashBoardStatisticsDO对象集，获取各指标按 heartbeat time 倒序排序，根据指标值获取其最大 topN
     * @param dashBoardStatisticsDOList DashBoardStatisticsDO 对象集
     * @param topN top 数
     * @return 返回根据给定DashBoardStatisticsDO对象集，获取到的各指标按 heartbeat time 倒序排序，根据指标值获取其最大 topN
     */
    private List<DashBoardStatisticsDO> getMetricPointListLastestTop5(List<DashBoardStatisticsDO> dashBoardStatisticsDOList, int topN) {
        Map<Object, List<DashBoardStatisticsDO>> id2DashboardStatisticsDOMap = new HashMap<>();
        for (DashBoardStatisticsDO dashBoardStatisticsDO : dashBoardStatisticsDOList) {
            List<DashBoardStatisticsDO> list = id2DashboardStatisticsDOMap.get(dashBoardStatisticsDO.getKey());
            if(null == list) {
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
            Collections.sort(list, dashBoardStatisticsDOHeartbeatTimeComparator);
            if(CollectionUtils.isNotEmpty(list)) {
                dashBoardStatisticsDOLastest1MinList.add(list.get(0));
            }
        }
        Collections.sort(dashBoardStatisticsDOLastest1MinList, dashBoardStatisticsDOValueComparator);
        List<DashBoardStatisticsDO> sendBytesTop5List = new ArrayList<>(topN);
        for (int i = 0, size = dashBoardStatisticsDOLastest1MinList.size() > topN ? topN : dashBoardStatisticsDOLastest1MinList.size(); i < size; i++) {
            DashBoardStatisticsDO dashBoardStatisticsDO = dashBoardStatisticsDOLastest1MinList.get(i);
            sendBytesTop5List.add(dashBoardStatisticsDO);
        }
        return sendBytesTop5List;
    }


    @Override
    public List<MetricPointList> getLogCollectTaskListCollectCountLastest1MinTop5(Long startTime, Long endTime) {
        List<DashBoardStatisticsDO> dashBoardStatisticsDOList = agentMetricsDAO.groupByKeyAndMinuteLogCollectTaskMetric(startTime, endTime, AgentMetricField.LOG_MODE_ID.getRdsValue(), CalcFunction.SUM.getValue(), AgentMetricField.SEND_COUNT.getRdsValue());
        List<DashBoardStatisticsDO> sendCountTopNList = getMetricPointListLastestTop5(dashBoardStatisticsDOList, 5);
        List<MetricPointList> result = new ArrayList<>(sendCountTopNList.size());
        for (DashBoardStatisticsDO dashBoardStatisticsDO : sendCountTopNList) {
            Object key = dashBoardStatisticsDO.getKey();
            Long logCollectTaskId = (Long) key;
            List<MetricPoint> metricPoint = agentMetricsDAO.queryAggregationByTask(logCollectTaskId, startTime, endTime, AgentMetricField.SEND_COUNT, CalcFunction.SUM);
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(metricPoint);
            LogCollectTaskDO logCollectTaskDO = logCollectTaskManageService.getById(logCollectTaskId);
            if(null != logCollectTaskDO) {
                metricPointList.setName(logCollectTaskDO.getLogCollectTaskName());
            } else {
                metricPointList.setName(StringUtils.EMPTY);
                LOGGER.warn(
                        "class=AgentMetricsManageServiceImpl||method=getLogCollectTaskListCollectCountLastest1MinTop5||msg={}",
                        String.format("系统中不存在id={%d}的LogCollectTask，将其指标Name设置为空串\"\"", dashBoardStatisticsDO.getKey())
                );
            }
            result.add(metricPointList);
        }
        return result;
    }

    @Override
    public List<MetricPointList> getAgentListCollectBytesLastest1MinTop5(Long startTime, Long endTime) {
        List<DashBoardStatisticsDO> dashBoardStatisticsDOList = agentMetricsDAO.groupByKeyAndMinuteLogCollectTaskMetric(startTime, endTime, AgentMetricField.HOSTNAME.getRdsValue(), CalcFunction.SUM.getValue(), AgentMetricField.SEND_BYTE.getRdsValue());
        List<DashBoardStatisticsDO> sendBytesTopNList = getMetricPointListLastestTop5(dashBoardStatisticsDOList, 5);
        List<MetricPointList> result = new ArrayList<>(sendBytesTopNList.size());
        for (DashBoardStatisticsDO dashBoardStatisticsDO : sendBytesTopNList) {
            Object key = dashBoardStatisticsDO.getKey();
            String agentHostName = (String) key;
            List<MetricPoint> metricPoint = agentMetricsDAO.queryAggregationByAgentFromLogCollectTaskMetrics(agentHostName, startTime, endTime, AgentMetricField.SEND_BYTE, CalcFunction.SUM);
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(metricPoint);
            metricPointList.setName(agentHostName);
            result.add(metricPointList);
        }
        return result;
    }

    @Override
    public List<MetricPointList> getAgentListCollectCountLastest1MinTop5(Long startTime, Long endTime) {
        List<DashBoardStatisticsDO> dashBoardStatisticsDOList = agentMetricsDAO.groupByKeyAndMinuteLogCollectTaskMetric(startTime, endTime, AgentMetricField.HOSTNAME.getRdsValue(), CalcFunction.SUM.getValue(), AgentMetricField.SEND_COUNT.getRdsValue());
        List<DashBoardStatisticsDO> sendBytesTopNList = getMetricPointListLastestTop5(dashBoardStatisticsDOList, 5);
        List<MetricPointList> result = new ArrayList<>(sendBytesTopNList.size());
        for (DashBoardStatisticsDO dashBoardStatisticsDO : sendBytesTopNList) {
            Object key = dashBoardStatisticsDO.getKey();
            String agentHostName = (String) key;
            List<MetricPoint> metricPoint = agentMetricsDAO.queryAggregationByAgentFromLogCollectTaskMetrics(agentHostName, startTime, endTime, AgentMetricField.SEND_COUNT, CalcFunction.SUM);
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(metricPoint);
            metricPointList.setName(agentHostName);
            result.add(metricPointList);
        }
        return result;
    }

    @Override
    public List<MetricPointList> getAgentListCpuUsageLastest1MinTop5(Long startTime, Long endTime) {
        List<DashBoardStatisticsDO> dashBoardStatisticsDOList = agentMetricsDAO.groupByKeyAndMinuteAgentMetric(startTime, endTime, AgentMetricField.HOSTNAME.getRdsValue(), CalcFunction.MAX.getValue(), AgentMetricField.CPU_USAGE.getRdsValue());
        List<DashBoardStatisticsDO> cpuUsageTopNList = getMetricPointListLastestTop5(dashBoardStatisticsDOList, 5);
        List<MetricPointList> result = new ArrayList<>(cpuUsageTopNList.size());
        for (DashBoardStatisticsDO dashBoardStatisticsDO : cpuUsageTopNList) {
            Object key = dashBoardStatisticsDO.getKey();
            String agentHostName = (String) key;
            List<MetricPoint> metricPoint = agentMetricsDAO.queryAggregationByAgentFromAgentMetrics(agentHostName, startTime, endTime, AgentMetricField.CPU_USAGE, CalcFunction.MAX);
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(metricPoint);
            metricPointList.setName(agentHostName);
            result.add(metricPointList);
        }
        return result;
    }

    @Override
    public List<MetricPointList> getAgentListFdUsedLastest1MinTop5(Long startTime, Long endTime) {
        List<DashBoardStatisticsDO> dashBoardStatisticsDOList = agentMetricsDAO.groupByKeyAndMinuteAgentMetric(startTime, endTime, AgentMetricField.HOSTNAME.getRdsValue(), CalcFunction.MAX.getValue(), AgentMetricField.FD_COUNT.getRdsValue());
        List<DashBoardStatisticsDO> fdUsedTopNList = getMetricPointListLastestTop5(dashBoardStatisticsDOList, 5);
        List<MetricPointList> result = new ArrayList<>(fdUsedTopNList.size());
        for (DashBoardStatisticsDO dashBoardStatisticsDO : fdUsedTopNList) {
            Object key = dashBoardStatisticsDO.getKey();
            String agentHostName = (String) key;
            List<MetricPoint> metricPoint = agentMetricsDAO.queryAggregationByAgentFromAgentMetrics(agentHostName, startTime, endTime, AgentMetricField.FD_COUNT, CalcFunction.MAX);
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(metricPoint);
            metricPointList.setName(agentHostName);
            result.add(metricPointList);
        }
        return result;
    }

    @Override
    public List<MetricPointList> getAgentListMemoryUsedLastest1MinTop5(Long startTime, Long endTime) {
        List<DashBoardStatisticsDO> dashBoardStatisticsDOList = agentMetricsDAO.groupByKeyAndMinuteAgentMetric(startTime, endTime, AgentMetricField.HOSTNAME.getRdsValue(), CalcFunction.MAX.getValue(), AgentMetricField.MEMORY_USAGE.getRdsValue());
        List<DashBoardStatisticsDO> fdUsedTopNList = getMetricPointListLastestTop5(dashBoardStatisticsDOList, 5);
        List<MetricPointList> result = new ArrayList<>(fdUsedTopNList.size());
        for (DashBoardStatisticsDO dashBoardStatisticsDO : fdUsedTopNList) {
            Object key = dashBoardStatisticsDO.getKey();
            String agentHostName = (String) key;
            List<MetricPoint> metricPoint = agentMetricsDAO.queryAggregationByAgentFromAgentMetrics(agentHostName, startTime, endTime, AgentMetricField.MEMORY_USAGE, CalcFunction.MAX);
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(metricPoint);
            metricPointList.setName(agentHostName);
            result.add(metricPointList);
        }
        return result;
    }

    @Override
    public List<MetricPointList> getAgentListFullGcCountLastest1MinTop5(Long startTime, Long endTime) {
        List<DashBoardStatisticsDO> dashBoardStatisticsDOList = agentMetricsDAO.groupByKeyAndMinuteAgentMetric(startTime, endTime, AgentMetricField.HOSTNAME.getRdsValue(), CalcFunction.MAX.getValue(), AgentMetricField.GC_COUNT.getRdsValue());
        List<DashBoardStatisticsDO> fdUsedTopNList = getMetricPointListLastestTop5(dashBoardStatisticsDOList, 5);
        List<MetricPointList> result = new ArrayList<>(fdUsedTopNList.size());
        for (DashBoardStatisticsDO dashBoardStatisticsDO : fdUsedTopNList) {
            Object key = dashBoardStatisticsDO.getKey();
            String agentHostName = (String) key;
            List<MetricPoint> metricPoint = agentMetricsDAO.queryAggregationByAgentFromAgentMetrics(agentHostName, startTime, endTime, AgentMetricField.GC_COUNT, CalcFunction.MAX);
            MetricPointList metricPointList = new MetricPointList();
            metricPointList.setMetricPointList(metricPoint);
            metricPointList.setName(agentHostName);
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

    class DashBoardStatisticsDOHeartbeatTimeComparator implements Comparator<DashBoardStatisticsDO> {
        @Override
        public int compare(DashBoardStatisticsDO o1, DashBoardStatisticsDO o2) {
            long result = o1.getHeartbeatTime() - o2.getHeartbeatTime();
            if(0L == result) {
                return 0;
            } else if(0 < result) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    class DashBoardStatisticsDOValueComparator implements Comparator<DashBoardStatisticsDO> {
        @Override
        public int compare(DashBoardStatisticsDO o1, DashBoardStatisticsDO o2) {
            if(o1.getMetricValue() instanceof Long) {
                Long o1Value = (Long) o1.getMetricValue();
                Long o2Value = (Long )o2.getMetricValue();
                Long result = o1Value - o2Value;
                if(0L == result) {
                    return 0;
                } else if(0 < result) {
                    return 1;
                } else {
                    return -1;
                }
            } else if(o1.getMetricValue() instanceof Integer) {
                Integer o1Value = (Integer) o1.getMetricValue();
                Integer o2Value = (Integer )o2.getMetricValue();
                Integer result = o1Value - o2Value;
                if(0L == result) {
                    return 0;
                } else if(0 < result) {
                    return 1;
                } else {
                    return -1;
                }
            } else if(o1.getMetricValue() instanceof Float) {
                Float o1Value = (Float) o1.getMetricValue();
                Float o2Value = (Float )o2.getMetricValue();
                Float result = o1Value - o2Value;
                if(0L == result) {
                    return 0;
                } else if(0 < result) {
                    return 1;
                } else {
                    return -1;
                }
            } else if(o1.getMetricValue() instanceof Double) {
                Double o1Value = (Double) o1.getMetricValue();
                Double o2Value = (Double )o2.getMetricValue();
                Double result = o1Value - o2Value;
                if(0L == result) {
                    return 0;
                } else if(0 < result) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                throw new ServiceException(
                        String.format(
                                "class=DashBoardStatisticsDOValueComparator||method=compare||msg={%s}",
                                String.format("给定DashBoardStatisticsDO对象={%s}对应metricValue属性值类型={%s}系统不支持", JSON.toJSONString(o1), o1.getMetricValue().getClass().getName())
                        ),
                        ErrorCodeEnum.UNSUPPORTED_CLASS_CAST_EXCEPTION.getCode()
                );
            }
        }
    }

}
