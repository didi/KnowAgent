package com.didichuxing.datachannel.agent.node.service.http.client;

import java.util.HashMap;
import java.util.Map;

import com.didichuxing.datachannel.agent.engine.utils.HttpUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClient {

    private static final Logger LOGGER      = LoggerFactory.getLogger(HttpClient.class.getName());
    private static String       URL_PATTERN = "http://%s:%d%s";

    public static String get(String ip, int port, String uri, Map<String, String> param) {
        if (StringUtils.isBlank(ip) || StringUtils.isBlank(uri) || port == 0) {
            return null;
        }
        String urlStr = String.format(URL_PATTERN, ip, port, uri);
        int max = 3;
        while (true) {
            String result = getResult(urlStr, param);
            if (StringUtils.isNotBlank(result)) {
                return result;
            }
            max--;
            if (max < 0) {
                break;
            }
            try {
                Thread.sleep(500L);
            } catch (Exception e) {
                LOGGER.error("sleep is interrupt.", e);
            }
        }
        return null;
    }

    private static String getResult(String url, Map<String, String> param) {
        try {
            return HttpUtils.get(url, param);
        } catch (Throwable e) {
            return null;
        }
    }

    public static String post(String ip, int port, String uri, String requestBody) {
        if (StringUtils.isBlank(ip) || StringUtils.isBlank(uri) || port == 0) {
            return null;
        }
        String urlStr = String.format(URL_PATTERN, ip, port, uri);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=utf-8");
        int max = 3;
        while (true) {
            String result = postResult(urlStr, requestBody, headers);
            if (StringUtils.isNotBlank(result)) {
                return result;
            }
            max--;
            if (max < 0) {
                break;
            }
            try {
                Thread.sleep(500L);
            } catch (Exception e) {
                LOGGER.error("sleep is interrupt.", e);
            }
        }
        return null;
    }

    private static String postResult(String url, String requestBody, Map<String, String> headers) {
        try {
            return HttpUtils.postForString(url, requestBody, headers);
        } catch (Throwable e) {
            return null;
        }
    }

}
