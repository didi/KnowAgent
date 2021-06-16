package com.didichuxing.datachannel.agentmanager.thirdpart.elasticsearch.service;

import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Base64;

@org.springframework.stereotype.Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    private static final ILog LOGGER = LogFactory.getLog(ElasticsearchServiceImpl.class);

    @Value("${agent.metrics.datasource.elasticsearch.ip}")
    private String ip;
    @Value("${agent.metrics.datasource.elasticsearch.port}")
    private int port;
    @Value("${agent.metrics.datasource.elasticsearch.appId}")
    private String appId;
    @Value("${agent.metrics.datasource.elasticsearch.appSecret}")
    private String appSecret;

    private volatile RestHighLevelClient restHighLevelClient;

    private void setClient() {
        if(null == restHighLevelClient) {
            synchronized (this) {
                if(null == restHighLevelClient) {
                    HttpHost httpHost = new HttpHost(ip,  port);
                    Header authHeader = new BasicHeader(
                            "Authorization", "Basic " + Base64.getEncoder().encodeToString(
                            String.format("%s:%s", appId, appSecret).getBytes()));
                    RestClientBuilder restClientBuilder = RestClient
                            .builder(httpHost)
                            .setDefaultHeaders(new Header[]{authHeader});
                    this.restHighLevelClient = new RestHighLevelClient(restClientBuilder);
                }
            }
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
        if(null == response || response.status().getStatus() != RestStatus.OK.getStatus()) {
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
        if(null == countResponse || countResponse.status().getStatus() != RestStatus.OK.getStatus()) {
            throw new ServiceException(
                    "class=ElasticsearchServiceImpl||method=doQuery||errMsg=query elasticsearch failed",
                    ErrorCodeEnum.ELASTICSEARCH_QUERY_FAILED.getCode()
            );
        }
        return countResponse;
    }

}
