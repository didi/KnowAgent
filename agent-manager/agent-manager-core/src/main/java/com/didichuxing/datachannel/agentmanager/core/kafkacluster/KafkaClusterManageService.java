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
     * 根据kafka集群id获取对应kafka集群对象
     * @param kafkaClusterId kafka集群id
     * @return kafka集群对象
     */
    ReceiverDO getKafkaClusterByKafkaClusterId(Long kafkaClusterId);

    ReceiverDO[] getDefaultReceivers();

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
     * @param id 待删除 id
     * @param ignoreLogCollectTaskAndAgentRelationCheck 是否忽略待删除kafkaCluster存在关联的LogCollectTask & Agent
     * @param operator 操作人
     */
    void deleteKafkaClusterById(Long id, boolean ignoreLogCollectTaskAndAgentRelationCheck, String operator);

    /**
     * 注：由于KafkaCluster同步操作可能会涉及大量KafkaCluster对象持久化，将持续较长时间，时长不可控，可能导致事务超时 回滚，因而，该函
     *     数涉及到的KafkaCluster新增、删除、修改并不采用全局事务，而采用局部事务（即：一个KafkaCluster持久化操作一个事务），如期间遇到
     *     某KafkaCluster对象持久化失败，将以 ERROR 日志进行记录
     */
    void pullKafkaClusterListFromRemoteAndMergeKafkaClusterInLocal();

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
     * 根据接收端对象id获取该接收端对象对应的kafka集群的所有topic列表
     * @param receiverId 接收端对象 id
     * @return 返回根据接收端对象id获取到的该接收端对象对应的kafka集群的所有topic列表
     */
    List<String> listTopics(Long receiverId);

    /**
     * 校验给定接收端对应topic是否存在限流
     * @param kafkaClusterId KafkaCluster对象id值
     * @param topic topic 名
     * @return true：存在限流 false：不存在限流
     */
    boolean checkTopicLimitExists(Long kafkaClusterId, String topic);

    /**
     * 检查给定 KafkaCluster & topic 连通性
     * @param kafkaClusterId KafkaCluster 对象 id 值
     * @param topic topic 名
     * @return true：可连通 false：无法连通
     */
    boolean checkConnectivity(Long kafkaClusterId, String topic);

    /**
     *
     * @return 返回系统全局 agent error logs 流对应接收端
     */
    ReceiverDO getAgentErrorLogsTopicExistsReceiver();

    /**
     * @return 返回系统全局 agent metrics 流对应接收端
     */
    ReceiverDO getAgentMetricsTopicExistsReceiver();

}
