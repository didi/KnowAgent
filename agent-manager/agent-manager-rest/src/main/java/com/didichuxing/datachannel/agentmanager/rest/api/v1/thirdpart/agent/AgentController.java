package com.didichuxing.datachannel.agentmanager.rest.api.v1.thirdpart.agent;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.agent.AgentRegisterDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config.AgentCollectConfigDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.util.SpringTool;
import com.didichuxing.datachannel.agentmanager.core.agent.configuration.AgentCollectConfigManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.version.AgentVersionManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Agent维度注册、采集配置拉取接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_PREFIX + "agent")
public class AgentController {

    @Autowired
    private AgentCollectConfigManageService agentCollectConfigManageService;

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private AgentVersionManageService agentVersionManageService;

    @ApiOperation(value = "获取Agent配置 & Agent 待采集日志采集任务集", notes = "")
    @RequestMapping(path = "/config", method = RequestMethod.GET)
    public Result<String> getAgentCollectConfiguration(@RequestParam String hostName) {
        AgentCollectConfigDO agentCollectConfigDO = agentCollectConfigManageService.getAgentConfigDOByHostName(hostName);
        return Result.buildSucc(JSON.toJSONString(agentCollectConfigDO));
    }

    @ApiOperation(value = "Agent注册", notes = "")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public Result registerAgent(@RequestBody AgentRegisterDTO dto) {
        String agentVersion = dto.getVersion();
        if(StringUtils.isBlank(agentVersion)) {
            return Result.build(
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    "入参Agent版本号字段[version]值不可为空"
            );
        }
        AgentVersionDO agentVersionDO = agentVersionManageService.getByVersion(dto.getVersion());
        if(null == agentVersionDO) {
            return Result.build(
                    ErrorCodeEnum.AGENT_VERSION_NOT_EXISTS.getCode(),
                    String.format("待注册Agent={%s}的版本号在系统中不存在，请联系管理员在系统中维护该Agent版本信息！", JSON.toJSONString(dto))
            );
        }
        AgentDO agentDO = agentRegisterDTO2AgentDO(dto, agentVersionDO);
        Long agentId = agentManageService.createAgent(agentDO, SpringTool.getUserName(), true);
        return Result.buildSucc(agentId);
    }

    /**
     * 将给定AgentRegisterDTO对象转化为AgentDO对象
     * @param dto 待转化AgentRegisterDTO对象
     * @param agentVersionDO
     * @return 返回将给定AgentRegisterDTO对象转化为AgentDO对象
     */
    private AgentDO agentRegisterDTO2AgentDO(AgentRegisterDTO dto, AgentVersionDO agentVersionDO) {
        AgentDO agentDO = new AgentDO();
        agentDO.setCollectType(dto.getCollectType());
        agentDO.setIp(dto.getIp());
        agentDO.setHostName(dto.getHostName());
        agentDO.setAgentVersionId(agentVersionDO.getId());
        agentDO.setCpuLimitThreshold(100);//cpu默认限流 1 cpu core
        return agentDO;
    }

}
