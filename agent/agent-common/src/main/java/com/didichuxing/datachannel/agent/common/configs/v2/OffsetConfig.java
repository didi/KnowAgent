package com.didichuxing.datachannel.agent.common.configs.v2;

import java.io.File;

/**
 * @description: offset 配置
 * @author: huangjw
 * @Date: 19/7/1 14:46
 */
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
