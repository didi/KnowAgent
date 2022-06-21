package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsAgentPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.health.AgentHealthErrorDetailVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.agentmanager.core.agent.health.AgentHealthManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "Normal-AgentHealth相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "agent/health")
public class NormalAgentHealthController {

    @Autowired
    private AgentHealthManageService agentHealthManageService;

    @ApiOperation(value = "根据给定主机名获取导致 agent 健康状态为 AGENT_ERROR_LOGS_EXISTS 的错误信息详情列表", notes = "")
    @RequestMapping(value = "/error-detail", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<AgentHealthErrorDetailVO>> getErrorDetails(@RequestParam String hostName) {
        List<MetricsAgentPO> metricsAgentPOList = agentHealthManageService.getErrorDetails(hostName);
        return Result.buildSucc(convert2AgentHealthErrorDetailVOList(metricsAgentPOList));
    }

    @ApiOperation(value = "根据给定 agent 业务指标 id 与 agent 健康度巡检状态码更新对应日志采集任务的健康度offset", notes = "")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public Result solveErrorDetail(@RequestParam Long agentMetricId) {
        agentHealthManageService.solveErrorDetail(agentMetricId);
        return Result.buildSucc();
    }

    private List<AgentHealthErrorDetailVO> convert2AgentHealthErrorDetailVOList(List<MetricsAgentPO> metricsAgentPOList) {
        List<AgentHealthErrorDetailVO> agentHealthErrorDetailVOList = new ArrayList<>();
        for (MetricsAgentPO metricsAgentPO: metricsAgentPOList) {
            AgentHealthErrorDetailVO agentHealthErrorDetailVO = new AgentHealthErrorDetailVO();
            agentHealthErrorDetailVO.setAgentMetricId(metricsAgentPO.getId());
            agentHealthErrorDetailVO.setErrorLogsCount(metricsAgentPO.getErrorlogscount());
            agentHealthErrorDetailVO.setErrorLogs(agentHealthManageService.getErrorLogsInHeartbeatScope(metricsAgentPO.getHostname(), metricsAgentPO.getHeartbeattime()));
            agentHealthErrorDetailVO.setHostName(metricsAgentPO.getHostname());
            agentHealthErrorDetailVO.setHeartbeatTime(DateUtils.getDateTimeStr(metricsAgentPO.getHeartbeattime()));
            agentHealthErrorDetailVOList.add(agentHealthErrorDetailVO);
        }
        return agentHealthErrorDetailVOList;
    }

}
