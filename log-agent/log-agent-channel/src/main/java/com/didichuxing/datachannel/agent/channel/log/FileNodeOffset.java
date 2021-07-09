package com.didichuxing.datachannel.agent.channel.log;

import com.didichuxing.datachannel.agent.engine.bean.LogEvent;

/**
 * @description:
 * @author: huangjw
 * @Date: 19/7/9 20:40
 */
public class FileNodeOffset {

    private String fileNodeKey;

    private long   offset;
    private long   timestamp;

    public String getFileNodeKey() {
        return fileNodeKey;
    }

    public void setFileNodeKey(String fileNodeKey) {
        this.fileNodeKey = fileNodeKey;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public FileNodeOffset(LogEvent logEvent) {
        this.fileNodeKey = logEvent.getFileNodeKey();
        this.offset = logEvent.getOffset();
        this.timestamp = logEvent.getLogTime();
    }

    @Override
    public String toString() {
        return "FileNodeOffset{" + "fileNodeKey='" + fileNodeKey + '\'' + ", offset=" + offset
               + ", timestamp=" + timestamp + '}';
    }
}
