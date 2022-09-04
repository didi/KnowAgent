package com.didichuxing.datachannel.agentmanager.task;

import com.didichuxing.datachannel.agentmanager.core.errorlogs.ErrorLogsManageService;
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
@Task(name = "ErrorLogsWriteTask", description = "ErrorLogsWriteTask", cron = "*/5 * * * * ?", autoRegister = true, timeout = 300, consensual = ConsensualEnum.RANDOM)
public class ErrorLogsWriteTask implements Job {

    private static final ILog logger = LogFactory.getLog(ErrorLogsWriteTask.class);

    @Autowired
    private ErrorLogsManageService errorLogsManageService;

    @Override
    public TaskResult execute(JobContext jobContext) {
        logger.info("start to run ErrorLogsWriteTask");
        try {
            errorLogsManageService.consumeAndWriteErrorLogs();;
        } catch (Exception ex) {
            logger.error(String.format(" write error logs to db error, root cause is: %s", ex.getMessage()), ex);
        }
        logger.info("run ErrorLogsWriteTask finish.");
        return TaskResult.buildSuccess();
    }

}