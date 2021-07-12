package com.didichuxing.datachannel.agent.common.api;

/**
 * @description: 配置开关
 * @author: huangjw
 * @Date: 19/7/1 15:10
 */
public enum ServiceSwitch {
    ON(0, "on"), OFF(1, "off");

    private int    status;

    private String comment;

    private ServiceSwitch(int status, String comment) {
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
