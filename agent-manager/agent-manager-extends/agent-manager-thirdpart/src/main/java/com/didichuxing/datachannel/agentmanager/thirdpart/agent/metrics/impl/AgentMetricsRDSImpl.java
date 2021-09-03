package com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentMetricDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.metrics.DashBoardStatisticsDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.CollectTaskMetricDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.ErrorLogPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.AgentMetricField;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.AgentMetricPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.CollectTaskMetricPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.CalcFunction;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentMetricMapper;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.CollectTaskMetricMapper;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.ErrorLogMapper;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.AgentMetricsDAO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.List;

public class AgentMetricsRDSImpl implements AgentMetricsDAO {

    @Autowired
    private AgentMetricMapper agentMetricMapper;

    @Autowired
    private CollectTaskMetricMapper collectTaskMetricMapper;

    @Autowired
    private ErrorLogMapper errorLogMapper;

    @Override
    public void writeMetrics(ConsumerRecords<String, String> records) {
        for (ConsumerRecord<String, String> record : records) {
            JSONObject object = JSON.parseObject(record.value());
            if (object.getInteger(AgentMetricField.LOG_MODE_ID.getEsValue()) < 0) {
                AgentMetricDO agentMetric = JSON.parseObject(record.value(), AgentMetricDO.class);
                AgentMetricPO agentMetricPO = ConvertUtil.obj2Obj(agentMetric, AgentMetricPO.class);
                agentMetricMapper.insertSelective(agentMetricPO);
            } else {
                CollectTaskMetricDO collectTaskMetric = JSON.parseObject(record.value(), CollectTaskMetricDO.class);
                CollectTaskMetricPO collectTaskMetricPO = ConvertUtil.obj2Obj(collectTaskMetric, CollectTaskMetricPO.class);
                collectTaskMetricMapper.insertSelective(collectTaskMetricPO);
            }
        }
    }

    @Override
    public void writeErrors(ConsumerRecords<String, String> records) {
        for (ConsumerRecord<String, String> record : records) {
            ErrorLogPO errorLogPO = JSON.parseObject(record.value(), ErrorLogPO.class);
            errorLogMapper.insertSelective(errorLogPO);
        }
    }

    @Override
    public Long getContainerSendCountEqualsZeroRecordSize(String containerHostName, String parentHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        return collectTaskMetricMapper.selectContainerCountEqualsZero(heartbeatStartTime, heartbeatEndTime, logCollectTaskId, parentHostName, containerHostName, AgentMetricField.SEND_COUNT.getRdsValue());
    }

    @Override
    public Long getContainerSendCountGtZeroRecordSize(String containerHostName, String parentHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        return collectTaskMetricMapper.selectContainerCountGtZero(heartbeatStartTime, heartbeatEndTime, logCollectTaskId, parentHostName, containerHostName, AgentMetricField.SEND_COUNT.getRdsValue());
    }

    @Override
    public Long getHostSendCountEqualsZeroRecordSize(String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        return collectTaskMetricMapper.selectSingleCountEqualsZero(heartbeatStartTime, heartbeatEndTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricField.SEND_COUNT.getRdsValue());
    }

    @Override
    public Long getHostSendCountGtZeroRecordSize(String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        return collectTaskMetricMapper.selectSingleCountGtZero(heartbeatStartTime, heartbeatEndTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricField.SEND_COUNT.getRdsValue());
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
        Long value = collectTaskMetricMapper.selectSingleCountWithTerm(startTime, endTime, logCollectTaskId, logCollectTaskHostName, fileLogCollectPathId, AgentMetricField.IS_FILE_EXIST.getRdsValue(), false);
        return value == null ? 0 : value.intValue();
    }

    @Override
    public Integer getAbnormalTruncationCountByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        Long value = collectTaskMetricMapper.selectSingleSum(startTime, endTime, logCollectTaskId, logCollectTaskHostName, fileLogCollectPathId, AgentMetricField.FILTER_TOO_LARGE_COUNT.getRdsValue());
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
            count += files.split("\""+ AgentMetricField.IS_FILE_ORDER.getRdsValue() +"\":1").length - 1;
        }
        return count;
    }

    @Override
    public Integer getSliceErrorCount(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
        Long value = collectTaskMetricMapper.selectSingleCountWithTerm(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricField.VALID_TIME_CONFIG.getRdsValue(), true);
        return value == null ? 0 : value.intValue();
    }

    @Override
    public Long getLatestCollectTime(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
        return (Long) collectTaskMetricMapper.selectSingleMax(logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricField.LOG_TIME.getRdsValue());
    }

    @Override
    public Long getLatestStartupTime(String hostName) {
        return agentMetricMapper.selectMaxByHostname(hostName, AgentMetricField.START_TIME.getRdsValue());
    }

    @Override
    public Long getHostCpuLimitDuration(Long startTime, Long endTime, String hostName) {
        return collectTaskMetricMapper.selectSumByHostname(startTime, endTime, hostName, AgentMetricField.LIMIT_RATE.getRdsValue());
    }

    @Override
    public Long getHostByteLimitDuration(Long startTime, Long endTime, String hostName) {
        return collectTaskMetricMapper.selectSumByHostname(startTime, endTime, hostName, AgentMetricField.LIMIT_TIME.getRdsValue());
    }

    @Override
    public Long getHostByteLimitDuration(Long startTime, Long endTime, String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId) {
        return collectTaskMetricMapper.selectSingleSum(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricField.LIMIT_TIME.getRdsValue());
    }

    @Override
    public Integer getErrorLogCount(Long startTime, Long endTime, String hostName) {
        Long value = errorLogMapper.selectCount(startTime, endTime, hostName);
        return value == null ? 0 : value.intValue();
    }

    @Override
    public AgentMetricPO selectLatestByHostname(String hostname) {
        return agentMetricMapper.selectLatestByHostname(hostname);
    }

    @Override
    public Long getGCCount(Long startTime, Long endTime, String hostName) {
        return agentMetricMapper.selectSum(startTime, endTime, hostName, AgentMetricField.GC_COUNT.getRdsValue());
    }

    @Override
    public List<MetricPoint> getAgentCpuUsagePerMin(Long startTime, Long endTime, String hostName) {
        return agentMetricMapper.selectAvgPerMin(startTime, endTime, hostName, AgentMetricField.CPU_USAGE.getRdsValue());
    }

    @Override
    public List<MetricPoint> getAgentMemoryUsagePerMin(Long startTime, Long endTime, String hostName) {
        List<MetricPoint> graph = agentMetricMapper.selectAvgPerMin(startTime, endTime, hostName, AgentMetricField.MEMORY_USAGE.getRdsValue());
        for (MetricPoint metricPoint : graph) {
            BigDecimal b1 = TypeUtils.castToBigDecimal(metricPoint.getValue());
            BigDecimal b2 = b1.divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP);
            metricPoint.setValue(b2);
        }
        return graph;
    }

    @Override
    public List<MetricPoint> getAgentGCTimesPerMin(Long startTime, Long endTime, String hostName) {
        return agentMetricMapper.selectSumPerMin(startTime, endTime, hostName, AgentMetricField.GC_COUNT.getRdsValue());
    }

    @Override
    public List<MetricPoint> getAgentOutputBytesPerMin(Long startTime, Long endTime, String hostName) {
        List<MetricPoint> graph = collectTaskMetricMapper.selectSumByHostnamePerMin(startTime, endTime, hostName, AgentMetricField.SEND_BYTE.getRdsValue());
        for (MetricPoint metricPoint : graph) {
            BigDecimal b1 = TypeUtils.castToBigDecimal(metricPoint.getValue());
            BigDecimal b2 = b1.divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP);
            metricPoint.setValue(b2);
        }
        return graph;
    }

    @Override
    public List<MetricPoint> getAgentOutputLogsPerMin(Long startTime, Long endTime, String hostName) {
        return collectTaskMetricMapper.selectSumByHostnamePerMin(startTime, endTime, hostName, AgentMetricField.SEND_COUNT.getRdsValue());
    }

    @Override
    public List<MetricPoint> getAgentFdUsagePerMin(Long startTime, Long endTime, String hostName) {
        List<MetricPoint> graph = agentMetricMapper.selectAvgPerMin(startTime, endTime, hostName, AgentMetricField.FD_COUNT.getRdsValue());
        for (MetricPoint metricPoint : graph) {
            Integer i = TypeUtils.castToInt(metricPoint.getValue());
            metricPoint.setValue(i);
        }
        return graph;
    }

    @Override
    public List<MetricPoint> getAgentStartupExistsPerMin(Long startTime, Long endTime, String hostName) {
        return agentMetricMapper.selectSinglePerMin(startTime, endTime, hostName, AgentMetricField.START_TIME.getRdsValue());
    }

    @Override
    public List<MetricPoint> getLogCollectTaskBytesPerMin(Long taskId, Long startTime, Long endTime) {
        List<MetricPoint> graph = collectTaskMetricMapper.selectSumByTaskIdPerMin(startTime, endTime, taskId, AgentMetricField.SEND_BYTE.getRdsValue());
        for (MetricPoint metricPoint : graph) {
            BigDecimal b1 = TypeUtils.castToBigDecimal(metricPoint.getValue());
            BigDecimal b2 = b1.divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP);
            metricPoint.setValue(b2);
        }
        return graph;
    }

    @Override
    public List<MetricPoint> getLogCollectTaskLogCountPerMin(Long taskId, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSumByTaskIdPerMin(startTime, endTime, taskId, AgentMetricField.READ_COUNT.getRdsValue());
    }

    @Override
    public List<MetricPoint> getFileLogPathNotExistsPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSinglePerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricField.IS_FILE_EXIST.getRdsValue());
    }

    @Override
    public List<MetricPoint> getFileLogPathDisorderPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        List<MetricPoint> graph = collectTaskMetricMapper.selectFileDisorderPerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId);
        for (MetricPoint metricPoint : graph) {
            String collectFiles = TypeUtils.castToString(metricPoint.getValue());
            metricPoint.setValue(collectFiles.matches( "\"" + AgentMetricField.IS_FILE_ORDER.getRdsValue() + "\":1") ? 1 : 0);
        }
        return graph;
    }

    @Override
    public List<MetricPoint> getFilterOutPerLogPathPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSinglePerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricField.FILTER_OUT.getRdsValue());
    }

    @Override
    public List<MetricPoint> getMinCurrentCollectTimePerLogPathPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectMinPerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricField.LOG_TIME.getRdsValue());
    }

    @Override
    public List<MetricPoint> getLimitTimePerLogPathPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSumPerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricField.LIMIT_TIME.getRdsValue());
    }

    @Override
    public List<MetricPoint> getFileLogPathLogSliceErrorPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        List<MetricPoint> graph = collectTaskMetricMapper.selectFileDisorderPerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId);
        for (MetricPoint metricPoint : graph) {
            String collectFiles = TypeUtils.castToString(metricPoint.getValue());
            metricPoint.setValue(collectFiles.matches( "\"" + AgentMetricField.VALID_TIME_CONFIG.getRdsValue() + "\":false") ? 1 : 0);
        }
        return graph;
    }

    @Override
    public List<MetricPoint> getFileLogPathAbnormalTruncationPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectSinglePerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricField.FILTER_TOO_LARGE_COUNT.getRdsValue());
    }

    @Override
    public List<MetricPoint> getCollectDelayPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return collectTaskMetricMapper.selectDelayTimePerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId);
    }

    @Override
    public List<MetricPoint> getAgentErrorLogCountPerMin(String hostname, Long startTime, Long endTime) {
        return agentMetricMapper.selectAgentErrorLogCountPerMin(hostname, startTime, endTime);
    }

    @Override
    public List<MetricPoint> queryByTask(Long logCollectTaskId, Long startTime, Long endTime, AgentMetricField column) {
        return collectTaskMetricMapper.selectByTask(logCollectTaskId, startTime, endTime, column.getRdsValue());
    }

    @Override
    public List<MetricPoint> queryAggregationByTask(Long logCollectTaskId, Long startTime, Long endTime, AgentMetricField column, CalcFunction method, int step) {
        return collectTaskMetricMapper.selectAggregationByTask(logCollectTaskId, startTime, endTime, column.getRdsValue(), method.getValue(), step);
    }

    @Override
    public List<MetricPoint> queryAggregationByHostname(String hostname, Long startTime, Long endTime, AgentMetricField column, CalcFunction method, int step) {
        return collectTaskMetricMapper.selectAggregationByHostname(hostname, startTime, endTime, column.getRdsValue(), method.getValue(), step);
    }

    @Override
    public List<MetricPoint> queryByLogModel(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime, AgentMetricField column) {
        return collectTaskMetricMapper.selectByLogModel(logCollectTaskId, logModelHostName, fileLogCollectPathId, startTime, endTime, column.getRdsValue());
    }

    @Override
    public List<MetricPoint> queryAggregationByLogModel(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime, AgentMetricField column, CalcFunction method, int step) {
        return collectTaskMetricMapper.selectAggregationByLogModel(logCollectTaskId, logModelHostName, fileLogCollectPathId, startTime, endTime, column.getRdsValue(), method.getValue(), step);
    }

    @Override
    public List<MetricPoint> queryAgent(String hostname, Long startTime, Long endTime, AgentMetricField column) {
        return agentMetricMapper.selectGraph(hostname, startTime, endTime, column.getRdsValue());
    }

    @Override
    public List<MetricPoint> queryAgentAggregation(String hostname, Long startTime, Long endTime, AgentMetricField column, CalcFunction method, int step) {
        return agentMetricMapper.selectAggregation(hostname, startTime, endTime, column.getRdsValue(), method.getValue(), step);
    }

    @Override
    public Double queryAggregationForAll(Long startTime, Long endTime, AgentMetricField column, CalcFunction method) {
        return collectTaskMetricMapper.selectAggregationForAll(startTime, endTime, column.getRdsValue(), method.getValue());
    }

    @Override
    public CollectTaskMetricPO selectLatestMetric(Long taskId) {
        return collectTaskMetricMapper.selectLatest(taskId);
    }

    @Override
    public List<CollectTaskMetricPO> queryLatestMetrics(Long time, int step) {
        return collectTaskMetricMapper.selectLatestMetrics(time, step);
    }

    @Override
    public List<AgentMetricPO> queryLatestAgentMetrics(Long time, int step) {
        return agentMetricMapper.selectLatestMetrics(time, step);
    }

    @Override
    public List<DashBoardStatisticsDO> groupByKeyAndMinuteLogCollectTaskMetric(Long startTime, Long endTime, String key, String function, String metric) {
        return collectTaskMetricMapper.groupByKeyAndMinute(startTime, endTime, key, function, metric);
    }

    @Override
    public List<MetricPoint> queryAggregationByAgentFromLogCollectTaskMetrics(String agentHostName, Long startTime, Long endTime, AgentMetricField column, CalcFunction function) {
        return collectTaskMetricMapper.selectAggregationByAgent(agentHostName, startTime, endTime, column.getRdsValue(), function.getValue());
    }

    @Override
    public List<DashBoardStatisticsDO> groupByKeyAndMinuteAgentMetric(Long startTime, Long endTime, String key, String function, String metric) {
        return agentMetricMapper.groupByKeyAndMinute(startTime, endTime, key, function, metric);
    }

    @Override
    public List<MetricPoint> queryAggregationByAgentFromAgentMetrics(String agentHostName, Long startTime, Long endTime, AgentMetricField column, CalcFunction function) {
        return agentMetricMapper.selectAggregationByAgent(agentHostName, startTime, endTime, column.getRdsValue(), function.getValue());
    }

    @Override
    public List<MetricPoint> queryAggregationByAgent(String agentHostName, Long startTime, Long endTime, AgentMetricField column, CalcFunction function) {
        return collectTaskMetricMapper.selectAggregationByAgent(agentHostName, startTime, endTime, column.getRdsValue(), function.getValue());
    }

    @Override
    public List<MetricPoint> queryAggregationGroupByMinute(Long startTime, Long endTime, AgentMetricField column, CalcFunction function) {
        return collectTaskMetricMapper.selectAggregationGroupByMinute(startTime, endTime, column.getRdsValue(), function.getValue());
    }

}
