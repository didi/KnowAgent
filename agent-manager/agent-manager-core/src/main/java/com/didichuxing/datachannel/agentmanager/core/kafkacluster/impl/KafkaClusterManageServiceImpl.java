package com.didichuxing.datachannel.agentmanager.core.kafkacluster.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.common.ListCompareResult;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.receiver.KafkaClusterPO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.ModuleEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.OperationEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.*;
import com.didichuxing.datachannel.agentmanager.common.util.Comparator;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.common.OperateRecordService;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.KafkaClusterMapper;
import com.didichuxing.datachannel.agentmanager.remote.kafkacluster.RemoteKafkaClusterService;
import com.didichuxing.datachannel.agentmanager.thirdpart.kafkacluster.extension.KafkaClusterManageServiceExtension;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author huqidong
 * @date 2020-09-21
 * kafka 集群信息管理服务实现
 */
@org.springframework.stereotype.Service
public class KafkaClusterManageServiceImpl implements KafkaClusterManageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaClusterManageServiceImpl.class);

    @Autowired
    private KafkaClusterMapper kafkaClusterDAO;

    @Autowired
    private KafkaClusterManageServiceExtension kafkaClusterManageServiceExtension;

    @Autowired
    private OperateRecordService operateRecordService;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private AgentManageService agentManageService;

    @Override
    public List<ReceiverDO> list() {
        List<KafkaClusterPO> kafkaClusterPOList = kafkaClusterDAO.list();
        if (CollectionUtils.isEmpty(kafkaClusterPOList)) {
            return new ArrayList<>();
        } else {
            return kafkaClusterManageServiceExtension.kafkaClusterPOList2ReceiverDOList(kafkaClusterPOList);
        }
    }

    @Override
    @Transactional
    public Long createKafkaCluster(ReceiverDO kafkaCluster, String operator) {
        return handleCreateKafkaCluster(kafkaCluster, operator);
    }

    /**
     * 创建 KafkaClusterPO 对象流程
     *
     * @param kafkaClusterDO 待创建 KafkaClusterPO 对象
     * @param operator       操作人
     * @return 批量创建是否成功 true：成功 false：不成功
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long handleCreateKafkaCluster(ReceiverDO kafkaClusterDO, String operator) throws ServiceException {
        /*
         * 参数 校验
         */
        CheckResult checkResult = kafkaClusterManageServiceExtension.checkCreateParameterKafkaCluster(kafkaClusterDO);
        if (!checkResult.getCheckResult()) {
            throw new ServiceException(checkResult.getMessage(), checkResult.getCode());
        }
        /*
         * 校验 kafkaClusterName & kafkaClusterBrokerConfiguration 在系统中是否已存在
         */
        KafkaClusterPO kafkaClusterPO = kafkaClusterDAO.selectByKafkaClusterName(kafkaClusterDO.getKafkaClusterName());
        if (null != kafkaClusterPO) {
            throw new ServiceException(
                    String.format("系统中已存在kafkaClusterName={%s}的KafkaCluster对象", kafkaClusterDO.getKafkaClusterName()),
                    ErrorCodeEnum.KAFKA_CLUSTER_NAME_DUPLICATE.getCode()
            );
        }

        /*
         * agent error logs 流对应 topic 不为空，须校验系统中是否已存在该 topic 配置有值
         */
        if(StringUtils.isNotBlank(kafkaClusterDO.getAgentErrorLogsTopic())) {
            KafkaClusterPO agentErrorLogsTopicExistsKafkaCluster = kafkaClusterDAO.getAgentErrorLogsTopicExistsKafkaCluster();
            if(null != agentErrorLogsTopicExistsKafkaCluster) {
                throw new ServiceException(
                        String.format("系统已存在配置 agent errorlogs 流对应 topic 的 kafkacluster={%s}", JSON.toJSONString(agentErrorLogsTopicExistsKafkaCluster)),
                        ErrorCodeEnum.KAFKA_CLUSTER_CREATE_OR_UPDATE_FAILED_CAUSE_BY_AGENT_ERROR_LOGS_TOPIC_EXISTS.getCode()
                );
            }
        }
        /*
         * agent metrics 流对应 topic 不为空，须校验系统中是否已存在该 topic 配置有值
         */
        if(StringUtils.isNotBlank(kafkaClusterDO.getAgentMetricsTopic())) {
            KafkaClusterPO agentMetricsTopicExistsKafkaCluster = kafkaClusterDAO.getAgentMetricsTopicExistsKafkaCluster();
            if(null != agentMetricsTopicExistsKafkaCluster) {
                throw new ServiceException(
                        String.format("系统已存在配置 agent metrics 流对应 topic 的 kafkacluster={%s}", JSON.toJSONString(agentMetricsTopicExistsKafkaCluster)),
                        ErrorCodeEnum.KAFKA_CLUSTER_CREATE_OR_UPDATE_FAILED_CAUSE_BY_AGENT_METRICS_TOPIC_EXISTS.getCode()
                );
            }
        }

//        kafkaClusterPO = kafkaClusterDAO.selectByKafkaClusterBrokerConfiguration(kafkaClusterDO.getKafkaClusterBrokerConfiguration());
//        if(null != kafkaClusterPO) {
//            throw new ServiceException(
//                    String.format("系统中已存在kafkaClusterBrokerConfiguration={%s}的KafkaCluster对象", kafkaClusterDO.getKafkaClusterBrokerConfiguration()),
//                    ErrorCodeEnum.KAFKA_CLUSTER_BROKER_CONFIGURATION_DUPLICATE.getCode()
//            );
//        }

        /*
         * 持久化 kafkaCluster
         */
        kafkaClusterPO = kafkaClusterManageServiceExtension.kafkaCluster2KafkaClusterPO(kafkaClusterDO);
        kafkaClusterPO.setOperator(CommonConstant.getOperator(operator));
        kafkaClusterDAO.insert(kafkaClusterPO);

        /*
         * 校验：如待构建 agent error logs 或 metrics 流对应全局接收端对象，获取全部 agent 对象 & 更新对应 topic & producer configuration 信息
         */
        kafkaClusterDO.setId(kafkaClusterPO.getId());
        checkAndUpdateAllAgentMetricsAndErrorLogsReceiver(kafkaClusterDO);

        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.RECEIVER,
                OperationEnum.ADD,
                kafkaClusterPO.getId(),
                String.format("创建KafkaCluster={%s}，创建成功的KafkaCluster对象id={%d}", JSON.toJSONString(kafkaClusterDO), kafkaClusterPO.getId()),
                operator
        );
        return kafkaClusterPO.getId();
    }

    @Override
    @Transactional
    public void updateKafkaCluster(ReceiverDO kafkaCluster, String operator) {
        this.handleUpdateKafkaCluster(kafkaCluster, operator);
    }

    /**
     * 修改给定 KafkaClusterPO 对象
     *
     * @param kafkaClusterDO 待修改 KafkaClusterPO 对象
     * @param operator       操作人
     * @return 修改是否成功 true：成功 false：不成功
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleUpdateKafkaCluster(ReceiverDO kafkaClusterDO, String operator) throws ServiceException {

        /*
         * 参数 校验
         */
        CheckResult checkResult = kafkaClusterManageServiceExtension.checkModifyParameterKafkaCluster(kafkaClusterDO);
        if (!checkResult.getCheckResult()) {
            throw new ServiceException(checkResult.getMessage(), checkResult.getCode());
        }
        /*
         * 校验待更新KafkaCluster对象在系统是否已存在
         */
        ReceiverDO sourceReceiverDO = getById(kafkaClusterDO.getId());
        if (null == sourceReceiverDO) {
            throw new ServiceException(
                    String.format("待更新KafkaCluster对象={id=%d}在系统中不存在", kafkaClusterDO.getId()),
                    ErrorCodeEnum.KAFKA_CLUSTER_NOT_EXISTS.getCode()
            );
        }
        /*
         * 校验待更新 KafkaCluster 对象对应 kafkaClusterName & kafkaClusterBrokerConfiguration 在系统中是否已存在
         */
        if (!sourceReceiverDO.getKafkaClusterName().equals(kafkaClusterDO.getKafkaClusterName())) {
            KafkaClusterPO kafkaClusterPO = kafkaClusterDAO.selectByKafkaClusterName(kafkaClusterDO.getKafkaClusterName());
            if (null != kafkaClusterPO) {
                throw new ServiceException(
                        String.format("系统中已存在kafkaClusterName={%s}的KafkaCluster对象", kafkaClusterDO.getKafkaClusterName()),
                        ErrorCodeEnum.KAFKA_CLUSTER_NAME_DUPLICATE.getCode()
                );
            }
        }

        /*
         * agent error logs 流对应 topic 不为空，须校验系统中是否已存在该 topic 配置有值
         */
        if(StringUtils.isNotBlank(kafkaClusterDO.getAgentErrorLogsTopic())) {
            KafkaClusterPO agentErrorLogsTopicExistsKafkaCluster = kafkaClusterDAO.getAgentErrorLogsTopicExistsKafkaCluster();
            if(null != agentErrorLogsTopicExistsKafkaCluster && !agentErrorLogsTopicExistsKafkaCluster.getId().equals(kafkaClusterDO.getId())) {
                throw new ServiceException(
                        String.format("系统已存在配置 agent errorlogs 流对应 topic 的 kafkacluster={%s}", JSON.toJSONString(agentErrorLogsTopicExistsKafkaCluster)),
                        ErrorCodeEnum.KAFKA_CLUSTER_CREATE_OR_UPDATE_FAILED_CAUSE_BY_AGENT_ERROR_LOGS_TOPIC_EXISTS.getCode()
                );
            }
        }
        /*
         * agent metrics 流对应 topic 不为空，须校验系统中是否已存在该 topic 配置有值
         */
        if(StringUtils.isNotBlank(kafkaClusterDO.getAgentMetricsTopic())) {
            KafkaClusterPO agentMetricsTopicExistsKafkaCluster = kafkaClusterDAO.getAgentMetricsTopicExistsKafkaCluster();
            if(null != agentMetricsTopicExistsKafkaCluster && !agentMetricsTopicExistsKafkaCluster.getId().equals(kafkaClusterDO.getId())) {
                throw new ServiceException(
                        String.format("系统已存在配置 agent metrics 流对应 topic 的 kafkacluster={%s}", JSON.toJSONString(agentMetricsTopicExistsKafkaCluster)),
                        ErrorCodeEnum.KAFKA_CLUSTER_CREATE_OR_UPDATE_FAILED_CAUSE_BY_AGENT_METRICS_TOPIC_EXISTS.getCode()
                );
            }
        }

//        if(!sourceReceiverDO.getKafkaClusterBrokerConfiguration().equals(kafkaClusterDO.getKafkaClusterBrokerConfiguration())) {
//            KafkaClusterPO kafkaClusterPO = kafkaClusterDAO.selectByKafkaClusterBrokerConfiguration(kafkaClusterDO.getKafkaClusterBrokerConfiguration());
//            if(null != kafkaClusterPO) {
//                throw new ServiceException(
//                        String.format("系统中已存在kafkaClusterBrokerConfiguration={%s}的KafkaCluster对象", kafkaClusterDO.getKafkaClusterBrokerConfiguration()),
//                        ErrorCodeEnum.KAFKA_CLUSTER_BROKER_CONFIGURATION_DUPLICATE.getCode()
//                );
//            }
//        }

        /*
         * 校验：如修改的是agent error logs 或 metrics 流对应全局接收端对象，获取全部 agent 对象 & 更新对应 topic & producer configuration 信息
         */
        checkAndUpdateAllAgentMetricsAndErrorLogsReceiver(kafkaClusterDO);

        /*
         * 更新KafkaCluster对象至db
         */
        ReceiverDO persistReceiver = kafkaClusterManageServiceExtension.updateKafkaCluster(sourceReceiverDO, kafkaClusterDO);
        KafkaClusterPO kafkaClusterPO = kafkaClusterManageServiceExtension.kafkaCluster2KafkaClusterPO(persistReceiver);
        kafkaClusterPO.setOperator(CommonConstant.getOperator(operator));
        kafkaClusterPO.setModifyTime(new Date());
        kafkaClusterDAO.updateByPrimaryKey(kafkaClusterPO);

        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.RECEIVER,
                OperationEnum.EDIT,
                kafkaClusterDO.getId(),
                String.format("修改KafkaCluster={%s}，修改成功的KafkaCluster对象id={%d}", JSON.toJSONString(kafkaClusterDO), kafkaClusterDO.getId()),
                operator
        );
    }

    private void checkAndUpdateAllAgentMetricsAndErrorLogsReceiver(ReceiverDO kafkaClusterDO) {
        boolean errorLogsReceiverChanged = false;
        boolean metricsReceiverChanged = false;
        if(StringUtils.isNotBlank(kafkaClusterDO.getAgentErrorLogsTopic())) {
            errorLogsReceiverChanged = true;
        }
        if(StringUtils.isNotBlank(kafkaClusterDO.getAgentMetricsTopic())) {
            metricsReceiverChanged = true;
        }
        if(errorLogsReceiverChanged || metricsReceiverChanged) {
            List<AgentDO> agentDOList = agentManageService.list();
            for (AgentDO agentDO : agentDOList) {
                if(errorLogsReceiverChanged) {
                    agentDO.setErrorLogsSendReceiverId(kafkaClusterDO.getId());
                    agentDO.setErrorLogsSendTopic(kafkaClusterDO.getAgentErrorLogsTopic());
                    agentDO.setErrorLogsProducerConfiguration(kafkaClusterDO.getKafkaClusterProducerInitConfiguration());
                }
                if(metricsReceiverChanged) {
                    agentDO.setMetricsSendReceiverId(kafkaClusterDO.getId());
                    agentDO.setMetricsSendTopic(kafkaClusterDO.getAgentMetricsTopic());
                    agentDO.setMetricsProducerConfiguration(kafkaClusterDO.getKafkaClusterProducerInitConfiguration());
                }
                agentManageService.updateAgent(agentDO, null);
            }
        }
    }

    @Override
    @Transactional
    public void deleteKafkaClusterById(List<Long> receiverIdList, boolean ignoreLogCollectTaskAndAgentRelationCheck, String operator) {
        for (Long receiverId : receiverIdList) {
            this.handleRemoveKafkaClusterById(receiverId, ignoreLogCollectTaskAndAgentRelationCheck, operator);
        }
    }

    /**
     * 根据 id 删除对应 KafkaClusterPO 对象
     *
     * @param id                                        待删除 id
     * @param ignoreLogCollectTaskAndAgentRelationCheck 是否忽略待删除kafkaCluster存在关联的LogCollectTask & Agent
     * @param operator                                  操作人
     * @throws ServiceException 执行该函数过程中出现的异常
     *
     * todo 采集任务是否需要级联删除
     */
    private void handleRemoveKafkaClusterById(Long id, boolean ignoreLogCollectTaskAndAgentRelationCheck, String operator) throws ServiceException {
        if (null == id) {
            throw new ServiceException(
                    "删除失败：待删除接收端id不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        KafkaClusterPO kafkaClusterPO = kafkaClusterDAO.selectByPrimaryKey(id);
        if (null == kafkaClusterPO) {
            throw new ServiceException(
                    "删除失败：待删除接收端在系统中不存在",
                    ErrorCodeEnum.KAFKA_CLUSTER_NOT_EXISTS.getCode()
            );
        }
        if (!ignoreLogCollectTaskAndAgentRelationCheck) {
            /*
             * 校验待删除kafkaCluster在系统中是否被 logcollecttask & agent 所引用，如存在引用，则不允许删除
             */
            List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageService.getLogCollectTaskListByKafkaClusterId(id);
            if(CollectionUtils.isNotEmpty(logCollectTaskDOList)) {
                throw new ServiceException(
                        String.format("删除失败：待删除接收端存在%d个关联的采集任务", logCollectTaskDOList.size()),
                        ErrorCodeEnum.KAFKA_CLUSTER_DELETE_FAILED_CAUSE_BY_RELA_LOGCOLLECTTASK_EXISTS.getCode()
                );
            }
            List<AgentDO> agentDOList = agentManageService.getAgentListByKafkaClusterId(id);
            if(CollectionUtils.isNotEmpty(agentDOList)) {
                throw new ServiceException(
                        String.format("删除失败：待删除接收端存在%d个关联的Agent", agentDOList.size()),
                        ErrorCodeEnum.KAFKA_CLUSTER_DELETE_FAILED_CAUSE_BY_RELA_AGENT_EXISTS.getCode()
                );
            }
        }
        /*
         * 删除对应kafkacluster记录
         */
        kafkaClusterDAO.deleteByPrimaryKey(kafkaClusterPO.getId());
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.RECEIVER,
                OperationEnum.DELETE,
                id,
                String.format("删除KafkaCluster对象={id={%d}}", id),
                operator
        );
    }

    @Override
    public ReceiverDO getById(Long receiverId) {
        if (receiverId == null || receiverId <= 0) {
            return null;
        }
        KafkaClusterPO kafkaClusterPO = kafkaClusterDAO.selectByPrimaryKey(receiverId);
        if (null != kafkaClusterPO) {
            return kafkaClusterManageServiceExtension.kafkaClusterPO2KafkaCluster(kafkaClusterPO);
        } else {
            return null;
        }
    }

    @Override
    public List<ReceiverDO> paginationQueryByCondition(ReceiverPaginationQueryConditionDO query) {
        String column = query.getSortColumn();
        if (column != null) {
            for (char c : column.toCharArray()) {
                if (!Character.isLetter(c) && c != '_') {
                    return Collections.emptyList();
                }
            }
        }
        List<KafkaClusterPO> kafkaClusterPOList = kafkaClusterDAO.paginationQueryByConditon(query);
        return kafkaClusterManageServiceExtension.kafkaClusterPOList2ReceiverDOList(kafkaClusterPOList);
    }

    @Override
    public Integer queryCountByCondition(ReceiverPaginationQueryConditionDO receiverPaginationQueryConditionDO) {
        return kafkaClusterDAO.queryCountByConditon(receiverPaginationQueryConditionDO);
    }

    @Override
    public ReceiverDO getAgentErrorLogsTopicExistsReceiver() {
        KafkaClusterPO kafkaClusterPO = kafkaClusterDAO.getAgentErrorLogsTopicExistsKafkaCluster();
        if(null != kafkaClusterPO) {
            return kafkaClusterManageServiceExtension.kafkaClusterPO2KafkaCluster(kafkaClusterPO);
        } else {
            return null;
        }
    }

    @Override
    public ReceiverDO getAgentMetricsTopicExistsReceiver() {
        KafkaClusterPO kafkaClusterPO = kafkaClusterDAO.getAgentMetricsTopicExistsKafkaCluster();
        if(null != kafkaClusterPO) {
            return kafkaClusterManageServiceExtension.kafkaClusterPO2KafkaCluster(kafkaClusterPO);
        } else {
            return null;
        }
    }

    @Override
    public Boolean checkReceiverConfigured(Long receiverId, String topic, String producerConfiguration) {
        if(
                null != receiverId &&
                        0l != receiverId &&
                        StringUtils.isNotBlank(topic) &&
                        StringUtils.isNotBlank(producerConfiguration)
        ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean checkReceiverConfigValid(Long receiverId, String topic, String producerConfiguration) {
        ReceiverDO receiverDO = getById(receiverId);
        if(null == receiverDO) {
            return false;
        }
        String brokerConfiguration = receiverDO.getKafkaClusterBrokerConfiguration();
        /*
         * 校验 metricsSendTopic
         */
        if(!checkMetricsSendTopicValid(topic)) {
            return false;
        }
        /*
         * 校验 metricsProducerConfiguration
         */
        if(!checkMetricsProducerConfigurationValid(producerConfiguration)) {
            return false;
        }
        /*
         * 通过构建 kafka producer，校验其配置是否 ok
         */
        return kafkaClusterManageServiceExtension.checkProducerConfigurationValid(
                brokerConfiguration,
                topic,
                producerConfiguration
        );
    }

    private boolean checkMetricsProducerConfigurationValid(String metricsProducerConfiguration) {
        String[] configItemArray = metricsProducerConfiguration.split(CommonConstant.COMMA);
        if(ArrayUtils.isEmpty(configItemArray)) {
            return false;
        }
        for (String configItem : configItemArray) {
            String[] item = configItem.split(CommonConstant.EQUAL_SIGN);
            if(ArrayUtils.isEmpty(item) || item.length != 2) {
                return false;
            }
        }
        return true;
    }

    private boolean checkMetricsSendTopicValid(String topic) {
        if(StringUtils.isNotBlank(topic)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean checkBrokerConfigurationValid(String brokerConfiguration) {
        if(StringUtils.isBlank(brokerConfiguration)) {
            return false;
        }
        String[] brokerServerIpPortArray = brokerConfiguration.split(CommonConstant.COMMA);
        if(ArrayUtils.isEmpty(brokerServerIpPortArray)) {
            return false;
        }
        for (String brokerServerIpPort : brokerServerIpPortArray) {
            if(!brokerServerIpPort.contains(CommonConstant.COLON)) {
                continue;
            }
            String[] ipPort = brokerServerIpPort.split(CommonConstant.COLON);
            if(ipPort.length != 2) {
                continue;
            }
            String ip = ipPort[0];
            String port = ipPort[1];
            if(NetworkUtil.telnet(ip, Integer.valueOf(port))) {
                return true;
            }
        }
        return false;
    }

    class ReceiverDOComparator implements Comparator<ReceiverDO, Long> {
        @Override
        public Long getKey(ReceiverDO kafkaCluster) {
            return kafkaCluster.getKafkaClusterId();
        }

        @Override
        public boolean compare(ReceiverDO t1, ReceiverDO t2) {
            return t1.getKafkaClusterName().equals(t2.getKafkaClusterName()) &&
                    t1.getKafkaClusterBrokerConfiguration().equals(t2.getKafkaClusterBrokerConfiguration());
        }

        @Override
        public ReceiverDO getModified(ReceiverDO source, ReceiverDO target) {
            source.setKafkaClusterName(target.getKafkaClusterName());
            source.setKafkaClusterBrokerConfiguration(target.getKafkaClusterBrokerConfiguration());
            return source;
        }
    }

}
