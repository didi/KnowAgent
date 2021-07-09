package com.didichuxing.datachannel.agentmanager.thirdpart.service.extension.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskServicePO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.thirdpart.service.extension.ServiceLogCollectTaskManageServiceExtension;

@org.springframework.stereotype.Service
public class ServiceLogCollectTaskManageServiceExtensionImpl implements ServiceLogCollectTaskManageServiceExtension {

    @Override
    public CheckResult checkCreateParameterLogCollectTaskService(LogCollectTaskServicePO logCollectTaskServicePO) {
        if(null == logCollectTaskServicePO) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    String.format(
                            "class=ServiceLogCollectTaskManageServiceExtensionImpl||method=checkCreateParameterLogCollectTaskService||errorMsg={%s}!",
                            "入参logCollectTaskService对象为空"
                    )
            );
        }
        if(null == logCollectTaskServicePO.getServiceId() || logCollectTaskServicePO.getServiceId() <= 0) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    String.format(
                            "class=ServiceLogCollectTaskManageServiceExtensionImpl||method=checkCreateParameterLogCollectTaskService||errorMsg={%s}!",
                            String.format("入参logCollectTaskService对象={%s}对应serviceId字段值非法", JSON.toJSONString( logCollectTaskServicePO ))
                    )
            );
        }
        if(null == logCollectTaskServicePO.getLogCollectorTaskId() || logCollectTaskServicePO.getLogCollectorTaskId() <= 0) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    String.format(
                            "class=ServiceLogCollectTaskManageServiceExtensionImpl||method=checkCreateParameterLogCollectTaskService||errorMsg={%s}!",
                            String.format("入参logCollectTaskService对象={%s}对应logCollectorTaskId字段值非法", JSON.toJSONString( logCollectTaskServicePO ))
                    )
            );
        }
        return new CheckResult(true);
    }

}
