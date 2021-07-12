package com.didichuxing.datachannel.agentmanager.core.common;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.operaterecord.OperateRecordDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.operaterecord.OperateRecordDTO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.ModuleEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.OperationEnum;

import java.util.Date;
import java.util.List;

/**
 *
 * @author d06679
 * @date 2019/3/14
 */
public interface OperateRecordService {

    /**
     * 根据指定的查询条件查询
     * @param condt 查询条件dto
     * @return 操作记录列表
     */
    List<OperateRecordDO> list(OperateRecordDTO condt);

    /**
     * 插入一条操作记录
     * @param moduleId 模块id  比如索引模板、应用管理、DSL审核
     * @param operateId 操作行为  OperationEnum
     * @param bizId 业务id  例如索引模板id、应用id 或者工单id
     * @param content 操作详情
     * @param operator 操作人
     * @return 成功 true   失败 false
     *
     */
    void save(int moduleId, int operateId, String bizId, String content, String operator);

    /**
     * 插入一条操作记录
     * @param moduleEnum
     * @param operationEnum
     * @param bizId
     * @param content
     * @param operator
     */
    void save(ModuleEnum moduleEnum, OperationEnum operationEnum, Object bizId, String content, String operator);

    /**
     * 插入一条操作记录
     * @return 成功 true   失败 false
     *
     */
    void save(OperateRecordDTO param);

    /**
     * 查询某个最新的操作记录
     * @param moduleId 模块id
     * @param operateId 操作行为
     * @param bizId 业务id
     * @param beginDate 起始时间 时间范围是:[beginDate, beginDate + 24h]
     * @return 如果没有一条 返回 null
     */
    OperateRecordDO getLastRecord(int moduleId, int operateId, String bizId, Date beginDate);

}
