package com.didichuxing.datachannel.swan.agent.sink.kafkaSink;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.didichuxing.datachannel.swan.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.swan.agent.integration.test.basic.BasicUtil;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-25 10:40
 */
public class KafkaMessageProducerTest {

    private static final Long   modelId = 0L;

    private static final String topic_0 = "test_topic_0";
    private static final String topic_1 = "test_topic_1";

    @BeforeClass
    public static void before() {
        // 启动前先停止
        // destory();
        // 启动kafka
        BasicUtil.getInstance().prepare();
        BasicUtil.getInstance().createTopic(topic_0, 1);
        BasicUtil.getInstance().createTopic(topic_1, 1);

        List<String> topics = new ArrayList<>();
        topics.add(topic_0);
        topics.add(topic_1);
        BasicUtil.getInstance().initTopicList(topics);
    }

    @AfterClass
    public static void destory() {
        // 停止kafka
        BasicUtil.getInstance().tearDown();
    }

    @Test
    public void sendSync() {
        String key = System.currentTimeMillis() + "";
        String value = "test for sendsync." + ",time is " + System.currentTimeMillis();
        KafkaMessageProducer producer = getProducer();
        producer.sendSync(topic_0, key, value);
        int max = 30;
        int result = max;
        for (int i = 0; i <= max; i++) {
            ConsumerRecords<String, String> records = BasicUtil.getInstance()
                .getNextMessageFromConsumer(topic_0);
            for (ConsumerRecord<String, String> record : records) {
                if (record != null && StringUtils.isNotBlank(record.value())
                    && record.value().equals(value) && record.key().equals(key)) {
                    System.out.println(record.key());
                    System.out.println(record.value());
                    result = i;
                    return;
                }
            }
        }
        assertTrue(result < max);
    }

    @Test
    public void sendASync() {
        String key = System.currentTimeMillis() + "";
        String value = "test for sendsync." + ",time is " + System.currentTimeMillis();
        KafkaMessageProducer producer = getProducer();
        TestKafkaCallBack callBack = new TestKafkaCallBack(key, value);
        producer.send(topic_1, key, value, callBack);
        int max = 30;
        int result = max;
        for (int i = 0; i <= max; i++) {
            if (callBack.isSend()) {
                result = i;
                break;
            }
            try {

                Thread.sleep(1000L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        assertTrue(result < max && callBack.isSend() && callBack.getKey().equals(key)
                   && callBack.getValue().equals(value));
    }

    private KafkaMessageProducer getProducer() {
        String bootstrap = BasicUtil.getInstance().getKafkaServerUrl();
        String propertiesStr = "serializer.class=kafka.serializer.StringEncoder,compression.codec=3,max.request.size=4194304";
        KafkaMessageProducer kafkaMessageProducer = new KafkaMessageProducer(
            CommonUtils.string2Property(propertiesStr), bootstrap, modelId, 0);
        return kafkaMessageProducer;
    }

    class TestKafkaCallBack implements Callback {

        private static final String errorKey   = "error.key";
        private static final String errorValue = "error.value";
        private volatile boolean    isSend     = false;

        String                      key;
        String                      value;

        public TestKafkaCallBack(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            isSend = true;
            if (metadata == null || exception != null) {
                this.key = errorKey;
                this.value = errorValue;
            }
            System.out.println("TestKafkaCallBack.key:" + this.key);
            System.out.println("TestKafkaCallBack.value:" + this.value);
        }

        public boolean isSend() {
            return isSend;
        }

        public void setSend(boolean send) {
            isSend = send;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
