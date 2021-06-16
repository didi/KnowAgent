package com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentMetricMapper;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.CollectTaskMetricMapper;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.AgentMetricsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AgentMetricsMysqlDAOImpl implements AgentMetricsDAO {

    @Autowired
    private AgentMetricMapper agentMetricMapper;

    @Autowired
    private CollectTaskMetricMapper collectTaskMetricMapper;

    @Override
    public Long getContainerSendCountEqualsZeroRecordSize(String containerHostName, String parentHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        return null;
    }

    @Override
    public Long getContainerSendCountGtZeroRecordSize(String containerHostName, String parentHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        return null;
    }

    @Override
    public Long getHostSendCountEqualsZeroRecordSize(String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        return null;
    }

    @Override
    public Long getHostSendCountGtZeroRecordSize(String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        return null;
    }

    @Override
    public Long getHeartbeatTimesByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        return null;
    }

    @Override
    public Long getHeartBeatTimes(Long startTime, Long endTime, String hostName) {
        return null;
    }

    @Override
    public Integer getFilePathNotExistsCountByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        return null;
    }

    @Override
    public Integer getAbnormalTruncationCountByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        return null;
    }

    @Override
    public Integer getFileDisorderCount(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
        return null;
    }

    @Override
    public Integer getSliceErrorCount(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
        return null;
    }

    @Override
    public Long getLatestCollectTime(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
        return null;
    }

    @Override
    public Long getLatestStartupTime(String hostName) {
        return null;
    }

    @Override
    public Long getHostCpuLimitDuration(Long startTime, Long endTime, String hostName) {
        return null;
    }

    @Override
    public Long getHostByteLimitDuration(Long startTime, Long endTime, String hostName) {
        return null;
    }

    @Override
    public Long getHostByteLimitDuration(Long startTime, Long endTime, String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId) {
        return null;
    }

    @Override
    public Integer getErrorLogCount(Long startTime, Long endTime, String hostName) {
        return null;
    }

    @Override
    public Integer getLatestFdUsage(String hostName) {
        return null;
    }

    @Override
    public Double getLatestCpuUsage(String hostName) {
        return null;
    }

    @Override
    public Long getGCCount(Long startTime, Long endTime, String hostName) {
        return null;
    }

    @Override
    public List<MetricPoint> getAgentCpuUsagePerMin(Long startTime, Long endTime, String hostName) {
        return agentMetricMapper.selectSinglePerMin(startTime, endTime, hostName, "cpu_usage");
    }

    @Override
    public List<MetricPoint> getAgentGCTimesPerMin(Long startTime, Long endTime, String hostName) {
        return agentMetricMapper.selectSinglePerMin(startTime, endTime, hostName, "gc_count");
    }

    @Override
    public List<MetricPoint> getAgentOutputBytesPerMin(Long startTime, Long endTime, String hostName) {
        return collectTaskMetricMapper.selectSumByHostnamePerMin(startTime, endTime, hostName, "send_byte");
    }

    @Override
    public List<MetricPoint> getAgentOutputLogsPerMin(Long startTime, Long endTime, String hostName) {
        return collectTaskMetricMapper.selectSumByHostnamePerMin(startTime, endTime, hostName, "send_count");
    }

    @Override
    public List<MetricPoint> getAgentFdUsagePerMin(Long startTime, Long endTime, String hostName) {
        return agentMetricMapper.selectSinglePerMin(startTime, endTime, hostName, "fd_count");
    }

    @Override
    public List<MetricPoint> getAgentStartupExistsPerMin(Long startTime, Long endTime, String hostName) {
        return agentMetricMapper.selectSinglePerMin(startTime, endTime, hostName, "start_time");
    }

    @Override
    public List<MetricPoint> getLogCollectTaskBytesPerMin(Long taskId, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSumByTaskIdPerMin(startTime, endTime, taskId, "send_byte");
    }

    @Override
    public List<MetricPoint> getLogCollectTaskLogCountPerMin(Long taskId, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSingleByTaskIdPerMin(startTime, endTime, taskId, "read_count");
    }

    @Override
    public List<MetricPoint> getFileLogPathNotExistsPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSinglePerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, "is_file_exist");
    }

    @Override
    public List<MetricPoint> getFileLogPathDisorderPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSinglePerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, "is_file_disorder");
    }

    @Override
    public List<MetricPoint> getFilterOutPerLogPathPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSinglePerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, "filter_out");
    }

    @Override
    public List<MetricPoint> getMinCurrentCollectTimePerLogPathPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectMinPerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, "log_time");
    }

    @Override
    public List<MetricPoint> getFileLogPathLogSliceErrorPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSinglePerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, "valid_time_config");
    }

    @Override
    public List<MetricPoint> getFileLogPathAbnormalTruncationPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSinglePerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, "filter_too_large_count");
    }
}
