package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.DashBoardVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Normal-Dashboard 相关接口")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "dashboard")
public class DashboardController {

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

        //TODO：

        return Result.buildSucc(dashBoardVO);

    }

}
