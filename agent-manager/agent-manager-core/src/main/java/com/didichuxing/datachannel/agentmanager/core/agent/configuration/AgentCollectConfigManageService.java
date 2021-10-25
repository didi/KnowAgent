package com.didichuxing.datachannel.agentmanager.core.agent.configuration;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config.AgentCollectConfigDO;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;

/**
 * @author huqidong
 * @date 2020-09-21
 * Agent采集配置管理服务接口
 */
public interface AgentCollectConfigManageService {

    /**
     * @param hostName 主机名
     * @return 返回给定主机名对应Agent的采集配置信息
     */
    AgentCollectConfigDO getAgentConfigDOByHostName(String hostName);

    /**
     * 判断给定日志采集任务是否须部署至给定主机
     * @param logCollectTask 日志采集任务对象
     * @param host 主机对象
     * @return true：须部署 false：不须部署
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    boolean need2Deploy(LogCollectTaskDO logCollectTask, HostDO host) throws ServiceException;

}
