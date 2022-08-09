package com.didichuxing.datachannel.agentmanager.thirdpart.agent.manage.extension;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.operationtask.AgentOperationTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.AgentPO;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;

import java.util.List;

public interface AgentManageServiceExtension {

    /**
     * 校验创建Agent方法对应的参数"Agent对象"信息是否合法
     * 注：该操作不应抛出异常，校验过程中出现异常需要对应实现内部处理好
     * @param agent 待校验Agent对象
     * @return 合法：true 不合法：false
     */
    CheckResult checkCreateParameterAgent(AgentDO agent);

    /**
     * 校验删除Agent方法对应的参数"Agent对象"信息是否合法
     * 注：该操作不应抛出异常，校验过程中出现异常需要对应实现内部处理好
     * @param agent 待校验Agent对象
     * @return 合法：true 不合法：false
     */
    CheckResult checkDeleteParameterAgent(AgentDO agent);

    /**
     * 根据给定agent对象构造一个用于安装该agent的AgentOperationTask对象
     * @param agent 待安装 agent 对象
     * @return 返回根据给定agent对象构造的一个用于安装该agent的AgentOperationTask对象
     * @throws ServiceException 执行 "根据给定agent对象构造一个用于安装该agent的AgentOperationTask对象" 过程中出现的异常
     */
    AgentOperationTaskDO agent2AgentOperationTaskInstall(AgentDO agent) throws ServiceException;

    /**
     * 将给定AgentPO对象转化为Agent对象
     * @param agentPO 待转化agentPO对象
     * @return 返回将给定AgentPO对象转化的Agent对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    AgentDO agentPO2AgentDO(AgentPO agentPO) throws ServiceException;

    /**
     * 校验创建更新Agent方法对应的参数"Agent对象"信息是否合法
     * @param agentDO 待校验 Agent 对象
     * @return 合法：true 不合法：false
     */
    CheckResult checkUpdateParameterAgent(AgentDO agentDO);

    /**
     * 根据待更新AgentDO对象targetAgent更新源AgentDO对象sourceAgent，并返回更新后源AgentDO对象sourceAgent
     * @param sourceAgent 源AgentDO对象
     * @param targetAgent 待更新AgentDO对象
     * @return 返回更新后源AgentDO对象sourceAgent
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    AgentDO updateAgent(AgentDO sourceAgent, AgentDO targetAgent);

    /**
     * 将给定AgentPO对象集转化为AgentDO对象集
     * @param agentPOList 待转化AgentPO对象集
     * @return 返回将给定AgentDO对象转化后的AgentDO对象集
     */
    List<AgentDO> agentPOList2AgentDOList(List<AgentPO> agentPOList);

    /**
     *
     * @param hostName 主机名
     * @param path 主 文件路径
     * @param suffixRegular 文件后缀匹配正则
     * @return 根据给定主文件路径与文件后缀匹配正则获取满足匹配对应规则的文件集
     */
    Result<List<String>> listFiles(String hostName, String path, String suffixRegular);

    Result<String> readFileContent(String hostName, String path);

}
