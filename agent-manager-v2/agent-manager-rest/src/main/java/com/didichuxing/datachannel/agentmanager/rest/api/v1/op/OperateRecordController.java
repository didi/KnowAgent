package com.didichuxing.datachannel.agentmanager.rest.api.v1.op;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.operaterecord.OperateRecordDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.operaterecord.OperateRecordVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.ModuleEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.OperationEnum;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.core.common.OperateRecordService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX + "record")
@Api(value = "运维操作记录接口(REST)")
public class OperateRecordController {

    private static final int     MAX_RECORD_COUNT = 200;

    @Autowired
    private OperateRecordService operateRecordService;

    @RequestMapping(path = "/list", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询操作记录接口", notes = "")
    // @CheckPermission(permission = AGENT_TASK_OPERATION_RECORD_LIST)
    public Result<List<OperateRecordVO>> list(@RequestBody OperateRecordDTO query) {
        List<OperateRecordVO> records = ConvertUtil.list2List(operateRecordService.list(query), OperateRecordVO.class);

        fillVOField(records);
        return Result.buildSucc(records);
    }

    @RequestMapping(path = "/listModules", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取所有模块", notes = "")
    public Result<List<Map<String, Object>>> listModules() {
        List<Map<String, Object>> objects = Lists.newArrayList();
        for (ModuleEnum moduleEnum : ModuleEnum.values()) {
            objects.add(moduleEnum.toMap());
        }
        return Result.buildSucc(objects);
    }

    /**
     * 添加操作记录
     * @return
     */
    @RequestMapping(path = "", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "保存操作记录接口", notes = "")
    public Result add(@RequestBody OperateRecordDTO param) {
        operateRecordService.save(param);
        return Result.buildSucc();
    }

    /**************************************** private method ****************************************************/
    private void fillVOField(List<OperateRecordVO> records) {
        if(CollectionUtils.isEmpty(records)){return;}

        if (records.size() > MAX_RECORD_COUNT) {
            records = records.subList(0, MAX_RECORD_COUNT);
        }

        for(OperateRecordVO vo : records){
            vo.setModule(ModuleEnum.valueOf(vo.getModuleId()).getDesc());
            vo.setOperate(OperationEnum.valueOf(vo.getOperateId()).getDesc());
        }
    }
}
