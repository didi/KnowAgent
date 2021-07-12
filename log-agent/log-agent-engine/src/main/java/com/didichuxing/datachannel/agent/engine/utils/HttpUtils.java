package com.didichuxing.datachannel.agent.engine.utils;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {

    private static final Logger     LOGGER               = LoggerFactory.getLogger(HttpUtils.class
                                                             .getName());

    public static final String      UTF8                 = "UTF-8";
    public static final String      GBK                  = "GBK";
    public static final String      GB2312               = "GB2312";
    private static final int        CONNECTION_TIMEOUT   = 5000;
    private static final int        SO_TIMEOUT           = 20000;
    private static final int        MAX_TOTAL            = 200;
    private static final int        DEFAULT_MAX_PERROUTE = 50;

    private static final HttpClient HTTP_CLIENT;

    static {
        PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
        connectionManager.setMaxTotal(MAX_TOTAL);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PERROUTE);
        HTTP_CLIENT = new DefaultHttpClient(connectionManager);
        HTTP_CLIENT.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
            CONNECTION_TIMEOUT);
        HTTP_CLIENT.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
        HTTP_CLIENT.getParams().setParameter(HttpMethodParams.USER_AGENT,
            "Mozilla/5.0 (Windows NT 6.1; rv:8.0.1) Gecko/20100101 Firefox/8.0.1");

    }

    /**
     * 以post方式发送请求并返回响应
     *
     * @param url 发送请求的url
     * @param params post参数,所有的参数值均视为字符串
     * @return 响应
     * @author kyo.ou 2013-6-8
     */
    public static String post(String url, Map<String, Object> params) {
        return post(url, params, null, null, null);
    }

    /**
     * 以post方式发送请求并返回响应
     *
     * @param url 请求url
     * @param params post参数,所有的参数值均视为字符串
     * @param reqEncode 请求参数编码，默认：UTF-8
     * @param resEncode 响应返回编码，默认：UTF-8
     * @return
     * @author yu.han 20131226
     */
    public static String postEncode(String url, Map<String, Object> params, String reqEncode,
                                    String resEncode) {
        return post(url, params, null, reqEncode, resEncode);
    }

    /**
     * 以post方式发送请求并返回响应
     *
     * @param url 发送请求的url
     * @param params post参数,所有的参数值均视为字符串
     * @param headers http头
     * @return 响应
     * @author kyo.ou 2013-6-8
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static String post(String url, Map<String, Object> params, Map<String, String> headers,
                              String reqEncode, String resEncode) {
        HttpPost post = new HttpPost(url);

        if (StringUtils.isBlank(reqEncode)) {
            reqEncode = UTF8;
        }
        if (StringUtils.isBlank(resEncode)) {
            resEncode = UTF8;
        }

        List httpParams = null;
        if (params != null && !params.isEmpty()) {
            httpParams = new ArrayList(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String k = entry.getKey();
                Object v = entry.getValue();
                if (v == null) {
                    httpParams.add(new BasicNameValuePair(k, null));
                } else if (!v.getClass().isArray()) {
                    httpParams.add(new BasicNameValuePair(k, v.toString()));
                } else {// 数组要作特殊处理
                    int len = Array.getLength(v);
                    for (int i = 0; i < len; i++) {
                        Object element = Array.get(v, i);
                        if (element != null) {
                            httpParams.add(new BasicNameValuePair(k, element.toString()));
                        } else {
                            httpParams.add(new BasicNameValuePair(k, null));
                        }
                    }
                }
            }
            if (headers != null) {
                for (Map.Entry<String, String> e : headers.entrySet()) {
                    post.addHeader(e.getKey(), e.getValue());
                }
            }
            try {
                post.setEntity(new UrlEncodedFormEntity(httpParams, reqEncode));
                post.getParams().setParameter("http.protocol.cookie-policy",
                    CookiePolicy.BROWSER_COMPATIBILITY);
            } catch (UnsupportedEncodingException impossiable) {
                // shouldn't happen
                throw new RuntimeException("UTF-8 is not surportted", impossiable);
            }
        }
        String response = null;
        try {
            HttpEntity entity = HTTP_CLIENT.execute(post).getEntity();
            response = EntityUtils.toString(entity, resEncode);
        } catch (Exception e) {
            throw new RuntimeException("error post data to " + url, e);
        } finally {
            post.releaseConnection();
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("response=" + response);
        }
        return response;
    }

    /**
     * 以string格式提交数据
     *
     * @param url
     * @param content
     * @param headers
     * @return
     */
    public static String postForString(String url, String content, Map<String, String> headers) {
        HttpPost post = new HttpPost(url);

        if (StringUtils.isNotBlank(content)) {
            if (headers != null) {
                for (Map.Entry<String, String> e : headers.entrySet()) {
                    post.addHeader(e.getKey(), e.getValue());
                }
            }
            try {
                String body = content;
                BasicHttpEntity requestBody = new BasicHttpEntity();
                requestBody.setContent(new ByteArrayInputStream(body.getBytes("UTF-8")));
                requestBody.setContentLength(body.getBytes("UTF-8").length);
                post.setEntity(requestBody);
                post.getParams().setParameter("http.protocol.cookie-policy",
                    CookiePolicy.BROWSER_COMPATIBILITY);
            } catch (UnsupportedEncodingException impossiable) {
                // shouldn't happen
                throw new RuntimeException("UTF-8 is not surportted", impossiable);
            }
        }
        String response = null;
        try {
            HttpEntity entity = HTTP_CLIENT.execute(post).getEntity();
            response = EntityUtils.toString(entity, "UTF-8");
            EntityUtils.consume(entity);
        } catch (Exception e) {
            throw new RuntimeException("error post data to " + url, e);
        } finally {
            post.releaseConnection();
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("response=" + response);
        }
        return response;
    }

    public static String get(String url, Map<String, String> params) {
        if (params != null) {
            StringBuilder builder = new StringBuilder(url).append('?');
            for (Map.Entry<String, String> e : params.entrySet()) {
                builder.append(e.getKey()).append('=').append(e.getValue()).append('&');
            }
            url = builder.toString();
        }
        HttpGet get = new HttpGet(url);
        String response = null;
        try {
            HttpEntity entity = HTTP_CLIENT.execute(get).getEntity();
            response = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("error post data to " + url, e);
        } finally {
            get.releaseConnection();
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("response=" + response);
        }
        return response;
    }

}
