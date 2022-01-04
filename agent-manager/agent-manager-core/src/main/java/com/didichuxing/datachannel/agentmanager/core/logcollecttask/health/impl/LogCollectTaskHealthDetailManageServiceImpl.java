package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDetailDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskHealthDetailPO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthDetailManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.LogCollectTaskHealthDetailPOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
public class LogCollectTaskHealthDetailManageServiceImpl implements LogCollectTaskHealthDetailManageService {

    @Autowired
    private LogCollectTaskHealthDetailPOMapper logCollectTaskHealthDetailDAO;

    @Override
    public LogCollectTaskHealthDetailDO get(Long logCollectTaskId, Long pathId, String hostName) {
        return logCollectTaskHealthDetailDAO.select(logCollectTaskId, pathId, hostName);
    }

    @Override
    @Transactional
    public void update(LogCollectTaskHealthDetailDO logCollectTaskHealthDetailDO) {
        LogCollectTaskHealthDetailPO logCollectTaskHealthDetailPO = ConvertUtil.obj2Obj(logCollectTaskHealthDetailDO, LogCollectTaskHealthDetailPO.class);
        logCollectTaskHealthDetailDAO.updateByPrimaryKey(logCollectTaskHealthDetailPO);
    }

    @Override
    public LogCollectTaskHealthDetailDO getInit(Long logCollectTaskId, Long pathId, String hostName) {
        LogCollectTaskHealthDetailDO logCollectTaskHealthDetailDO = new LogCollectTaskHealthDetailDO();
        logCollectTaskHealthDetailDO.setLogCollectTaskId(logCollectTaskId);
        logCollectTaskHealthDetailDO.setPathId(pathId);
        logCollectTaskHealthDetailDO.setHostName(hostName);
        logCollectTaskHealthDetailDO.setCollectDqualityTime(COLLECT_DQUALITY_TIME_INIT);
        logCollectTaskHealthDetailDO.setFileDisorderCheckHealthyHeartbeatTime(FILE_DISORDER_CHECK_HEALTHY_HEARTBEAT_TIME_INIT);
        logCollectTaskHealthDetailDO.setLogSliceCheckHealthyHeartbeatTime(LOG_SLICE_CHECK_HEALTHY_HEARTBEAT_TIME_INIT);
        logCollectTaskHealthDetailDO.setFilePathExistsCheckHealthyHeartbeatTime(FILE_PATH_EXISTS_CHECK_HEALTHY_HEARTBEAT_TIME_INIT);
        logCollectTaskHealthDetailDO.setTooLargeTruncateCheckHealthyHeartbeatTime(TOO_LARGE_TRUNCATE_CHECK_HEALTHY_HEARTBEAT_TIME_INIT);
        logCollectTaskHealthDetailDO.setOperator(CommonConstant.getOperator(null));
        return logCollectTaskHealthDetailDO;
    }

    @Override
    @Transactional
    public void add(LogCollectTaskHealthDetailDO logCollectTaskHealthDetailDO) {
        LogCollectTaskHealthDetailPO logCollectTaskHealthDetailPO = ConvertUtil.obj2Obj(logCollectTaskHealthDetailDO, LogCollectTaskHealthDetailPO.class);
        logCollectTaskHealthDetailDAO.insert(logCollectTaskHealthDetailPO);
    }

}
