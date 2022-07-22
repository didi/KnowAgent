package com.didichuxing.datachannel.agentmanager.core.agent.manage.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.AgentPO;
import com.didichuxing.datachannel.agentmanager.common.constant.AgentConstant;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.host.HostTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.ModuleEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.OperationEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.core.agent.health.AgentHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.common.OperateRecordService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentMapper;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.manage.extension.AgentManageServiceExtension;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * Agent管理服务实现类
 */
@org.springframework.stereotype.Service
public class AgentManageServiceImpl implements AgentManageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentManageServiceImpl.class);

    @Autowired
    private AgentMapper agentDAO;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private AgentManageServiceExtension agentManageServiceExtension;

    @Autowired
    private OperateRecordService operateRecordService;

    @Autowired
    private AgentHealthManageService agentHealthManageService;

    @Autowired
    private KafkaClusterManageService kafkaClusterManageService;

    @Override
    @Transactional
    public Long createAgent(AgentDO agent, String operator, Boolean createHostWhenHostNotExists) {
        return this.handleCreateAgent(agent, operator, createHostWhenHostNotExists);
    }

    /**
     * 创建Agent对象处理流程
     *
     * @param agentDO  待创建Agent对象
     * @param operator 操作人
     * @param createHostWhenHostNotExists agent宿主机不存在时，是否创建对应宿主机信息，true：创建 false：不创建，报异常
     * @return 创建成功的Agent对象id
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long handleCreateAgent(AgentDO agentDO, String operator, Boolean createHostWhenHostNotExists) throws ServiceException {
        /*
         * 校验待创建 AgentPO 对象参数信息是否合法
         */
        CheckResult checkResult = agentManageServiceExtension.checkCreateParameterAgent(agentDO);
        if (!checkResult.getCheckResult()) {//agent对象信息不合法
            throw new ServiceException(checkResult.getMessage(), checkResult.getCode());
        }
        /*
         * 校验待创建Agent在系统中是否已存在（已存在判断条件为：系统中是否已存在主机名为待创建Agent对应主机名的Agent）？如不存在，表示合法，如已存在，表示非法
         */
        boolean agentExists = agentExists(agentDO.getHostName());
        if (agentExists) {
            throw new ServiceException(
                    String.format("Agent对象{%s}创建失败，原因为：系统中已存在主机名与待创建Agent主机名{%s}相同的Agent", JSON.toJSONString(agentDO), agentDO.getHostName()),
                    ErrorCodeEnum.AGENT_EXISTS_IN_HOST_WHEN_AGENT_CREATE.getCode()
            );
        }
        /*
         * 校验主机名为待添加Agent对象对应主机名的主机是否存在？如存在，表示合法，如不存在，表示非法，不能在一个在系统中不存在的主机安装Agent
         */
        boolean hostExists = hostExists(agentDO.getHostName());
        if (!hostExists) {
            if(createHostWhenHostNotExists) {
                hostManageService.createHost(convert2HostDO(agentDO), operator);
            } else {
                throw new ServiceException(
                        String.format("Agent对象{%s}创建失败，原因为：不能基于一个系统中不存在的主机创建Agent对象，系统中已不存在主机名为待创建Agent对应主机名{%s}的主机对象", JSON.toJSONString(agentDO), agentDO.getHostName()),
                        ErrorCodeEnum.HOST_NOT_EXISTS.getCode()
                );
            }
        }
        /*
         * 系统是否存在 agent errorlogs & metrics 流 对应 全局 接收端 配置，如存在 & 用户未设置待添加 agent 对应 errorlogs & metrcis 流 对应 topic，则 设置
         */
        if(StringUtils.isBlank(agentDO.getErrorLogsSendTopic())) {
            ReceiverDO receiverDO = kafkaClusterManageService.getAgentErrorLogsTopicExistsReceiver();
            if(null != receiverDO) {
                agentDO.setErrorLogsSendReceiverId(receiverDO.getId());
                agentDO.setErrorLogsSendTopic(receiverDO.getAgentErrorLogsTopic());
                agentDO.setErrorLogsProducerConfiguration(receiverDO.getKafkaClusterProducerInitConfiguration());
            }
        }
        if(StringUtils.isBlank(agentDO.getMetricsSendTopic())) {
            ReceiverDO receiverDO = kafkaClusterManageService.getAgentMetricsTopicExistsReceiver();
            if(null != receiverDO) {
                agentDO.setMetricsSendReceiverId(receiverDO.getId());
                agentDO.setMetricsSendTopic(receiverDO.getAgentMetricsTopic());
                agentDO.setMetricsProducerConfiguration(receiverDO.getKafkaClusterProducerInitConfiguration());
            }
        }

        /*
         * 持久化待创建Agent对象
         */
        Long savedAgentId = null;
        agentDO.setOperator(CommonConstant.getOperator(operator));
        agentDO.setConfigurationVersion(AgentConstant.AGENT_CONFIGURATION_VERSION_INIT);
        AgentPO agentPO = ConvertUtil.obj2Obj(agentDO, AgentPO.class);
        agentDAO.insertSelective(agentPO);
        savedAgentId = agentPO.getId();
        /*
         * 初始化 & 持久化Agent关联Agent健康度信息
         */
        agentHealthManageService.createInitialAgentHealth(savedAgentId, operator);
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.AGENT,
                OperationEnum.ADD,
                savedAgentId,
                String.format("创建Agent={%s}，创建成功的Agent对象id={%d}", JSON.toJSONString(agentDO), savedAgentId),
                operator
        );
        return savedAgentId;
    }

    private HostDO convert2HostDO(AgentDO agentDO) {
        HostDO hostDO = new HostDO();
        hostDO.setHostName(agentDO.getHostName());
        hostDO.setIp(agentDO.getIp());
        hostDO.setContainer(HostTypeEnum.HOST.getCode());
        hostDO.setParentHostName(StringUtils.EMPTY);
        hostDO.setMachineZone(StringUtils.EMPTY);
        hostDO.setDepartment(StringUtils.EMPTY);
        return hostDO;
    }

    /**
     * 校验系统中是否已存在给定主机名的主机对象
     *
     * @param hostName 主机名
     * @return true：存在 false：不存在
     * @throws ServiceException 执行 "校验系统中是否已存在给定主机名的主机对象" 过程中出现的异常
     */
    private boolean hostExists(String hostName) throws ServiceException {
        HostDO hostDO = hostManageService.getHostByHostName(hostName);
        return null != hostDO;
    }

    /**
     * 校验系统中是否已存在给定主机名的Agent对象
     *
     * @param hostName 主机名
     * @return true：存在 false：不存在
     * @throws ServiceException 执行 "校验系统中是否已存在给定主机名的Agent对象" 过程中出现的异常
     */
    private boolean agentExists(String hostName) throws ServiceException {
        try {
            AgentPO agentPO = agentDAO.selectByHostName(hostName);
            if (null == agentPO) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format(
                            "class=AgentManageServiceImpl||method=agentExists||msg={%s}",
                            String.format("调用接口[AgentMapper.selectByHostName(hostName={%s})失败]", hostName)
                    ),
                    ex,
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
    }

    @Override
    @Transactional
    public void deleteAgentByHostName(String hostName, boolean checkAgentCompleteCollect, boolean uninstall, String operator) {
        this.handleDeleteAgentByHostName(hostName, checkAgentCompleteCollect, uninstall, operator);
    }

    /**
     * 删除指定Agent对象
     *
     * @param hostName                  待删除Agent对象所在主机主机名
     * @param checkAgentCompleteCollect 是否检查待删除Agent是否已采集完其需要采集的所有日志
     *                                  true：将会校验待删除Agent所采集的所有日志采集任务是否都已采集完其所有的待采集文件，如未采集完，将导致删除该Agent对象失败，直到采集完其所有的待采集文件
     *                                  false：将会忽略待删除Agent所采集的所有日志采集任务是否都已采集完其所有的待采集文件，直接删除Agent对象（注意：将导致日志采集不完整情况，请谨慎使用）
     * @param uninstall                 是否卸载Agent true：卸载 false：不卸载
     * @param operator                  操作人
     * @throws ServiceException 执行"删除指定Agent对象"过程种出现的异常
     */
    private void handleDeleteAgentByHostName(String hostName, boolean checkAgentCompleteCollect, boolean uninstall, String operator) throws ServiceException {
        /*
         * 校验待删除Agent对象在系统中是否存在
         */
        AgentDO agentDO = getAgentByHostName(hostName);
        if (null == agentDO) {
            throw new ServiceException(
                    "删除失败：待删除 Agent 在系统中不存在",
                    ErrorCodeEnum.AGENT_NOT_EXISTS.getCode()
            );
        }
        /*
         * 删除 agent
         */
        deleteAgent(agentDO, checkAgentCompleteCollect, uninstall, operator);
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.AGENT,
                OperationEnum.DELETE,
                hostName,
                String.format("删除Agent对象={hostName={%s}, checkAgentCompleteCollect={%b}, uninstall={%b}}", hostName, checkAgentCompleteCollect, uninstall),
                operator
        );
    }

    /**
     * 删除指定Agent对象
     *
     * @param agentDO                   待删除agent对象
     * @param checkAgentCompleteCollect 是否检查待删除Agent是否已采集完其需要采集的所有日志
     *                                  true：将会校验待删除Agent所采集的所有日志采集任务是否都已采集完其所有的待采集文件，如未采集完，将导致删除该Agent对象失败，直到采集完其所有的待采集文件
     *                                  false：将会忽略待删除Agent所采集的所有日志采集任务是否都已采集完其所有的待采集文件，直接删除Agent对象（注意：将导致日志采集不完整情况，请谨慎使用）
     * @param uninstall                 是否卸载Agent true：卸载 false：不卸载
     * @param operator                  操作人
     * @throws ServiceException 执行"删除指定Agent对象"过程种出现的异常
     */
    private void deleteAgent(AgentDO agentDO, boolean checkAgentCompleteCollect, boolean uninstall, String operator) throws ServiceException {
        CheckResult checkResult = agentManageServiceExtension.checkDeleteParameterAgent(agentDO);
        if (!checkResult.getCheckResult()) {//agent对象信息不合法
            throw new ServiceException(checkResult.getMessage(), checkResult.getCode());
        }
        /*
         * 检查待删除 Agent 对象是否存在未被采集完的日志信息，如存在，则抛异常，终止 Agent 删除操作
         */
        if (checkAgentCompleteCollect) {
            CheckResult agentCompleteCollectCheckResult = this.checkAgentCompleteCollect(agentDO);
            if (!agentCompleteCollectCheckResult.getCheckResult()) {//agent存在未被采集完的日志，暂不可删除
                throw new ServiceException(
                        agentCompleteCollectCheckResult.getMessage(),
                        agentCompleteCollectCheckResult.getCode()
                );
            }
        }
        /*
         * 添加一条Agent卸载任务记录 todo 功能未完成
         */
//        if (uninstall) {
//            AgentOperationTaskDO agentOperationTask = agentOperationTaskManageService.agent2AgentOperationTaskUnInstall(agentDO);
//            agentOperationTaskManageService.createAgentOperationTask(agentOperationTask, operator);
//        }
        /*'
         * 删除Agent关联Agent健康信息
         */
        agentHealthManageService.deleteByAgentId(agentDO.getId(), operator);
        /*
         * 根据给定Agent对象id删除对应Agent对象
         */
        agentDAO.deleteByPrimaryKey(agentDO.getId());
    }

    @Override
    public AgentDO getAgentByHostName(String hostName) {
        AgentPO agentPO = agentDAO.selectByHostName(hostName);
        if (null == agentPO) return null;
        return agentManageServiceExtension.agentPO2AgentDO(agentPO);
    }

    @Override
    @Transactional
    public void updateAgent(AgentDO agentDO, String operator) {
        this.handleUpdateAgent(agentDO, operator);
    }

    @Override
    public AgentDO getById(Long id) {
        AgentPO agentPO = agentDAO.selectByPrimaryKey(id);
        if (null == agentPO) {
            return null;
        } else {
            return agentManageServiceExtension.agentPO2AgentDO(agentPO);
        }
    }

    @Override
    @Transactional
    public void deleteAgentById(Long id, boolean checkAgentCompleteCollect, boolean uninstall, String operator) {
        this.handleDeleteAgentById(id, checkAgentCompleteCollect, uninstall, operator);
    }

    @Override
    @Transactional
    public void deleteAgentByIds(List<Long> ids, boolean checkAgentCompleteCollect, boolean uninstall, String operator) {
        for (Long id : ids) {
            this.handleDeleteAgentById(id, checkAgentCompleteCollect, uninstall, operator);
        }
    }

    @Override
    public List<AgentDO> getAgentsByAgentVersionId(Long agentVersionId) {
        List<AgentPO> agentPOList = agentDAO.selectByAgentVersionId(agentVersionId);
        if (CollectionUtils.isEmpty(agentPOList)) {
            return new ArrayList<>();
        }
        return agentManageServiceExtension.agentPOList2AgentDOList(agentPOList);
    }

    @Override
    public List<AgentDO> list() {
        List<AgentPO> agentPOList = agentDAO.getAll();
        if (CollectionUtils.isEmpty(agentPOList)) {
            return new ArrayList<>();
        }
        return agentManageServiceExtension.agentPOList2AgentDOList(agentPOList);
    }

    @Override
    public List<AgentDO> getAgentListByKafkaClusterId(Long kafkaClusterId) {
        List<AgentPO> agentPOList = agentDAO.selectByKafkaClusterId(kafkaClusterId);
        if (CollectionUtils.isEmpty(agentPOList)) {
            return new ArrayList<>();
        }
        return agentManageServiceExtension.agentPOList2AgentDOList(agentPOList);
    }

    @Override
    public Result<List<String>> listFiles(String hostName, String path, String suffixMatchRegular) {
        /*
         * 根据对应主机类型获取 其 real path
         */
        String realPath = path;
        HostDO hostDO = hostManageService.getHostByHostName(hostName);
        if (null == hostDO) {
            throw new ServiceException(
                    String.format("待获取文件名集的主机hostName={%s}在系统中不存在对应agent", hostName),
                    ErrorCodeEnum.HOST_NOT_EXISTS.getCode()
            );
        }
        AgentDO agentDO = getAgentByHostName(hostName);
        if (null == agentDO) {
            throw new ServiceException(
                    String.format("待获取文件名集的主机hostName={%s}在系统中不存在", hostName),
                    ErrorCodeEnum.AGENT_NOT_EXISTS.getCode()
            );
        }
        if (hostDO.getContainer().equals(HostTypeEnum.CONTAINER.getCode())) {
            /*
             * TODO：根据 path 获取实际容器路径 real path
             */
        }
        return agentManageServiceExtension.listFiles(hostName, realPath, suffixMatchRegular);
    }

    @Override
    public Long countAll() {
        return agentDAO.countAll();
    }

    /**
     * 根据id删除对应agent对象
     *
     * @param id                        待删除agent对象 id
     * @param checkAgentCompleteCollect 删除agent时，是否检测该agent是否存在未被采集完的日志，如该参数值设置为true，当待删除agent存在未被采集完的日志时，将会抛出异常，不会删除该agent
     * @param uninstall                 是否卸载 agent，该参数设置为true，将添加一个该agent的卸载任务
     * @param operator                  操作人
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleDeleteAgentById(Long id, boolean checkAgentCompleteCollect, boolean uninstall, String operator) throws ServiceException {
        AgentDO agentDO = getById(id);
        if (null == agentDO) {
            throw new ServiceException(
                    "删除失败：待删除 Agent 在系统中不存在",
                    ErrorCodeEnum.AGENT_NOT_EXISTS.getCode()
            );
        }
        deleteAgent(agentDO, checkAgentCompleteCollect, uninstall, operator);
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.AGENT,
                OperationEnum.DELETE,
                id,
                String.format("删除Agent对象={id={%d}, checkAgentCompleteCollect={%b}, uninstall={%b}}", id, checkAgentCompleteCollect, uninstall),
                operator
        );
    }

    /**
     * 更新给定AgentDO对象
     *
     * @param agentDOTarget 待更新AgentDO对象
     * @param operator      操作者
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleUpdateAgent(AgentDO agentDOTarget, String operator) throws ServiceException {
        /*
         * 校验待创建 AgentPO 对象参数信息是否合法
         */
        CheckResult checkResult = agentManageServiceExtension.checkUpdateParameterAgent(agentDOTarget);
        if (!checkResult.getCheckResult()) {//待更新AgentDO对象信息不合法
            throw new ServiceException(checkResult.getMessage(), checkResult.getCode());
        }
        /*
         * 校验待更新AgentDO对象在系统中是否存在
         */
        AgentDO agentDOExists = getById(agentDOTarget.getId());
        if (null == agentDOExists) {
            throw new ServiceException(
                    String.format("待更新Agent对象={id=%d}在系统中不存在", agentDOTarget.getId()),
                    ErrorCodeEnum.AGENT_NOT_EXISTS.getCode()
            );
        }
        /*
         * 更新 Agent
         */
        AgentDO agentDO = agentManageServiceExtension.updateAgent(agentDOExists, agentDOTarget);
        AgentPO agentPO = ConvertUtil.obj2Obj(agentDO, AgentPO.class);

        agentPO.setOperator(CommonConstant.getOperator(operator));
        agentPO.setModifyTime(new Date());
        agentDAO.updateByPrimaryKeySelective(agentPO);
        /*
         * 添加对应操作记录
         */
        operateRecordService.save(
                ModuleEnum.AGENT,
                OperationEnum.EDIT,
                agentDOTarget.getId(),
                String.format("修改Agent={%s}，修改成功的Agent对象id={%d}", JSON.toJSONString(agentDOTarget), agentDOTarget.getId()),
                operator
        );
    }

    /**
     * 校验给定Agent是否已采集完所需采集的所有日志信息
     *
     * @param agentDO 待校验是否采集完毕的 agentDO 对象
     * @return true：已采集完 false：未采集完
     * @throws ServiceException
     */
    private CheckResult checkAgentCompleteCollect(AgentDO agentDO) throws ServiceException {
        /*
         * 根据给定agent对象对应采集类型，获取其需要采集的主机集
         */
        List<HostDO> checkHostList = hostManageService.getRelationHostListByAgent(agentDO);
        /*
         * 校验Agent需要采集的主机集是否存在未被采集完的日志信息
         */
        for (HostDO checkHost : checkHostList) {
            boolean completeCollect = completeCollect(checkHost);
            if (completeCollect) {//已采集完
                // do nothing
                continue;
            } else {//未采集完
                return new CheckResult(false);
            }
        }
        return new CheckResult(true);
    }

    private boolean completeCollect(HostDO checkHost) {
        //TODO：
        return true;
    }

    @Override
    public List<String> getAllHostNames() {
        return agentDAO.getAllHostNames();
    }

    @Override
    public List<AgentDO> getByHealthLevel(Integer agentHealthLevelCode) {
        List<AgentPO> agentPOList = agentDAO.getByHealthLevel(agentHealthLevelCode);
        return agentManageServiceExtension.agentPOList2AgentDOList(agentPOList);
    }

}
