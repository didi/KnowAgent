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
 * 文件是否存在检查
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 3, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class FilePathExistsCheckProcessor extends BaseProcessor {

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
         *  校验logCollectTaskId+fileLogCollectPathId在host上是否存在
         */
        boolean filePathExists = checkFilePathExists(
                context.getLogCollectTaskDO().getId(),
                context.getFileLogCollectPathDO().getId(),
                context.getHostDO().getHostName(),
                context.getLogCollectTaskHealthDetailDO().getFilePathExistsCheckHealthyHeartbeatTime(),
                context.getLogCollectTaskHealthCheckTimeEnd(),
                context.getMetricsManageService()
        );
        if(!filePathExists) {
            setLogCollectTaskHealthInfo(
                    context,
                    LogCollectTaskHealthInspectionResultEnum.LOG_PATH_NOT_EXISTS,
                    context.getFileLogCollectPathDO().getPath(),
                    context.getHostDO().getHostName()
            );
        }
    }

    /**
     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在
     *
     * @param logCollectTaskId 日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param hostName 日志采集任务运行主机名
     * @param healthCheckTimeStart 心跳开始时间戳
     * @param healthCheckTimeEnd 心跳结束时间戳
     * @param metricsManageService MetricsManageService 对象
     * @return 返回logCollectTaskId+fileLogCollectPathId在host上是否存在 true：存在 false：不存在
     */
    private boolean checkFilePathExists(
            Long logCollectTaskId,
            Long fileLogCollectPathId,
            String hostName,
            Long healthCheckTimeStart,
            Long healthCheckTimeEnd,
            MetricsManageService metricsManageService
    ) {
        /*
         * 获取自上次"日志采集路径存在"健康点 ~ 当前时间，logCollectTaskId+fileLogCollectPathId在host上是否存在对应待采集日志文件
         */
        Object filePathNotExistsCountObj = metricsManageService.getAggregationQueryPerLogCollectTskAndPathAndHostNameWithConditionFromMetricsLogCollectTask(
                logCollectTaskId,
                fileLogCollectPathId,
                hostName,
                healthCheckTimeStart,
                healthCheckTimeEnd,
                MetricFieldEnum.LOG_COLLECT_TASK_COLLECT_PATH_IS_EXISTS.getFieldName(),
                OperatorEnum.EQ.getOperatorType(),
                0,
                AggregationCalcFunctionEnum.COUNT.getValue(),
                "*"
        );
        Long filePathNotExistsCount = 0L;
        if(null != filePathNotExistsCountObj) {
            filePathNotExistsCount = Long.valueOf(filePathNotExistsCountObj.toString());
        }
        return filePathNotExistsCount == 0L;
    }

}
