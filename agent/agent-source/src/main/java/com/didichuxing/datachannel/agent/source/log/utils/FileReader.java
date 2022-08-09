package com.didichuxing.datachannel.agent.source.log.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.didichuxing.datachannel.agent.common.api.FileReadType;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.api.StandardLogType;
import com.didichuxing.datachannel.agent.common.configs.v2.component.CommonConfig;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.bean.LogEvent;
import com.didichuxing.datachannel.agent.engine.utils.TimeUtils;
import com.didichuxing.datachannel.agent.source.log.LogSource;
import com.didichuxing.datachannel.agent.source.log.beans.WorkingFileNode;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description:
 * @author: huangjw
 * @Date: 19/7/8 16:42
 */
public class FileReader {

    private static final Logger LOGGER           = LoggerFactory.getLogger(FileReader.class
                                                     .getName());
    private static final String LINE_SEPARATOR   = System.lineSeparator();

    /**
     * 当前采集的时间戳
     */
    private volatile Long       timeStamp        = null;

    /**
     * 当前采集的可读格式时间
     */
    private volatile String     timeString       = null;

    private volatile String     nextContent      = null;
    private volatile byte[]     nextBytes        = null;
    private volatile long       nextOffset       = 0L;
    private volatile long       firstPreOffset   = 0L;
    private volatile Long       nextTimeStamp    = null;
    private volatile String     nextTimeStampStr = null;

    private volatile String     lastWFNKey       = null;

    private LogSource           logSource;

    public FileReader(LogSource logSource) {
        this.logSource = logSource;
    }

    public LogEvent readEvent(WorkingFileNode wfn) {
        long start = TimeUtils.getNanoTime();
        long lastOffset = wfn.getCurOffset();
        try {
            LogEvent result;

            // 不解析时间戳
            if (LogConfigConstants.NO_LOG_TIME.equals(logSource.getLogSourceConfig()
                .getTimeFormat())) {
                result = readNoTimeRow(wfn);
                if (result != null) {
                    // 使用fileNode的modifyTime，而非实时获取，节省资源
                    result.setFileModifyTime(wfn.getFileNode().getModifyTime());
                    filterResult(result, start);
                }
            } else {
                if (logSource.getLogSourceConfig().getReadFileType()
                    .equals(FileReadType.SingleRow.getType())
                    || !wfn.getIsVaildTimeConfig()) {
                    // 单行日志读取
                    result = readSingleRow(wfn);
                    if (result != null) {
                        filterResult(result, start);
                    }
                } else {
                    // 多行日志聚合读取
                    result = readMultiRowNew(wfn);
                    lastWFNKey = wfn.getUniqueKey();
                    if (result != null) {
                        filterResult(result, start);
                    }
                }
            }

            // 记录metrics
            if (result != null) {
                long gap = TimeUtils.getNanoTime() - start;
                long timeout = logSource.getLogSourceConfig().getReadTimeOut() * 1000 * 1000;
                if (gap > timeout) {
                    gap -= timeout;
                }
                if (this.logSource.getTaskPatternStatistics() != null) {
                    this.logSource.getTaskPatternStatistics().sourceOneRecord(result.length(), gap);
                    this.logSource.getAgentStatistics().sourceOneRecord(result.length(), gap);
                }
            }
            return result;
        } catch (IOException e1) {
            if (wfn.isFileOpen()) {
                LogGather.recordErrorLog("FileReader error.",
                    "read file content error for IOException", e1);
                wfn.seek(lastOffset);
            }
        } catch (ArrayIndexOutOfBoundsException e2) {
            if (wfn.isFileOpen()) {
                LogGather
                    .recordErrorLog(
                        "FileReader error.",
                        "read file content error for ArrayIndexOutOfBoundsException when wfn's file is open",
                        e2);
                wfn.seek(lastOffset);
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("FileReader error.", "read file content error for Exception",
                e);
        }
        return null;
    }

    /**
     * seek file
     *
     * @param in 文件句柄
     * @param currentOffSet 当前的offset
     * @return result of seek
     */
    private boolean seekFile(BufferedRandomAccessFile in, long currentOffSet) {
        // 防止文件突然被清空
        try {
            in.seek(currentOffSet);
        } catch (Exception e) {
            LogGather.recordErrorLog("FileReader error", "seek offset error.currentOffSet is:"
                                                         + currentOffSet, e);
            return false;
        }
        return true;
    }

    /**
     * 无时间戳日志读取
     *
     * @param wfn
     * @return result of read line
     * @throws Exception read error
     */
    private LogEvent readNoTimeRow(WorkingFileNode wfn) throws Exception {
        String encodeString;
        BufferedRandomAccessFile in = wfn.getIn();
        byte[] lineContent = in.readNewLine(getReadTimeOut());
        if (lineContent == null) {
            return null;
        }
        //byte[] bytes = lineContent.getBytes(StandardCharsets.ISO_8859_1);
        encodeString = new String(lineContent, this.logSource.getModelConfig().getCommonConfig()
            .getEncodeType());
        timeStamp = System.currentTimeMillis();
        timeString = timeStamp.toString();
        long currentOffSet = in.getFilePointer();
        long preOffset = in.preOffset();
        return new LogEvent(encodeString, lineContent, currentOffSet, timeStamp, timeString,
            preOffset, wfn.getUniqueKey(), wfn.getFileNode().getFileKey(), wfn.getFileNode()
                .getParentPath(), wfn.getFileNode().getFileName(), logSource.getMasterFileName(),
            logSource.getDockerParentPath(), wfn.getFileNode().getFileOffSet().getFileHeadMd5());
    }

    /**
     * 单行读取，读取不到，则使用当前时间戳
     *
     * @param wfn
     * @return result of read line
     * @throws Exception read error
     */
    private LogEvent readSingleRow(WorkingFileNode wfn) throws Exception {
        CommonConfig commonConfig = this.logSource.getModelConfig().getCommonConfig();
        BufferedRandomAccessFile in = wfn.getIn();
        String suspectTimeString;
        String encodeString;
        byte[] lineContent = in.readNewLine(getReadTimeOut());
        if (lineContent == null) {
            return null;
        }
        //byte[] bytes = lineContent.getBytes(StandardCharsets.ISO_8859_1);
        encodeString = new String(lineContent, commonConfig.getEncodeType());
        suspectTimeString = FileUtils.getTimeStringFormLineByIndex(encodeString,
            this.logSource.getLogSourceConfig());
        if (StringUtils.isNotBlank(suspectTimeString) && !suspectTimeString.equals(timeString)) {
            timeStamp = TimeUtils.getLongTimeStamp(suspectTimeString, logSource
                .getLogSourceConfig().getTimeFormat());
        }
        timeString = suspectTimeString;
        if (timeStamp == null) {
            timeStamp = System.currentTimeMillis();
            timeString = timeStamp.toString();
        }
        long currentOffSet = in.getFilePointer();
        long preOffset = in.preOffset();
        return new LogEvent(encodeString, lineContent, currentOffSet, timeStamp, timeString,
            preOffset, wfn.getUniqueKey(), wfn.getFileNode().getFileKey(), wfn.getFileNode()
                .getParentPath(), wfn.getFileNode().getFileName(), logSource.getMasterFileName(),
            logSource.getDockerParentPath(), wfn.getFileNode().getFileOffSet().getFileHeadMd5());
    }

    /**
     * 多行聚合读取
     *
     * @param wfn
     * @return result of read line
     * @throws Exception read error
     */
    private LogEvent readMultiRowNew(WorkingFileNode wfn) throws Exception {
        //ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        List<Pair<String, byte[]>> multiRows = new ArrayList<>();
        CommonConfig commonConfig = this.logSource.getModelConfig().getCommonConfig();
        BufferedRandomAccessFile in = wfn.getIn();
        //StringBuffer sb = new StringBuffer();
        String suspectTimeString;
        String encodeString;
        int errorLine = 0;
        long currentOffSet = 0L;
        long preOffset = 0L;
        boolean isFirstLine = true;
        Long longTimeStamp;

        // 标记sb里是否有有效行的数据
        boolean isVaild = false;

        if (lastWFNKey == null || !lastWFNKey.equals(wfn.getUniqueKey())) {
            // 此时发生了文件切换，需要切换文件，使得所有缓存失效
            nextContent = null;
            nextBytes = null;
            nextOffset = 0L;
            firstPreOffset = 0L;
            nextTimeStamp = null;
            nextTimeStampStr = null;
            timeStamp = null;
            timeString = null;

            // 因上一次发生了多读一行的操作，恢复到wfn的读取offset位置
            wfn.seek(wfn.getCurOffset());
        }

        while (true) {
            // 若上一行存在，则直接拼接上一有效行
            if (nextContent != null) {
                //sb.append(nextContent);
                multiRows.add(Pair.of(nextContent, nextBytes));
                //byteStream.write(nextBytes);
                timeString = nextTimeStampStr;
                timeStamp = nextTimeStamp;
                nextContent = null;
                nextBytes = null;
                isVaild = true;
                if (isFirstLine) {
                    preOffset = firstPreOffset;
                    isFirstLine = false;
                }
            }

            // 读当前行
            byte[] lineContent = in.readNewLine(getReadTimeOut());
            firstPreOffset = in.preOffset();
            if (isFirstLine) {
                preOffset = firstPreOffset;
                isFirstLine = false;
            }

            if (lineContent == null) {
                currentOffSet = in.getFilePointer();
                break;
            }

            //byte[] bytes = lineContent.getBytes(StandardCharsets.ISO_8859_1);
            encodeString = new String(lineContent, commonConfig.getEncodeType());
            suspectTimeString = FileUtils.getTimeStringFormLineByIndex(encodeString,
                    this.logSource.getLogSourceConfig());
            if (StringUtils.isNotBlank(suspectTimeString) && suspectTimeString.equals(timeString)) {
                longTimeStamp = timeStamp;
            } else {
                longTimeStamp = TimeUtils.getLongTimeStamp(suspectTimeString, this.logSource
                        .getLogSourceConfig().getTimeFormat());
            }
            if (longTimeStamp != null) {
                long tmpTimeStamp = longTimeStamp;
                if (tmpTimeStamp > 0) {
                    if (isVaild) {
                        currentOffSet = nextOffset;

                        // 上一行存在，当前行解析正常【赋值返回】
                        nextTimeStamp = tmpTimeStamp;
                        nextTimeStampStr = suspectTimeString;
                        nextOffset = in.getFilePointer();
                        nextContent = encodeString;
                        nextBytes = lineContent;
                        break;
                    } else {
                        // 上一行不存在，当前行解析正常【继续读取】
                        nextTimeStamp = tmpTimeStamp;
                        nextTimeStampStr = suspectTimeString;
                        nextOffset = in.getFilePointer();
                        nextContent = encodeString;
                        nextBytes = lineContent;
                    }
                } else {
                    // 解析时间戳异常
                    if (isVaild) {
                        // 上一行存在，当前行解析异常【拼接上一行】
                        //sb.append(LINE_SEPARATOR).append(encodeString);
                        nextOffset = in.getFilePointer();
                        multiRows.add(Pair.of(encodeString, lineContent));
                        //byteStream.write(LogConfigConstants.CTRL_BYTES);
                        //byteStream.write(lineContent);
                    } else {
                        // 上一行不存在，当前行解析异常【忽略或者拼装】
                        //sb.append(encodeString).append(LINE_SEPARATOR);
                        multiRows.add(Pair.of(encodeString, lineContent));
                        //byteStream.write(lineContent);
                        //byteStream.write(LogConfigConstants.CTRL_BYTES);
                    }
                    errorLine++;
                }
            } else {
                // 解析时间戳异常
                if (isVaild) {
                    // 上一行存在，当前行解析异常【拼接上一行】
                    //sb.append(LINE_SEPARATOR).append(encodeString);
                    nextOffset = in.getFilePointer();
                    multiRows.add(Pair.of(encodeString, lineContent));
                    //byteStream.write(LogConfigConstants.CTRL_BYTES);
                    //byteStream.write(lineContent);
                } else {
                    // 上一行不存在，当前行解析异常【忽略或者拼装】
                    multiRows.add(Pair.of(encodeString, lineContent));
                    //sb.append(encodeString).append(LINE_SEPARATOR);
                    //byteStream.write(lineContent);
                    //byteStream.write(LogConfigConstants.CTRL_BYTES);
                }
                errorLine++;
            }

            // 连续n次无法读取到日志，说明日志配置失效，或者日志文件时间戳变更
            if (errorLine != 0
                    && errorLine >= this.logSource.getLogSourceConfig().getMaxErrorLineNum()) {
                LOGGER.warn("wfn's timestamp is not vaild. wfn is " + wfn);
                // 标记为错误的配置
                wfn.setIsVaildTimeConfig(false);
                currentOffSet = in.getFilePointer();
                in.seek(currentOffSet);

                break;
            }
        }
        if (multiRows.size() == 0) {
            return null;
        } else if (multiRows.size() == 1) {
            Pair<String, byte[]> first = multiRows.get(0);
            return new LogEvent(first.getLeft(), first.getRight(), currentOffSet, timeStamp,
                    timeString, preOffset, wfn.getUniqueKey(), wfn.getFileNode().getFileKey(), wfn
                    .getFileNode().getParentPath(), wfn.getFileNode().getFileName(),
                    logSource.getMasterFileName(), logSource.getDockerParentPath(), wfn.getFileNode()
                    .getFileOffSet().getFileHeadMd5());
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (int i = 0; i < multiRows.size(); i++) {
                out.write(multiRows.get(i).getRight());
                if (i < multiRows.size() - 1) {
                    out.write(LogConfigConstants.CTRL_BYTES);
                }
            }
            byte[] contentBytes = out.toByteArray();
            return new LogEvent(new String(contentBytes, commonConfig.getEncodeType()), contentBytes, currentOffSet, timeStamp,
                    timeString, preOffset, wfn.getUniqueKey(), wfn.getFileNode().getFileKey(), wfn
                    .getFileNode().getParentPath(), wfn.getFileNode().getFileName(),
                    logSource.getMasterFileName(), logSource.getDockerParentPath(), wfn.getFileNode()
                    .getFileOffSet().getFileHeadMd5());
        }

    }

    private Long getReadTimeOut() {
        return this.logSource.getLogSourceConfig().getReadTimeOut();
    }

    /**
     * public log中过滤包含_shadow的日志
     * @param content
     * @return
     */
    private boolean isNeedFilter(String content) {
        if (StringUtils.isBlank(content)) {
            return true;
        }

        // 记录第一个"||"
        int firstIndex = content.indexOf(LogConfigConstants.PUBLIC_LOG_TAG);

        //兼容public日志中存在一些日志不报||的情况
        if (firstIndex == -1) {
            return false;
        }

        String logKey = content.substring(0, firstIndex);

        return logKey.endsWith(LogConfigConstants.FILTER_KEY);
    }

    /**
     * 
     * @param logEvent
     * @return
     */
    private void filterResult(LogEvent logEvent, long startTime) {
        // TODO: 2021-01-15 这里以后需要设置个public日志预估过滤开关，目前所有public log都过滤
        if (logSource.getLogSourceConfig().getMatchConfig().getBusinessType()
            .equals(StandardLogType.Public.getType())
            && logEvent != null) {
            boolean filter = isNeedFilter(logEvent.getContent());
            if (filter && (logEvent.getOffset() != null)) {
                this.logSource.getCurWFileNode().setCurOffset(logEvent.getOffset());
                logEvent.setNeedToSend(false);
                logEvent.setContent("");
            }
            if (this.logSource.getTaskPatternStatistics() != null) {
                this.logSource.getTaskPatternStatistics().filterOneRecord(
                    TimeUtils.getNanoTime() - startTime, !filter);
            }
        }
    }

}
