package com.didichuxing.datachannel.agentmanager.thirdpart.agent.manage.extension.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.operationtask.AgentOperationTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.AgentPO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.common.util.HttpUtils;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.manage.extension.AgentManageServiceExtension;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class DefaultAgentManageServiceExtensionImpl implements AgentManageServiceExtension {

    /**
     * 远程请求 agent 端口
     */
    @Value("${agent.http.request.port}")
    private Integer requestPort;

    @Override
    public CheckResult checkCreateParameterAgent(AgentDO agent) {

        //TODO：

        //TODO：校验待更新 agent 版本是否在系统维护的 agentVersion 列表存在，如不存在，表示非法

        return new CheckResult(true);

    }

    @Override
    public CheckResult checkDeleteParameterAgent(AgentDO agent) {

        //TODO：

        return new CheckResult(true);

    }

    @Override
    public AgentOperationTaskDO agent2AgentOperationTaskInstall(AgentDO agent) throws ServiceException {

        //TODO：

        return null;

    }

    @Override
    public AgentDO agentPO2AgentDO(AgentPO agentPO) throws ServiceException {
        if(null == agentPO) {
            throw new ServiceException(
                    String.format(
                            "class=AgentManageServiceExtensionImpl||method=agentPO2AgentDO||msg={%s}",
                            "入参agentPO对象不可为空"
                    ),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        AgentDO agent = null;
        try {
            agent = ConvertUtil.obj2Obj(agentPO, AgentDO.class);
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format(
                            "class=AgentManageServiceExtensionImpl||method=agentPO2AgentDO||msg={%s}",
                            String.format("AgentPO对象={%s}转化为Agent对象失败，原因为：%s", JSON.toJSONString(agentPO), ex.getMessage())
                    ),
                    ex,
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        if(null == agent) {
            throw new ServiceException(
                    String.format(
                            "class=AgentManageServiceExtensionImpl||method=agentPO2AgentDO||msg={%s}",
                            String.format("AgentPO对象={%s}转化为Agent对象失败", JSON.toJSONString(agentPO))
                    ),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        return agent;
    }

    @Override
    public CheckResult checkUpdateParameterAgent(AgentDO agentDO) {
        if(null == agentDO) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    "入参agentDO对象不可为空"
            );
        }
        if(null == agentDO.getId()) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    "入参agentDO对象id属性值不可为空"
            );
        }

        //TODO：校验待更新 agent 版本是否在系统维护的 agentVersion 列表存在，如不存在，表示非法

        return new CheckResult(true);
    }

    @Override
    public AgentDO updateAgent(AgentDO sourceAgent, AgentDO targetAgent) throws ServiceException {
        sourceAgent.setCpuLimitThreshold(targetAgent.getCpuLimitThreshold());
        sourceAgent.setByteLimitThreshold(targetAgent.getByteLimitThreshold());
        sourceAgent.setMetricsSendReceiverId(targetAgent.getMetricsSendReceiverId());
        sourceAgent.setMetricsSendTopic(targetAgent.getMetricsSendTopic());
        sourceAgent.setMetricsProducerConfiguration(targetAgent.getMetricsProducerConfiguration());
        sourceAgent.setErrorLogsSendReceiverId(targetAgent.getErrorLogsSendReceiverId());
        sourceAgent.setErrorLogsSendTopic(targetAgent.getErrorLogsSendTopic());
        sourceAgent.setErrorLogsProducerConfiguration(targetAgent.getErrorLogsProducerConfiguration());
        sourceAgent.setAdvancedConfigurationJsonString(targetAgent.getAdvancedConfigurationJsonString());
        return sourceAgent;
    }

    @Override
    public List<AgentDO> agentPOList2AgentDOList(List<AgentPO> agentPOList) {
        return ConvertUtil.list2List(agentPOList, AgentDO.class);
    }

    @Override
    public Result<List<String>> listFiles(String hostName, String path, String suffixRegular) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("path", path);
        contentMap.put("suffixRegular", suffixRegular);
        String result = HttpUtils.get(
                String.format("http://%s:%d/log-agent/path", hostName, requestPort),
                null,
                headers,
                JSON.toJSONString(contentMap)
        );
        if(StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Result.class);
        } else {
            return null;
        }
    }

    @Override
    public Result<String> readFileContent(String hostName, String path) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("path", path);
        String result = HttpUtils.get(
                String.format("http://%s:%d/log-agent/file-content", hostName, requestPort),
                null,
                headers,
                JSON.toJSONString(contentMap)
        );
        if(StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Result.class);
        } else {
            return null;
        }
    }

}
