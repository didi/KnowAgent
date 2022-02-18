package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.chain.ProcessorChain;
import com.didichuxing.datachannel.agentmanager.common.constant.LogCollectTaskHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain.context.LogCollectTaskHealthCheckContext;

/**
 * 是否存在心跳检查
 * @author Ronaldo
 */
@HealthCheckProcessorAnnotation(seq = 1, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class LogCollectTaskHeartbeatCheckProcessor implements Processor {


    @Override
    public void process(Context context, ProcessorChain chain) {
        LogCollectTaskHealthCheckContext logCollectTaskHealthCheckContext = (LogCollectTaskHealthCheckContext) context;
        LogCollectTaskDO logCollectTaskDO = logCollectTaskHealthCheckContext.getLogCollectTaskDO();
        FileLogCollectPathDO fileLogCollectPathDO = logCollectTaskHealthCheckContext.getFileLogCollectPathDO();
        HostDO hostDO = logCollectTaskHealthCheckContext.getHostDO();
        Long logCollectTaskHealthCheckTimeEnd = logCollectTaskHealthCheckContext.getLogCollectTaskHealthCheckTimeEnd();
        AgentMetricsManageService agentMetricsManageService = logCollectTaskHealthCheckContext.getAgentMetricsManageService();

        /*
         * 校验在距当前时间的心跳存活判定周期内，logCollectTaskId+fileLogCollectPathId+hostName是否存在心跳
         */
        boolean alive = checkAliveByHeartbeat(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd, agentMetricsManageService);
        // 如果心跳存活
        if (alive) {
            chain.process(context, chain);
            return;
        }
        // 如果不存活
        LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_IN_HOST_HEART_BEAT_NOT_EXISTS.getLogCollectTaskHealthLevelEnum();
        String logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_IN_HOST_HEART_BEAT_NOT_EXISTS.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
        logCollectTaskHealthCheckContext.setLogCollectTaskHealthLevelEnum(logCollectTaskHealthLevelEnum);
        logCollectTaskHealthCheckContext.setLogCollectTaskHealthDescription(logCollectTaskHealthDescription);
    }

    /**
     * 校验在距当前时间的心跳存活判定周期内，logCollectTaskId+fileLogCollectPathId+hostName是否存在心跳
     *
     * @param logCollectTaskId                 日志采集任务 id
     * @param fileLogCollectPathId             日志采集路径 id
     * @param logCollectTaskHostName           日志采集任务对应主机名
     * @param logCollectTaskHealthCheckTimeEnd 日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一秒
     * @return 距当前时间的心跳存活判定周期内，logCollectTaskId+fileLogCollectPathId+hostName是否存在心跳 true：存在 心跳 false：不存在心跳
     */
    private boolean checkAliveByHeartbeat(Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName, Long logCollectTaskHealthCheckTimeEnd, AgentMetricsManageService agentMetricsManageService) {
        /*
         * 获取近 LogCollectTaskHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD 时间范围内 logCollectTaskId + fileLogCollectPathId + hostName 心跳数，
         * 心跳数量 == 0，表示 logCollectTaskId+fileLogCollectPathId 在 host 上不存在心跳
         */
        Long heartbeatTimes = agentMetricsManageService.getHeartbeatTimesByTimeFrame(
                logCollectTaskHealthCheckTimeEnd - LogCollectTaskHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                logCollectTaskHostName
        );
        return !heartbeatTimes.equals(0L);
    }
}
