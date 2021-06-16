package com.didichuxing.datachannel.agentmanager.remote.host;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;

import java.util.List;

public interface RemoteHostManageService {

    /**
     * @return 从远程获取全量主机信息
     */
    List<HostDO> getHostsByServiceIdFromRemote(Long serviceId);

}
