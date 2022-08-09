package com.didichuxing.datachannel.agentmanager.core.kafkacluster;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverPaginationQueryConditionDO;

import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * kafka 集群信息管理服务
 */
public interface KafkaClusterManageService {

    /**
     * 获取全量kafka集群对象
     * @return 全量kafka集群对象
     */
    List<ReceiverDO> list();

    /**
     * 创建 KafkaClusterPO 对象
     * @param kafkaCluster 待创建 KafkaClusterPO 对象
     * @param operator 操作人
     * @return 批量创建是否成功 true：成功 false：不成功
     */
    Long createKafkaCluster(ReceiverDO kafkaCluster, String operator);

    /**
     * 修改 KafkaClusterPO 对象
     * @param kafkaCluster 待修改 KafkaClusterPO 对象
     * @param operator 操作人
     */
    void updateKafkaCluster(ReceiverDO kafkaCluster, String operator);

    /**
     * 根据 id 删除对应 KafkaClusterPO 对象
     * @param receiverIdList 待删除 id 集
     * @param ignoreLogCollectTaskAndAgentRelationCheck 是否忽略待删除kafkaCluster存在关联的LogCollectTask & Agent
     * @param operator 操作人
     */
    void deleteKafkaClusterById(List<Long> receiverIdList, boolean ignoreLogCollectTaskAndAgentRelationCheck, String operator);

    /**
     * 根据接收端对象 id 查询对应接收端对象
     * @param receiverId 接收端对象 id
     * @return 返回根据接收端对象 id 查询到的对应接收端对象
     */
    ReceiverDO getById(Long receiverId);

    /**
     * 根据给定参数分页查询结果集
     * @param receiverPaginationQueryConditionDO 分页查询条件
     * @return 返回根据给定参数分页查询到的结果集
     */
    List<ReceiverDO> paginationQueryByCondition(ReceiverPaginationQueryConditionDO receiverPaginationQueryConditionDO);

    /**
     * 根据给定参数查询满足条件的结果集总数量
     * @param receiverPaginationQueryConditionDO 查询条件
     * @return 返回根据给定参数查询到的满足条件的结果集总数量
     */
    Integer queryCountByCondition(ReceiverPaginationQueryConditionDO receiverPaginationQueryConditionDO);

    /**
     *
     * @return 返回系统全局 agent error logs 流对应接收端
     */
    ReceiverDO getAgentErrorLogsTopicExistsReceiver();

    /**
     * @return 返回系统全局 agent metrics 流对应接收端
     */
    ReceiverDO getAgentMetricsTopicExistsReceiver();

    /**
     * 校验给定参数值是否已配置
     * @param receiverId 接收端对象 id
     * @param topic topic 名
     * @param producerConfiguration 生产者参数
     * @return true：已配置 false：未配置
     */
    Boolean checkReceiverConfigured(Long receiverId, String topic, String producerConfiguration);

    /**
     * 校验给定参数值是否配置正确
     * @param receiverId 接收端对象 id
     * @param topic topic 名
     * @param producerConfiguration 生产者参数
     * @return true：正确 false：错误
     */
    Boolean checkReceiverConfigValid(Long receiverId, String topic, String producerConfiguration);

    /**
     * 校验给定 kafka broker 配置参数是否正确 & 可连通
     * @param brokerConfiguration kafka broker 配置参数
     * @return true：正确 & 可连通 false：无法连通
     * 注：如配置多个 broker，仅存在一个可连通即返回 true
     */
    Boolean checkBrokerConfigurationValid(String brokerConfiguration);

}
