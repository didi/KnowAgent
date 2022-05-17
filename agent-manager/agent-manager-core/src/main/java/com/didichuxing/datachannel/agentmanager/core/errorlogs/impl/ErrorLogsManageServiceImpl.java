package com.didichuxing.datachannel.agentmanager.core.errorlogs.impl;

import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.agentmanager.core.errorlogs.ErrorLogsManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.ErrorLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@org.springframework.stereotype.Service
public class ErrorLogsManageServiceImpl implements ErrorLogsManageService {

    @Autowired
    private ErrorLogMapper errorLogDAO;

    @Override
    public List<String> getErrorLogs(String hostName, Long startTime, Long endTime) {
        return errorLogDAO.getErrorLogs(hostName, startTime, endTime);
    }

    @Override
    @Transactional
    public void clearExpireErrorLogs(Integer metricsExpireDays) {
        Long heartBeatTime = DateUtils.getBeforeDays(new Date(), metricsExpireDays).getTime();
        errorLogDAO.deleteBeforeTime(heartBeatTime);
    }

}
