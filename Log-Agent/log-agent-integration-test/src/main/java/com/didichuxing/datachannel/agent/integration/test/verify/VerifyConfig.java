package com.didichuxing.datachannel.agent.integration.test.verify;

/**
 * @description: 读取本地配置，生成需要校验的内容
 * @author: huangjw
 * @Date: 19/2/13 18:15
 */
public class VerifyConfig {

    private String name;
    private String type;
    int            format;
    int            transType;
    String         sourcePath;
    String         targetPath;
    int            logNum;

    public int getLogNum() {
        return logNum;
    }

    public void setLogNum(int logNum) {
        this.logNum = logNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public int getTransType() {
        return transType;
    }

    public void setTransType(int transType) {
        this.transType = transType;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    @Override
    public String toString() {
        return "VerifyConfig{" + "name='" + name + '\'' + ", type='" + type + '\'' + ", format="
               + format + ", transType=" + transType + ", sourcePath='" + sourcePath + '\''
               + ", targetPath='" + targetPath + '\'' + '}';
    }
}
