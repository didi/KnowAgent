package com.didichuxing.datachannel.agent.engine.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

public class HttpUtilsV2 {
    private static final Logger LOGGER           = LoggerFactory.getLogger(HttpUtils.class);
    private static int          CONNECT_TIME_OUT = 15000;
    private static int          READ_TIME_OUT    = 3000;

    public HttpUtilsV2() {
    }

    public static String get(String url, Map<String, String> params) {
        return sendRequest(url, "GET", params, (Map) null, (InputStream) null);
    }

    public static String get(String url, Map<String, String> params, Map<String, String> headers) {
        return sendRequest(url, "GET", params, headers, (InputStream) null);
    }

    public static String postForString(String url, String content, Map<String, String> headers) {
        ByteArrayInputStream in = null;

        try {
            if (content != null && !content.isEmpty()) {
                in = new ByteArrayInputStream(content.getBytes("UTF-8"));
            }
        } catch (UnsupportedEncodingException var5) {
            throw new RuntimeException(var5);
        }

        return sendRequest(url, "POST", (Map) null, headers, in);
    }

    public static String putForString(String url, String content, Map<String, String> headers) {
        ByteArrayInputStream in = null;

        try {
            if (content != null && !content.isEmpty()) {
                in = new ByteArrayInputStream(content.getBytes("UTF-8"));
            }
        } catch (UnsupportedEncodingException var5) {
            throw new RuntimeException(var5);
        }

        return sendRequest(url, "PUT", (Map) null, headers, in);
    }

    public static String deleteForString(String url, String content, Map<String, String> headers) {
        ByteArrayInputStream in = null;

        try {
            if (content != null && !content.isEmpty()) {
                in = new ByteArrayInputStream(content.getBytes("UTF-8"));
            }
        } catch (UnsupportedEncodingException var5) {
            throw new RuntimeException(var5);
        }

        return sendRequest(url, "DELETE", (Map) null, headers, in);
    }

    private static String sendRequest(String url, String method, Map<String, String> params,
                                      Map<String, String> headers, InputStream bodyStream) {
        HttpURLConnection conn = null;

        String var8;
        try {
            String paramUrl = setUrlParams(url, params);
            URL urlObj = new URL(paramUrl);
            conn = (HttpURLConnection) urlObj.openConnection();
            setConnProperties(conn, method, headers);
            if (bodyStream != null) {
                conn.setDoOutput(true);
                copyStreamAndClose(bodyStream, conn.getOutputStream());
            }

            var8 = handleResponseBodyToString(conn.getInputStream());
        } catch (Exception var12) {
            throw new RuntimeException(var12);
        } finally {
            closeConnection(conn);
        }

        return var8;
    }

    private static String setUrlParams(String url, Map<String, String> params) {
        if (url != null && params != null && !params.isEmpty()) {
            StringBuilder sb = (new StringBuilder(url)).append('?');
            Iterator var3 = params.entrySet().iterator();

            while (var3.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry) var3.next();
                sb.append((String) entry.getKey()).append('=').append((String) entry.getValue())
                    .append('&');
            }

            return sb.deleteCharAt(sb.length() - 1).toString();
        } else {
            return url;
        }
    }

    private static void setConnProperties(HttpURLConnection conn, String method,
                                          Map<String, String> headers) throws Exception {
        conn.setConnectTimeout(CONNECT_TIME_OUT);
        conn.setReadTimeout(READ_TIME_OUT);
        if (method != null && !method.isEmpty()) {
            conn.setRequestMethod(method);
        }

        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        if (headers != null && !headers.isEmpty()) {
            Iterator var3 = headers.entrySet().iterator();

            while (var3.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry) var3.next();
                conn.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
            }

        }
    }

    private static String handleResponseBodyToString(InputStream in) throws Exception {
        ByteArrayOutputStream bytesOut = null;

        String var2;
        try {
            bytesOut = new ByteArrayOutputStream();
            copyStreamAndClose(in, bytesOut);
            var2 = new String(bytesOut.toByteArray(), "UTF-8");
        } finally {
            closeStream(bytesOut);
        }

        return var2;
    }

    private static void copyStreamAndClose(InputStream in, OutputStream out) {
        try {
            byte[] buf = new byte[1024];
            boolean var3 = true;

            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }

            out.flush();
        } catch (Exception var7) {
            var7.printStackTrace();
        } finally {
            closeStream(in);
            closeStream(out);
        }

    }

    private static void closeConnection(HttpURLConnection conn) {
        if (conn != null) {
            try {
                conn.disconnect();
            } catch (Exception var2) {
                LOGGER.error("close connection failed", var2);
            }
        }

    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception var2) {
                LOGGER.error("close stream failed", var2);
            }
        }

    }
}
