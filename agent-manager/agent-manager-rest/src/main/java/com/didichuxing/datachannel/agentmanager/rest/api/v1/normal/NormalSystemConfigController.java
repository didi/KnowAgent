package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Normal-SystemConfig维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "system-config")
public class NormalSystemConfigController {

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @ApiOperation(value = "获取系统配置的所有日期/时间格式", notes = "")
    @RequestMapping(value = "/datetime-format", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<String>> listDateTimeFormats() {
        return Result.buildSucc(logCollectTaskManageService.getDateTimeFormats());
    }

}
