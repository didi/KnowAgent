package com.didichuxing.datachannel.agent.source.log.utils;

import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import org.apache.commons.lang3.StringUtils;

import com.didichuxing.datachannel.agent.common.api.OrderFile;
import com.didichuxing.datachannel.agent.engine.bean.LogEvent;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.source.log.LogSource;
import com.didichuxing.datachannel.agent.source.log.beans.WorkingFileNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 包装解析器
 * @author: huangjw
 * @Date: 19/7/9 14:10
 */
public class EventParser {

    private static final Logger LOGGER         = LoggerFactory.getLogger(EventParser.class
                                                   .getName());
    /**
    * 上一条有效日志的时间戳
    */
    private Long                lastTimestamp  = null;

    /**
     * 上一条有效日志的时间
     */
    private String              lastTimeString = "";

    /**
     * 上一条有效日志的wfn'key
     */
    private String              lastWFNKey     = "";

    public void parse(LogSource logSource, WorkingFileNode workingFileNode, LogEvent logEvent) {
        if (logEvent != null) {
            Long timeStamp = logEvent.getLogTime();
            String timeString = logEvent.getTimestamp();
            if (null != logEvent.getContent()) {
                logEvent.setLogId(logSource.getCurWFileNode().getFileNode().getModelId());
                if (timeStamp == null || StringUtils.isEmpty(timeString)) {
                    // 若timeStamp从未读取过，则使用当前时间戳表示其时间戳
                    timeStamp = logEvent.getTransTime();
                    timeString = timeStamp.toString();
                    logEvent.setLogTime(timeStamp);
                    logEvent.setTimestamp(timeString);
                }
                // 构造唯一Key, 不解析时间戳时，唯一key使用workingFileNode的区间时间
                if (LogConfigConstants.NO_LOG_TIME.equals(logSource.getLogSourceConfig()
                    .getTimeFormat())) {
                    logEvent.setUniqueKey(buildUniqueKey(logSource, logEvent.getOffset(),
                        workingFileNode.getIntervalTime()));
                } else {
                    logEvent
                        .setUniqueKey(buildUniqueKey(logSource, logEvent.getOffset(), timeStamp));
                }
            }

            logEvent.setLogId(logSource.getLogPath().getLogModelId());
            logEvent.setLogPathId(logSource.getLogPath().getPathId());

            // 检测采集文件，是否是顺序文件
            if (lastTimestamp != null && timeStamp != null
                && lastWFNKey.equals(workingFileNode.getUniqueKey())
                && lastTimestamp - timeStamp > logSource.getLogSourceConfig().getOrderTimeMaxGap()) {
                workingFileNode.setIsFileOrder(OrderFile.OutOfOrderFile.getStatus());
            }

            lastTimestamp = timeStamp;
            lastTimeString = timeString;
            lastWFNKey = workingFileNode.getUniqueKey();
            workingFileNode.setLatestLogTime(lastTimestamp);
            workingFileNode.setLatestLogTimeStr(lastTimeString);

            // 设置maxGap
            logSource.setMaxGapLogTime(timeStamp);
        }
    }

    /**
     * 构造日志唯一key
     *
     * @param currentOffSet current offset
     * @param timeStamp timestamp
     * @return content's uniquekey
     */

    private String buildUniqueKey(LogSource logSource, long currentOffSet, long timeStamp) {
        String md5 = Md5ConfigService.getLogPath2Md5Map().get(logSource.getUniqueKey());
        if (StringUtils.isBlank(md5)) {
            String masterFilePath = logSource.getLogPath().getPath();
            LOGGER.info("md5 is empty or null when masterFilePath is " + masterFilePath
                        + ",and pathID is " + logSource.getLogPath().getPathId());
            md5 = CommonUtils.getMd5(masterFilePath, null);
            Md5ConfigService.setLogPath2Md5Map(logSource.getUniqueKey(), md5);
        }
        return md5 + "_" + currentOffSet + "_" + timeStamp;
    }

    public Long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(Long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public String getLastTimeString() {
        return lastTimeString;
    }

    public void setLastTimeString(String lastTimeString) {
        this.lastTimeString = lastTimeString;
    }
}
