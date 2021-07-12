package com.didichuxing.datachannel.agentmanager.thirdpart.service.extension.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServiceHostPO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.thirdpart.service.extension.ServiceHostManageServiceExtension;

@org.springframework.stereotype.Service
public class ServiceHostManageServiceExtensionImpl implements ServiceHostManageServiceExtension {

    @Override
    public CheckResult checkCreateParameterServiceHost(ServiceHostPO serviceHostPO) {
        if(null == serviceHostPO) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    String.format(
                            "class=ServiceHostManageServiceExtensionImpl||method=checkCreateParameterServiceHost||errorMsg={%s}!",
                            "入参serviceHost对象为空"
                    )
            );
        }
        if(null == serviceHostPO.getServiceId() || serviceHostPO.getServiceId() <= 0) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    String.format(
                            "class=ServiceHostManageServiceExtensionImpl||method=checkCreateParameterServiceHost||errorMsg={%s}!",
                            String.format("入参serviceHost对象={%s}对应serviceId字段值非法", JSON.toJSONString( serviceHostPO ))
                    )
            );
        }
        if(null == serviceHostPO.getHostId() || serviceHostPO.getHostId() <= 0) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    String.format(
                            "class=ServiceHostManageServiceExtensionImpl||method=checkCreateParameterServiceHost||errorMsg={%s}!",
                            String.format("入参serviceHost对象={%s}对应hostId字段值非法", JSON.toJSONString( serviceHostPO ))
                    )
            );
        }
        return new CheckResult(true);
    }

}
