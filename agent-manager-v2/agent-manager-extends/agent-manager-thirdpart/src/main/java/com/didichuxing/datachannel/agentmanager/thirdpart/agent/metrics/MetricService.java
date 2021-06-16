package com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentMetricDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.CollectTaskMetricDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverTopicDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.AgentMetricPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.AgentPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.CollectTaskMetricPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.receiver.KafkaClusterPO;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentMapper;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentMetricMapper;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.CollectTaskMetricMapper;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.KafkaClusterMapper;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * todo 动态设置kafka连接
 */
@Service
public class MetricService {
    private static final ILog LOGGER = LogFactory.getLog(MetricService.class);

    @Autowired
    private AgentMetricMapper agentMetricMapper;

    @Autowired
    private AgentMapper agentMapper;

    @Autowired
    private KafkaClusterMapper kafkaClusterMapper;

    @Autowired
    private CollectTaskMetricMapper collectTaskMetricMapper;

    private static final String CONSUMER_GROUP_ID = "g1";

    private static Set<ReceiverTopicDO> receiverSet = new HashSet<>();

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            5, 20, 2, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));

    private void loadClustersAndTopics() {
        receiverSet.clear();
        List<AgentPO> agentPOList = agentMapper.getAll();
        List<AgentDO> agentDOList = ConvertUtil.list2List(agentPOList, AgentDO.class);
        for (AgentDO agentDO : agentDOList) {
            ReceiverTopicDO receiverTopicDO = new ReceiverTopicDO();
            if (agentDO.getMetricsSendReceiverId() == null || agentDO.getMetricsSendTopic() == null) {
                continue;
            }
            receiverTopicDO.setReceiverId(agentDO.getMetricsSendReceiverId());
            receiverTopicDO.setTopic(agentDO.getMetricsSendTopic());
            receiverSet.add(receiverTopicDO);
        }
    }

    public void writeMetrics(ReceiverTopicDO receiverTopicDO) {
        KafkaClusterPO kafkaClusterPO = kafkaClusterMapper.selectByPrimaryKey(receiverTopicDO.getReceiverId());
        LOGGER.info("Thread: {}, cluster name: {}, topic: {}", Thread.currentThread().getName(), kafkaClusterPO.getKafkaClusterName(), receiverTopicDO.getTopic());
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaClusterPO.getKafkaClusterBrokerConfiguration());
        props.put("group.id", CONSUMER_GROUP_ID);
        props.put("auto.offset.reset", "latest");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(receiverTopicDO.getTopic()));

        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
                for (ConsumerRecord<String, String> record : records) {
                    JSONObject object = JSON.parseObject(record.value());
                    if (object.getInteger("logModeId") == -1) {
                        AgentMetricDO agentMetric = JSON.parseObject(record.value(), AgentMetricDO.class);
                        AgentMetricPO agentMetricPO = ConvertUtil.obj2Obj(agentMetric, AgentMetricPO.class);
                        agentMetricMapper.insertSelective(agentMetricPO);
                    } else {
                        CollectTaskMetricDO collectTaskMetric = JSON.parseObject(record.value(), CollectTaskMetricDO.class);
                        CollectTaskMetricPO collectTaskMetricPO = ConvertUtil.obj2Obj(collectTaskMetric, CollectTaskMetricPO.class);
                        collectTaskMetricMapper.insertSelective(collectTaskMetricPO);
                    }
                }
            } catch (Throwable e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    public void clear() {
        agentMetricMapper.deleteBeforeTime(System.currentTimeMillis() - 7 * 24 * 3600 * 1000);
        collectTaskMetricMapper.deleteBeforeTime(System.currentTimeMillis() - 7 * 24 * 3600 * 1000);
    }

    @Async
    public void run() {
        loadClustersAndTopics();
        executor.execute(() -> {
            try {
                Thread.sleep(60 * 1000);
                loadClustersAndTopics();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        for (ReceiverTopicDO receiverTopicDO : receiverSet) {
            executor.execute(() -> writeMetrics(receiverTopicDO));
        }
    }

}
