package com.didichuxing.datachannel.agent.sink.kafkaSink;

import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.didichuxing.datachannel.agent.common.api.KafkaCompression;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: kafka producer
 * @author: huangjw
 * @Date: 18/6/21 19:01
 */
public class KafkaMessageProducer {

    private static final Logger           LOGGER             = LoggerFactory
                                                                 .getLogger(KafkaMessageProducer.class);

    /** * 默认配置文件 */
    private static String                 DEFAULT_PROPERTIES = "default.kafka-producer.properties";

    /** * 默认超时时间 */
    private static final Long             DEFAULT_TIME_OUT   = 3000L;

    /** * bootstrap地址 */
    private String                        bootstrap          = null;

    /** * 外部配置 */
    private Properties                    properties         = null;

    /** * kafka客户端 */
    private KafkaProducer<String, byte[]> kafkaProducer      = null;

    /** * flush标志 */
    private volatile AtomicBoolean        isflushing         = new AtomicBoolean(false);

    /** * 是否已经停止 */
    private volatile boolean              isStop             = false;

    /**
     * 唯一id
     */
    private String                        uniqueKey          = null;

    private String                        kafkaProducerKey   = "";

    public KafkaMessageProducer(Properties propreties, String bootstrap, Long modelId, int orderNum) {
        this.properties = propreties;
        // gateway地址
        if (propreties.containsKey(LogConfigConstants.GATEWAY)) {
            String gateway = (String) propreties.get(LogConfigConstants.GATEWAY);
            if (StringUtils.isNotBlank(gateway)) {
                this.bootstrap = gateway.replace(";", ",");
            } else {
                this.bootstrap = bootstrap;
            }
        } else {
            this.bootstrap = bootstrap;
        }
        this.uniqueKey = modelId + "_" + orderNum;
        try {
            init();
        } catch (Exception e) {
            LogGather.recordErrorLog("KafkaMessageProducer error",
                "init kafka producer error.propreties is " + propreties + ", bootstrap is "
                        + bootstrap, e);
        }
    }

    private void init() throws Exception {
        LOGGER.info("begin to init kafka producer.properties is " + properties + ",bootstrap is "
                    + bootstrap);
        // 读取配置文件的属性
        Properties confProperties = new Properties();
        try {
            confProperties.load(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(DEFAULT_PROPERTIES));
        } catch (Exception e) {
            LOGGER.info("there is no kafka-producer.properties file!", e);
        }

        if (properties != null && properties.size() > 0) {
            confProperties.putAll(properties);
        }

        if (StringUtils.isNotBlank(bootstrap)) {
            confProperties.put("bootstrap.servers", bootstrap);
        }

        // 兼容压缩参数
        String compressionType = confProperties.getProperty("compression.codec");
        if (compressionType != null && compressionType.length() > 0) {
            confProperties.put(
                "compression.type",
                KafkaCompression.forId(
                    Integer.parseInt((String) confProperties.get("compression.codec"))).getName());
            confProperties.remove("compression.codec");
        } else {
            // 默认值
            confProperties.put("compression.type", KafkaCompression.forId(0).getName());
        }

        // 兼容key,value类型参数
        // 当没有配置文件时，设置最基本的序列化类
        confProperties.put("key.serializer",
            "org.apache.kafka.common.serialization.StringSerializer");
        confProperties.remove("serializer.class");

        // 兼容ack参数
        String ackType = confProperties.getProperty("request.required.acks");
        if (ackType != null && ackType.length() > 0) {
            confProperties.put("acks", confProperties.get("request.required.acks"));
            confProperties.remove("request.required.acks");
        } else {
            // 默认值
            confProperties.put("acks", 1 + "");
        }

        // auth鉴权配置，新老兼容
        if (confProperties.containsKey(LogConfigConstants.SEC_PROTOCOL)) {
            confProperties.put("security.protocol",
                confProperties.getProperty(LogConfigConstants.SEC_PROTOCOL));
            confProperties.remove(LogConfigConstants.SEC_PROTOCOL);
        }

        if (confProperties.containsKey(LogConfigConstants.SASL_MECHANISM)) {
            confProperties.put("sasl.mechanism",
                confProperties.getProperty(LogConfigConstants.SASL_MECHANISM));
            confProperties.remove(LogConfigConstants.SASL_MECHANISM);
        }

        if (confProperties.containsKey(LogConfigConstants.SASL_JAAS_CONFIG)) {
            confProperties.put("sasl.jaas.config",
                confProperties.getProperty(LogConfigConstants.SASL_JAAS_CONFIG));
            confProperties.remove(LogConfigConstants.SASL_JAAS_CONFIG);
        }

        Properties bytesConfProperties = new Properties();
        bytesConfProperties.putAll(confProperties);
        // 发送kafka的类型定义为bytes数组
        bytesConfProperties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        kafkaProducer = new KafkaProducer<>(bytesConfProperties);
        LOGGER.info("init kafka client successfully.config is " + confProperties);
        kafkaProducerKey = buildProducerKey();
        LOGGER.info("init kafka producer key success. key is " + kafkaProducerKey);
    }

    private String buildProducerKey() {
        return this.uniqueKey + "_" + System.currentTimeMillis();
    }

    /**
     * 判断是否初始化完成, 在dns出现异常，或者断网时，producer初始化会失败，所以需要该方法进行校验
     */
    public boolean isInitOk() {
        return kafkaProducer == null ? false : true;
    }

    /**
     * 同步发送
     *
     * @param topic
     * @param key
     * @param value
     * @return
     */
    public boolean sendSync(String topic, String key, byte[] value) {
        if (kafkaProducer == null || isStop) {
            return false;
        }
        try {
            Future<RecordMetadata> future = kafkaProducer.send(new ProducerRecord<>(
                topic, key, value));
            RecordMetadata metadata = future.get(DEFAULT_TIME_OUT, TimeUnit.MILLISECONDS);
            if (metadata != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            LogGather.recordErrorLog("KafkaMessageProducer error",
                "producerSync meesage to kafka failed! topic is " + topic + ",uniqueKey is "
                        + uniqueKey + ",producerKey is " + kafkaProducerKey, e);
        }
        return false;
    }

    /**
     * timestamp作为key的发送方法
     *
     * @param topic
     * @param value
     */
    public void send(String topic, byte[] value) {
        kafkaProducer.send(new ProducerRecord<>(topic, String.valueOf(System
            .currentTimeMillis()), value));
    }

    /**
     * 无callback的发送方法
     *
     * @param topic
     * @param key
     * @param value
     */
    public void send(String topic, String key, byte[] value) {
        kafkaProducer.send(new ProducerRecord<>(topic, key, value));
    }

    /**
     * producer
     *
     * @param topic
     * @param key
     * @param value
     * @param callback
     */
    public boolean send(String topic, String key, byte[] value, Callback callback) {
        kafkaProducer.send(new ProducerRecord<>(topic, key, value), callback);
        return true;
    }

    /**
     * producer with partition
     *
     * @param topic
     * @param partition
     * @param key
     * @param value
     * @param callback
     */
    public void send(String topic, Integer partition, String key, byte[] value, Callback callback) {
        kafkaProducer.send(new ProducerRecord<>(topic, partition, key, value),
            callback);
    }

    /**
     * 发送数据
     *
     * @param topic
     * @param partition
     * @param timestamp
     * @param key
     * @param value
     * @param callback
     */
    public void send(String topic, Integer partition, Long timestamp, String key, byte[] value,
                     Callback callback) {
        kafkaProducer.send(new ProducerRecord<>(topic, partition, timestamp, key,
            value), callback);
    }

    /**
     * 强制刷新,需要保证数据的数据均发送成功 stop时，标记刷新为失败，此时可能有任务在重试发送，被强制停止了
     */
    public boolean flush() {
        if (!isflushing.get()) {
            isflushing.compareAndSet(false, true);
            kafkaProducer.flush();
            isflushing.compareAndSet(true, false);
        }
        if (isStop) {
            return false;
        }
        return true;
    }

    /**
     * 停止producer 强制关闭producer，不需要保证其数据发送成功
     */
    public void stop() {
        LOGGER.info("begin to stop producer! uniqueKey is " + uniqueKey + ",producerKey is "
                    + kafkaProducerKey);
        // 停止producer
        setStop(true);
        if (isInitOk()) {
            kafkaProducer.close();
        }
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getKafkaProducerKey() {
        return kafkaProducerKey;
    }
}
