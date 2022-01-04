package com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.metrics.DashBoardStatisticsDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.CollectTaskMetricDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.ErrorLogPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.AgentMetricField;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.CalcFunction;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.ErrorLogMapper;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.AgentMetricsDAO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

public class AgentMetricsRDSImpl implements AgentMetricsDAO {

    @Autowired
    private ErrorLogMapper errorLogMapper;

    @Override
    public void writeMetrics(ConsumerRecords<String, String> records) {
//        for (ConsumerRecord<String, String> record : records) {
//            JSONObject object = JSON.parseObject(record.value());
//            if (object.getInteger(AgentMetricField.LOG_MODE_ID.getEsValue()) < 0) {
//                AgentMetricPO agentMetricPO = JSON.parseObject(record.value(), AgentMetricPO.class);
//                agentMetricMapper.insertSelective(agentMetricPO);
//            } else {
//                CollectTaskMetricDO collectTaskMetric = JSON.parseObject(record.value(), CollectTaskMetricDO.class);
//                CollectTaskMetricPO collectTaskMetricPO = ConvertUtil.obj2Obj(collectTaskMetric, CollectTaskMetricPO.class);
//                collectTaskMetricMapper.insertSelective(collectTaskMetricPO);
//            }
//        }
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
//        return collectTaskMetricMapper.selectContainerCountEqualsZero(heartbeatStartTime, heartbeatEndTime, logCollectTaskId, parentHostName, containerHostName, AgentMetricField.SEND_COUNT.getRdsValue());
        return null;
    }

    @Override
    public Long getContainerSendCountGtZeroRecordSize(String containerHostName, String parentHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
//        return collectTaskMetricMapper.selectContainerCountGtZero(heartbeatStartTime, heartbeatEndTime, logCollectTaskId, parentHostName, containerHostName, AgentMetricField.SEND_COUNT.getRdsValue());
        return null;
    }

    @Override
    public Long getHostSendCountEqualsZeroRecordSize(String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
//        return collectTaskMetricMapper.selectSingleCountEqualsZero(heartbeatStartTime, heartbeatEndTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricField.SEND_COUNT.getRdsValue());
        return null;
    }

    @Override
    public Long getHostSendCountGtZeroRecordSize(String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
//        return collectTaskMetricMapper.selectSingleCountGtZero(heartbeatStartTime, heartbeatEndTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricField.SEND_COUNT.getRdsValue());
        return null;
    }

    @Override
    public Long getHeartbeatTimesByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
//        return collectTaskMetricMapper.selectHeartbeatCount(startTime, endTime, logCollectTaskId, logCollectTaskHostName, fileLogCollectPathId);
        return null;
    }

    @Override
    public Long getHeartBeatTimes(Long startTime, Long endTime, String hostName) {
//        return agentMetricMapper.selectHeartbeatCount(startTime, endTime, hostName);
        return null;
    }

    @Override
    public Integer getFilePathNotExistsCountByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
//        Long value = collectTaskMetricMapper.selectSingleCountWithTerm(startTime, endTime, logCollectTaskId, logCollectTaskHostName, fileLogCollectPathId, AgentMetricField.IS_FILE_EXIST.getRdsValue(), false);
//        return value == null ? 0 : value.intValue();
        return null;
    }

    @Override
    public Integer getAbnormalTruncationCountByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
//        Long value = collectTaskMetricMapper.selectSingleSum(startTime, endTime, logCollectTaskId, logCollectTaskHostName, fileLogCollectPathId, AgentMetricField.FILTER_TOO_LARGE_COUNT.getRdsValue());
//        return value == null ? 0 : value.intValue();
        return null;
    }

    @Override
    public Integer getFileDisorderCount(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
//        List<CollectTaskMetricPO> list = collectTaskMetricMapper.selectSome(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId);
//        if (list == null || list.isEmpty()) {
//            return 0;
//        }
//        int count = 0;
//        for (CollectTaskMetricPO collectTaskMetricPO : list) {
//            String files = collectTaskMetricPO.getCollectFiles();
//            if (StringUtils.isEmpty(files) || "[]".equals(files)) {
//                continue;
//            }
//            count += files.split("\""+ AgentMetricField.IS_FILE_ORDER.getRdsValue() +"\":1").length - 1;
//        }
//        return count;
        return null;
    }

    @Override
    public Integer getSliceErrorCount(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
//        Long value = collectTaskMetricMapper.selectSingleCountWithTerm(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricField.VALID_TIME_CONFIG.getRdsValue(), true);
//        return value == null ? 0 : value.intValue();
        return null;
    }

    @Override
    public Long getHostCpuLimitDuration(Long startTime, Long endTime, String hostName) {
//        return collectTaskMetricMapper.selectSumByHostname(startTime, endTime, hostName, AgentMetricField.LIMIT_RATE.getRdsValue());
        return null;
    }

    @Override
    public Long getHostByteLimitDuration(Long startTime, Long endTime, String hostName) {
//        return collectTaskMetricMapper.selectSumByHostname(startTime, endTime, hostName, AgentMetricField.LIMIT_TIME.getRdsValue());
        return null;
    }

    @Override
    public Long getHostByteLimitDuration(Long startTime, Long endTime, String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId) {
//        return collectTaskMetricMapper.selectSingleSum(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricField.LIMIT_TIME.getRdsValue());
        return null;
    }

    @Override
    public Integer getErrorLogCount(Long startTime, Long endTime, String hostName) {
        Long value = errorLogMapper.selectCount(startTime, endTime, hostName);
        return value == null ? 0 : value.intValue();
    }

    @Override
    public Long getGCCount(Long startTime, Long endTime, String hostName) {
//        return agentMetricMapper.selectSum(startTime, endTime, hostName, AgentMetricField.GC_COUNT.getRdsValue());
        return null;
    }

    @Override
    public List<MetricPoint> getAgentStartupExistsPerMin(Long startTime, Long endTime, String hostName) {
//        return agentMetricMapper.selectSinglePerMin(startTime, endTime, hostName, AgentMetricField.START_TIME.getRdsValue());
        return null;
    }

    @Override
    public List<MetricPoint> getFileLogPathNotExistsPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
//        return collectTaskMetricMapper.selectSinglePerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId, AgentMetricField.IS_FILE_EXIST.getRdsValue());
        return null;
    }

    @Override
    public List<MetricPoint> getFileLogPathDisorderPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
//        List<MetricPoint> graph = collectTaskMetricMapper.selectFileDisorderPerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId);
//        for (MetricPoint metricPoint : graph) {
//            String collectFiles = TypeUtils.castToString(metricPoint.getValue());
//            metricPoint.setValue(collectFiles.matches( "\"" + AgentMetricField.IS_FILE_ORDER.getRdsValue() + "\":1") ? 1 : 0);
//        }
//        return graph;
        return null;
    }

    @Override
    public List<MetricPoint> getFileLogPathLogSliceErrorPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
//        List<MetricPoint> graph = collectTaskMetricMapper.selectFileDisorderPerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId);
//        for (MetricPoint metricPoint : graph) {
//            String collectFiles = TypeUtils.castToString(metricPoint.getValue());
//            metricPoint.setValue(collectFiles.matches( "\"" + AgentMetricField.VALID_TIME_CONFIG.getRdsValue() + "\":false") ? 1 : 0);
//        }
//        return graph;
        return null;
    }

    @Override
    public List<MetricPoint> getCollectDelayPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
//        return collectTaskMetricMapper.selectDelayTimePerMin(startTime, endTime, logCollectTaskId, logModelHostName, fileLogCollectPathId);
        return null;
    }

    @Override
    public List<MetricPoint> getAgentErrorLogCountPerMin(String hostname, Long startTime, Long endTime) {
//        return agentMetricMapper.selectAgentErrorLogCountPerMin(hostname, startTime, endTime);
        return null;
    }

    @Override
    public List<MetricPoint> queryByTask(Long logCollectTaskId, Long startTime, Long endTime, AgentMetricField column) {
//        return collectTaskMetricMapper.selectByTask(logCollectTaskId, startTime, endTime, column.getRdsValue());
        return null;
    }

    @Override
    public List<MetricPoint> queryAggregationByTask(Long logCollectTaskId, Long startTime, Long endTime, AgentMetricField column, CalcFunction method, int step) {
//        return collectTaskMetricMapper.selectAggregationByTask(logCollectTaskId, startTime, endTime, column.getRdsValue(), method.getValue(), step);
        return null;
    }

    @Override
    public List<MetricPoint> queryAggregationByHostname(String hostname, Long startTime, Long endTime, AgentMetricField column, CalcFunction method, int step) {
//        return collectTaskMetricMapper.selectAggregationByHostname(hostname, startTime, endTime, column.getRdsValue(), method.getValue(), step);
        return null;
    }

    @Override
    public List<MetricPoint> queryByLogModel(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime, AgentMetricField column) {
//        return collectTaskMetricMapper.selectByLogModel(logCollectTaskId, logModelHostName, fileLogCollectPathId, startTime, endTime, column.getRdsValue());
        return null;
    }

    @Override
    public List<MetricPoint> queryAggregationByLogModel(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime, AgentMetricField column, CalcFunction method, int step) {
//        return collectTaskMetricMapper.selectAggregationByLogModel(logCollectTaskId, logModelHostName, fileLogCollectPathId, startTime, endTime, column.getRdsValue(), method.getValue(), step);
        return null;
    }

    @Override
    public List<MetricPoint> queryAgent(String hostname, Long startTime, Long endTime, AgentMetricField column) {
//        return agentMetricMapper.selectGraph(hostname, startTime, endTime, column.getRdsValue());
        return null;
    }

    @Override
    public List<MetricPoint> queryAgentAggregation(String hostname, Long startTime, Long endTime, AgentMetricField column, CalcFunction method, int step) {
//        return agentMetricMapper.selectAggregation(hostname, startTime, endTime, column.getRdsValue(), method.getValue(), step);
        return null;
    }

    @Override
    public Double queryAggregationForAll(Long startTime, Long endTime, AgentMetricField column, CalcFunction method) {
//        return collectTaskMetricMapper.selectAggregationForAll(startTime, endTime, column.getRdsValue(), method.getValue());
        return null;
    }

    @Override
    public MetricsLogCollectTaskPO selectLatestMetric(Long taskId) {
//        return collectTaskMetricMapper.selectLatest(taskId);
        return null;
    }

    @Override
    public List<DashBoardStatisticsDO> groupByKeyAndMinuteLogCollectTaskMetric(Long startTime, Long endTime, String key, String function, String metric) {
//        return collectTaskMetricMapper.groupByKeyAndMinute(startTime, endTime, key, function, metric);
        return null;
    }

    @Override
    public List<DashBoardStatisticsDO> groupByKeyAndMinuteAgentMetric(Long startTime, Long endTime, String key, String function, String metric) {
//        return agentMetricMapper.groupByKeyAndMinute(startTime, endTime, key, function, metric);
        return null;
    }

}
