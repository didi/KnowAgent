package com.didichuxing.datachannel.agentmanager.core.agent.operation.task.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.operationtask.AgentOperationSubTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.operationtask.AgentOperationTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.operationtask.AgentOperationTaskPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.operationtask.AgentOperationTaskPO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentCollectTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentOperationTaskStatusEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.operation.task.AgentOperationSubTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.operation.task.AgentOperationTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.version.AgentVersionManageService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentOperationTaskMapper;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.RemoteOperationTaskService;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.AgentOperationTaskCreation;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.AgentOperationTaskLog;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskActionEnum;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskStateEnum;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskSubStateEnum;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskTypeEnum;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.n9e.RemoteN9eOperationTaskServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author huqidong
 * @date 2020-09-21
 *  Agent操作任务管理服务实现类
 */
@org.springframework.stereotype.Service
public class AgentOperationTaskManageServiceImpl implements AgentOperationTaskManageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentOperationTaskManageServiceImpl.class);

    @Autowired
    private RemoteOperationTaskService remoteOperationTaskService;

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private AgentVersionManageService agentVersionManageService;

    @Autowired
    private AgentOperationTaskMapper agentOperationTaskDAO;

    @Autowired
    private AgentOperationSubTaskManageService agentOperationSubTaskManageService;

    @Override
    @Transactional
    public Long createAgentOperationTask(AgentOperationTaskDO agentOperationTask, String operator) {
        /*
         * 基本参数校验
         */
        if(null == agentOperationTask) {
            throw new ServiceException(
                    "入参agentOperationTask对象不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        if(null == agentOperationTask.getTaskType()) {
            throw new ServiceException(
                    "入参agentOperationTask对象.taskType属性值不可为空",
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        /*
         * 设置 agentOperationTask.taskName 初始值，后续修改为prd给定值
         */
        agentOperationTask.setTaskName(UUID.randomUUID().toString());
        /*
         * 根据 agent 操作类型分别调用对应处理器进行处理
         */
        Long taskId;
        String operateTypeDesc = "";//操作类型，如：升级、卸载、安装
        String operateObject = "";//操作对象，如：批量/主机名
        if(agentOperationTask.getTaskType().equals(AgentOperationTaskTypeEnum.INSTALL.getCode())) {//安装
            taskId = handleCreateAgentInstallOperationTask(agentOperationTask, operator);
            operateTypeDesc = AgentOperationTaskTypeEnum.INSTALL.getMessage();
            if(agentOperationTask.getHostIdList().size() > 1) {
                operateObject = "批量";
            } else {
                operateObject = hostManageService.getById(agentOperationTask.getHostIdList().get(0)).getHostName();
            }
        } else if(agentOperationTask.getTaskType().equals(AgentOperationTaskTypeEnum.UPGRADE.getCode())) {//升级
            taskId = handleCreateAgentUpgradeOperationTask(agentOperationTask, operator);
            operateTypeDesc = AgentOperationTaskTypeEnum.UPGRADE.getMessage();
            if(agentOperationTask.getAgentIdList().size() > 1) {
                operateObject = "批量";
            } else {
                operateObject = agentManageService.getById(agentOperationTask.getAgentIdList().get(0)).getHostName();
            }
        } else if(agentOperationTask.getTaskType().equals(AgentOperationTaskTypeEnum.UNINSTALL.getCode())) {//卸载
            taskId = handleCreateAgentUninstallOperationTask(agentOperationTask, operator);
            operateTypeDesc = AgentOperationTaskTypeEnum.UNINSTALL.getMessage();
            if(agentOperationTask.getAgentIdList().size() > 1) {
                operateObject = "批量";
            } else {
                operateObject = agentManageService.getById(agentOperationTask.getAgentIdList().get(0)).getHostName();
            }
        } else {
            throw new ServiceException(
                    String.format("class=AgentOperationTaskManageServiceImpl||method=createAgentOperationTask||msg={未知的agent任务类型={%d}}", agentOperationTask.getTaskType()),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        String taskName = String.format("%s%sAgent任务", operateObject, operateTypeDesc);
        /*
         * 根据taskId获取对应任务名，并更新对应任务名
         */
        AgentOperationTaskPO agentOperationTaskPO = new AgentOperationTaskPO();
        agentOperationTaskPO.setId(taskId);
        agentOperationTaskPO.setTaskName(taskName);
        agentOperationTaskDAO.updateTaskNameByPrimaryKey(agentOperationTaskPO);
        return taskId;
    }

    /**
     * 处理agent升级任务
     * @param agentOperationTaskDO agent 升级任务对象
     * @param operator 操作人
     * @throws ServiceException 执行该操作过程中出现的异常
     */
    private Long handleCreateAgentUpgradeOperationTask(AgentOperationTaskDO agentOperationTaskDO, String operator) throws ServiceException {
        /*
         * 校验入参
         */
        CheckResult checkResult = checkUpgradeParameter(agentOperationTaskDO);
        if(!checkResult.getCheckResult()) {
            throw new ServiceException(checkResult.getMessage(), checkResult.getCode());
        }
        /*
         * 校验待升级agent是否存在
         */
        List<Long> agentIdList = agentOperationTaskDO.getAgentIdList();
        Set<String> hostNameSet = new HashSet<>();//之所以采用set，原因为：单host可能部署多agent
        List<String> hostIpList = new ArrayList<>(agentIdList.size());
        for (Long agentId : agentIdList) {
            AgentDO agentDO = agentManageService.getById(agentId);
            if(null == agentDO) {
                throw new ServiceException(String.format("Agent={id=%d}在系统中不存在", agentId), ErrorCodeEnum.AGENT_NOT_EXISTS.getCode());
            }
            if(StringUtils.isBlank(agentDO.getHostName())) {
                throw new ServiceException(
                        String.format("Agent对象={id={%d}}对应hostName属性值为空", agentId),
                        ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
                );
            } else {
                if(hostNameSet.add(agentDO.getHostName())) {
                    hostIpList.add(hostManageService.getHostByHostName(agentDO.getHostName()).getIp());
                }
            }
        }
        /*
         * 调用第三方接口进行agent升级任务下发
         */
        Long externalTaskId = commitRemoteAgentUpgradeTask(agentOperationTaskDO, hostIpList);
        /*
         * 调用第三方接口，执行该任务
         */
        if(!remoteOperationTaskService.actionTask(externalTaskId, AgentOperationTaskActionEnum.START)) {//执行失败
            throw new ServiceException(
                    String.format("第三方接口RemoteOperationTaskService.actionTask(externalTaskId={%d}, AgentOperationTaskActionEnum.START)调用失败", externalTaskId),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        /*
         * 保存agent安装任务信息至本地
         */
        agentOperationTaskDO.setExternalAgentTaskId(externalTaskId);
        agentOperationTaskDO.setOperator(CommonConstant.getOperator(operator));
        AgentOperationTaskPO agentOperationTaskPO = agentOperationTaskDO2AgentOperationTaskPOUpgrade(agentOperationTaskDO, hostNameSet.size());
        agentOperationTaskDAO.insert(agentOperationTaskPO);
        saveAgentOperationSubTask(hostNameSet, agentOperationTaskPO.getId(), AgentOperationTaskTypeEnum.UPGRADE, operator);
        return agentOperationTaskPO.getId();
    }

    /**
     * 根据agent操作任务对象提交请求至远程agent操作任务接口，并返回对应远程agent操作任务id
     * @param agentOperationTaskDO agent操作任务对象
     * @param hostNameList
     * @return 返回对应远程agent操作任务id
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long commitRemoteAgentUpgradeTask(AgentOperationTaskDO agentOperationTaskDO, List<String> hostNameList) throws ServiceException {
        AgentOperationTaskCreation agentOperationTaskCreation = new AgentOperationTaskCreation();
        agentOperationTaskCreation.setTaskType(agentOperationTaskDO.getTaskType());
        agentOperationTaskCreation.setTaskName(agentOperationTaskDO.getTaskName());
        agentOperationTaskCreation.setHostList(hostNameList);
        //set about agent version
        Long agentVerisonId = agentOperationTaskDO.getTargetAgentVersionId();
        AgentVersionDO agentVersionDO = agentVersionManageService.getById(agentVerisonId);
        if(null == agentVersionDO) {
            throw new ServiceException(
                    String.format("待安装Agent的AgentVersion={id=%d}在系统中不存在", agentVerisonId),
                    ErrorCodeEnum.AGENT_VERSION_NOT_EXISTS.getCode()
            );
        }
        String downloadUrl = agentVersionManageService.getAgentInstallFileDownloadUrl(agentVerisonId);
        agentOperationTaskCreation.setAgentPackageDownloadUrl(downloadUrl);
        agentOperationTaskCreation.setAgentPackageMd5(agentVersionDO.getFileMd5());
        agentOperationTaskCreation.setAgentPackageName(agentVersionDO.getFileName());
        Result<Long> externalTaskIdResult = remoteOperationTaskService.createTask(agentOperationTaskCreation);
        if(externalTaskIdResult.failed()) {
            throw new ServiceException(externalTaskIdResult.getMessage(), externalTaskIdResult.getCode());
        }
        Long externalTaskId = externalTaskIdResult.getData();
        return externalTaskId;
    }

    /**
     * 根据agent操作任务对象提交请求至远程agent操作任务接口，并返回对应远程agent操作任务id
     * @param agentOperationTaskDO agent操作任务对象
     * @return 返回对应远程agent操作任务id
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long commitRemoteAgentUninstallTask(AgentOperationTaskDO agentOperationTaskDO, List<String> hostNameList) throws ServiceException {
        AgentOperationTaskCreation agentOperationTaskCreation = new AgentOperationTaskCreation();
        agentOperationTaskCreation.setTaskType(agentOperationTaskDO.getTaskType());
        agentOperationTaskCreation.setTaskName(agentOperationTaskDO.getTaskName());
        agentOperationTaskCreation.setHostList(hostNameList);
        Result<Long> externalTaskIdResult = remoteOperationTaskService.createTask(agentOperationTaskCreation);
        if(externalTaskIdResult.failed()) {
            throw new ServiceException(externalTaskIdResult.getMessage(), externalTaskIdResult.getCode());
        }
        Long externalTaskId = externalTaskIdResult.getData();
        return externalTaskId;
    }

    /**
     * 将AgentOperationTaskDO对象转化为agent升级case对应AgentOperationTaskPO对象
     * @param agentOperationTaskDO 待转化AgentOperationTaskDO对象
     * @return 返回将AgentOperationTaskDO对象转化为的agent升级case对应AgentOperationTaskPO对象
     */
    private AgentOperationTaskPO agentOperationTaskDO2AgentOperationTaskPOUpgrade(AgentOperationTaskDO agentOperationTaskDO, Integer hostsNumber) {
        AgentOperationTaskPO agentOperationTaskPO = new AgentOperationTaskPO();
        //安装任务创建后默认开始执行
        agentOperationTaskPO.setTaskStatus(AgentOperationTaskStatusEnum.RUNNING.getCode());
        agentOperationTaskPO.setTaskType(agentOperationTaskDO.getTaskType());
        agentOperationTaskPO.setTaskName(agentOperationTaskDO.getTaskName());
        agentOperationTaskPO.setExternalAgentTaskId(agentOperationTaskDO.getExternalAgentTaskId());
        agentOperationTaskPO.setHostsNumber(hostsNumber);
        agentOperationTaskPO.setOperator(agentOperationTaskDO.getOperator());
        agentOperationTaskPO.setTaskStartTime(new Date());
        agentOperationTaskPO.setTargetAgentVersionId(agentOperationTaskDO.getTargetAgentVersionId());
        return agentOperationTaskPO;
    }

    /**
     * 将AgentOperationTaskDO对象转化为agent卸载case对应AgentOperationTaskPO对象
     * @param agentOperationTaskDO 待转化AgentOperationTaskDO对象
     * @return 返回将AgentOperationTaskDO对象转化为的agent卸载case对应AgentOperationTaskPO对象
     */
    private AgentOperationTaskPO agentOperationTaskDO2AgentOperationTaskPOUninstall(AgentOperationTaskDO agentOperationTaskDO, Integer hostsNumber) {
        AgentOperationTaskPO agentOperationTaskPO = new AgentOperationTaskPO();
        //安装任务创建后默认开始执行
        agentOperationTaskPO.setTaskStatus(AgentOperationTaskStatusEnum.RUNNING.getCode());
        agentOperationTaskPO.setTaskType(agentOperationTaskDO.getTaskType());
        agentOperationTaskPO.setTaskName(agentOperationTaskDO.getTaskName());
        agentOperationTaskPO.setExternalAgentTaskId(agentOperationTaskDO.getExternalAgentTaskId());
        agentOperationTaskPO.setHostsNumber(hostsNumber);
        agentOperationTaskPO.setOperator(agentOperationTaskDO.getOperator());
        agentOperationTaskPO.setTaskStartTime(new Date());
        return agentOperationTaskPO;
    }

    /**
     * 校验agent升级场景参数是否正确
     * @return 检查结果
     */
    private CheckResult checkUpgradeParameter(AgentOperationTaskDO agentOperationTaskDO) {
        if(CollectionUtils.isEmpty(agentOperationTaskDO.getAgentIdList())) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    "入参agentIdList不可为空"
            );
        }
        if(null == agentOperationTaskDO.getTargetAgentVersionId() || agentOperationTaskDO.getTargetAgentVersionId() <= 0) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    "入参targetAgentVersionId不可为空，且须大于等于0"
            );
        }
        return new CheckResult(true);
    }

    /**
     * 校验agent卸载场景参数是否正确
     * @return 检查结果
     */
    private CheckResult checkUninstallParameter(AgentOperationTaskDO agentOperationTaskDO) {
        if(CollectionUtils.isEmpty(agentOperationTaskDO.getAgentIdList())) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    "入参agentIdList不可为空"
            );
        }
        return new CheckResult(true);
    }

    /**
     * 处理Agent卸载任务
     * @param agentOperationTaskDO 封装 agent 卸载任务的 AgentOperationTaskDO 对象
     * @param operator 操作人
     * @return 卸载任务id
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long handleCreateAgentUninstallOperationTask(AgentOperationTaskDO agentOperationTaskDO, String operator) throws ServiceException {
        /*
         * 校验入参
         */
        CheckResult checkResult = checkUninstallParameter(agentOperationTaskDO);
        if(!checkResult.getCheckResult()) {
            throw new ServiceException(checkResult.getMessage(), checkResult.getCode());
        }
        /*
         * 校验待卸载agent是否存在
         */
        List<Long> agentIdList = agentOperationTaskDO.getAgentIdList();
        Set<String> hostNameSet = new HashSet<>();//之所以采用set，原因为：单host可能部署多agent
        List<String> hostIpList = new ArrayList<>(agentIdList.size());
        for (Long agentId : agentIdList) {
            AgentDO agentDO = agentManageService.getById(agentId);
            if(null == agentDO) {
                throw new ServiceException(String.format("Agent={id=%d}在系统中不存在", agentId), ErrorCodeEnum.HOST_NOT_EXISTS.getCode());
            }
            if(StringUtils.isBlank(agentDO.getHostName())) {
                throw new ServiceException(
                        String.format("Agent对象={id={%d}}对应hostName属性值为空", agentId),
                        ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
                );
            } else {
                if(hostNameSet.add(agentDO.getHostName())) {
                    hostIpList.add(hostManageService.getHostByHostName(agentDO.getHostName()).getIp());
                }
            }
        }
        /*
         * 调用第三方接口进行agent升级任务下发
         */
        Long externalTaskId = commitRemoteAgentUninstallTask(agentOperationTaskDO, hostIpList);
        /*
         * 调用第三方接口，执行该任务
         */
        if(!remoteOperationTaskService.actionTask(externalTaskId, AgentOperationTaskActionEnum.START)) {//执行失败
            throw new ServiceException(
                    String.format("第三方接口RemoteOperationTaskService.actionTask(externalTaskId={%d}, AgentOperationTaskActionEnum.START)调用失败", externalTaskId),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        /*
         * 保存agent安装任务信息至本地
         */
        agentOperationTaskDO.setExternalAgentTaskId(externalTaskId);
        agentOperationTaskDO.setOperator(CommonConstant.getOperator(operator));
        AgentOperationTaskPO agentOperationTaskPO = agentOperationTaskDO2AgentOperationTaskPOUninstall(agentOperationTaskDO, hostNameSet.size());
        agentOperationTaskDAO.insert(agentOperationTaskPO);
        saveAgentOperationSubTask(hostNameSet, agentOperationTaskPO.getId(), AgentOperationTaskTypeEnum.UNINSTALL, operator);
        return agentOperationTaskPO.getId();
    }

    /**
     * 处理agent安装任务
     * @param agentOperationTaskDO agent 安装任务对象
     * @param operator 操作人
     * @throws ServiceException 执行该操作过程中出现的异常
     */
    private Long handleCreateAgentInstallOperationTask(AgentOperationTaskDO agentOperationTaskDO, String operator) throws ServiceException {
        /*
         * 校验入参
         */
        CheckResult checkResult = checkInstallParameter(agentOperationTaskDO);
        if(!checkResult.getCheckResult()) {
            throw new ServiceException(checkResult.getMessage(), checkResult.getCode());
        }
        /*
         * 校验待安装agent主机是否存在，主机是否已存在agent
         */
        List<Long> hostIdList = agentOperationTaskDO.getHostIdList();
        List<String> hostNameList = new ArrayList<>(hostIdList.size());
        List<String> hostIpList = new ArrayList<>(hostIdList.size());
        for (Long hostId : hostIdList) {
            HostDO hostDO = hostManageService.getById(hostId);
            if(null == hostDO) {
                throw new ServiceException(String.format("待安装Agent的主机={id=%d}在系统中不存在", hostId), ErrorCodeEnum.HOST_NOT_EXISTS.getCode());
            }
            AgentDO agentDO = agentManageService.getAgentByHostName(hostDO.getHostName());
            if(null != agentDO) {
                throw new ServiceException(String.format("待安装Agent的主机={id=%d}上已存在Agent={id=%d}", hostId, agentDO.getId()), ErrorCodeEnum.AGENT_EXISTS_IN_HOST_WHEN_AGENT_INSTALL.getCode());
            }
            if(StringUtils.isBlank(hostDO.getHostName())) {
                throw new ServiceException(
                        String.format("Host对象={id={%d}}对应hostName属性值为空", hostId),
                        ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
                );
            } else {
                hostNameList.add(hostDO.getHostName());
                hostIpList.add(hostDO.getIp());
            }
        }
        /*
         * 调用第三方接口进行agent安装任务下发
         */
        Long externalTaskId = commitRemoteAgentInstallTask(agentOperationTaskDO, hostIpList);
        /*
         * 调用第三方接口，执行该任务
         * TODO：remoteOperationTaskService 名字
         */
        if(!remoteOperationTaskService.actionTask(externalTaskId, AgentOperationTaskActionEnum.START)) {//执行失败
            throw new ServiceException(
                    String.format("第三方接口RemoteOperationTaskService.actionTask(externalTaskId={%d}, AgentOperationTaskActionEnum.START)调用失败", externalTaskId),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        /*
         * 保存agent安装任务信息至本地
         */
        agentOperationTaskDO.setExternalAgentTaskId(externalTaskId);
        agentOperationTaskDO.setOperator(CommonConstant.getOperator(operator));
        AgentOperationTaskPO agentOperationTaskPO = agentOperationTaskDO2AgentOperationTaskPOInstall(agentOperationTaskDO);
        agentOperationTaskDAO.insert(agentOperationTaskPO);
        saveAgentOperationSubTask(hostNameList, agentOperationTaskPO.getId(), AgentOperationTaskTypeEnum.INSTALL, operator);
        return agentOperationTaskPO.getId();
    }

    /**
     * 保存粒度至主机的agent操作记录
     * @param hostNameList 待操作主机名列表
     * @param agentOperationTaskId AgentOperationTask 对象 id
     * @param agentOperationTaskTypeEnum AgentOperationTask 类型
     * @param operator 操作人
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void saveAgentOperationSubTask(Collection<String> hostNameList, Long agentOperationTaskId, AgentOperationTaskTypeEnum agentOperationTaskTypeEnum, String operator) throws ServiceException {
        for (String hostName : hostNameList) {
            AgentOperationSubTaskDO agentOperationSubTaskDO = new AgentOperationSubTaskDO();
            HostDO hostDO = hostManageService.getHostByHostName(hostName);
            if(null == hostDO) {
                throw new ServiceException(
                        String.format("Host={hostName=%s}在系统中不存在", hostName),
                        ErrorCodeEnum.HOST_NOT_EXISTS.getCode()
                );
            }
            agentOperationSubTaskDO.setAgentOperationTaskId(agentOperationTaskId);
            agentOperationSubTaskDO.setContainer(hostDO.getContainer());
            agentOperationSubTaskDO.setHostName(hostName);
            agentOperationSubTaskDO.setIp(hostDO.getIp());
            agentOperationSubTaskDO.setTaskStartTime(new Date());
            if(!agentOperationTaskTypeEnum.getCode().equals(AgentOperationTaskTypeEnum.INSTALL.getCode())) {//非 "安装" agent 操作 类型
                AgentDO agentDO = agentManageService.getAgentByHostName(hostName);
                if(null == agentDO) {
                    throw new ServiceException(
                            String.format("Agent={hostName=%s}在系统中不存在", hostName),
                            ErrorCodeEnum.AGENT_NOT_EXISTS.getCode()
                    );
                }
                agentOperationSubTaskDO.setSourceAgentVersionId(agentDO.getAgentVersionId());
            }
            agentOperationSubTaskManageService.createAgentOperationSubTask(agentOperationSubTaskDO, CommonConstant.getOperator(operator));
        }
    }

    /**
     * 将AgentOperationTaskDO对象转化为agent安装case对应AgentOperationTaskPO对象
     * @param agentOperationTaskDO 待转化AgentOperationTaskDO对象
     * @return 返回将AgentOperationTaskDO对象转化为的agent安装case对应AgentOperationTaskPO对象
     */
    private AgentOperationTaskPO agentOperationTaskDO2AgentOperationTaskPOInstall(AgentOperationTaskDO agentOperationTaskDO) {
        AgentOperationTaskPO agentOperationTaskPO = new AgentOperationTaskPO();
        //安装任务创建后默认开始执行
        agentOperationTaskPO.setTaskStatus(AgentOperationTaskStatusEnum.RUNNING.getCode());
        agentOperationTaskPO.setTaskType(agentOperationTaskDO.getTaskType());
        agentOperationTaskPO.setTaskName(agentOperationTaskDO.getTaskName());
        agentOperationTaskPO.setExternalAgentTaskId(agentOperationTaskDO.getExternalAgentTaskId());
        agentOperationTaskPO.setHostsNumber(agentOperationTaskDO.getHostIdList().size());
        agentOperationTaskPO.setTargetAgentVersionId(agentOperationTaskDO.getTargetAgentVersionId());
        agentOperationTaskPO.setOperator(agentOperationTaskDO.getOperator());
        agentOperationTaskPO.setTaskStartTime(new Date());
        return agentOperationTaskPO;
    }

    /**
     * 根据agent操作任务对象提交请求至远程agent操作任务接口，并返回对应远程agent操作任务id
     * @param agentOperationTaskDO agent操作任务对象
     * @param hostNameList
     * @return 返回对应远程agent操作任务id
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long commitRemoteAgentInstallTask(AgentOperationTaskDO agentOperationTaskDO, List<String> hostNameList) throws ServiceException {
        AgentOperationTaskCreation agentOperationTaskCreation = new AgentOperationTaskCreation();
        agentOperationTaskCreation.setTaskType(agentOperationTaskDO.getTaskType());
        agentOperationTaskCreation.setTaskName(agentOperationTaskDO.getTaskName());
        agentOperationTaskCreation.setHostList(hostNameList);
        //set about agent version
        Long agentVerisonId = agentOperationTaskDO.getTargetAgentVersionId();
        AgentVersionDO agentVersionDO = agentVersionManageService.getById(agentVerisonId);
        if(null == agentVersionDO) {
            throw new ServiceException(
                    String.format("待安装Agent的AgentVersion={id=%d}在系统中不存在", agentVerisonId),
                    ErrorCodeEnum.AGENT_VERSION_NOT_EXISTS.getCode()
            );
        }
        String downloadUrl = agentVersionManageService.getAgentInstallFileDownloadUrl(agentVerisonId);
        agentOperationTaskCreation.setAgentPackageDownloadUrl(downloadUrl);
        agentOperationTaskCreation.setAgentPackageMd5(agentVersionDO.getFileMd5());
        agentOperationTaskCreation.setAgentPackageName(agentVersionDO.getFileName());
        Result<Long> externalTaskIdResult = remoteOperationTaskService.createTask(agentOperationTaskCreation);
        if(externalTaskIdResult.failed()) {
            throw new ServiceException(externalTaskIdResult.getMessage(), externalTaskIdResult.getCode());
        }
        Long externalTaskId = externalTaskIdResult.getData();
        return externalTaskId;
    }

    /**
     * 校验agent安装场景参数是否正确
     * @return 检查结果
     */
    private CheckResult checkInstallParameter(AgentOperationTaskDO agentOperationTaskDO) {
        if(CollectionUtils.isEmpty(agentOperationTaskDO.getHostIdList())) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    "入参hostIdList不可为空"
            );
        }
        if(null == agentOperationTaskDO.getTargetAgentVersionId() || agentOperationTaskDO.getTargetAgentVersionId() <= 0) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    "入参targetAgentVersionId不可为空，且须大于等于0"
            );
        }
        return new CheckResult(true);
    }

    @Override
    public AgentOperationTaskDO getById(Long taskId) {
        AgentOperationTaskPO agentOperationTaskPO = null;
        agentOperationTaskPO = agentOperationTaskDAO.selectByPrimaryKey(taskId);
        if(null == agentOperationTaskPO) {
            return null;
        } else {
            return ConvertUtil.obj2Obj(agentOperationTaskPO, AgentOperationTaskDO.class);
        }
    }

    @Override
    public String getTaskLog(Long taskId, String hostname) {
        AgentOperationTaskDO agentOperationTaskDO = getById(taskId);
        if(null == agentOperationTaskDO) {
            throw new ServiceException(String.format("AgentOperationTask={id=%d}在系统中不存在", taskId), ErrorCodeEnum.AGENT_OPERATION_TASK_NOT_EXISTS.getCode());
        }
        Result<AgentOperationTaskLog> agentOperationTaskLogResult = remoteOperationTaskService.getTaskLog(agentOperationTaskDO.getExternalAgentTaskId(), hostname);
        if(agentOperationTaskLogResult.failed()) {
            throw new ServiceException(agentOperationTaskLogResult.getMessage(), agentOperationTaskLogResult.getCode());
        }
        AgentOperationTaskLog agentOperationTaskLog = agentOperationTaskLogResult.getData();
        return agentOperationTaskLog.getStdout();
    }

    @Override
    public List<AgentOperationTaskDO> paginationQueryByConditon(AgentOperationTaskPaginationQueryConditionDO agentOperationTaskPaginationQueryConditionDO) {
        String column = agentOperationTaskPaginationQueryConditionDO.getSortColumn();
        if (column != null) {
            for (char c : column.toCharArray()) {
                if (!Character.isLetter(c) && c != '_') {
                    return Collections.emptyList();
                }
            }
        }
        List<AgentOperationTaskPO> agentOperationTaskPOList = agentOperationTaskDAO.paginationQueryByConditon(agentOperationTaskPaginationQueryConditionDO);
        if(CollectionUtils.isEmpty(agentOperationTaskPOList)) {
            return new ArrayList<>();
        }
        return ConvertUtil.list2List(agentOperationTaskPOList, AgentOperationTaskDO.class);
    }

    @Override
    public Integer queryCountByCondition(AgentOperationTaskPaginationQueryConditionDO agentOperationTaskPaginationQueryConditionDO) {
        return agentOperationTaskDAO.queryCountByConditon(agentOperationTaskPaginationQueryConditionDO);
    }

    @Override
    public Map<String, AgentOperationTaskSubStateEnum> getTaskResultByExternalTaskId(Long externalTaskId) {
        Result<Map<String, AgentOperationTaskSubStateEnum>> result = remoteOperationTaskService.getTaskResult(externalTaskId);
        if(result.failed()) {
            throw new ServiceException(result.getMessage(), result.getCode());
        } else {
            return result.getData();
        }
    }

    @Override
    public void updateAgentOperationTasks() {

        /*
         * 获取系统中所有处于非状态"FINISHED" agent operation task list
         */
        List<AgentOperationTaskPO> agentOperationTaskPOList = agentOperationTaskDAO.selectByTaskStatus(AgentOperationTaskStateEnum.RUNNING.getCode());
        agentOperationTaskPOList.addAll(agentOperationTaskDAO.selectByTaskStatus(AgentOperationTaskStateEnum.BLOCKED.getCode()));

        /*
         * 调用第三方远程接口获取对应 agent operation task 状态，如 agent operation task 状态为"执行完"，根据任务类型 添加、删除、更新对应 agent 记录
         * 无论 agent operation task 是否已执行完，更新其关联各主机对应 sub agent operation task 状态，并将状态更新至表 tb_agent_operation_task、tb_agent_operation_sub_task 对应记录
         */
        for (AgentOperationTaskPO agentOperationTaskPO : agentOperationTaskPOList) {
            Result<AgentOperationTaskStateEnum> agentOperationTaskStateEnumResult = remoteOperationTaskService.getTaskExecuteState(agentOperationTaskPO.getExternalAgentTaskId());
            if(agentOperationTaskStateEnumResult.failed()) {
                throw new ServiceException(agentOperationTaskStateEnumResult.getMessage(), agentOperationTaskStateEnumResult.getCode());
            } else {
                Result<Map<String, AgentOperationTaskSubStateEnum>> hostName2AgentOperationTaskSubStateEnumMapResult = remoteOperationTaskService.getTaskResult(agentOperationTaskPO.getExternalAgentTaskId());
                AgentOperationTaskStateEnum agentOperationTaskStateEnum = agentOperationTaskStateEnumResult.getData();
                if(hostName2AgentOperationTaskSubStateEnumMapResult.failed()) {
                    throw new ServiceException(hostName2AgentOperationTaskSubStateEnumMapResult.getMessage(), hostName2AgentOperationTaskSubStateEnumMapResult.getCode());
                } else {
                    Map<String, AgentOperationTaskSubStateEnum> hostName2AgentOperationTaskSubStateEnumMap = hostName2AgentOperationTaskSubStateEnumMapResult.getData();
                    //TODO：电科院场景 hostName2AgentOperationTaskSubStateEnumMap.key 为 ip
                    Map<String, AgentOperationSubTaskDO> hostName2AgentOperationSubTaskDOMapInLocal = agentOperationSubTaskDOList2Map(agentOperationSubTaskManageService.getByAgentOperationTaskId(agentOperationTaskPO.getId()));
                    for(Map.Entry<String, AgentOperationTaskSubStateEnum> entry : hostName2AgentOperationTaskSubStateEnumMap.entrySet()) {
                        String hostName = entry.getKey();//主机名
                        AgentOperationTaskSubStateEnum agentOperationTaskSubStateEnum = entry.getValue();//主机对应 agent 操作任务 sub state
                        AgentOperationSubTaskDO agentOperationSubTaskDOInLocal = hostName2AgentOperationSubTaskDOMapInLocal.get(hostName);
                        if(null == agentOperationSubTaskDOInLocal) {
                            LOGGER.error(
                                    String.format("待同步子任务状态的AgentOperationSubTaskDO[hostName=%s]在系统中不存在，跳过", hostName)
                            );
                            continue;
                        }
                        if(AgentOperationTaskStateEnum.FINISHED.getCode().equals(agentOperationTaskStateEnum.getCode())) {//任务已执行完
                            //根据任务类型 添加、删除、更新对应 agent 记录
                            //TODO：电科院场景 hostName 为 ip
                            HostDO hostDO = hostManageService.getHostByIp(hostName);
                            if(null == hostDO) {
                                throw new ServiceException(
                                        String.format("Host={ip=%s}在系统中不存在", hostName),
                                        ErrorCodeEnum.HOST_NOT_EXISTS.getCode()
                                );
                            } else {
                                try {
                                    actionAgent(agentOperationTaskPO, hostDO.getHostName());
                                } catch (ServiceException ex) {
                                    if(!ex.getServiceExceptionCode().equals(ErrorCodeEnum.AGENT_EXISTS_IN_HOST_WHEN_AGENT_CREATE.getCode())) {
                                        throw ex;
                                    }
                                }
                            }
                        }
                        if(agentOperationTaskSubStateEnum.finish()) {//执行完 TODO：
                            //更新 sub agent operation task 对应 task_end_time 字段值为当前时间
                            agentOperationSubTaskDOInLocal.setTaskEndTime(new Date());
                        }
                        //更新 agentOperationTask 关联各主机对应 sub agent operation task 状态，并将状态更新至表 tb_agent_operation_sub_task 对应记录
                        agentOperationSubTaskDOInLocal.setExecuteStatus(agentOperationTaskSubStateEnum.getCode());
                        agentOperationSubTaskManageService.updateAgentOperationSubTask(agentOperationSubTaskDOInLocal);
                    }
                    //更新 agent operation task 状态，如状态为 "FINISHED"，将当前时间更新为表 tb_agent_operation_task 对应记录字段 task_end_time 值
                    if((AgentOperationTaskStateEnum.FINISHED.getCode().equals(agentOperationTaskStateEnum.getCode()))) {
                        agentOperationTaskPO.setTaskEndTime(new Date());
                    }
                    agentOperationTaskPO.setTaskStatus(agentOperationTaskStateEnum.getCode());
                    agentOperationTaskDAO.updateByPrimaryKey(agentOperationTaskPO);
                }
            }
        }

    }

    @Override
    public boolean unfinishedAgentOperationTaskExistsByAgentVersionId(Long agentVersionId) {
        List<AgentOperationTaskPO> agentOperationTaskPOList = agentOperationTaskDAO.selectByAgentVersionId(agentVersionId);
        if(CollectionUtils.isNotEmpty(agentOperationTaskPOList)) {
            for (AgentOperationTaskPO agentOperationTaskPO : agentOperationTaskPOList) {
                if(!AgentOperationTaskStatusEnum.isFinished(agentOperationTaskPO.getTaskStatus())) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * 将给定AgentOperationSubTaskDO对象集转化为 hostName : AgentOperationSubTaskDO 对象集
     * @param agentOperationSubTaskDOList AgentOperationSubTaskDO 对象集
     * @return 返回将给定AgentOperationSubTaskDO对象集转化为的 hostName : AgentOperationSubTaskDO 对象集
     */
    private Map<String, AgentOperationSubTaskDO> agentOperationSubTaskDOList2Map(List<AgentOperationSubTaskDO> agentOperationSubTaskDOList) {
        Map<String, AgentOperationSubTaskDO> hostName2AgentOperationSubTaskDOMapInLocal = new HashMap<>();
        for (AgentOperationSubTaskDO agentOperationSubTaskDO : agentOperationSubTaskDOList) {
            //TODO：电科院场景 hostName 2 ip
            HostDO hostDO = hostManageService.getHostByHostName(agentOperationSubTaskDO.getHostName());
            if(null != hostDO) {
                hostName2AgentOperationSubTaskDOMapInLocal.put(hostDO.getIp(), agentOperationSubTaskDO);
            }
        }
        return hostName2AgentOperationSubTaskDOMapInLocal;
    }

    /**
     * 根据任务类型 添加、删除、更新对应 agent 记录
     * @param agentOperationTaskPO AgentOperationTaskPO 对象
     * @param hostName 主机名
     */
    private void actionAgent(AgentOperationTaskPO agentOperationTaskPO, String hostName) {
        if(AgentOperationTaskTypeEnum.INSTALL.getCode().equals(agentOperationTaskPO.getTaskType())) {
            /*
             * 添加对应Agent记录
             */
            HostDO hostDO = hostManageService.getHostByHostName(hostName);
            if(null == hostDO) {
                throw new ServiceException(
                        String.format("Host={hostName=%s}在系统中不存在", hostName),
                        ErrorCodeEnum.HOST_NOT_EXISTS.getCode()
                );
            }
            AgentDO agentDO = new AgentDO();
            agentDO.setHostName(hostDO.getHostName());
            agentDO.setIp(hostDO.getIp());
            agentDO.setCollectType(AgentCollectTypeEnum.COLLECT_HOST_AND_CONTAINERS.getCode());
            agentDO.setAgentVersionId(agentOperationTaskPO.getTargetAgentVersionId());
            agentManageService.createAgent(agentDO, null);
        } else if(AgentOperationTaskTypeEnum.UNINSTALL.getCode().equals(agentOperationTaskPO.getTaskType())) {
            /*
             * 删除对应Agent记录
             */
            agentManageService.deleteAgentByHostName(hostName, false, false, null);
        } else if(AgentOperationTaskTypeEnum.UPGRADE.getCode().equals(agentOperationTaskPO.getTaskType())) {
            /*
             * 更新对应Agent记录对应 agent version id
             */
            AgentDO agentDO = agentManageService.getAgentByHostName(hostName);
            if(null == agentDO) {
                throw new ServiceException(
                        String.format("Agent={hostName=%s}在系统中不存在", hostName),
                        ErrorCodeEnum.AGENT_NOT_EXISTS.getCode()
                );
            }
            agentDO.setAgentVersionId(agentOperationTaskPO.getTargetAgentVersionId());
            agentManageService.updateAgent(agentDO, null);
        } else {
            throw new ServiceException(
                    String.format("AgentOperationTaskPO对象={%s}的taskType属性值={%d}非法，合法属性值见枚举类：AgentOperationTaskTypeEnum", JSON.toJSONString(agentOperationTaskPO), agentOperationTaskPO.getTaskType()),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
    }

}
