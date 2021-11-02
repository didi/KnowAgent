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

import java.util.Map;

/**
 * 日志切片是否配置错误检查
 * @author Ronaldo
 */
@HealthCheckProcessorAnnotation(seq = 5, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class LogSliceErrorExistsCheckProcessor implements Processor {

    @Override
    public void process(Context context, ProcessorChain chain) {
        LogCollectTaskHealthCheckContext logCollectTaskHealthCheckContext = (LogCollectTaskHealthCheckContext) context;
        LogCollectTaskDO logCollectTaskDO = logCollectTaskHealthCheckContext.getLogCollectTaskDO();
        FileLogCollectPathDO fileLogCollectPathDO = logCollectTaskHealthCheckContext.getFileLogCollectPathDO();
        HostDO hostDO = logCollectTaskHealthCheckContext.getHostDO();
        Long logCollectTaskHealthCheckTimeEnd = logCollectTaskHealthCheckContext.getLogCollectTaskHealthCheckTimeEnd();
        AgentMetricsManageService agentMetricsManageService = logCollectTaskHealthCheckContext.getAgentMetricsManageService();
        Map<Long, Long> fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap = logCollectTaskHealthCheckContext.getFileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap();

        /*
         * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在日志切片配置错误
         */
        boolean errorLogsExists = checkLogSliceErrorExists(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd, fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap, agentMetricsManageService);
        // 不存在日志切片配置错误
        if (!errorLogsExists) {
            logCollectTaskHealthCheckContext.setLogSliceCheckHealthyCount(logCollectTaskHealthCheckContext.getLogSliceCheckHealthyCount() + 1);
            chain.process(context, chain);
            return;
        }
        // 存在日志切片错误
        LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SLICE_ERROR_EXISTS.getLogCollectTaskHealthLevelEnum();
        String logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SLICE_ERROR_EXISTS.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
        logCollectTaskHealthCheckContext.setLogCollectTaskHealthLevelEnum(logCollectTaskHealthLevelEnum);
        logCollectTaskHealthCheckContext.setLogCollectTaskHealthDescription(logCollectTaskHealthDescription);
    }

    /**
     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在日志切片错误
     *
     * @param logCollectTaskId                                        日志采集任务 id
     * @param fileLogCollectPathId                                    日志采集路径 id
     * @param hostName                                                主机名
     * @param logCollectTaskHealthCheckTimeEnd                        日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一毫秒
     * @param fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap filePathId : lastestLogSliceCheckHealthyTimeMap
     * @return 返回logCollectTaskId+fileLogCollectPathId在host上是否存在日志切片错误
     */
    private boolean checkLogSliceErrorExists(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long logCollectTaskHealthCheckTimeEnd, Map<Long, Long> fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap, AgentMetricsManageService agentMetricsManageService) {
        /*
         * 获取自上次"错误日志输出存在"健康点 ~ 当前时间，logCollectTaskId+fileLogCollectPathId在host上是否存在日志切片错误
         */
        Long lastestCheckTime = fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap.get(fileLogCollectPathId);
        if (null == lastestCheckTime) {
            throw new ServiceException(
                    String.format("FileLogCollectPath={id=%d}对应LogSliceCheckHealthyTime不存在", fileLogCollectPathId),
                    ErrorCodeEnum.LOGCOLLECTTASK_HEALTH_LOG_SLICE_CHECK_HEALTHY_TIME_NOT_EXISTS.getCode()
            );
        }
        Integer logSliceErrorCount = agentMetricsManageService.getSliceErrorCount(
                lastestCheckTime,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                hostName
        );
        return logSliceErrorCount > 0;
    }
}
