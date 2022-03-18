package com.didichuxing.datachannel.agent.source.log.monitor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.AbstractTask;
import com.didichuxing.datachannel.agent.engine.component.TaskComponent;
import com.didichuxing.datachannel.agent.engine.monitor.Monitor;
import com.didichuxing.datachannel.agent.engine.service.DefaultThreadFactory;
import com.didichuxing.datachannel.agent.source.log.LogSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 校验文件是否可以关闭
 * @author: huangjw
 * @Date: 2019-07-24 16:13
 */
public enum FileCloseMonitor implements Monitor {
                                                 INSTANCE;

private static final Logger LOGGER = LoggerFactory.getLogger("monitor");private static final int                     PERIOD           = 2;

    private static ScheduledExecutorService      scheduledService = null;

    private ConcurrentHashMap<String, LogSource> logSourceMap     = new ConcurrentHashMap<>();

    private static volatile boolean              running          = false;

    @Override
    public boolean register(TaskComponent task) {
        if (!running) {
            return false;
        }
        LogSource logSource = (LogSource) ((AbstractTask) task).getSource();

        LOGGER.info("begin to register logSource to FileCloseMonitor. logSource is " + logSource.getUniqueKey());
        logSourceMap.put(logSource.getUniqueKey(), logSource);
        LOGGER.info("register logSource success!");
        return true;
    }

    @Override
    public boolean unregister(TaskComponent task) {
        if (!running) {
            return false;
        }
        LogSource logSource = (LogSource) ((AbstractTask) task).getSource();

        LOGGER.info("begin to unregister logSource from FileCloseMonitor. logSource is " + logSource.getUniqueKey());
        logSourceMap.remove(logSource.getUniqueKey());
        LOGGER.info("unregister logSource success!");
        return true;
    }

    @Override
    public void start() {
        LOGGER.info("begin to start FileCloseMonitor.");
        if (running) {
            return;
        }
        try {
            scheduledService = new ScheduledThreadPoolExecutor(1,
                                                               new DefaultThreadFactory("ScheduleCloseFile-Monitor-schedule"));
            running = true;
        } catch (Throwable e) {
            LogGather.recordErrorLog("FileCloseMonitor error!", "init ScheduleFileMonitor service error!", e);
        }

        scheduledService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                LOGGER.info("begin to check files to close!");
                for (LogSource logSource : logSourceMap.values()) {
                    try {
                        logSource.closeFiles();
                    } catch (Throwable e) {
                        LOGGER.warn("FileCloseMonitor error!",
                                    "scheduledService thread run error! logSource is " + logSource, e);
                    }
                }
            }
        }, 0, PERIOD, TimeUnit.MINUTES);
        LOGGER.info("init schedule watchThread success!");
    }

    @Override
    public void stop() {
        if (logSourceMap.size() != 0) {
            return;
        }
        running = false;
        LOGGER.info("begin to stop fileclose monitor!");
        logSourceMap.clear();
        scheduledService.shutdown();
        LOGGER.info("stop fileclose monitor success!");
    }
}
