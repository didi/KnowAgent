package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.GlobalProperties;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Api(tags = "Normal-SystemConfig维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "system-config")
public class NormalSystemConfigController {

    /**
     * 日期/时间格式串集
     */
    @Value("${system.config.datetime.formats}")
    private String dateTimeFormats;

    @ApiOperation(value = "获取系统配置的所有日期/时间格式", notes = "")
    @RequestMapping(value = "/datetime-format", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<String>> listDateTimeFormats() {
        // TODO：见 https://www.jb51.net/article/162825.htm
        if(CollectionUtils.isEmpty(GlobalProperties.dateTimeFormats)) {
            String[] dateTimeFormatArray = dateTimeFormats.split(CommonConstant.COMMA);
            GlobalProperties.dateTimeFormats = Arrays.asList(dateTimeFormatArray);
        }
        return Result.buildSucc(GlobalProperties.dateTimeFormats);
    }

}
