package com.didichuxing.datachannel.agentmanager.rest;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class SimpleConsumer {

    private static final Logger LOGGER           = LoggerFactory.getLogger(SimpleConsumer.class);

    private static String topicName = "metric";
    private static final String address = "10.255.1.196:9092";

    private static String group = "d2";

    public static void print() {

        LOGGER.error(" start to print ");

        Properties props = new Properties();
        props.put("bootstrap.servers", address);
        props.put("group.id", group);
        props.put("auto.offset.reset", "latest");
        props.put("compression.type", "lz4");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topicName));

        long count = 0;
        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(3));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(count + "\t" + record.value());
                    LOGGER.error(count + "\t" + record.value());
                    LOGGER.error(" ==================== ");
                    count++;
                }
            } catch (Throwable ignored) {
            }
        }
    }
}
