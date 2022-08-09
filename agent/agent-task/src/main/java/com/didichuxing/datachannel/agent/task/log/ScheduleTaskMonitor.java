package com.didichuxing.datachannel.agent.task.log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.didichuxing.datachannel.agent.engine.component.TaskComponent;
import com.didichuxing.datachannel.agent.engine.monitor.Monitor;
import com.didichuxing.datachannel.agent.engine.service.DefaultThreadFactory;

import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 兼容弹性云场景
 * @author: huangjw
 * @Date: 2020-03-31 14:13
 */
public enum ScheduleTaskMonitor implements Monitor {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger("monitor");
    private static ScheduledExecutorService scheduledService = null;

    /**
     * key: key of LogModel
     * value: LogModel
     */
    private static ConcurrentHashMap<String, LogModel> modelMap = new ConcurrentHashMap<>();

    private static volatile boolean running = false;

    private final Object lock = new Object();

    @Override
    public boolean register(TaskComponent component) {
        LogModel logModel = (LogModel) component;
        return register(logModel);
    }

    public boolean register(LogModel logModel) {
        LOGGER.info("begin to register logModel to scheduleTaskMonitor. logModel's key is " + logModel.getUniqueKey());
        if (!running) {
            LOGGER.warn("register logModel failed for scheduleTaskMonitor is stopping!");
            return false;
        }
        synchronized (lock) {
            modelMap.put(logModel.getUniqueKey(), logModel);
        }
        LOGGER.info("register logModel success!");
        return true;
    }

    @Override
    public boolean unregister(TaskComponent component) {
        LogModel logModel = (LogModel) component;
        return unregister(logModel);
    }

    public boolean unregister(LogModel logModel) {
        LOGGER.info("begin to unregister logModel from scheduleTaskMonitor. logModel's key is "
                + logModel.getUniqueKey());
        if (!running) {
            LOGGER.warn("unregister logModel failed for scheduleTaskMonitor is stopping!");
            return false;
        }
        synchronized (lock) {
            modelMap.remove(logModel.getUniqueKey());
        }
        LOGGER.info("unregister logModel success!");
        return true;
    }

    @Override
    public void start() {
        if (running) {
            return;
        }
        try {
            scheduledService = new ScheduledThreadPoolExecutor(1,
                    new DefaultThreadFactory("ScheduleTask-Monitor-schedule"));
            running = true;
        } catch (Exception e) {
            LogGather.recordErrorLog("ScheduleTaskMonitor error!", "init ScheduleTaskMonitor service error!", e);
        }

        startRelatedTaskScheduleMonitor();
    }

    /**
     * 启动定时任务检测文件滚动
     */
    private void startRelatedTaskScheduleMonitor() {
        LOGGER.info("begin to init schedule watchThread to monitor filePath change.");
        scheduledService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                LOGGER.info("begin to check all the logModel for fileChange.");

                try {
                    synchronized (lock) {
                        for (LogModel logModel : modelMap.values()) {
                            logModel.checkTask();
                        }
                    }
                } catch (Throwable t) {
                    LogGather.recordErrorLog("ScheduleDirMonitor error", "process error", t);
                }
                LOGGER.info("success to check all the logModel for fileChange.");
            }
        }, 0, 5, TimeUnit.MINUTES);
        LOGGER.info("init schedule watchThread success!");
    }

    @Override
    public void stop() {
        if (modelMap.size() != 0) {
            return;
        }
        running = false;
        LOGGER.info("begin to stop related task monitor!");
        modelMap.clear();
        scheduledService.shutdown();
        LOGGER.info("stop related task monitor success!");
    }
}
