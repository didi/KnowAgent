package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.MetricsLogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.BusinessMetricsQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask.CollectTaskMetricVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricNodeVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "Metrics相关接口")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "metrics")
public class MetricsController {

    @Autowired
    private MetricsManageService metricsManageService;

    @ApiOperation(value = "根据指标类型获取对应以树结构组织的指标集 type：1：agent相关指标 2：日志采集任务相关指标", notes = "")
    @RequestMapping(value = "/{metricsTypeCode}", method = RequestMethod.GET)
    @ResponseBody
    public Result<MetricNodeVO> getMetricsByType(@PathVariable Integer metricsTypeCode) {
        MetricNodeVO metricNodeVO = metricsManageService.getMetricsTreeByMetricType(metricsTypeCode);
        return Result.buildSucc(metricNodeVO);
    }

    @ApiOperation(value = "获取给定指标code对应的指标数据信息", notes = "")
    @RequestMapping(value = "/metric", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricPanel> getMetric(@RequestBody BusinessMetricsQueryDTO metricQueryDTO) {
        MetricPanel metricPanel = metricsManageService.getMetric(metricQueryDTO);
        return Result.buildSucc(metricPanel);
    }

    @ApiOperation(value = "获取指定agent关联的最近采集任务指标集", notes = "")
    @RequestMapping(value = "/last-agent-collect-tasks-metrics", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<CollectTaskMetricVO>> getLastCollectTaskMetricsByAgentHostName(@RequestParam("hostName") String hostName) {
        if (StringUtils.isBlank(hostName)) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "主机名为空");
        }
        List<MetricsLogCollectTaskDO> collectTaskMetricDOList = metricsManageService.getLastCollectTaskMetricsByAgentHostName(hostName);
        List<CollectTaskMetricVO> collectTaskMetricVOList = new ArrayList<>();
        for (MetricsLogCollectTaskDO collectTaskMetricDO : collectTaskMetricDOList) {
            CollectTaskMetricVO collectTaskMetricVO = ConvertUtil.obj2Obj(collectTaskMetricDO, CollectTaskMetricVO.class);
            collectTaskMetricVOList.add(collectTaskMetricVO);
        }
        return Result.buildSucc(collectTaskMetricVOList);
    }

}
