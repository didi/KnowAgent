package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.DashBoardVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.FileLogCollectPathManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Normal-Dashboard 相关接口")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "dashboard")
public class DashboardController {

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
    private AgentMetricsManageService agentMetricsManageService;

    @ApiOperation(value = "获取dashboard全量指标", notes = "")
    @RequestMapping(value = "/{startTime}/{endTime}", method = RequestMethod.GET)
    @ResponseBody
    public Result<DashBoardVO> dashboard(@PathVariable Long startTime, @PathVariable Long endTime) {

        if (null == startTime) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参startTime不可为空");
        }
        if (null == endTime) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参endTime不可为空");
        }

        DashBoardVO dashBoardVO = new DashBoardVO();

        /*********************** part 1：标量 ***********************/
        dashBoardVO.setLogCollectTaskNum(logCollectTaskManageService.countAll());
        dashBoardVO.setLogCollectPathNum(fileLogCollectPathManageService.countAll());
        dashBoardVO.setServiceNum(serviceManageService.countAll());
        dashBoardVO.setHostNum(hostManageService.countAllHost());
        dashBoardVO.setContainerNum(hostManageService.countAllContainer());
        dashBoardVO.setAgentNum(agentManageService.countAll());
        List<Long> logCollectTaskIdList = logCollectTaskManageService.getAllIds();
        Long nonRelateAnyHostLogCollectTaskNum = 0L;
        for (Long logCollectTaskId : logCollectTaskIdList) {
            if(logCollectTaskManageService.checkNotRelateAnyHost(logCollectTaskId)) {
                nonRelateAnyHostLogCollectTaskNum++;
            }
        }
        dashBoardVO.setNonRelateAnyHostLogCollectTaskNum(nonRelateAnyHostLogCollectTaskNum);
        List<String> agentHostNameList = agentManageService.getAllHostNames();
        Long nonRelateAnyLogCollectTaskAgentNum = 0L;
        for (String hostName : agentHostNameList) {
            if(agentManageService.checkAgentNotRelateAnyLogCollectTask(hostName)) {
                nonRelateAnyLogCollectTaskAgentNum++;
            }
        }
        dashBoardVO.setNonRelateAnyLogCollectTaskAgentNum(nonRelateAnyLogCollectTaskAgentNum);
        //TODO：当日采集量
//        agentMetricsManageService.getLogCollectTaskLogsBytesPerMinMetric();
//        dashBoardVO.setCollectBytesDay();
        //TODO：当前采集量
//        dashBoardVO.setCurrentCollectBytes();
//        //TODO：当日采集条数
//        dashBoardVO.setCollectLogEventsDay();
//        //TODO：当前采集条数
//        dashBoardVO.setCurrentCollectLogEvents();

        /*********************** part 2：占比 饼图 ***********************/

        /*********************** part 3：时序 图 指标 ***********************/









        //TODO：

        return Result.buildSucc(dashBoardVO);

    }

}
