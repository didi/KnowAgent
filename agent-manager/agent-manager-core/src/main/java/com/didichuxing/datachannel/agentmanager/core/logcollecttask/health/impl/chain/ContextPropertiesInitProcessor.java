package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDetailDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthDetailManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain.context.LogCollectTaskHealthCheckContext;

/**
 * context 属性初始化处理器
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 1, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class ContextPropertiesInitProcessor extends BaseProcessor {

    @Override
    protected void process(LogCollectTaskHealthCheckContext context) {

        LogCollectTaskDO logCollectTaskDO = context.getLogCollectTaskDO();
        FileLogCollectPathDO fileLogCollectPathDO = context.getFileLogCollectPathDO();
        HostDO hostDO = context.getHostDO();
        LogCollectTaskHealthDetailManageService logCollectTaskHealthDetailManageService = context.getLogCollectTaskHealthDetailManageService();

        /*
         * set logCollectTaskHealthDetailDO
         */
        LogCollectTaskHealthDetailDO logCollectTaskHealthDetailDO = logCollectTaskHealthDetailManageService.get(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
        if(null == logCollectTaskHealthDetailDO) {
            //初始化
            logCollectTaskHealthDetailDO = logCollectTaskHealthDetailManageService.getInit(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
            logCollectTaskHealthDetailManageService.add(logCollectTaskHealthDetailDO);
            logCollectTaskHealthDetailDO = logCollectTaskHealthDetailManageService.get(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
        }
        context.setLogCollectTaskHealthDetailDO(logCollectTaskHealthDetailDO);

        /*
         * set lastLogCollectTaskMetric
         */
        MetricsLogCollectTaskPO metricsLogCollectTaskPO = context.getMetricsManageService().getLastLogCollectTaskMetric(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
        if(null != metricsLogCollectTaskPO) {
            context.setLastLogCollectTaskMetric(metricsLogCollectTaskPO);
        }

        /*
         * set logCollectTaskHealthCheckTimeEnd
         */
        if(null != metricsLogCollectTaskPO) {
            context.setLogCollectTaskHealthCheckTimeEnd(metricsLogCollectTaskPO.getHeartbeattime());
        }

    }

}
