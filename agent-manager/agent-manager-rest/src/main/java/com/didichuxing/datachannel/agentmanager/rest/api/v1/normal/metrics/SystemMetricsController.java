package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metrics.*;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.agent.AgentMetricsQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.agent.AgentMultiMetricsQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricFieldEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "System-Metrics相关接口")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "metrics/system")
public class SystemMetricsController {

//    @Autowired
//    private MetricsSystemService metricsSystemService;

    @ApiOperation(value = "获取指标在给定时间范围内最晚的一个指标值", notes = "")
    @RequestMapping(value = "/lable", method = RequestMethod.POST)
    @ResponseBody
    public Result<Object> getLable(@RequestBody AgentMetricsQueryDTO metricQueryDTO) {
        MetricFieldEnum metricFieldEnum = MetricFieldEnum.fromMetricName(metricQueryDTO.getMetricName());
        if(null == metricFieldEnum) {
            Result.build(
                    ErrorCodeEnum.METRICS_QUERY_METRIC_NOT_EXISTS.getCode(),
                    String.format("%s:%s", metricQueryDTO.getMetricName(), ErrorCodeEnum.METRICS_QUERY_METRIC_NOT_EXISTS.getMessage())
            );
        }
        return null;
    }

    @ApiOperation(value = "获取单条线指标点集", notes = "")
    @RequestMapping(value = "/line-chat/single", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<StatisticMetricPoint>> getSingleLine(@RequestBody AgentMetricsQueryDTO metricQueryDTO) {
        MetricFieldEnum metricFieldEnum = MetricFieldEnum.fromMetricName(metricQueryDTO.getMetricName());
        if(null == metricFieldEnum) {
            Result.build(
                    ErrorCodeEnum.METRICS_QUERY_METRIC_NOT_EXISTS.getCode(),
                    String.format("%s:%s", metricQueryDTO.getMetricName(), ErrorCodeEnum.METRICS_QUERY_METRIC_NOT_EXISTS.getMessage())
            );
        }
        return null;
    }

    @ApiOperation(value = "获取多条线指标点集-diskio", notes = "")
    @RequestMapping(value = "/line-chat/multi/disk-io", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<List<DiskIOStatisticMetricPoint>>> getMultiLineDiskIO(@RequestBody AgentMultiMetricsQueryDTO multiMetricsQueryDTO) {

        return null;
    }

    @ApiOperation(value = "获取多条线指标点集-netcard", notes = "")
    @RequestMapping(value = "/line-chat/multi/net-card", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<List<NetCardStatisticMetricPoint>>> getMultiLineNetCard(@RequestBody AgentMultiMetricsQueryDTO multiMetricsQueryDTO) {

        return null;
    }

}
