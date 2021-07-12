package com.didichuxing.datachannel.agentmanager.core.agent.manage;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.ApplicationTests;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentVersionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Transactional
//@Rollback
public class AgentManageServiceTest extends ApplicationTests {

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private KafkaClusterManageService kafkaClusterManageService;

    @Autowired
    private AgentVersionMapper agentVersionMapper;

    @Autowired
    private ServiceManageService serviceManageService;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

//    @Test
//    public void agentCreateCaseAgentRegisterTest() {
//        initData(false);
//        AgentDO agentDO = agentManageService.getById(agentDOCreated.getId()).getData();
//        assert agentDO.getHostName().equals(agentDOCreated.getHostName());
//        assert agentDO.getIp().equals(agentDOCreated.getIp());
//        assert agentDO.getCollectType().equals(agentDOCreated.getCollectType());
//        assert agentDO.getAgentVersionId().equals(agentDOCreated.getAgentVersionId());
//    }
//
//    @Test
//    public void agentUpdateTest() {
//
//        initData(false);
//        AgentDO agentDOBeforeUpdate = agentManageService.getById(agentDOCreated.getId()).getData();
//        assert agentDOBeforeUpdate.getConfigurationVersion() == 0;
//        agentDOBeforeUpdate.setErrorLogsSendTopic(UUID.randomUUID().toString());
//        agentDOBeforeUpdate.setErrorLogsSendReceiverId(errorLogsReceiverDO.getId());
//        agentDOBeforeUpdate.setMetricsSendTopic(UUID.randomUUID().toString());
//        agentDOBeforeUpdate.setMetricsSendReceiverId(metricsReceiverDO.getId());
//        agentDOBeforeUpdate.setAdvancedConfigurationJsonString(UUID.randomUUID().toString());
//        agentDOBeforeUpdate.setByteLimitThreshold(new Random().nextLong());
//        agentDOBeforeUpdate.setCpuLimitThreshold(new Random().nextInt());
//        agentManageService.updateAgent(agentDOBeforeUpdate, null);
//        AgentDO agentDOAfterUpdate = agentManageService.getById(agentDOCreated.getId()).getData();
//        assert agentDOAfterUpdate.getConfigurationVersion() == 1;
//        assert EqualsBuilder.reflectionEquals(agentDOAfterUpdate, agentDOBeforeUpdate, "configurationVersion", "operator", "modifyTime", "createTime");
//
//    }
//
//    @Test
//    public void agentHealthLevelUpdateTest() {
//        initData(false);
//        AgentDO agentDOBeforeUpdate = agentManageService.getById(agentDOCreated.getId()).getData();
//        Result updateAgentResult = agentManageService.updateAgentHealthLevel(agentDOCreated.getId(), AgentHealthLevelEnum.RED);
//        assert updateAgentResult.success();
//        AgentDO agentDOAfterUpdate = agentManageService.getById(agentDOCreated.getId()).getData();
//        assert agentDOAfterUpdate.getHealthLevel().equals(AgentHealthLevelEnum.RED.getCode());
//    }
//
//    @Test
//    public void agentRemoveTest() {
//
//        initData(false);
//        Result result = agentManageService.deleteAgentByHostName(agentDOCreated.getHostName(), true, true, null);
//        assert result.success();
//        assert agentManageService.getAgentByHostName(agentDOCreated.getHostName()) == null;
//
//    }
//
//    private String hostName = "测试主机_1";
//    private String ip = "192.168.0.1";
//    private AgentDO agentDOCreated;
//    private ReceiverDO metricsReceiverDO;
//    private ReceiverDO errorLogsReceiverDO;
//
//    private void initData(boolean agentInstall) {
//
//        //构建宿主机对象
//        HostDO host = new HostDO();
//        host.setContainer(YesOrNoEnum.NO.getCode());
//        host.setDepartment("department_test");
//        host.setHostName(hostName);
//        host.setIp(ip);
//        host.setMachineZone("gz01");
//        host.setParentHostName(StringUtils.EMPTY);
//        Result<Long> result = hostManageService.createHost(host, null);
//        assert result.success();
//        assert result.getData() > 0;
//
//        //构建 agent version 对象
//        AgentVersionPO agentVersionPO = new AgentVersionPO();
//        agentVersionPO.setDescription(UUID.randomUUID().toString());
//        agentVersionPO.setFileMd5(UUID.randomUUID().toString());
//        agentVersionPO.setFileName(UUID.randomUUID().toString());
//        agentVersionPO.setFileType(1);
//        agentVersionPO.setVersion("agent_version_001");
//        agentVersionPO.setOperator(Constant.getOperator(null));
//        assert agentVersionMapper.insert(agentVersionPO) > 0;
//        assert agentVersionPO.getId() > 0;
//
//        //构建 agent 对象
//        agentDOCreated = new AgentDO();
//        agentDOCreated.setHostName(hostName);
//        agentDOCreated.setIp(ip);
//        agentDOCreated.setCollectType(AgentCollectTypeEnum.COLLECT_HOST_AND_CONTAINERS.getCode());
//        agentDOCreated.setAgentVersionId(agentVersionPO.getId());
//        agentDOCreated.setId(agentManageService.createAgent(agentDOCreated, agentInstall, null));
//
//        //构建 agent 对象对应 errorlogs &  metrics 流对应接收端对象
//        metricsReceiverDO = new ReceiverDO();
//        metricsReceiverDO.setKafkaClusterBrokerConfiguration(UUID.randomUUID().toString());
//        metricsReceiverDO.setKafkaClusterId(new Random().nextLong());
//        metricsReceiverDO.setKafkaClusterName(UUID.randomUUID().toString());
//        metricsReceiverDO.setKafkaClusterProducerInitConfiguration(UUID.randomUUID().toString());
//        result = kafkaClusterManageService.createKafkaCluster(metricsReceiverDO, null);
//        assert result.success();
//        metricsReceiverDO.setId(result.getData());
//
//        errorLogsReceiverDO = new ReceiverDO();
//        errorLogsReceiverDO.setKafkaClusterBrokerConfiguration(UUID.randomUUID().toString());
//        errorLogsReceiverDO.setKafkaClusterId(new Random().nextLong());
//        errorLogsReceiverDO.setKafkaClusterName(UUID.randomUUID().toString());
//        errorLogsReceiverDO.setKafkaClusterProducerInitConfiguration(UUID.randomUUID().toString());
//        result = kafkaClusterManageService.createKafkaCluster(errorLogsReceiverDO, null);
//        assert result.success();
//        errorLogsReceiverDO.setId(result.getData());
//
//    }
//
//    @Test
//    public void testGetAgentsByAgentVersionId() {
//
//        initData(false);
//        Result<List<AgentDO>> result = agentManageService.getAgentsByAgentVersionId(agentDOCreated.getAgentVersionId());
//        assert result.success();
//        assert result.getData().size() == 1;
//
//    }
//
//    @Test
//    public void testGetLogCollectTaskListByAgentId() throws ParseException {
//
//        Long agentId = initTestGetAgentCollectConfigurationByHostNameWhenCollectHostOnlyData();
//        Result<List<LogCollectTaskDO>> logCollectTaskDOListResult = agentManageService.getLogCollectTaskListByAgentId(agentId);
//        assert logCollectTaskDOListResult.success();
//        assert logCollectTaskDOListResult.getData().size() == 8;
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
//     *  @return 待测试 agent id
//     *  @throws ParseException 日期转换异常
//     */
//    private Long initTestGetAgentCollectConfigurationByHostNameWhenCollectHostOnlyData() throws ParseException {
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
//        assert kafkaClusterManageService.createKafkaCluster(metricsReceiverDO, null).success();
//
//        ReceiverDO errorLogsReceiverDO = new ReceiverDO();
//        errorLogsReceiverDO.setKafkaClusterBrokerConfiguration(UUID.randomUUID().toString());
//        errorLogsReceiverDO.setKafkaClusterId(errorLogsKafkaClusterId);
//        errorLogsReceiverDO.setKafkaClusterName(UUID.randomUUID().toString());
//        errorLogsReceiverDO.setKafkaClusterProducerInitConfiguration(UUID.randomUUID().toString());
//        assert kafkaClusterManageService.createKafkaCluster(errorLogsReceiverDO, null).success();
//
//        ReceiverDO logdataReceiverDO = new ReceiverDO();
//        logdataReceiverDO.setKafkaClusterBrokerConfiguration(UUID.randomUUID().toString());
//        logdataReceiverDO.setKafkaClusterId(logdataKafkaClusterId);
//        logdataReceiverDO.setKafkaClusterName(UUID.randomUUID().toString());
//        logdataReceiverDO.setKafkaClusterProducerInitConfiguration(UUID.randomUUID().toString());
//        assert kafkaClusterManageService.createKafkaCluster(logdataReceiverDO, null).success();
//
//    }
//
//    /**
//     * 初始化仅采集宿主机日志Agent对象
//     * {主机名：测试主机_1 ip：192.168.0.1 collect_type：0} -- 采集物理机日志
//     * @return agent对象对应主机名
//     */
//    private Long initAgentCollectHostOnly() {
//
//        AgentVersionPO agentVersionPO = new AgentVersionPO();
//        agentVersionPO.setDescription(UUID.randomUUID().toString());
//        agentVersionPO.setFileMd5(UUID.randomUUID().toString());
//        agentVersionPO.setFileName(UUID.randomUUID().toString());
//        agentVersionPO.setFileType(1);
//        agentVersionPO.setVersion("agent_version_001");
//        agentVersionPO.setOperator(Constant.getOperator(null));
//        assert agentVersionMapper.insert(agentVersionPO) > 0;
//        assert agentVersionPO.getId() > 0;
//
//        AgentDO agent = new AgentDO();
//        agent.setAdvancedConfigurationJsonString("advancedConfigurationJsonString");
//        agent.setByteLimitThreshold(1024 * 1024 * 9L);
//        agent.setCollectType(AgentCollectTypeEnum.COLLECT_HOST_AND_CONTAINERS.getCode());
//        agent.setCpuLimitThreshold(50);
//        agent.setHostName("测试主机_1");
//        agent.setIp("192.168.0.1");
//        agent.setAgentVersionId(agentVersionPO.getId());
//        agent.setConfigurationVersion(AgentConstant.AGENT_CONFIGURATION_VERSION_INIT);
//        agent.setHealthLevel(AgentHealthLevelEnum.GREEN.getCode());
//        agent.setMetricsSendReceiverId(kafkaClusterManageService.getKafkaClusterByKafkaClusterId(metricsKafkaClusterId).getData().getId());
//        agent.setMetricsSendTopic("topic_metrics");
//        agent.setErrorLogsSendReceiverId(kafkaClusterManageService.getKafkaClusterByKafkaClusterId(errorLogsKafkaClusterId).getData().getId());
//        agent.setErrorLogsSendTopic("topic_error_logs");
//
//        Long result = agentManageService.createAgent(agent,true, null);
//        assert result > 0;
//        return result;
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
//        Result<ServiceDO> relationService1 = serviceManageService.getServiceByServiceName("测试服务_1");
//        Result<ServiceDO> relationService2 = serviceManageService.getServiceByServiceName("测试服务_2");
//        Result<ServiceDO> relationService3 = serviceManageService.getServiceByServiceName("测试服务_3");
//        Result<ServiceDO> relationService4 = serviceManageService.getServiceByServiceName("测试服务_4");
//        Result<ServiceDO> relationService5 = serviceManageService.getServiceByServiceName("测试服务_5");
//
//        assert relationService1.success() && null != relationService1.getData();
//        assert relationService2.success() && null != relationService2.getData();
//        assert relationService3.success() && null != relationService3.getData();
//        assert relationService4.success() && null != relationService4.getData();
//        assert relationService5.success() && null != relationService5.getData();
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
//        logCollectTask1.setKafkaClusterId(kafkaClusterManageService.getKafkaClusterByKafkaClusterId(logdataKafkaClusterId).getData().getId());
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
//        logCollectTask1.setServiceIdList(Arrays.asList(relationService1.getData().getId(), relationService2.getData().getId()));
//        Result<Long> result = logCollectTaskManageService.createLogCollectTask(logCollectTask1, null);
//        assert result.success();
//        assert result.getData() > 0;
//
//        //logCollectTask2
//        LogCollectTaskDO logCollectTask2 = new LogCollectTaskDO();
//        logCollectTask2.setAdvancedConfigurationJsonString("advancedConfigurationJsonString");
//        logCollectTask2.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTask2.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTask2.setHostFilterRuleLogicJsonString("hostFilterRuleLogicJsonString");
//        logCollectTask2.setKafkaClusterId(kafkaClusterManageService.getKafkaClusterByKafkaClusterId(logdataKafkaClusterId).getData().getId());
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
//        logCollectTask2.setServiceIdList(Arrays.asList(relationService3.getData().getId()));
//
//        result = logCollectTaskManageService.createLogCollectTask(logCollectTask2, null);
//        assert result.success();
//        assert result.getData() > 0;
//
//        //logCollectTask3
//        LogCollectTaskDO logCollectTask3 = new LogCollectTaskDO();
//        logCollectTask3.setAdvancedConfigurationJsonString("advancedConfigurationJsonString");
//        logCollectTask3.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTask3.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTask3.setHostFilterRuleLogicJsonString("hostFilterRuleLogicJsonString");
//        logCollectTask3.setKafkaClusterId(kafkaClusterManageService.getKafkaClusterByKafkaClusterId(logdataKafkaClusterId).getData().getId());
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
//        logCollectTask3.setServiceIdList(Arrays.asList(relationService4.getData().getId()));
//
//        result = logCollectTaskManageService.createLogCollectTask(logCollectTask3, null);
//        assert result.success();
//        assert result.getData() > 0;
//
//        //logCollectTask4
//        LogCollectTaskDO logCollectTask4 = new LogCollectTaskDO();
//        logCollectTask4.setAdvancedConfigurationJsonString("advancedConfigurationJsonString");
//        logCollectTask4.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTask4.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTask4.setHostFilterRuleLogicJsonString("hostFilterRuleLogicJsonString");
//        logCollectTask4.setKafkaClusterId(kafkaClusterManageService.getKafkaClusterByKafkaClusterId(logdataKafkaClusterId).getData().getId());
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
//        logCollectTask4.setServiceIdList(Arrays.asList(relationService5.getData().getId()));
//
//        result = logCollectTaskManageService.createLogCollectTask(logCollectTask4, null);
//        assert result.success();
//        assert result.getData() > 0;
//
//        //logCollectTask5
//        LogCollectTaskDO logCollectTask5 = new LogCollectTaskDO();
//        logCollectTask5.setAdvancedConfigurationJsonString("advancedConfigurationJsonString");
//        logCollectTask5.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTask5.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTask5.setHostFilterRuleLogicJsonString("hostFilterRuleLogicJsonString");
//        logCollectTask5.setKafkaClusterId(kafkaClusterManageService.getKafkaClusterByKafkaClusterId(logdataKafkaClusterId).getData().getId());
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
//        logCollectTask5.setServiceIdList(Arrays.asList(relationService1.getData().getId(), relationService2.getData().getId(), relationService3.getData().getId(), relationService4.getData().getId(), relationService5.getData().getId()));
//
//        result = logCollectTaskManageService.createLogCollectTask(logCollectTask5, null);
//        assert result.success();
//        assert result.getData() > 0;
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
//        Result<Long> result = serviceManageService.createService(测试服务_1, Constant.getOperator(null));
//        assert result.success();
//        assert result.getData() > 0;
//
//        ServiceDO 测试服务_2 = new ServiceDO();
//        测试服务_2.setServicename("测试服务_2");
//        测试服务_2.setHostIdList(Arrays.asList(测试主机_1.getId()));
//        result = serviceManageService.createService(测试服务_2, Constant.getOperator(null));
//        assert result.success();
//        assert result.getData() > 0;
//
//        ServiceDO 测试服务_3 = new ServiceDO();
//        测试服务_3.setServicename("测试服务_3");
//        测试服务_3.setHostIdList(Arrays.asList(测试容器_1.getId()));
//        result = serviceManageService.createService(测试服务_3, Constant.getOperator(null));
//        assert result.success();
//        assert result.getData() > 0;
//
//        ServiceDO 测试服务_4 = new ServiceDO();
//        测试服务_4.setServicename("测试服务_4");
//        测试服务_4.setHostIdList(Arrays.asList(测试容器_2.getId()));
//        result = serviceManageService.createService(测试服务_4, Constant.getOperator(null));
//        assert result.success();
//        assert result.getData() > 0;
//
//        ServiceDO 测试服务_5 = new ServiceDO();
//        测试服务_5.setServicename("测试服务_5");
//        测试服务_5.setHostIdList(Arrays.asList(测试容器_3.getId()));
//        result = serviceManageService.createService(测试服务_5, Constant.getOperator(null));
//        assert result.success();
//        assert result.getData() > 0;
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
//        Result<Long> result = hostManageService.createHost(host, null);
//        assert result.success();
//        assert result.getData() > 0;
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
//        assert result.success();
//        assert result.getData() > 0;
//
//        HostDO container2 = new HostDO();
//        container2.setContainer(YesOrNoEnum.YES.getCode());
//        container2.setDepartment("department_test");
//        container2.setHostName("测试容器_2");
//        container2.setIp("192.168.0.3");
//        container2.setMachineZone("gz01");
//        container2.setParentHostName("测试主机_1");
//        result = hostManageService.createHost(container2, null);
//        assert result.success();
//        assert result.getData() > 0;
//
//        HostDO container3 = new HostDO();
//        container3.setContainer(YesOrNoEnum.YES.getCode());
//        container3.setDepartment("department_test");
//        container3.setHostName("测试容器_3");
//        container3.setIp("192.168.0.4");
//        container3.setMachineZone("gz01");
//        container3.setParentHostName("测试主机_1");
//        result = hostManageService.createHost(container3, null);
//        assert result.success();
//        assert result.getData() > 0;
//
//    }
//
//    @Test
//    public void testGetAll() {
//
//        initData(false);
//        Result<List<AgentDO>> agentDOListResult = agentManageService.list();
//        assert agentDOListResult.success();
//        assert agentDOListResult.getData().size() > 0;
//
//    }
//

    @Test
    public void agentHealthTest() {
        List<AgentDO> agentDOList = agentManageService.list();
        for (AgentDO agentDO : agentDOList) {
            AgentHealthLevelEnum agentHealthLevelEnum = agentManageService.checkAgentHealth(agentDO);
        }
    }
}
