package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
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
     * AgentPO 指标管理服务接口
     */
    private AgentMetricsManageService agentMetricsManageService;

    /**
     * kafka 集群信息管理服务
     */
    private KafkaClusterManageService kafkaClusterManageService;

    /**
     * 日志采集任务管理服务接口
     */
    private LogCollectTaskManageService logCollectTaskManageService;

    /**
     * 日志采集任务健康度检查结果
     */
    private AgentHealthLevelEnum agentHealthLevelEnum;

    /**
     * 日志采集任务健康检查描述
     */
    private String agentHealthDescription;

}
