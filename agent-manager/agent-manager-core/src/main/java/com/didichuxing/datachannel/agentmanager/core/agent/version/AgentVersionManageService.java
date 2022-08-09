package com.didichuxing.datachannel.agentmanager.core.agent.version;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.agent.version.AgentVersionDTO;

import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * Agent版本管理服务接口
 */
public interface AgentVersionManageService {

    /**
     * 创建一个 AgentVersion 对象
     * @param agentVersionDTO  待创建 AgentVersion 对象
     * @param operator 操作人
     * @return 创建成功的 agentVersion 对象id
     */
    Long createAgentVersion(AgentVersionDTO agentVersionDTO, String operator);

    /**
     * 根据agent版本号信息获取对应agent版本对象
     * @param version agent版本号
     * @return 返回根据agent版本号信息获取到的对应agent版本对象
     */
    AgentVersionDO getByVersion(String version);

    /**
     * 根据agent id 信息获取对应agent版本对象
     * @param agentVersionId agent版本对象 id
     * @return 返回根据agent id 信息获取到的对应agent版本对象
     */
    AgentVersionDO getById(Long agentVersionId);

    /**
     * @return 返回系统全量AgentVersion对象集
     */
    List<AgentVersionDO> list();

    /**
     * 更新 AgentVersion 对象
     * @param agentVersionDTO 待更新 AgentVersion 对象
     * @param operator 操作人
     */
    void updateAgentVersion(AgentVersionDTO agentVersionDTO, String operator);

    /**
     * 根据 id 删除给定 agentVersion 对象
     * @param agentVersionIdList 待删除 agentVersion 对象 id 集
     * @param operator 操作人
     * 注：删除 AgentVersion 对象，其关联的文件将不被删除，如文件过多，过大，须对应清理
     */
    void deleteAgentVersion(List<Long> agentVersionIdList, String operator);

    /**
     * 根据条件分页查询 agentVersion 数据集
     * @param agentVersionPaginationQueryConditionDO 封装分页查询条件的 AgentVersionPaginationQueryConditionDO 对象
     * @return 返回根据条件分页查询得到的 agentVersion 数据集
     */
    List<AgentVersionDO> paginationQueryByConditon(AgentVersionPaginationQueryConditionDO agentVersionPaginationQueryConditionDO);

    /**
     * 根据条件查询符合条件 agentVersion 数据集数量
     * @param agentVersionPaginationQueryConditionDO 封装查询条件的 AgentVersionPaginationQueryConditionDO 对象
     * @return 返回根据条件查询得到的 agentVersion 数据集数量
     */
    Integer queryCountByCondition(AgentVersionPaginationQueryConditionDO agentVersionPaginationQueryConditionDO);

    /**
     * 下载给定 AgentVersion 对应 Agent 安装包文件
     * @param agentVersionId 待下载 AgentVersion id
     * @return 返回对应下载 url
     *
     * TODO：记录下载对应操作记录
     *
     */
    String getAgentInstallFileDownloadUrl(Long agentVersionId);

}
