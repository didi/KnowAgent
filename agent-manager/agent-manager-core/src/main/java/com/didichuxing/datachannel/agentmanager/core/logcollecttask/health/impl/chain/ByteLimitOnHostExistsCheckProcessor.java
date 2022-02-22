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
 * 是否存在采集端出口限流检查
 * @author Ronaldo
 */
@HealthCheckProcessorAnnotation(seq = 9, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class ByteLimitOnHostExistsCheckProcessor extends BaseProcessor {

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
         * 校验 logcollecttask + logpath 在 host 端是否存在采集端出口限流
         */
        boolean byteLimitOnHostExists = checkByteLimitOnHostExists(
                context.getLogCollectTaskDO().getId(),
                context.getFileLogCollectPathDO().getId(),
                context.getHostDO().getHostName(),
                context.getMetricsManageService()
        );
        if (byteLimitOnHostExists) {//存在采集端出口流量阈值限流
            context.setLogCollectTaskHealthLevelEnum(LogCollectTaskHealthInspectionResultEnum.HOST_BYTES_LIMIT_EXISTS.getLogCollectTaskHealthLevelEnum());
            String logCollectTaskHealthDescription = String.format(
                    "%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}",
                    LogCollectTaskHealthInspectionResultEnum.HOST_BYTES_LIMIT_EXISTS.getDescription(),
                    context.getLogCollectTaskDO().getId(),
                    context.getFileLogCollectPathDO().getId(),
                    context.getHostDO().getHostName()
            );
            context.setLogCollectTaskHealthDescription(logCollectTaskHealthDescription);
            context.setLogCollectTaskHealthInspectionResultEnum(LogCollectTaskHealthInspectionResultEnum.HOST_BYTES_LIMIT_EXISTS);
        }
    }

    /**
     * 校验 logcollecttask + logpath 在 host 端是否存在采集端出口流量阈值限流
     *
     * @param logCollectTaskId     日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param hostName             主机名
     * @param metricsManageService MetricsManageService 对象
     * @return true：存在出口流量阈值限流 false：不存在出口流量阈值限流
     */
    private boolean checkByteLimitOnHostExists(
            Long logCollectTaskId,
            Long fileLogCollectPathId,
            String hostName,
            MetricsManageService metricsManageService
    ) {
        /*
         * 获取近 LogCollectTaskHealthCheckConstant.HOST_BYTE_LIMIT_CHECK_LASTEST_MS_THRESHOLD 时间范围内 logCollectTaskId+fileLogCollectPathId+hostName 指标集中，
         * 总限流时间是否超过阈值 LogCollectTaskHealthCheckConstant.HOST_BYTE_LIMIT_MS_THRESHOLD
         */
        Long currentTime = System.currentTimeMillis();
        Long startTime = currentTime - LogCollectTaskHealthCheckConstant.HOST_BYTE_LIMIT_CHECK_LASTEST_MS_THRESHOLD;
        Object limitMsObj = metricsManageService.getAggregationQueryPerLogCollectTskAndPathAndHostNameFromMetricsLogCollectTask(
                logCollectTaskId,
                fileLogCollectPathId,
                hostName,
                startTime,
                currentTime,
                AggregationCalcFunctionEnum.SUM.getValue(),
                "limitTime"
        );
        Long limitMs = 0L;
        if(null != limitMsObj) {
            limitMs = Long.valueOf(limitMsObj.toString());
        }
        //主机cpu限流时长 单位：ms
        return limitMs > LogCollectTaskHealthCheckConstant.HOST_BYTE_LIMIT_MS_THRESHOLD;
    }

}
