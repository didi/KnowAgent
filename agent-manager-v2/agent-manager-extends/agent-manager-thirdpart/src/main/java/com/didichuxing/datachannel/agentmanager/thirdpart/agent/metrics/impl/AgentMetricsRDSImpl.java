package com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.impl;

import com.alibaba.fastjson.util.TypeUtils;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.AgentMetricRDSField;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.AgentMetricPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.CollectTaskMetricPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentMetricMapper;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.CollectTaskMetricMapper;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.ErrorLogMapper;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.AgentMetricsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.List;

@Repository
public class AgentMetricsRDSImpl implements AgentMetricsDAO {

    @Autowired
    private AgentMetricMapper agentMetricMapper;

    @Autowired
    private CollectTaskMetricMapper collectTaskMetricMapper;

    @Autowired
    private ErrorLogMapper errorLogMapper;

    @Override
    public Long getContainerSendCountEqualsZeroRecordSize(String containerHostName, String parentHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        return collectTaskMetricMapper.selectContainerCountEqualsZero(heartbeatStartTime, heartbeatEndTime, logCollectTaskId, parentHostName, containerHostName, AgentMetricRDSField.SEND_COUNT.getValue());
    }

    @Override
    public Long getContainerSendCountGtZeroRecordSize(String containerHostName, String parentHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        return collectTaskMetricMapper.selectContainerCountGtZero(heartbeatStartTime, heartbeatEndTime, logCollectTaskId, parentHostName, containerHostName, AgentMetricRDSField.SEND_COUNT.getValue());
    }

    @Override
    public Long getHostSendCountEqualsZeroRecordSize(String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        return collectTaskMetricMapper.selectSingleCountEqualsZero(heartbeatStartTime, heartbeatEndTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricRDSField.SEND_COUNT.getValue());
    }

    @Override
    public Long getHostSendCountGtZeroRecordSize(String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        return collectTaskMetricMapper.selectSingleCountGtZero(heartbeatStartTime, heartbeatEndTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricRDSField.SEND_COUNT.getValue());
    }

    @Override
    public Long getHeartbeatTimesByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        return collectTaskMetricMapper.selectHeartbeatCount(startTime, endTime, logCollectTaskId, logCollectTaskHostName, fileLogCollectPathId);
    }

    @Override
    public Long getHeartBeatTimes(Long startTime, Long endTime, String hostName) {
        return agentMetricMapper.selectHeartbeatCount(startTime, endTime, hostName);
    }

    @Override
    public Integer getFilePathNotExistsCountByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        Long value = collectTaskMetricMapper.selectSingleCountWithTerm(startTime, endTime, logCollectTaskId, logCollectTaskHostName, fileLogCollectPathId, AgentMetricRDSField.IS_FILE_EXIST.getValue(), false);
        return value == null ? 0 : value.intValue();
    }

    @Override
    public Integer getAbnormalTruncationCountByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        Long value = collectTaskMetricMapper.selectSingleSum(startTime, endTime, logCollectTaskId, logCollectTaskHostName, fileLogCollectPathId, AgentMetricRDSField.FILTER_TOO_LARGE_COUNT.getValue());
        return value == null ? 0 : value.intValue();
    }

    @Override
    public Integer getFileDisorderCount(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
        List<CollectTaskMetricPO> list = collectTaskMetricMapper.selectSome(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId);
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (CollectTaskMetricPO collectTaskMetricPO : list) {
            String files = collectTaskMetricPO.getCollectFiles();
            if (StringUtils.isEmpty(files) || "[]".equals(files)) {
                continue;
            }
            count += files.split("\""+ AgentMetricRDSField.IS_FILE_ORDER.getValue() +"\":1").length - 1;
        }
        return count;
    }

    @Override
    public Integer getSliceErrorCount(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
        Long value = collectTaskMetricMapper.selectSingleCountWithTerm(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricRDSField.VALID_TIME_CONFIG.getValue(), true);
        return value == null ? 0 : value.intValue();
    }

    @Override
    public Long getLatestCollectTime(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
        return (Long) collectTaskMetricMapper.selectSingleMax(logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricRDSField.LOG_TIME.getValue());
    }

    @Override
    public Long getLatestStartupTime(String hostName) {
        return agentMetricMapper.selectMaxByHostname(hostName, AgentMetricRDSField.START_TIME.getValue());
    }

    @Override
    public Long getHostCpuLimitDuration(Long startTime, Long endTime, String hostName) {
        return collectTaskMetricMapper.selectSumByHostname(startTime, endTime, hostName, AgentMetricRDSField.LIMIT_RATE.getValue());
    }

    @Override
    public Long getHostByteLimitDuration(Long startTime, Long endTime, String hostName) {
        return collectTaskMetricMapper.selectSumByHostname(startTime, endTime, hostName, AgentMetricRDSField.LIMIT_TIME.getValue());
    }

    @Override
    public Long getHostByteLimitDuration(Long startTime, Long endTime, String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId) {
        return collectTaskMetricMapper.selectSingleSum(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricRDSField.LIMIT_TIME.getValue());
    }

    @Override
    public Integer getErrorLogCount(Long startTime, Long endTime, String hostName) {
        Long value = errorLogMapper.selectCount(startTime, endTime, hostName);
        return value == null ? 0 : value.intValue();
    }

    @Override
    public Integer getLatestFdUsage(String hostName) {
        AgentMetricPO agentMetricPO = agentMetricMapper.selectLatestByHostname(hostName);
        return agentMetricPO.getFdCount();
    }

    @Override
    public Double getLatestCpuUsage(String hostName) {
        AgentMetricPO agentMetricPO = agentMetricMapper.selectLatestByHostname(hostName);
        return agentMetricPO.getCpuUsage();
    }

    @Override
    public Long getLatestMemoryUsage(String hostName) {
        AgentMetricPO agentMetricPO = agentMetricMapper.selectLatestByHostname(hostName);
        return agentMetricPO.getMemoryUsage();
    }

    @Override
    public Long getGCCount(Long startTime, Long endTime, String hostName) {
        return agentMetricMapper.selectSum(startTime, endTime, hostName, AgentMetricRDSField.GC_COUNT.getValue());
    }

    @Override
    public List<MetricPoint> getAgentCpuUsagePerMin(Long startTime, Long endTime, String hostName) {
        return agentMetricMapper.selectAvgPerMin(startTime, endTime, hostName, AgentMetricRDSField.CPU_USAGE.getValue());
    }

    @Override
    public List<MetricPoint> getAgentMemoryUsagePerMin(Long startTime, Long endTime, String hostName) {
        List<MetricPoint> graph = agentMetricMapper.selectAvgPerMin(startTime, endTime, hostName, AgentMetricRDSField.MEMORY_USAGE.getValue());
        for (MetricPoint metricPoint : graph) {
            BigDecimal b1 = TypeUtils.castToBigDecimal(metricPoint.getValue());
            BigDecimal b2 = b1.divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP);
            metricPoint.setValue(b2);
        }
        return graph;
    }

    @Override
    public List<MetricPoint> getAgentGCTimesPerMin(Long startTime, Long endTime, String hostName) {
        return agentMetricMapper.selectSumPerMin(startTime, endTime, hostName, AgentMetricRDSField.GC_COUNT.getValue());
    }

    @Override
    public List<MetricPoint> getAgentOutputBytesPerMin(Long startTime, Long endTime, String hostName) {
        List<MetricPoint> graph = collectTaskMetricMapper.selectSumByHostnamePerMin(startTime, endTime, hostName, AgentMetricRDSField.SEND_BYTE.getValue());
        for (MetricPoint metricPoint : graph) {
            BigDecimal b1 = TypeUtils.castToBigDecimal(metricPoint.getValue());
            BigDecimal b2 = b1.divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP);
            metricPoint.setValue(b2);
        }
        return graph;
    }

    @Override
    public List<MetricPoint> getAgentOutputLogsPerMin(Long startTime, Long endTime, String hostName) {
        return collectTaskMetricMapper.selectSumByHostnamePerMin(startTime, endTime, hostName, AgentMetricRDSField.SEND_COUNT.getValue());
    }

    @Override
    public List<MetricPoint> getAgentFdUsagePerMin(Long startTime, Long endTime, String hostName) {
        List<MetricPoint> graph = agentMetricMapper.selectAvgPerMin(startTime, endTime, hostName, AgentMetricRDSField.FD_COUNT.getValue());
        for (MetricPoint metricPoint : graph) {
            Integer i = TypeUtils.castToInt(metricPoint.getValue());
            metricPoint.setValue(i);
        }
        return graph;
    }

    @Override
    public List<MetricPoint> getAgentStartupExistsPerMin(Long startTime, Long endTime, String hostName) {
        return agentMetricMapper.selectSinglePerMin(startTime, endTime, hostName, AgentMetricRDSField.START_TIME.getValue());
    }

    @Override
    public List<MetricPoint> getLogCollectTaskBytesPerMin(Long taskId, Long startTime, Long endTime) {
        List<MetricPoint> graph = collectTaskMetricMapper.selectSumByTaskIdPerMin(startTime, endTime, taskId, AgentMetricRDSField.SEND_BYTE.getValue());
        for (MetricPoint metricPoint : graph) {
            BigDecimal b1 = TypeUtils.castToBigDecimal(metricPoint.getValue());
            BigDecimal b2 = b1.divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP);
            metricPoint.setValue(b2);
        }
        return graph;
    }

    @Override
    public List<MetricPoint> getLogCollectTaskLogCountPerMin(Long taskId, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSumByTaskIdPerMin(startTime, endTime, taskId, AgentMetricRDSField.READ_COUNT.getValue());
    }

    @Override
    public List<MetricPoint> getFileLogPathNotExistsPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSinglePerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricRDSField.IS_FILE_EXIST.getValue());
    }

    @Override
    public List<MetricPoint> getFileLogPathDisorderPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        List<MetricPoint> graph = collectTaskMetricMapper.selectFileDisorderPerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId);
        for (MetricPoint metricPoint : graph) {
            String collectFiles = TypeUtils.castToString(metricPoint.getValue());
            metricPoint.setValue(collectFiles.matches( "\"" + AgentMetricRDSField.IS_FILE_ORDER.getValue() + "\":1"));
        }
        return graph;
    }

    @Override
    public List<MetricPoint> getFilterOutPerLogPathPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSinglePerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricRDSField.FILTER_OUT.getValue());
    }

    @Override
    public List<MetricPoint> getMinCurrentCollectTimePerLogPathPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectMinPerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricRDSField.LOG_TIME.getValue());
    }

    @Override
    public List<MetricPoint> getLimitTimePerLogPathPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSumPerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricRDSField.LIMIT_TIME.getValue());
    }

    @Override
    public List<MetricPoint> getFileLogPathLogSliceErrorPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSinglePerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricRDSField.VALID_TIME_CONFIG.getValue());
    }

    @Override
    public List<MetricPoint> getFileLogPathAbnormalTruncationPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSinglePerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricRDSField.FILTER_TOO_LARGE_COUNT.getValue());
    }

}
