//package com.didi;
//
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.BufferedInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//
//public class MetricClient {
//    private static final Logger log = LoggerFactory.getLogger(MetricClient.class);
//
//    private Request.Builder builder;
//    private String url;
//    private static String address;
//    private OkHttpClient client = new OkHttpClient();
//
//    static {
//        try {
//            File file = new File("./env.properties");
//            InputStream in = new BufferedInputStream(new FileInputStream(file));
//            Properties properties = new Properties();
//            properties.load(in);
//            address = properties.getProperty("es-address");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public String write(String requestBody) throws IOException {
//        log.info("POST {}", this.url);
//        log.info("{}", requestBody);
//
//        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
//        RequestBody body = RequestBody.create(requestBody, mediaType);
//        Request request = builder.post(body).build();
//
//        try (Response response = client.newCall(request).execute()) {
//            return response.body().string();
//        }
//    }
//
//    public MetricClient(String index) {
//        this.url = "http://" + address + "/" + index + "/_doc";
//        builder = new Request.Builder().addHeader("Authorization", "Basic MTE6UjhYSlJtMFdydjdad3Zx").url(url);
//    }
//}
