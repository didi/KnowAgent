package com.didichuxing.datachannel.agentmanager.rest.api.v1.op;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.agentmanager.common.annotation.CheckPermission;
import com.didichuxing.datachannel.agentmanager.common.bean.common.PaginationResult;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.operationtask.AgentOperationSubTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.operationtask.AgentOperationTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.operationtask.AgentOperationTaskPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.agent.operationtask.AgentOperationTaskDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.agent.operationtask.AgentOperationTaskPaginationRequestDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.operationtask.AgentOperationTaskDetailVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.operationtask.AgentOperationTaskExecuteDetailListVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.operationtask.AgentOperationTaskExecuteDetailVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.operationtask.AgentOperationTaskPaginationRecordVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ResultTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.SpringTool;
import com.didichuxing.datachannel.agentmanager.core.agent.operation.task.AgentOperationSubTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.operation.task.AgentOperationTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.version.AgentVersionManageService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskSubStateEnum;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskTypeEnum;
import com.didichuxing.datachannel.agentmanager.remote.uic.UicInterfaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.didichuxing.datachannel.agentmanager.common.constant.PermissionConstant.*;

@Api(tags = "OP-AgentOperationTask维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX + "operation-tasks")
public class OPAgentOperationTaskController {

    @Autowired
    private AgentOperationTaskManageService agentOperationTaskManageService;

    @Autowired
    private AgentVersionManageService agentVersionManageService;

    @Autowired
    private AgentOperationSubTaskManageService agentOperationSubTaskManageService;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private UicInterfaceService n9eUicInterfaceServiceImpl;

    @ApiOperation(value = "创建Agent操作任务", notes = "")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    @CheckPermission(permission = {AGENT_MACHINE_INSTALL, AGENT_MACHINE_UPGRADE, AGENT_MACHINE_UNINSTALL})
    public Result<Long> createTask(@RequestBody AgentOperationTaskDTO dto, HttpServletRequest request) throws Exception {

        AgentOperationTaskDO agentOperationTaskDO = new AgentOperationTaskDO();
        agentOperationTaskDO.setAgentIdList(dto.getAgentIds());
        agentOperationTaskDO.setTargetAgentVersionId(dto.getAgentVersionId());
        agentOperationTaskDO.setHostIdList(dto.getHostIds());
        agentOperationTaskDO.setTaskType(dto.getTaskType());
        agentOperationTaskDO.setTaskName(dto.getTaskName());

        JSONObject dat = n9eUicInterfaceServiceImpl.getUserThreadCache().get();
        if (dat != null) {
            if (dto.getTaskType() == null) {
                throw new ServiceException("任务类型非法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
            }
            if (dto.getTaskType().equals(AgentOperationTaskTypeEnum.INSTALL.getCode()) && !dat.getBoolean(AGENT_MACHINE_INSTALL) ||
                    dto.getTaskType().equals(AgentOperationTaskTypeEnum.UNINSTALL.getCode()) && !dat.getBoolean(AGENT_MACHINE_UNINSTALL) ||
                    dto.getTaskType().equals(AgentOperationTaskTypeEnum.UPGRADE.getCode()) && !dat.getBoolean(AGENT_MACHINE_UPGRADE)) {
                throw new ServiceException("没有操作权限", ErrorCodeEnum.CHECK_PERMISSION_REMOTE_FAILED.getCode());
            }
        }

        return Result.buildSucc(agentOperationTaskManageService.createAgentOperationTask(agentOperationTaskDO, SpringTool.getUserName()));
    }

    @ApiOperation(value = "查询Agent操作任务列表", notes = "")
    @RequestMapping(value = "/paging", method = RequestMethod.POST)
    @ResponseBody
    // @CheckPermission(permission = AGENT_DEVOPSTASK_LIST)
    public Result<PaginationResult<AgentOperationTaskPaginationRecordVO>> listAgentOperationTask(@RequestBody AgentOperationTaskPaginationRequestDTO dto) {
        AgentOperationTaskPaginationQueryConditionDO agentOperationTaskPaginationQueryConditionDO = agentOperationTaskPaginationRequestDTO2AgentOperationTaskPaginationQueryConditionDO(dto);
        List<AgentOperationTaskPaginationRecordVO> agentOperationTaskPaginationRecordVOList = AgentOperationTaskDOList2AgentOperationTaskPaginationRecordVOList(agentOperationTaskManageService.paginationQueryByConditon(agentOperationTaskPaginationQueryConditionDO));
        PaginationResult paginationResult = new PaginationResult(agentOperationTaskPaginationRecordVOList, agentOperationTaskManageService.queryCountByCondition(agentOperationTaskPaginationQueryConditionDO), dto.getPageNo(), dto.getPageSize());
        return Result.buildSucc(paginationResult);
    }

    private List<AgentOperationTaskPaginationRecordVO> AgentOperationTaskDOList2AgentOperationTaskPaginationRecordVOList(List<AgentOperationTaskDO> agentOperationTaskDOList) throws ServiceException {
        List<AgentOperationTaskPaginationRecordVO> result = new ArrayList<>();
        for (AgentOperationTaskDO agentOperationTaskDO : agentOperationTaskDOList) {
            AgentOperationTaskPaginationRecordVO agentOperationTaskPaginationRecordVO = new AgentOperationTaskPaginationRecordVO();
            if (null != agentOperationTaskDO.getTaskEndTime()) {
                agentOperationTaskPaginationRecordVO.setAgentOperationTaskEndTime(agentOperationTaskDO.getTaskEndTime().getTime());
            }
            if (null != agentOperationTaskDO.getTaskStartTime()) {
                agentOperationTaskPaginationRecordVO.setAgentOperationTaskStartTime(agentOperationTaskDO.getTaskStartTime().getTime());
            }
            agentOperationTaskPaginationRecordVO.setAgentOperationTaskId(agentOperationTaskDO.getId());
            agentOperationTaskPaginationRecordVO.setAgentOperationTaskName(agentOperationTaskDO.getTaskName());
            agentOperationTaskPaginationRecordVO.setAgentOperationTaskStatus(agentOperationTaskDO.getTaskStatus());
            agentOperationTaskPaginationRecordVO.setAgentOperationTaskType(agentOperationTaskDO.getTaskType());
            agentOperationTaskPaginationRecordVO.setOperator(agentOperationTaskDO.getOperator());
            agentOperationTaskPaginationRecordVO.setRelationHostCount(agentOperationTaskDO.getHostsNumber());
            //set failed & successful
            Integer failed = 0, successful = 0;
            Map<String, AgentOperationTaskSubStateEnum> taskExecuteResultMap = agentOperationTaskManageService.getTaskResultByExternalTaskId(agentOperationTaskDO.getExternalAgentTaskId());
            for (AgentOperationTaskSubStateEnum agentOperationTaskSubStateEnum : taskExecuteResultMap.values()) {
                switch (agentOperationTaskSubStateEnum) {
                    case TIMEOUT:
                    case FAILED:
                        failed++;
                        break;
                    case SUCCEED:
                        successful++;
                        break;
                }
            }
            agentOperationTaskPaginationRecordVO.setFailed(failed);
            agentOperationTaskPaginationRecordVO.setSuccessful(successful);
            result.add(agentOperationTaskPaginationRecordVO);
        }
        return result;
    }

    private AgentOperationTaskPaginationQueryConditionDO agentOperationTaskPaginationRequestDTO2AgentOperationTaskPaginationQueryConditionDO(AgentOperationTaskPaginationRequestDTO dto) {
        AgentOperationTaskPaginationQueryConditionDO agentOperationTaskPaginationQueryConditionDO = new AgentOperationTaskPaginationQueryConditionDO();
        if (StringUtils.isNotBlank(dto.getAgentOperationTaskName())) {
            agentOperationTaskPaginationQueryConditionDO.setTaskName(dto.getAgentOperationTaskName().replace("_", "\\_").replace("%", "\\%"));
        }
        if (null != dto.getAgentOperationTaskId()) {
            agentOperationTaskPaginationQueryConditionDO.setId(dto.getAgentOperationTaskId());
        }
        if (CollectionUtils.isNotEmpty(dto.getAgentOperationTaskStatusList())) {
            agentOperationTaskPaginationQueryConditionDO.setTaskStatusList(dto.getAgentOperationTaskStatusList());
        }
        if (CollectionUtils.isNotEmpty(dto.getAgentOperationTaskTypeList())) {
            agentOperationTaskPaginationQueryConditionDO.setTaskTypeList(dto.getAgentOperationTaskTypeList());
        }
        if (null != dto.getAgentOperationTaskStartTimeEnd()) {
            agentOperationTaskPaginationQueryConditionDO.setTaskStartTimeEnd(new Date(dto.getAgentOperationTaskStartTimeEnd()));
        }
        if (null != dto.getAgentOperationTaskStartTimeStart()) {
            agentOperationTaskPaginationQueryConditionDO.setTaskStartTimeStart(new Date(dto.getAgentOperationTaskStartTimeStart()));
        }
        agentOperationTaskPaginationQueryConditionDO.setLimitFrom(dto.getLimitFrom());
        agentOperationTaskPaginationQueryConditionDO.setLimitSize(dto.getLimitSize());
        agentOperationTaskPaginationQueryConditionDO.setSortColumn(dto.getSortColumn());
        agentOperationTaskPaginationQueryConditionDO.setAsc(dto.getAsc());
        return agentOperationTaskPaginationQueryConditionDO;
    }

    @ApiOperation(value = "根据Agent操作任务 id 查询对应 Agent 操作任务详情", notes = "")
    @RequestMapping(value = "/{agentOperationTaskId}", method = RequestMethod.GET)
    @ResponseBody
    public Result<AgentOperationTaskDetailVO> getAgentOperationTaskDetail(@PathVariable Long agentOperationTaskId) {
        AgentOperationTaskDetailVO agentOperationTaskDetailVO = new AgentOperationTaskDetailVO();
        AgentOperationTaskDO agentOperationTaskDO = agentOperationTaskManageService.getById(agentOperationTaskId);
        if (null == agentOperationTaskDO) {
            return Result.build(
                    ErrorCodeEnum.AGENT_OPERATION_TASK_NOT_EXISTS.getCode(),
                    String.format("AgentOperationTask={id=%d}在系统中不存在", agentOperationTaskId)
            );
        }
        if (null != agentOperationTaskDO.getTaskEndTime()) {
            agentOperationTaskDetailVO.setAgentOperationTaskEndTime(agentOperationTaskDO.getTaskEndTime().getTime());
        }
        if (null != agentOperationTaskDO.getTaskStartTime()) {
            agentOperationTaskDetailVO.setAgentOperationTaskStartTime(agentOperationTaskDO.getTaskStartTime().getTime());
        }
        agentOperationTaskDetailVO.setAgentOperationTaskId(agentOperationTaskDO.getId());
        agentOperationTaskDetailVO.setAgentOperationTaskName(agentOperationTaskDO.getTaskName());
        agentOperationTaskDetailVO.setAgentOperationTaskStatus(agentOperationTaskDO.getTaskStatus());
        agentOperationTaskDetailVO.setAgentOperationTaskType(agentOperationTaskDO.getTaskType());
        agentOperationTaskDetailVO.setOperator(agentOperationTaskDO.getOperator());
        agentOperationTaskDetailVO.setRelationHostCount(agentOperationTaskDO.getHostsNumber());
        //set agent version
        if (!AgentOperationTaskTypeEnum.UNINSTALL.getCode().equals(agentOperationTaskDO.getTaskType())) {//卸载 case，无须版本号信息
            Long agentVersionId = agentOperationTaskDO.getTargetAgentVersionId();
            if (null == agentVersionId) {
                return Result.build(
                        ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode(),
                        String.format("AgentOperationTask={%s}对应agentVersionId字段值为空", JSON.toJSONString(agentOperationTaskDO))
                );
            }
            AgentVersionDO agentVersionDO = agentVersionManageService.getById(agentVersionId);
            if (null == agentVersionDO) {
                return Result.build(
                        ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode(),
                        String.format("AgentOperationTask={%s}对应AgentVersion={id=%d}在系统不存在", JSON.toJSONString(agentOperationTaskDO), agentVersionId)
                );
            }
            agentOperationTaskDetailVO.setAgentVersion(agentVersionDO.getVersion());
        }
        //set agentOperationTaskExecuteDetailListVO
        AgentOperationTaskExecuteDetailListVO agentOperationTaskExecuteDetailListVO = new AgentOperationTaskExecuteDetailListVO();
        Integer failed = 0, successful = 0, execution = 0, pending = 0;
        Map<String, AgentOperationTaskSubStateEnum> taskExecuteResultMap = agentOperationTaskManageService.getTaskResultByExternalTaskId(agentOperationTaskDO.getExternalAgentTaskId());
        List<AgentOperationTaskExecuteDetailVO> agentOperationTaskExecuteDetailVOList = new ArrayList<>();

        List<AgentOperationSubTaskDO> agentOperationSubTaskDOList = agentOperationSubTaskManageService.getByAgentOperationTaskId(agentOperationTaskId);
        Map<String, AgentOperationSubTaskDO> hostName2AgentOperationSubTaskDOMap = new HashMap<>();
        for (AgentOperationSubTaskDO agentOperationSubTaskDO : agentOperationSubTaskDOList) {
//            hostName2AgentOperationSubTaskDOMap.put(agentOperationSubTaskDO.getHostName(), agentOperationSubTaskDO);
            //TODO：电科院场景 hostname 2 ip
            hostName2AgentOperationSubTaskDOMap.put(agentOperationSubTaskDO.getIp(), agentOperationSubTaskDO);
        }

        for (Map.Entry<String, AgentOperationTaskSubStateEnum> entry : taskExecuteResultMap.entrySet()) {
            String hostName = entry.getKey();
            AgentOperationTaskSubStateEnum agentOperationTaskSubStateEnum = entry.getValue();
            AgentOperationSubTaskDO agentOperationSubTaskDO = hostName2AgentOperationSubTaskDOMap.get(hostName);
            if (null == agentOperationSubTaskDO) {
                return Result.build(
                        ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode(),
                        String.format("hostName={%s}在系统中不存在对应agent操作信息", hostName)
                );
            }
            //set agentOperationTaskExecuteDetailVO
            AgentOperationTaskExecuteDetailVO agentOperationTaskExecuteDetailVO = new AgentOperationTaskExecuteDetailVO();
            agentOperationTaskExecuteDetailVO.setContainer(agentOperationSubTaskDO.getContainer());
            agentOperationTaskExecuteDetailVO.setExecuteStatus(agentOperationTaskSubStateEnum.getCode());
            agentOperationTaskExecuteDetailVO.setHostName(hostName);
            agentOperationTaskExecuteDetailVO.setIp(agentOperationSubTaskDO.getIp());
            if (!agentOperationTaskDO.getTaskType().equals(AgentOperationTaskTypeEnum.INSTALL.getCode())) {
                Long sourceAgentVersionId = agentOperationSubTaskDO.getSourceAgentVersionId();
                AgentVersionDO agentVersionDO = agentVersionManageService.getById(sourceAgentVersionId);
                if (null == agentVersionDO) {
                    return Result.build(
                            ErrorCodeEnum.AGENT_VERSION_NOT_EXISTS.getCode(),
                            String.format("AgentVersion={id=%d}在系统中不存在", sourceAgentVersionId)
                    );
                }
                agentOperationTaskExecuteDetailVO.setSourceAgentVersion(agentVersionDO.getVersion());
            }

            if (null != agentOperationSubTaskDO.getTaskEndTime()) {
                agentOperationTaskExecuteDetailVO.setExecuteEndTime(agentOperationSubTaskDO.getTaskEndTime().getTime());
            }
            if (null != agentOperationSubTaskDO.getTaskStartTime()) {
                agentOperationTaskExecuteDetailVO.setExecuteStartTime(agentOperationSubTaskDO.getTaskStartTime().getTime());
            }
            //calculate failed successful execution pending
            switch (agentOperationTaskSubStateEnum) {
                case TIMEOUT:
                    failed++;
                    break;
                case FAILED:
                    failed++;
                    break;
                case SUCCEED:
                    successful++;
                    break;
                case WAITING:
                    pending++;
                    break;
                case IGNORED:
                    successful++;
                    break;
                case KILLING:
                    execution++;
                    break;
                case RUNNING:
                    execution++;
                    break;
                case KILL_FAILED:
                    failed++;
                    break;
                case CANCELED:
                    successful++;
                    break;
            }
            agentOperationTaskExecuteDetailVOList.add(agentOperationTaskExecuteDetailVO);
        }
        agentOperationTaskExecuteDetailListVO.setTotal(taskExecuteResultMap.size());
        agentOperationTaskExecuteDetailListVO.setExecution(execution);
        agentOperationTaskExecuteDetailListVO.setFailed(failed);
        agentOperationTaskExecuteDetailListVO.setPending(pending);
        agentOperationTaskExecuteDetailListVO.setSuccessful(successful);
        agentOperationTaskExecuteDetailListVO.setAgentOperationTaskExecuteDetailVOList(agentOperationTaskExecuteDetailVOList);
        agentOperationTaskDetailVO.setAgentOperationTaskExecuteDetailListVO(agentOperationTaskExecuteDetailListVO);
        return Result.buildSucc(agentOperationTaskDetailVO);
    }

    @ApiOperation(value = "查询Agent操作任务在指定主机的执行日志", notes = "")
    @RequestMapping(value = "/{agentOperationTaskId}/{hostName}", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getAgentOperationTaskExecuteLog(@PathVariable Long agentOperationTaskId, @PathVariable String hostName) {
        //TODO：电科院场景：ip 取代 主机名
//        HostDO hostDO = hostManageService.getHostByHostName(hostName);
//        if(null == hostDO) {
//            return Result.buildFail("Host={hostname=%s}在系统中不存在");
//        } else {
//            return Result.buildSucc(agentOperationTaskManageService.getTaskLog(agentOperationTaskId, hostDO.getIp()), ResultTypeEnum.SUCCESS.getMessage());
//        }

        return Result.buildSucc(agentOperationTaskManageService.getTaskLog(agentOperationTaskId, hostName), ResultTypeEnum.SUCCESS.getMessage());

    }

}
