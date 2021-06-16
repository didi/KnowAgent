package com.didichuxing.datachannel.swan.agent.task.log.log2hdfs;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.swan.agent.sink.hdfsSink.HdfsFileSystem;
import com.didichuxing.datachannel.swan.agent.sink.hdfsSink.HdfsTargetConfig;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import com.didichuxing.tunnel.util.log.LogGather;

/**
 * @description: 提供容器服务 异常时负责重建链接
 * @author: huangjw
 * @Date: 2019-07-16 11:22
 */
public class HdfsFileSystemContainer {

    private final static ILog                              LOGGER           = LogFactory.getLog(HdfsFileSystemContainer.class.getName());

    /**
     * key: username + password + rootPath
     * value: set of model's uniqueKey
     */
    private static Map<String, Map<String, Log2HdfsModel>> upAndModelMap    = new ConcurrentHashMap<>();

    /**
     * key： username + password + rootPath
     * value: hdfsFileSystem
     */
    private static Map<String, HdfsFileSystem>             fileSystemMap    = new ConcurrentHashMap<>();

    /**
     * key: username + password + rootPath
     * value: times of failed
     */
    private static Map<String, Integer>                    failedTimesMap   = new HashMap<>();

    // 最大失败次数
    private final static int                               MAX_FAILED_TIMES = 5;

    // 是否正在运行
    private static volatile boolean                        isRunning        = false;

    /**
     * 注册
     * @param log2HdfsModel
     * @return
     */
    public static synchronized void register(Log2HdfsModel log2HdfsModel) {
        HdfsTargetConfig hdfsTargetConfig = (HdfsTargetConfig) log2HdfsModel.getModelConfig().getTargetConfig();
        String username = hdfsTargetConfig.getUsername();
        String rootPath = hdfsTargetConfig.getRootPath();
        String password = hdfsTargetConfig.getPassword();
        register(log2HdfsModel, username, password, rootPath);
    }

    private static void register(Log2HdfsModel log2HdfsModel, String username, String password, String rootPath) {
        LOGGER.info("begin to register model to HdfsFileSystemContainer. model's uniqueKey is "
                    + log2HdfsModel.getUniqueKey());
        String upKey = getKey(username, password, rootPath);
        if (fileSystemMap.get(upKey) == null) {
            HdfsFileSystem fileSystem = new HdfsFileSystem(rootPath, username, password);
            if (fileSystem != null) {
                fileSystemMap.put(upKey, fileSystem);
                failedTimesMap.put(upKey, 0);
                LOGGER.info("register model to fileSystemMap success!");
            }
        }

        if (upAndModelMap.get(upKey) == null) {
            Map<String, Log2HdfsModel> modelMap = new HashMap<>();
            upAndModelMap.put(upKey, modelMap);
        }
        upAndModelMap.get(upKey).put(log2HdfsModel.getUniqueKey(), log2HdfsModel);
    }

    public static HdfsFileSystem getHdfsFileSystem(Log2HdfsModel log2HdfsModel) {
        HdfsTargetConfig hdfsTargetConfig = (HdfsTargetConfig) log2HdfsModel.getModelConfig().getTargetConfig();
        String username = hdfsTargetConfig.getUsername();
        String password = hdfsTargetConfig.getPassword();
        String rootPath = hdfsTargetConfig.getRootPath();
        String upKey = getKey(username, password, rootPath);
        if (fileSystemMap.get(upKey) == null) {
            register(log2HdfsModel);
        }
        return fileSystemMap.get(upKey);
    }

    /**
     * 注销
     * @param log2HdfsModel
     * @return
     */
    public static synchronized void unregister(Log2HdfsModel log2HdfsModel) {
        HdfsTargetConfig hdfsTargetConfig = (HdfsTargetConfig) log2HdfsModel.getModelConfig().getTargetConfig();
        String username = hdfsTargetConfig.getUsername();
        String password = hdfsTargetConfig.getPassword();
        String rootPath = hdfsTargetConfig.getRootPath();
        unregister(log2HdfsModel, username, password, rootPath);
    }

    private static void unregister(Log2HdfsModel log2HdfsModel, String username, String password, String rootPath) {
        LOGGER.info("begin to unregister model from HdfsFileSystemContainer. model's uniqueKey is "
                    + log2HdfsModel.getUniqueKey());
        String upKey = getKey(username, password, rootPath);
        Map<String, Log2HdfsModel> modelMap = upAndModelMap.get(upKey);
        if (modelMap != null) {
            if (modelMap.containsKey(log2HdfsModel.getUniqueKey())) {
                modelMap.remove(log2HdfsModel.getUniqueKey());
                LOGGER.info("unregister model from upAndModelMap success.");
            }

            if (modelMap.size() == 0) {
                fileSystemMap.get(upKey).close();
                upAndModelMap.remove(upKey);
                fileSystemMap.remove(upKey);
                failedTimesMap.remove(upKey);
                LOGGER.info("unregister model from fileSystem and upAndModelMap success!");
            }
        } else {
            if (fileSystemMap.containsKey(upKey)) {
                fileSystemMap.get(upKey).close();
                fileSystemMap.remove(upKey);
                LOGGER.info("unregister model from fileSystem success!");
            }
        }
    }

    public static synchronized void release(Log2HdfsModel log2HdfsModel, ModelConfig oldOne) {
        LOGGER.info("begin to release model's config in HdfsFileSystemContainer. model's uniqueKey is "
                    + log2HdfsModel.getUniqueKey());
        HdfsTargetConfig oldHdfsConfig = (HdfsTargetConfig) oldOne.getTargetConfig();
        String oldUpKey = getKey(oldHdfsConfig.getUsername(), oldHdfsConfig.getPassword(), oldHdfsConfig.getRootPath());
        HdfsFileSystem hdfsFileSystem = fileSystemMap.get(oldUpKey);
        hdfsFileSystem.reBuildDataFile();

        if (!upAndModelMap.containsKey(oldUpKey)) {
            hdfsFileSystem.close();
            fileSystemMap.remove(oldUpKey);
        }
    }

    /**
     * 替换,一般发生于密码更换
     * @param log2HdfsModel
     * @param newOne
     * @param oldOne
     */
    public static synchronized void changeFileSystem(Log2HdfsModel log2HdfsModel, ModelConfig newOne,
                                                     ModelConfig oldOne) {
        LOGGER.info("begin to change model's config in HdfsFileSystemContainer. model's uniqueKey is "
                    + log2HdfsModel.getUniqueKey());
        HdfsTargetConfig newHdfsConfig = (HdfsTargetConfig) newOne.getTargetConfig();
        HdfsTargetConfig oldHdfsConfig = (HdfsTargetConfig) oldOne.getTargetConfig();
        String oldUpKey = getKey(oldHdfsConfig.getUsername(), oldHdfsConfig.getPassword(), oldHdfsConfig.getRootPath());
        Map<String, Log2HdfsModel> modelMap = upAndModelMap.get(oldUpKey);
        if (modelMap != null) {
            if (modelMap.containsKey(log2HdfsModel.getUniqueKey())) {
                modelMap.remove(log2HdfsModel.getUniqueKey());
                LOGGER.info("unregister model from upAndModelMap success.");
            }

            if (modelMap.size() == 0) {
                upAndModelMap.remove(oldUpKey);
                failedTimesMap.remove(oldUpKey);
                LOGGER.info("unregister fileSystem from failedTimesMap and upAndModelMap success!");
            }
        }

        register(log2HdfsModel, newHdfsConfig.getUsername(), newHdfsConfig.getPassword(), newHdfsConfig.getRootPath());
    }

    private static String getKey(String username, String password, String rootPath) {
        return username + "_" + password + "_" + rootPath;
    }

    public static void start() {
        if (isRunning) {
            return;
        }
        LOGGER.info("begin to start HdfsFileSystem check thread");
        isRunning = true;
        Thread checkThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (!isRunning) {
                    try {
                        // 3分钟检测一次
                        Thread.sleep(3 * 60 * 1000);
                    } catch (Exception e) {

                    }

                    try {
                        for (String upKey : fileSystemMap.keySet()) {
                            HdfsFileSystem hdfsFileSystem = fileSystemMap.get(upKey);
                            if (hdfsFileSystem.checkHealth()) {
                                failedTimesMap.put(upKey, 0);
                            } else {
                                failedTimesMap.put(upKey, failedTimesMap.get(upKey) + 1);
                                if (failedTimesMap.get(upKey) >= MAX_FAILED_TIMES) {
                                    // 重建
                                    hdfsFileSystem.reBuildSystem();
                                }
                            }
                        }
                    } catch (Exception e) {
                        LogGather.recordErrorLog("HdfsFileSystemContainer error", "check map of fileSystem error!", e);
                    }
                }
            }
        });
        checkThread.start();
    }

    public static void stop() {
        if (fileSystemMap.size() != 0) {
            return;
        }
        LOGGER.info("begin to stop HdfsFileSystem check thread");
        isRunning = false;
    }
}
