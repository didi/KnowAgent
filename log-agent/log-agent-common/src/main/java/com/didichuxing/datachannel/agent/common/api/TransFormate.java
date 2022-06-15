package com.didichuxing.datachannel.agent.common.api;

/**
 * @description: 发送类型
 * @author: huangjw
 * @Date: 18/6/22 18:19
 */
public enum TransFormate {
    /**
     * List列表
     */
    MQList(0, "mqLogEventList"),

    /**
     * 单个List
     */
    MQItem(1, "mqLogEvent"),

    /**
     * 纯内容
     */
    Content(2, "content");

    private int    status;
    private String comment;

    private TransFormate(int status, String comment) {
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
