package com.didichuxing.datachannel.agentmanager.core.common.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.operaterecord.OperateRecordDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.operaterecord.OperateRecordDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.operaterecord.OperateRecordPO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.ModuleEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.OperationEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.agentmanager.core.common.OperateRecordService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.OperateRecordDAO;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @author d06679
 * @date 2019/3/14
 */
@Service
public class OperateRecordServiceImpl implements OperateRecordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperateRecordServiceImpl.class);@Autowired
    private OperateRecordDAO operateRecordDAO;

    /**
     * 根据指定的查询条件查询
     * @param condt 查询条件dto
     * @return 操作记录列表
     */
    @Override
    public List<OperateRecordDO> list(OperateRecordDTO condt) {
        if (condt == null) {
            return Lists.newArrayList();
        }

        List<OperateRecordPO> pos = operateRecordDAO.queryByCondt(ConvertUtil.obj2Obj(condt, OperateRecordPO.class));
        return ConvertUtil.list2List(pos, OperateRecordDO.class);
    }

    /**
     * 插入一条操作记录
     * @param moduleEnum    模块id  比如索引模板、应用管理、DSL审核
     * @param operationEnum 操作行为  OperationEnum
     * @param bizId     业务id  例如索引模板id、应用id 或者工单id
     * @param content   操作详情
     * @param operator  操作人
     */
    @Override
    @Transactional
    public void save(ModuleEnum moduleEnum, OperationEnum operationEnum, Object bizId, String content,
                       String operator) {
        save(moduleEnum.getCode(), operationEnum.getCode(), String.valueOf(bizId), content, operator);
    }

    /**
     * 插入一条操作记录
     * @param moduleId  模块id  比如索引模板、应用管理、DSL审核
     * @param operateId 操作行为  OperationEnum
     * @param bizId     业务id  例如索引模板id、应用id 或者工单id
     * @param content   操作详情
     * @param operator  操作人
     * @return result
     */
    @Override
    @Transactional
    public void save(int moduleId, int operateId, String bizId, String content, String operator) {
        if (operator == null) {
            operator = "unknown";
        }

        OperateRecordDTO param = new OperateRecordDTO();
        param.setModuleId(moduleId);
        param.setOperateId(operateId);
        param.setBizId(bizId);
        param.setContent(content);
        param.setOperator(CommonConstant.getOperator(operator));

        save(param);
    }

    @Override
    @Transactional
    public void save(OperateRecordDTO param) {
        Result checkResult = checkParam(param);

        if (checkResult.failed()) {
            LOGGER.warn("class=OperateRecordServiceImpl||method=save||msg={}||msg=check fail!",
                checkResult.getMessage());
            throw new ServiceException(checkResult.getMessage(), checkResult.getCode());
        }

        if (OperationEnum.EDIT.getCode() == param.getOperateId()) {
            if (StringUtils.isBlank(param.getContent())) {
                return;
            }
        }
        operateRecordDAO.insert(ConvertUtil.obj2Obj(param, OperateRecordPO.class));
        return;
    }

    /**
     * 查询某个最新的操作记录
     * @param moduleId  模块id
     * @param operateId 操作行为
     * @param bizId     业务id
     * @param beginDate 起始时间 时间范围是:[beginDate, beginDate + 24h]
     * @return 如果没有一条 返回 null
     */
    @Override
    public OperateRecordDO getLastRecord(int moduleId, int operateId, String bizId, Date beginDate) {
        OperateRecordPO condt = new OperateRecordPO();
        condt.setModuleId(moduleId);
        condt.setOperateId(operateId);
        condt.setBizId(bizId);
        condt.setBeginTime(DateUtils.getZeroDate(beginDate));
        condt.setEndTime(DateUtils.getAfterDays(condt.getBeginTime(), 1));

        List<OperateRecordPO> pos = operateRecordDAO.queryByCondt(condt);

        if (CollectionUtils.isEmpty(pos)) {
            return null;
        }

        return ConvertUtil.obj2Obj(pos.get(0), OperateRecordDO.class);
    }

    /******************************************* private method **************************************************/
    private Result checkParam(OperateRecordDTO param) {
        if (param == null) {
            return Result.buildParamIllegal("记录为空");
        }
        if (param.getModuleId() == null) {
            return Result.buildParamIllegal("模块为空");
        }
        if (param.getOperateId() == null) {
            return Result.buildParamIllegal("操作为空");
        }
        if (StringUtils.isBlank(param.getBizId())) {
            return Result.buildParamIllegal("业务id为空");
        }
        if (StringUtils.isBlank(param.getContent())) {
            return Result.buildParamIllegal("操作内容为空");
        }
        if (StringUtils.isBlank(param.getOperator())) {
            return Result.buildParamIllegal("操作人为空");
        }
        if (!ModuleEnum.validate(param.getModuleId())) {
            return Result.buildParamIllegal("模块非法");
        }
        if (!OperationEnum.validate(param.getOperateId())) {
            return Result.buildParamIllegal("操作非法");
        }

        return Result.buildSucc();
    }
}
