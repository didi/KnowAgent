package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain.context;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsAgentPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsProcessPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsSystemPO;
import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;
import lombok.Data;

/**
 * @author Ronaldo
 * @Date 2021/10/31
 */
@Data
public class AgentHealthCheckContext extends Context {

    /**
     * 待检查 AgentDO 对象
     */
    private AgentDO agentDO;

    /**
     * agentDO关联的AgentHealth对象
     */
    private AgentHealthDO agentHealthDO;

    /**
     * 日志采集任务健康度检查结果
     */
    private AgentHealthLevelEnum agentHealthLevelEnum;

    /**
     * 日志采集任务健康检查描述
     */
    private String agentHealthDescription;

    /**
     * 日志采集任务健康度巡检结果枚举对象
     */
    private AgentHealthInspectionResultEnum agentHealthInspectionResultEnum;

    /**
     * kafka 集群信息管理服务
     */
    private KafkaClusterManageService kafkaClusterManageService;

    /**
     * 日志采集任务管理服务接口
     */
    private LogCollectTaskManageService logCollectTaskManageService;

    /**
     * 指标管理服务接口
     */
    private MetricsManageService metricsManageService;

    /**
     * 最近一次 agent 指标
     */
    private MetricsAgentPO lastMetricsAgent;

    /**
     * agent 健康度检查流程获取 agent 心跳数据右边界时间
     */
    private Long agentHealthCheckTimeEnd;

    /**
     * 最近一次 system 指标
     */
    private MetricsSystemPO lastMetricsSystem;

    /**
     * 最近一次 process 指标
     */
    private MetricsProcessPO lastMetricsProcess;

}
