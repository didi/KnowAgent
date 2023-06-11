package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.didichuxing.datachannel.agentmanager.common.GlobalProperties;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskHealthPO;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.chain.ProcessorChain;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskStatusEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthDetailManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain.context.LogCollectTaskHealthCheckContext;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.LogCollectTaskHealthMapper;
import com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.health.extension.LogCollectTaskHealthManageServiceExtension;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务健康管理服务实现类
 */
@org.springframework.stereotype.Service
public class LogCollectTaskHealthManageServiceImpl implements LogCollectTaskHealthManageService {

    @Autowired
    private LogCollectTaskHealthMapper logCollectTaskHealthDAO;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private LogCollectTaskHealthManageServiceExtension logCollectTaskHealthManageServiceExtension;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private LogCollectTaskHealthDetailManageService logCollectTaskHealthDetailManageService;

    @Autowired
    private MetricsManageService metricsManageService;

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private KafkaClusterManageService kafkaClusterManageService;

    @Override
    @Transactional
    public Long createInitialLogCollectorTaskHealth(Long logCollectTaskId, String operator) {
        return this.handleCreateInitialLogCollectorTaskHealth(logCollectTaskId, operator);
    }

    @Override
    @Transactional
    public void deleteByLogCollectTaskId(Long logCollectTaskId, String operator) {
        this.handleDeleteByLogCollectTaskId(logCollectTaskId, operator);
    }

    @Override
    public LogCollectTaskHealthDO getByLogCollectTaskId(Long logCollectTaskId) {
        LogCollectTaskHealthPO logCollectTaskHealthPO = logCollectTaskHealthDAO.selectByLogCollectTaskId(logCollectTaskId);
        if (null == logCollectTaskHealthPO) {
            return null;
        } else {
            return logCollectTaskHealthManageServiceExtension.logCollectTaskHealthPO2LogCollectTaskHealthDO(logCollectTaskHealthPO);
        }
    }

    @Override
    @Transactional
    public void updateLogCollectorTaskHealth(LogCollectTaskHealthDO logCollectTaskHealthDO, String operator) {
        this.handleUpdateLogCollectorTaskHealth(logCollectTaskHealthDO, operator);
    }

    /**
     * 更新LogCollectTaskHealthDO
     * @param logCollectTaskHealthDO 待更新LogCollectTaskHealthDO对象
     * @param operator 操作人
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleUpdateLogCollectorTaskHealth(LogCollectTaskHealthDO logCollectTaskHealthDO, String operator) throws ServiceException {
        if(null == logCollectTaskHealthDAO.selectByPrimaryKey(logCollectTaskHealthDO.getId())) {
            throw new ServiceException(
                    String.format("LogCollectTaskHealth={id=%d}在系统中不存在", logCollectTaskHealthDO.getId()),
                    ErrorCodeEnum.LOGCOLLECTTASK_HEALTH_NOT_EXISTS.getCode()
            );
        }
        LogCollectTaskHealthPO logCollectorTaskHealthPO = logCollectTaskHealthManageServiceExtension.logCollectTaskHealthDO2LogCollectTaskHealthPO(logCollectTaskHealthDO);
        logCollectorTaskHealthPO.setOperator(CommonConstant.getOperator(operator));
        logCollectTaskHealthDAO.updateByPrimaryKey(logCollectorTaskHealthPO);
    }

    /**
     * 根据给定日志采集任务id创建初始日志采集任务健康对象流程
     * @param logCollectTaskId 日志采集任务id
     * @param operator 操作人
     * @return 返回根据给定日志采集任务id创建的初始日志采集任务健康对象id
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long handleCreateInitialLogCollectorTaskHealth(Long logCollectTaskId, String operator) throws ServiceException {
        LogCollectTaskDO logCollectTaskDO = logCollectTaskManageService.getById(logCollectTaskId);
        if(null == logCollectTaskDO) {
            throw new ServiceException(
                    String.format("系统中不存在id={%d}的LogCollectTask对象", logCollectTaskId),
                    ErrorCodeEnum.LOGCOLLECTTASK_NOT_EXISTS.getCode()
            );
        }
        LogCollectTaskHealthPO logCollectorTaskHealthPO = logCollectTaskHealthManageServiceExtension.buildInitialLogCollectorTaskHealthPO(logCollectTaskDO, operator);
        logCollectTaskHealthDAO.insert(logCollectorTaskHealthPO);
        return logCollectorTaskHealthPO.getId();
    }

    /**
     * 根据给定日志采集任务 id 删除对应日志采集任务关联的日志采集任务健康对象流程
     * @param logCollectTaskId 日志采集任务 id
     * @param operator 操作人
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleDeleteByLogCollectTaskId(Long logCollectTaskId, String operator) throws ServiceException {

        /*
         * 删除 表 tb_log_collect_task_health_detail 相关记录
         */
        logCollectTaskHealthDetailManageService.deleteByLogCollectTaskId(logCollectTaskId);
        LogCollectTaskHealthPO logCollectorTaskHealthPO = logCollectTaskHealthDAO.selectByLogCollectTaskId(logCollectTaskId);
        if(null == logCollectorTaskHealthPO) {
            throw new ServiceException(
                    String.format("根据给定日志采集任务id={%d}删除对应日志采集任务健康对象失败，原因为：系统中不存在logCollectTaskId为{%d}的LogCollectorTaskHealthPO对象", logCollectTaskId, logCollectTaskId),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        logCollectTaskHealthDAO.deleteByLogCollectTaskId(logCollectTaskId);
    }

    @Override
    public LogCollectTaskHealthLevelEnum checkLogCollectTaskHealth(LogCollectTaskDO logCollectTaskDO) {
        /*
         * 校验日志采集任务是否须被诊断
         */
        CheckResult checkResult = logCollectTaskNeedCheck(logCollectTaskDO);
        /*
         * 日志采集任务是否在一轮指标周期内添加/更新（ps：规避日志采集任务添加完，metrics未上报上来，开始巡检逻辑 case）
         */
        if(logCollectTaskCreateOrUpdateJustNow(logCollectTaskDO)) {
            return LogCollectTaskHealthLevelEnum.fromMetricCode(getByLogCollectTaskId(logCollectTaskDO.getId()).getLogCollectTaskHealthLevel());
        }
        /*
         * 日志采集任务对应接收端信息是否在一轮指标周期内添加/更新（ps：规避日志采集任务添加完，metrics未上报上来，开始巡检逻辑 case）
         */
        ReceiverDO receiverDO = kafkaClusterManageService.getById(logCollectTaskDO.getKafkaClusterId());
        if(
                null != receiverDO &&
                receiverCreateOrUpdateJustNow(receiverDO)
        ) {
            return LogCollectTaskHealthLevelEnum.fromMetricCode(getByLogCollectTaskId(logCollectTaskDO.getId()).getLogCollectTaskHealthLevel());
        }
        /*
         * 诊断对应日志采集任务
         */
        if (checkResult.getCheckResult()) {//须诊断对应日志采集任务
            if (logCollectTaskDO.getLogCollectTaskType().equals(LogCollectTaskTypeEnum.NORMAL_COLLECT.getCode())) {
                return handleCheckNormalLogCollectTaskHealth(logCollectTaskDO, true);
            } else if (logCollectTaskDO.getLogCollectTaskType().equals(LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode()) && !logCollectTaskDO.getLogCollectTaskStatus().equals(LogCollectTaskStatusEnum.FINISH.getCode())) {
                //
                throw new ServiceException(
                        String.format("LogCollectTask={id=%d}不支持时间范围采集类型={%d}采集任务健康度检查", logCollectTaskDO.getId(), LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getDescription(), logCollectTaskDO.getLogCollectTaskType()),
                        ErrorCodeEnum.SCOPE_COLLECT_LOGCOLLECTTASK_HEALTH_CHECK_NOT_SUPPORT.getCode()
                );
            } else {
                throw new ServiceException(
                        String.format("LogCollectTask={id=%d}未知采集类型={%d}", logCollectTaskDO.getId(), logCollectTaskDO.getLogCollectTaskType()),
                        ErrorCodeEnum.UNKNOWN_COLLECT_TYPE.getCode()
                );
            }
        } else {//该日志采集任务无须被诊断
            return LogCollectTaskHealthLevelEnum.GREEN;
        }
    }

    private boolean receiverCreateOrUpdateJustNow(ReceiverDO receiverDO) {
        Long currentTime = System.currentTimeMillis();
        if(
                ((currentTime - receiverDO.getModifyTime().getTime()) > 3 * 60 * 1000l)
        ) {
            return false;
        }
        return true;
    }

    private boolean logCollectTaskCreateOrUpdateJustNow(LogCollectTaskDO logCollectTaskDO) {
        Long currentTime = System.currentTimeMillis();
        if(
                ((currentTime - logCollectTaskDO.getModifyTime().getTime()) > 3 * 60 * 1000l)
        ) {
            return false;
        }
        return true;
    }

    /**
     * 校验给定日志采集任务是否需要进行诊断
     *
     * @param logCollectTaskDO 待校验日志采集任务对象
     * @return 返回给定日志采集任务是否需要进行诊断 true：需要 false：不需要
     */
    private CheckResult logCollectTaskNeedCheck(LogCollectTaskDO logCollectTaskDO) throws ServiceException {
        //TODO：后续添加日志采集任务黑名单功能后，须添加黑名单过滤规则
        Integer logCollectTaskType = logCollectTaskDO.getLogCollectTaskType();
        if (LogCollectTaskTypeEnum.NORMAL_COLLECT.getCode().equals(logCollectTaskType)) {//流采
            if (logCollectTaskDO.getLogCollectTaskStatus().equals(LogCollectTaskStatusEnum.STOP.getCode())) {//待校验日志采集任务处于"停止"状态
                return new CheckResult(false);
            }
        } else if (LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode().equals(logCollectTaskType)) {//时间范围采集
            if (logCollectTaskDO.getLogCollectTaskStatus().equals(LogCollectTaskStatusEnum.FINISH.getCode())) {//待校验日志采集任务处于"已完成"状态
                return new CheckResult(false);
            }
        } else {
            throw new ServiceException(
                    String.format("待校验日志采集任务对象={%s}的logCollectTaskType属性值={%d}不合法，合法值范围见枚举类LogCollectTaskTypeEnum定义", JSON.toJSONString(logCollectTaskDO), logCollectTaskType),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        return new CheckResult(true);
    }

    /**
     * 诊断给定流式日志采集任务
     *
     * @param logCollectTaskDO  待诊断日志采集任务对象
     * @param checkCollectDelay 是否校验日志采集任务采集延时 true：校验 false：不校验
     * @return 待诊断日志采集任务对象对应诊断结果
     */
    private LogCollectTaskHealthLevelEnum handleCheckNormalLogCollectTaskHealth(LogCollectTaskDO logCollectTaskDO, boolean checkCollectDelay) throws ServiceException {
        /*
         * 获取待检测日志采集任务对应健康记录
         */
        LogCollectTaskHealthDO logCollectTaskHealthDO = getByLogCollectTaskId(logCollectTaskDO.getId());
        if (null == logCollectTaskHealthDO) {
            throw new ServiceException(String.format("LogCollectTaskHealth={logCollectTaskId=%d}在系统中不存在", logCollectTaskDO.getId()), ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode());
        }
        /*
         * 获取日式采集任务须部署的主机列表
         */
        List<HostDO> hostDOList = hostManageService.getHostListByLogCollectTaskId(logCollectTaskDO.getId());//部署日志采集任务的主机对象集
        /*
         * 获取日志采集任务须采集的文件路径集
         */
        List<FileLogCollectPathDO> fileLogCollectPathDOList = logCollectTaskDO.getFileLogCollectPathList();//日志采集任务对应各采集路径
        /*
         * 进入巡检流程，初始化
         */
        LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum = LogCollectTaskHealthLevelEnum.GREEN;//日志采集任务健康度检查结果
        String logCollectTaskHealthDescription = LogCollectTaskHealthInspectionResultEnum.HEALTHY.getDescription();//日志采集任务健康检查描述
        LogCollectTaskHealthInspectionResultEnum logCollectTaskHealthInspectionResultEnum = LogCollectTaskHealthInspectionResultEnum.HEALTHY;//日志采集任务检查明细项枚举对象

        if (CollectionUtils.isNotEmpty(fileLogCollectPathDOList)) {
            for (FileLogCollectPathDO fileLogCollectPathDO : fileLogCollectPathDOList) {
                for (HostDO hostDO : hostDOList) {
                    /*
                     * 构建 处理链 & 处理链对应上下文对象
                     */
                    ProcessorChain processorChain = getLogCollectTaskHealthCheckProcessorChain();
                    LogCollectTaskHealthCheckContext context = buildProcessChainContext(
                            logCollectTaskDO,
                            checkCollectDelay,
                            logCollectTaskHealthLevelEnum,
                            logCollectTaskHealthDescription,
                            fileLogCollectPathDO,
                            hostDO,
                            logCollectTaskHealthInspectionResultEnum
                    );
                    /*
                     * 执行巡检流程
                     */
                    processorChain.process(context, processorChain);
                    /*
                     * 判断巡检结果
                     */
                    if (context.getLogCollectTaskHealthLevelEnum() != LogCollectTaskHealthLevelEnum.GREEN) {
                        /*
                         * 更新日志采集任务健康信息
                         */
                        updateLogCollectHealth(logCollectTaskHealthDO, context.getLogCollectTaskHealthLevelEnum(), context.getLogCollectTaskHealthDescription(), context.getLogCollectTaskHealthInspectionResultEnum());
                        return context.getLogCollectTaskHealthLevelEnum();
                    }
                }
            }
        }
        if (
                null != logCollectTaskHealthLevelEnum &&
                        (
                                logCollectTaskHealthLevelEnum.getCode().equals(LogCollectTaskHealthLevelEnum.RED.getCode()) ||
                                        logCollectTaskHealthLevelEnum.getCode().equals(LogCollectTaskHealthLevelEnum.YELLOW.getCode())
                        )
        ) {
            //表示健康度已被检测为 red or yellow，此时，无须再进行校验
            //do nothing
        } else {
            /*
             * 校验 logcollecttask 对应下游 topic 是否被限流
             */
            boolean topicLimitExists = checkTopicLimitExists(logCollectTaskDO.getKafkaClusterId(), logCollectTaskDO.getSendTopic());
            if (topicLimitExists) {//存在下游 topic 端被限流
                logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.TOPIC_LIMIT_EXISTS.getLogCollectTaskHealthLevelEnum();
                logCollectTaskHealthDescription = String.format(
                        LogCollectTaskHealthInspectionResultEnum.TOPIC_LIMIT_EXISTS.getDescription(),
                        logCollectTaskDO.getLogCollectTaskName(),
                        logCollectTaskDO.getSendTopic()
                );
                logCollectTaskHealthInspectionResultEnum = LogCollectTaskHealthInspectionResultEnum.TOPIC_LIMIT_EXISTS;
            }
            /*
             * 校验 logcollecttask 是否未关联主机
             */
            boolean notRelateAnyHost = logCollectTaskManageService.checkNotRelateAnyHost(logCollectTaskDO.getId());
            if (notRelateAnyHost) {//logcollecttask 未关联主机
                logCollectTaskHealthLevelEnum = LogCollectTaskHealthInspectionResultEnum.NOT_RELATE_ANY_HOST.getLogCollectTaskHealthLevelEnum();
                logCollectTaskHealthDescription = String.format(
                        LogCollectTaskHealthInspectionResultEnum.NOT_RELATE_ANY_HOST.getDescription(),
                        logCollectTaskDO.getLogCollectTaskName()
                );
                logCollectTaskHealthInspectionResultEnum = LogCollectTaskHealthInspectionResultEnum.NOT_RELATE_ANY_HOST;
            }
        }
        /*
         * 更新日志采集任务健康信息
         */
        updateLogCollectHealth(logCollectTaskHealthDO, logCollectTaskHealthLevelEnum, logCollectTaskHealthDescription, logCollectTaskHealthInspectionResultEnum);
        return logCollectTaskHealthLevelEnum;
    }

    /**
     * 根据给定参数更新对应LogCollectTaskHealthDO对象
     * @param logCollectTaskHealthDO 待更新LogCollectTaskHealthDO对象
     * @param logCollectTaskHealthLevelEnum 日志采集任务健康度枚举对象
     * @param logCollectTaskHealthDescription 日志采集任务健康度描述信息
     * @param logCollectTaskHealthInspectionResultEnum 日志采集任务健康度巡检结果枚举对象
     */
    private void updateLogCollectHealth(
            LogCollectTaskHealthDO logCollectTaskHealthDO,
            LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum,
            String logCollectTaskHealthDescription,
            LogCollectTaskHealthInspectionResultEnum logCollectTaskHealthInspectionResultEnum
    ) {
        logCollectTaskHealthDO.setLogCollectTaskHealthLevel(logCollectTaskHealthLevelEnum.getCode());
        logCollectTaskHealthDO.setLogCollectTaskHealthDescription(logCollectTaskHealthDescription);
        logCollectTaskHealthDO.setLogCollectTaskHealthInspectionResultType(logCollectTaskHealthInspectionResultEnum.getCode());
        updateLogCollectorTaskHealth(logCollectTaskHealthDO, CommonConstant.getOperator(null));
    }

    private LogCollectTaskHealthCheckContext buildProcessChainContext(LogCollectTaskDO logCollectTaskDO, boolean checkCollectDelay, LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum, String logCollectTaskHealthDescription, FileLogCollectPathDO fileLogCollectPathDO, HostDO hostDO, LogCollectTaskHealthInspectionResultEnum logCollectTaskHealthInspectionResultEnum) {
        LogCollectTaskHealthCheckContext context = new LogCollectTaskHealthCheckContext();
        context.setLogCollectTaskDO(logCollectTaskDO);
        context.setFileLogCollectPathDO(fileLogCollectPathDO);
        context.setHostDO(hostDO);
        context.setCheckCollectDelay(checkCollectDelay);
        context.setLogCollectTaskHealthLevelEnum(logCollectTaskHealthLevelEnum);
        context.setLogCollectTaskHealthDescription(logCollectTaskHealthDescription);
        context.setLogCollectTaskHealthInspectionResultEnum(logCollectTaskHealthInspectionResultEnum);
        context.setLogCollectTaskHealthDetailManageService(logCollectTaskHealthDetailManageService);
        context.setMetricsManageService(metricsManageService);
        context.setHostManageService(hostManageService);
        context.setAgentManageService(agentManageService);
        return context;
    }

    /**
     * 校验给定kafka集群topic是否存在限流
     * @param kafkaClusterId kafka集群id
     * @param sendTopic topic
     * @return true：存在限流 false：不存在限流
     */
    private boolean checkTopicLimitExists(Long kafkaClusterId, String sendTopic) {

        /*
         *
         * TODO：
         *
         */

        return false;

    }

    /**
     * @return 获取日志采集任务健康度检查处理器链
     */
    private ProcessorChain getLogCollectTaskHealthCheckProcessorChain() {
        ProcessorChain processorChain = new ProcessorChain();
        for(Class<Processor> clazz : GlobalProperties.LOG_COLLECT_TASK_HEALTH_CHECK_PROCESSOR_CLASS_LIST) {
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
