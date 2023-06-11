package com.didichuxing.datachannel.agentmanager.task.resource.metric;

import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;
import com.didiglobal.logi.auvjob.annotation.Task;
import com.didiglobal.logi.auvjob.core.job.Job;
import com.didiglobal.logi.auvjob.core.job.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Task(name = "MetricClearTask", description = "定时清除系统中超过7天的metric，每一天执行一次",
        cron = "0 0 * * * ?", autoRegister = true)
public class MetricClearTask implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricClearTask.class);

    @Value("${metrics.expire.days}")
    private Integer metricsExpireDays;

    @Autowired
    private MetricsManageService metricsManageService;

    @Override
    public Object execute(JobContext jobContext) throws Exception {
        LOGGER.info(String.format("class=MetricClearTask||method=execute||msg=%s", "cleanStart"));
        metricsManageService.clearExpireMetrics(metricsExpireDays);

        //TODO：错误日志 清理

        LOGGER.info(String.format("class=MetricClearTask||method=execute||msg=%s", "cleanEnd"));
        return "success";
    }

}
