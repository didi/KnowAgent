package com.didichuxing.datachannel.agentmanager.thirdpart.service.extension;

import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServiceHostPO;

public interface ServiceHostManageServiceExtension {

    /**
     * 校验添加ServiceHost方法对应的参数"ServiceHost对象"信息是否合法
     * 注：该操作不应抛出异常，校验过程中出现异常需要对应实现内部处理好
     * @param serviceHostPO 待校验serviceHost对象
     * @return 合法：true 不合法：false
     */
    CheckResult checkCreateParameterServiceHost(ServiceHostPO serviceHostPO);

}
