package com.didichuxing.datachannel.agent.integration.test.basic;

/**
 * @description:
 * @author: huangjw
 * @Date: 19/2/12 16:26
 *
 * A Kafka Consumer implementation. This uses the current thread to fetch the
 * next message from the queue and doesn't use a multi threaded implementation.
 * So this implements a synchronous blocking call.
 * To avoid infinite waiting, a timeout is implemented to wait only for
 * 10 seconds before concluding that the message will not be available.
 *
 */

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConsumer {

    private static final Logger LOGGER        = LoggerFactory.getLogger(KafkaConsumer.class
                                                  .getName());
    private static final String consumerGroup = "consumer-group";

    public void shutdown() {
        kafkaConsumer.close();
    }

    org.apache.kafka.clients.consumer.KafkaConsumer<String, String> kafkaConsumer = null;
    String                                                          bootstraps;
    String                                                          topic         = null;

    public KafkaConsumer(String bootstraps, String topic) {
        this.bootstraps = bootstraps;
        this.topic = topic;
        kafkaConsumer = new org.apache.kafka.clients.consumer.KafkaConsumer<String, String>(
            buildPro());
        initTopic(topic);
    }

    Properties buildPro() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstraps);
        props.put("group.id", consumerGroup + "_" + topic + "_" + System.currentTimeMillis());
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }

    public void initTopicList(List<String> topics) {
        kafkaConsumer.subscribe(topics);
    }

    public void initTopic(String topic) {
        kafkaConsumer.subscribe(Collections.singleton(topic));
    }

    public ConsumerRecords<String, String> getNextMessage(String topic) {
        try {
            return kafkaConsumer.poll(1000L);
        } catch (Exception e) {
            LOGGER.error("0 messages available to fetch for the topic " + topic);
            return null;
        }
    }
}
