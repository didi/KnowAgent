package com.didichuxing.datachannel.agentmanager.rest.api.v1.op;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetadataSyncResult;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.core.metadata.MetadataManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "OP-Metadata维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX + "metadata")
public class OpMetadataController {

    @Autowired
    private MetadataManageService metadataManageService;

    @ApiOperation(value = "手动同步元数据", notes = "")
    @RequestMapping(value = "sync-result", method = RequestMethod.GET)
    @ResponseBody
    public Result<MetadataSyncResult> sync() {
        MetadataSyncResult metadataSyncResult = metadataManageService.sync();
        return Result.buildSucc(metadataSyncResult);
    }

}
