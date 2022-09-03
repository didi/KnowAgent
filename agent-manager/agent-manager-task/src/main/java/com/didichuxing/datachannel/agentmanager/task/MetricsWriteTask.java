package com.didichuxing.datachannel.agentmanager.task;

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
@Task(name = "MetricsWriteTask", description = "MetricsWriteTask", cron = "0 0/5 * * * ? *", autoRegister = true, timeout = 300, consensual = ConsensualEnum.RANDOM)
public class MetricsWriteTask implements Job {

    private static final ILog logger = LogFactory.getLog(MetricsWriteTask.class);

    @Autowired
    private MetricsManageService metricsManageService;

    @Override
    public TaskResult execute(JobContext jobContext) {
        logger.info("start to run MetricsWriteTask");
        try {
            metricsManageService.consumeAndWriteMetrics();
        } catch (Exception ex) {
            logger.error(String.format(" write metrics to db error, root cause is: %s", ex.getMessage()), ex);
        }
        logger.info("run MetricsWriteTask finish.");
        return TaskResult.buildSuccess();
    }

}