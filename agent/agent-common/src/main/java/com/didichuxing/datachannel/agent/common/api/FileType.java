package com.didichuxing.datachannel.agent.common.api;

/**
 * @description: 待采集文件类型
 * @author: huangjw
 * @Date: 18/7/18 15:00
 */
public enum FileType {
    File(0, "file"), // 文件
    Dir(1, "directory"); // 文件夹

    private int    status;

    private String comment;

    private FileType(int status, String comment) {
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
