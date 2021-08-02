package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.receiver.KafkaClusterPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "kafkaClusterDAO")
public interface KafkaClusterMapper {
    int deleteByPrimaryKey(Long id);

    int insert(KafkaClusterPO record);

    KafkaClusterPO selectByPrimaryKey(Long id);

    int updateByPrimaryKey(KafkaClusterPO record);

    KafkaClusterPO selectByKafkaClusterId(Long kafkaClusterId);

    List<KafkaClusterPO> list();

    List<KafkaClusterPO> paginationQueryByConditon(ReceiverPaginationQueryConditionDO receiverPaginationQueryConditionDO);

    Integer queryCountByConditon(ReceiverPaginationQueryConditionDO receiverPaginationQueryConditionDO);

    KafkaClusterPO selectByKafkaClusterName(String kafkaClusterName);

    KafkaClusterPO selectByKafkaClusterBrokerConfiguration(String kafkaClusterBrokerConfiguration);

    KafkaClusterPO getAgentErrorLogsTopicExistsKafkaCluster();

    KafkaClusterPO getAgentMetricsTopicExistsKafkaCluster();

}
