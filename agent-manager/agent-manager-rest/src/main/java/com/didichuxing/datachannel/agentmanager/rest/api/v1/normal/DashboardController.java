package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.dashboard.DashboardRequestDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.dashboard.DashBoardVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.dashboard.DashboardMetricEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.FileLogCollectPathManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "Normal-Dashboard 相关接口")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "dashboard")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private FileLogCollectPathManageService fileLogCollectPathManageService;

    @Autowired
    private ServiceManageService serviceManageService;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private MetricsManageService metricsManageService;

    @ApiOperation(value = "获取dashboard全量指标", notes = "")
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public Result<DashBoardVO> dashboard(@PathVariable Long startTime, @PathVariable Long endTime) {

        if (null == startTime) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参startTime不可为空");
        }
        if (null == endTime) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参endTime不可为空");
        }

        DashBoardVO dashBoardVO = new DashBoardVO();









        /*********************** part 3：时序 图 指标 ***********************/

//        dashBoardVO.setLogCollectTaskListCollectBytesTop5(agentMetricsManageService.getLogCollectTaskListCollectBytesLastest1MinTop5(startTime, endTime));
//        dashBoardVO.setLogCollectTaskListCollectCountTop5(agentMetricsManageService.getLogCollectTaskListCollectCountLastest1MinTop5(startTime, endTime));
//        dashBoardVO.setLogCollectTaskListRelateHostsTop5(logCollectTaskManageService.getTop5HostCount(startTime, endTime));
//        dashBoardVO.setLogCollectTaskListRelateAgentsTop5(logCollectTaskManageService.getTop5AgentCount(startTime, endTime));
//
//        dashBoardVO.setAgentListCollectBytesTop5(agentMetricsManageService.getAgentListCollectBytesLastest1MinTop5(startTime, endTime));
//        dashBoardVO.setAgentListCollectCountTop5(agentMetricsManageService.getAgentListCollectCountLastest1MinTop5(startTime, endTime));
//        dashBoardVO.setAgentListCpuUsageTop5(agentMetricsManageService.getAgentListCpuUsageLastest1MinTop5(startTime, endTime));
//        dashBoardVO.setAgentListFdUsedTop5(agentMetricsManageService.getAgentListFdUsedLastest1MinTop5(startTime, endTime));
//        dashBoardVO.setAgentListMemoryUsageTop5(agentMetricsManageService.getAgentListMemoryUsedLastest1MinTop5(startTime, endTime));
//        dashBoardVO.setAgentListFullGcCountTop5(agentMetricsManageService.getAgentListFullGcCountLastest1MinTop5(startTime, endTime));
//        dashBoardVO.setAgentListRelateLogCollectTasksTop5(agentManageService.getTop5LogCollectTaskCount(startTime, endTime));

        return Result.buildSucc(dashBoardVO);

    }



}
