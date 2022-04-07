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
 * 是否存在心跳检查
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 2, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class LogCollectTaskHeartbeatCheckProcessor extends BaseProcessor {

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
        /*
         *  校验在距当前时间的心跳存活判定周期内，logCollectTaskId+fileLogCollectPathId+hostName是否存在心跳
         */
        boolean alive = checkAliveByHeartbeat(
                context.getLogCollectTaskDO().getId(),
                context.getFileLogCollectPathDO().getId(),
                context.getHostDO().getHostName(),
                System.currentTimeMillis(),
                context.getMetricsManageService()
        );
        if(!alive) {
            context.setLogCollectTaskHealthLevelEnum(LogCollectTaskHealthInspectionResultEnum.LOG_PATH_IN_HOST_HEART_BEAT_NOT_EXISTS.getLogCollectTaskHealthLevelEnum());
            String logCollectTaskHealthDescription = String.format(
                    "%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}",
                    LogCollectTaskHealthInspectionResultEnum.LOG_PATH_IN_HOST_HEART_BEAT_NOT_EXISTS.getDescription(),
                    context.getLogCollectTaskDO().getId(),
                    context.getFileLogCollectPathDO().getId(),
                    context.getHostDO().getHostName()
            );
            context.setLogCollectTaskHealthDescription(logCollectTaskHealthDescription);
            context.setLogCollectTaskHealthInspectionResultEnum(LogCollectTaskHealthInspectionResultEnum.LOG_PATH_IN_HOST_HEART_BEAT_NOT_EXISTS);
        }
    }

    /**
     * 校验在距当前时间的心跳存活判定周期内，logCollectTaskId+fileLogCollectPathId+hostName是否存在心跳
     * @param logCollectTaskId                 日志采集任务 id
     * @param fileLogCollectPathId             日志采集路径 id
     * @param logCollectTaskHostName           日志采集任务对应主机名
     * @param logCollectTaskHealthCheckTimeEnd 日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一秒
     * @param metricsManageService 指标管理服务对象
     * @return 距当前时间的心跳存活判定周期内，logCollectTaskId+fileLogCollectPathId+hostName是否存在心跳 true：存在 心跳 false：不存在心跳
     */
    private boolean checkAliveByHeartbeat(
            Long logCollectTaskId,
            Long fileLogCollectPathId,
            String logCollectTaskHostName,
            Long logCollectTaskHealthCheckTimeEnd,
            MetricsManageService metricsManageService
    ) {
        /*
         * 获取近 LogCollectTaskHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD 时间范围内 logCollectTaskId + fileLogCollectPathId + hostName 心跳数，
         * 心跳数量 == 0，表示 logCollectTaskId+fileLogCollectPathId 在 host 上不存在心跳
         */
        Object heartbeatTimesObj = metricsManageService.getAggregationQueryPerLogCollectTskAndPathAndHostNameFromMetricsLogCollectTask(
                logCollectTaskId,
                fileLogCollectPathId,
                logCollectTaskHostName,
                logCollectTaskHealthCheckTimeEnd - LogCollectTaskHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD,
                logCollectTaskHealthCheckTimeEnd,
                AggregationCalcFunctionEnum.COUNT.getValue(),
                "*"
        );
        Long heartbeatTimes = 0L;
        if(null != heartbeatTimesObj) {
            heartbeatTimes = Long.valueOf(heartbeatTimesObj.toString());
        }
        return heartbeatTimes != 0L;
    }

}
