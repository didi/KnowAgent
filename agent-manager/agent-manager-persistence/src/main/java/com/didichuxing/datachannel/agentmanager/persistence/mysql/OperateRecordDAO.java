package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.operaterecord.OperateRecordPO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * @author d06679
 * @date 2019/3/14
 */
@Repository
public interface OperateRecordDAO {

    List<OperateRecordPO> queryByCondt(OperateRecordPO condt);

    int insert(OperateRecordPO po);
}
