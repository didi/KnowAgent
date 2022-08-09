package com.didichuxing.datachannel.agent.task.log;

import static junit.framework.TestCase.assertTrue;

import java.util.*;

import com.didichuxing.datachannel.agent.common.configs.v2.component.ChannelConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.CommonConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.EventMetricsConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelLimitConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.didichuxing.datachannel.agent.common.api.CollectType;
import com.didichuxing.datachannel.agent.common.api.FileType;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.beans.LogPath;
import com.didichuxing.datachannel.agent.common.configs.v2.OffsetConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.targetConfig.TargetConfig;
import com.didichuxing.datachannel.agent.engine.AbstractTask;
import com.didichuxing.datachannel.agent.engine.source.AbstractSource;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.source.log.LogSource;
import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.agent.source.log.config.MatchConfig;
import com.didichuxing.datachannel.agent.source.log.offset.OffsetManager;
import com.didichuxing.datachannel.agent.source.log.utils.FileUtils;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-29 14:21
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ CommonUtils.class })
public class LogModelTest extends LogFileUtils {

    private volatile long time = 1;

    @Test
    public void init() {
        ModelConfig modelConfig = getModelConfig();
        TestLogModel logModel = new TestLogModel(modelConfig);
        logModel.init(modelConfig);
        assertTrue(ScheduleDirMonitor.INSTANCE.getModelMap().size() == 0);
        logModel.delete();

        // dir
        LogSourceConfig logSourceConfig = (LogSourceConfig) modelConfig.getSourceConfig();
        logSourceConfig.getMatchConfig().setFileType(FileType.Dir.getStatus());
        logSourceConfig.getLogPaths().get(0).setRealPath(baseFile);

        initFile(baseFilePath_6);
        logModel.init(modelConfig);
        assertTrue(ScheduleDirMonitor.INSTANCE.getModelMap().size() == 1);
    }

    @Test
    public void configure() {
        ModelConfig modelConfig = getModelConfig();
        TestLogModel logModel = new TestLogModel(modelConfig);
        logModel.configure(modelConfig);
        assertTrue(logModel.getSources().size() == 1);
        assertTrue(logModel.getTasks().size() == 1);
        logModel.delete();

        // dir
        LogSourceConfig logSourceConfig = (LogSourceConfig) modelConfig.getSourceConfig();
        logSourceConfig.getMatchConfig().setFileType(FileType.Dir.getStatus());
        logSourceConfig.getLogPaths().get(0).setRealPath(baseFile);

        initFile(baseFilePath_6);
        logModel.configure(modelConfig);
        assertTrue(logModel.getSources().size() == 3);
        assertTrue(logModel.getTasks().size() == 3);
    }

    @Test
    public void stop() {
        ModelConfig modelConfig = getModelConfig();
        TestLogModel logModel = new TestLogModel(modelConfig);

        // dir
        LogSourceConfig logSourceConfig = (LogSourceConfig) modelConfig.getSourceConfig();
        logSourceConfig.getMatchConfig().setFileType(FileType.Dir.getStatus());
        logSourceConfig.getLogPaths().get(0).setRealPath(baseFile);

        initFile(baseFilePath_6);
        logModel.init(modelConfig);
        logModel.start();
        assertTrue(ScheduleDirMonitor.INSTANCE.getModelMap().size() == 1);
        logModel.stop(true);
        assertTrue(ScheduleDirMonitor.INSTANCE.getModelMap().size() == 0);
    }

    @Test
    public void delete() {
        ModelConfig modelConfig = getModelConfig();
        TestLogModel logModel = new TestLogModel(modelConfig);

        // dir
        LogSourceConfig logSourceConfig = (LogSourceConfig) modelConfig.getSourceConfig();
        logSourceConfig.getMatchConfig().setFileType(FileType.Dir.getStatus());
        logSourceConfig.getLogPaths().get(0).setRealPath(baseFile);

        initFile(baseFilePath_6);
        logModel.init(modelConfig);
        logModel.start();
        assertTrue(ScheduleDirMonitor.INSTANCE.getModelMap().size() == 1);
        logModel.delete();
        assertTrue(ScheduleDirMonitor.INSTANCE.getModelMap().size() == 0);
    }

    @Test
    public void addTask() {
        ModelConfig modelConfig = getModelConfig();
        LogSource logSource = getLogSource(modelConfig);

        TestLogModel logModel = new TestLogModel(modelConfig);

        logModel.addTask(modelConfig, logSource);
        assertTrue(logModel.getSources().size() == 1);
        assertTrue(logModel.getTasks().size() == 1);
    }

    @Test
    public void replaceHostName() {
        String path0 = "/tmp/new-log-agent/logSourceTest/didi.log";
        String path1 = "/tmp/new-log-agent/logSourceTest/didi_${hostname}.log";
        String path11 = "/tmp/new-log-agent/logSourceTest/didi_" + CommonUtils.getHOSTNAME() + ".log";
        String path2 = "/tmp/new-log-agent/logSourceTest_${hostname}/didi.log";
        String path21 = "/tmp/new-log-agent/logSourceTest_" + CommonUtils.getHOSTNAME() + "/didi.log";

        LogPath logPath0 = new LogPath(0L, 0L, path0);
        LogPath logPath1 = new LogPath(0L, 1L, path1);
        LogPath logPath2 = new LogPath(0L, 2L, path2);
        List<LogPath> logPaths = new ArrayList<>();
        logPaths.add(logPath0);
        logPaths.add(logPath1);
        logPaths.add(logPath2);

        LogSourceConfig logSourceConfig = new LogSourceConfig();
        logSourceConfig.setLogPaths(logPaths);

        LogModel logModel = new LogModel(getModelConfig()) {

            @Override
            public AbstractTask buildTask(ModelConfig config, LogSource logSource) {
                return null;
            }
        };

        logModel.replaceHostName(logSourceConfig);

        for (LogPath logPath : logSourceConfig.getLogPaths()) {
            if (logPath.getPathId() == 0) {
                assertTrue(logPath.getRealPath().equals(path0));
            } else if (logPath.getPathId() == 1) {
                assertTrue(logPath.getRealPath().equals(path11));
            } else if (logPath.getPathId() == 2) {
                assertTrue(logPath.getRealPath().equals(path21));
            }
        }
    }

    @Test
    public void canStart() {
        ModelConfig modelConfig = getModelConfig();

        LogModel logModel = new LogModel(modelConfig) {

            @Override
            public AbstractTask buildTask(ModelConfig config, LogSource logSource) {
                return null;
            }
        };

        assertTrue(logModel.canStart());

        modelConfig.getEventMetricsConfig().setOriginalAppName(LogConfigConstants.GLOBAL_FLAG);
        modelConfig.getEventMetricsConfig().setLocation("py,ys");
        logModel.onChange(modelConfig);

        CommonUtils.setHOSTNAME("didi_host_name." + "py");
        assertTrue(logModel.canStart());

        CommonUtils.setHOSTNAME("didi_host_name." + "us01");
        assertTrue(!logModel.canStart());
    }

    @Test
    public void getAddedTasks() {
        // file
        ModelConfig oldModelConfig = getModelConfig();

        ModelConfig newModelConfig = getModelConfig();
        ((LogSourceConfig) newModelConfig.getSourceConfig()).getLogPaths().add(getLogPath());

        TestLogModel logModel = new TestLogModel(oldModelConfig);
        logModel.init(oldModelConfig);
        List<AbstractTask> addTasks = logModel.getAddedTasks(oldModelConfig, newModelConfig);
        List<AbstractTask> updateTasks = logModel.getUpdatedTasks(oldModelConfig, newModelConfig);
        List<AbstractTask> delTasks = logModel.getDeletedTasks(oldModelConfig, newModelConfig);
        assertTrue(addTasks.size() == 1);
        assertTrue(updateTasks.size() == 1);
        assertTrue(delTasks.size() == 0);
        assertTrue(!updateTasks.get(0).getUniqueKey().equals(addTasks.get(0).getUniqueKey()));
        logModel.stop(true);

        // dir
        ModelConfig oldModelConfigDir = getModelConfig();
        LogSourceConfig oldDirLogSourceConfig = (LogSourceConfig) oldModelConfigDir
            .getSourceConfig();
        oldDirLogSourceConfig.setMatchConfig(getMatchConfigForPath7());
        oldDirLogSourceConfig.getLogPaths().get(0).setRealPath(baseFile);

        ModelConfig newModelConfigDir = getModelConfig();
        LogSourceConfig newDirLogSourceConfig = (LogSourceConfig) newModelConfigDir
            .getSourceConfig();
        newDirLogSourceConfig.setMatchConfig(getMatchConfigForAll());
        newDirLogSourceConfig.getLogPaths().get(0).setRealPath(baseFile);

        TestLogModel logModelDir = new TestLogModel(oldModelConfigDir);
        logModelDir.init(oldModelConfigDir);
        List<AbstractTask> addDirTasks = logModelDir.getAddedTasks(oldModelConfigDir,
            newModelConfigDir);
        List<AbstractTask> updateDirTasks = logModelDir.getUpdatedTasks(oldModelConfigDir,
            newModelConfigDir);
        List<AbstractTask> delDirTasks = logModelDir.getDeletedTasks(oldModelConfigDir,
            newModelConfigDir);
        assertTrue(addDirTasks.size() == 1);
        assertTrue(updateDirTasks.size() == 1);
        assertTrue(delDirTasks.size() == 0);
        assertTrue(!updateTasks.get(0).getUniqueKey().equals(addTasks.get(0).getUniqueKey()));
        logModelDir.stop(true);
    }

    @Test
    public void getDeletedTasks() {
        // file
        ModelConfig oldModelConfig = getModelConfig();
        ((LogSourceConfig) oldModelConfig.getSourceConfig()).getLogPaths().add(getLogPath());

        ModelConfig newModelConfig = getModelConfig();

        TestLogModel logModel = new TestLogModel(oldModelConfig);
        logModel.init(oldModelConfig);
        List<AbstractTask> addTasks = logModel.getAddedTasks(oldModelConfig, newModelConfig);
        List<AbstractTask> updataTasks = logModel.getUpdatedTasks(oldModelConfig, newModelConfig);
        List<AbstractTask> deltTasks = logModel.getDeletedTasks(oldModelConfig, newModelConfig);
        assertTrue(addTasks.size() == 0);
        assertTrue(updataTasks.size() == 1);
        assertTrue(deltTasks.size() == 1);
        assertTrue(!updataTasks.get(0).getUniqueKey().equals(deltTasks.get(0).getUniqueKey()));
        logModel.stop(true);

        // dir
        ModelConfig oldModelConfigDir = getModelConfig();
        LogSourceConfig oldDirLogSourceConfig = (LogSourceConfig) oldModelConfigDir
            .getSourceConfig();
        oldDirLogSourceConfig.setMatchConfig(getMatchConfigForAll());
        oldDirLogSourceConfig.getLogPaths().get(0).setRealPath(baseFile);

        ModelConfig newModelConfigDir = getModelConfig();
        LogSourceConfig newDirLogSourceConfig = (LogSourceConfig) newModelConfigDir
            .getSourceConfig();
        newDirLogSourceConfig.setMatchConfig(getMatchConfigForPath7());
        newDirLogSourceConfig.getLogPaths().get(0).setRealPath(baseFile);

        TestLogModel logModelDir = new TestLogModel(oldModelConfigDir);
        logModelDir.init(oldModelConfigDir);
        List<AbstractTask> addDirTasks = logModelDir.getAddedTasks(oldModelConfigDir,
            newModelConfigDir);
        List<AbstractTask> updateDirTasks = logModelDir.getUpdatedTasks(oldModelConfigDir,
            newModelConfigDir);
        List<AbstractTask> delDirTasks = logModelDir.getDeletedTasks(oldModelConfigDir,
            newModelConfigDir);
        assertTrue(addDirTasks.size() == 0);
        assertTrue(updateDirTasks.size() == 1);
        assertTrue(delDirTasks.size() == 1);
        assertTrue(!updataTasks.get(0).getUniqueKey().equals(deltTasks.get(0).getUniqueKey()));
        logModelDir.stop(true);
    }

    protected MatchConfig getMatchConfigForAll() {
        MatchConfig matchConfig = new MatchConfig();
        matchConfig.setFileType(FileType.Dir.getStatus());
        matchConfig.setFileFilterType(LogConfigConstants.FILE_FILTER_TYPE_WHIAT);
        List<String> files = new ArrayList<>();
        files.add(baseFile + ".*");
        matchConfig.setFileFilterRules(files);
        return matchConfig;
    }

    protected MatchConfig getMatchConfigForPath7() {
        MatchConfig matchConfig = new MatchConfig();
        matchConfig.setFileType(FileType.Dir.getStatus());
        matchConfig.setFileFilterType(LogConfigConstants.FILE_FILTER_TYPE_WHIAT);
        List<String> files = new ArrayList<>();
        files.add(baseFile + "didi_test.*");
        matchConfig.setFileFilterRules(files);
        return matchConfig;
    }

    private LogPath getLogPath() {
        LogPath logPath = new LogPath(defaultModelId, time, getFilePath(time));
        time++;
        return logPath;
    }

    private LogSourceConfig getFileLogSourceConfig() {
        LogSourceConfig logSourceConfig = new LogSourceConfig();
        List<LogPath> logPaths = new ArrayList<>();
        LogPath logPath1 = new LogPath(defaultModelId, time * 1, getFilePath(time * 1));
        LogPath logPath2 = new LogPath(defaultModelId, time * 2, getFilePath(time * 2));
        logPaths.add(logPath1);
        logPaths.add(logPath2);
        logSourceConfig.setLogPaths(logPaths);
        time++;
        return logSourceConfig;
    }

    private String getFilePath(long time) {
        StringBuilder sb = new StringBuilder(baseFile);
        for (int i = 0; i < time; i++) {
            sb.append("d");
        }
        return sb.toString() + ".log";
    }

    @Test
    public void checkDir() {
        ModelConfig modelConfig = getModelConfig();
        TestLogModel logModel = new TestLogModel(modelConfig);

        // dir
        LogSourceConfig logSourceConfig = (LogSourceConfig) modelConfig.getSourceConfig();
        logSourceConfig.getMatchConfig().setFileType(FileType.Dir.getStatus());
        logSourceConfig.getLogPaths().get(0).setRealPath(baseFile);

        initFile(baseFilePath_6);
        logModel.init(modelConfig);
        logModel.start();
        assertTrue(ScheduleDirMonitor.INSTANCE.getModelMap().size() == 1);

        initFile(baseFilePath_7);
        deleteFile(baseFilePath_6);

        logModel.checkDir(getSourcesByModel(logModel));

        assertTrue(logModel.getTasks().size() == 2);
        int i = 0;
        for (AbstractTask task : logModel.getTasks().values()) {
            String path = ((LogSource) task.getSource()).getLogPath().getRealPath();
            if (path.equals(baseFilePath_7) || path.equals(baseFilePath)) {
                i++;
            }
        }

        assertTrue(i == 2);
    }

    @Test
    public void checkTask() {
        String prePath = "/home/prePath";
        String sufPath = "/home/sufPath";

        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.getHOSTNAME()).thenReturn("checkTask_tmp_host");

        ModelConfig modelConfig = getModelConfig();
        modelConfig.setCollectType(CollectType.COLLECT_IN_DDCLOUD_SERVER.getStatus());
        modelConfig.setHostname(CommonUtils.getHOSTNAME());
        TestLogModel logModel = new TestLogModel(modelConfig);

        logModel.init(modelConfig);
        logModel.start();

        logModel.checkTask();

        assertTrue(logModel.getTasks().size() == 1);
        assertTrue(logModel.getSources().size() == 1);

        // 判断sourceKey
        LogSource logSource = null;
        for (AbstractSource source : logModel.getSources().values()) {
            logSource = (LogSource) source;
        }
        String sourceKey = logSource.getUniqueKey();
        assertTrue(sourceKey.contains(sufPath) && !sourceKey.contains(prePath));
        assertTrue(sourceKey.equals(getSourceKey(modelConfig)));

        // 判断taskKey
        TestTask testTask = null;
        for (AbstractTask task : logModel.getTasks().values()) {
            testTask = (TestTask) task;
        }
        String taskKey = testTask.getUniqueKey();
        assertTrue(taskKey.contains(sufPath) && !taskKey.contains(prePath));
        assertTrue(taskKey.equals(getTaskKey(sourceKey, modelConfig)));
    }

    private String getSourceKey(ModelConfig modelConfig) {
        LogSourceConfig logSourceConfig = (LogSourceConfig) modelConfig.getSourceConfig();
        LogPath logPath = logSourceConfig.getLogPaths().get(0);
        return logPath.getPathId() + "_" + logPath.getRealPath() + "_" + modelConfig.getHostname()
               + "_";
    }

    private String getTaskKey(String sourceKey, ModelConfig modelConfig) {
        String sourceId = sourceKey;
        String modelId = modelConfig.getCommonConfig().getModelId() + "";
        String tag = modelConfig.getTag();
        return modelId + "_" + sourceId + "_" + tag;
    }

    private Map<String, LogSource> getSourcesByModel(LogModel logModel) {
        Map<String, LogSource> map = new HashMap<String, LogSource>();
        List<LogPath> logPaths = ((LogSourceConfig) logModel.getModelConfig().getSourceConfig())
            .getLogPaths();
        if (logPaths != null) {
            for (LogPath logPath : logPaths) {
                Map<String, LogSource> tmpMap = getSourcesByDir(logModel.getModelConfig(), logPath);
                if (tmpMap != null) {
                    map.putAll(tmpMap);
                }
            }
        }
        return map;
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

    protected ModelConfig getModelConfig() {
        TargetConfig targetConfig = null;

        ModelConfig modelConfig = new ModelConfig("log2hdfs");
        modelConfig.setTargetConfig(targetConfig);

        CommonConfig commonConfig = new CommonConfig();
        commonConfig.setModelId(defaultModelId);
        modelConfig.setCommonConfig(commonConfig);

        EventMetricsConfig eventMetricsConfig = new EventMetricsConfig();
        eventMetricsConfig.setOdinLeaf("hna");
        eventMetricsConfig.setOriginalAppName("");
        eventMetricsConfig.setTransName("");
        modelConfig.setEventMetricsConfig(eventMetricsConfig);

        LogSourceConfig logSourceConfig = getLogSourceConfig();
        modelConfig.setSourceConfig(logSourceConfig);

        ModelLimitConfig modelLimitConfig = new ModelLimitConfig();
        modelLimitConfig.setRate(10 * 1024 * 1024);
        modelConfig.setModelLimitConfig(modelLimitConfig);

        ChannelConfig channelConfig = new ChannelConfig();
        modelConfig.setChannelConfig(channelConfig);

        return modelConfig;
    }

    protected LogSourceConfig getLogSourceConfig() {
        LogPath logPath = new LogPath(defaultModelId, defaultPathId, baseFilePath);
        List<LogPath> logPathList = new ArrayList<>();
        logPathList.add(logPath);
        Map<Long, List<LogPath>> pathMap = new HashMap<>();
        pathMap.put(defaultModelId, logPathList);

        writeOffset(offsetParent);

        OffsetConfig offsetConfig = new OffsetConfig();
        offsetConfig.setRootDir(offsetParent);
        OffsetManager.init(offsetConfig, pathMap);

        LogSourceConfig logSourceConfig = new LogSourceConfig();
        List<LogPath> logPaths = new ArrayList<>();
        logPaths.add(logPath);
        logSourceConfig.setLogPaths(logPaths);
        logSourceConfig.setMatchConfig(getMatchConfig());
        logSourceConfig.setTimeStartFlag("timestamp=");
        logSourceConfig.setTimeFormat(LogConfigConstants.LONG_TIMESTAMP);

        return logSourceConfig;
    }
}
