package com.didichuxing.datachannel.agentmanager.core.agent.manage.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.operationtask.AgentOperationTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.k8s.K8sPodDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.AgentPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.http.PathRequest;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.*;
import com.didichuxing.datachannel.agentmanager.common.constant.AgentConstant;
import com.didichuxing.datachannel.agentmanager.common.constant.AgentHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.host.HostTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.ModuleEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.OperationEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.agentmanager.common.util.HttpUtils;
import com.didichuxing.datachannel.agentmanager.core.agent.health.AgentHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.common.OperateRecordService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.k8s.K8sPodManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentMapper;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.operation.task.AgentOperationTaskManageService;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.manage.extension.AgentManageServiceExtension;
import com.didichuxing.datachannel.agentmanager.thirdpart.metadata.k8s.util.K8sUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private AgentMapper agentDAO;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private AgentOperationTaskManageService agentOperationTaskManageService;

    @Autowired
    private AgentManageServiceExtension agentManageServiceExtension;

    @Autowired
    private AgentMetricsManageService agentMetricsManageService;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private OperateRecordService operateRecordService;

    @Autowired
    private AgentHealthManageService agentHealthManageService;

    @Autowired
    private K8sPodManageService k8sPodManageService;

    /**
     * 远程请求 agent url
     */
    @Value("${agent.http.path.request.url}")
    private String requestUrl;

    /**
     * 远程请求 agent 端口
     */
    @Value("${agent.http.path.request.port}")
    private Integer requestPort;

    @Override
    @Transactional
    public Long createAgent(AgentDO agent, String operator) {
        return this.handleCreateAgent(agent, operator);//创建 agent 对象
    }

    /**
     * 创建Agent对象处理流程
     * @param agentDO 待创建Agent对象
     * @param operator 操作人
     * @return 创建成功的Agent对象id
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long handleCreateAgent(AgentDO agentDO, String operator) throws ServiceException {
        /*
         * 校验待创建 AgentPO 对象参数信息是否合法
         */
        CheckResult checkResult = agentManageServiceExtension.checkCreateParameterAgent(agentDO);
        if(!checkResult.getCheckResult()) {//agent对象信息不合法
            throw new ServiceException(checkResult.getMessage(), checkResult.getCode());
        }
        /*
         * 校验待创建Agent在系统中是否已存在（已存在判断条件为：系统中是否已存在主机名为待创建Agent对应主机名的Agent）？如不存在，表示合法，如已存在，表示非法
         */
        boolean agentExists = agentExists(agentDO.getHostName());
        if(agentExists) {
            throw new ServiceException(
                    String.format("Agent对象{%s}创建失败，原因为：系统中已存在主机名与待创建Agent主机名{%s}相同的Agent", JSON.toJSONString(agentDO), agentDO.getHostName()),
                    ErrorCodeEnum.AGENT_EXISTS_IN_HOST_WHEN_AGENT_CREATE.getCode()
            );
        }
        /*
         * 校验主机名为待添加Agent对象对应主机名的主机是否存在？如存在，表示合法，如不存在，表示非法，不能在一个在系统中不存在的主机安装Agent
         */
        boolean hostExists = hostExists(agentDO.getHostName());
        if(!hostExists) {
            throw new ServiceException(
                    String.format("Agent对象{%s}创建失败，原因为：不能基于一个系统中不存在的主机创建Agent对象，系统中已不存在主机名为待创建Agent对应主机名{%s}的主机对象", JSON.toJSONString(agentDO), agentDO.getHostName()),
                    ErrorCodeEnum.HOST_NOT_EXISTS.getCode()
            );
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

    /**
     * 校验系统中是否已存在给定主机名的主机对象
     * @param hostName 主机名
     * @return true：存在 false：不存在
     * @throws ServiceException 执行 "校验系统中是否已存在给定主机名的主机对象" 过程中出现的异常
     */
    private boolean hostExists(String hostName) throws ServiceException {
        HostDO hostDO = hostManageService.getHostByHostName(hostName);
        if(null == hostDO) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 校验系统中是否已存在给定主机名的Agent对象
     * @param hostName 主机名
     * @return true：存在 false：不存在
     * @throws ServiceException 执行 "校验系统中是否已存在给定主机名的Agent对象" 过程中出现的异常
     */
    private boolean agentExists(String hostName) throws ServiceException {
        try {
            AgentPO agentPO = agentDAO.selectByHostName(hostName);
            if(null == agentPO) {
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
     * @param hostName 待删除Agent对象所在主机主机名
     * @param checkAgentCompleteCollect 是否检查待删除Agent是否已采集完其需要采集的所有日志
     *                                  true：将会校验待删除Agent所采集的所有日志采集任务是否都已采集完其所有的待采集文件，如未采集完，将导致删除该Agent对象失败，直到采集完其所有的待采集文件
     *                                  false：将会忽略待删除Agent所采集的所有日志采集任务是否都已采集完其所有的待采集文件，直接删除Agent对象（注意：将导致日志采集不完整情况，请谨慎使用）
     * @param uninstall 是否卸载Agent true：卸载 false：不卸载
     * @param operator 操作人
     * @throws ServiceException 执行"删除指定Agent对象"过程种出现的异常
     */
    private void handleDeleteAgentByHostName(String hostName, boolean checkAgentCompleteCollect, boolean uninstall, String operator) throws ServiceException {
        /*
         * 校验待删除Agent对象在系统中是否存在
         */
        AgentDO agentDO = getAgentByHostName(hostName);
        if(null == agentDO) {
            throw new ServiceException(
                    String.format("根据hostName删除Agent对象失败，原因为：系统中不存在hostName为{%s}的Agent对象", hostName),
                    ErrorCodeEnum.AGENT_NOT_EXISTS.getCode()
            );
        }
        /*
         * 删除 agent
         */
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
     * @param agentDO 待删除agent对象
     * @param checkAgentCompleteCollect 是否检查待删除Agent是否已采集完其需要采集的所有日志
     *                                   true：将会校验待删除Agent所采集的所有日志采集任务是否都已采集完其所有的待采集文件，如未采集完，将导致删除该Agent对象失败，直到采集完其所有的待采集文件
     *                                   false：将会忽略待删除Agent所采集的所有日志采集任务是否都已采集完其所有的待采集文件，直接删除Agent对象（注意：将导致日志采集不完整情况，请谨慎使用）
     * @param uninstall 是否卸载Agent true：卸载 false：不卸载
     * @param operator 操作人
     * @throws ServiceException 执行"删除指定Agent对象"过程种出现的异常
     */
    private void deleteAgent(AgentDO agentDO, boolean checkAgentCompleteCollect, boolean uninstall, String operator) throws ServiceException {
        CheckResult checkResult = agentManageServiceExtension.checkDeleteParameterAgent(agentDO);
        if(!checkResult.getCheckResult()) {//agent对象信息不合法
            throw new ServiceException(checkResult.getMessage(), checkResult.getCode());
        }
        /*
         * 检查待删除 Agent 对象是否存在未被采集完的日志信息，如存在，则抛异常，终止 Agent 删除操作
         */
        if(checkAgentCompleteCollect) {
            CheckResult agentCompleteCollectCheckResult = this.checkAgentCompleteCollect(agentDO);
            if(!agentCompleteCollectCheckResult.getCheckResult()) {//agent存在未被采集完的日志，暂不可删除
                throw new ServiceException(
                        agentCompleteCollectCheckResult.getMessage(),
                        agentCompleteCollectCheckResult.getCode()
                );
            }
        }
        if(uninstall) {
            /*
             * 添加一条Agent卸载任务记录
             */
            AgentOperationTaskDO agentOperationTask = agentOperationTaskManageService.agent2AgentOperationTaskUnInstall(agentDO);
            agentOperationTaskManageService.createAgentOperationTask(agentOperationTask, operator);
        }
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
        if(null == agentPO) return null;
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
        if(null == agentPO) {
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
        if(CollectionUtils.isEmpty(agentPOList)) {
            return new ArrayList<>();
        }
        return agentManageServiceExtension.agentPOList2AgentDOList(agentPOList);
    }

    @Override
    public List<MetricPanelGroup> listAgentMetrics(Long agentId, Long startTime, Long endTime) {
        return handleListAgentMetrics(agentId, startTime, endTime);
    }

    /**
     * 根据 agent id 获取给定时间范围内对应 agent 运行时指标信息
     * @param agentId agent id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回根据 agent id 获取到的给定时间范围内对应 agent 运行时指标信息
     */
    private List<MetricPanelGroup> handleListAgentMetrics(Long agentId, Long startTime, Long endTime) {
        /*
         * 获取agent信息
         */
        AgentDO agentDO = getById(agentId);
        if(null == agentDO) {
            throw new ServiceException(
                    String.format("待获取Agent指标信息的Agent={id=%d}在系统中不存在", agentId),
                    ErrorCodeEnum.AGENT_NOT_EXISTS.getCode()
            );
        }
        MetricsDashBoard metricsDashBoard = new MetricsDashBoard();
        MetricPanelGroup agentMetricPanelGroup = metricsDashBoard.buildMetricPanelGroup(AgentConstant.AGENT_METRIC_PANEL_GROUP_NAME_RUNTIME);
        /*
         * 构建"Agent cpu使用率/分钟"指标
         */
        MetricPanel agentCpuUsagePerMinMetricPanel = agentMetricPanelGroup.buildMetricPanel(AgentConstant.AGENT_METRIC_PANEL_NAME_CPU_USAGE_PER_MIN);
        List<MetricPoint> agentCpuUsagePerMinMetricPointList = agentMetricsManageService.getAgentCpuUsagePerMinMetric(agentDO.getHostName(), startTime, endTime);
        agentCpuUsagePerMinMetricPanel.buildMetric(AgentConstant.AGENT_METRIC_NAME_CPU_USAGE_PER_MIN, agentCpuUsagePerMinMetricPointList);

        /*
         * 构建"Agent 内存使用量/分钟"指标
         */
        MetricPanel agentMemoryUsagePerMinMetricPanel = agentMetricPanelGroup.buildMetricPanel(AgentConstant.AGENT_METRIC_PANEL_NAME_MEMORY_USAGE_PER_MIN);
        List<MetricPoint> agentMemoryUsagePerMinMetricPointList = agentMetricsManageService.getAgentMemoryUsagePerMinMetric(agentDO.getHostName(), startTime, endTime);
        agentMemoryUsagePerMinMetricPanel.buildMetric(AgentConstant.AGENT_METRIC_PANEL_NAME_MEMORY_USAGE_PER_MIN, agentMemoryUsagePerMinMetricPointList);
        /*
         * 构建"Agent fd使用量/分钟"指标
         */
        MetricPanel agentFdUsagePerMinMetricPanel = agentMetricPanelGroup.buildMetricPanel(AgentConstant.AGENT_METRIC_PANEL_NAME_FD_USAGE_PER_MIN);
        List<MetricPoint> agentFdUsagePerMinMetricPointList = agentMetricsManageService.getAgentFdUsagePerMinMetric(agentDO.getHostName(), startTime, endTime);
        agentFdUsagePerMinMetricPanel.buildMetric(AgentConstant.AGENT_METRIC_NAME_FD_USAGE_PER_MIN, agentFdUsagePerMinMetricPointList);
        /*
         * 构建"Agent fullgc次数/分钟"指标
         */
        MetricPanel agentFullGcTimesPerMinMetricPanel = agentMetricPanelGroup.buildMetricPanel(AgentConstant.AGENT_METRIC_PANEL_NAME_FULL_GC_TIMES_PER_MIN);
        List<MetricPoint> agentFullGcTimesPerMinMetricPointList = agentMetricsManageService.getAgentFullGcTimesPerMinMetric(agentDO.getHostName(), startTime, endTime);
        agentFullGcTimesPerMinMetricPanel.buildMetric(AgentConstant.AGENT_METRIC_NAME_FULL_GC_TIMES_PER_MIN, agentFullGcTimesPerMinMetricPointList);
        /*
         * 构建"Agent数据流出口发送流量bytes/分钟"指标
         */
        MetricPanel agentOutputBytesPerMinMetricPanel = agentMetricPanelGroup.buildMetricPanel(AgentConstant.AGENT_METRIC_PANEL_NAME_OUTPUT_BYTES_PER_MIN);
        List<MetricPoint> agentOutputBytesPerMinMetricPointList = agentMetricsManageService.getAgentOutputBytesPerMinMetric(agentDO.getHostName(), startTime, endTime);
        agentOutputBytesPerMinMetricPanel.buildMetric(AgentConstant.AGENT_METRIC_NAME_OUTPUT_BYTES_PER_MIN, agentOutputBytesPerMinMetricPointList);
        /*
         * 构建"Agent数据流出口发送条数/分钟"指标
         */
        MetricPanel agentOutputLogsCountPerMinMetricPanel = agentMetricPanelGroup.buildMetricPanel(AgentConstant.AGENT_METRIC_PANEL_NAME_OUTPUT_LOGS_COUNT_PER_MIN);
        List<MetricPoint> agentOutputLogsCountPerMinMetricPointList = agentMetricsManageService.getAgentOutputLogsCountPerMinMetric(agentDO.getHostName(), startTime, endTime);
        agentOutputLogsCountPerMinMetricPanel.buildMetric(AgentConstant.AGENT_METRIC_NAME_OUTPUT_LOGS_COUNT_PER_MIN, agentOutputLogsCountPerMinMetricPointList);

        return metricsDashBoard.getMetricPanelGroupList();
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
    public List<AgentDO> list() {
        List<AgentPO> agentPOList = agentDAO.getAll();
        if(CollectionUtils.isEmpty(agentPOList)) {
            return new ArrayList<>();
        }
        return agentManageServiceExtension.agentPOList2AgentDOList(agentPOList);
    }

    @Override
    public List<AgentDO> getAgentListByKafkaClusterId(Long kafkaClusterId) {
        List<AgentPO> agentPOList = agentDAO.selectByKafkaClusterId(kafkaClusterId);
        if(CollectionUtils.isEmpty(agentPOList)) {
            return new ArrayList<>();
        }
        return agentManageServiceExtension.agentPOList2AgentDOList(agentPOList);
    }

    @Override
    public List<String> listFiles(String hostName, String path, String suffixMatchRegular) {
        /*
         * 根据对应主机类型获取 其 real path
         */
        String realPath = path;
        HostDO hostDO = hostManageService.getHostByHostName(hostName);
        if(null == hostDO) {
            throw new ServiceException(
                    String.format("待获取文件名集的主机hostName={%s}在系统中不存在对应agent", hostName),
                    ErrorCodeEnum.HOST_NOT_EXISTS.getCode()
            );
        }
        AgentDO agentDO = getAgentByHostName(hostName);
        if(null == agentDO) {
            throw new ServiceException(
                    String.format("待获取文件名集的主机hostName={%s}在系统中不存在", hostName),
                    ErrorCodeEnum.AGENT_NOT_EXISTS.getCode()
            );
        }
        if(hostDO.getContainer().equals(HostTypeEnum.CONTAINER.getCode())) {
            K8sPodDO k8sPodDO = k8sPodManageService.getByContainerId(hostDO.getId());
            String logMountPath = k8sPodDO.getLogMountPath();
            String logHostPath = k8sPodDO.getLogHostPath();
            realPath = K8sUtil.getRealPath(logMountPath, logHostPath, path);
        }
        String pathRequestUrl = String.format(requestUrl, hostName, requestPort);
        String requestContent = JSON.toJSONString(new PathRequest(realPath, suffixMatchRegular));
        String responseStr = HttpUtils.postForString(pathRequestUrl, requestContent, null);
        List<String> fileNameList = JSON.parseObject(responseStr, List.class);
        return fileNameList;
    }

    /**
     * 校验给定Agent是否需要被健康巡检
     * @param agentDO 待校验 AgentDO 对象
     * @return true：须巡检 false：不须巡检
     * @throws ServiceException
     */
    private CheckResult agentNeedCheckHealth(AgentDO agentDO) throws ServiceException {
        //TODO：后续添加 agent 黑名单功能后，须添加黑名单过滤规则
        return new CheckResult(true);
    }

    /**
     * 根据id删除对应agent对象
     * @param id 待删除agent对象 id
     * @param checkAgentCompleteCollect 删除agent时，是否检测该agent是否存在未被采集完的日志，如该参数值设置为true，当待删除agent存在未被采集完的日志时，将会抛出异常，不会删除该agent
     * @param uninstall 是否卸载 agent，该参数设置为true，将添加一个该agent的卸载任务
     * @param operator 操作人
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleDeleteAgentById(Long id, boolean checkAgentCompleteCollect, boolean uninstall, String operator) throws ServiceException {
        AgentDO agentDO = getById(id);
        if(null == agentDO) {
            throw new ServiceException(
                    String.format("系统中不存在id={%d}的Agent对象", id),
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
     * @param agentDOTarget 待更新AgentDO对象
     * @param operator 操作者
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private void handleUpdateAgent(AgentDO agentDOTarget, String operator) throws ServiceException {
        /*
         * 校验待创建 AgentPO 对象参数信息是否合法
         */
        CheckResult checkResult = agentManageServiceExtension.checkUpdateParameterAgent(agentDOTarget);
        if(!checkResult.getCheckResult()) {//待更新AgentDO对象信息不合法
            throw new ServiceException(checkResult.getMessage(), checkResult.getCode());
        }
        /*
         * 校验待更新AgentDO对象在系统中是否存在
         */
        AgentDO agentDOExists = getById(agentDOTarget.getId());
        if(null == agentDOExists) {
            throw new ServiceException(
                    String.format("待更新Agent对象={id=%d}在系统中不存在", agentDOTarget.getId()),
                    ErrorCodeEnum.AGENT_NOT_EXISTS.getCode()
            );
        }
        /*
         * 更新 Agent
         */
        AgentDO agentDO = agentManageServiceExtension.updateAgent(agentDOExists, agentDOTarget);
        AgentPO agentPO = ConvertUtil.obj2Obj(agentDO, AgentPO.class);;
        agentPO.setOperator(CommonConstant.getOperator(operator));
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
            boolean completeCollect = agentMetricsManageService.completeCollect(checkHost);
            if(completeCollect) {//已采集完
                // do nothing
                continue;
            } else {//未采集完
                return new CheckResult(false);
            }
        }
        return new CheckResult(true);
    }

    /**
     * 检查给定 Agent 健康度，返回并将检查结果信息更新至表 tb_agent
     * @param agentDO 待检查 AgentDO 对象
     * @return 返回给定 agent 健康度检查结果
     */
    private AgentHealthLevelEnum handleCheckAgentHealth(AgentDO agentDO) throws ServiceException {
        AgentHealthDO agentHealthDO = agentHealthManageService.getByAgentId(agentDO.getId());//agentDO关联的AgentHealth对象
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
        if(!alive) {//不存活
            agentHealthLevelEnum = AgentHealthInspectionResultEnum.AGENT_HEART_BEAT_NOT_EXISTS.getAgentHealthLevel();
            agentHealthDescription = String.format(
                    "%s:AgentId={%d}, HostName={%s}",
                    AgentHealthInspectionResultEnum.AGENT_HEART_BEAT_NOT_EXISTS.getDescription(),
                    agentDO.getId(),
                    agentDO.getHostName()
            );
            agentHealthDO.setAgentHealthDescription(agentHealthDescription);
            agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
            agentHealthManageService.updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
            return agentHealthLevelEnum;
        }
        /*
         * 校验 agent 是否存在错误日志输出
         */
        Long agentHealthCheckTimeEnd = DateUtils.getBeforeSeconds(new Date(), 1).getTime();//Agent健康度检查流程获取agent心跳数据右边界时间，取当前时间前一秒
        boolean errorLogsExists = checkErrorLogsExists(agentDO.getHostName(), agentHealthDO, agentHealthCheckTimeEnd);
        if(errorLogsExists) {//agent 存在错误日志输出
            agentHealthLevelEnum = AgentHealthInspectionResultEnum.AGENT_ERRORLOGS_EXISTS.getAgentHealthLevel();
            agentHealthDescription = String.format(
                    "%s:AgentId={%d}, HostName={%s}",
                    AgentHealthInspectionResultEnum.AGENT_ERRORLOGS_EXISTS.getDescription(),
                    agentDO.getId(),
                    agentDO.getHostName()
            );
            agentHealthDO.setAgentHealthDescription(agentHealthDescription);
            agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
            agentHealthManageService.updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
            return agentHealthLevelEnum;
        } else {//agent 不存在错误日志输出
            agentHealthDO.setLastestErrorLogsExistsCheckHealthyTime(agentHealthCheckTimeEnd);
        }
        /************************ mark yellow cases ************************/
        /*
         * 校验是否存在 agent 非人工启动过频
         */
        boolean agentStartupFrequentlyExists = checkAgentStartupFrequentlyExists(agentDO.getHostName(), agentHealthDO);
        if(agentStartupFrequentlyExists) {//存在 agent 非人工启动过频
            agentHealthLevelEnum = AgentHealthInspectionResultEnum.AGENT_STARTUP_FREQUENTLY.getAgentHealthLevel();
            agentHealthDescription = String.format(
                    "%s:AgentId={%d}, HostName={%s}",
                    AgentHealthInspectionResultEnum.AGENT_STARTUP_FREQUENTLY.getDescription(),
                    agentDO.getId(),
                    agentDO.getHostName()
            );
            agentHealthDO.setAgentHealthDescription(agentHealthDescription);
            agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
            agentHealthManageService.updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
            return agentHealthLevelEnum;
        }
        /*
         * 校验是否存在 agent 进程 gc 指标异常
         */
        boolean agentGcMetricExceptionExists = checkAgentGcMetricExceptionExists(agentDO.getHostName());
        if(agentGcMetricExceptionExists) {//存在 agent 进程 gc 指标异常
            agentHealthLevelEnum = AgentHealthInspectionResultEnum.AGENT_GC_METRIC_EXCEPTION.getAgentHealthLevel();
            agentHealthDescription = String.format(
                    "%s:AgentId={%d}, HostName={%s}",
                    AgentHealthInspectionResultEnum.AGENT_GC_METRIC_EXCEPTION.getDescription(),
                    agentDO.getId(),
                    agentDO.getHostName()
            );
            agentHealthDO.setAgentHealthDescription(agentHealthDescription);
            agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
            agentHealthManageService.updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
            return agentHealthLevelEnum;
        }
        /*
         * 校验是否存在 agent 进程 cpu 使用率指标异常
         */
        boolean agentCpuUageMetricExceptionExists = checkAgentCpuUageMetricExceptionExists(agentDO.getHostName(), agentDO.getCpuLimitThreshold());
        if(agentCpuUageMetricExceptionExists) {//存在 agent 进程 cpu 使用率指标异常
            agentHealthLevelEnum = AgentHealthInspectionResultEnum.AGENT_CPU_USAGE_METRIC_EXCEPTION.getAgentHealthLevel();
            agentHealthDescription = String.format(
                    "%s:AgentId={%d}, HostName={%s}",
                    AgentHealthInspectionResultEnum.AGENT_CPU_USAGE_METRIC_EXCEPTION.getDescription(),
                    agentDO.getId(),
                    agentDO.getHostName()
            );
            agentHealthDO.setAgentHealthDescription(agentHealthDescription);
            agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
            agentHealthManageService.updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
            return agentHealthLevelEnum;
        }
        /*
         * 校验是否存在 agent 进程 fd 使用量指标异常
         */
        boolean agentFdUsageMetricExceptionExists = checkAgentFdUsageMetricExceptionExists(agentDO.getHostName());
        if(agentFdUsageMetricExceptionExists) {
            agentHealthLevelEnum = AgentHealthInspectionResultEnum.AGENT_FD_USAGE_METRIC_EXCEPTION.getAgentHealthLevel();
            agentHealthDescription = String.format(
                    "%s:AgentId={%d}, HostName={%s}",
                    AgentHealthInspectionResultEnum.AGENT_FD_USAGE_METRIC_EXCEPTION.getDescription(),
                    agentDO.getId(),
                    agentDO.getHostName()
            );
            agentHealthDO.setAgentHealthDescription(agentHealthDescription);
            agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
            agentHealthManageService.updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
            return agentHealthLevelEnum;
        }
        /*
         * 校验 agent 端是否存在出口限流
         */
        boolean byteLimitOnAgentExists = checkByteLimitOnAgentExists(agentDO.getHostName());
        if(byteLimitOnAgentExists) {
            agentHealthLevelEnum = AgentHealthInspectionResultEnum.HOST_BYTES_LIMIT_EXISTS.getAgentHealthLevel();
            agentHealthDescription = String.format(
                    "%s:AgentId={%d}, HostName={%s}",
                    AgentHealthInspectionResultEnum.AGENT_FD_USAGE_METRIC_EXCEPTION.getDescription(),
                    agentDO.getId(),
                    agentDO.getHostName()
            );
            agentHealthDO.setAgentHealthDescription(agentHealthDescription);
            agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
            agentHealthManageService.updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
            return agentHealthLevelEnum;
        }
        /*
         * 校验 agent 端是否未关联任何日志采集任务
         */
        boolean notRelateAnyLogCollectTask = checkAgentNotRelateAnyLogCollectTask(agentDO.getHostName());
        if(notRelateAnyLogCollectTask) {
            agentHealthLevelEnum = AgentHealthInspectionResultEnum.NOT_RELATE_ANY_LOGCOLLECTTASK.getAgentHealthLevel();
            agentHealthDescription = String.format(
                    "%s:AgentId={%d}, HostName={%s}",
                    AgentHealthInspectionResultEnum.AGENT_FD_USAGE_METRIC_EXCEPTION.getDescription(),
                    agentDO.getId(),
                    agentDO.getHostName()
            );
            agentHealthDO.setAgentHealthDescription(agentHealthDescription);
            agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
            agentHealthManageService.updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
            return agentHealthLevelEnum;
        }

        agentHealthDO.setAgentHealthDescription(agentHealthDescription);
        agentHealthDO.setAgentHealthLevel(agentHealthLevelEnum.getCode());
        agentHealthManageService.updateAgentHealth(agentHealthDO, CommonConstant.getOperator(null));
        return agentHealthLevelEnum;
    }

    /**
     * 校验给定主机名的Agent是否不关联任何日志采集任务
     * @param hostName 主机名
     * @return true：不关联任何日志采集任务 false：存在关联的日志采集任务
     */
    private boolean checkAgentNotRelateAnyLogCollectTask(String hostName) {
        /*
         * 根据 hostName 获取其对应 agent
         */
        AgentDO agentDO = getAgentByHostName(hostName);
        if(null == agentDO) {
            throw new ServiceException(
                    String.format("Agent={hostName=%s}在系统中不存在", hostName),
                    ErrorCodeEnum.AGENT_NOT_EXISTS.getCode()
            );
        }
        /*
         * 获取 agent 关联的日志采集任务集
         */
        List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageService.getLogCollectTaskListByAgentId(agentDO.getId());
        /*
         * 日志采集任务集是否为空？true：false
         */
        return CollectionUtils.isEmpty(logCollectTaskDOList);
    }

    /**
     * 校验 agent 端是否存在 cpu 阈值限流
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
        if(hostCpuLimiDturationMs > AgentHealthCheckConstant.HOST_CPU_LIMIT_MS_THRESHOLD) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 校验 agent 端是否存在出口流量阈值限流
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
        Long hostCpuLimiDturationMs = agentMetricsManageService.getHostByteLimiDturationByTimeFrame(
                startTime,
                endTime,
                hostName
        );//主机cpu限流时长 单位：ms
        return hostCpuLimiDturationMs > AgentHealthCheckConstant.HOST_BYTE_LIMIT_MS_THRESHOLD;
    }

    /**
     * 校验是否存在 agent 进程 fd 使用量指标异常
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
     * @param hostName agent 主机名
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

    /**
     * 校验是否存在 agent 非人工启动过频
     * @param hostName agent 主机名
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
        if(null == agentStartupTime || agentStartupTime <= 0) {//agent初次启动上报
            agentHealthDO.setAgentStartupTime(lastestAgentStartupTime);
            return false;
        } else {//agent非初次启动上报
            if(lastestAgentStartupTime.equals(agentStartupTime)) {//表示 agent 自系统记录的启动时间以来未有启动行为
               //do nothing
            } else {//表示 agent 自系统记录的启动时间以来存在启动行为，将系统记录的 agent 启动时间记为上一次启动时间，将最近一次心跳对应 agent 启动时间记为系统记录的 agent 启动时间
                agentHealthDO.setAgentStartupTimeLastTime(agentStartupTime);
                agentHealthDO.setAgentStartupTime(lastestAgentStartupTime);
            }
            Long agentStartupTimeLastTime = agentHealthDO.getAgentStartupTimeLastTime();//agent上一次启动时间
            if(null == agentStartupTimeLastTime) {//表示agent处于初次启动
                return false;
            } else {//表示agent非初次启动
                if(agentHealthDO.getAgentStartupTime() - agentHealthDO.getAgentStartupTimeLastTime() > AgentHealthCheckConstant.AGENT_STARTUP_FREQUENTLY_THRESHOLD) {

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
     * @param hostName agent 主机名
     * @param agentHealthDO AgentHealthDO对象
     * @param agentHealthCheckTimeEnd Agent健康度检查流程获取agent心跳数据右边界时间
     * @return true：存在错误日志输出 false：不存在错误日志输出
     */
    private boolean checkErrorLogsExists(String hostName, AgentHealthDO agentHealthDO, Long agentHealthCheckTimeEnd) {
        /*
         * 获取自上次"错误日志输出存在"健康点 ~ 当前时间，agent 是否存在错误日志输出
         */
        Long lastestCheckTime = agentHealthDO.getLastestErrorLogsExistsCheckHealthyTime();
        if(null == lastestCheckTime) {
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
        return !heartbeatTimes.equals(0L);
    }

}
