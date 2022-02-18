package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDetailDO;
import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.chain.ProcessorChain;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthDetailManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain.context.LogCollectTaskHealthCheckContext;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsLogCollectTaskService;

/**
 * 记录采集完整性时间处理器
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 9, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class CollectDqualityTimeProcessor implements Processor {

    @Override
    public void process(Context context, ProcessorChain chain) {
        LogCollectTaskHealthCheckContext logCollectTaskHealthCheckContext = (LogCollectTaskHealthCheckContext) context;
        LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum = logCollectTaskHealthCheckContext.getLogCollectTaskHealthLevelEnum();
        if(logCollectTaskHealthLevelEnum.equals(LogCollectTaskHealthLevelEnum.RED)) {
            /*
             * 健康度为 red，此时无须记录该采集任务在path、host 完整性时间，完整性时间采用上一次非 red 时对应完整性时间
             */
            return;
        } else {
            /*
             * 健康度为 yellow | green，不影响数据完整性，获取该采集任务在path、host 最近一次采集时间作为完整性时间
             */
            LogCollectTaskDO logCollectTaskDO = logCollectTaskHealthCheckContext.getLogCollectTaskDO();
            FileLogCollectPathDO fileLogCollectPathDO = logCollectTaskHealthCheckContext.getFileLogCollectPathDO();
            HostDO hostDO = logCollectTaskHealthCheckContext.getHostDO();
            MetricsLogCollectTaskService metricsLogCollectTaskService = logCollectTaskHealthCheckContext.getMetricsLogCollectTaskService();
            Long lastCollectBusinessTime = metricsLogCollectTaskService.getLastCollectBusinessTime(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
            LogCollectTaskHealthDetailManageService logCollectTaskHealthDetailManageService = logCollectTaskHealthCheckContext.getLogCollectTaskHealthDetailManageService();
            LogCollectTaskHealthDetailDO logCollectTaskHealthDetailDO = logCollectTaskHealthDetailManageService.get(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
            if(null != logCollectTaskHealthDetailDO) {
                logCollectTaskHealthDetailDO.setCollectDqualityTime(lastCollectBusinessTime);
                logCollectTaskHealthDetailManageService.update(logCollectTaskHealthDetailDO);
            } else {
                logCollectTaskHealthDetailDO = logCollectTaskHealthDetailManageService.getInit(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
                logCollectTaskHealthDetailDO.setCollectDqualityTime(lastCollectBusinessTime);
                logCollectTaskHealthDetailManageService.add(logCollectTaskHealthDetailDO);
            }
        }
    }

}
