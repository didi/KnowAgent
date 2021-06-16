package com.didichuxing.datachannel.agentmanager.thirdpart.host.extension;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostAgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.host.HostAgentPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.host.HostPO;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.util.Comparator;

import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * 主机管理服务扩展接口
 */
public interface HostManageServiceExtension {

    /**
     * 校验添加主机方法对应的参数"主机对象"信息是否合法
     * 注：该操作不应抛出异常，校验过程中出现异常需要对应实现内部处理好
     * @param host 待校验主机对象
     * @return 合法：true 不合法：false
     */
    CheckResult checkCreateParameterHost(HostDO host);

    /**
     * 校验修改主机方法对应的参数"主机对象"信息是否合法
     * 注：该操作不应抛出异常，校验过程中出现异常需要对应实现内部处理好
     * @param host 待校验主机对象
     * @return 合法：true 不合法：false
     */
    CheckResult checkModifyParameterHost(HostDO host);

    /**
     * 根据给定源主机对象 sourceHost & 待更新主机对象 targetHost，进行各属性值对比，返回最终的待更新的主机对象
     * @param sourceHost 源主机对象
     * @param targetHost 待更新主机对象
     * @return 返回根据给定源主机对象 sourceHost & 待更新主机对象 targetHost，进行各属性值对比，得到最终的待更新主机对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    HostDO updateHost(HostDO sourceHost, HostDO targetHost) throws ServiceException;

    /**
     * 将给定hostPO对象转化为Host对象
     * @param hostPO 待转化 hostPO 对象
     * @return hostPO转化后得到的Host对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    HostDO hostPO2HostDO(HostPO hostPO) throws ServiceException;

    /**
     * 将给定host对象转化为待持久化HostPO对象
     * @param host 待转化 host 对象
     * @return host转化后得到的HostPO对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    HostPO host2HostPO(HostDO host) throws ServiceException;

    /**
     * 将给定HostAgentPO对象转化为HostAgentDO对象
     * @param hostAgentPO 待转化HostAgentPO对象
     * @return 返回将给定HostAgentPO对象转化为的HostAgentDO对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    HostAgentDO hostAgentPO2HostAgentDO(HostAgentPO hostAgentPO) throws ServiceException;

    /**
     * 将给定HostPO对象集转化为HostDO对象集
     * @param hostPOList 待转化HostPO对象集
     * @return 返回将给定HostPO对象集转化为的HostDO对象集
     */
    List<HostDO> hostPOList2HostDOList(List<HostPO> hostPOList);

    /**
     * 将HostAgentPO对象集转化为HostAgentDO对象集
     * @param hostAgentPOList 待转化HostAgentPO对象集
     * @return 返回将HostAgentPO对象集转化为的HostAgentDO对象集
     */
    List<HostAgentDO> hostAgentPOList2HostAgentDOList(List<HostAgentPO> hostAgentPOList);

}
