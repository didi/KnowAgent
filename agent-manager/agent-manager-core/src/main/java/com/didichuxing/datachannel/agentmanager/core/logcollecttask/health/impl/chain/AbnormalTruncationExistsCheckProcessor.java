package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.chain.ProcessorChain;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain.context.LogCollectTaskHealthCheckContext;

import java.util.Map;

/**
 * 日志异常截断检查
 * @author Ronaldo
 */
@HealthCheckProcessorAnnotation(seq = 4, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class AbnormalTruncationExistsCheckProcessor implements Processor {


    @Override
    public void process(Context context, ProcessorChain chain) {
        LogCollectTaskHealthCheckContext logCollectTaskHealthCheckContext = (LogCollectTaskHealthCheckContext) context;
        LogCollectTaskDO logCollectTaskDO = logCollectTaskHealthCheckContext.getLogCollectTaskDO();
        FileLogCollectPathDO fileLogCollectPathDO = logCollectTaskHealthCheckContext.getFileLogCollectPathDO();
        HostDO hostDO = logCollectTaskHealthCheckContext.getHostDO();
        Long logCollectTaskHealthCheckTimeEnd = logCollectTaskHealthCheckContext.getLogCollectTaskHealthCheckTimeEnd();
        AgentMetricsManageService agentMetricsManageService = logCollectTaskHealthCheckContext.getAgentMetricsManageService();
        Map<Long, Long> fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap = logCollectTaskHealthCheckContext.getFileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap();


        /*
         * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在日志被异常截断
         */
        boolean abnormalTruncationExists = checkAbnormalTruncationExists(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd, fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap, agentMetricsManageService);
        // 不存在异常截断
        if (!abnormalTruncationExists) {
            logCollectTaskHealthCheckContext.setAbnormalTruncationCheckHealthyCount(logCollectTaskHealthCheckContext.getAbnormalTruncationCheckHealthyCount() + 1);
            chain.process(context, chain);
            return;
        }
        // 存在异常截断
        LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SIZE_OVERRUN_TRUNCATE_EXISTS.getLogCollectTaskHealthLevelEnum();
        String logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SIZE_OVERRUN_TRUNCATE_EXISTS.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
        logCollectTaskHealthCheckContext.setLogCollectTaskHealthLevelEnum(logCollectTaskHealthLevelEnum);
        logCollectTaskHealthCheckContext.setLogCollectTaskHealthDescription(logCollectTaskHealthDescription);
    }

    /**
     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在日志被异常截断
     *
     * @param logCollectTaskId                                                  日志采集任务 id
     * @param fileLogCollectPathId                                              日志采集路径 id
     * @param hostName                                                          主机名
     * @param logCollectTaskHealthCheckTimeEnd                                  日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一毫秒
     * @param fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap filePathId : LastestAbnormalTruncationCheckHealthyTime
     * @return 返回logCollectTaskId+fileLogCollectPathId在host上是否存在日志被异常截断 true：存在 异常截断 false：不存在 异常截断
     */
    private boolean checkAbnormalTruncationExists(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long logCollectTaskHealthCheckTimeEnd, Map<Long, Long> fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap, AgentMetricsManageService agentMetricsManageService) {
        /*
         * 获取自上次"异常截断"健康点 ~ 当前时间，logCollectTaskId+fileLogCollectPathId在host上是否存在异常截断
         */
        Long lastestCheckTime = fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap.get(fileLogCollectPathId);
        if (null == lastestCheckTime) {
            throw new ServiceException(
                    String.format("FileLogCollectPath={id=%d}对应AbnormalTruncationExistsCheckHealthyTime不存在", fileLogCollectPathId),
                    ErrorCodeEnum.LOGCOLLECTTASK_HEALTH_ABNORMAL_TRUNCATION_EXISTS_CHECK_HEALTHY_TIME_NOT_EXISTS.getCode()
            );
        }
        Integer abnormalTruncationCount = agentMetricsManageService.getAbnormalTruncationCountByTimeFrame(
                lastestCheckTime,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                hostName
        );
        return abnormalTruncationCount > 0;
    }
}
