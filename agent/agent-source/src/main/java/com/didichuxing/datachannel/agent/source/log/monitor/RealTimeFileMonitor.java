package com.didichuxing.datachannel.agent.source.log.monitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.source.log.offset.OffsetManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.didichuxing.datachannel.agent.engine.AbstractTask;
import com.didichuxing.datachannel.agent.engine.component.TaskComponent;
import com.didichuxing.datachannel.agent.engine.monitor.Monitor;
import com.didichuxing.datachannel.agent.source.log.LogSource;
import com.didichuxing.datachannel.agent.source.log.beans.FileNode;
import com.didichuxing.datachannel.agent.source.log.utils.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 实时文件监控
 * @author: huangjw
 * @Date: 19/7/4 14:21
 */
public enum RealTimeFileMonitor implements Monitor {

    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger("monitor");
    /**
     * 考虑到软连接目录的key和真是目录key是一致的，因此将value调整成set
     */
    private static Map<WatchKey, Set<String>> keyDirMap = new ConcurrentHashMap<>();
    private static AtomicInteger keyDirMapSize = new AtomicInteger(0);

    private static Map<String, Map<String, LogSource>> dirMap = new ConcurrentHashMap<>();

    private static WatchService watch = null;
    private static volatile boolean running = false;

    @Override
    public boolean register(TaskComponent component) {
        LogSource logSource = (LogSource) ((AbstractTask) component).getSource();
        return register(logSource);
    }

    public boolean register(LogSource logSource) {
        String filePath = logSource.getLogPath().getRealPath();
        String dir = FileUtils.getPathDir(filePath);
        if (StringUtils.isBlank(dir)) {
            LOGGER.warn("dir is null where logPath's path is " + filePath);
            return true;
        }
        LOGGER.info("begin to register dir " + dir + ", modelId is "
                + logSource.getModelConfig().getCommonConfig().getModelId());
        if (dirMap.containsKey(dir)) {
            LOGGER.info("dir " + dir
                    + " is already in com.didichuxing.datachannel.agent.source.log.monitor.ignore!");
            dirMap.get(dir).put(logSource.getUniqueKey(), logSource);
        } else {
            Map<String, LogSource> map = new ConcurrentHashMap<>(16);
            dirMap.put(dir, map);
            map.put(logSource.getUniqueKey(), logSource);
            registerWatchKey(dir);
        }
        return true;
    }

    /**
     * 注册到监控服务中
     *
     * @param dir dir to regiter watch key
     */
    private void registerWatchKey(String dir) {
        try {
            File dirFile = new File(dir);
            // 不存在的目录无法注册监控
            if (dirFile.isDirectory() && dirFile.exists()) {
                WatchKey key = Paths.get(dir).register(watch, StandardWatchEventKinds.ENTRY_CREATE);
                if (keyDirMap.get(key) == null) {
                    Set<String> set = new HashSet<>();
                    keyDirMap.put(key, set);
                }
                if (!keyDirMap.get(key).contains(dir)) {
                    keyDirMap.get(key).add(dir);
                    keyDirMapSize.incrementAndGet();

                    LOGGER.info("register dir to watchservice success.dir is " + dir);
                }
            }
        } catch (IOException e) {
            LogGather.recordErrorLog("realLogFileMonitor error!", "register dir error, dir:" + dir, e);
        }
    }

    /**
     * 替换fileKey 通常发生于目录被删除后的重新创建
     * 若之前watchKey不存在，则进行注册操作
     *
     * @param dir
     */
    public void replaceWatchKey(String dir) {
        LOGGER.warn("dir " + dir + " has been deleted already. current dir is new. begin to replace watch!");
        WatchKey watchKey = null;
        for (WatchKey key : keyDirMap.keySet()) {
            if (keyDirMap.get(key).contains(dir)) {
                watchKey = key;
                break;
            }
        }

        if (watchKey != null) {
            try {
                File dirFile = new File(dir);
                // 不存在的目录无法注册监控
                if (dirFile.isDirectory() && dirFile.exists()) {
                    WatchKey key = Paths.get(dir).register(watch, StandardWatchEventKinds.ENTRY_CREATE);
                    Set<String> dirSet = keyDirMap.get(watchKey);
                    if (dirSet != null) {
                        keyDirMap.remove(watchKey);
                        keyDirMap.put(key, dirSet);
                        LOGGER.info("replaceWatchKey dir to watchservice success.dir is " + dir);
                    }
                }
            } catch (IOException e) {
                LogGather.recordErrorLog("FileWathcerService error!", "replaceWatchKey error, dir:" + dir, e);
            }
        } else {
            registerWatchKey(dir);
        }
    }

    /**
     * 注销监控服务
     *
     * @param component AbstractTask
     */
    @Override
    public boolean unregister(TaskComponent component) {
        LogSource logSource = (LogSource) ((AbstractTask) component).getSource();
        return unregister(logSource);
    }

    public boolean unregister(LogSource logSource) {
        String dir = FileUtils.getPathDir(logSource.getLogPath().getRealPath());
        LOGGER.info("begin to unregiser dir " + dir);

        if (!dirMap.containsKey(dir)) {
            LOGGER.warn("dir is not in com.didichuxing.datachannel.agent.source.log.monitor!ignore");
            return true;
        }

        String sourceKey = logSource.getUniqueKey();
        if (dirMap.get(dir).get(sourceKey) == null) {
            LOGGER.warn("sourceKey do not exist in dirMap.key is " + sourceKey);
        } else {
            dirMap.get(dir).remove(sourceKey);
        }

        if (dirMap.get(dir).size() > 0) {
            LOGGER.warn("there is other task monitored by dir!ignore");
            return true;
        }

        dirMap.remove(dir);
        WatchKey watchKey = null;
        for (WatchKey key : keyDirMap.keySet()) {
            if (keyDirMap.get(key).contains(dir)) {
                watchKey = key;
                break;
            }
        }

        if (watchKey != null) {
            if (keyDirMap.get(watchKey) != null) {
                keyDirMap.get(watchKey).remove(dir);
                keyDirMapSize.decrementAndGet();
            }

            if (keyDirMap.get(watchKey).size() == 0) {
                watchKey.cancel();
                keyDirMap.remove(watchKey);
                LOGGER.info("keyDirMap is empty. cancel and delete watchkey!");
            }
        }
        LOGGER.info("Unregister the dir " + dir);
        return true;
    }

    /**
     * 启动inotify监控服务
     */
    @Override
    public void start() {
        if (running) {
            return;
        }
        LOGGER.info("begin to init inotify watchThread to com.didichuxing.datachannel.agent.source.log.monitor file change.");

        try {
            watch = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            LogGather.recordErrorLog("RealLogFileMonitor error", "init watch service error!", e);
            return;
        }

        running = true;
        Thread watchThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (running) {
                    try {
                        // 检测文件目录是否存在
                        if (dirMap.size() != keyDirMapSize.get()) {
                            int size = keyDirMap.size();
                            for (String dir : dirMap.keySet()) {
                                int i = 0;
                                for (Set<String> set : keyDirMap.values()) {
                                    if (!set.contains(dir)) {
                                        i++;
                                    }
                                }
                                // dir不存在于keyDirMap中
                                if (i >= size) {
                                    registerWatchKey(dir);
                                }
                            }
                        }

                        WatchKey key;
                        try {
                            key = watch.poll(500, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException ex) {
                            LogGather.recordErrorLog("RealLogFileMonitor", "watchService.poll() failed!:", ex);
                            continue;
                        }

                        if (key == null) {
                            continue;
                        }

                        for (WatchEvent<?> event : key.pollEvents()) {
                            WatchEvent.Kind<?> kind = event.kind();
                            if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                                Set<String> set = keyDirMap.get(key);
                                if(CollectionUtils.isNotEmpty(set)) {
                                    for (String dir : set) {
                                        processAdd(((Path) event.context()).toString(), dir);
                                    }
                                }
                            }
                        }
                        if (!key.reset()) {
                            LOGGER.warn("stop moniotr file change for unknown reason! key is " + key);
                            continue;
                        }
                    } catch (Throwable e) {
                        LogGather.recordErrorLog("RealLogFileMonitor", "catch unintended exception:", e);
                    }

                }
            }
        }, "RealLogFileMonitorThread");

        watchThread.start();
    }

    /**
     * 停止监控服务
     */
    @Override
    public void stop() {
        if (dirMap.size() != 0) {
            return;
        }

        LOGGER.info("begin to end watchThread");
        running = false;
        if (watch != null) {
            try {
                // 等待500毫秒，确保watchThread彻底停止
                Thread.sleep(500L);
                watch.close();
            } catch (Exception e) {
                LogGather.recordErrorLog("FileWathcerService error!", "watch close error.", e);
            }
        }
    }

    /**
     * 处理文件新增逻辑
     *
     * @param newFileName new file
     * @param dir         file's dir
     */
    private void processAdd(String newFileName, String dir) {
        LOGGER.info("new find created.file is " + (dir + File.separator + newFileName));
        File file = new File(dir + File.separator + newFileName);
        if (file.isDirectory()) {
            // 过滤目录
            LOGGER.info("file[" + file.getAbsolutePath() + "] is a directory.ignore!");
            return;
        }
        if (!dirMap.containsKey(dir)) {
            return;
        }

        for (LogSource logSource : dirMap.get(dir).values()) {
            if (!FileUtils.match(file, logSource.getLogPath().getRealPath(),
                    logSource.getLogSourceConfig().getMatchConfig())) {
                continue;
            }

            if (OffsetManager.checkFileKeyExist(StringUtils.isNotBlank(logSource.getModelConfig().getHostname()) ? logSource.getModelConfig().getHostname()
                            + CommonUtils.getHOSTNAMESUFFIX() : "",
                    logSource.getLogPath(), FileUtils.getFileKeyByAttrs(file))) {
                LOGGER.info("RealTimeFileMonitor: file key is already in offset map! fileName: "
                        + file.getAbsolutePath() + ",fileKey:" + FileUtils.getFileKeyByAttrs(file));
                continue;
            }

            if (FileUtils.getFileNodeHeadMd5(file).equals(LogConfigConstants.MD5_FAILED_TAG)) {
                continue;
            }

            FileNode fileNode = new FileNode(logSource.getModelConfig().getCommonConfig().getModelId(),
                    logSource.getLogPath().getPathId(), FileUtils.getFileKeyByAttrs(file),
                    FileUtils.getModifyTime(file), dir, newFileName, file.length(), file);
            if (logSource.checkStandardLogType(fileNode)) {
                logSource.appendFile(fileNode);
            }
        }
    }
}
