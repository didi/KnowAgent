//package com.didichuxing.datachannel.agentmanager.core.agent.version;
//
//import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionPaginationQueryConditionDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.version.AgentVersionPO;
//import com.didichuxing.datachannel.agentmanager.common.constant.Constant;
//import com.didichuxing.datachannel.agentmanager.core.ApplicationTests;
//import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentVersionMapper;
//import org.apache.commons.lang.StringUtils;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.UUID;
//
//@Transactional
//@Rollback
//public class AgentVersionManageServiceTest extends ApplicationTests {
//
//    @Autowired
//    private AgentVersionManageService agentVersionManageService;
//
//    @Autowired
//    private AgentVersionMapper agentVersionDAO;
//
//    @Test
//    public void testList() {
//        initTestListData();
//        List<AgentVersionDO> result = agentVersionManageService.list();
//        assert result.size() == 5;
//        for (AgentVersionDO agentVersionDO : result) {
//            assert StringUtils.isNotBlank(agentVersionDO.getDescription());
//            assert StringUtils.isNotBlank(agentVersionDO.getFileMd5());
//            assert StringUtils.isNotBlank(agentVersionDO.getFileName());
//            assert StringUtils.isNotBlank(agentVersionDO.getVersion());
//            assert StringUtils.isNotBlank(agentVersionDO.getOperator());
//            assert agentVersionDO.getFileType() == 1;
//            assert agentVersionDO.getId() > 0;
//            assert agentVersionDO.getCreateTime() != null;
//            assert agentVersionDO.getModifyTime() != null;
//        }
//    }
//
//    @Test
//    public void testDeleteAgentVersion() {
//        String version = initTestDeleteAgentVersionData();
//        Result<AgentVersionDO> agentVersionDOResult = agentVersionManageService.getByVersion(version);
//        assert agentVersionDOResult.success() && null != agentVersionDOResult.getData();
//        AgentVersionDO agentVersionDO = agentVersionDOResult.getData();
//        Result result = agentVersionManageService.deleteAgentVersion(agentVersionDO.getId(), null);
//        assert result.success();
//        agentVersionDOResult = agentVersionManageService.getByVersion(version);
//        assert agentVersionDOResult.success();
//        assert agentVersionDOResult.getData() == null;
//    }
//
//    @Test
//    public void testPaginationQuery() throws ParseException {
//
//        initTestListData();
//        AgentVersionPaginationQueryConditionDO agentVersionPaginationQueryConditionDO = new AgentVersionPaginationQueryConditionDO();
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        agentVersionPaginationQueryConditionDO.setAgentVersion("agent_version_0");
//        agentVersionPaginationQueryConditionDO.setAgentPackageName("");
//        agentVersionPaginationQueryConditionDO.setAgentVersionCreateTimeStart(dateFormat.parse("2021-01-01 00:00:00"));
//        agentVersionPaginationQueryConditionDO.setAgentVersionCreateTimeEnd(new Date(System.currentTimeMillis()));
//        agentVersionPaginationQueryConditionDO.setLimitSize(10000);
//        agentVersionPaginationQueryConditionDO.setLimitFrom(0);
//        Result<List<AgentVersionDO>> paginationQueryByConditonResult = agentVersionManageService.paginationQueryByConditon(agentVersionPaginationQueryConditionDO);
//        Result<Integer> queryCountByConditionResult = agentVersionManageService.queryCountByCondition(agentVersionPaginationQueryConditionDO);
//        assert paginationQueryByConditonResult.success();
//        assert queryCountByConditionResult.success();
//        assert queryCountByConditionResult.getData() == 5;
//        assert paginationQueryByConditonResult.getData().size() == 5;
//
//    }
//
//    /**
//     * 初始化 agentVersion 对象，并返回该 agentVersion 对象对应 version 属性值
//     * @return 返回已创建 agentVersion 对象对应 version 属性值
//     */
//    private String initTestDeleteAgentVersionData() {
//        AgentVersionPO agentVersionPO = new AgentVersionPO();
//        agentVersionPO.setDescription(UUID.randomUUID().toString());
//        agentVersionPO.setFileMd5(UUID.randomUUID().toString());
//        agentVersionPO.setFileName(UUID.randomUUID().toString());
//        agentVersionPO.setFileType(1);
//        String version = UUID.randomUUID().toString();
//        agentVersionPO.setVersion(version);
//        agentVersionPO.setOperator(Constant.getOperator(null));
//        assert agentVersionDAO.insert(agentVersionPO) > 0;
//        assert agentVersionPO.getId() > 0;
//
//        return version;
//    }
//
//    /**
//     * 初始化测试数据
//     */
//    private void initTestListData() {
//
//        AgentVersionPO agentVersionPO1 = new AgentVersionPO();
//        agentVersionPO1.setDescription(UUID.randomUUID().toString());
//        agentVersionPO1.setFileMd5(UUID.randomUUID().toString());
//        agentVersionPO1.setFileName(UUID.randomUUID().toString());
//        agentVersionPO1.setFileType(1);
//        agentVersionPO1.setVersion("agent_version_01");
//        agentVersionPO1.setOperator(Constant.getOperator(null));
//        assert agentVersionDAO.insert(agentVersionPO1) > 0;
//        assert agentVersionPO1.getId() > 0;
//
//        AgentVersionPO agentVersionPO2 = new AgentVersionPO();
//        agentVersionPO2.setDescription(UUID.randomUUID().toString());
//        agentVersionPO2.setFileMd5(UUID.randomUUID().toString());
//        agentVersionPO2.setFileName(UUID.randomUUID().toString());
//        agentVersionPO2.setFileType(1);
//        agentVersionPO2.setVersion("agent_version_02");
//        agentVersionPO2.setOperator(Constant.getOperator(null));
//        assert agentVersionDAO.insert(agentVersionPO2) > 0;
//        assert agentVersionPO2.getId() > 0;
//
//        AgentVersionPO agentVersionPO3 = new AgentVersionPO();
//        agentVersionPO3.setDescription(UUID.randomUUID().toString());
//        agentVersionPO3.setFileMd5(UUID.randomUUID().toString());
//        agentVersionPO3.setFileName(UUID.randomUUID().toString());
//        agentVersionPO3.setFileType(1);
//        agentVersionPO3.setVersion("agent_version_03");
//        agentVersionPO3.setOperator(Constant.getOperator(null));
//        assert agentVersionDAO.insert(agentVersionPO3) > 0;
//        assert agentVersionPO3.getId() > 0;
//
//        AgentVersionPO agentVersionPO4 = new AgentVersionPO();
//        agentVersionPO4.setDescription(UUID.randomUUID().toString());
//        agentVersionPO4.setFileMd5(UUID.randomUUID().toString());
//        agentVersionPO4.setFileName(UUID.randomUUID().toString());
//        agentVersionPO4.setFileType(1);
//        agentVersionPO4.setVersion("agent_version_04");
//        agentVersionPO4.setOperator(Constant.getOperator(null));
//        assert agentVersionDAO.insert(agentVersionPO4) > 0;
//        assert agentVersionPO4.getId() > 0;
//
//        AgentVersionPO agentVersionPO5 = new AgentVersionPO();
//        agentVersionPO5.setDescription(UUID.randomUUID().toString());
//        agentVersionPO5.setFileMd5(UUID.randomUUID().toString());
//        agentVersionPO5.setFileName(UUID.randomUUID().toString());
//        agentVersionPO5.setFileType(1);
//        agentVersionPO5.setVersion("agent_version_05");
//        agentVersionPO5.setOperator(Constant.getOperator(null));
//        assert agentVersionDAO.insert(agentVersionPO5) > 0;
//        assert agentVersionPO5.getId() > 0;
//
//    }
//
//}
