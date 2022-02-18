package com.didichuxing.datachannel.agent.engine.bean;

import com.didichuxing.datachannel.agent.engine.metrics.source.AgentStatistics;

public class GlobalProperties {

    private static volatile AgentStatistics agentStatistics;

    public static void setAgentStatistics(AgentStatistics agentStatistics) {
        GlobalProperties.agentStatistics = agentStatistics;
    }

    public static AgentStatistics getAgentStatistics() {
        return agentStatistics;
    }
}
