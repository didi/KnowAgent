package com.didichuxing.datachannel.agent.integration.test.agent;

import java.io.File;
import java.util.List;

import com.didichuxing.datachannel.agent.common.configs.v2.AgentConfig;
import com.didichuxing.datachannel.agent.integration.test.verify.DataVerifyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: log-agent控制器
 * @author: huangjw
 * @Date: 19/2/13 14:11
 */
public class Controller {

    private static final Logger LOGGER   = LoggerFactory.getLogger(Controller.class.getName());
    private String              local    = System.getProperty("user.home") + File.separator
                                           + "conf.local";

    AgentConfig                 agentConfig;

    private static Controller   instance = new Controller();

    public static Controller getInstance() {
        return instance;
    }

    public AgentConfig getAgentConfig() {
        return agentConfig;
    }

    public void setAgentConfig(AgentConfig agentConfig) {
        this.agentConfig = agentConfig;
    }

    public void init(List<DataVerifyConfig> configs) {
    }

    public void stop() {
    }
}
