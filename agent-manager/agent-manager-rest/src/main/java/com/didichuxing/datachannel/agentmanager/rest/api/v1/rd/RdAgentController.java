package com.didichuxing.datachannel.agentmanager.rest.api.v1.rd;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.AgentVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanelGroup;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
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

    @ApiOperation(value = "根据给定Agent对象id，获取给定时间范围（startTime ~ endTime）内的Agent运行指标集", notes = "")
    @RequestMapping(value = "/{agentId}/metrics/{startTime}/{endTime}", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<MetricPanelGroup>> listAgentMetrics(@PathVariable Long agentId, @PathVariable Long startTime, @PathVariable Long endTime) {
        return Result.buildSucc(agentManageService.listAgentMetrics(agentId, startTime, endTime));
    }

    @ApiOperation(value = "根据id获取Agent对象信息", notes = "")
    @RequestMapping(value = "/{agentId}", method = RequestMethod.GET)
    @ResponseBody
    public Result<AgentVO> getById(@PathVariable Long agentId) {
        AgentDO agentDO = agentManageService.getById(agentId);
        if (null == agentDO) {
            return Result.buildSucc(null);
        } else {
            AgentVersionDO agentVersionDO = agentVersionManageService.getById(agentDO.getAgentVersionId());
            if (null == agentVersionDO) {
                return Result.build(
                        ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode(),
                        String.format("Agent对象={agentId={%d}}对应AgentVerison对象={agentVersionId={%d}}在系统中不存在", agentId, agentDO.getAgentVersionId())
                );
            }
            AgentVO agentVO = ConvertUtil.obj2Obj(agentDO, AgentVO.class);
            agentVO.setVersion(agentVersionDO.getVersion());
            return Result.buildSucc(agentVO);
        }
    }

    @ApiOperation(value = "根据给定路径 & 文件匹配正则获取匹配到的文件列表集", notes = "")
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<String>> listFiles(
            @RequestParam (value = "path") String path,
            @RequestParam (value = "suffixMatchRegular") String suffixMatchRegular,
            @RequestParam (value = "hostName") String hostName
    ) {
        return Result.buildSucc(agentManageService.listFiles(hostName, path, suffixMatchRegular));
    }

}
