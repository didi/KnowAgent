package com.didichuxing.datachannel.agentmanager.remote.service;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;

import java.util.List;

public interface RemoteServiceManageService {

    /**
     * @return 从远程获取服务节点信息
     */
    List<ServiceDO> getServicesFromRemote();

}
