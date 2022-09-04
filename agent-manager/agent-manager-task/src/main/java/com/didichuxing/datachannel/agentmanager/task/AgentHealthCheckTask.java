package com.didichuxing.datachannel.agentmanager.task;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.health.AgentHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.health.impl.AgentHealthManageServiceImpl;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.impl.AgentManageServiceImpl;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.job.core.job.Job;
import com.didiglobal.logi.job.core.job.JobContext;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.Future;

@Component
@Task(name = "AgentHealthCheckTask", description = "AgentHealthCheckTask", cron = "0 0/1 * * * ? *", autoRegister = true, timeout = 300, consensual = ConsensualEnum.RANDOM)
public class AgentHealthCheckTask implements Job {

    private static final ILog logger = LogFactory.getLog(AgentHealthCheckTask.class);

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private AgentHealthManageService agentHealthManageService;

    @Override
    public TaskResult execute(JobContext jobContext) {
        logger.info("start to run AgentHealthCheckTask");
        try {
            List<AgentDO> agentDOList = agentManageService.list();
            if (CollectionUtils.isEmpty(agentDOList)) {
                logger.info("class=AgentHealthCheckTask||method=execute||msg=AgentDO List task is empty!!");
            }
            for (AgentDO agentDO : agentDOList) {
                AgentHealthLevelEnum agentHealthLevelEnum = agentHealthManageService.checkAgentHealth(agentDO);
                logger.info("class=AgentHealthCheckTask||method=execute||agentId={}||"
                        + "agentHealthLevelEnum={}", agentDO.getId(), agentHealthLevelEnum.getDescription());
            }
        } catch (Exception ex) {
            logger.error(String.format(" check agent health error, root cause is: %s", ex.getMessage()), ex);
        }
        logger.info("run AgentHealthCheckTask finish.");
        return TaskResult.buildSuccess();
    }

}