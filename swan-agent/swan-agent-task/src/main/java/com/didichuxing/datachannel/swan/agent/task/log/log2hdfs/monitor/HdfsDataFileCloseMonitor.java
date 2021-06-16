package com.didichuxing.datachannel.swan.agent.task.log.log2hdfs.monitor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.didichuxing.datachannel.swan.agent.engine.component.TaskComponent;
import com.didichuxing.datachannel.swan.agent.engine.monitor.Monitor;
import com.didichuxing.datachannel.swan.agent.engine.service.DefaultThreadFactory;
import com.didichuxing.datachannel.swan.agent.task.log.log2hdfs.Log2HdfsTask;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import com.didichuxing.tunnel.util.log.LogGather;

/**
 * @description: hdfs 文件关闭校验逻辑
 * @author: huangjw
 * @Date: 2019-07-25 17:29
 */
public enum HdfsDataFileCloseMonitor implements Monitor {
                                                         INSTANCE;

    private static final ILog                       LOGGER           = LogFactory.getLog("monitor");

    private static final int                        PERIOD           = 2;

    private static ScheduledExecutorService         scheduledService = null;

    private ConcurrentHashMap<String, Log2HdfsTask> log2HdfsTaskMap  = new ConcurrentHashMap<>();

    private static volatile boolean                 running          = false;

    @Override
    public boolean register(TaskComponent task) {
        if (!running) {
            return false;
        }

        LOGGER.info("begin to register log2HdfsTask to HdfsDataFileCloseMonitor. log2HdfsTask is "
                    + task.getUniqueKey());
        log2HdfsTaskMap.put(task.getUniqueKey(), (Log2HdfsTask) task);
        LOGGER.info("register log2HdfsTask success!");
        return true;
    }

    @Override
    public boolean unregister(TaskComponent task) {
        if (!running) {
            return false;
        }

        LOGGER.info("begin to unregister log2HdfsTask from HdfsDataFileCloseMonitor. log2HdfsTask is "
                    + task.getUniqueKey());
        log2HdfsTaskMap.remove(task.getUniqueKey());
        LOGGER.info("unregister log2HdfsTask success!");
        return true;
    }

    @Override
    public void start() {
        LOGGER.info("begin to start HdfsDataFileCloseMonitor.");
        if (running) {
            return;
        }
        try {
            scheduledService = new ScheduledThreadPoolExecutor(1,
                                                               new DefaultThreadFactory("ScheduleCloseHdfsDataFile-Monitor-schedule"));
            running = true;
        } catch (Exception e) {
            LogGather.recordErrorLog("HdfsDataFileCloseMonitor error!", "init ScheduleCloseHdfsDataFile service error!",
                                     e);
        }

        scheduledService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                LOGGER.info("begin to check to file!");
                for (Log2HdfsTask task : log2HdfsTaskMap.values()) {
                    try {
                        task.sync();
                    } catch (Exception e) {
                        LogGather.recordErrorLog("HdfsDataFileCloseMonitor error!",
                                                 "task sync error! task'uniqueKey is " + task.getUniqueKey(), e);
                    }
                }
            }
        }, 0, PERIOD, TimeUnit.MINUTES);
        LOGGER.info("init ScheduleCloseHdfsDataFile Thread success!");
    }

    @Override
    public void stop() {
        if (log2HdfsTaskMap.size() != 0) {
            return;
        }
        running = false;
        LOGGER.info("begin to stop hdfsDataFileclose monitor!");
        log2HdfsTaskMap.clear();
        scheduledService.shutdown();
        LOGGER.info("stop hdfsDataFileclose monitor success!");
    }
}
