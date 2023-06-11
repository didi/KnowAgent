//package test;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//
//import java.time.Duration;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Properties;
//
//public class SimpleConsumer {
//    private static String topicName = "xgTest1";
//    private static String group = "cg_logi_kafka_test_3";
//
//    public static void main(String[] args) {
//        Properties props = new Properties();
//        //请填写正确的bootstrap server地址
//        props.put("bootstrap.servers", "10.190.46.198:9092,10.190.14.237:9092,10.190.50.65:9092");//填写服务发现地址
////        props.put("bootstrap.servers", "10.96.64.14:9092");
//        props.put("group.id", group);
//        props.put("auto.offset.reset", "earliest");   //earliest/latest消息消费起始位置，earliest代表消费历史数据，latest代表消费最新的数据
//        props.put("enable.auto.commit", "true");      // 自动commit
//        props.put("auto.commit.interval.ms", "10000"); // 自动commit的间隔
//        props.put("partition.assignment.strategy", Collections.singletonList(CooperativeStickyAssignor.class)); // 自动commit的间隔
//
//
//        //根据实际场景选择序列化类
//        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//
////        props.put("security.protocol", "SASL_PLAINTEXT");
////        props.put("sasl.mechanism", "PLAIN");
////        props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"dkm_admin\" password=\"km_kMl4N8as1Kp0CCY\";");
//
//        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
//        consumer.subscribe(Arrays.asList(topicName)); // 可消费多个Topic, 组成一个List
//
//        long now = System.currentTimeMillis();
//        while (true) {
//            try {
//                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
//                for (ConsumerRecord<String, String> record : records) {
//                    System.out.println("offset = " + record.offset() + ", key = " + record.key() + ", value = " + record.value());
//                }
//                if (System.currentTimeMillis() - now > 1 * 60 * 10000) {
//                    break;
//                }
//            }catch (Throwable e){
//                //TODO 输出你的异常
//            }
//        }
//        consumer.close();
//    }
//}
