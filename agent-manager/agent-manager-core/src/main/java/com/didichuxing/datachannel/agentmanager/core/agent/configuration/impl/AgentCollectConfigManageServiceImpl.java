package com.didichuxing.datachannel.agentmanager.core.agent.configuration.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config.*;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.YesOrNoEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentCollectTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.host.HostTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.core.agent.configuration.AgentCollectConfigManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.collect.configuration.extension.impl.HostFilterRule;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author huqidong
 * @date 2020-09-21
 * Agent采集配置管理服务接口实现类
 */
@org.springframework.stereotype.Service
public class AgentCollectConfigManageServiceImpl implements AgentCollectConfigManageService {

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private KafkaClusterManageService kafkaClusterManageService;

    @Autowired
    private ServiceManageService serviceManageService;

    @Override
    public AgentCollectConfigDO getAgentConfigDOByHostName(String hostName) {
        return this.handleGetAgentConfigByHostName(hostName);
    }

    @Override
    public boolean need2Deploy(LogCollectTaskDO logCollectTask, HostDO hostDO) throws ServiceException {
        if(null == logCollectTask) {
            throw new ServiceException(
                    String.format(
                            "class=AgentCollectConfigurationManageServiceExtensionImpl||method=need2Deploy||msg={%s}",
                            "入参logCollectTask对象不可为空"
                    ),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        if(null == hostDO) {
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
            needToDeploy = HostFilterRule.getResult(hostDO, logCollectTask.getHostFilterRuleLogicJsonString());
        } catch (ServiceException ex) {
            throw ex;
        }
        return needToDeploy;
    }

    /**
     * 根据主机名获取该主机上运行的Agent所需要的配置信息
     * @param hostName 主机名
     * @return 返回给定主机名对应主机上运行的Agent所需要的配置信息，如 hostName 对应 AgentPO 不存在，return null
     */
    private AgentCollectConfigDO handleGetAgentConfigByHostName(String hostName) throws ServiceException {
        /*
         * 获取hostName对应Agent
         */
        AgentDO agentDO = getAgentByHostName(hostName);
        if(null == agentDO) {//主机名对应Agent不存在
            throw new ServiceException(
                    String.format("系统中不存在hostName={%s}的Agent", hostName),
                    ErrorCodeEnum.AGENT_NOT_EXISTS.getCode()
            );
        }
        /*
         * 根据Agent采集类型 & 宿主机主机名获取对应的日志采集任务信息（主机名：日志采集任务集）
         */
        Map<HostInfoDO, List<LogCollectTaskDO>> hostInfo2LogCollectTaskMap = null;
        Integer agentCollectType = agentDO.getCollectType();
        if(agentCollectType.equals(AgentCollectTypeEnum.COLLECT_HOST_ONLY.getCode())) {//仅采集宿主机日志
            hostInfo2LogCollectTaskMap = getLogCollectTaskListOfHost(hostName);
        } else if(agentCollectType.equals(AgentCollectTypeEnum.COLLECT_CONTAINERS_ONLY.getCode())) {//仅采集宿主机挂载的所有容器日志
            hostInfo2LogCollectTaskMap = getLogCollectTaskListOfContainersInHost(hostName);
        } else if(agentCollectType.equals(AgentCollectTypeEnum.COLLECT_HOST_AND_CONTAINERS.getCode())) {//采集宿主机日志 & 宿主机挂载的所有容器日志
            hostInfo2LogCollectTaskMap = getLogCollectTaskListOfContainersInHostAndHost(hostName);
        } else {
            throw new ServiceException(
                    String.format("agent对象={%s}对应agentCollectType属性值=[%d]非法，该属性值枚举范围见枚举类AgentCollectTypeEnum", JSON.toJSONString(agentDO), agentCollectType),
                    ErrorCodeEnum.AGENT_UNKNOWN_COLLECT_TYPE.getCode()
            );
        }
        /*
         * 构建Agent配置信息（包括"Agent自身配置信息" & "Agent待采集的日志采集任务配置信息"）
         */
        return buildAgentCollectConfig(agentDO, hostInfo2LogCollectTaskMap);
    }

    /**
     * 根据宿主机主机名获取需要部署在对应宿主机上的日志采集任务集
     * @param hostName 宿主机主机名
     * @return 根据宿主机主机名获取到的需要部署在对应宿主机上的日志采集任务集
     * @throws ServiceException 执行"根据宿主机主机名获取需要部署在对应宿主机上的日志采集任务集"操作过程中出现的异常
     */
    private Map<HostInfoDO, List<LogCollectTaskDO>> getLogCollectTaskListOfHost(String hostName) throws ServiceException {
        Map<HostInfoDO, List<LogCollectTaskDO>> hostInfo2LogCollectTaskMap = new HashMap<>();
        Pair<HostInfoDO, List<LogCollectTaskDO>> pair = getLogCollectTaskListByHostName(hostName, null);
        if(null != pair) {
            hostInfo2LogCollectTaskMap.put(pair.getKey(), pair.getValue());
        } else {
            //主机场景，拉取配置信息的主机不存在
            throw new ServiceException(
                    String.format("Agent拉取主机[hostName=%s]需要运行的日志采集任务集时，发现该主机[hostName=%s]在系统中不存在！", hostName, hostName),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        return hostInfo2LogCollectTaskMap;
    }

    /**
     * 根据宿主机主机名获取需要部署在该宿主机挂载的所有容器上的日志采集任务配置集
     * @param hostName 宿主机主机名
     * @return 根据宿主机主机名获取到的需要部署在该宿主机挂载的所有容器上的日志采集任务配置集
     * @throws ServiceException 执行"根据宿主机主机名获取需要部署在该宿主机挂载的所有容器上的日志采集任务配置集"操作过程中出现的异常
     */
    private Map<HostInfoDO, List<LogCollectTaskDO>> getLogCollectTaskListOfContainersInHost(String hostName) throws ServiceException {
        Map<HostInfoDO, List<LogCollectTaskDO>> hostInfo2LogCollectTaskMap = new HashMap<>();
        List<HostDO> containerList = hostManageService.getContainerListByParentHostName(hostName);
        if(null == containerList) {
            return hostInfo2LogCollectTaskMap;
        }
        for (HostDO container : containerList) {
            String containerHostName = container.getHostName();
            Pair<HostInfoDO, List<LogCollectTaskDO>> pair = getLogCollectTaskListByHostName(containerHostName, container);
            if(null != pair) {
                hostInfo2LogCollectTaskMap.put(pair.getKey(), pair.getValue());
            } else {
                //容器场景，pair 不可能为空，do nothing
            }
        }
        return hostInfo2LogCollectTaskMap;
    }

    /**
     * 根据宿主机主机名获取需要部署在对应宿主机上的日志采集任务集 & 需要部署在该宿主机挂载的所有容器上的日志采集任务集
     * @param hostName 宿主机主机名
     * @return 根据宿主机主机名获取到的需要部署在对应宿主机上的日志采集任务集 & 需要部署在该宿主机挂载的所有容器上的日志采集任务集
     * @throws ServiceException 执行"根据宿主机主机名获取需要部署在对应宿主机上的日志采集任务集 & 需要部署在该宿主机挂载的所有容器上的日志采集任务集"操作过程中出现的异常
     */
    private Map<HostInfoDO, List<LogCollectTaskDO>> getLogCollectTaskListOfContainersInHostAndHost(String hostName) throws ServiceException {
        Map<HostInfoDO, List<LogCollectTaskDO>> hostInfo2LogCollectTaskMap = getLogCollectTaskListOfHost(hostName);
        hostInfo2LogCollectTaskMap.putAll(getLogCollectTaskListOfContainersInHost(hostName));
        return hostInfo2LogCollectTaskMap;
    }

    /**
     * 根据主机名获取需要部署在对应主机上的日志采集任务集
     * @param hostName 主机名 必填
     * @param hostDO 主机对象 选填，在容器场景下，该 hostDO 对象不为空，之所以加该参数，目的为：容器场景下减少一次 mysql 查询
     * @return 根据主机名获取到的需要部署在对应主机上的日志采集任务集
     * @throws ServiceException 执行"根据主机名获取需要部署在对应主机上的日志采集任务集"操作过程中出现的异常
     */
    private Pair<HostInfoDO, List<LogCollectTaskDO>> getLogCollectTaskListByHostName(String hostName, HostDO hostDO) throws ServiceException {
        /*
         * 入参校验
         */
        if(StringUtils.isBlank(hostName)) {
            throw new ServiceException(
                    "入参hostName不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        /*
         * 获取待获取日志采集任务集的主机对象
         */
        if(null == hostDO) {
            hostDO = hostManageService.getHostByHostName(hostName);
            if(null == hostDO) {
                return null;
            }
        }
        /*
         * 根据主机对象id获取该主机关联的日志采集任务集（含：主机过滤逻辑）
         */
        List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageService.getLogCollectTaskListByHost(hostDO);
        /*
         * 根据hostDO对象构建对应HostInfo对象
         */
        HostInfoDO hostInfoDO = buildHostInfoByHost(hostDO);
        return new Pair<>(hostInfoDO, logCollectTaskDOList);
    }

    /**
     * 根据主机名获取Agent对象
     * @param hostName 主机名
     * @return 主机名对应Agent对象
     * @throws ServiceException 执行"根据主机名获取Agent对象"操作过程中出现的异常
     */
    private AgentDO getAgentByHostName(String hostName) throws ServiceException {
        return agentManageService.getAgentByHostName(hostName);
    }

    /**
     * 根据给定Agent自身配置信息、Agent需要采集的主机 & 需要运行在该主机上的日志采集任务配置集信息构建Agent配置信息（AgentConfiguration）
     * @param agent Agent对象
     * @param hostInfo2LogCollectTaskMap Agent需要采集的主机信息 & 需要运行在该主机上的日志采集任务集
     * @return 根据给定Agent自身配置信息、Agent需要采集的主机 & 需要运行在该主机上的日志采集任务配置集信息构建的Agent配置信息（AgentConfiguration）
     * @throws ServiceException 执行"根据给定Agent自身配置信息、Agent需要采集的主机 & 需要运行在该主机上的日志采集任务配置集信息构建Agent配置信息（AgentConfiguration）"操作过程中出现的异常
     */
    private AgentCollectConfigDO buildAgentCollectConfig(AgentDO agent, Map<HostInfoDO, List<LogCollectTaskDO>> hostInfo2LogCollectTaskMap) throws ServiceException {
        if (null == agent) {
            throw new ServiceException(
                    "入参agent对象不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        if (null == hostInfo2LogCollectTaskMap) {
            throw new ServiceException(
                    "入参hostInfo2LogCollectTaskMap对象不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        AgentCollectConfigDO agentConfiguration = new AgentCollectConfigDO();
        /*
         * 加载agent关联的metrics流 & errorlogs流对应的ReceiverDO对象
         */
        ReceiverDO metricsReceiverDO = kafkaClusterManageService.getById(agent.getMetricsSendReceiverId());
        ReceiverDO errorLogsReceiverDO = kafkaClusterManageService.getById(agent.getErrorLogsSendReceiverId());
        if (null == metricsReceiverDO) {
            throw new ServiceException(
                    String.format("Agent={%s}关联的MetricsReceiver对象={receiverId=%d}在系统中不存在", JSON.toJSONString(agent), agent.getMetricsSendReceiverId()),
                    ErrorCodeEnum.KAFKA_CLUSTER_NOT_EXISTS.getCode()
            );
        }
        if (null == errorLogsReceiverDO) {
            throw new ServiceException(
                    String.format("Agent={%s}关联的ErrorLogsReceiver对象={receiverId=%d}在系统中不存在", JSON.toJSONString(agent), agent.getErrorLogsSendReceiverId()),
                    ErrorCodeEnum.KAFKA_CLUSTER_NOT_EXISTS.getCode()
            );
        }
        /*
         * 构建AgentConfiguration对象
         */
        agentConfiguration.setAgentConfiguration(agent2AgentConfiguration(agent, metricsReceiverDO, errorLogsReceiverDO));
        /*
         * 构建agent待采集各主机 & 日志采集任务对象集
         */
        Map<HostInfoDO, List<LogCollectTaskConfiguration>> hostInfo2LogCollectTaskConfigurationMap = new HashMap<>();
        for (Map.Entry<HostInfoDO, List<LogCollectTaskDO>> hostInfo2LogCollectTaskEntry : hostInfo2LogCollectTaskMap.entrySet()) {
            HostInfoDO hostInfoDO = hostInfo2LogCollectTaskEntry.getKey();
            List<LogCollectTaskDO> logCollectTaskList = hostInfo2LogCollectTaskEntry.getValue();
            List<LogCollectTaskConfiguration> logCollectTaskConfigurationList = new ArrayList<>(logCollectTaskList.size());
            for (LogCollectTaskDO logCollectTask : logCollectTaskList) {
                ReceiverDO logReceiverDO = kafkaClusterManageService.getById(logCollectTask.getKafkaClusterId());
                if (null == logReceiverDO) {
                    throw new ServiceException(
                            String.format("LogCollectTaskDO={id=%d}关联的Receiver对象={receiverId=%d}在系统中不存在", logCollectTask.getId(), logCollectTask.getKafkaClusterId()),
                            ErrorCodeEnum.KAFKA_CLUSTER_NOT_EXISTS.getCode()
                    );
                }
                LogCollectTaskConfiguration logCollectTaskConfiguration = logCollectTask2LogCollectTaskConfiguration(logCollectTask, logReceiverDO);
                logCollectTaskConfigurationList.add(logCollectTaskConfiguration);
            }
            hostInfo2LogCollectTaskConfigurationMap.put(hostInfoDO, logCollectTaskConfigurationList);
        }
        agentConfiguration.setHostName2LogCollectTaskConfigurationMap(hostInfo2LogCollectTaskConfigurationMap);
        return agentConfiguration;
    }

    /**
     * 将给定Agent对象转化为AgentConfiguration对象
     * @param agent 待转化Agent对象
     * @param metricsReceiverDO Agent关联的指标数据对应接收端配置信息
     * @param errorLogsReceiverDO Agent关联的错误日志数据对应接收端配置信息
     * @return 返回将给定Agent对象转化的AgentConfiguration对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private AgentConfigDO agent2AgentConfiguration(AgentDO agent, ReceiverDO metricsReceiverDO, ReceiverDO errorLogsReceiverDO) throws ServiceException {
        if (null == agent) {
            throw new ServiceException(String.format("class=AgentCollectConfigurationManageServiceExtensionImpl||method=agent2AgentConfiguration||msg={%s}", "入参agent对象不可为空"), ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        /*
         * agentDo 2 agentConfiguration
         */
        try {
            AgentConfigDO agentConfigDO = new AgentConfigDO();
            agentConfigDO.setAdvancedConfigurationJsonString(agent.getAdvancedConfigurationJsonString());
            agentConfigDO.setAgentConfigurationVersion(agent.getConfigurationVersion());
            agentConfigDO.setAgentId(agent.getId());
            agentConfigDO.setHostName(agent.getHostName());
            agentConfigDO.setIp(agent.getIp());
            agentConfigDO.setCpuLimitThreshold(agent.getCpuLimitThreshold());
            agentConfigDO.setByteLimitThreshold(agent.getByteLimitThreshold());

            ReceiverConfigDO metricsReceiverConfigDO = new ReceiverConfigDO();
            metricsReceiverConfigDO.setNameServer(metricsReceiverDO.getKafkaClusterBrokerConfiguration());
            metricsReceiverConfigDO.setProperties(metricsReceiverDO.getKafkaClusterProducerInitConfiguration());
            metricsReceiverConfigDO.setReceiverId(metricsReceiverDO.getId());
            metricsReceiverConfigDO.setTopic(agent.getMetricsSendTopic());

            ReceiverConfigDO errorLogsReceiverConfigDO = new ReceiverConfigDO();
            errorLogsReceiverConfigDO.setNameServer(errorLogsReceiverDO.getKafkaClusterBrokerConfiguration());
            errorLogsReceiverConfigDO.setProperties(errorLogsReceiverDO.getKafkaClusterProducerInitConfiguration());
            errorLogsReceiverConfigDO.setReceiverId(errorLogsReceiverDO.getId());
            errorLogsReceiverConfigDO.setTopic(agent.getErrorLogsSendTopic());

            agentConfigDO.setMetricsProducerConfiguration(metricsReceiverConfigDO);
            agentConfigDO.setErrorLogsProducerConfiguration(errorLogsReceiverConfigDO);
            return agentConfigDO;
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

    /**
     * 将给定LogCollectTask对象转化为LogCollectTaskConfiguration对象
     * @param logCollectTask 待转化LogCollectTask对象
     * @param logReceiverDO 日志采集任务对应数据流接收端 id
     * @return 返回将给定LogCollectTask对象转化的LogCollectTaskConfiguration对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    LogCollectTaskConfiguration logCollectTask2LogCollectTaskConfiguration(LogCollectTaskDO logCollectTask, ReceiverDO logReceiverDO) throws ServiceException {
        if (null == logCollectTask) {
            throw new ServiceException(
                    "入参logCollectTask对象不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        LogCollectTaskConfiguration logCollectTaskConfiguration = new LogCollectTaskConfiguration();
        ReceiverConfigDO receiverConfigDO = new ReceiverConfigDO();
        receiverConfigDO.setTopic(logCollectTask.getSendTopic());
        receiverConfigDO.setReceiverId(logReceiverDO.getId());
        receiverConfigDO.setProperties(logReceiverDO.getKafkaClusterProducerInitConfiguration());
        receiverConfigDO.setNameServer(logReceiverDO.getKafkaClusterBrokerConfiguration());
        logCollectTaskConfiguration.setLogProducerConfiguration(receiverConfigDO);
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
        logCollectTaskConfiguration.setServiceNames(getSortedServiceNames(logCollectTask.getId()));
        if (CollectionUtils.isEmpty(logCollectTask.getDirectoryLogCollectPathList()) && CollectionUtils.isEmpty(logCollectTask.getFileLogCollectPathList())) {
            throw new ServiceException(
                    String.format("LogCollectTask={id=%d}关联的目录型采集路径集 & 文件型采集路径集不可都为空", logCollectTask.getId()),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        if (CollectionUtils.isNotEmpty(logCollectTask.getFileLogCollectPathList())) {
            List<FileLogCollectPathDO> fileLogCollectPathList = logCollectTask.getFileLogCollectPathList();
            List<FileLogCollectPathConfigDO> fileLogCollectPathConfigDOList = new ArrayList<>(fileLogCollectPathList.size());
            for (FileLogCollectPathDO fileLogCollectPath : fileLogCollectPathList) {
                FileLogCollectPathConfigDO fileLogCollectPathConfigDO = fileLogCollectPath2FileLogCollectPathConfiguration(fileLogCollectPath);
                fileLogCollectPathConfigDOList.add(fileLogCollectPathConfigDO);
            }
            logCollectTaskConfiguration.setFileLogCollectPathList(fileLogCollectPathConfigDOList);
        }
        return logCollectTaskConfiguration;
    }

    private String getSortedServiceNames(Long logCollectTaskId) {
        List<ServiceDO> serviceDOList = serviceManageService.getServicesByLogCollectTaskId(logCollectTaskId);
        String[] serviceNameArray = new String[serviceDOList.size()];
        for (int i = 0; i < serviceDOList.size(); i++) {
            serviceNameArray[i] = serviceDOList.get(i).getServicename();
        }
        Arrays.sort(serviceNameArray);
        return StringUtils.join(serviceNameArray, CommonConstant.COMMA);
    }

    /**
     * 将给定FileLogCollectPath对象转化为FileLogCollectPathConfiguration对象
     * @param fileLogCollectPath 待转化FileLogCollectPath对象
     * @return 返回将给定FileLogCollectPath对象转化的FileLogCollectPathConfiguration对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    public FileLogCollectPathConfigDO fileLogCollectPath2FileLogCollectPathConfiguration(FileLogCollectPathDO fileLogCollectPath) throws ServiceException {
        if (null == fileLogCollectPath) {
            throw new ServiceException(
                    "入参fileLogCollectPath对象不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        FileLogCollectPathConfigDO fileLogCollectPathConfigDO = new FileLogCollectPathConfigDO();
        fileLogCollectPathConfigDO.setLogCollectTaskId(fileLogCollectPath.getLogCollectTaskId());
        fileLogCollectPathConfigDO.setPath(fileLogCollectPath.getPath());
        fileLogCollectPathConfigDO.setPathId(fileLogCollectPath.getId());
        fileLogCollectPathConfigDO.setRealPath(fileLogCollectPath.getRealPath());
        return fileLogCollectPathConfigDO;
    }

    /**
     * 根据给定主机对象构建对应HostInfo对象
     * @param host 主机对象
     * @return 返回根据给定主机对象构建的HostInfo对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private HostInfoDO buildHostInfoByHost(HostDO host) throws ServiceException {
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
        HostInfoDO hostInfoDO = new HostInfoDO(host.getHostName(), hostType, host.getExtendField());
        return hostInfoDO;
    }

}
