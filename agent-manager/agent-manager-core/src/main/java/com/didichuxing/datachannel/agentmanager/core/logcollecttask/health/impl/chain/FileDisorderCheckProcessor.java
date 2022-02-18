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
 * 文件乱序检查
 * @author Ronaldo
 */
@HealthCheckProcessorAnnotation(seq = 3, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class FileDisorderCheckProcessor implements Processor {

    @Override
    public void process(Context context, ProcessorChain chain) {
        LogCollectTaskHealthCheckContext logCollectTaskHealthCheckContext = (LogCollectTaskHealthCheckContext) context;
        LogCollectTaskDO logCollectTaskDO = logCollectTaskHealthCheckContext.getLogCollectTaskDO();
        FileLogCollectPathDO fileLogCollectPathDO = logCollectTaskHealthCheckContext.getFileLogCollectPathDO();
        HostDO hostDO = logCollectTaskHealthCheckContext.getHostDO();
        Long logCollectTaskHealthCheckTimeEnd = logCollectTaskHealthCheckContext.getLogCollectTaskHealthCheckTimeEnd();
        AgentMetricsManageService agentMetricsManageService = logCollectTaskHealthCheckContext.getAgentMetricsManageService();
        Map<Long, Long> fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap = logCollectTaskHealthCheckContext.getFileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap();

        /*
         * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在乱序
         */
        boolean fileDisorder = checkFileDisorder(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd, fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap, agentMetricsManageService);
        // 不存在乱序
        if (!fileDisorder) {
            logCollectTaskHealthCheckContext.setFileDisorderCheckHealthyCount(logCollectTaskHealthCheckContext.getFileDisorderCheckHealthyCount() + 1);
            chain.process(context, chain);
            return;
        }
        // 存在乱序
        LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_DISORDER.getLogCollectTaskHealthLevelEnum();
        String logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_DISORDER.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
        logCollectTaskHealthCheckContext.setLogCollectTaskHealthLevelEnum(logCollectTaskHealthLevelEnum);
        logCollectTaskHealthCheckContext.setLogCollectTaskHealthDescription(logCollectTaskHealthDescription);
    }

    /**
     * 校验 logCollectTaskId+fileLogCollectPathId 在 host 上是否存在乱序
     *
     * @param logCollectTaskId                                            日志采集任务 id
     * @param fileLogCollectPathId                                        日志采集路径 id
     * @param hostName                                                    主机名
     * @param logCollectTaskHealthCheckTimeEnd                            日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一毫秒
     * @param fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap filePathId : LastestFileDisorderCheckHealthyTime
     * @return logCollectTaskId+fileLogCollectPathId 在 host 上是否存在乱序 true：存在 乱序 false：不存在 乱序
     */
    private boolean checkFileDisorder(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long logCollectTaskHealthCheckTimeEnd, Map<Long, Long> fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap, AgentMetricsManageService agentMetricsManageService) {
        /*
         * 获取自上次"文件乱序"健康点 ~ 当前时间，logCollectTaskId+fileLogCollectPathId在host上是否存在日志乱序
         */
        Long lastestCheckTime = fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap.get(fileLogCollectPathId);
        if (null == lastestCheckTime) {
            throw new ServiceException(
                    String.format("FileLogCollectPath={id=%d}对应FileDisorderCheckHealthyTime不存在", fileLogCollectPathId),
                    ErrorCodeEnum.LOGCOLLECTTASK_HEALTH_FILE_DISORDER_CHECK_HEALTHY_TIME_NOT_EXISTS.getCode()
            );
        }
        Integer fileDisorderCount = agentMetricsManageService.getFileDisorderCountByTimeFrame(
                lastestCheckTime,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                hostName
        );
        return fileDisorderCount > 0;
    }

}
