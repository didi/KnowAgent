package com.didichuxing.datachannel.agent.sink.kafkaSink;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @description: kafka producer池
 * @author: huangjw
 * @Date: 18/6/22 14:03
 */
public class KafkaProducerContainer {

private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerContainer.class.getName());// key:modelId + orderNum
    private static ConcurrentHashMap<String, KafkaMessageProducer> kafkaMessageProducerMap = new ConcurrentHashMap<>();

    private static Object                                          lock                    = new Object();

    /** * 获取kafka producer */
    public static KafkaMessageProducer getMessageProducer(KafkaSink kafkaSink) {
        String key = getkafkaProducerKey(kafkaSink);
        KafkaMessageProducer producer = kafkaMessageProducerMap.get(key);
        if (producer != null) {
            return producer;
        } else {
            synchronized (lock) {
                if (kafkaMessageProducerMap.get(key) == null) {
                    synchronized (lock) {
                        producer = new KafkaMessageProducer(CommonUtils.string2Property(kafkaSink.getKafkaTargetConfig().getProperties()),
                                                            kafkaSink.getKafkaTargetConfig().getBootstrap(),
                                                            kafkaSink.getModelConfig().getCommonConfig().getModelId(),
                                                            kafkaSink.getOrderNum());
                        if (producer.isInitOk()) {
                            kafkaMessageProducerMap.put(key, producer);
                            return producer;
                        } else {
                            return null;
                        }
                    }
                } else {
                    return kafkaMessageProducerMap.get(key);
                }
            }
        }
    }

    /**
     * 删除发送配置
     *
     * @param modelId
     * @return
     */
    public synchronized static void delete(Integer modelId) {
        LOGGER.info("delete producer.modelId is " + modelId);
        Set<String> keySet = new HashSet<>();
        for (String key : kafkaMessageProducerMap.keySet()) {
            if (key.startsWith(modelId + "_")) {
                keySet.add(key);
            }
        }

        if (keySet.size() != 0) {
            for (String key : keySet) {
                KafkaMessageProducer producer = kafkaMessageProducerMap.get(key);
                if (producer != null) {
                    producer.stop();
                    kafkaMessageProducerMap.remove(key);
                }
            }
        }
    }

    /**
     * 删除发送配置
     *
     * @param modelId
     * @param orderNum
     * @return
     */
    public synchronized static void delete(Long modelId, int orderNum) {
        LOGGER.info("delete producer.modelId is " + modelId + ", orderNum is " + orderNum);
        String key = getkafkaProducerKey(modelId, orderNum);
        KafkaMessageProducer producer = kafkaMessageProducerMap.get(key);
        if (producer != null) {
            producer.stop();
            kafkaMessageProducerMap.remove(key);
        }
    }

    /**
     * 更改发送配置
     *
     * @param
     * @return
     */
    public synchronized static void update(ModelConfig modelConfig) {
        KafkaTargetConfig kafkaTargetConfig = (KafkaTargetConfig) modelConfig.getTargetConfig();
        long modelId = modelConfig.getCommonConfig().getModelId();
        LOGGER.info("update producer.config is " + kafkaTargetConfig + ", modelId is " + modelId);

        Set<String> keySet = new HashSet<>();
        for (String key : kafkaMessageProducerMap.keySet()) {
            if (key.startsWith(modelId + "_")) {
                keySet.add(key);
            }
        }

        if (keySet.size() != 0) {
            for (String key : keySet) {
                KafkaMessageProducer producer = kafkaMessageProducerMap.get(key);
                int orderNum = Integer.parseInt(key.substring(key.indexOf("_") + 1));
                if (producer != null) {
                    KafkaMessageProducer newProducer = new KafkaMessageProducer(CommonUtils.string2Property(kafkaTargetConfig.getProperties()),
                                                                                kafkaTargetConfig.getBootstrap(),
                                                                                modelId, orderNum);
                    if (newProducer.isInitOk()) {
                        LOGGER.info("update producer.success. modelId is " + modelId + ", orderNum is " + orderNum);
                        kafkaMessageProducerMap.put(key, newProducer);
                    }
                    producer.stop();
                }
            }
        }

    }

    /**
     * 停止
     */
    public synchronized static void stop() {
        LOGGER.info("begin to stop producers in kafkaProducerContainer.");
        if (kafkaMessageProducerMap != null && kafkaMessageProducerMap.size() != 0) {
            for (KafkaMessageProducer kafkaMessageProducer : kafkaMessageProducerMap.values()) {
                kafkaMessageProducer.stop();
            }
        }
        LOGGER.info("success to stop producers in kafkaProducerContainer.");
    }

    private static String getkafkaProducerKey(KafkaSink sink) {
        return sink.getModelConfig().getCommonConfig().getModelId() + "_" + sink.getOrderNum();
    }

    private static String getkafkaProducerKey(Long modelId, int orderNum) {
        return modelId + "_" + orderNum;
    }
}
