package com.didichuxing.datachannel.agent.source.log.monitor;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.didichuxing.datachannel.agent.common.configs.v2.component.CommonConfig;
import com.didichuxing.datachannel.agent.source.log.config.MatchConfig;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.didichuxing.datachannel.agent.common.beans.LogPath;
import com.didichuxing.datachannel.agent.common.configs.v2.OffsetConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.source.log.LogSource;
import com.didichuxing.datachannel.agent.source.log.beans.FileNode;
import com.didichuxing.datachannel.agent.source.log.beans.WorkingFileNode;
import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.agent.source.log.offset.FileOffSet;
import com.didichuxing.datachannel.agent.source.log.offset.OffsetManager;
import com.didichuxing.datachannel.agent.source.log.utils.FileUtils;

/**
 * @description:
 * @author: huangjw
 * @Date: 18/6/19 15:19
 */
public class RealLogFileMonitorTest {

    private static final Long   defaultModelId = 0L;
    private static final Long   defaultPathId  = 0L;
    private static final long   _2WEEK         = 2 * 7 * 24 * 60 * 60 * 1000L;
    private static final String SEPARATORCHAR  = "###";

    private static final String defaultPath    = "/tmp/tmp/test.txt";
    private static final String offsetParent   = "/tmp/new-log-agent/offset/";

    @Test
    public void start() throws Exception {

        try {
            LogSource logSource = getLogSource(null);
            RealTimeFileMonitor.INSTANCE.start();
            RealTimeFileMonitor.INSTANCE.register(logSource);

            Thread.sleep(5000L);

            createFiles(defaultPath);

            Thread.sleep(500L);
            createFiles(defaultPath + ".1");
            Thread.sleep(500L);
            createFiles(defaultPath + ".2");
            Thread.sleep(500L);
            createFiles(defaultPath + ".10");
            Thread.sleep(500L);
            createFiles("/tmp/tmp/tmp/tmp/test1.txt");
            Thread.sleep(500L);
            renames(defaultPath, defaultPath + ".3");

            int i = 0;
            while (true) {
                for (WorkingFileNode fileNode : logSource.getCollectingFileNodeMap().values()) {
                    System.out.println(fileNode.getUniqueKey());
                }
                if (logSource.getCollectingFileNodeMap().size() >= 3) {
                    break;
                }
                Thread.sleep(1000L);
                if (i >= 30) {
                    break;
                }
                i++;
            }
            assertTrue(i < 30);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            removeFile(defaultPath);
            removeFile(defaultPath + ".1");
            removeFile(defaultPath + ".10");
            removeFile(defaultPath + ".2");
            removeFile(defaultPath + ".3");
            removeFile("/tmp/tmp/tmp/tmp/test1.txt");
            RealTimeFileMonitor.INSTANCE.stop();
        }
    }

    @Test
    public void start2() throws Exception {
        String path = "/tmp/tmp/tmp/test.txt";
        try {
            LogSource logSource = getLogSource(path);

            RealTimeFileMonitor.INSTANCE.start();
            RealTimeFileMonitor.INSTANCE.register(logSource);

            Thread.sleep(5000L);

            createFiles(path);

            Thread.sleep(500L);
            createFiles(path + ".1");
            Thread.sleep(500L);
            createFiles(path + ".2");
            Thread.sleep(500L);
            createFiles(path + ".10");
            Thread.sleep(500L);
            renames(path, path + ".3");

            int i = 0;
            while (true) {
                for (WorkingFileNode fileNode : logSource.getCollectingFileNodeMap().values()) {
                    System.out.println(fileNode.getUniqueKey());
                }
                if (logSource.getCollectingFileNodeMap().size() >= 3) {
                    break;
                }
                Thread.sleep(1000L);
                if (i >= 30) {
                    break;
                }
                i++;
            }
            assertTrue(i < 30);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            removeFile(path);
            removeFile(path + ".1");
            removeFile(path + ".10");
            removeFile(path + ".2");
            removeFile(path + ".3");
            RealTimeFileMonitor.INSTANCE.stop();

        }
    }

    private void removeFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    private void createFiles(String path) {
        File file = new File(path);
        File dir = new File(path.substring(0, path.lastIndexOf(File.separator) + 1));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void renames(String source, String target) {
        File file = new File(source);
        if (file.exists()) {
            try {
                file.renameTo(new File(target));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private LogSource getLogSource(String path) {
        LogPath logPath = new LogPath(defaultModelId, defaultPathId, StringUtils.isNotBlank(path) ? path : defaultPath);
        List<LogPath> logPathList = new ArrayList<>();
        logPathList.add(logPath);
        Map<Long, List<LogPath>> pathMap = new HashMap<>();
        pathMap.put(defaultModelId, logPathList);

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
        commonConfig.setModelId(defaultModelId);
        modelConfig.setCommonConfig(commonConfig);

        LogSource logSource = new LogSource(modelConfig, logPath);
        logSource.configure(null);
        List<FileNode> fileNodes = logSource.getFileNodes();
        logSource.addToPeriodicityCollect(fileNodes);

        return logSource;
    }

    private MatchConfig getMatchConfig() {
        MatchConfig matchConfig = new MatchConfig();
        matchConfig.setFileSuffix(".1");
        return matchConfig;
    }

    private List<String> getContent(Map<String, FileOffSet> m) {
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

    private String getLogMod4PathKey(Integer logId, Integer pathId, String masterFilePath) {
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

    private FileOffSet getFileOffset(String filePath) {
        FileNode fileNode = getFileNode(filePath);
        FileOffSet fileOffSet = new FileOffSet(defaultModelId, defaultPathId, filePath,
            fileNode.getFileKey());
        fileOffSet.setLastModifyTime(FileUtils.getModifyTime(new File(filePath)));
        return fileOffSet;
    }

    private FileNode getFileNode(String filePath) {
        File file = new File(filePath);
        FileNode fileNode = new FileNode(defaultModelId, defaultPathId,
            FileUtils.getFileKeyByAttrs(file), file.lastModified(), file.getParent(),
            file.getName(), file.length(), file);
        return fileNode;
    }

    private long getFileSize(String filePath) {
        return new File(filePath).length();
    }
}
