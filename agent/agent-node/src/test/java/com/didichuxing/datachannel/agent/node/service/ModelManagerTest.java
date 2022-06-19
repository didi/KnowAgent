package com.didichuxing.datachannel.agent.node.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.didichuxing.datachannel.agent.node.ConfigServiceTest;
import org.junit.Test;

import com.didichuxing.datachannel.agent.common.configs.v2.AgentConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-29 21:28
 */
public class ModelManagerTest extends ConfigServiceTest {

    @Test
    public void init() {
        AgentConfig agentConfig = getAgentConfig();
        ModelManager modelManager = new ModelManager();
        modelManager.init(agentConfig);
        assertEquals(modelManager.getModels().size(), 2);
    }

    @Test
    public void onChange() {
        AgentConfig oldAgentConfig = getAgentConfig();
        ModelManager modelManager = new ModelManager();
        modelManager.init(oldAgentConfig);

        AgentConfig newAgentConfig = getAgentConfig();
        newAgentConfig.getModelConfigs().add(getKafkaModelConfig());

        modelManager.onChange(newAgentConfig);
        assertEquals(modelManager.getModels().size(), 3);
        assertTrue(modelManager.getModels().containsKey(1));
        assertTrue(modelManager.getModels().containsKey(2));
        assertTrue(modelManager.getModels().containsKey(-1));
        assertTrue(!modelManager.getModels().containsKey(0));
    }

    @Test
    public void onChangeForAddModel() {
        AgentConfig oldAgentConfig = getAgentConfigWithOutModels();
        ModelManager modelManager = new ModelManager();
        modelManager.init(oldAgentConfig);

        AgentConfig newAgentConfig = getAgentConfig();
        newAgentConfig.getModelConfigs().add(getKafkaModelConfig());

        modelManager.onChange(newAgentConfig);
    }

    @Test
    public void onChangeForAddSinkNum() {
        AgentConfig oldAgentConfig = getAgentConfig();
        ModelManager modelManager = new ModelManager();
        modelManager.init(oldAgentConfig);

        AgentConfig newAgentConfig = getAgentConfig();
        newAgentConfig.getModelConfigs().get(1).getTargetConfig().setSinkNum(5);
        newAgentConfig.getModelConfigs().get(1).setVersion(1);
        modelManager.onChange(newAgentConfig);
    }

    @Test
    public void onChangeForDelSinkNum() {
        AgentConfig oldAgentConfig = getAgentConfig();
        oldAgentConfig.getModelConfigs().get(1).getTargetConfig().setSinkNum(5);
        ModelManager modelManager = new ModelManager();
        modelManager.init(oldAgentConfig);

        AgentConfig newAgentConfig = getAgentConfig();
        newAgentConfig.getModelConfigs().get(1).getTargetConfig().setSinkNum(1);
        newAgentConfig.getModelConfigs().get(1).setVersion(1);
        modelManager.onChange(newAgentConfig);
    }

    public AgentConfig getAgentConfig() {
        AgentConfig agentConfig = new AgentConfig();
        agentConfig.setVersion(0);
        agentConfig.setHostname(CommonUtils.getHOSTNAME());
        agentConfig.setErrorLogConfig(getErrLogConfig());
        agentConfig.setMetricConfig(getMetricConfig());
        agentConfig.setLimitConfig(getLimitConfig());
        agentConfig.setOffsetConfig(getOffsetConfig());

        List<ModelConfig> list = new ArrayList<>();
        list.add(getKafkaModelConfig());
        agentConfig.setModelConfigs(list);
        return agentConfig;
    }

    public AgentConfig getAgentConfigWithOutModels() {
        AgentConfig agentConfig = new AgentConfig();
        agentConfig.setVersion(0);
        agentConfig.setHostname(CommonUtils.getHOSTNAME());
        agentConfig.setErrorLogConfig(getErrLogConfig());
        agentConfig.setMetricConfig(getMetricConfig());
        agentConfig.setLimitConfig(getLimitConfig());
        agentConfig.setOffsetConfig(getOffsetConfig());

        List<ModelConfig> list = new ArrayList<>();
        agentConfig.setModelConfigs(list);
        return agentConfig;
    }
}
