package com.didichuxing.datachannel.agent.common.api;

/**
 * @description: 文件匹配类型
 * @author: huangjw
 * @Date: 18/7/18 14:58
 */
public enum FileMatchType {
    Length(0, "Length"), // 按照长度
    Regex(1, "Regex"); // 按照正则匹配

    private int    status;

    private String comment;

    private FileMatchType(int status, String comment) {
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
