package com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask;

public class CollectFileVO {
    private Boolean fileEnd;

    private String fileName;

    private Long lastModifyTime;

    private Long logTime;

    private Integer rate;

    private Boolean vaildTimeConfig;

    private Integer isFileOrder;

    public Boolean getFileEnd() {
        return fileEnd;
    }

    public void setFileEnd(Boolean fileEnd) {
        this.fileEnd = fileEnd;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getLogTime() {
        return logTime;
    }

    public void setLogTime(Long logTime) {
        this.logTime = logTime;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Boolean getVaildTimeConfig() {
        return vaildTimeConfig;
    }

    public void setVaildTimeConfig(Boolean vaildTimeConfig) {
        this.vaildTimeConfig = vaildTimeConfig;
    }

    public Integer getIsFileOrder() {
        return isFileOrder;
    }

    public void setIsFileOrder(Integer isFileOrder) {
        this.isFileOrder = isFileOrder;
    }
}
