package com.didichuxing.datachannel.agent.common.configs.v1.collector;

import com.didichuxing.datachannel.agent.common.beans.LogPath;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 采集源头配置
 * @author: huangjw
 * @Date: 18/6/20 12:03
 */
public class SourceConfig implements Cloneable {

    List<LogPath> logPaths;

    public List<LogPath> getLogPaths() {
        return logPaths;
    }

    public void setLogPaths(List<LogPath> logPaths) {
        this.logPaths = logPaths;
    }

    @Override
    public SourceConfig clone() {
        SourceConfig sourceConfig = null;
        try {
            List<LogPath> newLogPath = new ArrayList<>();
            sourceConfig = (SourceConfig)super.clone();
            sourceConfig.setLogPaths(newLogPath);
            for (LogPath logPath : logPaths) {
                LogPath newPath = logPath.clone();
                if (newPath != null) {
                    newLogPath.add(newPath);
                }
            }
        } catch (CloneNotSupportedException e) {

        }
        return sourceConfig;
    }

    @Override
    public String toString() {
        return "SourceConfig{" + "logPaths=" + logPaths + '}';
    }
}
