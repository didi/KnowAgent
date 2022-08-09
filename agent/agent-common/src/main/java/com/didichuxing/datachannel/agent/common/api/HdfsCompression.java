package com.didichuxing.datachannel.agent.common.api;

/**
 * @description: 压缩方式
 * @author: huangjw
 * @Date: 2019-07-11 14:36
 */
public enum HdfsCompression {
    TEXT(0, "text"), SNAPPY(1, "snappy"), LZ4(2, "lz4"), SEQ_SNAPPY(3, "sqFile-snappy"), SEQ_LZ4(4,
                                                                                                 "sqFile-lz4"), SEQ_GZIP(
                                                                                                                         5,
                                                                                                                         "sqFile-gzip"), GZIP(
                                                                                                                                              6,
                                                                                                                                              "gzip");

    private int    status;

    private String comment;

    private HdfsCompression(int status, String comment) {
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
