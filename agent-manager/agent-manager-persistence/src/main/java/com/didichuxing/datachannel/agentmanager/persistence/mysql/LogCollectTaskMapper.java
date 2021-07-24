package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskPaginationRecordDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "logCollectTaskDAO")
public interface LogCollectTaskMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LogCollectTaskPO record);

    LogCollectTaskPO selectByPrimaryKey(Long id);

    int updateByPrimaryKey(LogCollectTaskPO record);

    List<LogCollectTaskPO> getLogCollectTaskListByHostId(Long hostId);

    List<LogCollectTaskPaginationRecordDO> paginationQueryByConditon(LogCollectTaskPaginationQueryConditionDO logCollectTaskPaginationQueryConditionDO);

    Integer queryCountByCondition(LogCollectTaskPaginationQueryConditionDO logCollectTaskPaginationQueryConditionDO);

    List<LogCollectTaskPO> getByStatus(Integer logCollectTaskStatus);

    List<LogCollectTaskPO> getLogCollectTaskListByServiceId(Long serviceId);

    List<LogCollectTaskPO> getLogCollectTaskListByKafkaClusterId(Long kafkaClusterId);

    Long countAll();

}
