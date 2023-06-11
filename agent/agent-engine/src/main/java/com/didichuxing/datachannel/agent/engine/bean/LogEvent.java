package com.didichuxing.datachannel.agent.engine.bean;

/**
 * @description: logEvent
 * @author: huangjw
 * @Date: 19/7/9 11:33
 */
public class LogEvent extends Event {

    /**
     * 本条日志生成时间
     */

    private String  timestamp;

    /**
     * 日志采集时间
     */
    private Long    collectTime;

    /**
     * 日志的时间
     */
    private Long    logTime;

    /**
     * 在日志文件中的偏移量
     */
    private Long    offset;

    /**
     * 唯一key
     */
    private String  uniqueKey;

    /**
     * 前一行日志的偏移量
     */
    private Long    preOffset;

    /**
     * 日志模型logId
     */
    private Long    logId;

    /**
     * 日志文件唯一key
     */
    private String  fileKey;

    /**
     * 日志文件唯一key + 日志id + 路径id
     */
    private String  fileNodeKey;

    /**
     * 文件路径
     */
    private String  filePath;

    /**
     * 文件名
     */
    private String  fileName;

    /**
     * 容器的父目录
     */
    private String  dockerParentPath;

    /**
     * 路径pathId
     */
    private long    logPathId;

    /**
     * 主文件名
     */
    private String  masterFileName;

    /**
     * 是否需要发送
     */
    private boolean needToSend     = true;

    /**
     * 文件的最新修改时间
     */
    private Long    fileModifyTime = -1L;

    /**
     * 文件头部MD5值
     */
    private String  fileHeadMd5;

    public LogEvent() {
        super("", new byte[1]);
    }

    public LogEvent(String content, byte[] bytes, Long offset, Long timestamp, String timeString,
                    Long preOffset, String fileNodeKey, String fileKey, String filePath,
                    String fileName, String masterFileName, String dockerParentPath,
                    String fileHeadMd5) {
        super(content, bytes);
        this.offset = offset;
        this.timestamp = timeString;
        this.logTime = timestamp;
        this.preOffset = preOffset;
        this.fileNodeKey = fileNodeKey;
        this.fileKey = fileKey;
        this.collectTime = this.transTime;

        this.filePath = filePath;
        this.fileName = fileName;
        this.masterFileName = masterFileName;
        this.dockerParentPath = dockerParentPath;
        this.fileHeadMd5 = fileHeadMd5;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Long collectTime) {
        this.collectTime = collectTime;
    }

    public Long getLogTime() {
        return logTime;
    }

    public void setLogTime(Long logTime) {
        this.logTime = logTime;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public Long getPreOffset() {
        return preOffset;
    }

    public void setPreOffset(Long preOffset) {
        this.preOffset = preOffset;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getFileNodeKey() {
        return fileNodeKey;
    }

    public void setFileNodeKey(String fileNodeKey) {
        this.fileNodeKey = fileNodeKey;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getLogPathId() {
        return logPathId;
    }

    public void setLogPathId(long logPathId) {
        this.logPathId = logPathId;
    }

    public boolean isNeedToSend() {
        return needToSend;
    }

    public void setNeedToSend(boolean needToSend) {
        this.needToSend = needToSend;
    }

    public String getMasterFileName() {
        return masterFileName;
    }

    public void setMasterFileName(String masterFileName) {
        this.masterFileName = masterFileName;
    }

    public String getDockerParentPath() {
        return dockerParentPath;
    }

    public void setDockerParentPath(String dockerParentPath) {
        this.dockerParentPath = dockerParentPath;
    }

    public Long getFileModifyTime() {
        return fileModifyTime;
    }

    public void setFileModifyTime(Long fileModifyTime) {
        this.fileModifyTime = fileModifyTime;
    }

    @Override
    public String toString() {
        return "LogEvent{" + "timestamp='" + timestamp + '\'' + ", collectTime=" + collectTime
               + ", logTime=" + logTime + ", offset=" + offset + ", uniqueKey='" + uniqueKey + '\''
               + ", preOffset=" + preOffset + ", logId=" + logId + ", fileKey='" + fileKey + '\''
               + ", fileNodeKey='" + fileNodeKey + '\'' + ", filePath='" + filePath + '\''
               + ", fileName='" + fileName + '\'' + ", dockerParentPath='" + dockerParentPath
               + '\'' + ", logPathId=" + logPathId + ", masterFileName='" + masterFileName + '\''
               + ", needToSend=" + needToSend + ", fileModifyTime=" + fileModifyTime + '}';
    }
}
