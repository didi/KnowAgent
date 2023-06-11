package com.didichuxing.datachannel.agentmanager.rest.api.v1.op;

import com.didichuxing.datachannel.agentmanager.common.annotation.CheckPermission;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.service.ServiceCreateDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.service.ServiceUpdateDTO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.common.util.SpringTool;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.didichuxing.datachannel.agentmanager.common.constant.PermissionConstant.AGENT_APP_EDIT;

@Api(tags = "OP-Service维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX + "services")
public class OpServiceController {

    @Autowired
    private ServiceManageService serviceManageService;

    @ApiOperation(value = "新增服务", notes = "")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public Result createService(
                @RequestBody ServiceCreateDTO dto
            ) {
        ServiceDO serviceDO = ConvertUtil.obj2Obj(dto, ServiceDO.class);
        return Result.buildSucc(serviceManageService.createService(serviceDO, SpringTool.getUserName()));
    }

    @ApiOperation(value = "修改服务", notes = "")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    @CheckPermission(permission = AGENT_APP_EDIT)
    public Result updateService(@RequestBody ServiceUpdateDTO dto) {
        ServiceDO serviceDO = ConvertUtil.obj2Obj(dto, ServiceDO.class);
        serviceManageService.updateService(serviceDO, SpringTool.getUserName());
        return Result.buildSucc();
    }

    @ApiOperation(value = "删除服务集 入参为待删除服务id集（逗号分割）0：删除成功 27000：待删除 Service 不存在 27002：Service删除失败，原因为：系统存在Service关联的主机 27003：Service删除失败，原因为：系统存在Service关联的日志采集任务", notes = "")
    @RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
    @ResponseBody
    public Result deleteService(@PathVariable String ids) {
        String[] idArray = ids.split(",");
        if(null != idArray && idArray.length != 0) {
            List<Long> serviceIdList = new ArrayList<>(idArray.length);
            for (String id : idArray) {
                serviceIdList.add(Long.valueOf(id));
            }
            serviceManageService.deleteServices(serviceIdList, false ,SpringTool.getUserName());
        }
        return Result.buildSucc();
    }

}

