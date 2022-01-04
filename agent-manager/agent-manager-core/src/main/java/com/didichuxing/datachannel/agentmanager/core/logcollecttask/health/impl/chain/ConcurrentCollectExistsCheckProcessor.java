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
 * 是否存在多 agent 并发采集检查
 * @author Ronaldo
 */
@HealthCheckProcessorAnnotation(seq = 7, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class ConcurrentCollectExistsCheckProcessor implements Processor {


    @Override
    public void process(Context context, ProcessorChain chain) {
        LogCollectTaskHealthCheckContext logCollectTaskHealthCheckContext = (LogCollectTaskHealthCheckContext) context;
        LogCollectTaskDO logCollectTaskDO = logCollectTaskHealthCheckContext.getLogCollectTaskDO();
        FileLogCollectPathDO fileLogCollectPathDO = logCollectTaskHealthCheckContext.getFileLogCollectPathDO();
        HostDO hostDO = logCollectTaskHealthCheckContext.getHostDO();
        Long logCollectTaskHealthCheckTimeEnd = logCollectTaskHealthCheckContext.getLogCollectTaskHealthCheckTimeEnd();
        AgentMetricsManageService agentMetricsManageService = logCollectTaskHealthCheckContext.getAgentMetricsManageService();
        boolean concurrentCollectExists = checkConcurrentCollectExists(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskHealthCheckTimeEnd, agentMetricsManageService);
        // 不存在agent并发采集
        if (!concurrentCollectExists) {
            chain.process(context, chain);
            return;
        }
        // 存在并发采集
        LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_CONCURRENT_COLLECT.getLogCollectTaskHealthLevelEnum();
        String logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_CONCURRENT_COLLECT.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
        logCollectTaskHealthCheckContext.setLogCollectTaskHealthLevelEnum(logCollectTaskHealthLevelEnum);
        logCollectTaskHealthCheckContext.setLogCollectTaskHealthDescription(logCollectTaskHealthDescription);
    }

    /**
     * 校验 logCollectTaskId+fileLogCollectPathId 在host上是否存在多 agent 并发采集
     *
     * @param logCollectTaskId                 日志采集任务 id
     * @param fileLogCollectPathId             日志采集路径 id
     * @param hostName                         主机名
     * @param logCollectTaskHealthCheckTimeEnd 日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一毫秒
     * @return 返回 logCollectTaskId+fileLogCollectPathId 在host上是否存在多 agent 并发采集 true：存在 并发采集 false：不存在 并发采集
     */
    private boolean checkConcurrentCollectExists(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long logCollectTaskHealthCheckTimeEnd, AgentMetricsManageService agentMetricsManageService) {
        /*
         * 获取近 LogCollectTaskHealthCheckConstant.HOST_CPU_LIMIT_CHECK_LASTEST_MS_THRESHOLD 时间范围内 logCollectTaskId + fileLogCollectPathId + hostName 心跳数，
         * 心跳数量 > LogCollectTaskHealthCheckConstant.HOST_CPU_LIMIT_CHECK_LASTEST_MS_THRESHOLD，表示 logCollectTaskId+fileLogCollectPathId 在 host 上是否存在多 agent 并发采集
         */
        Long startTime = logCollectTaskHealthCheckTimeEnd - LogCollectTaskHealthCheckConstant.CONCURRENT_COLLECT_CHECK_LASTEST_MS_THRESHOLD;
        Long heartbeatTimes = agentMetricsManageService.getHeartbeatTimesByTimeFrame(
                startTime,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                hostName
        );
        return heartbeatTimes > LogCollectTaskHealthCheckConstant.CONCURRENT_COLLECT_CHECK_HEARTBEAT_TIMES_THRESHOLD;
    }
}
