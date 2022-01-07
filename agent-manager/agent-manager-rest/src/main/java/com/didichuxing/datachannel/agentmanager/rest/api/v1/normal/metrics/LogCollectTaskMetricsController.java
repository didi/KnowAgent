package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metrics.StatisticMetricPoint;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.logcollecttask.LogCollectTaskLableMetricsQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.logcollecttask.LogCollectTaskMultiLineMetricsQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.logcollecttask.LogCollectTaskSingleLineMetricsQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricFieldEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "LogCollectTask-Metrics相关接口")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "metrics/log-collect-task")
public class LogCollectTaskMetricsController {

    @ApiOperation(value = "获取指标在给定时间范围内最晚的一个指标值", notes = "")
    @RequestMapping(value = "/lable", method = RequestMethod.POST)
    @ResponseBody
    public Result<Object> getLable(@RequestBody LogCollectTaskLableMetricsQueryDTO metricQueryDTO) {
        MetricFieldEnum metricFieldEnum = MetricFieldEnum.fromMetricName(metricQueryDTO.getMetricName());
        if(null == metricFieldEnum) {
            Result.build(
                    ErrorCodeEnum.METRICS_QUERY_METRIC_NOT_EXISTS.getCode(),
                    String.format("%s:%s", metricQueryDTO.getMetricName(), ErrorCodeEnum.METRICS_QUERY_METRIC_NOT_EXISTS.getMessage())
            );
        }
        return null;
    }

    @ApiOperation(value = "获取单条线指标点集-\"日志采集任务\"维度", notes = "")
    @RequestMapping(value = "/line-chat/single/task", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<StatisticMetricPoint>> getSingleLineTask(@RequestBody LogCollectTaskSingleLineMetricsQueryDTO metricQueryDTO) {
        MetricFieldEnum metricFieldEnum = MetricFieldEnum.fromMetricName(metricQueryDTO.getMetricName());
        if(null == metricFieldEnum) {
            Result.build(
                    ErrorCodeEnum.METRICS_QUERY_METRIC_NOT_EXISTS.getCode(),
                    String.format("%s:%s", metricQueryDTO.getMetricName(), ErrorCodeEnum.METRICS_QUERY_METRIC_NOT_EXISTS.getMessage())
            );
        }
        return null;
    }

    @ApiOperation(value = "获取多条线指标点集-\"日志采集任务 & path\"维度，注意：请求入参 hostName 无须设置值，设置会被忽略", notes = "")
    @RequestMapping(value = "/line-chat/multi/task-path", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<List<StatisticMetricPoint>>> getMultiLineTaskAndPath(@RequestBody LogCollectTaskMultiLineMetricsQueryDTO multiMetricsQueryDTO) {

        return null;
    }

    @ApiOperation(value = "获取多条线指标点集-\"日志采集任务 & hostName\"维度，注意：请求入参 pathId 无须设置值，设置会被忽略", notes = "")
    @RequestMapping(value = "/line-chat/multi/task-hostname", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<List<StatisticMetricPoint>>> getMultiLineTaskAndHostName(@RequestBody LogCollectTaskMultiLineMetricsQueryDTO multiMetricsQueryDTO) {

        return null;
    }

    @ApiOperation(value = "获取多条线指标点集-\"日志采集任务 & path & hostName\"维度", notes = "")
    @RequestMapping(value = "/line-chat/multi/task-path-hostname", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<List<StatisticMetricPoint>>> getMultiLineTaskAndPathAndHostName(@RequestBody LogCollectTaskMultiLineMetricsQueryDTO multiMetricsQueryDTO) {

        return null;
    }

}
