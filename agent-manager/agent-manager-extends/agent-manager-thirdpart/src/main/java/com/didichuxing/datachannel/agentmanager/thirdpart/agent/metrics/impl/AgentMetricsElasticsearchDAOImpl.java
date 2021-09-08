package com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.impl;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.metrics.DashBoardStatisticsDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.AgentMetricPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.CollectTaskMetricPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.AgentMetricField;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.CalcFunction;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.constant.MetricConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.AgentMetricsDAO;
import com.didichuxing.datachannel.agentmanager.thirdpart.elasticsearch.service.ElasticsearchService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.HistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;
import org.elasticsearch.search.aggregations.metrics.Stats;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AgentMetricsElasticsearchDAOImpl implements AgentMetricsDAO {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Value("${agent.metrics.datasource.elasticsearch.agentMetricsIndexName}")
    private String agentMetricsIndex;

    @Value("${agent.metrics.datasource.elasticsearch.agentErrorLogIndexName}")
    private String agentErrorLogIndex;

    @Override
    public void writeMetrics(ConsumerRecords<String, String> records) {
        BulkRequest bulkRequest = new BulkRequest();
        for (ConsumerRecord<String, String> record : records) {
            IndexRequest indexRequest = new IndexRequest(agentMetricsIndex);
            indexRequest.source(record.value(), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        elasticsearchService.bulkInsert(bulkRequest);
    }

    @Override
    public void writeErrors(ConsumerRecords<String, String> records) {
        BulkRequest bulkRequest = new BulkRequest();
        for (ConsumerRecord<String, String> record : records) {
            IndexRequest indexRequest = new IndexRequest(agentErrorLogIndex);
            indexRequest.source(record.value(), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        elasticsearchService.bulkInsert(bulkRequest);
    }

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
        Long value = countByFieldTerm(startTime, endTime, logCollectTaskId, fileLogCollectPathId, logCollectTaskHostName, AgentMetricField.IS_FILE_EXIST.getEsValue(), false);
        return value.intValue();
    }

    @Override
    public Integer getAbnormalTruncationCountByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        return (int) aggregateByLogModel(logCollectTaskId, fileLogCollectPathId, logCollectTaskHostName, startTime, endTime, AgentMetricField.FILTER_TOO_LARGE_COUNT.getEsValue(), CalcFunction.SUM);
    }

    @Override
    public Integer getFileDisorderCount(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
        Long value = countByFieldTerm(startTime, endTime, logCollectTaskId, fileLogCollectPathId, logModelHostName, AgentMetricField.IS_FILE_ORDER.getEsValue(), "1");
        return value.intValue();
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
        Long value = countByFieldTerm(startTime, endTime, logCollectTaskId, fileLogCollectPathId, logModelHostName, AgentMetricField.VALID_TIME_CONFIG.getEsValue(), true);
        if (value != null) {
            return value.intValue();
        } else {
            return 0;
        }
    }

    @Override
    public Long getHostCpuLimitDuration(Long startTime, Long endTime, String hostName) {
        return (long) aggregateByHost(startTime, endTime, hostName, AgentMetricField.LIMIT_RATE.getEsValue(), CalcFunction.SUM);
    }

    @Override
    public Long getHostByteLimitDuration(Long startTime, Long endTime, String hostName) {
        return (long) aggregateByHost(startTime, endTime, hostName, AgentMetricField.LIMIT_TIME.getEsValue(), CalcFunction.SUM);
    }

    @Override
    public Long getHostByteLimitDuration(Long startTime, Long endTime, String logModelHostName, Long logCollectTaskId, Long fileLogCollectPathId) {
        return (long) aggregateByLogModel(logCollectTaskId, fileLogCollectPathId, logModelHostName, startTime, endTime, AgentMetricField.LIMIT_TIME.getEsValue(), CalcFunction.SUM);
    }

    @Override
    public Integer getErrorLogCount(Long startTime, Long endTime, String hostName) {
        CountRequest countRequest = new CountRequest(agentErrorLogIndex);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.HOSTNAME.getEsValue(), hostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));
        countRequest.query(boolQueryBuilder);
        CountResponse countResponse = elasticsearchService.doCount(countRequest);
        return (int) countResponse.getCount();
    }

    @Override
    public AgentMetricPO selectLatestByHostname(String hostname) {
        long time = System.currentTimeMillis();
        long startTime = time - MetricConstant.HEARTBEAT_PERIOD;
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, true).to(time, false));
        builder.query(boolQueryBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits.length == 0) {
            return null;
        }
        SearchHit hit = hits[0];
        Map<String, Object> resultMap = hit.getSourceAsMap();
        return TypeUtils.castToJavaBean(resultMap, AgentMetricPO.class, ParserConfig.getGlobalInstance());
    }

    @Override
    public Long getGCCount(Long startTime, Long endTime, String hostName) {
        return (long) aggregateByHost(startTime, endTime, hostName, AgentMetricField.GC_COUNT.getEsValue(), CalcFunction.SUM);
    }

    @Override
    public List<MetricPoint> getAgentStartupExistsPerMin(Long startTime, Long endTime, String hostName) {
        return queryAgentAggregation(hostName, startTime, endTime, AgentMetricField.START_TIME, CalcFunction.MAX, MetricConstant.QUERY_INTERVAL);
    }

    @Override
    public List<MetricPoint> getLogCollectTaskBytesPerMin(Long taskId, Long startTime, Long endTime) {
        return queryAggregationByTask(taskId, startTime, endTime, AgentMetricField.SEND_BYTE, CalcFunction.COUNT, MetricConstant.QUERY_INTERVAL);
    }

    @Override
    public List<MetricPoint> getLogCollectTaskLogCountPerMin(Long taskId, Long startTime, Long endTime) {
        return queryAggregationByTask(taskId, startTime, endTime, AgentMetricField.SEND_COUNT, CalcFunction.COUNT, MetricConstant.QUERY_INTERVAL);
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
    public List<MetricPoint> getFileLogPathLogSliceErrorPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        return logModelMetricCountByMinute(startTime, endTime, logCollectTaskId, fileLogCollectPathId, logModelHostName, AgentMetricField.VALID_TIME_CONFIG.getEsValue(), false);
    }

    @Override
    public List<MetricPoint> getCollectDelayPerMin(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime) {
        List<MetricPoint> graph = queryAggregationByLogModel(logCollectTaskId, fileLogCollectPathId, logModelHostName, startTime, endTime, AgentMetricField.LOG_TIME, CalcFunction.MIN, MetricConstant.QUERY_INTERVAL);
        for (MetricPoint metricPoint : graph) {
            long timestamp = metricPoint.getTimestamp();
            long logTime = (long) metricPoint.getValue();
            metricPoint.setValue(timestamp - logTime);
        }
        return graph;
    }

    @Override
    public List<MetricPoint> getAgentErrorLogCountPerMin(String hostName, Long startTime, Long endTime) {
        String histogramName = "dataHistogram";
        String aggName = "Agg";
        SearchRequest searchRequest = new SearchRequest(agentErrorLogIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));
        HistogramAggregationBuilder histogramAggregationBuilder = AggregationBuilders.histogram(histogramName)
                .interval(MetricConstant.QUERY_INTERVAL).field(AgentMetricField.HEARTBEAT_TIME.getEsValue())
                .subAggregation(setAggregate(AgentMetricField.HEARTBEAT_TIME.getEsValue(), CalcFunction.COUNT, aggName));
        builder.query(boolQueryBuilder);
        builder.aggregation(histogramAggregationBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);

        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            return Collections.emptyList();
        }
        Histogram histogram = aggregations.get(histogramName);
        List<MetricPoint> list = new ArrayList<>();
        for (Histogram.Bucket bucket : histogram.getBuckets()) {
            Long timeKey = DateUtils.castToTimestamp(bucket.getKey());
            NumericMetricsAggregation.SingleValue value = (NumericMetricsAggregation.SingleValue) bucket.getAggregations().getAsMap().get(aggName);
            MetricPoint point = new MetricPoint();
            point.setTimestamp(timeKey);
            point.setValue((long) value.value());
            list.add(point);
        }
        return list;
    }

    @Override
    public List<MetricPoint> queryByTask(Long logCollectTaskId, Long startTime, Long endTime, AgentMetricField column) {
        String histogramName = "dataHistogram";
        String aggName = column + "Agg";
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), logCollectTaskId));
        HistogramAggregationBuilder histogramAggregationBuilder = AggregationBuilders.histogram(histogramName)
                .interval(MetricConstant.HEARTBEAT_PERIOD).field(AgentMetricField.HEARTBEAT_TIME.getEsValue());
        builder.query(boolQueryBuilder);
        builder.aggregation(histogramAggregationBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);

        Aggregations aggregations = searchResponse.getAggregations();
        return handleHistogramResult(aggregations, histogramName, aggName);
    }

    @Override
    public List<MetricPoint> queryAggregationByTask(Long logCollectTaskId, Long startTime, Long endTime, AgentMetricField column, CalcFunction method, int step) {
        String histogramName = "dataHistogram";
        String aggName = column + "Agg";
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), logCollectTaskId));
        HistogramAggregationBuilder histogramAggregationBuilder = AggregationBuilders.histogram(histogramName)
                .interval(step).field(AgentMetricField.HEARTBEAT_TIME.getEsValue())
                .subAggregation(setAggregate(column.getEsValue(), method, aggName));
        builder.query(boolQueryBuilder);
        builder.aggregation(histogramAggregationBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);

        Aggregations aggregations = searchResponse.getAggregations();
        return handleHistogramResult(aggregations, histogramName, aggName);
    }

    @Override
    public List<MetricPoint> queryAggregationByHostname(String hostname, Long startTime, Long endTime, AgentMetricField column, CalcFunction method, int step) {
        String histogramName = "dataHistogram";
        String aggName = column + "Agg";
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true))
                .must(QueryBuilders.termQuery(AgentMetricField.HOSTNAME.getEsValue(), hostname));
        HistogramAggregationBuilder histogramAggregationBuilder = AggregationBuilders.histogram(histogramName)
                .interval(step).field(AgentMetricField.HEARTBEAT_TIME.getEsValue())
                .subAggregation(setAggregate(column.getEsValue(), method, aggName));
        builder.query(boolQueryBuilder);
        builder.aggregation(histogramAggregationBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);

        Aggregations aggregations = searchResponse.getAggregations();
        return handleHistogramResult(aggregations, histogramName, aggName);
    }

    @Override
    public List<MetricPoint> queryByLogModel(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime, AgentMetricField column) {
        String histogramName = "dataHistogram";
        String aggName = column + "Agg";
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));
        if (logCollectTaskId != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), logCollectTaskId));
        }
        if (logModelHostName != null && !logModelHostName.isEmpty()) {
            boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODEL_HOST_NAME.getEsValue(), logModelHostName));
        }
        if (fileLogCollectPathId != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.PATH_ID.getEsValue(), fileLogCollectPathId));
        }

        HistogramAggregationBuilder histogramAggregationBuilder = AggregationBuilders.histogram(histogramName)
                .interval(MetricConstant.HEARTBEAT_PERIOD).field(AgentMetricField.HEARTBEAT_TIME.getEsValue());
        builder.query(boolQueryBuilder);
        builder.aggregation(histogramAggregationBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);

        Aggregations aggregations = searchResponse.getAggregations();
        return handleHistogramResult(aggregations, histogramName, aggName);
    }

    @Override
    public List<MetricPoint> queryAggregationByLogModel(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime, AgentMetricField column, CalcFunction method, int step) {
        String histogramName = "dataHistogram";
        String aggName = column + "Agg";
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));
        if (logCollectTaskId != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), logCollectTaskId));
        }
        if (logModelHostName != null && !logModelHostName.isEmpty()) {
            boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODEL_HOST_NAME.getEsValue(), logModelHostName));
        }
        if (fileLogCollectPathId != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.PATH_ID.getEsValue(), fileLogCollectPathId));
        }

        HistogramAggregationBuilder histogramAggregationBuilder = AggregationBuilders.histogram(histogramName)
                .interval(step).field(AgentMetricField.HEARTBEAT_TIME.getEsValue())
                .subAggregation(setAggregate(column.getEsValue(), method, aggName));
        builder.query(boolQueryBuilder);
        builder.aggregation(histogramAggregationBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);
        Aggregations aggregations = searchResponse.getAggregations();
        return handleHistogramResult(aggregations, histogramName, aggName);
    }

    @Override
    public List<MetricPoint> queryAgent(String hostname, Long startTime, Long endTime, AgentMetricField column) {
        String histogramName = "dataHistogram";
        String aggName = column + "Agg";
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.HOSTNAME.getEsValue(), hostname))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, true).to(endTime, false));

        HistogramAggregationBuilder histogramAggregationBuilder = AggregationBuilders.histogram(histogramName)
                .interval(MetricConstant.HEARTBEAT_PERIOD).field(AgentMetricField.HEARTBEAT_TIME.getEsValue());
        builder.query(boolQueryBuilder);
        builder.aggregation(histogramAggregationBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);
        Aggregations aggregations = searchResponse.getAggregations();
        return handleHistogramResult(aggregations, histogramName, aggName);
    }

    @Override
    public List<MetricPoint> queryAgentAggregation(String hostname, Long startTime, Long endTime, AgentMetricField column, CalcFunction method, int step) {
        String histogramName = "dataHistogram";
        String aggName = column + "Agg";
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.HOSTNAME.getEsValue(), hostname))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, true).to(endTime, false));

        HistogramAggregationBuilder histogramAggregationBuilder = AggregationBuilders.histogram(histogramName)
                .interval(step).field(AgentMetricField.HEARTBEAT_TIME.getEsValue())
                .subAggregation(setAggregate(column.getEsValue(), method, aggName));
        builder.query(boolQueryBuilder);
        builder.aggregation(histogramAggregationBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);
        Aggregations aggregations = searchResponse.getAggregations();
        return handleHistogramResult(aggregations, histogramName, aggName);
    }

    @Override
    public Double queryAggregationForAll(Long startTime, Long endTime, AgentMetricField column, CalcFunction method) {
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.mustNot(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));
        builder.query(boolQueryBuilder);
        String aggName = "columnStats";
        builder.aggregation(AggregationBuilders.stats(column.getEsValue()).field(aggName));

        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);
        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            return 0D;
        }

        Stats stats = aggregations.get(aggName);
        return getAggregate(stats, method);
    }

    @Override
    public CollectTaskMetricPO selectLatestMetric(Long taskId) {
        long time = System.currentTimeMillis();
        long startTime = time - MetricConstant.HEARTBEAT_PERIOD;
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.mustNot(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, true).to(time, false));
        builder.query(boolQueryBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits.length == 0) {
            return null;
        }
        SearchHit hit = hits[0];
        Map<String, Object> resultMap = hit.getSourceAsMap();
        return TypeUtils.castToJavaBean(resultMap, CollectTaskMetricPO.class, ParserConfig.getGlobalInstance());
    }

    @Override
    public List<CollectTaskMetricPO> queryLatestMetrics(Long time, int step) {
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.mustNot(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(time - step, true).to(time, false));
        builder.query(boolQueryBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits.length == 0) {
            return null;
        }
        List<CollectTaskMetricPO> list = new ArrayList<>();
        for (SearchHit hit : hits) {
            Map<String, Object> resultMap = hit.getSourceAsMap();
            CollectTaskMetricPO collectTaskMetricPO = TypeUtils.castToJavaBean(resultMap, CollectTaskMetricPO.class, ParserConfig.getGlobalInstance());
            list.add(collectTaskMetricPO);
        }
        return list;
    }

    @Override
    public List<AgentMetricPO> queryLatestAgentMetrics(Long time, int step) {
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(time - step, true).to(time, false));
        builder.query(boolQueryBuilder);
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits.length == 0) {
            return null;
        }
        List<AgentMetricPO> list = new ArrayList<>();
        for (SearchHit hit : hits) {
            Map<String, Object> resultMap = hit.getSourceAsMap();
            AgentMetricPO agentMetricPO = TypeUtils.castToJavaBean(resultMap, AgentMetricPO.class, ParserConfig.getGlobalInstance());
            list.add(agentMetricPO);
        }
        return list;
    }

    @Override
    public List<DashBoardStatisticsDO> groupByKeyAndMinuteLogCollectTaskMetric(Long startTime, Long endTime, String key, String function, String metric) {
        return null;
    }

    @Override
    public List<DashBoardStatisticsDO> groupByKeyAndMinuteAgentMetric(Long startTime, Long endTime, String key, String function, String metric) {
        return null;
    }

    private AggregationBuilder setAggregate(String column, CalcFunction function, String aggName) {
        switch (function) {
            case MAX:
                return AggregationBuilders.max(aggName).field(column);
            case MIN:
                return AggregationBuilders.min(aggName).field(column);
            case AVG:
                return AggregationBuilders.avg(aggName).field(column);
            case SUM:
                return AggregationBuilders.sum(aggName).field(column);
            case COUNT:
                return AggregationBuilders.count(aggName).field(column);
            default:
                throw new ServiceException(String.format("不支持的聚合类型%s", function.getValue()), ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
    }

    private double getAggregate(Stats stats, CalcFunction function) {
        switch (function) {
            case AVG:
                return stats.getAvg();
            case SUM:
                return stats.getSum();
            case MAX:
                return stats.getMax();
            case MIN:
                return stats.getMin();
            case COUNT:
                return stats.getCount();
            default:
                throw new ServiceException(String.format("不支持的聚合类型%s", function.getValue()), ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
    }

    private Long countByFieldTerm(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, String fieldName, Object value) {
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

    private double aggregateByHost(Long startTime, Long endTime, String hostName, String fieldName, CalcFunction function) {
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.HOSTNAME.getEsValue(), hostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), -1))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));
        builder.query(boolQueryBuilder);
        String aggName = fieldName + "Agg";
        builder.aggregation(setAggregate(fieldName, function, aggName));
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);
        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            return 0;
        }
        Sum sum = aggregations.get(aggName);
        return sum.getValue();
    }

    private double aggregateByLogModel(Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName, Long startTime, Long endTime, String fieldName, CalcFunction function) {
        SearchRequest searchRequest = new SearchRequest(agentMetricsIndex);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery(AgentMetricField.LOG_MODEL_HOST_NAME.getEsValue(), logModelHostName))
                .must(QueryBuilders.termQuery(AgentMetricField.LOG_MODE_ID.getEsValue(), logCollectTaskId))
                .must(QueryBuilders.termQuery(AgentMetricField.PATH_ID.getEsValue(), fileLogCollectPathId))
                .must(QueryBuilders.rangeQuery(AgentMetricField.HEARTBEAT_TIME.getEsValue()).from(startTime, false).to(endTime, true));
        builder.query(boolQueryBuilder);
        String aggName = fieldName + "Agg";
        builder.aggregation(setAggregate(fieldName, function, aggName));
        searchRequest.source(builder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);
        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            return 0;
        }
        Sum sum = aggregations.get(aggName);
        return sum.getValue();
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
                .interval(MetricConstant.QUERY_INTERVAL).field(AgentMetricField.HEARTBEAT_TIME.getEsValue())
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

    private List<MetricPoint> handleHistogramResult(Aggregations aggregations, String histogramName, String aggName) {
        if (aggregations == null) {
            return Collections.emptyList();
        }
        Histogram histogram = aggregations.get(histogramName);
        List<MetricPoint> list = new ArrayList<>();
        for (Histogram.Bucket bucket : histogram.getBuckets()) {
            Long timeKey = DateUtils.castToTimestamp(bucket.getKey());
            NumericMetricsAggregation.SingleValue value = (NumericMetricsAggregation.SingleValue) bucket.getAggregations().getAsMap().get(aggName);
            MetricPoint point = new MetricPoint();
            point.setTimestamp(timeKey);
            point.setValue((long) value.value());
            list.add(point);
        }
        return list;
    }
}
