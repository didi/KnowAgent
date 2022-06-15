package com.didichuxing.datachannel.agent.task.log;

import java.io.File;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;

import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.beans.LogPath;
import com.didichuxing.datachannel.agent.common.configs.v2.OffsetConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.CommonConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.source.log.LogSource;
import com.didichuxing.datachannel.agent.source.log.beans.FileNode;
import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.agent.source.log.config.MatchConfig;
import com.didichuxing.datachannel.agent.source.log.offset.FileOffSet;
import com.didichuxing.datachannel.agent.source.log.offset.OffsetManager;
import com.didichuxing.datachannel.agent.source.log.utils.FileUtils;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-24 20:53
 */
public class LogFileUtils {

    protected static final long   _2WEEK         = 2 * 7 * 24 * 60 * 60 * 1000L;
    protected static final String SEPARATORCHAR  = "###";

    protected static final String baseFile       = "/tmp/new-log-agent/logSourceTest/";
    protected static final String baseFilePath   = "/tmp/new-log-agent/logSourceTest/didi.log";
    protected static final String baseFilePath_1 = "/tmp/new-log-agent/logSourceTest/didi.log.2018010101";
    protected static final String baseFilePath_2 = "/tmp/new-log-agent/logSourceTest/didi.log.2018010102";
    protected static final String baseFilePath_3 = "/tmp/new-log-agent/logSourceTest/didi.log.2018010103";

    protected static final String baseFilePath_4 = "/tmp/new-log-agent/logSourceTest/didi.log.2018010104";
    protected static final String baseFilePath_5 = "/tmp/new-log-agent/logSourceTest/didi.log.2018010105";
    protected static final String baseFilePath_6 = "/tmp/new-log-agent/logSourceTest/didi.log.wf.2018010105";
    protected static final String baseFilePath_7 = "/tmp/new-log-agent/logSourceTest/didi_test.log.wf";

    protected static final String offsetParent   = "/tmp/new-log-agent/offset/";
    protected static final String offsetParent_0 = "/tmp/new-log-agent/offset/test/";

    protected Long                defaultModelId = 0L;
    protected Long                defaultPathId  = 0L;

    protected static final int    MAX_LINE       = 100;

    @Before
    public void before() {
        try {
            initFile(baseFilePath);
            Thread.sleep(500);
            initFile(baseFilePath_1);
            Thread.sleep(500);
            initFile(baseFilePath_2);
            Thread.sleep(500);
            initFile(baseFilePath_3);
            Thread.sleep(500);
            initFile(baseFilePath_7);
        } catch (Exception e) {
            e.printStackTrace();
        }

        File dir = new File(offsetParent);
        dir.mkdirs();

        File dir0 = new File(offsetParent_0);
        dir0.mkdirs();
    }

    @After
    public void after() {
        deleteFile(baseFilePath);
        deleteFile(baseFilePath_1);
        deleteFile(baseFilePath_2);
        deleteFile(baseFilePath_3);
        deleteFile(baseFilePath_4);
        deleteFile(baseFilePath_5);
        deleteFile(baseFilePath_6);
        deleteFile(baseFilePath_7);

        FileUtils.delFile(new File(offsetParent));
        OffsetManager.stop();
    }

    protected List<String> getContent(Map<String, FileOffSet> m) {
        List<String> ret = new ArrayList<>();
        for (String key : m.keySet()) {
            FileOffSet fileOffSet = m.get(key);
            if (fileOffSet.getLastModifyTime() != null && !fileOffSet.getLastModifyTime().equals(0L)
                && System.currentTimeMillis() - fileOffSet.getLastModifyTime() > _2WEEK) {
                // 过滤掉2周之前的offset信息,防止offset暴增无法清理
                // LOGGER.warn("offset is over due. ignore! offset is " + fileOffSet);
                continue;
            }
            ret.add(fileOffSet.getOffsetKeyForOldVersion() + SEPARATORCHAR + fileOffSet.toJson().toJSONString());
        }
        return ret;
    }

    protected String getLogMod4PathKey(Long logId, Long pathId, String masterFilePath) {
        if (StringUtils.isNotBlank(masterFilePath)) {
            masterFilePath = masterFilePath.trim();
            String suffix = null;
            if (masterFilePath.contains("/")) {
                suffix = masterFilePath.replace("/", "_");
            } else {
                suffix = masterFilePath;
            }
            return logId + "_" + pathId + "_" + suffix;
        } else {
            return logId + "_" + pathId;
        }
    }

    protected FileOffSet getFileOffset(String filePath) {
        FileNode fileNode = getFileNode(filePath);
        FileOffSet fileOffSet = new FileOffSet(defaultModelId, defaultPathId, filePath,
            fileNode.getFileKey());
        fileOffSet.setLastModifyTime(FileUtils.getModifyTime(new File(filePath)));
        return fileOffSet;
    }

    public FileNode getFileNode(String filePath) {
        File file = new File(filePath);
        FileNode fileNode = new FileNode(defaultModelId, defaultPathId,
            FileUtils.getFileKeyByAttrs(file), file.lastModified(), file.getParent(),
            file.getName(), file.length(), file);
        return fileNode;
    }

    protected long getFileSize(String filePath) {
        return new File(filePath).length();
    }

    public void initFile(String path) {
        File file = new File(path);
        createFiles(path);
        try {
            List<String> lines = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            String bigLine = "bigline";
            for (int i = 0; i < MAX_LINE; i++) {
                lines.add("timestamp=" + System.currentTimeMillis() + ",line=" + i + "," + bigLine + " from file:"
                          + path);
            }

            FileUtils.writeFileContent(file, lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void overWriteFileForOneLine(String path) {
        File file = new File(path);
        createFiles(path);
        try {
            List<String> lines = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            String bigLine = "bigline";
            for (int i = 0; i < 1; i++) {
                lines.add("overWriteFileForOneLine.timestamp=" + System.currentTimeMillis() + ",line=" + i + "," + bigLine + " from file:"
                          + path);
            }

            FileUtils.writeFileContent(file, lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected int getLineNum(String line) {
        if (StringUtils.isNotBlank(line)) {
            line = line.substring(line.indexOf("line=") + 5);
            line = line.substring(0, line.indexOf(","));
            return Integer.parseInt(line);
        }
        return -1;
    }

    public void createFiles(String path) {
        File file = new File(path);
        File dir = new File(path.substring(0, path.lastIndexOf(File.separator) + 1));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {

            }
        }
    }

    protected LogSource getLogSource() {
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

        ModelConfig modelConfig = new ModelConfig("log");
        modelConfig.setSourceConfig(logSourceConfig);
        CommonConfig commonConfig = new CommonConfig();
        modelConfig.setCommonConfig(commonConfig);

        LogSource logSource = new LogSource(modelConfig, logPath);
        logSource.configure(null);
        List<FileNode> fileNodes = logSource.getFileNodes();
        logSource.addToPeriodicityCollect(fileNodes);

        return logSource;
    }

    protected LogSource getLogSource(ModelConfig modelConfig) {
        LogSourceConfig logSourceConfig = (LogSourceConfig) modelConfig.getSourceConfig();
        LogPath logPath = logSourceConfig.getLogPaths().get(0);
        LogSource logSource = new LogSource(modelConfig, logPath);
        return logSource;
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

    protected LogSourceConfig getLogSourceConfigWF() {
        LogPath logPath = new LogPath(defaultModelId, defaultPathId, baseFilePath_7);
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

    protected MatchConfig getMatchConfig() {
        MatchConfig matchConfig = new MatchConfig();
        matchConfig.setFileSuffix(".2018010101");
        return matchConfig;
    }

    /**
     * 返回待采集set of key
     * @return
     */
    protected Set<String> writeOffset(String dir) {
        Set<String> result = new HashSet<>();
        Map<String, FileOffSet> fileOffSetMap = new HashMap<>();
        // 设置offset
        FileOffSet fileOffSet0 = getFileOffset(baseFilePath);
        fileOffSet0.setOffSet(0L);
        result.add(fileOffSet0.getFileKey());

        FileOffSet fileOffSet1 = getFileOffset(baseFilePath_1);
        fileOffSet1.setOffSet(0L);
        result.add(fileOffSet1.getFileKey());

        FileOffSet fileOffSet2 = getFileOffset(baseFilePath_2);
        fileOffSet2.setOffSet(0L);
        result.add(fileOffSet2.getFileKey());

        FileOffSet fileOffSet3 = getFileOffset(baseFilePath_3);
        fileOffSet3.setOffSet(0L);
        result.add(fileOffSet3.getFileKey());

        fileOffSetMap.put(fileOffSet0.getFileKey(), fileOffSet0);
        fileOffSetMap.put(fileOffSet1.getFileKey(), fileOffSet1);
        fileOffSetMap.put(fileOffSet2.getFileKey(), fileOffSet2);
        fileOffSetMap.put(fileOffSet3.getFileKey(), fileOffSet3);

        String fileName = getLogMod4PathKey(defaultModelId, defaultPathId, baseFilePath);
        List<String> offsets = getContent(fileOffSetMap);
        try {
            FileUtils.writeFileContent(new File(dir + fileName), offsets);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Long getDefaultModelId() {
        return defaultModelId;
    }

    public void setDefaultModelId(Long defaultModelId) {
        this.defaultModelId = defaultModelId;
    }

    public Long getDefaultPathId() {
        return defaultPathId;
    }

    public void setDefaultPathId(Long defaultPathId) {
        this.defaultPathId = defaultPathId;
    }
}
