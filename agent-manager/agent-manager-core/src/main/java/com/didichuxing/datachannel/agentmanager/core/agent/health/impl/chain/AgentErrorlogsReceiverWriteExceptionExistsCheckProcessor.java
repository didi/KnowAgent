package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.chain.ProcessorChain;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.NetworkUtil;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * agent的errorlogs流对应的下游接收端是否存在写入失败情况
 * @author Ronaldo
 * @Date 2021/11/1
 */
@HealthCheckProcessorAnnotation(seq = 3, type = HealthCheckProcessorEnum.AGENT)
public class AgentErrorlogsReceiverWriteExceptionExistsCheckProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentHeartBeatCheckProcessor.class);

    @Override
    public void process(Context context, ProcessorChain chain) {
        AgentHealthCheckContext agentHealthCheckContext = (AgentHealthCheckContext) context;
        AgentDO agentDO = agentHealthCheckContext.getAgentDO();
        AgentMetricsManageService agentMetricsManageService = agentHealthCheckContext.getAgentMetricsManageService();
        KafkaClusterManageService kafkaClusterManageService = agentHealthCheckContext.getKafkaClusterManageService();

        // 判断agent的errorlogs流对应的下游接收端是否存在写入失败情况，继续执行链，否则结束

        boolean errorlogsReceiverWriteExceptionExists = checkErrorlogsReceiverWriteExceptionExists();
        // 如果不存在写入失败情况
        if (!errorlogsReceiverWriteExceptionExists) {
            chain.process(context, chain);
            return;
        }

        // 如果存在写入失败情况，继续查找原因
        /*
         * agent是否已配置errorlogs流对应下游接收端信息
         */
        boolean configurationExists = checkAgentMetricsConfigurationExists(agentDO.getErrorLogsSendTopic(), agentDO.getErrorLogsSendReceiverId());
        // 如果接收端不存在
        if (!configurationExists) {
            setAgentHealthCheckResult(agentDO, AgentHealthInspectionResultEnum.AGENT_ERRORLOGS_CONFIGURATION_NOT_EXISTS, agentHealthCheckContext);
            return;
        }

        /*
         * agent的errorlogs流对应下游接收端连通性是否正常
         */
        boolean agentErrorLogsReceiverConnectivities = checkAgentMetricsReceiverConnectivities(agentDO.getErrorLogsSendReceiverId(), kafkaClusterManageService);
        // 如果连通不正常
        if (!agentErrorLogsReceiverConnectivities) {
            setAgentHealthCheckResult(agentDO, AgentHealthInspectionResultEnum.AGENT_ERRORLOGS_RECEIVER_NOT_CONNECTED, agentHealthCheckContext);
            return;
        }

        // agent进程故障
        setAgentHealthCheckResult(agentDO, AgentHealthInspectionResultEnum.AGENT_PROCESS_BROKES_DOWN, agentHealthCheckContext);
    }

    /**
     * TODO: 判断agent的errorlogs流对应的下游接收端是否存在写入失败情况
     * @return
     */
    private boolean checkErrorlogsReceiverWriteExceptionExists() {
        return true;
    }

    /**
     * agent是否已配置metrics流对应下游接收端信息
     * TODO 校验内容可扩展
     * @param errorLogsSendTopic 接收端topic
     * @param errorLogsSendReceiverId 接收端Id
     * @return true: 存活 false: 不存活
     */
    private boolean checkAgentMetricsConfigurationExists(String errorLogsSendTopic, Long errorLogsSendReceiverId) {
        return (errorLogsSendReceiverId != null) && StringUtils.isNotBlank(errorLogsSendTopic);
    }

    /**
     * agent的metrics流对应下游接收端连通性是否正常
     * @param errorLogsSendReceiverId 接收端Id
     * @param kafkaClusterManageService kafka 集群信息管理服务
     * @return
     * @throws ServiceException
     */
    private boolean checkAgentMetricsReceiverConnectivities(Long errorLogsSendReceiverId, KafkaClusterManageService kafkaClusterManageService) throws ServiceException{
        ReceiverDO receiverDO = kafkaClusterManageService.getById(errorLogsSendReceiverId);
        if (null == receiverDO) {
            throw new ServiceException(
                    String.format("系统中不存在id={%d}的Receiver对象", errorLogsSendReceiverId),
                    ErrorCodeEnum.KAFKA_CLUSTER_NOT_EXISTS.getCode()
            );
        }
        String kafkaClusterBrokerConfiguration = receiverDO.getKafkaClusterBrokerConfiguration();
        String[] array = kafkaClusterBrokerConfiguration.split(":");
        if(array.length < 2) {
            LOGGER.error(String.format("class=AgentHeartBeatCheckProcessor||method=checkAgentMetricsReceiverConnectivities||errMsg={%s}",
                    "对象KafkaCluster的配置错误"));
            return false;
        }
        Integer port = Integer.parseInt(array[1]);
        return NetworkUtil.connect(array[0], port);
    }

    /**
     * 设置agent健康度检查结果
     * @param agentDO agent对象
     * @param agentHealthInspectionResultEnum 日志采集任务健康度巡检结果枚举
     */
    private void setAgentHealthCheckResult(AgentDO agentDO, AgentHealthInspectionResultEnum agentHealthInspectionResultEnum, AgentHealthCheckContext agentHealthCheckContext) {
        AgentHealthLevelEnum agentHealthLevelEnum = agentHealthInspectionResultEnum.getAgentHealthLevel();
        String agentHealthDescription = String.format(
                "%s:AgentId={%d}, HostName={%s}",
                agentHealthInspectionResultEnum.getDescription(),
                agentDO.getId(),
                agentDO.getHostName()
        );
        agentHealthCheckContext.setAgentHealthLevelEnum(agentHealthLevelEnum);
        agentHealthCheckContext.setAgentHealthDescription(agentHealthDescription);
    }
}
