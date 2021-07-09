package com.didichuxing.datachannel.agentmanager.thirdpart.elasticsearch.service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.search.aggregations.AggregationBuilder;

public interface ElasticsearchService {

    SearchResponse doQuery(SearchRequest searchRequest);

    CountResponse doCount(CountRequest countRequest);
}
