package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.OperatorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.AggregationCalcFunctionEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricFieldEnum;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain.context.LogCollectTaskHealthCheckContext;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;

/**
 * 日志异常截断检查
 * @author Ronaldo
 */
@HealthCheckProcessorAnnotation(seq = 6, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class TooLargeTruncateExistsCheckProcessor extends BaseProcessor {

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
         * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在日志被异常截断
         */
        boolean tooLargeTruncateExists = checkTooLargeTruncateExists(
                context.getLogCollectTaskDO().getId(),
                context.getFileLogCollectPathDO().getId(),
                context.getHostDO().getHostName(),
                context.getLogCollectTaskHealthDetailDO().getTooLargeTruncateCheckHealthyHeartbeatTime(),
                context.getLogCollectTaskHealthCheckTimeEnd(),
                context.getMetricsManageService()
        );
        if (tooLargeTruncateExists) {// 存在异常截断
            setLogCollectTaskHealthInfo(
                    context,
                    LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SIZE_OVERRUN_TRUNCATE_EXISTS,
                    context.getHostDO().getHostName(),
                    context.getFileLogCollectPathDO().getPath()
            );
        }
    }

    /**
     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在日志被异常截断
     *
     * @param logCollectTaskId 日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param hostName 日志采集任务运行主机名
     * @param healthCheckTimeStart 心跳开始时间戳
     * @param healthCheckTimeEnd 心跳结束时间戳
     * @param metricsManageService MetricsManageService 对象
     * @return 返回logCollectTaskId+fileLogCollectPathId在host上是否存在日志被异常截断 true：存在 异常截断 false：不存在 异常截断
     */
    private boolean checkTooLargeTruncateExists(
            Long logCollectTaskId,
            Long fileLogCollectPathId,
            String hostName,
            Long healthCheckTimeStart,
            Long healthCheckTimeEnd,
            MetricsManageService metricsManageService
    ) {
        /*
         * 获取自上次"异常截断"健康点 ~ 当前时间，logCollectTaskId+fileLogCollectPathId在host上是否存在异常截断
         */
        Object tooLargeTruncateCountObj = metricsManageService.getAggregationQueryPerLogCollectTskAndPathAndHostNameWithConditionFromMetricsLogCollectTask(
                logCollectTaskId,
                fileLogCollectPathId,
                hostName,
                healthCheckTimeStart,
                healthCheckTimeEnd,
                MetricFieldEnum.LOG_COLLECT_TASK_TOO_LARGE_TRUNCATE_NUM.getFieldName(),
                OperatorEnum.GT.getOperatorType(),
                0,
                AggregationCalcFunctionEnum.COUNT.getValue(),
                "*"
        );
        Long tooLargeTruncateCount = 0L;
        if(null != tooLargeTruncateCountObj) {
            tooLargeTruncateCount = Long.valueOf(tooLargeTruncateCountObj.toString());
        }
        return tooLargeTruncateCount != 0L;
    }

}
