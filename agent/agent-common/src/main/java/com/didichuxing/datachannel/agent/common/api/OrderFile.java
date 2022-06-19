package com.didichuxing.datachannel.agent.common.api;

/**
 * @description: 文件顺序性
 * @author: huangjw
 * @Date: 18/6/26 14:04
 */
public enum OrderFile {
    OrderFile(0, "order"), // 顺序
    OutOfOrderFile(1, "outOfOrder"); // 乱序

    private int    status;

    private String comment;

    private OrderFile(int status, String comment) {
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
