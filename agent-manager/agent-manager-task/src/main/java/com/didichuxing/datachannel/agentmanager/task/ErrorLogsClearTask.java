package com.didichuxing.datachannel.agentmanager.task;

import com.didichuxing.datachannel.agentmanager.core.errorlogs.ErrorLogsManageService;
import com.didichuxing.datachannel.agentmanager.core.errorlogs.impl.ErrorLogsManageServiceImpl;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;
import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.job.core.job.Job;
import com.didiglobal.logi.job.core.job.JobContext;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Task(name = "ErrorLogsClearTask", description = "ErrorLogsClearTask", cron = "0 0 1 * * ?", autoRegister = true, timeout = 300, consensual = ConsensualEnum.RANDOM)
public class ErrorLogsClearTask implements Job {

    private static final ILog logger = LogFactory.getLog(ErrorLogsClearTask.class);

    @Autowired
    private ErrorLogsManageService errorLogsManageService;

    @Override
    public TaskResult execute(JobContext jobContext) {
        logger.info("start to run ErrorLogsClearTask");
        try {
            errorLogsManageService.clearExpireErrorLogs(7);
        } catch (Exception ex) {
            logger.error(String.format(" delete expire error logs error, root cause is: %s", ex.getMessage()), ex);
        }
        logger.info("run ErrorLogsClearTask finish.");
        return TaskResult.buildSuccess();
    }

}