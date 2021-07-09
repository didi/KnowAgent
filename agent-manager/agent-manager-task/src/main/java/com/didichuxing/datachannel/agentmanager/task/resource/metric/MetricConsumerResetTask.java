package com.didichuxing.datachannel.agentmanager.task.resource.metric;

import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.MetricService;
import com.didiglobal.logi.auvjob.annotation.Task;
import com.didiglobal.logi.auvjob.core.job.Job;
import com.didiglobal.logi.auvjob.core.job.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Task(name = "MetricClearTask", description = "定期同步agent配置的metric流、error logs流的消费端，每10分钟执行一次",
        cron = "0 0/10 * * * ?", autoRegister = true)
public class MetricConsumerResetTask implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricClearTask.class);

    @Autowired
    private MetricService metricService;

    @Override
    public Object execute(JobContext jobContext) throws Exception {
        LOGGER.info(String.format("class=MetricConsumerResetTask||method=execute||msg=%s", "consumer reset started"));
        metricService.resetMetricConsumers();
        LOGGER.info(String.format("class=MetricConsumerResetTask||method=execute||msg=%s", "cleanEnd"));
        return "success";
    }
}
