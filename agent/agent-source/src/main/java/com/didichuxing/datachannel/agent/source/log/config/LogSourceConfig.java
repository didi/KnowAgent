package com.didichuxing.datachannel.agent.source.log.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;
import com.didichuxing.datachannel.agent.common.beans.LogPath;
import com.didichuxing.datachannel.agent.common.configs.v2.component.sourceConfig.SourceConfig;
import com.didichuxing.datachannel.agent.common.constants.Tags;

/**
 * @description: log source config
 * @author: huangjw
 * @Date: 19/7/1 16:06
 */
public class LogSourceConfig extends SourceConfig {

    /** 日志内容中时间的格式 */
    private String        timeFormat         = "yyyy-MM-dd HH:mm:ss";

    private int           timeFormatLength   = timeFormat.length();

    /** 日志内容开始标示 */
    private String        timeStartFlag      = "";

    /** 第几个开始标示 */
    private int           timeStartFlagIndex = 0;

    /**
     * 上线两条日志时间戳相差orderTimeMaxGap，即认为是乱序的日志,乱序阈值
     */
    private Long          orderTimeMaxGap    = 10 * 60 * 1000L;

    /**
     * 是否顺序采集.只能保证2个文件之间的顺序，若出现延迟，则无法保证
     */
    private Boolean       sequentialCollect  = false;

    /**
     * 开始采集位置
     */
    private Integer       collectLocation    = 0;

    /**
     * 是否是顺序文件
     */
    private Integer       isOrderFile        = 0;

    /**
     * 有效的最新文件，最新的前2个文件，一直处于等待状态，不close
     */
    private Integer       validLatestFiles   = 2;

    /**
     * 校验是否可关闭的时间间隔
     */
    private Long          waitCheckTime      = 5 * 60 * 1000L;

    /**
     * 最大的同时采集的线程数
     */
    private Integer       maxThreadNum       = 10;

    /**
     * 连续100行无法解析到日志，则采集结束
     */
    private Integer       maxErrorLineNum    = 100;

    /**
     * 是否是有效的时间戳配置
     */
    private Boolean       isValidTimeConfig  = true;

    /**
     * 文件读取类型，默认多行聚合（java栈）
     */
    private Integer       readFileType       = 0;

    /**
     * 文件最晚修改时间
     */
    private Long          maxModifyTime      = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 日志读取超时时间，单位为ms。即读到文件末尾，等待timeout秒再读一次
     */
    private Long          readTimeOut        = 3000L;

    /**
     * 文件匹配模式
     */
    private MatchConfig   matchConfig;

    /**
     * 日志路径
     */
    private List<LogPath> logPaths;

    public List<LogPath> getLogPaths() {
        return logPaths;
    }

    public void setLogPaths(List<LogPath> logPaths) {
        this.logPaths = logPaths;
    }

    public LogSourceConfig() {
        super(Tags.SOURCE_LOG);
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getTimeStartFlag() {
        return timeStartFlag;
    }

    public void setTimeStartFlag(String timeStartFlag) {
        this.timeStartFlag = timeStartFlag;
    }

    public int getTimeStartFlagIndex() {
        return timeStartFlagIndex;
    }

    public void setTimeStartFlagIndex(int timeStartFlagIndex) {
        this.timeStartFlagIndex = timeStartFlagIndex;
    }

    public Long getOrderTimeMaxGap() {
        return orderTimeMaxGap;
    }

    public void setOrderTimeMaxGap(Long orderTimeMaxGap) {
        this.orderTimeMaxGap = orderTimeMaxGap;
    }

    public Boolean getSequentialCollect() {
        return sequentialCollect;
    }

    public void setSequentialCollect(Boolean sequentialCollect) {
        this.sequentialCollect = sequentialCollect;
    }

    public Integer getCollectLocation() {
        return collectLocation;
    }

    public void setCollectLocation(Integer collectLocation) {
        this.collectLocation = collectLocation;
    }

    public Integer getIsOrderFile() {
        return isOrderFile;
    }

    public void setIsOrderFile(Integer isOrderFile) {
        this.isOrderFile = isOrderFile;
    }

    public Integer getValidLatestFiles() {
        return validLatestFiles;
    }

    public void setValidLatestFiles(Integer validLatestFiles) {
        this.validLatestFiles = validLatestFiles;
    }

    public Long getWaitCheckTime() {
        return waitCheckTime;
    }

    public void setWaitCheckTime(Long waitCheckTime) {
        this.waitCheckTime = waitCheckTime;
    }

    public Integer getMaxThreadNum() {
        return maxThreadNum;
    }

    public void setMaxThreadNum(Integer maxThreadNum) {
        this.maxThreadNum = maxThreadNum;
    }

    public Integer getMaxErrorLineNum() {
        return maxErrorLineNum;
    }

    public void setMaxErrorLineNum(Integer maxErrorLineNum) {
        this.maxErrorLineNum = maxErrorLineNum;
    }

    public int getTimeFormatLength() {
        return timeFormatLength;
    }

    public void setTimeFormatLength(int timeFormatLength) {
        this.timeFormatLength = timeFormatLength;
    }

    public Boolean getValidTimeConfig() {
        return isValidTimeConfig;
    }

    public void setValidTimeConfig(Boolean vaildTimeConfig) {
        isValidTimeConfig = vaildTimeConfig;
    }

    public Integer getReadFileType() {
        return readFileType;
    }

    public void setReadFileType(Integer readFileType) {
        this.readFileType = readFileType;
    }

    public Long getMaxModifyTime() {
        return maxModifyTime;
    }

    public void setMaxModifyTime(Long maxModifyTime) {
        this.maxModifyTime = maxModifyTime;
    }

    public Long getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(Long readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    @JSONField(serialize = false)
    public Map<Long, LogPath> getLogPathMap() {
        Map<Long, LogPath> logPathMap = new HashMap<>();
        for (LogPath logPath : logPaths) {
            logPathMap.put(logPath.getPathId(), logPath);
        }
        return logPathMap;
    }

    public MatchConfig getMatchConfig() {
        return matchConfig;
    }

    public void setMatchConfig(MatchConfig matchConfig) {
        this.matchConfig = matchConfig;
    }

    @Override
    public String toString() {
        return "LogSourceConfig{" + "timeFormat='" + timeFormat + '\'' + ", timeStartFlag='"
               + timeStartFlag + '\'' + ", timeStartFlagIndex=" + timeStartFlagIndex
               + ", orderTimeMaxGap=" + orderTimeMaxGap + ", sequentialCollect="
               + sequentialCollect + ", collectLocation=" + collectLocation + ", isOrderFile="
               + isOrderFile + ", validLatestFiles=" + validLatestFiles + ", waitCheckTime="
               + waitCheckTime + ", maxThreadNum=" + maxThreadNum + ", maxErrorLineNum="
               + maxErrorLineNum + ", isValidTimeConfig=" + isValidTimeConfig + ", readFileType="
               + readFileType + ", maxModifyTime=" + maxModifyTime + ", readTimeOut=" + readTimeOut
               + ", matchConfig=" + matchConfig + ", logPaths=" + logPaths + '}';
    }
}
