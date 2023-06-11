package com.didichuxing.datachannel.agentmanager.rest.api.v1.rd;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.PaginationResult;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostAgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.DirectoryLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.host.HostPaginationRequestDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsProcessPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.host.HostAgentVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.service.ServiceVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskStatusEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.common.util.NetworkUtil;
import com.didichuxing.datachannel.agentmanager.core.agent.health.AgentHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.version.AgentVersionManageService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.DirectoryLogCollectPathManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.FileLogCollectPathManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(tags = "Rd-Host维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX + "host")
public class RdHostController {

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private ServiceManageService serviceManageService;

    @Autowired
    private AgentVersionManageService agentVersionManageService;

    @Autowired
    private AgentHealthManageService agentHealthManageService;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private FileLogCollectPathManageService fileLogCollectPathManageService;

    @Autowired
    private DirectoryLogCollectPathManageService directoryLogCollectPathManageService;

    @Autowired
    private MetricsManageService metricsManageService;

    @ApiOperation(value = "测试主机名连通性", notes = "")
    @RequestMapping(value = "/connectivity/{hostname}", method = RequestMethod.GET)
    @ResponseBody
    public Result connect(@PathVariable String hostname) {
        boolean result = NetworkUtil.ping(hostname);
        return result ? Result.buildSucc() : Result.buildFail();
    }

    @ApiOperation(value = "查询主机&Agent列表", notes = "")
    @RequestMapping(value = "/paging", method = RequestMethod.POST)
    @ResponseBody
    public Result<PaginationResult<HostAgentVO>> listHostsAndAgents(@RequestBody HostPaginationRequestDTO dto) {
        HostPaginationQueryConditionDO hostPaginationQueryConditionDO = hostPaginationRequestDTO2HostPaginationQueryConditionDO(dto);
        List<HostAgentDO> hostAgentDOList = hostManageService.paginationQueryByConditon(hostPaginationQueryConditionDO);
        List<HostAgentVO> resultSet = hostAgentDOList2HostAgentVOList(hostAgentDOList);
        PaginationResult<HostAgentVO> paginationResult = new PaginationResult<>(resultSet, hostManageService.queryCountByCondition(hostPaginationQueryConditionDO), dto.getPageNo(), dto.getPageSize());
        return Result.buildSucc(paginationResult);
    }

    private List<HostAgentVO> hostAgentDOList2HostAgentVOList(List<HostAgentDO> hostAgentDOList) throws ServiceException {
        List<HostAgentVO> resultSet = new ArrayList<>(hostAgentDOList.size());
        for (HostAgentDO hostAgentDO : hostAgentDOList) {
            HostAgentVO hostAgentVO = new HostAgentVO();
            hostAgentVO.setHostName(hostAgentDO.getHostName());
            hostAgentVO.setHostId(hostAgentDO.getHostId());
            hostAgentVO.setIp(hostAgentDO.getHostIp());
            hostAgentVO.setContainer(hostAgentDO.getHostType());
            //set servicevo list
            List<ServiceDO> serviceDOList = serviceManageService.getServicesByHostId(hostAgentDO.getHostId());
            List<ServiceVO> serviceVOList = ConvertUtil.list2List(serviceDOList, ServiceVO.class);
            hostAgentVO.setServiceList(serviceVOList);
            //set agent version
            if (hostAgentDO.getAgentVersionId() != null) {
                AgentVersionDO agentVersionDO = agentVersionManageService.getById(hostAgentDO.getAgentVersionId());
                hostAgentVO.setAgentVersion(agentVersionDO.getVersion());
            }
            hostAgentVO.setAgentHealthLevel(hostAgentDO.getAgentHealthLevel());
            hostAgentVO.setAgentHealthDescription(hostAgentDO.getAgentHealthDescription());
            hostAgentVO.setMachineZone(hostAgentDO.getHostMachineZone());
            hostAgentVO.setHostCreateTime(hostAgentDO.getHostCreateTime().getTime());
            hostAgentVO.setAgentId(hostAgentDO.getAgentId());
            hostAgentVO.setParentHostName(hostAgentDO.getParentHostName());
            hostAgentVO.setAgentHealthInspectionResultType(hostAgentDO.getAgentHealthInspectionResultType());
            resultSet.add(hostAgentVO);
        }
        return resultSet;
    }

    /**
     * 将HostPaginationRequestDTO对象转化为HostPaginationQueryConditionDO对象并返回
     *
     * @param dto 待转化HostPaginationRequestDTO对象
     * @return 返回将HostPaginationRequestDTO对象转化为的HostPaginationQueryConditionDO对象
     */
    private HostPaginationQueryConditionDO hostPaginationRequestDTO2HostPaginationQueryConditionDO(HostPaginationRequestDTO dto) {
        HostPaginationQueryConditionDO hostPaginationQueryConditionDO = new HostPaginationQueryConditionDO();
        hostPaginationQueryConditionDO.setLimitSize(dto.getLimitSize());
        hostPaginationQueryConditionDO.setLimitFrom(dto.getLimitFrom());
        if (StringUtils.isNotBlank(dto.getIp())) {
            hostPaginationQueryConditionDO.setIp(dto.getIp().replace("_", "\\_").replace("%", "\\%"));
        }
        if (StringUtils.isNotBlank(dto.getHostName())) {
            hostPaginationQueryConditionDO.setHostName(dto.getHostName().replace("_", "\\_").replace("%", "\\%"));
        }
        if (CollectionUtils.isNotEmpty(dto.getServiceIdList())) {
            hostPaginationQueryConditionDO.setServiceIdList(dto.getServiceIdList());
        }
        if (CollectionUtils.isNotEmpty(dto.getContainerList())) {
            hostPaginationQueryConditionDO.setContainerList(dto.getContainerList());
        }
        if (CollectionUtils.isNotEmpty(dto.getAgentVersionIdList())) {
            hostPaginationQueryConditionDO.setAgentVersionIdList(dto.getAgentVersionIdList());
        }
        if (CollectionUtils.isNotEmpty(dto.getAgentHealthLevelList())) {
            hostPaginationQueryConditionDO.setAgentHealthLevelList(dto.getAgentHealthLevelList());
        }
        if(CollectionUtils.isNotEmpty(dto.getMachineZoneList())) {
            hostPaginationQueryConditionDO.setMachineZoneList(dto.getMachineZoneList());
        }
        if (null != dto.getHostCreateTimeEnd()) {
            hostPaginationQueryConditionDO.setCreateTimeEnd(new Date(dto.getHostCreateTimeEnd()));
        }
        if (null != dto.getHostCreateTimeStart()) {
            hostPaginationQueryConditionDO.setCreateTimeStart(new Date(dto.getHostCreateTimeStart()));
        }
        if(StringUtils.isNotBlank(dto.getQueryTerm())) {
            hostPaginationQueryConditionDO.setQueryTerm(dto.getQueryTerm());
        }
        hostPaginationQueryConditionDO.setSortColumn(dto.getSortColumn());
        hostPaginationQueryConditionDO.setAsc(dto.getAsc());
        return hostPaginationQueryConditionDO;
    }

    @ApiOperation(value = "根据host对象id获取Host&Agent对象信息", notes = "")
    @RequestMapping(value = "/{hostId}", method = RequestMethod.GET)
    @ResponseBody
    public Result<HostAgentVO> getHostAndAgentByHostId(@PathVariable Long hostId) {
        HostDO hostDO = hostManageService.getById(hostId);
        if (null == hostDO) {
            return Result.buildSucc(null);
        } else {
            HostAgentVO hostAgentVO = getHostAndAgentVOByHostDO(hostDO);
            return Result.buildSucc(hostAgentVO);
        }
    }

    @ApiOperation(value = "根据hostname获取Host&Agent对象信息", notes = "")
    @RequestMapping(value = "/host-agent", method = RequestMethod.GET)
    @ResponseBody
    public Result<HostAgentVO> getHostAndAgentByHostName(@RequestParam(value = "hostName") String hostName) {
        HostDO hostDO = hostManageService.getHostByHostName(hostName);
        if (null == hostDO) {
            return Result.buildSucc(null);
        } else {
            HostAgentVO hostAgentVO = getHostAndAgentVOByHostDO(hostDO);
            return Result.buildSucc(hostAgentVO);
        }
    }

    /**
     * 根据host对象id获取Host&Agent对象信息
     *
     * @param hostDO host 对象
     * @return 返回根据host对象id获取到的Host&Agent对象信息
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private HostAgentVO getHostAndAgentVOByHostDO(HostDO hostDO) throws ServiceException {
            HostAgentVO hostAgentVO = new HostAgentVO();
            /*
             * 设置主机相关信息
             */
            hostAgentVO.setContainer(hostDO.getContainer());
            hostAgentVO.setDepartment(hostDO.getDepartment());
            hostAgentVO.setHostCreateTime(hostDO.getCreateTime().getTime());
            hostAgentVO.setHostId(hostDO.getId());
            hostAgentVO.setHostName(hostDO.getHostName());
            hostAgentVO.setIp(hostDO.getIp());
            hostAgentVO.setMachineZone(hostDO.getMachineZone());
            hostAgentVO.setParentHostName(hostDO.getParentHostName());

            /*
             * 设置主机关联服务相关信息
             */
            List<ServiceDO> serviceDOList = serviceManageService.getServicesByHostId(hostDO.getId());
            List<ServiceVO> serviceVOList = ConvertUtil.list2List(serviceDOList, ServiceVO.class);
            hostAgentVO.setServiceList(serviceVOList);
            /*
             * 设置Agent相关信息
             */
            AgentDO agentDO = agentManageService.getAgentByHostName(hostDO.getHostName());
            if (null != agentDO) {
                AgentHealthDO agentHealthDO = agentHealthManageService.getByAgentId(agentDO.getId());
                if (null == agentHealthDO) {
                    throw new ServiceException(
                            String.format("AgentHealth={agentId=%d}在系统中不存在", agentDO.getId()),
                            ErrorCodeEnum.AGENT_HEALTH_NOT_EXISTS.getCode()
                    );
                } else {
                    hostAgentVO.setAgentHealthLevel(agentHealthDO.getAgentHealthLevel());
                    hostAgentVO.setAgentHealthDescription(agentHealthDO.getAgentHealthDescription());
                }

                hostAgentVO.setAgentId(agentDO.getId());
                AgentVersionDO agentVersionDO = agentVersionManageService.getById(agentDO.getAgentVersionId());
                if (null == agentVersionDO) {
                    throw new ServiceException(
                            String.format("hostId={%d}对应主机上的agent对象={%s}对应agentVersion={agentVersionId={%d}}对象在系统中不存在", hostDO.getId(), JSON.toJSONString(agentDO), agentDO.getAgentVersionId()),
                            ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
                    );
                }
                hostAgentVO.setAgentVersion(agentVersionDO.getVersion());
                List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageService.getLogCollectTaskListByAgentId(agentDO.getId());
                Integer openedLogCollectTaskNum = 0;
                Integer openedLogPathNum = 0;
                for (LogCollectTaskDO logCollectTaskDO : logCollectTaskDOList) {
                    if(logCollectTaskDO.getLogCollectTaskStatus().equals(LogCollectTaskStatusEnum.RUNNING.getCode())) {
                        openedLogCollectTaskNum++;
                        List<FileLogCollectPathDO> fileLogCollectPathDOList = fileLogCollectPathManageService.getAllFileLogCollectPathByLogCollectTaskId(logCollectTaskDO.getId());
                        List<DirectoryLogCollectPathDO> directoryLogCollectPathDOList = directoryLogCollectPathManageService.getAllDirectoryLogCollectPathByLogCollectTaskId(logCollectTaskDO.getId());
                        openedLogPathNum = openedLogPathNum + fileLogCollectPathDOList.size() + directoryLogCollectPathDOList.size();
                    }
                }
                hostAgentVO.setOpenedLogCollectTaskNum(openedLogCollectTaskNum);
                hostAgentVO.setOpenedLogPathNum(openedLogPathNum);
                MetricsProcessPO lastMetricsProcess = metricsManageService.getLastProcessMetric(agentDO.getHostName());
                if(null != lastMetricsProcess) {
                    hostAgentVO.setLastestAgentStartupTime(lastMetricsProcess.getProcstartuptime());
                }
            }
            return hostAgentVO;
    }

}
