package com.didichuxing.datachannel.agentmanager.rest.api.v1.rd;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.version.AgentVersionDropdownVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.core.agent.version.AgentVersionManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Api(tags = "Rd-AgentVersion维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX + "version")
public class RdAgentVersionController {

    @Autowired
    private AgentVersionManageService agentVersionManageService;

    @ApiOperation(value = "系统全量Agent版本号列表", notes = "")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    // @CheckPermission(permission = AGENT_VERSION_LIST)
    public Result<List<AgentVersionDropdownVO>> listAgentVersions() {
        List<AgentVersionDO> agentVersionDOList = agentVersionManageService.list();
        List<AgentVersionDropdownVO> agentVersionDropdownVOList = new ArrayList<>(agentVersionDOList.size());
        for (AgentVersionDO agentVersionDO : agentVersionDOList) {
            AgentVersionDropdownVO agentVersionDropdownVO = new AgentVersionDropdownVO();
            agentVersionDropdownVO.setAgentVersion(agentVersionDO.getVersion());
            agentVersionDropdownVO.setAgentVersionId(agentVersionDO.getId());
            agentVersionDropdownVOList.add(agentVersionDropdownVO);
        }
        return Result.buildSucc(agentVersionDropdownVOList);
    }

}
