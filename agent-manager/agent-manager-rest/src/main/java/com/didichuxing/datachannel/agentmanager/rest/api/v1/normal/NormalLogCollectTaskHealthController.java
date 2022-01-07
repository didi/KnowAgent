package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.health.LogCollectTaskHealthSolveErrorDetailDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask.health.LogCollectTaskHealthErrorDetailPathHostsVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask.health.LogCollectTaskHealthErrorDetailVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Normal-LogCollectTaskHealth相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "log-collect-task/health")
public class NormalLogCollectTaskHealthController {

    @ApiOperation(value = "根据给定日志采集任务id与日志采集任务健康度巡检状态码获取导致该状态的采集路径与主机集", notes = "")
    @RequestMapping(value = "/error-detail/path-host/{logCollectTaskId}/{logCollectTaskHealthInspectionCode}", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<LogCollectTaskHealthErrorDetailPathHostsVO>> getPathHosts(@PathVariable Long logCollectTaskId, @PathVariable Integer logCollectTaskHealthInspectionCode) {

        return null;
    }

    @ApiOperation(value = "根据给定日志采集任务id，采集路径id，主机名，日志采集任务健康度巡检状态码获取导致日志采集任务健康状态为logCollectTaskHealthInspectionCode的错误信息详情列表", notes = "")
    @RequestMapping(value = "/error-detail/{logCollectTaskId}/{pathId}/{hostName}/{logCollectTaskHealthInspectionCode}", method = RequestMethod.GET)
    @ResponseBody
    public Result<LogCollectTaskHealthErrorDetailVO> getErrorDetails(@PathVariable Long logCollectTaskId, @PathVariable Long pathId, @PathVariable String hostName, @PathVariable Integer logCollectTaskHealthInspectionCode) {

        return null;
    }

    @ApiOperation(value = "根据给定日志采集任务指标id与日志采集任务健康度巡检状态码更新对应日志采集任务的健康度offset", notes = "")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public Result solveErrorDetail(@RequestBody LogCollectTaskHealthSolveErrorDetailDTO solveErrorDetailDTO) {

        return null;
    }

}
