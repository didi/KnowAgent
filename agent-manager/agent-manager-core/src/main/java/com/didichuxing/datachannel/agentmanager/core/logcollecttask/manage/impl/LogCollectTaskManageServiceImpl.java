package com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.common.ListCompareResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.k8s.K8sPodDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.DirectoryLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskPaginationRecordDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskServicePO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.constant.LogCollectTaskConstant;
import com.didichuxing.datachannel.agentmanager.common.constant.LogCollectTaskHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.host.HostTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskStatusEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.ModuleEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.OperationEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.Comparator;
import com.didichuxing.datachannel.agentmanager.common.util.ListCompareUtil;
import com.didichuxing.datachannel.agentmanager.core.agent.configuration.AgentCollectConfigManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.common.OperateRecordService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.k8s.K8sPodManageService;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.DirectoryLogCollectPathManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.FileLogCollectPathManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceLogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.LogCollectTaskMapper;
import com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.manage.extension.LogCollectTaskManageServiceExtension;
import com.didichuxing.datachannel.agentmanager.thirdpart.metadata.k8s.util.K8sUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务管理服务实现类
 */
@Service
public class LogCollectTaskManageServiceImpl implements LogCollectTaskManageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCollectTaskManageServiceImpl.class);
    private static final int HEARTBEAT_PERIOD = 30;

    @Autowired
    private LogCollectTaskMapper logCollectorTaskDAO;

    @Autowired
    private LogCollectTaskManageServiceExtension logCollectTaskManageServiceExtension;

    @Autowired
    private LogCollectTaskHealthManageService logCollectTaskHealthManageService;

    @Autowired
    private DirectoryLogCollectPathManageService directoryLogCollectPathManageService;

    @Autowired
    private FileLogCollectPathManageService fileLogCollectPathManageService;

    @Autowired
    private ServiceManageService serviceManageService;

    @Autowired
    private ServiceLogCollectTaskManageService serviceLogCollectTaskManageService;

    @Autowired
    private KafkaClusterManageService kafkaClusterManageService;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private OperateRecordService operateRecordService;

    @Autowired
    private AgentCollectConfigManageService agentCollectConfigManageService;

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private AgentMetricsManageService agentMetricsManageService;

    @Autowired
    private K8sPodManageService k8sPodManageService;

    @Override
    @Transactional
    public Long createLogCollectTask(LogCollectTaskDO logCollectTask, String operator) {
        return this.handleCreateLogCollectorTask(logCollectTask, operator);
    }

    @Override
    @Transactional
    public void switchLogCollectTask(Long logCollectTaskId, Integer status, String operator) {
        this.handleSwitchLogCollectTask(logCollectTaskId, status, operator);
    }

    @Override
    public List<LogCollectTaskPaginationRecordDO> paginationQueryByConditon(LogCollectTaskPaginationQueryConditionDO logCollectTaskPaginationQueryConditionDO) {
        String column = logCollectTaskPaginationQueryConditionDO.getSortColumn();
        if (column != null) {
            for (char c : column.toCharArray()) {
                if (!Character.isLetter(c) && c != '_') {
                    return Collections.emptyList();
                }
            }
        }
        List<LogCollectTaskPaginationRecordDO> logCollectTaskPaginationRecordDOList = logCollectorTaskDAO.paginationQueryByConditon(logCollectTaskPaginationQueryConditionDO);
        if (CollectionUtils.isEmpty(logCollectTaskPaginationRecordDOList)) {
            return new ArrayList<>();
        } else {
            for (LogCollectTaskPaginationRecordDO logCollectTaskPaginationRecordDO : logCollectTaskPaginationRecordDOList) {
                logCollectTaskPaginationRecordDO.setRelationServiceList(serviceManageService.getServicesByLogCollectTaskId(logCollectTaskPaginationRecordDO.getLogCollectTaskId()));
                logCollectTaskPaginationRecordDO.setRelationReceiverDO(kafkaClusterManageService.getById(logCollectTaskPaginationRecordDO.getKafkaClusterId()));
            }
        }
        return logCollectTaskPaginationRecordDOList;
    }

    @Override
    public Integer queryCountByCondition(LogCollectTaskPaginationQueryConditionDO logCollectTaskPaginationQueryConditionDO) {
        return logCollectorTaskDAO.queryCountByCondition(logCollectTaskPaginationQueryConditionDO);
    }

    /**
     * 启/停日志采集任务
     *
     * @param logCollectTaskId 日志采集任务id
     * @param operator         操作人
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleSwitchLogCollectTask(Long logCollectTaskId, Integer status, String operator) throws ServiceException {
        /*
         * 校验 status 是否合法
         */
        if (null == status || (LogCollectTaskStatusEnum.invalidStatus(status))) {
            throw new ServiceException(
                    String.format("给定日志采集任务启|停状态={%d}非法，合法取值范围为[0,1]", status),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        /*
         * 校验待启/停日志采集任务在系统中是否存在
         */
        LogCollectTaskPO logCollectTaskPO = logCollectorTaskDAO.selectByPrimaryKey(logCollectTaskId);
        if (null == logCollectTaskPO) {
            throw new ServiceException(
                    String.format("待启/停LogCollectTask={id=%d}在系统中不存在", logCollectTaskId),
                    ErrorCodeEnum.LOGCOLLECTTASK_NOT_EXISTS.getCode()
            );
        }
        /*
         * 更新日志采集任务启/停状态
         */
        logCollectTaskPO.setLogCollectTaskStatus(status);
        logCollectTaskPO.setOperator(CommonConstant.getOperator(operator));
        logCollectorTaskDAO.updateByPrimaryKey(logCollectTaskPO);
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.LOG_COLLECT_TASK,
                OperationEnum.EDIT,
                logCollectTaskId,
                String.format("修改LogCollectTask={id={%d}}对应状态logCollectTaskStatus={%d}", logCollectTaskId, status),
                operator
        );
    }

    /**
     * 创建一个日志采集任务信息，在日志采集任务添加操作成功后，自动构建日志采集任务 & 服务关联关系
     * 注：该函数作为一个整体运行在一个事务中，不抛异常提交事务，抛异常回滚事务
     *
     * @param logCollectTaskDO 日志采集任务对象
     * @param operator         操作人
     * @return 创建成功的日志采集任务对象id值
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long handleCreateLogCollectorTask(LogCollectTaskDO logCollectTaskDO, String operator) throws ServiceException {
        /*
         * 校验日志采集任务对象参数信息是否合法
         */
        CheckResult checkResult = logCollectTaskManageServiceExtension.checkCreateParameterLogCollectTask(logCollectTaskDO);
        if (!checkResult.getCheckResult()) {//日志采集任务对象信息不合法
            throw new ServiceException(
                    checkResult.getMessage(),
                    checkResult.getCode()
            );
        }
        /*
         * 持久化给定logCollectTask对象，及其关联的LogCollectPath对象集，并获取持久化的日志采集任务对象 id
         */
        Long savedLogCollectTaskId = saveLogCollectTask(logCollectTaskDO, operator);
        /*
         * 初始化 & 持久化日志采集任务关联的日志采集任务健康度信息
         */
        logCollectTaskHealthManageService.createInitialLogCollectorTaskHealth(savedLogCollectTaskId, operator);
        /*
         * 持久化待创建日志采集任务对象 & 服务关联关系
         */
        saveServiceLogCollectTaskRelation(logCollectTaskDO.getServiceIdList(), savedLogCollectTaskId);
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.LOG_COLLECT_TASK,
                OperationEnum.ADD,
                savedLogCollectTaskId,
                String.format("创建LogCollectTask={%s}，创建成功的LogCollectTask对象id={%d}", JSON.toJSONString(logCollectTaskDO), savedLogCollectTaskId),
                operator
        );
        return savedLogCollectTaskId;
    }

    /**
     * 根据给定 serviceIdList & logCollectTaskId 持久化对应服务 & 日志采集任务关联关系
     *
     * @param serviceIdList    服务对象id集
     * @param logCollectTaskId 日志采集任务对象id
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void saveServiceLogCollectTaskRelation(List<Long> serviceIdList, Long logCollectTaskId) throws ServiceException {
        List<LogCollectTaskServicePO> logCollectTaskServicePOList = new ArrayList<>(serviceIdList.size());
        for (Long serviceId : serviceIdList) {
            logCollectTaskServicePOList.add(new LogCollectTaskServicePO(logCollectTaskId, serviceId));
        }
        serviceLogCollectTaskManageService.createLogCollectTaskServiceList(logCollectTaskServicePOList);
    }

    /**
     * 持久化给定日志采集任务 & 关联 LogCollectPath对象集 & LogCollectTaskHealthPO 对象对象并返回已持久化的日志采集任务对象 id 值
     *
     * @param logCollectTaskDO 日志采集任务对象
     * @return 持久化的日志采集任务对象 id 值
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long saveLogCollectTask(LogCollectTaskDO logCollectTaskDO, String operator) throws ServiceException {
        /*
         * 持久化日志采集任务对象 LogCollectTaskPO
         */
        LogCollectTaskPO logCollectTaskPO = logCollectTaskManageServiceExtension.logCollectTask2LogCollectTaskPO(logCollectTaskDO);
        logCollectTaskPO.setOperator(CommonConstant.getOperator(operator));
        logCollectTaskPO.setConfigurationVersion(LogCollectTaskConstant.LOG_COLLECT_TASK_CONFIGURATION_VERSION_INIT);
        logCollectTaskPO.setLogCollectTaskStatus(LogCollectTaskStatusEnum.RUNNING.getCode());
        logCollectorTaskDAO.insert(logCollectTaskPO);
        Long logCollectTaskId = logCollectTaskPO.getId();
        /*
         * 持久化日志采集任务关联的日志采集路径对象集
         */
        List<DirectoryLogCollectPathDO> directoryLogCollectPathList = logCollectTaskDO.getDirectoryLogCollectPathList();
        List<FileLogCollectPathDO> fileLogCollectPathList = logCollectTaskDO.getFileLogCollectPathList();
        if (CollectionUtils.isEmpty(directoryLogCollectPathList) && CollectionUtils.isEmpty(fileLogCollectPathList)) {
            throw new ServiceException(
                    String.format(
                            "class=LogCollectTaskManageServiceImpl||method=saveLogCollectTask||msg={%s}",
                            String.format("LogCollectTask对象={%s}关联的日志采集路径不可为空", JSON.toJSONString(logCollectTaskDO))
                    ),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        if (CollectionUtils.isNotEmpty(directoryLogCollectPathList)) {
            for (DirectoryLogCollectPathDO directoryLogCollectPath : directoryLogCollectPathList) {
                //持久化目录类型日志采集路径对象 DirectoryLogCollectPathPO
                directoryLogCollectPath.setLogCollectTaskId(logCollectTaskId);
                directoryLogCollectPathManageService.createDirectoryLogCollectPath(directoryLogCollectPath, operator);
            }
        }
        if (CollectionUtils.isNotEmpty(fileLogCollectPathList)) {
            for (FileLogCollectPathDO fileLogCollectPath : fileLogCollectPathList) {
                //持久化文件类型日志采集路径对象 FileLogCollectPathPO
                fileLogCollectPath.setLogCollectTaskId(logCollectTaskId);
                fileLogCollectPathManageService.createFileLogCollectPath(fileLogCollectPath, operator);
            }
        }
        return logCollectTaskId;
    }

    @Override
    @Transactional
    public void deleteLogCollectTask(Long id, String operator) {
        this.handleDeleteLogCollectTask(id, operator);
    }

    /**
     * 删除给定id对应日志采集任务对象
     *
     * @param logCollectTaskId 待删除日志采集任务对象 logCollectTaskId 值
     * @param operator         操作人
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleDeleteLogCollectTask(Long logCollectTaskId, String operator) throws ServiceException {
        /*
         * 检查入参 logCollectTaskId 是否为空
         */
        if (null == logCollectTaskId) {
            throw new ServiceException(
                    "入参logCollectTaskId不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        /*
         * 检查待删除日志采集任务 logCollectTaskId 对应日志采集任务对象在系统是否存在
         */
        if (null == logCollectorTaskDAO.selectByPrimaryKey(logCollectTaskId)) {
            throw new ServiceException(
                    String.format("删除logCollectTask对象{logCollectTaskId=%d}失败，原因为：系统中不存在id={%d}的logCollectTask对象", logCollectTaskId, logCollectTaskId),
                    ErrorCodeEnum.LOGCOLLECTTASK_NOT_EXISTS.getCode()
            );
        }
        /*
         * 删除日志采集任务 & 服务关联关系
         */
        serviceLogCollectTaskManageService.removeServiceLogCollectTaskByLogCollectTaskId(logCollectTaskId);
        /*
         * 删除日志采集任务关联的日志采集任务健康信息
         */
        logCollectTaskHealthManageService.deleteByLogCollectTaskId(logCollectTaskId, operator);
        /*
         * 删除日志采集任务关联的日志采集路径对象集
         */
        directoryLogCollectPathManageService.deleteByLogCollectTaskId(logCollectTaskId);
        fileLogCollectPathManageService.deleteByLogCollectTaskId(logCollectTaskId);
        /*
         * 删除日志采集任务信息
         */
        logCollectorTaskDAO.deleteByPrimaryKey(logCollectTaskId);
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.LOG_COLLECT_TASK,
                OperationEnum.DELETE,
                logCollectTaskId,
                String.format("删除LogCollectTask对象={id={%d}}", logCollectTaskId),
                operator
        );
    }

    @Override
    @Transactional
    public void updateLogCollectTask(LogCollectTaskDO logCollectTask, String operator) {
        this.handleUpdateLogCollectTask(logCollectTask, operator);
    }

    /**
     * 更新一个日志采集任务信息：删除更新前的日志采集任务 & 服务关联关系 -> 构建更新后的日志采集任务 & 已存在服务关联关系 -> 更新"除日志采集任务 & 已存在服务关联关系外的全量日志采集任务元信息"
     * 注：该函数作为一个整体运行在一个事务中，不抛异常提交事务，抛异常回滚事务
     *
     * @param logCollectTaskDO 待更新日志采集任务对象
     * @param operator         操作人
     */
    private void handleUpdateLogCollectTask(LogCollectTaskDO logCollectTaskDO, String operator) throws ServiceException {
        /*
         * 校验日志采集任务对象参数信息是否合法
         */
        CheckResult checkResult = logCollectTaskManageServiceExtension.checkUpdateParameterLogCollectTask(logCollectTaskDO);
        if (!checkResult.getCheckResult()) {//日志采集任务对象信息不合法
            throw new ServiceException(
                    checkResult.getMessage(),
                    checkResult.getCode()
            );
        }
        /*
         * 校验待更新日志采集任务在系统中是否存在
         */
        LogCollectTaskDO logCollectTaskDOSource = getById(logCollectTaskDO.getId());
        if (null == logCollectTaskDOSource) {
            throw new ServiceException(
                    String.format("待更新LogCollectTask={id=%d}在系统中不存在", logCollectTaskDO.getId()),
                    ErrorCodeEnum.LOGCOLLECTTASK_NOT_EXISTS.getCode()
            );
        }
        /*
         * 更新日志采集任务
         */
        LogCollectTaskDO logCollectTaskDO2Save = logCollectTaskManageServiceExtension.updateLogCollectTask(logCollectTaskDOSource, logCollectTaskDO);
        LogCollectTaskPO logCollectTaskPO = logCollectTaskManageServiceExtension.logCollectTask2LogCollectTaskPO(logCollectTaskDO2Save);
        logCollectTaskPO.setOperator(CommonConstant.getOperator(operator));
        logCollectorTaskDAO.updateByPrimaryKey(logCollectTaskPO);
        /*
         * 更新日志采集任务关联的日志采集路径集相关信息
         */
        ListCompareResult<DirectoryLogCollectPathDO> directoryLogCollectPathDOListCompareResult = ListCompareUtil.compare(logCollectTaskDOSource.getDirectoryLogCollectPathList(), logCollectTaskDO.getDirectoryLogCollectPathList(), new Comparator<DirectoryLogCollectPathDO, String>() {
            @Override
            public String getKey(DirectoryLogCollectPathDO directoryLogCollectPathDO) {
                return directoryLogCollectPathDO.getPath();
            }

            @Override
            public boolean compare(DirectoryLogCollectPathDO t1, DirectoryLogCollectPathDO t2) {
                return t1.getCollectFilesFilterRegularPipelineJsonString().equals(t2.getCollectFilesFilterRegularPipelineJsonString()) &&
                        t1.getDirectoryCollectDepth().equals(t2.getDirectoryCollectDepth()) &&
                        t1.getPath().equals(t2.getPath());
            }

            @Override
            public DirectoryLogCollectPathDO getModified(DirectoryLogCollectPathDO source, DirectoryLogCollectPathDO target) {
                if (!source.getPath().equals(target.getPath())) {
                    source.setPath(target.getPath());
                }
                if (!source.getDirectoryCollectDepth().equals(target.getDirectoryCollectDepth())) {
                    source.setDirectoryCollectDepth(target.getDirectoryCollectDepth());
                }
                if (!source.getCollectFilesFilterRegularPipelineJsonString().equals(target.getCollectFilesFilterRegularPipelineJsonString())) {
                    source.setCollectFilesFilterRegularPipelineJsonString(target.getCollectFilesFilterRegularPipelineJsonString());
                }
                return source;
            }
        });
        for (DirectoryLogCollectPathDO directoryLogCollectPathDO : directoryLogCollectPathDOListCompareResult.getCreateList()) {
            directoryLogCollectPathDO.setLogCollectTaskId(logCollectTaskDO.getId());
            directoryLogCollectPathManageService.createDirectoryLogCollectPath(directoryLogCollectPathDO, operator);
        }
        for (DirectoryLogCollectPathDO directoryLogCollectPathDO : directoryLogCollectPathDOListCompareResult.getRemoveList()) {
            directoryLogCollectPathManageService.deleteDirectoryLogCollectPath(directoryLogCollectPathDO.getId(), operator);
        }
        for (DirectoryLogCollectPathDO directoryLogCollectPathDO : directoryLogCollectPathDOListCompareResult.getModifyList()) {
            directoryLogCollectPathManageService.updateDirectoryLogCollectPath(directoryLogCollectPathDO, operator);
        }
        ListCompareResult<FileLogCollectPathDO> fileLogCollectPathDOListCompareResult = ListCompareUtil.compare(logCollectTaskDOSource.getFileLogCollectPathList(), logCollectTaskDO.getFileLogCollectPathList(), new Comparator<FileLogCollectPathDO, String>() {
            @Override
            public String getKey(FileLogCollectPathDO fileLogCollectPathDO) {
                return fileLogCollectPathDO.getPath();
            }

            @Override
            public boolean compare(FileLogCollectPathDO t1, FileLogCollectPathDO t2) {
                return t1.getPath().equals(t2.getPath());
            }

            @Override
            public FileLogCollectPathDO getModified(FileLogCollectPathDO source, FileLogCollectPathDO target) {
                if (!source.getPath().equals(target.getPath())) {
                    source.setPath(target.getPath());
                }
                return source;
            }
        });

        for (FileLogCollectPathDO fileLogCollectPathDO : fileLogCollectPathDOListCompareResult.getRemoveList()) {
            fileLogCollectPathManageService.deleteFileLogCollectPath(fileLogCollectPathDO.getId(), operator);
        }
        for (FileLogCollectPathDO fileLogCollectPathDO : fileLogCollectPathDOListCompareResult.getModifyList()) {
            fileLogCollectPathManageService.updateFileLogCollectPath(fileLogCollectPathDO, operator);
        }

        /*
         * 更新日志采集任务对象 & 服务关联关系
         */
        serviceLogCollectTaskManageService.removeServiceLogCollectTaskByLogCollectTaskId(logCollectTaskDO.getId());
        saveServiceLogCollectTaskRelation(logCollectTaskDO.getServiceIdList(), logCollectTaskDO.getId());
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.LOG_COLLECT_TASK,
                OperationEnum.EDIT,
                logCollectTaskDO.getId(),
                String.format("修改LogCollectTask={%s}，修改成功的LogCollectTask对象id={%d}", JSON.toJSONString(logCollectTaskDO), logCollectTaskDO.getId()),
                operator
        );
    }

    @Override
    public List<LogCollectTaskDO> getLogCollectTaskListByHostId(Long hostId) {
        HostDO hostDO = hostManageService.getById(hostId);
        if (null == hostDO) {
            throw new ServiceException(
                    String.format("Host={id=%d}在系统中不存在", hostId),
                    ErrorCodeEnum.HOST_NOT_EXISTS.getCode()
            );
        }
        return getLogCollectTaskListByHost(hostDO);
    }

    @Override
    public LogCollectTaskDO getById(Long id) {
        /*
         * 加载LogCollectTaskDO
         */
        LogCollectTaskPO logCollectTaskPO = this.logCollectorTaskDAO.selectByPrimaryKey(id);
        if (null == logCollectTaskPO) {
            return null;
        }
        LogCollectTaskDO logCollectTaskDO = logCollectTaskManageServiceExtension.logCollectTaskPO2LogCollectTaskDO(logCollectTaskPO);
        /*
         * 加载LogCollectTaskDO.serviceIdList
         */
        List<ServiceDO> serviceDOList = serviceManageService.getServicesByLogCollectTaskId(id);
        List<Long> serviceIdList = new ArrayList<>(serviceDOList.size());
        for (ServiceDO serviceDO : serviceDOList) {
            serviceIdList.add(serviceDO.getId());
        }
        logCollectTaskDO.setServiceIdList(serviceIdList);
        /*
         * 加载LogCollectTaskDO.directoryLogCollectPathList
         */
        logCollectTaskDO.setDirectoryLogCollectPathList(directoryLogCollectPathManageService.getAllDirectoryLogCollectPathByLogCollectTaskId(id));
        /*
         * 加载LogCollectTaskDO.fileLogCollectPathList
         */
        logCollectTaskDO.setFileLogCollectPathList(fileLogCollectPathManageService.getAllFileLogCollectPathByLogCollectTaskId(id));
        return logCollectTaskDO;
    }

    @Override
    public Integer getRelatedAgentCount(Long id) {
        if (id <= 0) {
            throw new ServiceException("task id非法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        Set<Long> agentIds = new HashSet<>();
        List<HostDO> hosts = hostManageService.getHostListByLogCollectTaskId(id);
        Set<String> hostnames = new HashSet<>();
        for (HostDO host : hosts) {
            if (HostTypeEnum.CONTAINER.getCode().equals(host.getContainer())) {
                hostnames.add(host.getParentHostName());
            } else {
                hostnames.add(host.getHostName());
            }
        }
        for (String hostname : hostnames) {
            AgentDO agentDO = agentManageService.getAgentByHostName(hostname);
            if (agentDO != null) {
                agentIds.add(agentDO.getId());
            }
        }
        return agentIds.size();
    }

    @Override
    public List<LogCollectTaskDO> getAllLogCollectTask2HealthCheck() {
        List<LogCollectTaskPO> logCollectTaskPOList = logCollectorTaskDAO.getByStatus(LogCollectTaskStatusEnum.RUNNING.getCode());
        List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageServiceExtension.logCollectTaskPOList2LogCollectTaskDOList(logCollectTaskPOList);
        for (LogCollectTaskDO logCollectTaskDO : logCollectTaskDOList) {
            logCollectTaskDO.setDirectoryLogCollectPathList(directoryLogCollectPathManageService.getAllDirectoryLogCollectPathByLogCollectTaskId(logCollectTaskDO.getId()));
            logCollectTaskDO.setFileLogCollectPathList(fileLogCollectPathManageService.getAllFileLogCollectPathByLogCollectTaskId(logCollectTaskDO.getId()));
        }
        return logCollectTaskDOList;
    }

    @Override
    public List<LogCollectTaskDO> getLogCollectTaskListByHost(HostDO hostDO) {
        List<LogCollectTaskPO> logCollectTaskPOList = logCollectorTaskDAO.getLogCollectTaskListByHostId(hostDO.getId());
        if (CollectionUtils.isEmpty(logCollectTaskPOList)) {
            return new ArrayList<>();
        }
        String logMountPath = "";//k8s 容器内路径
        String logHostPath = "";//k8s 主机路径
        if (hostDO.getContainer().equals(HostTypeEnum.CONTAINER.getCode())) {
            K8sPodDO k8sPodDO = k8sPodManageService.getByContainerId(hostDO.getId());
            logMountPath = k8sPodDO.getLogMountPath();
            logHostPath = k8sPodDO.getLogHostPath();
        }
        List<LogCollectTaskDO> logCollectTaskList = new ArrayList<>(logCollectTaskPOList.size());
        for (LogCollectTaskPO logCollectTaskPO : logCollectTaskPOList) {
            LogCollectTaskDO logCollectTaskDO = logCollectTaskManageServiceExtension.logCollectTaskPO2LogCollectTaskDO(logCollectTaskPO);
            if (agentCollectConfigManageService.need2Deploy(logCollectTaskDO, hostDO)) {
                //根据日志采集任务id获取其关联的日志采集任务路径对象集
                List<FileLogCollectPathDO> fileLogCollectPathDOList = fileLogCollectPathManageService.getAllFileLogCollectPathByLogCollectTaskId(logCollectTaskDO.getId());
                /*
                 * 对于容器日志，进行日志路径转化 映射
                 */
                if (hostDO.getContainer().equals(HostTypeEnum.CONTAINER.getCode())) {
                    for (FileLogCollectPathDO fileLogCollectPathDO : fileLogCollectPathDOList) {
                        String path = fileLogCollectPathDO.getPath();
                        String realPath = K8sUtil.getRealPath(logMountPath, logHostPath, path);
                        fileLogCollectPathDO.setRealPath(realPath);
                    }
                }
                logCollectTaskDO.setFileLogCollectPathList(fileLogCollectPathDOList);
                logCollectTaskList.add(logCollectTaskDO);
            }
        }
        return logCollectTaskList;
    }

    @Override
    public List<LogCollectTaskDO> getLogCollectTaskListByServiceId(Long serviceId) {
        List<LogCollectTaskPO> logCollectTaskPOList = logCollectorTaskDAO.getLogCollectTaskListByServiceId(serviceId);
        return logCollectTaskPOList2LogCollectTaskDOListAndLoadRelationLogCollectPath(logCollectTaskPOList);
    }





    /**
     * 校验 时间范围采集类型日志采集任务是否已执行完
     *
     * @param logCollectTaskId     日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param hostDO               主机对象
     * @return 返回时间范围采集类型日志采集任务是否已执行完 true：已采集完 false：未采集完
     */
    private boolean checkTimeScopeCollectComplete(Long logCollectTaskId, Long fileLogCollectPathId, HostDO hostDO) {
        /*
         * 校验 logCollectTaskId + fileLogCollectPathId + hostName 近5分钟是否心跳正常 & 采集数量为 0
         */
        if (hostDO.getContainer().equals(HostTypeEnum.HOST.getCode())) {
            return agentMetricsManageService.hostCompleteCollect(hostDO.getHostName(), logCollectTaskId, fileLogCollectPathId);
        } else if (hostDO.getContainer().equals(HostTypeEnum.CONTAINER.getCode())) {
            return agentMetricsManageService.containerCompleteCollect(hostDO.getHostName(), hostDO.getParentHostName(), logCollectTaskId, fileLogCollectPathId);
        } else {
            throw new ServiceException(
                    String.format("Host={id=%d}未知主机类型={%d}", hostDO.getId(), hostDO.getContainer()),
                    ErrorCodeEnum.UNKNOWN_HOST_TYPE.getCode()
            );
        }
    }

    /**
     * 校验 logcollecttask 对应下游 topic 是否被限流
     *
     * @param kafkaClusterId 日志采集任务对应下游 kafkaCLuster id
     * @param sendTopic      日志采集任务对应下游 topic
     * @return true：限流 false：非限流
     */
    private boolean checkTopicLimitExists(Long kafkaClusterId, String sendTopic) {
        return kafkaClusterManageService.checkTopicLimitExists(kafkaClusterId, sendTopic);
    }

    public boolean checkNotRelateAnyHost(Long logCollectTaskId) {
        List<HostDO> hostDOList = hostManageService.getHostListByLogCollectTaskId(logCollectTaskId);
        return !CollectionUtils.isNotEmpty(hostDOList);
    }

    @Override
    public List<LogCollectTaskDO> getByHealthLevel(Integer logCollectTaskHealthLevelCode) {
        List<LogCollectTaskPO> logCollectTaskPOList = logCollectorTaskDAO.getLogCollectTaskListByHealthLevel(logCollectTaskHealthLevelCode);
        return logCollectTaskManageServiceExtension.logCollectTaskPOList2LogCollectTaskDOList(logCollectTaskPOList);
    }

    @Override
    public List<LogCollectTaskDO> getAll() {
        List<LogCollectTaskPO> logCollectTaskPOList = logCollectorTaskDAO.queryAll();
        return logCollectTaskManageServiceExtension.logCollectTaskPOList2LogCollectTaskDOList(logCollectTaskPOList);
    }

    /**
     * 校验 logcollecttask + logpath 在 host 端是否存在采集端出口流量阈值限流
     *
     * @param logCollectTaskId     日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param hostName             主机名
     * @return
     */
    private boolean checkByteLimitOnHostExists(Long logCollectTaskId, Long fileLogCollectPathId, String hostName) {
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

    /**
     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在执行超时情况
     *
     * @param logCollectTaskDO               日志采集任务对象
     * @param fileLogCollectPathId           日志采集路径 id
     * @param hostName                       主机名
     * @param logCollectTaskExecuteTimeoutMs 超时阈值 单位：ms
     * @return 返回 logCollectTaskId+fileLogCollectPathId在host上是否存在执行超时情况 true：存在 执行超时 false：不存在 执行超时
     */
    private boolean checkTimeScopeLogCollectTaskExecuteTimeout(LogCollectTaskDO logCollectTaskDO, Long fileLogCollectPathId, String hostName, Long logCollectTaskExecuteTimeoutMs) {
        /*
         * 根据 logCollectTaskId 获取对应日志采集任务对象，并获取其开始执行时间 executeStartTime
         */
        Long executeStartTime = logCollectTaskDO.getCreateTime().getTime();
        /*
         * 获取logCollectTaskId+fileLogCollectPathId在host上对应当前采集时间 currentCollectTime
         */
        Long currentCollectTime = getCurrentCompleteTime(logCollectTaskDO.getId(), fileLogCollectPathId, hostName);
        /*
         * currentCollectTime - executeStartTime > logCollectTaskExecuteTimeoutMs ? true : false
         */
        return currentCollectTime - executeStartTime > logCollectTaskExecuteTimeoutMs;
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
    private boolean checkCollectDelay(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long collectDelayThresholdMs) {
        /*
         * 获取logCollectTaskId+fileLogCollectPathId在host上对应当前采集时间 currentCollectTime
         */
        Long currentCollectTime = getCurrentCompleteTime(logCollectTaskId, fileLogCollectPathId, hostName);
        /*
         * System.currentTimeMillis() - currentCollectTime > collectDelayThresholdMs ? true : false
         */
        return System.currentTimeMillis() - currentCollectTime > collectDelayThresholdMs;
    }

    /**
     * 校验 logCollectTaskId+fileLogCollectPathId 在host上是否存在多 agent 并发采集
     *
     * @param logCollectTaskId                 日志采集任务 id
     * @param fileLogCollectPathId             日志采集路径 id
     * @param hostName                         主机名
     * @param logCollectTaskHealthCheckTimeEnd 日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一毫秒
     * @return 返回 logCollectTaskId+fileLogCollectPathId 在host上是否存在多 agent 并发采集 true：存在 并发采集 false：不存在 并发采集
     */
    private boolean checkConcurrentCollectExists(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long logCollectTaskHealthCheckTimeEnd) {
        /*
         * 获取近 LogCollectTaskHealthCheckConstant.HOST_CPU_LIMIT_CHECK_LASTEST_MS_THRESHOLD 时间范围内 logCollectTaskId + fileLogCollectPathId + hostName 心跳数，
         * 心跳数量 > LogCollectTaskHealthCheckConstant.HOST_CPU_LIMIT_CHECK_LASTEST_MS_THRESHOLD，表示 logCollectTaskId+fileLogCollectPathId 在 host 上是否存在多 agent 并发采集
         */
        Long startTime = logCollectTaskHealthCheckTimeEnd - LogCollectTaskHealthCheckConstant.CONCURRENT_COLLECT_CHECK_LASTEST_MS_THRESHOLD;
        Long heartbeatTimes = agentMetricsManageService.getHeartbeatTimesByTimeFrame(
                startTime,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                hostName
        );
        return heartbeatTimes > LogCollectTaskHealthCheckConstant.CONCURRENT_COLLECT_CHECK_HEARTBEAT_TIMES_THRESHOLD;
    }

    /**
     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在日志被异常截断
     *
     * @param logCollectTaskId                                                  日志采集任务 id
     * @param fileLogCollectPathId                                              日志采集路径 id
     * @param hostName                                                          主机名
     * @param logCollectTaskHealthCheckTimeEnd                                  日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一毫秒
     * @param fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap filePathId : LastestAbnormalTruncationCheckHealthyTime
     * @return 返回logCollectTaskId+fileLogCollectPathId在host上是否存在日志被异常截断 true：存在 异常截断 false：不存在 异常截断
     */
    private boolean checkAbnormalTruncationExists(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long logCollectTaskHealthCheckTimeEnd, Map<Long, Long> fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap) {
        /*
         * 获取自上次"异常截断"健康点 ~ 当前时间，logCollectTaskId+fileLogCollectPathId在host上是否存在异常截断
         */
        Long lastestCheckTime = fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap.get(fileLogCollectPathId);
        if (null == lastestCheckTime) {
            throw new ServiceException(
                    String.format("FileLogCollectPath={id=%d}对应AbnormalTruncationExistsCheckHealthyTime不存在", fileLogCollectPathId),
                    ErrorCodeEnum.LOGCOLLECTTASK_HEALTH_ABNORMAL_TRUNCATION_EXISTS_CHECK_HEALTHY_TIME_NOT_EXISTS.getCode()
            );
        }
        Integer abnormalTruncationCount = agentMetricsManageService.getAbnormalTruncationCountByTimeFrame(
                lastestCheckTime,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                hostName
        );
        return abnormalTruncationCount > 0;
    }

    /**
     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在日志切片错误
     *
     * @param logCollectTaskId                                        日志采集任务 id
     * @param fileLogCollectPathId                                    日志采集路径 id
     * @param hostName                                                主机名
     * @param logCollectTaskHealthCheckTimeEnd                        日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一毫秒
     * @param fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap filePathId : lastestLogSliceCheckHealthyTimeMap
     * @return 返回logCollectTaskId+fileLogCollectPathId在host上是否存在日志切片错误
     */
    private boolean checkLogSliceErrorExists(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long logCollectTaskHealthCheckTimeEnd, Map<Long, Long> fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap) {
        /*
         * 获取自上次"错误日志输出存在"健康点 ~ 当前时间，logCollectTaskId+fileLogCollectPathId在host上是否存在日志切片错误
         */
        Long lastestCheckTime = fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap.get(fileLogCollectPathId);
        if (null == lastestCheckTime) {
            throw new ServiceException(
                    String.format("FileLogCollectPath={id=%d}对应LogSliceCheckHealthyTime不存在", fileLogCollectPathId),
                    ErrorCodeEnum.LOGCOLLECTTASK_HEALTH_LOG_SLICE_CHECK_HEALTHY_TIME_NOT_EXISTS.getCode()
            );
        }
        Integer logSliceErrorCount = agentMetricsManageService.getSliceErrorCount(
                lastestCheckTime,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                hostName
        );
        return logSliceErrorCount > 0;
    }

    /**
     * 校验 logCollectTaskId+fileLogCollectPathId 在 host 上是否存在乱序
     *
     * @param logCollectTaskId                                            日志采集任务 id
     * @param fileLogCollectPathId                                        日志采集路径 id
     * @param hostName                                                    主机名
     * @param logCollectTaskHealthCheckTimeEnd                            日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一毫秒
     * @param fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap filePathId : LastestFileDisorderCheckHealthyTime
     * @return logCollectTaskId+fileLogCollectPathId 在 host 上是否存在乱序 true：存在 乱序 false：不存在 乱序
     */
    private boolean checkFileDisorder(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long logCollectTaskHealthCheckTimeEnd, Map<Long, Long> fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap) {
        /*
         * 获取自上次"文件乱序"健康点 ~ 当前时间，logCollectTaskId+fileLogCollectPathId在host上是否存在日志乱序
         */
        Long lastestCheckTime = fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap.get(fileLogCollectPathId);
        if (null == lastestCheckTime) {
            throw new ServiceException(
                    String.format("FileLogCollectPath={id=%d}对应FileDisorderCheckHealthyTime不存在", fileLogCollectPathId),
                    ErrorCodeEnum.LOGCOLLECTTASK_HEALTH_FILE_DISORDER_CHECK_HEALTHY_TIME_NOT_EXISTS.getCode()
            );
        }
        Integer fileDisorderCount = agentMetricsManageService.getFileDisorderCountByTimeFrame(
                lastestCheckTime,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                hostName
        );
        return fileDisorderCount > 0;
    }

    /**
     * 校验logCollectTaskId+fileLogCollectPathId在host上是否存在
     *
     * @param logCollectTaskId                                              日志采集任务 id
     * @param fileLogCollectPathId                                          日志采集路径 id
     * @param hostName                                                      主机名
     * @param logCollectTaskHealthCheckTimeEnd                              日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一秒
     * @param fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap filePathId : LastestFilePathExistsCheckHealthyTime
     * @return 返回logCollectTaskId+fileLogCollectPathId在host上是否存在 true：存在 false：不存在
     */
    private boolean checkFilePathExists(Long logCollectTaskId, Long fileLogCollectPathId, String hostName, Long logCollectTaskHealthCheckTimeEnd, Map<Long, Long> fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap) {
        /*
         * 获取自上次"日志采集路径存在"健康点 ~ 当前时间，logCollectTaskId+fileLogCollectPathId在host上是否存在对应待采集日志文件
         */
        Long lastestCheckTime = fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap.get(fileLogCollectPathId);
        if (null == lastestCheckTime) {
            throw new ServiceException(
                    String.format("FileLogCollectPath={id=%d}对应FilePathExistsCheckHealthyTime不存在", fileLogCollectPathId),
                    ErrorCodeEnum.LOGCOLLECTTASK_HEALTH_FILE_PATH_EXISTS_CHECK_HEALTHY_TIME_NOT_EXISTS.getCode()
            );
        }
        Integer filePathNotExistsCount = agentMetricsManageService.getFilePathNotExistsCountByTimeFrame(
                lastestCheckTime,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                hostName
        );
        return filePathNotExistsCount == 0;
    }

    /**
     * 校验在距当前时间的心跳存活判定周期内，logCollectTaskId+fileLogCollectPathId+hostName是否存在心跳
     *
     * @param logCollectTaskId                 日志采集任务 id
     * @param fileLogCollectPathId             日志采集路径 id
     * @param logCollectTaskHostName           日志采集任务对应主机名
     * @param logCollectTaskHealthCheckTimeEnd 日志采集任务健康度检查流程获取agent心跳数据右边界时间，取当前时间前一秒
     * @return 距当前时间的心跳存活判定周期内，logCollectTaskId+fileLogCollectPathId+hostName是否存在心跳 true：存在 心跳 false：不存在心跳
     */
    private boolean checkAliveByHeartbeat(Long logCollectTaskId, Long fileLogCollectPathId, String logCollectTaskHostName, Long logCollectTaskHealthCheckTimeEnd) {
        /*
         * 获取近 LogCollectTaskHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD 时间范围内 logCollectTaskId + fileLogCollectPathId + hostName 心跳数，
         * 心跳数量 == 0，表示 logCollectTaskId+fileLogCollectPathId 在 host 上不存在心跳
         */
        Long heartbeatTimes = agentMetricsManageService.getHeartbeatTimesByTimeFrame(
                logCollectTaskHealthCheckTimeEnd - LogCollectTaskHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD,
                logCollectTaskHealthCheckTimeEnd,
                logCollectTaskId,
                fileLogCollectPathId,
                logCollectTaskHostName
        );
        return !heartbeatTimes.equals(0L);
    }

    /**
     * 获取logCollectTaskId+fileLogCollectPathId+hostName当前采集时间
     *
     * @param logCollectTaskId     日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param hostName             主机名
     * @return 返回logCollectTaskId+fileLogCollectPathId+hostName当前采集时间
     */
    private Long getCurrentCompleteTime(Long logCollectTaskId, Long fileLogCollectPathId, String hostName) {
        return agentMetricsManageService.getLastestCollectTime(logCollectTaskId, fileLogCollectPathId, hostName);
    }



    @Override
    public List<LogCollectTaskDO> getLogCollectTaskListByAgentId(Long agentId) {
        List<HostDO> collectHostDOList = hostManageService.getRelationHostListByAgentId(agentId);//agent 待采集 host 集
        List<LogCollectTaskDO> result = new ArrayList<>();
        /*
         * 获取各host对应日志采集任务
         */
        for (HostDO hostDO : collectHostDOList) {
            List<LogCollectTaskDO> LogCollectTaskDOList = getLogCollectTaskListByHost(hostDO);
            if (CollectionUtils.isNotEmpty(LogCollectTaskDOList)) {
                result.addAll(LogCollectTaskDOList);
            }
        }
        return result;
    }

    @Override
    public List<LogCollectTaskDO> getLogCollectTaskListByAgentHostName(String agentHostName) {
        /*
         * 根据 hostName 获取其对应 agent
         */
        AgentDO agentDO = agentManageService.getAgentByHostName(agentHostName);
        if (null == agentDO) {
            return new ArrayList<>();
        }
        /*
         * 获取 agent 关联的日志采集任务集
         */
        List<LogCollectTaskDO> logCollectTaskDOList = getLogCollectTaskListByAgentId(agentDO.getId());
        return logCollectTaskDOList;
    }

    @Override
    public List<LogCollectTaskDO> getLogCollectTaskListByKafkaClusterId(Long kafkaClusterId) {
        List<LogCollectTaskPO> logCollectTaskPOList = logCollectorTaskDAO.getLogCollectTaskListByKafkaClusterId(kafkaClusterId);
        return logCollectTaskPOList2LogCollectTaskDOListAndLoadRelationLogCollectPath(logCollectTaskPOList);
    }

    @Override
    public Long countAll() {
        return logCollectorTaskDAO.countAll();
    }

    @Override
    public List<Long> getAllIds() {
        return logCollectorTaskDAO.getAllIds();
    }

    /**
     * 将给定LogCollectTaskPO对象集转化为LogCollectTaskDO对象集，并在转化过程中加载各LogCollectTaskDO对象所关联的LogCollectPath对象集
     * 注：加载将会导致两次db查询
     *
     * @param logCollectTaskPOList 待转化LogCollectTaskPO对象集
     * @return 返回将给定LogCollectTaskPO对象集转化为的LogCollectTaskDO对象集，并在转化过程中加载各LogCollectTaskDO对象所关联的LogCollectPath对象集
     */
    private List<LogCollectTaskDO> logCollectTaskPOList2LogCollectTaskDOListAndLoadRelationLogCollectPath(List<LogCollectTaskPO> logCollectTaskPOList) {
        if (CollectionUtils.isEmpty(logCollectTaskPOList)) {
            return new ArrayList<>();
        }
        List<LogCollectTaskDO> logCollectTaskList = new ArrayList<>(logCollectTaskPOList.size());
        for (LogCollectTaskPO logCollectTaskPO : logCollectTaskPOList) {
            LogCollectTaskDO logCollectTaskDO = logCollectTaskManageServiceExtension.logCollectTaskPO2LogCollectTaskDO(logCollectTaskPO);
            //根据日志采集任务id获取其关联的日志采集任务路径对象集
            logCollectTaskDO.setDirectoryLogCollectPathList(directoryLogCollectPathManageService.getAllDirectoryLogCollectPathByLogCollectTaskId(logCollectTaskDO.getId()));
            logCollectTaskDO.setFileLogCollectPathList(fileLogCollectPathManageService.getAllFileLogCollectPathByLogCollectTaskId(logCollectTaskDO.getId()));
            logCollectTaskList.add(logCollectTaskDO);
        }
        return logCollectTaskList;
    }

}
