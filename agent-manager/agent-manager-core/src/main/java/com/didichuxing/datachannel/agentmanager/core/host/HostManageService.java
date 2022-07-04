package com.didichuxing.datachannel.agentmanager.core.host;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostAgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;

import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * 主机管理服务接口
 */
public interface HostManageService {

    /**
     * 创建一个主机对象
     *
     * @param host     待创建主机对象
     * @param operator 操作人
     * @return 创建成功的主机对象id
     */
    Long createHost(HostDO host, String operator);

    /**
     * 删除一个主机对象
     *
     * @param hostId                     待删除主机对象id
     * @param ignoreUncompleteCollect    是否忽略待删除主机N上是否存在未被采完日志或待删除主机存在未采集完日志的Agent，
     *                                   如该参数设置为true，表示即使待删除主机上存在未被采完日志或待删除主机存在未采集完日志的Agent，也会卸载Agent & 删除该主机对象
     *                                   如该参数设置为true，表示当待删除主机上存在未被采完日志或待删除主机存在未采集完日志的Agent，将终止删除操作，返回对应错误信息 & 错误码
     * @param cascadeDeleteAgentIfExists 待删除主机对象存在关联Agent对象时，是否级联删除关联Agent对象，true：删除 false：不删除（并抛出异常）
     * @param operator                   操作人
     */
    void deleteHost(Long hostId, boolean ignoreUncompleteCollect, boolean cascadeDeleteAgentIfExists, String operator);

    /**
     * 更新一个主机对象
     *
     * @param host     待更新主机对象
     * @param operator 操作人
     * @return 操作是否成功
     */
    void updateHost(HostDO host, String operator);

    /**
     * 根据主机名获取对应主机对象
     *
     * @param hostName 主机名
     * @return 主机名对应的主机对象
     */
    HostDO getHostByHostName(String hostName);

    /**
     * 根据 ip 获取对应主机对象集
     *
     * @param ip 主机 ip
     * @return ip 对应的主机对象集
     */
    List<HostDO> getHostByIp(String ip);

    /**
     * 根据主机名获取对应主机上所有容器对象集
     *
     * @param hostName 主机名
     * @return 主机名对应主机上所有容器对象集
     */
    List<HostDO> getContainerListByParentHostName(String hostName);

    /**
     * @return 返回全量主机对象集
     */
    List<HostDO> list();

    /**
     * 根据服务id查询该服务下的主机列表
     *
     * @param serviceId 服务id
     * @return 返回根据服务id查询到的该服务下的主机列表
     */
    List<HostDO> getHostsByServiceId(Long serviceId);

    List<HostDO> getHostsByPodId(Long podId);

    /**
     * 根据主机id获取对应主机对象
     *
     * @param id 主机id
     * @return 返回根据主机id获取到的对应主机对象
     */
    HostDO getById(Long id);

    /**
     * 根据给定参数分页查询结果集
     *
     * @param hostPaginationQueryConditionDO 分页查询条件
     * @return 返回根据给定参数分页查询到的结果集
     */
    List<HostAgentDO> paginationQueryByConditon(HostPaginationQueryConditionDO hostPaginationQueryConditionDO);

    /**
     * 根据给定参数查询满足条件的结果集总数量
     *
     * @param hostPaginationQueryConditionDO 查询条件
     * @return 返回根据给定参数查询到的满足条件的结果集总数量
     */
    Integer queryCountByCondition(HostPaginationQueryConditionDO hostPaginationQueryConditionDO);

    /**
     * 获取去重后系统全量机器单元信息集
     *
     * @return 返回系统全量机器单元信息集
     */
    List<String> getAllMachineZones();

    /**
     * 根据给定日志采集任务对象 id 获取该日志采集任务待采集的主机对象集
     *
     * @param logCollectTaskId 日志采集任务 id
     * @return 返回根据给定日志采集任务对象 id 获取到的该日志采集任务待采集的主机对象集
     */
    List<HostDO> getHostListByLogCollectTaskId(Long logCollectTaskId);

    List<HostDO> getHostListContainsAgentByLogCollectTaskId(Long logCollectTaskId);

    /**
     * 获取给定agent关联的主机列表
     *
     * @param agentId agent 对象 id
     * @return 返回给定agent关联的主机列表
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    List<HostDO> getRelationHostListByAgentId(Long agentId) throws ServiceException;

    /**
     * 获取给定agent关联的主机列表
     *
     * @param agentDO AgentDO 对象
     * @return 返回给定agent关联的主机列表
     */
    List<HostDO> getRelationHostListByAgent(AgentDO agentDO);

    /**
     * @return 返回系统全量主机数
     */
    Long countAllHost();

    /**
     * @return 返回系统全量容器数
     */
    Long countAllContainer();

    /**
     * @return 返回全量故障主机数
     */
    Long countAllFaultyHost();

}
