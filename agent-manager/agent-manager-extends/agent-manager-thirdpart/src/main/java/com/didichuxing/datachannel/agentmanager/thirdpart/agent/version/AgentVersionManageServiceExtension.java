package com.didichuxing.datachannel.agentmanager.thirdpart.agent.version;

import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.agent.version.AgentVersionDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.version.AgentVersionPO;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;

import java.util.List;

public interface AgentVersionManageServiceExtension {

    /**
     * 校验添加agentVersion方法对应的参数"agentVersion对象"信息是否合法
     * 注：该操作不应抛出异常，校验过程中出现异常需要对应实现内部处理好
     * @param agentVersionDTO 待校验agentVersion对象
     * @return 合法：true 不合法：false
     */
    CheckResult checkCreateParameterAgentVersion(AgentVersionDTO agentVersionDTO);

    /**
     * 将给定AgentVersionDTO对象转化为AgentVersionPO对象
     * @param agentVersionDTO 待转化AgentVersionDTO对象
     * @return 将给定AgentVersionDTO对象转化为的AgentVersionPO对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    AgentVersionPO AgentVersionDTO2AgentVersionPO(AgentVersionDTO agentVersionDTO);

    /**
     * 将给定AgentVersionPO对象转化为AgentVersionDO对象
     * @param agentVersionPO 待转化AgentVersionPO对象
     * @return 将给定AgentVersionPO对象转化为的AgentVersionDO对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    AgentVersionDO agentVersionPO2AgentVersionDO(AgentVersionPO agentVersionPO);

    /**
     * 将给定AgentVersionPO对象集转化为AgentVersionDO对象集
     * @param agentVersionPOList 待转化 AgentVersionPO 对象集
     * @return 返回将给定AgentVersionPO对象集转化为的AgentVersionDO对象集
     */
    List<AgentVersionDO> agentVersionPOList2AgentVersionDOList(List<AgentVersionPO> agentVersionPOList);

    /**
     * 校验更新agentVersion方法对应的参数"agentVersion对象"信息是否合法
     * 注：该操作不应抛出异常，校验过程中出现异常需要对应实现内部处理好
     * @param agentVersionDTO 待校验agentVersion对象
     * @return 合法：true 不合法：false
     */
    CheckResult checkUpdateParameterAgentVersion(AgentVersionDTO agentVersionDTO);

}
