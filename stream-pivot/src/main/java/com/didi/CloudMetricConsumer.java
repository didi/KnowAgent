//package com.didi;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//
//import java.time.Duration;
//import java.util.Arrays;
//import java.util.Properties;
//
//public class CloudMetricConsumer {
//    private static String topicName;
//    private static String metricName;
//    private static String group = "c2";
//    private static final String address;
//
//    private static String appId = "depri_admin";
//    private static String appPwd = "epri_nuVCIOU13c193Vc1";
//    private static String clusterId = "2";
//
//    static {
//        /*try {
//            String fileName = "./env.properties";
//            InputStream in = new BufferedInputStream(new FileInputStream(fileName));
//            Properties properties = new Properties();
//            properties.load(in);
//            address = properties.getProperty("kafka-address");
//            topicName = properties.getProperty("data-topic");
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException();
//        }*/
//        address = "172.17.238.70:9093,172.17.238.71:9093";
//        topicName = "LogX_channel_147";
//    }
//
//    public static void main(String[] args) {
//        Properties props = new Properties();
//        props.put("bootstrap.servers", address);
//        props.put("group.id", group);
//        props.put("auto.offset.reset", "latest");
//        props.put("compression.type", "lz4");
//        props.put("enable.auto.commit", "true");
//        props.put("auto.commit.interval.ms", "1000");
//        props.put("session.timeout.ms", "30000");
//        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//
//        String format = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s.%s\" password=\"%s\";";
//        String jaasConfig = String.format(format, clusterId, appId, appPwd);
//        props.put("sasl.jaas.config", jaasConfig);
//        props.put("security.protocol", "SASL_PLAINTEXT");
//        props.put("sasl.mechanism", "PLAIN");
//
//        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
//        consumer.subscribe(Arrays.asList(topicName));
//
//        while (true) {
//            try {
//                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
//                String response = null;
//                for (ConsumerRecord<String, String> record : records) {
//                    System.out.println(record.value());
////                    response = new MetricClient("epri_swan_agent_metrics").write(record.value());
////                    System.out.println(response);
//                }
//            } catch (Throwable e) {
//                //TODO print  your error,特别注意这里的poll 可能因为网络问题等原因发生异常,不能catch到异常后就close KafkaConsumer实例,否则无法继续消费
//            }
//        }
//    }
//}
