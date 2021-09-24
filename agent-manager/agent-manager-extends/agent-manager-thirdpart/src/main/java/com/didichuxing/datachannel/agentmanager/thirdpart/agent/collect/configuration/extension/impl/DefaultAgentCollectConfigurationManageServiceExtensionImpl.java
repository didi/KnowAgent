package com.didichuxing.datachannel.agentmanager.thirdpart.agent.collect.configuration.extension.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.DirectoryLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config.*;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.host.HostTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.YesOrNoEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.collect.configuration.extension.AgentCollectConfigurationManageServiceExtension;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class DefaultAgentCollectConfigurationManageServiceExtensionImpl implements AgentCollectConfigurationManageServiceExtension {

    @Override
    public AgentConfiguration agent2AgentConfiguration(AgentDO agent, ReceiverDO metricsReceiverDO, ReceiverDO errorLogsReceiverDO) throws ServiceException {
        if(null == agent) {
            throw new ServiceException(
                    String.format(
                            "class=AgentCollectConfigurationManageServiceExtensionImpl||method=agent2AgentConfiguration||msg={%s}",
                            "入参agent对象不可为空"
                    ),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        /*
         * agentDo 2 agentConfiguration
         */
        try {
            AgentConfiguration agentConfiguration = new AgentConfiguration();
            agentConfiguration.setAdvancedConfigurationJsonString(agent.getAdvancedConfigurationJsonString());
            agentConfiguration.setAgentConfigurationVersion(agent.getConfigurationVersion());
            agentConfiguration.setAgentId(agent.getId());
            agentConfiguration.setHostName(agent.getHostName());
            agentConfiguration.setIp(agent.getIp());
            agentConfiguration.setCpuLimitThreshold(agent.getCpuLimitThreshold());
            agentConfiguration.setByteLimitThreshold(agent.getByteLimitThreshold());

            ProducerConfiguration metricsProducerConfiguration = new ProducerConfiguration();
            metricsProducerConfiguration.setNameServer(metricsReceiverDO.getKafkaClusterBrokerConfiguration());
            metricsProducerConfiguration.setProperties(metricsReceiverDO.getKafkaClusterProducerInitConfiguration());
            metricsProducerConfiguration.setReceiverId(metricsReceiverDO.getId());
            metricsProducerConfiguration.setTopic(agent.getMetricsSendTopic());

            ProducerConfiguration errorLogsProducerConfiguration = new ProducerConfiguration();
            errorLogsProducerConfiguration.setNameServer(errorLogsReceiverDO.getKafkaClusterBrokerConfiguration());
            errorLogsProducerConfiguration.setProperties(errorLogsReceiverDO.getKafkaClusterProducerInitConfiguration());
            errorLogsProducerConfiguration.setReceiverId(errorLogsReceiverDO.getId());
            errorLogsProducerConfiguration.setTopic(agent.getErrorLogsSendTopic());

            agentConfiguration.setMetricsProducerConfiguration(metricsProducerConfiguration);
            agentConfiguration.setErrorLogsProducerConfiguration(errorLogsProducerConfiguration);
            return agentConfiguration;
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format(
                            "class=AgentCollectConfigurationManageServiceExtensionImpl||method=agent2AgentConfiguration||msg={%s}",
                            String.format("agent对象={%s}转化为AgentConfiguration对象失败，原因为：%s", JSON.toJSONString(agent), ex.getMessage())
                    ),
                    ex,
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
    }

    @Override
    public LogCollectTaskConfiguration logCollectTask2LogCollectTaskConfiguration(LogCollectTaskDO logCollectTask, ReceiverDO logReceiverDO) throws ServiceException {
        if (null == logCollectTask) {
            throw new ServiceException(
                    "入参logCollectTask对象不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        LogCollectTaskConfiguration logCollectTaskConfiguration = new LogCollectTaskConfiguration();
        ProducerConfiguration producerConfiguration = new ProducerConfiguration();
        producerConfiguration.setTopic(logCollectTask.getSendTopic());
        producerConfiguration.setReceiverId(logReceiverDO.getId());
        producerConfiguration.setProperties(logReceiverDO.getKafkaClusterProducerInitConfiguration());
        producerConfiguration.setNameServer(logReceiverDO.getKafkaClusterBrokerConfiguration());
        logCollectTaskConfiguration.setLogProducerConfiguration(producerConfiguration);
        logCollectTaskConfiguration.setAdvancedConfigurationJsonString(logCollectTask.getAdvancedConfigurationJsonString());
        logCollectTaskConfiguration.setCollectEndTimeBusiness(logCollectTask.getCollectEndTimeBusiness());
        logCollectTaskConfiguration.setCollectStartTimeBusiness(logCollectTask.getCollectStartTimeBusiness());
        logCollectTaskConfiguration.setConfigurationVersion(logCollectTask.getConfigurationVersion());
        logCollectTaskConfiguration.setLimitPriority(logCollectTask.getLimitPriority());
        logCollectTaskConfiguration.setLogCollectTaskId(logCollectTask.getId());
        logCollectTaskConfiguration.setLogCollectTaskStatus(logCollectTask.getLogCollectTaskStatus());
        logCollectTaskConfiguration.setLogCollectTaskType(logCollectTask.getLogCollectTaskType());
        logCollectTaskConfiguration.setOldDataFilterType(logCollectTask.getOldDataFilterType());
        logCollectTaskConfiguration.setFileNameSuffixMatchRuleLogicJsonString(logCollectTask.getFileNameSuffixMatchRuleLogicJsonString());
        logCollectTaskConfiguration.setLogContentSliceRuleLogicJsonString(logCollectTask.getLogContentSliceRuleLogicJsonString());
        if (CollectionUtils.isEmpty(logCollectTask.getDirectoryLogCollectPathList()) && CollectionUtils.isEmpty(logCollectTask.getFileLogCollectPathList())) {
            throw new ServiceException(
                    String.format("LogCollectTask={id=%d}关联的目录型采集路径集 & 文件型采集路径集不可都为空", logCollectTask.getId()),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        if (CollectionUtils.isNotEmpty(logCollectTask.getFileLogCollectPathList())) {
            List<FileLogCollectPathDO> fileLogCollectPathList = logCollectTask.getFileLogCollectPathList();
            List<FileLogCollectPathConfiguration> fileLogCollectPathConfigurationList = new ArrayList<>(fileLogCollectPathList.size());
            for (FileLogCollectPathDO fileLogCollectPath : fileLogCollectPathList) {
                FileLogCollectPathConfiguration fileLogCollectPathConfiguration = fileLogCollectPath2FileLogCollectPathConfiguration(fileLogCollectPath);
                fileLogCollectPathConfigurationList.add(fileLogCollectPathConfiguration);
            }
            logCollectTaskConfiguration.setFileLogCollectPathList(fileLogCollectPathConfigurationList);
        }
        return logCollectTaskConfiguration;
    }

    @Override
    public FileLogCollectPathConfiguration fileLogCollectPath2FileLogCollectPathConfiguration(FileLogCollectPathDO fileLogCollectPath) throws ServiceException {
        if (null == fileLogCollectPath) {
            throw new ServiceException(
                    "入参fileLogCollectPath对象不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        FileLogCollectPathConfiguration fileLogCollectPathConfiguration = new FileLogCollectPathConfiguration();
        fileLogCollectPathConfiguration.setLogCollectTaskId(fileLogCollectPath.getLogCollectTaskId());
        fileLogCollectPathConfiguration.setPath(fileLogCollectPath.getPath());
        fileLogCollectPathConfiguration.setPathId(fileLogCollectPath.getId());
        fileLogCollectPathConfiguration.setRealPath(fileLogCollectPath.getRealPath());
        return fileLogCollectPathConfiguration;
    }

    @Override
    public HostInfo buildHostInfoByHost(HostDO host) throws ServiceException {
        if(null == host) {
            throw new ServiceException(
                    String.format(
                            "class=AgentCollectConfigurationManageServiceImpl||method=buildHostInfoByHost||msg={%s}",
                            "入参host对象不可为空"
                    ),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        YesOrNoEnum yesOrNoEnum = YesOrNoEnum.valueOf(host.getContainer());
        if(null == yesOrNoEnum) {
            throw new ServiceException(
                    String.format(
                            "class=AgentCollectConfigurationManageServiceImpl||method=buildHostInfoByHost||msg={%s}",
                            String.format("主机=[%s]字段container值[%d]非法，该值合法取值范围为[0,1]，导致获取需要运行在该主机上所有的日志采集任务对象集失败", JSON.toJSONString(host), host.getContainer())
                    ),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        Integer hostType = (yesOrNoEnum == YesOrNoEnum.YES ? HostTypeEnum.CONTAINER.getCode() : HostTypeEnum.HOST.getCode());
        HostInfo hostInfo = new HostInfo(host.getHostName(), hostType, host.getExtendField());
        return hostInfo;
    }

    @Override
    public boolean need2Deploy(LogCollectTaskDO logCollectTask, HostDO host) throws ServiceException {
        if(null == logCollectTask) {
            throw new ServiceException(
                    String.format(
                            "class=AgentCollectConfigurationManageServiceExtensionImpl||method=need2Deploy||msg={%s}",
                            "入参logCollectTask对象不可为空"
                    ),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        if(null == host) {
            throw new ServiceException(
                    String.format(
                            "class=AgentCollectConfigurationManageServiceExtensionImpl||method=need2Deploy||msg={%s}",
                            "入参host对象不可为空"
                    ),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        boolean needToDeploy = false;
        try {
            needToDeploy = HostFilterRule.getResult(host, logCollectTask.getHostFilterRuleLogicJsonString());
        } catch (ServiceException ex) {
            throw ex;
        }
        if(needToDeploy) {
            return true;
        } else {
            return false;
        }
    }

}
