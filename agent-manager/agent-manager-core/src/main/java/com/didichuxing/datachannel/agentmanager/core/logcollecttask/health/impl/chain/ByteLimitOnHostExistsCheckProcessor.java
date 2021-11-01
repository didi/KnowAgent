package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.chain.ProcessorChain;
import com.didichuxing.datachannel.agentmanager.common.constant.LogCollectTaskHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;

import java.util.Map;

/**
 * 是否存在采集端出口限流检查
 * @author Ronaldo
 */
@HealthCheckProcessorAnnotation(seq = 8, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class ByteLimitOnHostExistsCheckProcessor implements Processor {
    @Override
    public void process(Context context, ProcessorChain chain) {
        LogCollectTaskHealthCheckContext logCollectTaskHealthCheckContext = (LogCollectTaskHealthCheckContext) context;
        LogCollectTaskDO logCollectTaskDO = logCollectTaskHealthCheckContext.getLogCollectTaskDO();
        FileLogCollectPathDO fileLogCollectPathDO = logCollectTaskHealthCheckContext.getFileLogCollectPathDO();
        HostDO hostDO = logCollectTaskHealthCheckContext.getHostDO();
        AgentMetricsManageService agentMetricsManageService = logCollectTaskHealthCheckContext.getAgentMetricsManageService();

        /*
         * 校验 logcollecttask + logpath 在 host 端是否存在采集端出口限流
         */
        boolean byteLimitOnHostExists = checkByteLimitOnHostExists(logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName(), agentMetricsManageService);
        // 不存在采集端出口流量阈值限流
        if (!byteLimitOnHostExists) {
            chain.process(context, chain);
            return;
        }
        LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.HOST_BYTES_LIMIT_EXISTS.getLogCollectTaskHealthLevelEnum();
        String logCollectTaskHealthDescription = String.format("%s:LogCollectTaskId={%d}, FileLogCollectPathId={%d}, HostName={%s}", LogCollectTaskHealthInspectionResultEnum.HOST_BYTES_LIMIT_EXISTS.getDescription(), logCollectTaskDO.getId(), fileLogCollectPathDO.getId(), hostDO.getHostName());
        logCollectTaskHealthCheckContext.setLogCollectTaskHealthLevelEnum(logCollectTaskHealthLevelEnum);
        logCollectTaskHealthCheckContext.setLogCollectTaskHealthDescription(logCollectTaskHealthDescription);
    }

    /**
     * 校验 logcollecttask + logpath 在 host 端是否存在采集端出口流量阈值限流
     *
     * @param logCollectTaskId     日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param hostName             主机名
     * @return
     */
    private boolean checkByteLimitOnHostExists(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, AgentMetricsManageService agentMetricsManageService) {
        /*
         * 获取近 LogCollectTaskHealthCheckConstant.HOST_BYTE_LIMIT_CHECK_LASTEST_MS_THRESHOLD 时间范围内 logCollectTaskId+fileLogCollectPathId+hostName 指标集中，
         * 总限流时间是否超过阈值 LogCollectTaskHealthCheckConstant.HOST_BYTE_LIMIT_MS_THRESHOLD
         */
        Long startTime = System.currentTimeMillis() - LogCollectTaskHealthCheckConstant.HOST_BYTE_LIMIT_CHECK_LASTEST_MS_THRESHOLD;
        Long endTime = System.currentTimeMillis();
        Long hostCpuLimiDturationMs = agentMetricsManageService.getHostByteLimitDurationByTimeFrame(
                startTime,
                endTime,
                logCollectTaskId,
                fileLogCollectPathId,
                hostName
        );//主机cpu限流时长 单位：ms
        return hostCpuLimiDturationMs > LogCollectTaskHealthCheckConstant.HOST_BYTE_LIMIT_MS_THRESHOLD;
    }
}
