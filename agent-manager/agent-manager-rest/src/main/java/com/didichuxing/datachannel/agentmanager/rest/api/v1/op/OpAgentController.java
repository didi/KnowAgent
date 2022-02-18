package com.didichuxing.datachannel.agentmanager.rest.api.v1.op;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.agent.AgentUpdateDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.AgentVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.common.util.SpringTool;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Api(tags = "OP-Agent维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX + "agent")
public class OpAgentController {

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @ApiOperation(value = "获取系统全量Agent信息", notes = "")
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<AgentVO>> getAllAgents() {
        List<AgentDO> agentDOList = agentManageService.list();
        List<AgentVO> agentVOList = ConvertUtil.list2List(agentDOList, AgentVO.class);
        return Result.buildSucc(agentVOList);
    }

    @ApiOperation(value = "修改Agent信息", notes = "")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    // @CheckPermission(permission = AGENT_MACHINE_EDIT)
    public Result updateAgent(@RequestBody AgentUpdateDTO dto) {
        AgentDO agentDO = ConvertUtil.obj2Obj(dto, AgentDO.class);
        agentManageService.updateAgent(agentDO, SpringTool.getUserName());
        return Result.buildSucc();
    }

    @ApiOperation(value = "校验给定agentIdList对应各agent是否存在日志采集任务，true：存在 false：不存在", notes = "")
    @RequestMapping(value = "/collect-task-exists", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> logCollectTaskExists(@RequestParam String agentIdListJsonString) {
        List<Integer> agentIdList = JSON.parseObject(agentIdListJsonString, List.class);
        for (Integer agentId : agentIdList) {
            List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageService.getLogCollectTaskListByAgentId(agentId.longValue());
            if (CollectionUtils.isNotEmpty(logCollectTaskDOList)) {//agent存在关联日志采集任务
                return Result.buildSucc(Boolean.TRUE);
            } else {
                continue;
            }
        }
        return Result.buildSucc(Boolean.FALSE);
    }

}
