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
 * 日志切片是否配置错误检查
 * @author Ronaldo
 */
@HealthCheckProcessorAnnotation(seq = 4, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class LogSliceErrorExistsCheckProcessor extends BaseProcessor {

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
         * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在日志切片配置错误
         */
        boolean errorLogsExists = checkLogSliceErrorExists(
                context.getLogCollectTaskDO().getId(),
                context.getFileLogCollectPathDO().getId(),
                context.getHostDO().getHostName(),
                context.getLogCollectTaskHealthDetailDO().getLogSliceCheckHealthyHeartbeatTime(),
                context.getLogCollectTaskHealthCheckTimeEnd(),
                context.getMetricsManageService()
        );
        if (errorLogsExists) {// 存在日志切片错误
            setLogCollectTaskHealthInfo(
                    context,
                    LogCollectTaskHealthInspectionResultEnum.LOG_PATH_LOG_SLICE_ERROR_EXISTS,
                    context.getHostDO().getHostName(),
                    context.getFileLogCollectPathDO().getPath()
            );
        }
    }

    /**
     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在日志切片错误
     *
     * @param logCollectTaskId 日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param hostName 日志采集任务运行主机名
     * @param healthCheckTimeStart 心跳开始时间戳
     * @param healthCheckTimeEnd 心跳结束时间戳
     * @param metricsManageService MetricsManageService 对象
     * @return 返回logCollectTaskId+fileLogCollectPathId在host上是否存在日志切片错误
     */
    private boolean checkLogSliceErrorExists(
            Long logCollectTaskId,
            Long fileLogCollectPathId,
            String hostName,
            Long healthCheckTimeStart,
            Long healthCheckTimeEnd,
            MetricsManageService metricsManageService
    ) {
        /*
         * 获取自上次"错误日志输出存在"健康点 ~ 当前时间，logCollectTaskId+fileLogCollectPathId在host上是否存在日志切片错误
         */
        Object sliceErrorExistsObj = metricsManageService.getAggregationQueryPerLogCollectTskAndPathAndHostNameWithConditionFromMetricsLogCollectTask(
                logCollectTaskId,
                fileLogCollectPathId,
                hostName,
                healthCheckTimeStart,
                healthCheckTimeEnd,
                MetricFieldEnum.LOG_COLLECT_TASK_SLICE_ERROR_EXISTS.getFieldName(),
                OperatorEnum.EQ.getOperatorType(),
                1,
                AggregationCalcFunctionEnum.COUNT.getValue(),
                "*"
        );
        Long sliceErrorExists = 0L;
        if(null != sliceErrorExistsObj) {
            sliceErrorExists = Long.valueOf(sliceErrorExistsObj.toString());
        }
        return sliceErrorExists != 0L;
    }

}
