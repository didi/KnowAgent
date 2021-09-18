package com.didichuxing.datachannel.agent.node.service.metrics.sink;

import com.didichuxing.datachannel.agent.common.configs.v2.MetricConfig;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-18 21:44
 */
public class KafkaTopicSink {

    private static final Logger  LOGGER = LoggerFactory.getLogger(KafkaTopicSink.class.getName());
    private MetricConfig         config;
    private KafkaMessageProducer producer;

    public KafkaTopicSink(MetricConfig config) throws Exception {
        this.config = config;

        if (!config.isValid()) {
            throw new Exception("kafka topic sink config error, config :" + config);
        }

        try {
            producer = new KafkaMessageProducer(
                CommonUtils.string2Property(config.getProperties()), config.getNameServer(), null,
                -1);
            LOGGER.info("success init kafka topic sink by config:" + config.toString());
        } catch (Exception e) {
            LOGGER.error("init kafka topic sink failed!config is:" + config, e);
        }
    }

    public void stop() {
        producer.stop();
    }

    public void flush() {
        producer.flush();
    }

    public void send(String content) {
        // producer.send(config.getTopic(), content);
        // 主机名作为key发送
        if (producer == null) {
            try {
                producer = new KafkaMessageProducer(CommonUtils.string2Property(config
                    .getProperties()), config.getNameServer(), null, -1);
            } catch (Exception e) {
                LOGGER
                    .error("init kafka topic sink failed in send process! config is:" + config, e);
            }
        }

        if (producer != null) {
            producer.send(config.getTopic(), CommonUtils.getHOSTNAME(),
                content.getBytes(StandardCharsets.UTF_8));
        }
    }

    // 返回是否需要重新构建一个kafkTopicSink
    public boolean onChange(MetricConfig newConfig) {
        if (!config.getNameServer().equalsIgnoreCase(newConfig.getNameServer())) {
            return true;
        }

        if (config.getProperties() == null && newConfig.getProperties() != null) {
            return true;
        }

        if (!config.getProperties().equalsIgnoreCase(newConfig.getProperties())) {
            return true;
        }

        this.config = newConfig;

        return false;
    }

    @Override
    public String toString() {
        return "KafkaTopicSink{" + "config=" + config + ", producer=" + producer + '}';
    }
}
