package com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverTopicDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.AgentPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.receiver.KafkaClusterPO;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.*;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class MetricService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricService.class);

    @Autowired
    private AgentMetricMapper agentMetricMapper;

    @Autowired
    private AgentMapper agentMapper;

    @Autowired
    private KafkaClusterMapper kafkaClusterMapper;

    @Autowired
    private CollectTaskMetricMapper collectTaskMetricMapper;

    @Autowired
    private ErrorLogMapper errorLogMapper;

    @Autowired
    private AgentMetricsDAO agentMetricsDAO;

    @Value("${agent.metrics.producer.appId:#{null}}")
    private String appId;

    @Value("${agent.metrics.producer.clusterId:#{null}}")
    private String clusterId;

    @Value("${agent.metrics.producer.password:#{null}}")
    private String password;

    private static volatile boolean trigger = false;

    private static final String CONSUMER_GROUP_ID = "g1";
    private static final long RETENTION_TIME = 7 * 24 * 3600 * 1000;

    private static Set<ReceiverTopicDO> metricSet = new HashSet<>();
    private static Set<ReceiverTopicDO> errorSet = new HashSet<>();

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            5, 20, 2, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));

    private void loadClustersAndTopics() {
        List<AgentPO> agentPOList = agentMapper.getAll();
        List<AgentDO> agentDOList = ConvertUtil.list2List(agentPOList, AgentDO.class);
        for (AgentDO agentDO : agentDOList) {
            ReceiverTopicDO receiverTopicDO = new ReceiverTopicDO();
            if (agentDO.getMetricsSendReceiverId() == null || agentDO.getMetricsSendTopic() == null) {
                continue;
            }
            receiverTopicDO.setReceiverId(agentDO.getMetricsSendReceiverId());
            receiverTopicDO.setTopic(agentDO.getMetricsSendTopic());
            metricSet.add(receiverTopicDO);
        }
        for (AgentDO agentDO : agentDOList) {
            ReceiverTopicDO receiverTopicDO = new ReceiverTopicDO();
            if (agentDO.getErrorLogsSendReceiverId() == null || agentDO.getErrorLogsSendTopic() == null) {
                continue;
            }
            receiverTopicDO.setReceiverId(agentDO.getErrorLogsSendReceiverId());
            receiverTopicDO.setTopic(agentDO.getErrorLogsSendTopic());
            errorSet.add(receiverTopicDO);
        }
    }

    public void writeMetrics(ReceiverTopicDO receiverTopicDO) {
        KafkaClusterPO kafkaClusterPO = kafkaClusterMapper.selectByPrimaryKey(receiverTopicDO.getReceiverId());
        LOGGER.info("Thread: {}, cluster name: {}, topic: {}", Thread.currentThread().getName(), kafkaClusterPO.getKafkaClusterName(), receiverTopicDO.getTopic());
        Properties properties = getProducerProps(kafkaClusterPO.getKafkaClusterBrokerConfiguration());
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(receiverTopicDO.getTopic()));

        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
                agentMetricsDAO.writeMetrics(records);
                if (trigger) {
                    consumer.close();
                    break;
                }
            } catch (Throwable e) {
                LOGGER.error(e.getMessage());
                consumer.close();
                break;
            }
        }
    }

    public void writeErrors(ReceiverTopicDO receiverTopicDO) {
        KafkaClusterPO kafkaClusterPO = kafkaClusterMapper.selectByPrimaryKey(receiverTopicDO.getReceiverId());
        LOGGER.info("Thread: {}, cluster name: {}, topic: {}", Thread.currentThread().getName(), kafkaClusterPO.getKafkaClusterName(), receiverTopicDO.getTopic());
        Properties properties = getProducerProps(kafkaClusterPO.getKafkaClusterBrokerConfiguration());
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(receiverTopicDO.getTopic()));
        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
                agentMetricsDAO.writeErrors(records);
                if (trigger) {
                    consumer.close();
                    break;
                }
            } catch (Throwable e) {
                LOGGER.error(e.getMessage());
                consumer.close();
            }
        }
    }

    private Properties getProducerProps(String bootstrapServers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", CONSUMER_GROUP_ID);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        if (appId != null && clusterId != null && password != null) {
            String format = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s.%s\" password=\"%s\";";
            String jaasConfig = String.format(format, clusterId, appId, password);
            props.put("sasl.jaas.config", jaasConfig);
            props.put("security.protocol", "SASL_PLAINTEXT");
            props.put("sasl.mechanism", "PLAIN");
        }
        return props;
    }

    public void clear() {
        agentMetricMapper.deleteBeforeTime(System.currentTimeMillis() - RETENTION_TIME);
        collectTaskMetricMapper.deleteBeforeTime(System.currentTimeMillis() - RETENTION_TIME);
        errorLogMapper.deleteBeforeTime(System.currentTimeMillis() - RETENTION_TIME);
    }

    @PostConstruct
    public void resetMetricConsumers() {
        metricSet.clear();
        errorSet.clear();
        trigger = true;
        try {
            // 等待现有的kafka consumer线程全部关闭
            Thread.sleep(10 * 1000);
            loadClustersAndTopics();
        } catch (InterruptedException e) {
            LOGGER.error("thread interrupted", e);
        }
        trigger = false;
        for (ReceiverTopicDO receiverTopicDO : metricSet) {
            executor.execute(() -> writeMetrics(receiverTopicDO));
        }
        for (ReceiverTopicDO receiverTopicDO : errorSet) {
            executor.execute(() -> writeErrors(receiverTopicDO));
        }
    }

}
