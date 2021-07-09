//package com.didichuxing.datachannel.agentmanager.core.host;
//
//import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostAgentDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostPaginationQueryConditionDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.DirectoryLogCollectPathDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.version.AgentVersionPO;
//import com.didichuxing.datachannel.agentmanager.common.constant.AgentConstant;
//import com.didichuxing.datachannel.agentmanager.common.constant.Constant;
//import com.didichuxing.datachannel.agentmanager.common.enumeration.*;
//import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentCollectTypeEnum;
//import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
//import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskLimitPriorityLevelEnum;
//import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskTypeEnum;
//import com.didichuxing.datachannel.agentmanager.core.ApplicationTests;
//import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
//import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
//import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
//import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
//import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentVersionMapper;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang.StringUtils;
//import org.apache.commons.lang3.builder.EqualsBuilder;
//import org.junit.jupiter.api.Test;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//@Transactional
//@Rollback
//public class HostManageServiceTest extends ApplicationTests {
//
//    @Autowired
//    private HostManageService hostManageService;
//
//    @Autowired
//    private ServiceManageService serviceManageService;
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
//    @Autowired
//    private LogCollectTaskManageService logCollectTaskManageService;
//
//    @Test
//    public void testHostUpdate() {
//
//        /*
//         * 初始化数据
//         */
//        initHost();
//        Result<HostDO> hostDOResult = hostManageService.getById(hostDOCreated.getId());
//        assert hostDOResult.success();
//        HostDO hostBeforeUpdate = hostDOResult.getData();
//        hostBeforeUpdate.setDepartment(UUID.randomUUID().toString());
//        hostBeforeUpdate.setMachineZone(UUID.randomUUID().toString());
//        hostBeforeUpdate.setContainer(YesOrNoEnum.YES.getCode());
//        hostBeforeUpdate.setHostName(UUID.randomUUID().toString());
//        hostBeforeUpdate.setIp(UUID.randomUUID().toString());
//        hostBeforeUpdate.setParentHostName(UUID.randomUUID().toString());
//        Result hostUpdateResult = hostManageService.updateHost(hostBeforeUpdate, null);
//        assert hostUpdateResult.success();
//        hostDOResult = hostManageService.getById(hostDOCreated.getId());
//        HostDO hostAfterUpdate = hostDOResult.getData();
//        assert EqualsBuilder.reflectionEquals(
//                hostAfterUpdate,
//                hostBeforeUpdate,
//                "createTime","operator","modifyTime"
//        );
//
//    }
//
//    @Test
//    public void testHostRemoveWithoutAgent() {
//
//        initHost();
//        Result result = hostManageService.deleteHost(hostDOCreated.getId(), false,null);
//        assert result.success();
//        assert hostManageService.getById(hostDOCreated.getId()).getData() == null;
//        assert CollectionUtils.isEmpty(serviceManageService.getServicesByHostId(hostDOCreated.getId()).getData());
//
//    }
//
//    @Test
//    public void testHostRemoveWithAgent() {
//
//        initHost();
//        initAgent();
//        Result result = hostManageService.deleteHost(hostDOCreated.getId(), false,null);
//        assert result.success();
//        assert hostManageService.getById(hostDOCreated.getId()).getData() == null;
//        assert CollectionUtils.isEmpty(serviceManageService.getServicesByHostId(hostDOCreated.getId()).getData());
//        assert agentManageService.getAgentByHostName(hostDOCreated.getHostName()) == null;
//
//    }
//
//    /**
//     * 初始化主机对象（附带 Agent 对象）
//     */
//    private void initAgent() {
//
//        ReceiverDO metricsReceiverDO = new ReceiverDO();
//        metricsReceiverDO.setKafkaClusterBrokerConfiguration(UUID.randomUUID().toString());
//        metricsReceiverDO.setKafkaClusterId(new Random().nextLong());
//        metricsReceiverDO.setKafkaClusterName(UUID.randomUUID().toString());
//        metricsReceiverDO.setKafkaClusterProducerInitConfiguration(UUID.randomUUID().toString());
//        Result<Long> result = kafkaClusterManageService.createKafkaCluster(metricsReceiverDO, null);
//        assert result.success();
//        Long metricsKafkaClusterId = result.getData();
//
//        ReceiverDO errorLogsReceiverDO = new ReceiverDO();
//        errorLogsReceiverDO.setKafkaClusterBrokerConfiguration(UUID.randomUUID().toString());
//        errorLogsReceiverDO.setKafkaClusterId(new Random().nextLong());
//        errorLogsReceiverDO.setKafkaClusterName(UUID.randomUUID().toString());
//        errorLogsReceiverDO.setKafkaClusterProducerInitConfiguration(UUID.randomUUID().toString());
//        result = kafkaClusterManageService.createKafkaCluster(errorLogsReceiverDO, null);
//        assert result.success();
//        Long errorLogsKafkaClusterId = result.getData();
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
//        agentDOCreated = new AgentDO();
//        agentDOCreated.setAdvancedConfigurationJsonString("advancedConfigurationJsonString");
//        agentDOCreated.setByteLimitThreshold(1024 * 1024 * 9L);
//        agentDOCreated.setCollectType(AgentCollectTypeEnum.COLLECT_HOST_ONLY.getCode());
//        agentDOCreated.setCpuLimitThreshold(50);
//        agentDOCreated.setHostName(hostName);
//        agentDOCreated.setIp("192.168.0.1");
//        agentDOCreated.setAgentVersionId(agentVersionPO.getId());
//        agentDOCreated.setConfigurationVersion(AgentConstant.AGENT_CONFIGURATION_VERSION_INIT);
//        agentDOCreated.setHealthLevel(AgentHealthLevelEnum.GREEN.getCode());
//        agentDOCreated.setMetricsSendReceiverId(metricsKafkaClusterId);
//        agentDOCreated.setMetricsSendTopic("topic_metrics");
//        agentDOCreated.setErrorLogsSendReceiverId(errorLogsKafkaClusterId);
//        agentDOCreated.setErrorLogsSendTopic("topic_error_logs");
//        assert agentManageService.createAgent(agentDOCreated,true, null) > 0;
//
//    }
//
//    /**
//     * 已创建主机对象
//     */
//    private HostDO hostDOCreated;
//    /**
//     * 已创建 agent 对象
//     */
//    private AgentDO agentDOCreated;
//    /**
//     * agent & host 对应 主机名
//     */
//    private String hostName = "测试主机_11";
//
//    /**
//     * 初始化主机对象
//     */
//    private void initHost() {
//        hostDOCreated = new HostDO();
//        hostDOCreated.setContainer(YesOrNoEnum.NO.getCode());
//        hostDOCreated.setDepartment("department_test");
//        hostDOCreated.setHostName(hostName);
//        hostDOCreated.setIp("192.168.0.1");
//        hostDOCreated.setMachineZone("gz01");
//        hostDOCreated.setParentHostName(StringUtils.EMPTY);
//        Result<Long> result = hostManageService.createHost(hostDOCreated, null);
//        assert result.success();
//        hostDOCreated.setId(result.getData());
//    }
//
//    @Test
//    public void testPaginationQuery() throws ParseException {
//
//        initPaginationQuertData();
//        HostPaginationQueryConditionDO hostPaginationQueryConditionDO = new HostPaginationQueryConditionDO();
//        hostPaginationQueryConditionDO.setCreateTimeStart(dateFormat.parse("2020-12-28 00:00:00"));
//        hostPaginationQueryConditionDO.setCreateTimeEnd(new Date(System.currentTimeMillis()));
//        hostPaginationQueryConditionDO.setAgentHealthLevel(AgentHealthLevelEnum.GREEN.getCode());
//        hostPaginationQueryConditionDO.setAgentVersionId(agentVersionPOCreated.getId());
//        hostPaginationQueryConditionDO.setContainer(YesOrNoEnum.YES.getCode());
//        hostPaginationQueryConditionDO.setServiceId(serviceDOCreated.getId());
//        hostPaginationQueryConditionDO.setHostName("测试");
//        hostPaginationQueryConditionDO.setIp("192.168.0.");
//        hostPaginationQueryConditionDO.setLimitFrom(0);
//        hostPaginationQueryConditionDO.setLimitSize(1000000);
//
//        Result<List<HostAgentDO>> paginationQueryByConditonResult = hostManageService.paginationQueryByConditon(hostPaginationQueryConditionDO);
//        Result<Integer> queryCountByConditionResult = hostManageService.queryCountByCondition(hostPaginationQueryConditionDO);
//
//        assert paginationQueryByConditonResult.success();
//        assert queryCountByConditionResult.success();
//
//        List<HostAgentDO> hostAgentDOList = paginationQueryByConditonResult.getData();
//
//        assert hostAgentDOList.size() == 2;
//        assert queryCountByConditionResult.getData() == 2;
//
//    }
//
//    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    private AgentVersionPO agentVersionPOCreated;
//    private ServiceDO serviceDOCreated;
//
//    private void initPaginationQuertData() {
//
//        initHostList();
//        initServiceAndServiceHostRelationList();
//        initAgentVersion();
//        initAgents();
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
//        serviceDOCreated = new ServiceDO();
//        serviceDOCreated.setServicename("测试服务_1");
//        serviceDOCreated.setHostIdList(Arrays.asList(测试主机_1.getId(),测试容器_1.getId(), 测试容器_2.getId(), 测试容器_3.getId()));
//        Result<Long> result = serviceManageService.createService(serviceDOCreated, Constant.getOperator(null));
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
//    private void initAgentVersion() {
//        agentVersionPOCreated = new AgentVersionPO();
//        agentVersionPOCreated.setOperator(Constant.getOperator(null));
//        agentVersionPOCreated.setVersion(UUID.randomUUID().toString());
//        agentVersionPOCreated.setFileType(1);
//        agentVersionPOCreated.setFileName(UUID.randomUUID().toString());
//        agentVersionPOCreated.setFileMd5(UUID.randomUUID().toString());
//        agentVersionPOCreated.setDescription(UUID.randomUUID().toString());
//        assert agentVersionMapper.insert(agentVersionPOCreated) > 0;
//    }
//
//    private void initAgents() {
//        AgentDO agentDO1 = new AgentDO();
//        agentDO1.setAgentVersionId(agentVersionPOCreated.getId());
//        agentDO1.setHostName("测试主机_1");
//        agentDO1.setIp("192.168.0.1");
//        agentDO1.setCollectType(AgentCollectTypeEnum.COLLECT_HOST_AND_CONTAINERS.getCode());
//        assert agentManageService.createAgent(agentDO1, false,null) > 0;
//
//        AgentDO agentDO2 = new AgentDO();
//        agentDO2.setAgentVersionId(agentVersionPOCreated.getId());
//        agentDO2.setHostName("测试容器_2");
//        agentDO2.setIp("192.168.0.3");
//        agentDO2.setCollectType(AgentCollectTypeEnum.COLLECT_HOST_ONLY.getCode());
//        assert agentManageService.createAgent(agentDO2, false,null) > 0;
//
//        AgentDO agentDO3 = new AgentDO();
//        agentDO3.setAgentVersionId(agentVersionPOCreated.getId());
//        agentDO3.setHostName("测试容器_3");
//        agentDO3.setIp("192.168.0.4");
//        agentDO3.setCollectType(AgentCollectTypeEnum.COLLECT_HOST_ONLY.getCode());
//        assert agentManageService.createAgent(agentDO3, false,null) > 0;
//
//    }
//
//    @Test
//    public void testGetAllMachineZones() {
//        initHostList();
//        Result<List<String>> result = hostManageService.getAllMachineZones();
//        assert result.success();
//        assert result.getData().size() == 1;
//    }
//
//    @Test
//    public void testGetHostListByLogCollectTaskId() {
//        initHostList();
//        initServiceAndServiceHostRelationList();
//        Long logCollectTaskId = initLogCollectTask();
//        Result<List<HostDO>> result = hostManageService.getHostListByLogCollectTaskId(logCollectTaskId);
//        assert result.success();
//        assert result.getData().size() == 4;
//    }
//
//    /**
//     * 初始化日志采集任务对象
//     */
//    private Long initLogCollectTask() {
//
//        Result<ServiceDO> relationService1 = serviceManageService.getServiceByServiceName("测试服务_1");
//
//        assert relationService1.success() && null != relationService1.getData();
//
//        //logCollectTask1
//        LogCollectTaskDO logCollectTask1 = new LogCollectTaskDO();
//        logCollectTask1.setAdvancedConfigurationJsonString("advancedConfigurationJsonString");
//        logCollectTask1.setHostFilterRuleLogicJsonString("hostFilterRuleLogicJsonString");
//        logCollectTask1.setKafkaClusterId(new Random().nextLong());
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
//        logCollectTask1.setServiceIdList(Arrays.asList(relationService1.getData().getId()));
//        Result<Long> result = logCollectTaskManageService.createLogCollectTask(logCollectTask1, null);
//        assert result.success();
//        assert result.getData() > 0;
//
//        return result.getData();
//
//    }
//
//}
