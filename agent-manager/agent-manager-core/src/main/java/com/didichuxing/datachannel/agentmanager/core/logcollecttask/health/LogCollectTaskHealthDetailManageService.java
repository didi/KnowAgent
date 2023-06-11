package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDetailDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskPO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LogCollectTaskHealthDetailManageService {

    /**
     * 采集完整性时间初始值
     */
    Long COLLECT_DQUALITY_TIME_INIT = 0L;
    /**
     * 文件乱序检查健康点对应 agent 心跳时间初始值
     */
    Long FILE_DISORDER_CHECK_HEALTHY_HEARTBEAT_TIME_INIT = 0L;
    /**
     * 文件切片错误检查健康点对应 agent 心跳时间初始值
     */
    Long LOG_SLICE_CHECK_HEALTHY_HEARTBEAT_TIME_INIT = 0L;
    /**
     * 待采集文件不存在检查健康点对应 agent 心跳时间初始值
     */
    Long FILE_PATH_EXISTS_CHECK_HEALTHY_HEARTBEAT_TIME_INIT = 0L;
    /**
     * 过长截断检查健康点对应 agent 心跳时间初始值
     */
    Long TOO_LARGE_TRUNCATE_CHECK_HEALTHY_HEARTBEAT_TIME_INIT = 0L;

    LogCollectTaskHealthDetailDO get(Long logCollectTaskId, Long pathId, String hostName);

    void update(LogCollectTaskHealthDetailDO logCollectTaskHealthDetailDO);

    LogCollectTaskHealthDetailDO getInit(Long logCollectTaskId, Long pathId, String hostName);

    void add(LogCollectTaskHealthDetailDO logCollectTaskHealthDetailDO);

    List<LogCollectTaskHealthDetailDO> getByLogCollectTaskIdAndLogCollectTaskHealthInspectionCode(Long logCollectTaskId, Integer logCollectTaskHealthInspectionCode);

    void deleteById(Long id);

    List<MetricsLogCollectTaskPO> getErrorDetails(Long logCollectTaskId, Long pathId, String hostName, Integer logCollectTaskHealthInspectionCode);

    void solveErrorDetail(Long logCollectTaskMetricId, Integer logCollectTaskHealthInspectionCode);

    void deleteByLogCollectPathId(Long logCollectPathId);

    void deleteByLogCollectTaskId(Long logCollectTaskId);

    void deleteByHostName(String hostName);

}
