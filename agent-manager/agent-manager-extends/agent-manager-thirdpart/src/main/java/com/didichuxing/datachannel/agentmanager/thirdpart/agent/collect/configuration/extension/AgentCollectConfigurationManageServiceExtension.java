package com.didichuxing.datachannel.agentmanager.thirdpart.agent.collect.configuration.extension;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.DirectoryLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config.*;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;

public interface AgentCollectConfigurationManageServiceExtension {

    /**
     * 将给定Agent对象转化为AgentConfiguration对象
     * @param agent 待转化Agent对象
     * @param metricsReceiverDO Agent关联的指标数据对应接收端配置信息
     * @param errorLogsReceiverDO Agent关联的错误日志数据对应接收端配置信息
     * @return 返回将给定Agent对象转化的AgentConfiguration对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    AgentConfiguration agent2AgentConfiguration(AgentDO agent, ReceiverDO metricsReceiverDO, ReceiverDO errorLogsReceiverDO) throws ServiceException;

    /**
     * 将给定LogCollectTask对象转化为LogCollectTaskConfiguration对象
     * @param logCollectTask 待转化LogCollectTask对象
     * @param logReceiverDO 日志采集任务对应数据流接收端 id
     * @return 返回将给定LogCollectTask对象转化的LogCollectTaskConfiguration对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    LogCollectTaskConfiguration logCollectTask2LogCollectTaskConfiguration(LogCollectTaskDO logCollectTask, ReceiverDO logReceiverDO) throws ServiceException;

    /**
     * 将给定FileLogCollectPath对象转化为FileLogCollectPathConfiguration对象
     * @param fileLogCollectPath 待转化FileLogCollectPath对象
     * @return 返回将给定FileLogCollectPath对象转化的FileLogCollectPathConfiguration对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    FileLogCollectPathConfiguration fileLogCollectPath2FileLogCollectPathConfiguration(FileLogCollectPathDO fileLogCollectPath) throws ServiceException;

    /**
     * 根据给定主机对象构建对应HostInfo对象
     * @param host 主机对象
     * @return 返回根据给定主机对象构建的HostInfo对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    HostInfo buildHostInfoByHost(HostDO host) throws ServiceException;

    /**
     * 判断给定日志采集任务是否须部署至给定主机
     * @param logCollectTask 日志采集任务对象
     * @param host 主机对象
     * @return true：须部署 false：不须部署
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    boolean need2Deploy(LogCollectTaskDO logCollectTask, HostDO host) throws ServiceException;

}
