package com.didichuxing.datachannel.agentmanager.core.agent.metrics;

import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.agentmanager.core.ApplicationTests;
import com.didichuxing.datachannel.agentmanager.thirdpart.elasticsearch.service.ElasticsearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

@Transactional
@Rollback
public class AgentMetricsManageServiceTest extends ApplicationTests {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Test
    public void testQueryAgentHeatbeatInLastest5Mins() {

        String formatStr = "yyyy-MM-dd HH:mm:ss";

        String indexName = "epri_swan_agent_metrics";
        String agentHostName = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr);
        Date to = new Date();
        Date from = DateUtils.getBeforeSeconds(to, 300);
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("heartbeatTime").format(formatStr).from(dateFormat.format(from)).to(dateFormat.format(to)).includeLower(true).includeUpper(true);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        TermQueryBuilder termQuery = QueryBuilders.termQuery("hostname", agentHostName);
        boolQueryBuilder.must(rangeQueryBuilder);
//        sourceBuilder.query(boolQueryBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = elasticsearchService.doQuery(searchRequest);

        assert searchResponse != null;

    }

}
