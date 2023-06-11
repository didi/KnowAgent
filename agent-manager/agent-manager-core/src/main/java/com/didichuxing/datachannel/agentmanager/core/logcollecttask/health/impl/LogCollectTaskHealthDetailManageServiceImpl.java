package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDetailDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskHealthDetailPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthDetailManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.LogCollectTaskHealthDetailPOMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@org.springframework.stereotype.Service
public class LogCollectTaskHealthDetailManageServiceImpl implements LogCollectTaskHealthDetailManageService {

    @Autowired
    private LogCollectTaskHealthDetailPOMapper logCollectTaskHealthDetailDAO;

    @Autowired
    private LogCollectTaskHealthManageService logCollectTaskHealthManageService;

    @Autowired
    private MetricsManageService metricsManageService;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Override
    public LogCollectTaskHealthDetailDO get(Long logCollectTaskId, Long pathId, String hostName) {
        Map<String, Object> params = new HashMap<>();
        params.put("logCollectTaskId", logCollectTaskId);
        params.put("pathId", pathId);
        params.put("hostName", hostName);
        return ConvertUtil.obj2Obj(logCollectTaskHealthDetailDAO.get(params), LogCollectTaskHealthDetailDO.class);
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

    @Override
    public List<LogCollectTaskHealthDetailDO> getByLogCollectTaskIdAndLogCollectTaskHealthInspectionCode(Long logCollectTaskId, Integer logCollectTaskHealthInspectionCode) {
        LogCollectTaskHealthInspectionResultEnum logCollectTaskHealthInspectionResultEnum = LogCollectTaskHealthInspectionResultEnum.getByCode(logCollectTaskHealthInspectionCode);
        if(null == logCollectTaskHealthInspectionResultEnum) {
            //TODO：未知健康状态诊断码
            throw new RuntimeException();
        } else {
            /*
             * 校验日志采集任务[logCollectTaskId]对应健康状态码是否为logCollectTaskHealthInspectionCode
             */
            LogCollectTaskHealthDO logCollectTaskHealthDO = logCollectTaskHealthManageService.getByLogCollectTaskId(logCollectTaskId);
            if(!logCollectTaskHealthDO.getLogCollectTaskHealthInspectionResultType().equals(logCollectTaskHealthInspectionCode)) {
                return new ArrayList<>();
            } else {
                List<LogCollectTaskHealthDetailPO> logCollectTaskHealthDetailPOList = logCollectTaskHealthDetailDAO.selectByLogCollectTaskId(logCollectTaskId);
                List<LogCollectTaskHealthDetailPO> result = new ArrayList<>();
                /*
                 * logCollectTaskHealthDetailPOList可能存在系统中不存在的主机或与当前日志采集任务不相关的主机，此时进行过滤 & 清理
                 */
                List<HostDO> relaHostDOList = hostManageService.getHostListByLogCollectTaskId(logCollectTaskId);
                Set<String> hostNameSet = new HashSet<>();
                for (HostDO hostDO : relaHostDOList) {
                    hostNameSet.add(hostDO.getHostName());
                }
                for (LogCollectTaskHealthDetailPO logCollectTaskHealthDetailPO : logCollectTaskHealthDetailPOList) {
                    String hostName = logCollectTaskHealthDetailPO.getHostName();
                    if(null == hostManageService.getHostByHostName(hostName) || !hostNameSet.contains(hostName)) {
                        /*
                         * TODO：后续清理动作作为主机删除、或主机-服务解绑时进行对应处理
                         */
                        deleteById(logCollectTaskHealthDetailPO.getId());
                        continue;
                    } else {
                        result.add(logCollectTaskHealthDetailPO);
                    }
                }
                return ConvertUtil.list2List(result, LogCollectTaskHealthDetailDO.class);
            }
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        logCollectTaskHealthDetailDAO.deleteById(id);
    }

    @Override
    public List<MetricsLogCollectTaskPO> getErrorDetails(Long logCollectTaskId, Long pathId, String hostName, Integer logCollectTaskHealthInspectionCode) {

        /*
         * 根据logCollectTaskHealthInspectionCode获取对应case在logCollectTaskId&pathId&hostName时最近一次健康时间点
         */

        Map<String, Object> params = new HashMap<>();
        params.put("logCollectTaskId", logCollectTaskId);
        params.put("pathId", pathId);
        params.put("hostName", hostName);
        LogCollectTaskHealthDetailPO logCollectTaskHealthDetailPO = logCollectTaskHealthDetailDAO.get(params);
        String errorFieldNameInMetricsTable = "";
        Long lastCheckHealthyTimestamp = 0L;
        if(null == logCollectTaskHealthDetailPO) {
            //TODO：
            throw new RuntimeException();
        } else {
            if(logCollectTaskHealthInspectionCode.equals(LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SIZE_OVERRUN_TRUNCATE_EXISTS.getCode())) {
                errorFieldNameInMetricsTable = "tooLargeTruncateNum";
                lastCheckHealthyTimestamp = logCollectTaskHealthDetailPO.getTooLargeTruncateCheckHealthyHeartbeatTime();
            } else if(logCollectTaskHealthInspectionCode.equals(LogCollectTaskHealthInspectionResultEnum.LOG_PATH_NOT_EXISTS.getCode())) {
                errorFieldNameInMetricsTable = "collectPathIsExists";
                lastCheckHealthyTimestamp = logCollectTaskHealthDetailPO.getFilePathExistsCheckHealthyHeartbeatTime();
            } else if(logCollectTaskHealthInspectionCode.equals(LogCollectTaskHealthInspectionResultEnum.LOG_PATH_DISORDER.getCode())) {
                errorFieldNameInMetricsTable = "disorderExists";
                lastCheckHealthyTimestamp = logCollectTaskHealthDetailPO.getFileDisorderCheckHealthyHeartbeatTime();
            } else if(logCollectTaskHealthInspectionCode.equals(LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SLICE_ERROR_EXISTS.getCode())) {
                errorFieldNameInMetricsTable = "sliceErrorExists";
                lastCheckHealthyTimestamp = logCollectTaskHealthDetailPO.getLogSliceCheckHealthyHeartbeatTime();
            } else {
                //TODO：
                throw new RuntimeException();
            }
        }

        /*
         * 从日志采集任务心跳表查询近一次健康时间点到当前时间点所有涉及到logCollectTaskHealthInspectionCode心跳信息，根据心跳时间顺序排序
         */

        return metricsManageService.getErrorMetrics(logCollectTaskId, pathId, hostName, errorFieldNameInMetricsTable, lastCheckHealthyTimestamp, System.currentTimeMillis());

    }

    @Override
    @Transactional
    public void solveErrorDetail(Long logCollectTaskMetricId, Integer logCollectTaskHealthInspectionCode) {
        MetricsLogCollectTaskPO metricsLogCollectTaskPO = metricsManageService.getMetricLogCollectTask(logCollectTaskMetricId);
        if(null == metricsLogCollectTaskPO) {
            //TODO：
            throw new RuntimeException();
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("logCollectTaskId", metricsLogCollectTaskPO.getCollecttaskid());
            params.put("pathId", metricsLogCollectTaskPO.getPathid());
            params.put("hostName", metricsLogCollectTaskPO.getCollecttaskhostname());
            LogCollectTaskHealthDetailPO logCollectTaskHealthDetailPO = logCollectTaskHealthDetailDAO.get(params);
            if(logCollectTaskHealthInspectionCode.equals(LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SIZE_OVERRUN_TRUNCATE_EXISTS.getCode())) {
                logCollectTaskHealthDetailPO.setTooLargeTruncateCheckHealthyHeartbeatTime(metricsLogCollectTaskPO.getHeartbeattime());
            } else if(logCollectTaskHealthInspectionCode.equals(LogCollectTaskHealthInspectionResultEnum.LOG_PATH_NOT_EXISTS.getCode())) {
                logCollectTaskHealthDetailPO.setFilePathExistsCheckHealthyHeartbeatTime(metricsLogCollectTaskPO.getHeartbeattime());
            } else if(logCollectTaskHealthInspectionCode.equals(LogCollectTaskHealthInspectionResultEnum.LOG_PATH_DISORDER.getCode())) {
                logCollectTaskHealthDetailPO.setFileDisorderCheckHealthyHeartbeatTime(metricsLogCollectTaskPO.getHeartbeattime());
            } else if(logCollectTaskHealthInspectionCode.equals(LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SLICE_ERROR_EXISTS.getCode())) {
                logCollectTaskHealthDetailPO.setLogSliceCheckHealthyHeartbeatTime(metricsLogCollectTaskPO.getHeartbeattime());
            } else {
                //TODO：
                throw new RuntimeException();
            }
            logCollectTaskHealthDetailDAO.updateByPrimaryKey(logCollectTaskHealthDetailPO);
//            //重走一遍日志采集任务诊断流程
//            logCollectTaskHealthManageService.checkLogCollectTaskHealth(logCollectTaskManageService.getById(metricsLogCollectTaskPO.getCollecttaskid()));
        }
    }

    @Override
    @Transactional
    public void deleteByLogCollectPathId(Long logCollectPathId) {
        logCollectTaskHealthDetailDAO.deleteByLogCollectPathId(logCollectPathId);
    }

    @Override
    @Transactional
    public void deleteByLogCollectTaskId(Long logCollectTaskId) {
        logCollectTaskHealthDetailDAO.deleteByLogCollectTaskId(logCollectTaskId);
    }

    @Override
    @Transactional
    public void deleteByHostName(String hostName) {
        logCollectTaskHealthDetailDAO.deleteByHostName(hostName);
    }

}
