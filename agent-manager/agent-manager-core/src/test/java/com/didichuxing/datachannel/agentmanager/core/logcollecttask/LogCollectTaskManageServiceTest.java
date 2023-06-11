package com.didichuxing.datachannel.agentmanager.core.logcollecttask;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask.LogRecordVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask.LogSliceRuleVO;
import com.didichuxing.datachannel.agentmanager.core.ApplicationTests;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.manage.extension.LogCollectTaskManageServiceExtension;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Rollback
public class LogCollectTaskManageServiceTest extends ApplicationTests {

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private LogCollectTaskManageServiceExtension logCollectTaskManageServiceExtension;

    @Test
    public void testSlice() {
        String content = "[2022-04-12 11:25:08.920] [main] INFO  logger2 -    42512997 1649733908920 7\n" +
                "[2022-04-12 11:25:08.921] [main] INFO  logger2 -    42512998 1649733908921 8\n" +
                "[2022-04-12 11:25:08.924] [main] INFO  logger2 -    42512999 1649733908924 9\n" +
                "[2022-04-12 11:25:08.926] [main] INFO  logger2 -    42513000 1649733908926 0\n" +
                "[2022-04-12 11:25:08.928] [main] INFO  logger2 -    42513001 1649733908928 1\n" +
                "[2022-04-12 11:25:08.929] [main] INFO  logger2 -    42513002 1649733908929 2\n" +
                "[2022-04-12 11:25:08.931] [main] INFO  logger2 -    42513003 1649733908931 3\n" +
                "[2022-04-12 11:25:08.934] [main] INFO  logger2 -    42513004 1649733908934 4\n" +
                "[2022-04-12 11:25:08.936] [main] INFO  logger2 -    42513005 1649733908936 5\n" +
                "[2022-04-12 11:25:08.937] [main] INFO  logger2 -    42513006 1649733908937 6\n";

        String sliceTimestampFormat = "yyyy-MM-dd HH:mm:ss.SSS";
        String sliceTimestampPrefixString = "[";
        Integer sliceTimestampPrefixStringIndex = 0;
        List<LogRecordVO> logRecordVOList = logCollectTaskManageServiceExtension.slice(
                content,
                sliceTimestampFormat,
                sliceTimestampPrefixString,
                sliceTimestampPrefixStringIndex
        );
        for (LogRecordVO logRecordVO : logRecordVOList) {
            System.err.println(JSON.toJSONString(logRecordVO));
            System.err.println("============================================================================");
        }

    }

    @Test
    public void testGetSliceRule() {
        String info = "23123[qdsadq[qweqw[4[2022-05-27 19:02:47!110] [main] INFO  logger2 -  1652852225 1653649367110     \n" +
                "2022-05-27";
        Integer startIndex = info.indexOf("2022-05-27 19:02:47.110");
        Integer endIndex = startIndex + "2022-05-27 19:02:47.110".length() - 1;
        assert "2022-05-27 19:02:47.110".equals(info.substring(startIndex, endIndex+1));
        LogSliceRuleVO logSliceRuleVO = logCollectTaskManageService.getSliceRule(info, startIndex, endIndex);
        assert null != logSliceRuleVO;
    }
//    /**
//     * 测试日志采集任务修改服务接口 case：删除所有对应文件采集路径集
//     */
//    @Test
//    public void testUpdateLogCollectTaskRemoveAllAndAddRelationCollectFilePathList() {
//
//        /*
//         * 初始化数据
//         */
//        initData();
//
//        /*
//         * 更新 logcollecttask
//         */
//        Result<LogCollectTaskDO> createdLogCollectTaskDOResult = logCollectTaskManageService.getById(logCollectTaskDO.getId());
//        assert createdLogCollectTaskDOResult.success();
//        LogCollectTaskDO createdLogCollectTaskDO = createdLogCollectTaskDOResult.getData();
//        assert createdLogCollectTaskDO.getConfigurationVersion() == 0;
//        createdLogCollectTaskDO.setId(logCollectTaskDO.getId());
//        createdLogCollectTaskDO.setLogCollectTaskType(LogCollectTaskTypeEnum.NORMAL_COLLECT.getCode());
//        createdLogCollectTaskDO.setCollectEndTimeBusiness(-1L);
//        createdLogCollectTaskDO.setOldDataFilterType(1);
//        createdLogCollectTaskDO.setLimitPriority(LogCollectTaskLimitPriorityLevelEnum.MIDDLE.getCode());
//        createdLogCollectTaskDO.setSendTopic(UUID.randomUUID().toString());
//        createdLogCollectTaskDO.setLogCollectTaskExecuteTimeoutMs(99999L);
//        createdLogCollectTaskDO.setKafkaClusterId(logdataReceiverDOAfterUpdate.getId());
//        createdLogCollectTaskDO.setHostFilterRuleLogicJsonString(UUID.randomUUID().toString());
//        createdLogCollectTaskDO.setAdvancedConfigurationJsonString(UUID.randomUUID().toString());
//        createdLogCollectTaskDO.setServiceIdList(Arrays.asList(serviceDOAfterUpdate1.getId(), serviceDOAfterUpdate2.getId()));
//        createdLogCollectTaskDO.setDirectoryLogCollectPathList(null);
//        createdLogCollectTaskDO.setFileLogCollectPathList(Arrays.asList(fileLogCollectPathDOAfterUpdate2, fileLogCollectPathDOAfterUpdate1));
//        Date logCollectTaskFinishTime = new Date(System.currentTimeMillis());
//        createdLogCollectTaskDO.setLogCollectTaskFinishTime(logCollectTaskFinishTime);
//        Result result = logCollectTaskManageService.updateLogCollectTask(createdLogCollectTaskDO, null);
//        assert result.success();
//
//        Result<LogCollectTaskDO> updatedLogCollectTaskDOResult = logCollectTaskManageService.getById(createdLogCollectTaskDO.getId());
//        assert updatedLogCollectTaskDOResult.success();
//        /*
//         * 校验更新结果是否正确
//         */
//        LogCollectTaskDO updatedLogCollectTaskDO = updatedLogCollectTaskDOResult.getData();
//        assert updatedLogCollectTaskDO.getConfigurationVersion() == 1;
//
//        assert updatedLogCollectTaskDO.getLogCollectTaskType().equals(createdLogCollectTaskDO.getLogCollectTaskType());
//        assert updatedLogCollectTaskDO.getCollectEndTimeBusiness().equals(createdLogCollectTaskDO.getCollectEndTimeBusiness());
//        assert updatedLogCollectTaskDO.getLogCollectTaskStatus().equals(createdLogCollectTaskDO.getLogCollectTaskStatus());
//        assert updatedLogCollectTaskDO.getOldDataFilterType().equals(createdLogCollectTaskDO.getOldDataFilterType());
//        assert updatedLogCollectTaskDO.getLimitPriority().equals(createdLogCollectTaskDO.getLimitPriority());
//        assert updatedLogCollectTaskDO.getSendTopic().equals(createdLogCollectTaskDO.getSendTopic());
//        assert updatedLogCollectTaskDO.getKafkaClusterId().equals(createdLogCollectTaskDO.getKafkaClusterId());
//        assert updatedLogCollectTaskDO.getLogCollectTaskExecuteTimeoutMs().equals(createdLogCollectTaskDO.getLogCollectTaskExecuteTimeoutMs());
//        assert updatedLogCollectTaskDO.getHostFilterRuleLogicJsonString().equals(createdLogCollectTaskDO.getHostFilterRuleLogicJsonString());
//        assert updatedLogCollectTaskDO.getAdvancedConfigurationJsonString().equals(createdLogCollectTaskDO.getAdvancedConfigurationJsonString());
//        assert updatedLogCollectTaskDO.getLogCollectTaskFinishTime() != null;
//        assert ListCompareUtil.listEquals(updatedLogCollectTaskDO.getServiceIdList(), createdLogCollectTaskDO.getServiceIdList());
//        assert CollectionUtils.isEmpty(updatedLogCollectTaskDO.getDirectoryLogCollectPathList());
//        assert updatedLogCollectTaskDO.getOperator().equals(Constant.getOperator(null));
//
//        //compate file log path
//        assert updatedLogCollectTaskDO.getFileLogCollectPathList().size() == createdLogCollectTaskDO.getFileLogCollectPathList().size();
//        Map<String, FileLogCollectPathDO> fileLogCollectPathDOMap = new HashMap<>();
//        for (FileLogCollectPathDO fileLogCollectPathDO : updatedLogCollectTaskDO.getFileLogCollectPathList()) {
//            fileLogCollectPathDOMap.put(fileLogCollectPathDO.getPath(), fileLogCollectPathDO);
//        }
//        for (FileLogCollectPathDO createdFileLogCollectPathDO : createdLogCollectTaskDO.getFileLogCollectPathList()) {
//            FileLogCollectPathDO updatedFileLogCollectPathDO = fileLogCollectPathDOMap.get(createdFileLogCollectPathDO.getPath());
//            assert updatedFileLogCollectPathDO.getPath().equals(createdFileLogCollectPathDO.getPath());
//            assert updatedFileLogCollectPathDO.getMaxBytesPerLogEvent().equals(createdFileLogCollectPathDO.getMaxBytesPerLogEvent());
//            assert updatedFileLogCollectPathDO.getLogContentSliceRuleLogicJsonString().equals(createdFileLogCollectPathDO.getLogContentSliceRuleLogicJsonString());
//            assert updatedFileLogCollectPathDO.getFdOffsetExpirationTimeMs().equals(createdFileLogCollectPathDO.getFdOffsetExpirationTimeMs());
//            assert updatedFileLogCollectPathDO.getCharset().equals(createdFileLogCollectPathDO.getCharset());
//            assert updatedFileLogCollectPathDO.getFileNameSuffixMatchRuleLogicJsonString().equals(createdFileLogCollectPathDO.getFileNameSuffixMatchRuleLogicJsonString());
//            assert updatedFileLogCollectPathDO.getCollectDelayThresholdMs().equals(createdFileLogCollectPathDO.getCollectDelayThresholdMs());
//        }
//    }
//
//    /**
//     * 测试日志采集任务修改服务接口 case：更新 & 添加一个对应文件采集路径集
//     */
//    @Test
//    public void testUpdateLogCollectTaskUpdateAndAddRelationCollectFilePathList() {
//
//        /*
//         * 初始化数据
//         */
//        initData();
//
//        /*
//         * 更新 logcollecttask
//         */
//        Result<LogCollectTaskDO> createdLogCollectTaskDOResult = logCollectTaskManageService.getById(logCollectTaskDO.getId());
//        assert createdLogCollectTaskDOResult.success();
//        LogCollectTaskDO createdLogCollectTaskDO = createdLogCollectTaskDOResult.getData();
//        assert createdLogCollectTaskDO.getConfigurationVersion() == 0;
//        createdLogCollectTaskDO.setId(logCollectTaskDO.getId());
//        createdLogCollectTaskDO.setLogCollectTaskType(LogCollectTaskTypeEnum.NORMAL_COLLECT.getCode());
//        createdLogCollectTaskDO.setCollectEndTimeBusiness(-1L);
//        createdLogCollectTaskDO.setOldDataFilterType(1);
//        createdLogCollectTaskDO.setLimitPriority(LogCollectTaskLimitPriorityLevelEnum.MIDDLE.getCode());
//        createdLogCollectTaskDO.setSendTopic(UUID.randomUUID().toString());
//        createdLogCollectTaskDO.setLogCollectTaskExecuteTimeoutMs(99999L);
//        createdLogCollectTaskDO.setKafkaClusterId(logdataReceiverDOAfterUpdate.getId());
//        createdLogCollectTaskDO.setHostFilterRuleLogicJsonString(UUID.randomUUID().toString());
//        createdLogCollectTaskDO.setAdvancedConfigurationJsonString(UUID.randomUUID().toString());
//        createdLogCollectTaskDO.setServiceIdList(Arrays.asList(serviceDOAfterUpdate1.getId(), serviceDOAfterUpdate2.getId()));
//        createdLogCollectTaskDO.setDirectoryLogCollectPathList(null);
//        assert 1 == createdLogCollectTaskDO.getFileLogCollectPathList().size();
//        fileLogCollectPathDOAfterUpdate1 = createdLogCollectTaskDO.getFileLogCollectPathList().get(0);
//        fileLogCollectPathDOAfterUpdate1.setFileNameSuffixMatchRuleLogicJsonString(UUID.randomUUID().toString());
//        fileLogCollectPathDOAfterUpdate1.setCharset(UUID.randomUUID().toString());
//        fileLogCollectPathDOAfterUpdate1.setFdOffsetExpirationTimeMs(new Random().nextLong());
//        fileLogCollectPathDOAfterUpdate1.setMaxBytesPerLogEvent(new Random().nextLong());
//        fileLogCollectPathDOAfterUpdate1.setLogContentSliceRuleLogicJsonString(UUID.randomUUID().toString());
//        fileLogCollectPathDOAfterUpdate1.setCollectDelayThresholdMs(new Random().nextLong());
//        createdLogCollectTaskDO.setFileLogCollectPathList(Arrays.asList(fileLogCollectPathDOAfterUpdate2, fileLogCollectPathDOAfterUpdate1));
//        Result result = logCollectTaskManageService.updateLogCollectTask(createdLogCollectTaskDO, null);
//        assert result.success();
//
//        Result<LogCollectTaskDO> updatedLogCollectTaskDOResult = logCollectTaskManageService.getById(createdLogCollectTaskDO.getId());
//        assert updatedLogCollectTaskDOResult.success();
//        /*
//         * 校验更新结果是否正确
//         */
//        LogCollectTaskDO updatedLogCollectTaskDO = updatedLogCollectTaskDOResult.getData();
//        assert updatedLogCollectTaskDO.getConfigurationVersion() == 1;
//
//        assert updatedLogCollectTaskDO.getLogCollectTaskType().equals(createdLogCollectTaskDO.getLogCollectTaskType());
//        assert updatedLogCollectTaskDO.getCollectEndTimeBusiness().equals(createdLogCollectTaskDO.getCollectEndTimeBusiness());
//        assert updatedLogCollectTaskDO.getLogCollectTaskStatus().equals(createdLogCollectTaskDO.getLogCollectTaskStatus());
//        assert updatedLogCollectTaskDO.getOldDataFilterType().equals(createdLogCollectTaskDO.getOldDataFilterType());
//        assert updatedLogCollectTaskDO.getLimitPriority().equals(createdLogCollectTaskDO.getLimitPriority());
//        assert updatedLogCollectTaskDO.getSendTopic().equals(createdLogCollectTaskDO.getSendTopic());
//        assert updatedLogCollectTaskDO.getKafkaClusterId().equals(createdLogCollectTaskDO.getKafkaClusterId());
//        assert updatedLogCollectTaskDO.getLogCollectTaskExecuteTimeoutMs().equals(createdLogCollectTaskDO.getLogCollectTaskExecuteTimeoutMs());
//        assert updatedLogCollectTaskDO.getHostFilterRuleLogicJsonString().equals(createdLogCollectTaskDO.getHostFilterRuleLogicJsonString());
//        assert updatedLogCollectTaskDO.getAdvancedConfigurationJsonString().equals(createdLogCollectTaskDO.getAdvancedConfigurationJsonString());
//        assert ListCompareUtil.listEquals(updatedLogCollectTaskDO.getServiceIdList(), createdLogCollectTaskDO.getServiceIdList());
//        assert CollectionUtils.isEmpty(updatedLogCollectTaskDO.getDirectoryLogCollectPathList());
//
//        //compate file log path
//        assert updatedLogCollectTaskDO.getFileLogCollectPathList().size() == createdLogCollectTaskDO.getFileLogCollectPathList().size();
//        Map<String, FileLogCollectPathDO> fileLogCollectPathDOMap = new HashMap<>();
//        for (FileLogCollectPathDO fileLogCollectPathDO : updatedLogCollectTaskDO.getFileLogCollectPathList()) {
//            fileLogCollectPathDOMap.put(fileLogCollectPathDO.getPath(), fileLogCollectPathDO);
//        }
//        for (FileLogCollectPathDO createdFileLogCollectPathDO : createdLogCollectTaskDO.getFileLogCollectPathList()) {
//            FileLogCollectPathDO updatedFileLogCollectPathDO = fileLogCollectPathDOMap.get(createdFileLogCollectPathDO.getPath());
//            assert updatedFileLogCollectPathDO.getPath().equals(createdFileLogCollectPathDO.getPath());
//            assert updatedFileLogCollectPathDO.getMaxBytesPerLogEvent().equals(createdFileLogCollectPathDO.getMaxBytesPerLogEvent());
//            assert updatedFileLogCollectPathDO.getLogContentSliceRuleLogicJsonString().equals(createdFileLogCollectPathDO.getLogContentSliceRuleLogicJsonString());
//            assert updatedFileLogCollectPathDO.getFdOffsetExpirationTimeMs().equals(createdFileLogCollectPathDO.getFdOffsetExpirationTimeMs());
//            assert updatedFileLogCollectPathDO.getCharset().equals(createdFileLogCollectPathDO.getCharset());
//            assert updatedFileLogCollectPathDO.getFileNameSuffixMatchRuleLogicJsonString().equals(createdFileLogCollectPathDO.getFileNameSuffixMatchRuleLogicJsonString());
//            assert updatedFileLogCollectPathDO.getCollectDelayThresholdMs().equals(createdFileLogCollectPathDO.getCollectDelayThresholdMs());
//        }
//    }
//
//    /**
//     * 测试日志采集任务删除流程
//     */
//    @Test
//    public void testRemoveLogCollectTask() {
//
//        initData();
//        Result result = logCollectTaskManageService.deleteLogCollectTask(logCollectTaskDO.getId(), null);
//        assert result.success();
//
//        //校验日志采集任务 & 服务关联关系是否已删除
//        Result<List<ServiceDO>> serviceDOResult = serviceManageService.getServicesByLogCollectTaskId(logCollectTaskDO.getId());
//        assert CollectionUtils.isEmpty(serviceDOResult.getData());
//
//        //校验日志采集任务关联的日志采集路径对象集是否已删除
//        assert CollectionUtils.isEmpty(directoryLogCollectPathManageService.getAllDirectoryLogCollectPathByLogCollectTaskId(logCollectTaskDO.getId()).getData());
//        assert CollectionUtils.isEmpty(fileLogCollectPathManageService.getAllFileLogCollectPathByLogCollectTaskId(logCollectTaskDO.getId()).getData());
//
//        //校验日志采集任务关联的日志采集任务健康信息是否已删除
//        Result<LogCollectTaskHealthDO> logCollectTaskHealthDOResult = logCollectTaskHealthManageService.getByLogCollectTaskId(logCollectTaskDO.getId());
//        assert logCollectTaskHealthDOResult.getData() == null;
//
//        //校验日志采集任务信息是否已删除
//        assert logCollectTaskManageService.getById(logCollectTaskDO.getId()).getData() == null;
//
//    }

    /**
     * 日志采集任务更新前对应指标流 receiver 对象信息
     */
    private ReceiverDO logdataReceiverDOBeforeUpdate;
    /**
     * 日志采集任务更新后对应指标流 receiver 对象信息
     */
    private ReceiverDO logdataReceiverDOAfterUpdate;
    /**
     * 日志采集任务更新前对应服务对象
     */
    private ServiceDO serviceDOBeforeUpdate;
    /**
     * 日志采集任务更新后对应服务对象1
     */
    private ServiceDO serviceDOAfterUpdate1;
    /**
     * 日志采集任务更新后对应服务对象2
     */
    private ServiceDO serviceDOAfterUpdate2;
    /**
     * 已存在日志采集任务
     */
    private LogCollectTaskDO logCollectTaskDO;
    /**
     * 日志采集任务更新后对应文件型采集路径对象1
     */
    private FileLogCollectPathDO fileLogCollectPathDOAfterUpdate1;
    /**
     * 日志采集任务更新后对应文件型采集路径对象2
     */
    private FileLogCollectPathDO fileLogCollectPathDOAfterUpdate2;

    /**
     * 创建日志采集任务对应接收端对象、服务对象、日志采集任务对象
     */
//    private void initData() {
//
//        /*
//         * 构建日志采集任务更新前对应接收端对象
//         */
//        logdataReceiverDOBeforeUpdate = new ReceiverDO();
//        logdataReceiverDOBeforeUpdate.setKafkaClusterBrokerConfiguration(UUID.randomUUID().toString());
//        logdataReceiverDOBeforeUpdate.setKafkaClusterName(UUID.randomUUID().toString());
//        logdataReceiverDOBeforeUpdate.setKafkaClusterProducerInitConfiguration(UUID.randomUUID().toString());
//        Result<Long> result = kafkaClusterManageService.createKafkaCluster(logdataReceiverDOBeforeUpdate, null);
//        assert result.success();
//        logdataReceiverDOBeforeUpdate.setId(result.getData());
//
//        /*
//         * 构建日志采集任务更新后对应接收端对象
//         */
//        logdataReceiverDOAfterUpdate = new ReceiverDO();
//        logdataReceiverDOAfterUpdate.setKafkaClusterBrokerConfiguration(UUID.randomUUID().toString());
//        logdataReceiverDOAfterUpdate.setKafkaClusterName(UUID.randomUUID().toString());
//        logdataReceiverDOAfterUpdate.setKafkaClusterProducerInitConfiguration(UUID.randomUUID().toString());
//        result = kafkaClusterManageService.createKafkaCluster(logdataReceiverDOAfterUpdate, null);
//        assert result.success();
//        logdataReceiverDOAfterUpdate.setId(result.getData());
//
//        /*
//         * 构建日志采集任务更新前对应服务对象
//         */
//        serviceDOBeforeUpdate = new ServiceDO();
//        serviceDOBeforeUpdate.setServicename("serviceDOBeforeUpdate");
//        serviceDOBeforeUpdate.setHostIdList(Arrays.asList(1L,2L,3L));
//        result = serviceManageService.createService(serviceDOBeforeUpdate, Constant.getOperator(null));
//        assert result.success();
//        serviceDOBeforeUpdate.setId(result.getData());
//
//        /*
//         * 构建日志采集任务更新后对应服务对象
//         */
//        serviceDOAfterUpdate1 = new ServiceDO();
//        serviceDOAfterUpdate1.setServicename("serviceDOAfterUpdate1");
//        serviceDOAfterUpdate1.setHostIdList(Arrays.asList(4L,5L));
//        result = serviceManageService.createService(serviceDOAfterUpdate1, Constant.getOperator(null));
//        assert result.success();
//        serviceDOAfterUpdate1.setId(result.getData());
//
//        serviceDOAfterUpdate2 = new ServiceDO();
//        serviceDOAfterUpdate2.setServicename("serviceDOAfterUpdate2");
//        serviceDOAfterUpdate2.setHostIdList(Arrays.asList(99L));
//        result = serviceManageService.createService(serviceDOAfterUpdate2, Constant.getOperator(null));
//        assert result.success();
//        serviceDOAfterUpdate2.setId(result.getData());
//
//        /*
//         * 构建日志采集任务对象
//         */
//        logCollectTaskDO = new LogCollectTaskDO();
//        logCollectTaskDO.setAdvancedConfigurationJsonString("advancedConfigurationJsonString");
//        logCollectTaskDO.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTaskDO.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTaskDO.setHostFilterRuleLogicJsonString("hostFilterRuleLogicJsonString");
//        logCollectTaskDO.setKafkaClusterId(logdataReceiverDOBeforeUpdate.getId());
//        logCollectTaskDO.setLimitPriority(LogCollectTaskLimitPriorityLevelEnum.HIGH.getCode());
//        logCollectTaskDO.setLogCollectTaskName("测试日志采集任务_5");
//        logCollectTaskDO.setLogCollectTaskRemark("测试日志采集任务_remark_" + UUID.randomUUID().toString());
//        logCollectTaskDO.setLogCollectTaskType(LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode());
//        logCollectTaskDO.setSendTopic("topic_test_" + UUID.randomUUID().toString());
//        logCollectTaskDO.setLogCollectTaskExecuteTimeoutMs(new Random().nextLong());
//        logCollectTaskDO.setOldDataFilterType(2);
//        logCollectTaskDO.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTaskDO.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTaskDO.setLogCollectTaskExecuteTimeoutMs(new Random().nextLong());
//        logCollectTaskDO.setLogContentFilterRuleLogicJsonString(UUID.randomUUID().toString());
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
//        logCollectTaskDO.setDirectoryLogCollectPathList(directoryLogCollectPathList5);
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
//        logCollectTaskDO.setFileLogCollectPathList(fileLogCollectPathList5);
//        logCollectTaskDO.setServiceIdList(Arrays.asList(serviceDOBeforeUpdate.getId()));
//
//        result = logCollectTaskManageService.createLogCollectTask(logCollectTaskDO, null);
//        assert result.success();
//        logCollectTaskDO.setId(result.getData());
//
//        /*
//         * 构建更新后日志采集任务对象对应文件型日志采集路径集
//         */
//        fileLogCollectPathDOAfterUpdate1 = new FileLogCollectPathDO();
//        fileLogCollectPathDOAfterUpdate1.setCollectDelayThresholdMs(new Random().nextLong());
//        fileLogCollectPathDOAfterUpdate1.setLogContentSliceRuleLogicJsonString(UUID.randomUUID().toString());
//        fileLogCollectPathDOAfterUpdate1.setMaxBytesPerLogEvent(new Random().nextLong());
//        fileLogCollectPathDOAfterUpdate1.setFdOffsetExpirationTimeMs(new Random().nextLong());
//        fileLogCollectPathDOAfterUpdate1.setPath(UUID.randomUUID().toString());
//        fileLogCollectPathDOAfterUpdate1.setCharset(UUID.randomUUID().toString());
//        fileLogCollectPathDOAfterUpdate1.setFileNameSuffixMatchRuleLogicJsonString(UUID.randomUUID().toString());
//
//        fileLogCollectPathDOAfterUpdate2 = new FileLogCollectPathDO();
//        fileLogCollectPathDOAfterUpdate2.setCollectDelayThresholdMs(new Random().nextLong());
//        fileLogCollectPathDOAfterUpdate2.setLogContentSliceRuleLogicJsonString(UUID.randomUUID().toString());
//        fileLogCollectPathDOAfterUpdate2.setMaxBytesPerLogEvent(new Random().nextLong());
//        fileLogCollectPathDOAfterUpdate2.setFdOffsetExpirationTimeMs(new Random().nextLong());
//        fileLogCollectPathDOAfterUpdate2.setPath(UUID.randomUUID().toString());
//        fileLogCollectPathDOAfterUpdate2.setCharset(UUID.randomUUID().toString());
//        fileLogCollectPathDOAfterUpdate2.setFileNameSuffixMatchRuleLogicJsonString(UUID.randomUUID().toString());
//
//    }
//
//    @Test
//    public void testPaginationQuery() throws ParseException {
//        initPaginationQueryData();
//        LogCollectTaskPaginationQueryConditionDO logCollectTaskPaginationQueryConditionDO = new LogCollectTaskPaginationQueryConditionDO();
//        logCollectTaskPaginationQueryConditionDO.setLogCollectTaskName("日志采集任务_");
//        logCollectTaskPaginationQueryConditionDO.setLogCollectTaskHealthLevel(LogCollectTaskHealthLevelEnum.GREEN.getCode());
//        logCollectTaskPaginationQueryConditionDO.setLogCollectTaskType(LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode());
//        Result<ServiceDO> relationService1 = serviceManageService.getServiceByServiceName("测试服务_1");
//        Result<ServiceDO> relationService2 = serviceManageService.getServiceByServiceName("测试服务_2");
//        Result<ServiceDO> relationService3 = serviceManageService.getServiceByServiceName("测试服务_3");
//        Result<ServiceDO> relationService4 = serviceManageService.getServiceByServiceName("测试服务_4");
//        Result<ServiceDO> relationService5 = serviceManageService.getServiceByServiceName("测试服务_5");
//        logCollectTaskPaginationQueryConditionDO.setServiceId(relationService4.getData().getId());
//        logCollectTaskPaginationQueryConditionDO.setLimitFrom(0);
//        logCollectTaskPaginationQueryConditionDO.setLimitSize(10000);
//        logCollectTaskPaginationQueryConditionDO.setCreateTimeStart(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-12-28 00:00:00"));
//        logCollectTaskPaginationQueryConditionDO.setCreateTimeEnd(new Date(System.currentTimeMillis()));
//        Result<List<LogCollectTaskPaginationRecordDO>> result = logCollectTaskManageService.paginationQueryByConditon(logCollectTaskPaginationQueryConditionDO);
//        assert result.getData().size() == 2;
//        assert StringUtils.isNotBlank(result.getData().get(0).getSendTopic());
//        assert result.getData().get(0).getLogCollectTaskStatus().equals(YesOrNoEnum.YES.getCode());
//    }
//
//    @Test
//    public void tesCountQuery() throws ParseException {
//        initPaginationQueryData();
//        LogCollectTaskPaginationQueryConditionDO logCollectTaskPaginationQueryConditionDO = new LogCollectTaskPaginationQueryConditionDO();
//        logCollectTaskPaginationQueryConditionDO.setLogCollectTaskName("日志采集任务_");
//        logCollectTaskPaginationQueryConditionDO.setLogCollectTaskHealthLevel(LogCollectTaskHealthLevelEnum.GREEN.getCode());
//        logCollectTaskPaginationQueryConditionDO.setLogCollectTaskType(LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode());
//        Result<ServiceDO> relationService1 = serviceManageService.getServiceByServiceName("测试服务_1");
//        Result<ServiceDO> relationService2 = serviceManageService.getServiceByServiceName("测试服务_2");
//        Result<ServiceDO> relationService3 = serviceManageService.getServiceByServiceName("测试服务_3");
//        Result<ServiceDO> relationService4 = serviceManageService.getServiceByServiceName("测试服务_4");
//        Result<ServiceDO> relationService5 = serviceManageService.getServiceByServiceName("测试服务_5");
//        logCollectTaskPaginationQueryConditionDO.setServiceId(relationService4.getData().getId());
//        logCollectTaskPaginationQueryConditionDO.setCreateTimeStart(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-12-28 00:00:00"));
//        logCollectTaskPaginationQueryConditionDO.setCreateTimeEnd(new Date(System.currentTimeMillis()));
//        Result<Integer> result = logCollectTaskManageService.queryCountByCondition(logCollectTaskPaginationQueryConditionDO);
//        assert result.getData() == 2;
//    }

//    private void initPaginationQueryData() throws ParseException {
//        initHostList();
//        initServiceAndServiceHostRelationList();
//        initReceivers();
//        initLogCollectTaskAndServiceLogCollectTaskRelationList();
//        initAgentCollectHostOnly();
//    }

    private static final Long metricsKafkaClusterId = 1L;
    private static final Long errorLogsKafkaClusterId = 2L;
    private static final Long logdataKafkaClusterId = 3L;
    /**
     * 初始化3个kafka集群，分别用于 metrics流、errorlogs流、log data 流
     */
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
//    private String initAgentCollectHostOnly() {
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
//        AgentDO agent = new AgentDO();
//        agent.setAdvancedConfigurationJsonString("advancedConfigurationJsonString");
//        agent.setByteLimitThreshold(1024 * 1024 * 9L);
//        agent.setCollectType(AgentCollectTypeEnum.COLLECT_HOST_ONLY.getCode());
//        agent.setCpuLimitThreshold(50);
//        agent.setHostName("测试主机_1");
//        agent.setIp("192.168.0.1");
//        agent.setAgentVersionId(agentVersionPO.getId());
//        agent.setHealthLevel(AgentHealthLevelEnum.GREEN.getCode());
//        agent.setMetricsSendReceiverId(kafkaClusterManageService.getKafkaClusterByKafkaClusterId(metricsKafkaClusterId).getData().getId());
//        agent.setMetricsSendTopic("topic_metrics");
//        agent.setErrorLogsSendReceiverId(kafkaClusterManageService.getKafkaClusterByKafkaClusterId(errorLogsKafkaClusterId).getData().getId());
//        agent.setErrorLogsSendTopic("topic_error_logs");
//
//        Long result = agentManageService.createAgent(agent,true, null);
//        assert result > 0;
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
//        logCollectTask1.setSendTopic("topic_test_" + UUID.randomUUID().toString());
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
//        logCollectTask2.setSendTopic("topic_test_" + UUID.randomUUID().toString());
//        logCollectTask2.setLogCollectTaskExecuteTimeoutMs(new Random().nextLong());
//        logCollectTask2.setOldDataFilterType(2);
//        logCollectTask2.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTask2.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTask2.setLogCollectTaskExecuteTimeoutMs(new Random().nextLong());
//        logCollectTask2.setLogContentFilterRuleLogicJsonString(UUID.randomUUID().toString());
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
//        logCollectTask3.setSendTopic("topic_test_" + UUID.randomUUID().toString());
//        logCollectTask3.setOldDataFilterType(2);
//        logCollectTask3.setLogCollectTaskExecuteTimeoutMs(new Random().nextLong());
//        logCollectTask3.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTask3.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTask3.setLogContentFilterRuleLogicJsonString(UUID.randomUUID().toString());
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
//        logCollectTask4.setSendTopic("topic_test_" + UUID.randomUUID().toString());
//        logCollectTask4.setLogCollectTaskExecuteTimeoutMs(new Random().nextLong());
//        logCollectTask4.setOldDataFilterType(2);
//        logCollectTask4.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTask4.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTask4.setLogContentFilterRuleLogicJsonString(UUID.randomUUID().toString());
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
//        logCollectTask5.setSendTopic("topic_test_" + UUID.randomUUID().toString());
//        logCollectTask5.setLogCollectTaskExecuteTimeoutMs(new Random().nextLong());
//        logCollectTask5.setOldDataFilterType(2);
//        logCollectTask5.setCollectStartTimeBusiness(System.currentTimeMillis());
//        logCollectTask5.setCollectEndTimeBusiness(System.currentTimeMillis());
//        logCollectTask5.setLogContentFilterRuleLogicJsonString(UUID.randomUUID().toString());
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
//    public void testUpdateInitialLogCollectorTaskHealth() {
//
//        /*
//         * 初始化数据
//         */
//        initData();
//
//        Result<LogCollectTaskHealthDO> result = logCollectTaskHealthManageService.getByLogCollectTaskId(logCollectTaskDO.getId());
//        LogCollectTaskHealthDO logCollectTaskHealthDO = result.getData();
//        logCollectTaskHealthDO.setLogCollectTaskHealthLevel(LogCollectTaskHealthLevelEnum.YELLOW.getCode());
//        String desc = UUID.randomUUID().toString();
//        logCollectTaskHealthDO.setLogCollectTaskHealthDescription(desc);
//        Result updateResult = logCollectTaskHealthManageService.updateLogCollectorTaskHealth(logCollectTaskHealthDO, null);
//        assert updateResult.success();
//        result = logCollectTaskHealthManageService.getByLogCollectTaskId(logCollectTaskDO.getId());
//        assert result.success();
//        assert result.getData().getLogCollectTaskHealthLevel().equals(LogCollectTaskHealthLevelEnum.YELLOW.getCode());
//        assert result.getData().getLogCollectTaskHealthDescription().equals(desc);
//
//    }
//
//    @Test
//    public void testGetAllLogCollectTask2BeHealthCheck() {
//
//        /*
//         * 初始化数据
//         */
//        initData();
//
//        Result<List<LogCollectTaskDO>> result = logCollectTaskManageService.getAllLogCollectTask2HealthCheck();
//
//        assert result.success();
//        assert result.getData().size() > 0;
//
//    }

//    @Test
//    public void checkHealthTest() {
//        List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageService.getAllLogCollectTask2HealthCheck();
//        for (LogCollectTaskDO task : logCollectTaskDOList) {
//            logCollectTaskManageService.checkLogCollectTaskHealth(task);
//        }
//    }

}
