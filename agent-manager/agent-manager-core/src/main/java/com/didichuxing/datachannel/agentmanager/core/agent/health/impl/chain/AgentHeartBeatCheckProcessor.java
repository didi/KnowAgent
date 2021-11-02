package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.chain.ProcessorChain;
import com.didichuxing.datachannel.agentmanager.common.constant.AgentHealthCheckConstant;
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
 * Agent心跳检查
 * @author Ronaldo
 * @date 2021-10-31
 */
@HealthCheckProcessorAnnotation(seq = 1, type = HealthCheckProcessorEnum.AGENT)
public class AgentHeartBeatCheckProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentHeartBeatCheckProcessor.class);

    @Override
    public void process(Context context, ProcessorChain chain) {
        AgentHealthCheckContext agentHealthCheckContext = (AgentHealthCheckContext) context;
        AgentDO agentDO = agentHealthCheckContext.getAgentDO();
        AgentMetricsManageService agentMetricsManageService = agentHealthCheckContext.getAgentMetricsManageService();
        KafkaClusterManageService kafkaClusterManageService = agentHealthCheckContext.getKafkaClusterManageService();

        /*
         * 校验在距当前时间的心跳存活判定周期内，agent 是否存在心跳
         */
        boolean alive = checkAliveByHeartbeat(agentDO.getHostName(), agentMetricsManageService);
        // 如果存在心跳
        if(alive) {
            chain.process(context, chain);
            return;
        }
        // 如果不存活，进一步查找原因

        /*
         * agent宿主机是否存活
         */
        boolean hostIsAlive = checkHostIsAlive(agentDO.getIp());
        // 如果宿主机不存活
        if (!hostIsAlive) {
            setAgentHealthCheckResult(agentDO, AgentHealthInspectionResultEnum.HOST_OF_AGENT_NOT_ALIVE, agentHealthCheckContext);
            return;
        }

        /*
         * agent是否已配置metrics流对应下游接收端信息
         */
        boolean configurationExists = checkAgentMetricsConfigurationExists(agentDO.getMetricsSendTopic(), agentDO.getMetricsSendReceiverId());
        // 如果接收端不存在
        if (!configurationExists) {
            setAgentHealthCheckResult(agentDO, AgentHealthInspectionResultEnum.AGENT_METRICS_CONFIGURATION_NOT_EXISTS, agentHealthCheckContext);
            return;
        }

        /*
         * agent的metrics流对应下游接收端连通性是否正常
         */
        boolean agentMetricsReceiverConnectivities = checkAgentMetricsReceiverConnectivities(agentDO.getMetricsSendReceiverId(), kafkaClusterManageService);
        // 如果连通不正常
        if (!agentMetricsReceiverConnectivities) {
            setAgentHealthCheckResult(agentDO, AgentHealthInspectionResultEnum.AGENT_METRICS_RECEIVER_NOT_CONNECTED, agentHealthCheckContext);
            return;
        }

        // agent进程故障
        setAgentHealthCheckResult(agentDO, AgentHealthInspectionResultEnum.AGENT_PROCESS_BROKES_DOWN, agentHealthCheckContext);
    }

    /**
     * 校验在距当前时间的心跳存活判定周期内，agent 是否存或
     *
     * @param hostName agent 主机名
     * @return true：存活 false：不存活
     */
    private boolean checkAliveByHeartbeat(String hostName, AgentMetricsManageService agentMetricsManageService) {
        /*
         * 获取近 AgentHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD 时间范围内 agent 心跳数，
         * 心跳数量 == 0，表示 agent 不存在心跳
         */
        Long currentTime = System.currentTimeMillis();
        Long heartbeatTimes = agentMetricsManageService.getHeartbeatTimesByTimeFrame(
                currentTime - AgentHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD,
                currentTime,
                hostName
        );
        return heartbeatTimes > 0;
    }

    /**
     * 检查agent所在的主机是否存活
     * @param hostIp agent主机Ip
     * @return true: 存活 false: 不存活
     */
    private boolean checkHostIsAlive(String hostIp) {
        return NetworkUtil.ping(hostIp);
    }

    /**
     * agent是否已配置metrics流对应下游接收端信息
     * TODO 校验内容可扩展
     * @param metricsSendTopic 接收端topic
     * @param metricsSendReceiverId 接收端Id
     * @return true: 存活 false: 不存活
     */
    private boolean checkAgentMetricsConfigurationExists(String metricsSendTopic, Long metricsSendReceiverId) {
        return (metricsSendReceiverId != null) && StringUtils.isNotBlank(metricsSendTopic);
    }

    /**
     * agent的metrics流对应下游接收端连通性是否正常
     * @param metricsSendReceiverId 接收端Id
     * @param kafkaClusterManageService kafka 集群信息管理服务
     * @return
     * @throws ServiceException
     */
    private boolean checkAgentMetricsReceiverConnectivities(Long metricsSendReceiverId, KafkaClusterManageService kafkaClusterManageService) throws ServiceException{
        ReceiverDO receiverDO = kafkaClusterManageService.getById(metricsSendReceiverId);
        if (null == receiverDO) {
            throw new ServiceException(
                    String.format("系统中不存在id={%d}的Receiver对象", metricsSendReceiverId),
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
