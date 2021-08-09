package com.didichuxing.datachannel.agent.common;

import com.didichuxing.datachannel.agent.common.metrics.lib.DefaultMetricsSystem;
import com.didichuxing.datachannel.agent.engine.metrics.source.AgentStatistics;
import org.junit.Before;
import org.junit.Test;

public class MetricTest {
    private DefaultMetricsSystem metricsSystem = DefaultMetricsSystem.INSTANCE;

    @Before
    public void init() {
        DefaultMetricsSystem.initialize("Agent");
    }

    @Test
    public void getMetricsTest() {
        metricsSystem.register("base", "", new AgentStatistics("", null, null));
    }
}
