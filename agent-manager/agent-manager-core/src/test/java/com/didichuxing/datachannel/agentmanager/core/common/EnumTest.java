package com.didichuxing.datachannel.agentmanager.core.common;

import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.AgentMetricField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumTest {

    @Test
    public void fromStringTest() {
        AgentMetricField f = AgentMetricField.fromString("a");
        Assertions.assertNull(f);
        AgentMetricField sendCount = AgentMetricField.fromString("SEND_COUNT");
        Assertions.assertEquals(AgentMetricField.SEND_COUNT, sendCount);
        AgentMetricField sendByte = AgentMetricField.fromString("send_byte");
        Assertions.assertEquals(AgentMetricField.SEND_BYTE, sendByte);
    }
}
