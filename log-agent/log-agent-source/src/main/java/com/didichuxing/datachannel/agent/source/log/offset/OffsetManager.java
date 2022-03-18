package com.didichuxing.datachannel.agent.source.log.offset;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.beans.LogPath;
import com.didichuxing.datachannel.agent.common.configs.v2.OffsetConfig;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.source.log.beans.FileNode;
import com.didichuxing.datachannel.agent.source.log.utils.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: offset管理器
 * @author: huangjw
 * @Date: 18/6/19 18:52
 */
public class OffsetManager {

private static final Logger LOGGER = LoggerFactory.getLogger(OffsetManager.class.getName());private static final String                                       OLD_AGENT_PATH                 = System.getProperty("user.home")
                                                                                                       + File.separator
                                                                                                       + ".logOffSet";

    private static final long                                         PERIOD                         = 30 * 1000L;                                      // 1min
    private static final long                                         _2WEEK                         = 2 * 7 * 24 * 60
                                                                                                       * 60 * 1000L;
    private static final long                                         _30HOUR                        = 30 * 60 * 60
                                                                                                       * 1000L;
    private static final long                                         _12HOUR                        = 12 * 60 * 60
                                                                                                       * 1000L;
    private static final String                                       SEPARATORCHAR                  = "###";
    private static final String                                       NO_INODE                       = "no_inode";

    public static final String                                        TMP_SUFFIX                     = ".agent.tmp";
    public static final String                                        DEL_SUFFIX                     = ".agent.del";

    /**
     * key:$taskId_$pathId_masterFile'slogPath value: fileKey, FileOffSe
     */
    private static Map<String, ConcurrentHashMap<String, FileOffSet>> offsetMap                      = new ConcurrentHashMap<>();

    private static OffsetConfig                                       config;
    private static boolean                                            init                           = false;
    private static final Object                                       lock                           = new Object();
    private static Thread                                             thread;
    private static volatile boolean                                   isRunning                      = false;
    /**
     * 文件最长长度
     */
    private static final int                                          FILE_MAX_LENGTH                = 250;

    public OffsetManager() {
    }

    public static void onChange(OffsetConfig newConfig) {
        LOGGER.info("begin to change offsetConfig.newConfig is " + newConfig);
        config = newConfig;
        if (init) {
            flush();
        }
    }

    /**
     * 初始化
     * @param offsetConfig OffsetConfig
     * @param logPathMap key：taskId, value:List<logPath>
     */
    public static synchronized void init(OffsetConfig offsetConfig, Map<Long, List<LogPath>> logPathMap) {
        config = offsetConfig;
        LOGGER.info("begin to init OffsetManager.config is " + config);
        if (init) {
            return;
        }

        if (!offsetMap.isEmpty()) {
            offsetMap.clear();
        }
        loadOffset(logPathMap);
        LOGGER.info("all of offset is " + offsetMap);

        flush();

        isRunning = true;
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (isRunning) {
                    try {
                        Thread.sleep(PERIOD);
                    } catch (InterruptedException e) {
                        LogGather.recordErrorLog("OffsetManager error", "sleep error", e);
                    }

                    try {
                        flush();
                    } catch (Throwable t) {
                        LogGather.recordErrorLog("OffsetManager error", "flush error", t);
                    }
                }
                LOGGER.info("offset flush thread exist!");
            }
        });
        thread.setDaemon(false);
        thread.setName("offset-manager-flush-thread");
        thread.start();

        init = true;
    }

    public static synchronized void stop() {
        LOGGER.info("begin to stop OffsetManager.");
        if (!init) {
            return;
        }
        flush();
        isRunning = false;
        thread.interrupt();
        init = false;

    }

    /**
     * 同步offset,删除多余offset
     */
    public static void sync(String hostname, Long modelId, Long pathId, Set<String> masterFiles) {
        LOGGER.info("begin to sync OffsetManager. logModelId is " + modelId + ", pathId is " + pathId + ", hostname is "
                    + hostname);
        Set<String> newKeySet = new HashSet<>();
        for (String masterFile : masterFiles) {
            if (StringUtils.isNotBlank(masterFile)) {
                if (StringUtils.isNotBlank(hostname)) {
                    newKeySet.add(getLogMod4PathKeyForK8sCloud(hostname, modelId, pathId, masterFile));
                } else {
                    newKeySet.add(getLogMod4PathKey(modelId, pathId, masterFile));
                }
            }
        }

        Set<String> needToDel = new HashSet<>();
        synchronized (lock) {
            for (String offsetKey : offsetMap.keySet()) {
                boolean isMatch = StringUtils.isNotBlank(hostname) ? offsetKey.startsWith(hostname + "_" + modelId + "_"
                                                                                          + pathId
                                                                                          + "__") : offsetKey.startsWith(modelId + "_" + pathId + "__");

                if (isMatch) {
                    // 仅仅扫描该日志模型和pathId
                    if (!newKeySet.contains(offsetKey)) {
                        // 新的主文件集中，不包含该offset,可删除offset
                        needToDel.add(offsetKey);
                    }
                }
            }

            if (needToDel.size() != 0) {
                for (String del : needToDel) {
                    offsetMap.remove(del);
                    LOGGER.info("file key[ " + del + " ] is deleted!");
                }
            }

        }
        LOGGER.info("success to sync OffsetManager!");
    }

    public static void getOffsetInfo(Long modId, Long pathId, String masterFilePath, List<FileNode> nodes) {
        synchronized (lock) {
            Map<String, FileOffSet> fileOffSetMap = getLogModel4Path(modId, pathId, masterFilePath);

            // 对nodes按照modifyTime排序
            FileUtils.sortByMTime(nodes);

            // 获得最小对创建时间和更新时间
            long minModifyTime = nodes.get(0).getModifyTime();
            for (FileNode fileNode : nodes) {
                if (minModifyTime > fileNode.getModifyTime()) {
                    minModifyTime = fileNode.getModifyTime();
                }
            }

            boolean isNewTask = true;
            int smallIndex = nodes.size() - 1;
            boolean isOldOffset = false;

            // 处理没有inode的offset
            // 1. 剔除日志日志时间太早的offset
            // 2. 将offset和fileNode对应起来
            List<FileOffSet> fileOffsetList = new ArrayList<>();
            for (Map.Entry<String, FileOffSet> entry : fileOffSetMap.entrySet()) {
                FileOffSet offset = entry.getValue();
                // 日志时间如果 比最小的修改时间还早，则直接放弃这个文件
                if (offset.getTimeStamp() == null || offset.getTimeStamp() < minModifyTime) {
                    LOGGER.info("log offset is over due.ignore! offset is " + offset);
                    continue;
                }
                offset.setFileKey(entry.getKey());
                fileOffsetList.add(offset);
            }
            // 对offset做倒叙排列
            FileUtils.sortOffsetByMTimeDesc(fileOffsetList);

            Map<String, FileOffSet> tmp = new HashMap<>();
            for (FileOffSet fileOffSet : fileOffsetList) {
                String key = fileOffSet.getFileKey();
                if (!key.startsWith(NO_INODE)) {
                    // 只处理没有inode的情况
                    // 曾今采集过
                    isNewTask = false;
                    continue;
                }

                isOldOffset = true;

                // 旧版本offset若为30小时之前的offset，则忽略
                if (System.currentTimeMillis() - fileOffSet.getTimeStamp() > _30HOUR) {
                    LOGGER.warn("offset is before than 30 hours. ignore! offset is " + fileOffSet);
                    continue;
                }

                for (int i = 0; i < nodes.size(); i++) {
                    FileNode fileNode = nodes.get(i);
                    if (fileNode.getModifyTime() >= fileOffSet.getTimeStamp()) {
                        String fileKey = fileNode.getFileKey();

                        if (tmp.containsKey(fileKey)) {
                            continue;
                        }

                        LOGGER.info("offset is matched to fileNode.filNode is " + fileNode + ", offset is "
                                    + fileOffSet);
                        fileOffSet.setFileKey(fileKey);
                        if (i < smallIndex) {
                            smallIndex = i;
                        }

                        tmp.put(fileKey, fileOffSet);
                        break;
                    }
                }
            }

            fileOffSetMap.putAll(tmp);

            // 删除没有inode的offset
            Set<String> removes = new HashSet<>();
            for (String key : fileOffSetMap.keySet()) {
                if (key.startsWith(NO_INODE)) {
                    removes.add(key);
                }
            }
            for (String key : removes) {
                fileOffSetMap.remove(key);
            }

            // 获得offset中的最大modifyTime
            long maxOffsetTime = 0;
            for (String key : fileOffSetMap.keySet()) {
                if (fileOffSetMap.get(key).getLastModifyTime() != null) {
                    if (fileOffSetMap.get(key).getLastModifyTime() > maxOffsetTime) {
                        maxOffsetTime = fileOffSetMap.get(key).getLastModifyTime();
                    }
                }
            }

            // 给分配fileNode分配offset
            for (int i = 0; i < nodes.size(); i++) {
                FileNode node = nodes.get(i);
                String key = node.getFileKey();
                if (fileOffSetMap.containsKey(key)) {
                    // 将offset和fileNode联系起来
                    FileOffSet fileOffSet = fileOffSetMap.get(key);
                    node.setFileOffSet(fileOffSet);

                    if (fileOffSet.getLastModifyTime() < node.getModifyTime()
                        || fileOffSet.getOffSet() < node.getLength()) {
                        // 位点时间小于文件修改时间 或者 文件偏移量小于文件长度
                        LOGGER.info("file need to be collected.fileNode is " + node);
                        node.setNeedCollect(true);
                    } else {
                        node.setNeedCollect(false);
                    }
                } else {
                    if (maxOffsetTime != 0 && node.getModifyTime() > maxOffsetTime) {
                        // 如果fileNode没有offset 且 fileNode的modifyTime大于最大的offset的modifyTime, 则自动分配偏移为0的offset

                        FileOffSet fileOffSet = getFileOffset(modId, pathId, masterFilePath, node.getParentPath(),
                                                       node.getFileKey(), node.getFile());

                        fileOffSet.setOffSet(0L);
                        fileOffSet.setLastModifyTime(node.getModifyTime());
                        fileOffSet.setFileName(node.getFileName());

                        node.setFileOffSet(fileOffSet);
                        node.setNeedCollect(true);
                        fileOffSetMap.put(node.getFileKey(), fileOffSet);
                        LOGGER.info("file's lastModify time is less then max modifyTime of offset. set offset 0. fileNode is "
                                    + node);
                    } else {
                        // 如果fileNode没有offset 且 fileNode的modifyTime小于最大的offset的modifyTime
                        FileOffSet fileOffSet = getFileOffset(modId, pathId, masterFilePath, node.getParentPath(),
                                                       node.getFileKey(), node.getFile());
                        if (isOldOffset && i >= smallIndex) {
                            // 旧版本转为新版后的offset匹配
                            fileOffSet.setOffSet(0L);
                            fileOffSet.setLastModifyTime(node.getModifyTime());
                            fileOffSet.setFileName(node.getFileName());

                            node.setFileOffSet(fileOffSet);
                            node.setNeedCollect(true);
                            LOGGER.info("this file has no any old offset matched. but it should be collected.fileNode is "
                                        + node);
                        } else {
                            if (isNewTask) {
                                // 若第一次部署采集任务，则自动分配偏移为当前length的offset
                                fileOffSet.setOffSet(node.getLength());
                                fileOffSet.setLastModifyTime(node.getModifyTime());
                                fileOffSet.setFileName(node.getFileName());

                                node.setFileOffSet(fileOffSet);
                                node.setNeedCollect(false);
                                LOGGER.info("file is collected for first time. it can ignore. fileNode is " + node);
                            } else {
                                // 若之前部署过采集任务，则设置offset为0
                                fileOffSet.setOffSet(0L);
                                fileOffSet.setLastModifyTime(node.getModifyTime());
                                fileOffSet.setFileName(node.getFileName());
                                node.setFileOffSet(fileOffSet);
                                node.setNeedCollect(true);
                                LOGGER.info("file need to be collected maybe stop for some time. fileNode is " + node);
                            }
                        }
                        fileOffSetMap.put(node.getFileKey(), fileOffSet);
                    }
                }
            }

            // 删除没有匹配的位点信息
            Set<String> matchSet = new HashSet<>();
            for (FileNode node : nodes) {
                matchSet.add(node.getFileKey());
            }

            removes.clear();
            for (String key : fileOffSetMap.keySet()) {
                if (!matchSet.contains(key)) {
                    removes.add(key);
                }
            }
            for (String key : removes) {
                fileOffSetMap.remove(key);
            }
        }
    }

    // 返回一个日志模型+path的偏移信息
    public static Map<String, FileOffSet> getLogModel4Path(Long logID, Long pathID, String masterFilePath) {
        synchronized (lock) {
            String key = getLogMod4PathKey(logID, pathID, masterFilePath);

            if (!offsetMap.containsKey(key)) {
                offsetMap.put(key, new ConcurrentHashMap<String, FileOffSet>());
            }

            return offsetMap.get(key);
        }
    }

    // 删除日志模型+path的偏移信息
    public static void removeLogModel4Path(Long logID, Long pathID, String masterFilePath, String hostname) {
        synchronized (lock) {
            LOGGER.info("removeLogModel4Path. logId is " + logID + ", pathId is " + pathID + ", masterFilePath is "
                        + masterFilePath + ", hostname is " + hostname);
            String key = null;
            if (StringUtils.isNotBlank(hostname)) {
                key = getLogMod4PathKeyForK8sCloud(hostname, logID, pathID, masterFilePath);
            } else {
                key = getLogMod4PathKey(logID, pathID, masterFilePath);
            }
            offsetMap.remove(key);
        }
    }

    // 没有则创建一个
    public static FileOffSet getFileOffset(Long logID, Long pathID, String masterFilePath, String parentPath,
                                           String fileKey, File file) {
        synchronized (lock) {
            String logMode4PathKey = getLogMod4PathKey(logID, pathID, masterFilePath);

            if (!offsetMap.containsKey(logMode4PathKey)) {
                offsetMap.put(logMode4PathKey, new ConcurrentHashMap<String, FileOffSet>());
            }

            if (!offsetMap.get(logMode4PathKey).containsKey(fileKey)) {
                FileOffSet fileOffSet = new FileOffSet(logID, pathID, parentPath, fileKey);
                fileOffSet.setFileHeadMd5(FileUtils.getFileNodeHeadMd5(file));
                offsetMap.get(logMode4PathKey).put(fileKey, fileOffSet);
                LOGGER.info("offset is null. create a new offset. offset is " + fileOffSet + ", key is "
                            + logMode4PathKey);
            }

            // 存在fileKey需要判断Md5值
            if (!checkFileHeadMd5(logMode4PathKey, file, fileKey)) {
                LOGGER.info("getFileOffset:Inode reuse, need to set offset to 0! logModeId:" + logID + ",path:"
                            + file.getAbsolutePath() + ",parentPath:" + parentPath);
                offsetMap.get(logMode4PathKey).get(fileKey).setOffSet(0L);
            }

            return offsetMap.get(logMode4PathKey).get(fileKey);
        }
    }

    public static void removeFileOffset(Long logID, Long pathID, String masterFilePath, String fileKey) {
        synchronized (lock) {
            LOGGER.info("removeFileOffset. logId is " + logID + ", pathId is " + pathID + ", masterFilePath is "
                        + masterFilePath + ", fileKey is " + fileKey);
            String logMode4PathKey = getLogMod4PathKey(logID, pathID, masterFilePath);

            if (!offsetMap.containsKey(logMode4PathKey)) {
                return;
            }

            offsetMap.get(logMode4PathKey).remove(fileKey);
            if (offsetMap.get(logMode4PathKey).size() == 0) {
                offsetMap.remove(logMode4PathKey);
            }
        }
    }

    /* 从磁盘读取数据 */
    public static void loadOffset(Map<Long, List<LogPath>> map) {
        synchronized (lock) {
            File file = new File(config.getRootDir());
            if (file.exists()) {
                loadOffset(file, map);
                return;
            }

            File tmp = new File(config.getRootDir() + TMP_SUFFIX);
            if (tmp.exists()) {
                loadOffset(tmp, map);
                // 将临时目录改成正式目录
                tmp.renameTo(file);
            }
        }
    }

    /**
     * 校验配置是否存在
     */
    public static boolean checkOffsetExist(String hostname, LogPath logPath) {
        Long logModelId = logPath.getLogModelId();
        Long logPathId = logPath.getPathId();
        String key;
        if (StringUtils.isNotBlank(hostname)) {
            key = getLogMod4PathKeyForK8sCloud(hostname, logModelId, logPathId, logPath.getDockerPath());
        } else {
            key = getLogMod4PathKey(logModelId, logPathId, logPath.getPath());
        }
        return offsetMap.get(key) != null;
    }

    /**
     * copy sourceLogModeId表示的日志模型至当前日志模型
     * copy条件：logModeId一致，path一致
     * @param hostname
     * @param logPath
     * @param sourceLogModeId
     */
    public static void copyOffsetBetweenLogModeId(String hostname, LogPath logPath, Long sourceLogModeId) {
        LOGGER.info("begin to copyOffsetBetweenLogModeId. hostname is " + hostname + ", logPath is " + logPath
                    + ", sourceLogModeId is " + sourceLogModeId);
        // check
        if (checkOffsetExist(hostname, logPath)) {
            LOGGER.info("offset is already exist. ignore! hostname is " + hostname + ", logPath is " + logPath);
            return;
        }

        // get offset of sourceLogModeId
        Map<String, ConcurrentHashMap<String, FileOffSet>> offsetMapForLogModeId = getOffsetMapByLogModeId(sourceLogModeId);
        if (offsetMapForLogModeId == null || offsetMapForLogModeId.isEmpty()) {
            LOGGER.warn("there is no any offset related to sourceLogModelId, sourceLogModeId is " + sourceLogModeId);
            return;
        }

        // match and copy offset
        String targetPathKey;
        if (StringUtils.isNotBlank(hostname)) {
            targetPathKey = getLogMod4PathKeyForK8sCloud(hostname, logPath.getLogModelId(), logPath.getPathId(),
                                                        logPath.getDockerPath());
        } else {
            targetPathKey = getLogMod4PathKey(logPath.getLogModelId(), logPath.getPathId(), logPath.getPath());
        }
        for (Map.Entry<String, ConcurrentHashMap<String, FileOffSet>> entry : offsetMapForLogModeId.entrySet()) {
            String originalPath = getLogPathFromOffset(entry.getKey());
            String originalTargetPath = getLogPathFromOffset(targetPathKey);
            if (StringUtils.isBlank(originalPath) || StringUtils.isBlank(originalTargetPath)) {
                continue;
            }
            if (originalPath.equals(originalTargetPath)) {
                for (FileOffSet offSet : entry.getValue().values()) {
                    offSet.setLogID(logPath.getLogModelId());
                    offSet.setPathID(logPath.getPathId());
                }
                offsetMap.put(targetPathKey, entry.getValue());
            }
        }
        LOGGER.info("success to copyOffset between LogModeId. hostname is " + hostname + ", logPath is " + logPath
                    + ", sourceLogModeId is " + sourceLogModeId);

    }

    /**
     * 根据日志模型获取对应的offset map
     * @param sourceLogModeId
     * @return
     */
    private static Map<String, ConcurrentHashMap<String, FileOffSet>> getOffsetMapByLogModeId(Long sourceLogModeId) {
        Map<String, ConcurrentHashMap<String, FileOffSet>> result = new ConcurrentHashMap<>();
        for (Map.Entry<String, ConcurrentHashMap<String, FileOffSet>> entry : offsetMap.entrySet()) {
            String logMod4PathKey = entry.getKey();
            if (!logMod4PathKey.contains(sourceLogModeId + "")) {
                continue;
            }
            if (result.get(logMod4PathKey) == null) {
                result.put(logMod4PathKey, new ConcurrentHashMap<>());
            }
            for (FileOffSet fileOffset : entry.getValue().values()) {
                if (fileOffset.getLogID().equals(sourceLogModeId)) {
                    FileOffSet newOne = getOffsetClone(fileOffset);
                    result.get(logMod4PathKey).put(newOne.getFileKey(), newOne);
                }
            }
        }
        return result;
    }

    private static FileOffSet getOffsetClone(FileOffSet origin) {
        FileOffSet target = new FileOffSet(origin.getLogID(), origin.getPathID(), origin.getParentPath(),
                                           origin.getFileKey(), origin.getOffSet(), origin.getFileName());
        target.setTimeStamp(origin.getTimeStamp());
        target.setLastModifyTime(origin.getLastModifyTime());
        return target;
    }

    /**
     * 获取路径中的path信息
     * @param path
     * @return
     */
    private static String getLogPathFromOffset(String path) {
        if (!path.contains("__")) {
            return null;
        }
        return path.substring(path.indexOf("__") + 2);
    }

    /**
     * 获取路径中的logId
     * @param path
     * @return
     */
    private static Integer getLogModelId(String path) {
        String[] result = path.split("_");
        if (result == null || result.length == 0) {
            return -1;
        }
        return Integer.parseInt(result[0]);
    }

    private static void loadOffset(File root, Map<Long, List<LogPath>> logPathMap) {
        List<String> logMode4Paths = FileUtils.listChildren(root);

        // 标记是否同时存在老的和旧的offset,存在任何offset key中带有"_"，则说明是新的offset，老的offset可以忽略
        boolean isNew = false;
        for (String logMode4Path : logMode4Paths) {
            if (logMode4Path.contains("_")) {
                isNew = true;
                break;
            }
        }

        for (String logMode4Path : logMode4Paths) {

            String fileName = root.getAbsolutePath() + File.separator + logMode4Path;

            List<String> content = FileUtils.readFileContent(new File(fileName), -1);
            ConcurrentHashMap<String, FileOffSet> offSetMap = toOffset(content);
            if (isNew) {
                if (logMode4Path.contains("_")) {
                    // 新的offset
                    loadNew(logMode4Path, offSetMap);
                } else {
                    // 此时旧的offst直接忽略
                    continue;
                }
            } else {
                LOGGER.info("offset is old. it's offset should be transform from old one.offset is  " + offSetMap);
                // 旧的offset
                if (logPathMap == null) {
                    continue;
                }
                loadOld(logMode4Path, offSetMap, logPathMap);
            }
        }
    }

    /**
     * 加载旧的offset
     *
     * @param logMode4Path
     * @param offSetMap
     */
    private static void loadOld(String logMode4Path, Map<String, FileOffSet> offSetMap,
                                Map<Long, List<LogPath>> map) {
        // 此时logMode4Path是 logModelId
        for (Map.Entry<String, FileOffSet> entry : offSetMap.entrySet()) {
            FileOffSet offset = entry.getValue();
            List<LogPath> logPaths = map.get(offset.getLogID());
            if (logPaths == null) {
                continue;
            }

            Map<Long, String> pathMap = new HashMap<>();
            for (LogPath logPath : logPaths) {
                pathMap.put(logPath.getPathId(), logPath.getRealPath());
            }

            String tmpLogMode4Path = getLogMod4PathKey(offset.getLogID(), offset.getPathID(),
                                                       pathMap.get(offset.getPathID()));
            if (offsetMap.get(tmpLogMode4Path) == null) {
                offsetMap.put(tmpLogMode4Path, new ConcurrentHashMap<String, FileOffSet>());
            }
            offsetMap.get(tmpLogMode4Path).put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 加载新的offset
     *
     * @param logMode4Path
     * @param offSetMap
     */
    private static void loadNew(String logMode4Path, ConcurrentHashMap<String, FileOffSet> offSetMap) {
        // 新offset排除以NO_INODE开头的offset
        ConcurrentHashMap<String, FileOffSet> newMap = new ConcurrentHashMap<>();
        for (Map.Entry<String, FileOffSet> entry : offSetMap.entrySet()) {
            if (!entry.getKey().startsWith(NO_INODE)) {
                newMap.put(entry.getKey(), entry.getValue());
            }
        }
        offsetMap.put(logMode4Path, newMap);
    }

    private static ConcurrentHashMap<String, FileOffSet> toOffset(List<String> content) {
        ConcurrentHashMap<String, FileOffSet> ret = new ConcurrentHashMap<>();

        int index = 0;
        for (String c : content) {
            String[] splitedContents = StringUtils.split(c, SEPARATORCHAR);

            if (splitedContents.length != 2) {
                LogGather.recordErrorLog("OffsetManager-load-error", "offset info error, content:" + c);
                continue;
            }

            String[] splitedIds = StringUtils.split(splitedContents[0], "_");
            if (splitedIds.length < 3 || StringUtils.isBlank(splitedIds[0]) || StringUtils.isBlank(splitedIds[1])) {
                continue;
            }

            if (splitedIds[0].equals("null") || splitedIds[1].equals("null")) {
                continue;
            }
            Long logId = Long.valueOf(splitedIds[0]);
            Long pathId = Long.valueOf(splitedIds[1]);

            // 可能没有inode和createTime
            FileOffSet fileOffSet = null;
            try {
                // 存在解析失败的问题
                fileOffSet = new FileOffSet(JSON.parseObject(splitedContents[1]));
            } catch (Exception e) {
                LogGather.recordErrorLog("OffsetManager-load-error", "parse offset error, content:" + c);
                continue;
            }

            if (logId != null && pathId != null) {
                fileOffSet.setLogID(logId);
                fileOffSet.setPathID(pathId);
            }

            if (fileOffSet.getFileKey() == null) {
                if (fileOffSet.getLastModifyTime() == null && fileOffSet.getTimeStamp() != null) {
                    // 老版本有有没有文件修改时间情况，这里将日志时间作为替换
                    fileOffSet.setLastModifyTime(fileOffSet.getTimeStamp());
                }
                ret.put(NO_INODE + index, fileOffSet);
                index++;
            } else {
                ret.put(fileOffSet.getFileKey(), fileOffSet);
            }
        }
        return ret;
    }

    public static void flush() {
        synchronized (lock) {
            flush2Dir(config.getRootDir());
            if (!OLD_AGENT_PATH.equalsIgnoreCase(config.getRootDir())) {
                flush2Dir(OLD_AGENT_PATH);
            }
        }
    }

    /* 借助于rename的原子特性，是的目录的修改保证原子性 */
    private static void flush2Dir(String rootPath) {
        String tmpDirName = rootPath + TMP_SUFFIX;

        File tmpDir = new File(tmpDirName);
        FileUtils.delFile(tmpDir);
        tmpDir.mkdirs();

        boolean isSuccess = true;
        for (String logMode4PathKey : offsetMap.keySet()) {
            List<String> content = getContent(offsetMap.get(logMode4PathKey));

            String fileName = tmpDirName + File.separator + logMode4PathKey;
            try {
                FileUtils.writeFileContent(new File(fileName), content);
            } catch (FileNotFoundException e) {
                LOGGER.warn("file is can not be created. file is " + fileName);
                isSuccess = false;
            }
        }

        if (isSuccess) {
            File rootDir = new File(rootPath);
            // 首先复制到del目录下，并保证del下的内容全部被删除(删除操作不是原子操作)，防止中途宕机，之后启动的时候加载老的offset
            if (rootDir.exists()) {
                File delDir = new File(rootPath + DEL_SUFFIX);
                FileUtils.delFile(delDir);

                rootDir.renameTo(delDir);
                FileUtils.delFile(delDir);
            }

            tmpDir.renameTo(rootDir);
        }
    }

    private static List<String> getContent(Map<String, FileOffSet> m) {
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

    /**
     * 构建在弹性云宿主机采集的场景下的唯一key
     * @param hostname 主机名
     * @param logId    日志模型Id
     * @param pathId   路径Id
     * @param masterFilePath 主文件路径
     * @return
     */
    public static String getLogMod4PathKeyForK8sCloud(String hostname, Long logId, Long pathId,
                                                      String masterFilePath) {
        String key = hostname + "_" + getLogMod4PathKey(logId, pathId, masterFilePath);
        if (key.length() > FILE_MAX_LENGTH) {
            // 兼容offset文件过长的问题
            if (StringUtils.isNotBlank(masterFilePath)) {
                key = hostname + "_" + logId + "_" + pathId + "_" + CommonUtils.getMd5(masterFilePath, hostname);
            }
        }
        return key;
    }

    /**
     * 构建唯一主文件的唯一key
     *
     * @param logId 日志模型Id
     * @param pathId 路径Id
     * @param masterFilePath 主文件路径
     * @return offset唯一key
     */
    public static String getLogMod4PathKey(Long logId, Long pathId, String masterFilePath) {
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

    /**
     * 构建offset的前缀
     *
     * @param logId 日志模型Id
     * @param pathId pathId
     * @return offset前缀
     */
    public static String getOffsetPrefix(Integer logId, Integer pathId) {
        return logId + "_" + pathId + "_";
    }

    public static boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        init = init;
    }

    /**
     * 校验新产生的fileKey是否已经存在
     * Map<String, ConcurrentHashMap<String, FileOffSet>>
     */
    public static boolean checkFileKeyExist(String hostname, LogPath logPath, String fileKey) {
        LOGGER.info("begin check file exit!hostname:" + hostname + ",filePath:" + logPath.getRealPath() + ",fileKey:"
                    + fileKey);
        // 文件为空fileKey为null
        if (StringUtils.isBlank(fileKey)) {
            return false;
        }
        Long logModeId = logPath.getLogModelId();
        Long pathId = logPath.getPathId();
        String logModePathKey = StringUtils.isNotBlank(hostname) ? getLogMod4PathKeyForK8sCloud(hostname, logModeId,
                                                                                               pathId,
                                                                                               logPath.getDockerPath()) : getLogMod4PathKey(logModeId,
                                                                                                                                            pathId,
                                                                                                                                            logPath.getPath());
        // 判断fileKey是否存在
        ConcurrentHashMap<String, FileOffSet> fileKeyMap = offsetMap.get(logModePathKey);
        if (fileKeyMap != null && StringUtils.isNotBlank(fileKey)) {
            for (String key : fileKeyMap.keySet()) {
                if (fileKey.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 校验fileKey相同情况下新文件头部MD5是否和map中的MD5相同,进入这个方法他的fileKeyMap就不为空，三者都不为空，
     * @param
     * @param
     * @return
     */
    public static boolean checkFileHeadMd5(String logMode4PathKey, File file, String fileKey) {
        ConcurrentHashMap<String, FileOffSet> fileKeyMap = offsetMap.get(logMode4PathKey);
        String newMd5 = FileUtils.getFileNodeHeadMd5(file);
        String oldMd5 = fileKeyMap.get(fileKey).getFileHeadMd5();
        // 老版本的MD5为空，这时候不进行MD5的判断
        if (StringUtils.isBlank(oldMd5)) {
            LOGGER.info("This is the old version fileOffset, Md5 is empty! filePath:" + file.getAbsolutePath()
                        + "fileKey：" + fileKey);
            return true;
        }

        // 获取MD5失败的文件都过滤掉了
        if (newMd5.equals(oldMd5) && !newMd5.equals(LogConfigConstants.MD5_FAILED_TAG)) {
            return true;
        }
        LOGGER.info("FileKey is the same, MD5 is different, which means that Inode has been reused!filePath:"
                    + file.getAbsolutePath() + ",fileKey:" + fileKey + ",newMd5:" + newMd5 + ",oldMd5:" + oldMd5);
        return false;
    }

    /**
     * 判断是否存在offset信息
     * @param logID
     * @param pathID
     * @param masterFilePath
     * @return
     */
    public static boolean checkOffsetInfoExit(Long logID, Long pathID, String masterFilePath) {
        String key = getLogMod4PathKey(logID, pathID, masterFilePath);
        return offsetMap.containsKey(key);
    }

}
