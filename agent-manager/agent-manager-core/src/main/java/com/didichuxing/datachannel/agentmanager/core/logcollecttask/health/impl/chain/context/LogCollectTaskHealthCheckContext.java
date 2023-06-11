package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain.context;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDetailDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthDetailManageService;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;
import lombok.Data;

/**
 * @author william.
 */
@Data
public class LogCollectTaskHealthCheckContext extends Context {

    /**
     * 待诊断日志采集任务对象
     */
    private LogCollectTaskDO logCollectTaskDO;

    /**
     * 日志采集任务须采集的文件路径
     */
    private FileLogCollectPathDO fileLogCollectPathDO;

    /**
     * 日式采集任务须部署的主机
     */
    private HostDO hostDO;

    /**
     * 日志采集任务健康度检查流程获取agent心跳数据右边界时间
     */
    private Long logCollectTaskHealthCheckTimeEnd;

    /**
     * 是否校验日志采集任务采集延时 true：校验 false：不校验
     */
    private Boolean checkCollectDelay;

    /**
     * 日志采集任务健康度检查结果枚举对象
     */
    private LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum;

    /**
     * 日志采集任务健康检查描述
     */
    private String logCollectTaskHealthDescription;

    /**
     * 日志采集任务健康度巡检结果枚举对象
     */
    private LogCollectTaskHealthInspectionResultEnum logCollectTaskHealthInspectionResultEnum;

    /**
     * LogCollectTaskHealthDetailManageService 对象
     */
    private LogCollectTaskHealthDetailManageService logCollectTaskHealthDetailManageService;

    /**
     * MetricsManageService 对象
     */
    private MetricsManageService metricsManageService;

    /**
     * HostManageService 对象
     */
    private HostManageService hostManageService;

    /**
     * AgentManageService 对象
     */
    private AgentManageService agentManageService;

    /**
     * LogCollectTaskHealthDetailDO 对象
     */
    private LogCollectTaskHealthDetailDO logCollectTaskHealthDetailDO;

    /**
     * 最近一次日志采集任务指标
     */
    private MetricsLogCollectTaskPO lastLogCollectTaskMetric;

}
