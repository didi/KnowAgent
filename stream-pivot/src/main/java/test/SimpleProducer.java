//package test;
//
//import org.apache.kafka.clients9.CommonClientConfigs;
//import org.apache.kafka.clients.producer.Callback;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.clients.producer.RecordMetadata;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//
///**
// * @author zengqiao
// * @date 20/11/28
// */
//public class SimpleProducer {
//    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleProducer.class);
//
//    private static Properties createProperties() {
//        Properties properties = new Properties();
////        properties.put("bootstrap.servers", "10.179.149.194:7092");
////        properties.put("bootstrap.servers", "10.179.149.164:4093,10.179.149.201:4093");
////        properties.put("bootstrap.servers", "10.179.162.171:9093");
//        properties.put("bootstrap.servers", "10.190.46.198:9092,10.190.14.237:9092,10.190.50.65:9092");
////        properties.put("bootstrap.servers", "10.96.64.14:9092");
////        properties.put("bootstrap.servers", "10.179.149.164:7092");
////        properties.put(CommonClientConfigs.CLIENT_ID_CONFIG, "dkm_admin.kmo_comminity");
//
//        properties.put("compression.type", "lz4");
////        properties.put("max.in.flight.requests.per.connection", 1); // 顺序发送必须加上
//        properties.put("retries", 3);
//        properties.put("request.timeout.ms", 12000);
//        properties.put("linger.ms", 10);
//        properties.put("batch.size", 65536);
//
//        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//
//        // 安全管控配置
////        properties.put("security.protocol", "SASL_PLAINTEXT");
////        properties.put("sasl.mechanism", "PLAIN");
////        properties.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"dkm_admin\" password=\"km_kMl4N8as1Kp0CCY\";");
//
//        return properties;
//    }
//
//
//    public static void main(String[] args){
////         kafka producer
//        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(createProperties());
//
//        for (int i= 0; i < 100000000009L; ++i) {
//            String topicName = "xgTest1";
//            producer.send(new ProducerRecord<String, String>(topicName, String.format("这是你的第%d条消息", i)), new Callback() {
//                @Override
//                public void onCompletion(RecordMetadata metadata, Exception exception) {
//                    if (metadata == null) {
//                        System.out.println(exception);
//                    }
//                }
//            });
//        }
//        producer.flush();
//        producer.close();
//    }
//}