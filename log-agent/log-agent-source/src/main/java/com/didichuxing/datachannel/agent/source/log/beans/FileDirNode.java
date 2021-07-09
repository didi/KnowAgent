package com.didichuxing.datachannel.agent.source.log.beans;

/**
 * @description: 文件目录标记
 * @author: huangjw
 * @Date: 2019-07-22 19:18
 */
public class FileDirNode {

    private Long   logModId;
    private Long   pathId;
    private String path;
    private String dirKey;

    // 可变
    private Long   modifyTime;

    public FileDirNode(Long logModId, Long pathId, String path, String dirKey, Long modifyTime) {
        this.logModId = logModId;
        this.path = path;
        this.pathId = pathId;
        this.dirKey = dirKey;
        this.modifyTime = modifyTime;
    }

    public String getDirKey() {
        return this.dirKey;
    }

    public Long getLogModId() {
        return logModId;
    }

    public void setLogModId(Long logModId) {
        this.logModId = logModId;
    }

    public Long getPathId() {
        return pathId;
    }

    public void setPathId(Long pathId) {
        this.pathId = pathId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDirKey(String dirKey) {
        this.dirKey = dirKey;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "FileDirNode{" + "logModId=" + logModId + ", pathId=" + pathId + ", path='" + path
               + '\'' + ", dirKey='" + dirKey + '\'' + ", modifyTime=" + modifyTime + '}';
    }
}
