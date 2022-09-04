package com.didichuxing.datachannel.agentmanager.task;

import com.alibaba.fastjson.JSON;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.job.Job;
import com.didiglobal.logi.job.core.job.JobContext;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobBroadcasWithParamtTest implements Job {

    private static final ILog logger = LogFactory.getLog(JobBroadcasWithParamtTest.class);

    @Autowired
    private Worker worker;

    @Override
    public TaskResult execute(JobContext jobContext) {
        logger.info("**************************************** JobBroadcasWithParamtTest start" + System.currentTimeMillis());

        System.err.println(
                String.format(
                        "params: %s, allWorkerCodes: %s, currentWorkerCode: %s",
                        jobContext.getParams(),
                        JSON.toJSONString(jobContext.getAllWorkerCodes()),
                        jobContext.getCurrentWorkerCode()
                )
        );

        worker.doWork();

        logger.info("**************************************** JobBroadcasWithParamtTest end" + System.currentTimeMillis());

        return TaskResult.buildSuccess();
    }
}