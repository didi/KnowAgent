package com.didichuxing.datachannel.agent.task.log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.didichuxing.datachannel.agent.common.beans.LogPath;
import com.didichuxing.datachannel.agent.engine.component.TaskComponent;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.monitor.Monitor;
import com.didichuxing.datachannel.agent.engine.service.DefaultThreadFactory;
import com.didichuxing.datachannel.agent.source.log.LogSource;
import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.agent.source.log.utils.FileUtils;

import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 目录监控
 * @author: huangjw
 * @Date: 2019-07-23 11:44
 */
public enum ScheduleDirMonitor implements Monitor {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger("monitor");
    private static ScheduledExecutorService            scheduledService = null;

    /**
     * key: key of LogModel
     * value: LogModel
     */
    private static ConcurrentHashMap<String, LogModel> modelMap         = new ConcurrentHashMap<>();

    private static volatile boolean                    running          = false;

    private final Object                               lock             = new Object();

    /**
     * 初始化
     */
    @Override
    public void start() {
        if (running) {
            return;
        }
        try {
            scheduledService = new ScheduledThreadPoolExecutor(1,
                                                               new DefaultThreadFactory("ScheduleDir-Monitor-schedule"));
            running = true;
        } catch (Exception e) {
            LogGather.recordErrorLog("ScheduleDirMonitor error!", "init ScheduleDirMonitor service error!", e);
        }

        startRelatedDirScheduleMonitor();
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        if (modelMap.size() != 0) {
            return;
        }
        running = false;
        LOGGER.info("begin to stop relatedDir monitor!");
        modelMap.clear();
        scheduledService.shutdown();
        LOGGER.info("stop relatedDir monitor success!");
    }

    /**
     * 启动定时任务检测文件滚动
     */
    private void startRelatedDirScheduleMonitor() {
        LOGGER.info("begin to init schedule watchThread to monitor file change.");
        scheduledService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                LOGGER.info("begin to check all the dir.");

                try {
                    synchronized (lock) {
                        for (LogModel logModel : modelMap.values()) {
                            Map<String, LogSource> map = new HashMap<String, LogSource>();
                            List<LogPath> logPaths = ((LogSourceConfig) logModel.getModelConfig().getSourceConfig()).getLogPaths();
                            if (logPaths != null) {
                                for (LogPath logPath : logPaths) {
                                    Map<String, LogSource> tmpMap = getSourcesByDir(logModel.getModelConfig(), logPath);
                                    if (tmpMap != null) {
                                        map.putAll(tmpMap);
                                    }
                                }
                            }
                            logModel.checkDir(map);
                        }
                    }
                } catch (Throwable t) {
                    LogGather.recordErrorLog("ScheduleDirMonitor error", "process error", t);
                }
            }
        }, 0, 2, TimeUnit.MINUTES);
        LOGGER.info("init schedule watchThread success!");
    }

    private Map<String, LogSource> getSourcesByDir(ModelConfig modelConfig, LogPath logPath) {
        Set<String> masterFiles = FileUtils.getMasterFliesUnderDir(logPath.getRealPath(), logPath.getRealPath(),
                                                                   ((LogSourceConfig) modelConfig.getSourceConfig()).getMatchConfig());
        Map<String, LogSource> result = new HashMap<>();
        if (masterFiles != null) {
            for (String masterFile : masterFiles) {
                LogPath tmpLogPath = new LogPath(logPath.getLogModelId(), logPath.getPathId(), masterFile);
                LogSource logSource = new LogSource(modelConfig, tmpLogPath);
                result.put(logSource.getUniqueKey(), logSource);
            }
            return result;
        }
        return null;
    }

    @Override
    public boolean register(TaskComponent component) {
        LogModel logModel = (LogModel) component;
        return register(logModel);
    }

    public boolean register(LogModel logModel) {
        LOGGER.info("begin to register logModel to scheduleDirMonitor. logModel's key is " + logModel.getUniqueKey());
        if (!running) {
            LOGGER.warn("register logModel failed for scheduleDirMonitor is stopping!");
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
        LOGGER.info("begin to unregister logModel from scheduleDirMonitor. logModel's key is "
                    + logModel.getUniqueKey());
        if (!running) {
            LOGGER.warn("unregister logModel failed for scheduleDirMonitor is stopping!");
            return false;
        }
        synchronized (lock) {
            modelMap.remove(logModel.getUniqueKey());
        }
        LOGGER.info("unregister logModel success!");
        return true;
    }

    public ConcurrentHashMap<String, LogModel> getModelMap() {
        return modelMap;
    }
}
