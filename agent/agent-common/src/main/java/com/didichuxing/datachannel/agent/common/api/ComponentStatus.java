package com.didichuxing.datachannel.agent.common.api;

/**
 * @description: 组件状态
 * @author: huangjw
 * @Date: 19/7/1 20:09
 */
public enum ComponentStatus {
    RUNNING(0, "running"), STOP(1, "stop");

    private int    status;

    private String comment;

    private ComponentStatus(int status, String comment) {
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
