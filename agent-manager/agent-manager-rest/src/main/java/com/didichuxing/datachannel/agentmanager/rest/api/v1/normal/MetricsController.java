package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.BusinessMetricsQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricNodeVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
