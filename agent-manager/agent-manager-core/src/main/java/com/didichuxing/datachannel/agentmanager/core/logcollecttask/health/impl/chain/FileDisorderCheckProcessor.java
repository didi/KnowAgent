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
 * 文件乱序检查
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 5, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class FileDisorderCheckProcessor extends BaseProcessor {

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
         * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在乱序
         */
        boolean fileDisorder = checkFileDisorder(
                context.getLogCollectTaskDO().getId(),
                context.getFileLogCollectPathDO().getId(),
                context.getHostDO().getHostName(),
                context.getLogCollectTaskHealthDetailDO().getFileDisorderCheckHealthyHeartbeatTime(),
                context.getLogCollectTaskHealthCheckTimeEnd(),
                context.getMetricsManageService()
        );
        if (fileDisorder) {
            // 存在乱序
            setLogCollectTaskHealthInfo(
                    context,
                    LogCollectTaskHealthInspectionResultEnum.LOG_PATH_DISORDER,
                    context.getHostDO().getHostName(),
                    context.getFileLogCollectPathDO().getPath()
            );
        }
    }

    /**
     * 校验 logCollectTaskId+fileLogCollectPathId 在 host 上是否存在乱序
     *
     * @param logCollectTaskId 日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param hostName 日志采集任务运行主机名
     * @param healthCheckTimeStart 心跳开始时间戳
     * @param healthCheckTimeEnd 心跳结束时间戳
     * @param metricsManageService MetricsManageService 对象
     * @return logCollectTaskId+fileLogCollectPathId 在 host 上是否存在乱序 true：存在 乱序 false：不存在 乱序
     */
    private boolean checkFileDisorder(
            Long logCollectTaskId,
            Long fileLogCollectPathId,
            String hostName,
            Long healthCheckTimeStart,
            Long healthCheckTimeEnd,
            MetricsManageService metricsManageService
    ) {
        /*
         * 获取自上次"文件乱序"健康点 ~ 当前时间，logCollectTaskId+fileLogCollectPathId在host上是否存在日志乱序
         */
        Object fileDisorderCountObj = metricsManageService.getAggregationQueryPerLogCollectTskAndPathAndHostNameWithConditionFromMetricsLogCollectTask(
                logCollectTaskId,
                fileLogCollectPathId,
                hostName,
                healthCheckTimeStart,
                healthCheckTimeEnd,
                MetricFieldEnum.LOG_COLLECT_TASK_DISORDER_EXISTS.getFieldName(),
                OperatorEnum.EQ.getOperatorType(),
                1,
                AggregationCalcFunctionEnum.COUNT.getValue(),
                "*"

        );
        Long fileDisorderCount = 0L;
        if(null != fileDisorderCountObj) {
            fileDisorderCount = Long.valueOf(fileDisorderCountObj.toString());
        }
        return fileDisorderCount != 0L;
    }

}
