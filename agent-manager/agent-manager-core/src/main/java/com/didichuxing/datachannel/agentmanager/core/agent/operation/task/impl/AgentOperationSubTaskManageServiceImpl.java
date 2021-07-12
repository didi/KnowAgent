package com.didichuxing.datachannel.agentmanager.core.agent.operation.task.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.operationtask.AgentOperationSubTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.operationtask.AgentOperationSubTaskPO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.core.agent.operation.task.AgentOperationSubTaskManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentOperationSubTaskMapper;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskSubStateEnum;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class AgentOperationSubTaskManageServiceImpl implements AgentOperationSubTaskManageService {

    @Autowired
    private AgentOperationSubTaskMapper agentOperationSubTaskDAO;

    @Override
    @Transactional
    public Long createAgentOperationSubTask(AgentOperationSubTaskDO agentOperationSubTaskDO, String operator) {
        return handleCreateAgentOperationSubTask(agentOperationSubTaskDO, operator);
    }

    /**
     * 创建 AgentOperationSubTask 对象
     * @param agentOperationSubTaskDO  待创建 AgentOperationSubTask 对象
     * @param operator 操作人
     * @return 创建成功的 AgentOperationSubTask 对象 id
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long handleCreateAgentOperationSubTask(AgentOperationSubTaskDO agentOperationSubTaskDO, String operator) throws ServiceException {
        AgentOperationSubTaskPO agentOperationSubTaskPO = ConvertUtil.obj2Obj(agentOperationSubTaskDO, AgentOperationSubTaskPO.class);
        agentOperationSubTaskPO.setOperator(CommonConstant.getOperator(operator));
        agentOperationSubTaskDAO.insert(agentOperationSubTaskPO);
        return agentOperationSubTaskPO.getId();
    }

    @Override
    public List<AgentOperationSubTaskDO> getByAgentOperationTaskId(Long agentOperationTaskId) {
        List<AgentOperationSubTaskPO> agentOperationSubTaskPOList = agentOperationSubTaskDAO.getByAgentOperationTaskId(agentOperationTaskId);
        if(CollectionUtils.isEmpty(agentOperationSubTaskPOList)) {
            return new ArrayList<>();
        } else {
            return ConvertUtil.list2List(agentOperationSubTaskPOList, AgentOperationSubTaskDO.class);
        }
    }

    @Override
    @Transactional
    public void updateAgentOperationSubTask(AgentOperationSubTaskDO agentOperationSubTaskDO) {
        AgentOperationSubTaskPO agentOperationSubTaskPO = ConvertUtil.obj2Obj(agentOperationSubTaskDO, AgentOperationSubTaskPO.class);
        agentOperationSubTaskDAO.updateByPrimaryKey(agentOperationSubTaskPO);
    }

    @Override
    public boolean unfinishedAgentOperationSubTaskExistsByAgentVersionId(Long agentVersionId) {
        List<AgentOperationSubTaskPO> agentOperationSubTaskPOList = agentOperationSubTaskDAO.selectByAgentVersionId(agentVersionId);
        if(CollectionUtils.isNotEmpty(agentOperationSubTaskPOList)) {
            for (AgentOperationSubTaskPO agentOperationSubTaskPO : agentOperationSubTaskPOList) {
                if(!AgentOperationTaskSubStateEnum.isFinished(agentOperationSubTaskPO.getExecuteStatus())) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public List<AgentOperationSubTaskDO> getByHostNameAndAgentStartupTime(String hostName, Long agentStartupTime) {
        Map<String, Object> params = new HashMap<>();
        params.put("hostName", hostName);
        params.put("agentStartupTime", agentStartupTime);
        List<AgentOperationSubTaskPO> agentOperationSubTaskPOList = agentOperationSubTaskDAO.getByHostNameAndAgentStartupTime(params);
        if(CollectionUtils.isEmpty(agentOperationSubTaskPOList)) {
            return new ArrayList<>();
        } else {
            return ConvertUtil.list2List(agentOperationSubTaskPOList, AgentOperationSubTaskDO.class);
        }
    }

}
