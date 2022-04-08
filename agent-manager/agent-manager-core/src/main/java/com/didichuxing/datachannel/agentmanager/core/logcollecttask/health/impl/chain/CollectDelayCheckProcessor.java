package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain.context.LogCollectTaskHealthCheckContext;

/**
 * 是否存在采集延迟检查
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 8, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class CollectDelayCheckProcessor extends BaseProcessor {

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
        if(context.getCheckCollectDelay()) {
            boolean collectDelay = checkCollectDelay(
                    context.getLogCollectTaskDO().getCollectDelayThresholdMs(),
                    context.getLastLogCollectTaskMetric()
            );
            if (collectDelay) {// 存在采集延迟
                /*
                 * 下游接收端是否存在写入失败
                 */
                setLogCollectTaskHealthInfo(context, LogCollectTaskHealthInspectionResultEnum.LOG_PATH_COLLECT_DELAYED);
            }
        }
    }

    /**
     * 校验 logCollectTaskId+fileLogCollectPathId 在host上是否存在采集延迟
     * @param collectDelayThresholdMs 日志采集任务业务时间采集延时阈值
     * @param metricsLogCollectTaskPO 最近一次 LogCollectTask 采样指标对象
     * @return 返回 logCollectTaskId+fileLogCollectPathId 在host上是否存在采集延迟 true：存在 采集延时 false：不存在 采集延时
     */
    private boolean checkCollectDelay(
            Long collectDelayThresholdMs,
            MetricsLogCollectTaskPO metricsLogCollectTaskPO
    ) {
        /*
         * System.currentTimeMillis() - currentCollectTime > collectDelayThresholdMs ? true : false
         */
        return System.currentTimeMillis() - metricsLogCollectTaskPO.getBusinesstimestamp() > collectDelayThresholdMs;
    }

}
