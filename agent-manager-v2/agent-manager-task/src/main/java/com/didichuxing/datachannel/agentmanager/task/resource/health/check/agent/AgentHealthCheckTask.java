package com.didichuxing.datachannel.agentmanager.task.resource.health.check.agent;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didiglobal.logi.auvjob.annotation.Task;
import com.didiglobal.logi.auvjob.core.job.Job;
import com.didiglobal.logi.auvjob.core.job.JobContext;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务健康监控任务
 */
@Task(name = "AgentHealthCheckTask", description = "定时检查系统中所有的 Agent 健康度，并将检查结果记录至表 tb_agent 中，检查周期为 5分钟/次 ",
        cron = "0 0/10 * * * ?", autoRegister = true)
public class AgentHealthCheckTask implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger("AgentHealthCheckTask");
    private ExecutorService threadPool = Executors.newFixedThreadPool(2);

    @Autowired
    private AgentManageService agentManageService;

    @Override
    public Object execute(JobContext jobContext) throws Exception {
        List<AgentDO> agentDOList = agentManageService.list();
        if (CollectionUtils.isEmpty(agentDOList)) {
            LOGGER.warn("class=AgentHealthCheckTask||method=execute||msg=AgentDO List task is empty!!");
            return "task empty!!";
        }
        List<Future> futures = Lists.newArrayList();
        for (AgentDO agentDO : agentDOList) {
            futures.add(threadPool.submit(() -> {
                AgentHealthLevelEnum agentHealthLevelEnum = agentManageService.checkAgentHealth(agentDO);
                LOGGER.info("class=AgentHealthCheckTask||method=execute||agentId={}||"
                        + "agentHealthLevelEnum={}", agentDO.getId(), agentHealthLevelEnum.getDescription());
            }));
        }
        for (Future future : futures) {
            future.get();
        }
        return "success!! size=" + agentDOList.size();
    }

}
