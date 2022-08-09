package com.didichuxing.datachannel.agent.source.log;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.beans.LogPath;
import com.didichuxing.datachannel.agent.common.configs.v2.OffsetConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.CommonConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.bean.Event;
import com.didichuxing.datachannel.agent.source.log.beans.FileNode;
import com.didichuxing.datachannel.agent.source.log.beans.WorkingFileNode;
import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.agent.source.log.config.MatchConfig;
import com.didichuxing.datachannel.agent.source.log.offset.FileOffSet;
import com.didichuxing.datachannel.agent.source.log.offset.OffsetManager;
import com.didichuxing.datachannel.agent.source.log.utils.FileUtils;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-23 20:02
 */
public class LogSourceTest extends FileTest {

    private final static Long ONE_HOUR = 60 * 60 * 1000L;

    @Test
    public void appendFile() {
        LogSource logSource = getLogSource();
        logSource.getLogSourceConfig().setTimeFormat(LogConfigConstants.NO_LOG_TIME);
        logSource.getLogSourceConfig().setReadTimeOut(1L);
        logSource.getLogSourceConfig().setMaxThreadNum(4);

        FileNode fileNode0 = getFileNode(baseFilePath);

        initFile(baseFilePath_4);
        FileNode fileNode4 = getFileNode(baseFilePath_4);
        logSource.appendFile(fileNode4);

        assertTrue(logSource.getCollectingFileNodeMap().containsKey(fileNode0.getNodeKey()));
        assertTrue(logSource.getCollectingFileNodeMap().containsKey(fileNode4.getNodeKey()));

        initFile(baseFilePath_5);
        FileNode fileNode5 = getFileNode(baseFilePath_5);
        logSource.appendFile(fileNode5);

        assertTrue(!logSource.getCollectingFileNodeMap().containsKey(fileNode0.getNodeKey()));
        assertTrue(logSource.getCollectingFileNodeMap().containsKey(fileNode5.getNodeKey()));
    }

    @Test
    public void addToTemporalityCollect() {
        LogSource logSource = getLogSource();
        logSource.getModelConfig().getCommonConfig()
            .setStartTime(new Date(System.currentTimeMillis() - 10 * 60 * 1000L));
        logSource.getModelConfig().getCommonConfig()
            .setEndTime(new Date(System.currentTimeMillis()));
        List<FileNode> fileNodes = logSource.getFileNodes();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initFile(baseFilePath_4);
        FileNode fileNode4 = getFileNode(baseFilePath_4);
        FileOffSet fileOffSet4 = getFileOffset(baseFilePath_4);
        fileNode4.setFileOffSet(fileOffSet4);

        initFile(baseFilePath_5);
        FileNode fileNode5 = getFileNode(baseFilePath_5);
        FileOffSet fileOffSet5 = getFileOffset(baseFilePath_5);
        fileNode5.setFileOffSet(fileOffSet5);

        fileNodes.add(fileNode4);
        fileNodes.add(fileNode5);

        logSource.addToTemporalityCollect(fileNodes);

        Set<String> fileNodekeys = logSource.getCollectingFileNodeMap().keySet();
        assertTrue(fileNodekeys.contains(fileNode4.getNodeKey())
                   && !fileNodekeys.contains(fileNode5.getNodeKey()) && fileNodekeys.size() == 5);
    }

    @Test
    public void tryGetEvent() {
        LogSource logSource = getLogSource();
        logSource.getLogSourceConfig().setTimeFormat(LogConfigConstants.NO_LOG_TIME);
        logSource.getLogSourceConfig().setReadTimeOut(1L);
        logSource.getModelConfig().getCommonConfig()
            .setModelType(LogConfigConstants.COLLECT_TYPE_TEMPORALITY);

        int num = 0;
        for (int i = 0; i < 3 * MAX_LINE; i++) {
            Event event = logSource.tryGetEvent();
            if (event != null) {
                num++;
            }
        }

        assertTrue(num > MAX_LINE);
        Map<String, Object> metrics = logSource.metric();
        for (Map.Entry<String, Object> entry : metrics.entrySet()) {
            System.out.println("key:" + entry.getKey() + ",\tvalue:" + entry.getValue());
        }
    }

    @Test
    public void tryGetMultiLineEvent() {
        LogSource logSource = getLogSource();
        logSource.getLogSourceConfig().setTimeFormat(LogConfigConstants.LONG_TIMESTAMP);
        logSource.getLogSourceConfig().setTimeStartFlag("timestamp=");
        logSource.getLogSourceConfig().setTimeFormatLength(
            LogConfigConstants.LONG_TIMESTAMP.length());
        logSource.getLogSourceConfig().setReadTimeOut(1L);
        logSource.getModelConfig().getCommonConfig()
            .setModelType(LogConfigConstants.COLLECT_TYPE_TEMPORALITY);

        int num = 0;
        for (int i = 0; i < 3 * MAX_LINE; i++) {
            Event event = logSource.tryGetEvent();
            //System.out.println(event.toString());
            if (event != null) {
                num++;
            }
        }

        assertTrue(num > MAX_LINE);
        Map<String, Object> metrics = logSource.metric();
        for (Map.Entry<String, Object> entry : metrics.entrySet()) {
            System.out.println("key:" + entry.getKey() + ",\tvalue:" + entry.getValue());
        }
    }

    @Test
    public void closeFile_temporality() {
        LogSource logSource = getLogSource();
        logSource.getLogSourceConfig().setTimeFormat(LogConfigConstants.NO_LOG_TIME);
        logSource.getLogSourceConfig().setReadTimeOut(1L);
        logSource.getModelConfig().getCommonConfig()
            .setModelType(LogConfigConstants.COLLECT_TYPE_TEMPORALITY);

        for (int i = 0; i < 3 * MAX_LINE; i++) {
            Event event = logSource.tryGetEvent();
        }

        try {
            for (WorkingFileNode wfn : logSource.getCollectingFileNodeMap().values()) {
                wfn.getFileNode().setOffset(wfn.getIn().length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(logSource.getCollectingFileNodeMap().size() == 3);
        logSource.closeFiles();
        assertTrue(logSource.getCollectingFileNodeMap().size() == 0);
        assertTrue(logSource.getCollectingFileNodeList().size() == 0);
    }

    @Test
    public void closeFile_periodicity() {
        LogSource logSource = getLogSource();
        logSource.getLogSourceConfig().setTimeFormat(LogConfigConstants.NO_LOG_TIME);
        logSource.getLogSourceConfig().setReadTimeOut(1L);

        for (int i = 0; i < 3 * MAX_LINE; i++) {
            Event event = logSource.tryGetEvent();
        }

        try {
            for (WorkingFileNode wfn : logSource.getCollectingFileNodeMap().values()) {
                wfn.getFileNode().setOffset(wfn.getIn().length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(logSource.getCollectingFileNodeMap().size() == 3);
        logSource.closeFiles();
        assertTrue(logSource.getCollectingFileNodeMap().size() == 2);
        assertTrue(logSource.getCollectingFileNodeList().size() == 2);
    }

    @Test
    public void checkFileDelFile() {
        Set<String> fileSet = getFileKey023();
        LogSource logSource = getLogSource();

        Set<String> delFileSet = new HashSet<>();
        delFileSet.add(FileUtils.getFileKeyByAttrs(baseFilePath_3));
        deleteFile(baseFilePath_3);
        delFileSet.add(FileUtils.getFileKeyByAttrs(baseFilePath_2));
        deleteFile(baseFilePath_2);

        Map<String, FileNode> fileNodeMap = getFileNodeMap(FileUtils.getPathDir(baseFilePath));
        logSource.checkFile(fileNodeMap);

        for (WorkingFileNode wfn : logSource.getCollectingFileNodeMap().values()) {
            FileNode fileNode = wfn.getFileNode();
            if (fileSet.contains(fileNode.getFileKey())) {
                fileSet.remove(fileNode.getFileKey());
            }
        }

        assertTrue(fileSet.size() == 2);
        assertTrue(CollectionUtils.isEqualCollection(delFileSet, fileSet));
    }

    /**
     * 当前集合内的日志采集文件发生变更
     */
    @Test
    public void checkFileCollectingFileChange() {
        Set<String> fileSet = getFileKey023();
        LogSource logSource = getLogSource();
        logSource.setCurWFileNode(logSource.getCollectingFileNodeList().get(0));

        for (WorkingFileNode wfn : logSource.getCollectingFileNodeMap().values()) {
            wfn.setFileEnd(true);
            wfn.getFileNode().setOffset(wfn.getFileNode().getFile().length());
            wfn.getFileNode().setModifyTime(wfn.getFileNode().getFile().lastModified());
        }

        // 追加数据，改变文件
        appendFile(baseFilePath_2);

        Map<String, FileNode> fileNodeMap = getFileNodeMap(FileUtils.getPathDir(baseFilePath));
        logSource.checkFile(fileNodeMap);

        WorkingFileNode newCurWFN = logSource.getCurWFileNode();
        assertTrue(newCurWFN.getFileNode().getFileKey()
            .equals(FileUtils.getFileKeyByAttrs(baseFilePath_2)));
    }

    /**
     * 当前集合内的日志采集文件发生变更
     */
    @Test
    public void checkFileCollectingFileChange2() {
        LogSource logSource = getLogSource();

        WorkingFileNode wfn2 = null;
        for (WorkingFileNode wfn : logSource.getCollectingFileNodeMap().values()) {
            wfn.setFileEnd(true);
            wfn.getFileNode().setOffset(wfn.getFileNode().getFile().length());
            wfn.getFileNode().setModifyTime(wfn.getFileNode().getFile().lastModified());
            if (wfn.getFileNode().getFile().getAbsolutePath().equals(baseFilePath)) {
                logSource.setCurWFileNode(wfn);
            }

            if (wfn.getFileNode().getFile().getAbsolutePath().equals(baseFilePath_2)) {
                wfn2 = wfn;
            }
        }

        // 追加数据，改变文件
        appendFile(baseFilePath_2);
        wfn2.setFileEnd(false);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        appendFile(baseFilePath);

        // 执行多次，确保准确性
        Map<String, FileNode> fileNodeMap = getFileNodeMap(FileUtils.getPathDir(baseFilePath));
        logSource.checkFile(fileNodeMap);

        fileNodeMap = getFileNodeMap(FileUtils.getPathDir(baseFilePath));
        logSource.checkFile(fileNodeMap);

        WorkingFileNode newCurWFN = logSource.getCurWFileNode();
        assertTrue(newCurWFN.getFileNode().getFileKey()
            .equals(FileUtils.getFileKeyByAttrs(baseFilePath_2)));
    }

    /**
     * 测试两个文件同时变更的case
     */
    @Ignore
    @Test
    public void checkFileCollectingTwoFileChange() {
        // 去除文件2、3,防止干扰
        deleteFile(baseFilePath_2);
        deleteFile(baseFilePath_3);

        LogSource logSource = getLogSource();
        int num = 30;
        int i = 0;
        while (i < num) {
            String content = "content " + i;
            Map<String, FileNode> fileNodeMap;
            if (i % 2 == 0) {
                writeByLine(new File(baseFilePath), content);
                try {
                    Thread.sleep(1000L);
                } catch (Exception e) {

                }

                writeByLine(new File(baseFilePath_1), content);
                fileNodeMap = getFileNodeMap(FileUtils.getPathDir(baseFilePath));
                logSource.checkFile(fileNodeMap);

                WorkingFileNode newCurWFN = logSource.getCurWFileNode();
                assertTrue(newCurWFN.getFileNode().getFileKey()
                    .equals(FileUtils.getFileKeyByAttrs(baseFilePath)));
            } else {
                writeByLine(new File(baseFilePath_1), content);
                try {
                    Thread.sleep(1000L);
                } catch (Exception e) {

                }
                writeByLine(new File(baseFilePath), content);
                fileNodeMap = getFileNodeMap(FileUtils.getPathDir(baseFilePath));
                logSource.checkFile(fileNodeMap);

                WorkingFileNode newCurWFN = logSource.getCurWFileNode();
                assertTrue(newCurWFN.getFileNode().getFileKey()
                    .equals(FileUtils.getFileKeyByAttrs(baseFilePath_1)));
            }
            i++;
        }
    }

    @Test
    public void checkFileAddAndDelFile() {
        Set<String> fileSet = getFileKey023();
        LogSource logSource = getLogSource();

        Set<String> copyFileKeySet = new HashSet<>();
        for (String key : fileSet) {
            copyFileKeySet.add(key);
        }

        createFile(baseFilePath_4, fileSet);
        createFile(baseFilePath_5, fileSet);
        createFile(baseFilePath_6, fileSet);
        copyFileKeySet.add(FileUtils.getFileKeyByAttrs(baseFilePath_4));
        copyFileKeySet.add(FileUtils.getFileKeyByAttrs(baseFilePath_5));

        copyFileKeySet.remove(FileUtils.getFileKeyByAttrs(baseFilePath_3));
        deleteFile(baseFilePath_3);

        Map<String, FileNode> fileNodeMap = getFileNodeMap(FileUtils.getPathDir(baseFilePath));
        logSource.checkFile(fileNodeMap);

        Set<String> remindFileKeySet = new HashSet<>();
        for (WorkingFileNode wfn : logSource.getCollectingFileNodeMap().values()) {
            FileNode fileNode = wfn.getFileNode();
            remindFileKeySet.add(fileNode.getFileKey());
        }

        assertTrue(CollectionUtils.isEqualCollection(remindFileKeySet, copyFileKeySet));
    }

    @Test
    public void checkFileAddFile() {
        Set<String> fileSet = getFileKey023();
        LogSource logSource = getLogSource();

        Set<String> addFileSet = new HashSet<>();
        createFile(baseFilePath_4, fileSet);
        createFile(baseFilePath_5, fileSet);
        createFile(baseFilePath_6, fileSet);
        addFileSet.add(FileUtils.getFileKeyByAttrs(baseFilePath_4));
        addFileSet.add(FileUtils.getFileKeyByAttrs(baseFilePath_5));

        Map<String, FileNode> fileNodeMap = getFileNodeMap(FileUtils.getPathDir(baseFilePath));
        logSource.checkFile(fileNodeMap);

        Set<String> remindFileKeySet = new HashSet<>();
        for (WorkingFileNode wfn : logSource.getCollectingFileNodeMap().values()) {
            FileNode fileNode = wfn.getFileNode();
            if (fileSet.contains(fileNode.getFileKey())) {
                fileSet.remove(fileNode.getFileKey());
            } else {
                remindFileKeySet.add(fileNode.getFileKey());
            }
        }

        assertTrue(fileSet.size() == 1);
        assertTrue(fileSet.contains(FileUtils.getFileKeyByAttrs(baseFilePath_6)));
    }

    /**
     * 其中1表示已经采完
     * @return
     */
    private Set<String> getFileKey023() {
        Set<String> fileKeySet = new HashSet<>();
        fileKeySet.add(FileUtils.getFileKeyByAttrs(baseFilePath));
        // fileKeySet.add(FileUtils.getFileKeyByAttrs(baseFilePath_1));
        fileKeySet.add(FileUtils.getFileKeyByAttrs(baseFilePath_2));
        fileKeySet.add(FileUtils.getFileKeyByAttrs(baseFilePath_3));
        return fileKeySet;
    }

    private Map<String, FileNode> getFileNodeMap(String dir) {
        List<String> children = FileUtils.listChildren(new File(dir));
        Map<String, FileNode> fileNodeMap = new HashMap<>(16);
        for (String child : children) {
            File file = new File(dir + File.separator + child);
            if (!file.exists() || file.isDirectory()) {
                continue;
            }
            FileNode fileNode = new FileNode(null, null, FileUtils.getFileKeyByAttrs(file),
                                             FileUtils.getModifyTime(file), dir, child, file.length(), file);

            fileNodeMap.put(fileNode.getFileKey(), fileNode);
        }
        return fileNodeMap;
    }

    public LogSource getLogSource() {
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

    private void createFile(String path, Set<String> fileKeySet) {
        initFile(path);
        String fileKey = FileUtils.getFileKeyByAttrs(path);
        if (fileKey != null) {
            fileKeySet.add(fileKey);
        }
    }

    @Test
    public void getFileNodes() {
        LogPath logPath = new LogPath(defaultModelId, defaultPathId, baseFilePath);
        List<LogPath> logPathList = new ArrayList<>();
        logPathList.add(logPath);
        Map<Long, List<LogPath>> pathMap = new HashMap<>();
        pathMap.put(defaultModelId, logPathList);

        Set<String> fileKeySet = writeOffset(offsetParent_0);

        OffsetConfig offsetConfig = new OffsetConfig();
        offsetConfig.setRootDir(offsetParent_0);
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

        for (FileNode fileNode : fileNodes) {
            if (fileNode.getNeedCollect()) {
                if (fileKeySet.contains(fileNode.getFileKey())) {
                    fileKeySet.remove(fileNode.getFileKey());
                }
            }
        }
        for (String key : fileKeySet) {
            System.out.println(key);
        }
        assertTrue(fileKeySet.size() == 0);
    }

    private MatchConfig getMatchConfig() {
        MatchConfig matchConfig = new MatchConfig();
        matchConfig.setFileSuffix(".2018010101");
        return matchConfig;
    }

    /**
     * 返回待采集set of key
     * @return
     */
    private Set<String> writeOffset(String dir) {
        Set<String> result = new HashSet<>();
        Map<String, FileOffSet> fileOffSetMap = new HashMap<>();
        // 设置offset
        FileOffSet fileOffSet0 = getFileOffset(baseFilePath);
//        fileOffSet0.setOffSet(100L);
        result.add(fileOffSet0.getFileKey());

        FileOffSet fileOffSet1 = getFileOffset(baseFilePath_1);
        fileOffSet1.setOffSet(getFileSize(baseFilePath_1));

        FileOffSet fileOffSet2 = getFileOffset(baseFilePath_2);
        result.add(fileOffSet2.getFileKey());

        FileOffSet fileOffSet3 = getFileOffset(baseFilePath_3);
        fileOffSet3.setOffSet(getFileSize(baseFilePath_3) / 2);
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

    @Test
    public void testWFNIntervalTime() throws Exception {
        LogSource logSource = getLogSource();
        logSource.getLogSourceConfig().setTimeFormat(LogConfigConstants.NO_LOG_TIME);
        logSource.getLogSourceConfig().setReadTimeOut(1L);
        logSource.getLogSourceConfig().setMaxThreadNum(4);

        FileNode fileNode0 = getFileNode(baseFilePath);
        initFile(baseFilePath);
        logSource.appendFile(fileNode0);

        initFile(baseFilePath_4);
        FileNode fileNode1 = getFileNode(baseFilePath_4);
        logSource.appendFile(fileNode1);

        initFile(baseFilePath_8);
        FileNode fileNode2 = getFileNode(baseFilePath_8);
        logSource.appendFile(fileNode2);

        initFile(baseFilePath_9);
        FileNode fileNode3 = getFileNode(baseFilePath_9);
        logSource.appendFile(fileNode3);

        try {
            boolean allMatch = true;

            for (WorkingFileNode wfn : logSource.getCollectingFileNodeList()) {
                if (wfn.getFileNode().getAbsolutePath().equals(baseFilePath)) {
                    Long modifyTime = 1514847784000L;
                    wfn.getFileNode().setModifyTime(modifyTime);
                    wfn.open(0, 3);
                    allMatch = wfn.getIntervalTime() == 1514847600000L;
                    if (!allMatch) {
                        break;
                    }
                }

                wfn.open(0, 3);
                if (wfn.getFileNode().getAbsolutePath().equals(baseFilePath_4)) {
                    allMatch = wfn.getIntervalTime() == 1514750400000L;
                    if (!allMatch) {
                        break;
                    }
                }

                if (wfn.getFileNode().getAbsolutePath().equals(baseFilePath_8)) {
                    allMatch = wfn.getIntervalTime() == 1514736000000L;
                    if (!allMatch) {
                        break;
                    }
                }

                if (wfn.getFileNode().getAbsolutePath().equals(baseFilePath_9)) {
                    allMatch = wfn.getIntervalTime() == (System.currentTimeMillis() / ONE_HOUR)
                                                        * ONE_HOUR;
                    if (!allMatch) {
                        break;
                    }
                }
            }
            assertTrue(allMatch);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for (WorkingFileNode wfn : logSource.getCollectingFileNodeList()) {
                wfn.close();
            }
        }
    }

    @Test
    public void InodeReuseTest(){
        Long logModelId1 = 0L;
        Long pathId11 = 0L;
        String path11 = "/tmp/new-log-agent/logSourceTest/didi.log";
        String fileKey = "12345_6789";
        LogPath logPath = new LogPath(logModelId1,pathId11,path11);
        Map<String, ConcurrentHashMap<String, FileOffSet>> map1 = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, FileOffSet> map2 = new ConcurrentHashMap<>();

        String k1 = fileKey;
        FileOffSet fileOffSet = new FileOffSet(logModelId1,pathId11,path11,fileKey);
        map2.put(k1,fileOffSet);
        String key1 = OffsetManager.getLogMod4PathKey(logModelId1,pathId11,path11);
        map1.put(key1,map2);
        //OffsetManager.offsetMap.putAll(map1);
        System.out.println(map1.get(key1));

        LogSource logSource = getLogSource();
        logSource.getModelConfig().getCommonConfig()
                .setStartTime(new Date(System.currentTimeMillis() - 10 * 60 * 1000L));
        logSource.getModelConfig().getCommonConfig()
                .setEndTime(new Date(System.currentTimeMillis()));
        List<FileNode> fileNodes = logSource.getFileNodes();

        //第一种情况，Inode相同
        fileNodes.get(0).setFileKey("12345_6789");
        fileNodes.get(0).setFileKey("12345_6789");
        fileNodes.get(0).setOffset(10086L);
        logSource.appendFile(fileNodes.get(0));
        System.out.println(fileNodes.get(0).getOffset());
        assertTrue(fileNodes.get(0).getOffset()!=0L);
    }
}
