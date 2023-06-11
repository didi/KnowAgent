package com.didichuxing.datachannel.agent.integration.test.basic;

/**
 * @description: 本地kafka
 * @author: huangjw
 * @Date: 19/2/12 16:21
 */

import java.io.IOException;
import java.util.Properties;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;

/**
 * A local Kafka server for running unit tests. Reference: https://gist.github.com/fjavieralba/7930018/
 */
public class KafkaLocal {

    public KafkaServerStartable kafka;
    public ZooKeeperLocal       zookeeper;

    public KafkaLocal(Properties kafkaProperties) throws IOException, InterruptedException {
        KafkaConfig kafkaConfig = KafkaConfig.fromProps(kafkaProperties);

        // start local kafka broker
        kafka = new KafkaServerStartable(kafkaConfig);
    }

    public void start() throws Exception {
        kafka.startup();
    }

    public void stop() {
        kafka.shutdown();
    }

}
