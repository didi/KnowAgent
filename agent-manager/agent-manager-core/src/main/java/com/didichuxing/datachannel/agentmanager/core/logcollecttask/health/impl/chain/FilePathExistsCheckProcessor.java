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
 * 文件是否存在检查
 * @author Ronaldo
 */
@HealthCheckProcessorAnnotation(seq = 2, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class FilePathExistsCheckProcessor implements Processor {

    @Override
    public void process(Context context, ProcessorChain chain) {
        LogCollectTaskHealthCheckContext logCollectTaskHealthCheckContext = (LogCollectTaskHealthCheckContext) context;
        LogCollectTaskDO logCollectTaskDO = logCollectTaskHealthCheckContext.getLogCollectTaskDO();
        FileLogCollectPathDO fileLogCollectPathDO = logCollectTaskHealthCheckContext.getFileLogCollectPathDO();
        HostDO hostDO = logCollectTaskHealthCheckContext.getHostDO();
        Long logCollectTaskHealthCheckTimeEnd = logCollectTaskHealthCheckContext.getLogCollectTaskHealthCheckTimeEnd();
        AgentMetricsManageService agentMetricsManageService = logCollectTaskHealthCheckContext.getAgentMetricsManageService();
        Map<Long, Long> fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap = logCollectTaskHealthCheckContext.getFileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap();

        /*
         * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在
         */
        boolean filePathExists = checkFilePathExists(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd, fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap, agentMetricsManageService);
        // 如果文件存在
        if (filePathExists) {
            logCollectTaskHealthCheckContext.setFilePathExistsCheckHealthyCount(logCollectTaskHealthCheckContext.getFilePathExistsCheckHealthyCount() + 1);
            chain.process(context, chain);
            return;
        }
        // 如果不存在
        LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_NOT_EXISTS.getLogCollectTaskHealthLevelEnum();
        String logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_NOT_EXISTS.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
        logCollectTaskHealthCheckContext.setLogCollectTaskHealthLevelEnum(logCollectTaskHealthLevelEnum);
        logCollectTaskHealthCheckContext.setLogCollectTaskHealthDescription(logCollectTaskHealthDescription);
    }

    /**
     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在
     *
     * @param logCollectTaskId                                              日志采集任务 id
     * @param fileLogCollectPathId                                          日志采集路径 id
     * @param hostName                                                      主机名
     * @param logCollectTaskHealthCheckTimeEnd                              日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一秒
     * @param fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap filePathId : LastestFilePathExistsCheckHealthyTime
     * @return 返回logCollectTaskId+fileLogCollectPathId在host上是否存在 true：存在 false：不存在
     */
    private boolean checkFilePathExists(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long logCollectTaskHealthCheckTimeEnd, Map<Long, Long> fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap, AgentMetricsManageService agentMetricsManageService) {
        /*
         * 获取自上次"日志采集路径存在"健康点 ~ 当前时间，logCollectTaskId+fileLogCollectPathId在host上是否存在对应待采集日志文件
         */
        Long lastestCheckTime = fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap.get(fileLogCollectPathId);
        if (null == lastestCheckTime) {
            throw new ServiceException(
                    String.format("FileLogCollectPath={id=%d}对应FilePathExistsCheckHealthyTime不存在", fileLogCollectPathId),
                    ErrorCodeEnum.LOGCOLLECTTASK_HEALTH_FILE_PATH_EXISTS_CHECK_HEALTHY_TIME_NOT_EXISTS.getCode()
            );
        }
        Integer filePathNotExistsCount = agentMetricsManageService.getFilePathNotExistsCountByTimeFrame(
                lastestCheckTime,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                hostName
        );
        return filePathNotExistsCount == 0;
    }

}
