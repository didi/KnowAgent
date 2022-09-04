package com.didichuxing.datachannel.agentmanager.task;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class Worker {

    private static final ILog logger = LogFactory.getLog(Worker.class);

    public void doWork() {
        logger.info("do work().");
    }

}
