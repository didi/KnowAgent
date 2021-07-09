package com.didichuxing.datachannel.agentmanager.core.agent.metrics.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.constant.AgentConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.host.HostTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.AgentMetricsDAO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class AgentMetricsManageServiceImpl implements AgentMetricsManageService {

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private AgentMetricsDAO agentMetricsDAO;

    @Override
    public boolean completeCollect(HostDO hostDO) {
        /*
         *
         * 校验hostDO对应主机类型：
         *  主机：
         *      1.）获取该主机对应所有日志采集任务列表
         *      2.）针对日志采集任务列表中各日志采集任务，获取其待采集文件路径集，针对日志采集任务id+日志采集路径id+hostName，判断是是否已采集完
         *      3.）获取该主机关联的容器列表：
         *          针对各容器走如下 "容器" 判断逻辑
         *  容器：
         *      1.）获取该容器对应所有日志采集任务列表
         *      2.）针对日志采集任务列表中各日志采集任务，获取其待采集文件路径集，针对日志采集任务id+日志采集路径id+容器宿主机hostName+容器名，判断是是否已采集完
         *
         */
        if(HostTypeEnum.HOST.getCode().equals(hostDO.getContainer())) {//主机类型
            /*
             * 检查主机对应日志采集任务集是否已采集完
             */
            List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageService.getLogCollectTaskListByHostId(hostDO.getId());//主机关联的日志采集任务集
            for (LogCollectTaskDO logCollectTaskDO : logCollectTaskDOList) {
                List<FileLogCollectPathDO> logCollectPathDOList = logCollectTaskDO.getFileLogCollectPathList();
                for (FileLogCollectPathDO fileLogCollectPathDO : logCollectPathDOList) {
                    boolean hostCompleteCollect = hostCompleteCollect(hostDO.getHostName(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId());
                    if(!hostCompleteCollect) {//未完成 采集
                        return false;
                    }
                }
            }
            /*
             * 检查主机上运行的各容器对应日志采集任务集是否已采集完
             */
            List<HostDO> containerList = hostManageService.getContainerListByParentHostName(hostDO.getHostName());
            if(CollectionUtils.isEmpty(containerList)) {//主机未运行任何容器
                return true;
            }
            for (HostDO container : containerList) {
                List<LogCollectTaskDO> logCollectTaskDOListRelationContainer = logCollectTaskManageService.getLogCollectTaskListByHostId(hostDO.getId());
                for (LogCollectTaskDO logCollectTaskDO : logCollectTaskDOListRelationContainer) {
                    List<FileLogCollectPathDO> logCollectPathDOList = logCollectTaskDO.getFileLogCollectPathList();
                    for (FileLogCollectPathDO fileLogCollectPathDO : logCollectPathDOList) {
                        boolean containerCompleteCollect = containerCompleteCollect(container.getHostName(), hostDO.getHostName(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId());
                        if(!containerCompleteCollect) {//未完成 采集
                            return false;
                        }
                    }
                }
            }
            return true;
        } else if(HostTypeEnum.CONTAINER.getCode().equals(hostDO.getContainer())) {//容器类型
            List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageService.getLogCollectTaskListByHostId(hostDO.getId());//主机关联的日志采集任务集
            String parentHostName = hostDO.getParentHostName();//容器宿主机名
            for (LogCollectTaskDO logCollectTaskDO : logCollectTaskDOList) {
                List<FileLogCollectPathDO> logCollectPathDOList = logCollectTaskDO.getFileLogCollectPathList();
                for (FileLogCollectPathDO fileLogCollectPathDO : logCollectPathDOList) {
                    boolean containerCompleteCollect = containerCompleteCollect(hostDO.getHostName(), parentHostName, logCollectTaskDO.getId(), fileLogCollectPathDO.getId());
                    if(!containerCompleteCollect) {//未完成 采集
                        return false;
                    }
                }
            }
            return true;
        } else {
            throw new ServiceException(
                    String.format("获取主机={%s}关联的日志采集任务集失败，原因为：主机类型={%d}为系统未知主机类型", JSON.toJSONString(hostDO), hostDO.getContainer()),
                    ErrorCodeEnum.UNKNOWN_HOST_TYPE.getCode()
            );
        }
    }

    @Override
    public Long getHostByteLimitDurationByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logModelHostName) {
        return agentMetricsDAO.getHostByteLimitDuration(startTime, endTime, logModelHostName, logCollectTaskId, fileLogCollectPathId);
    }

    @Override
    public Long getHeartbeatTimesByTimeFrame(Long startTime, Long endTime, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        return agentMetricsDAO.getHeartbeatTimesByTimeFrame(startTime, endTime, logCollectTaskId, fileLogCollectPathId, logCollectTaskHostName);
    }

    @Override
    public Long getLastestCollectTime(Long logCollectTaskId, Long fileLogCollectPathId, String hostName) {
        return agentMetricsDAO.getLatestCollectTime(logCollectTaskId, fileLogCollectPathId, hostName);
    }

    @Override
    public Integer getFilePathNotExistsCountByTimeFrame(Long logCollectTaskHealthLastestCheckTime, Long logCollectTaskHealthCheckTimeEnd, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskhostName) {
        return agentMetricsDAO.getFilePathNotExistsCountByTimeFrame(logCollectTaskHealthLastestCheckTime, logCollectTaskHealthCheckTimeEnd, logCollectTaskId, fileLogCollectPathId, logCollectTaskhostName);
    }

    @Override
    public Integer getFileDisorderCountByTimeFrame(Long logCollectTaskHealthLastestCheckTime, Long logCollectTaskHealthCheckTimeEnd, Long logCollectTaskId, Long fileLogCollectPathId, String hostName) {
        return agentMetricsDAO.getFileDisorderCount(logCollectTaskHealthLastestCheckTime, logCollectTaskHealthCheckTimeEnd, logCollectTaskId, fileLogCollectPathId, hostName);
    }

    @Override
    public Integer getSliceErrorCount(Long logCollectTaskHealthLastestCheckTime, Long logCollectTaskHealthCheckTimeEnd, Long logCollectTaskId, Long fileLogCollectPathId, String hostName) {
        return agentMetricsDAO.getSliceErrorCount(logCollectTaskHealthLastestCheckTime, logCollectTaskHealthCheckTimeEnd, logCollectTaskId, fileLogCollectPathId, hostName);
    }

    @Override
    public Integer getAbnormalTruncationCountByTimeFrame(Long logCollectTaskHealthLastestCheckTime, Long logCollectTaskHealthCheckTimeEnd, Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName) {
        return agentMetricsDAO.getAbnormalTruncationCountByTimeFrame(logCollectTaskHealthLastestCheckTime, logCollectTaskHealthCheckTimeEnd, logCollectTaskId, fileLogCollectPathId, logCollectTaskHostName);
    }

    public boolean containerCompleteCollect(String containerHostName, String parentHostName, Long logCollectTaskId, Long fileLogCollectPathId) {
        /*
         * 获取近距当前时间近5mins关于 "parentHostName+containerHostName+logCollectTaskId+fileLogCollectPathId"
         * 对应 metric sendCount > 0 的列表为空 & 对应 metric sendCount = 0 列表不为空（ps：采集量为空且心跳正常情况下才代表采集完成）
         */
        Long endTime = System.currentTimeMillis();
        Long startTime = endTime - AgentConstant.AGENT_COLLECT_COMPLETE_TIME_THRESHOLD;
        Long sendCountEqualsZeroSize = agentMetricsDAO.getContainerSendCountEqualsZeroRecordSize(containerHostName, parentHostName, logCollectTaskId, fileLogCollectPathId, startTime, endTime);
        Long sendCountGtZeroSize = agentMetricsDAO.getContainerSendCountGtZeroRecordSize(containerHostName, parentHostName, logCollectTaskId, fileLogCollectPathId, startTime, endTime);
        boolean containerCompleteCollect = sendCountEqualsZeroSize > 0 && sendCountGtZeroSize == 0;
        return containerCompleteCollect;
    }

    public boolean hostCompleteCollect(String hostName, Long logCollectTaskId, Long fileLogCollectPathId) {
        /*
         * 获取近距当前时间近5mins关于 "hostName+logCollectTaskId+fileLogCollectPathId"
         * 对应 metric sendCount > 0 的列表为空 & 对应 metric sendCount = 0 列表不为空（ps：采集量为空且心跳正常情况下才代表采集完成）
         */
        Long endTime = System.currentTimeMillis();
        Long startTime = endTime - AgentConstant.AGENT_COLLECT_COMPLETE_TIME_THRESHOLD;
        Long sendCountEqualsZeroSize = agentMetricsDAO.getHostSendCountEqualsZeroRecordSize(hostName, logCollectTaskId, fileLogCollectPathId, startTime, endTime);
        Long sendCountGtZeroSize = agentMetricsDAO.getHostSendCountGtZeroRecordSize(hostName, logCollectTaskId, fileLogCollectPathId, startTime, endTime);
        boolean containerCompleteCollect = sendCountEqualsZeroSize > 0 && sendCountGtZeroSize == 0;
        return containerCompleteCollect;
    }

    @Override
    public Long getHostCpuLimiDturationByTimeFrame(Long startTime, Long endTime, String hostName) {
        return agentMetricsDAO.getHostCpuLimitDuration(startTime, endTime, hostName);
    }

    @Override
    public Long getHostByteLimitDurationByTimeFrame(Long startTime, Long endTime, String hostName) {
        Long value = agentMetricsDAO.getHostByteLimitDuration(startTime, endTime, hostName);
        return value == null ? 0L : value;
    }

    @Override
    public Integer getLastestFdUsage(String hostName) {
        return agentMetricsDAO.getLatestFdUsage(hostName);
    }

    @Override
    public Integer getErrorLogCount(Long lastestCheckTimeStart, Long agentHealthCheckTimeEnd, String hostName) {
        // TODO: 2021-06-28 优化错误日志的管理
//        return agentMetricsDAO.getErrorLogCount(lastestCheckTimeStart, agentHealthCheckTimeEnd, hostName);
        return 0;
    }

    @Override
    public Long getHeartbeatTimesByTimeFrame(Long startTime, Long endTime, String hostName) {
        return agentMetricsDAO.getHeartBeatTimes(startTime, endTime, hostName);
    }

    @Override
    public Integer getLastestCpuUsage(String hostName) {
        return agentMetricsDAO.getLatestCpuUsage(hostName).intValue();
    }

    public Long getLatestMemoryUsage(String hostName) {
        return agentMetricsDAO.getLatestMemoryUsage(hostName);
    }

    @Override
    public Long getLastestAgentStartupTime(String hostName) {
        return agentMetricsDAO.getLatestStartupTime(hostName);
    }

    @Override
    public Long getAgentFullgcTimesByTimeFrame(Long startTime, Long endTime, String hostName) {
        return agentMetricsDAO.getGCCount(startTime, endTime, hostName);
    }

    @Override
    public List<MetricPoint> getAgentCpuUsagePerMinMetric(String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getAgentCpuUsagePerMin(startTime, endTime, hostName);
    }

    @Override
    public List<MetricPoint> getAgentMemoryUsagePerMinMetric(String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getAgentMemoryUsagePerMin(startTime, endTime, hostName);
    }

    @Override
    public List<MetricPoint> getAgentFullGcTimesPerMinMetric(String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getAgentGCTimesPerMin(startTime, endTime, hostName);
    }

    @Override
    public List<MetricPoint> getAgentOutputBytesPerMinMetric(String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getAgentOutputBytesPerMin(startTime, endTime, hostName);
    }

    @Override
    public List<MetricPoint> getAgentOutputLogsCountPerMinMetric(String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getAgentOutputLogsPerMin(startTime, endTime, hostName);
    }

    @Override
    public List<MetricPoint> getAgentFdUsagePerMinMetric(String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getAgentFdUsagePerMin(startTime, endTime, hostName);
    }

    @Override
    public List<MetricPoint> getAgentStartupExistsPerMinMetric(String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getAgentStartupExistsPerMin(startTime, endTime, hostName);
    }

    @Override
    public List<MetricPoint> getLogCollectTaskLogsBytesPerMinMetric(Long logCollectTaskId, Long startTime, Long endTime) {
        return agentMetricsDAO.getLogCollectTaskBytesPerMin(logCollectTaskId, startTime, endTime);
    }

    @Override
    public List<MetricPoint> getLogCollectTaskLogsCountPerMinMetric(Long logCollectTaskId, Long startTime, Long endTime) {
        return agentMetricsDAO.getLogCollectTaskLogCountPerMin(logCollectTaskId, startTime, endTime);
    }

    @Override
    public List<MetricPoint> getFileLogPathExistsPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getFileLogPathNotExistsPerMin(logCollectTaskId, fileLogCollectPathId, hostName, startTime, endTime);
    }

    @Override
    public List<MetricPoint> getFileLogPathDisorderPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getFileLogPathDisorderPerMin(logCollectTaskId, fileLogCollectPathId, hostName, startTime, endTime);
    }

    @Override
    public List<MetricPoint> getFileLogPathLogSliceErrorPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getFileLogPathLogSliceErrorPerMin(logCollectTaskId, fileLogCollectPathId, hostName, startTime, endTime);
    }

    @Override
    public List<MetricPoint> getFileLogPathAbnormalTruncationPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getFileLogPathAbnormalTruncationPerMin(logCollectTaskId, fileLogCollectPathId, hostName, startTime, endTime);
    }

    @Override
    public List<MetricPoint> getFilterOutPerLogPathPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getFilterOutPerLogPathPerMin(logCollectTaskId, fileLogCollectPathId, hostName, startTime, endTime);
    }

    @Override
    public List<MetricPoint> getMinCurrentCollectTimePerLogPathPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getMinCurrentCollectTimePerLogPathPerMin(logCollectTaskId, fileLogCollectPathId, hostName, startTime, endTime);
    }

    @Override
    public List<MetricPoint> getLimitTimePerLogPathPerMinMetric(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long startTime, Long endTime) {
        return agentMetricsDAO.getLimitTimePerLogPathPerMin(logCollectTaskId, fileLogCollectPathId, hostName, startTime, endTime);
    }

}
