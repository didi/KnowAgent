package com.didichuxing.datachannel.agentmanager.rest.api.v1.rd;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web.ListFilesDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.AgentVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.core.agent.health.AgentHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.version.AgentVersionManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Rd-Agent维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX + "agent")
public class RdAgentController {

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private AgentVersionManageService agentVersionManageService;

    @Autowired
    private AgentHealthManageService agentHealthManageService;

    @ApiOperation(value = "根据id获取Agent对象信息", notes = "")
    @RequestMapping(value = "/{agentId}", method = RequestMethod.GET)
    @ResponseBody
    public Result<AgentVO> getById(@PathVariable Long agentId) {
        AgentDO agentDO = agentManageService.getById(agentId);
        if (null == agentDO) {
            return Result.buildSucc(null);
        } else {
            return Result.buildSucc(getByAgentDO(agentDO));
        }
    }

    @ApiOperation(value = "根据hostname获取Agent对象信息", notes = "")
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public Result<AgentVO> getByHostName(@RequestParam(value = "hostName") String hostName) {
        AgentDO agentDO = agentManageService.getAgentByHostName(hostName);
        if (null == agentDO) {
            return Result.buildSucc(null);
        } else {
            return Result.buildSucc(getByAgentDO(agentDO));
        }
    }

    private AgentVO getByAgentDO(AgentDO agentDO) {
        AgentVersionDO agentVersionDO = agentVersionManageService.getById(agentDO.getAgentVersionId());
        if (null == agentVersionDO) {
            throw new ServiceException(
                    String.format("Agent对象={agentId={%d}}对应AgentVerison对象={agentVersionId={%d}}在系统中不存在", agentDO.getId(), agentDO.getAgentVersionId()),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        AgentVO agentVO = ConvertUtil.obj2Obj(agentDO, AgentVO.class);
        agentVO.setVersion(agentVersionDO.getVersion());
        AgentHealthDO agentHealthDO = agentHealthManageService.getByAgentId(agentDO.getId());
        if (null == agentHealthDO) {
            throw new ServiceException(
                    String.format("AgentHealth={agentId=%d}在系统中不存在", agentDO.getId()),
                    ErrorCodeEnum.AGENT_HEALTH_NOT_EXISTS.getCode()
            );
        } else {
            agentVO.setHealthLevel(agentHealthDO.getAgentHealthLevel());
            agentVO.setAgentHealthDescription(agentHealthDO.getAgentHealthDescription());
        }
        return agentVO;
    }

    @ApiOperation(value = "根据给定路径 & 文件匹配正则获取匹配到的文件列表集", notes = "")
    @RequestMapping(value = "/path", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<String>> listFiles(
            @RequestBody ListFilesDTO listFilesDTO
    ) {
        return agentManageService.listFiles(listFilesDTO.getHostName(), listFilesDTO.getPath(), listFilesDTO.getSuffixRegular());
    }

}
