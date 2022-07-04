package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.GlobalProperties;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.dashboard.MaintenanceDashBoardVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.dashboard.OperatingDashBoardVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.core.dashboard.DashboardManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Normal-Dashboard 相关接口")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "dashboard")
public class DashboardController {

    private static final Logger LOGGER = LoggerFactory.getLogger("DashboardController");

    @Autowired
    private DashboardManageService dashboardManageService;

    @ApiOperation(value = "获取运营大盘全量指标", notes = "")
    @RequestMapping(value = "/operating", method = RequestMethod.GET)
    @ResponseBody
    public Result<OperatingDashBoardVO> operatingDashboard() {
        return Result.buildSucc(GlobalProperties.operatingDashBoardVO);
    }

    @ApiOperation(value = "获取运维大盘全量指标", notes = "")
    @RequestMapping(value = "/maintenance", method = RequestMethod.GET)
    @ResponseBody
    public Result<MaintenanceDashBoardVO> maintenanceDashboard() {
        return Result.buildSucc(GlobalProperties.maintenanceDashBoardVO);
    }

}
