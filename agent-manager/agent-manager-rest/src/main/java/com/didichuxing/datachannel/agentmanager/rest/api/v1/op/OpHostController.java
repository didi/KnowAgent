package com.didichuxing.datachannel.agentmanager.rest.api.v1.op;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.host.HostCreateDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.host.HostUpdateDTO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.common.util.SpringTool;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "OP-Host维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX + "hosts")
public class OpHostController {

    @Autowired
    private HostManageService hostManageService;

    @ApiOperation(value = "新增主机", notes = "")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public Result createHost(@RequestBody HostCreateDTO dto) {
        HostDO hostDO = ConvertUtil.obj2Obj(dto, HostDO.class);
        return Result.buildSucc(hostManageService.createHost(hostDO, SpringTool.getUserName()));
    }

    @ApiOperation(value = "修改主机", notes = "")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public Result updateHost(@RequestBody HostUpdateDTO dto) {
        HostDO hostDO = ConvertUtil.obj2Obj(dto, HostDO.class);
        hostManageService.updateHost(hostDO, SpringTool.getUserName());
        return Result.buildSucc();
    }

    @ApiOperation(value = "删除主机 0：删除成功 10000：参数错误 23000：待删除主机在系统不存在 23004：主机存在关联的容器导致主机删除失败 22001：Agent存在未采集完的日志 23008：主机存在关联的应用导致主机删除失败 ", notes = "")
    @RequestMapping(value = "/{hostId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Result deleteHost(
            @PathVariable Long hostId
    ) {
        hostManageService.deleteHost(hostId, true, true, SpringTool.getUserName());
        return Result.buildSucc();
    }

}
