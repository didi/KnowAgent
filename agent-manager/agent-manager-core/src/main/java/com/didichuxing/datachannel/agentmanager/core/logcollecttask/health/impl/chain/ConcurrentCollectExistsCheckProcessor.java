package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.constant.LogCollectTaskHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.AggregationCalcFunctionEnum;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain.context.LogCollectTaskHealthCheckContext;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;

/**
 * 是否存在多 agent 并发采集检查
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 7, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class ConcurrentCollectExistsCheckProcessor extends BaseProcessor {

    @Override
    protected void process(LogCollectTaskHealthCheckContext context) {
        /*
         * 校验日志采集任务是否为红 黄
         */
        if(
                context.getLogCollectTaskHealthLevelEnum().equals(LogCollectTaskHealthLevelEnum.RED) ||
                        context.getLogCollectTaskHealthLevelEnum().equals(LogCollectTaskHealthLevelEnum.YELLOW)
        ) {
            return;
        }
        boolean concurrentCollectExists = checkConcurrentCollectExists(
                context.getLogCollectTaskDO().getId(),
                context.getFileLogCollectPathDO().getId(),
                context.getHostDO().getHostName(),
                System.currentTimeMillis(),
                context.getMetricsManageService()
        );
        if (concurrentCollectExists) {// 存在并发采集
            setLogCollectTaskHealthInfo(
                    context,
                    LogCollectTaskHealthInspectionResultEnum.LOG_PATH_CONCURRENT_COLLECT,
                    context.getHostDO().getHostName(),
                    context.getFileLogCollectPathDO().getPath()
            );
        }
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
    private boolean checkConcurrentCollectExists(
            Long logCollectTaskId,
            Long fileLogCollectPathId,
            String hostName,
            Long logCollectTaskHealthCheckTimeEnd,
            MetricsManageService metricsManageService
    ) {
        /*
         * 获取近 LogCollectTaskHealthCheckConstant.HOST_CPU_LIMIT_CHECK_LASTEST_MS_THRESHOLD 时间范围内 logCollectTaskId + fileLogCollectPathId + hostName 心跳数，
         * 心跳数量 > LogCollectTaskHealthCheckConstant.HOST_CPU_LIMIT_CHECK_LASTEST_MS_THRESHOLD，表示 logCollectTaskId+fileLogCollectPathId 在 host 上是否存在多 agent 并发采集
         */
        Long startTime = logCollectTaskHealthCheckTimeEnd - LogCollectTaskHealthCheckConstant.CONCURRENT_COLLECT_CHECK_LASTEST_MS_THRESHOLD;
        Object heartbeatTimesObj = metricsManageService.getAggregationQueryPerLogCollectTskAndPathAndHostNameFromMetricsLogCollectTask(
                logCollectTaskId,
                fileLogCollectPathId,
                hostName,
                startTime,
                logCollectTaskHealthCheckTimeEnd,
                AggregationCalcFunctionEnum.COUNT.getValue(),
                "*"
        );
        Long heartbeatTimes = 0L;
        if(null != heartbeatTimesObj) {
            heartbeatTimes = Long.valueOf(heartbeatTimesObj.toString());
        }
        return heartbeatTimes > LogCollectTaskHealthCheckConstant.CONCURRENT_COLLECT_CHECK_HEARTBEAT_TIMES_THRESHOLD;
    }

}
