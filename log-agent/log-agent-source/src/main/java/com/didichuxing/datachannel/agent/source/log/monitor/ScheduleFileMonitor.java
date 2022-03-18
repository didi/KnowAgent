package com.didichuxing.datachannel.agent.source.log.monitor;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import org.apache.commons.lang3.StringUtils;

import com.didichuxing.datachannel.agent.common.beans.LogPath;
import com.didichuxing.datachannel.agent.engine.AbstractTask;
import com.didichuxing.datachannel.agent.engine.component.TaskComponent;
import com.didichuxing.datachannel.agent.engine.monitor.Monitor;
import com.didichuxing.datachannel.agent.engine.service.DefaultThreadFactory;
import com.didichuxing.datachannel.agent.source.log.LogSource;
import com.didichuxing.datachannel.agent.source.log.beans.FileNode;
import com.didichuxing.datachannel.agent.source.log.utils.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 定时文件监控
 * @author: huangjw
 * @Date: 19/7/4 14:22
 */
public enum ScheduleFileMonitor implements Monitor {
                                                    INSTANCE;

private static final Logger LOGGER = LoggerFactory.getLogger("monitor");private static ScheduledExecutorService                          scheduledService = null;

    /**
     * 监控2天内的文件变动，可配置
     */
    private static Long                                              maxTimeGap       = 7 * 24 * 60 * 60 * 1000L;

    /**
     * key: dir;value: map<LogSource.getUniqueKey(), LogSource>
     */
    private static ConcurrentHashMap<String, Map<String, LogSource>> fileMap          = new ConcurrentHashMap<>();

    private static volatile boolean                                  running          = false;

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
                                                               new DefaultThreadFactory("ScheduleFile-Monitor-schedule"));
            running = true;
        } catch (Exception e) {
            LogGather.recordErrorLog("ScheduleFileMonitor error!", "init ScheduleFileMonitor service error!", e);
        }

        startRelatedFileScheduleMonitor();
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        if (fileMap.size() != 0) {
            return;
        }
        running = false;
        LOGGER.info("begin to stop relatedFile monitor!");
        fileMap.clear();
        scheduledService.shutdown();
        LOGGER.info("stop relatedFile monitor success!");
    }

    /**
     * 启动定时任务检测文件滚动
     */
    private void startRelatedFileScheduleMonitor() {
        LOGGER.info("begin to init schedule watchThread to com.didichuxing.datachannel.agent.source.log.monitor file change.");
        scheduledService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                LOGGER.info("begin to check all the file.");

                try {
                    long currentTime = System.currentTimeMillis();

                    for (String dir : fileMap.keySet()) {
                        Map<String, FileNode> fileNodeMap = new HashMap<>(16);

                        List<String> children = FileUtils.listChildren(new File(dir));
                        for (String child : children) {
                            File file = new File(dir + File.separator + child);
                            if (!file.exists() || file.isDirectory()) {
                                continue;
                            }
                            long modifyTime = FileUtils.getModifyTime(file);
                            if (currentTime - modifyTime > maxTimeGap) {
                                continue;
                            }

                            FileNode fileNode = new FileNode(null, null, FileUtils.getFileKeyByAttrs(file), modifyTime,
                                                             dir, child, file.length(), file);

                            fileNodeMap.put(fileNode.getFileKey(), fileNode);
                        }

                        Map<String, LogSource> m = fileMap.get(dir);

                        // TODO: 19/7/4 修改逻辑
                        for (String key : m.keySet()) {
                            m.get(key).checkFile(fileNodeMap);
                        }
                    }
                } catch (Throwable t) {
                    LogGather.recordErrorLog("RelatedFileMonitor", "process error", t);
                }
            }
        }, 0, 2, TimeUnit.MINUTES);
        LOGGER.info("init schedule watchThread success!");
    }

    @Override
    public boolean register(TaskComponent component) {
        LogSource logSource = (LogSource) ((AbstractTask) component).getSource();
        return register(logSource);
    }

    public boolean register(LogSource logSource) {
        LogPath logPath = logSource.getLogPath();
        LOGGER.info("begin to register logTask to scheduleFileMonitor. logPath is " + logPath);

        String dir = FileUtils.getPathDir(logPath.getRealPath());
        if (StringUtils.isBlank(dir)) {
            LOGGER.warn("dir is null where logPath.s path is " + logPath.getRealPath());
            return true;
        }
        if (!fileMap.containsKey(dir)) {
            fileMap.put(dir, new ConcurrentHashMap<String, LogSource>(16));
        }

        fileMap.get(dir).put(logSource.getUniqueKey(), logSource);
        LOGGER.info("register logTask success!");
        return true;
    }

    @Override
    public boolean unregister(TaskComponent component) {
        LogSource logSource = (LogSource) ((AbstractTask) component).getSource();
        return unregister(logSource);
    }

    public boolean unregister(LogSource logSource) {
        LogPath logPath = logSource.getLogPath();
        LOGGER.info("begin to unregister logTask from scheduleFileMonitor. logPath is " + logPath);

        String dir = FileUtils.getPathDir(logPath.getRealPath());
        if (StringUtils.isBlank(dir)) {
            LOGGER.warn("dir is null where logPath.s path is " + logPath.getRealPath());
            return true;
        }

        if (!fileMap.containsKey(dir)) {
            LOGGER.warn("dir " + dir
                        + " is not in related file com.didichuxing.datachannel.agent.source.log.monitor.ignore!");
            return true;
        }

        String uniqueKey = logSource.getUniqueKey();
        if (fileMap.get(dir).get(uniqueKey) == null) {
            LOGGER.warn("uniqueKey do not exist in fileMap.key is " + uniqueKey);
        } else {
            fileMap.get(dir).remove(uniqueKey);
        }

        if (fileMap.get(dir).size() == 0) {
            fileMap.remove(dir);
        }
        LOGGER.info("unregister logTask success!");
        return true;
    }

    public static ConcurrentHashMap<String, Map<String, LogSource>> getFileMap() {
        return fileMap;
    }

    public static void setFileMap(ConcurrentHashMap<String, Map<String, LogSource>> fileMap) {
        ScheduleFileMonitor.fileMap = fileMap;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        ScheduleFileMonitor.running = running;
    }
}
