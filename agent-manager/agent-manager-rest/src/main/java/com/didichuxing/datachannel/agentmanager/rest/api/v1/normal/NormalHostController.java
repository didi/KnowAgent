package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.host.HostAgentVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.host.HostVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.service.ServiceVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.core.agent.health.AgentHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.version.AgentVersionManageService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "Normal-Host维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "host")
public class NormalHostController {

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private ServiceManageService serviceManageService;

    @Autowired
    private AgentVersionManageService agentVersionManageService;

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private AgentHealthManageService agentHealthManageService;

    @ApiOperation(value = "查询给定日志采集任务关联的主机信息", notes = "")
    @RequestMapping(value = "/collect-task/{logCollectTaskId}", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<HostAgentVO>> listHostsAndAgentsBylogCollectTaskId(@PathVariable Long logCollectTaskId) {
        List<HostDO> hostDOList = hostManageService.getHostListByLogCollectTaskId(logCollectTaskId);
        return Result.buildSucc(hostDOList2HostAgentVOList(hostDOList));
    }

    @ApiOperation(value = "查询全量主机列表", notes = "")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<HostVO>> listHosts() {
        List<HostDO> hostDOList = hostManageService.list();
        List<HostVO> hostVOList = new ArrayList<>(hostDOList.size());
        for (HostDO hostDO : hostDOList) {
            HostVO hostVO = ConvertUtil.obj2Obj(hostDO, HostVO.class);
            hostVO.setContainer(hostDO.getContainer());
            hostVOList.add(hostVO);
        }
        return Result.buildSucc(hostVOList);
    }

    @ApiOperation(value = "根据id获取Host对象信息", notes = "")
    @RequestMapping(value = "/{hostId}", method = RequestMethod.GET)
    @ResponseBody
    public Result<HostVO> getById(@PathVariable Long hostId) {
        HostDO hostDO = hostManageService.getById(hostId);
        if (null == hostDO) {
            return Result.buildSucc(null);
        } else {
            HostVO hostVO = ConvertUtil.obj2Obj(hostDO, HostVO.class);
            hostVO.setContainer(hostDO.getContainer());
            return Result.buildSucc(hostVO);
        }
    }

    @ApiOperation(value = "根据id获取Host对象信息", notes = "")
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public Result<HostVO> getByHostname(@RequestParam String hostname) {
        if (StringUtils.isBlank(hostname)) {
            return Result.buildFail("hostname为空");
        }
        HostDO hostDO = hostManageService.getHostByHostName(hostname);
        return Result.buildSucc(ConvertUtil.obj2Obj(hostDO, HostVO.class));
    }

    @ApiOperation(value = "获取系统中已存在的全量machineZone", notes = "")
    @RequestMapping(value = "/machine-zones", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<String>> listMachineZones() {
        return Result.buildSucc(hostManageService.getAllMachineZones());
    }

    private List<HostAgentVO> hostDOList2HostAgentVOList(List<HostDO> hostDOList) throws ServiceException {
        List<HostAgentVO> hostAgentVOList = new ArrayList<>(hostDOList.size());
        for (HostDO hostDO : hostDOList) {
            HostAgentVO hostAgentVO = new HostAgentVO();
            hostAgentVO.setHostCreateTime(hostDO.getCreateTime().getTime());
            hostAgentVO.setMachineZone(hostDO.getMachineZone());
            hostAgentVO.setContainer(hostDO.getContainer());
            hostAgentVO.setIp(hostDO.getIp());
            hostAgentVO.setHostId(hostDO.getId());
            hostAgentVO.setHostName(hostDO.getHostName());
            hostAgentVO.setParentHostName(hostDO.getParentHostName());
            hostAgentVO.setDepartment(hostDO.getDepartment());
            //set relation servicedo list
            List<ServiceDO> serviceDOList = serviceManageService.getServicesByHostId(hostDO.getId());
            List<ServiceVO> serviceVOList = ConvertUtil.list2List(serviceDOList, ServiceVO.class);
            hostAgentVO.setServiceList(serviceVOList);
            //set agent info
            AgentDO relationAgentDO = agentManageService.getAgentByHostName(hostDO.getHostName());
            if (null != relationAgentDO) {
                AgentHealthDO agentHealthDO = agentHealthManageService.getByAgentId(relationAgentDO.getId());
                if (null == agentHealthDO) {
                    throw new ServiceException(
                            String.format("AgentHealth={agentId=%d}在系统中不存在", relationAgentDO.getId()),
                            ErrorCodeEnum.AGENT_HEALTH_NOT_EXISTS.getCode()
                    );
                } else {
                    hostAgentVO.setAgentHealthLevel(agentHealthDO.getAgentHealthLevel());
                    hostAgentVO.setAgentHealthDescription(agentHealthDO.getAgentHealthDescription());
                }
                hostAgentVO.setAgentId(relationAgentDO.getId());
                AgentVersionDO agentVersionDO = agentVersionManageService.getById(relationAgentDO.getAgentVersionId());
                hostAgentVO.setAgentVersion(agentVersionDO.getVersion());
            }
            hostAgentVOList.add(hostAgentVO);
        }
        return hostAgentVOList;
    }

}
