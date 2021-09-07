package com.didichuxing.datachannel.agentmanager.thirdpart.elasticsearch.service;

import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@org.springframework.stereotype.Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchServiceImpl.class);

    @Value("${agent.metrics.datasource.elasticsearch.ip}")
    private String ip;
    @Value("${agent.metrics.datasource.elasticsearch.port}")
    private int port;
    @Value("${agent.metrics.datasource.elasticsearch.appId:#{null}}")
    private String appId;
    @Value("${agent.metrics.datasource.elasticsearch.appSecret:#{null}}")
    private String appSecret;

    private volatile RestHighLevelClient restHighLevelClient;

    @PostConstruct
    public void setClient() {
        if (null == restHighLevelClient) {
            synchronized (this) {
                if (null == restHighLevelClient) {
                    HttpHost httpHost = new HttpHost(ip, port);
                    RestClientBuilder restClientBuilder = RestClient.builder(httpHost);
                    if (appId != null && appSecret != null) {
                        Header authHeader = new BasicHeader(
                                "Authorization", "Basic " + Base64.getEncoder().encodeToString(
                                String.format("%s:%s", appId, appSecret).getBytes()));
                        restClientBuilder.setDefaultHeaders(new Header[]{authHeader});
                    }
                    this.restHighLevelClient = new RestHighLevelClient(restClientBuilder);
                }
            }
        }
    }

    @Override
    public void bulkInsert(BulkRequest bulkRequest) {
        BulkResponse response = null;
        try {
            response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ServiceException(
                    "class=ElasticsearchServiceImpl||method=bulkInsert||errMsg=query elasticsearch failed", e,
                    ErrorCodeEnum.ELASTICSEARCH_QUERY_FAILED.getCode()
            );
        }
    }

    @Override
    public SearchResponse doQuery(SearchRequest searchRequest) {
        setClient();
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException ex) {
            throw new ServiceException(
                    "class=ElasticsearchServiceImpl||method=doQuery||errMsg=query elasticsearch failed",
                    ex,
                    ErrorCodeEnum.ELASTICSEARCH_QUERY_FAILED.getCode()
            );
        }
        if (null == response || response.status().getStatus() != RestStatus.OK.getStatus()) {
            throw new ServiceException(
                    "class=ElasticsearchServiceImpl||method=doQuery||errMsg=query elasticsearch failed",
                    ErrorCodeEnum.ELASTICSEARCH_QUERY_FAILED.getCode()
            );
        }
        return response;
    }

    @Override
    public CountResponse doCount(CountRequest countRequest) {
        setClient();
        CountResponse countResponse;
        try {
            countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
        } catch (IOException ex) {
            throw new ServiceException(
                    "class=ElasticsearchServiceImpl||method=doQuery||errMsg=query elasticsearch failed",
                    ex,
                    ErrorCodeEnum.ELASTICSEARCH_QUERY_FAILED.getCode()
            );
        }
        if (null == countResponse || countResponse.status().getStatus() != RestStatus.OK.getStatus()) {
            throw new ServiceException(
                    "class=ElasticsearchServiceImpl||method=doQuery||errMsg=query elasticsearch failed",
                    ErrorCodeEnum.ELASTICSEARCH_QUERY_FAILED.getCode()
            );
        }
        return countResponse;
    }

}
