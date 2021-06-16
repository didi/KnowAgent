package com.didichuxing.datachannel.agentmanager.thirdpart.service.extension;

import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskServicePO;

public interface ServiceLogCollectTaskManageServiceExtension {

    /**
     * 校验添加LogCollectTaskService方法对应的参数"LogCollectTaskService对象"信息是否合法
     * 注：该操作不应抛出异常，校验过程中出现异常需要对应实现内部处理好
     * @param logCollectTaskServicePO 待校验logCollectTaskService对象
     * @return 合法：true 不合法：false
     */
    CheckResult checkCreateParameterLogCollectTaskService(LogCollectTaskServicePO logCollectTaskServicePO);

}
