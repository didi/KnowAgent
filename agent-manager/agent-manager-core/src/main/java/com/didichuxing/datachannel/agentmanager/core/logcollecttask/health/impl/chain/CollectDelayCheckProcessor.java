package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.chain.ProcessorChain;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;

import java.util.Map;

/**
 * 是否存在采集延迟检查
 * @author Ronaldo
 */
@HealthCheckProcessorAnnotation(seq = 7, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class CollectDelayCheckProcessor implements Processor {
    @Override
    public void process(Context context, ProcessorChain chain) {
        LogCollectTaskHealthCheckContext logCollectTaskHealthCheckContext = (LogCollectTaskHealthCheckContext) context;
        LogCollectTaskDO logCollectTaskDO = logCollectTaskHealthCheckContext.getLogCollectTaskDO();
        FileLogCollectPathDO fileLogCollectPathDO = logCollectTaskHealthCheckContext.getFileLogCollectPathDO();
        HostDO hostDO = logCollectTaskHealthCheckContext.getHostDO();
        Boolean checkCollectDelay = logCollectTaskHealthCheckContext.getCheckCollectDelay();
        AgentMetricsManageService agentMetricsManageService = logCollectTaskHealthCheckContext.getAgentMetricsManageService();

        /*
         * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在采集延迟
         */
        //该文件型日志采集路径须做采集延迟监控
        if (checkCollectDelay && null != logCollectTaskDO.getCollectDelayThresholdMs() && logCollectTaskDO.getCollectDelayThresholdMs() > 0) {
            boolean collectDelay = examineCollectDelay(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), logCollectTaskDO.getCollectDelayThresholdMs(), agentMetricsManageService);
            // 不存在采集延迟
            if (!collectDelay) {
                chain.process(context, chain);
                return;
            }
            // 存在采集延迟
            LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.LOG_PATH_COLLECT_DELAYED.getLogCollectTaskHealthLevelEnum();
            String logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.LOG_PATH_COLLECT_DELAYED.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
            logCollectTaskHealthCheckContext.setLogCollectTaskHealthLevelEnum(logCollectTaskHealthLevelEnum);
            logCollectTaskHealthCheckContext.setLogCollectTaskHealthDescription(logCollectTaskHealthDescription);
        }
    }

    /**
     * 校验 logCollectTaskId+fileLogCollectPathId 在host上是否存在采集延迟
     *
     * @param logCollectTaskId        日志采集任务 id
     * @param fileLogCollectPathId    日志采集路径 id
     * @param hostName                主机名
     * @param collectDelayThresholdMs 采集延时阈值
     * @return 返回 logCollectTaskId+fileLogCollectPathId 在host上是否存在采集延迟 true：存在 采集延时 false：不存在 采集延时
     */
    private boolean examineCollectDelay(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long collectDelayThresholdMs, AgentMetricsManageService agentMetricsManageService) {
        /*
         * 获取logCollectTaskId+fileLogCollectPathId在host上对应当前采集时间 currentCollectTime
         */
        Long currentCollectTime = agentMetricsManageService.getLastestCollectTime(logCollectTaskId, fileLogCollectPathId, hostName);
        /*
         * System.currentTimeMillis() - currentCollectTime > collectDelayThresholdMs ? true : false
         */
        return System.currentTimeMillis() - currentCollectTime > collectDelayThresholdMs;
    }
}
