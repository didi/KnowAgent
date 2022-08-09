package com.didichuxing.datachannel.agentmanager.rest.api.v1.op;

import com.didichuxing.datachannel.agentmanager.common.annotation.CheckPermission;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.receiver.ReceiverCreateDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.receiver.ReceiverUpdateDTO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.common.util.SpringTool;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.didichuxing.datachannel.agentmanager.common.constant.PermissionConstant.AGENT_KAFKA_CLUSTER_EDIT;

@Api(tags = "OP-Receiver维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX + "receivers")
public class OpReceiverController {

    @Autowired
    private KafkaClusterManageService kafkaClusterManageService;

    @ApiOperation(value = "新增接收端", notes = "")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public Result<Long> createReceiver(@RequestBody ReceiverCreateDTO dto) {
        ReceiverDO kafkaClusterDO = ConvertUtil.obj2Obj(dto, ReceiverDO.class);
        return Result.buildSucc(kafkaClusterManageService.createKafkaCluster(kafkaClusterDO, SpringTool.getUserName()));
    }

    @ApiOperation(value = "修改接收端", notes = "")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    @CheckPermission(permission = AGENT_KAFKA_CLUSTER_EDIT)
    public Result updateReceiver(@RequestBody ReceiverUpdateDTO dto) {
        ReceiverDO kafkaClusterDO = ConvertUtil.obj2Obj(dto, ReceiverDO.class);
        kafkaClusterManageService.updateKafkaCluster(kafkaClusterDO, SpringTool.getUserName());
        return Result.buildSucc();
    }

    @ApiOperation(value = "删除接收端 入参为待删除接收端对象id集（逗号分割）0：删除成功 10000：参数错误 26000：KafkaCluster不存在 26001：KafkaCluster删除失败，原因为：系统存在KafkaCluster关联的日志采集任务 26002：KafkaCluster删除失败，原因为：系统存在KafkaCluster关联的Agent", notes = "")
    @RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
    @ResponseBody
    public Result deleteReceiver(@PathVariable String ids) {
        String[] idArray = ids.split(",");
        if(null != idArray && idArray.length != 0) {
            List<Long> receiverIdList = new ArrayList<>(idArray.length);
            for (String id : idArray) {
                receiverIdList.add(Long.valueOf(id));
            }
            kafkaClusterManageService.deleteKafkaClusterById(receiverIdList, false, SpringTool.getUserName());
        }
        return Result.buildSucc();
    }

}
