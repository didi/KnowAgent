package com.didichuxing.datachannel.agentmanager.task.resource.health.check.logcollecttask;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthDetailManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
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
@Task(name = "LogCollectTaskHealthCheckTask", description = "定时检查系统中所有的日志采集任务健康度，并将检查结果记录至表 tb_log_collect_task_health 中，检查周期为 5分钟/次 ",
        cron = "0 0/10 * * * ?", autoRegister = true)
public class LogCollectTaskHealthCheckTask implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger("LogCollectTaskHealthCheckTask");
    private ExecutorService threadPool = Executors.newFixedThreadPool(2);

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private LogCollectTaskHealthManageService logCollectTaskHealthManageService;

    @Override
    public Object execute(JobContext jobContext) throws Exception {
        List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageService.getAllLogCollectTask2HealthCheck();
        if (CollectionUtils.isEmpty(logCollectTaskDOList)) {
            LOGGER.warn("class=LogCollectTaskHealthCheckTask||method=execute||msg=LogCollectTaskDO List task is empty!!");
            return "task empty!!";
        }
        List<Future> futures = Lists.newArrayList();
        for (LogCollectTaskDO logCollectTaskDO : logCollectTaskDOList) {
            futures.add(threadPool.submit(() -> {
                LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum = logCollectTaskHealthManageService.checkLogCollectTaskHealth(logCollectTaskDO);
                LOGGER.info("class=LogCollectTaskHealthCheckTask||method=execute||logCollectTaskId={}||"
                        + "logCollectTaskHealthLevel={}", logCollectTaskDO.getId(), logCollectTaskHealthLevelEnum.getDescription());
                return logCollectTaskHealthLevelEnum;
            }));
        }
        for (Future future : futures) {
            future.get();
        }
        return "success!! size=" + logCollectTaskDOList.size();
    }

}
