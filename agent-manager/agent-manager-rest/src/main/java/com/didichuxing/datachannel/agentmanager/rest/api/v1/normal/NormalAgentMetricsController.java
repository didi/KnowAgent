package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.AgentMetricQueryDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.CollectTaskMetricDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web.AgentMetricQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask.CollectFileVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask.CollectTaskMetricVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricAggregate;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPointList;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "Agent-Metric相关接口")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "agent/metrics")
public class NormalAgentMetricsController {
    @Autowired
    private AgentManageService service;

    @ApiOperation(value = "获取指定agent的cpu使用率", notes = "")
    @RequestMapping(value = "/cpu-usage", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricPointList> cpuUsage(@RequestBody AgentMetricQueryDTO agentMetricQueryDTO) {
        Result result = checkParam(agentMetricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(service.getCpuUsage(ConvertUtil.obj2Obj(agentMetricQueryDTO, AgentMetricQueryDO.class)));
    }

    @ApiOperation(value = "获取指定agent的内存使用量mb", notes = "")
    @RequestMapping(value = "/memory-usage", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricPointList> memoryUsage(@RequestBody AgentMetricQueryDTO agentMetricQueryDTO) {
        Result result = checkParam(agentMetricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(service.getMemoryUsage(ConvertUtil.obj2Obj(agentMetricQueryDTO, AgentMetricQueryDO.class)));
    }

    @ApiOperation(value = "获取指定agent的fd使用量", notes = "")
    @RequestMapping(value = "/fd-usage", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricPointList> fdUsage(@RequestBody AgentMetricQueryDTO agentMetricQueryDTO) {
        Result result = checkParam(agentMetricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(service.getFdUsage(ConvertUtil.obj2Obj(agentMetricQueryDTO, AgentMetricQueryDO.class)));
    }

    @ApiOperation(value = "获取指定agent的gc使用量", notes = "")
    @RequestMapping(value = "/full-gc-times", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricPointList> gcCount(@RequestBody AgentMetricQueryDTO agentMetricQueryDTO) {
        Result result = checkParam(agentMetricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(service.getGcCount(ConvertUtil.obj2Obj(agentMetricQueryDTO, AgentMetricQueryDO.class)));
    }

    @ApiOperation(value = "获取指定agent的出口流量mb", notes = "")
    @RequestMapping(value = "/exit-send-traffic", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricPointList> sendByte(@RequestBody AgentMetricQueryDTO agentMetricQueryDTO) {
        Result result = checkParam(agentMetricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(service.getSendByte(ConvertUtil.obj2Obj(agentMetricQueryDTO, AgentMetricQueryDO.class)));
    }

    @ApiOperation(value = "获取指定agent的出口发送条数", notes = "")
    @RequestMapping(value = "/exit-send-bar", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricPointList> sendCount(@RequestBody AgentMetricQueryDTO agentMetricQueryDTO) {
        Result result = checkParam(agentMetricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(service.getSendCount(ConvertUtil.obj2Obj(agentMetricQueryDTO, AgentMetricQueryDO.class)));
    }

    @ApiOperation(value = "获取指定agent的入口流量mb", notes = "")
    @RequestMapping(value = "/inlet-collect-traffic", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricPointList> readByte(@RequestBody AgentMetricQueryDTO agentMetricQueryDTO) {
        Result result = checkParam(agentMetricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(service.getReadByte(ConvertUtil.obj2Obj(agentMetricQueryDTO, AgentMetricQueryDO.class)));
    }

    @ApiOperation(value = "获取指定agent的入口条数", notes = "")
    @RequestMapping(value = "/inlet-collect-bar", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricPointList> readCount(@RequestBody AgentMetricQueryDTO agentMetricQueryDTO) {
        Result result = checkParam(agentMetricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(service.getReadCount(ConvertUtil.obj2Obj(agentMetricQueryDTO, AgentMetricQueryDO.class)));
    }

    @ApiOperation(value = "获取指定agent的错误日志输出数量", notes = "")
    @RequestMapping(value = "/logfault-output-bar", method = RequestMethod.POST)
    @ResponseBody
    public Result<MetricPointList> errorLogCount(@RequestBody AgentMetricQueryDTO agentMetricQueryDTO) {
        Result result = checkParam(agentMetricQueryDTO);
        if (result.failed()) {
            return result;
        }
        return Result.buildSucc(service.getErrorLogCount(ConvertUtil.obj2Obj(agentMetricQueryDTO, AgentMetricQueryDO.class)));
    }

    @ApiOperation(value = "获取指定agent关联的采集任务数", notes = "")
    @RequestMapping(value = "/log-collect-task", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<MetricAggregate>> collectTaskCount(@RequestBody AgentMetricQueryDTO agentMetricQueryDTO) {
        if (agentMetricQueryDTO == null) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "请求参数为空");
        }
        if (agentMetricQueryDTO.getAgentId() == null) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "agent id为空");
        }
        return Result.buildSucc(service.getCollectTaskCount(ConvertUtil.obj2Obj(agentMetricQueryDTO, AgentMetricQueryDO.class)));
    }

    @ApiOperation(value = "获取指定agent关联的采集路径数", notes = "")
    @RequestMapping(value = "/log-collect-path", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<MetricAggregate>> collectPathCount(@RequestBody AgentMetricQueryDTO agentMetricQueryDTO) {
        if (agentMetricQueryDTO == null) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "请求参数为空");
        }
        if (agentMetricQueryDTO.getAgentId() == null) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "agent id为空");
        }
        return Result.buildSucc(service.getCollectPathCount(ConvertUtil.obj2Obj(agentMetricQueryDTO, AgentMetricQueryDO.class)));
    }

    @ApiOperation(value = "获取指定agent关联的采集任务指标", notes = "")
    @RequestMapping(value = "/collect-tasks", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<CollectTaskMetricVO>> collectTaskMetrics(@RequestParam("hostname") String hostname) {
        if (StringUtils.isBlank(hostname)) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "主机名为空");
        }
        List<CollectTaskMetricDO> collectTaskMetricList = service.getRelatedTaskMetrics(hostname);
        List<CollectTaskMetricVO> voList = new ArrayList<>();
        for (CollectTaskMetricDO collectTaskMetricDO : collectTaskMetricList) {
            CollectTaskMetricVO collectTaskMetricVO = convertToCollectTaskMetric(collectTaskMetricDO);
            voList.add(collectTaskMetricVO);
        }
        return Result.buildSucc(voList);
    }

    private Result checkParam(AgentMetricQueryDTO dto) {
        if (dto == null) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "请求参数为空");
        }
        if (dto.getAgentId() == null) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "agent id为空");
        }
        if (dto.getStartTime() == null) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "查询开始时间为空");
        }
        if (dto.getEndTime() == null) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "查询结束时间为空");
        }
        return Result.buildSucc();
    }

    private CollectTaskMetricVO convertToCollectTaskMetric(CollectTaskMetricDO collectTaskMetricDO) {
        CollectTaskMetricVO collectTaskMetricVO = ConvertUtil.obj2Obj(collectTaskMetricDO, CollectTaskMetricVO.class, "collectFiles");
        collectTaskMetricVO.setCollectFiles(JSON.parseObject(collectTaskMetricDO.getCollectFiles(), new TypeReference<List<CollectFileVO>>() {
        }));
        return collectTaskMetricVO;
    }
}
