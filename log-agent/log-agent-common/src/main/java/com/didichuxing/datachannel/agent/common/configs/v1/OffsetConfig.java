package com.didichuxing.datachannel.agent.common.configs.v1;

import java.io.File;

public class OffsetConfig {
    private String rootDir;

    public OffsetConfig() {
        this.rootDir = System.getProperty("user.home") + File.separator + ".logOffSet";
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public String getRootDir() {
        return rootDir;
    }

    @Override
    public String toString() {
        return "OffsetConfig{" + "rootDir='" + rootDir + '\'' + '}';
    }
}
