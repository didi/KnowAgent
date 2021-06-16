package com.didichuxing.datachannel.agentmanager.core.agent.health.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health.AgentHealthDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.health.AgentHealthPO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.core.agent.health.AgentHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentHealthMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
public class AgentHealthManageServiceImpl implements AgentHealthManageService {

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private AgentHealthMapper agentHealthDAO;

    @Override
    @Transactional
    public Long createInitialAgentHealth(Long savedAgentId, String operator) {
        return this.handleCreateInitialAgentHealth(savedAgentId, operator);
    }

    /**
     * 根据给定Agent 对象 id 值创建初始AgentHealth对象
     * @param agentId Agent 对象 id 值
     * @param operator 操作人
     * @return 返回创建的Agent健康对象 id 值
     */
    private Long handleCreateInitialAgentHealth(Long agentId, String operator) {
        AgentDO agentDO = agentManageService.getById(agentId);
        if(null == agentDO) {
            throw new ServiceException(
                    String.format("系统中不存在id={%d}的Agent对象", agentId),
                    ErrorCodeEnum.AGENT_NOT_EXISTS.getCode()
            );
        }
        AgentHealthPO agentHealthPO = buildInitialAgentHealthPO(agentDO, operator);
        agentHealthDAO.insert(agentHealthPO);
        return agentHealthPO.getId();
    }

    /**
     * 根据已创建AgentDO对象构建其关联初始化AgentHealthPO对象
     * @param agentDO 已创建AgentDO对象
     * @param operator 操作人
     * @return 返回根据已创建AgentDO对象构建的其关联初始化AgentHealthPO对象
     */
    private AgentHealthPO buildInitialAgentHealthPO(AgentDO agentDO, String operator) {
        if(null == agentDO) {
            throw new ServiceException(
                    String.format(
                            "class=AgentHealthManageServiceImpl||method=buildInitialAgentHealthPO||msg={%s}",
                            "入参agentDO不可为空"
                    ),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        AgentHealthPO agentHealthPO = new AgentHealthPO();
        agentHealthPO.setAgentHealthDescription(StringUtils.EMPTY);
        agentHealthPO.setAgentHealthLevel(AgentHealthLevelEnum.GREEN.getCode());
        agentHealthPO.setAgentId(agentDO.getId());
        agentHealthPO.setLastestErrorLogsExistsCheckHealthyTime(System.currentTimeMillis());
        agentHealthPO.setOperator(CommonConstant.getOperator(operator));
        return agentHealthPO;
    }

    @Override
    @Transactional
    public void deleteByAgentId(Long agentId, String operator) {
        this.handleDeleteByAgentId(agentId, operator);
    }

    @Override
    public AgentHealthDO getByAgentId(Long agentId) {
        AgentHealthPO agentHealthPO = agentHealthDAO.selectByAgentId(agentId);
        return ConvertUtil.obj2Obj(agentHealthPO, AgentHealthDO.class);
    }

    @Override
    @Transactional
    public void updateAgentHealth(AgentHealthDO agentHealthDO, String operator) {
        this.handleUpdateAgentHealth(agentHealthDO, operator);
    }

    /**
     * 更新给定AgentHealthDO对象
     * @param agentHealthDO 待更新AgentHealthDO对象
     * @param operator 操作人
     */
    private void handleUpdateAgentHealth(AgentHealthDO agentHealthDO, String operator) {
        if(null == agentHealthDAO.selectByPrimaryKey(agentHealthDO.getId())) {
            throw new ServiceException(
                    String.format("AgentHealth={id=%d}在系统中不存在", agentHealthDO.getId()),
                    ErrorCodeEnum.AGENT_HEALTH_NOT_EXISTS.getCode()
            );
        }
        AgentHealthPO agentHealthPO = ConvertUtil.obj2Obj(agentHealthDO, AgentHealthPO.class);
        agentHealthPO.setOperator(CommonConstant.getOperator(operator));
        agentHealthDAO.updateByPrimaryKey(agentHealthPO);
    }

    /**
     * 根据Agent对象id值删除对应AgentHealth对象
     * @param agentId Agent对象id值
     * @param operator 操作人
     */
    private void handleDeleteByAgentId(Long agentId, String operator) {
        AgentHealthPO agentHealthPO = agentHealthDAO.selectByAgentId(agentId);
        if(null == agentHealthPO) {
            throw new ServiceException(
                    String.format("根据给定Agent对象id={%d}删除对应AgentHealth对象失败，原因为：系统中不存在agentId={%d}的AgentHealth对象", agentId, agentId),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        agentHealthDAO.deleteByAgentId(agentId);
    }

}
