package com.didichuxing.datachannel.agentmanager.core.common;

import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.AgentMetricRDSField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumTest {

    @Test
    public void fromStringTest() {
        AgentMetricRDSField f = AgentMetricRDSField.fromString("a");
        Assertions.assertNull(f);
        AgentMetricRDSField sendCount = AgentMetricRDSField.fromString("SEND_COUNT");
        Assertions.assertEquals(AgentMetricRDSField.SEND_COUNT, sendCount);
        AgentMetricRDSField sendByte = AgentMetricRDSField.fromString("send_byte");
        Assertions.assertEquals(AgentMetricRDSField.SEND_BYTE, sendByte);
    }
}
