package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDetailDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.health.LogCollectTaskHealthSolveErrorDetailDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask.health.LogCollectTaskHealthErrorDetailPathHostsVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask.health.LogCollectTaskHealthErrorDetailVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthDetailManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.FileLogCollectPathManageService;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "Normal-LogCollectTaskHealth相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "log-collect-task/health")
public class NormalLogCollectTaskHealthController {

    @Autowired
    private LogCollectTaskHealthDetailManageService logCollectTaskHealthDetailManageService;

    @Autowired
    private FileLogCollectPathManageService fileLogCollectPathManageService;

    @Autowired
    private HostManageService hostManageService;

    @ApiOperation(value = "根据给定日志采集任务id与日志采集任务健康度巡检状态码获取导致该状态的采集路径与主机集", notes = "")
    @RequestMapping(value = "/error-detail/path-host/{logCollectTaskId}/{logCollectTaskHealthInspectionCode}", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<LogCollectTaskHealthErrorDetailPathHostsVO>> getPathHosts(@PathVariable Long logCollectTaskId, @PathVariable Integer logCollectTaskHealthInspectionCode) {
        List<LogCollectTaskHealthDetailDO> logCollectTaskHealthDetailDOList = logCollectTaskHealthDetailManageService.getByLogCollectTaskIdAndLogCollectTaskHealthInspectionCode(logCollectTaskId, logCollectTaskHealthInspectionCode);
        return Result.buildSucc(convert2LogCollectTaskHealthErrorDetailPathHostsVOList(logCollectTaskHealthDetailDOList));
    }

    @ApiOperation(value = "根据给定日志采集任务id，采集路径id，主机名，日志采集任务健康度巡检状态码获取导致日志采集任务健康状态为logCollectTaskHealthInspectionCode的错误信息详情列表", notes = "")
    @RequestMapping(value = "/error-detail", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<LogCollectTaskHealthErrorDetailVO>> getErrorDetails(@RequestParam Long logCollectTaskId, @RequestParam Long pathId, @RequestParam String hostName, @RequestParam Integer logCollectTaskHealthInspectionCode) {
        List<MetricsLogCollectTaskPO> metricsLogCollectTaskPOList = logCollectTaskHealthDetailManageService.getErrorDetails(logCollectTaskId, pathId, hostName, logCollectTaskHealthInspectionCode);
        return Result.buildSucc(convert2LogCollectTaskHealthErrorDetailVOList(metricsLogCollectTaskPOList));
    }

    @ApiOperation(value = "根据给定日志采集任务指标id与日志采集任务健康度巡检状态码更新对应日志采集任务的健康度offset", notes = "")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public Result solveErrorDetail(@RequestBody LogCollectTaskHealthSolveErrorDetailDTO solveErrorDetailDTO) {
        logCollectTaskHealthDetailManageService.solveErrorDetail(solveErrorDetailDTO.getLogCollectTaskMetricId(), solveErrorDetailDTO.getLogCollectTaskHealthInspectionCode());
        return Result.buildSucc();
    }

    private List<LogCollectTaskHealthErrorDetailPathHostsVO> convert2LogCollectTaskHealthErrorDetailPathHostsVOList(List<LogCollectTaskHealthDetailDO> logCollectTaskHealthDetailDOList) {
        Map<Long, List<String>> path2HostNameMap = new HashMap();
        for (LogCollectTaskHealthDetailDO logCollectTaskHealthDetailDO: logCollectTaskHealthDetailDOList) {
            List<String> hostNameList = path2HostNameMap.get(logCollectTaskHealthDetailDO.getPathId());
            if(CollectionUtils.isEmpty(hostNameList)) {
                hostNameList = new ArrayList<>();
                hostNameList.add(logCollectTaskHealthDetailDO.getHostName());
                path2HostNameMap.put(logCollectTaskHealthDetailDO.getPathId(), hostNameList);
            } else {
                hostNameList.add(logCollectTaskHealthDetailDO.getHostName());
            }
        }
        List<LogCollectTaskHealthErrorDetailPathHostsVO> logCollectTaskHealthErrorDetailPathHostsVOList = new ArrayList<>();
        for (Map.Entry<Long, List<String>> entry : path2HostNameMap.entrySet()) {
            Long pathId = entry.getKey();
            List<String> hostNameList = entry.getValue();
            LogCollectTaskHealthErrorDetailPathHostsVO logCollectTaskHealthErrorDetailPathHostsVO = new LogCollectTaskHealthErrorDetailPathHostsVO();
            logCollectTaskHealthErrorDetailPathHostsVO.setPathId(pathId);
            logCollectTaskHealthErrorDetailPathHostsVO.setHostNameList(hostNameList);
            logCollectTaskHealthErrorDetailPathHostsVO.setPath(fileLogCollectPathManageService.getById(pathId).getPath());
            logCollectTaskHealthErrorDetailPathHostsVOList.add(logCollectTaskHealthErrorDetailPathHostsVO);
        }
        return logCollectTaskHealthErrorDetailPathHostsVOList;
    }

    private List<LogCollectTaskHealthErrorDetailVO> convert2LogCollectTaskHealthErrorDetailVOList(List<MetricsLogCollectTaskPO> metricsLogCollectTaskPOList) {
        List<LogCollectTaskHealthErrorDetailVO> logCollectTaskHealthErrorDetailVOList = new ArrayList<>();
        for (MetricsLogCollectTaskPO logCollectTaskPO: metricsLogCollectTaskPOList) {
            LogCollectTaskHealthErrorDetailVO logCollectTaskHealthErrorDetailVO = new LogCollectTaskHealthErrorDetailVO();
            logCollectTaskHealthErrorDetailVO.setLogCollectTaskId(logCollectTaskPO.getCollecttaskid());
            logCollectTaskHealthErrorDetailVO.setLogCollectTaskMetricId(logCollectTaskPO.getId());
            logCollectTaskHealthErrorDetailVO.setCollectFiles(logCollectTaskPO.getCollectfiles());
            logCollectTaskHealthErrorDetailVO.setLogTime(DateUtils.getDateTimeStr(logCollectTaskPO.getBusinesstimestamp()));
            logCollectTaskHealthErrorDetailVO.setHeartbeatTime(DateUtils.getDateTimeStr(logCollectTaskPO.getHeartbeattime()));
            logCollectTaskHealthErrorDetailVO.setPath(logCollectTaskPO.getPath());
            logCollectTaskHealthErrorDetailVO.setHostName(logCollectTaskPO.getCollecttaskhostname());
            logCollectTaskHealthErrorDetailVO.setPathId(logCollectTaskPO.getPathid());
            logCollectTaskHealthErrorDetailVO.setIp(hostManageService.getHostByHostName(logCollectTaskPO.getCollecttaskhostname()).getIp());
            logCollectTaskHealthErrorDetailVOList.add(logCollectTaskHealthErrorDetailVO);
        }
        return logCollectTaskHealthErrorDetailVOList;
    }

}
