package com.didichuxing.datachannel.agent.source.log.metrics;

import com.didichuxing.datachannel.agent.common.api.OrderFile;

/**
 * @description: 文件属性类
 * @author: huangjw
 * @Date: 18/8/27 18:31
 */
public class FileStatistic {

    private String  fileName;
    private Long    lastModifyTime;
    private Long    logTime;
    private Boolean isFileEnd = false;
    private Integer isFileOrder;
    private Boolean isVaildTimeConfig;

    // 采集进度long
    private long    rate      = 0L;

    public FileStatistic() {
    }

    public FileStatistic(String fileName, boolean isFileEnd, Long lastModifyTime,
                         Integer isFileOrder, boolean isVaildTimeConfig, Long logTime, long rate) {
        this.fileName = fileName;
        this.isFileEnd = isFileEnd;
        this.lastModifyTime = lastModifyTime;
        this.isFileOrder = isFileOrder;
        this.isVaildTimeConfig = isVaildTimeConfig;
        this.logTime = logTime;
        this.rate = rate;
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

    public Boolean isFileEnd() {
        return isFileEnd;
    }

    public void setFileEnd(boolean fileEnd) {
        isFileEnd = fileEnd;
    }

    public Integer getIsFileOrder() {
        return isFileOrder;
    }

    public void setIsFileOrder(Integer isFileOrder) {
        this.isFileOrder = isFileOrder;
    }

    public Boolean getVaildTimeConfig() {
        return isVaildTimeConfig;
    }

    public void setVaildTimeConfig(Boolean vaildTimeConfig) {
        isVaildTimeConfig = vaildTimeConfig;
    }

    public Long getLogTime() {
        return logTime;
    }

    public void setLogTime(Long logTime) {
        this.logTime = logTime;
    }

    public Boolean getFileEnd() {
        return isFileEnd;
    }

    public void setFileEnd(Boolean fileEnd) {
        isFileEnd = fileEnd;
    }

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"fileName\":\"").append(fileName).append("\",");
        sb.append("\"lastModifyTime\":").append(lastModifyTime).append(",");
        sb.append("\"rate\":\"").append(rate).append("\",");
        if (logTime != null) {
            sb.append("\"logTime\":").append(logTime).append(",");
        }
        sb.append("\"isFileEnd\":").append(isFileEnd);
        if (isFileOrder != null && isFileOrder.equals(OrderFile.OutOfOrderFile.getStatus())) {
            sb.append(",");
            sb.append("\"isFileOrder\":").append(isFileOrder);
        }

        if (isVaildTimeConfig != null && !isVaildTimeConfig) {
            sb.append(",");
            sb.append("\"isVaildTimeConfig\":").append(isVaildTimeConfig);
        }
        sb.append("}");
        return sb.toString();
    }

}
