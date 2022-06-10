package com.didichuxing.datachannel.agent.integration.test.basic;

import java.io.File;
import java.net.BindException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.consumer.ConsumerRecords;



import kafka.admin.AdminUtils;
import kafka.utils.ZkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 启动/停止 kafka server
 * @author: huangjw
 * @Date: 19/2/12 16:25
 */
public class BasicUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicUtil.class);
    private static BasicUtil   instance    = new BasicUtil();

    private Random             randPortGen = new Random(System.currentTimeMillis());
    private KafkaLocal         kafkaServer;
    private String             hostname    = "localhost";
    private int                kafkaLocalPort;
    private int                zkLocalPort;

    Map<String, KafkaConsumer> consuemrMap = new HashMap<>();

    private BasicUtil(){
        init();
    }

    public static BasicUtil getInstance() {
        return instance;
    }

    private void init() {
        // get the localhost.
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            LOGGER.warn("Error getting the value of localhost. " + "Proceeding with 'localhost'.", e);
        }
    }

    private void removeData() {
        LOGGER.info("starting remove zk and kafka data.");
        Properties kafkaProperties = new Properties();
        Properties zkProperties = new Properties();
        try {
            zkProperties.load(Class.class.getResourceAsStream("/zookeeper.properties"));
            kafkaProperties.load(Class.class.getResourceAsStream("/kafka-server.properties"));
            String zkDirPath = zkProperties.getProperty("dataDir");
            String kafkaDirPath = kafkaProperties.getProperty("log.dirs");
            String path = System.getProperty("user.dir");

            File zkDir = new File(path + "/" + zkDirPath + "/version-2");
            FileUtils.deleteDirectory(zkDir);

            File kafkaDir = new File(path + "/" + kafkaDirPath + "/kafka-logs");
            FileUtils.deleteDirectory(kafkaDir);

        } catch (Exception e) {
            LOGGER.error("remove zk and kafka data error", e);
        }
    }

    private boolean startKafkaServer() {
        Properties kafkaProperties = new Properties();
        Properties zkProperties = new Properties();

        LOGGER.info("Starting kafka server.");
        try {
            // load properties
            zkProperties.load(Class.class.getResourceAsStream("/zookeeper.properties"));

            ZooKeeperLocal zookeeper;
            while (true) {
                // start local Zookeeper
                try {
                    zkLocalPort = getNextPort();
                    // override the Zookeeper client port with the generated one.
                    zkProperties.setProperty("clientPort", Integer.toString(zkLocalPort));
                    zookeeper = new ZooKeeperLocal(zkProperties);
                    break;
                } catch (BindException bindEx) {
                    // bind exception. port is already in use. Try a different port.
                }
            }
            LOGGER.info("ZooKeeper instance is successfully started on port " + zkLocalPort);

            kafkaProperties.load(Class.class.getResourceAsStream("/kafka-server.properties"));
            // override the Zookeeper url.
            kafkaProperties.setProperty("zookeeper.connect", getZkUrl());
            while (true) {
                kafkaLocalPort = getNextPort();
                // override the Kafka server port
                kafkaProperties.setProperty("port", Integer.toString(kafkaLocalPort));
                kafkaServer = new KafkaLocal(kafkaProperties);
                try {
                    kafkaServer.start();
                    break;
                } catch (Exception e) {
                    // let's try another port.
                    e.printStackTrace();
                }
            }
            LOGGER.info("Kafka Server is successfully started on port " + kafkaLocalPort);
            return true;

        } catch (Exception e) {
            LOGGER.error("Error starting the Kafka Server.", e);
            return false;
        }
    }

    public void initTopicList(List<String> topics) {
        for (String topic : topics) {
            if (consuemrMap.get(topic) == null) {
                consuemrMap.put(topic, new KafkaConsumer(getKafkaServerUrl(), topic));
            }
        }
    }

    public ConsumerRecords<String, String> getNextMessageFromConsumer(String topic) {
        return consuemrMap.get(topic).getNextMessage(topic);
    }

    public void prepare() {
        removeData();
        boolean startStatus = startKafkaServer();
        if (!startStatus) {
            throw new RuntimeException("Error starting the server!");
        }
        try {
            Thread.sleep(3 * 1000); // add this sleep time to
            // ensure that the server is fully started before proceeding with tests.
        } catch (InterruptedException e) {
            // ignore
        }
        LOGGER.info("Completed the prepare phase.");
    }

    public void tearDown() {
        LOGGER.info("Shutting down the Kafka Consumer.");
        for (KafkaConsumer consumer : consuemrMap.values()) {
            try {
                consumer.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(3 * 1000); // add this sleep time to
            // ensure that the server is fully started before proceeding with tests.
        } catch (InterruptedException e) {
            // ignore
        }
        LOGGER.info("Shutting down the kafka Server.");
        kafkaServer.stop();
        removeData();
        LOGGER.info("Completed the tearDown phase.");
    }

    private synchronized int getNextPort() {
        // generate a random port number between 49152 and 65535
        return randPortGen.nextInt(65535 - 49152) + 49152;
    }

    public String getZkUrl() {
        return hostname + ":" + zkLocalPort;
    }

    public String getKafkaServerUrl() {
        return hostname + ":" + kafkaLocalPort;
    }

    /**
     * 创建topic
     * 
     * @param topicName
     * @param numPartitions
     */
    public void createTopic(String topicName, int numPartitions) {
        try {
            int sessionTimeoutMs = 10000;
            int connectionTimeoutMs = 10000;
            ZkUtils zkUtils = ZkUtils.apply(instance.getZkUrl(), sessionTimeoutMs, connectionTimeoutMs, false);
            int replicationFactor = 1;
            Properties topicConfig = new Properties();
            AdminUtils.createTopic(zkUtils, topicName, numPartitions, replicationFactor, topicConfig, null);
            LOGGER.info("create topic[" + topicName + "], partition is " + numPartitions + " success!");
            zkUtils.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除topic
     * 
     * @param topicName
     */
    public void deleteTopic(String topicName) {
        int sessionTimeoutMs = 10000;
        int connectionTimeoutMs = 10000;
        ZkUtils zkUtils = ZkUtils.apply(instance.getZkUrl(), sessionTimeoutMs, connectionTimeoutMs, false);
        AdminUtils.deleteTopic(zkUtils, topicName);
    }
}
