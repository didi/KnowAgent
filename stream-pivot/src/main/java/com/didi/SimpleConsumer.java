//package com.didi;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.common.TopicPartition;
//
//import java.time.Duration;
//import java.util.Arrays;
//import java.util.Properties;
//
//public class SimpleConsumer {
//        private static String topicName = "metric";
////        private static String topicName = "errlog";
////    private static String topicName = "LogX_channel_1331";
////    private static final String address = "10.190.33.217:9092";
//    private static final String address = "10.255.1.144:9092";
////    private static final String address = "10.178.24.118:8092";
//    private static String group = "d2";
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
//        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
////        consumer.assign(Arrays.asList(new TopicPartition(topicName, 0)));
//        consumer.subscribe(Arrays.asList(topicName));
//
//        long count = 0;
//        while (true) {
//            try {
//                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(3));
//                for (ConsumerRecord<String, String> record : records) {
//                    System.out.println(count + "\t" + record.value());
//                    count++;
//                }
//            } catch (Throwable ignored) {
//            }
//        }
//    }
//}
