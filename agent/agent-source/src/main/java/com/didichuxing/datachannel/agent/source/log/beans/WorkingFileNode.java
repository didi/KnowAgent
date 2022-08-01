package com.didichuxing.datachannel.agent.source.log.beans;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import org.apache.commons.lang3.StringUtils;

import com.didichuxing.datachannel.agent.common.api.CollectLocation;
import com.didichuxing.datachannel.agent.common.api.FileMatchType;
import com.didichuxing.datachannel.agent.common.api.OrderFile;
import com.didichuxing.datachannel.agent.common.api.StandardRolledTimeType;
import com.didichuxing.datachannel.agent.engine.bean.LogEvent;
import com.didichuxing.datachannel.agent.engine.utils.TimeUtils;
import com.didichuxing.datachannel.agent.source.log.LogSource;
import com.didichuxing.datachannel.agent.source.log.utils.BufferedRandomAccessFile;
import com.didichuxing.datachannel.agent.source.log.utils.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 运行的FileNoe
 * @author: huangjw
 * @Date: 19/7/8 17:30
 */
public class WorkingFileNode {

    private static final Logger      LOGGER                 = LoggerFactory
            .getLogger(WorkingFileNode.class);

    FileNode                         fileNode;

    private BufferedRandomAccessFile in                     = null;
    private volatile boolean         isFileOpen             = false;
    private volatile long            curOffset              = 0L;
    // 文件发生了一次清空
    private volatile boolean         backflow               = false;

    private String                   uniqueKey;
    private boolean                  isFileEnd;
    /** * 默认重试次数 */
    private int                      idleRetryTimes         = 0;

    /** * 没有日志产生，又未结束时，内部先重读几次，如果依旧未读到数据，则Sleep一段时间 */
    private static Integer           maxIdelRetryTimes      = 10;

    /** * 空闲休息 */
    private final static Long        IDLE_SLEEP_TIME        = 500L;

    /**
     * 最近一次校验等待关闭时间
     */
    private long                     lastestWaitToCloseTime = 0L;

    private LogSource                logSource;

    /**
     * 采集文件顺序性
     */
    private AtomicInteger            isFileOrder            = new AtomicInteger(
            OrderFile.OrderFile.getStatus());

    /**
     * 采集配置有效性
     */
    private AtomicBoolean            isVaildTimeConfig      = new AtomicBoolean(true);

    /**
     * 最新采集时间
     */
    private Long                     latestLogTime;

    /**
     * 最新采集时间 字符串形式
     */
    private String                   latestLogTimeStr;

    /**
     * 该文件对应的文件区间，如:
     * public.log.2019112611，则对应了时间2019112611的时间戳
     * public.log.20191126,则对应了时间20191126的时间戳
     *
     */
    private Long                     intervalTime           = 0L;

    private final static Long        ONE_HOUR               = 60 * 60 * 1000L;
    private final static Long        ONE_DAY                = 24 * 60 * 60 * 1000L;

    public WorkingFileNode(FileNode fileNode, LogSource logSource) {
        this.fileNode = fileNode;
        this.uniqueKey = fileNode.getNodeKey();
        this.logSource = logSource;
    }

    private void open(int location) throws Exception {
        in = new BufferedRandomAccessFile(this.fileNode.getAbsolutePath(), "r");
        if (in.length() < this.fileNode.getOffset()) {
            LOGGER.info("the file length is less than offset！offset:" + this.fileNode.getOffset()
                    + ",fileNode is " + fileNode.toString());
            if (location == CollectLocation.Earliest.getLocation()) {
                this.fileNode.setOffset(0L);
            } else {
                this.fileNode.setOffset(in.length());
            }
        }
        LOGGER.info("begin to open fileNode. fileNode is " + fileNode.toString() + ", offset is "
                + this.fileNode.getOffset());
        in.seek(this.fileNode.getOffset());
        this.curOffset = this.fileNode.getOffset();
        isFileOpen = true;

        setIntervalTime();
    }

    /**
     * 设置intervalTime
     */
    private void setIntervalTime() {
        String fileName = this.fileNode.getFileName();
        String masterFileName = this.logSource.getMasterFileName();
        String suffixForThisFile = null;
        if (!fileName.equals(masterFileName)) {
            // 从文件名获取
            suffixForThisFile = fileName.substring(masterFileName.length() + 1);
        }
        StandardRolledTimeType timeType = getTimeType(suffixForThisFile);
        if (timeType != null) {
            intervalTime = TimeUtils.getLongTimeStamp(suffixForThisFile, timeType.getFormat());
        } else {
            // 从配置获取,此时使用当前时间的时间后缀
            if (logSource.getLogSourceConfig().getMatchConfig().getMatchType() == FileMatchType.Length
                    .getStatus()) {
                String timeStr = logSource.getLogSourceConfig().getMatchConfig().getFileSuffix();
                if (StringUtils.isNotBlank(timeStr)) {
                    timeStr = timeStr.substring(1);
                    StandardRolledTimeType tmpTimeType = getTimeType(timeStr);
                    long interval = 0L;
                    if (tmpTimeType == StandardRolledTimeType.YMDH
                            || tmpTimeType == StandardRolledTimeType.Y_M_D_H) {
                        interval = ONE_HOUR;
                    } else {
                        interval = ONE_DAY;
                    }
                    LOGGER
                            .warn("timeType is null. use fileNode's modifyTime() to set intervalTime. uniqueKey is "
                                    + uniqueKey);
                    intervalTime = (fileNode.getModifyTime() / interval) * interval;
                }
            }
        }
        if (intervalTime == 0L) {
            LOGGER.info("use currentTime to set intervalTime. uniqueKey is " + uniqueKey);
            intervalTime = (System.currentTimeMillis() / ONE_HOUR) * ONE_HOUR;
        }
    }

    private StandardRolledTimeType getTimeType(String suffix) {
        if (StringUtils.isBlank(suffix)) {
            return null;
        }

        for (StandardRolledTimeType type : StandardRolledTimeType.values()) {
            boolean isMatch = Pattern.matches(type.getMatchSample(), suffix);
            if (isMatch) {
                return type;
            }
        }
        return null;
    }

    public synchronized void close() {
        LOGGER.info("begin to close fileNode. fileNode is " + fileNode.toString());
        try {
            if (in != null) {
                in.close();
                isFileOpen = false;
            } else {
                // 文件不存在
                isFileOpen = false;
            }
            LOGGER.info("success to close fileNode");
        } catch (Exception e) {
            LogGather.recordErrorLog("WorkingFileNode error", "file close failed!, fileInfo is "
                    + this.fileNode.getFileName(), e);
        }
    }

    public void seek(long offset) {
        LOGGER.info("seek to new offset. uniqueKey Key is " + uniqueKey + ", offset is " + offset);
        try {
            in.seek(offset);
            curOffset = offset;
            if (offset < in.length() && isFileEnd) {
                LOGGER.info("change fileEnd status to false. uniqueKey Key is " + uniqueKey);
                isFileEnd = false;
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("WorkingFileNode error",
                    "seek offset error! uniqueKey Key is " + uniqueKey, e);
        }
    }

    public boolean open(int location, int retryTimes) {
        LOGGER
                .info("begin to open fileNode. fileNode is " + fileNode + ", location is " + location);
        // 修复批量补采过程中由于文件滚动删除导致补采任务卡住问题
        if (!fileNode.getFile().exists()) {
            LOGGER.info("open@file is not exit! fileNode is " + fileNode.toString());
            isFileEnd = true;
            return false;
        }

        for (int i = 0; i < retryTimes; i++) {
            try {
                open(location);
                return true;
            } catch (Exception ex) {
                if (i == retryTimes - 1) {
                    LOGGER.warn(
                            "WorkingFileNode error. open file failed; file:"
                                    + this.fileNode.getFileName(), ex);
                    close();
                } else {
                    try {
                        // 先关闭文件，防止文件被重复打开
                        close();
                        // 等待1s
                        Thread.sleep(1000L);
                    } catch (Exception e) {
                        LogGather.recordErrorLog("WorkingFileNode error",
                                "sleep is interrupt when waiting file to open.", e);
                    }
                    LOGGER.warn("open file failed;file:" + this.fileNode.getFileName()
                            + ",reTry! time is " + i);
                }
            }
        }
        return false;
    }

    /**
     * 检测是否采集到文件末尾
     * @param logEvent
     * @throws Exception
     */
    public boolean whetherToFileEnd(LogEvent logEvent) throws Exception {
        if (!isFileOpen) {
            return false;
        }

        if (logEvent != null && logEvent.getFileNodeKey().equals(uniqueKey)) {
            isFileEnd = false;
            idleRetryTimes = 0;
        } else {
            if (curOffset > in.length()) {
                // 此时说明文件被突然清空,重置offset
                LOGGER.warn("file:" + fileNode.getFileName()
                        + "'s offset is larger than current.set recorded offset to 0 "
                        + ",curOffset is " + curOffset + ", length is " + in.length());
                curOffset = 0L;
                in.seek(curOffset);
                getFileNode().setOffset(curOffset);
                setBackflow(true);
            } else {
                if (checkFileEnd()) {
                    isFileEnd = true;
                }
            }
        }
        return isFileEnd;
    }

    /** * 检测是否采集到文件末尾 */
    private boolean checkFileEnd() {
        try {
            if (!fileNode.getFile().exists() || in.ifEnd()) {
                return true;
            } else {
                // 重新seek，防止因为read导致的offset前移
                in.seek(curOffset);
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("WorkingFileNode error",
                    "checkFileEnd error..logModelId is " + fileNode.getModelId() + ".actualLogPath is "
                            + fileNode.getFileName(), e);
        }
        return false;
    }

    /**
     * 是否采集文件末尾，且数据均发送成功
     * @return
     */
    public boolean checkFileCollectEnd() {
        try {
            if (!fileNode.getFile().exists()) {
                LOGGER.info("checkFileCollectEnd@file is not exit! fileNode is "
                        + fileNode.toString());
                return true;
            }

            if (getFileNode().getOffset() > in.length()) {
                LOGGER
                        .warn("offset's offset is large than length and set offset to length.offset is "
                                + getFileNode().getFileOffSet() + ", length is " + in.length());
                getFileNode().setOffset(in.length());
                return true;
            }
            return getFileNode().getOffset() == in.length();
        } catch (Exception e) {
            LogGather.recordErrorLog("WorkingFileNode error",
                    "checkFileCollectEnd error..logModelId is " + fileNode.getModelId()
                            + ".actualLogPath is " + fileNode.getFileName(), e);
            return false;
        }
    }

    /**
     * 同步文件属性
     */
    public void syncFile() {
        String path = logSource.getLogPath().getRealPath();
        String fileKey = fileNode.getFileKey();
        List<File> files = FileUtils.getRelatedFiles(path, logSource.getLogSourceConfig()
                .getMatchConfig());
        if (files != null && files.size() != 0) {
            for (File file : files) {
                String tmpFileKey = FileUtils.getFileKeyByAttrs(file);
                if (StringUtils.isNotBlank(tmpFileKey) && fileKey.equals(tmpFileKey)) {
                    fileNode.setModifyTime(FileUtils.getModifyTime(file));
                    fileNode.setFileName(file.getName());
                    return;
                }
            }
        }
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public FileNode getFileNode() {
        return fileNode;
    }

    public void setFileNode(FileNode fileNode) {
        this.fileNode = fileNode;
    }

    public boolean isFileOpen() {
        return isFileOpen;
    }

    public void setFileOpen(boolean fileOpen) {
        isFileOpen = fileOpen;
    }

    public BufferedRandomAccessFile getIn() {
        return in;
    }

    public void setIn(BufferedRandomAccessFile in) {
        this.in = in;
    }

    public Long getModifyTime() {
        return this.fileNode.getModifyTime();
    }

    public boolean isFileEnd() {
        return isFileEnd;
    }

    public void setFileEnd(boolean fileEnd) {
        isFileEnd = fileEnd;
    }

    public Integer getIsFileOrder() {
        return isFileOrder.get();
    }

    public void setIsFileOrder(Integer order) {
        this.isFileOrder.set(order);
    }

    public boolean getIsVaildTimeConfig() {
        return isVaildTimeConfig.get();
    }

    public void setIsVaildTimeConfig(boolean isVaildTimeConfig) {
        this.isVaildTimeConfig.set(isVaildTimeConfig);
    }

    public Long getLatestLogTime() {
        return latestLogTime;
    }

    public String getLatestLogTimeStr() {
        return latestLogTimeStr;
    }

    public void setLatestLogTimeStr(String latestLogTimeStr) {
        this.latestLogTimeStr = latestLogTimeStr;
    }

    public void setLatestLogTime(Long latestLogTime) {
        this.latestLogTime = latestLogTime;
    }

    public long getCurOffset() {
        return curOffset;
    }

    public void setCurOffset(long curOffset) {
        this.curOffset = curOffset;
    }

    public boolean isBackflow() {
        return backflow;
    }

    public void setBackflow(boolean backflow) {
        LOGGER.info("set backflow to " + backflow + ", wfn's key is " + uniqueKey);
        this.backflow = backflow;
    }

    public Long getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(Long intervalTime) {
        this.intervalTime = intervalTime;
    }

    @Override
    public String toString() {
        return "WorkingFileNode{" + "fileNode=" + fileNode + ", in=" + in + ", isFileOpen="
                + isFileOpen + ", curOffset=" + curOffset + ", backflow=" + backflow
                + ", uniqueKey='" + uniqueKey + '\'' + ", isFileEnd=" + isFileEnd
                + ", idleRetryTimes=" + idleRetryTimes + ", lastestWaitToCloseTime="
                + lastestWaitToCloseTime + ", logSource=" + logSource + ", isFileOrder="
                + isFileOrder + ", isVaildTimeConfig=" + isVaildTimeConfig + ", latestLogTime="
                + latestLogTime + ", latestLogTimeStr='" + latestLogTimeStr + '\''
                + ", intervalTime=" + intervalTime + '}';
    }
}
