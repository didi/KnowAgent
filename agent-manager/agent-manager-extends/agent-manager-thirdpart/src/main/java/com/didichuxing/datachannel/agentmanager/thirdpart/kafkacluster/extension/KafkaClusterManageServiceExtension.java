package com.didichuxing.datachannel.agentmanager.thirdpart.kafkacluster.extension;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.receiver.KafkaClusterPO;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.util.Comparator;

import java.util.List;
import java.util.Properties;
import java.util.Set;

public interface KafkaClusterManageServiceExtension {

    /**
     * 将给定KafkaClusterPO对象转化为KafkaCluster对象
     * @param kafkaClusterPO 待转化KafkaClusterPO对象
     * @return 返回将给定KafkaClusterPO对象转化的KafkaCluster对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    ReceiverDO kafkaClusterPO2KafkaCluster(KafkaClusterPO kafkaClusterPO) throws ServiceException;

    /**
     * 将给定KafkaCluster对象转化为KafkaClusterPO对象
     * @param kafkaCluster 待转化KafkaCluster对象
     * @return 返回将给定KafkaCluster对象转化的KafkaClusterPO对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    KafkaClusterPO kafkaCluster2KafkaClusterPO(ReceiverDO kafkaCluster) throws ServiceException;

    /**
     * 校验给定待修改KafkaCluster对象是否合法
     * @param kafkaCluster 待校验KafkaCluster对象
     * @return true：合法 false：不合法
     */
    CheckResult checkModifyParameterKafkaCluster(ReceiverDO kafkaCluster);

    /**
     * 校验给定待创建KafkaCluster对象是否合法
     * @param kafkaCluster 待校验KafkaCluster对象
     * @return true：合法 false：不合法
     */
    CheckResult checkCreateParameterKafkaCluster(ReceiverDO kafkaCluster);

    /**
     * @return 返回 KafkaClusterPO 对象比较器
     */
    Comparator<ReceiverDO, Long> getComparator();

    /**
     * 根据给定待更新ReceiverDO对象targetKafkaClusterDO更新对应源ReceiverDO对象sourceReceiverDO
     * @param sourceReceiverDO 源ReceiverDO对象
     * @param targetKafkaClusterDO 待更新ReceiverDO对象
     * @return 待更新至db的ReceiverDO对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    ReceiverDO updateKafkaCluster(ReceiverDO sourceReceiverDO, ReceiverDO targetKafkaClusterDO) throws ServiceException;

    /**
     * 将KafkaClusterPO对象集转化为ReceiverDO对象集
     * @param kafkaClusterPOList 待转化KafkaClusterPO对象集
     * @return 返回将KafkaClusterPO对象集转化为的ReceiverDO对象集
     */
    List<ReceiverDO> kafkaClusterPOList2ReceiverDOList(List<KafkaClusterPO> kafkaClusterPOList);

    /**
     * 校验给定接收端对应topic端是否存在限流
     * @param receiverDO 接收端对象
     * @param topic topic 名
     * @return true：存在限流 false：不存在限流
     */
    boolean checkTopicLimitExists(ReceiverDO receiverDO, String topic);

    /**
     * 获取给定接收端对应各Kafka Broker节点对应ip & port
     * @param receiverDO 接收端对象
     * @return 返回获取到的给定接收端对应各Kafka Broker节点对应ip & port
     */
    List<Pair<String, Integer>> getBrokerIp2PortPairList(ReceiverDO receiverDO);

    /**
     * 校验通过给定 kafka producer 参数构建 kafka producer 对象是否可用
     * @param brokerConfiguration kafka broker 配置参数
     * @param topic topic 名
     * @param producerConfiguration kafka producer 配置
     * @return true：可用 false：不可用
     */
    Boolean checkProducerConfigurationValid(String brokerConfiguration, String topic, String producerConfiguration);

    /**
     * 根据 kafka broker 配置获取 topic 集
     * @param kafkaClusterBrokerConfiguration kafka broker 配置
     * @return 返回根据 kafka broker 配置获取到的 topic 集
     */
    Set<String> listTopics(String kafkaClusterBrokerConfiguration);

    Properties getKafkaConsumerProperties(String bootstrapServers);

}
