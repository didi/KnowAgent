package com.didichuxing.datachannel.agent.common.api;

/**
 * @description: 采集开始位置
 * @author: huangjw
 * @Date: 18/6/21 18:49
 */
public enum CollectLocation {
    /**
     * 从最新开始采集
     */
    Latest(0, "Latest"),

    /**
     * 从头开始采集
     */
    Earliest(1, "Earliest");

    private int    location;

    private String comment;

    private CollectLocation(int location, String comment) {
        this.location = location;
        this.comment = comment;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
