package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.agentmanager.common.annotation.CheckPermission;
import com.didichuxing.datachannel.agentmanager.common.bean.common.PaginationResult;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.DirectoryLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskPaginationRecordDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web.*;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.host.HostFilterRuleVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask.*;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.receiver.ReceiverVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.service.ServiceVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.constant.LogCollectTaskConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskStatusEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.common.util.SpringTool;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.manage.extension.AgentManageServiceExtension;
import com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.manage.extension.LogCollectTaskManageServiceExtension;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.didichuxing.datachannel.agentmanager.common.constant.PermissionConstant.*;

@Api(tags = "Normal-LogCollectTask维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "collect-task")
public class NormalLogCollectTaskController {

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private KafkaClusterManageService kafkaClusterManageService;

    @Autowired
    private ServiceManageService serviceManageService;

    @Autowired
    private LogCollectTaskHealthManageService logCollectTaskHealthManageService;

    @Autowired
    private LogCollectTaskManageServiceExtension logCollectTaskManageServiceExtension;

    @Autowired
    private AgentManageServiceExtension agentManageServiceExtension;

    @ApiOperation(value = "新增日志采集任务", notes = "")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    @CheckPermission(permission = AGENT_TASK_ADD)
    public Result createLogCollectTask(@RequestBody LogCollectTaskCreateDTO dto) {
        LogCollectTaskDO logCollectTaskDO = logCollectTaskCreateDTO2LogCollectTaskDO(dto);
        return Result.buildSucc(logCollectTaskManageService.createLogCollectTask(logCollectTaskDO, SpringTool.getUserName()));
    }

    @ApiOperation(value = "修改日志采集任务", notes = "")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    @CheckPermission(permission = AGENT_TASK_EDIT)
    public Result updateLogCollectTask(@RequestBody LogCollectTaskUpdateDTO dto) {
        LogCollectTaskDO logCollectTaskDO = logCollectTaskUpdateDTO2LogCollectTaskDO(dto);
        logCollectTaskManageService.updateLogCollectTask(logCollectTaskDO, SpringTool.getUserName());
        return Result.buildSucc();
    }

    @ApiOperation(value = "删除日志采集任务 0：删除成功 10000：参数错误 28000：待删除 LogCollectTask 不存在", notes = "")
    @RequestMapping(value = "/{logCollectTaskId}", method = RequestMethod.DELETE)
    @ResponseBody
    @CheckPermission(permission = AGENT_TASK_DELETE)
    public Result deleteLogCollectTask(@PathVariable Long logCollectTaskId) {
        logCollectTaskManageService.deleteLogCollectTask(logCollectTaskId, SpringTool.getUserName());
        return Result.buildSucc();
    }

    @ApiOperation(value = "查询日志采集任务列表", notes = "")
    @RequestMapping(value = "/paging", method = RequestMethod.POST)
    @ResponseBody
    public Result<PaginationResult<LogCollectTaskPaginationRecordVO>> listLogCollectTasks(@RequestBody LogCollectTaskPaginationRequestDTO dto) {
        LogCollectTaskPaginationQueryConditionDO logCollectTaskPaginationQueryConditionDO = LogCollectTaskPaginationRequestDTO2LogCollectTaskPaginationQueryConditionDO(dto);
        List<LogCollectTaskPaginationRecordVO> logCollectTaskPaginationRecordVOList = logCollectTaskPaginationRecordDOList2LogCollectTaskPaginationRecordVOList(
                logCollectTaskManageService.paginationQueryByConditon(logCollectTaskPaginationQueryConditionDO)
        );
        PaginationResult<LogCollectTaskPaginationRecordVO> paginationResult = new PaginationResult<>(
                logCollectTaskPaginationRecordVOList,
                logCollectTaskManageService.queryCountByCondition(logCollectTaskPaginationQueryConditionDO),
                dto.getPageNo(),
                dto.getPageSize()
        );
        return Result.buildSucc(paginationResult);
    }

    @ApiOperation(value = "查看日志采集任务详情", notes = "")
    @RequestMapping(value = "/{logCollectTaskId}", method = RequestMethod.GET)
    @ResponseBody
    public Result<LogCollectTaskVO> getLogCollectTaskById(@PathVariable Long logCollectTaskId) {
        LogCollectTaskDO logCollectTaskDO = logCollectTaskManageService.getById(logCollectTaskId);
        int agentCount = logCollectTaskManageService.getRelatedAgentCount(logCollectTaskId);
        logCollectTaskDO.setRelateAgentNum(agentCount);
        return Result.buildSucc(logCollectTaskDO2LogCollectTaskVO(logCollectTaskDO));
    }

    @ApiOperation(value = "启动/停止日志采集任务", notes = "")
    @RequestMapping(value = "/switch", method = RequestMethod.GET)
    @ResponseBody
    @CheckPermission(permission = AGENT_TASK_START_PAUSE)
    public Result switchLogCollectTask(@RequestParam(value = "logCollectTaskId") Long logCollectTaskId, @RequestParam(value = "status") Integer status) {
        logCollectTaskManageService.switchLogCollectTask(logCollectTaskId, status, SpringTool.getUserName());
        return Result.buildSucc();
    }

    @ApiOperation(value = "获取系统全量日志采集任务", notes = "")
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<LogCollectTaskVO>> getAll() {
        List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageService.getAll();
        return Result.buildSucc(ConvertUtil.list2List(logCollectTaskDOList, LogCollectTaskVO.class));
    }

    @ApiOperation(value = "根据给定主文件路径与文件后缀匹配正则获取满足匹配对应规则的文件集", notes = "")
    @RequestMapping(value = "/files", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<String>> listFiles(
            @RequestBody ListFilesDTO listFilesDTO

    ) {
        return agentManageServiceExtension.listFiles(listFilesDTO.getHostName(), listFilesDTO.getPath(), listFilesDTO.getSuffixRegular());
    }

    @ApiOperation(value = "根据给定日志样本与切片时间戳串获取对应切片规则配置", notes = "")
    @RequestMapping(value = "/slice_rule", method = RequestMethod.POST)
    @ResponseBody
    public Result<LogSliceRuleVO> getSliceRule(@RequestBody SliceSampleDTO sliceSampleDTO) {
        return Result.buildSucc(logCollectTaskManageService.getSliceRule(sliceSampleDTO.getContent(), sliceSampleDTO.getSliceDateTimeStringStartIndex(), sliceSampleDTO.getSliceDateTimeStringEndIndex()));
    }

    @ApiOperation(value = "读取文件内容 注：最多读取 100 行", notes = "")
    @RequestMapping(value = "/file-content", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> readFileContent(
            @RequestParam(value = "hostName") String hostName,
            @RequestParam(value = "path") String path
    ) {
        return agentManageServiceExtension.readFileContent(hostName, path);
    }

    @ApiOperation(value = "根据给定日志切片条件与待切片日志内容获取对应日志切片结果集", notes = "")
    @RequestMapping(value = "/result-slice", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<LogRecordVO>> slice(@RequestBody SliceDTO sliceDTO) {
        List<LogRecordVO> logList = logCollectTaskManageServiceExtension.slice(
                sliceDTO.getContent(),
                sliceDTO.getSliceTimestampFormat(),
                sliceDTO.getSliceTimestampPrefixString(),
                sliceDTO.getSliceTimestampPrefixStringIndex()
        );
        return Result.buildSucc(logList);
    }

    @ApiOperation(value = "获取所有文件名后缀匹配正则样例集", notes = "")
    @RequestMapping(value = "/file-name-suffix-regular-expression-examples-tips", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map<String, String>> get() {
        Map<String, String> fileNameSuffixRegularExpressionExample2TipsMap = new HashMap<>();
        fileNameSuffixRegularExpressionExample2TipsMap.put("info.log.1", ".\\d");
        fileNameSuffixRegularExpressionExample2TipsMap.put("info.log.2022-06-16-14", ".\\S");
        return Result.buildSucc(fileNameSuffixRegularExpressionExample2TipsMap);
    }

    /**
     * @param dto 待转化LogCollectTaskUpdateDTO对象
     * @return 返回将LogCollectTaskUpdateDTO对象转化为LogCollectTaskDO对象
     */
    private LogCollectTaskDO logCollectTaskUpdateDTO2LogCollectTaskDO(LogCollectTaskUpdateDTO dto) {
        LogCollectTaskDO logCollectTaskDO = new LogCollectTaskDO();
        logCollectTaskDO.setId(dto.getId());
        logCollectTaskDO.setLogContentFilterRuleLogicJsonString(JSON.toJSONString(dto.getLogContentFilterLogicDTO()));
        logCollectTaskDO.setLogCollectTaskExecuteTimeoutMs(dto.getLogCollectTaskExecuteTimeoutMs());
        logCollectTaskDO.setServiceIdList(dto.getServiceIdList());
        logCollectTaskDO.setAdvancedConfigurationJsonString(dto.getAdvancedConfigurationJsonString());
        logCollectTaskDO.setHostFilterRuleLogicJsonString(JSON.toJSONString(dto.getHostFilterRuleDTO()));
        logCollectTaskDO.setKafkaClusterId(dto.getKafkaClusterId());
        logCollectTaskDO.setSendTopic(dto.getSendTopic());
        logCollectTaskDO.setLimitPriority(dto.getLimitPriority());
        logCollectTaskDO.setOldDataFilterType(dto.getOldDataFilterType());
        logCollectTaskDO.setCollectEndTimeBusiness(dto.getCollectEndBusinessTime());
        logCollectTaskDO.setLogCollectTaskType(dto.getLogCollectTaskType());
        logCollectTaskDO.setCollectStartTimeBusiness(dto.getCollectStartBusinessTime());
        logCollectTaskDO.setLogCollectTaskRemark(dto.getLogCollectTaskRemark());
        logCollectTaskDO.setLogCollectTaskName(dto.getLogCollectTaskName());
        logCollectTaskDO.setCollectDelayThresholdMs(dto.getCollectDelayThresholdMs());
        logCollectTaskDO.setFileNameSuffixMatchRuleLogicJsonString(JSON.toJSONString(dto.getFileNameSuffixMatchRuleDTO()));
        logCollectTaskDO.setKafkaProducerConfiguration(dto.getKafkaProducerConfiguration());
        logCollectTaskDO.setLogContentSliceRuleLogicJsonString(JSON.toJSONString(dto.getLogSliceRuleDTO()));
        //  setDirectoryLogCollectPathList
        if (CollectionUtils.isNotEmpty(dto.getDirectoryLogCollectPathList())) {
            List<DirectoryLogCollectPathDO> directoryLogCollectPathList = new ArrayList<>(dto.getDirectoryLogCollectPathList().size());
            for (DirectoryLogCollectPathUpdateDTO directoryLogCollectPathUpdateDTO : dto.getDirectoryLogCollectPathList()) {
                DirectoryLogCollectPathDO directoryLogCollectPathDO = new DirectoryLogCollectPathDO();
                directoryLogCollectPathDO.setCollectFilesFilterRegularPipelineJsonString(JSON.toJSONString(directoryLogCollectPathUpdateDTO.getFilterRuleChain()));
                directoryLogCollectPathDO.setDirectoryCollectDepth(directoryLogCollectPathUpdateDTO.getDirectoryCollectDepth());
                directoryLogCollectPathDO.setPath(directoryLogCollectPathUpdateDTO.getPath());
                directoryLogCollectPathDO.setId(directoryLogCollectPathUpdateDTO.getId());
                directoryLogCollectPathDO.setCharset(directoryLogCollectPathUpdateDTO.getCharset());
                directoryLogCollectPathList.add(directoryLogCollectPathDO);
            }
            logCollectTaskDO.setDirectoryLogCollectPathList(directoryLogCollectPathList);
        }
        //  setFileLogCollectPathList
        if (CollectionUtils.isNotEmpty(dto.getFileLogCollectPathList())) {
            List<FileLogCollectPathDO> fileLogCollectPathList = new ArrayList<>(dto.getFileLogCollectPathList().size());
            for (FileLogCollectPathUpdateDTO fileLogCollectPathUpdateDTO : dto.getFileLogCollectPathList()) {
                FileLogCollectPathDO fileLogCollectPathDO = new FileLogCollectPathDO();
                fileLogCollectPathDO.setPath(fileLogCollectPathUpdateDTO.getPath());
                fileLogCollectPathDO.setId(fileLogCollectPathUpdateDTO.getId());
                fileLogCollectPathDO.setCharset(fileLogCollectPathUpdateDTO.getCharset());
                fileLogCollectPathList.add(fileLogCollectPathDO);
            }
            logCollectTaskDO.setFileLogCollectPathList(fileLogCollectPathList);
        }
        return logCollectTaskDO;
    }

    /**
     * @param dto 待转化LogCollectTaskCreateDTO对象
     * @return 返回将LogCollectTaskCreateDTO对象转化为LogCollectTaskDO对象
     */
    private LogCollectTaskDO logCollectTaskCreateDTO2LogCollectTaskDO(LogCollectTaskCreateDTO dto) {
        LogCollectTaskDO logCollectTaskDO = new LogCollectTaskDO();
        logCollectTaskDO.setLogContentFilterRuleLogicJsonString(JSON.toJSONString(dto.getLogContentFilterLogicDTO()));
        logCollectTaskDO.setLogCollectTaskExecuteTimeoutMs(dto.getLogCollectTaskExecuteTimeoutMs());
        logCollectTaskDO.setServiceIdList(dto.getServiceIdList());
        logCollectTaskDO.setAdvancedConfigurationJsonString(dto.getAdvancedConfigurationJsonString());
        logCollectTaskDO.setHostFilterRuleLogicJsonString(JSON.toJSONString(dto.getHostFilterRuleDTO()));
        logCollectTaskDO.setKafkaClusterId(dto.getKafkaClusterId());
        logCollectTaskDO.setSendTopic(dto.getSendTopic());
        logCollectTaskDO.setLimitPriority(dto.getLimitPriority());
        logCollectTaskDO.setOldDataFilterType(dto.getOldDataFilterType());
        logCollectTaskDO.setLogCollectTaskStatus(LogCollectTaskStatusEnum.RUNNING.getCode());
        logCollectTaskDO.setCollectEndTimeBusiness(dto.getCollectEndBusinessTime());
        logCollectTaskDO.setLogCollectTaskType(dto.getLogCollectTaskType());
        logCollectTaskDO.setCollectStartTimeBusiness(dto.getCollectStartBusinessTime());
        logCollectTaskDO.setLogCollectTaskRemark(dto.getLogCollectTaskRemark());
        logCollectTaskDO.setLogCollectTaskName(dto.getLogCollectTaskName());
        logCollectTaskDO.setConfigurationVersion(LogCollectTaskConstant.LOG_COLLECT_TASK_CONFIGURATION_VERSION_INIT);
        //  setDirectoryLogCollectPathList
        logCollectTaskDO.setCollectDelayThresholdMs(dto.getCollectDelayThresholdMs());
        logCollectTaskDO.setLogContentSliceRuleLogicJsonString(JSON.toJSONString(dto.getLogSliceRuleDTO()));
        logCollectTaskDO.setFileNameSuffixMatchRuleLogicJsonString(JSON.toJSONString(dto.getFileNameSuffixMatchRuleDTO()));
        logCollectTaskDO.setKafkaProducerConfiguration(dto.getKafkaProducerConfiguration());
        if (CollectionUtils.isNotEmpty(dto.getDirectoryLogCollectPathList())) {
            List<DirectoryLogCollectPathDO> directoryLogCollectPathList = new ArrayList<>(dto.getDirectoryLogCollectPathList().size());
            for (DirectoryLogCollectPathCreateDTO directoryLogCollectPathCreateDTO : dto.getDirectoryLogCollectPathList()) {
                DirectoryLogCollectPathDO directoryLogCollectPathDO = new DirectoryLogCollectPathDO();
                directoryLogCollectPathDO.setCollectFilesFilterRegularPipelineJsonString(JSON.toJSONString(directoryLogCollectPathCreateDTO.getFilterRuleChain()));
                directoryLogCollectPathDO.setDirectoryCollectDepth(directoryLogCollectPathCreateDTO.getDirectoryCollectDepth());
                directoryLogCollectPathDO.setPath(directoryLogCollectPathCreateDTO.getPath());
                directoryLogCollectPathDO.setCharset(directoryLogCollectPathCreateDTO.getCharset());
                directoryLogCollectPathList.add(directoryLogCollectPathDO);
            }
            logCollectTaskDO.setDirectoryLogCollectPathList(directoryLogCollectPathList);
        }
        //  setFileLogCollectPathList
        if (CollectionUtils.isNotEmpty(dto.getFileLogCollectPathList())) {
            List<FileLogCollectPathDO> fileLogCollectPathList = new ArrayList<>(dto.getFileLogCollectPathList().size());
            for (FileLogCollectPathCreateDTO fileLogCollectPathCreateDTO : dto.getFileLogCollectPathList()) {
                FileLogCollectPathDO fileLogCollectPathDO = new FileLogCollectPathDO();
                fileLogCollectPathDO.setPath(fileLogCollectPathCreateDTO.getPath());
                fileLogCollectPathDO.setCharset(fileLogCollectPathCreateDTO.getCharset());
                fileLogCollectPathList.add(fileLogCollectPathDO);
            }
            logCollectTaskDO.setFileLogCollectPathList(fileLogCollectPathList);
        }
        return logCollectTaskDO;
    }

    /**
     * 将LogCollectTaskPaginationRecordDO对象集转化为LogCollectTaskPaginationRecordVO对象集
     *
     * @param logCollectTaskPaginationRecordDOList 待转化LogCollectTaskPaginationRecordDO对象集
     * @return 返回将LogCollectTaskPaginationRecordDO对象集转化为LogCollectTaskPaginationRecordVO对象集
     */
    private List<LogCollectTaskPaginationRecordVO> logCollectTaskPaginationRecordDOList2LogCollectTaskPaginationRecordVOList(List<LogCollectTaskPaginationRecordDO> logCollectTaskPaginationRecordDOList) {
        List<LogCollectTaskPaginationRecordVO> logCollectTaskPaginationRecordVOList = new ArrayList<>(logCollectTaskPaginationRecordDOList.size());
        for (LogCollectTaskPaginationRecordDO logCollectTaskPaginationRecordDO : logCollectTaskPaginationRecordDOList) {
            LogCollectTaskPaginationRecordVO logCollectTaskPaginationRecordVO = new LogCollectTaskPaginationRecordVO();
            logCollectTaskPaginationRecordVO.setLogCollectTaskCreateTime(logCollectTaskPaginationRecordDO.getCreateTime().getTime());
            if (logCollectTaskPaginationRecordDO.getLogCollectTaskType().equals(LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode())) {//仅当日志采集任务为时间范围采集类型日志采集任务时，存在日志采集任务完成时间
                if (null != logCollectTaskPaginationRecordDO.getLogCollectTaskFinishTime()) {
                    logCollectTaskPaginationRecordVO.setLogCollectTaskFinishTime(logCollectTaskPaginationRecordDO.getLogCollectTaskFinishTime().getTime());
                }
            }
            logCollectTaskPaginationRecordVO.setLogCollectTaskHealthLevel(logCollectTaskPaginationRecordDO.getLogCollectTaskHealthLevel());
            logCollectTaskPaginationRecordVO.setLogCollectTaskHealthDescription(logCollectTaskPaginationRecordDO.getLogCollectTaskHealthDescription());
            logCollectTaskPaginationRecordVO.setLogCollectTaskHealthInspectionResultType(logCollectTaskPaginationRecordDO.getLogCollectTaskHealthInspectionResultType());
            logCollectTaskPaginationRecordVO.setLogCollectTaskId(logCollectTaskPaginationRecordDO.getLogCollectTaskId());
            logCollectTaskPaginationRecordVO.setLogCollectTaskName(logCollectTaskPaginationRecordDO.getLogCollectTaskName());
            logCollectTaskPaginationRecordVO.setLogCollectTaskType(logCollectTaskPaginationRecordDO.getLogCollectTaskType());
            logCollectTaskPaginationRecordVO.setReceiverTopic(logCollectTaskPaginationRecordDO.getSendTopic());
            ReceiverDO receiverDO = logCollectTaskPaginationRecordDO.getRelationReceiverDO();
            ReceiverVO receiverVO = ConvertUtil.obj2Obj(receiverDO, ReceiverVO.class);
            logCollectTaskPaginationRecordVO.setReceiverVO(receiverVO);
            List<ServiceDO> serviceDOList = logCollectTaskPaginationRecordDO.getRelationServiceList();
            List<ServiceVO> serviceVOList = ConvertUtil.list2List(serviceDOList, ServiceVO.class);
            logCollectTaskPaginationRecordVO.setServiceList(serviceVOList);
            logCollectTaskPaginationRecordVO.setLogCollectTaskStatus(logCollectTaskPaginationRecordDO.getLogCollectTaskStatus());
            logCollectTaskPaginationRecordVOList.add(logCollectTaskPaginationRecordVO);
        }
        return logCollectTaskPaginationRecordVOList;
    }

    /**
     * 将 LogCollectTaskPaginationRequestDTO 对象转化为 LogCollectTaskPaginationQueryConditionDO 对象
     *
     * @param dto 待转化 LogCollectTaskPaginationRequestDTO 对象
     * @return 返回将 LogCollectTaskPaginationRequestDTO 对象转化为 LogCollectTaskPaginationQueryConditionDO 对象
     */
    private LogCollectTaskPaginationQueryConditionDO LogCollectTaskPaginationRequestDTO2LogCollectTaskPaginationQueryConditionDO(LogCollectTaskPaginationRequestDTO dto) {
        LogCollectTaskPaginationQueryConditionDO logCollectTaskPaginationQueryConditionDO = new LogCollectTaskPaginationQueryConditionDO();
        if (StringUtils.isNotBlank(dto.getLogCollectTaskName())) {
            logCollectTaskPaginationQueryConditionDO.setLogCollectTaskName(dto.getLogCollectTaskName().replace("_", "\\_").replace("%", "\\%"));
        }
        if (CollectionUtils.isNotEmpty(dto.getLogCollectTaskHealthLevelList())) {
            logCollectTaskPaginationQueryConditionDO.setLogCollectTaskHealthLevelList(dto.getLogCollectTaskHealthLevelList());
        }
        if (CollectionUtils.isNotEmpty(dto.getLogCollectTaskTypeList())) {
            logCollectTaskPaginationQueryConditionDO.setLogCollectTaskTypeList(dto.getLogCollectTaskTypeList());
        }
        if (CollectionUtils.isNotEmpty(dto.getServiceIdList())) {
            logCollectTaskPaginationQueryConditionDO.setServiceIdList(dto.getServiceIdList());
        }
        if (CollectionUtils.isNotEmpty(dto.getLogCollectTaskStatusList())) {
            logCollectTaskPaginationQueryConditionDO.setLogCollectTaskStatusList(dto.getLogCollectTaskStatusList());
        }
        if (null != dto.getLogCollectTaskId()) {
            logCollectTaskPaginationQueryConditionDO.setLogCollectTaskId(dto.getLogCollectTaskId());
        }
        if (null != dto.getLocCollectTaskCreateTimeEnd()) {
            logCollectTaskPaginationQueryConditionDO.setCreateTimeEnd(new Date(dto.getLocCollectTaskCreateTimeEnd()));
        }
        if (null != dto.getLocCollectTaskCreateTimeStart()) {
            logCollectTaskPaginationQueryConditionDO.setCreateTimeStart(new Date(dto.getLocCollectTaskCreateTimeStart()));
        }
        if(StringUtils.isNotBlank(dto.getQueryTerm())) {
            logCollectTaskPaginationQueryConditionDO.setQueryTerm(dto.getQueryTerm());
        }
        logCollectTaskPaginationQueryConditionDO.setLimitFrom(dto.getLimitFrom());
        logCollectTaskPaginationQueryConditionDO.setLimitSize(dto.getLimitSize());
        logCollectTaskPaginationQueryConditionDO.setSortColumn(dto.getSortColumn());
        logCollectTaskPaginationQueryConditionDO.setAsc(dto.getAsc());
        return logCollectTaskPaginationQueryConditionDO;
    }

    /**
     * 将给定LogCollectTaskDO对象转化为LogCollectTaskVO对象
     *
     * @param logCollectTaskDO 待转化LogCollectTaskDO对象
     * @return 返回将给定LogCollectTaskDO对象转化为LogCollectTaskVO对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private LogCollectTaskVO logCollectTaskDO2LogCollectTaskVO(LogCollectTaskDO logCollectTaskDO) throws ServiceException {
        LogCollectTaskVO logCollectTaskVO = new LogCollectTaskVO();
        logCollectTaskVO.setAdvancedConfigurationJsonString(logCollectTaskDO.getAdvancedConfigurationJsonString());
        logCollectTaskVO.setCollectEndBusinessTime(logCollectTaskDO.getCollectEndTimeBusiness());
        logCollectTaskVO.setCollectStartBusinessTime(logCollectTaskDO.getCollectStartTimeBusiness());
        logCollectTaskVO.setHostFilterRuleVO(JSON.parseObject(logCollectTaskDO.getHostFilterRuleLogicJsonString(), HostFilterRuleVO.class));
        logCollectTaskVO.setId(logCollectTaskDO.getId());
        logCollectTaskVO.setLimitPriority(logCollectTaskDO.getLimitPriority());
        logCollectTaskVO.setLogCollectTaskExecuteTimeoutMs(logCollectTaskDO.getLogCollectTaskExecuteTimeoutMs());
        logCollectTaskVO.setLogCollectTaskName(logCollectTaskDO.getLogCollectTaskName());
        logCollectTaskVO.setLogCollectTaskRemark(logCollectTaskDO.getLogCollectTaskRemark());
        logCollectTaskVO.setLogCollectTaskType(logCollectTaskDO.getLogCollectTaskType());
        logCollectTaskVO.setLogContentFilterRuleVO(JSON.parseObject(logCollectTaskDO.getLogContentFilterRuleLogicJsonString(), LogContentFilterRuleVO.class));
        logCollectTaskVO.setOldDataFilterType(logCollectTaskDO.getOldDataFilterType());
        logCollectTaskVO.setRelateAgentNum(logCollectTaskDO.getRelateAgentNum());
        //set receiver
        ReceiverDO receiverDO = kafkaClusterManageService.getById(logCollectTaskDO.getKafkaClusterId());
        if (null == receiverDO) {
            logCollectTaskVO.setReceiver(null);
            logCollectTaskVO.setSendTopic(null);
            throw new ServiceException(String.format("LogCollectTask对象={id=%d}关联的Receiver对象={id=%d}在系统中不存在", logCollectTaskDO.getId(), logCollectTaskDO.getKafkaClusterId()), ErrorCodeEnum.KAFKA_CLUSTER_NOT_EXISTS.getCode());
        } else {
            logCollectTaskVO.setReceiver(ConvertUtil.obj2Obj(receiverDO, ReceiverVO.class));
            logCollectTaskVO.setSendTopic(logCollectTaskDO.getSendTopic());
        }
        //set service list
        List<Long> serviceIdList = logCollectTaskDO.getServiceIdList();
        List<ServiceVO> serviceVOList = new ArrayList<>(serviceIdList.size());
        for (Long serviceId : serviceIdList) {
            ServiceDO serviceDO = serviceManageService.getServiceById(serviceId);
            if (null == serviceDO) {
                throw new ServiceException(String.format("LogCollectTask对象={id=%d}关联的Service对象={id=%d}在系统中不存在", logCollectTaskDO.getId(), serviceId), ErrorCodeEnum.SERVICE_NOT_EXISTS.getCode());
            }
            serviceVOList.add(ConvertUtil.obj2Obj(serviceDO, ServiceVO.class));
        }
        logCollectTaskVO.setServices(serviceVOList);
        if (null != logCollectTaskDO.getLogCollectTaskFinishTime()) {
            logCollectTaskVO.setLogCollectTaskFinishTime(logCollectTaskDO.getLogCollectTaskFinishTime().getTime());
        }
        logCollectTaskVO.setLogCollectTaskStatus(logCollectTaskDO.getLogCollectTaskStatus());
        //set directoryLogCollectPathList
        List<DirectoryLogCollectPathDO> directoryLogCollectPathDOList = logCollectTaskDO.getDirectoryLogCollectPathList();
        List<DirectoryLogCollectPathVO> directoryLogCollectPathVOList = new ArrayList<>(directoryLogCollectPathDOList.size());
        for (DirectoryLogCollectPathDO directoryLogCollectPathDO : directoryLogCollectPathDOList) {
            DirectoryLogCollectPathVO directoryLogCollectPathVO = new DirectoryLogCollectPathVO();
            directoryLogCollectPathVO.setDirectoryCollectDepth(directoryLogCollectPathDO.getDirectoryCollectDepth());
            //setFilterRuleChain
            List<Pair<Integer, String>> collectFilesFilterRegularPipeline = JSON.parseObject(directoryLogCollectPathDO.getCollectFilesFilterRegularPipelineJsonString(), new ArrayList<Pair<Integer, String>>().getClass());
            if (CollectionUtils.isNotEmpty(collectFilesFilterRegularPipeline)) {
                List<Pair<Integer, String>> filterRuleChain = new ArrayList<>(collectFilesFilterRegularPipeline.size());
                for (Object obj : collectFilesFilterRegularPipeline) {
                    JSONObject pair = (JSONObject) obj;
                    Pair<Integer, String> filterRulePair = new Pair<>(pair.getInteger("key"), pair.getString("value"));
                    filterRuleChain.add(filterRulePair);
                }
                directoryLogCollectPathVO.setFilterRuleChain(filterRuleChain);
            }
            directoryLogCollectPathVO.setId(directoryLogCollectPathDO.getId());
            directoryLogCollectPathVO.setLogCollectTaskId(directoryLogCollectPathDO.getLogCollectTaskId());
            directoryLogCollectPathVO.setPath(directoryLogCollectPathDO.getPath());
            directoryLogCollectPathVO.setCharset(directoryLogCollectPathDO.getCharset());
            directoryLogCollectPathVOList.add(directoryLogCollectPathVO);
        }
        logCollectTaskVO.setDirectoryLogCollectPathList(directoryLogCollectPathVOList);
        //set fileLogCollectPathList
        List<FileLogCollectPathDO> fileLogCollectPathDOList = logCollectTaskDO.getFileLogCollectPathList();
        List<FileLogCollectPathVO> fileLogCollectPathVOList = new ArrayList<>(fileLogCollectPathDOList.size());
        for (FileLogCollectPathDO fileLogCollectPathDO : fileLogCollectPathDOList) {
            FileLogCollectPathVO fileLogCollectPathVO = new FileLogCollectPathVO();
            fileLogCollectPathVO.setId(fileLogCollectPathDO.getId());
            fileLogCollectPathVO.setLogCollectTaskId(fileLogCollectPathDO.getLogCollectTaskId());
            fileLogCollectPathVO.setPath(fileLogCollectPathDO.getPath());
            fileLogCollectPathVO.setCharset(fileLogCollectPathDO.getCharset());
            fileLogCollectPathVOList.add(fileLogCollectPathVO);
        }
        logCollectTaskVO.setFileLogCollectPathList(fileLogCollectPathVOList);
        //set logCollectTaskHealthLevel
        LogCollectTaskHealthDO logCollectTaskHealthDO = logCollectTaskHealthManageService.getByLogCollectTaskId(logCollectTaskDO.getId());
        if (null == logCollectTaskHealthDO) {
            throw new ServiceException(String.format("LogCollectTask对象={id=%d}关联的LogCollectTaskHealth对象在系统中不存在", logCollectTaskDO.getId()), ErrorCodeEnum.LOGCOLLECTTASK_HEALTH_NOT_EXISTS.getCode());
        }
        logCollectTaskVO.setLogCollectTaskHealthLevel(logCollectTaskHealthDO.getLogCollectTaskHealthLevel());
        logCollectTaskVO.setLogCollectTaskCreator(logCollectTaskHealthDO.getOperator());
        logCollectTaskVO.setLogCollectTaskHealthDescription(logCollectTaskHealthDO.getLogCollectTaskHealthDescription());
        logCollectTaskVO.setKafkaProducerConfiguration(logCollectTaskDO.getKafkaProducerConfiguration());
        logCollectTaskVO.setLogContentSliceRule(JSON.parseObject(logCollectTaskDO.getLogContentSliceRuleLogicJsonString(), LogSliceRuleVO.class));
        logCollectTaskVO.setFileNameSuffixMatchRule(JSON.parseObject(logCollectTaskDO.getFileNameSuffixMatchRuleLogicJsonString(), FileNameSuffixMatchRuleVO.class));
        logCollectTaskVO.setCollectDelayThresholdMs(logCollectTaskDO.getCollectDelayThresholdMs());
        return logCollectTaskVO;
    }

}
