package com.didichuxing.datachannel.agent.task.log;

import com.didichuxing.datachannel.agent.common.api.FileType;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.beans.LogPath;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.AbstractModel;
import com.didichuxing.datachannel.agent.engine.AbstractTask;
import com.didichuxing.datachannel.agent.engine.service.TaskRunningPool;
import com.didichuxing.datachannel.agent.engine.utils.CollectUtils;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.source.log.LogSource;
import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.agent.source.log.utils.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @description: log 模型
 * @author: huangjw
 * @Date: 2019-07-12 19:23
 */
public abstract class LogModel extends AbstractModel {

    private static final Logger LOGGER         = LoggerFactory.getLogger(LogModel.class.getName());
    private static final Logger DDCLOUD_LOGGER = LoggerFactory.getLogger("ddcloud");

    public LogModel(ModelConfig config) {
        super(config);
    }

    @Override
    public boolean init(ComponentConfig config) {
        configure(config);
        if (((LogSourceConfig) this.modelConfig.getSourceConfig()).getMatchConfig().getFileType() == FileType.Dir
            .getStatus()) {
            // 目录采集
            ScheduleDirMonitor.INSTANCE.start();
            ScheduleDirMonitor.INSTANCE.register(this);
        }

        return true;
    }

    @Override
    public void configure(ComponentConfig config) {
        this.modelConfig = (ModelConfig) config;
        LOGGER.info("begin to init LogModel. config is " + this.modelConfig);
        LogSourceConfig logSourceConfig = (LogSourceConfig) (this.modelConfig.getSourceConfig());

        // replace
        replaceHostName(logSourceConfig);

        List<LogPath> logPaths = logSourceConfig.getLogPaths();
        if (logPaths != null) {
            for (LogPath logPath : logPaths) {
                if (((LogSourceConfig) this.modelConfig.getSourceConfig()).getMatchConfig()
                    .getFileType() == FileType.File.getStatus()) {
                    // 文件
                    LogSource logSource = new LogSource(modelConfig, logPath);
                    addTask(this.modelConfig, logSource);
                } else {
                    // 目录
                    List<LogSource> logSources = getSourcesByDir(logPath.getRealPath(), logPath);
                    for (LogSource logSource : logSources) {
                        addTask(this.modelConfig, logSource);
                    }
                }
            }
        }
    }

    @Override
    public boolean stop(boolean force) {
        if (isStop()) {
            LOGGER.info("model is already stoped. ignore! uniqueKey is " + getUniqueKey());
            return true;
        }
        LOGGER.info("begin to stop model. force is " + force + ",modelId is " + this.taskId
                    + ", modelTag is " + modelTag);
        setStop(true);
        if (this.tasks != null) {
            for (AbstractTask task : tasks.values()) {
                task.stop(force);
            }
        }
        if (((LogSourceConfig) this.modelConfig.getSourceConfig()).getMatchConfig().getFileType() == FileType.Dir
            .getStatus()) {
            // 目录采集
            ScheduleDirMonitor.INSTANCE.unregister(this);
            ScheduleDirMonitor.INSTANCE.stop();
        }
        return true;
    }

    /**
     * 删除
     * @return
     */
    @Override
    public boolean delete() {
        if (isStop()) {
            LOGGER.info("model is already stoped. ignore! uniqueKey is " + getUniqueKey());
            return true;
        }
        setStop(true);
        LOGGER.info("begin to delete model. modelId is " + this.taskId + ", modelTag is "
                    + modelTag);
        if (this.tasks != null) {
            for (AbstractTask task : tasks.values()) {
                task.delete();
            }
        }
        if (((LogSourceConfig) this.modelConfig.getSourceConfig()).getMatchConfig().getFileType() == FileType.Dir
            .getStatus()) {
            // 目录采集
            ScheduleDirMonitor.INSTANCE.unregister(this);
            ScheduleDirMonitor.INSTANCE.stop();
        }

        return true;
    }

    protected void addTask(ModelConfig config, LogSource logSource) {
        if (canStart()) {
            AbstractTask task = buildTask(this.modelConfig, logSource);
            LOGGER.info("begin to addTask to model. model's uniqueKey is " + getUniqueKey()
                        + ", task's uniqueKey is " + task.getUniqueKey());

            this.sources.put(logSource.getUniqueKey(), logSource);
            task.init(config);
            this.tasks.put(task.getUniqueKey(), task);
        } else {
            LOGGER.info("model is not allow to add! model'id is "
                        + config.getCommonConfig().getModelId());
        }
    }

    /**
     * 检测路径中是否有${hostname}，有则替换
     *
     * @param logSourceConfig logSourceConfig
     */
    protected void replaceHostName(LogSourceConfig logSourceConfig) {
        LOGGER.info("begin to check hostName if path contains " + LogConfigConstants.HOSTNAME_FLAG);
        if (logSourceConfig == null) {
            return;
        }

        boolean isReplaced = false;

        for (LogPath logPath : logSourceConfig.getLogPaths()) {
            if (logPath.getRealPath().contains(LogConfigConstants.HOSTNAME_FLAG)) {
                isReplaced = true;
                logPath.setRealPath(logPath.getRealPath().replace(LogConfigConstants.HOSTNAME_FLAG,
                    CommonUtils.getHOSTNAME()));
            }
        }
        if (isReplaced) {
            LOGGER.info("collectorConfig contains " + LogConfigConstants.HOSTNAME_FLAG
                        + ".new sourceConfig is " + logSourceConfig);
        }
    }

    @Override
    public boolean canStart() {
        if (!isStop()) {
            LOGGER.info("model is already started.ignore!");
            return false;
        }
        // 全局日志模型 地区过滤
        if (!checkLocation(modelConfig)) {
            setStop(true);
            return false;
        }
        // 状态为已停止的日志模型不需要启动
        if (modelConfig.getCommonConfig().isStop()) {
            LOGGER.warn("model is stopped. ingore!");
            setStop(true);
            return false;
        }
        return true;
    }

    /**
     * 地区信息校验
     * 全局日志模型
     *
     * @param modelConfig modelConfig
     * @return true:有效日志模型；
     */
    public boolean checkLocation(ModelConfig modelConfig) {
        String originalAppName = modelConfig.getEventMetricsConfig().getOriginalAppName();
        String suffix = modelConfig.getEventMetricsConfig().getLocation();
        String serverName = CommonUtils.getHOSTNAME();

        if (StringUtils.isBlank(originalAppName) || !originalAppName.equals(LogConfigConstants.GLOBAL_FLAG)
            || StringUtils.isBlank(suffix)) {
            return true;
        }

        String hostIdcSuffix = getHostIdcSuffix(serverName);
        if (StringUtils.isBlank(hostIdcSuffix)) {
            return false;
        }

        try {
            String[] suffixsArray = suffix.split(",");
            Set<String> suffixSet = new HashSet<>();
            for (int i = 0; i < suffixsArray.length; i++) {
                suffixSet.add(suffixsArray[i]);
            }
            if (suffixSet.contains(hostIdcSuffix)) {
                return true;
            } else {
                LOGGER.warn("hostName:" + serverName + "is not in " + "businessConfig["
                            + modelConfig.getEventMetricsConfig()
                            + "]'s rangeDataCenter.this collectorConfig need to be filtered!");
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("LogModel error, checkLocation error.serverName is " + serverName + ", suffix is " + suffix, e);
        }
        return false;
    }

    /**
     * 获取主机名后缀
     *
     * @param serverName 主机名
     * @return 主机名后缀
     */
    private String getHostIdcSuffix(String serverName) {
        if (serverName.contains(".")) {
            return serverName.substring(serverName.lastIndexOf(".") + 1, serverName.length());
        }
        return null;
    }

    /**
     * 构建task
     * @param config
     * @param logSource
     */
    public abstract AbstractTask buildTask(ModelConfig config, LogSource logSource);

    @Override
    public List<AbstractTask> getAddedTasks(ModelConfig oldModelConfig, ModelConfig newModelConfig) {
        Map<Long, LogPath> oldLogPaths = ((LogSourceConfig) oldModelConfig.getSourceConfig()).getLogPathMap();
        Map<Long, LogPath> newLogPaths = ((LogSourceConfig) newModelConfig.getSourceConfig()).getLogPathMap();
        Set<Long> addIds = CollectUtils.getAdd(oldLogPaths.keySet(), newLogPaths.keySet());
        List<AbstractTask> tasks = new ArrayList<>();
        if (((LogSourceConfig) oldModelConfig.getSourceConfig()).getMatchConfig().getFileType() == FileType.File.getStatus()) {
            // 文件
            for (Long id : addIds) {
                LogPath logPath = newLogPaths.get(id);
                LogSource logSource = new LogSource(newModelConfig, logPath);
                AbstractTask task = buildTask(newModelConfig, logSource);
                tasks.add(task);
            }
        } else {
            // 目录
            // add
            for (Long id : addIds) {
                LogPath logPath = newLogPaths.get(id);
                List<LogSource> logSources = getSourcesByDir(logPath.getRealPath(), logPath);
                if (logSources != null && logSources.size() != 0) {
                    for (LogSource logSource : logSources) {
                        if (!this.sources.containsKey(logSource.getUniqueKey())) {
                            AbstractTask task = buildTask(newModelConfig, logSource);
                            tasks.add(task);
                        }
                    }
                }
            }

            // update
            Set<Long> updateIds = CollectUtils.getSame(oldLogPaths.keySet(), newLogPaths.keySet());
            for (Long id : updateIds) {
                LogPath logPath = oldLogPaths.get(id);
                Set<String> oldMasterFiles = FileUtils.getMasterFliesUnderDir(logPath.getRealPath(),
                                                                              logPath.getRealPath(),
                                                                              ((LogSourceConfig) oldModelConfig.getSourceConfig()).getMatchConfig());
                Set<String> newMasterFiles = FileUtils.getMasterFliesUnderDir(logPath.getRealPath(),
                                                                              logPath.getRealPath(),
                                                                              ((LogSourceConfig) newModelConfig.getSourceConfig()).getMatchConfig());

                Set<String> addMasterFiles = CollectUtils.getStrAdd(oldMasterFiles, newMasterFiles);
                for (String masterFile : addMasterFiles) {
                    LogPath tmpLogPath = new LogPath(logPath.getLogModelId(), logPath.getPathId(), masterFile);
                    LogSource logSource = new LogSource(newModelConfig, tmpLogPath);
                    if (!this.sources.containsKey(logSource.getUniqueKey())) {
                        AbstractTask task = buildTask(newModelConfig, logSource);
                        tasks.add(task);
                    }
                }
            }

        }
        return tasks;
    }

    @Override
    public List<AbstractTask> getUpdatedTasks(ModelConfig oldModelConfig, ModelConfig newModelConfig) {
        Map<Long, LogPath> oldLogPaths = ((LogSourceConfig) oldModelConfig.getSourceConfig()).getLogPathMap();
        Map<Long, LogPath> newLogPaths = ((LogSourceConfig) newModelConfig.getSourceConfig()).getLogPathMap();
        Set<Long> updateIds = CollectUtils.getSame(oldLogPaths.keySet(), newLogPaths.keySet());
        List<AbstractTask> tasks = new ArrayList<>();
        if (((LogSourceConfig) oldModelConfig.getSourceConfig()).getMatchConfig().getFileType() == FileType.File.getStatus()) {
            // 文件
            for (Long id : updateIds) {
                LogPath logPath = oldLogPaths.get(id);
                String taskKey = buildPreLogTaskKey(oldModelConfig.getHostname(), logPath.getLogModelId(), logPath.getPathId());
                for (Map.Entry<String, AbstractTask> entry : this.tasks.entrySet()) {
                    if (entry.getKey().startsWith(taskKey)) {
                        tasks.add(entry.getValue());
                    }
                }
            }
        } else {
            // 目录
            for (Long id : updateIds) {
                LogPath logPath = oldLogPaths.get(id);
                Set<String> oldMasterFiles = FileUtils.getMasterFliesUnderDir(logPath.getRealPath(),
                                                                              logPath.getRealPath(),
                                                                              ((LogSourceConfig) oldModelConfig.getSourceConfig()).getMatchConfig());
                Set<String> newMasterFiles = FileUtils.getMasterFliesUnderDir(logPath.getRealPath(),
                                                                              logPath.getRealPath(),
                                                                              ((LogSourceConfig) newModelConfig.getSourceConfig()).getMatchConfig());

                Set<String> sameMasterFiles = CollectUtils.getStrSame(oldMasterFiles, newMasterFiles);
                for (String masterFile : sameMasterFiles) {
                    LogPath tmpLogPath = new LogPath(logPath.getLogModelId(), logPath.getPathId(), masterFile);
                    String logSourceKey = getLogSourceKey(tmpLogPath);
                    tasks.add(this.tasks.get(buildLogTaskKey(logPath.getLogModelId(), logSourceKey)));
                }
            }
        }

        return tasks;
    }

    @Override
    public List<AbstractTask> getDeletedTasks(ModelConfig oldModelConfig, ModelConfig newModelConfig) {
        Map<Long, LogPath> oldLogPaths = ((LogSourceConfig) oldModelConfig.getSourceConfig()).getLogPathMap();
        Map<Long, LogPath> newLogPaths = ((LogSourceConfig) newModelConfig.getSourceConfig()).getLogPathMap();

        List<AbstractTask> tasks = new ArrayList<>();

        Set<Long> delIds = CollectUtils.getDel(oldLogPaths.keySet(), newLogPaths.keySet());
        for (Long id : delIds) {
            LogPath logPath = oldLogPaths.get(id);
            String taskKey = buildPreLogTaskKey(oldModelConfig.getHostname(), logPath.getLogModelId(), logPath.getPathId());
            for (Map.Entry<String, AbstractTask> entry : this.tasks.entrySet()) {
                if (entry.getKey().startsWith(taskKey)) {
                    tasks.add(entry.getValue());
                }
            }
        }

        if (((LogSourceConfig) oldModelConfig.getSourceConfig()).getMatchConfig().getFileType() == FileType.Dir.getStatus()) {
            // 目录
            Set<Long> updateIds = CollectUtils.getSame(oldLogPaths.keySet(), newLogPaths.keySet());
            for (Long id : updateIds) {
                LogPath logPath = newLogPaths.get(id);
                Set<String> oldMasterFiles = FileUtils.getMasterFliesUnderDir(logPath.getRealPath(),
                                                                              logPath.getRealPath(),
                                                                              ((LogSourceConfig) oldModelConfig.getSourceConfig()).getMatchConfig());
                Set<String> newMasterFiles = FileUtils.getMasterFliesUnderDir(logPath.getRealPath(),
                                                                              logPath.getRealPath(),
                                                                              ((LogSourceConfig) newModelConfig.getSourceConfig()).getMatchConfig());

                Set<String> delMasterFiels = CollectUtils.getStrDel(oldMasterFiles, newMasterFiles);
                for (String masterFile : delMasterFiels) {
                    LogPath tmpLogPath = new LogPath(logPath.getLogModelId(), logPath.getPathId(), masterFile);
                    String logSourceKey = getLogSourceKey(tmpLogPath);
                    tasks.add(this.tasks.get(buildLogTaskKey(logPath.getLogModelId(), logSourceKey)));
                }
            }
        }
        return tasks;
    }

    protected List<LogSource> getSourcesByDir(String dir, LogPath logPath) {
        Set<String> masterFiles = FileUtils.getMasterFliesUnderDir(dir, logPath.getRealPath(),
                                                                   ((LogSourceConfig) modelConfig.getSourceConfig()).getMatchConfig());
        List<LogSource> result = new ArrayList<>();
        if (masterFiles != null) {
            for (String masterFile : masterFiles) {
                LogPath tmpLogPath = null;
                if (StringUtils.isNotBlank(logPath.getDockerPath())) {
                    // docker场景
                    String suffix = FileUtils.getFileSuffix(logPath.getRealPath(), masterFile);
                    tmpLogPath = new LogPath(logPath.getLogModelId(), logPath.getPathId(), masterFile,
                                             logPath.getDockerPath() + suffix);
                } else {
                    tmpLogPath = new LogPath(logPath.getLogModelId(), logPath.getPathId(), masterFile);
                }
                LogSource logSource = new LogSource(this.modelConfig, tmpLogPath);
                result.add(logSource);
            }
            return result;
        }
        return null;
    }

    public void checkDir(Map<String, LogSource> logSourceMap) {
        if (isStop()) {
            LOGGER.warn("this model has bean stopped. ignore. uniqueKey is " + getUniqueKey());
            return;
        }
        if (logSourceMap != null) {
            Set<String> needToAdd = CollectUtils.getStrAdd(this.sources.keySet(),
                logSourceMap.keySet());
            Set<String> needToDel = CollectUtils.getStrDel(this.sources.keySet(),
                logSourceMap.keySet());

            // add
            for (String key : needToAdd) {
                LogSource logSource = logSourceMap.get(key);
                addTask(this.modelConfig, logSource);
            }

            // del
            for (String key : needToDel) {
                String taskKey = buildLogTaskKey(this.modelConfig.getCommonConfig().getModelId(),
                    key);
                AbstractTask task = tasks.get(taskKey);
                if (task != null) {
                    task.delete();
                    sources.remove(task.getSource().getUniqueKey());
                    tasks.remove(task.getUniqueKey());
                }
            }
        }
    }

    public void checkTask() {
        if (isStop()) {
            LOGGER.warn("this model has bean stopped. ignore. uniqueKey is " + getUniqueKey());
            return;
        }

        Map<Long, LogPath> oldPathMap = new HashMap<>();
        LogSourceConfig logSourceConfig = (LogSourceConfig) (this.modelConfig.getSourceConfig());
        if (logSourceConfig.getLogPaths() != null) {
            for (LogPath logPath : logSourceConfig.getLogPaths()) {
                oldPathMap.put(logPath.getPathId(), logPath.clone());
            }
        }

        replaceHostName(logSourceConfig);

        Map<String, LogSource> newLogSourceMap = new HashMap<>();
        List<LogPath> logPaths = logSourceConfig.getLogPaths();
        if (logPaths != null) {
            for (LogPath logPath : logPaths) {
                if (((LogSourceConfig) this.modelConfig.getSourceConfig()).getMatchConfig().getFileType() == FileType.File.getStatus()) {
                    // 文件
                    LogSource logSource = new LogSource(modelConfig, logPath);
                    newLogSourceMap.put(logSource.getUniqueKey(), logSource);
                } else {
                    // 目录
                    List<LogSource> logSources = getSourcesByDir(logPath.getRealPath(), logPath);
                    for (LogSource logSource : logSources) {
                        newLogSourceMap.put(logSource.getUniqueKey(), logSource);
                    }
                }
            }
        }

        Set<String> needToAdd = CollectUtils.getStrAdd(this.sources.keySet(), newLogSourceMap.keySet());
        Set<String> needToDel = CollectUtils.getStrDel(this.sources.keySet(), newLogSourceMap.keySet());

        // del
        if (!needToDel.isEmpty()) {
            LOGGER.warn("logPath has changed which to delete. needToDel path is " + needToDel + ",key is "
                        + getUniqueKey());
            for (String key : needToDel) {
                String taskKey = buildLogTaskKey(this.modelConfig.getCommonConfig().getModelId(), key);
                AbstractTask task = tasks.get(taskKey);
                if (task != null) {
                    Long pathId = ((LogSource) task.getSource()).getLogPath().getPathId();
                    task.specialDelete(oldPathMap.get(pathId));
                    sources.remove(key);
                    tasks.remove(task.getUniqueKey());
                }
            }
        }

        // add
        if (!needToAdd.isEmpty()) {
            LOGGER.warn("logPath has changed which to add. needToAdd path is " + needToAdd + ",key is "
                        + getUniqueKey());
            for (String key : needToAdd) {
                LogSource logSource = newLogSourceMap.get(key);
                directAddTask(logSource);
            }
        }
    }

    /**
     * 直接提交任务
     * @param logSource
     */
    private void directAddTask(LogSource logSource) {
        LOGGER.info("directAddTask to taskRunning pool. logSource is " + logSource.getUniqueKey());
        this.sources.put(logSource.getUniqueKey(), logSource);
        AbstractTask task = buildTask(this.modelConfig, logSource);
        task.init(this.modelConfig);
        task.start();
        tasks.put(task.getUniqueKey(), task);
        TaskRunningPool.submit(task);
    }

    private String buildPreLogTaskKey(String logModelHostName, Long modelId, Long pathId) {
        return logModelHostName + "_" + modelId + "_" + pathId + "_";
    }

    private String buildLogTaskKey(Long modelId, String sourceKey) {
        return modelId + "_" + sourceKey + "_" + modelConfig.getTag();
    }

    private String getLogSourceKey(LogPath logPath) {
        return logPath.getPathId() + "_" + logPath.getPath();
    }

    @Override
    public Map<String, Object> metric() {
        return null;
    }

}
