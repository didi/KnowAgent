package com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.health.extension.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskHealthPO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.health.extension.LogCollectTaskHealthManageServiceExtension;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class DefaultLogCollectTaskHealthManageServiceExtensionImpl implements LogCollectTaskHealthManageServiceExtension {

    @Override
    public LogCollectTaskHealthPO buildInitialLogCollectorTaskHealthPO(LogCollectTaskDO logCollectTaskDO, String operator) throws ServiceException {
        if(null == logCollectTaskDO) {
            throw new ServiceException(
                    String.format(
                            "class=LogCollectTaskHealthManageServiceExtensionImpl||method=buildInitialLogCollectorTaskHealthPO||msg={%s}",
                            "入参logCollectTaskId不可为空"
                    ),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        LogCollectTaskHealthPO logCollectorTaskHealthPO = new LogCollectTaskHealthPO();
        logCollectorTaskHealthPO.setLogCollectTaskHealthDescription(StringUtils.EMPTY);
        logCollectorTaskHealthPO.setLogCollectTaskId(logCollectTaskDO.getId());
        logCollectorTaskHealthPO.setLogCollectTaskHealthLevel(LogCollectTaskHealthLevelEnum.GREEN.getCode());
        logCollectorTaskHealthPO.setLogCollectTaskHealthInspectionResultType(LogCollectTaskHealthInspectionResultEnum.HEALTHY.getCode());
        logCollectorTaskHealthPO.setOperator(CommonConstant.getOperator(operator));
        return logCollectorTaskHealthPO;
    }

    @Override
    public LogCollectTaskHealthDO logCollectTaskHealthPO2LogCollectTaskHealthDO(LogCollectTaskHealthPO logCollectTaskHealthPO) {
        return ConvertUtil.obj2Obj(logCollectTaskHealthPO, LogCollectTaskHealthDO.class);
    }

    @Override
    public LogCollectTaskHealthPO logCollectTaskHealthDO2LogCollectTaskHealthPO(LogCollectTaskHealthDO logCollectTaskHealthDO) {
        return ConvertUtil.obj2Obj(logCollectTaskHealthDO, LogCollectTaskHealthPO.class);
    }

}
