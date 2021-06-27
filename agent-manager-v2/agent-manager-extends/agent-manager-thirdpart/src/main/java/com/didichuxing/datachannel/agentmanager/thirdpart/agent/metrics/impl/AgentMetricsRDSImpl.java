package com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.AgentMetricPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.CollectTaskMetricPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentMetricMapper;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.CollectTaskMetricMapper;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.AgentMetricsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AgentMetricsRDSImpl implements AgentMetricsDAO {

    @Autowired
    private AgentMetricMapper agentMetricMapper;

    @Autowired
    private CollectTaskMetricMapper collectTaskMetricMapper;

    @Override
    public Long getContainerSendCountEqualsZeroRecordSize(String containerHostName, String parentHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        return collectTaskMetricMapper.selectContainerCountEqualsZero(heartbeatStartTime, heartbeatEndTime, logCollectTaskId, parentHostName, containerHostName, "send_count");
    }

    @Override
    public Long getContainerSendCountGtZeroRecordSize(String containerHostName, String parentHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        return collectTaskMetricMapper.selectContainerCountGtZero(heartbeatStartTime, heartbeatEndTime, logCollectTaskId, parentHostName, containerHostName, "send_count");
    }

    @Override
    public Long getHostSendCountEqualsZeroRecordSize(String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        return collectTaskMetricMapper.selectSingleCountEqualsZero(heartbeatStartTime, heartbeatEndTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, "send_count");
    }

    @Override
    public Long getHostSendCountGtZeroRecordSize(String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        return collectTaskMetricMapper.selectSingleCountGtZero(heartbeatStartTime, heartbeatEndTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, "send_count");
    }

    @Override
    public Long getHeartbeatTimesByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        return collectTaskMetricMapper.selectHeartbeatCount(startTime, endTime, logCollectTaskId, logCollectTaskHostName, fileLogCollectPathId);
    }

    @Override
    public Long getHeartBeatTimes(Long startTime, Long endTime, String hostName) {
        return collectTaskMetricMapper.selectHeartbeatCountByHostname(startTime, endTime, hostName);
    }

    @Override
    public Integer getFilePathNotExistsCountByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        Long value = collectTaskMetricMapper.selectSingleCountWithTerm(startTime, endTime, logCollectTaskId, logCollectTaskHostName, fileLogCollectPathId, "is_file_exist", false);
        return value == null ? 0 : value.intValue();
    }

    @Override
    public Integer getAbnormalTruncationCountByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        Long value = collectTaskMetricMapper.selectSingleSum(startTime, endTime, logCollectTaskId, logCollectTaskHostName, fileLogCollectPathId, "filter_too_large_count");
        return value == null ? 0 : value.intValue();
    }

    @Override
    public Integer getFileDisorderCount(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
        List<CollectTaskMetricPO> list = collectTaskMetricMapper.selectSome(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId);
        int count = 0;
        for (CollectTaskMetricPO collectTaskMetricPO : list) {
            String files = collectTaskMetricPO.getCollectFiles();
            count += files.split("\"isFileOrder\":1").length - 1;
        }
        return count;
    }

    @Override
    public Integer getSliceErrorCount(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
        Long value = collectTaskMetricMapper.selectSingleCountWithTerm(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, "valid_time_config", true);
        return value == null ? 0 : value.intValue();
    }

    @Override
    public Long getLatestCollectTime(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
        return (Long) collectTaskMetricMapper.selectSingleMax(logCollectTaskId, logModelHostName, fileLogCollectPathId, "log_time");
    }

    @Override
    public Long getLatestStartupTime(String hostName) {
        return agentMetricMapper.selectMaxByHostname(hostName, "start_time");
    }

    @Override
    public Long getHostCpuLimitDuration(Long startTime, Long endTime, String hostName) {
        return collectTaskMetricMapper.selectSumByHostname(startTime, endTime, hostName, "limit_rate");
    }

    @Override
    public Long getHostByteLimitDuration(Long startTime, Long endTime, String hostName) {
        return collectTaskMetricMapper.selectSumByHostname(startTime, endTime, hostName, "limit_time");
    }

    @Override
    public Long getHostByteLimitDuration(Long startTime, Long endTime, String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId) {
        return collectTaskMetricMapper.selectSingleSum(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, "limit_time");
    }

    @Override
    public Integer getErrorLogCount(Long startTime, Long endTime, String hostName) {
        return 0;
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
        return agentMetricMapper.selectSum(startTime, endTime, hostName, "gc_count");
    }

    @Override
    public List<MetricPoint> getAgentCpuUsagePerMin(Long startTime, Long endTime, String hostName) {
        return agentMetricMapper.selectSinglePerMin(startTime, endTime, hostName, "cpu_usage");
    }

    @Override
    public List<MetricPoint> getAgentMemoryUsagePerMin(Long startTime, Long endTime, String hostName) {
        List<MetricPoint> graph = agentMetricMapper.selectSinglePerMin(startTime, endTime, hostName, "memory_usage");
        for (MetricPoint metricPoint : graph) {
            metricPoint.setValue(((Number) metricPoint.getValue()).longValue() / 1024 / 1024);
        }
        return graph;
    }

    @Override
    public List<MetricPoint> getAgentGCTimesPerMin(Long startTime, Long endTime, String hostName) {
        return agentMetricMapper.selectSinglePerMin(startTime, endTime, hostName, "gc_count");
    }

    @Override
    public List<MetricPoint> getAgentOutputBytesPerMin(Long startTime, Long endTime, String hostName) {
        List<MetricPoint> graph = collectTaskMetricMapper.selectSumByHostnamePerMin(startTime, endTime, hostName, "send_byte");
        for (MetricPoint metricPoint : graph) {
            metricPoint.setValue(((Number) metricPoint.getValue()).longValue() / 1024 / 1024);
        }
        return graph;
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
        List<MetricPoint> graph = collectTaskMetricMapper.selectSumByTaskIdPerMin(startTime, endTime, taskId, "send_byte");
        // 修改单位为MB
        for (MetricPoint metricPoint : graph) {
            metricPoint.setValue(((Number) metricPoint.getValue()).longValue() / 1024 / 1024);
        }
        return graph;
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
    public List<MetricPoint> getLimitTimePerLogPathPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSinglePerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, "limit_time");
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
