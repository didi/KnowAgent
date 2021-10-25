package com.didichuxing.datachannel.agentmanager.core.agent.health.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.health.AgentHealthPO;
import com.didichuxing.datachannel.agentmanager.common.constant.AgentHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.core.agent.health.AgentHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentHealthMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
        agentHealthPO.setLastestErrorLogsExistsCheckHealthyTime(System.currentTimeMillis());
        agentHealthPO.setOperator(CommonConstant.getOperator(operator));
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
        AgentHealthDO agentHealthDO = getByAgentId(agentDO.getId());//agentDO关联的AgentHealth对象
        AgentHealthLevelEnum agentHealthLevelEnum = AgentHealthInspectionResultEnum.HEALTHY.getAgentHealthLevel();
        String agentHealthDescription = String.format(
                "%s:AgentId={%d}, HostName={%s}",
                AgentHealthInspectionResultEnum.HEALTHY.getDescription(),
                agentDO.getId(),
                agentDO.getHostName()
        );//AgentHealth检查描述
        /************************ mark red cases ************************/
        /*
         * 校验在距当前时间的心跳存活判定周期内，agent 是否存在心跳
         */
        boolean alive = checkAliveByHeartbeat(agentDO.getHostName());
        if (!alive) {//不存活
            agentHealthLevelEnum = AgentHealthInspectionResultEnum.AGENT_HEART_BEAT_NOT_EXISTS.getAgentHealthLevel();
            agentHealthDescription = String.format(
                    "%s:AgentId={%d}, HostName={%s}",
                    AgentHealthInspectionResultEnum.AGENT_HEART_BEAT_NOT_EXISTS.getDescription(),
                    agentDO.getId(),
                    agentDO.getHostName()
            );
            agentHealthDO.setAgentHealthDescription(agentHealthDescription);
            agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
            updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
            return agentHealthLevelEnum;
        }
        /*
         * 校验 agent 是否存在错误日志输出
         */
        Long agentHealthCheckTimeEnd = System.currentTimeMillis() - 1000; //Agent健康度检查流程获取agent心跳数据右边界时间，取当前时间前一秒
        boolean errorLogsExists = checkErrorLogsExists(agentDO.getHostName(), agentHealthDO, agentHealthCheckTimeEnd);
        if (errorLogsExists) {//agent 存在错误日志输出
            agentHealthLevelEnum = AgentHealthInspectionResultEnum.AGENT_ERRORLOGS_EXISTS.getAgentHealthLevel();
            agentHealthDescription = String.format(
                    "%s:AgentId={%d}, HostName={%s}",
                    AgentHealthInspectionResultEnum.AGENT_ERRORLOGS_EXISTS.getDescription(),
                    agentDO.getId(),
                    agentDO.getHostName()
            );
            agentHealthDO.setAgentHealthDescription(agentHealthDescription);
            agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
            updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
            return agentHealthLevelEnum;
        } else {//agent 不存在错误日志输出
            agentHealthDO.setLastestErrorLogsExistsCheckHealthyTime(agentHealthCheckTimeEnd);
        }
        /************************ mark yellow cases ************************/
        /*
         * 校验是否存在 agent 非人工启动过频
         */
        boolean agentStartupFrequentlyExists = checkAgentStartupFrequentlyExists(agentDO.getHostName(), agentHealthDO);
        if (agentStartupFrequentlyExists) {//存在 agent 非人工启动过频
            agentHealthLevelEnum = AgentHealthInspectionResultEnum.AGENT_STARTUP_FREQUENTLY.getAgentHealthLevel();
            agentHealthDescription = String.format(
                    "%s:AgentId={%d}, HostName={%s}",
                    AgentHealthInspectionResultEnum.AGENT_STARTUP_FREQUENTLY.getDescription(),
                    agentDO.getId(),
                    agentDO.getHostName()
            );
            agentHealthDO.setAgentHealthDescription(agentHealthDescription);
            agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
            updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
            return agentHealthLevelEnum;
        }
        /*
         * 校验是否存在 agent 进程 gc 指标异常
         */
        boolean agentGcMetricExceptionExists = checkAgentGcMetricExceptionExists(agentDO.getHostName());
        if (agentGcMetricExceptionExists) {//存在 agent 进程 gc 指标异常
            agentHealthLevelEnum = AgentHealthInspectionResultEnum.AGENT_GC_METRIC_EXCEPTION.getAgentHealthLevel();
            agentHealthDescription = String.format(
                    "%s:AgentId={%d}, HostName={%s}",
                    AgentHealthInspectionResultEnum.AGENT_GC_METRIC_EXCEPTION.getDescription(),
                    agentDO.getId(),
                    agentDO.getHostName()
            );
            agentHealthDO.setAgentHealthDescription(agentHealthDescription);
            agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
            updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
            return agentHealthLevelEnum;
        }
        /*
         * 校验是否存在 agent 进程 cpu 使用率指标异常
         */
        boolean agentCpuUageMetricExceptionExists = checkAgentCpuUageMetricExceptionExists(agentDO.getHostName(), agentDO.getCpuLimitThreshold());
        if (agentCpuUageMetricExceptionExists) {//存在 agent 进程 cpu 使用率指标异常
            agentHealthLevelEnum = AgentHealthInspectionResultEnum.AGENT_CPU_USAGE_METRIC_EXCEPTION.getAgentHealthLevel();
            agentHealthDescription = String.format(
                    "%s:AgentId={%d}, HostName={%s}",
                    AgentHealthInspectionResultEnum.AGENT_CPU_USAGE_METRIC_EXCEPTION.getDescription(),
                    agentDO.getId(),
                    agentDO.getHostName()
            );
            agentHealthDO.setAgentHealthDescription(agentHealthDescription);
            agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
            updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
            return agentHealthLevelEnum;
        }
        /*
         * 校验是否存在 agent 进程 fd 使用量指标异常
         */
        boolean agentFdUsageMetricExceptionExists = checkAgentFdUsageMetricExceptionExists(agentDO.getHostName());
        if (agentFdUsageMetricExceptionExists) {
            agentHealthLevelEnum = AgentHealthInspectionResultEnum.AGENT_FD_USAGE_METRIC_EXCEPTION.getAgentHealthLevel();
            agentHealthDescription = String.format(
                    "%s:AgentId={%d}, HostName={%s}",
                    AgentHealthInspectionResultEnum.AGENT_FD_USAGE_METRIC_EXCEPTION.getDescription(),
                    agentDO.getId(),
                    agentDO.getHostName()
            );
            agentHealthDO.setAgentHealthDescription(agentHealthDescription);
            agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
            updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
            return agentHealthLevelEnum;
        }
        /*
         * 校验 agent 端是否存在出口限流
         */
        boolean byteLimitOnAgentExists = checkByteLimitOnAgentExists(agentDO.getHostName());
        if (byteLimitOnAgentExists) {
            agentHealthLevelEnum = AgentHealthInspectionResultEnum.HOST_BYTES_LIMIT_EXISTS.getAgentHealthLevel();
            agentHealthDescription = String.format(
                    "%s:AgentId={%d}, HostName={%s}",
                    AgentHealthInspectionResultEnum.HOST_BYTES_LIMIT_EXISTS.getDescription(),
                    agentDO.getId(),
                    agentDO.getHostName()
            );
            agentHealthDO.setAgentHealthDescription(agentHealthDescription);
            agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
            updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
            return agentHealthLevelEnum;
        }
        /*
         * 校验 agent 端是否未关联任何日志采集任务
         */
        boolean notRelateAnyLogCollectTask = CollectionUtils.isEmpty(logCollectTaskManageService.getLogCollectTaskListByAgentHostName(agentDO.getHostName()));
        if (notRelateAnyLogCollectTask) {
            agentHealthLevelEnum = AgentHealthInspectionResultEnum.NOT_RELATE_ANY_LOGCOLLECTTASK.getAgentHealthLevel();
            agentHealthDescription = String.format(
                    "%s:AgentId={%d}, HostName={%s}",
                    AgentHealthInspectionResultEnum.NOT_RELATE_ANY_LOGCOLLECTTASK.getDescription(),
                    agentDO.getId(),
                    agentDO.getHostName()
            );
            agentHealthDO.setAgentHealthDescription(agentHealthDescription);
            agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
            updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
            return agentHealthLevelEnum;
        }

        agentHealthDO.setAgentHealthDescription(agentHealthDescription);
        agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
        updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
        return agentHealthLevelEnum;
    }

    /**
     * 校验是否存在 agent 非人工启动过频
     *
     * @param hostName      agent 主机名
     * @param agentHealthDO AgentHealthDO 对象
     * @return true：存在启动过频 false：不存在启动过频
     */
    private boolean checkAgentStartupFrequentlyExists(String hostName, AgentHealthDO agentHealthDO) {
        /*
         * 获取 agent 最近一次心跳对应 agent 启动时间，并对比该时间与系统记录的 agent 启动时间是否一致：
         *  一致：表示 agent 自系统记录的启动时间以来未有启动行为 do nothing.
         *  不一致：表示 agent 自系统记录的启动时间以来存在启动行为，将系统记录的 agent 启动时间记为上一次启动时间，将最近一次心跳对应 agent 启动时间记为系统记录的 agent 启动时间
         * 判断 "系统记录的 agent 启动时间" - "agent 上一次启动时间" < 对应系统设定阈值：
         *  是：进一步判断 "系统记录的 agent 启动时间" 是否由人工触发（人工触发判断条件为："系统记录的 agent 启动时间" 对应 agent 是否存在对应安装、升级类型 AgentOperationTask 执行）TODO：该步骤待实现
         *  否：不存在启动过频
         */
        Long lastestAgentStartupTime = agentMetricsManageService.getLastestAgentStartupTime(hostName);//agent心跳上报最近一次启动时间
        Long agentStartupTime = agentHealthDO.getAgentStartupTime();//系统记录的agent最近一次启动时间
        if (null == agentStartupTime || agentStartupTime <= 0) {//agent初次启动上报
            agentHealthDO.setAgentStartupTime(lastestAgentStartupTime);
            return false;
        } else {//agent非初次启动上报
            if (lastestAgentStartupTime.equals(agentStartupTime)) {//表示 agent 自系统记录的启动时间以来未有启动行为
                //do nothing
            } else {//表示 agent 自系统记录的启动时间以来存在启动行为，将系统记录的 agent 启动时间记为上一次启动时间，将最近一次心跳对应 agent 启动时间记为系统记录的 agent 启动时间
                agentHealthDO.setAgentStartupTimeLastTime(agentStartupTime);
                agentHealthDO.setAgentStartupTime(lastestAgentStartupTime);
            }
            Long agentStartupTimeLastTime = agentHealthDO.getAgentStartupTimeLastTime();//agent上一次启动时间
            if (null == agentStartupTimeLastTime) {//表示agent处于初次启动
                return false;
            } else {//表示agent非初次启动
                if (agentHealthDO.getAgentStartupTime() - agentHealthDO.getAgentStartupTimeLastTime() > AgentHealthCheckConstant.AGENT_STARTUP_FREQUENTLY_THRESHOLD) {

                    //TODO：判断 "系统记录的 agent 启动时间" 是否由人工触发（人工触发判断条件为："系统记录的 agent 启动时间" 对应 agent 是否存在对应安装、升级类型 AgentOperationTask 执行）

                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    /**
     * 校验 agent 是否存在错误日志输出
     *
     * @param hostName                agent 主机名
     * @param agentHealthDO           AgentHealthDO对象
     * @param agentHealthCheckTimeEnd Agent健康度检查流程获取agent心跳数据右边界时间
     * @return true：存在错误日志输出 false：不存在错误日志输出
     */
    private boolean checkErrorLogsExists(String hostName, AgentHealthDO agentHealthDO, Long agentHealthCheckTimeEnd) {
        /*
         * 获取自上次"错误日志输出存在"健康点 ~ 当前时间，agent 是否存在错误日志输出
         */
        Long lastestCheckTime = agentHealthDO.getLastestErrorLogsExistsCheckHealthyTime();
        if (null == lastestCheckTime) {
            throw new ServiceException(
                    String.format("Agent={hostName=%s}对应lastestErrorLogsExistsCheckHealthyTime不存在", hostName),
                    ErrorCodeEnum.AGENT_HEALTH_ERROR_LOGS_EXISTS_CHECK_HEALTHY_TIME_NOT_EXISTS.getCode()
            );
        }
        Integer errorlogsCount = agentMetricsManageService.getErrorLogCount(
                lastestCheckTime,
                agentHealthCheckTimeEnd,
                hostName
        );
        return errorlogsCount > 0;
    }

    /**
     * 校验在距当前时间的心跳存活判定周期内，agent 是否存或
     *
     * @param hostName agent 主机名
     * @return true：存活 false：不存活
     */
    private boolean checkAliveByHeartbeat(String hostName) {
        /*
         * 获取近 AgentHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD 时间范围内 agent 心跳数，
         * 心跳数量 == 0，表示 agent 不存在心跳
         */
        Long currentTime = System.currentTimeMillis();
        Long heartbeatTimes = agentMetricsManageService.getHeartbeatTimesByTimeFrame(
                currentTime - AgentHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD,
                currentTime,
                hostName
        );
        return heartbeatTimes > 0;
    }

    /**
     * 校验 agent 端是否存在 cpu 阈值限流
     *
     * @param hostName agent 主机名
     * @return true：存在限流 false：不存在限流
     */
    private boolean checkCpuLimitOnAgentExists(String hostName) {
        /*
         * 获取近 AgentHealthCheckConstant.HOST_CPU_LIMIT_CHECK_LASTEST_MS_THRESHOLD 时间范围内 hostName 指标集中，
         * 总限流时间是否超过阈值 AgentHealthCheckConstant.HOST_CPU_LIMIT_MS_THRESHOLD
         */
        Long startTime = System.currentTimeMillis();
        Long endTime = System.currentTimeMillis() - AgentHealthCheckConstant.HOST_CPU_LIMIT_CHECK_LASTEST_MS_THRESHOLD;
        Long hostCpuLimiDturationMs = agentMetricsManageService.getHostCpuLimiDturationByTimeFrame(
                startTime,
                endTime,
                hostName
        );//主机cpu限流时长 单位：ms
        if (hostCpuLimiDturationMs > AgentHealthCheckConstant.HOST_CPU_LIMIT_MS_THRESHOLD) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 校验 agent 端是否存在出口流量阈值限流
     *
     * @param hostName agent 主机名
     * @return true：存在限流 false：不存在限流
     */
    private boolean checkByteLimitOnAgentExists(String hostName) {
        /*
         * 获取近 AgentHealthCheckConstant.HOST_BYTE_LIMIT_CHECK_LASTEST_MS_THRESHOLD 时间范围内 hostName 指标集中，
         * 总限流时间是否超过阈值 AgentHealthCheckConstant.HOST_BYTE_LIMIT_MS_THRESHOLD
         */
        Long startTime = System.currentTimeMillis() - AgentHealthCheckConstant.HOST_BYTE_LIMIT_CHECK_LASTEST_MS_THRESHOLD;
        Long endTime = System.currentTimeMillis();
        Long hostByteLimitDurationMs = agentMetricsManageService.getHostByteLimitDurationByTimeFrame(
                startTime,
                endTime,
                hostName
        );//主机流量限流时长 单位：ms
        return hostByteLimitDurationMs > AgentHealthCheckConstant.HOST_BYTE_LIMIT_MS_THRESHOLD;
    }

    /**
     * 校验是否存在 agent 进程 fd 使用量指标异常
     *
     * @param hostName agent 主机名
     * @return true：存在异常 false：不存在异常
     */
    private boolean checkAgentFdUsageMetricExceptionExists(String hostName) {
        /*
         * 获取最近一次agent心跳中对应 fd 使用量值，判断该值是否 > AgentHealthCheckConstant.AGENT_FD_USED_THRESHOLD
         */
        Integer fdUsage = agentMetricsManageService.getLastestFdUsage(hostName);
        return fdUsage > AgentHealthCheckConstant.AGENT_FD_USED_THRESHOLD;
    }

    /**
     * 校验是否存在 agent 进程 cpu 使用率指标异常
     *
     * @param hostName          agent 主机名
     * @param cpuLimitThreshold agent cpu 限流阈值
     * @return true：存在异常 false：不存在异常
     */
    private boolean checkAgentCpuUageMetricExceptionExists(String hostName, Integer cpuLimitThreshold) {
        /*
         * 获取 agent 最近一次心跳对应 cpu_usage（ps：表示一个心跳周期内，cpu平均 使用率）
         */
        Integer cpuUsage = agentMetricsManageService.getLastestCpuUsage(hostName);
        /*
         * cpu_usage 是否超限
         */
        return cpuUsage > cpuLimitThreshold;
    }

    /**
     * 校验是否存在 agent 进程 gc 指标异常
     *
     * @param hostName agent 主机名
     * @return true：存在 agent 进程 gc 指标异常 false：不存在 agent 进程 gc 指标异常
     */
    private boolean checkAgentGcMetricExceptionExists(String hostName) {
        /*
         * 获取 agent 近一小时内 fullgc 次数是否 > 1，如是：表示存在 agent 进程 gc 指标异常 如不是：表示不存在 agent 进程 gc 指标异常
         *
         */
        Long startTime = System.currentTimeMillis() - AgentHealthCheckConstant.AGENT_GC_METRIC_CHECK_LASTEST_MS_THRESHOLD;
        Long endTime = System.currentTimeMillis();
        Long agentFullgcTimes = agentMetricsManageService.getAgentFullgcTimesByTimeFrame(
                startTime,
                endTime,
                hostName
        );
        return agentFullgcTimes > AgentHealthCheckConstant.AGENT_GC_TIMES_METRIC_CHECK_THRESHOLD;
    }

}
