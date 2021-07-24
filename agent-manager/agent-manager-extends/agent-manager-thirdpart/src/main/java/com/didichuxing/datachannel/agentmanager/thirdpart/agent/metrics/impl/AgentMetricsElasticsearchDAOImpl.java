package com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.impl;

import com.alibaba.fastjson.util.TypeUtils;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.CollectTaskMetricPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.AgentMetricField;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.CalcFunction;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricAggregate;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.AgentMetricsDAO;
import com.didichuxing.datachannel.agentmanager.thirdpart.elasticsearch.service.ElasticsearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.HistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class AgentMetricsElasticsearchDAOImpl implements AgentMetricsDAO {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Value("${agent.metrics.datasource.elasticsearch.agentMetricsIndexName}")
    private String agentMetricsIndex;

    @Value("${agent.metrics.datasource.elasticsearch.agentErrorLogIndexName}")
    private String agentErrlogIndex;

    private static final long TIME_INTERVAL = 60 * 1000;

    @Override
    public Long getContainerSendCountEqualsZeroRecordSize(String containerHostName, String parentHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        CountRequest countRequest = new CountRequest(agentMetricsIndex);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODEL_HOST_NAME.getEsValue(), containerHostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), logCollectTaskId))
                .must(QueryBuilders.termQuery(AgentMetricField.PATH_ID.getEsValue(), fileLogCollectPathId))
                .must(QueryBuilders.termQuery(AgentMetricField.HOSTNAME.getEsValue(), parentHostName))
                .must(QueryBuilders.termQuery(AgentMetricField.SEND_COUNT.getEsValue(), 0))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(heartbeatStartTime, true).to(heartbeatEndTime, true));

        countRequest.query(boolQueryBuilder);
        CountResponse countResponse = elasticsearchService.doCount(countRequest);
        return countResponse.getCount();
    }

    @Override
    public Long getContainerSendCountGtZeroRecordSize(String containerHostName, String parentHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        CountRequest countRequest = new CountRequest(agentMetricsIndex);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODEL_HOST_NAME.getEsValue(), containerHostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), logCollectTaskId))
                .must(QueryBuilders.termQuery(AgentMetricField.PATH_ID.getEsValue(), fileLogCollectPathId))
                .must(QueryBuilders.termQuery(AgentMetricField.HOSTNAME.getEsValue(), parentHostName))
                .must(QueryBuilders.rangeQuery(AgentMetricField.SEND_COUNT.getEsValue()).from(0, false))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(heartbeatStartTime, true).to(heartbeatEndTime, true));

        countRequest.query(boolQueryBuilder);
        CountResponse countResponse = elasticsearchService.doCount(countRequest);
        return countResponse.getCount();
    }

    @Override
    public Long getHostSendCountEqualsZeroRecordSize(String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        CountRequest countRequest = new CountRequest(agentMetricsIndex);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODEL_HOST_NAME.getEsValue(), logModelHostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), logCollectTaskId))
                .must(QueryBuilders.termQuery(AgentMetricField.PATH_ID.getEsValue(), fileLogCollectPathId))
                .must(QueryBuilders.termQuery(AgentMetricField.SEND_COUNT.getEsValue(), 0))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(heartbeatStartTime, true).to(heartbeatEndTime, true));

        countRequest.query(boolQueryBuilder);
        CountResponse countResponse = elasticsearchService.doCount(countRequest);
        return countResponse.getCount();
    }

    @Override
    public Long getHostSendCountGtZeroRecordSize(String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId, Long heartbeatStartTime, Long heartbeatEndTime) throws ServiceException {
        CountRequest countRequest = new CountRequest(agentMetricsIndex);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODEL_HOST_NAME.getEsValue(), logModelHostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), logCollectTaskId))
                .must(QueryBuilders.termQuery(AgentMetricField.PATH_ID.getEsValue(), fileLogCollectPathId))
                .must(QueryBuilders.rangeQuery(AgentMetricField.SEND_COUNT.getEsValue()).from(0, false))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(heartbeatStartTime, true).to(heartbeatEndTime, true));

        countRequest.query(boolQueryBuilder);
        CountResponse countResponse = elasticsearchService.doCount(countRequest);
        return countResponse.getCount();
    }

    @Override
    public Long getHeartbeatTimesByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        CountRequest countRequest = new CountRequest(agentMetricsIndex);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODEL_HOST_NAME.getEsValue(), logCollectTaskHostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), logCollectTaskId))
                .must(QueryBuilders.termQuery(AgentMetricField.PATH_ID.getEsValue(), fileLogCollectPathId))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));
        countRequest.query(boolQueryBuilder);
        CountResponse countResponse = elasticsearchService.doCount(countRequest);
        return countResponse.getCount();
    }

    @Override
    public Long getHeartBeatTimes(Long startTime, Long endTime, String hostName) {
        CountRequest countRequest = new CountRequest(agentMetricsIndex);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.HOSTNAME.getEsValue(), hostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));
        countRequest.query(boolQueryBuilder);
        CountResponse countResponse = elasticsearchService.doCount(countRequest);
        return countResponse.getCount();
    }

    @Override
    public Integer getFilePathNotExistsCountByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        Long value = selectCountByFieldName(startTime, endTime, logCollectTaskId, fileLogCollectPathId, logCollectTaskHostName, AgentMetricField.IS_FILE_EXIST.getEsValue(), false);
        if (value != null) {
            return value.intValue();
        } else {
            return 0;
        }
    }

    @Override
    public Integer getAbnormalTruncationCountByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        return (int) taskSumByFieldName(logCollectTaskId, fileLogCollectPathId, logCollectTaskHostName, startTime, endTime, AgentMetricField.FILTER_TOO_LARGE_COUNT.getEsValue());
    }

    @Override
    public Integer getFileDisorderCount(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
        CountRequest countRequest = new CountRequest(agentMetricsIndex);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODEL_HOST_NAME.getEsValue(), logModelHostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), logCollectTaskId))
                .must(QueryBuilders.termQuery(AgentMetricField.PATH_ID.getEsValue(), fileLogCollectPathId))
                .must(QueryBuilders.termQuery(AgentMetricField.IS_FILE_ORDER.getEsValue(), "1"))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));
        countRequest.query(boolQueryBuilder);
        CountResponse countResponse = elasticsearchService.doCount(countRequest);
        return (int) countResponse.getCount();
    }

    /**
     * @param startTime            心跳开始时间
     * @param endTime              心跳结束时间
     * @param logCollectTaskId     日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param logModelHostName     主机名
     * @return
     */
    @Override
    public Integer getSliceErrorCount(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
        Long value = selectCountByFieldName(startTime, endTime, logCollectTaskId, fileLogCollectPathId, logModelHostName, AgentMetricField.VALID_TIME_CONFIG.getEsValue(), true);
        if (value != null) {
            return value.intValue();
        } else {
            return 0;
        }
    }

    @Override
    public Long getLatestCollectTime(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODEL_HOST_NAME.getEsValue(), logModelHostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), logCollectTaskId))
                .must(QueryBuilders.termQuery(AgentMetricField.PATH_ID.getEsValue(), fileLogCollectPathId))
                .filter(QueryBuilders.existsQuery(AgentMetricField.LOG_TIME.getEsValue()));
        builder.query(boolQueryBuilder);
        builder.sort(AgentMetricField.HEARTBEAT_TIME.getEsValue(), SortOrder.DESC);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits.length == 0) {
            return 0L;
        }
        SearchHit hit = hits[0];
        return TypeUtils.castToLong(hit.getSourceAsMap().get(AgentMetricField.LOG_TIME.getEsValue()));
    }

    @Override
    public Long getLatestStartupTime(String hostName) {
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.filter(QueryBuilders.termQuery(AgentMetricField.HOSTNAME.getEsValue(), hostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .filter(QueryBuilders.existsQuery(AgentMetricField.START_TIME.getEsValue()));
        builder.query(boolQueryBuilder);
        builder.sort(AgentMetricField.HEARTBEAT_TIME.getEsValue(), SortOrder.DESC);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits.length == 0) {
            return 0L;
        }
        SearchHit hit = hits[0];
        return TypeUtils.castToLong(hit.getSourceAsMap().get(AgentMetricField.START_TIME.getEsValue()));
    }

    @Override
    public Long getHostCpuLimitDuration(Long startTime, Long endTime, String hostName) {
        return (long) hostSumByFieldName(startTime, endTime, hostName, AgentMetricField.LIMIT_RATE.getEsValue());
    }

    @Override
    public Long getHostByteLimitDuration(Long startTime, Long endTime, String hostName) {
        return (long) hostSumByFieldName(startTime, endTime, hostName, AgentMetricField.LIMIT_TIME.getEsValue());
    }

    @Override
    public Long getHostByteLimitDuration(Long startTime, Long endTime, String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId) {
        return (long) taskSumByFieldName(logCollectTaskId, fileLogCollectPathId, logModelHostName, startTime, endTime, AgentMetricField.LIMIT_TIME.getEsValue());
    }

    @Override
    public Integer getErrorLogCount(Long startTime, Long endTime, String hostName) {
        CountRequest countRequest = new CountRequest(agentErrlogIndex);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.HOSTNAME.getEsValue(), hostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));
        countRequest.query(boolQueryBuilder);
        CountResponse countResponse = elasticsearchService.doCount(countRequest);
        return (int) countResponse.getCount();
    }

    @Override
    public Integer getLatestFdUsage(String hostName) {
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.filter(QueryBuilders.termQuery(AgentMetricField.HOSTNAME.getEsValue(), hostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .filter(QueryBuilders.existsQuery(AgentMetricField.FD_COUNT.getEsValue()));
        builder.query(boolQueryBuilder);
        builder.sort(AgentMetricField.HEARTBEAT_TIME.getEsValue(), SortOrder.DESC);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits.length == 0) {
            return 0;
        }
        SearchHit hit = hits[0];
        return TypeUtils.castToInt(hit.getSourceAsMap().get(AgentMetricField.FD_COUNT.getEsValue()));
    }

    @Override
    public Double getLatestCpuUsage(String hostName) {
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.filter(QueryBuilders.termQuery(AgentMetricField.HOSTNAME.getEsValue(), hostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .filter(QueryBuilders.existsQuery(AgentMetricField.CPU_USAGE.getEsValue()));
        builder.query(boolQueryBuilder);
        builder.sort(AgentMetricField.HEARTBEAT_TIME.getEsValue(), SortOrder.DESC);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits.length == 0) {
            return 0D;
        }
        SearchHit hit = hits[0];
        return TypeUtils.castToDouble(hit.getSourceAsMap().get(AgentMetricField.CPU_USAGE.getEsValue()));
    }

    @Override
    public Long getLatestMemoryUsage(String hostName) {
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.filter(QueryBuilders.termQuery(AgentMetricField.HOSTNAME.getEsValue(), hostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .filter(QueryBuilders.existsQuery(AgentMetricField.MEMORY_USAGE.getEsValue()));
        builder.query(boolQueryBuilder);
        builder.sort(AgentMetricField.HEARTBEAT_TIME.getEsValue(), SortOrder.DESC);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits.length == 0) {
            return 0L;
        }
        SearchHit hit = hits[0];
        return TypeUtils.castToLong(hit.getSourceAsMap().get(AgentMetricField.MEMORY_USAGE.getEsValue()));
    }

    @Override
    public Long getGCCount(Long startTime, Long endTime, String hostName) {
        return (long) hostSumByFieldName(startTime, endTime, hostName, AgentMetricField.GC_COUNT.getEsValue());
    }

    @Override
    public List<MetricPoint> getAgentCpuUsagePerMin(Long startTime, Long endTime, String hostName) {
        return hostMetricSumByMinute(startTime, endTime, hostName, AgentMetricField.CPU_USAGE.getEsValue());
    }

    @Override
    public List<MetricPoint> getAgentMemoryUsagePerMin(Long startTime, Long endTime, String hostName) {
        return hostMetricSumByMinute(startTime, endTime, hostName, AgentMetricField.MEMORY_USAGE.getEsValue());
    }

    @Override
    public List<MetricPoint> getAgentGCTimesPerMin(Long startTime, Long endTime, String hostName) {
        return hostMetricSumByMinute(startTime, endTime, hostName, AgentMetricField.GC_COUNT.getEsValue());
    }

    @Override
    public List<MetricPoint> getAgentOutputBytesPerMin(Long startTime, Long endTime, String hostName) {
        return hostMetricSumByMinute(startTime, endTime, hostName, AgentMetricField.SEND_BYTE.getEsValue());
    }

    @Override
    public List<MetricPoint> getAgentOutputLogsPerMin(Long startTime, Long endTime, String hostName) {
        return hostMetricSumByMinute(startTime, endTime, hostName, AgentMetricField.SEND_COUNT.getEsValue());
    }

    @Override
    public List<MetricPoint> getAgentFdUsagePerMin(Long startTime, Long endTime, String hostName) {
        return hostMetricSumByMinute(startTime, endTime, hostName, AgentMetricField.FD_COUNT.getEsValue());
    }

    @Override
    public List<MetricPoint> getAgentStartupExistsPerMin(Long startTime, Long endTime, String hostName) {
        return hostMetricMaxByMinute(startTime, endTime, hostName, AgentMetricField.START_TIME.getEsValue());
    }

    @Override
    public List<MetricPoint> getLogCollectTaskBytesPerMin(Long taskId, Long startTime, Long endTime) {
        return taskMetricSumByMinute(startTime, endTime, taskId, AgentMetricField.SEND_BYTE.getEsValue());
    }

    @Override
    public List<MetricPoint> getLogCollectTaskLogCountPerMin(Long taskId, Long startTime, Long endTime) {
        return taskMetricSumByMinute(startTime, endTime, taskId, AgentMetricField.SEND_COUNT.getEsValue());
    }

    @Override
    public List<MetricPoint> getFileLogPathNotExistsPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return logModelMetricCountByMinute(startTime, endTime, logCollectTaskId, fileLogCollectPathId, logModelHostName, AgentMetricField.IS_FILE_EXIST.getEsValue(), false);
    }

    @Override
    public List<MetricPoint> getFileLogPathDisorderPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return logModelMetricCountByMinute(startTime, endTime, logCollectTaskId, fileLogCollectPathId, logModelHostName, AgentMetricField.IS_FILE_ORDER.getEsValue(), 1);
    }

    @Override
    public List<MetricPoint> getFilterOutPerLogPathPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return logModelMetricSumByMinute(startTime, endTime, logCollectTaskId, fileLogCollectPathId, logModelHostName, AgentMetricField.FILTER_OUT.getEsValue());
    }

    @Override
    public List<MetricPoint> getMinCurrentCollectTimePerLogPathPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return logModelMetricMinByMinute(startTime, endTime, logCollectTaskId, fileLogCollectPathId, logModelHostName, AgentMetricField.LOG_TIME.getEsValue());
    }

    @Override
    public List<MetricPoint> getLimitTimePerLogPathPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return logModelMetricSumByMinute(startTime, endTime, logCollectTaskId, fileLogCollectPathId, logModelHostName, AgentMetricField.LIMIT_TIME.getEsValue());
    }

    @Override
    public List<MetricPoint> getFileLogPathLogSliceErrorPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return logModelMetricCountByMinute(startTime, endTime, logCollectTaskId, fileLogCollectPathId, logModelHostName, AgentMetricField.VALID_TIME_CONFIG.getEsValue(), false);
    }

    @Override
    public List<MetricPoint> getFileLogPathAbnormalTruncationPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return logModelMetricSumByMinute(startTime, endTime, logCollectTaskId, fileLogCollectPathId, logModelHostName, AgentMetricField.FILTER_TOO_LARGE_COUNT.getEsValue());
    }

    @Override
    public List<MetricPoint> getCollectDelayPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return null;
    }

    @Override
    public List<MetricPoint> getAgentErrorLogCountPerMin(String hostName, Long startTime, Long endTime) {
        return null;
    }

    @Override
    public List<MetricPoint> queryByTask(Long logCollectTaskId, Long startTime, Long endTime, AgentMetricField column) {
        return null;
    }

    @Override
    public List<MetricPoint> queryAggregationByTask(Long logCollectTaskId, Long startTime, Long endTime, AgentMetricField column, CalcFunction method) {
        return null;
    }

    @Override
    public List<MetricPoint> queryByLogModel(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime, AgentMetricField column) {
        return null;
    }

    @Override
    public List<MetricPoint> queryAggregationByLogModel(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime, AgentMetricField column, CalcFunction method) {
        return null;
    }

    @Override
    public List<MetricPoint> queryAgent(String hostname, Long startTime, Long endTime, AgentMetricField column) {
        return null;
    }

    @Override
    public List<MetricPoint> queryAgentAggregation(String hostname, Long startTime, Long endTime, AgentMetricField column, CalcFunction method) {
        return null;
    }

    @Override
    public CollectTaskMetricPO selectLatestMetric(Long taskId) {
        return null;
    }

    private String setAggregate(SearchSourceBuilder builder, AgentMetricField field, CalcFunction method) {
        String resultName = field.getEsValue() + method.getValue();
        switch (method) {
            case MAX:
                builder.aggregation(AggregationBuilders.max(resultName).field(field.getEsValue()));
                break;
            case MIN:
                builder.aggregation(AggregationBuilders.min(resultName).field(field.getEsValue()));
                break;
            case AVG:
                builder.aggregation(AggregationBuilders.avg(resultName).field(field.getEsValue()));
                break;
            case SUM:
                builder.aggregation(AggregationBuilders.sum(resultName).field(field.getEsValue()));
                break;
            case COUNT:
                builder.aggregation(AggregationBuilders.count(resultName).field(field.getEsValue()));
                break;
            default:
                break;
        }
        return resultName;
    }

    private String setHeartbeatTimeHistogramAggregation(SearchSourceBuilder builder, AgentMetricField field, CalcFunction method) {
        return null;
    }

    private Long selectCountByFieldName(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, String fieldName, Object value) {
        CountRequest countRequest = new CountRequest(agentMetricsIndex);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODEL_HOST_NAME.getEsValue(), logModelHostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), logCollectTaskId))
                .must(QueryBuilders.termQuery(AgentMetricField.PATH_ID.getEsValue(), fileLogCollectPathId))
                .must(QueryBuilders.termQuery(fieldName, value))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));
        countRequest.query(boolQueryBuilder);
        CountResponse countResponse = elasticsearchService.doCount(countRequest);
        return countResponse.getCount();
    }

    private double hostSumByFieldName(Long startTime, Long endTime, String hostName, String fieldName) {
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.HOSTNAME.getEsValue(), hostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));
        builder.query(boolQueryBuilder);
        String sumName = setAggregate(builder, AgentMetricField.fromString(fieldName), CalcFunction.SUM);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);
        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            return 0;
        }
        Sum sum = aggregations.get(sumName);
        return sum.getValue();
    }

    private double taskSumByFieldName(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime, String fieldName) {
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODEL_HOST_NAME.getEsValue(), logModelHostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), logCollectTaskId))
                .must(QueryBuilders.termQuery(AgentMetricField.PATH_ID.getEsValue(), fileLogCollectPathId))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));
        builder.query(boolQueryBuilder);
        String sumName = setAggregate(builder, AgentMetricField.fromString(fieldName), CalcFunction.SUM);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);
        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            return 0;
        }
        Sum sum = aggregations.get(sumName);
        return sum.getValue();
    }

    private List<MetricPoint> hostMetricSumByMinute(Long startTime, Long endTime, String hostName, String field) {
        String customName = "total" + field;
        String sumName = field + "Sum";
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.HOSTNAME.getEsValue(), hostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));

        HistogramAggregationBuilder histogramAggregationBuilder = AggregationBuilders.histogram(sumName)
                .interval(TIME_INTERVAL).field(AgentMetricField.HEARTBEAT_TIME.getEsValue())
                .subAggregation(AggregationBuilders.sum(customName).field(field));
        builder.query(boolQueryBuilder);
        builder.aggregation(histogramAggregationBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);

        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            return Collections.emptyList();
        }
        Histogram histogram = aggregations.get(sumName);
        List<MetricPoint> list = new ArrayList<>();
        for (Histogram.Bucket bucket : histogram.getBuckets()) {
            Long timeKey = DateUtils.castToTimestamp(bucket.getKey());
            NumericMetricsAggregation.SingleValue value = (NumericMetricsAggregation.SingleValue) bucket.getAggregations().getAsMap().get(customName);
            MetricPoint point = new MetricPoint();
            point.setTimestamp(timeKey);
            point.setValue(value);
            list.add(point);
        }
        return list;
    }

    private List<MetricPoint> hostMetricMaxByMinute(Long startTime, Long endTime, String hostName, String field) {
        String customName = "total" + field;
        String sumName = field + "Max";
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.HOSTNAME.getEsValue(), hostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));

        HistogramAggregationBuilder histogramAggregationBuilder = AggregationBuilders.histogram(sumName)
                .interval(TIME_INTERVAL).field(AgentMetricField.HEARTBEAT_TIME.getEsValue())
                .subAggregation(AggregationBuilders.max(customName).field(field));
        builder.query(boolQueryBuilder);
        builder.aggregation(histogramAggregationBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);

        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            return Collections.emptyList();
        }
        Histogram histogram = aggregations.get(sumName);
        List<MetricPoint> list = new ArrayList<>();
        for (Histogram.Bucket bucket : histogram.getBuckets()) {
            Long timeKey = DateUtils.castToTimestamp(bucket.getKey());
            NumericMetricsAggregation.SingleValue value = (NumericMetricsAggregation.SingleValue) bucket.getAggregations().getAsMap().get(customName);
            MetricPoint point = new MetricPoint();
            point.setTimestamp(timeKey);
            point.setValue(value);
            list.add(point);
        }
        return list;
    }

    private List<MetricPoint> taskMetricSumByMinute(Long startTime, Long endTime, Long taskId, String field) {
        String customName = "total" + field;
        String sumName = field + "Sum";
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), taskId))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));

        HistogramAggregationBuilder histogramAggregationBuilder = AggregationBuilders.histogram(sumName)
                .interval(TIME_INTERVAL).field(AgentMetricField.HEARTBEAT_TIME.getEsValue())
                .subAggregation(AggregationBuilders.max(customName).field(field));
        builder.query(boolQueryBuilder);
        builder.aggregation(histogramAggregationBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);

        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            return Collections.emptyList();
        }
        Histogram histogram = aggregations.get(sumName);
        List<MetricPoint> list = new ArrayList<>();
        for (Histogram.Bucket bucket : histogram.getBuckets()) {
            Long timeKey = DateUtils.castToTimestamp(bucket.getKey());
            NumericMetricsAggregation.SingleValue value = (NumericMetricsAggregation.SingleValue) bucket.getAggregations().getAsMap().get(customName);
            MetricPoint point = new MetricPoint();
            point.setTimestamp(timeKey);
            point.setValue(value);
            list.add(point);
        }
        return list;
    }

    private List<MetricPoint> taskMetricMinByMinute(Long startTime, Long endTime, Long taskId, String field) {
        String customName = "total" + field;
        String sumName = field + "Min";
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), taskId))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));

        HistogramAggregationBuilder histogramAggregationBuilder = AggregationBuilders.histogram(sumName)
                .interval(TIME_INTERVAL).field(AgentMetricField.HEARTBEAT_TIME.getEsValue())
                .subAggregation(AggregationBuilders.max(customName).field(field));
        builder.query(boolQueryBuilder);
        builder.aggregation(histogramAggregationBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);

        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            return Collections.emptyList();
        }
        Histogram histogram = aggregations.get(sumName);
        List<MetricPoint> list = new ArrayList<>();
        for (Histogram.Bucket bucket : histogram.getBuckets()) {
            Long timeKey = DateUtils.castToTimestamp(bucket.getKey());
            NumericMetricsAggregation.SingleValue value = (NumericMetricsAggregation.SingleValue) bucket.getAggregations().getAsMap().get(customName);
            MetricPoint point = new MetricPoint();
            point.setTimestamp(timeKey);
            point.setValue(value);
            list.add(point);
        }
        return list;
    }

    private List<MetricPoint> logModelMetricSumByMinute(Long startTime, Long endTime, Long taskId, Long pathId, String logModelHostName, String field) {
        String customName = "total" + field;
        String sumName = field + "Sum";
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), taskId))
                .must(QueryBuilders.termQuery(AgentMetricField.PATH_ID.getEsValue(), pathId))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODEL_HOST_NAME.getEsValue(), logModelHostName))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));

        HistogramAggregationBuilder histogramAggregationBuilder = AggregationBuilders.histogram(sumName)
                .interval(TIME_INTERVAL).field(AgentMetricField.HEARTBEAT_TIME.getEsValue())
                .subAggregation(AggregationBuilders.max(customName).field(field));
        builder.query(boolQueryBuilder);
        builder.aggregation(histogramAggregationBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);

        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            return Collections.emptyList();
        }
        Histogram histogram = aggregations.get(sumName);
        List<MetricPoint> list = new ArrayList<>();
        for (Histogram.Bucket bucket : histogram.getBuckets()) {
            Long timeKey = DateUtils.castToTimestamp(bucket.getKey());
            NumericMetricsAggregation.SingleValue value = (NumericMetricsAggregation.SingleValue) bucket.getAggregations().getAsMap().get(customName);
            MetricPoint point = new MetricPoint();
            point.setTimestamp(timeKey);
            point.setValue(value);
            list.add(point);
        }
        return list;
    }

    private List<MetricPoint> logModelMetricMinByMinute(Long startTime, Long endTime, Long taskId, Long pathId, String logModelHostName, String field) {
        String customName = "total" + field;
        String sumName = field + "Min";
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), taskId))
                .must(QueryBuilders.termQuery(AgentMetricField.PATH_ID.getEsValue(), pathId))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODEL_HOST_NAME.getEsValue(), logModelHostName))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));

        HistogramAggregationBuilder histogramAggregationBuilder = AggregationBuilders.histogram(sumName)
                .interval(TIME_INTERVAL).field(AgentMetricField.HEARTBEAT_TIME.getEsValue())
                .subAggregation(AggregationBuilders.max(customName).field(field));
        builder.query(boolQueryBuilder);
        builder.aggregation(histogramAggregationBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);

        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            return Collections.emptyList();
        }
        Histogram histogram = aggregations.get(sumName);
        List<MetricPoint> list = new ArrayList<>();
        for (Histogram.Bucket bucket : histogram.getBuckets()) {
            Long timeKey = DateUtils.castToTimestamp(bucket.getKey());
            NumericMetricsAggregation.SingleValue value = (NumericMetricsAggregation.SingleValue) bucket.getAggregations().getAsMap().get(customName);
            MetricPoint point = new MetricPoint();
            point.setTimestamp(timeKey);
            point.setValue(value);
            list.add(point);
        }
        return list;
    }

    private List<MetricPoint> logModelMetricCountByMinute(Long startTime, Long endTime, Long taskId, Long pathId, String logModelHostName, String field, Object fieldValue) {
        String customName = "total" + field;
        String sumName = field + "Count";
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), taskId))
                .must(QueryBuilders.termQuery(AgentMetricField.PATH_ID.getEsValue(), pathId))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODEL_HOST_NAME.getEsValue(), logModelHostName))
                .must(QueryBuilders.termQuery(field, fieldValue))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));

        HistogramAggregationBuilder histogramAggregationBuilder = AggregationBuilders.histogram(sumName)
                .interval(TIME_INTERVAL).field(AgentMetricField.HEARTBEAT_TIME.getEsValue())
                .subAggregation(AggregationBuilders.max(customName).field(field));
        builder.query(boolQueryBuilder);
        builder.aggregation(histogramAggregationBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);

        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            return Collections.emptyList();
        }
        Histogram histogram = aggregations.get(sumName);
        List<MetricPoint> list = new ArrayList<>();
        for (Histogram.Bucket bucket : histogram.getBuckets()) {
            Long timeKey = DateUtils.castToTimestamp(bucket.getKey());
            NumericMetricsAggregation.SingleValue value = (NumericMetricsAggregation.SingleValue) bucket.getAggregations().getAsMap().get(customName);
            MetricPoint point = new MetricPoint();
            point.setTimestamp(timeKey);
            point.setValue(value);
            list.add(point);
        }
        return list;
    }

}
