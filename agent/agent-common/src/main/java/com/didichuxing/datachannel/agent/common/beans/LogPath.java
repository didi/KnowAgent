package com.didichuxing.datachannel.agent.common.beans;

import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;

/**
 * @description:
 * @author: huangjw
 * @Date: 18/6/20 12:12
 */
public class LogPath implements Cloneable {

    /**
     * 日志模型Id
     */
    private Long   logModelId;

    /**
     * 日志采集路径Id
     */
    private Long   pathId;

    /**
     * 日志采集路径
     */
    private String path;

    /**
     * 容器路径
     */
    private String dockerPath;

    /**
     * logPath key
     * 
     * @return
     */
    public String getLogPathKey() {
        return logModelId + LogConfigConstants.UNDERLINE_SEPARATOR + pathId
               + LogConfigConstants.UNDERLINE_SEPARATOR + path;
    }

    public LogPath() {

    }

    public LogPath(String path) {
        this(0L, 0L, path);
    }

    public LogPath(Long logModelId, Long pathId, String path) {
        this.logModelId = logModelId;
        this.pathId = pathId;
        this.path = path;
    }

    public LogPath(Long logModelId, Long pathId, String path, String dockerPath) {
        this.logModelId = logModelId;
        this.pathId = pathId;
        this.path = path;
        this.dockerPath = dockerPath;
    }

    public Long getLogModelId() {
        return logModelId;
    }

    public void setLogModelId(Long logModelId) {
        this.logModelId = logModelId;
    }

    public Long getPathId() {
        return pathId;
    }

    public void setPathId(Long pathId) {
        this.pathId = pathId;
    }

    public String getRealPath() {
        if (dockerPath != null && dockerPath.length() != 0) {
            return dockerPath;
        }
        return path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setRealPath(String path) {
        if (dockerPath != null && dockerPath.length() != 0) {
            this.dockerPath = path;
        } else {
            this.path = path;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LogPath logPath = (LogPath) o;

        if (logModelId != null ? !logModelId.equals(logPath.logModelId)
            : logPath.logModelId != null) {
            return false;
        }
        return pathId != null ? pathId.equals(logPath.pathId) : logPath.pathId == null;

    }

    @Override
    public int hashCode() {
        int result = logModelId != null ? logModelId.hashCode() : 0;
        result = 31 * result + (pathId != null ? pathId.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    @Override
    public LogPath clone() {
        LogPath logPath = null;
        try {
            logPath = (LogPath) super.clone();
        } catch (CloneNotSupportedException e) {

        }
        return logPath;
    }

    @Override
    public String toString() {
        return "LogPath{" + "logModelId=" + logModelId + ", pathId=" + pathId + ", path='" + path
               + '\'' + ", dockerPath='" + dockerPath + '\'' + '}';
    }

    public String getDockerPath() {
        return dockerPath;
    }

    public void setDockerPath(String dockerPath) {
        this.dockerPath = dockerPath;
    }
}
