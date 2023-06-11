package com.didichuxing.datachannel.agent.source.log;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agent.common.api.*;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agentmanager.common.metrics.TaskMetrics;
import org.apache.commons.lang3.StringUtils;

import com.didichuxing.datachannel.agent.common.beans.LogPath;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.bean.Event;
import com.didichuxing.datachannel.agent.engine.bean.LogEvent;
import com.didichuxing.datachannel.agent.engine.source.AbstractSource;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.engine.utils.TimeUtils;
import com.didichuxing.datachannel.agent.source.log.beans.FileNode;
import com.didichuxing.datachannel.agent.source.log.beans.WorkingFileNode;
import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.agent.source.log.metrics.FileStatistic;
import com.didichuxing.datachannel.agent.source.log.monitor.RealTimeFileMonitor;
import com.didichuxing.datachannel.agent.source.log.monitor.ScheduleFileMonitor;
import com.didichuxing.datachannel.agent.source.log.offset.FileOffSet;
import com.didichuxing.datachannel.agent.source.log.offset.OffsetManager;
import com.didichuxing.datachannel.agent.source.log.utils.EventParser;
import com.didichuxing.datachannel.agent.source.log.utils.FileReader;
import com.didichuxing.datachannel.agent.source.log.utils.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: log source
 * @author: huangjw
 * @Date: 19/7/2 14:21
 */
public class LogSource extends AbstractSource {

    private static final Logger      LOGGER                     = LoggerFactory
                                                                    .getLogger(LogSource.class
                                                                        .getName());
    private final Object             lock                       = new Object();
    private int                      num;

    private LogPath                  logPath;

    Map<String, FileNode>            relatedFileNodeMap;
    List<WorkingFileNode>            collectingFileNodeList;
    Map<String, WorkingFileNode>     collectingFileNodeMap;

    private String                   parentPath;
    private String                   dockerParentPath;
    private String                   masterFileName;
    private String                   parentPathDirKey;

    private LogSourceConfig          logSourceConfig;
    private ModelConfig              modelConfig;

    private volatile boolean         isStopping                 = false;

    private volatile WorkingFileNode curWFileNode;

    private static final int         OPEN_RETRY_TIMES           = 3;

    FileReader                       fileReader;
    EventParser                      eventParser;

    /**
     * 一个心跳周期内的最大延迟量
     */
    private volatile long            maxLogTime                 = 0L;

    /**
     * 是否匹配标准日志
     */
    private volatile boolean         isMatchStandard            = true;

    /**
     * 3分钟
     */
    private static final Long        THREE_MINS                 = 3 * 60 * 1000L;

    private Long                     latestLogTimeUsedOnMetrics = 0l;

    public LogSource(ModelConfig config, LogPath logPath){
        super(config.getSourceConfig());
        this.logPath = logPath;
        this.logSourceConfig = (LogSourceConfig) this.sourceConfig;
        this.modelConfig = config;
        String masterFile = this.logPath.getRealPath();
        this.masterFileName = FileUtils.getMasterFile(masterFile);
        this.parentPath = FileUtils.getPathDir(this.logPath.getPath());
        if (StringUtils.isNotBlank(parentPath)) {
            this.parentPathDirKey = FileUtils.getFileKeyByAttrs(this.parentPath);
        }
        this.dockerParentPath = FileUtils.getPathDir(this.logPath.getDockerPath());

        this.relatedFileNodeMap = new ConcurrentHashMap<>();
        this.collectingFileNodeMap = new ConcurrentHashMap<>();
        this.collectingFileNodeList = new CopyOnWriteArrayList();
        bulidUniqueKey();
    }

    @Override
    public boolean init(ComponentConfig config) {
        LOGGER.info("begin to init logSource. config is " + this.sourceConfig);
        try {
            configure(config);

            prepare();

            // 采集文件初始化
            synchronized (lock) {
                try {
                    List<FileNode> fileNodes = getFileNodes();
                    if (modelConfig.getCommonConfig().getModelType() == LogConfigConstants.COLLECT_TYPE_PERIODICITY) {
                        addToPeriodicityCollect(fileNodes);
                    } else if (modelConfig.getCommonConfig().getModelType() == LogConfigConstants.COLLECT_TYPE_TEMPORALITY) {
                        addToTemporalityCollect(fileNodes);
                    }
                } catch (Exception e) {
                    LogGather.recordErrorLog("logSource error", "init logSource error!", e);
                }
            }

            if (logSourceConfig.getMatchConfig().getFileType() == FileType.File.getStatus()) {
                // 同步offset
                Set<String> masterFiles = new HashSet<>();
                masterFiles.add(logPath.getRealPath());
                OffsetManager.sync(null, logPath.getLogModelId(), logPath.getPathId(), masterFiles);
            }
            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("logSource error", "init logSource error!", e);
            return false;
        }
    }

    /**
     * 1. 启动前准备，一般用于在文件弹性云从容器内迁移至宿主机时使用
     * 2. 日志模型之间的offset拷贝
     */
    private void prepare() {

        // 兼容初始化时dockerPath赋值失败的case
        boolean needToCopyOffsetBetweenLogModeId = false;
        if (this.modelConfig.getSourceLogModeId() != 0) {
            needToCopyOffsetBetweenLogModeId = true;
        }

        if (needToCopyOffsetBetweenLogModeId) {
            OffsetManager.copyOffsetBetweenLogModeId(
                StringUtils.isNotBlank(modelConfig.getHostname()) ? modelConfig.getHostname()
                                                                    + CommonUtils
                                                                        .getHOSTNAMESUFFIX() : "",
                logPath, this.modelConfig.getSourceLogModeId());
        }
    }

    private Map<String, FileNode> getFileNodesToCopy() {
        Long modelId = logPath.getLogModelId();
        Long logPathId = logPath.getPathId();
        List<File> relatedFiles = FileUtils.getRelatedFiles(logPath.getRealPath(),
                                                            this.logSourceConfig.getMatchConfig());
        Map<String, FileNode> fileNodeMap = new HashMap<>();
        if (relatedFiles == null || relatedFiles.size() == 0) {
            LOGGER.warn("there is no any file which need to be collected.");
            return null;
        }

        for (File file : relatedFiles) {
            // 过滤历史数据
            if (System.currentTimeMillis() - file.lastModified() > logSourceConfig.getMaxModifyTime()) {
                continue;
            }
            String fileKey = FileUtils.getFileKeyByAttrs(file);
            fileNodeMap.put(fileKey, new FileNode(modelId, logPathId, fileKey, file.lastModified(), file.getParent(),
                                                  file.getName(), file.length(), file));
        }
        return fileNodeMap;
    }

    @Override
    public void configure(ComponentConfig config) {
        fileReader = new FileReader(this);
        eventParser = new EventParser();
    }

    public void addToPeriodicityCollect(List<FileNode> fileNodes) {
        if (fileNodes != null && fileNodes.size() != 0) {
            for (FileNode fileNode : fileNodes) {
                relatedFileNodeMap.put(fileNode.getNodeKey(), fileNode);
            }

            for (FileNode fileNode : fileNodes) {
                if (fileNode.getNeedCollect()) {
                    if (checkStandardLogType(fileNode)) {
                        appendFile(fileNode, false);
                    }
                }
            }

            // 若为标准日志，同时所有的标准日志均被过滤掉，则将心跳与实时监控注销掉
            if (collectingFileNodeList.size() == 0) {
                LOGGER.warn("there is no any vaild file to collect. filtered. logPath is "
                            + logPath);
                isMatchStandard = false;
                ScheduleFileMonitor.INSTANCE.unregister(this);
            }
        }
    }

    public void addToTemporalityCollect(List<FileNode> fileNodes) {
        if (modelConfig.getCommonConfig().getStartTime() != null
            && modelConfig.getCommonConfig().getEndTime() != null) {
            if (fileNodes != null && fileNodes.size() != 0) {

                // 对nodes按照modifyTime排序, 显示标注
                FileUtils.sortByMTime(fileNodes);

                // 采集文件：第一个lastModifyTime>startTime 直到 第一个lastModifyTime > endTime
                for (FileNode fileNode : fileNodes) {
                    if (fileNode.getModifyTime() < modelConfig.getCommonConfig().getStartTime()
                        .getTime()) {
                        continue;
                    }

                    fileNode.setOffset(0L);
                    if (checkStandardLogType(fileNode)) {
                        appendFile(fileNode);
                    }

                    if (fileNode.getModifyTime() > modelConfig.getCommonConfig().getEndTime()
                        .getTime()) {
                        break;
                    }
                }
            }
        } else {
            LogGather.recordErrorLog("LogSource error",
                "params is not right.type is " + LogConfigConstants.COLLECT_TYPE_TEMPORALITY
                        + ",startTime is " + modelConfig.getCommonConfig().getStartTime()
                        + ", endTime is " + modelConfig.getCommonConfig().getEndTime());
        }
    }

    /**
     * 校验是否match对应的type
     *
     * @param fileNode need to check
     * @return result if standard files
     */
    public boolean checkStandardLogType(FileNode fileNode) {
        if (!FileUtils.checkStandard(new File(fileNode.getAbsolutePath()),
            logSourceConfig.getMatchConfig())) {
            LOGGER.warn("fileNode is not match the standard.ignore!" + " file is "
                        + fileNode.getAbsolutePath() + ",required StandardLogType is "
                        + logSourceConfig.getMatchConfig());
            return false;
        }
        return true;
    }

    /**
     * 添加文件入采集，并更新curFileNode
     * @param fileNode
     */
    public void appendFile(FileNode fileNode) {
        if (appendFile(fileNode, false)) {
            refreshCurWFN();
            LOGGER.info("success to refresh current working fileNode. fileNode is "
                        + (curWFileNode == null ? null : curWFileNode.getFileNode()));
        }
    }

    public boolean appendFile(FileNode fileNode, boolean toSort) {
        LOGGER.info("appendFile fileNode to collect. fileNode is " + fileNode);
        if (isStopping) {
            LOGGER.warn("logSource is stopping. ignore this fileNode!");
            return false;
        }

        if (!isMatchStandard) {
            RealTimeFileMonitor.INSTANCE.register(this);
            isMatchStandard = true;
        }

        // 校验是否可以添加
        if (!checkToAdd(fileNode)) {
            return false;
        }

        synchronized (lock) {
            if (fileNode.getFileOffSet() == null) {
                FileOffSet fileOffSet = null;
                fileOffSet = OffsetManager.getFileOffset(logPath.getLogModelId(),
                    logPath.getPathId(), logPath.getRealPath(), fileNode.getParentPath(),
                    fileNode.getFileKey(), fileNode.getFile());
                fileOffSet.setFileName(fileNode.getFileName());
                fileOffSet.setLastModifyTime(fileNode.getModifyTime());
                fileNode.setFileOffSet(fileOffSet);
            }

            // 兼容新老版本切换的时候老的offset信息没有MD5值情况,可能是Inode复用也可能是文件突然清空，都重头开始采集
            if (StringUtils.isBlank(fileNode.getFileOffSet().getFileHeadMd5())) {
                fileNode.getFileOffSet().setFileHeadMd5(
                    FileUtils.getFileNodeHeadMd5(fileNode.getFile()));
            }

            if (fileNode.getOffset() > fileNode.getLength()) {
                LOGGER.info("appendFile:the file length is less than offset！offset:"
                            + fileNode.getOffset() + ",fileNode is " + fileNode.toString());
                fileNode.setOffset(0L);
                fileNode.setOffsetTimeStamp(0L);
            }

            if (!collectingFileNodeMap.containsKey(fileNode.getNodeKey())) {
                relatedFileNodeMap.put(fileNode.getNodeKey(), fileNode);

                WorkingFileNode wfn = new WorkingFileNode(fileNode, this);
                collectingFileNodeMap.put(wfn.getUniqueKey(), wfn);
                collectingFileNodeList.add(wfn);
                if (toSort) {
                    FileUtils.sortWFNByMTime(collectingFileNodeList);
                }

                // 打开全部文件 且 非补采
                if (!wfn.isFileOpen()
                    && modelConfig.getCommonConfig().getModelType() != LogConfigConstants.COLLECT_TYPE_TEMPORALITY) {
                    wfn.open(logSourceConfig.getCollectLocation(), OPEN_RETRY_TIMES);
                }

                LOGGER.info("add fileNode success. collectingFileNodeMap size is "
                            + collectingFileNodeMap.size() + ", relatedFileNodeMap size is "
                            + relatedFileNodeMap.size());
                return true;
            } else {
                // MD5不同认为是新文件,需要更新map并更新当前的CurWFN
                String curFileNodeMd5 = FileUtils.getFileNodeHeadMd5(fileNode.getFile());
                String oldFileNodeMd5 = collectingFileNodeMap.get(fileNode.getNodeKey())
                    .getFileNode().getFileOffSet().getFileHeadMd5();
                // 当MD5不同时候（前面过滤保证一定能获取到MD5值），老版本fileOffset没有MD5值不校验
                if (fileNode.getFile().exists() && StringUtils.isNotBlank(oldFileNodeMd5)
                    && !curFileNodeMd5.equals(oldFileNodeMd5)) {
                    LOGGER
                        .warn("appendFile:Inode reuse, need to set offset to 0 and flush map! logPath is "
                              + this.logPath + ",fileName is " + fileNode.getFileName());
                    // 停止老的文件
                    collectingFileNodeMap.get(fileNode.getNodeKey()).close();

                    // 同步relatedFileNodeMap
                    relatedFileNodeMap.remove(fileNode.getNodeKey());
                    relatedFileNodeMap.put(fileNode.getNodeKey(), fileNode);

                    // 同步collectingFileNodeMap
                    collectingFileNodeMap.remove(fileNode.getNodeKey());
                    collectingFileNodeMap.put(fileNode.getNodeKey(), new WorkingFileNode(fileNode,
                        this));

                    // 同步collectingFileNodeList
                    Iterator<WorkingFileNode> iterator = collectingFileNodeList.iterator();
                    while (iterator.hasNext()) {
                        WorkingFileNode wfn = iterator.next();
                        if (wfn.getUniqueKey().equals(fileNode.getNodeKey())) {
                            collectingFileNodeList.remove(wfn);
                            collectingFileNodeList.add(new WorkingFileNode(fileNode, this));
                        }
                    }

                    // 这时候文件处于关闭状态，从0打开，保证从头开始采集
                    WorkingFileNode newWfn = collectingFileNodeMap.get(fileNode.getNodeKey());
                    if (!newWfn.isFileOpen()
                        && modelConfig.getCommonConfig().getModelType() != LogConfigConstants.COLLECT_TYPE_TEMPORALITY) {
                        newWfn.open(0, OPEN_RETRY_TIMES);
                    }
                    return true;
                }
                LOGGER.warn("file is already in collecting. ignore! logPath is " + this.logPath
                            + ",fileName is " + fileNode.getFileName());
                return false;
            }
        }
    }

    /**
     * 校验是否添加
     * @param fileNode
     */
    private boolean checkToAdd(FileNode fileNode) {
        if (getModelConfig().getCommonConfig().getModelType() != LogConfigConstants.COLLECT_TYPE_TEMPORALITY) {
            if (collectingFileNodeMap.size() >= this.logSourceConfig.getMaxThreadNum()) {
                WorkingFileNode needToDelete = null;
                long earliestModifyTime = 0;
                int i = 0;
                for (WorkingFileNode wfn : collectingFileNodeMap.values()) {
                    if (i == 0) {
                        earliestModifyTime = wfn.getFileNode().getModifyTime();
                        needToDelete = wfn;
                    } else {
                        if (wfn.getFileNode().getModifyTime() < earliestModifyTime) {
                            earliestModifyTime = wfn.getFileNode().getModifyTime();
                            needToDelete = wfn;
                        }
                    }
                    i++;
                }
                if (fileNode.getModifyTime() < earliestModifyTime) {
                    LOGGER.info("file is not in lastest " + this.logSourceConfig.getMaxThreadNum()
                                + " files. ignore. file is " + fileNode);
                    return false;
                }

                if (needToDelete != null) {
                    needToDelete.close();
                    release(needToDelete);
                } else {
                    LogGather.recordErrorLog(
                        "LogSource error",
                        "collectingFileNodeMap'size is too large ["
                                + (collectingFileNodeMap.size() + 1)
                                + "] which means collection delay. logId is "
                                + logPath.getLogModelId() + ", pathId is " + logPath.getPathId());
                }
            }
        }
        return true;
    }

    /**
     * 获取有效的采集文件
     *
     * @return vaild files to collect
     */
    public List<FileNode> getFileNodes() {
        LOGGER.info("begin to get fileNodes. logPath is " + logPath);
        Long modelId = logPath.getLogModelId();
        Long logPathId = logPath.getPathId();

        List<File> relatedFiles = FileUtils.getRelatedFiles(logPath.getRealPath(),
                                                            this.logSourceConfig.getMatchConfig());

        List<FileNode> fileNodeList = new ArrayList<>();
        if (relatedFiles == null || relatedFiles.size() == 0) {
            LOGGER.warn("there is no any file which need to be collected.");
            return null;
        }

        for (File file : relatedFiles) {
            // 过滤历史数据
            if (System.currentTimeMillis() - file.lastModified() > logSourceConfig.getMaxModifyTime()) {
                continue;
            }
            fileNodeList.add(new FileNode(modelId, logPathId, FileUtils.getFileKeyByAttrs(file), file.lastModified(),
                                          file.getParent(), file.getName(), file.length(), file));
        }

        // 没有文件，则返回，后续扫描和触发机制回收集新的文件
        if (fileNodeList.size() == 0) {
            LOGGER.warn("there is no any fileNode which need to be collected.");
            return fileNodeList;
        }

        // 判断offsetMap中是否存在offset信息、宿主机采集需要hostname,offset信息存在代表不是新任务
        boolean isNewTask = !OffsetManager.checkOffsetInfoExit(modelId, logPathId, logPath.getRealPath());

        // 按照最新修改时间倒叙排列
        OffsetManager.getOffsetInfo(modelId, logPathId, logPath.getRealPath(), fileNodeList);

        // 按照最新修改时间倒叙排列
        FileUtils.sortByMTimeDesc(fileNodeList);

        if (modelConfig.getCommonConfig().getModelType() == LogConfigConstants.COLLECT_TYPE_TEMPORALITY) {
            // 临时采集
            for (FileNode fileNode : fileNodeList) {
                fileNode.setOffset(0L);
                fileNode.getFileOffSet().setLastModifyTime(0L);
                fileNode.setNeedCollect(true);
            }
        } else {
            // 正常采集
            boolean haveNeedCollectFile = false;
            int count = 0;
            for (FileNode fileNode : fileNodeList) {
                if (!fileNode.getNeedCollect()) {
                    continue;
                }

                haveNeedCollectFile = true;
                // count++;
                // if (count > logSourceConfig.getValidLatestFiles()) {
                // // 从而能够在扫描的过程中重新加入收集的队列
                // fileNode.getFileOffSet().setLastModifyTime(0L);
                // fileNode.setNeedCollect(false);
                // }
            }

            // 处理没有offset的情况，则只拉取最新的那个
            if (!haveNeedCollectFile) {
                LOGGER.info("this is a new task. collect latest file! logPath is " + logPath);
                FileNode node = fileNodeList.get(0);
                node.setNeedCollect(true);
                // 判断采集是在宿主机上进行还是容器中进行
                if (isNewTask) {
                    if (modelConfig.getCollectType() == CollectType.COLLECT_IN_NORMAL_SERVER.getStatus()) {
                        if (logSourceConfig.getCollectLocation() == CollectLocation.Earliest.getLocation()) {
                            node.getFileOffSet().setOffSet(0L);
                        } else {
                            // 若文件大小为1G,
                            // 且文件的最新更新时间为30小时以内
                            // 则设置为从0开始采集，保护数据;
                            File file = new File(node.getAbsolutePath());
                            if (file.length() < 1024 * 1024 * 1024
                                    && System.currentTimeMillis() - file.lastModified() < 3 * 60 * 60 * 1000L) {
                                LOGGER.info("file is less than 1G. set fileNode's offset to 0. fileNode is " + node);
                                node.getFileOffSet().setOffSet(0L);
                            } else {
                                node.getFileOffSet().setOffSet(file.length());
                            }
                        }
                    } else {
                        // 在宿主机上采集，存在容器漂移后跨整点，导致上个时间点文件无法采集问题
                        // 因此在宿主机上采集的新任务默认往前多采一个文件，并且当前文件从offset为0的位置开始采集
                        node.setOffset(0L);
                        if (fileNodeList.size() >= 2) {
                            LOGGER.info("This is a new task that is collected on the ddcloud and requires one more file to be collected forward！fileNode is"
                                    + node);
                            FileNode preNode = fileNodeList.get(1);
                            preNode.setNeedCollect(true);
                            preNode.getFileOffSet().setOffSet(0L);
                        }
                    }
                }
            }
        }

        return fileNodeList;
    }

    /**
     * 校验文件
     *
     * @param fns files which need to be checked
     */
    public void checkFile(Map<String/* smallKey */, FileNode> fns) {
        Set<String/* bigKey */> fnsDeletes = new HashSet<>();
        List<FileNode> needToAddFns = new ArrayList<>();
        String pathDir = FileUtils.getPathDir(logPath.getRealPath());
        Long modelId = logPath.getLogModelId();
        Long logPathId = logPath.getPathId();
        Set<String> vaildFileNodeSet = new HashSet<>();
        boolean collectingFileChanged = false;
        for (FileNode fileNode : fns.values()) {
            // 删除多余的文件
            if (System.currentTimeMillis() - fileNode.getModifyTime() > logSourceConfig.getMaxModifyTime()) {
                continue;
            }

            boolean macthResult = FileUtils.match(new File(fileNode.getAbsolutePath()), logPath.getRealPath(),
                                                  logSourceConfig.getMatchConfig());
            if (!macthResult) {
                continue;
            }

            // 判断能否获取到MD5值，不能话就跳过不加入采集列表（要么文件有一行、要么文件满足1K）
            if (FileUtils.getFileNodeHeadMd5(fileNode.getFile()).equals(LogConfigConstants.MD5_FAILED_TAG)) {
                continue;
            }

            fileNode.setModelId(modelId);
            fileNode.setPathId(logPathId);

            vaildFileNodeSet.add(fileNode.getNodeKey());

            // 文件新增 或者 历史文件发生滚动
            if (relatedFileNodeMap.get(fileNode.getNodeKey()) == null
                || ((relatedFileNodeMap.get(fileNode.getNodeKey()) != null
                     && collectingFileNodeMap.get(fileNode.getNodeKey()) == null
                     && (relatedFileNodeMap.get(fileNode.getNodeKey()).getModifyTime() < fileNode.getModifyTime()
                         || relatedFileNodeMap.get(fileNode.getNodeKey()).getOffset() < fileNode.getLength())))) {
                FileNode newFileNode = fileNode.clone();
                newFileNode.setModelId(modelId);
                newFileNode.setPathId(logPathId);

                FileOffSet fileOffSet;
                fileOffSet = OffsetManager.getFileOffset(logPath.getLogModelId(), logPath.getPathId(),
                                                             logPath.getRealPath(), fileNode.getParentPath(),
                                                             fileNode.getFileKey(), fileNode.getFile());

                fileOffSet.setFileName(fileNode.getFileName());
                fileOffSet.setLastModifyTime(fileNode.getModifyTime());
                newFileNode.setFileOffSet(fileOffSet);

                needToAddFns.add(newFileNode);
                continue;
            }

            String fileName = fileNode.getFileName();
            Long modifyTime = fileNode.getModifyTime();

            // collectingFileNodeMap中的文件，正在采集的文件发生数据变更
            WorkingFileNode wfn = collectingFileNodeMap.get(fileNode.getNodeKey());
            if (curWFileNode != null && wfn != null && curWFileNode.getUniqueKey().equals(wfn.getUniqueKey())) {
                curWFileNode.getFileNode().setFileName(fileName);
                curWFileNode.getFileNode().setModifyTime(modifyTime);
            }

            // collectingFileNodeMap中的文件，正在采集的文件发生数据变更
            if (wfn != null && wfn.isFileEnd() && wfn.getFileNode().getOffset() < fileNode.getLength()) {
                wfn.setFileEnd(false);
                if (curWFileNode != null && !curWFileNode.getUniqueKey().equals(wfn.getUniqueKey())) {
                    // 非当前文件时，需要切换curWFileNode
                    collectingFileChanged = true;
                }
            }

            // 兼容业务同时写两个文件的case
            if (wfn != null && wfn.getFileNode().getOffset() < fileNode.getLength()) {
                wfn.setFileEnd(false);
                if (curWFileNode != null && !curWFileNode.getUniqueKey().equals(wfn.getUniqueKey())
                    && modifyTime - THREE_MINS < curWFileNode.getModifyTime()) {
                    // 非当前文件时，需要切换curWFileNode
                    collectingFileChanged = true;
                }
            }

            if (relatedFileNodeMap.containsKey(fileNode.getNodeKey())) {
                // 同步文件名和更新时间
                relatedFileNodeMap.get(fileNode.getNodeKey()).setFileName(fileName);
                relatedFileNodeMap.get(fileNode.getNodeKey()).setModifyTime(modifyTime);
            }

            if (collectingFileNodeMap.containsKey(fileNode.getNodeKey())) {
                // 同步文件名和更新时间
                collectingFileNodeMap.get(fileNode.getNodeKey()).getFileNode().setFileName(fileName);
                collectingFileNodeMap.get(fileNode.getNodeKey()).getFileNode().setModifyTime(modifyTime);
            }
        }

        for (String key : relatedFileNodeMap.keySet()) {
            if (!vaildFileNodeSet.contains(key)) {
                // 说明文件被删除，此时需要删除unCollectFiles中的filenode
                fnsDeletes.add(key);
            }
        }

        if (fnsDeletes.size() != 0) {
            for (String key : fnsDeletes) {
                relatedFileNodeMap.remove(key);
                if (collectingFileNodeMap.get(key) != null) {
                    release(collectingFileNodeMap.get(key));
                }

                // 需要删除offset
                // key为nodekey: logModId + UNDERLINE_SEPARATOR + pathId+
                // UNDERLINE_SEPARATOR + fileKey
                String fileKey = FileUtils.getFileKeyFromNodeKey(key);
                OffsetManager.removeFileOffset(modelId, logPathId, logPath.getRealPath(), fileKey);
            }
        }

        if (needToAddFns.size() != 0) {
            // 防止目录被删除后重新创建
            String dir = FileUtils.getPathDir(logPath.getRealPath());
            String newKey = FileUtils.getFileKeyByAttrs(dir);
            if (parentPathDirKey != null && !this.parentPathDirKey.equals(newKey)) {
                RealTimeFileMonitor.INSTANCE.replaceWatchKey(dir);
                parentPathDirKey = newKey;
            }
        }

        for (FileNode fileNode : needToAddFns) {
            if (checkStandardLogType(fileNode)) {
                appendFile(fileNode);
            }
        }

        if (collectingFileChanged) {
            refreshCurWFN();
            LOGGER.info("success to refresh current working fileNode. fileNode is "
                        + (curWFileNode == null ? null : curWFileNode.getFileNode()));
        }
    }

    @Override
    public void bulidUniqueKey() {
        setUniqueKey(logPath.getPathId() + "_" + logPath.getPath());
    }

    @Override
    public boolean onChange(ComponentConfig newOne) {
        LOGGER.info("begin to change logSouce config. logPath is " + this.logPath + ", newOne is "
                    + newOne);
        try {
            this.modelConfig = (ModelConfig) newOne;
            this.sourceConfig = this.modelConfig.getSourceConfig();
            this.logSourceConfig = (LogSourceConfig) this.sourceConfig;
            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("logSource error", "onChange error! new config is " + newOne,
                e);
        }
        return false;
    }

    @Override
    public boolean start() {
        isStopping = false;
        return true;
    }

    @Override
    public Event tryGetEvent() {
        if (collectingFileNodeList.size() == 0) {
            return null;
        }

        if (curWFileNode == null) {
            refreshCurWFN();
            if (curWFileNode == null) {
                return null;
            }
        }

        LogEvent event = null;
        try {
            synchronized (lock) {
                // release时，将会重置为null
                if (curWFileNode == null) {
                    return null;
                }
                if (!curWFileNode.isFileOpen()) {
                    curWFileNode.open(logSourceConfig.getCollectLocation(), OPEN_RETRY_TIMES);
                    if (!curWFileNode.isFileOpen()) {
                        curWFileNode = null;
                        return null;
                    }
                }

                // 读取
                event = fileReader.readEvent(curWFileNode);
                if (event != null) {
                    curWFileNode.setCurOffset(event.getOffset());
                }
                // 解析
                eventParser.parse(this, curWFileNode, event);
                curWFileNode.whetherToFileEnd(event);
                if (curWFileNode.isFileEnd()) {
                    // 释放当前文件
                    curWFileNode = null;
                }
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("LogSource error",
                "check file is collected to end error. file is " + curWFileNode.getFileNode(), e);
        }

        return event;
    }

    public void closeFiles() {
        List<WorkingFileNode> needToRelease = new ArrayList<>();
        if (getModelConfig().getCommonConfig().getModelType() == LogConfigConstants.COLLECT_TYPE_TEMPORALITY) {
            for (WorkingFileNode wfn : collectingFileNodeList) {
                if (wfn.isFileEnd()) {
                    wfn.syncFile();
                }

                // 补采时wfn处于未打开状态
                if (!wfn.isFileOpen()) {
                    continue;
                }

                if (wfn.checkFileCollectEnd()) {
                    // 表示文件被采集到末尾，并且被关闭，此时可释放
                    // 且文件偏移量到了文件的结尾
                    needToRelease.add(wfn);
                }
            }
        } else {
            if (collectingFileNodeMap.size() <= logSourceConfig.getValidLatestFiles()) {
                return;
            }

            // 递增排序
            FileUtils.sortWFNByMTime(collectingFileNodeList);
            int i = 0;
            for (WorkingFileNode wfn : collectingFileNodeList) {
                if (wfn.isFileEnd()) {
                    wfn.syncFile();
                }
                if (i < collectingFileNodeList.size() - logSourceConfig.getValidLatestFiles()) {
                    if (wfn.isFileEnd() && wfn.checkFileCollectEnd()) {
                        // 表示文件被采集到末尾，并且被关闭，此时可释放
                        // 且文件偏移量到了文件的结尾
                        needToRelease.add(wfn);
                    }
                }
                i++;
            }
        }

        for (WorkingFileNode wfn : needToRelease) {
            release(wfn);
        }
    }

    private void release(WorkingFileNode wfn) {
        LOGGER.info("begin to release the working fileNode. fileNode is " + wfn);
        synchronized (lock) {
            wfn.close();
            String uniqueKey = wfn.getUniqueKey();
            collectingFileNodeMap.remove(uniqueKey);
            List<WorkingFileNode> newOne = new CopyOnWriteArrayList();
            for (WorkingFileNode w : collectingFileNodeList) {
                if (!w.getUniqueKey().equals(wfn.getUniqueKey())) {
                    newOne.add(w);
                }
            }
            collectingFileNodeList.clear();
            collectingFileNodeList = newOne;
            // 触发refresh
            if (curWFileNode != null && curWFileNode.getUniqueKey().equals(wfn.getUniqueKey())) {
                curWFileNode = null;
            }
        }
        LOGGER.info("release fileNode success");
    }

    /**
     * 更新当前工作woke fileNode
     */
    private void refreshCurWFN() {
        synchronized (lock) {
            if (!collectingFileNodeList.isEmpty()) {
                FileUtils.sortWFNByMTime(this.collectingFileNodeList);
                WorkingFileNode result = null;
                List<WorkingFileNode> readyToCollectList = new ArrayList<>();
                for (WorkingFileNode wfn : this.collectingFileNodeList) {
                    if (!wfn.isFileEnd()) {
                        readyToCollectList.add(wfn);
                    }
                }

                try {
                    if (!readyToCollectList.isEmpty()) {
                        if (modelConfig.getCommonConfig().getModelType() == LogConfigConstants.COLLECT_TYPE_PERIODICITY) {
                            long lag = 0L;
                            long modifyTime = 0L;
                            for (int i = 0; i < readyToCollectList.size(); i++) {
                                if (i + 1 > logSourceConfig.getValidLatestFiles()) {
                                    break;
                                }
                                WorkingFileNode wfn = readyToCollectList.get(i);
                                if (i == 0) {
                                    modifyTime = wfn.getModifyTime();
                                    result = wfn;
                                    lag = wfn.getFileNode().getFile().length() - wfn.getFileNode().getOffset();
                                } else {
                                    // 兼容业务多个日志文件同时更新的case
                                    if (modifyTime != 0L && wfn.getModifyTime() - modifyTime < 5 * 1000L) {
                                        long minute = TimeUtils.getSpecificMinute(wfn.getModifyTime());
                                        // 第i个（非第0个）文件的更新时间不可以是整点0分或者59分，用于排除正常的日志切割
                                        if (minute > 1 && minute < 59) {
                                            modifyTime = wfn.getModifyTime();
                                            if (lag < wfn.getFileNode().getFile().length()
                                                      - wfn.getFileNode().getOffset()) {
                                                result = wfn;
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            result = readyToCollectList.get(0);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("refreshCurWFN error. uniqueKey is " + uniqueKey, e);
                }

                if (result == null) {
                    // 表示全部采集完成,则设置为最新的文件
                    result = collectingFileNodeList.get(collectingFileNodeList.size() - 1);
                }
                curWFileNode = result;
            }
        }
    }

    @Override
    public boolean stop(boolean force) {
        LOGGER.info("begin to stop logSource. logPath is " + logPath);
        isStopping = true;
        close();
        collectingFileNodeList.clear();
        collectingFileNodeMap.clear();
        relatedFileNodeMap.clear();
        return true;
    }

    @Override
    public boolean delete() {
        if (!isStopping) {
            stop(true);
        }
        OffsetManager.removeLogModel4Path(logPath.getLogModelId(), logPath.getPathId(),
            logPath.getRealPath(), null);
        return false;
    }

    @Override
    public boolean specialDelete(Object object) {
        if (!isStopping) {
            stop(true);
        }

        if (logPath != null) {
            OffsetManager.removeLogModel4Path(logPath.getLogModelId(), logPath.getPathId(),
                logPath.getRealPath(), null);
        }
        return true;
    }

    @Override
    public void setMetrics(TaskMetrics taskMetrics) {

        taskMetrics.setPath(logPath.getPath());
        taskMetrics.setPathid(logPath.getPathId());
        taskMetrics.setMasterfile(masterFileName);
        taskMetrics.setCollectpathisexists(collectingFileNodeMap.size() != 0 ? 1 : 0);

        List<FileStatistic> collectFiles = new ArrayList<>();
        String latestFileName = "null";
        Long latestLogTime = 0L;
        String logTimeStr = "";

        Integer isFileOrder = OrderFile.OrderFile.getStatus();
        Integer sliceErrorExists = 0;

        // 采集中的文件
        for (Map.Entry<String, WorkingFileNode> entry : collectingFileNodeMap.entrySet()) {
            WorkingFileNode wfn = entry.getValue();
            FileNode fileNode = wfn.getFileNode();
            long fileLength = 0L;
            try {
                fileLength = wfn.getFileNode().getFile().length();
            } catch (Exception e) {
                LOGGER.error(
                        String.format("get source metrics error. wfn is %s. root cause is: %s", wfn, e.getMessage()),
                        e
                );
            }
            FileStatistic fileStatistic = new FileStatistic(fileNode.getFileName(),
                    wfn.isFileEnd() && wfn.checkFileCollectEnd(),
                    fileNode.getModifyTime(), wfn.getIsFileOrder(),
                    wfn.getIsVaildTimeConfig(), wfn.getLatestLogTime(),
                    fileLength == 0L ? 0L : fileNode.getOffset() * 100
                            / fileLength);
            if(wfn.getIsFileOrder().equals(OrderFile.OutOfOrderFile.getStatus())) {
                isFileOrder = OrderFile.OutOfOrderFile.getStatus();
            }
            if(wfn.getIsVaildTimeConfig() == false) {
                sliceErrorExists = 1;
            }
            collectFiles.add(fileStatistic);
            if (wfn.getLatestLogTime() != null && latestLogTime < wfn.getLatestLogTime()) {
                latestLogTime = wfn.getLatestLogTime();
                logTimeStr = wfn.getLatestLogTimeStr();
            }
        }

        // 最新的文件
        File latestFile = FileUtils.getLatestRelatedFile(FileUtils.getPathDir(logPath.getRealPath()),
                FileUtils.getMasterFile(logPath.getRealPath()),
                logSourceConfig.getMatchConfig());
        if (latestFile != null) {
            latestFileName = latestFile.getName();
//            ret.put(FileMetricsFields.LATEST_MODIFY_TIME, latestFile.lastModified());
            //TODO：后期如需要，进行添加
        }

        // metrics内容
//        EventMetricsConfig eventMetricsConfig = modelConfig.getEventMetricsConfig();
//        if (eventMetricsConfig != null && eventMetricsConfig.getOtherMetrics() != null
//                && eventMetricsConfig.getOtherMetrics().size() != 0) {
//            for (Map.Entry<String, String> entry : eventMetricsConfig.getOtherMetrics().entrySet()) {
//                if (org.apache.commons.lang.StringUtils.isNotBlank(entry.getValue())
//                        && org.apache.commons.lang.StringUtils.isNotBlank(entry.getKey())) {
//                    ret.put(entry.getKey(), entry.getValue());
//                }
//            }
//        }
        //TODO：后期如需要，进行添加

        taskMetrics.setDisorderexists(isFileOrder);
        taskMetrics.setSliceerrorexists(sliceErrorExists);
        taskMetrics.setCollectfiles(JSON.toJSONString(collectFiles));
        taskMetrics.setLatestfile(latestFileName);
        taskMetrics.setMaxbusinesstimestampdelay(maxLogTime);
        if(!latestLogTime.equals(0L)) {
            latestLogTimeUsedOnMetrics = latestLogTime;
            taskMetrics.setBusinesstimestamp(latestLogTime);
        } else {
            taskMetrics.setBusinesstimestamp(latestLogTimeUsedOnMetrics);
        }
        taskMetrics.setRelatedfiles(relatedFileNodeMap.size());

        resetMaxLogTime();

    }

    private void close() {
        for (WorkingFileNode wfd : collectingFileNodeList) {
            wfd.close();
        }
    }

    public void syncOffset() {
        LOGGER.info("sync offset. logPath is " + logPath);
        if (collectingFileNodeList != null) {
            for (WorkingFileNode wfn : collectingFileNodeList) {
                wfn.seek(wfn.getFileNode().getOffset());
            }
        }
    }

    @Override
    public Map<String, Object> metric() {
        return null;
    }

    public LogPath getLogPath() {
        return logPath;
    }

    public void setLogPath(LogPath logPath) {
        this.logPath = logPath;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public LogSourceConfig getLogSourceConfig() {
        return logSourceConfig;
    }

    public void setLogSourceConfig(LogSourceConfig logSourceConfig) {
        this.logSourceConfig = logSourceConfig;
    }

    public ModelConfig getModelConfig() {
        return modelConfig;
    }

    public void setModelConfig(ModelConfig modelConfig) {
        this.modelConfig = modelConfig;
    }

    public WorkingFileNode getCurWFileNode() {
        return curWFileNode;
    }

    public void setCurWFileNode(WorkingFileNode curWFileNode) {
        this.curWFileNode = curWFileNode;
    }

    public Map<String, WorkingFileNode> getCollectingFileNodeMap() {
        return collectingFileNodeMap;
    }

    public void setCollectingFileNodeMap(Map<String, WorkingFileNode> collectingFileNodeMap) {
        this.collectingFileNodeMap = collectingFileNodeMap;
    }

    public String getMasterFileName() {
        return masterFileName;
    }

    public void setMasterFileName(String masterFileName) {
        this.masterFileName = masterFileName;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getDockerParentPath() {
        return dockerParentPath;
    }

    public void setDockerParentPath(String dockerParentPath) {
        this.dockerParentPath = dockerParentPath;
    }

    public Long getLogTime() {
        return eventParser.getLastTimestamp();
    }

    public void resetMaxLogTime() {
        this.maxLogTime = 0;
    }

    public Map<String, FileNode> getRelatedFileNodeMap() {
        return relatedFileNodeMap;
    }

    public void setRelatedFileNodeMap(Map<String, FileNode> relatedFileNodeMap) {
        this.relatedFileNodeMap = relatedFileNodeMap;
    }

    public List<WorkingFileNode> getCollectingFileNodeList() {
        return collectingFileNodeList;
    }

    public void setCollectingFileNodeList(List<WorkingFileNode> collectingFileNodeList) {
        this.collectingFileNodeList = collectingFileNodeList;
    }

    public void setMaxGapLogTime(Long logTime) {
        if (logTime == null) {
            return;
        }
        Long gap = System.currentTimeMillis() - logTime;
        if (gap > maxLogTime) {
            maxLogTime = gap;
        }
    }

}
