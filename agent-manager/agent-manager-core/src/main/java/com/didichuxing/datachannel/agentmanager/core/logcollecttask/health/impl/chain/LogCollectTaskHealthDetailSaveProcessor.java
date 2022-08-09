package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDetailDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain.context.LogCollectTaskHealthCheckContext;

/**
 * LogCollectTaskHealthDetailDO对象信息保存处理器
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 11, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class LogCollectTaskHealthDetailSaveProcessor extends BaseProcessor {

    @Override
    protected void process(LogCollectTaskHealthCheckContext context) {
        if(
                context.getLogCollectTaskHealthLevelEnum().equals(LogCollectTaskHealthLevelEnum.YELLOW) ||
                        context.getLogCollectTaskHealthLevelEnum().equals(LogCollectTaskHealthLevelEnum.GREEN)
        ) {
            /*
             * 健康度为 yellow | green，不影响数据完整性，获取该采集任务在path、host 最近一次采集时间作为完整性时间，最近一次心跳时间作为各健康度时间戳
             */
            MetricsLogCollectTaskPO metricsLogCollectTaskPO = context.getLastLogCollectTaskMetric();
            LogCollectTaskHealthDetailDO logCollectTaskHealthDetailDO = context.getLogCollectTaskHealthDetailDO();
            logCollectTaskHealthDetailDO.setCollectDqualityTime(metricsLogCollectTaskPO.getBusinesstimestamp());
            logCollectTaskHealthDetailDO.setLogSliceCheckHealthyHeartbeatTime(metricsLogCollectTaskPO.getHeartbeattime());
            logCollectTaskHealthDetailDO.setTooLargeTruncateCheckHealthyHeartbeatTime(metricsLogCollectTaskPO.getHeartbeattime());
            logCollectTaskHealthDetailDO.setFilePathExistsCheckHealthyHeartbeatTime(metricsLogCollectTaskPO.getHeartbeattime());
            logCollectTaskHealthDetailDO.setFileDisorderCheckHealthyHeartbeatTime(metricsLogCollectTaskPO.getHeartbeattime());
            context.getLogCollectTaskHealthDetailManageService().update(logCollectTaskHealthDetailDO);
        }
    }

}
