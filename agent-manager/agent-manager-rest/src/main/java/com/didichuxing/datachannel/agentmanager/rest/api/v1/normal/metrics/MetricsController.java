package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metrics.StatisticMetricPoint;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.agent.AgentMetricsQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricNodeVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricsItemVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricFieldEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Metrics相关接口")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "metrics")
public class MetricsController {

    @ApiOperation(value = "根据指标类型代码 code 获取其子类型或指标集 注意：code 传入 0，将获取全量一级指标", notes = "")
    @RequestMapping(value = "/{code}", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<MetricsItemVO>> getMetricsByCode(@PathVariable Integer code) {

        //TODO：确定是否需要？

        return null;

    }

    @ApiOperation(value = "获取以树结构方式组织的全量指标集", notes = "")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public Result<MetricNodeVO> getMetrics() {

        return null;

    }

}
