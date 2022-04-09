package com.didichuxing.datachannel.agentmanager.core.agent.health.impl;

import com.didichuxing.datachannel.agentmanager.common.GlobalProperties;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.health.AgentHealthPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskHealthDetailPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsAgentPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.chain.ProcessorChain;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.core.agent.health.AgentHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain.context.AgentHealthCheckContext;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentHealthMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class AgentHealthManageServiceImpl implements AgentHealthManageService {

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private AgentHealthMapper agentHealthDAO;

    @Autowired
    private AgentMetricsManageService agentMetricsManageService;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private KafkaClusterManageService kafkaClusterManageService;

    @Autowired
    private MetricsManageService metricsManageService;

    @Override
    @Transactional
    public Long createInitialAgentHealth(Long savedAgentId, String operator) {
        return this.handleCreateInitialAgentHealth(savedAgentId, operator);
    }

    /**
     * 根据给定Agent 对象 id 值创建初始AgentHealth对象
     * @param agentId Agent 对象 id 值
     * @param operator 操作人
     * @return 返回创建的Agent健康对象 id 值
     */
    private Long handleCreateInitialAgentHealth(Long agentId, String operator) {
        AgentDO agentDO = agentManageService.getById(agentId);
        if(null == agentDO) {
            throw new ServiceException(
                    String.format("系统中不存在id={%d}的Agent对象", agentId),
                    ErrorCodeEnum.AGENT_NOT_EXISTS.getCode()
            );
        }
        AgentHealthPO agentHealthPO = buildInitialAgentHealthPO(agentDO, operator);
        agentHealthDAO.insertSelective(agentHealthPO);
        return agentHealthPO.getId();
    }

    /**
     * 根据已创建AgentDO对象构建其关联初始化AgentHealthPO对象
     * @param agentDO 已创建AgentDO对象
     * @param operator 操作人
     * @return 返回根据已创建AgentDO对象构建的其关联初始化AgentHealthPO对象
     */
    private AgentHealthPO buildInitialAgentHealthPO(AgentDO agentDO, String operator) {
        if(null == agentDO) {
            throw new ServiceException(
                    String.format(
                            "class=AgentHealthManageServiceImpl||method=buildInitialAgentHealthPO||msg={%s}",
                            "入参agentDO不可为空"
                    ),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        AgentHealthPO agentHealthPO = new AgentHealthPO();
        agentHealthPO.setAgentHealthDescription(StringUtils.EMPTY);
        agentHealthPO.setAgentHealthLevel(AgentHealthLevelEnum.GREEN.getCode());
        agentHealthPO.setAgentId(agentDO.getId());
        agentHealthPO.setLastestErrorLogsExistsCheckHealthyTime(0L);
        agentHealthPO.setOperator(CommonConstant.getOperator(operator));
        agentHealthPO.setAgentHealthInspectionResultType(AgentHealthInspectionResultEnum.HEALTHY.getCode());
        return agentHealthPO;
    }

    @Override
    @Transactional
    public void deleteByAgentId(Long agentId, String operator) {
        this.handleDeleteByAgentId(agentId, operator);
    }

    @Override
    public AgentHealthDO getByAgentId(Long agentId) {
        AgentHealthPO agentHealthPO = agentHealthDAO.selectByAgentId(agentId);
        return ConvertUtil.obj2Obj(agentHealthPO, AgentHealthDO.class);
    }

    @Override
    @Transactional
    public void updateAgentHealth(AgentHealthDO agentHealthDO, String operator) {
        this.handleUpdateAgentHealth(agentHealthDO, operator);
    }

    /**
     * 更新给定AgentHealthDO对象
     * @param agentHealthDO 待更新AgentHealthDO对象
     * @param operator 操作人
     */
    private void handleUpdateAgentHealth(AgentHealthDO agentHealthDO, String operator) {
        if(null == agentHealthDAO.selectByPrimaryKey(agentHealthDO.getId())) {
            throw new ServiceException(
                    String.format("AgentHealth={id=%d}在系统中不存在", agentHealthDO.getId()),
                    ErrorCodeEnum.AGENT_HEALTH_NOT_EXISTS.getCode()
            );
        }
        AgentHealthPO agentHealthPO = ConvertUtil.obj2Obj(agentHealthDO, AgentHealthPO.class);
        agentHealthPO.setOperator(CommonConstant.getOperator(operator));
        agentHealthDAO.updateByPrimaryKey(agentHealthPO);
    }

    /**
     * 根据Agent对象id值删除对应AgentHealth对象
     * @param agentId Agent对象id值
     * @param operator 操作人
     */
    private void handleDeleteByAgentId(Long agentId, String operator) {
        AgentHealthPO agentHealthPO = agentHealthDAO.selectByAgentId(agentId);
        if(null == agentHealthPO) {
            throw new ServiceException(
                    String.format("根据给定Agent对象id={%d}删除对应AgentHealth对象失败，原因为：系统中不存在agentId={%d}的AgentHealth对象", agentId, agentId),
                    ErrorCodeEnum.AGENT_HEALTH_NOT_EXISTS.getCode()
            );
        }
        agentHealthDAO.deleteByAgentId(agentId);
    }

    @Override
    public AgentHealthLevelEnum checkAgentHealth(AgentDO agentDO) {
        /*
         * 校验日志采集任务是否须被诊断
         */
        CheckResult checkResult = agentNeedCheckHealth(agentDO);
        /*
         * 诊断对应日志采集任务
         */
        if (checkResult.getCheckResult()) {//须诊断对应 agent
            AgentHealthLevelEnum agentHealthLevel = handleCheckAgentHealth(agentDO);
            return agentHealthLevel;
        } else {//该日志采集任务无须被诊断 返回 AgentHealthLevelEnum.GREEN
            return AgentHealthLevelEnum.GREEN;
        }
    }

    @Override
    public List<MetricsAgentPO> getErrorDetails(String hostName) {
        /*
         * 根据agentHealthInspectionCode获取对应case在hostName时最近一次健康时间点
         */
        AgentDO agentDO = agentManageService.getAgentByHostName(hostName);
        if(null == agentDO) {
            throw new ServiceException(
                    String.format("Agent[hostName=%s]在系统中不存在", hostName),
                    ErrorCodeEnum.AGENT_NOT_EXISTS.getCode()
            );
        }
        AgentHealthPO agentHealthPO = agentHealthDAO.selectByAgentId(agentDO.getId());
        Long lastCheckHealthyTimestamp = 0L;
        if(null == agentHealthPO) {
            throw new ServiceException(
                    String.format("Agent[hostName=%s]关联的AgentHealth对象在系统中不存在", hostName),
                    ErrorCodeEnum.AGENT_HEALTH_NOT_EXISTS.getCode()
            );
        } else {
            lastCheckHealthyTimestamp = agentHealthPO.getLastestErrorLogsExistsCheckHealthyTime();
        }
        /*
         * 从agent业务指标表查询近一次健康时间点到当前时间点所有心跳信息，根据心跳时间顺序排序
         */
        return metricsManageService.getErrorMetrics(hostName, lastCheckHealthyTimestamp, System.currentTimeMillis());
    }

    @Override
    @Transactional
    public void solveErrorDetail(Long agentMetricId) {
        MetricsAgentPO metricsAgentPO = metricsManageService.getMetricAgent(agentMetricId);
        if(null == metricsAgentPO) {
            throw new ServiceException(
                    String.format("MetricsAgent[id=%d]在系统中不存在", agentMetricId),
                    ErrorCodeEnum.METRICS_RECORD_NOT_EXISTS.getCode()
            );
        } else {
            String hostName = metricsAgentPO.getHostname();
            AgentDO agentDO = agentManageService.getAgentByHostName(hostName);
            if(null == agentDO) {
                throw new ServiceException(
                        String.format("Agent[hostName=%s]在系统中不存在", hostName),
                        ErrorCodeEnum.AGENT_NOT_EXISTS.getCode()
                );
            }
            AgentHealthPO agentHealthPO = agentHealthDAO.selectByAgentId(agentDO.getId());
            if(null == agentHealthPO) {
                throw new ServiceException(
                        String.format("Agent[hostName=%s]关联的AgentHealth对象在系统中不存在", hostName),
                        ErrorCodeEnum.AGENT_HEALTH_NOT_EXISTS.getCode()
                );
            } else {
                agentHealthPO.setLastestErrorLogsExistsCheckHealthyTime(metricsAgentPO.getHeartbeattime());
            }
            agentHealthDAO.updateByPrimaryKey(agentHealthPO);
        }
    }

    /**
     * 校验给定Agent是否需要被健康巡检
     *
     * @param agentDO 待校验 AgentDO 对象
     * @return true：须巡检 false：不须巡检
     * @throws ServiceException
     */
    private CheckResult agentNeedCheckHealth(AgentDO agentDO) throws ServiceException {
        //TODO：后续添加 agent 黑名单功能后，须添加黑名单过滤规则
        return new CheckResult(true);
    }

    /**
     * 检查给定 Agent 健康度，返回并将检查结果信息更新至表 tb_agent
     *
     * @param agentDO 待检查 AgentDO 对象
     * @return 返回给定 agent 健康度检查结果
     */
    private AgentHealthLevelEnum handleCheckAgentHealth(AgentDO agentDO) throws ServiceException {
        /*
         * 构建处理链对应上下文对象
         */
        AgentHealthCheckContext agentHealthCheckContext = buildAgentHealthCheckContext(agentDO);
        /*
         * 构建处理链
         */
        ProcessorChain processorChain = getLogCollectTaskHealthCheckProcessorChain();
        /*
         * 执行巡检流程
         */
        processorChain.process(agentHealthCheckContext, processorChain);
        /*
         * 获取巡检结果并持久化
         */
        updateAgentHealth(agentHealthCheckContext);
        return agentHealthCheckContext.getAgentHealthLevelEnum();
    }

    /**
     * 根据 agent 健康度巡检结果更新对应 AgentHealth 对象
     * @param context 封装 agent 健康度巡检结果上下文对象
     */
    private void updateAgentHealth(AgentHealthCheckContext context) {
        AgentHealthDO agentHealthDO = context.getAgentHealthDO();
        agentHealthDO.setAgentHealthLevel(context.getAgentHealthLevelEnum().getCode());
        agentHealthDO.setAgentHealthDescription(context.getAgentHealthDescription());
        agentHealthDO.setAgentHealthInspectionResultType(context.getAgentHealthInspectionResultEnum().getCode());
        updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
    }

    /**
     * @param agentDO AgentDO 对象
     * @return 根据给定 AgentDO 对象构建对应 AgentHealthCheckContext 对象
     */
    private AgentHealthCheckContext buildAgentHealthCheckContext(AgentDO agentDO) {
        /*
         * 构建上下文对象
         */
        AgentHealthDO agentHealthDO = getByAgentId(agentDO.getId());//agentDO关联的AgentHealth对象
        String agentHealthDescription = String.format(
                "%s:AgentId={%d}, HostName={%s}",
                AgentHealthInspectionResultEnum.HEALTHY.getDescription(),
                agentDO.getId(),
                agentDO.getHostName()
        );//AgentHealth检查描述
        AgentHealthCheckContext agentHealthCheckContext = new AgentHealthCheckContext();
        agentHealthCheckContext.setAgentDO(agentDO);
        agentHealthCheckContext.setAgentHealthDO(agentHealthDO);
        agentHealthCheckContext.setAgentHealthLevelEnum(AgentHealthInspectionResultEnum.HEALTHY.getAgentHealthLevel());
        agentHealthCheckContext.setAgentHealthDescription(agentHealthDescription);
        agentHealthCheckContext.setAgentHealthInspectionResultEnum(AgentHealthInspectionResultEnum.HEALTHY);
        agentHealthCheckContext.setKafkaClusterManageService(kafkaClusterManageService);
        agentHealthCheckContext.setLogCollectTaskManageService(logCollectTaskManageService);
        agentHealthCheckContext.setMetricsManageService(metricsManageService);
        return agentHealthCheckContext;
    }

    /**
     * @return 获取日志采集任务健康度检查处理器链
     */
    private ProcessorChain getLogCollectTaskHealthCheckProcessorChain() {
        ProcessorChain processorChain = new ProcessorChain();
        for(Class<Processor> clazz : GlobalProperties.AGENT_HEALTH_CHECK_PROCESSOR_CLASS_LIST) {
            try {
                processorChain.addProcessor(clazz.newInstance());
            } catch (Exception ex) {
                throw new ServiceException(
                        String.format("%s invoke newInstance() failed, cause by: %s", clazz.getName(), ex.getMessage()),
                        ex,
                        ErrorCodeEnum.REFLECTION_NEW_INSTANCE_EXCEPTION.getCode()
                );
            }
        }
        return processorChain;
    }

}
