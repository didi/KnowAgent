package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.MetricQueryDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web.MetricQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricAggregate;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricList;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Normal-LogCollectTask-Metrics相关接口")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "collect-task/metrics")
public class NormalLogCollectTaskMetricsController {

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @ApiOperation(value = "获取指定采集任务下存在心跳的主机数", notes = "")
    @RequestMapping(value = "/health/heart-host-count", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<MetricAggregate>> getHeartbeatCountMetric(@RequestBody MetricQueryDTO metricQueryDTO) {
        if (metricQueryDTO.getTaskId() == null) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "采集任务id为空");
        }
        return Result.buildSucc(logCollectTaskManageService.getAliveHostCount(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "数据最大延迟", notes = "")
    @RequestMapping(value = "/health/max-delay", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> maxDelay(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getCollectDelayMetric(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "最小采集时间", notes = "")
    @RequestMapping(value = "/health/min-collectbusiness-time", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> minLogTime(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getMinLogTime(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "限流时长ms", notes = "")
    @RequestMapping(value = "/health/limit-time", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> limitTime(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getLimitTime(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "异常截断数", notes = "")
    @RequestMapping(value = "/health/abnormal-truncation", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> abnormalTruncation(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getAbnormalTruncation(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "路径是否存在", notes = "")
    @RequestMapping(value = "/health/iscollectpath", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> collectPathExists(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getCollectPathExists(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "采集乱序", notes = "")
    @RequestMapping(value = "/health/isexist-collectpath-chaos", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> fileOrder(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getIsFileOrder(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "切片错误", notes = "")
    @RequestMapping(value = "/health/islog-chop-fault", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> sliceError(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getSliceError(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "读取字节数MB", notes = "")
    @RequestMapping(value = "/performance/log-read-bytes", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> readByte(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getReadByte(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "读取条数", notes = "")
    @RequestMapping(value = "/performance/log-read-bar", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> readCount(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getReadCount(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "读取总耗时ms", notes = "")
    @RequestMapping(value = "/performance/log-read-consuming", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> readTimeConsuming(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getTotalReadTime(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "读取最大耗时ms", notes = "")
    @RequestMapping(value = "/performance/logevent-max-consuming", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> readTimeMax(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getReadTimeMax(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "读取平均耗时ms", notes = "")
    @RequestMapping(value = "/performance/logevent-mean-consuming", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> readTimeMean(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getReadTimeMean(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "发送字节数MB", notes = "")
    @RequestMapping(value = "/performance/log-send-bytes", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> sendBytes(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getSendBytes(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "发送条数", notes = "")
    @RequestMapping(value = "/performance/log-send-bar", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> sendCount(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getSendCount(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "发送总时间ms", notes = "")
    @RequestMapping(value = "/performance/log-send-consuming", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> sendTimeConsuming(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getTotalSendTime(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "flush次数", notes = "")
    @RequestMapping(value = "/performance/logflush-times", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> flushCount(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getFlushCount(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "flush最大耗时ms", notes = "")
    @RequestMapping(value = "/performance/logflush-max-consuming", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> flushTimeMax(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getFlushTimeMax(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "flush平均耗时ms", notes = "")
    @RequestMapping(value = "/performance/logflush-mean-consuming", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> flushTimeMean(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getFlushTimeMean(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "flush失败次数", notes = "")
    @RequestMapping(value = "/performance/logflush-fail-times", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> flushFailedCount(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getFlushFailedCount(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    @ApiOperation(value = "过滤数", notes = "")
    @RequestMapping(value = "/performance/data-filter-times", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricList> filterTimes(@RequestBody MetricQueryDTO metricQueryDTO) {
        Result result = checkMetricQueryParam(metricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(logCollectTaskManageService.getFilterCount(ConvertUtil.obj2Obj(metricQueryDTO, MetricQueryDO.class)));
    }

    private Result checkMetricQueryParam(MetricQueryDTO metricQueryDTO) {
        if (metricQueryDTO == null) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "请求参数为空");
        }
        if (metricQueryDTO.getTaskId() == null) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "采集任务id为空");
        }
        if (metricQueryDTO.getStartTime() == null) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "查询开始时间为空");
        }
        if (metricQueryDTO.getEndTime() == null) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "查询结束时间为空");
        }
        if (metricQueryDTO.getEachHost() == null) {
            metricQueryDTO.setEachHost(false);
        }
        return Result.buildSucc();
    }

}
