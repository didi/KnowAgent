package com.didichuxing.datachannel.agentmanager.core.agent.metrcis;//package com.didichuxing.datachannel.agentmanager.core.agent.version;

import com.didichuxing.datachannel.agentmanager.core.ApplicationTests;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Rollback
public class AgentMetrcisManageServiceTest extends ApplicationTests {

    @Test
    public void test() {
        assert 1 == -1;
    }

}
