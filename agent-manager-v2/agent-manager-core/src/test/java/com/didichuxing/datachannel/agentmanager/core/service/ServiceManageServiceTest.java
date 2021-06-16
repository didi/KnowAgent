package com.didichuxing.datachannel.agentmanager.core.service;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServicePaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServicePaginationRecordDO;
import com.didichuxing.datachannel.agentmanager.core.ApplicationTests;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

//@Transactional
//@Rollback
public class ServiceManageServiceTest extends ApplicationTests {
    private static final ILog LOGGER = LogFactory.getLog(ServiceManageServiceTest.class);

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private ServiceManageService serviceManageService;

    @Autowired
    private KafkaClusterManageService kafkaClusterManageService;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

//    @Test
//    public void testServiceUpdate() {
//
//        /*
//         * 初始化数据
//         */
//        initService();
//        ServiceDO serviceDOBeforeUpdate = serviceManageService.getServiceById(serviceDOCreated.getId());
//        List<HostDO> hostDOList = hostManageService.getHostsByServiceId(serviceDOCreated.getId());
//        List<Long> relationHostIdList = new ArrayList<>(hostDOList.size());
//        for (HostDO hostDO : hostDOList) {
//            relationHostIdList.add(hostDO.getId());
//        }
//        assert ListCompareUtil.listEquals(relationHostIdList, Arrays.asList(hostDOBeforeServiceUpdate.getId()));
//        List<ServiceDO> serviceDOList = serviceManageService.getServicesByLogCollectTaskId(relationLogCollectTaskDO1.getId()).getData();
//        assert serviceDOList.size() == 1;
//        assert serviceDOList.get(0).getId().equals(serviceDOBeforeUpdate.getId());
//        serviceDOList = serviceManageService.getServicesByLogCollectTaskId(relationLogCollectTaskDO2.getId()).getData();
//        assert serviceDOList.size() == 1;
//        assert serviceDOList.get(0).getId().equals(serviceDOBeforeUpdate.getId());
//        /*
//         * 更新对应服务对象
//         */
//        serviceDOBeforeUpdate.setHostIdList(Arrays.asList(hostDOAfterServiceUpdate1.getId(), hostDOAfterServiceUpdate2.getId()));
//        Result result = serviceManageService.updateService(serviceDOBeforeUpdate, null);
//        assert result.success();
//        serviceDOResult = serviceManageService.getServiceById(serviceDOCreated.getId());
//        assert serviceDOResult.success();
//        ServiceDO serviceDOAfterUpdate = serviceDOResult.getData();
//        hostDOList = hostManageService.getHostsByServiceId(serviceDOAfterUpdate.getId()).getData();
//        relationHostIdList = new ArrayList<>(hostDOList.size());
//        for (HostDO hostDO : hostDOList) {
//            relationHostIdList.add(hostDO.getId());
//        }
//        assert ListCompareUtil.listEquals(relationHostIdList, Arrays.asList(hostDOAfterServiceUpdate2.getId(), hostDOAfterServiceUpdate1.getId()));
//        serviceDOList = serviceManageService.getServicesByLogCollectTaskId(relationLogCollectTaskDO1.getId()).getData();
//        assert serviceDOList.size() == 1;
//        assert serviceDOList.get(0).getId().equals(serviceDOAfterUpdate.getId());
//        serviceDOList = serviceManageService.getServicesByLogCollectTaskId(relationLogCollectTaskDO2.getId()).getData();
//        assert serviceDOList.size() == 1;
//        assert serviceDOList.get(0).getId().equals(serviceDOAfterUpdate.getId());
//
//    }
//
//    /**
//     * 初始化服务对象
//     */
//    private void initService() {
//
//        //构建服务对象，该服务对象关联两个主机对象，两个日志采集任务对象
//
//        /*
//         * 构建三个主机对象
//         */
//        hostDOBeforeServiceUpdate = new HostDO();
//        hostDOBeforeServiceUpdate.setContainer(YesOrNoEnum.NO.getCode());
//        hostDOBeforeServiceUpdate.setDepartment("department_test");
//        hostDOBeforeServiceUpdate.setHostName("测试主机_1");
//        hostDOBeforeServiceUpdate.setIp("192.168.0.1");
//        hostDOBeforeServiceUpdate.setMachineZone("gz01");
//        hostDOBeforeServiceUpdate.setParentHostName(StringUtils.EMPTY);
//        Result<Long> result = hostManageService.createHost(hostDOBeforeServiceUpdate, null);
//        assert result.success();
//        hostDOBeforeServiceUpdate.setId(result.getData());
//
//        hostDOAfterServiceUpdate1 = new HostDO();
//        hostDOAfterServiceUpdate1.setContainer(YesOrNoEnum.YES.getCode());
//        hostDOAfterServiceUpdate1.setDepartment("department_test");
//        hostDOAfterServiceUpdate1.setHostName("测试容器_1");
//        hostDOAfterServiceUpdate1.setIp("192.168.0.2");
//        hostDOAfterServiceUpdate1.setMachineZone("gz01");
//        hostDOAfterServiceUpdate1.setParentHostName("测试主机_1");
//        result = hostManageService.createHost(hostDOAfterServiceUpdate1, null);
//        assert result.success();
//        hostDOAfterServiceUpdate1.setId(result.getData());
//
//        hostDOAfterServiceUpdate2 = new HostDO();
//        hostDOAfterServiceUpdate2.setContainer(YesOrNoEnum.YES.getCode());
//        hostDOAfterServiceUpdate2.setDepartment("department_test");
//        hostDOAfterServiceUpdate2.setHostName("测试容器_2");
//        hostDOAfterServiceUpdate2.setIp("192.168.0.3");
//        hostDOAfterServiceUpdate2.setMachineZone("gz01");
//        hostDOAfterServiceUpdate2.setParentHostName("测试主机_1");
//        result = hostManageService.createHost(hostDOAfterServiceUpdate2, null);
//        assert result.success();
//        hostDOAfterServiceUpdate2.setId(result.getData());
//
//        /*
//         * 构建服务对象
//         */
//        serviceDOCreated = new ServiceDO();
//        serviceDOCreated.setServicename("serviceDOCreated");
//        serviceDOCreated.setHostIdList(Arrays.asList(hostDOBeforeServiceUpdate.getId()));
//        result = serviceManageService.createService(serviceDOCreated, Constant.getOperator(null));
//        assert result.success();
//        serviceDOCreated.setId(result.getData());
//
//        /*
//         * 构建日志采集任务对应接收端对象
//         */
//        ReceiverDO logdataReceiverDO = new ReceiverDO();
//        logdataReceiverDO.setKafkaClusterBrokerConfiguration(UUID.randomUUID().toString());
//        logdataReceiverDO.setKafkaClusterId(new Random().nextLong());
//        logdataReceiverDO.setKafkaClusterName(UUID.randomUUID().toString());
//        logdataReceiverDO.setKafkaClusterProducerInitConfiguration(UUID.randomUUID().toString());
//        Result<Long> createKafkaClusterResult = kafkaClusterManageService.createKafkaCluster(logdataReceiverDO, null);
//        assert createKafkaClusterResult.success();
//        logdataReceiverDO.setId(createKafkaClusterResult.getData());
//
//        /*
//         * 构建两个日志采集任务对象
//         */
//        //relationLogCollectTaskDO1
//        relationLogCollectTaskDO1 = new LogCollectTaskDO();
//        relationLogCollectTaskDO1.setAdvancedConfigurationJsonString("advancedConfigurationJsonString");
//        relationLogCollectTaskDO1.setHostFilterRuleLogicJsonString("hostFilterRuleLogicJsonString");
//        relationLogCollectTaskDO1.setKafkaClusterId(logdataReceiverDO.getId());
//        relationLogCollectTaskDO1.setLimitPriority(LogCollectTaskLimitPriorityLevelEnum.HIGH.getCode());
//        relationLogCollectTaskDO1.setLogCollectTaskName("测试日志采集任务_1");
//        relationLogCollectTaskDO1.setLogCollectTaskRemark("测试日志采集任务_remark_" + UUID.randomUUID().toString());
//        relationLogCollectTaskDO1.setLogCollectTaskType(LogCollectTaskTypeEnum.NORMAL_COLLECT.getCode());
//        relationLogCollectTaskDO1.setLogCollectTaskStatus(YesOrNoEnum.YES.getCode());
//        relationLogCollectTaskDO1.setSendTopic("topic_test_" + UUID.randomUUID().toString());
//        relationLogCollectTaskDO1.setConfigurationVersion(0);
//        relationLogCollectTaskDO1.setOldDataFilterType(0);
//        relationLogCollectTaskDO1.setCollectStartTimeBusiness(System.currentTimeMillis());
//        relationLogCollectTaskDO1.setLogContentFilterRuleLogicJsonString(UUID.randomUUID().toString());
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
//        relationLogCollectTaskDO1.setDirectoryLogCollectPathList(directoryLogCollectPathList);
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
//        relationLogCollectTaskDO1.setFileLogCollectPathList(fileLogCollectPathList);
//
//        relationLogCollectTaskDO1.setServiceIdList(Arrays.asList(serviceDOCreated.getId()));
//        result = logCollectTaskManageService.createLogCollectTask(relationLogCollectTaskDO1, null);
//        assert result.success();
//        relationLogCollectTaskDO1.setId(result.getData());
//
//        //relationLogCollectTaskDO2
//        relationLogCollectTaskDO2 = new LogCollectTaskDO();
//        relationLogCollectTaskDO2.setAdvancedConfigurationJsonString("advancedConfigurationJsonString");
//        relationLogCollectTaskDO2.setCollectStartTimeBusiness(System.currentTimeMillis());
//        relationLogCollectTaskDO2.setCollectEndTimeBusiness(System.currentTimeMillis());
//        relationLogCollectTaskDO2.setHostFilterRuleLogicJsonString("hostFilterRuleLogicJsonString");
//        relationLogCollectTaskDO2.setKafkaClusterId(logdataReceiverDO.getId());
//        relationLogCollectTaskDO2.setLimitPriority(LogCollectTaskLimitPriorityLevelEnum.HIGH.getCode());
//        relationLogCollectTaskDO2.setLogCollectTaskName("测试日志采集任务_2");
//        relationLogCollectTaskDO2.setLogCollectTaskRemark("测试日志采集任务_remark_" + UUID.randomUUID().toString());
//        relationLogCollectTaskDO2.setLogCollectTaskType(LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode());
//        relationLogCollectTaskDO2.setLogCollectTaskStatus(YesOrNoEnum.YES.getCode());
//        relationLogCollectTaskDO2.setSendTopic("topic_test_" + UUID.randomUUID().toString());
//        relationLogCollectTaskDO2.setConfigurationVersion(0);
//        relationLogCollectTaskDO2.setOldDataFilterType(2);
//        relationLogCollectTaskDO2.setCollectStartTimeBusiness(System.currentTimeMillis());
//        relationLogCollectTaskDO2.setCollectEndTimeBusiness(System.currentTimeMillis());
//        relationLogCollectTaskDO2.setLogContentFilterRuleLogicJsonString(UUID.randomUUID().toString());
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
//        relationLogCollectTaskDO2.setDirectoryLogCollectPathList(directoryLogCollectPathList2);
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
//        relationLogCollectTaskDO2.setFileLogCollectPathList(fileLogCollectPathList2);
//        relationLogCollectTaskDO2.setServiceIdList(Arrays.asList(serviceDOCreated.getId()));
//
//        result = logCollectTaskManageService.createLogCollectTask(relationLogCollectTaskDO2, null);
//        assert result.success();
//        relationLogCollectTaskDO2.setId(result.getData());
//
//    }
//
//    @Test
//    public void testServiceRemove() {
//
//        initService();
//        Result result = serviceManageService.deleteService(serviceDOCreated.getId(), null);
//        assert result.success();
//
//        assert serviceManageService.getServiceById(serviceDOCreated.getId()).getData() == null;
//        List<ServiceDO> serviceDOList = serviceManageService.getServicesByLogCollectTaskId(relationLogCollectTaskDO1.getId()).getData();
//        assert serviceDOList.size() == 0;
//        serviceDOList = serviceManageService.getServicesByLogCollectTaskId(relationLogCollectTaskDO2.getId()).getData();
//        assert serviceDOList.size() == 0;
//        assert CollectionUtils.isEmpty(hostManageService.getHostsByServiceId(serviceDOCreated.getId()).getData());
//
//    }
//
//    @Test
//    public void testServicePaginationQuery() throws ParseException {
//
//        initServiceAndHost();
//
//        ServicePaginationQueryConditionDO servicePaginationQueryConditionDO = new ServicePaginationQueryConditionDO();
//        servicePaginationQueryConditionDO.setServiceName("service_");
//        servicePaginationQueryConditionDO.setCreateTimeStart(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-12-28 00:00:00"));
//        servicePaginationQueryConditionDO.setCreateTimeEnd(new Date(System.currentTimeMillis()));
//        servicePaginationQueryConditionDO.setLimitFrom(0);
//        servicePaginationQueryConditionDO.setLimitSize(10000);
//
//        Result<List<ServicePaginationRecordDO>> paginationQueryByConditonResult = serviceManageService.paginationQueryByConditon(servicePaginationQueryConditionDO);
//        Result<Integer> queryCountByConditionResult = serviceManageService.queryCountByCondition(servicePaginationQueryConditionDO);
//        assert paginationQueryByConditonResult.getData().size() == 2;
//        assert paginationQueryByConditonResult.getData().get(0).getRelationHostCount().equals(3);
//        assert paginationQueryByConditonResult.getData().get(1).getRelationHostCount().equals(2);
//        assert queryCountByConditionResult.getData().equals(2);
//
//    }
//
//    /**
//     * 初始化服务 & 主机信息
//     */
//    private void initServiceAndHost() {
//
//        /*
//         * 构建三个主机对象
//         */
//        hostDOBeforeServiceUpdate = new HostDO();
//        hostDOBeforeServiceUpdate.setContainer(YesOrNoEnum.NO.getCode());
//        hostDOBeforeServiceUpdate.setDepartment("department_test");
//        hostDOBeforeServiceUpdate.setHostName("测试主机_1");
//        hostDOBeforeServiceUpdate.setIp("192.168.0.1");
//        hostDOBeforeServiceUpdate.setMachineZone("gz01");
//        hostDOBeforeServiceUpdate.setParentHostName(StringUtils.EMPTY);
//        Result<Long> result = hostManageService.createHost(hostDOBeforeServiceUpdate, null);
//        assert result.success();
//        hostDOBeforeServiceUpdate.setId(result.getData());
//
//        hostDOAfterServiceUpdate1 = new HostDO();
//        hostDOAfterServiceUpdate1.setContainer(YesOrNoEnum.YES.getCode());
//        hostDOAfterServiceUpdate1.setDepartment("department_test");
//        hostDOAfterServiceUpdate1.setHostName("测试容器_1");
//        hostDOAfterServiceUpdate1.setIp("192.168.0.2");
//        hostDOAfterServiceUpdate1.setMachineZone("gz01");
//        hostDOAfterServiceUpdate1.setParentHostName("测试主机_1");
//        result = hostManageService.createHost(hostDOAfterServiceUpdate1, null);
//        assert result.success();
//        hostDOAfterServiceUpdate1.setId(result.getData());
//
//        hostDOAfterServiceUpdate2 = new HostDO();
//        hostDOAfterServiceUpdate2.setContainer(YesOrNoEnum.YES.getCode());
//        hostDOAfterServiceUpdate2.setDepartment("department_test");
//        hostDOAfterServiceUpdate2.setHostName("测试容器_2");
//        hostDOAfterServiceUpdate2.setIp("192.168.0.3");
//        hostDOAfterServiceUpdate2.setMachineZone("gz01");
//        hostDOAfterServiceUpdate2.setParentHostName("测试主机_1");
//        result = hostManageService.createHost(hostDOAfterServiceUpdate2, null);
//        assert result.success();
//        hostDOAfterServiceUpdate2.setId(result.getData());
//
//        ServiceDO serviceDO1 = new ServiceDO();
//        serviceDO1.setServicename("service_1");
//        serviceDO1.setHostIdList(Arrays.asList(hostDOBeforeServiceUpdate.getId(), hostDOAfterServiceUpdate1.getId(), hostDOAfterServiceUpdate2.getId()));
//        result = serviceManageService.createService(serviceDO1, null);
//        Long serviceId1 = result.getData();
//
//        ServiceDO serviceDO2 = new ServiceDO();
//        serviceDO2.setServicename("service_2");
//        serviceDO2.setHostIdList(Arrays.asList(hostDOAfterServiceUpdate1.getId(), hostDOAfterServiceUpdate2.getId()));
//        result = serviceManageService.createService(serviceDO2, null);
//        Long serviceId2 = result.getData();
//
//    }
//
//    /**
//     * 服务更新前关联的主机对象
//     */
//    private HostDO hostDOBeforeServiceUpdate;
//    /**
//     * 服务更新后关联的主机对象1
//     */
//    private HostDO hostDOAfterServiceUpdate1;
//    /**
//     * 服务更新后关联的主机对象1
//     */
//    private HostDO hostDOAfterServiceUpdate2;
//    /**
//     * 更新前服务对象
//     */
//    private ServiceDO serviceDOCreated;
//    /**
//     * 服务关联的日志采集任务对象1
//     */
//    private LogCollectTaskDO relationLogCollectTaskDO1;
//    /**
//     * 服务关联的日志采集任务对象2
//     */
//    private LogCollectTaskDO relationLogCollectTaskDO2;
//
//    @Test
//    public void testServiceDO2ServicePO() {
//
//        ServiceDO serviceDO = new ServiceDO();
//        serviceDO.setId(new Random().nextLong());
//        serviceDO.setServicename(UUID.randomUUID().toString());
//        serviceDO.setHostIdList(Arrays.asList(1L, 2L, 9L));
//
//        ServiceVO serviceVO = ConvertUtil.obj2Obj(serviceDO, ServiceVO.class);
//
//        assert serviceVO.getId().equals(serviceDO.getId());
//        assert serviceVO.getServicename().equals(serviceDO.getServicename());
//
//    }

    @Test
    public void serviceQueryWithSortTest() {
        ServicePaginationQueryConditionDO query = new ServicePaginationQueryConditionDO();
        query.setSortColumn("service_name");
        query.setAsc(true);
        query.setLimitFrom(0);
        query.setLimitSize(20);
        List<ServicePaginationRecordDO> records = serviceManageService.paginationQueryByConditon(query);
        List<String> names = records.stream().map(ServicePaginationRecordDO::getServicename).collect(Collectors.toList());
        LOGGER.info("{}", names);
        List<String> sorted = names.stream().sorted().collect(Collectors.toList());
        Assertions.assertEquals(names, sorted);
    }

//    @Test
//    public void testPullServiceListFromRemoteAndMergeInLocal() {
//        serviceManageService.pullServiceListFromRemoteAndMergeInLocal();
//        hostManageService.pullHostListFromRemoteAndMergeInLocal();
//    }

}
