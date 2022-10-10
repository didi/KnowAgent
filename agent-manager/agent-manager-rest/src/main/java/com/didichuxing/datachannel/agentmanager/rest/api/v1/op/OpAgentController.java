package com.didichuxing.datachannel.agentmanager.rest.api.v1.op;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.agent.AgentUpdateDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.AgentVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask.AdvancedConfigItem;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask.AdvancedConfigTips;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.common.util.SpringTool;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Api(tags = "OP-Agent维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX + "agent")
public class OpAgentController {

    private static final String AGENT_ADVANCED_CONFIG_TIPS_SUMMARY = "高级配置项采用 json 格式，配置样例：\n" +
            "{\n" +
            "    “agentLimitStartThreshold”: 20000000,\n" +
            "    “agentLimitMinThreshold”: 1000000\n" +
            "}";
    private static final List<AdvancedConfigItem> AGENT_ADVANCED_CONFIG_ITEM_ARRAY_LIST = new ArrayList<>();

    static {
        loadAgentAdvancedConfigItemList();
    }

    private static void loadAgentAdvancedConfigItemList() {
        AGENT_ADVANCED_CONFIG_ITEM_ARRAY_LIST.add(
                new AdvancedConfigItem("agentLimitStartThreshold", "agent限流初始阈值。单位：byte，类型：Long", "20000000")
        );
        AGENT_ADVANCED_CONFIG_ITEM_ARRAY_LIST.add(
                new AdvancedConfigItem("agentLimitMinThreshold", "agent限流最小阈值。单位：byte，类型：Long", "1000000")
        );
    }

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

    @ApiOperation(value = "批量删除 agent 0：删除成功 10000：参数错误 22000：Agent 不存在 22001：Agent存在未采集完的日志", notes = "")
    @RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
    @ResponseBody
    public Result deleteAgents(@PathVariable String ids) {
        String[] idArray = ids.split(",");
        if(null != idArray && idArray.length != 0) {
            List<Long> agentIdList = new ArrayList<>(idArray.length);
            for (String id : idArray) {
                agentIdList.add(Long.valueOf(id));
            }
            agentManageService.deleteAgentByIds(agentIdList, true, true, SpringTool.getUserName());
        }
        return Result.buildSucc();
    }

    @ApiOperation(value = " Agent 高级配置提示信息", notes = "")
    @RequestMapping(value = "/advanced-config/tips", method = RequestMethod.GET)
    @ResponseBody
    public Result<AdvancedConfigTips> getLogCollectTaskAdvancedConfigTips() {
        AdvancedConfigTips advancedConfigTips = new AdvancedConfigTips();
        advancedConfigTips.setSummary(AGENT_ADVANCED_CONFIG_TIPS_SUMMARY);
        advancedConfigTips.setAdvancedConfigItemList(AGENT_ADVANCED_CONFIG_ITEM_ARRAY_LIST);
        return Result.buildSucc(advancedConfigTips);
    }

}
