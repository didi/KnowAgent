package com.didichuxing.datachannel.agentmanager.core.agent.operation.task;

import com.didichuxing.datachannel.agentmanager.core.ApplicationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Rollback
public class AgentOperationTaskManageServiceTest extends ApplicationTests {

    @Autowired
    private AgentOperationTaskManageService agentOperationTaskManageService;

    @Test
    public void testUpdateAgentOperationTasks() {
        agentOperationTaskManageService.updateAgentOperationTasks();

    }
}
