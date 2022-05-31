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
    private RemoteKafkaClusterService remoteKafkaClusterService;

    @Autowired
    private KafkaClusterManageServiceExtension kafkaClusterManageServiceExtension;

    @Autowired
    private OperateRecordService operateRecordService;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private AgentManageService agentManageService;

    private ReceiverDOComparator comparator = new ReceiverDOComparator();

    @Override
    public ReceiverDO getKafkaClusterByKafkaClusterId(Long kafkaClusterId) {
        KafkaClusterPO kafkaClusterPO = kafkaClusterDAO.selectByKafkaClusterId(kafkaClusterId);
        if (null != kafkaClusterPO) {
            return kafkaClusterManageServiceExtension.kafkaClusterPO2KafkaCluster(kafkaClusterPO);
        } else {
            return null;
        }
    }

    @Override
    public ReceiverDO[] getDefaultReceivers() {
        List<KafkaClusterPO> kafkaClusterPOList = kafkaClusterDAO.list();
        if (CollectionUtils.isEmpty(kafkaClusterPOList)) {
            return null;
        }
        ReceiverDO metricReceiver = null;
        ReceiverDO errorLogReceiver = null;
        for (KafkaClusterPO kafkaClusterPO : kafkaClusterPOList) {
            if (!StringUtils.isBlank(kafkaClusterPO.getAgentMetricsTopic())) {
                metricReceiver = ConvertUtil.obj2Obj(kafkaClusterPO, ReceiverDO.class);
            }
            if (!StringUtils.isBlank(kafkaClusterPO.getAgentErrorLogsTopic())) {
                errorLogReceiver = ConvertUtil.obj2Obj(kafkaClusterPO, ReceiverDO.class);
            }
        }
        return new ReceiverDO[]{metricReceiver, errorLogReceiver};
    }

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
                    "入参id不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        KafkaClusterPO kafkaClusterPO = kafkaClusterDAO.selectByPrimaryKey(id);
        if (null == kafkaClusterPO) {
            throw new ServiceException(
                    String.format("根据id={%d}删除KafkaCluster对象失败，原因为：系统中不存在id为{%d}的KafkaCluster对象", id, id),
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
                        String.format("待删除KafkaCluster={id=%d}存在{%d}个关联的LogCollectTask，请先解除关联", id, logCollectTaskDOList.size()),
                        ErrorCodeEnum.KAFKA_CLUSTER_DELETE_FAILED_CAUSE_BY_RELA_LOGCOLLECTTASK_EXISTS.getCode()
                );
            }
            List<AgentDO> agentDOList = agentManageService.getAgentListByKafkaClusterId(id);
            if(CollectionUtils.isNotEmpty(agentDOList)) {
                throw new ServiceException(
                        String.format("待删除KafkaCluster={id=%d}存在{%d}个关联的Agent，请先解除关联", id, agentDOList.size()),
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
    public void pullKafkaClusterListFromRemoteAndMergeKafkaClusterInLocal() {
        long startTime = System.currentTimeMillis();//use to lo
        /*
         * 获取远程对象集
         */
        List<ReceiverDO> remoteList = remoteKafkaClusterService.getKafkaClustersFromRemote();
        long getRemoteListTime = System.currentTimeMillis() - startTime;//获取远程信息集耗时
        /*
         * 获取本地对象集
         */
        long getLocalListStartTime = System.currentTimeMillis();
        List<ReceiverDO> localList = list();
        long getLocalListTime = System.currentTimeMillis() - getLocalListStartTime;//获取本地主机信息集耗时
        /*
         * 与本地服务节点集进行对比，得到待新增、待删除、待更新服务节点列表
         */
        long compareStartTime = System.currentTimeMillis();
        ListCompareResult<ReceiverDO> listCompareResult = ListCompareUtil.compare(localList, remoteList, comparator);
        long compareTime = System.currentTimeMillis() - compareStartTime;
        /*
         * 针对上一步得到的待新增、待删除、待更新服务节点列表，进行新增、删除、更新操作
         */
        long persistStartTime = System.currentTimeMillis();
        int createSuccessCount = 0, removeScucessCount = 0, modifiedSuccessCount = 0;//创建成功数、删除成功数、更新成功数
        //处理待创建对象集
        List<ReceiverDO> createList = listCompareResult.getCreateList();
        for (ReceiverDO receiverDO : createList) {
            Long savedId = this.createKafkaCluster(receiverDO, null);
            if (savedId > 0) {
                createSuccessCount++;
            } else {
                LOGGER.error(
                        String.format("class=KafkaClusterManageServiceImpl||method=pullKafkaClusterListFromRemoteAndMergeKafkaClusterInLocal||errMsg={%s}",
                                String.format("创建对象KafkaCluster={%s}失败", JSON.toJSONString(receiverDO)))
                );
            }
        }
        //处理待修改对象集
        List<ReceiverDO> modifyList = listCompareResult.getModifyList();
        for (ReceiverDO receiverDO : modifyList) {
            this.updateKafkaCluster(receiverDO, null);
            modifiedSuccessCount++;
        }
        //处理待删除对象集
        List<ReceiverDO> removeList = listCompareResult.getRemoveList();
        for (ReceiverDO receiverDO : removeList) {
            this.deleteKafkaClusterById(Arrays.asList(receiverDO.getId()), true, null);
            removeScucessCount++;
        }
        long persistTime = System.currentTimeMillis() - persistStartTime;
        /*
         * 记录日志
         */
        String logInfo = String.format(
                "class=KafkaClusterManageServiceImpl||method=pullKafkaClusterListFromRemoteAndMergeKafkaClusterInLocal||remoteListSize={%d}||localListSize={%d}||" +
                        "total-cost-time={%d}||getRemoteList-cost-time={%d}||getLocalList-cost-time={%d}||compareRemoteListAndLocalList-cost-time={%d}||persistList-cost-time={%d}||" +
                        "计划扩容数={%d}||扩容成功数={%d}||扩容失败数={%d}||计划缩容数={%d}||缩容成功数={%d}||缩容失败数={%d}||计划更新数={%d}||更新成功数={%d}||更新失败数={%d}",
                remoteList.size(),
                localList.size(),
                System.currentTimeMillis() - startTime,
                getRemoteListTime,
                getLocalListTime,
                compareTime,
                persistTime,
                listCompareResult.getCreateList().size(),
                createSuccessCount,
                (listCompareResult.getCreateList().size() - createSuccessCount),
                listCompareResult.getRemoveList().size(),
                removeScucessCount,
                (listCompareResult.getRemoveList().size() - removeScucessCount),
                listCompareResult.getModifyList().size(),
                modifiedSuccessCount,
                (listCompareResult.getModifyList().size() - modifiedSuccessCount)
        );
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(logInfo);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                    String.format(
                            "remoteList={%s}||localHostList={%s}",
                            JSON.toJSONString(remoteList),
                            JSON.toJSONString(localList)
                    )
            );
        }
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
    public List<String> listTopics(Long receiverId) {
        ReceiverDO receiverDO = getById(receiverId);
        if (null == receiverDO) {
            throw new ServiceException(
                    String.format("待获取topic列表的接收端={receiverId=%d}在系统中不存在", receiverDO),
                    ErrorCodeEnum.KAFKA_CLUSTER_NOT_EXISTS.getCode()
            );
        }
        //TODO：根据 kafka-manager 获取对应接收端 topic 集
        return null;
    }

    @Override
    public boolean checkTopicLimitExists(Long kafkaClusterId, String topic) {
        /*
         * 通过kafkaClusterId获取对应kafkaCluster对象
         */
        ReceiverDO receiverDO = getById(kafkaClusterId);
        /*
         * 校验kafkaCluster对象来源是否为kafka-manager端同步，如不是，抛出"暂不支持"异常，如是，调用kafka-manager端对应接口，获取是否存在限流
         * 注：校验kafkaCluster对象来源是否为kafka-manager端同步 校验规则为：kafkaCluster.kafkaClusterId 是否不为空 & > 0
         */
        if (null == receiverDO) {
            throw new ServiceException(
                    String.format("KafkaCluster={id=%d}在系统中不存在", kafkaClusterId),
                    ErrorCodeEnum.KAFKA_CLUSTER_NOT_EXISTS.getCode()
            );
        } else {
            return kafkaClusterManageServiceExtension.checkTopicLimitExists(receiverDO, topic);
        }
    }

    /**
     * 检查给定接收端receiverDO对应各Broker连通性是否可连通
     *
     * @param receiverDO 待检查接收端对象
     * @return true：receiverDO对应各Broker都可连通 false：receiverDO对应各Broker存在一个或多个无法连通
     */
    private boolean checkKafkaBrokerConnectivity(ReceiverDO receiverDO) {
        List<Pair<String, Integer>> brokerIp2PortPairList = kafkaClusterManageServiceExtension.getBrokerIp2PortPairList(receiverDO);
        for (Pair<String, Integer> brokerIp2PortPair : brokerIp2PortPairList) {
            if (!NetworkUtil.connect(brokerIp2PortPair.getKey(), brokerIp2PortPair.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验给定errorLogsSendTopic在接收端receiverDO对应KafkaCluster是否存在
     *
     * @param receiverDO 接收端对象
     * @param topic      名
     * @return true：存在 false：不存在
     */
    private boolean checkTopicExists(ReceiverDO receiverDO, String topic) {
        Long extenalKafkaClusteId = receiverDO.getKafkaClusterId();
        if (null == extenalKafkaClusteId || 0 >= extenalKafkaClusteId) {
            throw new ServiceException(
                    String.format("KafkaCluster={id=%d}非源于KafkaManager，仅支持通过源于KafkaManager同步而来的KafkaCluster", receiverDO.getId()),
                    ErrorCodeEnum.KAFKA_CLUSTER_NOT_ORIGINATED_FROM_KAFKA_MANAGER.getCode()
            );
        }
        Set<String> topicList = remoteKafkaClusterService.getTopicsByKafkaClusterId(extenalKafkaClusteId);
        if (CollectionUtils.isEmpty(topicList)) {
            return false;
        } else {
            return topicList.contains(topic);
        }
    }

    @Override
    public boolean checkConnectivity(Long kafkaClusterId, String topic) {
        ReceiverDO receiverDO = getById(kafkaClusterId);
        if (null == receiverDO) {
            throw new ServiceException(
                    String.format("KafkaCluster={id=%d}在系统中不存在", kafkaClusterId),
                    ErrorCodeEnum.KAFKA_CLUSTER_NOT_EXISTS.getCode()
            );
        }
        /*
         * 根据receiverDO.kafkaClusterBrokerConfiguration校验kafkaClusterBrokerConfiguration配置的各ip/hostname : port连通性是否都ok
         */
        boolean kafkaBrokerConnectivityCheckResult = checkKafkaBrokerConnectivity(receiverDO);
        /*
         * 校验errorLogsSendTopic在errorLogsSendReceiverId对应KafkaCluster是否存在
         */
        boolean topicExists = checkTopicExists(receiverDO, topic);
        return topicExists && kafkaBrokerConnectivityCheckResult;
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
