package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDetailDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskHealthDetailPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskPO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "logCollectTaskHealthDetailDAO")
public interface LogCollectTaskHealthDetailPOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LogCollectTaskHealthDetailPO record);

    int insertSelective(LogCollectTaskHealthDetailPO record);

    LogCollectTaskHealthDetailPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LogCollectTaskHealthDetailPO record);

    int updateByPrimaryKey(LogCollectTaskHealthDetailPO record);

    List<LogCollectTaskHealthDetailPO> selectByLogCollectTaskId(Long logCollectTaskId);

    /**
     * logCollectTaskId：日志采集任务id
     * pathId：日志采集路径 id
     * hostName：主机名
     */
    LogCollectTaskHealthDetailPO get(Map params);

    void deleteByLogCollectPathId(Long logCollectPathId);

    void deleteByLogCollectTaskId(Long logCollectTaskId);

    void deleteById(Long id);

    void deleteByHostName(String hostName);

}
