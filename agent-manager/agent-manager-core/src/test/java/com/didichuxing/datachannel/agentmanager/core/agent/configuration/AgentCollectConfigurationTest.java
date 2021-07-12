//package com.didichuxing.datachannel.agentmanager.core.agent.collect.configuration;
//
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.DirectoryLogCollectPathDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.version.AgentVersionPO;
//import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config.AgentCollectConfiguration;
//import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config.HostInfo;
//import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config.LogCollectTaskConfiguration;
//import com.didichuxing.datachannel.agentmanager.common.constant.AgentConstant;
//import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
//import com.didichuxing.datachannel.agentmanager.common.enumeration.*;
//import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentCollectTypeEnum;
//import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
//import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskLimitPriorityLevelEnum;
//import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskTypeEnum;
//import com.didichuxing.datachannel.agentmanager.core.ApplicationTests;
//import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
//import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
//import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
//import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
//import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
//import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentVersionMapper;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.junit.jupiter.api.Test;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//@Transactional
//@Rollback
//public class AgentCollectConfigurationTest extends ApplicationTests {
//
//    @Autowired
//    private AgentCollectConfigurationManageService agentCollectConfigurationManageService;
//
//    @Autowired
//    private ServiceManageService serviceManageService;
//
//    @Autowired
//    private HostManageService hostManageService;
//
//    @Autowired
//    private LogCollectTaskManageService logCollectTaskManageService;
//
//    @Autowired
//    private AgentManageService agentManageService;
//
//    @Autowired
//    private KafkaClusterManageService kafkaClusterManageService;
//
//    @Autowired
//    private AgentVersionMapper agentVersionMapper;
//
//    /**
//     * 测是仅采集宿主机日志
//     */
//    @Test
//    public void testGetAgentCollectConfigurationByHostNameWhenCollectHostOnly() throws ParseException {
//        String hostName = initTestGetAgentCollectConfigurationByHostNameWhenCollectHostOnlyData();
//        AgentCollectConfiguration agentCollectConfiguration = agentCollectConfigurationManageService.getAgentConfigurationByHostName(hostName);
//        assert agentCollectConfiguration.getAgentConfiguration().getAgentId() > 0;
//        assert 1 == agentCollectConfiguration.getHostName2LogCollectTaskConfigurationMap().size();
//        Map<HostInfo, List<LogCollectTaskConfiguration>> hostName2LogCollectTaskMap = agentCollectConfiguration.getHostName2LogCollectTaskConfigurationMap();
//        HostInfo hostInfo = new HostInfo("测试主机_1", 0);
//        List<LogCollectTaskConfiguration> logCollectTaskConfigurationDOList = hostName2LogCollectTaskMap.get(hostInfo);
//        assert CollectionUtils.isNotEmpty(logCollectTaskConfigurationDOList);
//        assert logCollectTaskConfigurationDOList.size() == 2;
//        for (LogCollectTaskConfiguration logCollectTaskConfiguration : logCollectTaskConfigurationDOList) {
//            assert 1 == logCollectTaskConfiguration.getFileLogCollectPathList().size();
//            assert 1 == logCollectTaskConfiguration.getDirectoryLogCollectPathList().size();
//        }
//    }
//
//    /**
//     * 测是仅采集容器日志
//     */
//    @Test
//    public void testGetAgentCollectConfigurationByHostNameWhenCollectContainerOnly() {
//
//    }
//
//    /**
//     * 该函数用于构建测试函数 "testGetAgentCollectConfigurationByHostNameWhenCollectHostOnly" 所须主机、AgentPO、日志采集任务、服务数据集，具体包括：
//     * 5个服务对象：["测试服务_1","测试服务_2","测试服务_3","测试服务_4","测试服务_5"]
//     * 4个主机对象，具体为：
//     *  1个宿主机对象：{主机名：测试主机_1 ip：192.168.0.1 关联服务集：["测试服务_1","测试服务_2"]}
//     *  3个容器对象：{主机名：测试容器_1 ip：192.168.0.2 关联服务集：["测试服务_3"]} {主机名：测试容器_2 ip：192.168.0.3 关联服务集：["测试服务_4"]} {主机名：测试容器_3 ip：192.168.0.4 关联服务集：["测试服务_5"]}
//     * 5个日志采集任务对象，具体为：
//     *  {日志采集任务名：测试日志采集任务_1 关联服务集：["测试服务_1","测试服务_2"]}
//     *  {日志采集任务名：测试日志采集任务_2 关联服务集：["测试服务_3"]}
//     *  {日志采集任务名：测试日志采集任务_3 关联服务集：["测试服务_4"]}
//     *  {日志采集任务名：测试日志采集任务_4 关联服务集：["测试服务_5"]}
//     *  {日志采集任务名：测试日志采集任务_5 关联服务集：["测试服务_1","测试服务_2","测试服务_3","测试服务_4","测试服务_5"]}
//     * 1个Agent对象，具体为：
//     *  {主机名：测试主机_1 ip：192.168.0.1 collect_type：0} -- 采集物理机日志
//     *  @return 待测试agent主机名
//     *  @throws ParseException 日期转换异常
//     */
//    private String initTestGetAgentCollectConfigurationByHostNameWhenCollectHostOnlyData() throws ParseException {
//        initHostList();
//        initServiceAndServiceHostRelationList();
//        initReceivers();
//        initLogCollectTaskAndServiceLogCollectTaskRelationList();
//        return initAgentCollectHostOnly();
//    }
//
//    private static final Long metricsKafkaClusterId = 1L;
//    private static final Long errorLogsKafkaClusterId = 2L;
//    private static final Long logdataKafkaClusterId = 3L;
//    /**
//     * 初始化3个kafka集群，分别用于 metrics流、errorlogs流、log data 流
//     */
//    private void initReceivers() {
//
//        ReceiverDO metricsReceiverDO = new ReceiverDO();
//        metricsReceiverDO.setKafkaClusterBrokerConfiguration(UUID.randomUUID().toString());
//        metricsReceiverDO.setKafkaClusterId(metricsKafkaClusterId);
//        metricsReceiverDO.setKafkaClusterName(UUID.randomUUID().toString());
//        metricsReceiverDO.setKafkaClusterProducerInitConfiguration(UUID.randomUUID().toString());
//        assert kafkaClusterManageService.createKafkaCluster(metricsReceiverDO, null) > 0;
//
//        ReceiverDO errorLogsReceiverDO = new ReceiverDO();
//        errorLogsReceiverDO.setKafkaClusterBrokerConfiguration(UUID.randomUUID().toString());
//        errorLogsReceiverDO.setKafkaClusterId(errorLogsKafkaClusterId);
//        errorLogsReceiverDO.setKafkaClusterName(UUID.randomUUID().toString());
//        errorLogsReceiverDO.setKafkaClusterProducerInitConfiguration(UUID.randomUUID().toString());
//        assert kafkaClusterManageService.createKafkaCluster(errorLogsReceiverDO, null) > 0;
//
//        ReceiverDO logdataReceiverDO = new ReceiverDO();
//        logdataReceiverDO.setKafkaClusterBrokerConfiguration(UUID.randomUUID().toString());
//        logdataReceiverDO.setKafkaClusterId(logdataKafkaClusterId);
//        logdataReceiverDO.setKafkaClusterName(UUID.randomUUID().toString());
//        logdataReceiverDO.setKafkaClusterProducerInitConfiguration(UUID.randomUUID().toString());
//        assert kafkaClusterManageService.createKafkaCluster(logdataReceiverDO, null) > 0;
//
//    }
//
//    /**
//     * 初始化仅采集宿主机日志Agent对象
//     * {主机名：测试主机_1 ip：192.168.0.1 collect_type：0} -- 采集物理机日志
//     * @return agent对象对应主机名
//     */
//    private String initAgentCollectHostOnly() {
//
//        AgentVersionPO agentVersionPO = new AgentVersionPO();
//        agentVersionPO.setDescription(UUID.randomUUID().toString());
//        agentVersionPO.setFileMd5(UUID.randomUUID().toString());
//        agentVersionPO.setFileName(UUID.randomUUID().toString());
//        agentVersionPO.setFileType(1);
//        agentVersionPO.setVersion("agent_version_001");
//        agentVersionPO.setOperator(CommonConstant.getOperator(null));
//        assert agentVersionMapper.insert(agentVersionPO) > 0;
//        assert agentVersionPO.getId() > 0;
//
//        AgentDO agent = new AgentDO();
//        agent.setAdvancedConfigurationJsonString("advancedConfigurationJsonString");
//        agent.setByteLimitThreshold(1024 * 1024 * 9L);
//        agent.setCollectType(AgentCollectTypeEnum.COLLECT_HOST_ONLY.getCode());
//        agent.setCpuLimitThreshold(50);
//        agent.setHostName("测试主机_1");
//        agent.setIp("192.168.0.1");
//        agent.setAgentVersionId(agentVersionPO.getId());
//        agent.setConfigurationVersion(AgentConstant.AGENT_CONFIGURATION_VERSION_INIT);
//        agent.setMetricsSendReceiverId(kafkaClusterManageService.getKafkaClusterByKafkaClusterId(metricsKafkaClusterId).getId());
//        agent.setMetricsSendTopic("topic_metrics");
//        agent.setErrorLogsSendReceiverId(kafkaClusterManageService.getKafkaClusterByKafkaClusterId(errorLogsKafkaClusterId).getId());
//        agent.setErrorLogsSendTopic("topic_error_logs");
//
//        assert agentManageService.createAgent(agent,true, null) > 0;
//
//        return agent.getHostName();
//    }
//
//    /**
//     * 初始化 5 个日志采集任务对象 & 日志采集任务 服务关联关系，具体为：
//     *      *  {日志采集任务名：测试日志采集任务_1 关联服务集：["测试服务_1","测试服务_2"]}
//     *      *  {日志采集任务名：测试日志采集任务_2 关联服务集：["测试服务_3"]}
//     *      *  {日志采集任务名：测试日志采集任务_3 关联服务集：["测试服务_4"]}
//     *      *  {日志采集任务名：测试日志采集任务_4 关联服务集：["测试服务_5"]}
//     *      *  {日志采集任务名：测试日志采集任务_5 关联服务集：["测试服务_1","测试服务_2","测试服务_3","测试服务_4","测试服务_5"]}
//     */
//    private void initLogCollectTaskAndServiceLogCollectTaskRelationList() throws ParseException {
//
//        ServiceDO relationService1 = serviceManageService.getServiceByServiceName("测试服务_1");
//        ServiceDO relationService2 = serviceManageService.getServiceByServiceName("测试服务_2");
//        ServiceDO relationService3 = serviceManageService.getServiceByServiceName("测试服务_3");
//        ServiceDO relationService4 = serviceManageService.getServiceByServiceName("测试服务_4");
//        ServiceDO relationService5 = serviceManageService.getServiceByServiceName("测试服务_5");
//
//        assert null != relationService1;
//        assert null != relationService2;
//        assert null != relationService3;
//        assert null != relationService4;
//        assert null != relationService5;
//
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        /*
//         *
//         * 构建 5 个日志采集任务对象，具体为：
//         *      {日志采集任务名：测试日志采集任务_1 关联服务集：["测试服务_1","测试服务_2"]}
//         *      {日志采集任务名：测试日志采集任务_2 关联服务集：["测试服务_3"]}
//         *      {日志采集任务名：测试日志采集任务_3 关联服务集：["测试服务_4"]}
//         *      {日志采集任务名：测试日志采集任务_4 关联服务集：["测试服务_5"]}
//         *      {日志采集任务名：测试日志采集任务_5 关联服务集：["测试服务_1","测试服务_2","测试服务_3","测试服务_4","测试服务_5"]}
//         */
//
//        //logCollectTask1
//        LogCollectTaskDO logCollectTask1 = new LogCollectTaskDO();
//        logCollectTask1.setAdvancedConfigurationJsonString("advancedConfigurationJsonString");
//        logCollectTask1.setHostFilterRuleLogicJsonString("hostFilterRuleLogicJsonString");
//        logCollectTask1.setKafkaClusterId(kafkaClusterManageService.getKafkaClusterByKafkaClusterId(logdataKafkaClusterId).getId());
//        logCollectTask1.setLimitPriority(LogCollectTaskLimitPriorityLevelEnum.HIGH.getCode());
//        logCollectTask1.setLogCollectTaskName("测试日志采集任务_1");
//        logCollectTask1.setLogCollectTaskRemark("测试日志采集任务_remark_" + UUID.randomUUID().toString());
//        logCollectTask1.setLogCollectTaskType(LogCollectTaskTypeEnum.NORMAL_COLLECT.getCode());
//        logCollectTask1.setLogCollectTaskStatus(YesOrNoEnum.YES.getCode());
//        logCollectTask1.setSendTopic("topic_test_" + UUID.randomUUID().toString());
//        logCollectTask1.setConfigurationVersion(0);
//        logCollectTask1.setOldDataFilterType(0);
//        logCollectTask1.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTask1.setLogContentFilterRuleLogicJsonString(UUID.randomUUID().toString());
//
//        List<DirectoryLogCollectPathDO> directoryLogCollectPathList = new ArrayList<>();
//        DirectoryLogCollectPathDO directoryLogCollectPath = new DirectoryLogCollectPathDO();
//        directoryLogCollectPath.setCollectFilesFilterRegularPipelineJsonString("collectFilesFilterRegularPipelineJsonString");
//        directoryLogCollectPath.setDirectoryCollectDepth(1);
//        directoryLogCollectPath.setCharset("utf-8");
//        directoryLogCollectPath.setFdOffsetExpirationTimeMs(24 * 3600 * 1000L);
//        directoryLogCollectPath.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        directoryLogCollectPath.setPath("/home/logger/dir/test/");
//        directoryLogCollectPath.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        directoryLogCollectPath.setMaxBytesPerLogEvent(100L);
//        directoryLogCollectPathList.add(directoryLogCollectPath);
//        logCollectTask1.setDirectoryLogCollectPathList(directoryLogCollectPathList);
//
//        List<FileLogCollectPathDO> fileLogCollectPathList = new ArrayList<>();
//        FileLogCollectPathDO fileLogCollectPath = new FileLogCollectPathDO();
//        fileLogCollectPath.setFileNameSuffixMatchRuleLogicJsonString("collectFileSuffixMatchRuleLogicJsonString");
//        fileLogCollectPath.setCharset("utf-8");
//        fileLogCollectPath.setFdOffsetExpirationTimeMs(24 * 3600 * 1000L);
//        fileLogCollectPath.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        fileLogCollectPath.setPath("/home/logger/file/test.log");
//        fileLogCollectPath.setMaxBytesPerLogEvent(1000L);
//        fileLogCollectPath.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        fileLogCollectPath.setCollectDelayThresholdMs(1000L);
//        fileLogCollectPathList.add(fileLogCollectPath);
//        logCollectTask1.setFileLogCollectPathList(fileLogCollectPathList);
//
//        logCollectTask1.setServiceIdList(Arrays.asList(relationService1.getId(), relationService2.getId()));
//        Long result = logCollectTaskManageService.createLogCollectTask(logCollectTask1, null);
//        assert result > 0;
//
//        //logCollectTask2
//        LogCollectTaskDO logCollectTask2 = new LogCollectTaskDO();
//        logCollectTask2.setAdvancedConfigurationJsonString("advancedConfigurationJsonString");
//        logCollectTask2.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTask2.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTask2.setHostFilterRuleLogicJsonString("hostFilterRuleLogicJsonString");
//        logCollectTask2.setKafkaClusterId(kafkaClusterManageService.getKafkaClusterByKafkaClusterId(logdataKafkaClusterId).getId());
//        logCollectTask2.setLimitPriority(LogCollectTaskLimitPriorityLevelEnum.HIGH.getCode());
//        logCollectTask2.setLogCollectTaskName("测试日志采集任务_2");
//        logCollectTask2.setLogCollectTaskRemark("测试日志采集任务_remark_" + UUID.randomUUID().toString());
//        logCollectTask2.setLogCollectTaskType(LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode());
//        logCollectTask2.setLogCollectTaskStatus(YesOrNoEnum.YES.getCode());
//        logCollectTask2.setSendTopic("topic_test_" + UUID.randomUUID().toString());
//        logCollectTask2.setConfigurationVersion(0);
//        logCollectTask2.setOldDataFilterType(2);
//        logCollectTask2.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTask2.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTask2.setLogContentFilterRuleLogicJsonString(UUID.randomUUID().toString());
//        logCollectTask2.setLogCollectTaskExecuteTimeoutMs(1024L);
//
//        List<DirectoryLogCollectPathDO> directoryLogCollectPathList2 = new ArrayList<>();
//        DirectoryLogCollectPathDO directoryLogCollectPath2 = new DirectoryLogCollectPathDO();
//        directoryLogCollectPath2.setCollectFilesFilterRegularPipelineJsonString("collectFilesFilterRegularPipelineJsonString");
//        directoryLogCollectPath2.setDirectoryCollectDepth(9);
//        directoryLogCollectPath2.setCharset("utf-8");
//        directoryLogCollectPath2.setFdOffsetExpirationTimeMs(24 * 3600 * 1000L);
//        directoryLogCollectPath2.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        directoryLogCollectPath2.setPath("/home/logger/dir/test/");
//        directoryLogCollectPath2.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        directoryLogCollectPath2.setMaxBytesPerLogEvent(100L);
//        directoryLogCollectPathList2.add(directoryLogCollectPath2);
//        logCollectTask2.setDirectoryLogCollectPathList(directoryLogCollectPathList2);
//
//        List<FileLogCollectPathDO> fileLogCollectPathList2 = new ArrayList<>();
//        FileLogCollectPathDO fileLogCollectPath2 = new FileLogCollectPathDO();
//        fileLogCollectPath2.setFileNameSuffixMatchRuleLogicJsonString("collectFileSuffixMatchRuleLogicJsonString");
//        fileLogCollectPath2.setCharset("utf-8");
//        fileLogCollectPath2.setFdOffsetExpirationTimeMs(24 * 3600 * 1000L);
//        fileLogCollectPath2.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        fileLogCollectPath2.setPath("/home/logger/file/test.log");
//        fileLogCollectPath2.setMaxBytesPerLogEvent(1000L);
//        fileLogCollectPath2.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        fileLogCollectPath2.setCollectDelayThresholdMs(1000L);
//        fileLogCollectPathList2.add(fileLogCollectPath2);
//        logCollectTask2.setFileLogCollectPathList(fileLogCollectPathList2);
//
//        logCollectTask2.setServiceIdList(Arrays.asList(relationService3.getId()));
//
//        result = logCollectTaskManageService.createLogCollectTask(logCollectTask2, null);
//        assert result > 0;
//
//        //logCollectTask3
//        LogCollectTaskDO logCollectTask3 = new LogCollectTaskDO();
//        logCollectTask3.setAdvancedConfigurationJsonString("advancedConfigurationJsonString");
//        logCollectTask3.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTask3.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTask3.setHostFilterRuleLogicJsonString("hostFilterRuleLogicJsonString");
//        logCollectTask3.setKafkaClusterId(kafkaClusterManageService.getKafkaClusterByKafkaClusterId(logdataKafkaClusterId).getId());
//        logCollectTask3.setLimitPriority(LogCollectTaskLimitPriorityLevelEnum.HIGH.getCode());
//        logCollectTask3.setLogCollectTaskName("测试日志采集任务_3");
//        logCollectTask3.setLogCollectTaskRemark("测试日志采集任务_remark_" + UUID.randomUUID().toString());
//        logCollectTask3.setLogCollectTaskType(LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode());
//        logCollectTask3.setLogCollectTaskStatus(YesOrNoEnum.YES.getCode());
//        logCollectTask3.setSendTopic("topic_test_" + UUID.randomUUID().toString());
//        logCollectTask3.setConfigurationVersion(0);
//        logCollectTask3.setOldDataFilterType(2);
//        logCollectTask3.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTask3.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTask3.setLogContentFilterRuleLogicJsonString(UUID.randomUUID().toString());
//        logCollectTask3.setLogCollectTaskExecuteTimeoutMs(1024L);
//
//        List<DirectoryLogCollectPathDO> directoryLogCollectPathList3 = new ArrayList<>();
//        DirectoryLogCollectPathDO directoryLogCollectPath3 = new DirectoryLogCollectPathDO();
//        directoryLogCollectPath3.setCollectFilesFilterRegularPipelineJsonString("collectFilesFilterRegularPipelineJsonString");
//        directoryLogCollectPath3.setDirectoryCollectDepth(9);
//        directoryLogCollectPath3.setCharset("utf-8");
//        directoryLogCollectPath3.setFdOffsetExpirationTimeMs(24 * 3600 * 1000L);
//        directoryLogCollectPath3.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        directoryLogCollectPath3.setPath("/home/logger/dir/test/");
//        directoryLogCollectPath3.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        directoryLogCollectPath3.setMaxBytesPerLogEvent(100L);
//        directoryLogCollectPathList3.add(directoryLogCollectPath3);
//        logCollectTask3.setDirectoryLogCollectPathList(directoryLogCollectPathList3);
//
//        List<FileLogCollectPathDO> fileLogCollectPathList3 = new ArrayList<>();
//        FileLogCollectPathDO fileLogCollectPath3 = new FileLogCollectPathDO();
//        fileLogCollectPath3.setFileNameSuffixMatchRuleLogicJsonString("collectFileSuffixMatchRuleLogicJsonString");
//        fileLogCollectPath3.setCharset("utf-8");
//        fileLogCollectPath3.setFdOffsetExpirationTimeMs(24 * 3600 * 1000L);
//        fileLogCollectPath3.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        fileLogCollectPath3.setPath("/home/logger/file/test.log");
//        fileLogCollectPath3.setMaxBytesPerLogEvent(1000L);
//        fileLogCollectPath3.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        fileLogCollectPath3.setCollectDelayThresholdMs(1000L);
//        fileLogCollectPathList3.add(fileLogCollectPath3);
//        logCollectTask3.setFileLogCollectPathList(fileLogCollectPathList3);
//
//        logCollectTask3.setServiceIdList(Arrays.asList(relationService4.getId()));
//
//        result = logCollectTaskManageService.createLogCollectTask(logCollectTask3, null);
//        assert result > 0;
//
//        //logCollectTask4
//        LogCollectTaskDO logCollectTask4 = new LogCollectTaskDO();
//        logCollectTask4.setAdvancedConfigurationJsonString("advancedConfigurationJsonString");
//        logCollectTask4.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTask4.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTask4.setHostFilterRuleLogicJsonString("hostFilterRuleLogicJsonString");
//        logCollectTask4.setKafkaClusterId(kafkaClusterManageService.getKafkaClusterByKafkaClusterId(logdataKafkaClusterId).getId());
//        logCollectTask4.setLimitPriority(LogCollectTaskLimitPriorityLevelEnum.HIGH.getCode());
//        logCollectTask4.setLogCollectTaskName("测试日志采集任务_4");
//        logCollectTask4.setLogCollectTaskRemark("测试日志采集任务_remark_" + UUID.randomUUID().toString());
//        logCollectTask4.setLogCollectTaskType(LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode());
//        logCollectTask4.setLogCollectTaskStatus(YesOrNoEnum.YES.getCode());
//        logCollectTask4.setSendTopic("topic_test_" + UUID.randomUUID().toString());
//        logCollectTask4.setConfigurationVersion(0);
//        logCollectTask4.setOldDataFilterType(2);
//        logCollectTask4.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTask4.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTask4.setLogContentFilterRuleLogicJsonString(UUID.randomUUID().toString());
//        logCollectTask4.setLogCollectTaskExecuteTimeoutMs(1024L);
//
//        List<DirectoryLogCollectPathDO> directoryLogCollectPathList4 = new ArrayList<>();
//        DirectoryLogCollectPathDO directoryLogCollectPath4 = new DirectoryLogCollectPathDO();
//        directoryLogCollectPath4.setCollectFilesFilterRegularPipelineJsonString("collectFilesFilterRegularPipelineJsonString");
//        directoryLogCollectPath4.setDirectoryCollectDepth(9);
//        directoryLogCollectPath4.setCharset("utf-8");
//        directoryLogCollectPath4.setFdOffsetExpirationTimeMs(24 * 3600 * 1000L);
//        directoryLogCollectPath4.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        directoryLogCollectPath4.setPath("/home/logger/dir/test/");
//        directoryLogCollectPath4.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        directoryLogCollectPath4.setMaxBytesPerLogEvent(100L);
//        directoryLogCollectPathList4.add(directoryLogCollectPath4);
//        logCollectTask4.setDirectoryLogCollectPathList(directoryLogCollectPathList4);
//
//        List<FileLogCollectPathDO> fileLogCollectPathList4 = new ArrayList<>();
//        FileLogCollectPathDO fileLogCollectPath4 = new FileLogCollectPathDO();
//        fileLogCollectPath4.setFileNameSuffixMatchRuleLogicJsonString("collectFileSuffixMatchRuleLogicJsonString");
//        fileLogCollectPath4.setCharset("utf-8");
//        fileLogCollectPath4.setFdOffsetExpirationTimeMs(24 * 3600 * 1000L);
//        fileLogCollectPath4.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        fileLogCollectPath4.setPath("/home/logger/file/test.log");
//        fileLogCollectPath4.setMaxBytesPerLogEvent(1000L);
//        fileLogCollectPath4.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        fileLogCollectPath4.setCollectDelayThresholdMs(1000L);
//        fileLogCollectPathList4.add(fileLogCollectPath4);
//        logCollectTask4.setFileLogCollectPathList(fileLogCollectPathList4);
//
//        logCollectTask4.setServiceIdList(Arrays.asList(relationService5.getId()));
//
//        result = logCollectTaskManageService.createLogCollectTask(logCollectTask4, null);
//        assert result > 0;
//
//        //logCollectTask5
//        LogCollectTaskDO logCollectTask5 = new LogCollectTaskDO();
//        logCollectTask5.setAdvancedConfigurationJsonString("advancedConfigurationJsonString");
//        logCollectTask5.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTask5.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTask5.setHostFilterRuleLogicJsonString("hostFilterRuleLogicJsonString");
//        logCollectTask5.setKafkaClusterId(kafkaClusterManageService.getKafkaClusterByKafkaClusterId(logdataKafkaClusterId).getId());
//        logCollectTask5.setLimitPriority(LogCollectTaskLimitPriorityLevelEnum.HIGH.getCode());
//        logCollectTask5.setLogCollectTaskName("测试日志采集任务_5");
//        logCollectTask5.setLogCollectTaskRemark("测试日志采集任务_remark_" + UUID.randomUUID().toString());
//        logCollectTask5.setLogCollectTaskType(LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode());
//        logCollectTask5.setLogCollectTaskStatus(YesOrNoEnum.YES.getCode());
//        logCollectTask5.setSendTopic("topic_test_" + UUID.randomUUID().toString());
//        logCollectTask5.setConfigurationVersion(0);
//        logCollectTask5.setOldDataFilterType(2);
//        logCollectTask5.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTask5.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTask5.setLogContentFilterRuleLogicJsonString(UUID.randomUUID().toString());
//        logCollectTask5.setLogCollectTaskExecuteTimeoutMs(1024L);
//
//        List<DirectoryLogCollectPathDO> directoryLogCollectPathList5 = new ArrayList<>();
//        DirectoryLogCollectPathDO directoryLogCollectPath5 = new DirectoryLogCollectPathDO();
//        directoryLogCollectPath5.setCollectFilesFilterRegularPipelineJsonString("collectFilesFilterRegularPipelineJsonString");
//        directoryLogCollectPath5.setDirectoryCollectDepth(9);
//        directoryLogCollectPath5.setCharset("utf-8");
//        directoryLogCollectPath5.setFdOffsetExpirationTimeMs(24 * 3600 * 1000L);
//        directoryLogCollectPath5.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        directoryLogCollectPath5.setPath("/home/logger/dir/test/");
//        directoryLogCollectPath5.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        directoryLogCollectPath5.setMaxBytesPerLogEvent(100L);
//        directoryLogCollectPathList5.add(directoryLogCollectPath5);
//        logCollectTask5.setDirectoryLogCollectPathList(directoryLogCollectPathList5);
//
//        List<FileLogCollectPathDO> fileLogCollectPathList5 = new ArrayList<>();
//        FileLogCollectPathDO fileLogCollectPath5 = new FileLogCollectPathDO();
//        fileLogCollectPath5.setFileNameSuffixMatchRuleLogicJsonString("collectFileSuffixMatchRuleLogicJsonString");
//        fileLogCollectPath5.setCharset("utf-8");
//        fileLogCollectPath5.setFdOffsetExpirationTimeMs(24 * 3600 * 1000L);
//        fileLogCollectPath5.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        fileLogCollectPath5.setPath("/home/logger/file/test.log");
//        fileLogCollectPath5.setMaxBytesPerLogEvent(1000L);
//        fileLogCollectPath5.setLogContentSliceRuleLogicJsonString("logContentSliceRuleLogicJsonString");
//        fileLogCollectPath5.setCollectDelayThresholdMs(1000L);
//        fileLogCollectPathList5.add(fileLogCollectPath5);
//        logCollectTask5.setFileLogCollectPathList(fileLogCollectPathList5);
//
//        logCollectTask5.setServiceIdList(Arrays.asList(relationService1.getId(), relationService2.getId(), relationService3.getId(), relationService4.getId(), relationService5.getId()));
//
//        result = logCollectTaskManageService.createLogCollectTask(logCollectTask5, null);
//        assert result > 0;
//
//    }
//
//    /**
//     *
//     * 初始化 5 个服务对象：["测试服务_1","测试服务_2","测试服务_3","测试服务_4","测试服务_5"]
//     *
//     * 1个宿主机对象：{主机名：测试主机_1 ip：192.168.0.1 关联服务集：["测试服务_1","测试服务_2"]}
//     * 3个容器对象：
//     *  {主机名：测试容器_1 ip：192.168.0.2 关联服务集：["测试服务_3"]}
//     *  {主机名：测试容器_2 ip：192.168.0.3 关联服务集：["测试服务_4"]}
//     *  {主机名：测试容器_3 ip：192.168.0.4 关联服务集：["测试服务_5"]}
//     *
//     */
//    private void initServiceAndServiceHostRelationList() {
//
//        HostDO 测试主机_1 = hostManageService.getHostByHostName("测试主机_1");
//        HostDO 测试容器_1 = hostManageService.getHostByHostName("测试容器_1");
//        HostDO 测试容器_2 = hostManageService.getHostByHostName("测试容器_2");
//        HostDO 测试容器_3 = hostManageService.getHostByHostName("测试容器_3");
//
//        ServiceDO 测试服务_1 = new ServiceDO();
//        测试服务_1.setServicename("测试服务_1");
//        测试服务_1.setHostIdList(Arrays.asList(测试主机_1.getId()));
//        Long result = serviceManageService.createService(测试服务_1, CommonConstant.getOperator(null));
//        assert result > 0;
//
//        ServiceDO 测试服务_2 = new ServiceDO();
//        测试服务_2.setServicename("测试服务_2");
//        测试服务_2.setHostIdList(Arrays.asList(测试主机_1.getId()));
//        result = serviceManageService.createService(测试服务_2, CommonConstant.getOperator(null));
//        assert result > 0;
//
//        ServiceDO 测试服务_3 = new ServiceDO();
//        测试服务_3.setServicename("测试服务_3");
//        测试服务_3.setHostIdList(Arrays.asList(测试容器_1.getId()));
//        result = serviceManageService.createService(测试服务_3, CommonConstant.getOperator(null));
//        assert result > 0;
//
//        ServiceDO 测试服务_4 = new ServiceDO();
//        测试服务_4.setServicename("测试服务_4");
//        测试服务_4.setHostIdList(Arrays.asList(测试容器_2.getId()));
//        result = serviceManageService.createService(测试服务_4, CommonConstant.getOperator(null));
//        assert result > 0;
//
//        ServiceDO 测试服务_5 = new ServiceDO();
//        测试服务_5.setServicename("测试服务_5");
//        测试服务_5.setHostIdList(Arrays.asList(测试容器_3.getId()));
//        result = serviceManageService.createService(测试服务_5, CommonConstant.getOperator(null));
//        assert result > 0;
//
//    }
//
//    /**
//     * 初始化 4 个主机对象 & 服务 主机关联关系，具体为：
//     *      *  1个宿主机对象：{主机名：测试主机_1 ip：192.168.0.1 关联服务集：["测试服务_1","测试服务_2"]}
//     *      *  3个容器对象：{主机名：测试容器_1 ip：192.168.0.2 关联服务集：["测试服务_3"]} {主机名：测试容器_2 ip：192.168.0.3 关联服务集：["测试服务_4"]} {主机名：测试容器_3 ip：192.168.0.4 关联服务集：["测试服务_5"]}
//     */
//    private void initHostList() {
//
//        //构建宿主机对象
//        HostDO host = new HostDO();
//        host.setContainer(YesOrNoEnum.NO.getCode());
//        host.setDepartment("department_test");
//        host.setHostName("测试主机_1");
//        host.setIp("192.168.0.1");
//        host.setMachineZone("gz01");
//        host.setParentHostName(StringUtils.EMPTY);
//        Long result = hostManageService.createHost(host, null);
//        assert result > 0;
//
//        //构建三个容器对象，三个容器对象挂载在上述主机对象下
//        HostDO container1 = new HostDO();
//        container1.setContainer(YesOrNoEnum.YES.getCode());
//        container1.setDepartment("department_test");
//        container1.setHostName("测试容器_1");
//        container1.setIp("192.168.0.2");
//        container1.setMachineZone("gz01");
//        container1.setParentHostName("测试主机_1");
//        result = hostManageService.createHost(container1, null);
//        assert result > 0;
//
//        HostDO container2 = new HostDO();
//        container2.setContainer(YesOrNoEnum.YES.getCode());
//        container2.setDepartment("department_test");
//        container2.setHostName("测试容器_2");
//        container2.setIp("192.168.0.3");
//        container2.setMachineZone("gz01");
//        container2.setParentHostName("测试主机_1");
//        result = hostManageService.createHost(container2, null);
//        assert result > 0;
//
//        HostDO container3 = new HostDO();
//        container3.setContainer(YesOrNoEnum.YES.getCode());
//        container3.setDepartment("department_test");
//        container3.setHostName("测试容器_3");
//        container3.setIp("192.168.0.4");
//        container3.setMachineZone("gz01");
//        container3.setParentHostName("测试主机_1");
//        result = hostManageService.createHost(container3, null);
//        assert result > 0;
//
//    }
//
//}
