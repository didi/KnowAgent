package com.didichuxing.datachannel.agentmanager.task.resource.metric;

import com.didichuxing.datachannel.agentmanager.common.util.LogUtil;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.MetricService;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import com.didiglobal.logi.auvjob.annotation.Task;
import com.didiglobal.logi.auvjob.core.job.Job;
import com.didiglobal.logi.auvjob.core.job.JobContext;
import org.springframework.beans.factory.annotation.Autowired;

@Task(name = "MetricClearTask", description = "定时清除系统中超过7天的metric，每一天执行一次",
        cron = "0 0 * * * ?", autoRegister = true)
public class MetricClearTask implements Job {
    private static final ILog LOGGER = LogFactory.getLog(MetricClearTask.class);

    @Autowired
    private MetricService metricService;

    @Override
    public Object execute(JobContext jobContext) throws Exception {
        LOGGER.info(String.format(LogUtil.defaultLogFormat(), "cleanStart"));
        metricService.clear();
        LOGGER.info(String.format(LogUtil.defaultLogFormat(), "cleanEnd"));
        return "success";
    }

}
