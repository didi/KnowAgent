package com.didichuxing.datachannel.agentmanager.core.receiver;

import com.didichuxing.datachannel.agentmanager.core.ApplicationTests;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;

@Transactional
@Rollback
public class ReceiverManageServiceTest extends ApplicationTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiverManageServiceTest.class);@Autowired
    private KafkaClusterManageService kafkaClusterManageService;

//    @Test
//    public void testReceiverUpdate() {
//        initData();
//        Result<ReceiverDO> receiverDOResult = kafkaClusterManageService.getById(createdReceiverDO.getId());
//        assert receiverDOResult.success();
//        ReceiverDO receiverDOBeforeUpdate = receiverDOResult.getData();
//        receiverDOBeforeUpdate.setKafkaClusterProducerInitConfiguration(UUID.randomUUID().toString());
//        receiverDOBeforeUpdate.setKafkaClusterName(UUID.randomUUID().toString());
//        receiverDOBeforeUpdate.setKafkaClusterId(new Random().nextLong());
//        receiverDOBeforeUpdate.setKafkaClusterBrokerConfiguration(UUID.randomUUID().toString());
//        Result updateKafkaClusterResult = kafkaClusterManageService.updateKafkaCluster(receiverDOBeforeUpdate, null);
//        assert updateKafkaClusterResult.success();
//        receiverDOResult = kafkaClusterManageService.getById(createdReceiverDO.getId());
//        assert receiverDOResult.success();
//        ReceiverDO receiverDOAfterUpdate = receiverDOResult.getData();
//        EqualsBuilder.reflectionEquals(receiverDOAfterUpdate, receiverDOBeforeUpdate, "operator", "modifyTime", "createTime");
//    }
//
//    @Test
//    public void testReceiverRemove() {
//        initData();
//        assert kafkaClusterManageService.deleteKafkaClusterById(createdReceiverDO.getId(), null).success();
//        assert kafkaClusterManageService.getById(createdReceiverDO.getId()).getData() == null;
//    }
//
//    /**
//     * 初始化 receiver 数据
//     */
//    private void initData() {
//
//        createdReceiverDO = new ReceiverDO();
//        createdReceiverDO.setKafkaClusterBrokerConfiguration(UUID.randomUUID().toString());
//        createdReceiverDO.setKafkaClusterId(new Random().nextLong());
//        createdReceiverDO.setKafkaClusterName(UUID.randomUUID().toString());
//        createdReceiverDO.setKafkaClusterProducerInitConfiguration(UUID.randomUUID().toString());
//        Result<Long> createKafkaClusterResult = kafkaClusterManageService.createKafkaCluster(createdReceiverDO, null);
//        assert createKafkaClusterResult.success();
//        createdReceiverDO.setId(createKafkaClusterResult.getData());
//
//    }
//
//    /**
//     * 已存在 receiver 对象
//     */
//    private ReceiverDO createdReceiverDO;
//
//    @Test
//    public void testGetKafkaClusterById() {
////        Long kafkaClusterId = 1L;
////        Result<ReceiverDO> result = kafkaClusterManageService.getKafkaClusterByKafkaClusterId(kafkaClusterId);
////        assert result.success();
////        assert result.getData().getId() > 0;
//    }
//
//    @Test
//    public void testList() throws ParseException {
//        Integer count = initReceiverList();
//        Result<List<ReceiverDO>> result = kafkaClusterManageService.list();
//        assert result.success();
//        assert result.getData().size() == count;
//    }
//
//    @Test
//    public void testCreateKafkaClusters() {
////        ReceiverDO kafkaCluster = new ReceiverDO();
////        kafkaCluster.setKafkaClusterBrokerConfiguration("bbb");
////        kafkaCluster.setKafkaClusterName("bbb");
////        kafkaCluster.setKafkaClusterProducerInitConfiguration("bbb");
////        kafkaCluster.setKafkaClusterId(2L);
////        Result<Long> result = kafkaClusterManageService.createKafkaCluster(kafkaCluster, null);
////        assert result.success();
////        assert result.getData() > 0;
//    }
//
//    @Test
//    public void testModifyKafkaClusters() {
////        ReceiverDO kafkaCluster = new ReceiverDO();
////        kafkaCluster.setKafkaClusterBrokerConfiguration("bbb1");
////        kafkaCluster.setKafkaClusterName("bbb1");
////        kafkaCluster.setKafkaClusterProducerInitConfiguration("bbb1");
////        kafkaCluster.setKafkaClusterId(99999L);
////        kafkaCluster.setId(6L);
////        Result<Void> result = kafkaClusterManageService.updateKafkaCluster(kafkaCluster, null);
////        assert result.success();
//    }
//
//    @Test
//    public void testRemoveKafkaClustersByIdList() {
////        Long id = 6L;
////        Result<Void> result = kafkaClusterManageService.deleteKafkaClusterById(id, null);
////        assert result.success();
//    }
//
//    @Test
//    public void testPaginationQueryByConditon() throws ParseException {
//
//        initReceiverList();
//
//        ReceiverPaginationQueryConditionDO receiverPaginationQueryConditionDOSearhAll = new ReceiverPaginationQueryConditionDO();
//        receiverPaginationQueryConditionDOSearhAll.setLimitFrom(0);
//        receiverPaginationQueryConditionDOSearhAll.setLimitSize(10);
//        List<ReceiverDO> receiverDOList = kafkaClusterManageService.paginationQueryByConditon(receiverPaginationQueryConditionDOSearhAll).getData();
//        assert receiverDOList.size() == 3;
//
//        ReceiverPaginationQueryConditionDO receiverPaginationQueryConditionDOSearchByKafkaClusterName = new ReceiverPaginationQueryConditionDO();
//        receiverPaginationQueryConditionDOSearchByKafkaClusterName.setKafkaClusterName("_cluster");
//        receiverPaginationQueryConditionDOSearchByKafkaClusterName.setLimitFrom(0);
//        receiverPaginationQueryConditionDOSearchByKafkaClusterName.setLimitSize(10);
//        receiverDOList = kafkaClusterManageService.paginationQueryByConditon(receiverPaginationQueryConditionDOSearchByKafkaClusterName).getData();
//        assert receiverDOList.size() == 2;
//
//        ReceiverPaginationQueryConditionDO receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScope = new ReceiverPaginationQueryConditionDO();
//        receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScope.setLimitFrom(0);
//        receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScope.setLimitSize(10);
//        receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScope.setCreateTimeStart(dateFormat.parse(receiverCreateTimeStart));
//        receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScope.setCreateTimeEnd(new Date(System.currentTimeMillis()));
//        receiverDOList = kafkaClusterManageService.paginationQueryByConditon(receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScope).getData();
//        assert receiverDOList.size() == 3;
//
//        ReceiverPaginationQueryConditionDO receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScopeAndKafkaClusterName = new ReceiverPaginationQueryConditionDO();
//        receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScopeAndKafkaClusterName.setLimitFrom(0);
//        receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScopeAndKafkaClusterName.setLimitSize(10);
//        receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScopeAndKafkaClusterName.setCreateTimeStart(dateFormat.parse(receiverCreateTimeStart));
//        receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScopeAndKafkaClusterName.setCreateTimeEnd(new Date(System.currentTimeMillis()));
//        receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScopeAndKafkaClusterName.setKafkaClusterName("_cluster");
//        receiverDOList = kafkaClusterManageService.paginationQueryByConditon(receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScopeAndKafkaClusterName).getData();
//        assert receiverDOList.size() == 2;
//
//    }
//
//    @Test
//    public void testQueryCountByCondition() throws ParseException {
//
//        initReceiverList();
//
//        ReceiverPaginationQueryConditionDO receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScopeAndKafkaClusterName = new ReceiverPaginationQueryConditionDO();
//        receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScopeAndKafkaClusterName.setCreateTimeStart(dateFormat.parse(receiverCreateTimeStart));
//        receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScopeAndKafkaClusterName.setCreateTimeEnd(new Date(System.currentTimeMillis()));
//        receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScopeAndKafkaClusterName.setKafkaClusterName("_cluster");
//        assert 2 == kafkaClusterManageService.queryCountByCondition(receiverPaginationQueryConditionDOSearchByKafkaClusterCreateTimeScopeAndKafkaClusterName).getData();
//
//    }
//
//    /**
//     * 接收端创建时间开始查询时间
//     */
//    private String receiverCreateTimeStart = "2020-12-28 00:00:00";
//
//    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    /**
//     * 初始化一批接收端对象
//     */
//    private Integer initReceiverList() throws ParseException {
//
//        ReceiverDO receiverDO1 = new ReceiverDO();
//        receiverDO1.setKafkaClusterBrokerConfiguration(UUID.randomUUID().toString());
//        receiverDO1.setKafkaClusterId(new Random().nextLong());
//        receiverDO1.setKafkaClusterName("kafka_cluster_01");
//        receiverDO1.setKafkaClusterProducerInitConfiguration(UUID.randomUUID().toString());
//        receiverDO1.setCreateTime(dateFormat.parse("2020-12-28 01:59:59"));
//        Result<Long> createKafkaClusterResult = kafkaClusterManageService.createKafkaCluster(receiverDO1, null);
//        assert createKafkaClusterResult.success();
//
//        ReceiverDO receiverDO2 = new ReceiverDO();
//        receiverDO2.setKafkaClusterBrokerConfiguration(UUID.randomUUID().toString());
//        receiverDO2.setKafkaClusterId(new Random().nextLong());
//        receiverDO2.setKafkaClusterName("kafka_02");
//        receiverDO2.setKafkaClusterProducerInitConfiguration(UUID.randomUUID().toString());
//        receiverDO1.setCreateTime(dateFormat.parse("2020-12-28 23:59:59"));
//        createKafkaClusterResult = kafkaClusterManageService.createKafkaCluster(receiverDO2, null);
//        assert createKafkaClusterResult.success();
//
//        ReceiverDO receiverDO3 = new ReceiverDO();
//        receiverDO3.setKafkaClusterBrokerConfiguration(UUID.randomUUID().toString());
//        receiverDO3.setKafkaClusterId(new Random().nextLong());
//        receiverDO3.setKafkaClusterName("kafka_cluster_03");
//        receiverDO3.setKafkaClusterProducerInitConfiguration(UUID.randomUUID().toString());
//        receiverDO1.setCreateTime(dateFormat.parse("2020-12-28 09:59:59"));
//        createKafkaClusterResult = kafkaClusterManageService.createKafkaCluster(receiverDO3, null);
//        assert createKafkaClusterResult.success();
//
//        return 3;
//
//    }

}
