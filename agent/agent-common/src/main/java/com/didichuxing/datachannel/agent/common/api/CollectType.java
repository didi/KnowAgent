package com.didichuxing.datachannel.agent.common.api;

/**
 * @description: 采集类型，用于区分容器内采集与弹性云宿主机采集
 * @author: huangjw
 * @Date: 2019-11-21 16:08
 */
public enum CollectType {
    COLLECT_IN_NORMAL_SERVER(0, "collect_in_normal_server"), // 正常采集
    COLLECT_IN_DDCLOUD_SERVER(1, "collect_in_ddcloud_server"); // 弹性云宿主机内采集

    private int    status;

    private String comment;

    private CollectType(int status, String comment) {
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
