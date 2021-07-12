package com.didichuxing.datachannel.agentmanager.remote.kafkacluster;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import java.util.List;
import java.util.Set;

public interface RemoteKafkaClusterService {

    /**
     * @return 从远程获取全量KafkaCluster信息
     */
    List<ReceiverDO> getKafkaClustersFromRemote();

    /**
     * @param kafkaClusterId 对应 kafka-manager 系统记录的kafkaclusterId
     * @return 返回kafkaclusterId对应kafka集群中所有的topic列表信息
     */
    Set<String> getTopicsByKafkaClusterId(Long kafkaClusterId);

    /**
     * 获取给定KafkaCluster={id=kafkaClusterId}对应topic={topicName}在租户={appId}下是否存在限流
     * @param kafkaClusterId KafkaCluster 对象 id
     * @param topicName topic 名
     * @param kafkaProducerSecurity kafka 生产单安全结构体
     * @return 返回给定KafkaCluster={id=kafkaClusterId}对应topic={topicName}在租户={appId}下是否存在限流 true：存在限流 false：不存在限流
     */
    boolean checkTopicLimitExists(Long kafkaClusterId, String topicName, KafkaProducerSecurity kafkaProducerSecurity);

}
