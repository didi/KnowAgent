package com.didichuxing.datachannel.agent.common.api;

/**
 * @description: 采集发送至hdfs类型接口
 * @author: huangjw
 * @Date: 2019-11-26 14:23
 */
public enum HdfsTransFormat {
    HDFS_STRING(0, "String"), HDFS_EVENT(1, "hdfsEvent");

    private int    status;

    private String comment;

    private HdfsTransFormat(int status, String comment) {
        this.status = status;
        this.comment = comment;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
