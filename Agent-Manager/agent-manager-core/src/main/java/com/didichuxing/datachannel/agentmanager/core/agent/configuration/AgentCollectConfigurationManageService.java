package com.didichuxing.datachannel.agentmanager.core.agent.configuration;

import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config.AgentCollectConfiguration;

/**
 * @author huqidong
 * @date 2020-09-21
 * Agent采集配置管理服务接口
 */
public interface AgentCollectConfigurationManageService {

    /**
     * @param hostName 主机名
     * @return 返回给定主机名对应Agent的采集配置信息
     */
    AgentCollectConfiguration getAgentConfigurationByHostName(String hostName);

}
