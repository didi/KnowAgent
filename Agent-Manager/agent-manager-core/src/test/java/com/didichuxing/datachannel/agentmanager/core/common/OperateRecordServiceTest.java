//package com.didichuxing.datachannel.agentmanager.core.common;
//
//import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
//import com.didichuxing.datachannel.agentmanager.common.bean.domain.operaterecord.OperateRecordDO;
//import com.didichuxing.datachannel.agentmanager.common.bean.dto.operaterecord.OperateRecordDTO;
//import com.didichuxing.datachannel.agentmanager.common.constant.Constant;
//import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.ModuleEnum;
//import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.OperationEnum;
//import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
//import com.didichuxing.datachannel.agentmanager.core.ApplicationTests;
//import org.apache.commons.collections.CollectionUtils;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//
//@Transactional
//@Rollback
//public class OperateRecordServiceTest extends ApplicationTests {
//
//    @Autowired
//    private OperateRecordService operateRecordService;
//
//    @Test
//    public void testSave() {
//
//        OperateRecordDTO operateRecordDTO = new OperateRecordDTO();
//        operateRecordDTO.setOperateTime(new Date());
//        operateRecordDTO.setBizId(UUID.randomUUID().toString());
//        operateRecordDTO.setOperator(Constant.getOperator(null));
//        operateRecordDTO.setContent(UUID.randomUUID().toString());
//        operateRecordDTO.setModuleId(ModuleEnum.UNKNOWN.getCode());
//        operateRecordDTO.setOperateId(OperationEnum.DELETE.getCode());
//        Result result = operateRecordService.save(operateRecordDTO);
//
//        assert result.success();
//
//        OperateRecordDTO queryCondition = new OperateRecordDTO();
//        List<OperateRecordDO> queryResult = operateRecordService.list(queryCondition);
//        assert CollectionUtils.isNotEmpty(queryResult);
//        assert queryResult.size() == 1;
//        OperateRecordDTO operateRecordDTO1 = ConvertUtil.obj2Obj(queryResult.get(0), OperateRecordDTO.class);
//        assert operateRecordDTO1.getOperateId().equals(operateRecordDTO.getOperateId());
//        assert operateRecordDTO1.getContent().equals(operateRecordDTO.getContent());
//        assert operateRecordDTO1.getModuleId().equals(operateRecordDTO.getModuleId());
//        assert operateRecordDTO1.getBizId().equals(operateRecordDTO.getBizId());
//        assert operateRecordDTO1.getOperator().equals(operateRecordDTO.getOperator());
//
//    }
//
//
//
//}
