package com.didichuxing.datachannel.agent.sink.kafkaSink.errorlog;

import com.didichuxing.datachannel.agent.common.configs.v2.ErrorLogConfig;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaCallBack;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @description: kafka发送工具
 * @author: huangjw
 * @Date: 2019-07-17 11:53
 */
public class KafkaSender {

    private static final Logger  LOGGER = LoggerFactory.getLogger(KafkaSender.class.getName());
    private ErrorLogConfig       config;
    private KafkaMessageProducer producer;

    public KafkaSender(ErrorLogConfig config) throws Exception {
        this.config = config;

        if (!config.isValid()) {
            throw new Exception("kafka sender's config error, config :" + config);
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
                LOGGER.error("init kafka sender failed in send process! config is:" + config, e);
            }
        }

        if (producer != null) {
            producer.send(config.getTopic(), CommonUtils.getHOSTNAME(),
                content.getBytes(StandardCharsets.UTF_8),
                new ErrorLogKafkaCallBack(config.getTopic()));

        }
    }

    // 返回是否需要重新构建一个KafkaSender
    public boolean onChange(ErrorLogConfig newConfig) {
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
        return "KafkaSender{" + "config=" + config + ", producer=" + producer + '}';
    }
}
